// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.ArrayList;

public class FocusVisualizer {
  private static final String TAG = "FocusVisualizer";
  private ArrayList<FocusElementHighlight> focusElementHighlights;
  private ArrayList<FocusElementLine> focusElementLines;
  private int tabStopCount;
  private Context context;
  private WindowManager windowManager;
  private FocusVisualizerStyles styles;
  private WindowManager.LayoutParams layoutParams;

  public FocusVisualizer(Context context) {
    // TODO: subscribe FocusVisualizer to AccessibilityEventDispatcher
    this.context = context;
    this.focusElementHighlights = new ArrayList<>();
    this.focusElementLines = new ArrayList<>();
    this.tabStopCount = 0;
    this.windowManager = getWindowManager();
    this.styles = new FocusVisualizerStyles();
    this.layoutParams = getLayoutParams(this.windowManager);
    //TODO: Check rotation of device here. Does it still work or do we need to move this out of the constructor?
  }

  public void HandleAccessibilityRedrawEvent(AccessibilityEvent event) {
    Log.v("Redraw Event!", event.toString());
  }

  public void HandleAccessibilityFocusEvent(AccessibilityEvent event) {
    tabStopCount++;

    AccessibilityNodeInfo eventSource = event.getSource();
    AccessibilityNodeInfo previousEventSource = this.getPreviousEventSource();

    if (focusElementLines.size() > 0) {
      this.setLineNonCurrent(focusElementLines.get(focusElementLines.size() - 1));
    }

    if (focusElementHighlights.size() > 0) {
      this.createFocusElementLine(eventSource, previousEventSource);
      this.createCurrentFocusElementHighlight(eventSource);

      FocusElementHighlight previousElement = getPreviousFocusElementHighlight();
      this.createNonCurrentFocusElementHighlight(previousElement);

      return;
    }

    this.createCurrentFocusElementHighlight(eventSource);
  }

  private void setLineNonCurrent(FocusElementLine line){
    line.setPaint(this.styles.getNonCurrentLinePaints());
  }

  private void removeLine(FocusElementLine line){
    line.setPaint(this.styles.getTransparentLinePaints());
  }

  private FocusElementHighlight getPreviousFocusElementHighlight() {
    return focusElementHighlights.get(focusElementHighlights.size() - 2);
  }

  private void createFocusElementLine(
      AccessibilityNodeInfo eventSource, AccessibilityNodeInfo previousEventSource) {
    FocusElementLine focusElementLine =
        new FocusElementLine(
            context, eventSource, previousEventSource, this.styles.getCurrentLinePaints());
    this.windowManager.addView(focusElementLine, this.layoutParams);
    focusElementLines.add(focusElementLine);
  }

  private void createNonCurrentFocusElementHighlight(FocusElementHighlight previousElement) {
    FocusElementHighlight newNonCurrentHighlight =
        new FocusElementHighlight(
            context,
            previousElement.getEventSource(),
            this.styles.getNonCurrentElementPaints(),
            styles.focusElementHighlightRadius,
            previousElement.getTabStopCount());
    this.windowManager.addView(newNonCurrentHighlight, this.layoutParams);
    focusElementHighlights.set(focusElementHighlights.size() - 2, newNonCurrentHighlight);
    this.removeFocusElementHighlight(previousElement);
  }

  private void createCurrentFocusElementHighlight(AccessibilityNodeInfo eventSource) {
    FocusElementHighlight focusElementHighlight =
        new FocusElementHighlight(
            context,
            eventSource,
            this.styles.getCurrentElementPaints(),
            styles.focusElementHighlightRadius,
            tabStopCount);
    this.windowManager.addView(focusElementHighlight, this.layoutParams);
    focusElementHighlights.add(focusElementHighlight);
  }

  private void removeFocusElementHighlight(FocusElementHighlight highlight){
    highlight.setPaint(this.styles.getTransparentElementPaints());
  }

  public WindowManager getWindowManager() {
    if (windowManager != null) {
      return windowManager;
    }

    return (WindowManager) this.context.getSystemService(WINDOW_SERVICE);
  }

  private AccessibilityNodeInfo getPreviousEventSource() {
    if (focusElementHighlights.size() == 0) {
      return null;
    }
    return focusElementHighlights.get(focusElementHighlights.size() - 1).getEventSource();
  }

  private void resetVisualizations() {
    tabStopCount = 0;

    for (int i = 0; i < focusElementHighlights.size(); i++) {
      this.removeFocusElementHighlight(focusElementHighlights.get(i));
    }
    for (int i = 0; i < focusElementLines.size(); i++) {
      this.removeLine(focusElementLines.get(i));
    }
    focusElementHighlights = new ArrayList<>();
    focusElementLines = new ArrayList<>();
  }

  private WindowManager.LayoutParams getLayoutParams(WindowManager windowManager) {
    Display display = windowManager.getDefaultDisplay();
    DisplayMetrics displayMetrics = new DisplayMetrics();
    display.getRealMetrics(displayMetrics);
    return new WindowManager.LayoutParams(
        displayMetrics.widthPixels,
        displayMetrics.heightPixels,
        WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        PixelFormat.TRANSLUCENT);
  }
}
