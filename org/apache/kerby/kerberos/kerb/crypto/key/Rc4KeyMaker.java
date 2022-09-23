// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.key;

import org.apache.kerby.kerberos.kerb.KrbException;
import java.security.MessageDigest;
import org.apache.kerby.kerberos.kerb.crypto.util.Md4;
import java.nio.charset.StandardCharsets;
import org.apache.kerby.kerberos.kerb.crypto.enc.EncryptProvider;

public class Rc4KeyMaker extends AbstractKeyMaker
{
    public Rc4KeyMaker(final EncryptProvider encProvider) {
        super(encProvider);
    }
    
    @Override
    public byte[] str2key(final String string, final String salt, final byte[] param) throws KrbException {
        if (param != null && param.length > 0) {
            throw new RuntimeException("Invalid param to str2Key");
        }
        final byte[] passwd = string.getBytes(StandardCharsets.UTF_16LE);
        final MessageDigest md = new Md4();
        md.update(passwd);
        return md.digest();
    }
    
    @Override
    public byte[] random2Key(final byte[] randomBits) throws KrbException {
        if (randomBits.length != this.encProvider().keyInputSize()) {
            throw new KrbException("Invalid random bits, not of correct bytes size");
        }
        return randomBits;
    }
}
