// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;

@RunWith(MockitoJUnitRunner.class)
public class RequestReaderTest {

  @Mock BufferedReader bufferedReaderMock;

  RequestReader testSubject;

  @Before
  public void prepare() {
    testSubject = new RequestReader(bufferedReaderMock);
  }

  @Test
  public void requestReaderExists() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void readsFromBufferedReader() throws IOException {
    String requestString = "test request string";
    setupReadLine(requestString);

    String actualRequestString = testSubject.readRequest();

    Assert.assertEquals(actualRequestString, requestString);
  }

  @Test
  public void limitsInputLength() throws IOException {
    OngoingStubbing<Integer> bufferedReaderStubbing = when(bufferedReaderMock.read());
    for (int i = 0; i < 300; i++) {
      bufferedReaderStubbing = bufferedReaderStubbing.thenReturn(42);
    }

    try {
      testSubject.readRequest();
      Assert.fail("Should have thrown exception");
    } catch (IOException e) {
      Assert.assertEquals(e.getMessage(), "input too long");
    }
  }

  private void setupReadLine(String str) {
    OngoingStubbing<Integer> bufferedReaderStubbing;
    try {
      bufferedReaderStubbing = when(bufferedReaderMock.read());
    } catch (IOException e) {
      Assert.fail(e.getMessage());
      return;
    }

    for (int i = 0; i < str.length(); i++) {
      bufferedReaderStubbing = bufferedReaderStubbing.thenReturn((int) str.charAt(i));
    }

    bufferedReaderStubbing.thenReturn((int) '\n');
  }
}
