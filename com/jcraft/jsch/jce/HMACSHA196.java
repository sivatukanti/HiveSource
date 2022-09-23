// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch.jce;

public class HMACSHA196 extends HMACSHA1
{
    private final byte[] _buf20;
    
    public HMACSHA196() {
        this._buf20 = new byte[20];
        this.name = "hmac-sha1-96";
    }
    
    @Override
    public int getBlockSize() {
        return 12;
    }
    
    @Override
    public void doFinal(final byte[] buf, final int offset) {
        super.doFinal(this._buf20, 0);
        System.arraycopy(this._buf20, 0, buf, offset, 12);
    }
}
