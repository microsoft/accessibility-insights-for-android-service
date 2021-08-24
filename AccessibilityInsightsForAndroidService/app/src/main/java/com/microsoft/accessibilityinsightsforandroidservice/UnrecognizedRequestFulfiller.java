// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.os.CancellationSignal;

public class UnrecognizedRequestFulfiller implements RequestFulfiller {
  private final String requestMethod;

  public UnrecognizedRequestFulfiller(String requestMethod) {
    this.requestMethod = requestMethod;
  }

  public String fulfillRequest(CancellationSignal cancellationSignal) {
    throw new RuntimeException("Unrecognized request: " + requestMethod);
  }
}
