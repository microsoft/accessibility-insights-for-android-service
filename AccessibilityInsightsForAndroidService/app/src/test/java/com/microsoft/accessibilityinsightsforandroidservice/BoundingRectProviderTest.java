// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BoundingRectProviderTest {

  AxeRectProvider testSubject;

  @Before
  public void prepare() {
    testSubject = new AxeRectProvider();
  }

  @Test
  public void boundingRectExists() {
    Assert.assertNotNull(testSubject.createAxeRect(0, 1, 2, 3));
  }
}
