// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.os.CancellationSignal;
import android.view.accessibility.AccessibilityNodeInfo;

public class ConfigRequestFulfiller implements RequestFulfiller {
  private final RootNodeFinder rootNodeFinder;
  private final EventHelper eventHelper;
  private final DeviceConfigFactory deviceConfigFactory;

  public ConfigRequestFulfiller(
      RootNodeFinder rootNodeFinder,
      EventHelper eventHelper,
      DeviceConfigFactory deviceConfigFactory) {
    this.rootNodeFinder = rootNodeFinder;
    this.deviceConfigFactory = deviceConfigFactory;
    this.eventHelper = eventHelper;
  }

  public String fulfillRequest(CancellationSignal cancellationSignal) {
    AccessibilityNodeInfo source = eventHelper.claimLastSource();
    AccessibilityNodeInfo rootNode = rootNodeFinder.getRootNodeFromSource(source);

    try {
      return deviceConfigFactory.getDeviceConfig(rootNode).toJson();
    } finally {
      if (rootNode != null && rootNode != source) {
        rootNode.recycle();
      }
      if (source != null && !eventHelper.restoreLastSource(source)) {
        source.recycle();
      }
    }
  }
}
