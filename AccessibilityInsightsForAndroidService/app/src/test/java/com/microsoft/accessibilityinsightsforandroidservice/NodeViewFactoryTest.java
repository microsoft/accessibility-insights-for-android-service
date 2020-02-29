// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.view.accessibility.AccessibilityNodeInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NodeViewFactoryTest {

  @Mock AccessibilityNodeInfo node;

  NodeViewFactory testSubject;

  @Before
  public void prepare() {
    testSubject = new NodeViewFactory();
  }

  @Test
  public void nodeViewIsNotNull() {
    Assert.assertNotNull(testSubject.buildAxeViewForNode(node, null, null));
  }
}
