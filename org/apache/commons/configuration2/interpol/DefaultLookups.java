// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.interpol;

public enum DefaultLookups
{
    SYSTEM_PROPERTIES("sys", (Lookup)new SystemPropertiesLookup()), 
    ENVIRONMENT("env", (Lookup)new EnvironmentLookup()), 
    CONST("const", (Lookup)new ConstantLookup());
    
    private final String prefix;
    private final Lookup lookup;
    
    private DefaultLookups(final String prfx, final Lookup look) {
        this.prefix = prfx;
        this.lookup = look;
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public Lookup getLookup() {
        return this.lookup;
    }
}
