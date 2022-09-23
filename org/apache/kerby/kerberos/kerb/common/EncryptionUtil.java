// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.common;

import java.util.LinkedHashMap;
import org.apache.kerby.kerberos.kerb.crypto.EncTypeHandler;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.kerberos.kerb.KrbCodec;
import org.apache.kerby.kerberos.kerb.type.base.EncryptedData;
import org.apache.kerby.kerberos.kerb.type.base.KeyUsage;
import org.apache.kerby.asn1.type.Asn1Encodeable;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.EncryptionHandler;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import java.util.Map;

public class EncryptionUtil
{
    private static final Map<String, String> CIPHER_ALGO_MAP;
    
    public static String getAlgoNameFromEncType(final EncryptionType encType) {
        final String cipherName = encType.getName().toLowerCase();
        for (final Map.Entry<String, String> entry : EncryptionUtil.CIPHER_ALGO_MAP.entrySet()) {
            if (cipherName.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        throw new IllegalArgumentException("Unknown algorithm name for the encryption type " + encType);
    }
    
    public static List<EncryptionType> orderEtypesByStrength(final List<EncryptionType> etypes) {
        final List<EncryptionType> ordered = new ArrayList<EncryptionType>(etypes.size());
        for (final String algo : EncryptionUtil.CIPHER_ALGO_MAP.values()) {
            for (final EncryptionType encType : etypes) {
                final String foundAlgo = getAlgoNameFromEncType(encType);
                if (algo.equals(foundAlgo)) {
                    ordered.add(encType);
                }
            }
        }
        return ordered;
    }
    
    public static List<EncryptionKey> generateKeys(final List<EncryptionType> encryptionTypes) throws KrbException {
        final List<EncryptionKey> results = new ArrayList<EncryptionKey>(encryptionTypes.size());
        for (final EncryptionType eType : encryptionTypes) {
            final EncryptionKey encKey = EncryptionHandler.random2Key(eType);
            encKey.setKvno(1);
            results.add(encKey);
        }
        return results;
    }
    
    public static List<EncryptionKey> generateKeys(final String principal, final String passwd, final List<EncryptionType> encryptionTypes) throws KrbException {
        final List<EncryptionKey> results = new ArrayList<EncryptionKey>(encryptionTypes.size());
        for (final EncryptionType eType : encryptionTypes) {
            final EncryptionKey encKey = EncryptionHandler.string2Key(principal, passwd, eType);
            encKey.setKvno(1);
            results.add(encKey);
        }
        return results;
    }
    
    public static EncryptionType getBestEncryptionType(final List<EncryptionType> requestedTypes, final List<EncryptionType> configuredTypes) {
        for (final EncryptionType encryptionType : configuredTypes) {
            if (requestedTypes.contains(encryptionType)) {
                return encryptionType;
            }
        }
        return null;
    }
    
    public static EncryptedData seal(final Asn1Encodeable asn1Type, final EncryptionKey key, final KeyUsage usage) throws KrbException {
        final byte[] encoded = KrbCodec.encode(asn1Type);
        final EncryptedData encrypted = EncryptionHandler.encrypt(encoded, key, usage);
        return encrypted;
    }
    
    public static <T extends Asn1Type> T unseal(final EncryptedData encrypted, final EncryptionKey key, final KeyUsage usage, final Class<T> krbType) throws KrbException {
        final byte[] encoded = EncryptionHandler.decrypt(encrypted, key, usage);
        return KrbCodec.decode(encoded, krbType);
    }
    
    public static byte[] encrypt(final EncryptionKey key, final byte[] plaintext, final KeyUsage usage) throws KrbException {
        final EncTypeHandler encType = EncryptionHandler.getEncHandler(key.getKeyType());
        final byte[] cipherData = encType.encrypt(plaintext, key.getKeyData(), usage.getValue());
        return cipherData;
    }
    
    public static byte[] decrypt(final EncryptionKey key, final byte[] cipherData, final KeyUsage usage) throws KrbException {
        final EncTypeHandler encType = EncryptionHandler.getEncHandler(key.getKeyType());
        final byte[] plainData = encType.decrypt(cipherData, key.getKeyData(), usage.getValue());
        return plainData;
    }
    
    static {
        (CIPHER_ALGO_MAP = new LinkedHashMap<String, String>()).put("rc4", "ArcFourHmac");
        EncryptionUtil.CIPHER_ALGO_MAP.put("aes256", "AES256");
        EncryptionUtil.CIPHER_ALGO_MAP.put("aes128", "AES128");
        EncryptionUtil.CIPHER_ALGO_MAP.put("des3", "DESede");
        EncryptionUtil.CIPHER_ALGO_MAP.put("des", "DES");
    }
}
