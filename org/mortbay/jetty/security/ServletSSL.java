// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.security;

public class ServletSSL
{
    public static final int deduceKeyLength(final String cipherSuite) {
        if (cipherSuite == null) {
            return 0;
        }
        if (cipherSuite.indexOf("WITH_AES_256_") >= 0) {
            return 256;
        }
        if (cipherSuite.indexOf("WITH_RC4_128_") >= 0) {
            return 128;
        }
        if (cipherSuite.indexOf("WITH_AES_128_") >= 0) {
            return 128;
        }
        if (cipherSuite.indexOf("WITH_RC4_40_") >= 0) {
            return 40;
        }
        if (cipherSuite.indexOf("WITH_3DES_EDE_CBC_") >= 0) {
            return 168;
        }
        if (cipherSuite.indexOf("WITH_IDEA_CBC_") >= 0) {
            return 128;
        }
        if (cipherSuite.indexOf("WITH_RC2_CBC_40_") >= 0) {
            return 40;
        }
        if (cipherSuite.indexOf("WITH_DES40_CBC_") >= 0) {
            return 40;
        }
        if (cipherSuite.indexOf("WITH_DES_CBC_") >= 0) {
            return 56;
        }
        return 0;
    }
}
