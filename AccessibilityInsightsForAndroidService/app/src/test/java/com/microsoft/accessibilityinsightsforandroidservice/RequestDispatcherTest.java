// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import android.os.CancellationSignal;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
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

  MockedStatic<Logger> loggerStaticMock;

  @Before
  public void prepare() {
    loggerStaticMock = Mockito.mockStatic(Logger.class);
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

  @After
  public void cleanUp() {
    loggerStaticMock.close();
  }

  private void setupMockRequestFulfiller() throws Exception {
    testSubject = Mockito.spy(testSubject);
    when(testSubject.getRequestFulfiller("mock method")).thenReturn(requestFulfillerMock);
    when(requestFulfillerMock.fulfillRequest(cancellationSignal)).thenReturn("mock response");
  }

  @Test
  public void requestLogsMethod() throws Exception {
    setupMockRequestFulfiller();

    testSubject.request("mock method", cancellationSignal);

    loggerStaticMock.verify(
        () -> Logger.logVerbose("RequestDispatcher", "Handling request for method mock method"));
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
