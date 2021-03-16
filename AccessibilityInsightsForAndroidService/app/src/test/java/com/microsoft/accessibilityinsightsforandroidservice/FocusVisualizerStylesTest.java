// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.graphics.Color;
import android.graphics.Paint;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FocusVisualizerStyles.class, Color.class})
public class FocusVisualizerStylesTest {

    FocusVisualizerStyles testSubject;

    @Mock Paint paintMock;

    @Before
    public void prepare() throws Exception {
        PowerMockito.whenNew(Paint.class).withNoArguments().thenReturn(paintMock);
        PowerMockito.mockStatic(Color.class);
        testSubject = new FocusVisualizerStyles();
    }

    @Test
    public void getCurrentElementPaintsReturnsAllRelevantPaints(){
        HashMap<String, Paint> paints = testSubject.getCurrentElementPaints();
        Assert.assertNotNull(paints.get("outerCircle"));
        Assert.assertNotNull(paints.get("innerCircle"));
        Assert.assertNotNull(paints.get("number"));
    }

    @Test
    public void getNonCurrentElementPaintsReturnsAllRelevantPaints(){
        HashMap<String, Paint> paints = testSubject.getNonCurrentElementPaints();
        Assert.assertNotNull(paints.get("outerCircle"));
        Assert.assertNotNull(paints.get("innerCircle"));
        Assert.assertNotNull(paints.get("number"));
    }

    @Test
    public void getNonCurrentLinePaintsReturnsAllRelevantPaints(){
        HashMap<String, Paint> paints = testSubject.getNonCurrentLinePaints();
        Assert.assertNotNull(paints.get("line"));
    }

    @Test
    public void getCurrentLinePaintsReturnsAllRelevantPaints(){
        HashMap<String, Paint> paints = testSubject.getCurrentLinePaints();
        Assert.assertNotNull(paints.get("line"));
    }
}