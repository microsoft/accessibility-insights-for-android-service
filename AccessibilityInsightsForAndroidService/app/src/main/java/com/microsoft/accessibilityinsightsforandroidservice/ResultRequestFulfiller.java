// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.graphics.Bitmap;
import android.view.accessibility.AccessibilityNodeInfo;
import com.deque.axe.android.AxeResult;

public class ResultRequestFulfiller implements RequestFulfiller {
  private final RootNodeFinder rootNodeFinder;
  private final EventHelper eventHelper;
  private final ResponseWriter responseWriter;
  private final AxeScanner axeScanner;
  private final ScreenshotController screenshotController;

  public ResultRequestFulfiller(
      ResponseWriter responseWriter,
      RootNodeFinder rootNodeFinder,
      EventHelper eventHelper,
      AxeScanner axeScanner,
      ScreenshotController screenshotController) {
    this.responseWriter = responseWriter;
    this.rootNodeFinder = rootNodeFinder;
    this.eventHelper = eventHelper;
    this.axeScanner = axeScanner;
    this.screenshotController = screenshotController;
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

  private String getScanContent(AccessibilityNodeInfo rootNode, Bitmap screenshot)
      throws ScanException, ViewChangedException {
    if (rootNode == null) {
      throw new ScanException("Unable to locate root node to scan");
    }
    AxeResult result = axeScanner.scanWithAxe(rootNode, screenshot);
    if (result == null) {
      throw new ScanException("Scanner returned no data");
    }
    return result.toJson();
  }
}
