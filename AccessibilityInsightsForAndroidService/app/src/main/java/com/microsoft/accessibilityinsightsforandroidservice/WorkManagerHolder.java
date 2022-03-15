// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.content.Context;
import androidx.work.Configuration;
import androidx.work.WorkManager;
import java.util.WeakHashMap;

public class WorkManagerHolder {
  private static final Object LockObject = new Object();
  private static WeakHashMap<Context, WorkManager> ContextToManagerMap =
      new WeakHashMap<Context, WorkManager>();

  public static WorkManager getWorkManager(Context context) {
    synchronized (LockObject) {
      WorkManager managerForContext = ContextToManagerMap.get(context);

      if (managerForContext == null) {
        WorkManager.initialize(context, new Configuration.Builder().build());
        managerForContext = WorkManager.getInstance(context);
        ContextToManagerMap.put(context, managerForContext);
      }
      return managerForContext;
    }
  }
}
