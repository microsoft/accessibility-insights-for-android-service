// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static java.lang.System.lineSeparator;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({StackTrace.class})
public class ResponseWriterTest {

  StringWriter stringWriter;
  ResponseWriter testSubject;

  final String newLine = lineSeparator();
  final String responseCommon = "Content-language: en" + newLine + newLine;

  @Before
  public void prepare() {
    stringWriter = new StringWriter();
    testSubject = new ResponseWriter(new PrintWriter(stringWriter));
  }

  @Test
  public void writeNotFoundResponse() {
    String requestLine = "test request line";
    String expectedMessage =
        "HTTP/1.0 404 File Not Found"
            + newLine
            + "Content-type: text/html; charset=UTF-8"
            + newLine
            + responseCommon
            + "<b>404</b> This service can't process <i>"
            + requestLine
            + "</i>"
            + newLine;
    testSubject.writeNotFoundResponse(requestLine);
    String writtenMessage = stringWriter.toString();

    Assert.assertEquals(writtenMessage, expectedMessage);
  }

  @Test
  public void writeSuccessfulResponse() {
    String content = "test content";
    String expectedMessage =
        "HTTP/1.0 200 OK"
            + newLine
            + "Content-type: application/json"
            + newLine
            + responseCommon
            + content
            + newLine;

    testSubject.writeSuccessfulResponse(content);
    String writtenMessage = stringWriter.toString();

    Assert.assertEquals(writtenMessage, expectedMessage);
  }

  @Test
  public void writeErrorResponse() {
    String stackTrace = "error stack trace";
    Exception e = mock(Exception.class);
    prepareErrorStackTrace(e, stackTrace);

    String expectedMessage = createExpectedErrorMessage(stackTrace);

    testSubject.writeErrorResponse(e);
    String writtenMessage = stringWriter.toString();

    Assert.assertEquals(writtenMessage, expectedMessage);
  }

  @Test
  public void writeErrorResponseReplacesTabs() {
    String stackTrace = "error\tat line";
    Exception e = mock(Exception.class);
    prepareErrorStackTrace(e, stackTrace);

    String expectedStackTrace = "error at<br> line";
    String expectedMessage = createExpectedErrorMessage(expectedStackTrace);

    testSubject.writeErrorResponse(e);
    String writtenMessage = stringWriter.toString();

    Assert.assertEquals(writtenMessage, expectedMessage);
  }

  private void prepareErrorStackTrace(Exception e, String stackTrace) {
    PowerMockito.mockStatic(StackTrace.class);
    when(StackTrace.getStackTrace(e)).thenReturn(stackTrace);
  }

  private String createExpectedErrorMessage(String stackTrace) {
    return "HTTP/1.0 500 Internal Server Error"
        + newLine
        + "Content-type: text/html; charset=UTF-8"
        + newLine
        + responseCommon
        + "<b>An Exception was thrown!</b><p>"
        + stackTrace
        + newLine;
  }
}
