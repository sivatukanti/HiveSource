// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.util;

import javax.crypto.Cipher;

public class CryptoUtil
{
    private static boolean isAES256Enabled;
    
    public static boolean isAES256Enabled() {
        return CryptoUtil.isAES256Enabled;
    }
    
    static {
        CryptoUtil.isAES256Enabled = false;
        try {
            CryptoUtil.isAES256Enabled = (Cipher.getMaxAllowedKeyLength("AES") >= 256);
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }
}
