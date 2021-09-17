// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.util.DisplayMetrics;
import java.util.function.Supplier;

public class AxeScannerFactory {
  public static AxeScanner createAxeScanner(
      DeviceConfigFactory deviceConfigFactory, Supplier<DisplayMetrics> displayMetricsSupplier) {
    final AxeViewsFactory axeViewsFactory =
        new AxeViewsFactory(new NodeViewBuilderFactory(), new AccessibilityNodeInfoQueueBuilder());
    final AxeImageFactory axeImageFactory =
        new AxeImageFactory(new ByteArrayOutputStreamProvider());
    final AxeDeviceFactory axeDeviceFactory =
        new AxeDeviceFactory(deviceConfigFactory, displayMetricsSupplier);
    final AxeContextFactory axeContextFactory =
        new AxeContextFactory(axeImageFactory, axeViewsFactory, axeDeviceFactory);
    final AxeRunnerFactory axeRunnerFactory = new AxeRunnerFactory();

    return new AxeScanner(axeRunnerFactory, axeContextFactory);
  }
}
