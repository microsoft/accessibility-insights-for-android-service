// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.graphics.Bitmap;
import android.view.accessibility.AccessibilityNodeInfo;
import com.deque.axe.android.AxeContext;
import com.deque.axe.android.AxeDevice;
import com.deque.axe.android.AxeView;
import com.deque.axe.android.colorcontrast.AxeImage;
import com.deque.axe.android.wrappers.AxeEventStream;

public class AxeContextFactory {
  private final AxeImageFactory axeImageFactory;
  private final AxeViewsFactory axeViewsFactory;
  private final AxeDeviceFactory axeDeviceFactory;

  public AxeContextFactory(
      AxeImageFactory axeImageFactory,
      AxeViewsFactory axeViewsFactory,
      AxeDeviceFactory axeDeviceFactory) {
    this.axeImageFactory = axeImageFactory;
    this.axeViewsFactory = axeViewsFactory;
    this.axeDeviceFactory = axeDeviceFactory;
  }

  public AxeContext createAxeContext(AccessibilityNodeInfo rootNode, Bitmap screenshot)
      throws ViewChangedException {
    AxeView axeView = axeViewsFactory.createAxeViews(rootNode);
    AxeDevice axeDevice = axeDeviceFactory.createAxeDevice(rootNode);
    AxeImage axeImage = axeImageFactory.createAxeImage(screenshot);
    AxeEventStream axeEventStream = new AxeEventStream();
    return new AxeContext(axeView, axeDevice, axeImage, axeEventStream);
  }
}
