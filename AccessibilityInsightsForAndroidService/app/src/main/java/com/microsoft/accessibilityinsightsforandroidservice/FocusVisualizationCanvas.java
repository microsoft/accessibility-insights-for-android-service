// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;

public class FocusVisualizationCanvas extends View {
  private ArrayList<FocusElementHighlight> focusElementHighlights;
  private ArrayList<FocusElementLine> focusElementLines;

  public FocusVisualizationCanvas(Context context) {
    super(context);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    this.drawHighlightsAndLines(canvas);
  }

  @VisibleForTesting
  public void drawHighlightsAndLines(Canvas canvas) {
    if (this.focusElementHighlights == null) {
      return;
    }

    for (int elementIndex = 0; elementIndex < this.focusElementHighlights.size(); elementIndex++) {
      if (elementIndex != 0) {
        this.drawTrailingHighlights(elementIndex, canvas);
      }

      this.focusElementHighlights.get(elementIndex).drawElementHighlight(canvas);
    }
  }

  private void drawTrailingHighlights(int elementIndex, Canvas canvas) {
    this.focusElementLines.get(elementIndex).drawLine(canvas);
    this.focusElementHighlights.get(elementIndex - 1).drawElementHighlight(canvas);
  }

  public void setDrawItems(
      ArrayList<FocusElementHighlight> highlights, ArrayList<FocusElementLine> lines) {
    this.focusElementHighlights = highlights;
    this.focusElementLines = lines;
  }

  public void redraw() {
    this.invalidate();
  }
}
