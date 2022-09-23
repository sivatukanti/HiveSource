// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.key;

import org.apache.kerby.kerberos.kerb.KrbException;

public interface KeyMaker
{
    byte[] str2key(final String p0, final String p1, final byte[] p2) throws KrbException;
    
    byte[] random2Key(final byte[] p0) throws KrbException;
}
