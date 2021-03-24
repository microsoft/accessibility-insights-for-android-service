// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doAnswer;

import android.view.accessibility.AccessibilityEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import org.junit.Assert;
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
  @Mock Consumer<Boolean> listenerStub;

  FocusVisualizerController testSubject;

  @Test
  public void exists() {
    testSubject =
        new FocusVisualizerController(
            focusVisualizerMock, focusVisualizationStateManagerMock, uiThreadRunner);
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void onFocusEventDoesNotCallVisualizerIfStateIsFalse() {
    testSubject =
        new FocusVisualizerController(
            focusVisualizerMock, focusVisualizationStateManagerMock, uiThreadRunner);
    when(focusVisualizationStateManagerMock.getState()).thenReturn(false);
    testSubject.onFocusEvent(accessibilityEventMock);
    verify(focusVisualizerMock, times(0)).addNewFocusedElement(any(AccessibilityEvent.class));
  }

  @Test
  public void onFocusEventCallsVisualizerIfStateIsTrue() {
    testSubject =
        new FocusVisualizerController(
            focusVisualizerMock, focusVisualizationStateManagerMock, uiThreadRunner);
    when(focusVisualizationStateManagerMock.getState()).thenReturn(true);
    testSubject.onFocusEvent(accessibilityEventMock);
    verify(focusVisualizerMock, times(1)).addNewFocusedElement(any(AccessibilityEvent.class));
  }

  @Test
  public void onRedrawEventDoesNotCallVisualizerIfStateIsFalse() {
    testSubject =
        new FocusVisualizerController(
            focusVisualizerMock, focusVisualizationStateManagerMock, uiThreadRunner);
    when(focusVisualizationStateManagerMock.getState()).thenReturn(false);
    testSubject.onRedrawEvent(accessibilityEventMock);
    verify(focusVisualizerMock, times(0)).refreshHighlights();
  }

  @Test
  public void onRedrawEventCallsVisualizerIfStateIsTrue() {
    testSubject =
        new FocusVisualizerController(
            focusVisualizerMock, focusVisualizationStateManagerMock, uiThreadRunner);
    when(focusVisualizationStateManagerMock.getState()).thenReturn(true);
    testSubject.onRedrawEvent(accessibilityEventMock);
    verify(focusVisualizerMock, times(1)).refreshHighlights();
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

    testSubject =
        new FocusVisualizerController(
            focusVisualizerMock, focusVisualizationStateManagerMock, uiThreadRunner);

    verifyNoInteractions(uiThreadRunner);
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
            focusVisualizerMock, focusVisualizationStateManagerMock, uiThreadRunner);

    verify(focusVisualizerMock).resetVisualizations();
  }
}
