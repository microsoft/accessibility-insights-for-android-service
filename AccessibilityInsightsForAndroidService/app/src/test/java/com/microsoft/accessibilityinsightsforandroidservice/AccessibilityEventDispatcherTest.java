// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class AccessibilityEventDispatcherTest {

  @Mock AccessibilityEvent eventMock;
  @Mock AccessibilityNodeInfo rootNodeMock;
  @Mock Consumer<AccessibilityNodeInfo> onAppChangedListenerMock;
  @Mock Consumer<AccessibilityEvent> onFocusEventListenerMock;
  @Mock Consumer<AccessibilityEvent> onRedrawEventListenerMock;

  AccessibilityEventDispatcher testSubject;

  @Before
  public void prepare() {
    CharSequence packageNameStub = "some package name";
    when(rootNodeMock.getPackageName()).thenReturn(packageNameStub);

    testSubject = new AccessibilityEventDispatcher();
  }

  @Test
  public void accessibilityEventDispatcherExists() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void onAppChangedFiresWithoutPreviousPackageName() {
    int trivialEventType = -1;
    when(eventMock.getEventType()).thenReturn(trivialEventType);

    testSubject.addOnAppChangedListener(onAppChangedListenerMock);
    testSubject.onAccessibilityEvent(eventMock, rootNodeMock);

    verify(onAppChangedListenerMock, times(1)).accept(rootNodeMock);
  }

  @Test
  public void onAppChangedFiresWhenPackageNameChanged() {
    int trivialEventType = -1;
    CharSequence differentPackageNameStub = "different package name";
    when(eventMock.getEventType()).thenReturn(trivialEventType);

    testSubject.addOnAppChangedListener(onAppChangedListenerMock);
    testSubject.onAccessibilityEvent(eventMock, rootNodeMock);

    reset(rootNodeMock);

    when(rootNodeMock.getPackageName()).thenReturn(differentPackageNameStub);
    testSubject.onAccessibilityEvent(eventMock, rootNodeMock);

    verify(onAppChangedListenerMock, times(2)).accept(rootNodeMock);
  }

  @Test
  public void onFocusEventListenerFiresOnFocusEvent() {
    int focusEventType = AccessibilityEvent.TYPE_VIEW_FOCUSED;
    when(eventMock.getEventType()).thenReturn(focusEventType);

    testSubject.addOnFocusEventListener(onFocusEventListenerMock);
    testSubject.onAccessibilityEvent(eventMock, rootNodeMock);

    verify(onFocusEventListenerMock, times(1)).accept(eventMock);
  }

  @Test
  public void onFocusEventListenerDoesNotFiresOnOtherEvent() {
    int trivialEventType = -1;
    when(eventMock.getEventType()).thenReturn(trivialEventType);

    testSubject.addOnFocusEventListener(onFocusEventListenerMock);
    testSubject.onAccessibilityEvent(eventMock, rootNodeMock);

    verify(onFocusEventListenerMock, times(0)).accept(eventMock);
  }

  @Test
  public void onRedrawEventListenerFiresOnRedrawEvents() {
    testSubject.addOnRedrawEventListener(onRedrawEventListenerMock);

    AccessibilityEventDispatcher.redrawEventTypes.forEach(
        eventType -> {
          when(eventMock.getEventType()).thenReturn(eventType);
          testSubject.onAccessibilityEvent(eventMock, rootNodeMock);
          reset(eventMock);
        });

    verify(onRedrawEventListenerMock, times(4)).accept(eventMock);
  }

  @Test
  public void onRedrawEventListenerDoesNotFiresOnOtherEvent() {
    int trivialEventType = -1;
    when(eventMock.getEventType()).thenReturn(trivialEventType);

    testSubject.addOnRedrawEventListener(onRedrawEventListenerMock);
    testSubject.onAccessibilityEvent(eventMock, rootNodeMock);

    verify(onRedrawEventListenerMock, times(0)).accept(eventMock);
  }
}
