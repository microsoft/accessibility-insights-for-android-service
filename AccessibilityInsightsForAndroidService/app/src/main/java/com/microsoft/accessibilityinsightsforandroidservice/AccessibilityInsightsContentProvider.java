// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AccessibilityInsightsContentProvider extends ContentProvider {
  private SynchronizedRequestDispatcher requestDispatcher;
  private TempFileProvider tempFileProvider;

  @Override
  public boolean onCreate() {
    return onCreate(
        SynchronizedRequestDispatcher.SharedInstance,
        new TempFileProvider(getContext().getCacheDir()));
  }

  public boolean onCreate(
      SynchronizedRequestDispatcher requestDispatcher, TempFileProvider tempFileProvider) {
    this.requestDispatcher = requestDispatcher;
    this.tempFileProvider = tempFileProvider;
    return true;
  }

  @Nullable
  @Override
  public Cursor query(
      @NonNull Uri uri,
      @Nullable String[] strings,
      @Nullable String s,
      @Nullable String[] strings1,
      @Nullable String s1) {
    return null;
  }

  @Nullable
  @Override
  public String getType(@NonNull Uri uri) {
    return null;
  }

  @Nullable
  @Override
  public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
    return null;
  }

  @Override
  public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
    return 0;
  }

  @Override
  public int update(
      @NonNull Uri uri,
      @Nullable ContentValues contentValues,
      @Nullable String s,
      @Nullable String[] strings) {
    return 0;
  }

  @Nullable
  @Override
  public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
    verifyCallerPermissions();

    Bundle output = new Bundle();

    try {
      String response = requestDispatcher.request("/" + method, new CancellationSignal());
      output.putString("response", response);
    } catch (Exception e) {
      output.putString("response", e.toString());
    }

    return output;
  }

  @Nullable
  @Override
  public ParcelFileDescriptor openFile(
      @NonNull Uri uri, @NonNull String mode, @Nullable CancellationSignal signal) {
    verifyCallerPermissions();

    if (signal == null) {
      signal = new CancellationSignal();
    }

    String method = uri.getPath();

    String response;
    try {
      response = requestDispatcher.request(method, signal);
    } catch (Exception e) {
      response = e.toString();
    }

    try {
      ParcelFileDescriptor file =
          ParcelFileDescriptor.open(
              tempFileProvider.createTempFileWithContents(response),
              ParcelFileDescriptor.MODE_READ_ONLY);
      return file;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private final int AID_SHELL = 2000; // from android_filesystem_config.h

  private void verifyCallerPermissions() {
    if (Binder.getCallingUid() != AID_SHELL) {
      throw new SecurityException("This provider may only be queried via adb's shell user");
    }
  }
}
