// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.graphics.Bitmap;
import android.media.Image;
import android.media.ImageReader;
import android.util.DisplayMetrics;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class OnScreenshotAvailable implements ImageReader.OnImageAvailableListener {
  private static final String TAG = "OnScreenshotAvailable";
  private boolean imageAlreadyProcessed = false;
  private Consumer<Bitmap> bitmapConsumer;
  private DisplayMetrics metrics;
  private BitmapProvider bitmapProvider;

  public OnScreenshotAvailable(
      Consumer<Bitmap> bitmapConsumer, DisplayMetrics metrics, BitmapProvider bitmapProvider) {
    this.bitmapConsumer = bitmapConsumer;
    this.metrics = metrics;
    this.bitmapProvider = bitmapProvider;
  }

  public synchronized void onImageAvailable(ImageReader imageReader) {
    // onImageAvailable can be called more than once; we only want one screenshot to be processed.
    if (imageAlreadyProcessed) {
      return;
    }

    Image image = imageReader.acquireLatestImage();
    Bitmap screenshotBitmap = getBitmapFromImage(image);
    image.close();
    imageAlreadyProcessed = true;
    bitmapConsumer.accept(screenshotBitmap);
  }

  private Bitmap getBitmapFromImage(Image image) {
    Image.Plane[] imagePlanes = image.getPlanes();
    int bitmapWidth = getBitmapWidth(image, imagePlanes);
    Bitmap screenshotBitmap =
        bitmapProvider.createBitmap(bitmapWidth, metrics.heightPixels, Bitmap.Config.ARGB_8888);
    ByteBuffer buffer = imagePlanes[0].getBuffer();
    screenshotBitmap.copyPixelsFromBuffer(buffer);
    return screenshotBitmap;
  }

  private int getBitmapWidth(Image image, Image.Plane[] imagePlanes) {
    int pixelStride = imagePlanes[0].getPixelStride();
    int rowStride = imagePlanes[0].getRowStride();
    int rowPadding = rowStride - pixelStride * metrics.widthPixels;
    return image.getWidth() + rowPadding / pixelStride;
  }
}
