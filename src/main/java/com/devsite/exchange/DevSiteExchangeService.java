package com.devsite.exchange;

import java.security.GeneralSecurityException;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;

import microsoft.exchange.webservices.data.EWSConstants;
import microsoft.exchange.webservices.data.core.EwsSSLProtocolSocketFactory;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;

public class DevSiteExchangeService extends ExchangeService
{

	public DevSiteExchangeService(ExchangeVersion version) 
	{
		super(version);
	}

	
	// From BaseExchangeService
	/**
	   * Create registry with configured {@link ConnectionSocketFactory} instances.
	   * Override this method to change how to work with different schemas.
	   *
	   * @return registry object
	   */
	  protected Registry<ConnectionSocketFactory> createConnectionSocketFactoryRegistry() {
	    try {
	      return RegistryBuilder.<ConnectionSocketFactory>create()
	        .register(EWSConstants.HTTP_SCHEME, new PlainConnectionSocketFactory())
	        .register(EWSConstants.HTTPS_SCHEME, DevSiteEwsSSLProtocolSocketFactory.build(null, NoopHostnameVerifier.INSTANCE))
	        .build();
	    } catch (GeneralSecurityException e) {
	      throw new RuntimeException(
	        "Could not initialize ConnectionSocketFactory instances for HttpClientConnectionManager", e
	      );
	    }
	  }
}
