// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.view.accessibility.AccessibilityNodeInfo;
import java.util.ArrayList;

public class FocusVisualizer {
  private ArrayList<FocusElementHighlight> focusElementHighlights;
  private ArrayList<FocusElementLine> focusElementLines;
  private int tabStopCount;
  private FocusVisualizerStyles styles;
  private FocusVisualizationCanvas focusVisualizationCanvas;

  public FocusVisualizer(
      FocusVisualizerStyles focusVisualizerStyles,
      FocusVisualizationCanvas focusVisualizationCanvas) {
    this.focusElementHighlights = new ArrayList<>();
    this.focusElementLines = new ArrayList<>();
    this.tabStopCount = 0;
    this.styles = focusVisualizerStyles;
    this.focusVisualizationCanvas = focusVisualizationCanvas;
  }

  public void refreshHighlights() {
    this.focusVisualizationCanvas.redraw();
  }

  public void addNewFocusedElement(AccessibilityNodeInfo eventSource) {
    tabStopCount++;

    AccessibilityNodeInfo previousEventSource = this.getPreviousEventSource();

    if (this.focusElementHighlights.size() > 0) {
      this.setPreviousElementHighlightNonCurrent(
          this.focusElementHighlights.get(this.focusElementHighlights.size() - 1));
    }
    if (focusElementLines.size() > 0) {
      this.setPreviousLineNonCurrent(this.focusElementLines.get(this.focusElementLines.size() - 1));
    }

    this.createFocusElementHighlight(eventSource);
    this.createFocusElementLine(eventSource, previousEventSource);

    this.setDrawItemsAndRedraw();
  }

  public void resetVisualizations() {
    this.tabStopCount = 0;
    this.focusElementHighlights.clear();
    this.focusElementLines.clear();
    this.setDrawItemsAndRedraw();
  }

  private void setPreviousLineNonCurrent(FocusElementLine line) {
    line.setPaint(this.styles.getNonCurrentLinePaints());
  }

  private void setPreviousElementHighlightNonCurrent(FocusElementHighlight focusElementHighlight) {
    focusElementHighlight.setAsNonCurrentElement();
    focusElementHighlight.setPaints(this.styles.getNonCurrentElementPaints());
  }

  private void createFocusElementLine(
      AccessibilityNodeInfo eventSource, AccessibilityNodeInfo previousEventSource) {
    FocusElementLine focusElementLine =
        new FocusElementLine(
            eventSource,
            previousEventSource,
            this.styles.getCurrentLinePaints(),
            this.focusVisualizationCanvas);
    this.focusElementLines.add(focusElementLine);
  }

  private void createFocusElementHighlight(AccessibilityNodeInfo eventSource) {
    FocusElementHighlight focusElementHighlight =
        new FocusElementHighlight(
            eventSource,
            this.styles.getCurrentElementPaints(),
            this.styles.focusElementHighlightRadius,
            this.tabStopCount,
            this.focusVisualizationCanvas);
    this.focusElementHighlights.add(focusElementHighlight);
  }

  private AccessibilityNodeInfo getPreviousEventSource() {
    if (this.focusElementHighlights.size() == 0) {
      return null;
    }
    return this.focusElementHighlights.get(this.focusElementHighlights.size() - 1).getEventSource();
  }

  private void setDrawItemsAndRedraw() {
    this.focusVisualizationCanvas.setDrawItems(this.focusElementHighlights, this.focusElementLines);
    this.focusVisualizationCanvas.redraw();
  }
}
