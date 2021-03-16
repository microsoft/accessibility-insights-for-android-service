// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.content.Context;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.ArrayList;

public class FocusVisualizer {
  private static final String TAG = "FocusVisualizer";
  private ArrayList<FocusElementHighlight> focusElementHighlights;
  private ArrayList<FocusElementLine> focusElementLines;
  private int tabStopCount;
  private Context context;
  private FocusVisualizerStyles styles;
  private FocusCanvasView focusCanvasView;

  public FocusVisualizer(
      Context context,
      FocusVisualizerStyles focusVisualizerStyles,
      FocusCanvasView focusCanvasView) {
    this.context = context;
    this.focusElementHighlights = new ArrayList<>();
    this.focusElementLines = new ArrayList<>();
    this.tabStopCount = 0;
    this.styles = focusVisualizerStyles;
    this.focusCanvasView = focusCanvasView;
  }

  public void HandleAccessibilityRedrawEvent(AccessibilityEvent event) {}

  public void HandleAccessibilityFocusEvent(AccessibilityEvent event) {
    tabStopCount++;

    AccessibilityNodeInfo eventSource = event.getSource();
    AccessibilityNodeInfo previousEventSource = this.getPreviousEventSource();

    this.createFocusElementHighlight(eventSource);
    this.createFocusElementLine(eventSource, previousEventSource);

    if (this.focusElementHighlights.size() > 1) {
      this.setPreviousElementHighlightNonCurrent(
          this.focusElementHighlights.get(this.focusElementHighlights.size() - 2));
    }
    if (focusElementLines.size() > 1) {
      this.setPreviousLineNonCurrent(this.focusElementLines.get(this.focusElementLines.size() - 2));
    }

    this.focusCanvasView.setFocusElementHighlights(this.focusElementHighlights);
    this.focusCanvasView.setFocusElementLines(this.focusElementLines);

    this.focusCanvasView.redraw();
  }

  private void setPreviousLineNonCurrent(FocusElementLine line) {
    line.setPaint(this.styles.getNonCurrentLinePaints());
  }

  private void setPreviousElementHighlightNonCurrent(FocusElementHighlight focusElementHighlight) {
    focusElementHighlight.setPaints(this.styles.getNonCurrentElementPaints());
  }

  private void createFocusElementLine(
      AccessibilityNodeInfo eventSource, AccessibilityNodeInfo previousEventSource) {
    FocusElementLine focusElementLine =
        new FocusElementLine(
            eventSource,
            previousEventSource,
            this.styles.getCurrentLinePaints(),
            this.focusCanvasView);
    this.focusElementLines.add(focusElementLine);
  }

  private void createFocusElementHighlight(AccessibilityNodeInfo eventSource) {
    FocusElementHighlight focusElementHighlight =
        new FocusElementHighlight(
            eventSource,
            this.styles.getCurrentElementPaints(),
            this.styles.focusElementHighlightRadius,
            this.tabStopCount,
            this.focusCanvasView);
    this.focusElementHighlights.add(focusElementHighlight);
  }

  private AccessibilityNodeInfo getPreviousEventSource() {
    if (this.focusElementHighlights.size() == 0) {
      return null;
    }
    return this.focusElementHighlights.get(this.focusElementHighlights.size() - 1).getEventSource();
  }

  public void resetVisualizations() {
    this.tabStopCount = 0;
    this.focusElementHighlights = new ArrayList<>();
    this.focusElementLines = new ArrayList<>();
    this.focusCanvasView.redraw();
  }

  private void updateDrawingsWithNewCoordinates() {
    for (int i = 0; i < this.focusElementHighlights.size(); i++) {
      this.focusElementHighlights.get(i).updateWithNewCoordinates();
    }
    for (int i = 0; i < this.focusElementLines.size(); i++) {
      this.focusElementLines.get(i).updateWithNewCoordinates();
    }
    this.focusCanvasView.redraw();
  }

  public void orientationChangedHandler() {
    this.updateDrawingsWithNewCoordinates();
  }
}
