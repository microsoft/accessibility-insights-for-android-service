// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DeviceConfigTest {
  String deviceName = "test-device-name";
  String packageName = "test-package-name";
  String serviceVersion = "test-service-version";

  DeviceConfig testSubject;

  @Before
  public void prepare() {
    testSubject = new DeviceConfig(deviceName, packageName, serviceVersion);
  }

  @Test
  public void deviceConfigExists() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void deviceConfigHasExpectedProperties() {
    Assert.assertEquals(deviceName, testSubject.deviceName);
    Assert.assertEquals(packageName, testSubject.packageName);
    Assert.assertEquals(serviceVersion, testSubject.serviceVersion);
  }
}
