// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.cksum.provider;

import org.apache.kerby.kerberos.kerb.crypto.util.Md4;

public class Md4Provider extends MessageDigestHashProvider
{
    public Md4Provider() {
        super(16, 64, "MD4");
    }
    
    @Override
    protected void init() {
        this.messageDigest = new Md4();
    }
}
