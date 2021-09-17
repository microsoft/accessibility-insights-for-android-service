// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.view.accessibility.AccessibilityNodeInfo;
import com.deque.axe.android.AxeView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class AxeViewsFactory {

  private static final int maxRetries = 5;

  NodeViewBuilderFactory nodeViewBuilderFactory;
  AccessibilityNodeInfoQueueBuilder queueBuilder;
  Map<AccessibilityNodeInfo, AxeView> axeMap;

  public AxeViewsFactory(
      NodeViewBuilderFactory nodeViewBuilderFactory,
      AccessibilityNodeInfoQueueBuilder queueBuilder) {
    this.nodeViewBuilderFactory = nodeViewBuilderFactory;
    this.queueBuilder = queueBuilder;
  }

  public AxeView createAxeViews(AccessibilityNodeInfo rootNode) throws ViewChangedException {
    return buildAxeViewsWithRetries(rootNode, maxRetries);
  }

  private AxeView buildAxeViewsWithRetries(AccessibilityNodeInfo rootNode, int retries)
      throws ViewChangedException {
    Queue<OrderedValue<AccessibilityNodeInfo>> queue = queueBuilder.buildPriorityQueue(rootNode);
    axeMap = new Hashtable<>();

    try {
      return buildAxeViews(queue, rootNode);
    } catch (ViewChangedException e) {
      if (retries > 0) {
        rootNode.refresh();
        return buildAxeViewsWithRetries(rootNode, retries - 1);
      } else {
        throw new ViewChangedException("Failed after " + maxRetries + " attempts.");
      }
    } finally {
      recycleAllNodes(rootNode, queue);
    }
  }

  private AxeView buildAxeViews(
      Queue<OrderedValue<AccessibilityNodeInfo>> queue, AccessibilityNodeInfo rootNode)
      throws ViewChangedException {
    OrderedValue<AccessibilityNodeInfo> nextOrderedNode;

    while ((nextOrderedNode = queue.poll()) != null) {
      AccessibilityNodeInfo node = nextOrderedNode.value;
      List<AxeView> children = getChildViews(node);
      AxeView labeledByView = getLabeledByView(node);
      AxeView nodeView =
          this.nodeViewBuilderFactory.createNodeViewBuilder(node, children, labeledByView).build();
      axeMap.put(node, nodeView);
    }

    return axeMap.get(rootNode);
  }

  private AxeView getLabeledByView(AccessibilityNodeInfo node) {
    AxeView labeledByView = null;
    AccessibilityNodeInfo labeledByNode = node.getLabeledBy();
    if (labeledByNode != null) {
      labeledByView = axeMap.get(labeledByNode);
    }

    return labeledByView;
  }

  private List<AxeView> getChildViews(AccessibilityNodeInfo node) throws ViewChangedException {
    int childCount = node.getChildCount();
    List<AxeView> children = new ArrayList<>(childCount);

    for (int loop = 0; loop < childCount; loop++) {
      AccessibilityNodeInfo child = node.getChild(loop);
      if (child == null) {
        throw new ViewChangedException();
      }
      children.add(axeMap.get(child));
    }

    return children;
  }

  private void recycleAllNodes(
      AccessibilityNodeInfo rootNode, Queue<OrderedValue<AccessibilityNodeInfo>> queue) {
    Set<AccessibilityNodeInfo> allNodes = new HashSet<>(axeMap.keySet());
    for (OrderedValue<AccessibilityNodeInfo> orderedNode : queue) {
      allNodes.add(orderedNode.value);
    }

    for (AccessibilityNodeInfo node : allNodes) {
      if (node != rootNode && node.getClassName() != null) {
        node.recycle();
      }
    }
  }
}
