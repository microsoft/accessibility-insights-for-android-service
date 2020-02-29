// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.media.projection.MediaProjection;

public class MediaProjectionHolder {
  private static MediaProjection sharedMediaProjection = null;

  public static void cleanUp() {
    if (sharedMediaProjection != null) {
      sharedMediaProjection.stop();
      sharedMediaProjection = null;
    }
  }

  public static MediaProjection get() {
    return sharedMediaProjection;
  }

  public static void set(MediaProjection projection) {
    sharedMediaProjection = projection;
  }
}
