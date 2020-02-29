// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.util.DisplayMetrics;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AxeScannerFactoryTest {

  @Mock DeviceConfigFactory deviceConfigFactoryMock;
  @Mock DisplayMetrics displayMetricsMock;

  @Before
  public void prepare() {}

  @Test
  public void axeScannerExists() {
    Assert.assertNotNull(
        AxeScannerFactory.createAxeScanner(deviceConfigFactoryMock, () -> displayMetricsMock));
  }
}
