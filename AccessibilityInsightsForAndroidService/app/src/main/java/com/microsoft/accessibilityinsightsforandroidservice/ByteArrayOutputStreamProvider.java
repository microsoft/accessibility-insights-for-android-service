// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import java.io.ByteArrayOutputStream;

public class ByteArrayOutputStreamProvider {

  public ByteArrayOutputStream get() {
    return new ByteArrayOutputStream();
  }
}
