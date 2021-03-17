// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.Mockito.verify;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TabStopsRequestFulfillerTest {
  @Mock ResponseWriter responseWriter;
  @Mock FocusVisualizationStateManager focusVisualizationStateManager;
  @Mock RunnableFunction onRequestFulfilled;

  TabStopsRequestFulfiller testSubject;

  @Test
  public void isBlockingRequestIsTrue() {
    testSubject =
        new TabStopsRequestFulfiller(responseWriter, focusVisualizationStateManager, true);
    Assert.assertTrue(testSubject.isBlockingRequest());
  }

  @Test
  public void fulfillRequestSetsTabStopState() {
    testSubject =
        new TabStopsRequestFulfiller(responseWriter, focusVisualizationStateManager, true);
    testSubject.fulfillRequest(onRequestFulfilled);

    verify(focusVisualizationStateManager).setState(true);
    verify(responseWriter).writeSuccessfulResponse("");
    verify(onRequestFulfilled).run();
    Assert.assertTrue(testSubject.isBlockingRequest());
  }
}
