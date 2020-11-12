// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.io.IOException;
import java.net.ServerSocket;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.stubbing.OngoingStubbing;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ServerThread.class, StackTrace.class, Logger.class})
public class ServerThreadTest {

  @Mock ServerSocketFactory serverSocketFactory;
  @Mock ResponseThreadFactory responseThreadFactory;
  @Mock ServerSocket serverSocketMock;
  @Mock IOException testException;

  ServerThread testSubject;

  int responseThreadCompletedCount;
  final int timeoutMillis = 5000;
  final String errorStackTrace = "error stack trace";

  class TestableResponseThread extends ResponseThread {

    public TestableResponseThread() {
      super(null, null, null, null);
    }

    public void run() {
      responseThreadCompletedCount++;
    }

    @Override
    public boolean isBlockingRequest() {
      return true;
    }
  }

  @Before
  public void prepare() throws Exception {
    responseThreadCompletedCount = 0;
    try {
      when(serverSocketFactory.createServerSocket(ServerThread.ServerPort))
          .thenReturn(serverSocketMock);
    } catch (IOException e) {
      Assert.fail(e.getMessage());
    }
    PowerMockito.mockStatic(ServerThread.class);
    PowerMockito.mockStatic(Logger.class);
    PowerMockito.mockStatic(StackTrace.class);
    Whitebox.setInternalState(ServerThread.class, "ServerSocket", (Object) null);
    PowerMockito.when(ServerThread.class, "setServerSocket", serverSocketMock).thenCallRealMethod();
    testSubject = new ServerThread(serverSocketFactory, responseThreadFactory);
    when(StackTrace.getStackTrace(testException)).thenReturn(errorStackTrace);
  }

  @Test
  public void serverThreadExists() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void runsResponseThread() {
    runServerThread(1);
    Assert.assertEquals(1, responseThreadCompletedCount);
  }

  @Test
  public void runs3ResponseThreads() {
    runServerThread(3);
    Assert.assertEquals(3, responseThreadCompletedCount);
  }

  @Test
  public void createsServerSocketOnce() throws IOException {
    runServerThread(2);
    verify(serverSocketFactory, times(1)).createServerSocket(ServerThread.ServerPort);
  }

  @Test
  public void closesServerSocketOnce() throws IOException {
    runServerThread(2);
    verify(serverSocketMock, times(1)).close();
  }

  @Test
  public void printExceptionOnSocketAcceptFailure() throws IOException {
    when(serverSocketMock.accept()).thenThrow(testException);

    runServerThread(1);

    PowerMockito.verifyStatic(Logger.class);
    Logger.logError("ServerThread", errorStackTrace);
  }

  @Test
  public void printExceptionOnSocketCloseFailure() throws IOException {
    doThrow(testException).when(serverSocketMock).close();

    runServerThread(1);

    PowerMockito.verifyStatic(Logger.class);
    Logger.logError("ServerThread", errorStackTrace);
  }

  public void setupResponseThreadStubs(int numThreads) {
    // Set up a chain of n responseThreadFactory calls
    // (They need to be different objects because we can only run threads once)
    OngoingStubbing responseThreadFactoryStubbing =
        when(responseThreadFactory.createResponseThread(any()));
    for (int i = 0; i < numThreads - 1; i++) {
      TestableResponseThread responseThreadStub = new TestableResponseThread();
      responseThreadFactoryStubbing = responseThreadFactoryStubbing.thenReturn(responseThreadStub);
    }

    responseThreadFactoryStubbing.thenAnswer(
        invocation -> {
          testSubject.exit();
          return new TestableResponseThread();
        });
  }

  public void runServerThread(int times) {
    setupResponseThreadStubs(times);
    testSubject.start();
    try {
      testSubject.join(timeoutMillis);
      Assert.assertFalse(testSubject.isAlive());
    } catch (InterruptedException e) {
      Assert.fail(e.getMessage());
    }
  }
}
