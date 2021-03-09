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

public class FocusElementLine extends View {
    private static final String TAG = "FocusElementLine";
    private AccessibilityNodeInfo eventSource;
    private AccessibilityNodeInfo previousEventSource;
    private int yOffset;
    private Paint currentPaint;
    private Paint nonCurrentPaint;
    private HashMap<String, Paint> paints;

    public FocusElementLine(Context context, AccessibilityNodeInfo eventSource, @Nullable AccessibilityNodeInfo previousEventSource, HashMap<String, Paint> paints) {
        super(context);
        this.eventSource = eventSource;
        this.previousEventSource = previousEventSource;
        this.yOffset = getYOffset();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        boolean refreshWorked = eventSource.refresh();
        boolean previousRefreshWorked = previousEventSource.refresh();
        if (!refreshWorked || !previousRefreshWorked) {
            return;
        }

        Rect currentRect = new Rect();
        eventSource.getBoundsInScreen(currentRect);
        currentRect.offset(0, this.yOffset);

        Rect prevRect = new Rect();
        eventSource.getBoundsInScreen(prevRect);
        prevRect.offset(0, this.yOffset);

        int x1Coordinate = currentRect.centerX();
        int y1Coordinate = currentRect.centerY();
        int x2Coordinate = prevRect.centerX();
        int y2Coordinate = prevRect.centerY();

        this.drawConnectingLine(canvas, (Paint) this.paints.get("line"), x1Coordinate, y1Coordinate, x2Coordinate, y2Coordinate);

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

}
