// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.view.accessibility.AccessibilityNodeInfo;

public class AccessibilityNodeInfoSorterFactory {

  public AccessibilityNodeInfoSorter createNodeSorter(AccessibilityNodeInfo node, Long order) {
    return new AccessibilityNodeInfoSorter(node, order);
  }
}
