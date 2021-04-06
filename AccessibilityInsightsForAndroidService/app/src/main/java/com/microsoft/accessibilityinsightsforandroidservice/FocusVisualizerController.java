// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.Date;

public class FocusVisualizerController {
  private FocusVisualizer focusVisualizer;
  private FocusVisualizationStateManager focusVisualizationStateManager;
  private UIThreadRunner uiThreadRunner;
  private WindowManager windowManager;
  private LayoutParamGenerator layoutParamGenerator;
  private FocusVisualizationCanvas focusVisualizationCanvas;
  private AccessibilityNodeInfo lastEventSource;
  private DateProvider dateProvider;
  private Date lastOrientationChange;
  private long maximumOrientationChangeDelay = 1000;

  public FocusVisualizerController(
      FocusVisualizer focusVisualizer,
      FocusVisualizationStateManager focusVisualizationStateManager,
      UIThreadRunner uiThreadRunner,
      WindowManager windowManager,
      LayoutParamGenerator layoutParamGenerator,
      FocusVisualizationCanvas focusVisualizationCanvas,
      DateProvider dateProvider) {
    this.focusVisualizer = focusVisualizer;
    this.focusVisualizationStateManager = focusVisualizationStateManager;
    this.uiThreadRunner = uiThreadRunner;
    this.windowManager = windowManager;
    this.layoutParamGenerator = layoutParamGenerator;
    this.focusVisualizationCanvas = focusVisualizationCanvas;
    this.dateProvider = dateProvider;
    this.focusVisualizationStateManager.subscribe(this::onFocusVisualizationStateChange);
    this.lastOrientationChange = dateProvider.get();
  }

  public void onFocusEvent(AccessibilityEvent event) {
    lastEventSource = event.getSource();
    if (focusVisualizationStateManager.getState() == false
        || ignoreFocusEventDueToRecentOrientationChange()) {
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
    lastOrientationChange = new Date();
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

  private boolean ignoreFocusEventDueToRecentOrientationChange() {
    Date currentTime = dateProvider.get();
    long cur = currentTime.getTime();
    long last = lastOrientationChange.getTime();
    long timeSinceLastOrientationChange = cur - last;
    return timeSinceLastOrientationChange < maximumOrientationChangeDelay;
  }
}
