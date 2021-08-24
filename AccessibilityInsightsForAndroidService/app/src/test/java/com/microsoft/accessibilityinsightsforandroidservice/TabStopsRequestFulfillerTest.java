// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import android.os.CancellationSignal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TabStopsRequestFulfillerTest {
  @Mock FocusVisualizationStateManager focusVisualizationStateManager;
  @Mock CancellationSignal cancellationSignal;

  TabStopsRequestFulfiller testSubject;

  @Test
  public void fulfillRequestSetsTabStopState() {
    testSubject = new TabStopsRequestFulfiller(focusVisualizationStateManager, true);
    assertEquals("", testSubject.fulfillRequest(cancellationSignal));

    verify(focusVisualizationStateManager).setState(true);
  }
}
