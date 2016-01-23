package org.apache.http.impl.auth;

import org.apache.http.annotation.Immutable;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

/**
 * Instead of the default {@link NTLMSchemeFactory}, this factory creates a semi
 * thread safe instance of {@link NTLMSchemeTS}.
 * 
 * @author christopher.klein[at]neos-it.de
 * @see <a href="https://github.com/OfficeDev/ews-java-api/pull/428">https://github.com/OfficeDev/ews-java-api/pull/428</a>
 */
@Immutable
@SuppressWarnings("deprecation")
public class NTLMSchemeTSFactory implements AuthSchemeFactory, AuthSchemeProvider {

	@Override
	public AuthScheme newInstance(final HttpParams params) {
		return new NTLMSchemeTS();
	}

	@Override
	public AuthScheme create(final HttpContext context) {
		return new NTLMSchemeTS();
	}
}
