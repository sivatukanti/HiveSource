// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.util;

import java.nio.charset.StandardCharsets;

public class Rc4
{
    private static final byte[] L40;
    
    public static byte[] getSalt(final int usage, final boolean exportable) {
        final int newUsage = convertUsage(usage);
        byte[] salt;
        if (exportable) {
            salt = new byte[14];
            System.arraycopy(Rc4.L40, 0, salt, 0, 9);
            BytesUtil.int2bytes(newUsage, salt, 10, false);
        }
        else {
            salt = new byte[4];
            BytesUtil.int2bytes(newUsage, salt, 0, false);
        }
        return salt;
    }
    
    private static int convertUsage(final int usage) {
        switch (usage) {
            case 1: {
                return 1;
            }
            case 2: {
                return 2;
            }
            case 3: {
                return 8;
            }
            case 4: {
                return 4;
            }
            case 5: {
                return 5;
            }
            case 6: {
                return 6;
            }
            case 7: {
                return 7;
            }
            case 8: {
                return 8;
            }
            case 9: {
                return 9;
            }
            case 10: {
                return 10;
            }
            case 11: {
                return 11;
            }
            case 12: {
                return 12;
            }
            case 23: {
                return 13;
            }
            default: {
                return usage;
            }
        }
    }
    
    static {
        L40 = "fortybits".getBytes(StandardCharsets.UTF_8);
    }
}
