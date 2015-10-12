package org.apache.http.impl.auth;

/**
 * NTLM is a proprietary authentication scheme developed by Microsoft and
 * optimized for Windows platforms.
 * 
 * Instead of the standard {@link NTLMEngineImpl} class, the
 * {@link NTLMEngineTSImpl} class is used. It provides a (semi) thread safe
 * {@link NTLMEngine#generateType1Msg(String, String)} method.
 *
 * @since 4.0
 * @see <a href="https://github.com/OfficeDev/ews-java-api/pull/428">https://github.com/OfficeDev/ews-java-api/pull/428</a>
 * @author christopher.klein[at]neos-it.de
 */
public class NTLMSchemeTS extends NTLMScheme {
	public NTLMSchemeTS(final NTLMEngine engine) {
		super(engine);
	}

	/**
	 * @since 4.3
	 */
	public NTLMSchemeTS() {
		// use semi thread safe instance
		this(new NTLMEngineTSImpl());
	}
}
