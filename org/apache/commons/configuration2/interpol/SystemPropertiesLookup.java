// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.interpol;

import org.apache.commons.lang3.text.StrLookup;

public class SystemPropertiesLookup implements Lookup
{
    private final StrLookup<String> sysLookup;
    
    public SystemPropertiesLookup() {
        this.sysLookup = StrLookup.systemPropertiesLookup();
    }
    
    @Override
    public Object lookup(final String variable) {
        return this.sysLookup.lookup(variable);
    }
}
