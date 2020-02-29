// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.graphics.Bitmap;
import android.media.Image;
import android.media.ImageReader;
import android.util.DisplayMetrics;
import java.nio.ByteBuffer;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OnScreenshotAvailableTest {

  @Mock ImageReader imageReaderMock;
  @Mock Consumer<Bitmap> bitmapConsumerMock;
  @Mock Image imageMock;
  @Mock Image.Plane imagePlaneMock;
  @Mock ByteBuffer bufferMock;
  @Mock BitmapProvider bitmapProviderMock;
  @Mock Bitmap bitmapMock;

  Image.Plane[] imagePlanesStub;
  OnScreenshotAvailable testSubject;
  int widthStub = 100;
  int heightStub = 200;
  int pixelStrideStub;
  int rowStrideStub;
  int rowPadding;
  int expectedBitmapWidth;

  @Before
  public void prepare() {
    DisplayMetrics metricsStub = new DisplayMetrics();
    metricsStub.widthPixels = widthStub;
    metricsStub.heightPixels = heightStub;
    pixelStrideStub = 20;
    rowStrideStub = 10;
    rowPadding = rowStrideStub - pixelStrideStub * widthStub;
    expectedBitmapWidth = widthStub + rowPadding / pixelStrideStub;
    imagePlanesStub = new Image.Plane[1];
    imagePlanesStub[0] = imagePlaneMock;

    testSubject = new OnScreenshotAvailable(bitmapConsumerMock, metricsStub, bitmapProviderMock);
  }

  @Test
  public void onScreenshotAvailableIsNotNull() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void onImageAvailableCreatesCorrectScreenshotBitmap() {
    setupMocksToCreateBitmap();

    testSubject.onImageAvailable(imageReaderMock);

    verify(bitmapMock, times(1)).copyPixelsFromBuffer(bufferMock);
    verify(bitmapConsumerMock, times(1)).accept(bitmapMock);
  }

  @Test
  public void onImageAvailableProcessesImageOnlyOnce() {
    setupMocksToCreateBitmap();
    testSubject.onImageAvailable(imageReaderMock);
    reset(
        imageReaderMock,
        bitmapConsumerMock,
        imageMock,
        imagePlaneMock,
        bufferMock,
        bitmapProviderMock,
        bitmapMock);

    testSubject.onImageAvailable(imageReaderMock);

    verify(bitmapMock, times(0)).copyPixelsFromBuffer(any());
    verify(bitmapConsumerMock, times(0)).accept(any());
  }

  private void setupMocksToCreateBitmap() {
    when(imageReaderMock.acquireLatestImage()).thenReturn(imageMock);
    when(imageMock.getPlanes()).thenReturn(imagePlanesStub);
    when(imagePlaneMock.getPixelStride()).thenReturn(pixelStrideStub);
    when(imagePlaneMock.getRowStride()).thenReturn(rowStrideStub);
    when(imageMock.getWidth()).thenReturn(widthStub);
    when(imagePlaneMock.getBuffer()).thenReturn(bufferMock);
    when(bitmapProviderMock.createBitmap(expectedBitmapWidth, heightStub, Bitmap.Config.ARGB_8888))
        .thenReturn(bitmapMock);
  }
}
