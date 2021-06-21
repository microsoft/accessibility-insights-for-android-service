// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import com.google.gson.GsonBuilder;
import java.net.Socket;

public class ResponseThreadFactory {
  private final ResponseWriterFactory responseWriterFactory;
  private final RequestReaderFactory requestReaderFactory;
  private final RequestHandlerFactory requestHandlerFactory;

  public ResponseThreadFactory(
      ScreenshotController screenshotController,
      EventHelper eventHelper,
      AxeScanner axeScanner,
      ATFAScanner atfaScanner,
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
            atfaScanner,
            deviceConfigFactory,
            new RequestHandlerImplFactory(),
            focusVisualizationStateManager,
            new ResultV1Serializer(),
            new ResultsV2ContainerSerializer(
                new ATFARulesSerializer(),
                new ATFAResultsSerializer(new GsonBuilder()),
                new GsonBuilder()));
  }

  public ResponseThread createResponseThread(Socket socket) {
    return new ResponseThread(
        socket, responseWriterFactory, requestReaderFactory, requestHandlerFactory);
  }
}
