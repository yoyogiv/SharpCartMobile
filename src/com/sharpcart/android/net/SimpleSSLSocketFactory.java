package com.sharpcart.android.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class SimpleSSLSocketFactory implements SocketFactory,
    LayeredSocketFactory {

  private SSLContext sslContext = null;

  private static SSLContext createSimpleSSLContext() throws IOException {
    try {
      final SSLContext context = SSLContext.getInstance("TLS");
      context.init(null,
          new TrustManager[] { new AcceptInvalidX509TrustManager() },
          null);
      return context;
    } catch (final Exception e) {
      throw new IOException(e.getMessage());
    }
  }

  private SSLContext getSSLContext() throws IOException {
    if (sslContext == null) {
      sslContext = createSimpleSSLContext();
    }
    return sslContext;
  }

  @Override
public Socket connectSocket(final Socket sock, final String host, final int port,
      final InetAddress localAddress, int localPort, final HttpParams params)
      throws IOException, UnknownHostException, ConnectTimeoutException {
    final int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
    final int soTimeout = HttpConnectionParams.getSoTimeout(params);

    final InetSocketAddress remoteAddress = new InetSocketAddress(host, port);
    final SSLSocket sslsock = (SSLSocket) ((sock != null) ? sock
        : createSocket());

    if ((localAddress != null) || (localPort > 0)) {
      if (localPort < 0) {
        localPort = 0;
      }
      final InetSocketAddress isa = new InetSocketAddress(localAddress,
          localPort);
      sslsock.bind(isa);
    }

    sslsock.connect(remoteAddress, connTimeout);
    sslsock.setSoTimeout(soTimeout);
    return sslsock;
  }

  @Override
public Socket createSocket() throws IOException {
    return getSSLContext().getSocketFactory().createSocket();
  }

  @Override
public boolean isSecure(final Socket socket)
      throws IllegalArgumentException {
    return true;
  }

  @Override
public Socket createSocket(final Socket socket, final String host, final int port,
      final boolean autoClose) throws IOException, UnknownHostException {
    return getSSLContext().getSocketFactory().createSocket(socket,
        host, port, autoClose);
  }
}
