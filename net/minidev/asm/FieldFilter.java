// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.asm;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

public interface FieldFilter
{
    boolean canUse(final Field p0);
    
    boolean canUse(final Field p0, final Method p1);
    
    boolean canRead(final Field p0);
    
    boolean canWrite(final Field p0);
}
