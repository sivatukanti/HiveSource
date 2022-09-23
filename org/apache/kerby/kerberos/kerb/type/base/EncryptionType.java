// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.base;

import org.apache.kerby.asn1.EnumType;

public enum EncryptionType implements EnumType
{
    NONE(0, "none", "None encryption type"), 
    DES_CBC_CRC(1, "des-cbc-crc", "DES cbc mode with CRC-32"), 
    DES_CBC_MD4(2, "des-cbc-md4", "DES cbc mode with RSA-MD4"), 
    DES_CBC_MD5(3, "des-cbc-md5", "DES cbc mode with RSA-MD5"), 
    DES(3, "des", "DES cbc mode with RSA-MD5"), 
    DES_CBC_RAW(4, "des-cbc-raw", "DES cbc mode raw"), 
    DES3_CBC_SHA(5, "des3-cbc-sha", "DES-3 cbc with SHA1"), 
    DES3_CBC_RAW(6, "des3-cbc-raw", "Triple DES cbc mode raw"), 
    DES_HMAC_SHA1(8, "des-hmac-sha1", "DES with HMAC/sha1"), 
    DSA_SHA1_CMS(9, "dsa-sha1-cms", "DSA with SHA1, CMS signature"), 
    MD5_RSA_CMS(10, "md5-rsa-cms", "MD5 with RSA, CMS signature"), 
    SHA1_RSA_CMS(11, "sha1-rsa-cms", "SHA1 with RSA, CMS signature"), 
    RC2_CBC_ENV(12, "rc2-cbc-env", "RC2 cbc mode, CMS enveloped data"), 
    RSA_ENV(13, "rsa-env", "RSA encryption, CMS enveloped data"), 
    RSA_ES_OAEP_ENV(14, "rsa-es-oaep-env", "RSA w/OEAP encryption, CMS enveloped data"), 
    DES3_CBC_ENV(15, "des3-cbc-env", "DES-3 cbc mode, CMS enveloped data"), 
    DES3_CBC_SHA1(16, "des3-cbc-sha1", "Triple DES cbc mode with HMAC/sha1"), 
    DES3_HMAC_SHA1(16, "des3-hmac-sha1", "Triple DES cbc mode with HMAC/sha1"), 
    DES3_CBC_SHA1_KD(16, "des3-cbc-sha1-kd", "Triple DES cbc mode with HMAC/sha1"), 
    AES128_CTS_HMAC_SHA1_96(17, "aes128-cts-hmac-sha1-96", "AES-128 CTS mode with 96-bit SHA-1 HMAC"), 
    AES128_CTS(17, "aes128-cts", "AES-128 CTS mode with 96-bit SHA-1 HMAC"), 
    AES256_CTS_HMAC_SHA1_96(18, "aes256-cts-hmac-sha1-96", "AES-256 CTS mode with 96-bit SHA-1 HMAC"), 
    AES256_CTS(18, "aes256-cts", "AES-256 CTS mode with 96-bit SHA-1 HMAC"), 
    ARCFOUR_HMAC(23, "arcfour-hmac", "ArcFour with HMAC/md5"), 
    RC4_HMAC(23, "rc4-hmac", "ArcFour with HMAC/md5"), 
    ARCFOUR_HMAC_MD5(23, "arcfour-hmac-md5", "ArcFour with HMAC/md5"), 
    ARCFOUR_HMAC_EXP(24, "arcfour-hmac-exp", "Exportable ArcFour with HMAC/md5"), 
    RC4_HMAC_EXP(24, "rc4-hmac-exp", "Exportable ArcFour with HMAC/md5"), 
    ARCFOUR_HMAC_MD5_EXP(24, "arcfour-hmac-md5-exp", "Exportable ArcFour with HMAC/md5"), 
    CAMELLIA128_CTS_CMAC(25, "camellia128-cts-cmac", "Camellia-128 CTS mode with CMAC"), 
    CAMELLIA128_CTS(25, "camellia128-cts", "Camellia-128 CTS mode with CMAC"), 
    CAMELLIA256_CTS_CMAC(26, "camellia256-cts-cmac", "Camellia-256 CTS mode with CMAC"), 
    CAMELLIA256_CTS(26, "camellia256-cts", "Camellia-256 CTS mode with CMAC");
    
    private final int value;
    private final String name;
    private final String displayName;
    
    private EncryptionType(final int value, final String name, final String displayName) {
        this.value = value;
        this.name = name;
        this.displayName = displayName;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public boolean usesAES256() {
        return this.name.contains("aes256");
    }
    
    public static EncryptionType fromValue(final Integer value) {
        if (value != null) {
            for (final EnumType e : values()) {
                if (e.getValue() == value) {
                    return (EncryptionType)e;
                }
            }
        }
        return EncryptionType.NONE;
    }
    
    public static EncryptionType fromName(final String name) {
        if (name != null) {
            for (final EncryptionType e : values()) {
                if (e.getName().equals(name)) {
                    return e;
                }
            }
        }
        return EncryptionType.NONE;
    }
}
