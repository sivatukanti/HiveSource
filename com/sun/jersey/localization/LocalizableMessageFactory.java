// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.localization;

public class LocalizableMessageFactory
{
    private final String _bundlename;
    
    public LocalizableMessageFactory(final String bundlename) {
        this._bundlename = bundlename;
    }
    
    public Localizable getMessage(final String key, final Object... args) {
        return new LocalizableMessage(this._bundlename, key, args);
    }
}
