// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import com.deque.axe.android.utils.JsonSerializable;

public class DeviceConfig implements JsonSerializable {
  public final String deviceName;
  public final String packageName;
  public final String serviceVersion;

  public DeviceConfig(String deviceName, String packageName, String serviceVersion) {
    this.deviceName = deviceName;
    this.packageName = packageName;
    this.serviceVersion = serviceVersion;
  }
}
