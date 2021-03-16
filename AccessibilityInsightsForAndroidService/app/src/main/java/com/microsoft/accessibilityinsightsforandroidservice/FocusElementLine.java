// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.HashMap;

public class FocusElementLine {
  private static final String TAG = "FocusElementLine";
  private AccessibilityNodeInfo eventSource;
  private AccessibilityNodeInfo previousEventSource;
  private int yOffset;
  private int x1Coordinate;
  private int y1Coordinate;
  private int x2Coordinate;
  private int y2Coordinate;
  private HashMap<String, Paint> paints;
  private Rect currentRect;
  private Rect prevRect;
  private View view;

  public FocusElementLine(
      AccessibilityNodeInfo eventSource,
      AccessibilityNodeInfo previousEventSource,
      HashMap<String, Paint> Paints,
      View view) {
    this.view = view;
    this.eventSource = eventSource;
    this.previousEventSource = previousEventSource;
    this.yOffset = setYOffset();
    this.paints = Paints;
    this.currentRect = new Rect();
    this.prevRect = new Rect();
    this.setCoordinates();
  }

  public void drawLine(Canvas canvas) {
    this.drawConnectingLine(
        this.x1Coordinate,
        this.y1Coordinate,
        this.x2Coordinate,
        this.y2Coordinate,
        this.paints.get("line"),
        canvas);
  }

  private void setCoordinates() {
    if (this.eventSource == null || this.previousEventSource == null) {
      return;
    }

    this.eventSource.getBoundsInScreen(this.currentRect);
    this.currentRect.offset(0, this.yOffset);

    this.previousEventSource.getBoundsInScreen(this.prevRect);
    this.prevRect.offset(0, this.yOffset);

    this.x1Coordinate = currentRect.centerX();
    this.y1Coordinate = currentRect.centerY();
    this.x2Coordinate = prevRect.centerX();
    this.y2Coordinate = prevRect.centerY();
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

  private void drawConnectingLine(
      int x1Coordinate,
      int y1Coordinate,
      int x2Coordinate,
      int y2Coordinate,
      Paint paint,
      Canvas canvas) {
    canvas.drawLine(x1Coordinate, y1Coordinate, x2Coordinate, y2Coordinate, paint);
  }

  public void setPaint(HashMap<String, Paint> paints) {
    this.paints = paints;
  }

  public void updateWithNewCoordinates() {
    this.setYOffset();
    this.setCoordinates();
  }
}
