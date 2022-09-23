// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

public class FixedMaskGen implements MaskGen
{
    private final byte[] _mask;
    
    public FixedMaskGen() {
        this(new byte[] { -1, -1, -1, -1 });
    }
    
    public FixedMaskGen(final byte[] mask) {
        System.arraycopy(mask, 0, this._mask = new byte[4], 0, 4);
    }
    
    public void genMask(final byte[] mask) {
        System.arraycopy(this._mask, 0, mask, 0, 4);
    }
}
