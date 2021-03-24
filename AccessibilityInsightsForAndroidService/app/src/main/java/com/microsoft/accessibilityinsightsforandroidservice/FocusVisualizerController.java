// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.view.accessibility.AccessibilityEvent;

public class FocusVisualizerController {
  private FocusVisualizer focusVisualizer;
  private FocusVisualizationStateManager focusVisualizationStateManager;
  private UIThreadRunner uiThreadRunner;

  public FocusVisualizerController(
      FocusVisualizer focusVisualizer,
      FocusVisualizationStateManager focusVisualizationStateManager,
      UIThreadRunner uiThreadRunner) {
    this.focusVisualizer = focusVisualizer;
    this.focusVisualizationStateManager = focusVisualizationStateManager;
    this.uiThreadRunner = uiThreadRunner;
    this.focusVisualizationStateManager.subscribe(this::onFocusVisualizationStateChange);
  }

  public void onFocusEvent(AccessibilityEvent event) {
    if (focusVisualizationStateManager.getState() == false) {
      return;
    }

    focusVisualizer.HandleAccessibilityFocusEvent(event);
  }

  public void onRedrawEvent(AccessibilityEvent event) {
    if (focusVisualizationStateManager.getState() == false) {
      return;
    }

    focusVisualizer.HandleAccessibilityRedrawEvent(event);
  }

  private void onFocusVisualizationStateChange(boolean newState) {
    if (newState) {
      return;
    }

    uiThreadRunner.run(focusVisualizer::resetVisualizations);
  }
}
