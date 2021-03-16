package com.microsoft.accessibilityinsightsforandroidservice;

import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FocusElementHighlight.class})
public class FocusElementHighlightTest {
    FocusElementHighlight testSubject;

    @Mock AccessibilityNodeInfo accessibilityNodeInfoMock;
    @Mock View viewMock;
    @Mock Paint paintMock;
    @Mock Resources resourcesMock;
    @Mock Rect rectMock;

    @Before
    public void prepare() throws Exception {
        HashMap<String, Paint> paintsStub = new HashMap<>();
        paintsStub.put("innerCircle", paintMock);
        paintsStub.put("outerCircle", paintMock);
        paintsStub.put("number", paintMock);

        when(viewMock.getResources()).thenReturn(resourcesMock);
        whenNew(Rect.class).withNoArguments().thenReturn(rectMock);
        doNothing().when(rectMock).offset(isA(Integer.class), isA(Integer.class));

        testSubject = new FocusElementHighlight(
                accessibilityNodeInfoMock,
                paintsStub,
                10,
                10,
                viewMock);
    }

    @Test
    public void returnsNotNull(){
        Assert.assertNotNull(testSubject);
    }

    @Test
    public void followsCorrectStepsToUpdateCoordinates() throws Exception {
        FocusElementHighlight elementSpy = spy(testSubject);
        elementSpy.updateWithNewCoordinates();
        verifyPrivate(elementSpy, times(1)).invoke("setYOffset");
        verifyPrivate(elementSpy, times(1)).invoke("setCoordinates");
    }

    //TODO:  figure out what other tests are needed on this class
}
