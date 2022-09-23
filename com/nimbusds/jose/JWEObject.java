// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

import java.text.ParseException;
import com.nimbusds.jose.util.Base64URL;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class JWEObject extends JOSEObject
{
    private static final long serialVersionUID = 1L;
    private JWEHeader header;
    private Base64URL encryptedKey;
    private Base64URL iv;
    private Base64URL cipherText;
    private Base64URL authTag;
    private State state;
    
    public JWEObject(final JWEHeader header, final Payload payload) {
        if (header == null) {
            throw new IllegalArgumentException("The JWE header must not be null");
        }
        this.header = header;
        if (payload == null) {
            throw new IllegalArgumentException("The payload must not be null");
        }
        this.setPayload(payload);
        this.encryptedKey = null;
        this.cipherText = null;
        this.state = State.UNENCRYPTED;
    }
    
    public JWEObject(final Base64URL firstPart, final Base64URL secondPart, final Base64URL thirdPart, final Base64URL fourthPart, final Base64URL fifthPart) throws ParseException {
        if (firstPart == null) {
            throw new IllegalArgumentException("The first part must not be null");
        }
        try {
            this.header = JWEHeader.parse(firstPart);
        }
        catch (ParseException e) {
            throw new ParseException("Invalid JWE header: " + e.getMessage(), 0);
        }
        if (secondPart == null || secondPart.toString().isEmpty()) {
            this.encryptedKey = null;
        }
        else {
            this.encryptedKey = secondPart;
        }
        if (thirdPart == null || thirdPart.toString().isEmpty()) {
            this.iv = null;
        }
        else {
            this.iv = thirdPart;
        }
        if (fourthPart == null) {
            throw new IllegalArgumentException("The fourth part must not be null");
        }
        this.cipherText = fourthPart;
        if (fifthPart == null || fifthPart.toString().isEmpty()) {
            this.authTag = null;
        }
        else {
            this.authTag = fifthPart;
        }
        this.state = State.ENCRYPTED;
        this.setParsedParts(firstPart, secondPart, thirdPart, fourthPart, fifthPart);
    }
    
    @Override
    public JWEHeader getHeader() {
        return this.header;
    }
    
    public Base64URL getEncryptedKey() {
        return this.encryptedKey;
    }
    
    public Base64URL getIV() {
        return this.iv;
    }
    
    public Base64URL getCipherText() {
        return this.cipherText;
    }
    
    public Base64URL getAuthTag() {
        return this.authTag;
    }
    
    public State getState() {
        return this.state;
    }
    
    private void ensureUnencryptedState() {
        if (this.state != State.UNENCRYPTED) {
            throw new IllegalStateException("The JWE object must be in an unencrypted state");
        }
    }
    
    private void ensureEncryptedState() {
        if (this.state != State.ENCRYPTED) {
            throw new IllegalStateException("The JWE object must be in an encrypted state");
        }
    }
    
    private void ensureEncryptedOrDecryptedState() {
        if (this.state != State.ENCRYPTED && this.state != State.DECRYPTED) {
            throw new IllegalStateException("The JWE object must be in an encrypted or decrypted state");
        }
    }
    
    private void ensureJWEEncrypterSupport(final JWEEncrypter encrypter) throws JOSEException {
        if (!encrypter.supportedJWEAlgorithms().contains(this.getHeader().getAlgorithm())) {
            throw new JOSEException("The \"" + this.getHeader().getAlgorithm() + "\" algorithm is not supported by the JWE encrypter: Supported algorithms: " + encrypter.supportedJWEAlgorithms());
        }
        if (!encrypter.supportedEncryptionMethods().contains(this.getHeader().getEncryptionMethod())) {
            throw new JOSEException("The \"" + this.getHeader().getEncryptionMethod() + "\" encryption method or key size is not supported by the JWE encrypter: Supported methods: " + encrypter.supportedEncryptionMethods());
        }
    }
    
    public synchronized void encrypt(final JWEEncrypter encrypter) throws JOSEException {
        this.ensureUnencryptedState();
        this.ensureJWEEncrypterSupport(encrypter);
        JWECryptoParts parts;
        try {
            parts = encrypter.encrypt(this.getHeader(), this.getPayload().toBytes());
        }
        catch (JOSEException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new JOSEException(e2.getMessage(), e2);
        }
        if (parts.getHeader() != null) {
            this.header = parts.getHeader();
        }
        this.encryptedKey = parts.getEncryptedKey();
        this.iv = parts.getInitializationVector();
        this.cipherText = parts.getCipherText();
        this.authTag = parts.getAuthenticationTag();
        this.state = State.ENCRYPTED;
    }
    
    public synchronized void decrypt(final JWEDecrypter decrypter) throws JOSEException {
        this.ensureEncryptedState();
        try {
            this.setPayload(new Payload(decrypter.decrypt(this.getHeader(), this.getEncryptedKey(), this.getIV(), this.getCipherText(), this.getAuthTag())));
        }
        catch (JOSEException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new JOSEException(e2.getMessage(), e2);
        }
        this.state = State.DECRYPTED;
    }
    
    @Override
    public String serialize() {
        this.ensureEncryptedOrDecryptedState();
        final StringBuilder sb = new StringBuilder(this.header.toBase64URL().toString());
        sb.append('.');
        if (this.encryptedKey != null) {
            sb.append(this.encryptedKey.toString());
        }
        sb.append('.');
        if (this.iv != null) {
            sb.append(this.iv.toString());
        }
        sb.append('.');
        sb.append(this.cipherText.toString());
        sb.append('.');
        if (this.authTag != null) {
            sb.append(this.authTag.toString());
        }
        return sb.toString();
    }
    
    public static JWEObject parse(final String s) throws ParseException {
        final Base64URL[] parts = JOSEObject.split(s);
        if (parts.length != 5) {
            throw new ParseException("Unexpected number of Base64URL parts, must be five", 0);
        }
        return new JWEObject(parts[0], parts[1], parts[2], parts[3], parts[4]);
    }
    
    public enum State
    {
        UNENCRYPTED("UNENCRYPTED", 0), 
        ENCRYPTED("ENCRYPTED", 1), 
        DECRYPTED("DECRYPTED", 2);
        
        private State(final String name, final int ordinal) {
        }
    }
}
