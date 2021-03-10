// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class AccessibilityEventDispatcher {
  private static final String TAG = "AccessibilityEventDispa";
  private CharSequence previousPackageName;

  private ArrayList<Consumer<AccessibilityNodeInfo>> onAppChangedListeners;
  private ArrayList<Consumer<AccessibilityEvent>> onFocusEventListeners;
  private ArrayList<Consumer<AccessibilityEvent>> onRedrawEventListeners;

  public AccessibilityEventDispatcher() {
    onAppChangedListeners = new ArrayList<Consumer<AccessibilityNodeInfo>>();
    onFocusEventListeners = new ArrayList<Consumer<AccessibilityEvent>>();
    onRedrawEventListeners = new ArrayList<Consumer<AccessibilityEvent>>();
  }

  public static List<Integer> redrawEventTypes =
      Arrays.asList(
          AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
          AccessibilityEvent.TYPE_VIEW_SCROLLED,
          AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
          AccessibilityEvent.TYPE_WINDOWS_CHANGED);

  public void onAccessibilityEvent(AccessibilityEvent event, AccessibilityNodeInfo rootNode) {
    int eventType = event.getEventType();

    if (previousPackageName == null || !previousPackageName.equals(rootNode.getPackageName())) {
      previousPackageName = rootNode.getPackageName();
      this.callListeners(onAppChangedListeners, rootNode);
    }

    if (isFocusEvent(eventType)) {
      this.callListeners(onFocusEventListeners, event);
      return;
    }

    if (isRedrawEvent(eventType)) {
      this.callListeners(onRedrawEventListeners, event);
      return;
    }
  }

  public void addOnFocusEventListener(Consumer<AccessibilityEvent> listener) {
    onFocusEventListeners.add(listener);
  }

  public void addOnRedrawEventListener(Consumer<AccessibilityEvent> listener) {
    onRedrawEventListeners.add(listener);
  }

  public void addOnAppChangedListener(Consumer<AccessibilityNodeInfo> listener) {
    onAppChangedListeners.add(listener);
  }

  private boolean isFocusEvent(int eventType) {
    return eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED;
  }

  private boolean isRedrawEvent(int eventType) {
    return redrawEventTypes.contains(eventType);
  }

  private <T> void callListeners(ArrayList<Consumer<T>> listeners, T newValue) {
    listeners.forEach(
        listener -> {
          listener.accept(newValue);
        });
  }
}
