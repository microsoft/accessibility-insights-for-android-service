// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import android.os.CancellationSignal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Logger.class})
public class RequestDispatcherTest {
  @Mock ScreenshotController screenshotController;
  @Mock AxeScanner axeScanner;
  @Mock ATFAScanner atfaScanner;
  @Mock RootNodeFinder rootNodeFinder;
  @Mock EventHelper eventHelper;
  @Mock DeviceConfigFactory deviceConfigFactory;
  @Mock FocusVisualizationStateManager focusVisualizationStateManager;
  @Mock ResultsV2ContainerSerializer resultsV2ContainerSerializer;
  @Mock CancellationSignal cancellationSignal;

  @Mock RequestFulfiller requestFulfillerMock;
  RequestDispatcher testSubject;

  @Before
  public void prepare() {
    mockStatic(Logger.class);
    testSubject =
        new RequestDispatcher(
            rootNodeFinder,
            screenshotController,
            eventHelper,
            axeScanner,
            atfaScanner,
            deviceConfigFactory,
            focusVisualizationStateManager,
            resultsV2ContainerSerializer);
  }

  private void setupMockRequestFulfiller() throws Exception {
    testSubject = PowerMockito.spy(testSubject);
    when(testSubject.getRequestFulfiller("mock method")).thenReturn(requestFulfillerMock);
    when(requestFulfillerMock.fulfillRequest(cancellationSignal)).thenReturn("mock response");
  }

  @Test
  public void requestLogsMethod() throws Exception {
    setupMockRequestFulfiller();

    testSubject.request("mock method", cancellationSignal);

    verifyStatic(Logger.class);
    Logger.logVerbose("RequestDispatcher", "Handling request for method mock method");
  }

  @Test
  public void requestDispatchesToGetRequestFulfiller() throws Exception {
    // This is testing an implementation detail, but doing so vastly simplifies all the test cases
    // to follow
    setupMockRequestFulfiller();

    String response = testSubject.request("mock method", cancellationSignal);

    assertEquals("mock response", response);
  }

  @Test
  public void requestConfigDispatchesToConfigRequestFulfiller() {
    assertTrue(testSubject.getRequestFulfiller("/config") instanceof ConfigRequestFulfiller);
  }

  @Test
  public void requestResultDispatchesToResultV2RequestFulfiller() {
    assertTrue(testSubject.getRequestFulfiller("/result") instanceof ResultV2RequestFulfiller);
  }

  @Test
  public void requestFocusTrackingEnableDispatchesToTabStopsRequestFulfiller() {
    assertTrue(
        testSubject.getRequestFulfiller("/FocusTracking/Enable")
            instanceof TabStopsRequestFulfiller);
  }

  @Test
  public void requestFocusTrackingDisableDispatchesToTabStopsRequestFulfiller() {
    assertTrue(
        testSubject.getRequestFulfiller("/FocusTracking/Disable")
            instanceof TabStopsRequestFulfiller);
  }

  @Test
  public void requestFocusTrackingResetDispatchesToTabStopsRequestFulfiller() {
    assertTrue(
        testSubject.getRequestFulfiller("/FocusTracking/Reset")
            instanceof TabStopsRequestFulfiller);
  }

  @Test
  public void requestForUnknownMethodDispatchesToUnrecognizedRequestFulfiller() {
    assertTrue(testSubject.getRequestFulfiller("/unknown") instanceof UnrecognizedRequestFulfiller);
  }
}
