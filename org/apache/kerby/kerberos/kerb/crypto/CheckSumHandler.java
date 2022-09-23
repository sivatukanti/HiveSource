// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto;

import org.apache.kerby.kerberos.kerb.type.base.KeyUsage;
import org.apache.kerby.kerberos.kerb.type.base.CheckSum;
import org.apache.kerby.kerberos.kerb.KrbErrorCode;
import org.apache.kerby.kerberos.kerb.crypto.cksum.Md5HmacRc4CheckSum;
import org.apache.kerby.kerberos.kerb.crypto.cksum.HmacMd5Rc4CheckSum;
import org.apache.kerby.kerberos.kerb.crypto.cksum.CmacCamellia256CheckSum;
import org.apache.kerby.kerberos.kerb.crypto.cksum.CmacCamellia128CheckSum;
import org.apache.kerby.kerberos.kerb.crypto.cksum.HmacSha1Aes256CheckSum;
import org.apache.kerby.kerberos.kerb.crypto.cksum.HmacSha1Aes128CheckSum;
import org.apache.kerby.kerberos.kerb.crypto.cksum.HmacSha1Des3CheckSum;
import org.apache.kerby.kerberos.kerb.crypto.cksum.RsaMd5DesCheckSum;
import org.apache.kerby.kerberos.kerb.crypto.cksum.RsaMd4DesCheckSum;
import org.apache.kerby.kerberos.kerb.crypto.cksum.Sha1CheckSum;
import org.apache.kerby.kerberos.kerb.crypto.cksum.RsaMd5CheckSum;
import org.apache.kerby.kerberos.kerb.crypto.cksum.RsaMd4CheckSum;
import org.apache.kerby.kerberos.kerb.crypto.cksum.DesCbcCheckSum;
import org.apache.kerby.kerberos.kerb.crypto.cksum.Crc32CheckSum;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;

public class CheckSumHandler
{
    public static CheckSumTypeHandler getCheckSumHandler(final String cksumType) throws KrbException {
        final CheckSumType eTypeEnum = CheckSumType.fromName(cksumType);
        return getCheckSumHandler(eTypeEnum);
    }
    
    public static CheckSumTypeHandler getCheckSumHandler(final int cksumType) throws KrbException {
        final CheckSumType eTypeEnum = CheckSumType.fromValue(cksumType);
        return getCheckSumHandler(eTypeEnum);
    }
    
    public static boolean isImplemented(final CheckSumType cksumType) throws KrbException {
        return getCheckSumHandler(cksumType, true) != null;
    }
    
    public static CheckSumTypeHandler getCheckSumHandler(final CheckSumType cksumType) throws KrbException {
        return getCheckSumHandler(cksumType, false);
    }
    
    private static CheckSumTypeHandler getCheckSumHandler(final CheckSumType cksumType, final boolean check) throws KrbException {
        CheckSumTypeHandler cksumHandler = null;
        switch (cksumType) {
            case CRC32: {
                cksumHandler = new Crc32CheckSum();
                break;
            }
            case DES_MAC: {
                cksumHandler = new DesCbcCheckSum();
                break;
            }
            case RSA_MD4: {
                cksumHandler = new RsaMd4CheckSum();
                break;
            }
            case RSA_MD5: {
                cksumHandler = new RsaMd5CheckSum();
                break;
            }
            case NIST_SHA: {
                cksumHandler = new Sha1CheckSum();
                break;
            }
            case RSA_MD4_DES: {
                cksumHandler = new RsaMd4DesCheckSum();
                break;
            }
            case RSA_MD5_DES: {
                cksumHandler = new RsaMd5DesCheckSum();
                break;
            }
            case HMAC_SHA1_DES3:
            case HMAC_SHA1_DES3_KD: {
                cksumHandler = new HmacSha1Des3CheckSum();
                break;
            }
            case HMAC_SHA1_96_AES128: {
                cksumHandler = new HmacSha1Aes128CheckSum();
                break;
            }
            case HMAC_SHA1_96_AES256: {
                cksumHandler = new HmacSha1Aes256CheckSum();
                break;
            }
            case CMAC_CAMELLIA128: {
                cksumHandler = new CmacCamellia128CheckSum();
                break;
            }
            case CMAC_CAMELLIA256: {
                cksumHandler = new CmacCamellia256CheckSum();
                break;
            }
            case HMAC_MD5_ARCFOUR: {
                cksumHandler = new HmacMd5Rc4CheckSum();
                break;
            }
            case MD5_HMAC_ARCFOUR: {
                cksumHandler = new Md5HmacRc4CheckSum();
                break;
            }
        }
        if (cksumHandler == null && !check) {
            final String message = "Unsupported checksum type: " + cksumType.name();
            throw new KrbException(KrbErrorCode.KDC_ERR_SUMTYPE_NOSUPP, message);
        }
        return cksumHandler;
    }
    
    public static CheckSum checksum(final CheckSumType checkSumType, final byte[] bytes) throws KrbException {
        final CheckSumTypeHandler handler = getCheckSumHandler(checkSumType);
        final byte[] checksumBytes = handler.checksum(bytes);
        final CheckSum checkSum = new CheckSum();
        checkSum.setCksumtype(checkSumType);
        checkSum.setChecksum(checksumBytes);
        return checkSum;
    }
    
    public static boolean verify(final CheckSum checkSum, final byte[] bytes) throws KrbException {
        final CheckSumType checkSumType = checkSum.getCksumtype();
        final CheckSumTypeHandler handler = getCheckSumHandler(checkSumType);
        return handler.verify(bytes, checkSum.getChecksum());
    }
    
    public static CheckSum checksumWithKey(final CheckSumType checkSumType, final byte[] bytes, final byte[] key, final KeyUsage usage) throws KrbException {
        final CheckSumTypeHandler handler = getCheckSumHandler(checkSumType);
        final byte[] checksumBytes = handler.checksumWithKey(bytes, key, usage.getValue());
        final CheckSum checkSum = new CheckSum();
        checkSum.setCksumtype(checkSumType);
        checkSum.setChecksum(checksumBytes);
        return checkSum;
    }
    
    public static boolean verifyWithKey(final CheckSum checkSum, final byte[] bytes, final byte[] key, final KeyUsage usage) throws KrbException {
        final CheckSumType checkSumType = checkSum.getCksumtype();
        final CheckSumTypeHandler handler = getCheckSumHandler(checkSumType);
        return handler.verifyWithKey(bytes, key, usage.getValue(), checkSum.getChecksum());
    }
}
