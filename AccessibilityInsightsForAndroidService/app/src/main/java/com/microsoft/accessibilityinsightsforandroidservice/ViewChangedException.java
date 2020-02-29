// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

class ViewChangedException extends Exception {
  public ViewChangedException() {
    this("");
  }

  public ViewChangedException(String additionalMessage) {
    super("The view hierarchy changed while building the AxeView tree. " + additionalMessage);
  }
}
