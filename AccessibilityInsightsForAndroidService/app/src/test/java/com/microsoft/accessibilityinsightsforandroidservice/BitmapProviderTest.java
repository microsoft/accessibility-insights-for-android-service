// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.powermock.api.mockito.PowerMockito.when;

import android.graphics.Bitmap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({Bitmap.class})
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

    PowerMockito.verifyStatic(Bitmap.class);
    Bitmap.createBitmap(width, height, config);
  }
}
