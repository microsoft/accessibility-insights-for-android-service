// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FocusElementHighlightTest {
  FocusElementHighlight testSubject;

  @Mock AccessibilityNodeInfo accessibilityNodeInfoMock;
  @Mock View viewMock;
  @Mock Paint innerCirclePaintMock;
  @Mock Paint outerCirclePaintMock;
  @Mock Paint differentOuterCirclePaintMock;
  @Mock Paint numberPaintMock;
  @Mock Paint transparentInnerCirclePaintMock;
  @Mock Resources resourcesMock;
  @Mock Canvas canvasMock;
  MockedConstruction<Rect> rectConstructionMock;

  HashMap<String, Paint> initialPaints;

  int tabStopCount = 10;

  @Before
  public void prepare() throws Exception {
    initialPaints = new HashMap<>();
    initialPaints.put("innerCircle", innerCirclePaintMock);
    initialPaints.put("outerCircle", outerCirclePaintMock);
    initialPaints.put("number", numberPaintMock);
    initialPaints.put("transparentInnerCircle", transparentInnerCirclePaintMock);

    when(viewMock.getResources()).thenReturn(resourcesMock);
    rectConstructionMock = Mockito.mockConstruction(Rect.class);

    testSubject =
        new FocusElementHighlight(accessibilityNodeInfoMock, initialPaints, 10, tabStopCount, viewMock);
  }

  @After
  public void cleanUp() {
    rectConstructionMock.close();
  }

  @Test
  public void returnsNotNull() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void drawElementHighlightDoesNothingWhenEventSourceIsNull() {
    testSubject = new FocusElementHighlight(null, initialPaints, 10, 10, viewMock);
    testSubject.drawElementHighlight(canvasMock);
    verifyNoInteractions(canvasMock);
  }

  @Test
  public void drawElementHighlightDoesNothingWhenEventSourceRefreshDoesNotWork() {
    when(accessibilityNodeInfoMock.refresh()).thenReturn(false);
    testSubject.drawElementHighlight(canvasMock);
    verifyNoInteractions(canvasMock);
  }

  @Test
  public void drawElementHighlightDrawsTwoCirclesForCurrentElement() throws Exception {
    when(accessibilityNodeInfoMock.refresh()).thenReturn(true);

    testSubject.drawElementHighlight(canvasMock);

    verify(canvasMock, times(1)).drawCircle(anyFloat(), anyFloat(), anyFloat(), same(transparentInnerCirclePaintMock));
    verify(canvasMock, times(1)).drawCircle(anyFloat(), anyFloat(), anyFloat(), same(outerCirclePaintMock));
    verifyNoMoreInteractions(canvasMock);
  }

  @Test
  public void drawElementHighlightDrawsTwoCirclesAndANumberForNonCurrentElement()
      throws Exception {
    testSubject.setAsNonCurrentElement();
    when(accessibilityNodeInfoMock.refresh()).thenReturn(true);
    testSubject.drawElementHighlight(canvasMock);

    verify(canvasMock, times(1)).drawCircle(anyFloat(), anyFloat(), anyFloat(), same(innerCirclePaintMock));
    verify(canvasMock, times(1)).drawCircle(anyFloat(), anyFloat(), anyFloat(), same(outerCirclePaintMock));
    String expectedText = tabStopCount + "";
    verify(canvasMock, times(1)).drawText(eq(expectedText), anyFloat(), anyFloat(), same(numberPaintMock));
    verifyNoMoreInteractions(canvasMock);
  }

  @Test
  public void setPaintsModifiesPaintsUsedToDrawElementHighlights() throws Exception {
    when(accessibilityNodeInfoMock.refresh()).thenReturn(true);

    HashMap<String, Paint> updatedPaints = new HashMap<>(initialPaints);
    updatedPaints.put("outerCircle", differentOuterCirclePaintMock);

    testSubject.setPaints(updatedPaints);
    testSubject.drawElementHighlight(canvasMock);

    verify(canvasMock, times(0)).drawCircle(anyFloat(), anyFloat(), anyFloat(), /* original */ same(outerCirclePaintMock));
    verify(canvasMock, times(1)).drawCircle(anyFloat(), anyFloat(), anyFloat(), same(differentOuterCirclePaintMock));
  }

  @Test
  public void getEventSourceReturnsAccessibilityNodeInfo() {
    Assert.assertEquals(testSubject.getEventSource(), accessibilityNodeInfoMock);
  }
}
