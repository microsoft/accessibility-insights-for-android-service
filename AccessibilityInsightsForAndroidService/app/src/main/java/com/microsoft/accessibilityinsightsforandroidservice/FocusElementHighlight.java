// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.HashMap;

public class FocusElementHighlight {
  private static final String TAG = "FocusElementHighlight";
  private AccessibilityNodeInfo eventSource;
  private int yOffset;
  private int tabStopCount;
  private int radius;
  private int xCoordinate;
  private int yCoordinate;
  private HashMap<String, Paint> paints;
  private Rect rect;
  private View view;

  public FocusElementHighlight(
      AccessibilityNodeInfo eventSource,
      HashMap<String, Paint> currentPaints,
      int radius,
      int tabStopCount,
      View view) {
    this.view = view;
    this.eventSource = eventSource;
    this.yOffset = setYOffset();
    this.tabStopCount = tabStopCount;
    this.radius = radius;
    this.rect = new Rect();
    this.setCoordinates();
    this.paints = currentPaints;
  }

  private void setCoordinates() {
    if (this.eventSource == null) {
      return;
    }
    this.eventSource.getBoundsInScreen(this.rect);
    this.rect.offset(0, this.yOffset);
    this.xCoordinate = rect.centerX();
    this.yCoordinate = rect.centerY();
  }

  private int setYOffset() {
    int offset = 0;
    int resourceId =
        this.view.getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      offset = this.view.getResources().getDimensionPixelSize(resourceId);
    }
    // divide by 2 to center
    offset = offset / 2;
    return offset;
  }

  public void drawElementHighlight(Canvas canvas) {
    this.drawInnerCircle(
        this.xCoordinate, this.yCoordinate, this.radius, this.paints.get("innerCircle"), canvas);
    this.drawNumberInCircle(
        this.xCoordinate, this.yCoordinate, this.tabStopCount, this.paints.get("number"), canvas);
    this.drawOuterCircle(
        this.xCoordinate, this.yCoordinate, this.radius, this.paints.get("outerCircle"), canvas);
  }

  private void drawInnerCircle(
      int xCoordinate, int yCoordinate, int radius, Paint paint, Canvas canvas) {
    canvas.drawCircle(xCoordinate, yCoordinate, radius, paint);
  }

  private void drawOuterCircle(
      int xCoordinate, int yCoordinate, int radius, Paint paint, Canvas canvas) {
    canvas.drawCircle(xCoordinate, yCoordinate, radius + 3, paint);
  }

  private void drawNumberInCircle(
      int xCoordinate, int yCoordinate, int tabStopCount, Paint paint, Canvas canvas) {
    canvas.drawText(
        Integer.toString(tabStopCount),
        xCoordinate,
        yCoordinate - ((paint.descent() + paint.ascent()) / 2),
        paint);
  }

  public void setPaints(HashMap<String, Paint> paints) {
    this.paints = paints;
  }

  public AccessibilityNodeInfo getEventSource() {
    return this.eventSource;
  }

  public void updateWithNewCoordinates() {
    this.setYOffset();
    this.setCoordinates();
  }
}
