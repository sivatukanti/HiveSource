// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.enc;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import org.apache.kerby.kerberos.kerb.crypto.cksum.HashProvider;
import org.apache.kerby.kerberos.kerb.crypto.cksum.provider.Crc32Provider;

public class DesCbcCrcEnc extends DesCbcEnc
{
    public DesCbcCrcEnc() {
        super(new Crc32Provider());
    }
    
    @Override
    public EncryptionType eType() {
        return EncryptionType.DES_CBC_CRC;
    }
    
    @Override
    public CheckSumType checksumType() {
        return CheckSumType.CRC32;
    }
    
    @Override
    public byte[] encrypt(final byte[] data, final byte[] key, final int usage) throws KrbException {
        final byte[] iv = new byte[this.encProvider().blockSize()];
        System.arraycopy(key, 0, iv, 0, key.length);
        return this.encrypt(data, key, iv, usage);
    }
    
    @Override
    public byte[] decrypt(final byte[] cipher, final byte[] key, final int usage) throws KrbException {
        final byte[] iv = new byte[this.encProvider().blockSize()];
        System.arraycopy(key, 0, iv, 0, key.length);
        return this.decrypt(cipher, key, iv, usage);
    }
}
