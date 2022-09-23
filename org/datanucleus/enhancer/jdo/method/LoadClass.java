// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.util.JavaUtils;
import org.datanucleus.asm.Label;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.ClassMethod;

public class LoadClass extends ClassMethod
{
    public static LoadClass getInstance(final ClassEnhancer enhancer) {
        return new LoadClass(enhancer, enhancer.getNamer().getLoadClassMethodName(), 9, Class.class, new Class[] { String.class }, new String[] { "className" });
    }
    
    public LoadClass(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
        super(enhancer, name, access, returnType, argTypes, argNames);
    }
    
    @Override
    public void execute() {
        this.visitor.visitCode();
        final Label l0 = new Label();
        final Label l2 = new Label();
        final Label l3 = new Label();
        this.visitor.visitTryCatchBlock(l0, l2, l3, "java/lang/ClassNotFoundException");
        this.visitor.visitLabel(l0);
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitMethodInsn(184, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;");
        this.visitor.visitLabel(l2);
        this.visitor.visitInsn(176);
        this.visitor.visitLabel(l3);
        if (JavaUtils.useStackMapFrames()) {
            this.visitor.visitFrame(4, 0, null, 1, new Object[] { "java/lang/ClassNotFoundException" });
        }
        this.visitor.visitVarInsn(58, 1);
        final Label l4 = new Label();
        this.visitor.visitLabel(l4);
        this.visitor.visitTypeInsn(187, "java/lang/NoClassDefFoundError");
        this.visitor.visitInsn(89);
        this.visitor.visitVarInsn(25, 1);
        this.visitor.visitMethodInsn(182, "java/lang/ClassNotFoundException", "getMessage", "()Ljava/lang/String;");
        this.visitor.visitMethodInsn(183, "java/lang/NoClassDefFoundError", "<init>", "(Ljava/lang/String;)V");
        this.visitor.visitInsn(191);
        final Label l5 = new Label();
        this.visitor.visitLabel(l5);
        this.visitor.visitLocalVariable(this.argNames[0], "Ljava/lang/String;", null, l0, l5, 0);
        this.visitor.visitLocalVariable("e", "Ljava/lang/ClassNotFoundException;", null, l4, l5, 1);
        this.visitor.visitMaxs(3, 2);
        this.visitor.visitEnd();
    }
}
