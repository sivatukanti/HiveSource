// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.asm.Opcodes;
import org.datanucleus.util.JavaUtils;
import org.datanucleus.asm.Label;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.ClassMethod;

public class JdoMakeDirty extends ClassMethod
{
    public static JdoMakeDirty getInstance(final ClassEnhancer enhancer) {
        return new JdoMakeDirty(enhancer, enhancer.getNamer().getMakeDirtyMethodName(), 1, null, new Class[] { String.class }, new String[] { "fieldName" });
    }
    
    public JdoMakeDirty(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
        super(enhancer, name, access, returnType, argTypes, argNames);
    }
    
    @Override
    public void execute() {
        final AbstractClassMetaData cmd = this.getClassEnhancer().getClassMetaData();
        final String pcSuperclassName = cmd.getPersistenceCapableSuperclass();
        this.visitor.visitCode();
        final Label startLabel = new Label();
        this.visitor.visitLabel(startLabel);
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getStateManagerFieldName(), this.getNamer().getStateManagerDescriptor());
        final Label l1 = new Label();
        this.visitor.visitJumpInsn(198, l1);
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getStateManagerFieldName(), this.getNamer().getStateManagerDescriptor());
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitVarInsn(25, 1);
        this.visitor.visitMethodInsn(185, this.getNamer().getStateManagerAsmClassName(), "makeDirty", "(" + this.getNamer().getPersistableDescriptor() + "Ljava/lang/String;" + ")V");
        this.visitor.visitLabel(l1);
        if (JavaUtils.useStackMapFrames()) {
            this.visitor.visitFrame(3, 0, null, 0, null);
        }
        if (cmd.isDetachable()) {
            this.visitor.visitVarInsn(25, 0);
            this.visitor.visitMethodInsn(182, this.getClassEnhancer().getASMClassName(), this.getNamer().getIsDetachedMethodName(), "()Z");
            final Label l2 = new Label();
            this.visitor.visitJumpInsn(153, l2);
            this.visitor.visitVarInsn(25, 1);
            this.visitor.visitJumpInsn(198, l2);
            this.visitor.visitInsn(1);
            this.visitor.visitVarInsn(58, 2);
            final Label l3 = new Label();
            this.visitor.visitLabel(l3);
            this.visitor.visitVarInsn(25, 1);
            this.visitor.visitIntInsn(16, 46);
            this.visitor.visitMethodInsn(182, "java/lang/String", "indexOf", "(I)I");
            final Label l4 = new Label();
            this.visitor.visitJumpInsn(155, l4);
            this.visitor.visitVarInsn(25, 1);
            this.visitor.visitVarInsn(25, 1);
            this.visitor.visitIntInsn(16, 46);
            this.visitor.visitMethodInsn(182, "java/lang/String", "lastIndexOf", "(I)I");
            this.visitor.visitInsn(4);
            this.visitor.visitInsn(96);
            this.visitor.visitMethodInsn(182, "java/lang/String", "substring", "(I)Ljava/lang/String;");
            this.visitor.visitVarInsn(58, 2);
            final Label l5 = new Label();
            this.visitor.visitJumpInsn(167, l5);
            this.visitor.visitLabel(l4);
            if (JavaUtils.useStackMapFrames()) {
                this.visitor.visitFrame(1, 1, new Object[] { "java/lang/String" }, 0, null);
            }
            this.visitor.visitVarInsn(25, 1);
            this.visitor.visitVarInsn(58, 2);
            this.visitor.visitLabel(l5);
            if (JavaUtils.useStackMapFrames()) {
                this.visitor.visitFrame(3, 0, null, 0, null);
            }
            this.visitor.visitInsn(3);
            this.visitor.visitVarInsn(54, 3);
            final Label l6 = new Label();
            this.visitor.visitLabel(l6);
            final Label l7 = new Label();
            this.visitor.visitJumpInsn(167, l7);
            final Label l8 = new Label();
            this.visitor.visitLabel(l8);
            if (JavaUtils.useStackMapFrames()) {
                this.visitor.visitFrame(1, 1, new Object[] { Opcodes.INTEGER }, 0, null);
            }
            this.visitor.visitFieldInsn(178, this.getClassEnhancer().getASMClassName(), this.getNamer().getFieldNamesFieldName(), "[Ljava/lang/String;");
            this.visitor.visitVarInsn(21, 3);
            this.visitor.visitInsn(50);
            this.visitor.visitVarInsn(25, 2);
            this.visitor.visitMethodInsn(182, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
            final Label l9 = new Label();
            this.visitor.visitJumpInsn(153, l9);
            this.visitor.visitVarInsn(25, 0);
            this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getDetachedStateFieldName(), "[Ljava/lang/Object;");
            this.visitor.visitInsn(5);
            this.visitor.visitInsn(50);
            this.visitor.visitTypeInsn(192, "java/util/BitSet");
            this.visitor.visitVarInsn(21, 3);
            this.visitor.visitFieldInsn(178, this.getClassEnhancer().getASMClassName(), this.getNamer().getInheritedFieldCountFieldName(), "I");
            this.visitor.visitInsn(96);
            this.visitor.visitMethodInsn(182, "java/util/BitSet", "get", "(I)Z");
            final Label l10 = new Label();
            this.visitor.visitJumpInsn(153, l10);
            this.visitor.visitVarInsn(25, 0);
            this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getDetachedStateFieldName(), "[Ljava/lang/Object;");
            this.visitor.visitInsn(6);
            this.visitor.visitInsn(50);
            this.visitor.visitTypeInsn(192, "java/util/BitSet");
            this.visitor.visitVarInsn(21, 3);
            this.visitor.visitFieldInsn(178, this.getClassEnhancer().getASMClassName(), this.getNamer().getInheritedFieldCountFieldName(), "I");
            this.visitor.visitInsn(96);
            this.visitor.visitMethodInsn(182, "java/util/BitSet", "set", "(I)V");
            this.visitor.visitInsn(177);
            this.visitor.visitLabel(l10);
            if (JavaUtils.useStackMapFrames()) {
                this.visitor.visitFrame(3, 0, null, 0, null);
            }
            if (this.enhancer.hasOption("generate-detach-listener")) {
                this.visitor.visitMethodInsn(184, this.getNamer().getDetachListenerAsmClassName(), "getInstance", "()L" + this.getNamer().getDetachListenerAsmClassName() + ";");
                this.visitor.visitVarInsn(25, 0);
                this.visitor.visitLdcInsn("field/property");
                this.visitor.visitMethodInsn(182, this.getNamer().getDetachListenerAsmClassName(), "undetachedFieldAccess", "(Ljava/lang/Object;Ljava/lang/String;)V");
            }
            else {
                this.visitor.visitTypeInsn(187, this.getNamer().getDetachedFieldAccessExceptionAsmClassName());
                this.visitor.visitInsn(89);
                this.visitor.visitLdcInsn("You have just attempted to access a field/property that hasn't been detached. Please detach it first before performing this operation");
                this.visitor.visitMethodInsn(183, this.getNamer().getDetachedFieldAccessExceptionAsmClassName(), "<init>", "(Ljava/lang/String;)V");
                this.visitor.visitInsn(191);
            }
            this.visitor.visitLabel(l9);
            if (JavaUtils.useStackMapFrames()) {
                this.visitor.visitFrame(3, 0, null, 0, null);
            }
            this.visitor.visitIincInsn(3, 1);
            this.visitor.visitLabel(l7);
            if (JavaUtils.useStackMapFrames()) {
                this.visitor.visitFrame(3, 0, null, 0, null);
            }
            this.visitor.visitVarInsn(21, 3);
            this.visitor.visitFieldInsn(178, this.getClassEnhancer().getASMClassName(), this.getNamer().getFieldNamesFieldName(), "[Ljava/lang/String;");
            this.visitor.visitInsn(190);
            this.visitor.visitJumpInsn(161, l8);
            this.visitor.visitLabel(l2);
            if (JavaUtils.useStackMapFrames()) {
                this.visitor.visitFrame(2, 2, null, 0, null);
            }
            if (pcSuperclassName != null) {
                this.visitor.visitVarInsn(25, 0);
                this.visitor.visitVarInsn(25, 1);
                this.visitor.visitMethodInsn(183, pcSuperclassName.replace('.', '/'), this.getNamer().getMakeDirtyMethodName(), "(Ljava/lang/String;)V");
            }
            this.visitor.visitInsn(177);
            final Label endLabel = new Label();
            this.visitor.visitLabel(endLabel);
            this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel, 0);
            this.visitor.visitLocalVariable(this.argNames[0], "Ljava/lang/String;", null, startLabel, endLabel, 1);
            this.visitor.visitLocalVariable("fldName", "Ljava/lang/String;", null, l3, l2, 2);
            this.visitor.visitLocalVariable("i", "I", null, l6, l2, 3);
            this.visitor.visitMaxs(3, 4);
        }
        else {
            if (JavaUtils.useStackMapFrames()) {
                this.visitor.visitFrame(3, 0, null, 0, null);
            }
            this.visitor.visitInsn(177);
            final Label endLabel2 = new Label();
            this.visitor.visitLabel(endLabel2);
            this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel2, 0);
            this.visitor.visitLocalVariable(this.argNames[0], "Ljava/lang/String;", null, startLabel, endLabel2, 1);
            this.visitor.visitMaxs(3, 2);
        }
        this.visitor.visitEnd();
    }
}
