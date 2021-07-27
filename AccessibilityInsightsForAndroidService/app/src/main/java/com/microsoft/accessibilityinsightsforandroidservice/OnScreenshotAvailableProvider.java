// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import java.util.function.Consumer;

public class OnScreenshotAvailableProvider {
  public OnScreenshotAvailable getOnScreenshotAvailable(
      DisplayMetrics metrics, BitmapProvider bitmapProvider, Consumer<Bitmap> bitmapConsumer) {
    return new OnScreenshotAvailable(metrics, bitmapProvider, bitmapConsumer);
  }
}
