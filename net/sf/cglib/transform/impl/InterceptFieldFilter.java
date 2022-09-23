// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.cglib.transform.impl;

import org.objectweb.asm.Type;

public interface InterceptFieldFilter
{
    boolean acceptRead(final Type p0, final String p1);
    
    boolean acceptWrite(final Type p0, final String p1);
}
