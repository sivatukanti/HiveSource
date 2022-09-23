// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.asm.Label;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.ClassMethod;

public class JdoNewInstance1 extends ClassMethod
{
    public static JdoNewInstance1 getInstance(final ClassEnhancer enhancer) {
        return new JdoNewInstance1(enhancer, enhancer.getNamer().getNewInstanceMethodName(), 1, enhancer.getNamer().getPersistableClass(), new Class[] { enhancer.getNamer().getStateManagerClass() }, new String[] { "sm" });
    }
    
    public JdoNewInstance1(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
        super(enhancer, name, access, returnType, argTypes, argNames);
    }
    
    @Override
    public void execute() {
        this.visitor.visitCode();
        final Label startLabel = new Label();
        this.visitor.visitLabel(startLabel);
        if (this.enhancer.getClassMetaData().isAbstract()) {
            this.visitor.visitTypeInsn(187, this.getNamer().getFatalInternalExceptionAsmClassName());
            this.visitor.visitInsn(89);
            this.visitor.visitLdcInsn("Cannot instantiate abstract class.");
            this.visitor.visitMethodInsn(183, this.getNamer().getFatalInternalExceptionAsmClassName(), "<init>", "(Ljava/lang/String;)V");
            this.visitor.visitInsn(191);
            final Label endLabel = new Label();
            this.visitor.visitLabel(endLabel);
            this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel, 0);
            this.visitor.visitLocalVariable(this.argNames[0], this.getNamer().getStateManagerDescriptor(), null, startLabel, endLabel, 1);
            this.visitor.visitMaxs(3, 2);
        }
        else {
            this.visitor.visitTypeInsn(187, this.getClassEnhancer().getASMClassName());
            this.visitor.visitInsn(89);
            this.visitor.visitMethodInsn(183, this.getClassEnhancer().getASMClassName(), "<init>", "()V");
            this.visitor.visitVarInsn(58, 2);
            final Label l1 = new Label();
            this.visitor.visitLabel(l1);
            this.visitor.visitVarInsn(25, 2);
            this.visitor.visitInsn(4);
            this.visitor.visitFieldInsn(181, this.getClassEnhancer().getASMClassName(), this.getNamer().getFlagsFieldName(), "B");
            this.visitor.visitVarInsn(25, 2);
            this.visitor.visitVarInsn(25, 1);
            this.visitor.visitFieldInsn(181, this.getClassEnhancer().getASMClassName(), this.getNamer().getStateManagerFieldName(), this.getNamer().getStateManagerDescriptor());
            this.visitor.visitVarInsn(25, 2);
            this.visitor.visitInsn(176);
            final Label endLabel2 = new Label();
            this.visitor.visitLabel(endLabel2);
            this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel2, 0);
            this.visitor.visitLocalVariable(this.argNames[0], this.getNamer().getStateManagerDescriptor(), null, startLabel, endLabel2, 1);
            this.visitor.visitLocalVariable("result", this.getClassEnhancer().getClassDescriptor(), null, l1, endLabel2, 2);
            this.visitor.visitMaxs(2, 3);
        }
        this.visitor.visitEnd();
    }
}
