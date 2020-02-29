// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import java.io.PrintWriter;

public class ResponseWriter {

  private final PrintWriter writer;

  public ResponseWriter(PrintWriter writer) {
    this.writer = writer;
  }

  public void writeNotFoundResponse(String line) {
    final String content = "<b>404</b> This service can't process <i>" + line + "</i>";

    writer.println("HTTP/1.0 404 File Not Found");
    writer.println("Content-type: text/html; charset=UTF-8");
    appendResponseCommon(content);
  }

  public void writeSuccessfulResponse(String content) {
    writer.println("HTTP/1.0 200 OK");
    writer.println("Content-type: application/json");
    appendResponseCommon(content);
  }

  public void writeErrorResponse(Exception e) {
    final String stackTrace = StackTrace.getStackTrace(e).replaceAll("\tat", " at<br>");
    final String content = "<b>An Exception was thrown!</b><p>" + stackTrace;

    writer.println("HTTP/1.0 500 Internal Server Error");
    writer.println("Content-type: text/html; charset=UTF-8");
    appendResponseCommon(content);
  }

  private void appendResponseCommon(String content) {
    writer.println("Content-language: en");
    writer.println();
    writer.println(content);
    writer.flush();
  }
}
