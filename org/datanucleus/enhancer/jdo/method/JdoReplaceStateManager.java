// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.util.JavaUtils;
import org.datanucleus.asm.Label;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.ClassMethod;

public class JdoReplaceStateManager extends ClassMethod
{
    public static JdoReplaceStateManager getInstance(final ClassEnhancer enhancer) {
        return new JdoReplaceStateManager(enhancer, enhancer.getNamer().getReplaceStateManagerMethodName(), 49, null, new Class[] { enhancer.getNamer().getStateManagerClass() }, new String[] { "sm" });
    }
    
    public JdoReplaceStateManager(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
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
        this.visitor.visitJumpInsn(198, l1);
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getStateManagerFieldName(), this.getNamer().getStateManagerDescriptor());
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitVarInsn(25, 1);
        this.visitor.visitMethodInsn(185, this.getNamer().getStateManagerAsmClassName(), "replacingStateManager", "(" + this.getNamer().getPersistableDescriptor() + this.getNamer().getStateManagerDescriptor() + ")" + this.getNamer().getStateManagerDescriptor());
        this.visitor.visitFieldInsn(181, this.getClassEnhancer().getASMClassName(), this.getNamer().getStateManagerFieldName(), this.getNamer().getStateManagerDescriptor());
        final Label l2 = new Label();
        this.visitor.visitJumpInsn(167, l2);
        this.visitor.visitLabel(l1);
        if (JavaUtils.useStackMapFrames()) {
            this.visitor.visitFrame(3, 0, null, 0, null);
        }
        this.visitor.visitVarInsn(25, 1);
        this.visitor.visitMethodInsn(184, this.getNamer().getImplHelperAsmClassName(), "checkAuthorizedStateManager", "(L" + this.getNamer().getStateManagerAsmClassName() + ";)V");
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitVarInsn(25, 1);
        this.visitor.visitFieldInsn(181, this.getClassEnhancer().getASMClassName(), this.getNamer().getStateManagerFieldName(), this.getNamer().getStateManagerDescriptor());
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitInsn(4);
        this.visitor.visitFieldInsn(181, this.getClassEnhancer().getASMClassName(), this.getNamer().getFlagsFieldName(), "B");
        this.visitor.visitLabel(l2);
        if (JavaUtils.useStackMapFrames()) {
            this.visitor.visitFrame(3, 0, null, 0, null);
        }
        this.visitor.visitInsn(177);
        final Label endLabel = new Label();
        this.visitor.visitLabel(endLabel);
        this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel, 0);
        this.visitor.visitLocalVariable(this.argNames[0], this.getNamer().getStateManagerDescriptor(), null, startLabel, endLabel, 1);
        this.visitor.visitMaxs(4, 2);
        this.visitor.visitEnd();
    }
}
