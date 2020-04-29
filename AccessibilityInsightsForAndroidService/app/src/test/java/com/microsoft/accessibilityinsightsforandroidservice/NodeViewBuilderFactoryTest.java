// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.view.accessibility.AccessibilityNodeInfo;
import com.deque.axe.android.AxeView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NodeViewBuilderFactoryTest {

  @Mock AccessibilityNodeInfo node;
  @Mock AxeView view;

  NodeViewBuilderFactory testSubject;

  @Before
  public void prepare() {
    testSubject = new NodeViewBuilderFactory();
  }

  @Test
  public void nodeViewIsNotNull() {
    Assert.assertNotNull(testSubject.createNodeViewBuilder(node, null, null));
  }
}
