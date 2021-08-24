// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.os.CancellationSignal;
import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;

public class SynchronizedRequestDispatcher {
  public static final SynchronizedRequestDispatcher SharedInstance =
      new SynchronizedRequestDispatcher();

  private RequestDispatcher underlyingDispatcher = null;
  private Object lock = new Object();
  private CancellationSignal teardownSignal = new CancellationSignal();

  @AnyThread
  public void setup(@NonNull RequestDispatcher instance) {
    synchronized (lock) {
      if (this.underlyingDispatcher != null) {
        throw new RuntimeException("Attempt to double-initialize instance");
      }
      this.teardownSignal = new CancellationSignal();
      this.underlyingDispatcher = instance;
    }
  }

  @AnyThread
  public void teardown() {
    CancellationSignal teardownSignal = this.teardownSignal;
    if (teardownSignal != null) {
      teardownSignal.cancel();
    }

    synchronized (lock) {
      this.underlyingDispatcher = null;
      this.teardownSignal = null;
    }
  }

  @AnyThread
  public String request(@NonNull String method, @NonNull CancellationSignal cancellationSignal)
      throws Exception {
    CancellationSignal combinedCancellationSignal = new CancellationSignal();

    synchronized (lock) {
      if (underlyingDispatcher == null) {
        throw new Exception("Service is not running");
      }

      teardownSignal.setOnCancelListener(combinedCancellationSignal::cancel);
      cancellationSignal.setOnCancelListener(combinedCancellationSignal::cancel);
      combinedCancellationSignal.throwIfCanceled();

      return underlyingDispatcher.request(method, combinedCancellationSignal);
    }
  }
}
