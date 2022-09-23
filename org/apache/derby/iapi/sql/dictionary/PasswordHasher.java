// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.io.UnsupportedEncodingException;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.util.StringUtil;

public class PasswordHasher
{
    private static final String ENCODING = "UTF-8";
    public static final String ID_PATTERN_SHA1_SCHEME = "3b60";
    public static final String ID_PATTERN_CONFIGURABLE_HASH_SCHEME = "3b61";
    public static final String ID_PATTERN_CONFIGURABLE_STRETCHED_SCHEME = "3b62";
    private static final char SEPARATOR_CHAR = ':';
    private String _messageDigestAlgorithm;
    private byte[] _salt;
    private int _iterations;
    
    public PasswordHasher(final String messageDigestAlgorithm, final byte[] salt, final int iterations) {
        this._messageDigestAlgorithm = messageDigestAlgorithm;
        this._salt = salt;
        this._iterations = iterations;
    }
    
    public PasswordHasher(final String s) {
        if (s.startsWith("3b61")) {
            this._messageDigestAlgorithm = s.substring(s.indexOf(58) + 1);
            this._salt = null;
            this._iterations = 1;
        }
        else if (s.startsWith("3b62")) {
            final int fromIndex = s.indexOf(58) + 1;
            final int n = s.indexOf(58, fromIndex) + 1;
            final int beginIndex = s.indexOf(58, n) + 1;
            this._salt = StringUtil.fromHexString(s, fromIndex, n - fromIndex - 1);
            this._iterations = Integer.parseInt(s.substring(n, beginIndex - 1));
            this._messageDigestAlgorithm = s.substring(beginIndex);
        }
    }
    
    public String hashPasswordIntoString(final String s, final String s2) throws StandardException {
        if (s2 == null) {
            return null;
        }
        byte[] bytes;
        byte[] bytes2;
        try {
            bytes = s.getBytes("UTF-8");
            bytes2 = s2.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException ex) {
            throw StandardException.plainWrapException(ex);
        }
        final MessageDigest emptyMessageDigest = this.getEmptyMessageDigest();
        byte[] digest = null;
        for (int i = 0; i < this._iterations; ++i) {
            emptyMessageDigest.reset();
            if (digest != null) {
                emptyMessageDigest.update(digest);
            }
            emptyMessageDigest.update(bytes);
            emptyMessageDigest.update(bytes2);
            if (this._salt != null) {
                emptyMessageDigest.update(this._salt);
            }
            digest = emptyMessageDigest.digest();
        }
        return StringUtil.toHexString(digest, 0, digest.length);
    }
    
    private MessageDigest getEmptyMessageDigest() throws StandardException {
        if (this._messageDigestAlgorithm == null) {
            throw this.badMessageDigest(null);
        }
        try {
            return MessageDigest.getInstance(this._messageDigestAlgorithm);
        }
        catch (NoSuchAlgorithmException ex) {
            throw this.badMessageDigest(ex);
        }
    }
    
    private StandardException badMessageDigest(final Throwable t) {
        return StandardException.newException("XBCXW.S", t, (this._messageDigestAlgorithm == null) ? "NULL" : this._messageDigestAlgorithm);
    }
    
    public String encodeHashingScheme() {
        return this.hashAndEncode("");
    }
    
    public String hashAndEncode(final String s, final String s2) throws StandardException {
        return this.hashAndEncode(this.hashPasswordIntoString(s, s2));
    }
    
    private String hashAndEncode(final String s) {
        if ((this._salt == null || this._salt.length == 0) && this._iterations == 1) {
            return "3b61" + s + ':' + this._messageDigestAlgorithm;
        }
        return "3b62" + s + ':' + StringUtil.toHexString(this._salt, 0, this._salt.length) + ':' + this._iterations + ':' + this._messageDigestAlgorithm;
    }
}
