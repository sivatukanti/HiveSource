// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.util.JavaUtils;
import org.datanucleus.asm.Label;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.ClassMethod;

public class JdoIsDetached extends ClassMethod
{
    public static JdoIsDetached getInstance(final ClassEnhancer enhancer) {
        return new JdoIsDetached(enhancer, enhancer.getNamer().getIsDetachedMethodName(), 1, Boolean.TYPE, null, null);
    }
    
    public JdoIsDetached(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
        super(enhancer, name, access, returnType, argTypes, argNames);
    }
    
    @Override
    public void execute() {
        this.visitor.visitCode();
        final Label startLabel = new Label();
        this.visitor.visitLabel(startLabel);
        if (this.getClassEnhancer().getClassMetaData().isDetachable()) {
            this.visitor.visitVarInsn(25, 0);
            this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getStateManagerFieldName(), "L" + this.getNamer().getStateManagerAsmClassName() + ";");
            final Label l1 = new Label();
            this.visitor.visitJumpInsn(199, l1);
            this.visitor.visitVarInsn(25, 0);
            this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getDetachedStateFieldName(), "[Ljava/lang/Object;");
            this.visitor.visitJumpInsn(198, l1);
            this.visitor.visitInsn(4);
            this.visitor.visitInsn(172);
            this.visitor.visitLabel(l1);
            if (JavaUtils.useStackMapFrames()) {
                this.visitor.visitFrame(3, 0, null, 0, null);
            }
        }
        this.visitor.visitInsn(3);
        this.visitor.visitInsn(172);
        final Label endLabel = new Label();
        this.visitor.visitLabel(endLabel);
        this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel, 0);
        this.visitor.visitMaxs(1, 1);
        this.visitor.visitEnd();
    }
}
