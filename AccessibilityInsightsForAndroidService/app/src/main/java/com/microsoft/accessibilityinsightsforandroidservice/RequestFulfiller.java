// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.os.CancellationSignal;

public interface RequestFulfiller {
  String fulfillRequest(CancellationSignal cancellationSignal) throws Exception;
}
