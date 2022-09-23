// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.util.JavaUtils;
import org.datanucleus.enhancer.EnhanceUtils;
import org.datanucleus.asm.Label;
import org.datanucleus.asm.Type;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.enhancer.ClassMethod;

public class JdoGetViaCheck extends ClassMethod
{
    protected AbstractMemberMetaData fmd;
    
    public JdoGetViaCheck(final ClassEnhancer enhancer, final AbstractMemberMetaData fmd) {
        super(enhancer, enhancer.getNamer().getGetMethodPrefixMethodName() + fmd.getName(), (fmd.isPublic() ? 1 : 0) | (fmd.isProtected() ? 4 : 0) | (fmd.isPrivate() ? 2 : 0) | 0x8, fmd.getType(), null, null);
        this.argTypes = new Class[] { this.getClassEnhancer().getClassBeingEnhanced() };
        this.argNames = new String[] { "objPC" };
        this.fmd = fmd;
    }
    
    @Override
    public void execute() {
        this.visitor.visitCode();
        final String fieldTypeDesc = Type.getDescriptor(this.fmd.getType());
        final Label startLabel = new Label();
        this.visitor.visitLabel(startLabel);
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getFlagsFieldName(), "B");
        final Label l1 = new Label();
        this.visitor.visitJumpInsn(158, l1);
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getStateManagerFieldName(), "L" + this.getNamer().getStateManagerAsmClassName() + ";");
        this.visitor.visitJumpInsn(198, l1);
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getStateManagerFieldName(), "L" + this.getNamer().getStateManagerAsmClassName() + ";");
        this.visitor.visitVarInsn(25, 0);
        EnhanceUtils.addBIPUSHToMethod(this.visitor, this.fmd.getFieldId());
        if (this.enhancer.getClassMetaData().getPersistenceCapableSuperclass() != null) {
            this.visitor.visitFieldInsn(178, this.getClassEnhancer().getASMClassName(), this.getNamer().getInheritedFieldCountFieldName(), "I");
            this.visitor.visitInsn(96);
        }
        this.visitor.visitMethodInsn(185, this.getNamer().getStateManagerAsmClassName(), "isLoaded", "(L" + this.getNamer().getPersistableAsmClassName() + ";I)Z");
        this.visitor.visitJumpInsn(154, l1);
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getStateManagerFieldName(), "L" + this.getNamer().getStateManagerAsmClassName() + ";");
        this.visitor.visitVarInsn(25, 0);
        EnhanceUtils.addBIPUSHToMethod(this.visitor, this.fmd.getFieldId());
        if (this.enhancer.getClassMetaData().getPersistenceCapableSuperclass() != null) {
            this.visitor.visitFieldInsn(178, this.getClassEnhancer().getASMClassName(), this.getNamer().getInheritedFieldCountFieldName(), "I");
            this.visitor.visitInsn(96);
        }
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.fmd.getName(), fieldTypeDesc);
        final String jdoMethodName = "get" + EnhanceUtils.getTypeNameForJDOMethod(this.fmd.getType()) + "Field";
        String argTypeDesc = fieldTypeDesc;
        if (jdoMethodName.equals("getObjectField")) {
            argTypeDesc = EnhanceUtils.CD_Object;
        }
        this.visitor.visitMethodInsn(185, this.getNamer().getStateManagerAsmClassName(), jdoMethodName, "(L" + this.getNamer().getPersistableAsmClassName() + ";I" + argTypeDesc + ")" + argTypeDesc);
        if (jdoMethodName.equals("getObjectField")) {
            this.visitor.visitTypeInsn(192, this.fmd.getTypeName().replace('.', '/'));
        }
        EnhanceUtils.addReturnForType(this.visitor, this.fmd.getType());
        this.visitor.visitLabel(l1);
        if (JavaUtils.useStackMapFrames()) {
            this.visitor.visitFrame(3, 0, null, 0, null);
        }
        final Label l2 = new Label();
        if (this.enhancer.getClassMetaData().isDetachable()) {
            this.visitor.visitVarInsn(25, 0);
            this.visitor.visitMethodInsn(182, this.getClassEnhancer().getASMClassName(), this.getNamer().getIsDetachedMethodName(), "()Z");
            this.visitor.visitJumpInsn(153, l2);
            this.visitor.visitVarInsn(25, 0);
            this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.getNamer().getDetachedStateFieldName(), "[Ljava/lang/Object;");
            this.visitor.visitInsn(5);
            this.visitor.visitInsn(50);
            this.visitor.visitTypeInsn(192, "java/util/BitSet");
            EnhanceUtils.addBIPUSHToMethod(this.visitor, this.fmd.getFieldId());
            if (this.enhancer.getClassMetaData().getPersistenceCapableSuperclass() != null) {
                this.visitor.visitFieldInsn(178, this.getClassEnhancer().getASMClassName(), this.getNamer().getInheritedFieldCountFieldName(), "I");
                this.visitor.visitInsn(96);
            }
            this.visitor.visitMethodInsn(182, "java/util/BitSet", "get", "(I)Z");
            this.visitor.visitJumpInsn(154, l2);
            if (this.enhancer.hasOption("generate-detach-listener")) {
                this.visitor.visitMethodInsn(184, this.getNamer().getDetachListenerAsmClassName(), "getInstance", "()L" + this.getNamer().getDetachListenerAsmClassName() + ";");
                this.visitor.visitVarInsn(25, 0);
                this.visitor.visitLdcInsn(this.fmd.getName());
                this.visitor.visitMethodInsn(182, this.getNamer().getDetachListenerAsmClassName(), "undetachedFieldAccess", "(Ljava/lang/Object;Ljava/lang/String;)V");
            }
            else {
                this.visitor.visitTypeInsn(187, this.getNamer().getDetachedFieldAccessExceptionAsmClassName());
                this.visitor.visitInsn(89);
                this.visitor.visitLdcInsn(JdoGetViaCheck.LOCALISER.msg("Enhancer.DetachedFieldAccess", this.fmd.getName()));
                this.visitor.visitMethodInsn(183, this.getNamer().getDetachedFieldAccessExceptionAsmClassName(), "<init>", "(Ljava/lang/String;)V");
                this.visitor.visitInsn(191);
            }
        }
        this.visitor.visitLabel(l2);
        if (JavaUtils.useStackMapFrames()) {
            this.visitor.visitFrame(3, 0, null, 0, null);
        }
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.fmd.getName(), fieldTypeDesc);
        EnhanceUtils.addReturnForType(this.visitor, this.fmd.getType());
        final Label endLabel = new Label();
        this.visitor.visitLabel(endLabel);
        this.visitor.visitLocalVariable(this.argNames[0], this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel, 0);
        this.visitor.visitMaxs(4, 1);
        this.visitor.visitEnd();
    }
}
