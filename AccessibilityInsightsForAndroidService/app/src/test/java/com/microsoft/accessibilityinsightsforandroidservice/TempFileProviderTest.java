// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.WorkerParameters;
import androidx.work.impl.model.WorkSpec;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TempFileProviderTest {

  @Mock Context contextMock;
  @Mock WorkManager workManagerMock;
  @Captor ArgumentCaptor<WorkRequest> workRequestCaptor;

  TempFileProvider testSubject;
  File cacheDirectory;

  void makeFileLookOld(File file) {
    // We subtract one second because setLastModified is documented as being accurate to 1s
    file.setLastModified(
        new Date().getTime() - TempFileProvider.tempFileLifetimeMillis - 60 * 1000);
  }

  @Before
  public void prepare() throws Exception {
    cacheDirectory = Files.createTempDirectory("tempFileProviderTest").toFile();
    when(contextMock.getCacheDir()).thenReturn(cacheDirectory);
    testSubject = new TempFileProvider(contextMock, workManagerMock);
  }

  @After
  public void cleanUp() {
    if (cacheDirectory.exists()) {
      cacheDirectory.delete();
    }
  }

  @Test
  public void tempFileIsCreatedWithContent() throws IOException {
    // Check the encoding
    String content = "Test string";
    File tempFile = testSubject.createTempFileWithContents(content);
    byte[] fileContent = Files.readAllBytes(tempFile.toPath());
    assertArrayEquals(content.getBytes(StandardCharsets.UTF_8), fileContent);
  }

  @Test
  public void tempFileCreateWithSpecialCharactersInContent() throws IOException {
    String specialCharacters = "\uD83E\uDD8F \uD83D\uDE1F \uD83D\uDC39 ⌛️";
    File tempFile = testSubject.createTempFileWithContents(specialCharacters);
    byte[] fileContent = Files.readAllBytes(tempFile.toPath());
    assertArrayEquals(specialCharacters.getBytes(StandardCharsets.UTF_8), fileContent);
  }

  @Test
  public void createTempFileWithContentsThrowsIOExceptionIfTempFileCanNotBeCreated()
      throws IOException {
    try (MockedStatic<File> fileStaticMock = Mockito.mockStatic(File.class)) {
      fileStaticMock.when(() -> File.createTempFile(any(), any(), any())).thenThrow(new IOException());
      assertThrows(IOException.class, () -> testSubject.createTempFileWithContents("Content"));
    }
  }

  @Test
  public void createTempFileWithContentsCreatesANewFileEveryTime() throws IOException {
    File firstTempFile = testSubject.createTempFileWithContents("Test string");
    File secondTempFile = testSubject.createTempFileWithContents("Test string");
    String firstPath = firstTempFile.getAbsolutePath();
    String secondPath = secondTempFile.getAbsolutePath();
    assertNotEquals(firstPath, secondPath);
  }

  @Test
  public void createTempFileWithContentCreatesFileUnderCacheDir() throws IOException {
    File output = testSubject.createTempFileWithContents("Test string");
    String path = output.getAbsolutePath();
    assertTrue(path.startsWith(cacheDirectory.getAbsolutePath()));
  }

  @Test
  public void cleanOldFilesOnlyDeletesOldFiles() throws IOException {
    File oldFile = testSubject.createTempFileWithContents("Old File");
    File newFile = testSubject.createTempFileWithContents("New File");
    makeFileLookOld(oldFile);
    testSubject.cleanOldFilesBestEffort();
    assertFalse(oldFile.exists());
    assertTrue(newFile.exists());
  }

  @Test
  public void cleanOldFilesNoopsIfTempDirIsDeleted() {
    cacheDirectory.delete();
    testSubject.cleanOldFilesBestEffort(); // Should not throw exception
  }

  @Test
  public void cleanOldFilesContinuesIfAFileCanNotBeDeleted() throws IOException {
    File erasableFile = testSubject.createTempFileWithContents("Test string");
    File noErasableFile = testSubject.createTempFileWithContents("Test sting");
    makeFileLookOld(erasableFile);
    makeFileLookOld(noErasableFile);
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(noErasableFile))) {
      testSubject.cleanOldFilesBestEffort();
    }
    assertTrue(noErasableFile.exists());
    assertFalse(erasableFile.exists());
  }

  @Test
  public void createTempFileSchedulesACleanWorker() throws IOException {
    File oldFile = testSubject.createTempFileWithContents("Old File");
    makeFileLookOld(oldFile);

    WorkSpec workSpec = getLastWorkManagerRequest();
    assertEquals(TempFileProvider.tempFileLifetimeMillis, workSpec.initialDelay);
    assertEquals(TempFileProvider.CleanWorker.class.getName(), workSpec.workerClassName);

    WorkerParameters workerParameters = createStubWorkerParameters(workSpec);
    TempFileProvider.CleanWorker cleanWorker =
        new TempFileProvider.CleanWorker(contextMock, workerParameters);
    ListenableWorker.Result result = cleanWorker.doWork();
    assertEquals(ListenableWorker.Result.success(), result);
    assertFalse(oldFile.exists());
  }

  @NonNull
  private WorkerParameters createStubWorkerParameters(WorkSpec workSpec) {
    Data inputData = workSpec.input;
    WorkerParameters workerParameters =
        new WorkerParameters(
            null, inputData, new ArrayList<>(), null, 1, null, null, null, null, null);
    return workerParameters;
  }
  
  @NonNull
  private WorkSpec getLastWorkManagerRequest() {
    verify(workManagerMock, times(1)).enqueue(workRequestCaptor.capture());
    WorkSpec workSpec = workRequestCaptor.getValue().getWorkSpec();
    return workSpec;
  }
}
