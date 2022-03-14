// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.content.Context;

import androidx.work.Configuration;
import androidx.work.WorkManager;

import java.util.HashMap;

public class WorkManagerHolder {
    private static HashMap<Context, WorkManager> ContextToManagerMap = new HashMap<Context, WorkManager>();

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
