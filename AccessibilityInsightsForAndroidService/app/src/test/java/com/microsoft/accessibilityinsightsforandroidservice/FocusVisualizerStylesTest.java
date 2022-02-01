// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.graphics.Color;
import android.graphics.Paint;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({FocusVisualizerStyles.class, Color.class})
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
