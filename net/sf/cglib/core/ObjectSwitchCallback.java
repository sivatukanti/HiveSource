// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.cglib.core;

import org.objectweb.asm.Label;

public interface ObjectSwitchCallback
{
    void processCase(final Object p0, final Label p1) throws Exception;
    
    void processDefault() throws Exception;
}
