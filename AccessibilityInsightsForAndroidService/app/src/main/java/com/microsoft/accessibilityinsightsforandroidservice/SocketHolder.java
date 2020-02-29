// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import java.io.IOException;
import java.net.Socket;

public class SocketHolder {

  private Socket socket;

  public SocketHolder(Socket socket) {
    this.socket = socket;
  }

  public void close(String logTag) {
    try {
      if (socket != null) {
        socket.close();
        socket = null;
      }
    } catch (IOException e) {
      Logger.logVerbose(logTag, e.toString());
    }
  }
}
