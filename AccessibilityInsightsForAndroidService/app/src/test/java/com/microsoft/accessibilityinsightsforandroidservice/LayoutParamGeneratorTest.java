// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class LayoutParamGeneratorTest {

  @Mock Supplier<DisplayMetrics> displayMetricsSupplier;

  @Mock WindowManager.LayoutParams layoutParams;

  @Mock DisplayMetrics displayMetrics;

  LayoutParamGenerator testSubject;

  @Before
  public void prepare() {
    testSubject = new LayoutParamGenerator(displayMetricsSupplier);
  }

  @Test
  public void generatesLayoutParams() throws Exception {
    when(displayMetricsSupplier.get()).thenReturn(displayMetrics);
    whenNew(WindowManager.LayoutParams.class)
        .withArguments(
            displayMetrics.widthPixels,
            displayMetrics.heightPixels,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT)
        .thenReturn(layoutParams);

    // Cannot check for equality due to not being able to mock toString on LayoutParams
    Assert.assertNotNull(testSubject.get());
  }
}
