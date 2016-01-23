package com.devsite.exchange;

import java.security.GeneralSecurityException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.ssl.SSLContexts;

import microsoft.exchange.webservices.data.core.EwsSSLProtocolSocketFactory;
import microsoft.exchange.webservices.data.core.EwsX509TrustManager;

public class DevSiteEwsSSLProtocolSocketFactory extends EwsSSLProtocolSocketFactory{

	public DevSiteEwsSSLProtocolSocketFactory(SSLContext context,
			HostnameVerifier hostnameVerifier) 
	{
		super(context, hostnameVerifier);
	}


	/**
	   * Create and configure SSL protocol socket factory using trust manager and hostname verifier.
	   *
	   * @param trustManager trust manager
	   * @param hostnameVerifier hostname verifier
	   * @return socket factory for SSL protocol
	   * @throws GeneralSecurityException on security error
	   */
	  public static EwsSSLProtocolSocketFactory build(
	    TrustManager trustManager, HostnameVerifier hostnameVerifier
	  ) throws GeneralSecurityException {
	    SSLContext sslContext = createSslContext(trustManager);
	    return new EwsSSLProtocolSocketFactory(sslContext, hostnameVerifier);
	  }
	  
	  /**
	   * Create SSL context and initialize it using specific trust manager.
	   *
	   * @param trustManager trust manager
	   * @return initialized SSL context
	   * @throws GeneralSecurityException on security error
	   */
	  public static SSLContext createSslContext(TrustManager trustManager)
	    throws GeneralSecurityException 
	  {
	    EwsX509TrustManager x509TrustManager = new DevSiteEwsX509TrustManager(null, trustManager);
	    SSLContext sslContext = SSLContexts.createDefault();
	    sslContext.init(
	      null,
	      new TrustManager[] { x509TrustManager },
	      null
	    );
	    return sslContext;
	  }


}
