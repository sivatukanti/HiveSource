// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.text.lookup;

final class SystemPropertyStringLookup extends AbstractStringLookup
{
    static final SystemPropertyStringLookup INSTANCE;
    
    private SystemPropertyStringLookup() {
    }
    
    @Override
    public String lookup(final String key) {
        try {
            return System.getProperty(key);
        }
        catch (SecurityException | NullPointerException | IllegalArgumentException ex2) {
            final RuntimeException ex;
            final RuntimeException e = ex;
            return null;
        }
    }
    
    static {
        INSTANCE = new SystemPropertyStringLookup();
    }
}
