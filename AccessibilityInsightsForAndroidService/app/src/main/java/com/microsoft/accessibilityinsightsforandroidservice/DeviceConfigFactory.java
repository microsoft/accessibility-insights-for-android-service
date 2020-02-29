// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.os.Build;
import android.view.accessibility.AccessibilityNodeInfo;

public class DeviceConfigFactory {
  private String serviceVersion = "0.1.0";

  public DeviceConfig getDeviceConfig(AccessibilityNodeInfo rootNode) {
    String packageName = getPackageNameFromAccessibilityNode(rootNode);

    return new DeviceConfig(Build.MODEL, packageName, serviceVersion);
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
