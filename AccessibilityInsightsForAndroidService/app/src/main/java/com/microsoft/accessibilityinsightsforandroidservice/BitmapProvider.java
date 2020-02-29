// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.graphics.Bitmap;

public class BitmapProvider {

  public Bitmap createBitmap(int width, int height, Bitmap.Config config) {
    return Bitmap.createBitmap(width, height, config);
  }
}
