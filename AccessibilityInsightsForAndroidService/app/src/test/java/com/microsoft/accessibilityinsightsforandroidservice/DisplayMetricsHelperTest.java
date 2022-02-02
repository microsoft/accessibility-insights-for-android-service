// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DisplayMetricsHelperTest {

  @Mock Resources resourcesMock;
  @Mock DisplayMetrics displayMetricsMock;
  @Mock Context contextMock;
  @Mock WindowManager windowManagerMock;
  @Mock Display displayMock;

  MockedStatic<Resources> resourcesStaticMock;

  @Before
  public void prepare() {
    setupDisplayMocks();
  }

  @After
  public void cleanUp() {
    resourcesStaticMock.close();
  }

  @Test
  public void returnsNotNull() {
    Assert.assertNotNull(DisplayMetricsHelper.getRealDisplayMetrics(contextMock));
  }

  @Test
  public void returnsExpectedDisplayMetrics() {
    DisplayMetrics actualDisplayMetrics = DisplayMetricsHelper.getRealDisplayMetrics(contextMock);

    verify(displayMock, times(1)).getRealMetrics(displayMetricsMock);
    Assert.assertEquals(actualDisplayMetrics, displayMetricsMock);
  }

  private void setupDisplayMocks() {
    resourcesStaticMock = Mockito.mockStatic(Resources.class);
    resourcesStaticMock.when(Resources::getSystem).thenReturn(resourcesMock);
    when(resourcesMock.getDisplayMetrics()).thenReturn(displayMetricsMock);
    when(contextMock.getSystemService(Context.WINDOW_SERVICE)).thenReturn(windowManagerMock);
    when(windowManagerMock.getDefaultDisplay()).thenReturn(displayMock);
  }
}
