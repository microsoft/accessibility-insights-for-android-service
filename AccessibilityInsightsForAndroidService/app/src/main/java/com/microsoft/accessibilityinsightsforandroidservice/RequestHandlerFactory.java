// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import java.net.Socket;

public class RequestHandlerFactory {

  private final ScreenshotController screenshotController;
  private final AxeScanner axeScanner;
  private final RootNodeFinder rootNodeFinder;
  private final EventHelper eventHelper;
  private final DeviceConfigFactory deviceConfigFactory;
  private final RequestHandlerImplFactory requestHandlerImplFactory;

  public RequestHandlerFactory(
      ScreenshotController screenshotController,
      RootNodeFinder rootNodeFinder,
      EventHelper eventHelper,
      AxeScanner axeScanner,
      DeviceConfigFactory deviceConfigFactory,
      RequestHandlerImplFactory requestHandlerImplFactory) {
    this.screenshotController = screenshotController;
    this.axeScanner = axeScanner;
    this.rootNodeFinder = rootNodeFinder;
    this.eventHelper = eventHelper;
    this.deviceConfigFactory = deviceConfigFactory;
    this.requestHandlerImplFactory = requestHandlerImplFactory;
  }

  public RequestHandler createHandlerForRequest(
      Socket socket, String requestString, ResponseWriter responseWriter) {
    SocketHolder socketHolder = new SocketHolder(socket);
    if (requestString != null) {
      if (requestString.startsWith("GET /AccessibilityInsights/result ")) {
        ResultRequestFulfiller resultRequestFulfiller =
            new ResultRequestFulfiller(
                responseWriter, rootNodeFinder, eventHelper, axeScanner, screenshotController);
        return requestHandlerImplFactory.createRequestHandler(
            socketHolder,
            resultRequestFulfiller,
            "processResultRequest",
            "*** About to process scan request");
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
