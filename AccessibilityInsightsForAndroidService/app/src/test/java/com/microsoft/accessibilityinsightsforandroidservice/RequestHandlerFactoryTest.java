// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.net.Socket;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RequestHandlerFactoryTest {

  @Mock ScreenshotController screenshotController;
  @Mock AxeScanner axeScanner;
  @Mock ATFAScanner atfaScanner;
  @Mock RootNodeFinder rootNodeFinder;
  @Mock EventHelper eventHelper;
  @Mock DeviceConfigFactory deviceConfigFactory;
  @Mock Socket socket;
  @Mock ResponseWriter responseWriter;
  @Mock RequestHandlerImplFactory requestHandlerImplFactory;
  @Mock FocusVisualizationStateManager focusVisualizationStateManager;
  @Mock ResultV1Serializer resultSerializerV1;
  @Mock ResultsV2ContainerSerializer resultsV2ContainerSerializer;

  RequestHandlerFactory testSubject;

  @Before
  public void prepare() {
    testSubject =
        new RequestHandlerFactory(
            screenshotController,
            rootNodeFinder,
            eventHelper,
            axeScanner,
            atfaScanner,
            deviceConfigFactory,
            requestHandlerImplFactory,
            focusVisualizationStateManager,
            resultSerializerV1,
            resultsV2ContainerSerializer);
  }

  @Test
  public void createsResultV1RequestHandler() {
    tryCreateRequestHandler("GET /AccessibilityInsights/result something else");
    verify(requestHandlerImplFactory)
        .createRequestHandler(
            any(SocketHolder.class),
            any(ResultV1RequestFulfiller.class),
            eq("processResultRequest"),
            eq("*** About to process scan request (v1)"));
  }

  @Test
  public void createsResultV2RequestHandler() {
    tryCreateRequestHandler("GET /AccessibilityInsights/result_v2 something else");
    verify(requestHandlerImplFactory)
        .createRequestHandler(
            any(SocketHolder.class),
            any(ResultV2RequestFulfiller.class),
            eq("processResultRequest"),
            eq("*** About to process scan request (v2)"));
  }

  @Test
  public void createsConfigRequestHandler() {
    tryCreateRequestHandler("GET /AccessibilityInsights/config something else");
    verify(requestHandlerImplFactory)
        .createRequestHandler(
            any(SocketHolder.class),
            any(ConfigRequestFulfiller.class),
            eq("processConfigRequest"),
            eq("*** About to process config request"));
  }

  @Test
  public void createEnableFocusTrackingRequestHandler() {
    tryCreateRequestHandler("GET /AccessibilityInsights/FocusTracking/Enable something else");
    verify(requestHandlerImplFactory)
        .createRequestHandler(
            any(SocketHolder.class),
            any(TabStopsRequestFulfiller.class),
            eq("processFocusTrackingEnableRequest"),
            eq("*** About to process focus tracking enable request"));
  }

  @Test
  public void createDisableFocusTrackingRequestHandler() {
    tryCreateRequestHandler("GET /AccessibilityInsights/FocusTracking/Disable something else");
    verify(requestHandlerImplFactory)
        .createRequestHandler(
            any(SocketHolder.class),
            any(TabStopsRequestFulfiller.class),
            eq("processFocusTrackingDisableRequest"),
            eq("*** About to process focus tracking disable request"));
  }

  @Test
  public void createResetFocusTrackingRequestHandler() {
    tryCreateRequestHandler("GET /AccessibilityInsights/FocusTracking/Reset something else");
    verify(requestHandlerImplFactory)
        .createRequestHandler(
            any(SocketHolder.class),
            any(TabStopsRequestFulfiller.class),
            eq("processFocusTrackingResetRequest"),
            eq("*** About to process focus tracking reset request"));
  }

  @Test
  public void createsUnrecognizedRequestHandler() {
    String requestString = "some invalid request";
    tryCreateRequestHandler(requestString);
    verify(requestHandlerImplFactory)
        .createRequestHandler(
            any(SocketHolder.class),
            any(UnrecognizedRequestFulfiller.class),
            eq("processUnrecognizedRequest"),
            eq("*** About to return 404: " + requestString));
  }

  private RequestHandler tryCreateRequestHandler(String request) {
    RequestHandler handler = null;
    try {
      handler = testSubject.createHandlerForRequest(socket, request, responseWriter);
    } catch (Exception e) {
      Assert.fail("Exception thrown when creating handler");
    }
    return handler;
  }
}
