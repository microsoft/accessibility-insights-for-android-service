// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeviceOrientationHandlerTest {

  @Mock Consumer<Integer> onChangeMock;
  int initialValue = 1;

  DeviceOrientationHandler testSubject;

  @Before
  public void prepare() {
    testSubject = new DeviceOrientationHandler(initialValue);
  }

  @Test
  public void setOrientationDoesNotCallOnChangeListenersIfOrientationDoesNotChange() {
    testSubject.subscribe(onChangeMock);
    testSubject.setOrientation(initialValue);
    verify(onChangeMock, times(0)).accept(2);
  }

  @Test
  public void setOrientationCallsOnChangeListenersOnOrientationchange() {
    testSubject.subscribe(onChangeMock);
    testSubject.setOrientation(2);
    verify(onChangeMock, times(1)).accept(2);
  }

  @Test
  public void setOrientationSupportsMultipleListeners() {
    testSubject.subscribe(onChangeMock);
    testSubject.subscribe(onChangeMock);

    testSubject.setOrientation(2);

    verify(onChangeMock, times(2)).accept(2);
  }
}
