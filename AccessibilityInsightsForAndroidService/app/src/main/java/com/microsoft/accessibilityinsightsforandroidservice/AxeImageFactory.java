// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.graphics.Bitmap;
import com.deque.axe.android.colorcontrast.AxeImage;

public class AxeImageFactory {
  private ByteArrayOutputStreamProvider byteArrayOutputStreamProvider;

  public AxeImageFactory(ByteArrayOutputStreamProvider byteArrayOutputStreamProvider) {
    this.byteArrayOutputStreamProvider = byteArrayOutputStreamProvider;
  }

  public AxeImage createAxeImage(Bitmap screenshot) {
    if (screenshot == null) {
      return null;
    }

    return new ScreenshotAxeImage(screenshot, byteArrayOutputStreamProvider);
  }
}
