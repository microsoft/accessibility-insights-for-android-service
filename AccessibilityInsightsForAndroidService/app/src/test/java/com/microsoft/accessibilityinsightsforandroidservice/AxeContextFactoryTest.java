// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.Mockito.when;

import android.graphics.Bitmap;
import android.view.accessibility.AccessibilityNodeInfo;
import com.deque.axe.android.AxeContext;
import com.deque.axe.android.AxeDevice;
import com.deque.axe.android.AxeView;
import com.deque.axe.android.colorcontrast.AxeImage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AxeContextFactoryTest {

  @Mock AxeImageFactory axeImageFactoryMock;
  @Mock AxeImage axeImageMock;
  @Mock AxeViewsFactory axeViewsFactoryMock;
  @Mock AxeView axeViewMock;
  @Mock AxeDeviceFactory axeDeviceFactoryMock;
  @Mock AxeDevice axeDeviceMock;
  @Mock AccessibilityNodeInfo rootNodeMock;
  @Mock Bitmap screenshotMock;

  AxeContextFactory testSubject;

  @Before
  public void prepare() throws ViewChangedException {
    when(axeImageFactoryMock.createAxeImage(screenshotMock)).thenReturn(axeImageMock);
    when(axeViewsFactoryMock.createAxeViews(rootNodeMock)).thenReturn(axeViewMock);
    when(axeDeviceFactoryMock.createAxeDevice(rootNodeMock)).thenReturn(axeDeviceMock);

    testSubject =
        new AxeContextFactory(axeImageFactoryMock, axeViewsFactoryMock, axeDeviceFactoryMock);
  }

  @Test
  public void axeContentIsNotNull() throws ViewChangedException {
    Assert.assertNotNull(testSubject.createAxeContext(rootNodeMock, screenshotMock));
  }

  @Test
  public void axeContentHasCorrectProperties() throws ViewChangedException {
    AxeContext axeContext = testSubject.createAxeContext(rootNodeMock, screenshotMock);
    Assert.assertEquals(axeContext.screenshot, axeImageMock);
    Assert.assertEquals(axeContext.axeDevice, axeDeviceMock);
    Assert.assertEquals(axeContext.axeView, axeViewMock);
    Assert.assertNotNull(axeContext.axeEventStream);
  }
}
