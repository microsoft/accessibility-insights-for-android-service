// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;
import com.deque.axe.android.AxeView;
import com.deque.axe.android.wrappers.AxeRect;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NodeViewBuilderTest {

  @Mock AccessibilityNodeInfo node;
  @Mock AxeRectProvider rectProvider;
  List<AxeView> children;

  private final int boundsLeft = 0;
  private final int boundsRight = 1;
  private final int boundsTop = 2;
  private final int boundsBottom = 3;
  private final AxeRect expectedBoundsRect =
      new AxeRect(boundsLeft, boundsRight, boundsTop, boundsBottom);

  NodeViewBuilder testSubject;

  @Before
  public void prepare() {
    children = new ArrayList<>();
    when(rectProvider.createAxeRect(boundsLeft, boundsRight, boundsTop, boundsBottom))
        .thenReturn(expectedBoundsRect);
    when(node.getClassName()).thenReturn("class name");
  }

  @Test
  public void nodeViewIsNotNull() {
    testSubject = new NodeViewBuilder(node, children, null, rectProvider);
    Assert.assertNotNull(testSubject.build());
  }

  @Test
  public void nodeViewHasCorrectBoundingRect() {
    setupBoundingRect();

    testSubject = new NodeViewBuilder(node, children, null, rectProvider);
    AxeView axeView = testSubject.build();

    AxeRect boundingRect = axeView.boundsInScreen;

    Assert.assertNotNull(boundingRect);
    Assert.assertEquals(boundingRect, expectedBoundsRect);
  }

  @Test
  public void nodeViewHasChildren() {
    AxeView child1 = mock(AxeView.class);
    AxeView child2 = mock(AxeView.class);
    children.add(child1);
    children.add(child2);

    testSubject = new NodeViewBuilder(node, children, null, rectProvider);
    AxeView axeView = testSubject.build();

    List<AxeView> viewChildren = axeView.children;
    Assert.assertNotNull(viewChildren);
    Assert.assertFalse(viewChildren.isEmpty());
    Assert.assertEquals(viewChildren, children);
  }

  @Test
  public void nodeViewHasLabeledBy() {
    AxeView labeledBy = mock(AxeView.class);

    testSubject = new NodeViewBuilder(node, children, labeledBy, rectProvider);
    AxeView axeView = testSubject.build();

    AxeView viewLabeledBy = axeView.labeledBy;
    Assert.assertNotNull(viewLabeledBy);
    Assert.assertSame(viewLabeledBy, labeledBy);
  }

  @Test
  public void nodeViewHasNullClassName() {
    AxeView labeledBy = mock(AxeView.class);
    when(node.getClassName()).thenReturn(null);

    testSubject = new NodeViewBuilder(node, children, labeledBy, rectProvider);
    AxeView axeView = testSubject.build();

    Assert.assertNotNull(axeView.className);
  }

  private void setupBoundingRect() {
    doAnswer(
            AdditionalAnswers.answerVoid(
                (emptyRect) -> {
                  Rect rect = (Rect) emptyRect;
                  rect.left = boundsLeft;
                  rect.top = boundsTop;
                  rect.right = boundsRight;
                  rect.bottom = boundsBottom;
                }))
        .when(node)
        .getBoundsInScreen(any());
  }
}
