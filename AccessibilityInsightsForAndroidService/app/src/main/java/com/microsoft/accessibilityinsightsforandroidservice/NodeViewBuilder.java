// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;
import com.deque.axe.android.AxeView;
import com.deque.axe.android.wrappers.AxeRect;
import java.util.List;

public class NodeViewBuilder implements AxeView.Builder {
  private final AccessibilityNodeInfo accessibilityNode;
  private final List<AxeView> children;
  private final AxeView labeledBy;
  private final AxeRect boundsRect;

  public AxeRect boundsInScreen() {
    return boundsRect;
  }

  public String className() {
    String rawClassName = safeToString(accessibilityNode.getClassName());
    return (rawClassName == null) ? "Class Name Not Specified--Inserted by Accessibility Insights" : rawClassName;
  }

  public String contentDescription() {
    return safeToString(accessibilityNode.getContentDescription());
  }

  public boolean isAccessibilityFocusable() {
    return accessibilityNode.isFocusable();
  }

  public boolean isClickable() {
    return accessibilityNode.isClickable();
  }

  public boolean isEnabled() {
    return accessibilityNode.isEnabled();
  }

  public boolean isImportantForAccessibility() {
    return accessibilityNode.isImportantForAccessibility();
  }

  public AxeView labeledBy() {
    return labeledBy;
  }

  public String packageName() {
    return safeToString(accessibilityNode.getPackageName());
  }

  public String paneTitle() {
    return null;
  }

  public String text() {
    return safeToString(accessibilityNode.getText());
  }

  public String viewIdResourceName() {
    return accessibilityNode.getViewIdResourceName();
  }

  public List<AxeView> children() {
    return children;
  }

  public String value() {
    return null;
  }

  public String hintText() {
    if (android.os.Build.VERSION.SDK_INT >= 26) {
      return safeToString(accessibilityNode.getHintText());
    }
    return null;
  }

  public NodeViewBuilder(
      AccessibilityNodeInfo node,
      List<AxeView> children,
      AxeView labeledBy,
      AxeRectProvider boundsRectProvider) {
    accessibilityNode = node;
    this.children = children;
    this.labeledBy = labeledBy;

    Rect rect = new Rect();
    accessibilityNode.getBoundsInScreen(rect);
    boundsRect = boundsRectProvider.createAxeRect(rect.left, rect.right, rect.top, rect.bottom);
  }

  private String safeToString(CharSequence chars) {
    if (chars == null) {
      return null;
    }

    return chars.toString();
  }
}
