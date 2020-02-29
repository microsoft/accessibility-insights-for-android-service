// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.util.Log;

public class Logger {

  private static boolean LOG = BuildConfig.DEBUG_MODE;

  public static void logVerbose(String tag, String message) {
    if (LOG) {
      Log.v(tag, message);
    }
  }

  public static void logDebug(String tag, String message) {
    if (LOG) {
      Log.d(tag, message);
    }
  }

  public static void logError(String tag, String message) {
    if (LOG) {
      Log.e(tag, message);
    }
  }

  public static void logInfo(String tag, String message) {
    if (LOG) {
      Log.i(tag, message);
    }
  }

  public static void logWarning(String tag, String message) {
    if (LOG) {
      Log.w(tag, message);
    }
  }
}
