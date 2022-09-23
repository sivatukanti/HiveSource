// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.asm.Opcodes;
import org.datanucleus.util.JavaUtils;
import org.datanucleus.asm.Label;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.ClassMethod;

public class JdoProvideFields extends ClassMethod
{
    public static JdoProvideFields getInstance(final ClassEnhancer enhancer) {
        return new JdoProvideFields(enhancer, enhancer.getNamer().getProvideFieldsMethodName(), 17, null, new Class[] { int[].class }, new String[] { "indices" });
    }
    
    public JdoProvideFields(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
        super(enhancer, name, access, returnType, argTypes, argNames);
    }
    
    @Override
    public void execute() {
        this.visitor.visitCode();
        final Label l0 = new Label();
        this.visitor.visitLabel(l0);
        this.visitor.visitVarInsn(25, 1);
        final Label l2 = new Label();
        this.visitor.visitJumpInsn(199, l2);
        this.visitor.visitTypeInsn(187, "java/lang/IllegalArgumentException");
        this.visitor.visitInsn(89);
        this.visitor.visitLdcInsn("argment is null");
        this.visitor.visitMethodInsn(183, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
        this.visitor.visitInsn(191);
        this.visitor.visitLabel(l2);
        if (JavaUtils.useStackMapFrames()) {
            this.visitor.visitFrame(3, 0, null, 0, null);
        }
        this.visitor.visitVarInsn(25, 1);
        this.visitor.visitInsn(190);
        this.visitor.visitInsn(4);
        this.visitor.visitInsn(100);
        this.visitor.visitVarInsn(54, 2);
        final Label l3 = new Label();
        this.visitor.visitLabel(l3);
        this.visitor.visitVarInsn(21, 2);
        final Label l4 = new Label();
        this.visitor.visitJumpInsn(155, l4);
        final Label l5 = new Label();
        this.visitor.visitLabel(l5);
        if (JavaUtils.useStackMapFrames()) {
            this.visitor.visitFrame(1, 1, new Object[] { Opcodes.INTEGER }, 0, null);
        }
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitVarInsn(25, 1);
        this.visitor.visitVarInsn(21, 2);
        this.visitor.visitInsn(46);
        this.visitor.visitMethodInsn(182, this.getClassEnhancer().getASMClassName(), this.getNamer().getProvideFieldMethodName(), "(I)V");
        this.visitor.visitIincInsn(2, -1);
        this.visitor.visitVarInsn(21, 2);
        this.visitor.visitJumpInsn(156, l5);
        this.visitor.visitLabel(l4);
        if (JavaUtils.useStackMapFrames()) {
            this.visitor.visitFrame(3, 0, null, 0, null);
        }
        this.visitor.visitInsn(177);
        final Label l6 = new Label();
        this.visitor.visitLabel(l6);
        this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, l0, l6, 0);
        this.visitor.visitLocalVariable(this.argNames[0], "[I", null, l0, l6, 1);
        this.visitor.visitLocalVariable("i", "I", null, l3, l6, 2);
        this.visitor.visitMaxs(3, 3);
        this.visitor.visitEnd();
    }
}
