// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.deque.axe.android.AxeResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ResultV1SerializerTest {

  @Mock AxeResult axeResultMock;

  ResultV1Serializer testSubject;

  final String scanResultJson = "axe scan result";

  @Before
  public void prepare() {
    testSubject = new ResultV1Serializer();
  }

  @Test
  public void resultSerializerExists() {
    Assert.assertNotNull(testSubject);
  }

  @Test
  public void generatesExpectedJson() {
    when(axeResultMock.toJson()).thenReturn(scanResultJson);

    testSubject.addAxeResult(axeResultMock);
    String generatedJson = testSubject.generateResultJson();

    verify(axeResultMock, times(1)).toJson();
    Assert.assertEquals(generatedJson, scanResultJson);
  }
}
