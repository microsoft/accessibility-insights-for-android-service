// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.accessibility.AccessibilityNodeInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EventHelperTest {
  @Mock ThreadSafeSwapper<AccessibilityNodeInfo> mockSwapper;

  @Mock AccessibilityNodeInfo mockNodeInfo;

  @Mock AccessibilityNodeInfo mockLastNodeInfo;

  EventHelper testSubject;

  @Before
  public void prepare() {
    testSubject = new EventHelper(mockSwapper);
  }

  @Test
  public void eventHelperExists() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void claimLastSourceReturnsExpectedNodeInfo() {
    when(mockSwapper.swap(null)).thenReturn(mockNodeInfo);

    AccessibilityNodeInfo actualResponse = testSubject.claimLastSource();

    Assert.assertEquals(mockNodeInfo, actualResponse);
  }

  @Test
  public void claimLastSourceCallsSwapWithNullObjectOnlyOnce() {
    testSubject.claimLastSource();

    verify(mockSwapper, times(1)).swap(null);
  }

  @Test
  public void restoreLastSourceCallsSetIfCurrentlyNullOnlyOnce() {
    testSubject.restoreLastSource(mockNodeInfo);

    verify(mockSwapper, times(1)).setIfCurrentlyNull(mockNodeInfo);
  }

  @Test
  public void recordEventProperlyHandlesNonNullEventSource() {
    when(mockSwapper.swap(mockNodeInfo)).thenReturn(mockLastNodeInfo);

    testSubject.recordEvent(mockNodeInfo);

    verify(mockLastNodeInfo, times(1)).recycle();
  }

  @Test
  public void recordEventProperlyHandlesNullEventSource() {
    testSubject.recordEvent(null);

    verify(mockSwapper, times(0)).swap(mockNodeInfo);
    verify(mockLastNodeInfo, times(0)).recycle();
  }

  @Test
  public void recordEventProperlyHandlesNullLastSource() {
    when(mockSwapper.swap(mockNodeInfo)).thenReturn(null);

    testSubject.recordEvent(mockNodeInfo);

    verify(mockLastNodeInfo, times(0)).recycle();
  }
}
