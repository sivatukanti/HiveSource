// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.dh;

import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

public class OctetString2Key
{
    public static byte[] kTruncate(final int k, final byte[] x) {
        final int numberOfBytes = k / 8;
        final byte[] result = new byte[numberOfBytes];
        int count = 0;
        byte[] filler = calculateIntegrity((byte)count, x);
        int position = 0;
        for (int i = 0; i < numberOfBytes; ++i) {
            if (position < filler.length) {
                result[i] = filler[position];
                ++position;
            }
            else {
                filler = calculateIntegrity((byte)(++count), x);
                position = 0;
                result[i] = filler[position];
                ++position;
            }
        }
        return result;
    }
    
    private static byte[] calculateIntegrity(final byte count, final byte[] data) {
        try {
            final MessageDigest digester = MessageDigest.getInstance("SHA1");
            digester.update(count);
            return digester.digest(data);
        }
        catch (NoSuchAlgorithmException nsae) {
            return new byte[0];
        }
    }
}
