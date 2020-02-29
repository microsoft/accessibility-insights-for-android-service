// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Logger.class, StackTrace.class})
public class ResponseThreadTest {

  @Mock Socket socket;
  @Mock ResponseWriterFactory responseWriterFactory;
  @Mock RequestReaderFactory requestReaderFactory;
  @Mock RequestHandlerFactory requestHandlerFactory;
  @Mock OutputStream socketOutputStream;
  @Mock InputStream socketInputStream;
  @Mock ResponseWriter responseWriter;
  @Mock RequestReader requestReader;
  @Mock RequestHandler requestHandler;
  @Mock IOException testException;

  String requestString = "test request string";
  String stackTrace = "test stack trace";

  ResponseThread testSubject;

  @Before
  public void prepare() {
    PowerMockito.mockStatic(Logger.class);
    PowerMockito.mockStatic(StackTrace.class);

    setupSocketStreams();
    setupFactoryCalls();
    setupRequestString();

    when(StackTrace.getStackTrace(testException)).thenReturn(stackTrace);

    testSubject =
        new ResponseThread(
            socket, responseWriterFactory, requestReaderFactory, requestHandlerFactory);
  }

  @Test
  public void responseThreadExists() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void createsRequestReaderFromSocketInputStream() {
    testSubject.run();
    verify(requestReaderFactory, times(1)).createRequestReader(socketInputStream);
  }

  @Test
  public void createsResponseWriterFromSocketOutputStream() {
    testSubject.run();
    verify(responseWriterFactory, times(1)).createResponseWriter(socketOutputStream);
  }

  @Test
  public void handlesExceptionOnGetInputStream() {
    try {
      when(socket.getInputStream()).thenThrow(testException);
    } catch (IOException e) {
      Assert.fail(e.getMessage());
    }

    testSubject.run();

    PowerMockito.verifyStatic(Logger.class);
    Logger.logVerbose("ResponseThread", stackTrace);
  }

  @Test
  public void handlesExceptionOnGetOutputStream() {
    try {
      when(socket.getOutputStream()).thenThrow(testException);
    } catch (IOException e) {
      Assert.fail(e.getMessage());
    }

    testSubject.run();

    PowerMockito.verifyStatic(Logger.class);
    Logger.logVerbose("ResponseThread", stackTrace);
  }

  @Test
  public void createsAndCallsRequestHandler() {
    testSubject.run();

    try {
      verify(requestHandlerFactory, times(1))
          .createHandlerForRequest(socket, requestString, responseWriter);
    } catch (Exception e) {
      Assert.fail(e.getMessage());
    }
    verify(requestHandler, times(1)).handleRequest();
  }

  private void setupSocketStreams() {
    try {
      when(socket.getOutputStream()).thenReturn(socketOutputStream);
      when(socket.getInputStream()).thenReturn(socketInputStream);
    } catch (IOException e) {
      Assert.fail(e.getMessage());
    }
  }

  private void setupFactoryCalls() {
    when(responseWriterFactory.createResponseWriter(socketOutputStream)).thenReturn(responseWriter);
    when(requestReaderFactory.createRequestReader(socketInputStream)).thenReturn(requestReader);
    try {
      when(requestHandlerFactory.createHandlerForRequest(eq(socket), any(), eq(responseWriter)))
          .thenReturn(requestHandler);
    } catch (Exception e) {
      Assert.fail(e.getMessage());
    }
  }

  private void setupRequestString() {
    try {
      when(requestReader.readRequest()).thenReturn(requestString);
    } catch (IOException e) {
      Assert.fail(e.getMessage());
    }
  }
}
