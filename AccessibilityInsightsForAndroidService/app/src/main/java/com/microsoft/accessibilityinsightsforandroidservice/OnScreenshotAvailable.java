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

  // The source Image.Plane and the destination Bitmap use the same byte encoding for image data,
  // 4 bytes per pixel in normal reading order, *except* that the Image.Plane can optionally contain
  // padding bytes at the end of each row's worth of pixel data, which the Bitmap doesn't support.
  //
  // The "row stride" refers to the number of bytes per row, *including* any optional padding.
  //
  // If the source doesn't use any padding, we copy its backing ByteBuffer directly into the
  // destination. If it *does* use padding, we create an intermediate ByteBuffer of our own and
  // selectively copy just the real/unpadded pixel data into it first.
  private void copyPixelsFromImagePlane(
      Bitmap destination, Image.Plane source, int width, int height) throws ImageFormatException {
    // Note: rowStride is usually pixelStride * width, but can sometimes be larger (with padding)
    int sourcePixelStride = source.getPixelStride(); // bytes per pixel
    int sourceRowStride = source.getRowStride(); // bytes per row, including any source row-padding
    int unpaddedRowStride = width * sourcePixelStride; // bytes per row in destination

    if (sourcePixelStride != IMAGE_PIXEL_STRIDE) {
      throw new ImageFormatException(
          "Invalid source Image: sourcePixelStride="
              + sourcePixelStride
              + ", expected "
              + IMAGE_PIXEL_STRIDE);
    }
    if (sourceRowStride < unpaddedRowStride) {
      throw new ImageFormatException(
          "Invalid source Image: sourceRowStride "
              + sourceRowStride
              + " is too small for width "
              + width
              + " at sourcePixelStride "
              + sourcePixelStride);
    }

    ByteBuffer sourceBuffer = source.getBuffer();
    ByteBuffer bitmapPixelDataWithoutRowPadding;

    if (sourceRowStride == unpaddedRowStride) {
      bitmapPixelDataWithoutRowPadding = sourceBuffer;
    } else {
      bitmapPixelDataWithoutRowPadding = ByteBuffer.allocate(unpaddedRowStride * height);
      for (int row = 0; row < height; ++row) {
        int sourceOffset = row * sourceRowStride;
        int destOffset = row * unpaddedRowStride;
        sourceBuffer.position(sourceOffset);
        sourceBuffer.get(bitmapPixelDataWithoutRowPadding.array(), destOffset, unpaddedRowStride);
      }
    }

    destination.copyPixelsFromBuffer(bitmapPixelDataWithoutRowPadding);
  }
}
