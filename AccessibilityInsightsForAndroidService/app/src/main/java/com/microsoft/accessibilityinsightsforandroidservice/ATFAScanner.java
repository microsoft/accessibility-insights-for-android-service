// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.content.Context;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ATFAScanner {
  private final Context context;
  private final AccessibilityCheckResult.AccessibilityCheckResultType[] relevantResultTypes = {
    AccessibilityCheckResult.AccessibilityCheckResultType.ERROR,
    AccessibilityCheckResult.AccessibilityCheckResultType.INFO,
    AccessibilityCheckResult.AccessibilityCheckResultType.WARNING,
    AccessibilityCheckResult.AccessibilityCheckResultType.RESOLVED,
    AccessibilityCheckResult.AccessibilityCheckResultType.NOT_RUN
  };

  public ATFAScanner(Context context) {
    this.context = context;
  }

  public List<AccessibilityHierarchyCheckResult> scanWithATFA(
      AccessibilityNodeInfo rootNode, BitmapImage screenshot) {
    Parameters parameters = new Parameters();
    parameters.setSaveViewImages(true);
    parameters.putCustomTouchTargetSize(44); // default is 48 but min size as defined by WCAG is 44
    parameters.putScreenCapture(screenshot);

    ImmutableSet<AccessibilityHierarchyCheck> checks =
        AccessibilityCheckPreset.getAccessibilityHierarchyChecksForPreset(
            AccessibilityCheckPreset.LATEST);
    AccessibilityHierarchyAndroid hierarchy =
        AccessibilityHierarchyAndroid.newBuilder(rootNode, this.context).build();
    List<AccessibilityHierarchyCheckResult> results = new ArrayList<>();

    for (AccessibilityHierarchyCheck check : checks) {
      results.addAll(check.runCheckOnHierarchy(hierarchy, null, parameters));
    }

    return AccessibilityCheckResultUtils.getResultsForTypes(
        results, new HashSet<>(Arrays.asList(relevantResultTypes)));
  }
}
