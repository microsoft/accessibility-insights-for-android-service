// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doAnswer;

import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class FocusVisualizerControllerTest {

  private static final ScheduledExecutorService mainThread =
      Executors.newSingleThreadScheduledExecutor();
  @Mock FocusVisualizer focusVisualizerMock;
  @Mock FocusVisualizationStateManager focusVisualizationStateManagerMock;
  @Mock AccessibilityEvent accessibilityEventMock;
  @Mock UIThreadRunner uiThreadRunner;
  @Mock WindowManager windowManager;
  @Mock LayoutParamGenerator layoutParamGenerator;
  @Mock FocusVisualizationCanvas focusVisualizationCanvas;
  @Mock WindowManager.LayoutParams layoutParams;
  @Mock AccessibilityNodeInfo accessibilityNodeInfo;

  FocusVisualizerController testSubject;

  @Before
  public void prepare() {
    when(layoutParamGenerator.get()).thenReturn(layoutParams);
    testSubject =
        new FocusVisualizerController(
            focusVisualizerMock,
            focusVisualizationStateManagerMock,
            uiThreadRunner,
            windowManager,
            layoutParamGenerator,
            focusVisualizationCanvas);
  }

  @Test
  public void exists() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void onFocusEventDoesNotCallVisualizerIfStateIsFalse() {
    when(focusVisualizationStateManagerMock.getState()).thenReturn(false);
    testSubject.onFocusEvent(accessibilityEventMock);
    verify(focusVisualizerMock, times(0)).addNewFocusedElement(any(AccessibilityEvent.class));
  }

  @Test
  public void onFocusEventCallsVisualizerIfStateIsTrue() {
    when(focusVisualizationStateManagerMock.getState()).thenReturn(true);
    testSubject.onFocusEvent(accessibilityEventMock);
    verify(focusVisualizerMock, times(1)).addNewFocusedElement(any(AccessibilityEvent.class));
  }

  @Test
  public void onRedrawEventDoesNotCallVisualizerIfStateIsFalse() {
    when(focusVisualizationStateManagerMock.getState()).thenReturn(false);
    testSubject.onRedrawEvent(accessibilityEventMock);
    verify(focusVisualizerMock, times(0)).refreshHighlights();
  }

  @Test
  public void onRedrawEventCallsVisualizerIfStateIsTrue() {
    when(focusVisualizationStateManagerMock.getState()).thenReturn(true);
    testSubject.onRedrawEvent(accessibilityEventMock);
    verify(focusVisualizerMock, times(1)).refreshHighlights();
  }

  @Test
  public void onAppChangeDoesNotCallVisualizerIfStateIsFalse() {
    when(focusVisualizationStateManagerMock.getState()).thenReturn(false);
    testSubject.onAppChanged(accessibilityNodeInfo);
    verify(focusVisualizerMock, times(0)).resetVisualizations();
  }

  @Test
  public void onAppChangeDoesCallVisualizerIfStateIsTrue() {
    when(focusVisualizationStateManagerMock.getState()).thenReturn(true);
    testSubject.onAppChanged(accessibilityNodeInfo);
    verify(focusVisualizerMock, times(1)).resetVisualizations();
  }

  @Test
  public void onOrientationChangeDoesNothingIfStateIsFalse() {
    when(focusVisualizationStateManagerMock.getState()).thenReturn(false);
    testSubject.onOrientationChanged(0);
    verify(focusVisualizerMock, times(0)).resetVisualizations();
    verify(windowManager, times(0)).updateViewLayout(focusVisualizationCanvas, layoutParams);
  }

  @Test
  public void onOrientationChangeUpdatesVisualizationAsNecessaryIfStateIsTrue() {
    when(focusVisualizationStateManagerMock.getState()).thenReturn(true);
    testSubject.onOrientationChanged(0);
    verify(focusVisualizerMock, times(1)).resetVisualizations();
    verify(windowManager, times(1)).updateViewLayout(focusVisualizationCanvas, layoutParams);
  }

  @Test
  public void onFocusVisualizationStateChangeWithoutStateChangeDoesNothing() {
    doAnswer(
            invocation -> {
              Consumer<Boolean> listener = invocation.getArgument(0);
              listener.accept(true);
              return null;
            })
        .when(focusVisualizationStateManagerMock)
        .subscribe(any());

    doAnswer(
            invocation -> {
              Runnable runnable = invocation.getArgument(0);
              runnable.run();
              return null;
            })
        .when(uiThreadRunner)
        .run(any());

    testSubject =
        new FocusVisualizerController(
            focusVisualizerMock,
            focusVisualizationStateManagerMock,
            uiThreadRunner,
            windowManager,
            layoutParamGenerator,
            focusVisualizationCanvas);

    verify(windowManager).addView(focusVisualizationCanvas, layoutParams);
  }

  @Test
  public void onFocusVisualizationStateChangeResetsVisualizationsOnUIThread() {
    doAnswer(
            invocation -> {
              Consumer<Boolean> listener = invocation.getArgument(0);
              listener.accept(false);
              return null;
            })
        .when(focusVisualizationStateManagerMock)
        .subscribe(any());

    doAnswer(
            invocation -> {
              Runnable runnable = invocation.getArgument(0);
              runnable.run();
              return null;
            })
        .when(uiThreadRunner)
        .run(any());

    testSubject =
        new FocusVisualizerController(
            focusVisualizerMock,
            focusVisualizationStateManagerMock,
            uiThreadRunner,
            windowManager,
            layoutParamGenerator,
            focusVisualizationCanvas);

    verify(focusVisualizerMock).resetVisualizations();
  }
}
