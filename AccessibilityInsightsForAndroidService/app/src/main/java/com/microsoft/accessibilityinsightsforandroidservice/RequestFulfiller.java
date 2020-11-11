// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

public interface RequestFulfiller {
  void fulfillRequest(RunnableFunction onRequestFulfilled);

  boolean getIsBlockingRequest();
}
