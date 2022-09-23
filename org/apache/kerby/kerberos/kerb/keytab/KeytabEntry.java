// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.keytab;

import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;

public class KeytabEntry
{
    private PrincipalName principal;
    private KerberosTime timestamp;
    private int kvno;
    private EncryptionKey key;
    
    public KeytabEntry(final PrincipalName principal, final KerberosTime timestamp, final int kvno, final EncryptionKey key) {
        this.principal = principal;
        this.timestamp = timestamp;
        this.kvno = kvno;
        this.key = key;
    }
    
    public KeytabEntry() {
    }
    
    void load(final KeytabInputStream kis, final int version, final int entrySize) throws IOException {
        final int bytesLeft = kis.available();
        this.principal = kis.readPrincipal(version);
        this.timestamp = kis.readTime();
        this.kvno = kis.readByte();
        this.key = kis.readKey();
        final int entryBytesRead = bytesLeft - kis.available();
        if (entryBytesRead + 4 <= entrySize) {
            final int tmp = kis.readInt();
            if (tmp != 0) {
                this.kvno = tmp;
            }
        }
        else if (entryBytesRead != entrySize) {
            throw new IOException(String.format("Bad input stream with less data read [%d] than expected [%d] for keytab entry.", entryBytesRead, entrySize));
        }
    }
    
    void store(final KeytabOutputStream kos) throws IOException {
        byte[] body = null;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final KeytabOutputStream subKos = new KeytabOutputStream(baos);
        this.writeBody(subKos, 0);
        subKos.flush();
        body = baos.toByteArray();
        kos.writeInt(body.length);
        kos.write(body);
    }
    
    public EncryptionKey getKey() {
        return this.key;
    }
    
    public int getKvno() {
        return this.kvno;
    }
    
    public PrincipalName getPrincipal() {
        return this.principal;
    }
    
    public KerberosTime getTimestamp() {
        return this.timestamp;
    }
    
    void writeBody(final KeytabOutputStream kos, final int version) throws IOException {
        kos.writePrincipal(this.principal, version);
        kos.writeTime(this.timestamp);
        kos.writeByte(this.kvno);
        kos.writeKey(this.key, version);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final KeytabEntry that = (KeytabEntry)o;
        return this.kvno == that.kvno && this.key.equals(that.key) && this.principal.equals(that.principal) && this.timestamp.equals(that.timestamp);
    }
    
    @Override
    public int hashCode() {
        int result = this.principal.hashCode();
        result = 31 * result + this.timestamp.hashCode();
        result = 31 * result + this.kvno;
        result = 31 * result + this.key.hashCode();
        return result;
    }
}
