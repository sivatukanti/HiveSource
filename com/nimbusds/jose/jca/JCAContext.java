// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.jca;

import java.security.SecureRandom;
import java.security.Provider;

public class JCAContext
{
    private Provider provider;
    private SecureRandom randomGen;
    
    public JCAContext() {
        this(null, null);
    }
    
    public JCAContext(final Provider provider, final SecureRandom randomGen) {
        this.provider = provider;
        this.randomGen = randomGen;
    }
    
    public Provider getProvider() {
        return this.provider;
    }
    
    public void setProvider(final Provider provider) {
        this.provider = provider;
    }
    
    public SecureRandom getSecureRandom() {
        return (this.randomGen != null) ? this.randomGen : new SecureRandom();
    }
    
    public void setSecureRandom(final SecureRandom randomGen) {
        this.randomGen = randomGen;
    }
}
