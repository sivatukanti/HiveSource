// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb;

import java.nio.charset.StandardCharsets;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import java.io.InputStream;
import java.io.DataInputStream;

public abstract class KrbInputStream extends DataInputStream
{
    public KrbInputStream(final InputStream in) {
        super(in);
    }
    
    public KerberosTime readTime() throws IOException {
        final long value = this.readInt();
        final KerberosTime time = new KerberosTime(value * 1000L);
        return time;
    }
    
    public abstract PrincipalName readPrincipal(final int p0) throws IOException;
    
    public EncryptionKey readKey() throws IOException {
        final int eType = this.readShort();
        final EncryptionType encType = EncryptionType.fromValue(eType);
        final byte[] keyData = this.readCountedOctets();
        if (encType == EncryptionType.NONE || keyData == null) {
            return null;
        }
        final EncryptionKey key = new EncryptionKey(encType, keyData);
        return key;
    }
    
    public String readCountedString() throws IOException {
        final byte[] countedOctets = this.readCountedOctets();
        if (countedOctets != null) {
            return new String(countedOctets, StandardCharsets.UTF_8);
        }
        return null;
    }
    
    public byte[] readCountedOctets() throws IOException {
        final int len = this.readOctetsCount();
        if (len == 0) {
            return null;
        }
        if (len < 0 || len > this.available()) {
            throw new IOException("Unexpected octets len: " + len);
        }
        final byte[] data = new byte[len];
        this.readFully(data);
        return data;
    }
    
    public abstract int readOctetsCount() throws IOException;
}
