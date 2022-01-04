// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(MockitoJUnitRunner.class)
public class FocusVisualizationStateManagerTest {

  @Mock Consumer<Boolean> onChangeMock;

  FocusVisualizationStateManager testSubject;

  @Before
  public void prepare() {
    testSubject = new FocusVisualizationStateManager();
  }

  @Test
  public void exists() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void getStateReturnsFalseByDefault() {
    Assert.assertFalse(testSubject.getState());
  }

  @Test
  public void getStateReturnsUpdatedState() {
    testSubject.setState(true);
    Assert.assertTrue(testSubject.getState());
  }

  @Test
  public void setStateDoesNotCallOnChangeListenersIfStateDoesNotChange() {
    testSubject.subscribe(onChangeMock);
    testSubject.setState(false);
    verify(onChangeMock, times(0)).accept(false);
  }

  @Test
  public void setStateCallsOnChangeListenersOnStateChange() {
    testSubject.subscribe(onChangeMock);
    testSubject.setState(true);
    Assert.assertTrue(testSubject.getState());
    verify(onChangeMock, times(1)).accept(true);
  }
}
