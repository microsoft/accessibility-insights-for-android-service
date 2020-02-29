// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ByteArrayOutputStreamProviderTest {

  ByteArrayOutputStreamProvider testSubject;

  @Before
  public void prepare() {
    testSubject = new ByteArrayOutputStreamProvider();
  }

  @Test
  public void byteArrayOutputStreamExists() {
    Assert.assertNotNull(testSubject.get());
  }
}
