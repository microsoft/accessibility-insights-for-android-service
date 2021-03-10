// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.HashMap;

public class FocusElementHighlight extends View {
  private static final String TAG = "FocusElementHighlight";
  private AccessibilityNodeInfo eventSource;
  private int yOffset;
  private int tabStopCount;
  private boolean isCurrent;
  private boolean isReset;
  private int radius;
  private int xCoordinate;
  private int yCoordinate;
  // performance based variables
  private HashMap<String, Paint> currentPaints;
  private HashMap<String, Paint> nonCurrentPaints;
  private Rect rect;

  public FocusElementHighlight(Context context){
    super(context);
  }
  public FocusElementHighlight(Context context, AttributeSet attrs){
    super(context, attrs);
  }
  public FocusElementHighlight(Context context, AttributeSet attrs, int toolInt){
    super(context, attrs, toolInt);
  }
  public FocusElementHighlight(
      Context context,
      AccessibilityNodeInfo eventSource,
      HashMap<String, Paint> currentPaints,
      HashMap<String, Paint> nonCurrentPaints,
      int radius,
      int tabStopCount,
      boolean isCurrent) {
    super(context);
    this.eventSource = eventSource;
    this.yOffset = getYOffset();
    this.tabStopCount = tabStopCount;
    this.currentPaints = currentPaints;
    this.nonCurrentPaints = nonCurrentPaints;
    this.radius = radius;
    this.isCurrent = isCurrent;
    this.isReset = false;
    this.rect = new Rect();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    if (eventSource == null) {
      return;
    }

    eventSource.getBoundsInScreen(rect);
    rect.offset(0, yOffset);

    this.xCoordinate = rect.centerX();
    this.yCoordinate = rect.centerY();

    if (isReset) {
      this.drawInnerCircle(
          xCoordinate, yCoordinate, radius, nonCurrentPaints.get("transparent"), canvas);
      this.drawNumberInCircle(
          xCoordinate,
          yCoordinate,
          tabStopCount,
          nonCurrentPaints.get("transparent"),
          canvas);
      this.drawOuterCircle(
          xCoordinate, yCoordinate, radius, nonCurrentPaints.get("transparent"), canvas);
      return;
    }

    if (!isCurrent) {
      this.drawInnerCircle(
          xCoordinate, yCoordinate, radius, nonCurrentPaints.get("innerCircle"), canvas);
      this.drawNumberInCircle(
          xCoordinate, yCoordinate, tabStopCount, nonCurrentPaints.get("number"), canvas);
      this.drawOuterCircle(
          xCoordinate, yCoordinate, radius, nonCurrentPaints.get("outerCircle"), canvas);
      return;
    }

    this.drawInnerCircle(
        xCoordinate, yCoordinate, radius, currentPaints.get("innerCircle"), canvas);
    this.drawNumberInCircle(
        xCoordinate, yCoordinate, tabStopCount, currentPaints.get("number"), canvas);
    this.drawOuterCircle(
        xCoordinate, yCoordinate, radius, currentPaints.get("outerCircle"), canvas);
  }

  public int getYOffset() {
    int offset = 0;
    int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      offset = getResources().getDimensionPixelSize(resourceId);
    }
    // divide by 2 to center
    offset = offset / 2;
    return offset;
  }

  public void drawInnerCircle(
      int xCoordinate, int yCoordinate, int radius, Paint paint, Canvas canvas) {
    canvas.drawCircle(xCoordinate, yCoordinate, radius, paint);
  }

  public void drawOuterCircle(
      int xCoordinate, int yCoordinate, int radius, Paint paint, Canvas canvas) {
    canvas.drawCircle(xCoordinate, yCoordinate, radius + 3, paint);
  }

  public void drawNumberInCircle(
      int xCoordinate, int yCoordinate, int tabStopCount, Paint paint, Canvas canvas) {
    canvas.drawText(
        Integer.toString(tabStopCount),
        xCoordinate,
        yCoordinate - ((paint.descent() + paint.ascent()) / 2),
        paint);
  }

  public void setNonCurrent() {
    this.isCurrent = false;
    this.invalidate();
  }

  public void reset() {
    this.isReset = true;
    this.invalidate();
  }

  public int getTabStopCount(){
    return this.tabStopCount;
  }

  public AccessibilityNodeInfo getEventSource(){
    return this.eventSource;
  }
}
