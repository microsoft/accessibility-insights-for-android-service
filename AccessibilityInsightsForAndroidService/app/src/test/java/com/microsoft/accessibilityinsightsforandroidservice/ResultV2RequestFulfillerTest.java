// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import android.graphics.Bitmap;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;
import android.view.accessibility.AccessibilityNodeInfo;
import com.deque.axe.android.AxeResult;
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityHierarchyCheckResult;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ResultV2RequestFulfillerTest {

  @Mock RootNodeFinder rootNodeFinder;
  @Mock EventHelper eventHelper;
  @Mock AxeScanner axeScanner;
  @Mock ATFAScanner atfaScanner;
  @Mock ScreenshotController screenshotController;
  @Mock Bitmap screenshotMock;
  @Mock AccessibilityNodeInfo sourceNode;
  @Mock AccessibilityNodeInfo rootNode;
  @Mock AxeResult axeResultMock;
  @Mock ResultsV2ContainerSerializer resultsV2ContainerSerializer;
  @Mock CancellationSignal cancellationSignal;

  final List<AccessibilityHierarchyCheckResult> atfaResults = Collections.emptyList();
  final String scanResultJson = "axe scan result";

  ResultV2RequestFulfiller testSubject;

  @Before
  public void prepare() {
    setupScreenshotParameter(screenshotMock);
    testSubject =
        new ResultV2RequestFulfiller(
            rootNodeFinder,
            eventHelper,
            axeScanner,
            atfaScanner,
            screenshotController,
            resultsV2ContainerSerializer);
  }

  @Test
  public void resultRequestFulfillerExists() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void callsGetScreenshotWithMediaProjection() throws Exception {
    setupSuccessfulRequest();

    testSubject.fulfillRequest(cancellationSignal);

    verify(screenshotController, times(1)).getScreenshotWithMediaProjection(any());
  }

  @Test
  public void writesSuccessfulResponse() throws Exception {
    setupSuccessfulRequest();

    assertEquals(scanResultJson, testSubject.fulfillRequest(cancellationSignal));
  }

  @Test
  public void recyclesNodes() throws Exception {
    setupSuccessfulRequest();

    testSubject.fulfillRequest(cancellationSignal);

    verify(rootNode, times(1)).recycle();
    verify(sourceNode, times(1)).recycle();
  }

  @Test
  public void recyclesNodeOnceIfRootEqualsSource() throws Exception {
    setupSuccessfulRequest();
    reset(rootNodeFinder);
    reset(axeScanner);
    when(rootNodeFinder.getRootNodeFromSource(any())).thenReturn(sourceNode);
    when(axeScanner.scanWithAxe(eq(sourceNode), any())).thenReturn(axeResultMock);

    testSubject.fulfillRequest(cancellationSignal);

    verifyNoInteractions(rootNode);
    verify(sourceNode, times(1)).recycle();
  }

  @Test
  public void throwsExceptionIfNoScreenshot() throws Exception {
    setupSuccessfulRequest();
    reset(screenshotController);
    setupScreenshotParameter(null);

    assertThrows(
        "Could not acquire screenshot. Has the user granted screen recording permissions?",
        Exception.class,
        () -> testSubject.fulfillRequest(cancellationSignal));
  }

  @Test
  public void throwsExceptionIfNoRootNode() {
    when(rootNodeFinder.getRootNodeFromSource(null)).thenReturn(null);

    assertThrows(
        "Unable to locate root node to scan",
        Exception.class,
        () -> testSubject.fulfillRequest(cancellationSignal));
  }

  @Test
  public void throwsExceptionIfScanFailed() throws ViewChangedException {
    when(eventHelper.claimLastSource()).thenReturn(sourceNode);
    when(rootNodeFinder.getRootNodeFromSource(any())).thenReturn(rootNode);
    when(axeScanner.scanWithAxe(eq(rootNode), any())).thenReturn(null);

    assertThrows(
        "Scanner returned no data",
        Exception.class,
        () -> testSubject.fulfillRequest(cancellationSignal));
  }

  @Test
  public void doesNotRecycleSourceIfRestoreLastSourceSucceeds() throws Exception {
    setupSuccessfulRequest();
    when(eventHelper.restoreLastSource(sourceNode)).thenReturn(true);

    testSubject.fulfillRequest(cancellationSignal);

    verify(rootNode, times(1)).recycle();
    verify(sourceNode, never()).recycle();
  }

  @Test
  public void supportsCancellationBetweenScreenshotAndFirstScan() throws Exception {
    setupSuccessfulRequest();
    reset(screenshotController);
    doAnswer(
            AdditionalAnswers.answerVoid(
                (Consumer<Bitmap> bitmapConsumer) -> {
                  simulateCancellation();
                  bitmapConsumer.accept(screenshotMock);
                }))
        .when(screenshotController)
        .getScreenshotWithMediaProjection(any());

    assertThrows(
        OperationCanceledException.class, () -> testSubject.fulfillRequest(cancellationSignal));

    verifyNoInteractions(axeScanner);
    verifyNoInteractions(atfaScanner);
  }

  @Test
  public void supportsCancellationBetweenScans() throws Exception {
    setupSuccessfulRequest();
    reset(axeScanner);
    when(axeScanner.scanWithAxe(eq(rootNode), any()))
        .thenAnswer(
            invocation -> {
              simulateCancellation();
              return axeResultMock;
            });
    assertThrows(
        OperationCanceledException.class, () -> testSubject.fulfillRequest(cancellationSignal));

    verifyNoInteractions(atfaScanner);
  }

  private void simulateCancellation() {
    doThrow(new OperationCanceledException()).when(cancellationSignal).throwIfCanceled();
  }

  private void setupSuccessfulRequest() {
    when(eventHelper.claimLastSource()).thenReturn(sourceNode);
    when(rootNodeFinder.getRootNodeFromSource(any())).thenReturn(rootNode);
    try {
      when(axeScanner.scanWithAxe(eq(rootNode), any())).thenReturn(axeResultMock);
    } catch (ViewChangedException e) {
      Assert.fail(e.getMessage());
    }
    when(atfaScanner.scanWithATFA(eq(sourceNode), any())).thenReturn(atfaResults);
    when(resultsV2ContainerSerializer.createResultsJson(axeResultMock, atfaResults))
        .thenReturn(scanResultJson);
  }

  private void setupScreenshotParameter(Bitmap value) {
    doAnswer(
            AdditionalAnswers.answerVoid(
                (Consumer<Bitmap> bitmapConsumer) -> {
                  bitmapConsumer.accept(value);
                }))
        .when(screenshotController)
        .getScreenshotWithMediaProjection(any());
  }
}
