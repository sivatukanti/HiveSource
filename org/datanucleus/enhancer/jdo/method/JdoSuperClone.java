// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.enhancer.EnhanceUtils;
import org.datanucleus.asm.Label;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.ClassMethod;

public class JdoSuperClone extends ClassMethod
{
    public static JdoSuperClone getInstance(final ClassEnhancer enhancer) {
        return new JdoSuperClone(enhancer, enhancer.getNamer().getSuperCloneMethodName(), 2, Object.class, null, null, new String[] { CloneNotSupportedException.class.getName().replace('.', '/') });
    }
    
    public JdoSuperClone(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames, final String[] exceptions) {
        super(enhancer, name, access, returnType, argTypes, argNames, exceptions);
    }
    
    @Override
    public void execute() {
        this.visitor.visitCode();
        final Label l0 = new Label();
        this.visitor.visitLabel(l0);
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitMethodInsn(183, EnhanceUtils.ACN_Object, "clone", "()" + EnhanceUtils.CD_Object);
        this.visitor.visitTypeInsn(192, this.getClassEnhancer().getASMClassName());
        this.visitor.visitVarInsn(58, 1);
        final Label l2 = new Label();
        this.visitor.visitLabel(l2);
        this.visitor.visitVarInsn(25, 1);
        this.visitor.visitInsn(3);
        this.visitor.visitFieldInsn(181, this.getClassEnhancer().getASMClassName(), this.getNamer().getFlagsFieldName(), "B");
        this.visitor.visitVarInsn(25, 1);
        this.visitor.visitInsn(1);
        this.visitor.visitFieldInsn(181, this.getClassEnhancer().getASMClassName(), this.getNamer().getStateManagerFieldName(), this.getNamer().getStateManagerDescriptor());
        this.visitor.visitVarInsn(25, 1);
        this.visitor.visitInsn(176);
        final Label l3 = new Label();
        this.visitor.visitLabel(l3);
        this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, l0, l3, 0);
        this.visitor.visitLocalVariable("o", this.getClassEnhancer().getClassDescriptor(), null, l2, l3, 1);
        this.visitor.visitMaxs(2, 2);
        this.visitor.visitEnd();
    }
}
