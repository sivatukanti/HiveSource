// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.cglib.proxy;

import com.google.inject.internal.cglib.core.$CodeEmitter;
import java.util.Iterator;
import com.google.inject.internal.cglib.core.$EmitUtils;
import com.google.inject.internal.cglib.core.$TypeUtils;
import com.google.inject.internal.cglib.core.$MethodInfo;
import java.util.List;
import com.google.inject.internal.cglib.core.$ClassEmitter;

class $NoOpGenerator implements $CallbackGenerator
{
    public static final $NoOpGenerator INSTANCE;
    
    public void generate(final $ClassEmitter ce, final Context context, final List methods) {
        for (final $MethodInfo method : methods) {
            if ($TypeUtils.isProtected(context.getOriginalModifiers(method)) && $TypeUtils.isPublic(method.getModifiers())) {
                final $CodeEmitter e = $EmitUtils.begin_method(ce, method);
                e.load_this();
                e.load_args();
                e.super_invoke();
                e.return_value();
                e.end_method();
            }
        }
    }
    
    public void generateStatic(final $CodeEmitter e, final Context context, final List methods) {
    }
    
    static {
        INSTANCE = new $NoOpGenerator();
    }
}
