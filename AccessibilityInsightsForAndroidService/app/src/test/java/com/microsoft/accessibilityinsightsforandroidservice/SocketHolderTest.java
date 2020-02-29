// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.doThrow;

import java.io.IOException;
import java.net.Socket;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.verification.VerificationMode;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Logger.class})
public class SocketHolderTest {

  @Mock Socket socketMock;

  final String logTag = "logTag";

  SocketHolder testSubject;

  @Before
  public void prepare() {
    PowerMockito.mockStatic(Logger.class);
    testSubject = new SocketHolder(socketMock);
  }

  @Test
  public void socketHolderExists() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void closesSocketOnlyOnce() {
    testSubject.close(logTag);
    verifySocketClosed(times(1));

    reset(socketMock);

    testSubject.close(logTag);
    verifySocketClosed(never());
  }

  @Test
  public void logsFailure() {
    IOException testException = new IOException("test exception message");
    try {
      doThrow(testException).when(socketMock).close();
    } catch (IOException e) {
      Assert.fail(e.getMessage());
    }

    testSubject.close(logTag);

    PowerMockito.verifyStatic(Logger.class);
    Logger.logVerbose(logTag, testException.toString());
  }

  private void verifySocketClosed(VerificationMode times) {
    try {
      verify(socketMock, times).close();
    } catch (IOException e) {
      Assert.fail(e.getMessage());
    }
  }
}
