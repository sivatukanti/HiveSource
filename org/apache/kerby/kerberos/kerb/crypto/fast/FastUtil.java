// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.fast;

import org.apache.kerby.kerberos.kerb.KrbException;
import java.nio.charset.StandardCharsets;
import org.apache.kerby.kerberos.kerb.crypto.EncryptionHandler;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;

public class FastUtil
{
    public static byte[] prfPlus(final EncryptionKey key, final String pepper, final int keyBytesLen) throws KrbException {
        final byte[] prfInbuf = new byte[pepper.length() + 1];
        final byte[] tmpbuf = new byte[keyBytesLen];
        final int prfSize = EncryptionHandler.getEncHandler(key.getKeyType()).prfSize();
        int iterations = keyBytesLen / prfSize;
        prfInbuf[0] = 1;
        System.arraycopy(pepper.getBytes(StandardCharsets.UTF_8), 0, prfInbuf, 1, pepper.length());
        if (keyBytesLen % prfSize != 0) {
            ++iterations;
        }
        final byte[] buffer = new byte[prfSize * iterations];
        for (int i = 0; i < iterations; ++i) {
            System.arraycopy(EncryptionHandler.getEncHandler(key.getKeyType()).prf(key.getKeyData(), prfInbuf), 0, buffer, i * prfSize, prfSize);
            final byte[] array = prfInbuf;
            final int n = 0;
            ++array[n];
        }
        System.arraycopy(buffer, 0, tmpbuf, 0, keyBytesLen);
        return tmpbuf;
    }
    
    public static EncryptionKey cf2(final EncryptionKey key1, final String pepper1, final EncryptionKey key2, final String pepper2) throws KrbException {
        final int keyBites = EncryptionHandler.getEncHandler(key1.getKeyType()).encProvider().keyInputSize();
        final byte[] buf1 = prfPlus(key1, pepper1, keyBites);
        final byte[] buf2 = prfPlus(key2, pepper2, keyBites);
        for (int i = 0; i < keyBites; ++i) {
            final byte[] array = buf1;
            final int n = i;
            array[n] ^= buf2[i];
        }
        final EncryptionKey outKey = EncryptionHandler.random2Key(key1.getKeyType(), buf1);
        return outKey;
    }
    
    public static EncryptionKey makeReplyKey(final EncryptionKey strengthenKey, final EncryptionKey existingKey) throws KrbException {
        return cf2(strengthenKey, "strengthenkey", existingKey, "replykey");
    }
    
    public static EncryptionKey makeArmorKey(final EncryptionKey subkey, final EncryptionKey ticketKey) throws KrbException {
        return cf2(subkey, "subkeyarmor", ticketKey, "ticketarmor");
    }
}
