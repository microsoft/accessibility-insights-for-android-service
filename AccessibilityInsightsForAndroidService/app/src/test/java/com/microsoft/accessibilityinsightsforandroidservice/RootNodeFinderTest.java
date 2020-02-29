// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.accessibility.AccessibilityNodeInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RootNodeFinderTest {

  @Mock AccessibilityNodeInfo sourceMock;
  @Mock AccessibilityNodeInfo parentMock;
  @Mock AccessibilityNodeInfo grandparentMock;

  RootNodeFinder testSubject;

  @Before
  public void prepare() {
    testSubject = new RootNodeFinder();
  }

  @Test
  public void returnNullIfSourceIsNull() {
    Assert.assertNull(testSubject.getRootNodeFromSource(null));
  }

  @Test
  public void rootNodeExistsIfSourceExists() {
    Assert.assertNotNull(testSubject.getRootNodeFromSource(sourceMock));
  }

  @Test
  public void rootNodeIsSource() {
    AccessibilityNodeInfo rootNode = testSubject.getRootNodeFromSource(sourceMock);
    Assert.assertEquals(rootNode, sourceMock);
  }

  @Test
  public void rootNodeIsSourceParent() {
    when(sourceMock.getParent()).thenReturn(parentMock);

    AccessibilityNodeInfo rootNode = testSubject.getRootNodeFromSource(sourceMock);
    Assert.assertEquals(rootNode, parentMock);
  }

  @Test
  public void rootNodeIsSourceAncestor() {
    when(sourceMock.getParent()).thenReturn(parentMock);
    when(parentMock.getParent()).thenReturn(grandparentMock);

    AccessibilityNodeInfo rootNode = testSubject.getRootNodeFromSource(sourceMock);
    Assert.assertEquals(rootNode, grandparentMock);
  }

  @Test
  public void uneededNodesGetRecycled() {
    when(sourceMock.getParent()).thenReturn(parentMock);
    when(parentMock.getParent()).thenReturn(grandparentMock);

    AccessibilityNodeInfo rootNode = testSubject.getRootNodeFromSource(sourceMock);
    verify(sourceMock, never()).recycle();
    verify(rootNode, never()).recycle();
    verify(parentMock, times(1)).recycle();
  }
}
