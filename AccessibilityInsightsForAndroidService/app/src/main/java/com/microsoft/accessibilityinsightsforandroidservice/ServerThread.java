// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import java.io.IOException;
import java.net.Socket;

public class ServerThread extends Thread {

  static final int ServerPort = 62442; // MAGIC on phone keypad

  static java.net.ServerSocket ServerSocket = null;

  private static final String TAG = "ServerThread";

  private boolean acceptRequests = true;
  private final ServerSocketFactory serverSocketFactory;
  private final ResponseThreadFactory responseThreadFactory;

  public ServerThread(
      ServerSocketFactory serverSocketFactory, ResponseThreadFactory responseThreadFactory) {
    this.serverSocketFactory = serverSocketFactory;
    this.responseThreadFactory = responseThreadFactory;
  }

  @Override
  public void run() {
    try {
      if (ServerSocket != null) {
        ServerSocket.close();
      }

      setServerSocket(serverSocketFactory.createServerSocket(ServerPort));

      while (acceptRequests) {
        Socket socket = ServerSocket.accept();

        ResponseThread responseThread = responseThreadFactory.createResponseThread(socket);
        responseThread.start();
        responseThread.join();
      }
    } catch (IOException e) {
      logExceptionIfRunning(e);
    } catch (InterruptedException e) {
      logExceptionIfRunning(e);
    }
  }

  private static void setServerSocket(java.net.ServerSocket socket) {
    ServerSocket = socket;
  }

  public void exit() {
    acceptRequests = false;
    try {
      ServerSocket.close();
    } catch (IOException e) {
      Logger.logError(TAG, StackTrace.getStackTrace(e));
    }
  }

  private void logExceptionIfRunning(Exception e) {
    if (acceptRequests) {
      Logger.logError(TAG, StackTrace.getStackTrace(e));
    }
  }
}
