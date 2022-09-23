// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import java.io.OutputStream;
import java.io.DataOutputStream;

public abstract class KrbOutputStream extends DataOutputStream
{
    public KrbOutputStream(final OutputStream out) {
        super(out);
    }
    
    public abstract void writePrincipal(final PrincipalName p0, final int p1) throws IOException;
    
    public void writeRealm(final String realm) throws IOException {
        this.writeCountedString(realm);
    }
    
    public abstract void writeKey(final EncryptionKey p0, final int p1) throws IOException;
    
    public void writeTime(final KerberosTime ktime) throws IOException {
        int time = 0;
        if (ktime != null) {
            time = (int)(ktime.getValue().getTime() / 1000L);
        }
        this.writeInt(time);
    }
    
    public void writeCountedString(final String string) throws IOException {
        final byte[] data = (byte[])((string != null) ? string.getBytes(StandardCharsets.UTF_8) : null);
        this.writeCountedOctets(data);
    }
    
    public void writeCountedOctets(final byte[] data) throws IOException {
        if (data != null) {
            this.writeInt(data.length);
            this.write(data);
        }
        else {
            this.writeInt(0);
        }
    }
}
