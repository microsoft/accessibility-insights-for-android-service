// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import java.util.List;
import java.util.function.Supplier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LayoutParamGeneratorTest {

  @Mock Supplier<DisplayMetrics> displayMetricsSupplier;

  @Mock DisplayMetrics displayMetrics;

  LayoutParamGenerator testSubject;

  @Before
  public void prepare() {
    testSubject = new LayoutParamGenerator(displayMetricsSupplier);
  }

  @Test
  public void generatesLayoutParams() throws Exception {
    when(displayMetricsSupplier.get()).thenReturn(displayMetrics);
    try (MockedConstruction<WindowManager.LayoutParams> layoutParamsConstructionMock =
        Mockito.mockConstruction(
            WindowManager.LayoutParams.class,
            (mockLayoutParams, context) -> {
              List<?> args = context.arguments();
              assertEquals(displayMetrics.widthPixels, args.get(0));
              assertEquals(displayMetrics.heightPixels, args.get(1));
              assertEquals(WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, args.get(2));
              assertEquals(
                  WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                      | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                      | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                      | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                  args.get(3));
              assertEquals(PixelFormat.TRANSLUCENT, args.get(4));
            })) {
      testSubject.get();
    }
  }
}
