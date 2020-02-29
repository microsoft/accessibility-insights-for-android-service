// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.when;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Resources.class})
public class DisplayMetricsHelperTest {

  @Mock Resources resourcesMock;
  @Mock DisplayMetrics displayMetricsMock;
  @Mock Context contextMock;
  @Mock WindowManager windowManagerMock;
  @Mock Display displayMock;

  @Before
  public void prepare() {
    setupDisplayMocks();
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
    PowerMockito.mockStatic(Resources.class);
    when(Resources.getSystem()).thenReturn(resourcesMock);
    when(resourcesMock.getDisplayMetrics()).thenReturn(displayMetricsMock);
    when(contextMock.getSystemService(Context.WINDOW_SERVICE)).thenReturn(windowManagerMock);
    when(windowManagerMock.getDefaultDisplay()).thenReturn(displayMock);
  }
}
