// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.util.JavaUtils;
import org.datanucleus.asm.Label;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.ClassMethod;

public class JdoGetPersistenceManager extends ClassMethod
{
    public static JdoGetPersistenceManager getInstance(final ClassEnhancer enhancer) {
        return new JdoGetPersistenceManager(enhancer, enhancer.getNamer().getGetPersistenceManagerMethodName(), 17, enhancer.getNamer().getPersistenceManagerClass(), null, null);
    }
    
    public JdoGetPersistenceManager(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
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
        this.visitor.visitMethodInsn(185, this.getNamer().getStateManagerAsmClassName(), "getPersistenceManager", "(" + this.getNamer().getPersistableDescriptor() + ")" + this.getNamer().getPersistenceManagerDescriptor());
        final Label l3 = new Label();
        this.visitor.visitJumpInsn(167, l3);
        this.visitor.visitLabel(l2);
        if (JavaUtils.useStackMapFrames()) {
            this.visitor.visitFrame(3, 0, null, 0, null);
        }
        this.visitor.visitInsn(1);
        this.visitor.visitLabel(l3);
        if (JavaUtils.useStackMapFrames()) {
            this.visitor.visitFrame(4, 0, null, 1, new Object[] { this.getClassEnhancer().getNamer().getPersistenceManagerAsmClassName() });
        }
        this.visitor.visitInsn(176);
        final Label l4 = new Label();
        this.visitor.visitLabel(l4);
        this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, l0, l4, 0);
        this.visitor.visitMaxs(2, 1);
        this.visitor.visitEnd();
    }
}
