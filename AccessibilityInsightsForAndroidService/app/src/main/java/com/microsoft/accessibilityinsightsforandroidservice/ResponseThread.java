// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import java.io.IOException;
import java.net.Socket;

public class ResponseThread extends Thread {

  private static final String TAG = "ResponseThread";
  private Socket socket;
  private ResponseWriterFactory responseWriterFactory;
  private RequestReaderFactory requestReaderFactory;
  private RequestHandlerFactory requestHandlerFactory;
  private boolean isBlockingRequest;

  ResponseThread(
      Socket socket,
      ResponseWriterFactory responseWriterFactory,
      RequestReaderFactory requestReaderFactory,
      RequestHandlerFactory requestHandlerFactory) {
    this.socket = socket;
    this.responseWriterFactory = responseWriterFactory;
    this.requestReaderFactory = requestReaderFactory;
    this.requestHandlerFactory = requestHandlerFactory;
  }

  public boolean getIsBlockingRequest() {
    return isBlockingRequest;
  }

  @Override
  public void run() {
    String requestString;
    ResponseWriter responseWriter;

    try {
      RequestReader reader = requestReaderFactory.createRequestReader(socket.getInputStream());
      requestString = reader.readRequest();
      responseWriter = responseWriterFactory.createResponseWriter(socket.getOutputStream());
    } catch (IOException e) {
      Logger.logVerbose(TAG, StackTrace.getStackTrace(e));
      return;
    }

    processRequest(requestString, responseWriter);
  }

  private void processRequest(String requestString, ResponseWriter responseWriter) {
    try {
      RequestHandler handler =
          requestHandlerFactory.createHandlerForRequest(socket, requestString, responseWriter);
      isBlockingRequest = handler.getIsBlockingRequest();
      handler.handleRequest();
    } catch (Exception e) {
      responseWriter.writeErrorResponse(e);
    }
  }
}
