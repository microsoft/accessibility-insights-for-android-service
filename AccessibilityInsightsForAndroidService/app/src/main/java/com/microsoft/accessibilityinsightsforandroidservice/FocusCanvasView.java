// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import java.util.ArrayList;

public class FocusCanvasView extends View {
  private ArrayList<FocusElementHighlight> focusElementHighlights;
  private ArrayList<FocusElementLine> focusElementLines;

  public FocusCanvasView(Context context) {
    super(context);
    this.focusElementHighlights = new ArrayList<>();
    this.focusElementLines = new ArrayList<>();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    this.drawHighlightsAndLines(canvas);
  }

  private void drawHighlightsAndLines(Canvas canvas) {
    for (int i = 0; i < this.focusElementHighlights.size(); i++) {
      if (focusElementHighlights.size() > 1 && i > 0) {
        this.focusElementLines.get(i).drawLine(canvas);
        this.focusElementHighlights.get(i - 1).drawElementHighlight(canvas);
      }
      this.focusElementHighlights.get(i).drawElementHighlight(canvas);
    }
  }

  public void setFocusElementHighlights(ArrayList<FocusElementHighlight> highlights) {
    this.focusElementHighlights = highlights;
  }

  public void setFocusElementLines(ArrayList<FocusElementLine> lines) {
    this.focusElementLines = lines;
  }

  public void redraw() {
    this.invalidate();
  }
}
