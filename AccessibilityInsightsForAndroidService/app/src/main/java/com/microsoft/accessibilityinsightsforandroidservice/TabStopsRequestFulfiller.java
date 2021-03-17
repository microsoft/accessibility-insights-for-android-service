// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

public class TabStopsRequestFulfiller implements RequestFulfiller {
  private ResponseWriter responseWriter;
  private FocusVisualizationStateManager focusVisualizationStateManager;
  private boolean requestValue;

  public TabStopsRequestFulfiller(
      ResponseWriter responseWriter,
      FocusVisualizationStateManager focusVisualizationStateManager,
      boolean requestValue) {
    this.responseWriter = responseWriter;
    this.focusVisualizationStateManager = focusVisualizationStateManager;
    this.requestValue = requestValue;
  }

  @Override
  public void fulfillRequest(RunnableFunction onRequestFulfilled) {
    focusVisualizationStateManager.setState(requestValue);
    responseWriter.writeSuccessfulResponse("");
    onRequestFulfilled.run();
  }

  @Override
  public boolean isBlockingRequest() {
    return true;
  }
}
