// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.util.Log;

public class TabStopsRequestFulfiller implements RequestFulfiller {
  private static final String TAG = "TabStopsRequestFulfiller";
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
    Log.v(TAG, "about to set state");
    focusVisualizationStateManager.setState(requestValue);
    Log.v(TAG, "about to write successful response");
    responseWriter.writeSuccessfulResponse("");
    onRequestFulfilled.run();
  }

  @Override
  public boolean isBlockingRequest() {
    return true;
  }
}
