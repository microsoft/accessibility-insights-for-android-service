// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.graphics.Bitmap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BitmapProviderTest {

  @Mock Bitmap.Config config;
  @Mock Bitmap bitmapMock;
  MockedStatic<Bitmap> bitmapStaticMock;

  private final int width = 1;
  private final int height = 2;

  BitmapProvider testSubject;

  @Before
  public void prepare() {
    bitmapStaticMock = Mockito.mockStatic(Bitmap.class);
    bitmapStaticMock.when(() -> Bitmap.createBitmap(width, height, config)).thenReturn(bitmapMock);
    testSubject = new BitmapProvider();
  }

  @After
  public void cleanUp() {
    bitmapStaticMock.close();
  }

  @Test
  public void bitmapIsNotNull() {
    Bitmap createdBitmap = testSubject.createBitmap(width, height, config);
    Assert.assertNotNull(createdBitmap);
    Assert.assertEquals(createdBitmap, bitmapMock);

    bitmapStaticMock.verify(() -> Bitmap.createBitmap(width, height, config));
  }
}
