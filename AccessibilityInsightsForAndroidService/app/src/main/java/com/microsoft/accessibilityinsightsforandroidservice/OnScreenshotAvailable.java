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
  private final Bitmap.Config IMAGE_BITMAP_FORMAT = Bitmap.Config.ARGB_8888;
  private final int IMAGE_PIXEL_STRIDE = 4; // Implied by ARGB_8888 (4 bytes per pixel)

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
    Bitmap screenshotBitmap = null;
    try {
      screenshotBitmap = getBitmapFromImage(image);
    } catch (ImageFormatException e) {
      Logger.logError(TAG, "ImageFormatException: " + e.toString());
    } finally {
      image.close();
    }

    // If we failed to convert the image, we just log an error and don't forward anything on to the
    // consumer that's forming the API response. From the API consumer's perspective, it will
    // propagate as results with no screenshot data available.
    if (screenshotBitmap != null) {
      imageAlreadyProcessed = true;
      bitmapConsumer.accept(screenshotBitmap);
    }
  }

  private Bitmap getBitmapFromImage(Image image) throws ImageFormatException {
    int width = image.getWidth();
    int height = image.getHeight();
    if (width != metrics.widthPixels || height != metrics.heightPixels) {
      Logger.logError(
          TAG,
          "Received image of dimensions "
              + width
              + "x"
              + height
              + ", mismatches device DisplayMetrics "
              + metrics.widthPixels
              + "x"
              + metrics.heightPixels);
    }

    Bitmap bitmap = bitmapProvider.createBitmap(width, height, IMAGE_BITMAP_FORMAT);
    copyPixelsFromImagePlane(bitmap, image.getPlanes()[0], width, height);

    return bitmap;
  }

  private void copyPixelsFromImagePlane(
      Bitmap destination, Image.Plane source, int width, int height) throws ImageFormatException {
    // Note: rowStride is usually pixelStride * width, but can sometimes be larger (with padding)
    int pixelStride = source.getPixelStride(); // bytes per pixel
    int rowStride = source.getRowStride(); // bytes per row

    if (pixelStride != IMAGE_PIXEL_STRIDE) {
      throw new ImageFormatException(
          "Invalid source Image: pixelStride=" + pixelStride + ", expected " + IMAGE_PIXEL_STRIDE);
    }
    if (rowStride < width * pixelStride) {
      throw new ImageFormatException(
          "Invalid source Image: rowStride "
              + rowStride
              + " is too small for width "
              + width
              + " at pixelStride "
              + pixelStride);
    }

    ByteBuffer sourceBuffer = source.getBuffer();

    if (rowStride == width * pixelStride) {
      destination.copyPixelsFromBuffer(sourceBuffer);
      return;
    }

    int unpaddedRowStride = width * pixelStride;
    ByteBuffer pixelDataWithRowPaddingRemoved = ByteBuffer.allocate(unpaddedRowStride * height);
    for (int row = 0; row < height; ++row) {
      sourceBuffer.position(row * rowStride);
      sourceBuffer.get(
          pixelDataWithRowPaddingRemoved.array(), row * unpaddedRowStride, unpaddedRowStride);
    }

    destination.copyPixelsFromBuffer(pixelDataWithRowPaddingRemoved);
  }
}
