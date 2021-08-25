// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.junit.Assert.assertThrows;

import android.os.CancellationSignal;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UnrecognizedRequestFulfillerTest {
  final String requestMethod = "Test request method";

  @Mock CancellationSignal cancellationSignal;

  UnrecognizedRequestFulfiller testSubject;

  @Before
  public void prepare() {
    testSubject = new UnrecognizedRequestFulfiller(requestMethod);
  }

  @Test
  public void unrecognizedResponseFulfillerExists() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void fulfillsRequestByThrowingPinnedException() {
    assertThrows(
        "Unrecognized request: Test request method",
        RuntimeException.class,
        () -> testSubject.fulfillRequest(cancellationSignal));
  }
}
