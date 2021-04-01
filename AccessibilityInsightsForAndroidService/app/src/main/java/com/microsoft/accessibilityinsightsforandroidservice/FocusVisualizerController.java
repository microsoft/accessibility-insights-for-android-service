// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class FocusVisualizerController {
  private FocusVisualizer focusVisualizer;
  private FocusVisualizationStateManager focusVisualizationStateManager;
  private UIThreadRunner uiThreadRunner;
  private WindowManager windowManager;
  private LayoutParamGenerator layoutParamGenerator;
  private FocusVisualizationCanvas focusVisualizationCanvas;
  private AccessibilityNodeInfo lastEventSource;

  public FocusVisualizerController(
      FocusVisualizer focusVisualizer,
      FocusVisualizationStateManager focusVisualizationStateManager,
      UIThreadRunner uiThreadRunner,
      WindowManager windowManager,
      LayoutParamGenerator layoutParamGenerator,
      FocusVisualizationCanvas focusVisualizationCanvas) {
    this.focusVisualizer = focusVisualizer;
    this.focusVisualizationStateManager = focusVisualizationStateManager;
    this.uiThreadRunner = uiThreadRunner;
    this.windowManager = windowManager;
    this.layoutParamGenerator = layoutParamGenerator;
    this.focusVisualizationCanvas = focusVisualizationCanvas;
    this.focusVisualizationStateManager.subscribe(this::onFocusVisualizationStateChange);
  }

  public void onFocusEvent(AccessibilityEvent event) {
    lastEventSource = event.getSource();

    if (focusVisualizationStateManager.getState() == false) {
      return;
    }

    focusVisualizer.addNewFocusedElement(event.getSource());
  }

  public void onRedrawEvent(AccessibilityEvent event) {
    if (focusVisualizationStateManager.getState() == false) {
      return;
    }

    focusVisualizer.refreshHighlights();
  }

  public void onAppChanged(AccessibilityNodeInfo nodeInfo) {
    if (focusVisualizationStateManager.getState() == false) {
      return;
    }

    focusVisualizer.resetVisualizations();
  }

  public void onOrientationChanged(Integer orientation) {
    if (focusVisualizationStateManager.getState() == false) {
      return;
    }

    windowManager.updateViewLayout(focusVisualizationCanvas, layoutParamGenerator.get());
    focusVisualizer.resetVisualizations();
  }

  private void onFocusVisualizationStateChange(boolean enabled) {
    if (enabled) {
      uiThreadRunner.run(this::addFocusVisualizationToScreen);
    } else {
      uiThreadRunner.run(this::removeFocusVisualizationToScreen);
    }
  }

  private void addFocusVisualizationToScreen() {
    if (lastEventSource != null) {
      focusVisualizer.addNewFocusedElement(lastEventSource);
    }
    windowManager.addView(focusVisualizationCanvas, layoutParamGenerator.get());
  }

  private void removeFocusVisualizationToScreen() {
    focusVisualizer.resetVisualizations();
    windowManager.removeView(focusVisualizationCanvas);
  }
}
