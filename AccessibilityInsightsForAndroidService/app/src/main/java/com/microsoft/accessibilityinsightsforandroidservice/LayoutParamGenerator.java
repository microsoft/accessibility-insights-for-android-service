// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import java.util.function.Supplier;

public class LayoutParamGenerator {
  private Supplier<DisplayMetrics> displayMetricsSupplier;

  public LayoutParamGenerator(Supplier<DisplayMetrics> displayMetricsSupplier) {
    this.displayMetricsSupplier = displayMetricsSupplier;
  }

  public WindowManager.LayoutParams get() {
    DisplayMetrics displayMetrics = displayMetricsSupplier.get();
    WindowManager.LayoutParams params =
        new WindowManager.LayoutParams(
            displayMetrics.widthPixels,
            displayMetrics.heightPixels,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT);

    return params;
  }
}
