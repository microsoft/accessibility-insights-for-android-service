// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import java.util.ArrayList;

public class FocusVisualizationStateManager {
  private boolean enabled = false;
  private ArrayList<Command> onChangedListeners;

  public FocusVisualizationStateManager() {
    onChangedListeners = new ArrayList<Command>();
  }

  public void subscribe(Command listener) {
    onChangedListeners.add(listener);
  }

  public void setState(boolean enabled) {
    if (this.enabled == enabled) {
      return;
    }

    this.enabled = enabled;
    this.emitChanged();
  }

  public boolean getState() {
    return this.enabled;
  }

  private void emitChanged() {
    onChangedListeners.forEach(
        listener -> {
          listener.execute();
        });
  }
}
