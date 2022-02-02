// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.os.Build;
import android.view.accessibility.AccessibilityNodeInfo;

public class DeviceConfigFactory {
  public DeviceConfigFactory(String buildModel) {
    this.buildModel = buildModel;
  }

  public DeviceConfigFactory() {
    this(Build.MODEL);
  }

  private String serviceVersion = "0.1.0";
  private String buildModel;

  public DeviceConfig getDeviceConfig(AccessibilityNodeInfo rootNode) {
    String packageName = getPackageNameFromAccessibilityNode(rootNode);

    return new DeviceConfig(buildModel, packageName, serviceVersion);
  }

  private String getPackageNameFromAccessibilityNode(AccessibilityNodeInfo rootNode) {
    String packageName = "No application detected";
    if (rootNode != null) {
      CharSequence sequence = rootNode.getPackageName();
      if (sequence != null) {
        packageName = sequence.toString();
      }
    }
    return packageName;
  }
}
