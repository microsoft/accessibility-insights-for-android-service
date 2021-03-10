// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.ArrayList;
import java.util.HashMap;

public class FocusVisualizer {
  private static final String TAG = "FocusVisualizer";
  private ArrayList<FocusElementHighlight> focusElementHighlights;
  private ArrayList<FocusElementLine> focusElementLines;
  private int tabStopCount;
  private Context context;
  private WindowManager windowManager;
  private FocusVisualizerStyles styles;
  private HashMap<String, Paint> currentElementPaints;
  private HashMap<String, Paint> nonCurrentElementPaints;
  private HashMap<String, Paint> currentLinePaints;
  private HashMap<String, Paint> nonCurrentLinePaints;
  private WindowManager.LayoutParams layoutParams;

  public FocusVisualizer(Context context) {
    // TODO: subscribe FocusVisualizer to AccessibilityEventDispatcher
    this.context = context;
    this.focusElementHighlights = new ArrayList<>();
    this.focusElementLines = new ArrayList<>();
    this.tabStopCount = 0;
    this.windowManager = getWindowManager();
    this.styles = new FocusVisualizerStyles();
    this.currentElementPaints = styles.getCurrentElementPaints();
    this.nonCurrentElementPaints = styles.getNonCurrentElementPaints();
    this.currentLinePaints = styles.getCurrentLinePaints();
    this.nonCurrentLinePaints = styles.getNonCurrentLinePaints();
    this.layoutParams = getLayoutParams(this.windowManager);
  }

  public void HandleAccessibilityRedrawEvent(AccessibilityEvent event) {
    Log.v("Redraw Event!", event.toString());
  }

  public void HandleAccessibilityFocusEvent(AccessibilityEvent event) {
    tabStopCount++;

    AccessibilityNodeInfo eventSource = event.getSource();
    AccessibilityNodeInfo previousEventSource = this.getPreviousEventSource();

    if (focusElementLines.size() > 0) {
      focusElementLines.get(focusElementLines.size() - 1).setNonCurrent();
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

  private FocusElementHighlight getPreviousFocusElementHighlight() {
    return focusElementHighlights.get(focusElementHighlights.size() - 2);
  }

  private void createFocusElementLine(
      AccessibilityNodeInfo eventSource, AccessibilityNodeInfo previousEventSource) {
    FocusElementLine focusElementLine =
        new FocusElementLine(
            context, eventSource, previousEventSource, currentLinePaints, nonCurrentLinePaints);
    this.windowManager.addView(focusElementLine, this.layoutParams);
    focusElementLines.add(focusElementLine);
  }

  private void createNonCurrentFocusElementHighlight(FocusElementHighlight previousElement) {
    FocusElementHighlight newNonCurrentHighlight =
        new FocusElementHighlight(
            context,
            previousElement.getEventSource(),
            currentElementPaints,
            nonCurrentElementPaints,
            styles.focusElementHighlightRadius,
            previousElement.getTabStopCount(),
            false);
    this.windowManager.addView(newNonCurrentHighlight, this.layoutParams);
    focusElementHighlights.set(focusElementHighlights.size() - 2, newNonCurrentHighlight);
    previousElement.reset();
  }

  private void createCurrentFocusElementHighlight(AccessibilityNodeInfo eventSource) {
    FocusElementHighlight focusElementHighlight =
        new FocusElementHighlight(
            context,
            eventSource,
            currentElementPaints,
            nonCurrentElementPaints,
            styles.focusElementHighlightRadius,
            tabStopCount,
            true);
    this.windowManager.addView(focusElementHighlight, this.layoutParams);
    focusElementHighlights.add(focusElementHighlight);
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
      focusElementHighlights.get(i).reset();
    }
    for (int i = 0; i < focusElementLines.size(); i++) {
      focusElementLines.get(i).reset();
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
