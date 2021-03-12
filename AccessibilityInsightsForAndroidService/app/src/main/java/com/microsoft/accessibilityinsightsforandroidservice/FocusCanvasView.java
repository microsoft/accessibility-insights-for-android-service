// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class FocusCanvasView extends View {

  private ArrayList<FocusElementHighlight> focusElementHighlights;
  private ArrayList<FocusElementLine> focusElementLines;
  public FocusCanvasView(Context context){
    super(context);
    this.focusElementHighlights = new ArrayList<>();
    this.focusElementLines = new ArrayList<>();
  }
  public FocusCanvasView(Context context, AttributeSet attrs){
    super(context, attrs);
    this.focusElementHighlights = new ArrayList<>();
    this.focusElementLines = new ArrayList<>();
  }
  public FocusCanvasView(Context context, AttributeSet attrs, int toolInt){
    super(context, attrs, toolInt);
    this.focusElementHighlights = new ArrayList<>();
    this.focusElementLines = new ArrayList<>();
  }


  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    for(int i = 0; i < this.focusElementHighlights.size(); i++){
      if(focusElementHighlights.size() > 1 && i > 0){
        this.focusElementLines.get(i).drawLine(canvas);
        this.focusElementHighlights.get(i-1).drawElementHighlight(canvas);
      }
      this.focusElementHighlights.get(i).drawElementHighlight(canvas);

    }

  }

  public void setFocusElementHighlights(ArrayList<FocusElementHighlight> highlights){
    this.focusElementHighlights = highlights;
  }

  public void setFocusElementLines(ArrayList<FocusElementLine> lines){
    this.focusElementLines = lines;
  }

  public void redraw(){
    this.invalidate();
  }

}
