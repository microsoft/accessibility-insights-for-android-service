// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import android.os.CancellationSignal;
import android.os.OperationCanceledException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({SynchronizedRequestDispatcher.class})
public class SynchronizedRequestDispatcherTest {
  @Mock RequestDispatcher underlyingDispatcher;

  CancellationSignal cancellationSignal;
  SynchronizedRequestDispatcher testSubject;

  @Before
  public void prepare() throws Exception {
    cancellationSignal = makeBasicCancellationSignal();
    whenNew(CancellationSignal.class)
        .withNoArguments()
        .thenAnswer(constructorInvocation -> makeBasicCancellationSignal());

    testSubject = new SynchronizedRequestDispatcher();
  }

  @Test
  public void teardownWaitsForOutstandingRequests() throws Exception {
    testSubject.setup(underlyingDispatcher);

    DelayedRequest delayedRequest = setupCancellableDelayedRequest();
    Thread request1Thread =
        startOnNewThread(
            () -> testSubject.request("cancellable delayed method", cancellationSignal));
    request1Thread.start();

    delayedRequest.waitForDelayedState();

    Thread teardownThread = startOnNewThread(() -> testSubject.teardown());
    teardownThread.start();

    // teardown shouldn't run until request1 is allowed to finish
    teardownThread.join(THREAD_DELAY_ALLOWANCE_MS);
    assertTrue(teardownThread.isAlive());

    delayedRequest.allowToFinishOrBeCancelled();
    request1Thread.join(THREAD_JOIN_TIMEOUT_MS);
    assertFalse(request1Thread.isAlive());

    teardownThread.join(THREAD_JOIN_TIMEOUT_MS);
    assertFalse(teardownThread.isAlive());
  }

  @Test
  public void multipleSetupThrowsException() {
    testSubject.setup(underlyingDispatcher);
    assertThrows(
        "RequestDispatcher cannot be set up twice",
        Exception.class,
        () -> testSubject.setup(underlyingDispatcher));
  }

  @Test
  public void teardownWithoutSetupIsNoop() {
    testSubject.teardown(); // shouldn't throw
  }

  @Test
  public void teardownIsIdempotent() {
    testSubject.setup(underlyingDispatcher);
    testSubject.teardown();
    testSubject.teardown(); // shouldn't throw
  }

  private static final int THREAD_DELAY_ALLOWANCE_MS = 500;
  private static final int THREAD_JOIN_TIMEOUT_MS = 5000;

  @Test
  public void teardownCancelsOngoingRequests() throws Exception {
    testSubject.setup(underlyingDispatcher);
    DelayedRequest delayedRequest = setupCancellableDelayedRequest();
    Thread requestThread =
        new Thread(
            () ->
                assertThrows(
                    OperationCanceledException.class,
                    () -> testSubject.request("method", cancellationSignal)));
    requestThread.start();

    testSubject.teardown();
    delayedRequest.allowToFinishOrBeCancelled();
    requestThread.join(THREAD_JOIN_TIMEOUT_MS);
    assertFalse(requestThread.isAlive());
  }

  private Thread startOnNewThread(ThrowingRunnable callback) {
    return new Thread(
        () -> {
          try {
            callback.run();
          } catch (Throwable e) {
            fail(
                "unexpected exception from async request:\n\n"
                    + e.getMessage()
                    + "\n\n"
                    + Arrays.toString(e.getStackTrace()));
          }
        });
  }

  @Test
  public void requestWithoutSetupThrowsException() {
    assertThrows(
        "Service is not running",
        Exception.class,
        () -> testSubject.request("any method", cancellationSignal));
  }

  @Test
  public void requestWaitsForOutstandingRequestToFinish() throws Exception {
    testSubject.setup(underlyingDispatcher);

    DelayedRequest delayedRequest = setupCancellableDelayedRequest();
    Thread request1Thread =
        startOnNewThread(
            () -> testSubject.request("cancellable delayed method", cancellationSignal));
    request1Thread.start();

    delayedRequest.waitForDelayedState();

    setupImmediatelySuccessfulRequest();
    Thread request2Thread =
        startOnNewThread(
            () -> testSubject.request("immediately successful method", cancellationSignal));
    request2Thread.start();

    // request2 shouldn't run until request1 is allowed to finish
    request2Thread.join(THREAD_DELAY_ALLOWANCE_MS);
    assertTrue(request2Thread.isAlive());

    delayedRequest.allowToFinishOrBeCancelled();
    request1Thread.join(THREAD_JOIN_TIMEOUT_MS);
    assertFalse(request1Thread.isAlive());

    request2Thread.join(THREAD_JOIN_TIMEOUT_MS);
    assertFalse(request2Thread.isAlive());
  }

