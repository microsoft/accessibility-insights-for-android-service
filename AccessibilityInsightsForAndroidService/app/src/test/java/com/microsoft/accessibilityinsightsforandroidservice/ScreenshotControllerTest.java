// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Surface;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ScreenshotControllerTest {

  @Mock Supplier<DisplayMetrics> displayMetricsSupplierMock;
  @Mock Handler handlerMock;
  @Mock OnScreenshotAvailableProvider onScreenshotAvailableProviderMock;
  @Mock BitmapProvider bitmapProviderMock;
  @Mock Consumer<Bitmap> bitmapConsumerMock;
  @Mock Supplier<MediaProjection> mediaProjectionSupplierMock;
  @Mock MediaProjection mediaProjectionMock;
  @Mock ImageReader imageReaderMock;
  @Mock Surface surfaceMock;
  @Mock Bitmap bitmapMock;
  @Mock OnScreenshotAvailable onScreenshotAvailableMock;
  @Mock VirtualDisplay displayMock;

  MockedStatic<ImageReader> imageReaderStaticMock;

  @Captor ArgumentCaptor<Consumer<Bitmap>> bitmapConsumerCallback;

  DisplayMetrics displayMetricsStub;
  ScreenshotController testSubject;

  @Before
  public void prepare() {
    imageReaderStaticMock = Mockito.mockStatic(ImageReader.class);
    displayMetricsStub = new DisplayMetricsStub();
    testSubject =
        new ScreenshotController(
            displayMetricsSupplierMock,
            handlerMock,
            onScreenshotAvailableProviderMock,
            bitmapProviderMock,
            mediaProjectionSupplierMock);
  }

  @After
  public void cleanUp() {
    imageReaderStaticMock.close();
  }

  @Test
  public void screenshotControllerIsNotNull() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void nullBitmapReturnedWhenSharedMediaProjectionIsNull() {
    when(mediaProjectionSupplierMock.get()).thenReturn(null);

    testSubject.getScreenshotWithMediaProjection(bitmapConsumerMock);

    verify(bitmapConsumerMock, times(1)).accept(null);
  }

  @Test
  public void createVirtualDisplayWithExpectedImageReader() {
    bitmapConsumerCallback = createBitmapConsumerCallback();
    when(mediaProjectionSupplierMock.get()).thenReturn(mediaProjectionMock);
    when(displayMetricsSupplierMock.get()).thenReturn(displayMetricsStub);
    imageReaderStaticMock
        .when(
            () ->
                ImageReader.newInstance(
                    displayMetricsStub.widthPixels,
                    displayMetricsStub.heightPixels,
                    PixelFormat.RGBA_8888,
                    2))
        .thenReturn(imageReaderMock);
    when(imageReaderMock.getSurface()).thenReturn(surfaceMock);
    when(onScreenshotAvailableProviderMock.getOnScreenshotAvailable(
            eq(displayMetricsStub), eq(bitmapProviderMock), bitmapConsumerCallback.capture()))
        .thenReturn(onScreenshotAvailableMock);
    when(mediaProjectionMock.createVirtualDisplay(
            "myDisplay",
            displayMetricsStub.widthPixels,
            displayMetricsStub.heightPixels,
            displayMetricsStub.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            surfaceMock,
            null,
            null))
        .thenReturn(displayMock);

    testSubject.getScreenshotWithMediaProjection(bitmapConsumerMock);
    bitmapConsumerCallback.getValue().accept(bitmapMock);

    verify(displayMock, times(1)).release();
    verify(bitmapConsumerMock, times(1)).accept(bitmapMock);
  }

  @SuppressWarnings("unchecked")
  private ArgumentCaptor<Consumer<Bitmap>> createBitmapConsumerCallback() {
    return ArgumentCaptor.forClass(Consumer.class);
  }

  @Test
  public void createVirtualDisplayCleansResourcesAppropriatelyBeforeGettingScreenshot() {
    when(mediaProjectionSupplierMock.get()).thenReturn(mediaProjectionMock);
    when(displayMetricsSupplierMock.get()).thenReturn(displayMetricsStub);
    imageReaderStaticMock
        .when(
            () ->
                ImageReader.newInstance(
                    displayMetricsStub.widthPixels,
                    displayMetricsStub.heightPixels,
                    PixelFormat.RGBA_8888,
                    2))
        .thenReturn(imageReaderMock);
    when(imageReaderMock.getSurface()).thenReturn(surfaceMock);
    when(mediaProjectionMock.createVirtualDisplay(
            "myDisplay",
            displayMetricsStub.widthPixels,
            displayMetricsStub.heightPixels,
            displayMetricsStub.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            surfaceMock,
            null,
            null))
        .thenReturn(displayMock);

    testSubject.getScreenshotWithMediaProjection(bitmapConsumerMock);
    testSubject.getScreenshotWithMediaProjection(bitmapConsumerMock);

    verify(displayMock, times(1)).release();
    verify(imageReaderMock, times(1)).close();
  }
}
