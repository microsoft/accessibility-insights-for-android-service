// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verifyNoInteractions;
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
  @Mock Paint paintMock;
  @Mock Resources resourcesMock;
  @Mock Canvas canvasMock;
  MockedConstruction<Rect> rectConstructionMock;

  HashMap<String, Paint> paintsStub;

  @Before
  public void prepare() throws Exception {
    paintsStub = new HashMap<>();
    paintsStub.put("innerCircle", paintMock);
    paintsStub.put("outerCircle", paintMock);
    paintsStub.put("number", paintMock);
    paintsStub.put("transparentInnerCircle", paintMock);

    when(viewMock.getResources()).thenReturn(resourcesMock);
    rectConstructionMock = Mockito.mockConstruction(Rect.class);

    testSubject =
        new FocusElementHighlight(accessibilityNodeInfoMock, paintsStub, 10, 10, viewMock);
  }

  @After
  public void cleanUp() {
    rectConstructionMock.close();
  }

  @Test
  public void returnsNotNull() {
    Assert.assertNotNull(testSubject);
  }

  /* TODO: fix Whitebox cases

  @Test
  public void setPaintsWorksProperly() {
    HashMap<String, Paint> testPaintsStub = new HashMap<>();
    testPaintsStub.put("test", paintMock);
    testSubject.setPaints(testPaintsStub);

    HashMap<String, Paint> resultingPaintsHashMap =
        Whitebox.getInternalState(testSubject, "paints");
    Assert.assertEquals(resultingPaintsHashMap.get("test"), paintMock);
    Assert.assertNull(resultingPaintsHashMap.get("innerCircle"));
  }

  @Test
  public void drawElementHighlightDoesNothingWhenEventSourceIsNull() {
    testSubject = new FocusElementHighlight(null, paintsStub, 10, 10, viewMock);
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
  public void drawElementHighlightCallsAllRelevantDrawMethodsForCurrentElement() throws Exception {
    when(accessibilityNodeInfoMock.refresh()).thenReturn(true);
    FocusElementHighlight elementSpy = spy(testSubject);
    elementSpy.drawElementHighlight(canvasMock);
    verifyPrivate(elementSpy, times(1))
        .invoke(
            "drawInnerCircle", anyInt(), anyInt(), anyInt(), any(Paint.class), any(Canvas.class));
    verifyPrivate(elementSpy, times(1))
        .invoke(
            "drawOuterCircle", anyInt(), anyInt(), anyInt(), any(Paint.class), any(Canvas.class));
  }

  @Test
  public void drawElementHighlightCallsAllRelevantDrawMethodsForNonCurrentElement()
      throws Exception {
    testSubject.setAsNonCurrentElement();
    when(accessibilityNodeInfoMock.refresh()).thenReturn(true);
    FocusElementHighlight elementSpy = spy(testSubject);
    elementSpy.drawElementHighlight(canvasMock);
    verifyPrivate(elementSpy, times(1))
        .invoke(
            "drawInnerCircle", anyInt(), anyInt(), anyInt(), any(Paint.class), any(Canvas.class));
    verifyPrivate(elementSpy, times(1))
        .invoke(
            "drawOuterCircle", anyInt(), anyInt(), anyInt(), any(Paint.class), any(Canvas.class));
    verifyPrivate(elementSpy, times(1))
        .invoke(
            "drawNumberInCircle",
            anyInt(),
            anyInt(),
            anyInt(),
            any(Paint.class),
            any(Canvas.class));
  }

  @Test
  public void getEventSourceReturnsAccessibilityNodeInfo() {
    Assert.assertEquals(testSubject.getEventSource(), accessibilityNodeInfoMock);
  }

  @Test
  public void setAsNonCurrentElementFunctionsAsExpected() {
    testSubject.setAsNonCurrentElement();
    Assert.assertEquals(Whitebox.getInternalState(testSubject, "isCurrentElement"), false);
  }

   */
}
