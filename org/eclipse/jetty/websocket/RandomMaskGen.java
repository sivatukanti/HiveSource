// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import java.util.Random;

public class RandomMaskGen implements MaskGen
{
    private final Random _random;
    
    public RandomMaskGen() {
        this(new Random());
    }
    
    public RandomMaskGen(final Random random) {
        this._random = random;
    }
    
    public void genMask(final byte[] mask) {
        this._random.nextBytes(mask);
    }
}
