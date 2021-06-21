// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import com.deque.axe.android.AxeResult;

public class ResultV1Serializer {
    private AxeResult axeResult;

    public ResultV1Serializer() {}

    public void addAxeResult(AxeResult axeResult) {
        this.axeResult = axeResult;
    }

    public String generateResultJson() {
        return this.axeResult.toJson();
    }
}
