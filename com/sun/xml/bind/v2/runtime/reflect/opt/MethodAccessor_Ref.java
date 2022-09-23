// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.runtime.reflect.opt;

import com.sun.xml.bind.v2.runtime.reflect.Accessor;

public class MethodAccessor_Ref extends Accessor
{
    public MethodAccessor_Ref() {
        super(Ref.class);
    }
    
    @Override
    public Object get(final Object bean) {
        return ((Bean)bean).get_ref();
    }
    
    @Override
    public void set(final Object bean, final Object value) {
        ((Bean)bean).set_ref((Ref)value);
    }
}
