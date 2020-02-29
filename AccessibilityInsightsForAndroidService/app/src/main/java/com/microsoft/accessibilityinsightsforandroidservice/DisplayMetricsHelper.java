// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class DisplayMetricsHelper {

  public static DisplayMetrics getRealDisplayMetrics(Context context) {

    DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics(); // Default values

    Display display = getDefaultDisplay(context);

    display.getRealMetrics(displayMetrics);
    return displayMetrics;
  }

  private static Display getDefaultDisplay(Context context) {
    return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
  }
}
