// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.Mockito.when;

import android.util.DisplayMetrics;
import android.view.accessibility.AccessibilityNodeInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AxeDeviceFactoryTest {

  @Mock DeviceConfigFactory deviceConfigFactoryMock;
  @Mock AccessibilityNodeInfo rootNodeMock;
  String deviceName = "test-device-name";
  String packageName = "test-package-name";
  String serviceVersion = "test-service-version";
  DeviceConfig deviceConfig;
  DisplayMetrics displayMetrics;

  AxeDeviceFactory testSubject;

  @Before
  public void prepare() {
    deviceConfig = new DeviceConfig(deviceName, packageName, serviceVersion);
    when(deviceConfigFactoryMock.getDeviceConfig(rootNodeMock)).thenReturn(deviceConfig);

    displayMetrics = new DisplayMetrics();
    displayMetrics.density = 1;
    displayMetrics.heightPixels = 2;
    displayMetrics.widthPixels = 3;

    testSubject = new AxeDeviceFactory(deviceConfigFactoryMock, () -> displayMetrics);
  }

  @Test
  public void axeDeviceIsNotNull() {
    Assert.assertNotNull(testSubject.createAxeDevice(rootNodeMock));
  }
}
