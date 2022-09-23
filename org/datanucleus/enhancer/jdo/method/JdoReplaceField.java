// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.asm.Type;
import org.datanucleus.metadata.PropertyMetaData;
import org.datanucleus.enhancer.EnhanceUtils;
import org.datanucleus.util.JavaUtils;
import org.datanucleus.asm.Label;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.ClassMethod;

public class JdoReplaceField extends ClassMethod
{
    public static JdoReplaceField getInstance(final ClassEnhancer enhancer) {
        return new JdoReplaceField(enhancer, enhancer.getNamer().getReplaceFieldMethodName(), 1, null, new Class[] { Integer.TYPE }, new String[] { "index" });
    }
    
    public JdoReplaceField(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
        super(enhancer, name, access, returnType, argTypes, argNames);
    }
    
    @Override
    public void execute() {
        final AbstractMemberMetaData[] fields = this.enhancer.getClassMetaData().getManagedMembers();
        final String pcSuperclassName = this.enhancer.getClassMetaData().getPersistenceCapableSuperclass();
        this.visitor.visitCode();
        final Label startLabel = new Label();
        this.visitor.visitLabel(startLabel);
        if (pcSuperclassName != null) {
            if (fields.length > 0) {
                this.visitor.visitVarInsn(25, 0);
                this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getStateManagerFieldName(), this.getNamer().getStateManagerDescriptor());
                final Label l1 = new Label();
                this.visitor.visitJumpInsn(199, l1);
                this.visitor.visitTypeInsn(187, "java/lang/IllegalStateException");
                this.visitor.visitInsn(89);
                this.visitor.visitLdcInsn("state manager is null");
                this.visitor.visitMethodInsn(183, "java/lang/IllegalStateException", "<init>", "(Ljava/lang/String;)V");
                this.visitor.visitInsn(191);
                this.visitor.visitLabel(l1);
                if (JavaUtils.useStackMapFrames()) {
                    this.visitor.visitFrame(3, 0, null, 0, null);
                }
                this.visitor.visitVarInsn(21, 1);
                this.visitor.visitFieldInsn(178, this.getClassEnhancer().getASMClassName(), this.getNamer().getInheritedFieldCountFieldName(), "I");
                this.visitor.visitInsn(100);
                final Label[] fieldOptions = new Label[fields.length];
                for (int i = 0; i < fields.length; ++i) {
                    fieldOptions[i] = new Label();
                }
                final Label defaultLabel = new Label();
                final Label endSwitchLabel = new Label();
                this.visitor.visitTableSwitchInsn(0, fields.length - 1, defaultLabel, fieldOptions);
                for (int j = 0; j < fields.length; ++j) {
                    this.visitor.visitLabel(fieldOptions[j]);
                    if (JavaUtils.useStackMapFrames()) {
                        this.visitor.visitFrame(3, 0, null, 0, null);
                    }
                    this.visitor.visitVarInsn(25, 0);
                    this.visitor.visitVarInsn(25, 0);
                    this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getStateManagerFieldName(), this.getNamer().getStateManagerDescriptor());
                    this.visitor.visitVarInsn(25, 0);
                    this.visitor.visitVarInsn(21, 1);
                    final String methodNameType = EnhanceUtils.getTypeNameForJDOMethod(fields[j].getType());
                    this.visitor.visitMethodInsn(185, this.getNamer().getStateManagerAsmClassName(), "replacing" + methodNameType + "Field", "(" + this.getNamer().getPersistableDescriptor() + "I)" + EnhanceUtils.getTypeDescriptorForJDOMethod(fields[j].getType()));
                    if (methodNameType.equals("Object")) {
                        this.visitor.visitTypeInsn(192, fields[j].getTypeName().replace('.', '/'));
                    }
                    if (fields[j] instanceof PropertyMetaData) {
                        this.visitor.visitMethodInsn(182, this.getClassEnhancer().getASMClassName(), this.getNamer().getSetMethodPrefixMethodName() + fields[j].getName(), "(" + Type.getDescriptor(fields[j].getType()) + ")V");
                    }
                    else {
                        this.visitor.visitFieldInsn(181, this.getClassEnhancer().getASMClassName(), fields[j].getName(), Type.getDescriptor(fields[j].getType()));
                    }
                    this.visitor.visitJumpInsn(167, endSwitchLabel);
                }
                this.visitor.visitLabel(defaultLabel);
                if (JavaUtils.useStackMapFrames()) {
                    this.visitor.visitFrame(3, 0, null, 0, null);
                }
                this.visitor.visitVarInsn(25, 0);
                this.visitor.visitVarInsn(21, 1);
                this.visitor.visitMethodInsn(183, pcSuperclassName.replace('.', '/'), this.getNamer().getReplaceFieldMethodName(), "(I)V");
                this.visitor.visitLabel(endSwitchLabel);
                if (JavaUtils.useStackMapFrames()) {
                    this.visitor.visitFrame(3, 0, null, 0, null);
                }
                this.visitor.visitInsn(177);
                final Label endLabel = new Label();
                this.visitor.visitLabel(endLabel);
                this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel, 0);
                this.visitor.visitLocalVariable(this.argNames[0], "I", null, startLabel, endLabel, 1);
                this.visitor.visitMaxs(4, 2);
            }
            else {
                this.visitor.visitVarInsn(25, 0);
                this.visitor.visitVarInsn(21, 1);
                this.visitor.visitMethodInsn(183, pcSuperclassName.replace('.', '/'), this.getNamer().getReplaceFieldMethodName(), "(I)V");
                this.visitor.visitInsn(177);
                final Label endLabel2 = new Label();
                this.visitor.visitLabel(endLabel2);
                this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel2, 0);
                this.visitor.visitLocalVariable(this.argNames[0], "I", null, startLabel, endLabel2, 1);
                this.visitor.visitMaxs(2, 2);
            }
        }
        else if (fields.length > 0) {
            this.visitor.visitVarInsn(25, 0);
            this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getStateManagerFieldName(), this.getNamer().getStateManagerDescriptor());
            final Label l1 = new Label();
            this.visitor.visitJumpInsn(199, l1);
            this.visitor.visitTypeInsn(187, "java/lang/IllegalStateException");
            this.visitor.visitInsn(89);
            this.visitor.visitLdcInsn("state manager is null");
            this.visitor.visitMethodInsn(183, "java/lang/IllegalStateException", "<init>", "(Ljava/lang/String;)V");
            this.visitor.visitInsn(191);
            this.visitor.visitLabel(l1);
            if (JavaUtils.useStackMapFrames()) {
                this.visitor.visitFrame(3, 0, null, 0, null);
            }
            this.visitor.visitVarInsn(21, 1);
            final Label[] fieldOptions = new Label[fields.length];
            for (int i = 0; i < fields.length; ++i) {
                fieldOptions[i] = new Label();
            }
            final Label defaultLabel = new Label();
            final Label endSwitchLabel = new Label();
            this.visitor.visitTableSwitchInsn(0, fields.length - 1, defaultLabel, fieldOptions);
            for (int j = 0; j < fields.length; ++j) {
                this.visitor.visitLabel(fieldOptions[j]);
                if (JavaUtils.useStackMapFrames()) {
                    this.visitor.visitFrame(3, 0, null, 0, null);
                }
                this.visitor.visitVarInsn(25, 0);
                this.visitor.visitVarInsn(25, 0);
                this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getStateManagerFieldName(), this.getNamer().getStateManagerDescriptor());
                this.visitor.visitVarInsn(25, 0);
                this.visitor.visitVarInsn(21, 1);
                final String methodNameType = EnhanceUtils.getTypeNameForJDOMethod(fields[j].getType());
                this.visitor.visitMethodInsn(185, this.getNamer().getStateManagerAsmClassName(), "replacing" + methodNameType + "Field", "(" + this.getNamer().getPersistableDescriptor() + "I)" + EnhanceUtils.getTypeDescriptorForJDOMethod(fields[j].getType()));
                if (methodNameType.equals("Object")) {
                    this.visitor.visitTypeInsn(192, fields[j].getTypeName().replace('.', '/'));
                }
                if (fields[j] instanceof PropertyMetaData) {
                    this.visitor.visitMethodInsn(182, this.getClassEnhancer().getASMClassName(), this.getNamer().getSetMethodPrefixMethodName() + fields[j].getName(), "(" + Type.getDescriptor(fields[j].getType()) + ")V");
                }
                else {
                    this.visitor.visitFieldInsn(181, this.getClassEnhancer().getASMClassName(), fields[j].getName(), Type.getDescriptor(fields[j].getType()));
                }
                this.visitor.visitJumpInsn(167, endSwitchLabel);
            }
            this.visitor.visitLabel(defaultLabel);
            if (JavaUtils.useStackMapFrames()) {
                this.visitor.visitFrame(3, 0, null, 0, null);
            }
            this.visitor.visitTypeInsn(187, "java/lang/IllegalArgumentException");
            this.visitor.visitInsn(89);
            this.visitor.visitTypeInsn(187, "java/lang/StringBuffer");
            this.visitor.visitInsn(89);
            this.visitor.visitLdcInsn("out of field index :");
            this.visitor.visitMethodInsn(183, "java/lang/StringBuffer", "<init>", "(Ljava/lang/String;)V");
            this.visitor.visitVarInsn(21, 1);
            this.visitor.visitMethodInsn(182, "java/lang/StringBuffer", "append", "(I)Ljava/lang/StringBuffer;");
            this.visitor.visitMethodInsn(182, "java/lang/StringBuffer", "toString", "()Ljava/lang/String;");
            this.visitor.visitMethodInsn(183, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
            this.visitor.visitInsn(191);
            this.visitor.visitLabel(endSwitchLabel);
            if (JavaUtils.useStackMapFrames()) {
                this.visitor.visitFrame(3, 0, null, 0, null);
            }
            this.visitor.visitInsn(177);
            final Label endLabel = new Label();
            this.visitor.visitLabel(endLabel);
            this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel, 0);
            this.visitor.visitLocalVariable(this.argNames[0], "I", null, startLabel, endLabel, 1);
            this.visitor.visitMaxs(5, 2);
        }
        else {
            this.visitor.visitInsn(177);
            final Label endLabel2 = new Label();
            this.visitor.visitLabel(endLabel2);
            this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel2, 0);
            this.visitor.visitLocalVariable(this.argNames[0], "I", null, startLabel, endLabel2, 1);
            this.visitor.visitMaxs(0, 2);
        }
        this.visitor.visitEnd();
    }
}
