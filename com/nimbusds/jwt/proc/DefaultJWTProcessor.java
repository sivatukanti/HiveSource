// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jwt.proc;

import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.proc.BadJWEException;
import com.nimbusds.jose.JWSVerifier;
import java.util.ListIterator;
import java.util.List;
import java.security.Key;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.JWTParser;
import java.text.ParseException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jose.crypto.factories.DefaultJWEDecrypterFactory;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.nimbusds.jose.proc.BadJWSException;
import com.nimbusds.jose.proc.JWEDecrypterFactory;
import com.nimbusds.jose.proc.JWSVerifierFactory;
import com.nimbusds.jose.proc.JWEKeySelector;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;

public class DefaultJWTProcessor<C extends SecurityContext> implements ConfigurableJWTProcessor<C>
{
    private static final BadJOSEException PLAIN_JWT_REJECTED_EXCEPTION;
    private static final BadJOSEException NO_JWS_KEY_SELECTOR_EXCEPTION;
    private static final BadJOSEException NO_JWE_KEY_SELECTOR_EXCEPTION;
    private static final JOSEException NO_JWS_VERIFIER_FACTORY_EXCEPTION;
    private static final JOSEException NO_JWE_DECRYPTER_FACTORY_EXCEPTION;
    private static final BadJOSEException NO_JWS_KEY_CANDIDATES_EXCEPTION;
    private static final BadJOSEException NO_JWE_KEY_CANDIDATES_EXCEPTION;
    private static final BadJOSEException INVALID_SIGNATURE;
    private static final BadJWTException INVALID_NESTED_JWT_EXCEPTION;
    private static final BadJOSEException NO_MATCHING_VERIFIERS_EXCEPTION;
    private static final BadJOSEException NO_MATCHING_DECRYPTERS_EXCEPTION;
    private JWSKeySelector<C> jwsKeySelector;
    private JWEKeySelector<C> jweKeySelector;
    private JWSVerifierFactory jwsVerifierFactory;
    private JWEDecrypterFactory jweDecrypterFactory;
    private JWTClaimsSetVerifier<C> claimsVerifier;
    private JWTClaimsVerifier deprecatedClaimsVerifier;
    
    static {
        PLAIN_JWT_REJECTED_EXCEPTION = new BadJOSEException("Unsecured (plain) JWTs are rejected, extend class to handle");
        NO_JWS_KEY_SELECTOR_EXCEPTION = new BadJOSEException("Signed JWT rejected: No JWS key selector is configured");
        NO_JWE_KEY_SELECTOR_EXCEPTION = new BadJOSEException("Encrypted JWT rejected: No JWE key selector is configured");
        NO_JWS_VERIFIER_FACTORY_EXCEPTION = new JOSEException("No JWS verifier is configured");
        NO_JWE_DECRYPTER_FACTORY_EXCEPTION = new JOSEException("No JWE decrypter is configured");
        NO_JWS_KEY_CANDIDATES_EXCEPTION = new BadJOSEException("Signed JWT rejected: Another algorithm expected, or no matching key(s) found");
        NO_JWE_KEY_CANDIDATES_EXCEPTION = new BadJOSEException("Encrypted JWT rejected: Another algorithm expected, or no matching key(s) found");
        INVALID_SIGNATURE = new BadJWSException("Signed JWT rejected: Invalid signature");
        INVALID_NESTED_JWT_EXCEPTION = new BadJWTException("The payload is not a nested signed JWT");
        NO_MATCHING_VERIFIERS_EXCEPTION = new BadJOSEException("JWS object rejected: No matching verifier(s) found");
        NO_MATCHING_DECRYPTERS_EXCEPTION = new BadJOSEException("Encrypted JWT rejected: No matching decrypter(s) found");
    }
    
