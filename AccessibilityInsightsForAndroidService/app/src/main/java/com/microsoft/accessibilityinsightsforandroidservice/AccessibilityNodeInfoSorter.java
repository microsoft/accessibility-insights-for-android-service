// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.view.accessibility.AccessibilityNodeInfo;
import androidx.annotation.NonNull;
import java.util.Objects;

public class AccessibilityNodeInfoSorter implements Comparable<AccessibilityNodeInfoSorter> {
  public final Long order;
  public final AccessibilityNodeInfo node;

  public AccessibilityNodeInfoSorter(AccessibilityNodeInfo node, Long order) {
    this.order = order;
    this.node = node;
  }

  @Override
  public int compareTo(@NonNull AccessibilityNodeInfoSorter data) {
    return this.order.compareTo(data.order);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AccessibilityNodeInfoSorter that = (AccessibilityNodeInfoSorter) o;
    return Objects.equals(order, that.order);
  }

  @Override
  public int hashCode() {
    return Objects.hash(order);
  }
}
