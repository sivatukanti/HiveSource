// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.util.JavaUtils;
import org.datanucleus.asm.Label;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.ClassMethod;

public class JdoPreSerialize extends ClassMethod
{
    public static JdoPreSerialize getInstance(final ClassEnhancer enhancer) {
        return new JdoPreSerialize(enhancer, enhancer.getNamer().getPreSerializeMethodName(), 20, null, null, null);
    }
    
    public JdoPreSerialize(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
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
        this.visitor.visitMethodInsn(185, this.getNamer().getStateManagerAsmClassName(), "preSerialize", "(" + this.getNamer().getPersistableDescriptor() + ")V");
        this.visitor.visitLabel(l2);
        if (JavaUtils.useStackMapFrames()) {
            this.visitor.visitFrame(3, 0, null, 0, null);
        }
        this.visitor.visitInsn(177);
        final Label l3 = new Label();
        this.visitor.visitLabel(l3);
        this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, l0, l3, 0);
        this.visitor.visitMaxs(2, 1);
        this.visitor.visitEnd();
    }
}
