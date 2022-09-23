// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.jca;

import java.security.SecureRandom;
import java.security.Provider;

public final class JWEJCAContext extends JCAContext
{
    private Provider keProvider;
    private Provider ceProvider;
    private Provider macProvider;
    
    public JWEJCAContext() {
        this(null, null, null, null, null);
    }
    
    public JWEJCAContext(final Provider generalProvider, final Provider keProvider, final Provider ceProvider, final Provider macProvider, final SecureRandom randomGen) {
        super(generalProvider, randomGen);
        this.keProvider = keProvider;
        this.ceProvider = ceProvider;
        this.macProvider = macProvider;
    }
    
    public void setKeyEncryptionProvider(final Provider keProvider) {
        this.keProvider = keProvider;
    }
    
    public Provider getKeyEncryptionProvider() {
        return (this.keProvider != null) ? this.keProvider : this.getProvider();
    }
    
    public void setContentEncryptionProvider(final Provider ceProvider) {
        this.ceProvider = ceProvider;
    }
    
    public Provider getContentEncryptionProvider() {
        return (this.ceProvider != null) ? this.ceProvider : this.getProvider();
    }
    
    public void setMACProvider(final Provider macProvider) {
        this.macProvider = macProvider;
    }
    
    public Provider getMACProvider() {
        return (this.macProvider != null) ? this.macProvider : this.getProvider();
    }
}
