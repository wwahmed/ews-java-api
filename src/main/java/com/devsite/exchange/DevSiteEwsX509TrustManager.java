package com.devsite.exchange;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;

import microsoft.exchange.webservices.data.core.EwsX509TrustManager;

public class DevSiteEwsX509TrustManager extends EwsX509TrustManager 
{

	public DevSiteEwsX509TrustManager(KeyStore keystore, TrustManager trustManager)
			throws NoSuchAlgorithmException, KeyStoreException 
	{
		super(keystore, trustManager);
	}
	
	public void checkServerTrusted(X509Certificate[] certificates, String authType)
		      throws CertificateException 
	{
		// Accept all server certificates
		return; 
		  
	}


}
