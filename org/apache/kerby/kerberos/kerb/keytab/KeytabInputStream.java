// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.keytab;

import java.util.List;
import org.apache.kerby.kerberos.kerb.type.base.NameType;
import java.util.ArrayList;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import java.io.InputStream;
import org.apache.kerby.kerberos.kerb.KrbInputStream;

public class KeytabInputStream extends KrbInputStream
{
    public KeytabInputStream(final InputStream in) {
        super(in);
    }
    
    @Override
    public KerberosTime readTime() throws IOException {
        final long value = this.readInt();
        final KerberosTime time = new KerberosTime(value * 1000L);
        return time;
    }
    
    @Override
    public PrincipalName readPrincipal(final int version) throws IOException {
        int numComponents = this.readShort();
        if (version == 1281) {
            --numComponents;
        }
        final String realm = this.readCountedString();
        final List<String> nameStrings = new ArrayList<String>();
        for (int i = 0; i < numComponents; ++i) {
            final String component = this.readCountedString();
            nameStrings.add(component);
        }
        final int type = this.readInt();
        final NameType nameType = NameType.fromValue(type);
        final PrincipalName principal = new PrincipalName(nameStrings, nameType);
        principal.setRealm(realm);
        return principal;
    }
    
    @Override
    public int readOctetsCount() throws IOException {
        return this.readShort();
    }
}
