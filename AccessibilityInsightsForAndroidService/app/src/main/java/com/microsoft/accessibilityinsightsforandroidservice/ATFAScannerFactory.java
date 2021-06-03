// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.content.Context;

public class ATFAScannerFactory {
  public static ATFAScanner createATFAScanner(Context context) {
    return new ATFAScanner(context);
  }
}
