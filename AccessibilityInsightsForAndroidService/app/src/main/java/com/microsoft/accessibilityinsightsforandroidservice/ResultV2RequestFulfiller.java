// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.graphics.Bitmap;
import android.os.CancellationSignal;
import android.view.accessibility.AccessibilityNodeInfo;
import com.deque.axe.android.AxeResult;
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityHierarchyCheckResult;
import com.google.android.apps.common.testing.accessibility.framework.utils.contrast.BitmapImage;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class ResultV2RequestFulfiller implements RequestFulfiller {
  private final RootNodeFinder rootNodeFinder;
  private final EventHelper eventHelper;
  private final AxeScanner axeScanner;
  private final ATFAScanner atfaScanner;
  private final ScreenshotController screenshotController;
  private final ResultsV2ContainerSerializer resultsV2ContainerSerializer;

  public ResultV2RequestFulfiller(
      RootNodeFinder rootNodeFinder,
      EventHelper eventHelper,
      AxeScanner axeScanner,
      ATFAScanner atfaScanner,
      ScreenshotController screenshotController,
      ResultsV2ContainerSerializer resultsV2ContainerSerializer) {
    this.rootNodeFinder = rootNodeFinder;
    this.eventHelper = eventHelper;
    this.axeScanner = axeScanner;
    this.atfaScanner = atfaScanner;
    this.screenshotController = screenshotController;
    this.resultsV2ContainerSerializer = resultsV2ContainerSerializer;
  }

  public String fulfillRequest(CancellationSignal cancellationSignal) throws Exception {
    AtomicReference<String> successResponse = new AtomicReference<>();
    AtomicReference<Exception> errorResponse = new AtomicReference<>();
    CountDownLatch doneSignal = new CountDownLatch(1);

    screenshotController.getScreenshotWithMediaProjection(
        screenshot -> {
          try {
            cancellationSignal.throwIfCanceled();

            if (screenshot == null) {
              throw new Exception(
                  "Could not acquire screenshot. Has the user granted screen recording permissions?");
            }

            AccessibilityNodeInfo source = eventHelper.claimLastSource();
            AccessibilityNodeInfo rootNode = rootNodeFinder.getRootNodeFromSource(source);

            successResponse.set(getScanContent(rootNode, screenshot, cancellationSignal));

            if (rootNode != null && rootNode != source) {
              rootNode.recycle();
            }
            if (source != null && !eventHelper.restoreLastSource(source)) {
              source.recycle();
            }
          } catch (Exception e) {
            errorResponse.set(e);
          }
          doneSignal.countDown();
        });

    doneSignal.await();
    if (errorResponse.get() != null) {
      throw errorResponse.get();
    }
    return successResponse.get();
  }

  private String getScanContent(
      AccessibilityNodeInfo rootNode, Bitmap screenshot, CancellationSignal cancellationSignal)
      throws ScanException, ViewChangedException {
    cancellationSignal.throwIfCanceled();
    if (rootNode == null) {
      throw new ScanException("Unable to locate root node to scan");
    }
    AxeResult axeResult = axeScanner.scanWithAxe(rootNode, screenshot);
    if (axeResult == null) {
      throw new ScanException("Scanner returned no data");
    }

    cancellationSignal.throwIfCanceled();
    List<AccessibilityHierarchyCheckResult> atfaResults =
        atfaScanner.scanWithATFA(rootNode, new BitmapImage(screenshot));

    return resultsV2ContainerSerializer.createResultsJson(axeResult, atfaResults);
  }
}
