// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.cksum.provider;

public class Sha1Provider extends MessageDigestHashProvider
{
    public Sha1Provider() {
        super(20, 64, "SHA1");
    }
}
