// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TempFileProvider {
  // Avoid ever changing this; we want new versions of the app to be able to recognize and clean up
  // old versions'
  @NonNull
  private static final String tempDirName =
      "com.microsoft.accessibilityinsightsforandroidservice.TempFileProvider";

  @NonNull public static final int tempFileLifetimeMillis = 5 * 60 * 1000; // 5 minutes
  @NonNull private File tempDir;
  @NonNull private WorkManager workManager;

  public TempFileProvider(Context context) {
    this(context, WorkManager.getInstance(context));
  }

  public TempFileProvider(Context context, WorkManager workManager) {
    this.workManager = workManager;
    File cacheDir = context.getCacheDir();
    String tempDirPath = cacheDir.getAbsolutePath() + File.separator + tempDirName;
    this.tempDir = new File(tempDirPath);
  }

  public void cleanOldFilesBestEffort() {
    cleanOldFilesBestEffort(tempDir);
  }

  private static void cleanOldFilesBestEffort(File tempDir) {
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

  public File createTempFileWithContents(String contents) throws IOException {
    ensureTempDirExists();
    File tempFile = File.createTempFile("TempFileProvider", "tmp", this.tempDir);
    try (BufferedWriter writer =
        new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8))) {
      writer.write(contents);
      writer.flush();
    }
    scheduleCleanOldFiles(tempDir.getAbsolutePath());
    return tempFile;
  }

  private void ensureTempDirExists() throws IOException {
    //noinspection ResultOfMethodCallIgnored
    this.tempDir.mkdir();
  }

  private void scheduleCleanOldFiles(String tempDir){
    Data inputData = new Data.Builder().putString("tempDir", tempDir).build();
    OneTimeWorkRequest cleanFilesWorker = new OneTimeWorkRequest.Builder(CleanWorker.class)
            .setInitialDelay(tempFileLifetimeMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build();
    workManager.enqueue(cleanFilesWorker);
  }

  public static class CleanWorker extends Worker {
    private String tempDir;

    public CleanWorker(@NonNull Context context, @NonNull  WorkerParameters workerParams) {
      super(context, workerParams);
      tempDir = workerParams.getInputData().getString("tempDir");
    }

    @NonNull
    @Override
    public Result doWork() {
      cleanOldFilesBestEffort(new File(tempDir));
      return Result.success();
    }
  }
}
