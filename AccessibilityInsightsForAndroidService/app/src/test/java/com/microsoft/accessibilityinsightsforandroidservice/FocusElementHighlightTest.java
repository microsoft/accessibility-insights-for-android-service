// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
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
@PrepareForTest({FocusElementHighlight.class, OffsetHelper.class})
public class FocusElementHighlightTest {
  FocusElementHighlight testSubject;

  @Mock AccessibilityNodeInfo accessibilityNodeInfoMock;
  @Mock View viewMock;
  @Mock Paint paintMock;
  @Mock Resources resourcesMock;
  @Mock Rect rectMock;
  @Mock Canvas canvasMock;

  @Before
  public void prepare() throws Exception {
    HashMap<String, Paint> paintsStub = new HashMap<>();
    paintsStub.put("innerCircle", paintMock);
    paintsStub.put("outerCircle", paintMock);
    paintsStub.put("number", paintMock);

    when(viewMock.getResources()).thenReturn(resourcesMock);
    whenNew(Rect.class).withNoArguments().thenReturn(rectMock);
    doNothing().when(rectMock).offset(isA(Integer.class), isA(Integer.class));

    testSubject =
        new FocusElementHighlight(accessibilityNodeInfoMock, paintsStub, 10, 10, viewMock);
  }

  @Test
  public void returnsNotNull() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void followsCorrectStepsToUpdateCoordinates() throws Exception {
    mockStatic(OffsetHelper.class);

    FocusElementHighlight elementSpy = spy(testSubject);
    elementSpy.updateWithNewCoordinates();

    verifyStatic(OffsetHelper.class, times(1));
    OffsetHelper.getYOffset(any(View.class));

    verifyPrivate(elementSpy, times(1)).invoke("setCoordinates");
  }

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
  public void drawElementHighlightCallsAllRelevantDrawMethods() throws Exception {
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
}