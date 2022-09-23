// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto;

import org.apache.kerby.kerberos.kerb.crypto.util.Random;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.kerberos.kerb.type.base.EncryptedData;
import org.apache.kerby.kerberos.kerb.type.base.KeyUsage;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.KrbErrorCode;
import org.apache.kerby.kerberos.kerb.crypto.enc.Rc4HmacExpEnc;
import org.apache.kerby.kerberos.kerb.crypto.enc.Rc4HmacEnc;
import org.apache.kerby.kerberos.kerb.crypto.enc.Camellia256CtsCmacEnc;
import org.apache.kerby.kerberos.kerb.crypto.enc.Camellia128CtsCmacEnc;
import org.apache.kerby.kerberos.kerb.crypto.enc.Aes256CtsHmacSha1Enc;
import org.apache.kerby.kerberos.kerb.crypto.enc.Aes128CtsHmacSha1Enc;
import org.apache.kerby.kerberos.kerb.crypto.enc.Des3CbcSha1Enc;
import org.apache.kerby.kerberos.kerb.crypto.enc.DesCbcMd4Enc;
import org.apache.kerby.kerberos.kerb.crypto.enc.DesCbcMd5Enc;
import org.apache.kerby.kerberos.kerb.crypto.enc.DesCbcCrcEnc;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;

public class EncryptionHandler
{
    public static EncryptionType getEncryptionType(final String eType) throws KrbException {
        final EncryptionType result = EncryptionType.fromName(eType);
        return result;
    }
    
    public static EncTypeHandler getEncHandler(final String eType) throws KrbException {
        final EncryptionType result = EncryptionType.fromName(eType);
        return getEncHandler(result);
    }
    
    public static EncTypeHandler getEncHandler(final int eType) throws KrbException {
        final EncryptionType eTypeEnum = EncryptionType.fromValue(eType);
        return getEncHandler(eTypeEnum);
    }
    
    public static EncTypeHandler getEncHandler(final EncryptionType eType) throws KrbException {
        return getEncHandler(eType, false);
    }
    
    private static EncTypeHandler getEncHandler(final EncryptionType eType, final boolean check) throws KrbException {
        EncTypeHandler encHandler = null;
        switch (eType) {
            case DES_CBC_CRC: {
                encHandler = new DesCbcCrcEnc();
                break;
            }
            case DES_CBC_MD5:
            case DES: {
                encHandler = new DesCbcMd5Enc();
                break;
            }
            case DES_CBC_MD4: {
                encHandler = new DesCbcMd4Enc();
                break;
            }
            case DES3_CBC_SHA1:
            case DES3_CBC_SHA1_KD:
            case DES3_HMAC_SHA1: {
                encHandler = new Des3CbcSha1Enc();
                break;
            }
            case AES128_CTS_HMAC_SHA1_96:
            case AES128_CTS: {
                encHandler = new Aes128CtsHmacSha1Enc();
                break;
            }
            case AES256_CTS_HMAC_SHA1_96:
            case AES256_CTS: {
                encHandler = new Aes256CtsHmacSha1Enc();
                break;
            }
            case CAMELLIA128_CTS_CMAC:
            case CAMELLIA128_CTS: {
                encHandler = new Camellia128CtsCmacEnc();
                break;
            }
            case CAMELLIA256_CTS_CMAC:
            case CAMELLIA256_CTS: {
                encHandler = new Camellia256CtsCmacEnc();
                break;
            }
            case RC4_HMAC:
            case ARCFOUR_HMAC:
            case ARCFOUR_HMAC_MD5: {
                encHandler = new Rc4HmacEnc();
                break;
            }
            case RC4_HMAC_EXP:
            case ARCFOUR_HMAC_EXP:
            case ARCFOUR_HMAC_MD5_EXP: {
                encHandler = new Rc4HmacExpEnc();
                break;
            }
        }
        if (encHandler == null && !check) {
            final String message = "Unsupported encryption type: " + eType.name();
            throw new KrbException(KrbErrorCode.KDC_ERR_ETYPE_NOSUPP, message);
        }
        return encHandler;
    }
    
    public static EncryptedData encrypt(final byte[] plainText, final EncryptionKey key, final KeyUsage usage) throws KrbException {
        final EncTypeHandler handler = getEncHandler(key.getKeyType());
        final byte[] cipher = handler.encrypt(plainText, key.getKeyData(), usage.getValue());
        final EncryptedData ed = new EncryptedData();
        ed.setCipher(cipher);
        ed.setEType(key.getKeyType());
        if (key.getKvno() > 0) {
            ed.setKvno(key.getKvno());
        }
        return ed;
    }
    
    public static byte[] decrypt(final byte[] data, final EncryptionKey key, final KeyUsage usage) throws KrbException {
        final EncTypeHandler handler = getEncHandler(key.getKeyType());
        final byte[] plainData = handler.decrypt(data, key.getKeyData(), usage.getValue());
        return plainData;
    }
    
    public static byte[] decrypt(final EncryptedData data, final EncryptionKey key, final KeyUsage usage) throws KrbException {
        final EncTypeHandler handler = getEncHandler(key.getKeyType());
        final byte[] plainData = handler.decrypt(data.getCipher(), key.getKeyData(), usage.getValue());
        return plainData;
    }
    
    public static boolean isImplemented(final EncryptionType eType) {
        EncTypeHandler handler = null;
        try {
            handler = getEncHandler(eType, true);
        }
        catch (KrbException e) {
            return false;
        }
        return handler != null;
    }
    
    public static EncryptionKey string2Key(final String principalName, final String passPhrase, final EncryptionType eType) throws KrbException {
        final PrincipalName principal = new PrincipalName(principalName);
        return string2Key(passPhrase, PrincipalName.makeSalt(principal), null, eType);
    }
    
    public static EncryptionKey string2Key(final String string, final String salt, final byte[] s2kparams, final EncryptionType eType) throws KrbException {
        final EncTypeHandler handler = getEncHandler(eType);
        final byte[] keyBytes = handler.str2key(string, salt, s2kparams);
        return new EncryptionKey(eType, keyBytes);
    }
    
    public static EncryptionKey random2Key(final EncryptionType eType) throws KrbException {
        final EncTypeHandler handler = getEncHandler(eType);
        final byte[] randomBytes = Random.makeBytes(handler.keyInputSize());
        final byte[] keyBytes = handler.random2Key(randomBytes);
        final EncryptionKey encKey = new EncryptionKey(eType, keyBytes);
        return encKey;
    }
    
    public static EncryptionKey random2Key(final EncryptionType eType, final byte[] randomBytes) throws KrbException {
        final EncTypeHandler handler = getEncHandler(eType);
        final byte[] randomBytes2 = randomBytes;
        final byte[] keyBytes = handler.random2Key(randomBytes2);
        final EncryptionKey encKey = new EncryptionKey(eType, keyBytes);
        return encKey;
    }
    
    public static EncryptionKey makeSubkey(final EncryptionKey encKey) {
        return encKey;
    }
}
