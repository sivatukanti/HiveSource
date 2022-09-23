// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch.jce;

import com.jcraft.jsch.ECDH;

public class ECDH256 extends ECDHN implements ECDH
{
    public void init() throws Exception {
        super.init(256);
    }
}
