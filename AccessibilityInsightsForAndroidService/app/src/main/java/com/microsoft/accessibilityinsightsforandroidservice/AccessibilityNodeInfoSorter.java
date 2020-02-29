// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.NonNull;

public class AccessibilityNodeInfoSorter implements Comparable<AccessibilityNodeInfoSorter> {
  public final Long order;
  public final AccessibilityNodeInfo node;

  public AccessibilityNodeInfoSorter(AccessibilityNodeInfo node, Long order) {
    this.order = order;
    this.node = node;
  }

  @Override
  public int compareTo(@NonNull AccessibilityNodeInfoSorter other) {
    return this.order.compareTo(other.order);
  }
}
