// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.os.Build;
import android.util.DisplayMetrics;
import android.view.accessibility.AccessibilityNodeInfo;
import com.deque.axe.android.AxeDevice;
import java.util.function.Supplier;

public class AxeDeviceFactory {
  private final DeviceConfigFactory deviceConfigFactory;
  private final Supplier<DisplayMetrics> displayMetricsSupplier;

  public AxeDeviceFactory(
      DeviceConfigFactory deviceConfigFactory, Supplier<DisplayMetrics> displayMetricsSupplier) {
    this.deviceConfigFactory = deviceConfigFactory;
    this.displayMetricsSupplier = displayMetricsSupplier;
  }

  public AxeDevice createAxeDevice(AccessibilityNodeInfo rootNode) {
    DisplayMetrics displayMetrics = displayMetricsSupplier.get();
    String compoundVersion = Build.VERSION.RELEASE + " API Level " + Build.VERSION.SDK_INT;
    DeviceConfig deviceConfig = deviceConfigFactory.getDeviceConfig(rootNode);
    return new AxeDevice(
        displayMetrics.density,
        deviceConfig.deviceName,
        compoundVersion,
        displayMetrics.heightPixels,
        displayMetrics.widthPixels);
  }
}
