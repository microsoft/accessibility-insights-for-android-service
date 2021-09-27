// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

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

  AccessibilityNodeInfoQueueBuilder testSubject;

  @Before
  public void prepare() {
    testSubject = new AccessibilityNodeInfoQueueBuilder();
  }

  @Test
  public void buildEmptyQueue() {
    Queue<OrderedValue<AccessibilityNodeInfo>> queue = testSubject.buildPriorityQueue(null);
    Assert.assertNotNull(queue);
    Assert.assertTrue(queue.isEmpty());
  }

  @Test
  public void buildSingleNodeQueue() {
    Queue<OrderedValue<AccessibilityNodeInfo>> queue = testSubject.buildPriorityQueue(rootNode);
    Assert.assertNotNull(queue);
    Assert.assertEquals(queue.size(), 1);

    assertNextQueueItemEquals(queue, rootNode, Long.MAX_VALUE);
  }

  @Test
  public void buildQueueWithChildren() {
    createChildren();

    Queue<OrderedValue<AccessibilityNodeInfo>> queue = testSubject.buildPriorityQueue(rootNode);

    long rootOrder = Long.MAX_VALUE;
    long childOrder = rootOrder - 1;
    long grandchildOrder = rootOrder - 2;

    Assert.assertEquals(queue.size(), 4);
    assertNextQueueItemEquals(queue, grandchildNode, grandchildOrder);
    // Since the child nodes have the same priority, they can appear here in any order,
    // so check for both ordering possibilities
    if (queue.peek().value == childNode0) {
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

    Queue<OrderedValue<AccessibilityNodeInfo>> queue = testSubject.buildPriorityQueue(rootNode);

    long rootOrder = Long.MAX_VALUE;
    long child0Order = (rootOrder - 1) / 2;
    long child1Order = rootOrder - 1;
    long grandchildOrder = child0Order - 1;

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
      Queue<OrderedValue<AccessibilityNodeInfo>> queue, AccessibilityNodeInfo node, long priority) {
    OrderedValue<AccessibilityNodeInfo> nextItem = queue.poll();
    Assert.assertNotNull(nextItem);
    Assert.assertEquals(nextItem.value, node);
    Assert.assertEquals(nextItem.order, priority);
  }
}
