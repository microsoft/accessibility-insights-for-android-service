// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
public class FocusElementLineTest {

  FocusElementLine testSubject;

  @Mock AccessibilityNodeInfo eventSourceMock;
  @Mock AccessibilityNodeInfo previousEventSourceMock;
  @Mock Paint paintMock;
  @Mock View viewMock;
  @Mock Resources resourcesMock;
  @Mock Canvas canvasMock;
  MockedConstruction<Rect> rectConstructionMock;

  HashMap<String, Paint> paintsStub;

  @Before
  public void prepare() throws Exception {
    paintsStub = new HashMap<>();
    paintsStub.put("foregroundLine", paintMock);
    paintsStub.put("backgroundLine", paintMock);

    when(viewMock.getResources()).thenReturn(resourcesMock);
    rectConstructionMock = Mockito.mockConstruction(Rect.class);

    testSubject =
        new FocusElementLine(eventSourceMock, previousEventSourceMock, paintsStub, viewMock);
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
  public void drawLineCallsCorrectPrivateMethod() throws Exception {
    when(eventSourceMock.refresh()).thenReturn(true);
    when(previousEventSourceMock.refresh()).thenReturn(true);

    FocusElementLine lineSpy = spy(testSubject);
    lineSpy.drawLine(canvasMock);
    verifyPrivate(lineSpy, times(2))
        .invoke(
            "drawConnectingLine",
            anyInt(),
            anyInt(),
            anyInt(),
            anyInt(),
            any(Paint.class),
            any(Canvas.class));
  }

  @Test
  public void drawLineDoesNothingWhenEventSourceIsNull() {
    testSubject = new FocusElementLine(null, previousEventSourceMock, paintsStub, viewMock);
    testSubject.drawLine(canvasMock);
    verifyNoInteractions(canvasMock);
  }

  @Test
  public void drawLineDoesNothingWhenPreviousEventSourceIsNull() {
    testSubject = new FocusElementLine(eventSourceMock, null, paintsStub, viewMock);
    testSubject.drawLine(canvasMock);
    verifyNoInteractions(canvasMock);
  }

  @Test
  public void drawLineDoesNothingWhenPreviousEventSourceDoesNotRefresh() {
    when(previousEventSourceMock.refresh()).thenReturn(false);
    testSubject.drawLine(canvasMock);
    verifyNoInteractions(canvasMock);
  }

  @Test
  public void drawLineDoesNothingWhenEventSourceDoesNotRefresh() {
    when(eventSourceMock.refresh()).thenReturn(false);
    testSubject.drawLine(canvasMock);
    verifyNoInteractions(canvasMock);
  }

  @Test
  public void setPaintWorksProperly() {
    HashMap<String, Paint> paintsStub2 = new HashMap<>();
    paintsStub2.put("test", paintMock);
    testSubject.setPaint(paintsStub2);

    HashMap<String, Paint> resultingPaintHashMap = Whitebox.getInternalState(testSubject, "paints");
    Assert.assertEquals(resultingPaintHashMap.get("test"), paintMock);
    Assert.assertNull(resultingPaintHashMap.get("foregroundLine"));
    Assert.assertNull(resultingPaintHashMap.get("backgroundLine"));
  }

     */
}
