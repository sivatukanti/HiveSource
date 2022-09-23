// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.proc;

import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWSVerifier;
import java.util.ListIterator;
import java.util.List;
import java.security.Key;
import com.nimbusds.jose.PlainObject;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSObject;
import java.text.ParseException;
import com.nimbusds.jose.JOSEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.factories.DefaultJWEDecrypterFactory;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.nimbusds.jose.JOSEException;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class DefaultJOSEProcessor<C extends SecurityContext> implements ConfigurableJOSEProcessor<C>
{
    private static final BadJOSEException PLAIN_JOSE_REJECTED_EXCEPTION;
    private static final BadJOSEException NO_JWS_KEY_SELECTOR_EXCEPTION;
    private static final BadJOSEException NO_JWE_KEY_SELECTOR_EXCEPTION;
    private static final JOSEException NO_JWS_VERIFIER_FACTORY_EXCEPTION;
    private static final JOSEException NO_JWE_DECRYPTER_FACTORY_EXCEPTION;
    private static final BadJOSEException NO_JWS_KEY_CANDIDATES_EXCEPTION;
    private static final BadJOSEException NO_JWE_KEY_CANDIDATES_EXCEPTION;
    private static final BadJOSEException INVALID_SIGNATURE;
    private static final BadJOSEException NO_MATCHING_VERIFIERS_EXCEPTION;
    private static final BadJOSEException NO_MATCHING_DECRYPTERS_EXCEPTION;
    private JWSKeySelector<C> jwsKeySelector;
    private JWEKeySelector<C> jweKeySelector;
    private JWSVerifierFactory jwsVerifierFactory;
    private JWEDecrypterFactory jweDecrypterFactory;
    
    static {
        PLAIN_JOSE_REJECTED_EXCEPTION = new BadJOSEException("Unsecured (plain) JOSE objects are rejected, extend class to handle");
        NO_JWS_KEY_SELECTOR_EXCEPTION = new BadJOSEException("JWS object rejected: No JWS key selector is configured");
        NO_JWE_KEY_SELECTOR_EXCEPTION = new BadJOSEException("JWE object rejected: No JWE key selector is configured");
        NO_JWS_VERIFIER_FACTORY_EXCEPTION = new JOSEException("No JWS verifier is configured");
        NO_JWE_DECRYPTER_FACTORY_EXCEPTION = new JOSEException("No JWE decrypter is configured");
        NO_JWS_KEY_CANDIDATES_EXCEPTION = new BadJOSEException("JWS object rejected: Another algorithm expected, or no matching key(s) found");
        NO_JWE_KEY_CANDIDATES_EXCEPTION = new BadJOSEException("JWE object rejected: Another algorithm expected, or no matching key(s) found");
        INVALID_SIGNATURE = new BadJWSException("JWS object rejected: Invalid signature");
        NO_MATCHING_VERIFIERS_EXCEPTION = new BadJOSEException("JWS object rejected: No matching verifier(s) found");
        NO_MATCHING_DECRYPTERS_EXCEPTION = new BadJOSEException("JWE object rejected: No matching decrypter(s) found");
    }
    
    public DefaultJOSEProcessor() {
        this.jwsVerifierFactory = new DefaultJWSVerifierFactory();
        this.jweDecrypterFactory = new DefaultJWEDecrypterFactory();
    }
    
    @Override
    public JWSKeySelector<C> getJWSKeySelector() {
        return this.jwsKeySelector;
    }
    
    @Override
    public void setJWSKeySelector(final JWSKeySelector<C> jwsKeySelector) {
        this.jwsKeySelector = jwsKeySelector;
    }
    
    @Override
    public JWEKeySelector<C> getJWEKeySelector() {
        return this.jweKeySelector;
    }
    
    @Override
    public void setJWEKeySelector(final JWEKeySelector<C> jweKeySelector) {
        this.jweKeySelector = jweKeySelector;
    }
    
    @Override
    public JWSVerifierFactory getJWSVerifierFactory() {
        return this.jwsVerifierFactory;
    }
    
    @Override
    public void setJWSVerifierFactory(final JWSVerifierFactory factory) {
        this.jwsVerifierFactory = factory;
    }
    
    @Override
    public JWEDecrypterFactory getJWEDecrypterFactory() {
        return this.jweDecrypterFactory;
    }
    
    @Override
    public void setJWEDecrypterFactory(final JWEDecrypterFactory factory) {
        this.jweDecrypterFactory = factory;
    }
    
    @Override
    public Payload process(final String compactJOSE, final C context) throws ParseException, BadJOSEException, JOSEException {
        return this.process(JOSEObject.parse(compactJOSE), context);
    }
    
    @Override
    public Payload process(final JOSEObject joseObject, final C context) throws BadJOSEException, JOSEException {
        if (joseObject instanceof JWSObject) {
            return this.process((JWSObject)joseObject, context);
        }
        if (joseObject instanceof JWEObject) {
            return this.process((JWEObject)joseObject, context);
        }
        if (joseObject instanceof PlainObject) {
            return this.process((PlainObject)joseObject, context);
        }
        throw new JOSEException("Unexpected JOSE object type: " + joseObject.getClass());
    }
    
    @Override
    public Payload process(final PlainObject plainObject, final C context) throws BadJOSEException {
        throw DefaultJOSEProcessor.PLAIN_JOSE_REJECTED_EXCEPTION;
    }
    
    @Override
    public Payload process(final JWSObject jwsObject, final C context) throws BadJOSEException, JOSEException {
        if (this.getJWSKeySelector() == null) {
            throw DefaultJOSEProcessor.NO_JWS_KEY_SELECTOR_EXCEPTION;
        }
        if (this.getJWSVerifierFactory() == null) {
            throw DefaultJOSEProcessor.NO_JWS_VERIFIER_FACTORY_EXCEPTION;
        }
        final List<? extends Key> keyCandidates = this.getJWSKeySelector().selectJWSKeys(jwsObject.getHeader(), context);
        if (keyCandidates == null || keyCandidates.isEmpty()) {
            throw DefaultJOSEProcessor.NO_JWS_KEY_CANDIDATES_EXCEPTION;
        }
        final ListIterator<? extends Key> it = keyCandidates.listIterator();
        while (it.hasNext()) {
            final JWSVerifier verifier = this.getJWSVerifierFactory().createJWSVerifier(jwsObject.getHeader(), (Key)it.next());
            if (verifier == null) {
                continue;
            }
            final boolean validSignature = jwsObject.verify(verifier);
            if (validSignature) {
                return jwsObject.getPayload();
            }
            if (!it.hasNext()) {
                throw DefaultJOSEProcessor.INVALID_SIGNATURE;
            }
        }
        throw DefaultJOSEProcessor.NO_MATCHING_VERIFIERS_EXCEPTION;
    }
    
    @Override
    public Payload process(final JWEObject jweObject, final C context) throws BadJOSEException, JOSEException {
        if (this.getJWEKeySelector() == null) {
            throw DefaultJOSEProcessor.NO_JWE_KEY_SELECTOR_EXCEPTION;
        }
        if (this.getJWEDecrypterFactory() == null) {
            throw DefaultJOSEProcessor.NO_JWE_DECRYPTER_FACTORY_EXCEPTION;
        }
        final List<? extends Key> keyCandidates = this.getJWEKeySelector().selectJWEKeys(jweObject.getHeader(), context);
        if (keyCandidates == null || keyCandidates.isEmpty()) {
            throw DefaultJOSEProcessor.NO_JWE_KEY_CANDIDATES_EXCEPTION;
        }
        final ListIterator<? extends Key> it = keyCandidates.listIterator();
        while (it.hasNext()) {
            final JWEDecrypter decrypter = this.getJWEDecrypterFactory().createJWEDecrypter(jweObject.getHeader(), (Key)it.next());
            if (decrypter == null) {
                continue;
            }
            try {
                jweObject.decrypt(decrypter);
            }
            catch (JOSEException e) {
                if (it.hasNext()) {
                    continue;
                }
                throw new BadJWEException("JWE object rejected: " + e.getMessage(), e);
            }
            if (!"JWT".equalsIgnoreCase(jweObject.getHeader().getContentType())) {
                return jweObject.getPayload();
            }
            final JWSObject nestedJWS = jweObject.getPayload().toJWSObject();
            if (nestedJWS == null) {
                return jweObject.getPayload();
            }
            return this.process(nestedJWS, context);
        }
        throw DefaultJOSEProcessor.NO_MATCHING_DECRYPTERS_EXCEPTION;
    }
}
