// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.graphics.Color;
import android.graphics.Paint;
import java.util.HashMap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FocusVisualizerStylesTest {

  FocusVisualizerStyles testSubject;

  MockedConstruction<Paint> paintConstructionMock;
  MockedStatic<Color> colorStaticMock;

  @Before
  public void prepare() throws Exception {
    paintConstructionMock = Mockito.mockConstruction(Paint.class);
    colorStaticMock = Mockito.mockStatic(Color.class);
    testSubject = new FocusVisualizerStyles();
  }

  @After
  public void cleanUp() {
    colorStaticMock.close();
    paintConstructionMock.close();
  }

  @Test
  public void getCurrentElementPaintsReturnsAllRelevantPaints() {
    HashMap<String, Paint> paints = testSubject.getCurrentElementPaints();
    Assert.assertNotNull(paints.get("outerCircle"));
    Assert.assertNotNull(paints.get("innerCircle"));
    Assert.assertNotNull(paints.get("number"));
    Assert.assertNotNull(paints.get("transparentInnerCircle"));
  }

  @Test
  public void getNonCurrentElementPaintsReturnsAllRelevantPaints() {
    HashMap<String, Paint> paints = testSubject.getNonCurrentElementPaints();
    Assert.assertNotNull(paints.get("outerCircle"));
    Assert.assertNotNull(paints.get("innerCircle"));
    Assert.assertNotNull(paints.get("number"));
  }

  @Test
  public void getNonCurrentLinePaintsReturnsAllRelevantPaints() {
    HashMap<String, Paint> paints = testSubject.getNonCurrentLinePaints();
    Assert.assertNotNull(paints.get("foregroundLine"));
    Assert.assertNotNull(paints.get("backgroundLine"));
  }

  @Test
  public void getCurrentLinePaintsReturnsAllRelevantPaints() {
    HashMap<String, Paint> paints = testSubject.getCurrentLinePaints();
    Assert.assertNotNull(paints.get("foregroundLine"));
    Assert.assertNotNull(paints.get("backgroundLine"));
  }
}
