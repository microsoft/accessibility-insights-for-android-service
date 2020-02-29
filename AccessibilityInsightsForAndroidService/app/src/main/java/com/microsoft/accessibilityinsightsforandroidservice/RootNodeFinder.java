// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.view.accessibility.AccessibilityNodeInfo;

public class RootNodeFinder {
  public AccessibilityNodeInfo getRootNodeFromSource(AccessibilityNodeInfo source) {
    AccessibilityNodeInfo rootNode = null;

    if (source != null) {
      AccessibilityNodeInfo currentNode = source;

      while (true) {
        AccessibilityNodeInfo parent = currentNode.getParent();

        if (parent == null) {
          rootNode = currentNode;
          break;
        }

        if (source != currentNode) { // Don't recycle the source!
          currentNode.recycle();
        }
        currentNode = parent;
      }
    }

    return rootNode;
  }
}
