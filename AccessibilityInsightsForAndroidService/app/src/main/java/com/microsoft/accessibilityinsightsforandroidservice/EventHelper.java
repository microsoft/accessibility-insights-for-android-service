// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.view.accessibility.AccessibilityNodeInfo;

public class EventHelper {
  private final ThreadSafeSwapper<AccessibilityNodeInfo> swapper;

  public EventHelper(ThreadSafeSwapper<AccessibilityNodeInfo> swapper) {
    this.swapper = swapper;
  }

  public void recordEvent(AccessibilityNodeInfo source) {
    if (source != null) {
      AccessibilityNodeInfo lastSource = swapper.swap(source);
      if (lastSource != null) {
        lastSource.recycle();
      }
    }
  }

  public AccessibilityNodeInfo claimLastSource() {
    return swapper.swap(null);
  }

  public boolean restoreLastSource(AccessibilityNodeInfo previousSource) {
    return swapper.setIfCurrentlyNull(previousSource);
  }
}
