// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.enhancer.EnhanceUtils;
import org.datanucleus.asm.Opcodes;
import org.datanucleus.util.JavaUtils;
import org.datanucleus.asm.Label;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.ClassMethod;

public class JdoCopyFields extends ClassMethod
{
    public static JdoCopyFields getInstance(final ClassEnhancer enhancer) {
        return new JdoCopyFields(enhancer, enhancer.getNamer().getCopyFieldsMethodName(), 1, null, new Class[] { Object.class, int[].class }, new String[] { "obj", "indices" });
    }
    
    public JdoCopyFields(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
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
        this.visitor.visitJumpInsn(199, l2);
        this.visitor.visitTypeInsn(187, "java/lang/IllegalStateException");
        this.visitor.visitInsn(89);
        this.visitor.visitLdcInsn("state manager is null");
        this.visitor.visitMethodInsn(183, "java/lang/IllegalStateException", "<init>", "(Ljava/lang/String;)V");
        this.visitor.visitInsn(191);
        this.visitor.visitLabel(l2);
        if (JavaUtils.useStackMapFrames()) {
            this.visitor.visitFrame(3, 0, null, 0, null);
        }
        this.visitor.visitVarInsn(25, 2);
        final Label l3 = new Label();
        this.visitor.visitJumpInsn(199, l3);
        this.visitor.visitTypeInsn(187, "java/lang/IllegalStateException");
        this.visitor.visitInsn(89);
        this.visitor.visitLdcInsn("fieldNumbers is null");
        this.visitor.visitMethodInsn(183, "java/lang/IllegalStateException", "<init>", "(Ljava/lang/String;)V");
        this.visitor.visitInsn(191);
        this.visitor.visitLabel(l3);
        if (JavaUtils.useStackMapFrames()) {
            this.visitor.visitFrame(3, 0, null, 0, null);
        }
        this.visitor.visitVarInsn(25, 1);
        this.visitor.visitTypeInsn(193, this.getClassEnhancer().getASMClassName());
        final Label l4 = new Label();
        this.visitor.visitJumpInsn(154, l4);
        this.visitor.visitTypeInsn(187, "java/lang/IllegalArgumentException");
        this.visitor.visitInsn(89);
        this.visitor.visitLdcInsn("object is not an object of type " + this.getClassEnhancer().getASMClassName().replace('/', '.'));
        this.visitor.visitMethodInsn(183, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
        this.visitor.visitInsn(191);
        this.visitor.visitLabel(l4);
        if (JavaUtils.useStackMapFrames()) {
            this.visitor.visitFrame(3, 0, null, 0, null);
        }
        this.visitor.visitVarInsn(25, 1);
        this.visitor.visitTypeInsn(192, this.getClassEnhancer().getASMClassName());
        this.visitor.visitVarInsn(58, 3);
        final Label l5 = new Label();
        this.visitor.visitLabel(l5);
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getStateManagerFieldName(), this.getNamer().getStateManagerDescriptor());
        this.visitor.visitVarInsn(25, 3);
        this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getStateManagerFieldName(), this.getNamer().getStateManagerDescriptor());
        final Label l6 = new Label();
        this.visitor.visitJumpInsn(165, l6);
        this.visitor.visitTypeInsn(187, "java/lang/IllegalArgumentException");
        this.visitor.visitInsn(89);
        this.visitor.visitLdcInsn("state managers do not match");
        this.visitor.visitMethodInsn(183, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
        this.visitor.visitInsn(191);
        this.visitor.visitLabel(l6);
        if (JavaUtils.useStackMapFrames()) {
            this.visitor.visitFrame(1, 1, new Object[] { this.getClassEnhancer().getASMClassName() }, 0, null);
        }
        this.visitor.visitVarInsn(25, 2);
        this.visitor.visitInsn(190);
        this.visitor.visitInsn(4);
        this.visitor.visitInsn(100);
        this.visitor.visitVarInsn(54, 4);
        final Label l7 = new Label();
        this.visitor.visitLabel(l7);
        this.visitor.visitVarInsn(21, 4);
        final Label l8 = new Label();
        this.visitor.visitJumpInsn(155, l8);
        final Label l9 = new Label();
        this.visitor.visitLabel(l9);
        if (JavaUtils.useStackMapFrames()) {
            this.visitor.visitFrame(1, 1, new Object[] { Opcodes.INTEGER }, 0, null);
        }
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitVarInsn(25, 3);
        this.visitor.visitVarInsn(25, 2);
        this.visitor.visitVarInsn(21, 4);
        this.visitor.visitInsn(46);
        this.visitor.visitMethodInsn(182, this.getClassEnhancer().getASMClassName(), this.getNamer().getCopyFieldMethodName(), "(" + this.getClassEnhancer().getClassDescriptor() + "I)V");
        this.visitor.visitIincInsn(4, -1);
        this.visitor.visitVarInsn(21, 4);
        this.visitor.visitJumpInsn(156, l9);
        this.visitor.visitLabel(l8);
        if (JavaUtils.useStackMapFrames()) {
            this.visitor.visitFrame(3, 0, null, 0, null);
        }
        this.visitor.visitInsn(177);
        final Label l10 = new Label();
        this.visitor.visitLabel(l10);
        this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, l0, l10, 0);
        this.visitor.visitLocalVariable(this.argNames[0], EnhanceUtils.CD_Object, null, l0, l10, 1);
        this.visitor.visitLocalVariable(this.argNames[1], "[I", null, l0, l10, 2);
        this.visitor.visitLocalVariable("other", this.getClassEnhancer().getClassDescriptor(), null, l5, l10, 3);
        this.visitor.visitLocalVariable("i", "I", null, l7, l10, 4);
        this.visitor.visitMaxs(4, 5);
        this.visitor.visitEnd();
    }
}
