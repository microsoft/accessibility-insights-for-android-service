// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.view.accessibility.AccessibilityNodeInfo;
import com.deque.axe.android.AxeView;
import java.util.List;

public class NodeViewBuilderFactory {

  public NodeViewBuilder createNodeViewBuilder(
      AccessibilityNodeInfo node,
      List<AxeView> children,
      AxeView labeledBy,
      AxeRectProvider boundsRectProvider) {
    return new NodeViewBuilder(node, children, labeledBy, boundsRectProvider);
  }
}
