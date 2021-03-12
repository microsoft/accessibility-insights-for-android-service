package com.microsoft.accessibilityinsightsforandroidservice;

import java.util.ArrayList;
import java.util.function.Consumer;

public class FocusVisualizationStateManager {
    private boolean enabled = false;
    private ArrayList<Consumer<Boolean>> onChangedListeners;

    public FocusVisualizationStateManager() {
        onChangedListeners = new ArrayList<Consumer<Boolean>>();
    }

    public void subscribe(Consumer<Boolean> listener) {
        onChangedListeners.add(listener);
    }

    public void setState(boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }

        this.enabled = enabled;
        this.emitChanged(enabled);
    }

    public boolean getState() {
        return this.enabled;
    }

    private void emitChanged(boolean enabled) {
        onChangedListeners.forEach(listener -> {
            listener.accept(enabled);
        });
    }
}
