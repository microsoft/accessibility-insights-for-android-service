// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static android.os.Build.MODEL;
import static org.mockito.Mockito.when;

import android.os.Build;
import android.view.accessibility.AccessibilityNodeInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeviceConfigFactoryTest {
  final CharSequence samplePackageName = "test-package-name";
  final String packageNameUnavailable = "No application detected";
  final String sampleBuildModel = "test-build-model";

  @Mock AccessibilityNodeInfo mockRootNode;

  DeviceConfigFactory testSubject;

  @Before
  public void prepare() {
    testSubject = new DeviceConfigFactory(sampleBuildModel);
  }

  @Test
  public void deviceConfigFactoryExists() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void getDeviceConfigReturnsNonNullDeviceConfig() {
    Assert.assertNotNull(testSubject.getDeviceConfig(mockRootNode));
  }

  @Test
  public void deviceConfigFactoryPropertiesExist() {
    DeviceConfig deviceConfig = testSubject.getDeviceConfig(mockRootNode);

    Assert.assertNotNull(deviceConfig.deviceName);
    Assert.assertEquals(sampleBuildModel, deviceConfig.deviceName);
    Assert.assertNotNull(deviceConfig.packageName);
    Assert.assertNotNull(deviceConfig.serviceVersion);
  }

  @Test
  public void deviceConfigFactoryGetsProperPackageName() {
    when(mockRootNode.getPackageName()).thenReturn(samplePackageName);

    Assert.assertEquals(samplePackageName, getActualPackageName(mockRootNode));
  }

  @Test
  public void deviceConfigFactoryGetsNoPackageNameWhenPackageNameIsNull() {
    when(mockRootNode.getPackageName()).thenReturn(null);

    Assert.assertEquals(packageNameUnavailable, getActualPackageName(mockRootNode));
  }

  @Test
  public void deviceConfigFactoryGetsNoPackageNameWhenRootNodeIsNull() {
    Assert.assertEquals(packageNameUnavailable, getActualPackageName(null));
  }

  private String getActualPackageName(AccessibilityNodeInfo rootNode) {
    return testSubject.getDeviceConfig(rootNode).packageName;
  }
}
