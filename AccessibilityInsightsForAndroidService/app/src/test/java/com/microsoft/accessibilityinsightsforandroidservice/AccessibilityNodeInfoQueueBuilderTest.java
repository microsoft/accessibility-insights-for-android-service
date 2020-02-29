// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.accessibility.AccessibilityNodeInfo;
import java.util.Queue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccessibilityNodeInfoQueueBuilderTest {

  @Mock AccessibilityNodeInfo rootNode;
  @Mock AccessibilityNodeInfo childNode0;
  @Mock AccessibilityNodeInfo childNode1;
  @Mock AccessibilityNodeInfo grandchildNode;
  @Mock AccessibilityNodeInfoSorterFactory nodeSorterFactory;

  AccessibilityNodeInfoQueueBuilder testSubject;

  @Before
  public void prepare() {
    when(nodeSorterFactory.createNodeSorter(any(), any())).thenCallRealMethod();
    testSubject = new AccessibilityNodeInfoQueueBuilder(nodeSorterFactory);
  }

  @Test
  public void buildEmptyQueue() {
    Queue<AccessibilityNodeInfoSorter> queue = testSubject.buildPriorityQueue(null);
    Assert.assertNotNull(queue);
    Assert.assertTrue(queue.isEmpty());
  }

  @Test
  public void buildSingleNodeQueue() {
    Queue<AccessibilityNodeInfoSorter> queue = testSubject.buildPriorityQueue(rootNode);
    Assert.assertNotNull(queue);
    Assert.assertEquals(queue.size(), 1);

    verify(nodeSorterFactory).createNodeSorter(rootNode, Long.MAX_VALUE);
    assertNextQueueItemEquals(queue, rootNode, Long.MAX_VALUE);
  }

  @Test
  public void buildQueueWithChildren() {
    createChildren();

    Queue<AccessibilityNodeInfoSorter> queue = testSubject.buildPriorityQueue(rootNode);

    Long rootOrder = Long.MAX_VALUE;
    Long childOrder = rootOrder - 1;
    Long grandchildOrder = rootOrder - 2;

    verifyAllNodesCreated(rootOrder, childOrder, childOrder, grandchildOrder);

    Assert.assertEquals(queue.size(), 4);
    assertNextQueueItemEquals(queue, grandchildNode, grandchildOrder);
    // Since the child nodes have the same priority, they can appear here in any order,
    // so check for both ordering possibilities
    if (queue.peek().node == childNode0) {
      assertNextQueueItemEquals(queue, childNode0, childOrder);
      assertNextQueueItemEquals(queue, childNode1, childOrder);
    } else {
      assertNextQueueItemEquals(queue, childNode1, childOrder);
      assertNextQueueItemEquals(queue, childNode0, childOrder);
    }
    assertNextQueueItemEquals(queue, rootNode, rootOrder);
  }

  @Test
  public void buildQueueWithLabelNodes() {
    createChildren();

    when(childNode0.getLabelFor()).thenReturn(childNode1);

    Queue<AccessibilityNodeInfoSorter> queue = testSubject.buildPriorityQueue(rootNode);

    Long rootOrder = Long.MAX_VALUE;
    Long child0Order = (rootOrder - 1) / 2;
    Long child1Order = rootOrder - 1;
    Long grandchildOrder = child0Order - 1;

    verifyAllNodesCreated(rootOrder, child0Order, child1Order, grandchildOrder);

    Assert.assertEquals(queue.size(), 4);
    assertNextQueueItemEquals(queue, grandchildNode, grandchildOrder);
    assertNextQueueItemEquals(queue, childNode0, child0Order);
    assertNextQueueItemEquals(queue, childNode1, child1Order);
    assertNextQueueItemEquals(queue, rootNode, rootOrder);
  }

  public void createChildren() {
    when(rootNode.getChildCount()).thenReturn(2);
    when(rootNode.getChild(0)).thenReturn(childNode0);
    when(rootNode.getChild(1)).thenReturn(childNode1);

    when(childNode0.getChildCount()).thenReturn(1);
    when(childNode0.getChild(0)).thenReturn(grandchildNode);
  }

  public void assertNextQueueItemEquals(
      Queue<AccessibilityNodeInfoSorter> queue, AccessibilityNodeInfo node, Long priority) {
    AccessibilityNodeInfoSorter nextItem = queue.poll();
    Assert.assertNotNull(nextItem);
    Assert.assertEquals(nextItem.node, node);
    Assert.assertEquals(nextItem.order, priority);
  }

  public void verifyAllNodesCreated(
      Long rootOrder, Long child0Order, Long child1Order, Long grandchildOrder) {
    verify(nodeSorterFactory).createNodeSorter(rootNode, rootOrder);
    verify(nodeSorterFactory).createNodeSorter(childNode0, child0Order);
    verify(nodeSorterFactory).createNodeSorter(childNode1, child1Order);
    verify(nodeSorterFactory).createNodeSorter(grandchildNode, grandchildOrder);
  }
}
