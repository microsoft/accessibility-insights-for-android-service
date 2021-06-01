// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import com.deque.axe.android.AxeResult;

public class ResultSerializer {
  private AxeResult axeResult;

  public ResultSerializer() {}

  public void addAxeResult(AxeResult axeResult) {
    this.axeResult = axeResult;
  }

  public String generateResultJson() {
    return this.axeResult.toJson();
  }
}
