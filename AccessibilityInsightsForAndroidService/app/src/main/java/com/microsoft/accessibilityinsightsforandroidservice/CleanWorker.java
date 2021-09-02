package com.microsoft.accessibilityinsightsforandroidservice;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.File;
import java.util.Date;

public class CleanWorker extends Worker {

    @NonNull public static final int tempFileLifetimeMillis = 5 * 60 * 1000; // 5 minutes
    private String cacheDir;

    public CleanWorker(@NonNull Context context, @NonNull  WorkerParameters workerParams) {
        super(context, workerParams);
        cacheDir = workerParams.getInputData().getString("cacheDirectoryPath");
    }

    private void cleanOldFilesBestEffort() {
        File tempDir = new File(cacheDir);
        long cutoffTime = new Date().getTime() - tempFileLifetimeMillis;
        File[] files = tempDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.lastModified() < cutoffTime) {
                    // We intentionally ignore failures (best-effort)
                    // noinspection ResultOfMethodCallIgnored
                    file.delete();
                }
            }
        }
    }

    @NonNull
    @Override
    public Result doWork() {
        cleanOldFilesBestEffort();
        return Result.success();
    }
}
