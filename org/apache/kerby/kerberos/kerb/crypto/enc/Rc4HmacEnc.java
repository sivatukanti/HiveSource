// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.enc;

import org.apache.kerby.kerberos.kerb.KrbErrorCode;
import org.apache.kerby.kerberos.kerb.crypto.AbstractCryptoTypeHandler;
import org.apache.kerby.kerberos.kerb.crypto.util.BytesUtil;
import org.apache.kerby.kerberos.kerb.crypto.util.Rc4;
import org.apache.kerby.kerberos.kerb.crypto.util.Confounder;
import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.util.Hmac;
import org.apache.kerby.kerberos.kerb.crypto.cksum.provider.Sha1Provider;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import org.apache.kerby.kerberos.kerb.crypto.key.KeyMaker;
import org.apache.kerby.kerberos.kerb.crypto.key.Rc4KeyMaker;
import org.apache.kerby.kerberos.kerb.crypto.cksum.HashProvider;
import org.apache.kerby.kerberos.kerb.crypto.cksum.provider.Md5Provider;
import org.apache.kerby.kerberos.kerb.crypto.enc.provider.Rc4Provider;

public class Rc4HmacEnc extends AbstractEncTypeHandler
{
    private boolean exportable;
    
    public Rc4HmacEnc() {
        this(false);
    }
    
    public Rc4HmacEnc(final boolean exportable) {
        super(new Rc4Provider(), new Md5Provider(), 20);
        this.keyMaker(new Rc4KeyMaker(this.encProvider()));
        this.exportable = exportable;
    }
    
    @Override
    public EncryptionType eType() {
        return EncryptionType.ARCFOUR_HMAC;
    }
    
    @Override
    public byte[] prf(final byte[] key, final byte[] seed) throws KrbException {
        return Hmac.hmac(new Sha1Provider(), key, seed, 20);
    }
    
    @Override
    protected int paddingLength(final int inputLen) {
        final int payloadLen = this.confounderSize() + inputLen;
        final int padding = this.paddingSize();
        if (padding == 0 || payloadLen % padding == 0) {
            return 0;
        }
        return padding - payloadLen % padding;
    }
    
    @Override
    public int confounderSize() {
        return 8;
    }
    
    @Override
    public int paddingSize() {
        return 0;
    }
    
    @Override
    public CheckSumType checksumType() {
        return CheckSumType.HMAC_MD5_ARCFOUR;
    }
    
    @Override
    protected void encryptWith(final byte[] workBuffer, final int[] workLens, final byte[] key, final byte[] iv, final int usage) throws KrbException {
        final int confounderLen = workLens[0];
        final int checksumLen = workLens[1];
        final int dataLen = workLens[2];
        final byte[] confounder = Confounder.makeBytes(confounderLen);
        System.arraycopy(confounder, 0, workBuffer, checksumLen, confounderLen);
        final byte[] usageKey = this.makeUsageKey(key, usage);
        final byte[] checksum = Hmac.hmac(this.hashProvider(), usageKey, workBuffer, checksumLen, confounderLen + dataLen);
        final byte[] encKey = this.makeEncKey(usageKey, checksum);
        final byte[] tmpEnc = new byte[confounderLen + dataLen];
        System.arraycopy(workBuffer, checksumLen, tmpEnc, 0, confounderLen + dataLen);
        this.encProvider().encrypt(encKey, iv, tmpEnc);
        System.arraycopy(checksum, 0, workBuffer, 0, checksumLen);
        System.arraycopy(tmpEnc, 0, workBuffer, checksumLen, tmpEnc.length);
    }
    
    protected byte[] makeUsageKey(final byte[] key, final int usage) throws KrbException {
        final byte[] salt = Rc4.getSalt(usage, this.exportable);
        return Hmac.hmac(this.hashProvider(), key, salt);
    }
    
    protected byte[] makeEncKey(final byte[] usageKey, final byte[] checksum) throws KrbException {
        byte[] tmpKey = usageKey;
        if (this.exportable) {
            tmpKey = BytesUtil.duplicate(usageKey);
            for (int i = 0; i < 9; ++i) {
                tmpKey[i + 7] = -85;
            }
        }
        return Hmac.hmac(this.hashProvider(), tmpKey, checksum);
    }
    
    @Override
    protected byte[] decryptWith(final byte[] workBuffer, final int[] workLens, final byte[] key, final byte[] iv, final int usage) throws KrbException {
        final int confounderLen = workLens[0];
        final int checksumLen = workLens[1];
        final int dataLen = workLens[2];
        final byte[] usageKey = this.makeUsageKey(key, usage);
        final byte[] checksum = new byte[checksumLen];
        System.arraycopy(workBuffer, 0, checksum, 0, checksumLen);
        final byte[] encKey = this.makeEncKey(usageKey, checksum);
        final byte[] tmpEnc = new byte[confounderLen + dataLen];
        System.arraycopy(workBuffer, checksumLen, tmpEnc, 0, confounderLen + dataLen);
        this.encProvider().decrypt(encKey, iv, tmpEnc);
        final byte[] newChecksum = Hmac.hmac(this.hashProvider(), usageKey, tmpEnc);
        if (!AbstractCryptoTypeHandler.checksumEqual(checksum, newChecksum)) {
            throw new KrbException(KrbErrorCode.KRB_AP_ERR_BAD_INTEGRITY);
        }
        final byte[] data = new byte[dataLen];
        System.arraycopy(tmpEnc, confounderLen, data, 0, dataLen);
        return data;
    }
}
