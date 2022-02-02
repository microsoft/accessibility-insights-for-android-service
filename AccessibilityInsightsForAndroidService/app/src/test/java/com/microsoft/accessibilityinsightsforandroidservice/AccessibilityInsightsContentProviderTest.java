// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccessibilityInsightsContentProviderTest {
  @Mock Uri uriMock;
  @Mock CancellationSignal cancellationSignalMock;
  @Mock TempFileProvider tempFileProviderMock;
  @Mock SynchronizedRequestDispatcher requestDispatcherMock;
  @Mock ParcelFileDescriptor tempFileDescriptor;
  @Mock File tempFileMock;

  MockedStatic<Binder> binderStaticMock;
  MockedStatic<ParcelFileDescriptor> parcelFileDescriptorStaticMock;
  MockedConstruction<Bundle> bundleConstructionMock;

  AccessibilityInsightsContentProvider testSubject;

  @Before
  public void prepare() throws Exception {
    binderStaticMock = Mockito.mockStatic(Binder.class);
    parcelFileDescriptorStaticMock = Mockito.mockStatic(ParcelFileDescriptor.class);
    bundleConstructionMock = Mockito.mockConstruction(Bundle.class, invocation -> null);

    testSubject = new AccessibilityInsightsContentProvider();
    assertTrue(testSubject.onCreate(requestDispatcherMock, tempFileProviderMock));

    parcelFileDescriptorStaticMock.when(() -> ParcelFileDescriptor.open(tempFileMock, ParcelFileDescriptor.MODE_READ_ONLY))
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

    assertEquals(1, bundleConstructionMock.constructed().size());
    Bundle bundleMock = bundleConstructionMock.constructed().get(0);

    assertSame(bundleMock, testSubject.call("method", null, null));
    verify(bundleMock).putString("response", dispatcherResponse);
  }

  @Test
  public void callEmitsBundleWithSerializedExceptionOnDispatcherError() throws Exception {
    setupCallerAsAdb();
    String expectedMethod = "/method";
    when(requestDispatcherMock.request(eq(expectedMethod), notNull()))
        .thenThrow(new Exception("dispatcher error"));

    assertEquals(1, bundleConstructionMock.constructed().size());
    Bundle bundleMock = bundleConstructionMock.constructed().get(0);

    String serializedException = "java.lang.Exception: dispatcher error";
    verify(bundleMock).putString("response", serializedException);
  }
}
