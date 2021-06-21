// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import java.net.Socket;

public class RequestHandlerFactory {

  private final ScreenshotController screenshotController;
  private final AxeScanner axeScanner;
  private final ATFAScanner atfaScanner;
  private final RootNodeFinder rootNodeFinder;
  private final EventHelper eventHelper;
  private final DeviceConfigFactory deviceConfigFactory;
  private final RequestHandlerImplFactory requestHandlerImplFactory;
  private final FocusVisualizationStateManager focusVisualizationStateManager;
  private final ResultV1Serializer resultV1Serializer;
  private final ResultsV2ContainerSerializer resultsV2ContainerSerializer;

  public RequestHandlerFactory(
      ScreenshotController screenshotController,
      RootNodeFinder rootNodeFinder,
      EventHelper eventHelper,
      AxeScanner axeScanner,
      ATFAScanner atfaScanner,
      DeviceConfigFactory deviceConfigFactory,
      RequestHandlerImplFactory requestHandlerImplFactory,
      FocusVisualizationStateManager focusVisualizationStateManager,
      ResultV1Serializer resultV1Serializer,
      ResultsV2ContainerSerializer resultsV2ContainerSerializer) {
    this.screenshotController = screenshotController;
    this.axeScanner = axeScanner;
    this.atfaScanner = atfaScanner;
    this.rootNodeFinder = rootNodeFinder;
    this.eventHelper = eventHelper;
    this.deviceConfigFactory = deviceConfigFactory;
    this.requestHandlerImplFactory = requestHandlerImplFactory;
    this.focusVisualizationStateManager = focusVisualizationStateManager;
    this.resultV1Serializer = resultV1Serializer;
    this.resultsV2ContainerSerializer = resultsV2ContainerSerializer;
  }

  public RequestHandler createHandlerForRequest(
      Socket socket, String requestString, ResponseWriter responseWriter) {
    SocketHolder socketHolder = new SocketHolder(socket);
    if (requestString != null) {
      if (requestString.startsWith("GET /AccessibilityInsights/result_v2 ")) {
        ResultV2RequestFulfiller resultV2RequestFulfiller =
            new ResultV2RequestFulfiller(
                responseWriter,
                rootNodeFinder,
                eventHelper,
                axeScanner,
                atfaScanner,
                screenshotController,
                resultsV2ContainerSerializer);
        return requestHandlerImplFactory.createRequestHandler(
            socketHolder,
            resultV2RequestFulfiller,
            "processResultRequest",
            "*** About to process scan request (v2)");
      }
      if (requestString.startsWith("GET /AccessibilityInsights/result ")) {
        ResultV1RequestFulfiller resultV1RequestFulfiller =
            new ResultV1RequestFulfiller(
                responseWriter,
                rootNodeFinder,
                eventHelper,
                axeScanner,
                screenshotController,
                resultV1Serializer);
        return requestHandlerImplFactory.createRequestHandler(
            socketHolder,
            resultV1RequestFulfiller,
            "processResultRequest",
            "*** About to process scan request (v1)");
      }
      if (requestString.startsWith("GET /AccessibilityInsights/config ")) {
        ConfigRequestFulfiller configRequestFulfiller =
            new ConfigRequestFulfiller(
                responseWriter, rootNodeFinder, eventHelper, deviceConfigFactory);
        return requestHandlerImplFactory.createRequestHandler(
            socketHolder,
            configRequestFulfiller,
            "processConfigRequest",
            "*** About to process config request");
      }
      if (requestString.startsWith("GET /AccessibilityInsights/FocusTracking/Enable ")) {
        TabStopsRequestFulfiller tabStopsRequestFulfiller =
            new TabStopsRequestFulfiller(responseWriter, focusVisualizationStateManager, true);
        return requestHandlerImplFactory.createRequestHandler(
            socketHolder,
            tabStopsRequestFulfiller,
            "processFocusTrackingEnableRequest",
            "*** About to process focus tracking enable request");
      }
      if (requestString.startsWith("GET /AccessibilityInsights/FocusTracking/Disable ")) {
        TabStopsRequestFulfiller tabStopsRequestFulfiller =
            new TabStopsRequestFulfiller(responseWriter, focusVisualizationStateManager, false);
        return requestHandlerImplFactory.createRequestHandler(
            socketHolder,
            tabStopsRequestFulfiller,
            "processFocusTrackingDisableRequest",
            "*** About to process focus tracking disable request");
      }
      if (requestString.startsWith("GET /AccessibilityInsights/FocusTracking/Reset ")) {
        TabStopsRequestFulfiller tabStopsRequestFulfiller =
            new TabStopsRequestFulfiller(responseWriter, focusVisualizationStateManager, false);
        return requestHandlerImplFactory.createRequestHandler(
            socketHolder,
            tabStopsRequestFulfiller,
            "processFocusTrackingResetRequest",
            "*** About to process focus tracking reset request");
      }
    }
    UnrecognizedRequestFulfiller unrecognizedRequestFulfiller =
        new UnrecognizedRequestFulfiller(responseWriter, requestString);
    return requestHandlerImplFactory.createRequestHandler(
        socketHolder,
        unrecognizedRequestFulfiller,
        "processUnrecognizedRequest",
        "*** About to return 404: " + requestString);
  }
}
