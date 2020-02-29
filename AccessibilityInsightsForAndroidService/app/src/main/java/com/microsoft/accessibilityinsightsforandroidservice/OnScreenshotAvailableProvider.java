// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import java.util.function.Consumer;

public class OnScreenshotAvailableProvider {
  public OnScreenshotAvailable getOnScreenshotAvailable(
      Consumer<Bitmap> bitmapConsumer, DisplayMetrics metrics, BitmapProvider bitmapProvider) {
    return new OnScreenshotAvailable(bitmapConsumer, metrics, bitmapProvider);
  }
}
