// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.ArrayList;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
public class DeviceOrientationHandlerTest {

  @Mock Consumer<Integer> onChangeMock;
  int initialValue = 1;

  DeviceOrientationHandler testSubject;

  @Before
  public void prepare() {
    testSubject = new DeviceOrientationHandler(initialValue);
  }

  @Test
  public void exists() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void setOrientationUpdatesOrientation() {
    testSubject.setOrientation(0);
    int orientation = Whitebox.getInternalState(testSubject, "orientation");
    Assert.assertEquals(orientation, 0);
  }

  @Test
  public void setOrientationDoesNotCallOnChangeListenersIfOrientationDoesNotChange() {
    testSubject.subscribe(onChangeMock);
    testSubject.setOrientation(1);
    int orientation = Whitebox.getInternalState(testSubject, "orientation");
    Assert.assertEquals(orientation, 1);
    verify(onChangeMock, times(0)).accept(2);
  }

  @Test
  public void setOrientationCallsOnChangeListenersOnOrientationchange() {
    testSubject.subscribe(onChangeMock);
    testSubject.setOrientation(2);
    int orientation = Whitebox.getInternalState(testSubject, "orientation");
    Assert.assertEquals(orientation, 2);
    verify(onChangeMock, times(1)).accept(2);
  }

  @Test
  public void subscribeAddsListenerToList() {
    testSubject.subscribe(onChangeMock);
    testSubject.subscribe(onChangeMock);
    ArrayList<Consumer<Integer>> listeners =
        Whitebox.getInternalState(testSubject, "onOrientationChangedListeners");
    Assert.assertEquals(listeners.size(), 2);
  }
}
