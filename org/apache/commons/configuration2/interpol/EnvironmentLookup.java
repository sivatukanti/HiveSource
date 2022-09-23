// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.interpol;

public class EnvironmentLookup implements Lookup
{
    @Override
    public String lookup(final String key) {
        return System.getenv(key);
    }
}
