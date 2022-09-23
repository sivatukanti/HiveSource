// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.schema.naming;

public enum NamingCase
{
    UPPER_CASE("UPPERCASE"), 
    UPPER_CASE_QUOTED("\"UPPERCASE\""), 
    LOWER_CASE("lowercase"), 
    LOWER_CASE_QUOTED("\"lowercase\""), 
    MIXED_CASE("MixedCase"), 
    MIXED_CASE_QUOTED("\"MixedCase\"");
    
    String name;
    
    private NamingCase(final String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
