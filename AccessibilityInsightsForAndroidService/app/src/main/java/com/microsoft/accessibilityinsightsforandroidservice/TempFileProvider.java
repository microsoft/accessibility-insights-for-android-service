// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Configuration;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
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
  @NonNull private Context context;
  @NonNull private  WorkerManagerRunner workerManagerRunner;

  public TempFileProvider(Context context, WorkerManagerRunner workerManagerRunner) {
    this.context = context;
    this.workerManagerRunner = workerManagerRunner;
    File cacheDir = context.getCacheDir();
    String tempDirPath = cacheDir.getAbsolutePath() + File.separator + tempDirName;
    this.tempDir = new File(tempDirPath);
  }

  public void cleanOldFilesBestEffort() {
    long cutoffTime = new Date().getTime() - tempFileLifetimeMillis;
    File[] files = this.tempDir.listFiles();
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


    //We need try to create our own configuration of the WorkManager
    //Because it use to be created in the activity (but we dont have one)
    //We are trying to move this part because now we are not abel to mock it (because its android level files)
    /*Configuration myConfig = new Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build();
    WorkManager.initialize(context, myConfig);


    OneTimeWorkRequest cleanFilesWorker = new OneTimeWorkRequest.Builder(CleanWorker.class)
            .setInitialDelay(5, TimeUnit.MINUTES)
            .build();
    WorkManager.getInstance(context).enqueue(cleanFilesWorker);

    WorkerManagerRunner workerManagerRunner = new WorkerManagerRunner(context);
    workerManagerRunner.startWorkerManager().enqueue(workerManagerRunner.createTask());*/
    workerManagerRunner.enqueueTask(CleanWorker.class);
    return tempFile;
  }

  private void ensureTempDirExists() throws IOException {
    //noinspection ResultOfMethodCallIgnored
    this.tempDir.mkdir();
  }

  /*public class CleanWorker extends Worker {

    private final Context context;

    public CleanWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
      super(context, workerParams);
      this.context = context;
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
      TempFileProvider tempFileProvider = new TempFileProvider(context.getCacheDir());
      tempFileProvider.cleanOldFilesBestEffort();
      return ListenableWorker.Result.success();
    }
  }*/
}
