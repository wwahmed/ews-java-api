package com.devsite.exchange;

import java.net.URI;
import java.util.Date;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;




public class CertificateTest 
{
	public static final String INSECURE_URI1 = "https://office.sultrade.cz";
	public static final String INSECURE_URI2 = "https://webmail.cagsgp.com/owa"; 
	public static final String INSECURE_URI3 = "https://webmail.wika.co.in";
	
	private static String watermark = null;

	private static void doTest() throws Exception
	{
		  ExchangeService service = new DevSiteExchangeService(ExchangeVersion.Exchange2010_SP2);
          ExchangeCredentials credentials = new WebCredentials("wahmed@cagsgp.com", "password1");
          service.setCredentials(credentials);
          service.setUrl(new URI(INSECURE_URI2));
          
          
          runInboxTest(service);
	}

	public static void main(String [] args)
	{
		try
		{
			doTest();
		}
		catch(Exception e)
		{
			if(ThrowableUtils.isCause(sun.security.provider.certpath.SunCertPathBuilderException.class, e))
			{
				System.out.println("Invalid Certificate");
				e.printStackTrace();
			}
			else if(ThrowableUtils.isCause(java.security.cert.CertificateExpiredException.class, e))
			{
				System.out.println("Expired Certificate");
				e.printStackTrace();
			}
			else if(ThrowableUtils.isCause(javax.net.ssl.SSLPeerUnverifiedException.class, e))
			{
				System.out.println("Certificate Subjects don't match");
			}
			else if(ThrowableUtils.isCause( javax.net.ssl.SSLException.class, e) &&
					ThrowableUtils.doesExceptionMessageContains("hostname in certificate didn't match", e))
			{
				System.out.println("Host Name Mismatch");
			}
			else
			{
				e.printStackTrace();
			}
		}
	}

	private static final void log(String text)
	{
		System.out.println(new Date()+ " :: " + text);
	}
	

	
	private static void runInboxTest(ExchangeService service) throws Exception
	{
		Folder inbox = Folder.bind(service, WellKnownFolderName.Inbox);
		System.out.println("Inbox folder id is: " + inbox.getId());
	}

}

