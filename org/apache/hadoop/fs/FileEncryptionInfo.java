// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.apache.commons.codec.binary.Hex;
import com.google.common.base.Preconditions;
import org.apache.hadoop.crypto.CryptoProtocolVersion;
import org.apache.hadoop.crypto.CipherSuite;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Serializable;

@InterfaceAudience.Private
public class FileEncryptionInfo implements Serializable
{
    private static final long serialVersionUID = 359316995L;
    private final CipherSuite cipherSuite;
    private final CryptoProtocolVersion version;
    private final byte[] edek;
    private final byte[] iv;
    private final String keyName;
    private final String ezKeyVersionName;
    
    public FileEncryptionInfo(final CipherSuite suite, final CryptoProtocolVersion version, final byte[] edek, final byte[] iv, final String keyName, final String ezKeyVersionName) {
        Preconditions.checkNotNull(suite);
        Preconditions.checkNotNull(version);
        Preconditions.checkNotNull(edek);
        Preconditions.checkNotNull(iv);
        Preconditions.checkNotNull(keyName);
        Preconditions.checkNotNull(ezKeyVersionName);
        Preconditions.checkArgument(iv.length == suite.getAlgorithmBlockSize(), (Object)"Unexpected IV length");
        this.cipherSuite = suite;
        this.version = version;
        this.edek = edek;
        this.iv = iv;
        this.keyName = keyName;
        this.ezKeyVersionName = ezKeyVersionName;
    }
    
    public CipherSuite getCipherSuite() {
        return this.cipherSuite;
    }
    
    public CryptoProtocolVersion getCryptoProtocolVersion() {
        return this.version;
    }
    
    public byte[] getEncryptedDataEncryptionKey() {
        return this.edek;
    }
    
    public byte[] getIV() {
        return this.iv;
    }
    
    public String getKeyName() {
        return this.keyName;
    }
    
    public String getEzKeyVersionName() {
        return this.ezKeyVersionName;
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("{");
        builder.append("cipherSuite: " + this.cipherSuite);
        builder.append(", cryptoProtocolVersion: " + this.version);
        builder.append(", edek: " + Hex.encodeHexString(this.edek));
        builder.append(", iv: " + Hex.encodeHexString(this.iv));
        builder.append(", keyName: " + this.keyName);
        builder.append(", ezKeyVersionName: " + this.ezKeyVersionName);
        builder.append("}");
        return builder.toString();
    }
    
    public String toStringStable() {
        final StringBuilder builder = new StringBuilder("{");
        builder.append("cipherSuite: " + this.cipherSuite);
        builder.append(", cryptoProtocolVersion: " + this.version);
        builder.append(", edek: " + Hex.encodeHexString(this.edek));
        builder.append(", iv: " + Hex.encodeHexString(this.iv));
        builder.append(", keyName: " + this.keyName);
        builder.append(", ezKeyVersionName: " + this.ezKeyVersionName);
        builder.append("}");
        return builder.toString();
    }
}
