// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.os.Handler;
import android.os.Looper;

public class UIThreadRunner {
  public void run(Runnable runnable) {
    new Handler(Looper.getMainLooper()).post(runnable);
  }
}
