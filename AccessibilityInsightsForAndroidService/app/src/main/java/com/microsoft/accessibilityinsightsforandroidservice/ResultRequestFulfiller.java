// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.graphics.Bitmap;
import android.view.accessibility.AccessibilityNodeInfo;
import com.deque.axe.android.AxeResult;
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityHierarchyCheckResult;
import com.google.android.apps.common.testing.accessibility.framework.utils.contrast.BitmapImage;
import java.util.List;

public class ResultRequestFulfiller implements RequestFulfiller {
  private final RootNodeFinder rootNodeFinder;
  private final EventHelper eventHelper;
  private final ResponseWriter responseWriter;
  private final AxeScanner axeScanner;
  private final ATFAScanner atfaScanner;
  private final ScreenshotController screenshotController;
  private final ResultSerializer resultSerializer;

  public ResultRequestFulfiller(
      ResponseWriter responseWriter,
      RootNodeFinder rootNodeFinder,
      EventHelper eventHelper,
      AxeScanner axeScanner,
      ATFAScanner atfaScanner,
      ScreenshotController screenshotController,
      ResultSerializer resultSerializer) {
    this.responseWriter = responseWriter;
    this.rootNodeFinder = rootNodeFinder;
    this.eventHelper = eventHelper;
    this.axeScanner = axeScanner;
    this.atfaScanner = atfaScanner;
    this.screenshotController = screenshotController;
    this.resultSerializer = resultSerializer;
  }

  public void fulfillRequest(RunnableFunction onRequestFulfilled) {
    screenshotController.getScreenshotWithMediaProjection(
        screenshot -> {
          try {
            AccessibilityNodeInfo source = eventHelper.claimLastSource();
            AccessibilityNodeInfo rootNode = rootNodeFinder.getRootNodeFromSource(source);

            String content = getScanContent(rootNode, screenshot);
            responseWriter.writeSuccessfulResponse(content);

            if (rootNode != null && rootNode != source) {
              rootNode.recycle();
            }
            if (source != null && !eventHelper.restoreLastSource(source)) {
              source.recycle();
            }
          } catch (Exception e) {
            responseWriter.writeErrorResponse(e);
          }
          onRequestFulfilled.run();
        });
  }

  @Override
  public boolean isBlockingRequest() {
    return true;
  }

  private String getScanContent(AccessibilityNodeInfo rootNode, Bitmap screenshot)
      throws ScanException, ViewChangedException {
    if (rootNode == null) {
      throw new ScanException("Unable to locate root node to scan");
    }
    AxeResult result = axeScanner.scanWithAxe(rootNode, screenshot);
    if (result == null) {
      throw new ScanException("Scanner returned no data");
    }

    List<AccessibilityHierarchyCheckResult> results =
        atfaScanner.scanWithATFA(rootNode, new BitmapImage(screenshot));

    return resultSerializer.createResultsJson(result, results);
  }
}
