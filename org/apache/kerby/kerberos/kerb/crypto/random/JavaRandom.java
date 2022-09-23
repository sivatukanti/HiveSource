// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.random;

import java.security.SecureRandom;

public class JavaRandom implements RandomProvider
{
    private SecureRandom random;
    
    public JavaRandom() {
        this.random = new SecureRandom();
    }
    
    @Override
    public void init() {
    }
    
    @Override
    public void setSeed(final byte[] seed) {
        this.random.setSeed(seed);
    }
    
    @Override
    public void nextBytes(final byte[] bytes) {
        this.random.nextBytes(bytes);
    }
    
    @Override
    public void destroy() {
    }
}
