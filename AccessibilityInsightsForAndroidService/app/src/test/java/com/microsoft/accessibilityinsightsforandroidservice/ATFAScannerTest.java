// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckPreset;
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResult;
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResultUtils;
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityHierarchyCheck;
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityHierarchyCheckResult;
import com.google.android.apps.common.testing.accessibility.framework.Parameters;
import com.google.android.apps.common.testing.accessibility.framework.uielement.AccessibilityHierarchyAndroid;
import com.google.android.apps.common.testing.accessibility.framework.utils.contrast.BitmapImage;
import com.google.common.collect.ImmutableSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AccessibilityCheckPreset.class, AccessibilityHierarchyAndroid.class, AccessibilityCheckResultUtils.class})
public class ATFAScannerTest {

  @Mock Bitmap bitmapMock;
  @Mock AccessibilityNodeInfo accessibilityNodeInfoMock;
  @Mock Context contextMock;
  @Mock Parameters parametersMock;
  @Mock AccessibilityHierarchyCheck checkMock;
  @Mock AccessibilityHierarchyAndroid hierarchyMock;
  @Mock AccessibilityHierarchyAndroid.BuilderAndroid builderMock;
  @Mock List<AccessibilityHierarchyCheckResult> resultsMock;
  @Mock List<AccessibilityCheckResult> filteredResultsMock;

  ATFAScanner testSubject;
  BitmapImage screenshotMock;

  @Before
  public void prepare() {
    PowerMockito.mockStatic(AccessibilityCheckPreset.class);
    PowerMockito.mockStatic(AccessibilityHierarchyAndroid.class);
    PowerMockito.mockStatic(AccessibilityCheckResultUtils.class);
    screenshotMock = new BitmapImage(bitmapMock);
    testSubject = new ATFAScanner(contextMock);
  }

  @Test
  public void scanWithATFAReturnsCorrectResult() throws ViewChangedException {
    when(AccessibilityCheckPreset.getAccessibilityHierarchyChecksForPreset(AccessibilityCheckPreset.LATEST)).thenReturn(ImmutableSet.of(checkMock));
    when(AccessibilityHierarchyAndroid.newBuilder(accessibilityNodeInfoMock, contextMock)).thenReturn(builderMock);
    when(builderMock.build()).thenReturn(hierarchyMock);
    when(checkMock.runCheckOnHierarchy(hierarchyMock, null, parametersMock)).thenReturn(resultsMock);
    when(AccessibilityCheckResultUtils.getResultsForTypes(eq(resultsMock), anySet())).thenReturn(filteredResultsMock);

    Assert.assertEquals(
        testSubject.scanWithATFA(accessibilityNodeInfoMock, screenshotMock), resultsMock);

  }
}
