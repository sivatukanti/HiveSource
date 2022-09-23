// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.cglib.proxy;

import net.sf.cglib.core.TypeUtils;
import net.sf.cglib.core.CodeEmitter;
import java.util.Iterator;
import net.sf.cglib.core.MethodInfo;
import java.util.List;
import net.sf.cglib.core.ClassEmitter;
import net.sf.cglib.core.Signature;
import org.objectweb.asm.Type;

class FixedValueGenerator implements CallbackGenerator
{
    public static final FixedValueGenerator INSTANCE;
    private static final Type FIXED_VALUE;
    private static final Signature LOAD_OBJECT;
    
    public void generate(final ClassEmitter ce, final Context context, final List methods) {
        for (final MethodInfo method : methods) {
            final CodeEmitter e = context.beginMethod(ce, method);
            context.emitCallback(e, context.getIndex(method));
            e.invoke_interface(FixedValueGenerator.FIXED_VALUE, FixedValueGenerator.LOAD_OBJECT);
            e.unbox_or_zero(e.getReturnType());
            e.return_value();
            e.end_method();
        }
    }
    
    public void generateStatic(final CodeEmitter e, final Context context, final List methods) {
    }
    
    static {
        INSTANCE = new FixedValueGenerator();
        FIXED_VALUE = TypeUtils.parseType("net.sf.cglib.proxy.FixedValue");
        LOAD_OBJECT = TypeUtils.parseSignature("Object loadObject()");
    }
}
