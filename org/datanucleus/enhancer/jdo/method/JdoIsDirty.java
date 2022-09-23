// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.enhancer.EnhanceUtils;
import org.datanucleus.util.JavaUtils;
import org.datanucleus.asm.Label;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.ClassMethod;

public class JdoIsDirty extends ClassMethod
{
    public static JdoIsDirty getInstance(final ClassEnhancer enhancer) {
        return new JdoIsDirty(enhancer, enhancer.getNamer().getIsDirtyMethodName(), 17, Boolean.TYPE, null, null);
    }
    
    public JdoIsDirty(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
        super(enhancer, name, access, returnType, argTypes, argNames);
    }
    
    @Override
    public void execute() {
        this.visitor.visitCode();
        final boolean detachable = this.enhancer.getClassMetaData().isDetachable();
        final Label startLabel = new Label();
        this.visitor.visitLabel(startLabel);
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getStateManagerFieldName(), this.getNamer().getStateManagerDescriptor());
        final Label l1 = new Label();
        this.visitor.visitJumpInsn(198, l1);
        final Label l2 = new Label();
        this.visitor.visitLabel(l2);
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getStateManagerFieldName(), this.getNamer().getStateManagerDescriptor());
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitMethodInsn(185, this.getNamer().getStateManagerAsmClassName(), "isDirty", "(" + this.getNamer().getPersistableDescriptor() + ")Z");
        this.visitor.visitInsn(172);
        this.visitor.visitLabel(l1);
        if (JavaUtils.useStackMapFrames()) {
            this.visitor.visitFrame(3, 0, null, 0, null);
        }
        if (!detachable) {
            this.visitor.visitInsn(3);
            this.visitor.visitInsn(172);
        }
        else {
            this.visitor.visitVarInsn(25, 0);
            this.visitor.visitMethodInsn(182, this.getClassEnhancer().getASMClassName(), this.getNamer().getIsDetachedMethodName(), "()Z");
            final Label l3 = new Label();
            this.visitor.visitJumpInsn(154, l3);
            this.visitor.visitInsn(3);
            this.visitor.visitInsn(172);
            this.visitor.visitLabel(l3);
            if (JavaUtils.useStackMapFrames()) {
                this.visitor.visitFrame(3, 0, null, 0, null);
            }
            this.visitor.visitVarInsn(25, 0);
            this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getDetachedStateFieldName(), "[" + EnhanceUtils.CD_Object);
            this.visitor.visitInsn(6);
            this.visitor.visitInsn(50);
            this.visitor.visitTypeInsn(192, "java/util/BitSet");
            this.visitor.visitMethodInsn(182, "java/util/BitSet", "length", "()I");
            final Label l4 = new Label();
            this.visitor.visitJumpInsn(157, l4);
            this.visitor.visitInsn(3);
            this.visitor.visitInsn(172);
            this.visitor.visitLabel(l4);
            if (JavaUtils.useStackMapFrames()) {
                this.visitor.visitFrame(3, 0, null, 0, null);
            }
            this.visitor.visitInsn(4);
            this.visitor.visitInsn(172);
        }
        final Label endLabel = new Label();
        this.visitor.visitLabel(endLabel);
        this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel, 0);
        this.visitor.visitMaxs(2, 1);
        this.visitor.visitEnd();
    }
}
