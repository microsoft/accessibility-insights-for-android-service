// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import android.content.Context;
import android.graphics.Canvas;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FocusVisualizationCanvasTest {
  FocusVisualizationCanvas testSubject;

  @Mock Context contextMock;
  @Mock FocusElementHighlight focusElementHighlightMock;
  @Mock FocusElementLine focusElementLineMock;
  @Mock Canvas canvasMock;

  @Before
  public void prepare() {
    testSubject = new FocusVisualizationCanvas(contextMock);
  }

  @Test
  public void drawHighlightsAndLinesOnlyDrawsHighlightOnFirstPass() throws Exception {
    ArrayList<FocusElementLine> lineStub = new ArrayList<>();
    lineStub.add(focusElementLineMock);

    ArrayList<FocusElementHighlight> highlightStub = new ArrayList<>();
    highlightStub.add(focusElementHighlightMock);

    testSubject.setDrawItems(highlightStub, lineStub);
    testSubject.drawHighlightsAndLines(canvasMock);

    verify(focusElementHighlightMock, times(1)).drawElementHighlight(any(Canvas.class));
    verify(focusElementLineMock, times(0)).drawLine(any(Canvas.class));
  }

  @Test
  public void drawHighlightsAndLinesDrawsAllRelevantObjectsOnSubsequentPasses() throws Exception {
    ArrayList<FocusElementLine> lineStub = new ArrayList<>();
    lineStub.add(focusElementLineMock);
    lineStub.add(focusElementLineMock);

    ArrayList<FocusElementHighlight> highlightStub = new ArrayList<>();
    highlightStub.add(focusElementHighlightMock);
    highlightStub.add(focusElementHighlightMock);

    testSubject.setDrawItems(highlightStub, lineStub);
    testSubject.drawHighlightsAndLines(canvasMock);

    // Note: drawElementHighlight will call twice for each subsequent onDraw event.  This is to
    // ensure that the line is drawn underneath the highlight, as the canvas drawings draw on
    // top of any previous drawings by default.
    verify(focusElementHighlightMock, times(3)).drawElementHighlight(any(Canvas.class));
    verify(focusElementLineMock, times(1)).drawLine(any(Canvas.class));
  }
}
