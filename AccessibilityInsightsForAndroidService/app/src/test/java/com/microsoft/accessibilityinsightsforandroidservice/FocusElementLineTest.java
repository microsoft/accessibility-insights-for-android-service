// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FocusElementLine.class, OffsetHelper.class})
public class FocusElementLineTest {

  FocusElementLine testSubject;

  @Mock AccessibilityNodeInfo eventSourceMock;
  @Mock AccessibilityNodeInfo previousEventSourceMock;
  @Mock Paint paintMock;
  @Mock View viewMock;
  @Mock Resources resourcesMock;
  @Mock Rect rectMock;
  @Mock Canvas canvasMock;
  HashMap<String, Paint> paintsStub;

  @Before
  public void prepare() throws Exception {
    paintsStub = new HashMap<>();
    paintsStub.put("foregroundLine", paintMock);
    paintsStub.put("backgroundLine", paintMock);

    when(viewMock.getResources()).thenReturn(resourcesMock);
    whenNew(Rect.class).withNoArguments().thenReturn(rectMock);
    doNothing().when(rectMock).offset(isA(Integer.class), isA(Integer.class));

    testSubject =
        new FocusElementLine(eventSourceMock, previousEventSourceMock, paintsStub, viewMock);
  }

  @Test
  public void returnsNotNull() {
    Assert.assertNotNull(testSubject);
  }

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
}
