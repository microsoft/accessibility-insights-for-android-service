// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.graphics.Bitmap;
import android.util.Base64;
import com.deque.axe.android.colorcontrast.AxeColor;
import com.deque.axe.android.colorcontrast.AxeImage;
import com.deque.axe.android.wrappers.AxeRect;
import java.io.ByteArrayOutputStream;

public class ScreenshotAxeImage extends AxeImage {
  private final AxeRect frameRect;
  private Bitmap screenshot;
  private ByteArrayOutputStreamProvider byteArrayOutputStreamProvider;

  public ScreenshotAxeImage(
      Bitmap screenshot, ByteArrayOutputStreamProvider byteArrayOutputStreamProvider) {
    this.screenshot = screenshot;
    this.byteArrayOutputStreamProvider = byteArrayOutputStreamProvider;
    frameRect = new AxeRect(0, screenshot.getWidth() - 1, 0, screenshot.getHeight() - 1);
  }

  @Override
  public AxeRect frame() {
    return frameRect;
  }

  @Override
  public AxeColor pixel(int x, int y) {
    AxeColor color = new AxeColor(this.screenshot.getPixel(x, y));
    return color;
  }

  @Override
  public String toBase64Png() {
    ByteArrayOutputStream byteArrayOutputStream = byteArrayOutputStreamProvider.get();
    screenshot.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
    byte[] byteArray = byteArrayOutputStream.toByteArray();
    return Base64.encodeToString(byteArray, Base64.NO_WRAP);
  }
}
