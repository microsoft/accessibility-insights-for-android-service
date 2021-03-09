// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.HashMap;

public class FocusElementLine extends View {
    private static final String TAG = "FocusElementLine";
    private AccessibilityNodeInfo eventSource;
    private AccessibilityNodeInfo previousEventSource;
    private int yOffset;
    private HashMap<String, Paint> currentPaints;
    private HashMap<String, Paint> nonCurrentPaints;
    private boolean isCurrent;
    private boolean isReset;

    public FocusElementLine(Context context, AccessibilityNodeInfo eventSource, AccessibilityNodeInfo previousEventSource, HashMap<String, Paint> currentPaints, HashMap<String, Paint> nonCurrentPaints) {
        super(context);
        this.eventSource = eventSource;
        this.previousEventSource = previousEventSource;
        this.yOffset = getYOffset();
        this.currentPaints = currentPaints;
        this.nonCurrentPaints = nonCurrentPaints;
        this.isCurrent = true;
        this.isReset = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(eventSource == null || previousEventSource == null){
            return;
        }

        Rect currentRect = new Rect();
        this.eventSource.getBoundsInScreen(currentRect);
        currentRect.offset(0, this.yOffset);

        Rect prevRect = new Rect();
        this.previousEventSource.getBoundsInScreen(prevRect);
        prevRect.offset(0, this.yOffset);

        int x1Coordinate = currentRect.centerX();
        int y1Coordinate = currentRect.centerY();
        int x2Coordinate = prevRect.centerX();
        int y2Coordinate = prevRect.centerY();

        if(isReset){
            this.drawConnectingLine(canvas, (Paint) this.nonCurrentPaints.get("transparent"), x1Coordinate, y1Coordinate, x2Coordinate, y2Coordinate);
            return;
        }

        if(!isCurrent){
            this.drawConnectingLine(canvas, (Paint) this.nonCurrentPaints.get("line"), x1Coordinate, y1Coordinate, x2Coordinate, y2Coordinate);
            return;
        }

        this.drawConnectingLine(canvas, (Paint) this.currentPaints.get("line"), x1Coordinate, y1Coordinate, x2Coordinate, y2Coordinate);

    }

    public int getYOffset(){
        int offset = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(resourceId > 0){
            offset = getResources().getDimensionPixelSize(resourceId)/2;
        }
        return offset;
    }


    public void drawConnectingLine(Canvas canvas, Paint paint, int x1Coordinate, int y1Coordinate, int x2Coordinate, int y2Coordinate){
        canvas.drawLine(x1Coordinate, y1Coordinate, x2Coordinate, y2Coordinate, paint);
    }

    public void setNonCurrent(){
        this.isCurrent = false;
        this.invalidate();
    }

    public void reset(){
        this.isReset = true;
        this.invalidate();
    }

}
