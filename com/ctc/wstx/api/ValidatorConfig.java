// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.api;

public final class ValidatorConfig extends CommonConfig
{
    static final ValidatorConfig sInstance;
    
    private ValidatorConfig(final ValidatorConfig base) {
        super(base);
    }
    
    public static ValidatorConfig createDefaults() {
        return ValidatorConfig.sInstance;
    }
    
    @Override
    protected int findPropertyId(final String propName) {
        return -1;
    }
    
    @Override
    protected Object getProperty(final int id) {
        return null;
    }
    
    @Override
    protected boolean setProperty(final String propName, final int id, final Object value) {
        return false;
    }
    
    static {
        sInstance = new ValidatorConfig(null);
    }
}