    public DefaultJWTProcessor() {
        this.jwsVerifierFactory = new DefaultJWSVerifierFactory();
        this.jweDecrypterFactory = new DefaultJWEDecrypterFactory();
        this.claimsVerifier = new DefaultJWTClaimsVerifier<C>();
        this.deprecatedClaimsVerifier = null;
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
    public JWTClaimsSetVerifier<C> getJWTClaimsSetVerifier() {
        return this.claimsVerifier;
    }
    
    @Override
    public void setJWTClaimsSetVerifier(final JWTClaimsSetVerifier<C> claimsVerifier) {
        this.claimsVerifier = claimsVerifier;
        this.deprecatedClaimsVerifier = null;
    }
    
    @Deprecated
    @Override
    public JWTClaimsVerifier getJWTClaimsVerifier() {
        return this.deprecatedClaimsVerifier;
    }
    
    @Deprecated
    @Override
    public void setJWTClaimsVerifier(final JWTClaimsVerifier claimsVerifier) {
        this.claimsVerifier = null;
        this.deprecatedClaimsVerifier = claimsVerifier;
    }
    
    private JWTClaimsSet verifyAndReturnClaims(final JWT jwt, final C context) throws BadJWTException {
        JWTClaimsSet claimsSet;
        try {
            claimsSet = jwt.getJWTClaimsSet();
        }
        catch (ParseException e) {
            throw new BadJWTException(e.getMessage(), e);
        }
        if (this.getJWTClaimsSetVerifier() != null) {
            this.getJWTClaimsSetVerifier().verify(claimsSet, context);
        }
        else if (this.getJWTClaimsVerifier() != null) {
            this.getJWTClaimsVerifier().verify(claimsSet);
        }
        return claimsSet;
    }
    
    @Override
    public JWTClaimsSet process(final String jwtString, final C context) throws ParseException, BadJOSEException, JOSEException {
        return this.process(JWTParser.parse(jwtString), context);
    }
    
    @Override
    public JWTClaimsSet process(final JWT jwt, final C context) throws BadJOSEException, JOSEException {
        if (jwt instanceof SignedJWT) {
            return this.process((SignedJWT)jwt, context);
        }
        if (jwt instanceof EncryptedJWT) {
            return this.process((EncryptedJWT)jwt, context);
        }
        if (jwt instanceof PlainJWT) {
            return this.process((PlainJWT)jwt, context);
        }
        throw new JOSEException("Unexpected JWT object type: " + jwt.getClass());
    }
    
    @Override
    public JWTClaimsSet process(final PlainJWT plainJWT, final C context) throws BadJOSEException, JOSEException {
        this.verifyAndReturnClaims(plainJWT, context);
        throw DefaultJWTProcessor.PLAIN_JWT_REJECTED_EXCEPTION;
    }
    
    @Override
    public JWTClaimsSet process(final SignedJWT signedJWT, final C context) throws BadJOSEException, JOSEException {
        if (this.getJWSKeySelector() == null) {
            throw DefaultJWTProcessor.NO_JWS_KEY_SELECTOR_EXCEPTION;
        }
        if (this.getJWSVerifierFactory() == null) {
            throw DefaultJWTProcessor.NO_JWS_VERIFIER_FACTORY_EXCEPTION;
        }
        final List<? extends Key> keyCandidates = this.getJWSKeySelector().selectJWSKeys(signedJWT.getHeader(), context);
        if (keyCandidates == null || keyCandidates.isEmpty()) {
            throw DefaultJWTProcessor.NO_JWS_KEY_CANDIDATES_EXCEPTION;
        }
        final ListIterator<? extends Key> it = keyCandidates.listIterator();
        while (it.hasNext()) {
            final JWSVerifier verifier = this.getJWSVerifierFactory().createJWSVerifier(signedJWT.getHeader(), (Key)it.next());
            if (verifier == null) {
                continue;
            }
            final boolean validSignature = signedJWT.verify(verifier);
            if (validSignature) {
                return this.verifyAndReturnClaims(signedJWT, context);
            }
            if (!it.hasNext()) {
                throw DefaultJWTProcessor.INVALID_SIGNATURE;
            }
        }
        throw DefaultJWTProcessor.NO_MATCHING_VERIFIERS_EXCEPTION;
    }
    
    @Override
    public JWTClaimsSet process(final EncryptedJWT encryptedJWT, final C context) throws BadJOSEException, JOSEException {
        if (this.getJWEKeySelector() == null) {
            throw DefaultJWTProcessor.NO_JWE_KEY_SELECTOR_EXCEPTION;
        }
        if (this.getJWEDecrypterFactory() == null) {
            throw DefaultJWTProcessor.NO_JWE_DECRYPTER_FACTORY_EXCEPTION;
        }
        final List<? extends Key> keyCandidates = this.getJWEKeySelector().selectJWEKeys(encryptedJWT.getHeader(), context);
        if (keyCandidates == null || keyCandidates.isEmpty()) {
            throw DefaultJWTProcessor.NO_JWE_KEY_CANDIDATES_EXCEPTION;
        }
        final ListIterator<? extends Key> it = keyCandidates.listIterator();
        while (it.hasNext()) {
            final JWEDecrypter decrypter = this.getJWEDecrypterFactory().createJWEDecrypter(encryptedJWT.getHeader(), (Key)it.next());
            if (decrypter == null) {
                continue;
            }
            try {
                encryptedJWT.decrypt(decrypter);
            }
            catch (JOSEException e) {
                if (it.hasNext()) {
                    continue;
                }
                throw new BadJWEException("Encrypted JWT rejected: " + e.getMessage(), e);
            }
            if (!"JWT".equalsIgnoreCase(encryptedJWT.getHeader().getContentType())) {
                return this.verifyAndReturnClaims(encryptedJWT, context);
            }
            final SignedJWT signedJWTPayload = encryptedJWT.getPayload().toSignedJWT();
            if (signedJWTPayload == null) {
                throw DefaultJWTProcessor.INVALID_NESTED_JWT_EXCEPTION;
            }
            return this.process(signedJWTPayload, context);
        }
        throw DefaultJWTProcessor.NO_MATCHING_DECRYPTERS_EXCEPTION;
    }
}
