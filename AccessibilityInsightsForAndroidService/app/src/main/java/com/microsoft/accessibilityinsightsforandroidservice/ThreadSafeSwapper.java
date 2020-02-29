// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

public class ThreadSafeSwapper<T> {
  private final Object lock_object = new Object();
  private T currentObject;

  public T swap(T newObject) {
    synchronized (lock_object) {
      T oldObject = currentObject;
      currentObject = newObject;
      return oldObject;
    }
  }

  public boolean setIfCurrentlyNull(T newObject) {
    synchronized (lock_object) {
      T oldObject = currentObject;
      if (oldObject != null) {
        return false;
      }

      currentObject = newObject;
      return true;
    }
  }
}
