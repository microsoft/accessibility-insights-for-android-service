// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.accessibility.AccessibilityNodeInfo;
import com.deque.axe.android.AxeView;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AxeViewsFactoryTest {

  @Mock NodeViewFactory nodeViewFactoryMock;
  @Mock AccessibilityNodeInfoQueueBuilder queueBuilderMock;
  @Mock AccessibilityNodeInfo rootNodeMock;
  @Mock AccessibilityNodeInfo childNodeMock;
  @Mock AccessibilityNodeInfo labelNodeMock;
  @Mock AxeView rootViewMock;
  @Mock AxeView childViewMock;
  @Mock AxeView labelViewMock;

  Queue<AccessibilityNodeInfoSorter> queue;

  AxeViewsFactory testSubject;

  final String nodeClassName = "node class name";

  @Before
  public void prepare() {
    when(nodeViewFactoryMock.buildAxeViewForNode(eq(rootNodeMock), any(), any()))
        .thenReturn(rootViewMock);
    when(nodeViewFactoryMock.buildAxeViewForNode(eq(childNodeMock), any(), any()))
        .thenReturn(childViewMock);
    when(nodeViewFactoryMock.buildAxeViewForNode(eq(labelNodeMock), any(), any()))
        .thenReturn(labelViewMock);

    queue = new LinkedList<>();
    when(queueBuilderMock.buildPriorityQueue(rootNodeMock)).thenReturn(queue);

    when(childNodeMock.getClassName()).thenReturn(nodeClassName);
    when(labelNodeMock.getClassName()).thenReturn(nodeClassName);

    testSubject = new AxeViewsFactory(nodeViewFactoryMock, queueBuilderMock);
  }

  @Test
  public void axeViewIsNotNull() throws ViewChangedException {
    enqueueNode(rootNodeMock);

    Assert.assertNotNull(testSubject.createAxeViews(rootNodeMock));
  }

  @Test
  public void createsAxeViewWithoutChildren() throws ViewChangedException {
    enqueueNode(rootNodeMock);

    AxeView axeView = testSubject.createAxeViews(rootNodeMock);
    Assert.assertNotNull(axeView);
    Assert.assertEquals(axeView, rootViewMock);

    verify(nodeViewFactoryMock, times(1))
        .buildAxeViewForNode(rootNodeMock, new ArrayList<>(), null);
  }

  @Test
  public void createsAxeViewWithChildNode() throws ViewChangedException {
    when(rootNodeMock.getChildCount()).thenReturn(1);
    when(rootNodeMock.getChild(0)).thenReturn(childNodeMock);

    enqueueNode(childNodeMock);
    enqueueNode(rootNodeMock);

    AxeView axeView = testSubject.createAxeViews(rootNodeMock);
    Assert.assertNotNull(axeView);
    Assert.assertEquals(axeView, rootViewMock);

    ArrayList<AxeView> children = new ArrayList<>();
    children.add(childViewMock);

    verify(nodeViewFactoryMock, times(1)).buildAxeViewForNode(rootNodeMock, children, null);
    verify(nodeViewFactoryMock, times(1))
        .buildAxeViewForNode(childNodeMock, new ArrayList<>(), null);
  }

  @Test
  public void createsAxeViewWithLabeledByNode() throws ViewChangedException {
    when(rootNodeMock.getLabeledBy()).thenReturn(labelNodeMock);

    enqueueNode(labelNodeMock);
    enqueueNode(rootNodeMock);

    AxeView axeView = testSubject.createAxeViews(rootNodeMock);
    Assert.assertNotNull(axeView);
    Assert.assertEquals(axeView, rootViewMock);

    verify(nodeViewFactoryMock, times(1))
        .buildAxeViewForNode(rootNodeMock, new ArrayList<>(), labelViewMock);
    verify(nodeViewFactoryMock, times(1))
        .buildAxeViewForNode(labelNodeMock, new ArrayList<>(), null);
  }

  @Test
  public void refreshAndRetryIfViewChanged() throws ViewChangedException {
    setupViewChangedScenario(true, false);

    AxeView axeView = testSubject.createAxeViews(rootNodeMock);
    Assert.assertNotNull(axeView);
    Assert.assertEquals(axeView, rootViewMock);

    verify(rootNodeMock, times(1)).refresh();
    verify(queueBuilderMock, times(2)).buildPriorityQueue(rootNodeMock);
    verify(rootNodeMock, times(2)).getChildCount();
    verify(rootNodeMock, times(2)).getChild(0);
  }

  @Test
  public void retriesFiveTimes() {
    setupViewChangedScenario(false, false);

    int numRetries = 5;

    try {
      testSubject.createAxeViews(rootNodeMock);
      Assert.fail("Expected createAxeViews to throw exception");
    } catch (ViewChangedException e) {
      verify(rootNodeMock, times(numRetries)).refresh();
      verify(queueBuilderMock, times(numRetries + 1)).buildPriorityQueue(rootNodeMock);
      verify(rootNodeMock, times(numRetries + 1)).getChildCount();
      verify(rootNodeMock, times(numRetries + 1)).getChild(0);
    }
  }

  @Test
  public void recyclesNodesOnSuccess() throws ViewChangedException {
    when(rootNodeMock.getChildCount()).thenReturn(1);
    when(rootNodeMock.getChild(0)).thenReturn(childNodeMock);
    when(rootNodeMock.getLabeledBy()).thenReturn(labelNodeMock);

    enqueueNode(labelNodeMock);
    enqueueNode(childNodeMock);
    enqueueNode(rootNodeMock);

    AxeView axeView = testSubject.createAxeViews(rootNodeMock);
    Assert.assertNotNull(axeView);
    Assert.assertEquals(axeView, rootViewMock);

    verify(childNodeMock, times(1)).recycle();
    verify(labelNodeMock, times(1)).recycle();
    verify(rootNodeMock, never()).recycle();
  }

  @Test
  public void recyclesNodesOnRetry() throws ViewChangedException {
    setupViewChangedScenario(true, true);

    AxeView axeView = testSubject.createAxeViews(rootNodeMock);
    Assert.assertNotNull(axeView);
    Assert.assertEquals(axeView, rootViewMock);

    verify(childNodeMock, times(2)).recycle();
    verify(labelNodeMock, times(2)).recycle();
    verify(rootNodeMock, never()).recycle();
  }

  @Test
  public void recyclesNodesOnRetryFailure() {
    setupViewChangedScenario(false, true);

    int numRetries = 5;

    try {
      testSubject.createAxeViews(rootNodeMock);
      Assert.fail("Expected createAxeViews to throw exception");
    } catch (ViewChangedException e) {
      verify(childNodeMock, times(numRetries + 1)).recycle();
      verify(labelNodeMock, times(numRetries + 1)).recycle();
      verify(rootNodeMock, never()).recycle();
    }
  }

  private void setupViewChangedScenario(boolean retryShouldSucceed, boolean withLabelNode) {
    when(rootNodeMock.getChildCount()).thenReturn(1);
    if (retryShouldSucceed) {
      when(rootNodeMock.getChild(0)).thenReturn(null).thenReturn(childNodeMock);
    } else {
      when(rootNodeMock.getChild(0)).thenReturn(null);
    }

    if (withLabelNode) {
      when(rootNodeMock.getLabeledBy()).thenReturn(labelNodeMock);
      enqueueNode(labelNodeMock);
    }

    enqueueNode(childNodeMock);
    enqueueNode(rootNodeMock);

    reset(queueBuilderMock);
    when(queueBuilderMock.buildPriorityQueue(rootNodeMock))
        .thenAnswer((rootNodeMock) -> new LinkedList<>(queue));
  }

  private void enqueueNode(AccessibilityNodeInfo node) {
    AccessibilityNodeInfoSorter sorter = new AccessibilityNodeInfoSorter(node, 0L);
    queue.add(sorter);
  }
}
