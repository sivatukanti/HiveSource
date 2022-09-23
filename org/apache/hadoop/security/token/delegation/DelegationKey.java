// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token.delegation;

import java.util.Arrays;
import java.io.DataInput;
import java.io.IOException;
import org.apache.hadoop.io.WritableUtils;
import java.io.DataOutput;
import javax.crypto.SecretKey;
import org.apache.avro.reflect.Nullable;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.io.Writable;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class DelegationKey implements Writable
{
    private int keyId;
    private long expiryDate;
    @Nullable
    private byte[] keyBytes;
    private static final int MAX_KEY_LEN = 1048576;
    
    public DelegationKey() {
        this(0, 0L, (SecretKey)null);
    }
    
    public DelegationKey(final int keyId, final long expiryDate, final SecretKey key) {
        this(keyId, expiryDate, (byte[])((key != null) ? key.getEncoded() : null));
    }
    
    public DelegationKey(final int keyId, final long expiryDate, final byte[] encodedKey) {
        this.keyBytes = null;
        this.keyId = keyId;
        this.expiryDate = expiryDate;
        if (encodedKey != null) {
            if (encodedKey.length > 1048576) {
                throw new RuntimeException("can't create " + encodedKey.length + " byte long DelegationKey.");
            }
            this.keyBytes = encodedKey;
        }
    }
    
    public int getKeyId() {
        return this.keyId;
    }
    
    public long getExpiryDate() {
        return this.expiryDate;
    }
    
    public SecretKey getKey() {
        if (this.keyBytes == null || this.keyBytes.length == 0) {
            return null;
        }
        final SecretKey key = AbstractDelegationTokenSecretManager.createSecretKey(this.keyBytes);
        return key;
    }
    
    public byte[] getEncodedKey() {
        return this.keyBytes;
    }
    
    public void setExpiryDate(final long expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        WritableUtils.writeVInt(out, this.keyId);
        WritableUtils.writeVLong(out, this.expiryDate);
        if (this.keyBytes == null) {
            WritableUtils.writeVInt(out, -1);
        }
        else {
            WritableUtils.writeVInt(out, this.keyBytes.length);
            out.write(this.keyBytes);
        }
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.keyId = WritableUtils.readVInt(in);
        this.expiryDate = WritableUtils.readVLong(in);
        final int len = WritableUtils.readVIntInRange(in, -1, 1048576);
        if (len == -1) {
            this.keyBytes = null;
        }
        else {
            in.readFully(this.keyBytes = new byte[len]);
        }
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (int)(this.expiryDate ^ this.expiryDate >>> 32);
        result = 31 * result + Arrays.hashCode(this.keyBytes);
        result = 31 * result + this.keyId;
        return result;
    }
    
    @Override
    public boolean equals(final Object right) {
        if (this == right) {
            return true;
        }
        if (right == null || this.getClass() != right.getClass()) {
            return false;
        }
        final DelegationKey r = (DelegationKey)right;
        return this.keyId == r.keyId && this.expiryDate == r.expiryDate && Arrays.equals(this.keyBytes, r.keyBytes);
    }
}
