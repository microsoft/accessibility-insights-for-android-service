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
  private AccessibilityNodeInfo eventSource;
  private AccessibilityNodeInfo previousEventSource;
  private int yOffset;
  private int xStart;
  private int yStart;
  private int xEnd;
  private int yEnd;
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
    this.paints = Paints;
    this.currentRect = new Rect();
    this.prevRect = new Rect();
    this.updateWithNewCoordinates();
  }

  public void drawLine(Canvas canvas) {
    this.drawConnectingLine(
        this.xStart, this.yStart, this.xEnd, this.yEnd, this.paints.get("line"), canvas);
  }

  private void setCoordinates() {
    if (this.eventSource == null || this.previousEventSource == null) {
      return;
    }

    this.eventSource.getBoundsInScreen(this.currentRect);
    this.currentRect.offset(0, this.yOffset);

    this.previousEventSource.getBoundsInScreen(this.prevRect);
    this.prevRect.offset(0, this.yOffset);

    this.xStart = currentRect.centerX();
    this.yStart = currentRect.centerY();
    this.xEnd = prevRect.centerX();
    this.yEnd = prevRect.centerY();
  }

  private void drawConnectingLine(
      int xStart, int yStart, int xEnd, int yEnd, Paint paint, Canvas canvas) {
    canvas.drawLine(xStart, yStart, xEnd, yEnd, paint);
  }

  public void setPaint(HashMap<String, Paint> paints) {
    this.paints = paints;
  }

  public void updateWithNewCoordinates() {
    this.yOffset = OffsetHelper.getYOffset(this.view);
    this.setCoordinates();
  }
}
