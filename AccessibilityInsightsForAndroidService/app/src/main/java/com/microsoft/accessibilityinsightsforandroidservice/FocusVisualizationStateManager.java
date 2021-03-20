// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.util.Log;

import java.util.ArrayList;
import java.util.function.Consumer;

public class FocusVisualizationStateManager {
  private static final String TAG = "FocusVisualizationState";
  private boolean enabled = false;
  private ArrayList<Consumer<Boolean>> onChangedListeners;

  public FocusVisualizationStateManager() {
    onChangedListeners = new ArrayList<Consumer<Boolean>>();
  }

  public void subscribe(Consumer<Boolean> listener) {
    onChangedListeners.add(listener);
  }

  public void setState(boolean newState) {
    if (this.enabled == newState) {
      return;
    }

    this.enabled = newState;
    Log.v(TAG, "state updated");
    this.emitChanged(enabled);
  }

  public boolean getState() {
    return this.enabled;
  }

  private void emitChanged(boolean enabled) {
    onChangedListeners.forEach(
        listener -> {
          listener.accept(enabled);
        });
  }
}
