// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import java.util.HashMap;

public class FocusVisualizerStyles {
  private Paint currentLinePaint;
  private Paint currentOuterCirclePaint;
  private Paint nonCurrentLinePaint;
  private Paint nonCurrentOuterCirclePaint;
  private Paint innerCirclePaint;
  private Paint numberPaint;

  private HashMap<String, Paint> currentElementPaints;
  private HashMap<String, Paint> nonCurrentElementPaints;
  private HashMap<String, Paint> currentLinePaints;
  private HashMap<String, Paint> nonCurrentLinePaints;

  public int focusElementHighlightRadius = 50;

  public FocusVisualizerStyles() {
    setCurrentOuterCirclePaint();
    setInnerCirclePaint();
    setNonCurrentLinePaint();
    setNumberPaint();
    setCurrentLinePaint();
    setNonCurrentOuterCirclePaint();

    setCurrentElementPaints();
    setNonCurrentElementPaints();
    setCurrentLinePaints();
    setNonCurrentLinePaints();
  }

  private void setCurrentElementPaints() {
    this.currentElementPaints = new HashMap<>();
    this.currentElementPaints.put("outerCircle", this.currentOuterCirclePaint);
    this.currentElementPaints.put("innerCircle", this.innerCirclePaint);
    this.currentElementPaints.put("number", this.numberPaint);
  }

  public HashMap<String, Paint> getCurrentElementPaints() {
    return currentElementPaints;
  }

  private void setNonCurrentElementPaints() {
    this.nonCurrentElementPaints = new HashMap<>();
    this.nonCurrentElementPaints.put("outerCircle", this.nonCurrentOuterCirclePaint);
    this.nonCurrentElementPaints.put("innerCircle", this.innerCirclePaint);
    this.nonCurrentElementPaints.put("number", this.numberPaint);
  }

  public HashMap<String, Paint> getNonCurrentElementPaints() {
    return nonCurrentElementPaints;
  }

  private void setCurrentLinePaints() {
    this.currentLinePaints = new HashMap<>();
    this.currentLinePaints.put("line", this.currentLinePaint);
  }

  public HashMap<String, Paint> getCurrentLinePaints() {
    return currentLinePaints;
  }

  private void setNonCurrentLinePaints() {
    this.nonCurrentLinePaints = new HashMap<>();
    this.nonCurrentLinePaints.put("line", this.nonCurrentLinePaint);
  }

  public HashMap<String, Paint> getNonCurrentLinePaints() {
    return nonCurrentLinePaints;
  }

  private void setNonCurrentLinePaint() {
    this.nonCurrentLinePaint = new Paint();
    this.nonCurrentLinePaint.setStyle(Paint.Style.STROKE);
    this.nonCurrentLinePaint.setColor(Color.BLACK);
    this.nonCurrentLinePaint.setStrokeWidth(3);
  }

  private void setNonCurrentOuterCirclePaint() {
    this.nonCurrentOuterCirclePaint = new Paint();
    this.nonCurrentOuterCirclePaint.setStyle(Paint.Style.STROKE);
    this.nonCurrentOuterCirclePaint.setColor(Color.BLACK);
    this.nonCurrentOuterCirclePaint.setStrokeWidth(3);
  }

  private void setCurrentOuterCirclePaint() {
    this.currentOuterCirclePaint = new Paint();
    this.currentOuterCirclePaint.setStyle(Paint.Style.STROKE);
    this.currentOuterCirclePaint.setColor(Color.parseColor("#B4009E"));
    this.currentOuterCirclePaint.setStrokeWidth(3);
  }

  private void setInnerCirclePaint() {
    this.innerCirclePaint = new Paint();
    this.innerCirclePaint.setStyle(Paint.Style.FILL);
    this.innerCirclePaint.setColor(Color.WHITE);
  }

  private void setNumberPaint() {
    this.numberPaint = new Paint();
    this.numberPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    this.numberPaint.setTextAlign(Paint.Align.CENTER);
    this.numberPaint.setColor(Color.BLACK);
    this.numberPaint.setStrokeWidth(3);
    this.numberPaint.setTextSize(45);
  }

  private void setCurrentLinePaint() {
    this.currentLinePaint = new Paint();
    this.currentLinePaint.setStyle(Paint.Style.STROKE);
    this.currentLinePaint.setColor(Color.parseColor("#B4009E"));
    this.currentLinePaint.setStrokeWidth(10);
    this.currentLinePaint.setPathEffect(new DashPathEffect(new float[] {25, 15}, 0));
  }
}
