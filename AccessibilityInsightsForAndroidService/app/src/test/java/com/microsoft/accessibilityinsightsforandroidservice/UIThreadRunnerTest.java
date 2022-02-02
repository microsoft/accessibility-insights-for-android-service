// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

import android.os.Handler;
import android.os.Looper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UIThreadRunnerTest {

  @Mock Looper looperMock;
  @Mock Runnable runnableMock;

  UIThreadRunner testSubject;

  @Test
  public void createsNewHandlerUsingMainLooper() throws Exception {
    try (MockedStatic<Looper> looperStaticMock = Mockito.mockStatic(Looper.class)) {
      looperStaticMock.when(Looper::getMainLooper).thenReturn(looperMock);
      try (MockedConstruction<Handler> handlerConstructionMock = Mockito.mockConstruction(Handler.class, (handlerMock, context) -> {
          doAnswer(
                  invocation -> {
                      Runnable runnable = invocation.getArgument(0);
                      runnable.run();
                      return null;
                  })
                  .when(handlerMock)
                  .post(any());
      })) {
          testSubject = new UIThreadRunner();
          testSubject.run(runnableMock);

          verify(runnableMock).run();
      }
    }
  }
}
