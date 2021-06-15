// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.graphics.Bitmap;
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
public class ResultRequestFulfillerTest {

  @Mock ResponseWriter responseWriter;
  @Mock RootNodeFinder rootNodeFinder;
  @Mock EventHelper eventHelper;
  @Mock AxeScanner axeScanner;
  @Mock ATFAScanner atfaScanner;
  @Mock ScreenshotController screenshotController;
  @Mock Bitmap screenshotMock;
  @Mock AccessibilityNodeInfo sourceNode;
  @Mock AccessibilityNodeInfo rootNode;
  @Mock AxeResult axeResultMock;
  @Mock RunnableFunction onRequestFulfilledMock;
  @Mock ResultsContainerSerializer resultsContainerSerializer;

  final List<AccessibilityHierarchyCheckResult> atfaResults = Collections.emptyList();
  final String scanResultJson = "axe scan result";

  ResultRequestFulfiller testSubject;

  @Before
  public void prepare() {
    doAnswer(
            AdditionalAnswers.answerVoid(
                (Consumer<Bitmap> bitmapConsumer) -> {
                  bitmapConsumer.accept(screenshotMock);
                }))
        .when(screenshotController)
        .getScreenshotWithMediaProjection(any());
    testSubject =
        new ResultRequestFulfiller(
            responseWriter,
            rootNodeFinder,
            eventHelper,
            axeScanner,
            atfaScanner,
            screenshotController,
            resultsContainerSerializer);
  }

  @Test
  public void resultRequestFulfillerExists() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void isBlockingRequestReturnsTrue() {
    Assert.assertTrue(testSubject.isBlockingRequest());
  }

  @Test
  public void callsOnRequestFulfilled() {
    setupSuccessfulRequest();

    testSubject.fulfillRequest(onRequestFulfilledMock);

    verifyOnRequestFulfilledCalled();
  }

  @Test
  public void callsGetScreenshotWithMediaProjection() {
    setupSuccessfulRequest();

    testSubject.fulfillRequest(onRequestFulfilledMock);

    verify(screenshotController, times(1)).getScreenshotWithMediaProjection(any());
  }

  @Test
  public void requestHandledInsideGetScreenshotWithMediaProjection() {
    reset(screenshotController);

    testSubject.fulfillRequest(onRequestFulfilledMock);

    verifyZeroInteractions(responseWriter);
    verifyZeroInteractions(onRequestFulfilledMock);
  }

  @Test
  public void writesSuccessfulResponse() {
    setupSuccessfulRequest();

    testSubject.fulfillRequest(onRequestFulfilledMock);

    verify(responseWriter, times(1)).writeSuccessfulResponse(scanResultJson);
  }

  @Test
  public void recyclesNodes() {
    setupSuccessfulRequest();

    testSubject.fulfillRequest(onRequestFulfilledMock);

    verify(rootNode, times(1)).recycle();
    verify(sourceNode, times(1)).recycle();
  }

  @Test
  public void recyclesNodeOnceIfRootEqualsSource() throws ViewChangedException {
    setupSuccessfulRequest();
    reset(rootNodeFinder);
    reset(axeScanner);
    when(rootNodeFinder.getRootNodeFromSource(any())).thenReturn(sourceNode);
    when(axeScanner.scanWithAxe(eq(sourceNode), any())).thenReturn(axeResultMock);

    testSubject.fulfillRequest(onRequestFulfilledMock);

    verifyZeroInteractions(rootNode);
    verify(sourceNode, times(1)).recycle();
  }

  @Test
  public void writesErrorIfNoRootNode() {
    when(rootNodeFinder.getRootNodeFromSource(null)).thenReturn(null);

    testSubject.fulfillRequest(onRequestFulfilledMock);

    verify(responseWriter, times(1))
        .writeErrorResponse(argThat((e) -> e.getMessage() == "Unable to locate root node to scan"));
    verifyOnRequestFulfilledCalled();
  }

  @Test
  public void writesErrorIfScanFailed() throws ViewChangedException {
    when(eventHelper.claimLastSource()).thenReturn(sourceNode);
    when(rootNodeFinder.getRootNodeFromSource(any())).thenReturn(rootNode);
    when(axeScanner.scanWithAxe(eq(rootNode), any())).thenReturn(null);

    testSubject.fulfillRequest(onRequestFulfilledMock);

    verify(responseWriter, times(1))
        .writeErrorResponse(argThat((e) -> e.getMessage() == "Scanner returned no data"));
    verifyOnRequestFulfilledCalled();
  }

  @Test
  public void doesNotRecycleSourceIfRestoreLastSourceSucceeds() {
    setupSuccessfulRequest();
    when(eventHelper.restoreLastSource(sourceNode)).thenReturn(true);

    testSubject.fulfillRequest(onRequestFulfilledMock);
    verify(rootNode, times(1)).recycle();
    verify(sourceNode, never()).recycle();
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
    when(resultsContainerSerializer.createResultsJson(axeResultMock, atfaResults))
        .thenReturn(scanResultJson);
  }

  private void verifyOnRequestFulfilledCalled() {
    verify(onRequestFulfilledMock, times(1)).run();
  }
}
