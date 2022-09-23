// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.enhancer.EnhanceUtils;
import org.datanucleus.util.JavaUtils;
import org.datanucleus.asm.Label;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.ClassMethod;

public class JdoReplaceDetachedState extends ClassMethod
{
    public static JdoReplaceDetachedState getInstance(final ClassEnhancer enhancer) {
        return new JdoReplaceDetachedState(enhancer, enhancer.getNamer().getReplaceDetachedStateMethodName(), 49, null, null, null);
    }
    
    public JdoReplaceDetachedState(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
        super(enhancer, name, access, returnType, argTypes, argNames);
    }
    
    @Override
    public void execute() {
        this.visitor.visitCode();
        final Label startLabel = new Label();
        this.visitor.visitLabel(startLabel);
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getStateManagerFieldName(), this.getNamer().getStateManagerDescriptor());
        final Label l1 = new Label();
        this.visitor.visitJumpInsn(199, l1);
        this.visitor.visitTypeInsn(187, "java/lang/IllegalStateException");
        this.visitor.visitInsn(89);
        this.visitor.visitLdcInsn("state manager is null");
        this.visitor.visitMethodInsn(183, "java/lang/IllegalStateException", "<init>", "(Ljava/lang/String;)V");
        this.visitor.visitInsn(191);
        this.visitor.visitLabel(l1);
        if (JavaUtils.useStackMapFrames()) {
            this.visitor.visitFrame(3, 0, null, 0, null);
        }
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getStateManagerFieldName(), this.getNamer().getStateManagerDescriptor());
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getDetachedStateFieldName(), "[" + EnhanceUtils.CD_Object);
        this.visitor.visitMethodInsn(185, this.getNamer().getStateManagerAsmClassName(), "replacingDetachedState", "(L" + this.getNamer().getDetachableAsmClassName() + ";[" + EnhanceUtils.CD_Object + ")[" + EnhanceUtils.CD_Object);
        this.visitor.visitFieldInsn(181, this.getClassEnhancer().getASMClassName(), this.getNamer().getDetachedStateFieldName(), "[" + EnhanceUtils.CD_Object);
        this.visitor.visitInsn(177);
        final Label endLabel = new Label();
        this.visitor.visitLabel(endLabel);
        this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel, 0);
        this.visitor.visitMaxs(4, 1);
        this.visitor.visitEnd();
    }
}
