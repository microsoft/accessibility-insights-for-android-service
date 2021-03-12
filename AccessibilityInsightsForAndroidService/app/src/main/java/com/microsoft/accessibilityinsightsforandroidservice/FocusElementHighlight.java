// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.HashMap;

public class FocusElementHighlight extends View {
  private static final String TAG = "FocusElementHighlight";
  private AccessibilityNodeInfo eventSource;
  private int yOffset;
  private int tabStopCount;
  private int radius;
  private int xCoordinate;
  private int yCoordinate;
  private HashMap<String, Paint> paints;
  private Rect rect;


  public FocusElementHighlight(
      Context context,
      AccessibilityNodeInfo eventSource,
      HashMap<String, Paint> currentPaints,
      int radius,
      int tabStopCount) {
    super(context);
    this.eventSource = eventSource;
    this.yOffset = setYOffset();
    this.tabStopCount = tabStopCount;
    this.radius = radius;
    this.rect = new Rect();
    this.setCoordinates();
    this.paints = currentPaints;
  }

  public void setCoordinates(){
    this.eventSource.getBoundsInScreen(this.rect);
    rect.offset(0, this.yOffset);
    this.xCoordinate = rect.centerX();
    this.yCoordinate = rect.centerY();
  }

  private int setYOffset(){
    int offset = 0;
    int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      offset = getResources().getDimensionPixelSize(resourceId);
    }
    // divide by 2 to center
    offset = offset / 2;
    return offset;
  }

  public void drawElementHighlight(Canvas canvas){
    this.drawInnerCircle(
            this.xCoordinate, this.yCoordinate, this.radius, paints.get("innerCircle"), canvas);
    this.drawNumberInCircle(
            xCoordinate, yCoordinate, tabStopCount, paints.get("number"), canvas);
    this.drawOuterCircle(
            xCoordinate, yCoordinate, radius, paints.get("outerCircle"), canvas);
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


  //layout params being called in constructor, try rotating device and see if it still works properly.

  public void setPaints(HashMap<String, Paint> paints) {
    this.paints = paints;
  }

  public AccessibilityNodeInfo getEventSource(){
    return this.eventSource;
  }

}
