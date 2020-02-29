// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RequestReaderFactory {

  public RequestReader createRequestReader(InputStream inputStream) {
    return new RequestReader(new BufferedReader(new InputStreamReader(inputStream)));
  }
}
