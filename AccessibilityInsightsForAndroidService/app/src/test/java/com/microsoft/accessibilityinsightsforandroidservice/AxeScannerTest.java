// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.mockito.Mockito.when;

import android.graphics.Bitmap;
import android.view.accessibility.AccessibilityNodeInfo;
import com.deque.axe.android.Axe;
import com.deque.axe.android.AxeContext;
import com.deque.axe.android.AxeResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AxeScannerTest {

  @Mock Bitmap screenshotMock;
  @Mock AccessibilityNodeInfo accessibilityNodeInfoMock;
  @Mock AxeRunnerFactory axeRunnerFactoryMock;
  @Mock AxeContextFactory axeContextFactoryMock;
  @Mock AxeResult axeResultMock;
  @Mock Axe axeMock;
  @Mock AxeContext axeContextMock;

  AxeScanner testSubject;

  @Before
  public void prepare() {
    testSubject = new AxeScanner(axeRunnerFactoryMock, axeContextFactoryMock);
  }

  @Test
  public void scanWithAxeReturnsCorrectResult() throws ViewChangedException {
    when(axeRunnerFactoryMock.createAxeRunner()).thenReturn(axeMock);
    when(axeContextFactoryMock.createAxeContext(accessibilityNodeInfoMock, screenshotMock))
        .thenReturn(axeContextMock);
    when(axeMock.run(axeContextMock)).thenReturn(axeResultMock);

    Assert.assertEquals(
        testSubject.scanWithAxe(accessibilityNodeInfoMock, screenshotMock), axeResultMock);
  }
}
