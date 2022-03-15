// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.content.Context;
import androidx.work.Configuration;
import androidx.work.WorkManager;
import java.util.WeakHashMap;

public class WorkManagerHolder {
  private static WeakHashMap<Context, WorkManager> ContextToManagerMap =
      new WeakHashMap<Context, WorkManager>();

  public static WorkManager getWorkManager(Context context) {
    WorkManager managerForContext = ContextToManagerMap.get(context);

    if (managerForContext == null) {
      WorkManager.initialize(context, new Configuration.Builder().build());
      managerForContext = WorkManager.getInstance(context);
      ContextToManagerMap.put(context, managerForContext);
    }
    return managerForContext;
  }
}
