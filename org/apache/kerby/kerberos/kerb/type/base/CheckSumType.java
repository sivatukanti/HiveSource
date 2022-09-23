// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.base;

import org.apache.kerby.asn1.EnumType;

public enum CheckSumType implements EnumType
{
    NONE(0, "none", "None checksum type"), 
    CRC32(1, "crc32", "CRC-32"), 
    RSA_MD4(2, "md4", "RSA-MD4"), 
    RSA_MD4_DES(3, "md4-des", "RSA-MD4 with DES cbc mode"), 
    DES_CBC(4, "des-cbc", "DES cbc mode"), 
    DES_MAC(4, "des-mac", "DES cbc mode"), 
    RSA_MD5(7, "md5", "RSA-MD5"), 
    RSA_MD5_DES(8, "md5-des", "RSA-MD5 with DES cbc mode"), 
    NIST_SHA(9, "sha", "NIST-SHA"), 
    HMAC_SHA1_DES3(12, "hmac-sha1-des3", "HMAC-SHA1 DES3 key"), 
    HMAC_SHA1_DES3_KD(12, "hmac-sha1-des3-kd", "HMAC-SHA1 DES3 key"), 
    HMAC_SHA1_96_AES128(15, "hmac-sha1-96-aes128", "HMAC-SHA1 AES128 key"), 
    HMAC_SHA1_96_AES256(16, "hmac-sha1-96-aes256", "HMAC-SHA1 AES256 key"), 
    CMAC_CAMELLIA128(17, "cmac-camellia128", "CMAC Camellia128 key"), 
    CMAC_CAMELLIA256(18, "cmac-camellia256", "CMAC Camellia256 key"), 
    MD5_HMAC_ARCFOUR(-137, "md5-hmac-rc4", "Microsoft MD5 HMAC"), 
    HMAC_MD5_ARCFOUR(-138, "hmac-md5-arcfour", "Microsoft HMAC MD5"), 
    HMAC_MD5_ENC(-138, "hmac-md5-enc", "Microsoft HMAC MD5"), 
    HMAC_MD5_RC4(-138, "hmac-md5-rc4", "Microsoft HMAC MD5");
    
    private final int value;
    private final String name;
    private final String displayName;
    
    private CheckSumType(final int value, final String name, final String displayName) {
        this.value = value;
        this.name = name;
        this.displayName = displayName;
    }
    
    public static CheckSumType fromValue(final Integer value) {
        if (value != null) {
            for (final EnumType e : values()) {
                if (e.getValue() == value) {
                    return (CheckSumType)e;
                }
            }
        }
        return CheckSumType.NONE;
    }
    
    public static CheckSumType fromName(final String name) {
        if (name != null) {
            for (final CheckSumType cs : values()) {
                if (cs.getName().equals(name)) {
                    return cs;
                }
            }
        }
        return CheckSumType.NONE;
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
}
