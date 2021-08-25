// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.os.CancellationSignal;

public class TabStopsRequestFulfiller implements RequestFulfiller {
  private final FocusVisualizationStateManager focusVisualizationStateManager;
  private final boolean requestValue;

  public TabStopsRequestFulfiller(
      FocusVisualizationStateManager focusVisualizationStateManager, boolean requestValue) {
    this.focusVisualizationStateManager = focusVisualizationStateManager;
    this.requestValue = requestValue;
  }

  @Override
  public String fulfillRequest(CancellationSignal cancellationSignal) {
    focusVisualizationStateManager.setState(requestValue);
    return "";
  }
}
