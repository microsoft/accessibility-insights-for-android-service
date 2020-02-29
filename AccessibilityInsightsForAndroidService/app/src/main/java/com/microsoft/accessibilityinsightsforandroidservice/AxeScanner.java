// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.graphics.Bitmap;
import android.view.accessibility.AccessibilityNodeInfo;
import com.deque.axe.android.Axe;
import com.deque.axe.android.AxeContext;
import com.deque.axe.android.AxeResult;

public class AxeScanner {
  private final AxeRunnerFactory axeRunnerFactory;
  private final AxeContextFactory axeContextFactory;

  public AxeScanner(AxeRunnerFactory axeRunnerFactory, AxeContextFactory axeContextFactory) {
    this.axeRunnerFactory = axeRunnerFactory;
    this.axeContextFactory = axeContextFactory;
  }

  public AxeResult scanWithAxe(AccessibilityNodeInfo rootNode, Bitmap screenshot)
      throws ViewChangedException {
    final Axe axe = axeRunnerFactory.createAxeRunner();
    final AxeContext axeContext = axeContextFactory.createAxeContext(rootNode, screenshot);
    return axe.run(axeContext);
  }
}
