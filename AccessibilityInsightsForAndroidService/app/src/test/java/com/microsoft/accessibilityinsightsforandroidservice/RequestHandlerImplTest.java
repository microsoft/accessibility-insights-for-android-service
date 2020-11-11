// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Logger.class})
public class RequestHandlerImplTest {

  @Mock SocketHolder socketHolder;
  @Mock RequestFulfiller requestFulfiller;

  private final String logTag = "logTag";
  private final String logMessage = "test log message";

  RequestHandlerImpl testSubject;

  @Before
  public void prepare() {
    PowerMockito.mockStatic(Logger.class);
    testSubject = new RequestHandlerImpl(socketHolder, requestFulfiller, logTag, logMessage);
  }

  @Test
  public void requestHandlerExists() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void getIsBlockingRequest_ReturnsFalse() {
    when(requestFulfiller.getIsBlockingRequest()).thenReturn(false);
    Assert.assertFalse(testSubject.getIsBlockingRequest());
    verify(requestFulfiller, times(1)).getIsBlockingRequest();
  }

  @Test
  public void getIsBlockingRequest_ReturnsTrue() {
    when(requestFulfiller.getIsBlockingRequest()).thenReturn(true);
    Assert.assertTrue(testSubject.getIsBlockingRequest());
    verify(requestFulfiller, times(1)).getIsBlockingRequest();
  }

  @Test
  public void logsRequestStart() {
    testSubject.handleRequest();
    PowerMockito.verifyStatic(Logger.class);
    Logger.logVerbose(logTag, logMessage);
  }

  @Test
  public void closesSocket() {
    doAnswer(
            AdditionalAnswers.answerVoid(
                (RunnableFunction onRequestFulfilled) -> onRequestFulfilled.run()))
        .when(requestFulfiller)
        .fulfillRequest(any());
    testSubject.handleRequest();

    verify(socketHolder, times(1)).close(logTag);
  }
}
