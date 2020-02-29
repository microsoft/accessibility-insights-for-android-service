// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.view.accessibility.AccessibilityNodeInfo;
import com.deque.axe.android.AxeView;
import java.util.List;

public class NodeViewFactory {

  public AxeView buildAxeViewForNode(
      AccessibilityNodeInfo node, List<AxeView> children, AxeView labeledBy) {
    NodeViewBuilder builder = new NodeViewBuilder(node, children, labeledBy, new AxeRectProvider());
    return builder.build();
  }
}
