// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

public class ZeroMaskGen implements MaskGen
{
    public void genMask(final byte[] mask) {
        final int n = 0;
        final int n2 = 1;
        final int n3 = 2;
        final int n4 = 3;
        final byte b = 0;
        mask[n3] = (mask[n4] = b);
        mask[n] = (mask[n2] = b);
    }
}
