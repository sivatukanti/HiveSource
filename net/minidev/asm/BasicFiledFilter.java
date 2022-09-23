// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.asm;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class BasicFiledFilter implements FieldFilter
{
    public static final BasicFiledFilter SINGLETON;
    
    static {
        SINGLETON = new BasicFiledFilter();
    }
    
    @Override
    public boolean canUse(final Field field) {
        return true;
    }
    
    @Override
    public boolean canUse(final Field field, final Method method) {
        return true;
    }
    
    @Override
    public boolean canRead(final Field field) {
        return true;
    }
    
    @Override
    public boolean canWrite(final Field field) {
        return true;
    }
}
