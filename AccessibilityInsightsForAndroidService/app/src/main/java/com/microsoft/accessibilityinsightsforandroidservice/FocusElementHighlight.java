// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Hashtable;

public class FocusElementHighlight extends View {
    private static final String TAG = "FocusElementHighlight";
    private AccessibilityNodeInfo eventSource;
    private AccessibilityNodeInfo previousEventSource;
    private int yOffset;
    private int tabStopCount;
    HashMap<String, Paint> paints;

    public FocusElementHighlight(Context context, AccessibilityNodeInfo eventSource, @Nullable AccessibilityNodeInfo previousEventSource, HashMap<String, Paint> paints, int tabStopCount) {
        super(context);
        this.eventSource = eventSource;
        this.previousEventSource = previousEventSource;
        this.yOffset = getYOffset();
        this.tabStopCount = tabStopCount;
        this.paints = paints;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        boolean refreshWorked = eventSource.refresh();
        if (!refreshWorked) {
            return;
        }

        Rect rect = new Rect();
        eventSource.getBoundsInScreen(rect);
        rect.offset(0, yOffset/2);

        int radius = 50;

        int xCoordinate = rect.centerX();
        int yCoordinate = rect.centerY();

        this.drawInnerCircle(xCoordinate, yCoordinate, radius, (Paint) paints.get("innerCircle"), canvas);
        this.drawNumberInCircle(xCoordinate, yCoordinate, tabStopCount, (Paint) paints.get("number"),  canvas);
        this.drawOuterCircleCurrent(xCoordinate, yCoordinate, radius, (Paint) paints.get("outerCircle"), canvas);
    }

    public int getYOffset(){
        int offset = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(resourceId > 0){
            offset = getResources().getDimensionPixelSize(resourceId);
        }
        return offset;
    }

    public void drawInnerCircle(int xCoordinate, int yCoordinate, int radius, Paint paint, Canvas canvas){
        canvas.drawCircle(xCoordinate, yCoordinate, radius, paint);
    }

    public void drawOuterCircleCurrent(int xCoordinate, int yCoordinate, int radius, Paint paint, Canvas canvas){
        canvas.drawCircle(xCoordinate, yCoordinate, radius+3, paint);
    }

    public void drawNumberInCircle(int xCoordinate, int yCoordinate, int tabStopCount, Paint paint, Canvas canvas){
        canvas.drawText(Integer.toString(tabStopCount),xCoordinate - (paint.getTextSize()/3), yCoordinate + (paint.getTextSize()/3), paint);
    }

}
