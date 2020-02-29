// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestReader {

  private BufferedReader reader;
  private static final int maxLineLength = 256;

  public RequestReader(BufferedReader reader) {
    this.reader = reader;
  }

  public String readRequest() throws IOException {

    StringBuffer buffer = new StringBuffer();
    int intC = reader.read();
    while (intC != -1) {
      char c = (char) intC;
      if (c == '\n') {
        break;
      }
      if (buffer.length() >= maxLineLength) {
        throw new IOException("input too long");
      }
      buffer.append(c);
      intC = reader.read();
    }

    return buffer.toString();
  }
}
