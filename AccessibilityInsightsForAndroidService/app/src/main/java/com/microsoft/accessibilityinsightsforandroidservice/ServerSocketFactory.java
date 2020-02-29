// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.accessibilityinsightsforandroidservice;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class ServerSocketFactory {
  public ServerSocket createServerSocket(int serverPort) throws IOException {
    // Create a localhost-only server socket
    return new ServerSocket(serverPort, 0, InetAddress.getLoopbackAddress());
  }
}