  @Test
  public void requestPropagatesResponseFromUnderlyingDispatcher() throws Exception {
    testSubject.setup(underlyingDispatcher);
    when(underlyingDispatcher.request(eq("method"), any()))
        .thenReturn("response from underlying dispatcher");

    String response = testSubject.request("method", cancellationSignal);

    assertEquals("response from underlying dispatcher", response);
  }

  @Test
  public void requestPropagatesExceptionFromUnderlyingDispatcher() throws Exception {
    testSubject.setup(underlyingDispatcher);
    when(underlyingDispatcher.request(eq("method"), any()))
        .thenThrow(new RuntimeException("exception from underlying dispatcher"));

    assertThrows(
        "exception from underlying dispatcher",
        RuntimeException.class,
        () -> testSubject.request("method", cancellationSignal));
  }

  @Test
  public void requestPropagatesCancellationToUnderlyingDispatcher() throws Exception {
    testSubject.setup(underlyingDispatcher);
    DelayedRequest delayedRequest = setupCancellableDelayedRequest();
    Thread requestThread =
        new Thread(
            () ->
                assertThrows(
                    OperationCanceledException.class,
                    () -> testSubject.request("method", cancellationSignal)));
    requestThread.start();

    cancellationSignal.cancel();
    delayedRequest.allowToFinishOrBeCancelled();
    requestThread.join(THREAD_JOIN_TIMEOUT_MS);
    assertFalse(requestThread.isAlive());
  }

  int delayedRequestPollIntervalMillis = 100;

  private enum DelayedRequestState {
    NotStarted,
    Delaying,
    Finishing,
    Finished
  }

  private class DelayedRequest {
    DelayedRequestState state = DelayedRequestState.NotStarted;

    public synchronized String request(CancellationSignal cancellationSignal)
        throws InterruptedException {
      state = DelayedRequestState.Delaying;
      while (state == DelayedRequestState.Delaying) {
        this.wait(delayedRequestPollIntervalMillis);
      }
      cancellationSignal.throwIfCanceled();
      state = DelayedRequestState.Finished;

      return "cancellable delayed method response";
    }

    public synchronized void allowToFinishOrBeCancelled() {
      state = DelayedRequestState.Finishing;
    }

    public synchronized void waitForDelayedState() throws InterruptedException {
      while (state == DelayedRequestState.NotStarted) {
        this.wait(delayedRequestPollIntervalMillis);
      }
      if (state != DelayedRequestState.Delaying) {
        throw new RuntimeException("State progressed further than expected");
      }
    }
  }

  // Returns a callback which finishes the delayed request
  private DelayedRequest setupCancellableDelayedRequest() throws Exception {
    DelayedRequest delayedRequest = new DelayedRequest();

    when(underlyingDispatcher.request(eq("cancellable delayed method"), any()))
        .thenAnswer(
            invocation -> {
              CancellationSignal signal = invocation.getArgument(1);
              return delayedRequest.request(signal);
            });

    return delayedRequest;
  }

  private void setupImmediatelySuccessfulRequest() throws Exception {
    when(underlyingDispatcher.request(eq("immediately successful method"), any()))
        .thenReturn("immediately successful method response");
  }

  private CancellationSignal makeBasicCancellationSignal() {
    CancellationSignal mockSignal = mock(CancellationSignal.class);
    AtomicBoolean isCancelled = new AtomicBoolean(false);
    AtomicReference<CancellationSignal.OnCancelListener> onCancelListener =
        new AtomicReference<>(null);

    doAnswer(
            invocation -> {
              synchronized (mockSignal) {
                isCancelled.set(true);
                CancellationSignal.OnCancelListener listener = onCancelListener.getAndSet(null);
                if (listener != null) {
                  listener.onCancel();
                }
              }
              return null;
            })
        .when(mockSignal)
        .cancel();

    doAnswer(
            invocation -> {
              synchronized (mockSignal) {
                if (isCancelled.get()) {
                  throw new OperationCanceledException();
                }
              }
              return null;
            })
        .when(mockSignal)
        .throwIfCanceled();

    doAnswer(
            invocation -> {
              CancellationSignal.OnCancelListener listener = invocation.getArgument(0);
              synchronized (mockSignal) {
                if (isCancelled.get()) {
                  listener.onCancel();
                } else {
                  onCancelListener.set(listener);
                }
              }
              return null;
            })
        .when(mockSignal)
        .setOnCancelListener(any());

    return mockSignal;
  }
}
