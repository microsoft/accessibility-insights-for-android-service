// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import java.io.OutputStream;
import java.io.PrintWriter;

public class ResponseWriterFactory {

  public ResponseWriter createResponseWriter(OutputStream outputStream) {
    return new ResponseWriter(new PrintWriter(outputStream));
  }
}
