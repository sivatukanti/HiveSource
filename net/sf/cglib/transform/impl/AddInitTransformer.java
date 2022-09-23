// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.cglib.transform.impl;

import net.sf.cglib.core.CodeEmitter;
import net.sf.cglib.core.Signature;
import org.objectweb.asm.Type;
import net.sf.cglib.core.Constants;
import java.lang.reflect.Member;
import net.sf.cglib.core.ReflectUtils;
import java.lang.reflect.Method;
import net.sf.cglib.core.MethodInfo;
import net.sf.cglib.transform.ClassEmitterTransformer;

public class AddInitTransformer extends ClassEmitterTransformer
{
    private MethodInfo info;
    
    public AddInitTransformer(final Method method) {
        this.info = ReflectUtils.getMethodInfo(method);
        final Type[] types = this.info.getSignature().getArgumentTypes();
        if (types.length != 1 || !types[0].equals(Constants.TYPE_OBJECT) || !this.info.getSignature().getReturnType().equals(Type.VOID_TYPE)) {
            throw new IllegalArgumentException(method + " illegal signature");
        }
    }
    
    public CodeEmitter begin_method(final int access, final Signature sig, final Type[] exceptions) {
        final CodeEmitter emitter = super.begin_method(access, sig, exceptions);
        if (sig.getName().equals("<init>")) {
            return new CodeEmitter(emitter) {
                public void visitInsn(final int opcode) {
                    if (opcode == 177) {
                        this.load_this();
                        this.invoke(AddInitTransformer.this.info);
                    }
                    super.visitInsn(opcode);
                }
            };
        }
        return emitter;
    }
}
