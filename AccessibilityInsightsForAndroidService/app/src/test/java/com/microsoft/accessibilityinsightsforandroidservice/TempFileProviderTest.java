// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.content.Context;

import androidx.work.Configuration;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TempFileProvider.class})
public class TempFileProviderTest {

  @Mock Context contextMock;

  Configuration configuration;
  OneTimeWorkRequest oneTimeWorkRequest;


  TempFileProvider testSubject;
  File cacheDirectory;

  void makeFileLookOld(File file) {
    // We substract one second because the setLastModified documentation said is acurate to one
    // second
    file.setLastModified(
        new Date().getTime() - TempFileProvider.tempFileLifetimeMillis - 60 * 1000);
  }

  void prepareWorkerManager(){
    configuration = new Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build();
    oneTimeWorkRequest = new OneTimeWorkRequest.Builder(CleanWorker.class)
            .setInitialDelay(5, TimeUnit.MINUTES)
            .build();
    when(WorkerManagerRunner.initializeConfiguration()).thenReturn(configuration);
    when(WorkerManagerRunner.createTask()).thenReturn(oneTimeWorkRequest);
    when(WorkerManagerRunner.startWorkerManager()).thenReturn(WorkManager.getInstance(contextMock));
  }

  @Before
  public void prepare() throws Exception {
    cacheDirectory = Files.createTempDirectory("tempFileProviderTest").toFile();
    when(contextMock.getCacheDir()).thenReturn(cacheDirectory);
    PowerMockito.mockStatic(WorkerManagerRunner.class);
    prepareWorkerManager();
    testSubject = new TempFileProvider(contextMock);
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
    String specialCharactes = "\uD83E\uDD8F \uD83D\uDE1F \uD83D\uDC39 ⌛️";
    File tempFile = testSubject.createTempFileWithContents(specialCharactes);
    byte[] fileContent = Files.readAllBytes(tempFile.toPath());
    assertArrayEquals(specialCharactes.getBytes(StandardCharsets.UTF_8), fileContent);
  }

  @Test
  public void createTempFileWithContentsThrowsIOExceptionIfTempFileCanNotBeCreated()
      throws IOException {
    PowerMockito.mockStatic(File.class);
    when(File.createTempFile(any(), any(), any())).thenThrow(new IOException());
    assertThrows(IOException.class, () -> testSubject.createTempFileWithContents("Content"));
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
}
