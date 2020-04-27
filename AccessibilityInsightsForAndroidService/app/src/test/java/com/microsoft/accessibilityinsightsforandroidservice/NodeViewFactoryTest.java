// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import android.view.accessibility.AccessibilityNodeInfo;
import com.deque.axe.android.AxeView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NodeViewFactoryTest {

  @Mock AccessibilityNodeInfo node;
  @Mock NodeViewBuilderFactory nodeViewBuilderFactory;
  @Mock NodeViewBuilder nodeViewBuilder;
  @Mock AxeView view;

  NodeViewFactory testSubject;

  @Before
  public void prepare() {
    when(nodeViewBuilderFactory.createNodeViewBuilder(eq(node), eq(null), eq(null), any()))
        .thenReturn(nodeViewBuilder);
    when(nodeViewBuilder.build()).thenReturn(view);

    testSubject = new NodeViewFactory();
  }

  @Test
  public void nodeViewIsNotNull() {
    Assert.assertNotNull(testSubject.buildAxeViewForNode(node, null, null, nodeViewBuilderFactory));
  }
}
