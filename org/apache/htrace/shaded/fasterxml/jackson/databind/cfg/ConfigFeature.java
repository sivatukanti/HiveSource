// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.cfg;

public interface ConfigFeature
{
    boolean enabledByDefault();
    
    int getMask();
}
