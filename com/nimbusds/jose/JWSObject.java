// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

import com.nimbusds.jose.util.StandardCharset;
import java.text.ParseException;
import com.nimbusds.jose.util.Base64URL;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class JWSObject extends JOSEObject
{
    private static final long serialVersionUID = 1L;
    private final JWSHeader header;
    private final String signingInputString;
    private Base64URL signature;
    private State state;
    
    public JWSObject(final JWSHeader header, final Payload payload) {
        if (header == null) {
            throw new IllegalArgumentException("The JWS header must not be null");
        }
        this.header = header;
        if (payload == null) {
            throw new IllegalArgumentException("The payload must not be null");
        }
        this.setPayload(payload);
        this.signingInputString = composeSigningInput(header.toBase64URL(), payload.toBase64URL());
        this.signature = null;
        this.state = State.UNSIGNED;
    }
    
    public JWSObject(final Base64URL firstPart, final Base64URL secondPart, final Base64URL thirdPart) throws ParseException {
        if (firstPart == null) {
            throw new IllegalArgumentException("The first part must not be null");
        }
        try {
            this.header = JWSHeader.parse(firstPart);
        }
        catch (ParseException e) {
            throw new ParseException("Invalid JWS header: " + e.getMessage(), 0);
        }
        if (secondPart == null) {
            throw new IllegalArgumentException("The second part must not be null");
        }
        this.setPayload(new Payload(secondPart));
        this.signingInputString = composeSigningInput(firstPart, secondPart);
        if (thirdPart == null) {
            throw new IllegalArgumentException("The third part must not be null");
        }
        this.signature = thirdPart;
        this.state = State.SIGNED;
        this.setParsedParts(firstPart, secondPart, thirdPart);
    }
    
    @Override
    public JWSHeader getHeader() {
        return this.header;
    }
    
    private static String composeSigningInput(final Base64URL firstPart, final Base64URL secondPart) {
        return String.valueOf(firstPart.toString()) + '.' + secondPart.toString();
    }
    
    public byte[] getSigningInput() {
        return this.signingInputString.getBytes(StandardCharset.UTF_8);
    }
    
    public Base64URL getSignature() {
        return this.signature;
    }
    
    public State getState() {
        return this.state;
    }
    
    private void ensureUnsignedState() {
        if (this.state != State.UNSIGNED) {
            throw new IllegalStateException("The JWS object must be in an unsigned state");
        }
    }
    
    private void ensureSignedOrVerifiedState() {
        if (this.state != State.SIGNED && this.state != State.VERIFIED) {
            throw new IllegalStateException("The JWS object must be in a signed or verified state");
        }
    }
    
    private void ensureJWSSignerSupport(final JWSSigner signer) throws JOSEException {
        if (!signer.supportedJWSAlgorithms().contains(this.getHeader().getAlgorithm())) {
            throw new JOSEException("The \"" + this.getHeader().getAlgorithm() + "\" algorithm is not allowed or supported by the JWS signer: Supported algorithms: " + signer.supportedJWSAlgorithms());
        }
    }
    
    public synchronized void sign(final JWSSigner signer) throws JOSEException {
        this.ensureUnsignedState();
        this.ensureJWSSignerSupport(signer);
        try {
            this.signature = signer.sign(this.getHeader(), this.getSigningInput());
        }
        catch (JOSEException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new JOSEException(e2.getMessage(), e2);
        }
        this.state = State.SIGNED;
    }
    
    public synchronized boolean verify(final JWSVerifier verifier) throws JOSEException {
        this.ensureSignedOrVerifiedState();
        boolean verified;
        try {
            verified = verifier.verify(this.getHeader(), this.getSigningInput(), this.getSignature());
        }
        catch (JOSEException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new JOSEException(e2.getMessage(), e2);
        }
        if (verified) {
            this.state = State.VERIFIED;
        }
        return verified;
    }
    
    @Override
    public String serialize() {
        this.ensureSignedOrVerifiedState();
        return String.valueOf(this.signingInputString) + '.' + this.signature.toString();
    }
    
    public static JWSObject parse(final String s) throws ParseException {
        final Base64URL[] parts = JOSEObject.split(s);
        if (parts.length != 3) {
            throw new ParseException("Unexpected number of Base64URL parts, must be three", 0);
        }
        return new JWSObject(parts[0], parts[1], parts[2]);
    }
    
    public enum State
    {
        UNSIGNED("UNSIGNED", 0), 
        SIGNED("SIGNED", 1), 
        VERIFIED("VERIFIED", 2);
        
        private State(final String name, final int ordinal) {
        }
    }
}
