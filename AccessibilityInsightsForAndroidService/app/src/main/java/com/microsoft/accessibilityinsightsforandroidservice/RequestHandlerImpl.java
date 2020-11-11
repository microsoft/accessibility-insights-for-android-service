// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

public class RequestHandlerImpl implements RequestHandler {
  private final SocketHolder socketHolder;
  private final RequestFulfiller requestFulfiller;
  private final String logTag;
  private final String requestStartMessage;

  public RequestHandlerImpl(
      SocketHolder socketHolder,
      RequestFulfiller requestFulfiller,
      String logTag,
      String requestStartMessage) {
    this.socketHolder = socketHolder;
    this.requestFulfiller = requestFulfiller;
    this.logTag = logTag;
    this.requestStartMessage = requestStartMessage;
  }

  public void handleRequest() {
    logRequestStart();
    requestFulfiller.fulfillRequest(this::onRequestFulfilled);
  }

  @Override
  public boolean getIsBlockingRequest() {
    return this.requestFulfiller.getIsBlockingRequest();
  }

  private void onRequestFulfilled() {
    socketHolder.close(logTag);
  }

  private void logRequestStart() {
    Logger.logVerbose(logTag, requestStartMessage);
  }
}
