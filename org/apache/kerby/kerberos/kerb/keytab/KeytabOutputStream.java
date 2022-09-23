// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.keytab;

import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import java.io.OutputStream;
import org.apache.kerby.kerberos.kerb.KrbOutputStream;

public class KeytabOutputStream extends KrbOutputStream
{
    public KeytabOutputStream(final OutputStream out) {
        super(out);
    }
    
    @Override
    public void writePrincipal(final PrincipalName principal, final int version) throws IOException {
        final List<String> nameStrings = principal.getNameStrings();
        final int numComponents = principal.getNameStrings().size();
        final String realm = principal.getRealm();
        this.writeShort(numComponents);
        this.writeCountedString(realm);
        for (final String nameCom : nameStrings) {
            this.writeCountedString(nameCom);
        }
        this.writeInt(principal.getNameType().getValue());
    }
    
    @Override
    public void writeKey(final EncryptionKey key, final int version) throws IOException {
        this.writeShort(key.getKeyType().getValue());
        this.writeCountedOctets(key.getKeyData());
    }
    
    @Override
    public void writeCountedOctets(final byte[] data) throws IOException {
        this.writeShort(data.length);
        this.write(data);
    }
}
