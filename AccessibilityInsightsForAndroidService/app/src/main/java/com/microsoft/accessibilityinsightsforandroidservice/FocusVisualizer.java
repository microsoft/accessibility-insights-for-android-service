// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.content.Context;
import android.graphics.Paint;
import android.provider.Settings;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.WINDOW_SERVICE;

public class FocusVisualizer {
    private static final String TAG = "FocusVisualizer";
    private ArrayList<FocusElementItem> focusElementItems;
    private int tabStopCount;
    private Context context;
    private WindowManager windowManager;
    private FocusVisualizerStyles styles;
    private HashMap<String, Paint> currentElementPaints;
    private HashMap<String, Paint> nonCurrentElementPaints;
    private HashMap<String, Paint> currentLinePaints;
    private HashMap<String, Paint> nonCurrentLinePaints;
    private WindowManager.LayoutParams layoutParams;

    public FocusVisualizer(Context context) {
        //TODO: create FocusVisualizer to keep list of AccessibilityNodeInfo objects, handle redraw/reset
        // subscribe FocusVisualizer to AccessibilityEventDispatcher
        this.context = context;
        focusElementItems = new ArrayList<>();
        tabStopCount = 0;
        windowManager = getWindowManager();
        styles = new FocusVisualizerStyles();
        currentElementPaints = styles.getCurrentElementPaints();
        nonCurrentElementPaints = styles.getNonCurrentElementPaints();
        currentLinePaints = styles.getCurrentLinePaints();
        nonCurrentLinePaints = styles.getNonCurrentLinePaints();
//        layoutParams = new WindowManager.LayoutParams(context.getRealDisplayMetrics)
        //TODO: set Layout Params, add view to window manager.
    }

    public void HandleAccessibilityFocusEvent(Context context, AccessibilityEvent event){
        tabStopCount++;
        AccessibilityNodeInfo eventSource = event.getSource();
        AccessibilityNodeInfo previousEventSource = getPreviousEventSource();

        FocusElementHighlight focusElementHighlight = new FocusElementHighlight(context, eventSource, previousEventSource, currentElementPaints, tabStopCount);
        if(focusElementItems.size() > 0){
            new FocusElementLine(context, eventSource, previousEventSource, currentLinePaints);
            //TODO: redraw previous Element to use nonCurrentPaints
        }
        if(focusElementItems.size() > 1){
            //TODO: redraw previous line to use nonCurrentPaints
        }
        FocusElementItem focusElementItem = new FocusElementItem(tabStopCount, eventSource, focusElementHighlight);
        focusElementItems.add(focusElementItem);
    }

    public void HandleAccessibilityRedrawEvent(Context context, AccessibilityEvent event){
        //RedrawHighlights();
    }

    public WindowManager getWindowManager(){
        if(windowManager != null){
            return windowManager;
        }

        if(Settings.canDrawOverlays(context)){
            return (WindowManager) context.getSystemService(WINDOW_SERVICE);
        }

        return null;
    }

    private AccessibilityNodeInfo getPreviousEventSource(){
        if(focusElementItems.size() == 0){
            return null;
        }
        return focusElementItems.get(focusElementItems.size() - 1).eventSource;
    }

    private void resetVisualizations(){
        tabStopCount = 0;

        //TODO: logic to remove visualizations when done with them.

        focusElementItems = new ArrayList<FocusElementItem>();
    }

}
