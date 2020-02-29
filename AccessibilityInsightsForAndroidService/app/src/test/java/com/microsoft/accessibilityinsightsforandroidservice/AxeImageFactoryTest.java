// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.graphics.Bitmap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AxeImageFactoryTest {

  @Mock ByteArrayOutputStreamProvider byteArrayOutputStreamProviderMock;
  @Mock Bitmap screenshotMock;

  AxeImageFactory testSubject;

  @Before
  public void prepare() {
    testSubject = new AxeImageFactory(byteArrayOutputStreamProviderMock);
  }

  @Test
  public void axeImageIsNotNull() {
    Assert.assertNotNull(testSubject.createAxeImage(screenshotMock));
  }
}
