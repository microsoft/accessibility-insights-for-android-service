// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

public class RequestHandlerImplFactory {

  public RequestHandlerImpl createRequestHandler(
      SocketHolder socketHolder,
      RequestFulfiller onFulfill,
      String logTag,
      String requestStartMessage) {
    return new RequestHandlerImpl(socketHolder, onFulfill, logTag, requestStartMessage);
  }
}
