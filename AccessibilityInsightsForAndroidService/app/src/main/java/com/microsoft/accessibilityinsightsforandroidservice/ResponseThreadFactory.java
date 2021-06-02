// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import java.net.Socket;

public class ResponseThreadFactory {
  private final ResponseWriterFactory responseWriterFactory;
  private final RequestReaderFactory requestReaderFactory;
  private final RequestHandlerFactory requestHandlerFactory;

  public ResponseThreadFactory(
      ScreenshotController screenshotController,
      EventHelper eventHelper,
      AxeScanner axeScanner,
      DeviceConfigFactory deviceConfigFactory,
      FocusVisualizationStateManager focusVisualizationStateManager) {
    responseWriterFactory = new ResponseWriterFactory();
    requestReaderFactory = new RequestReaderFactory();
    requestHandlerFactory =
        new RequestHandlerFactory(
            screenshotController,
            new RootNodeFinder(),
            eventHelper,
            axeScanner,
            deviceConfigFactory,
            new RequestHandlerImplFactory(),
            focusVisualizationStateManager,
            new ResultSerializer());
  }

  public ResponseThread createResponseThread(Socket socket) {
    return new ResponseThread(
        socket, responseWriterFactory, requestReaderFactory, requestHandlerFactory);
  }
}
