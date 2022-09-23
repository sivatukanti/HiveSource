// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.enhancer.EnhanceUtils;
import org.datanucleus.util.JavaUtils;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.asm.Label;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.ClassMethod;

public class JdoNewObjectIdInstance2 extends ClassMethod
{
    public static JdoNewObjectIdInstance2 getInstance(final ClassEnhancer enhancer) {
        return new JdoNewObjectIdInstance2(enhancer, enhancer.getNamer().getNewObjectIdInstanceMethodName(), 1, Object.class, new Class[] { Object.class }, new String[] { "key" });
    }
    
    public JdoNewObjectIdInstance2(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
        super(enhancer, name, access, returnType, argTypes, argNames);
    }
    
    @Override
    public void execute() {
        this.visitor.visitCode();
        final Label startLabel = new Label();
        this.visitor.visitLabel(startLabel);
        final ClassMetaData cmd = this.enhancer.getClassMetaData();
        if (cmd.getIdentityType() == IdentityType.APPLICATION) {
            if (!cmd.isInstantiable()) {
                this.visitor.visitTypeInsn(187, this.getClassEnhancer().getNamer().getFatalInternalExceptionAsmClassName());
                this.visitor.visitInsn(89);
                this.visitor.visitLdcInsn("This class has no identity");
                this.visitor.visitMethodInsn(183, this.getClassEnhancer().getNamer().getFatalInternalExceptionAsmClassName(), "<init>", "(Ljava/lang/String;)V");
                this.visitor.visitInsn(191);
                final Label endLabel = new Label();
                this.visitor.visitLabel(endLabel);
                this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel, 0);
                this.visitor.visitLocalVariable("key", "Ljava/lang/Object;", null, startLabel, endLabel, 1);
                this.visitor.visitMaxs(3, 2);
            }
            else {
                final String objectIdClass = cmd.getObjectidClass();
                final int[] pkFieldNums = cmd.getPKMemberPositions();
                if (this.enhancer.getMetaDataManager().getApiAdapter().isSingleFieldIdentityClass(objectIdClass)) {
                    final String ACN_objectIdClass = objectIdClass.replace('.', '/');
                    final AbstractMemberMetaData fmd = this.enhancer.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(pkFieldNums[0]);
                    this.visitor.visitVarInsn(25, 1);
                    final Label l1 = new Label();
                    this.visitor.visitJumpInsn(199, l1);
                    this.visitor.visitTypeInsn(187, "java/lang/IllegalArgumentException");
                    this.visitor.visitInsn(89);
                    this.visitor.visitLdcInsn("key is null");
                    this.visitor.visitMethodInsn(183, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
                    this.visitor.visitInsn(191);
                    this.visitor.visitLabel(l1);
                    if (JavaUtils.useStackMapFrames()) {
                        this.visitor.visitFrame(3, 0, null, 0, null);
                    }
                    this.visitor.visitVarInsn(25, 1);
                    this.visitor.visitTypeInsn(193, "java/lang/String");
                    final Label l2 = new Label();
                    this.visitor.visitJumpInsn(154, l2);
                    this.visitor.visitTypeInsn(187, ACN_objectIdClass);
                    this.visitor.visitInsn(89);
                    this.visitor.visitVarInsn(25, 0);
                    this.visitor.visitMethodInsn(182, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
                    this.visitor.visitVarInsn(25, 1);
                    String objectTypeInConstructor = EnhanceUtils.getASMClassNameForSingleFieldIdentityConstructor(fmd.getType());
                    final Class primitiveType = ClassUtils.getPrimitiveTypeForType(fmd.getType());
                    if (primitiveType != null) {
                        objectTypeInConstructor = fmd.getTypeName().replace('.', '/');
                    }
                    if (!objectIdClass.equals(this.getNamer().getObjectIdentityClass().getName()) || primitiveType != null) {
                        this.visitor.visitTypeInsn(192, objectTypeInConstructor);
                    }
                    this.visitor.visitMethodInsn(183, ACN_objectIdClass, "<init>", "(Ljava/lang/Class;L" + objectTypeInConstructor + ";)V");
                    this.visitor.visitInsn(176);
                    this.visitor.visitLabel(l2);
                    if (JavaUtils.useStackMapFrames()) {
                        this.visitor.visitFrame(3, 0, null, 0, null);
                    }
                    this.visitor.visitTypeInsn(187, ACN_objectIdClass);
                    this.visitor.visitInsn(89);
                    this.visitor.visitVarInsn(25, 0);
                    this.visitor.visitMethodInsn(182, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
                    this.visitor.visitVarInsn(25, 1);
                    this.visitor.visitTypeInsn(192, "java/lang/String");
                    this.visitor.visitMethodInsn(183, ACN_objectIdClass, "<init>", "(Ljava/lang/Class;Ljava/lang/String;)V");
                    this.visitor.visitInsn(176);
                    final Label endLabel2 = new Label();
                    this.visitor.visitLabel(endLabel2);
                    this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel2, 0);
                    this.visitor.visitLocalVariable("key", EnhanceUtils.CD_Object, null, startLabel, endLabel2, 1);
                    this.visitor.visitMaxs(4, 2);
                }
                else {
                    final String ACN_objectIdClass = objectIdClass.replace('.', '/');
                    this.visitor.visitTypeInsn(187, ACN_objectIdClass);
                    this.visitor.visitInsn(89);
                    this.visitor.visitVarInsn(25, 1);
                    this.visitor.visitTypeInsn(192, "java/lang/String");
                    this.visitor.visitMethodInsn(183, ACN_objectIdClass, "<init>", "(Ljava/lang/String;)V");
                    this.visitor.visitInsn(176);
                    final Label endLabel3 = new Label();
                    this.visitor.visitLabel(endLabel3);
                    this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel3, 0);
                    this.visitor.visitLocalVariable(this.argNames[0], EnhanceUtils.CD_Object, null, startLabel, endLabel3, 1);
                    this.visitor.visitMaxs(3, 2);
                    this.visitor.visitEnd();
                }
            }
        }
        else {
            this.visitor.visitInsn(1);
            this.visitor.visitInsn(176);
            final Label endLabel = new Label();
            this.visitor.visitLabel(endLabel);
            this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel, 0);
            this.visitor.visitLocalVariable(this.argNames[0], "Ljava/lang/Object;", null, startLabel, endLabel, 1);
            this.visitor.visitMaxs(1, 2);
        }
        this.visitor.visitEnd();
    }
}
