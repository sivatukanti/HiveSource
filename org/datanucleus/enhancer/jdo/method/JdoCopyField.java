// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.PropertyMetaData;
import org.datanucleus.util.JavaUtils;
import org.datanucleus.asm.Type;
import org.datanucleus.asm.Label;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.ClassMethod;

public class JdoCopyField extends ClassMethod
{
    public static JdoCopyField getInstance(final ClassEnhancer enhancer) {
        return new JdoCopyField(enhancer, enhancer.getNamer().getCopyFieldMethodName(), 20, null, new Class[] { enhancer.getClassBeingEnhanced(), Integer.TYPE }, new String[] { "obj", "index" });
    }
    
    public JdoCopyField(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
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
            final Class supercls = this.enhancer.getClassLoaderResolver().classForName(pcSuperclassName);
            final String superclsDescriptor = Type.getDescriptor(supercls);
            if (fields.length > 0) {
                this.visitor.visitVarInsn(21, 2);
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
                    this.visitor.visitVarInsn(25, 1);
                    if (fields[j] instanceof PropertyMetaData) {
                        this.visitor.visitMethodInsn(182, this.getClassEnhancer().getASMClassName(), this.getNamer().getGetMethodPrefixMethodName() + fields[j].getName(), "()" + Type.getDescriptor(fields[j].getType()));
                        this.visitor.visitMethodInsn(182, this.getClassEnhancer().getASMClassName(), this.getNamer().getSetMethodPrefixMethodName() + fields[j].getName(), "(" + Type.getDescriptor(fields[j].getType()) + ")V");
                    }
                    else {
                        this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), fields[j].getName(), Type.getDescriptor(fields[j].getType()));
                        this.visitor.visitFieldInsn(181, this.getClassEnhancer().getASMClassName(), fields[j].getName(), Type.getDescriptor(fields[j].getType()));
                    }
                    this.visitor.visitJumpInsn(167, endSwitchLabel);
                }
                this.visitor.visitLabel(defaultLabel);
                if (JavaUtils.useStackMapFrames()) {
                    this.visitor.visitFrame(3, 0, null, 0, null);
                }
                this.visitor.visitVarInsn(25, 0);
                this.visitor.visitVarInsn(25, 1);
                this.visitor.visitVarInsn(21, 2);
                this.visitor.visitMethodInsn(183, pcSuperclassName.replace('.', '/'), this.getNamer().getCopyFieldMethodName(), "(" + superclsDescriptor + "I)V");
                this.visitor.visitLabel(endSwitchLabel);
                if (JavaUtils.useStackMapFrames()) {
                    this.visitor.visitFrame(3, 0, null, 0, null);
                }
                this.visitor.visitInsn(177);
            }
            else {
                this.visitor.visitVarInsn(25, 0);
                this.visitor.visitVarInsn(25, 1);
                this.visitor.visitVarInsn(21, 2);
                this.visitor.visitMethodInsn(183, pcSuperclassName.replace('.', '/'), this.getNamer().getCopyFieldMethodName(), "(" + superclsDescriptor + "I)V");
                this.visitor.visitInsn(177);
            }
        }
        else if (fields.length > 0) {
            this.visitor.visitVarInsn(21, 2);
            final Label[] fieldOptions2 = new Label[fields.length];
            for (int k = 0; k < fields.length; ++k) {
                fieldOptions2[k] = new Label();
            }
            final Label defaultLabel2 = new Label();
            final Label endSwitchLabel2 = new Label();
            this.visitor.visitTableSwitchInsn(0, fields.length - 1, defaultLabel2, fieldOptions2);
            for (int i = 0; i < fields.length; ++i) {
                this.visitor.visitLabel(fieldOptions2[i]);
                if (JavaUtils.useStackMapFrames()) {
                    this.visitor.visitFrame(3, 0, null, 0, null);
                }
                this.visitor.visitVarInsn(25, 0);
                this.visitor.visitVarInsn(25, 1);
                if (fields[i] instanceof PropertyMetaData) {
                    this.visitor.visitMethodInsn(182, this.getClassEnhancer().getASMClassName(), this.getNamer().getGetMethodPrefixMethodName() + fields[i].getName(), "()" + Type.getDescriptor(fields[i].getType()));
                    this.visitor.visitMethodInsn(182, this.getClassEnhancer().getASMClassName(), this.getNamer().getSetMethodPrefixMethodName() + fields[i].getName(), "(" + Type.getDescriptor(fields[i].getType()) + ")V");
                }
                else {
                    this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), fields[i].getName(), Type.getDescriptor(fields[i].getType()));
                    this.visitor.visitFieldInsn(181, this.getClassEnhancer().getASMClassName(), fields[i].getName(), Type.getDescriptor(fields[i].getType()));
                }
                this.visitor.visitJumpInsn(167, endSwitchLabel2);
            }
            this.visitor.visitLabel(defaultLabel2);
            if (JavaUtils.useStackMapFrames()) {
                this.visitor.visitFrame(3, 0, null, 0, null);
            }
            this.visitor.visitTypeInsn(187, "java/lang/IllegalArgumentException");
            this.visitor.visitInsn(89);
            this.visitor.visitTypeInsn(187, "java/lang/StringBuffer");
            this.visitor.visitInsn(89);
            this.visitor.visitLdcInsn("out of field index :");
            this.visitor.visitMethodInsn(183, "java/lang/StringBuffer", "<init>", "(Ljava/lang/String;)V");
            this.visitor.visitVarInsn(21, 2);
            this.visitor.visitMethodInsn(182, "java/lang/StringBuffer", "append", "(I)Ljava/lang/StringBuffer;");
            this.visitor.visitMethodInsn(182, "java/lang/StringBuffer", "toString", "()Ljava/lang/String;");
            this.visitor.visitMethodInsn(183, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
            this.visitor.visitInsn(191);
            this.visitor.visitLabel(endSwitchLabel2);
            if (JavaUtils.useStackMapFrames()) {
                this.visitor.visitFrame(3, 0, null, 0, null);
            }
            this.visitor.visitInsn(177);
        }
        else {
            this.visitor.visitTypeInsn(187, "java/lang/IllegalArgumentException");
            this.visitor.visitInsn(89);
            this.visitor.visitTypeInsn(187, "java/lang/StringBuffer");
            this.visitor.visitInsn(89);
            this.visitor.visitLdcInsn("out of field index :");
            this.visitor.visitMethodInsn(183, "java/lang/StringBuffer", "<init>", "(Ljava/lang/String;)V");
            this.visitor.visitVarInsn(21, 2);
            this.visitor.visitMethodInsn(182, "java/lang/StringBuffer", "append", "(I)Ljava/lang/StringBuffer;");
            this.visitor.visitMethodInsn(182, "java/lang/StringBuffer", "toString", "()Ljava/lang/String;");
            this.visitor.visitMethodInsn(183, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
            this.visitor.visitInsn(191);
        }
        final Label endLabel = new Label();
        this.visitor.visitLabel(endLabel);
        this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel, 0);
        this.visitor.visitLocalVariable(this.argNames[0], this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel, 1);
        this.visitor.visitLocalVariable(this.argNames[1], "I", null, startLabel, endLabel, 2);
        if (pcSuperclassName != null) {
            this.visitor.visitMaxs(3, 3);
        }
        else {
            this.visitor.visitMaxs(5, 3);
        }
        this.visitor.visitEnd();
    }
}
