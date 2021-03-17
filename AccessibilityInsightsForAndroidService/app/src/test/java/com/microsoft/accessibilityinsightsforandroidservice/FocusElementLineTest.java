// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
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
import org.mockito.internal.verification.VerificationModeFactory;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FocusElementLine.class, OffsetHelper.class})
public class FocusElementLineTest {

  FocusElementLine testSubject;

  @Mock AccessibilityNodeInfo accessibilityNodeInfoMock;
  @Mock Paint paintMock;
  @Mock View viewMock;
  @Mock Resources resourcesMock;
  @Mock Rect rectMock;
  @Mock Canvas canvasMock;

  @Before
  public void prepare() throws Exception {
    HashMap<String, Paint> paintsStub = new HashMap<>();
    paintsStub.put("line", paintMock);

    when(viewMock.getResources()).thenReturn(resourcesMock);
    whenNew(Rect.class).withNoArguments().thenReturn(rectMock);
    doNothing().when(rectMock).offset(isA(Integer.class), isA(Integer.class));

    testSubject =
        new FocusElementLine(
            accessibilityNodeInfoMock, accessibilityNodeInfoMock, paintsStub, viewMock);
  }

  @Test
  public void returnsNotNull() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void followsCorrectStepsToUpdateCoordinates() throws Exception {
    mockStatic(OffsetHelper.class);
    FocusElementLine lineSpy = spy(testSubject);
    lineSpy.updateWithNewCoordinates();

    verifyStatic(OffsetHelper.class, VerificationModeFactory.times(1));
    OffsetHelper.getYOffset(any(View.class));

    verifyPrivate(lineSpy, times(1)).invoke("setCoordinates");
  }

  @Test
  public void drawLineCallsCorrectPrivateMethod() throws Exception {
    FocusElementLine lineSpy = spy(testSubject);
    lineSpy.drawLine(canvasMock);
    verifyPrivate(lineSpy, times(1))
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
  public void setPaintWorksProperly() {
    HashMap<String, Paint> paintsStub2 = new HashMap<>();
    paintsStub2.put("test", paintMock);
    testSubject.setPaint(paintsStub2);

    HashMap<String, Paint> resultingPaintHashMap = Whitebox.getInternalState(testSubject, "paints");
    Assert.assertEquals(resultingPaintHashMap.get("test"), paintMock);
    Assert.assertNull(resultingPaintHashMap.get("line"));
  }
}
