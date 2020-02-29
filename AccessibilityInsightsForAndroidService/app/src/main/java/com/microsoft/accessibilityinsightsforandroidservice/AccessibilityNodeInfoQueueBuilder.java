// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.view.accessibility.AccessibilityNodeInfo;
import java.util.PriorityQueue;
import java.util.Queue;

public class AccessibilityNodeInfoQueueBuilder {

  private AccessibilityNodeInfoSorterFactory nodeSorterFactory;

  public AccessibilityNodeInfoQueueBuilder(AccessibilityNodeInfoSorterFactory nodeSorterFactory) {
    this.nodeSorterFactory = nodeSorterFactory;
  }

  public Queue<AccessibilityNodeInfoSorter> buildPriorityQueue(AccessibilityNodeInfo rootNode) {
    PriorityQueue<AccessibilityNodeInfoSorter> queue = new PriorityQueue<>();
    recursivelyEnqueueNodes(queue, rootNode, Long.MAX_VALUE);
    return queue;
  }

  private void recursivelyEnqueueNodes(
      PriorityQueue<AccessibilityNodeInfoSorter> queue, AccessibilityNodeInfo node, Long order) {
    // The AxeView object requires that we create the AxeView
    // objects for both child nodes and for any labeledBy nodes. Child nodes use
    // easily predictable rules, but labeledBy nodes are less structured. We use
    // a PriorityQueue where children are given a slightly higher priority than
    // the parent, but nodes that are used as labels are given a significantly
    // higher priority. This will work for most "typical" cases, but not the case
    // where a node's labeledBy value points to a direct ancestor. That scenario
    // could require changes to the AxeView class, which is immutable after construction.

    if (node == null) {
      return;
    }

    if (node.getLabelFor() != null) {
      order /= 2;
    }

    queue.add(nodeSorterFactory.createNodeSorter(node, order));

    int childCount = node.getChildCount();

    for (int loop = 0; loop < childCount; loop++) {
      recursivelyEnqueueNodes(queue, node.getChild(loop), order - 1);
    }
  }
}
