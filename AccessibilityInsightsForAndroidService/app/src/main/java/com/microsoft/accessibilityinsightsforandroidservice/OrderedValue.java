// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import java.util.Objects;

public class OrderedValue<T> implements Comparable<OrderedValue<T>> {
  public final long order;
  public final T value;

  public OrderedValue(T value, long order) {
    this.value = value;
    this.order = order;
  }

  @Override
  public int compareTo(OrderedValue<T> other) {
    return Long.compare(this.order, other.order);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OrderedValue<?> that = (OrderedValue<?>) o;
    return order == that.order;
  }

  @Override
  public int hashCode() {
    return Objects.hash(order);
  }
}
