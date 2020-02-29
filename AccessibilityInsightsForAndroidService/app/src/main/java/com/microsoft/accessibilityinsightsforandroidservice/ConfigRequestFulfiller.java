// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.view.accessibility.AccessibilityNodeInfo;

public class ConfigRequestFulfiller implements RequestFulfiller {
  private final RootNodeFinder rootNodeFinder;
  private final EventHelper eventHelper;
  private final DeviceConfigFactory deviceConfigFactory;
  private final ResponseWriter responseWriter;

  public ConfigRequestFulfiller(
      ResponseWriter responseWriter,
      RootNodeFinder rootNodeFinder,
      EventHelper eventHelper,
      DeviceConfigFactory deviceConfigFactory) {
    this.responseWriter = responseWriter;
    this.rootNodeFinder = rootNodeFinder;
    this.deviceConfigFactory = deviceConfigFactory;
    this.eventHelper = eventHelper;
  }

  public void fulfillRequest(RunnableFunction onRequestFulfilled) {
    writeConfigResponse();
    onRequestFulfilled.run();
  }

  private void writeConfigResponse() {
    AccessibilityNodeInfo source = eventHelper.claimLastSource();
    AccessibilityNodeInfo rootNode = rootNodeFinder.getRootNodeFromSource(source);

    String content = deviceConfigFactory.getDeviceConfig(rootNode).toJson();
    responseWriter.writeSuccessfulResponse(content);

    if (rootNode != null && rootNode != source) {
      rootNode.recycle();
    }
    if (source != null && !eventHelper.restoreLastSource(source)) {
      source.recycle();
    }
  }
}
