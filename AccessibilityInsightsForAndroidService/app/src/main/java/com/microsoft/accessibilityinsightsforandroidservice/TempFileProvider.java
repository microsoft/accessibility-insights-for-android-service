// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import androidx.annotation.NonNull;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class TempFileProvider {
  // Avoid ever changing this; we want new versions of the app to be able to recognize and clean up
  // old versions'
  @NonNull
  private static final String tempDirName =
      "com.microsoft.accessibilityinsightsforandroidservice.TempFileProvider";

  @NonNull public static final int tempFileLifetimeMillis = 5 * 60 * 1000; // 5 minutes
  @NonNull private File tempDir;

  public TempFileProvider(File cacheDir) {
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
    return tempFile;
  }

  private void ensureTempDirExists() throws IOException {
    //noinspection ResultOfMethodCallIgnored
    this.tempDir.mkdir();
  }
}
