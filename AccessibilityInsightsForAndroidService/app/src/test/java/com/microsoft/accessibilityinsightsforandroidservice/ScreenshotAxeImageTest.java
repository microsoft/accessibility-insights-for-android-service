// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

import android.graphics.Bitmap;
import android.util.Base64;
import com.deque.axe.android.colorcontrast.AxeColor;
import com.deque.axe.android.wrappers.AxeRect;
import java.io.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Base64.class})
public class ScreenshotAxeImageTest {

  @Mock Bitmap bitmapMock;
  @Mock ByteArrayOutputStreamProvider byteArrayOutputStreamProviderMock;
  @Mock ByteArrayOutputStream byteArrayOutputStreamMock;

  int sampleWidth;
  int sampleHeight;
  ScreenshotAxeImage testSubject;

  @Before
  public void prepare() {
    sampleHeight = 100;
    sampleWidth = 50;

    when(bitmapMock.getWidth()).thenReturn(sampleWidth);
    when(bitmapMock.getHeight()).thenReturn(sampleHeight);

    testSubject = new ScreenshotAxeImage(bitmapMock, byteArrayOutputStreamProviderMock);
  }

  @Test
  public void screenShotAxeImageExists() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void pixelReturnsCorrectColor() {
    int givenX = 10;
    int givenY = 20;
    int colorIntStub = 100;

    when(bitmapMock.getPixel(givenX, givenY)).thenReturn(colorIntStub);

    AxeColor returnedAxeColor = testSubject.pixel(givenX, givenY);

    Assert.assertEquals(returnedAxeColor, new AxeColor(colorIntStub));
  }

  @Test
  public void frameReturnsCorrectRect() {
    AxeRect expectedRect = new AxeRect(0, sampleWidth - 1, 0, sampleHeight - 1);

    Assert.assertEquals(testSubject.frame(), expectedRect);
  }

  @Test
  public void toBase64PngReturnsCorrectString() {
    byte[] byteArrayStub = new byte[1];
    String expectedString = "some string";
    PowerMockito.mockStatic(Base64.class);

    when(byteArrayOutputStreamProviderMock.get()).thenReturn(byteArrayOutputStreamMock);
    when(byteArrayOutputStreamMock.toByteArray()).thenReturn(byteArrayStub);
    when(Base64.encodeToString(byteArrayStub, Base64.NO_WRAP)).thenReturn(expectedString);

    Assert.assertEquals(testSubject.toBase64Png(), expectedString);

    verify(bitmapMock, times(1))
        .compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStreamMock);
  }
}
