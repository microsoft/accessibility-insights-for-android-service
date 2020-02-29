// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

public class UnrecognizedRequestFulfiller implements RequestFulfiller {

  private final ResponseWriter responseWriter;
  private final String requestString;

  public UnrecognizedRequestFulfiller(ResponseWriter responseWriter, String requestString) {
    this.responseWriter = responseWriter;
    this.requestString = requestString;
  }

  public void fulfillRequest(RunnableFunction onRequestFulfilled) {
    responseWriter.writeNotFoundResponse(requestString);
    onRequestFulfilled.run();
  }
}
