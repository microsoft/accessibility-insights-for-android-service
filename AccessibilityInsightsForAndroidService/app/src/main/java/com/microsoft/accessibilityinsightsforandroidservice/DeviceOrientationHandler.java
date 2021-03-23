// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import java.util.ArrayList;
import java.util.function.Consumer;

public class DeviceOrientationHandler {
  private int orientation;
  private ArrayList<Consumer<Integer>> onOrientationChangedListeners;

  public DeviceOrientationHandler(int orientation) {
    this.orientation = orientation;
    this.onOrientationChangedListeners = new ArrayList<>();
  }

  public void subscribe(Consumer<Integer> listener) {
    this.onOrientationChangedListeners.add(listener);
  }

  public void setOrientation(int orientation) {
    if (this.orientation != orientation) {
      this.orientation = orientation;
      this.emitChanged(orientation);
    }
  }

  public int getOrientation() {
    return this.orientation;
  }

  private void emitChanged(int orientation) {
    this.onOrientationChangedListeners.forEach(
        listener -> {
          listener.accept(orientation);
        });
  }
}
