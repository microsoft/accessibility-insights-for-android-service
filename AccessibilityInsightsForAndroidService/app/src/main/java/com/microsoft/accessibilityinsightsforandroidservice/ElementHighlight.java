// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class ElementHighlight extends View {
    private static final String TAG = "ElementHighlight";
    private AccessibilityNodeInfo eventSource;
    private AccessibilityNodeInfo previousEventSource;
    private int yOffset;
    private int tabStopCount;

    public ElementHighlight(Context context, AccessibilityEvent event, AccessibilityEvent previousEvent, int yOffset, int tabStopCount) {
        super(context);
        //TODO: create FocusVisualizer to keep list of AccessibilityNodeInfo objects, handle redraw/reset
        // move paint and path effect creation to focusVisualizer and pass them in.
        // store tabStopCount in Focus Visualizer
        // move these event.getSource calls to FocusVisualizer
        // subscribe FocusVisualizer to AccessibilityEventDispatcher
        this.eventSource = event.getSource();
        this.previousEventSource = previousEvent.getSource();
        this.yOffset = yOffset;
        this.tabStopCount = tabStopCount;
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

        this.drawInnerCircle(xCoordinate, yCoordinate, radius, canvas);
        this.drawNumberInCircle(xCoordinate, yCoordinate, tabStopCount, canvas);
        this.drawOuterCircleMagenta(xCoordinate, yCoordinate, radius, canvas);
        // this.drawConnectingLineDotted(canvas, xCoordinate, yCoordinate, radius);

        tabStopCount++;
    }

    public AccessibilityNodeInfo getEventSource() {
        return eventSource;
    }

    public void drawInnerCircle(int xCoordinate, int yCoordinate, int radius, Canvas canvas){
        Paint innerCirclePaint = new Paint();
        innerCirclePaint.setStyle(Paint.Style.FILL);
        innerCirclePaint.setColor(Color.WHITE);
        
        canvas.drawCircle(xCoordinate, yCoordinate, radius, innerCirclePaint);
    }

    public void drawOuterCircleMagenta(int xCoordinate, int yCoordinate, int radius, Canvas canvas){
        Paint outerCircleMagentaPaint = new Paint();
        outerCircleMagentaPaint.setStyle(Paint.Style.STROKE);
        outerCircleMagentaPaint.setColor(Color.parseColor("#B4009E"));
        outerCircleMagentaPaint.setStrokeWidth(3);

        canvas.drawCircle(xCoordinate, yCoordinate, radius+3, outerCircleMagentaPaint);
    }

    public void drawNumberInCircle(int xCoordinate, int yCoordinate, int tabStopCount, Canvas canvas){
        Paint numberPaint = new Paint();
        numberPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        numberPaint.setColor(Color.BLACK);
        numberPaint.setStrokeWidth(3);
        numberPaint.setTextSize(45);

        canvas.drawText(Integer.toString(tabStopCount),xCoordinate - (numberPaint.getTextSize()/3), yCoordinate + (numberPaint.getTextSize()/3), numberPaint);
    }

    public void drawConnectingLineDotted(int x1Coordinate, int y1Coordinate, int x2Coordinate, int y2Coordinate, int radius, Canvas canvas){
        Path path = new Path();
        path.moveTo(xCoordinate, yCoordinate+radius);
        path.quadTo(xCoordinate, yCoordinate+radius, xCoordinate, yCoordinate+radius+225);

        Paint pathPaint = new Paint();
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setColor(Color.parseColor("#B4009E"));
        pathPaint.setStrokeWidth(10);
        pathPaint.setPathEffect(new DashPathEffect(new float[]{25,15},0));
        canvas.drawPath(path, pathPaint);
    }

    public void drawConnectingLineSolid(int x1Coordinate, int y1Coordinate, int x2Coordinate, int y2Coordinate, Canvas canvas)(
        Paint solidLinePaint = new Paint();
        solidLinePaint.setStyle(Paint.Style.STROKE);
        solidLinePaint.setColor(Color.BLACK);
        solidLinePaint.setStrokeWidth(3);

        canvas.drawLine(x1Coordinate, y1Coordinate, x2Coordinate, y2Coordinate, solidLinePaint);
    )
}
