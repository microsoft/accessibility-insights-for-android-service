// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import com.deque.axe.android.Axe;
import com.deque.axe.android.AxeConf;
import com.deque.axe.android.constants.AxeStandard;

public class AxeRunnerFactory {
  // AxeConf.removeStandard is marked deprecated, but is still suggested in the axe-android docs as
  // the recommended way to constrain which standards we scan against.
  //
  // https://github.com/dequelabs/axe-android/issues/145 tracks the request for a non-deprecated
  // replacement option.
  @SuppressWarnings("deprecation")
  public Axe createAxeRunner() {
    AxeConf axeConf = new AxeConf();

    axeConf.removeStandard(AxeStandard.BEST_PRACTICE);
    axeConf.removeStandard(AxeStandard.PLATFORM);
    return new Axe(axeConf);
  }
}
