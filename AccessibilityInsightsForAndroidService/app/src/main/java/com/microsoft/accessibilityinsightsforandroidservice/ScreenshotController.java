// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.os.Handler;
import android.util.DisplayMetrics;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ScreenshotController {
  private final OnScreenshotAvailableProvider onScreenshotAvailableProvider;
  private DisplayMetrics metrics;
  private Handler screenshotHandler;
  private ImageReader imageReader;
  private Supplier<DisplayMetrics> displayMetricsSupplier;
  private VirtualDisplay display;
  private BitmapProvider bitmapProvider;
  private Supplier<MediaProjection> mediaProjectionSupplier;

  public ScreenshotController(
      Supplier<DisplayMetrics> displayMetricsSupplier,
      Handler handler,
      OnScreenshotAvailableProvider onScreenshotAvailableProvider,
      BitmapProvider bitmapProvider,
      Supplier<MediaProjection> mediaProjectionSupplier) {
    this.displayMetricsSupplier = displayMetricsSupplier;
    this.screenshotHandler = handler;
    this.onScreenshotAvailableProvider = onScreenshotAvailableProvider;
    this.bitmapProvider = bitmapProvider;
    this.mediaProjectionSupplier = mediaProjectionSupplier;
  }

  public void getScreenshotWithMediaProjection(Consumer<Bitmap> bitmapConsumer) {
    MediaProjection sharedMediaProjection = mediaProjectionSupplier.get();

    if (sharedMediaProjection == null) {
      bitmapConsumer.accept(null);
      return;
    }

    if (imageReader != null) {
      imageReader.close();
    }

    if (display != null) {
      display.release();
    }

    metrics = displayMetricsSupplier.get();
    imageReader = getImageReader(metrics, bitmapConsumer);
    display =
        sharedMediaProjection.createVirtualDisplay(
            "myDisplay",
            metrics.widthPixels,
            metrics.heightPixels,
            metrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader.getSurface(),
            null,
            null);
  }

  private ImageReader getImageReader(DisplayMetrics metrics, Consumer<Bitmap> bitmapConsumer) {
    ImageReader imageReader =
        ImageReader.newInstance(
            metrics.widthPixels, metrics.heightPixels, PixelFormat.RGBA_8888, 2);

    Consumer<Bitmap> onBitmapAvailable =
        bitmap -> {
          display.release();
          bitmapConsumer.accept(bitmap);
        };

    OnScreenshotAvailable onScreenshotAvailable =
        onScreenshotAvailableProvider.getOnScreenshotAvailable(
            onBitmapAvailable, metrics, bitmapProvider);
    imageReader.setOnImageAvailableListener(onScreenshotAvailable, screenshotHandler);

    return imageReader;
  }
}
