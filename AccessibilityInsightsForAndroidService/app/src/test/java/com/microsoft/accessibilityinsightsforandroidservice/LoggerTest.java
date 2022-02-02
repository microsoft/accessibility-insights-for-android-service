// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.util.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoggerTest {

  final String logTag = "logTag";
  final String logMessage = "log message";

  boolean originalEnableLogging;
  MockedStatic<Log> logStaticMock;

  @Before
  public void prepare() {
    logStaticMock = Mockito.mockStatic(Log.class);
    originalEnableLogging = Logger.ENABLE_LOGGING;
  }

  @After
  public void cleanUp() {
    Logger.ENABLE_LOGGING = originalEnableLogging;
    logStaticMock.close();
  }

  @Test
  public void logVerboseDebugOn() {
    Logger.ENABLE_LOGGING = true;

    Logger.logVerbose(logTag, logMessage);

    logStaticMock.verify(() -> Log.v(logTag, logMessage));
  }

  @Test
  public void logVerboseDebugOff() {
    Logger.ENABLE_LOGGING = false;

    Logger.logVerbose(logTag, logMessage);

    logStaticMock.verifyNoMoreInteractions();
  }

  @Test
  public void logDebugDebugOn() {
    Logger.ENABLE_LOGGING = true;

    Logger.logDebug(logTag, logMessage);

    logStaticMock.verify(() -> Log.d(logTag, logMessage));
  }

  @Test
  public void logDebugDebugOff() {
    Logger.ENABLE_LOGGING = false;

    Logger.logDebug(logTag, logMessage);

    logStaticMock.verifyNoMoreInteractions();
  }

  @Test
  public void logErrorDebugOn() {
    Logger.ENABLE_LOGGING = true;

    Logger.logError(logTag, logMessage);

    logStaticMock.verify(() -> Log.e(logTag, logMessage));
  }

  @Test
  public void logErrorDebugOff() {
    Logger.ENABLE_LOGGING = false;

    Logger.logError(logTag, logMessage);

    logStaticMock.verifyNoMoreInteractions();
  }

  @Test
  public void logInfoDebugOn() {
    Logger.ENABLE_LOGGING = true;

    Logger.logInfo(logTag, logMessage);

    logStaticMock.verify(() -> Log.i(logTag, logMessage));
  }

  @Test
  public void logInfoDebugOff() {
    Logger.ENABLE_LOGGING = false;

    Logger.logInfo(logTag, logMessage);

    logStaticMock.verifyNoMoreInteractions();
  }

  @Test
  public void logWarningDebugOn() {
    Logger.ENABLE_LOGGING = true;

    Logger.logWarning(logTag, logMessage);

    logStaticMock.verify(() -> Log.w(logTag, logMessage));
  }

  @Test
  public void logWarningDebugOff() {
    Logger.ENABLE_LOGGING = false;

    Logger.logWarning(logTag, logMessage);

    logStaticMock.verifyNoMoreInteractions();
  }
}
