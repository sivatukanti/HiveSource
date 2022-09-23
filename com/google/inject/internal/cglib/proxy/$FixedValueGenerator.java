// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.cglib.proxy;

import com.google.inject.internal.cglib.core.$TypeUtils;
import com.google.inject.internal.cglib.core.$CodeEmitter;
import java.util.Iterator;
import com.google.inject.internal.cglib.core.$MethodInfo;
import java.util.List;
import com.google.inject.internal.cglib.core.$ClassEmitter;
import com.google.inject.internal.cglib.core.$Signature;
import com.google.inject.internal.asm.$Type;

class $FixedValueGenerator implements $CallbackGenerator
{
    public static final $FixedValueGenerator INSTANCE;
    private static final $Type FIXED_VALUE;
    private static final $Signature LOAD_OBJECT;
    
    public void generate(final $ClassEmitter ce, final Context context, final List methods) {
        for (final $MethodInfo method : methods) {
            final $CodeEmitter e = context.beginMethod(ce, method);
            context.emitCallback(e, context.getIndex(method));
            e.invoke_interface($FixedValueGenerator.FIXED_VALUE, $FixedValueGenerator.LOAD_OBJECT);
            e.unbox_or_zero(e.getReturnType());
            e.return_value();
            e.end_method();
        }
    }
    
    public void generateStatic(final $CodeEmitter e, final Context context, final List methods) {
    }
    
    static {
        INSTANCE = new $FixedValueGenerator();
        FIXED_VALUE = $TypeUtils.parseType("com.google.inject.internal.cglib.proxy.$FixedValue");
        LOAD_OBJECT = $TypeUtils.parseSignature("Object loadObject()");
    }
}
