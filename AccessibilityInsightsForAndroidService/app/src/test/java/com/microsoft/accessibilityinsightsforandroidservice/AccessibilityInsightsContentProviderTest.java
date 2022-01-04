// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import java.io.File;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({
  Binder.class,
  AccessibilityInsightsContentProvider.class,
  ParcelFileDescriptor.class
})
public class AccessibilityInsightsContentProviderTest {
  @Mock Uri uriMock;
  @Mock CancellationSignal cancellationSignalMock;
  @Mock TempFileProvider tempFileProviderMock;
  @Mock SynchronizedRequestDispatcher requestDispatcherMock;
  @Mock ParcelFileDescriptor tempFileDescriptor;
  @Mock File tempFileMock;
  @Mock Bundle bundleMock;

  MockedStatic<Binder> binderStaticMock;
  MockedStatic<ParcelFileDescriptor> parcelFileDescriptorStaticMock;

  AccessibilityInsightsContentProvider testSubject;

  @Before
  public void prepare() throws Exception {
    binderStaticMock = Mockito.mockStatic(Binder.class);
    parcelFileDescriptorStaticMock = Mockito.mockStatic(ParcelFileDescriptor.class);

    testSubject = new AccessibilityInsightsContentProvider();
    assertTrue(testSubject.onCreate(requestDispatcherMock, tempFileProviderMock));

    whenNew(Bundle.class).withNoArguments().thenReturn(bundleMock);
    doNothing().when(bundleMock).putString(anyString(), anyString());

    when(ParcelFileDescriptor.open(tempFileMock, ParcelFileDescriptor.MODE_READ_ONLY))
        .thenReturn(tempFileDescriptor);
  }

  @After
  public void cleanUp() {
    parcelFileDescriptorStaticMock.close();
    binderStaticMock.close();
  }

  private void setupCallerAsAdb() {
    setupCallerAsUid(2000);
  }

  private void setupCallerAsNotAdb() {
    setupCallerAsUid(1);
  }

  private void setupCallerAsUid(int uid) {
    when(Binder.getCallingUid()).thenReturn(uid);
  }

  @Test
  public void callEmitsBundleWithSerializedSecurityExceptionIfNotAdb() {
    setupCallerAsNotAdb();
    assertThrows(SecurityException.class, () -> testSubject.openFile(uriMock, "r", null));
  }

  @Test
  public void openFileThrowsSecurityExceptionIfCallerIsNotAdb() {
    setupCallerAsNotAdb();
    assertThrows(SecurityException.class, () -> testSubject.openFile(uriMock, "r", null));
  }

  @Test
  public void openFileDoesNotThrowSecurityExceptionIfCallerIsAdb() {
    setupCallerAsAdb();
    testSubject.openFile(uriMock, "r", null);
  }

  @Test
  public void openFileEmitsTempFileWithResponseFromDispatcher() throws Exception {
    setupCallerAsAdb();
    String dispatcherResponse = "dispatcher response";
    when(uriMock.getPath()).thenReturn("/uri-path");
    String expectedMethod = "/uri-path";
    when(requestDispatcherMock.request(expectedMethod, cancellationSignalMock))
        .thenReturn(dispatcherResponse);
    when(tempFileProviderMock.createTempFileWithContents(any())).thenReturn(tempFileMock);
    assertSame(tempFileDescriptor, testSubject.openFile(uriMock, "r", cancellationSignalMock));
    verify(tempFileProviderMock).createTempFileWithContents(dispatcherResponse);
  }

  @Test
  public void openFileEmitsTempFileWithSerializedExceptionOnDispatcherError() throws Exception {
    setupCallerAsAdb();
    when(uriMock.getPath()).thenReturn("/uri-path");
    String expectedMethod = "/uri-path";
    when(requestDispatcherMock.request(expectedMethod, cancellationSignalMock))
        .thenThrow(new Exception("dispatcher error"));
    when(tempFileProviderMock.createTempFileWithContents(any())).thenReturn(tempFileMock);
    assertSame(tempFileDescriptor, testSubject.openFile(uriMock, "r", cancellationSignalMock));

    String serializedException = "java.lang.Exception: dispatcher error";
    verify(tempFileProviderMock).createTempFileWithContents(serializedException);
  }

  @Test
  public void openFileThrowsRuntimeExceptionOnTempFileError() throws Exception {
    setupCallerAsAdb();
    String dispatcherResponse = "dispatcher response";
    when(uriMock.getPath()).thenReturn("uri-path");
    String expectedMethod = "/uri-path";
    when(requestDispatcherMock.request(expectedMethod, cancellationSignalMock))
        .thenReturn(dispatcherResponse);
    when(tempFileProviderMock.createTempFileWithContents(any()))
        .thenThrow(new IOException("tempFileProvider error"));

    assertThrows(
        "tempFileProvider error",
        RuntimeException.class,
        () -> testSubject.openFile(uriMock, "r", cancellationSignalMock));
  }

  @Test
  public void callEmitsBundleWithResponseFromDispatcher() throws Exception {
    setupCallerAsAdb();
    String dispatcherResponse = "dispatcher response";
    String expectedMethod = "/method";
    when(requestDispatcherMock.request(eq(expectedMethod), notNull()))
        .thenReturn(dispatcherResponse);

    assertSame(bundleMock, testSubject.call("method", null, null));
    verify(bundleMock).putString("response", dispatcherResponse);
  }

  @Test
  public void callEmitsBundleWithSerializedExceptionOnDispatcherError() throws Exception {
    setupCallerAsAdb();
    String expectedMethod = "/method";
    when(requestDispatcherMock.request(eq(expectedMethod), notNull()))
        .thenThrow(new Exception("dispatcher error"));

    assertSame(bundleMock, testSubject.call("method", null, null));

    String serializedException = "java.lang.Exception: dispatcher error";
    verify(bundleMock).putString("response", serializedException);
  }
}
