// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
  @Mock Paint foregroundLinePaintMock;
  @Mock Paint backgroundLinePaintMock;
  @Mock Paint differentBackgroundLinePaintMock;
  @Mock View viewMock;
  @Mock Resources resourcesMock;
  @Mock Canvas canvasMock;
  MockedConstruction<Rect> rectConstructionMock;

  HashMap<String, Paint> initialPaints;

  @Before
  public void prepare() throws Exception {
    initialPaints = new HashMap<>();
    initialPaints.put("foregroundLine", foregroundLinePaintMock);
    initialPaints.put("backgroundLine", backgroundLinePaintMock);

    when(viewMock.getResources()).thenReturn(resourcesMock);
    rectConstructionMock = Mockito.mockConstruction(Rect.class);

    testSubject =
        new FocusElementLine(eventSourceMock, previousEventSourceMock, initialPaints, viewMock);
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
  public void drawLineDrawsOneForegroundAndOneBackgroundLine() throws Exception {
    when(eventSourceMock.refresh()).thenReturn(true);
    when(previousEventSourceMock.refresh()).thenReturn(true);

    testSubject.drawLine(canvasMock);
    verify(canvasMock, times(1)).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(), same(foregroundLinePaintMock));
    verify(canvasMock, times(1)).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(), same(backgroundLinePaintMock));
    verifyNoMoreInteractions(canvasMock);
  }

  @Test
  public void setPaintUpdatesPaintsUsedToDrawLines() throws Exception {
    when(eventSourceMock.refresh()).thenReturn(true);
    when(previousEventSourceMock.refresh()).thenReturn(true);

    HashMap<String, Paint> updatedPaints = new HashMap<>(initialPaints);
    updatedPaints.put("backgroundLine", differentBackgroundLinePaintMock);

    testSubject.setPaint(updatedPaints);
    testSubject.drawLine(canvasMock);

    verify(canvasMock, times(0)).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(), same(/* original */ backgroundLinePaintMock));
    verify(canvasMock, times(1)).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(), same(differentBackgroundLinePaintMock));
  }


  @Test
  public void drawLineDoesNothingWhenEventSourceIsNull() {
    testSubject = new FocusElementLine(null, previousEventSourceMock, initialPaints, viewMock);
    testSubject.drawLine(canvasMock);
    verifyNoInteractions(canvasMock);
  }

  @Test
  public void drawLineDoesNothingWhenPreviousEventSourceIsNull() {
    testSubject = new FocusElementLine(eventSourceMock, null, initialPaints, viewMock);
    testSubject.drawLine(canvasMock);
    verifyNoInteractions(canvasMock);
  }

  @Test
  public void drawLineDoesNothingWhenPreviousEventSourceDoesNotRefresh() {
    when(eventSourceMock.refresh()).thenReturn(true);
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
}
