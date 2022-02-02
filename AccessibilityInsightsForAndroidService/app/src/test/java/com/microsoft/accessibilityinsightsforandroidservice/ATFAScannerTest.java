// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.accessibility.AccessibilityNodeInfo;
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckPreset;
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResultUtils;
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityHierarchyCheck;
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityHierarchyCheckResult;
import com.google.android.apps.common.testing.accessibility.framework.Parameters;
import com.google.android.apps.common.testing.accessibility.framework.uielement.AccessibilityHierarchyAndroid;
import com.google.android.apps.common.testing.accessibility.framework.utils.contrast.BitmapImage;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ATFAScannerTest {

  @Mock Bitmap bitmapMock;
  @Mock AccessibilityNodeInfo accessibilityNodeInfoMock;
  @Mock Context contextMock;
  @Mock AccessibilityHierarchyCheck checkMock;
  @Mock AccessibilityHierarchyAndroid hierarchyMock;
  @Mock AccessibilityHierarchyAndroid.BuilderAndroid builderMock;

  MockedStatic<AccessibilityCheckPreset> accessibilityCheckPresetStaticMock;
  MockedStatic<AccessibilityHierarchyAndroid> accessibilityHierarchyAndroidStaticMock;
  MockedStatic<AccessibilityCheckResultUtils> accessibilityCheckResultUtilsStaticMock;

  ATFAScanner testSubject;
  Parameters parametersStub;
  BitmapImage screenshotStub;
  List<AccessibilityHierarchyCheckResult> filteredResultsStub;

  @Before
  public void prepare() {
    accessibilityCheckPresetStaticMock = Mockito.mockStatic(AccessibilityCheckPreset.class);
    accessibilityHierarchyAndroidStaticMock = Mockito.mockStatic(AccessibilityHierarchyAndroid.class);
    accessibilityCheckResultUtilsStaticMock = Mockito.mockStatic(AccessibilityCheckResultUtils.class);
    screenshotStub = new BitmapImage(bitmapMock);
    parametersStub = new Parameters();
    filteredResultsStub = Collections.emptyList();
    testSubject = new ATFAScanner(contextMock);
  }

  @After
  public void cleanUp() {
    accessibilityCheckResultUtilsStaticMock.close();
    accessibilityHierarchyAndroidStaticMock.close();
    accessibilityCheckPresetStaticMock.close();
  }

  @Test
  public void scanWithATFAReturnsCorrectResult() throws ViewChangedException {
    accessibilityCheckPresetStaticMock.when(() -> AccessibilityCheckPreset.getAccessibilityHierarchyChecksForPreset(
            AccessibilityCheckPreset.LATEST))
        .thenReturn(ImmutableSet.of(checkMock));
    accessibilityHierarchyAndroidStaticMock.when(() -> AccessibilityHierarchyAndroid.newBuilder(accessibilityNodeInfoMock, contextMock))
        .thenReturn(builderMock);
    when(builderMock.build()).thenReturn(hierarchyMock);
    accessibilityCheckResultUtilsStaticMock.when(() -> AccessibilityCheckResultUtils.getResultsForTypes(eq(Collections.emptyList()), anySet()))
        .thenReturn(filteredResultsStub);

    Assert.assertEquals(
        testSubject.scanWithATFA(accessibilityNodeInfoMock, screenshotStub), filteredResultsStub);
  }
}
