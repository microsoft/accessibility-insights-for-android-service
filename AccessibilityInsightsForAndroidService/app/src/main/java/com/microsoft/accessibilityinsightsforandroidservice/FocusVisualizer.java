// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static android.content.Context.WINDOW_SERVICE;
import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOWS_CHANGED;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.View;
import android.view.Window;
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
  private FocusCanvasView focusCanvasView;

  public FocusVisualizer(Context context) {
    // TODO: subscribe FocusVisualizer to AccessibilityEventDispatcher
    this.context = context;
    this.focusElementHighlights = new ArrayList<>();
    this.focusElementLines = new ArrayList<>();
    this.tabStopCount = 0;
    this.windowManager = getWindowManager();
    this.styles = new FocusVisualizerStyles();
    this.focusCanvasView = this.createFocusCanvasView();
    //TODO: Check rotation of device here. Does it still work or do we need to move this out of the constructor?
  }

  public void HandleAccessibilityRedrawEvent(AccessibilityEvent event) {
    Log.v("Redraw Event!", event.toString());
  }

  public void HandleAccessibilityFocusEvent(AccessibilityEvent event) {
    Log.v("Focus Event!", event.toString());
    tabStopCount++;

    AccessibilityNodeInfo eventSource = event.getSource();
    AccessibilityNodeInfo previousEventSource = this.getPreviousEventSource();

    this.createFocusElementHighlight(eventSource);
    this.createFocusElementLine(eventSource, previousEventSource);

    if(focusElementHighlights.size() > 1){
      this.setPreviousElementHighlightNonCurrent(focusElementHighlights.get(focusElementHighlights.size() - 2));
    }
    if(focusElementLines.size() > 1){
      this.setPreviousLineNonCurrent(focusElementLines.get(focusElementLines.size() - 2));
    }

    this.focusCanvasView.setFocusElementHighlights(this.focusElementHighlights);
    this.focusCanvasView.setFocusElementLines(this.focusElementLines);

    this.focusCanvasView.redraw();

  }

  private void setPreviousLineNonCurrent(FocusElementLine line){
    line.setPaint(this.styles.getNonCurrentLinePaints());
  }

  private void setPreviousElementHighlightNonCurrent(FocusElementHighlight focusElementHighlight){
    focusElementHighlight.setPaints(this.styles.getNonCurrentElementPaints());
  }

  private void createFocusElementLine(
      AccessibilityNodeInfo eventSource, AccessibilityNodeInfo previousEventSource) {
    FocusElementLine focusElementLine =
        new FocusElementLine(eventSource, previousEventSource, this.styles.getCurrentLinePaints(), focusCanvasView);
    focusElementLines.add(focusElementLine);
  }

  private FocusCanvasView createFocusCanvasView(){
    FocusCanvasView focusCanvasView = new FocusCanvasView(context);
    WindowManager.LayoutParams layoutParams = this.getLayoutParams(this.windowManager);
    this.windowManager.addView(focusCanvasView, layoutParams);
    return focusCanvasView;
  }


  private void createFocusElementHighlight(AccessibilityNodeInfo eventSource) {
    FocusElementHighlight focusElementHighlight =
        new FocusElementHighlight(
            eventSource,
            this.styles.getCurrentElementPaints(),
            styles.focusElementHighlightRadius,
            tabStopCount,
            focusCanvasView);
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
    focusElementHighlights = new ArrayList<>();
    focusElementLines = new ArrayList<>();
    focusCanvasView.redraw();
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

  private void updateLayoutParams(){
    WindowManager.LayoutParams layoutParams = getLayoutParams(this.windowManager);
    this.windowManager.updateViewLayout(this.focusCanvasView, layoutParams);
  }

  private void updateDrawingsWithNewCoordinates(){
    for(int i = 0; i < focusElementHighlights.size(); i++){
      focusElementHighlights.get(i).updateWithNewCoordinates();
    }
    for(int i = 0; i < focusElementLines.size(); i++){
      focusElementLines.get(i).updateWithNewCoordinates();
    }
    this.focusCanvasView.redraw();
  }

  public void orientationChangedHandler() {
    Log.v("Orientation changed", "");
    this.updateLayoutParams();
//    this.updateDrawingsWithNewCoordinates();
    this.resetVisualizations();
  }
}
