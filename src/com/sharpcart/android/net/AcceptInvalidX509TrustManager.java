
package com.sharpcart.android.net;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class AcceptInvalidX509TrustManager implements X509TrustManager {

  /**
   * @see javax.net.ssl.X509TrustManager#checkClientTrusted(X509Certificate[],
   *      String authType)
   */
  @Override
public void checkClientTrusted(final X509Certificate[] certificates,
      final String authType) throws CertificateException {

  }

  /**
   * @see javax.net.ssl.X509TrustManager#checkServerTrusted(X509Certificate[],
   *      String authType)
   */
  @Override
public void checkServerTrusted(final X509Certificate[] certificates,
      final String authType) throws CertificateException {

  }

  /**
   * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
   */
  @Override
public X509Certificate[] getAcceptedIssuers() {
    return null;
  }

}
