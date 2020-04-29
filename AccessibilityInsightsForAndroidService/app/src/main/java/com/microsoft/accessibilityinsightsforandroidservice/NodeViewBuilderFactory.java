// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.view.accessibility.AccessibilityNodeInfo;
import com.deque.axe.android.AxeView;
import java.util.List;

public class NodeViewBuilderFactory {
  // The reason we need something called BuilderFactory is because axe-android requires us to
  // implement the AxeView.builder interface, which we do in NodeViewBuilder. This is a factory
  // for that class.

  public NodeViewBuilder createNodeViewBuilder(
      AccessibilityNodeInfo node, List<AxeView> children, AxeView labeledBy) {
    return new NodeViewBuilder(node, children, labeledBy, new AxeRectProvider());
  }
}
