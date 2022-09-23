// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.util.JavaUtils;
import org.datanucleus.enhancer.EnhanceUtils;
import org.datanucleus.asm.Label;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.ClassMethod;

public class JdoGetVersion extends ClassMethod
{
    public static JdoGetVersion getInstance(final ClassEnhancer enhancer) {
        return new JdoGetVersion(enhancer, enhancer.getNamer().getGetVersionMethodName(), 17, Object.class, null, null);
    }
    
    public JdoGetVersion(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
        super(enhancer, name, access, returnType, argTypes, argNames);
    }
    
    @Override
    public void execute() {
        this.visitor.visitCode();
        final Label l0 = new Label();
        this.visitor.visitLabel(l0);
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getStateManagerFieldName(), this.getNamer().getStateManagerDescriptor());
        final Label l2 = new Label();
        this.visitor.visitJumpInsn(198, l2);
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getStateManagerFieldName(), this.getNamer().getStateManagerDescriptor());
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitMethodInsn(185, this.getNamer().getStateManagerAsmClassName(), "getVersion", "(" + this.getNamer().getPersistableDescriptor() + ")" + EnhanceUtils.CD_Object);
        this.visitor.visitInsn(176);
        this.visitor.visitLabel(l2);
        if (JavaUtils.useStackMapFrames()) {
            this.visitor.visitFrame(3, 0, null, 0, null);
        }
        if (!this.enhancer.getClassMetaData().isDetachable()) {
            this.visitor.visitInsn(1);
            this.visitor.visitInsn(176);
            final Label l3 = new Label();
            this.visitor.visitLabel(l3);
            this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, l0, l3, 0);
            this.visitor.visitMaxs(2, 1);
        }
        else {
            this.visitor.visitVarInsn(25, 0);
            this.visitor.visitMethodInsn(182, this.getClassEnhancer().getASMClassName(), this.getNamer().getIsDetachedMethodName(), "()Z");
            final Label l3 = new Label();
            this.visitor.visitJumpInsn(154, l3);
            this.visitor.visitInsn(1);
            this.visitor.visitInsn(176);
            this.visitor.visitLabel(l3);
            if (JavaUtils.useStackMapFrames()) {
                this.visitor.visitFrame(3, 0, null, 0, null);
            }
            this.visitor.visitVarInsn(25, 0);
            this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getDetachedStateFieldName(), "[" + EnhanceUtils.CD_Object);
            this.visitor.visitInsn(4);
            this.visitor.visitInsn(50);
            this.visitor.visitInsn(176);
            final Label l4 = new Label();
            this.visitor.visitLabel(l4);
            this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, l0, l4, 0);
            this.visitor.visitMaxs(2, 1);
        }
        this.visitor.visitEnd();
    }
}
