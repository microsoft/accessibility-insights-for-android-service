// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.powermock.api.mockito.PowerMockito.when;

import android.content.res.Resources;
import android.view.View;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(MockitoJUnitRunner.class)
public class OffsetHelperTest {

  @Mock View viewMock;
  @Mock Resources resourcesMock;

  @Test
  public void getYOffsetReturnsCenterOfElement() {
    when(viewMock.getResources()).thenReturn(resourcesMock);
    when(resourcesMock.getIdentifier("status_bar_height", "dimen", "android")).thenReturn(1);
    when(resourcesMock.getDimensionPixelSize(anyInt())).thenReturn(10);

    Assert.assertEquals(OffsetHelper.getYOffset(viewMock), 5);
  }
}
