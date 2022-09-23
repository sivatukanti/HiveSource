// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.asm.Type;
import org.datanucleus.metadata.PropertyMetaData;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.asm.Label;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.ClassMethod;

public class JdoNewObjectIdInstance1 extends ClassMethod
{
    public static JdoNewObjectIdInstance1 getInstance(final ClassEnhancer enhancer) {
        return new JdoNewObjectIdInstance1(enhancer, enhancer.getNamer().getNewObjectIdInstanceMethodName(), 1, Object.class, null, null);
    }
    
    public JdoNewObjectIdInstance1(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
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
                this.visitor.visitMaxs(3, 1);
            }
            else {
                final String objectIdClass = cmd.getObjectidClass();
                final int[] pkFieldNums = cmd.getPKMemberPositions();
                if (this.enhancer.getMetaDataManager().getApiAdapter().isSingleFieldIdentityClass(objectIdClass)) {
                    final String ACN_objectIdClass = objectIdClass.replace('.', '/');
                    final AbstractMemberMetaData fmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(pkFieldNums[0]);
                    this.visitor.visitTypeInsn(187, ACN_objectIdClass);
                    this.visitor.visitInsn(89);
                    this.visitor.visitVarInsn(25, 0);
                    this.visitor.visitMethodInsn(182, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
                    this.visitor.visitVarInsn(25, 0);
                    if (fmd instanceof PropertyMetaData) {
                        this.visitor.visitMethodInsn(182, this.getClassEnhancer().getASMClassName(), this.getNamer().getGetMethodPrefixMethodName() + fmd.getName(), "()" + Type.getDescriptor(fmd.getType()));
                    }
                    else {
                        this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), fmd.getName(), Type.getDescriptor(fmd.getType()));
                    }
                    final Class primitiveType = ClassUtils.getPrimitiveTypeForType(fmd.getType());
                    if (primitiveType != null) {
                        this.visitor.visitMethodInsn(183, ACN_objectIdClass, "<init>", "(Ljava/lang/Class;" + Type.getDescriptor(fmd.getType()) + ")V");
                    }
                    else {
                        this.visitor.visitMethodInsn(183, ACN_objectIdClass, "<init>", "(Ljava/lang/Class;" + this.getNamer().getTypeDescriptorForSingleFieldIdentityGetKey(objectIdClass) + ")V");
                    }
                    this.visitor.visitInsn(176);
                    final Label endLabel2 = new Label();
                    this.visitor.visitLabel(endLabel2);
                    this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel2, 0);
                    this.visitor.visitMaxs(4, 1);
                }
                else {
                    final String ACN_objectIdClass = objectIdClass.replace('.', '/');
                    this.visitor.visitTypeInsn(187, ACN_objectIdClass);
                    this.visitor.visitInsn(89);
                    this.visitor.visitMethodInsn(183, ACN_objectIdClass, "<init>", "()V");
                    this.visitor.visitInsn(176);
                    final Label endLabel3 = new Label();
                    this.visitor.visitLabel(endLabel3);
                    this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel3, 0);
                    this.visitor.visitMaxs(2, 1);
                }
            }
        }
        else {
            this.visitor.visitInsn(1);
            this.visitor.visitInsn(176);
            final Label endLabel = new Label();
            this.visitor.visitLabel(endLabel);
            this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel, 0);
            this.visitor.visitMaxs(1, 1);
        }
        this.visitor.visitEnd();
    }
}
