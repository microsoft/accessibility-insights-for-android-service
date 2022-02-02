// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.util.Log;
import androidx.annotation.VisibleForTesting;

public class Logger {
  @VisibleForTesting public static boolean ENABLE_LOGGING = BuildConfig.DEBUG_MODE;

  public static void logVerbose(String tag, String message) {
    if (ENABLE_LOGGING) {
      Log.v(tag, message);
    }
  }

  public static void logDebug(String tag, String message) {
    if (ENABLE_LOGGING) {
      Log.d(tag, message);
    }
  }

  public static void logError(String tag, String message) {
    if (ENABLE_LOGGING) {
      Log.e(tag, message);
    }
  }

  public static void logInfo(String tag, String message) {
    if (ENABLE_LOGGING) {
      Log.i(tag, message);
    }
  }

  public static void logWarning(String tag, String message) {
    if (ENABLE_LOGGING) {
      Log.w(tag, message);
    }
  }
}
