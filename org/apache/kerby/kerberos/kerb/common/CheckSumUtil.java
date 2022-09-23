// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.common;

import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.kerberos.kerb.KrbCodec;
import org.apache.kerby.asn1.type.Asn1Encodeable;
import org.apache.kerby.kerberos.kerb.crypto.EncTypeHandler;
import org.apache.kerby.kerberos.kerb.crypto.EncryptionHandler;
import org.apache.kerby.kerberos.kerb.type.base.KeyUsage;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.CheckSumHandler;
import org.apache.kerby.kerberos.kerb.type.base.CheckSum;
import org.apache.kerby.kerberos.kerb.type.base.CheckSumType;

public class CheckSumUtil
{
    public static CheckSum makeCheckSum(final CheckSumType checkSumType, final byte[] input) throws KrbException {
        return CheckSumHandler.checksum(checkSumType, input);
    }
    
    public static CheckSum makeCheckSumWithKey(CheckSumType checkSumType, final byte[] input, final EncryptionKey key, final KeyUsage usage) throws KrbException {
        if (checkSumType == null || checkSumType == CheckSumType.NONE) {
            final EncTypeHandler handler = EncryptionHandler.getEncHandler(key.getKeyType());
            checkSumType = handler.checksumType();
            if (checkSumType == null) {
                checkSumType = CheckSumType.CMAC_CAMELLIA128;
            }
        }
        return CheckSumHandler.checksumWithKey(checkSumType, input, key.getKeyData(), usage);
    }
    
    public static CheckSum seal(final Asn1Encodeable asn1Object, final CheckSumType checkSumType) throws KrbException {
        final byte[] encoded = KrbCodec.encode(asn1Object);
        final CheckSum checksum = makeCheckSum(checkSumType, encoded);
        return checksum;
    }
    
    public static CheckSum seal(final Asn1Encodeable asn1Object, final CheckSumType checkSumType, final EncryptionKey key, final KeyUsage usage) throws KrbException {
        final byte[] encoded = KrbCodec.encode(asn1Object);
        final CheckSum checksum = makeCheckSumWithKey(checkSumType, encoded, key, usage);
        return checksum;
    }
}
