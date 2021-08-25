// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.os.CancellationSignal;
import androidx.annotation.NonNull;

public class RequestDispatcher {
  private static final String TAG = "RequestDispatcher";

  private final RootNodeFinder rootNodeFinder;
  private final ScreenshotController screenshotController;
  private final EventHelper eventHelper;
  private final AxeScanner axeScanner;
  private final ATFAScanner atfaScanner;
  private final DeviceConfigFactory deviceConfigFactory;
  private final FocusVisualizationStateManager focusVisualizationStateManager;
  private final ResultsV2ContainerSerializer resultsV2ContainerSerializer;

  public RequestDispatcher(
      @NonNull RootNodeFinder rootNodeFinder,
      @NonNull ScreenshotController screenshotController,
      @NonNull EventHelper eventHelper,
      @NonNull AxeScanner axeScanner,
      @NonNull ATFAScanner atfaScanner,
      @NonNull DeviceConfigFactory deviceConfigFactory,
      @NonNull FocusVisualizationStateManager focusVisualizationStateManager,
      @NonNull ResultsV2ContainerSerializer resultsV2ContainerSerializer) {
    this.rootNodeFinder = rootNodeFinder;
    this.screenshotController = screenshotController;
    this.eventHelper = eventHelper;
    this.axeScanner = axeScanner;
    this.atfaScanner = atfaScanner;
    this.deviceConfigFactory = deviceConfigFactory;
    this.focusVisualizationStateManager = focusVisualizationStateManager;
    this.resultsV2ContainerSerializer = resultsV2ContainerSerializer;
  }

  public String request(@NonNull String method, @NonNull CancellationSignal cancellationSignal)
      throws Exception {
    Logger.logVerbose(TAG, "Handling request for method " + method);
    return getRequestFulfiller(method).fulfillRequest(cancellationSignal);
  }

  public RequestFulfiller getRequestFulfiller(@NonNull String method) {
    switch (method) {
      case "/config":
        return new ConfigRequestFulfiller(rootNodeFinder, eventHelper, deviceConfigFactory);
      case "/result":
        return new ResultV2RequestFulfiller(
            rootNodeFinder,
            eventHelper,
            axeScanner,
            atfaScanner,
            screenshotController,
            resultsV2ContainerSerializer);
      case "/FocusTracking/Enable":
        return new TabStopsRequestFulfiller(focusVisualizationStateManager, true);
      case "/FocusTracking/Disable": // Intentional fallthrough
      case "/FocusTracking/Reset":
        return new TabStopsRequestFulfiller(focusVisualizationStateManager, false);
      default:
        return new UnrecognizedRequestFulfiller(method);
    }
  }
}
