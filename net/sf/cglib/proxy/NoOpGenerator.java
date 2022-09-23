// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.cglib.proxy;

import net.sf.cglib.core.CodeEmitter;
import java.util.Iterator;
import net.sf.cglib.core.EmitUtils;
import net.sf.cglib.core.TypeUtils;
import net.sf.cglib.core.MethodInfo;
import java.util.List;
import net.sf.cglib.core.ClassEmitter;

class NoOpGenerator implements CallbackGenerator
{
    public static final NoOpGenerator INSTANCE;
    
    public void generate(final ClassEmitter ce, final Context context, final List methods) {
        for (final MethodInfo method : methods) {
            if (TypeUtils.isProtected(context.getOriginalModifiers(method)) && TypeUtils.isPublic(method.getModifiers())) {
                final CodeEmitter e = EmitUtils.begin_method(ce, method);
                e.load_this();
                e.load_args();
                e.super_invoke();
                e.return_value();
                e.end_method();
            }
        }
    }
    
    public void generateStatic(final CodeEmitter e, final Context context, final List methods) {
    }
    
    static {
        INSTANCE = new NoOpGenerator();
    }
}
