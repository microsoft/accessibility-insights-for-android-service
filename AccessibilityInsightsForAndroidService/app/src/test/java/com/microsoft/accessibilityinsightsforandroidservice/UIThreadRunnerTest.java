// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import android.os.Handler;
import android.os.Looper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Looper.class, UIThreadRunner.class})
public class UIThreadRunnerTest {

  @Mock Looper looperMock;
  @Mock Handler handlerMock;
  @Mock Runnable runnableMock;

  UIThreadRunner testSubject;

  @Test
  public void createsNewHandlerUsingMainLooper() throws Exception {
    PowerMockito.mockStatic(Looper.class);
    when(Looper.getMainLooper()).thenReturn(looperMock);
    whenNew(Handler.class).withArguments(looperMock).thenReturn(handlerMock);

    doAnswer(
            invocation -> {
              Runnable runnable = invocation.getArgument(0);
              runnable.run();
              return null;
            })
        .when(handlerMock)
        .post(any());

    testSubject = new UIThreadRunner();
    testSubject.run(runnableMock);

    verify(runnableMock).run();
  }
}
