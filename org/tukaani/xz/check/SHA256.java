// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz.check;

import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

public class SHA256 extends Check
{
    private MessageDigest sha256;
    
    public SHA256() throws NoSuchAlgorithmException {
        this.size = 32;
        this.name = "SHA-256";
        this.sha256 = MessageDigest.getInstance("SHA-256");
    }
    
    public void update(final byte[] input, final int offset, final int len) {
        this.sha256.update(input, offset, len);
    }
    
    public byte[] finish() {
        final byte[] digest = this.sha256.digest();
        this.sha256.reset();
        return digest;
    }
}
