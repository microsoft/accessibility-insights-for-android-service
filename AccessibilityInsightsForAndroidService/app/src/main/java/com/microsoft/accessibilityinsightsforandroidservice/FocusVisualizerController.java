package com.microsoft.accessibilityinsightsforandroidservice;

import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import java.util.function.Supplier;

public class FocusVisualizerController {
    private FocusVisualizer focusVisualizer;
    private FocusVisualizationStateManager focusVisualizationStateManager;
    private AccessibilityEvent lastEvent;
    private WindowManager windowManager;
    private Supplier<WindowManager.LayoutParams>  layoutParamGenerator;
    private FocusVisualizationCanvas focusVisualizationCanvas;

    public FocusVisualizerController(FocusVisualizer focusVisualizer, FocusVisualizationStateManager focusVisualizationStateManager, WindowManager windowManager, Supplier<WindowManager.LayoutParams> layoutParamGenerator, FocusVisualizationCanvas focusVisualizationCanvas) {
        this.focusVisualizer = focusVisualizer;
        this.focusVisualizationStateManager = focusVisualizationStateManager;
        this.windowManager = windowManager;
        this.layoutParamGenerator = layoutParamGenerator;
        this.focusVisualizationCanvas = focusVisualizationCanvas;
        this.focusVisualizationStateManager.subscribe(this::onFocusVisualizationStateChange);
    }

    public void onFocusEvent(AccessibilityEvent event) {
        lastEvent = event;

        if (focusVisualizationStateManager.getState() == false) {
            return;
        }

        focusVisualizer.HandleAccessibilityFocusEvent(event);
    }

    public void onRedrawEvent(AccessibilityEvent event) {
        lastEvent = event;

        if (focusVisualizationStateManager.getState() == false) {
            return;
        }

        focusVisualizer.HandleAccessibilityRedrawEvent(event);
    }

    private void onFocusVisualizationStateChange(boolean newState) {
        if (newState) {
//            focusVisualizer.HandleAccessibilityFocusEvent(lastEvent);
        }
        else {
            focusVisualizer.resetVisualizations();
        }
    }
}
