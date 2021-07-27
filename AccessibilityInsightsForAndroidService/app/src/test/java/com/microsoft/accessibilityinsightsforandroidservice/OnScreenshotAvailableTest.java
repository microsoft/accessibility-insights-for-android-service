// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Logger.class})
public class OnScreenshotAvailableTest {

  @Mock ImageReader imageReaderMock;
  @Mock Consumer<Bitmap> bitmapConsumerMock;
  @Mock Image imageMock;
  @Mock Image.Plane imagePlaneMock;
  @Mock BitmapProvider bitmapProviderMock;
  @Mock Bitmap bitmapMock;

  Image.Plane[] imagePlanesStub;
  OnScreenshotAvailable testSubject;
  int widthStub;
  int heightStub;
  ByteBuffer imagePlaneStubBuffer;
  int pixelStrideStub;
  int rowStrideStub;

  @Before
  public void prepare() {
    PowerMockito.mockStatic(Logger.class);

    DisplayMetrics metricsStub = new DisplayMetrics();
    metricsStub.widthPixels = widthStub;
    metricsStub.heightPixels = heightStub;
    widthStub = 100;
    heightStub = 200;
    pixelStrideStub = 4;
    rowStrideStub = widthStub * pixelStrideStub;
    imagePlanesStub = new Image.Plane[1];
    imagePlanesStub[0] = imagePlaneMock;

    testSubject = new OnScreenshotAvailable(metricsStub, bitmapProviderMock, bitmapConsumerMock);
  }

  @Test
  public void onScreenshotAvailableIsNotNull() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void onImageAvailableIgnoresImagesWithInvalidPixelStrides() {
    // pixelStride should be the number of bytes per pixel; our input should always be in ARGB_8888
    // format, so it should be fixed at 4. But if it's not, we shouldn't crash.
    pixelStrideStub = 5;

    setupMocksToCreateBitmap();

    testSubject.onImageAvailable(imageReaderMock);

    verify(bitmapMock, times(0)).copyPixelsFromBuffer(imagePlaneStubBuffer);
    verify(bitmapConsumerMock, times(0)).accept(bitmapMock);
  }

  @Test
  public void onImageAvailableIgnoresImagesWithInvalidRowStrides() {
    // rowStride should be the number of bytes per row plus optionally some padding; it should
    // never be lower than widthStub * pixelStrideStub, but if it is, we shouldn't crash.
    rowStrideStub = widthStub * pixelStrideStub - 1;

    setupMocksToCreateBitmap();

    testSubject.onImageAvailable(imageReaderMock);

    verify(bitmapMock, times(0)).copyPixelsFromBuffer(imagePlaneStubBuffer);
    verify(bitmapConsumerMock, times(0)).accept(bitmapMock);
  }

  @Test
  public void onImageAvailableWithUnpaddedImageBufferCreatesBitmapDirectlyFromSourceBuffer() {
    setupMocksToCreateBitmap();

    testSubject.onImageAvailable(imageReaderMock);

    verify(bitmapMock, times(1)).copyPixelsFromBuffer(imagePlaneStubBuffer);
    verify(bitmapConsumerMock, times(1)).accept(bitmapMock);
  }

  @Test
  public void onImageAvailableWithPaddedImageBufferStripsPaddingBeforeCopyingPixels() {
    widthStub = 2;
    heightStub = 2;
    int rowPaddingBytes = 1;
    rowStrideStub = (pixelStrideStub * widthStub + rowPaddingBytes);

    imagePlaneStubBuffer =
        ByteBuffer.wrap(new byte[] {1, 1, 1, 1, 2, 2, 2, 2, 0, 3, 3, 3, 3, 4, 4, 4, 4, 0});
    ByteBuffer bufferWithPaddingRemoved =
        ByteBuffer.wrap(new byte[] {1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4});

    setupMocksToCreateBitmap();

    testSubject.onImageAvailable(imageReaderMock);

    verify(bitmapMock, times(1)).copyPixelsFromBuffer(eq(bufferWithPaddingRemoved));
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
    when(imageMock.getHeight()).thenReturn(heightStub);
    when(imagePlaneMock.getBuffer()).thenReturn(imagePlaneStubBuffer);
    when(bitmapProviderMock.createBitmap(widthStub, heightStub, Bitmap.Config.ARGB_8888))
        .thenReturn(bitmapMock);
  }
}
