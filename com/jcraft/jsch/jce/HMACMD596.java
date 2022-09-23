// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch.jce;

public class HMACMD596 extends HMACMD5
{
    private final byte[] _buf16;
    
    public HMACMD596() {
        this._buf16 = new byte[16];
        this.name = "hmac-md5-96";
    }
    
    @Override
    public int getBlockSize() {
        return 12;
    }
    
    @Override
    public void doFinal(final byte[] buf, final int offset) {
        super.doFinal(this._buf16, 0);
        System.arraycopy(this._buf16, 0, buf, offset, 12);
    }
}
