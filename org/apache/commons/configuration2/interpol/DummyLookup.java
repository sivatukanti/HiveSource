// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.interpol;

public enum DummyLookup implements Lookup
{
    INSTANCE;
    
    @Override
    public Object lookup(final String variable) {
        return null;
    }
}
