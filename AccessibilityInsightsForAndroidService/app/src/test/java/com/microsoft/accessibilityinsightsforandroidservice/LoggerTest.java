// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import android.util.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({Log.class})
public class LoggerTest {

  final String logTag = "logTag";
  final String logMessage = "log message";

  MockedStatic<Log> logStaticMock;

  @Before
  public void prepare() {
    logStaticMock = Mockito.mockStatic(Log.class);
  }

  @After
  public void cleanUp() {
    logStaticMock.close();
  }

  @Test
  public void logVerboseDebugOn() {
    Whitebox.setInternalState(Logger.class, "LOG", true);

    Logger.logVerbose(logTag, logMessage);

    logStaticMock.verify(() -> Log.v(logTag, logMessage));
  }

  @Test
  public void logVerboseDebugOff() {
    Whitebox.setInternalState(Logger.class, "LOG", false);

    Logger.logVerbose(logTag, logMessage);

    logStaticMock.verifyNoMoreInteractions();
  }

  @Test
  public void logDebugDebugOn() {
    Whitebox.setInternalState(Logger.class, "LOG", true);

    Logger.logDebug(logTag, logMessage);

    logStaticMock.verify(() -> Log.d(logTag, logMessage));
  }

  @Test
  public void logDebugDebugOff() {
    Whitebox.setInternalState(Logger.class, "LOG", false);

    Logger.logDebug(logTag, logMessage);

    logStaticMock.verifyNoMoreInteractions();
  }

  @Test
  public void logErrorDebugOn() {
    Whitebox.setInternalState(Logger.class, "LOG", true);

    Logger.logError(logTag, logMessage);

    logStaticMock.verify(() -> Log.e(logTag, logMessage));
  }

  @Test
  public void logErrorDebugOff() {
    Whitebox.setInternalState(Logger.class, "LOG", false);

    Logger.logError(logTag, logMessage);

    logStaticMock.verifyNoMoreInteractions();
  }

  @Test
  public void logInfoDebugOn() {
    Whitebox.setInternalState(Logger.class, "LOG", true);

    Logger.logInfo(logTag, logMessage);

    logStaticMock.verify(() -> Log.i(logTag, logMessage));
  }

  @Test
  public void logInfoDebugOff() {
    Whitebox.setInternalState(Logger.class, "LOG", false);

    Logger.logInfo(logTag, logMessage);

    logStaticMock.verifyNoMoreInteractions();
  }

  @Test
  public void logWarningDebugOn() {
    Whitebox.setInternalState(Logger.class, "LOG", true);

    Logger.logWarning(logTag, logMessage);

    logStaticMock.verify(() -> Log.w(logTag, logMessage));
  }

  @Test
  public void logWarningDebugOff() {
    Whitebox.setInternalState(Logger.class, "LOG", false);

    Logger.logWarning(logTag, logMessage);

    logStaticMock.verifyNoMoreInteractions();
  }
}
