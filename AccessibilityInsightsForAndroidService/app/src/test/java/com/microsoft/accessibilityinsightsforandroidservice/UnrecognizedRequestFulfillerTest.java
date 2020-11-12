// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.Mockito.verify;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UnrecognizedRequestFulfillerTest {

  @Mock ResponseWriter responseWriter;
  @Mock RunnableFunction onRequestFulfilledMock;

  final String requestString = "Test request string";

  UnrecognizedRequestFulfiller testSubject;

  @Before
  public void prepare() {
    testSubject = new UnrecognizedRequestFulfiller(responseWriter, requestString);
  }

  @Test
  public void unrecognizedResponseFulfillerExists() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void isBlockingRequestReturnsFalse() {
    Assert.assertFalse(testSubject.isBlockingRequest());
  }

  @Test
  public void callsOnRequestFulfilled() {
    testSubject.fulfillRequest(onRequestFulfilledMock);
  }

  @Test
  public void writesResponseMessage() {
    testSubject.fulfillRequest(onRequestFulfilledMock);
    verify(responseWriter).writeNotFoundResponse(requestString);
  }
}
