// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.content.Context;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ATFAScannerFactoryTest {
  @Mock Context contextMock;

  @Before
  public void prepare() {}

  @Test
  public void atfaScannerExists() {
    Assert.assertNotNull(ATFAScannerFactory.createATFAScanner(contextMock));
  }
}
