// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import android.os.CancellationSignal;
import android.view.accessibility.AccessibilityNodeInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConfigRequestFulfillerTest {

  @Mock RootNodeFinder rootNodeFinder;
  @Mock EventHelper eventHelper;
  @Mock DeviceConfigFactory deviceConfigFactory;
  @Mock AccessibilityNodeInfo sourceNodeMock;
  @Mock AccessibilityNodeInfo rootNodeMock;
  @Mock DeviceConfig deviceConfig;
  @Mock CancellationSignal cancellationSignal;

  String configJson = "test config";

  ConfigRequestFulfiller testSubject;

  @Before
  public void prepare() {
    testSubject = new ConfigRequestFulfiller(rootNodeFinder, eventHelper, deviceConfigFactory);
  }

  @Test
  public void configRequestFulfillerExists() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void writesCorrectResponse() {
    setupSuccessfulRequest();

    assertEquals(configJson, testSubject.fulfillRequest(cancellationSignal));
  }

  @Test
  public void recyclesNodes() {
    setupSuccessfulRequest();

    testSubject.fulfillRequest(cancellationSignal);

    verify(rootNodeMock, times(1)).recycle();
    verify(sourceNodeMock, times(1)).recycle();
  }

  @Test
  public void recyclesNodeOnceIfRootEqualsSource() {
    setupSuccessfulRequest();
    reset(rootNodeFinder);
    reset(deviceConfigFactory);
    when(rootNodeFinder.getRootNodeFromSource(any())).thenReturn(sourceNodeMock);
    when(deviceConfigFactory.getDeviceConfig(sourceNodeMock)).thenReturn(deviceConfig);

    testSubject.fulfillRequest(cancellationSignal);

    verifyNoInteractions(rootNodeMock);
    verify(sourceNodeMock, times(1)).recycle();
  }

  @Test
  public void doesNotRecycleSourceIfRestoreLastSourceSucceeds() {
    setupSuccessfulRequest();
    when(eventHelper.restoreLastSource(sourceNodeMock)).thenReturn(true);

    testSubject.fulfillRequest(cancellationSignal);
    verify(rootNodeMock, times(1)).recycle();
    verify(sourceNodeMock, never()).recycle();
  }

  private void setupSuccessfulRequest() {
    when(eventHelper.claimLastSource()).thenReturn(sourceNodeMock);
    when(rootNodeFinder.getRootNodeFromSource(sourceNodeMock)).thenReturn(rootNodeMock);
    when(deviceConfigFactory.getDeviceConfig(rootNodeMock)).thenReturn(deviceConfig);
    when(deviceConfig.toJson()).thenReturn(configJson);
  }
}
