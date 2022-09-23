// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ClassMetaData;
import java.lang.reflect.Modifier;
import org.datanucleus.metadata.PropertyMetaData;
import org.datanucleus.asm.Type;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.util.JavaUtils;
import org.datanucleus.enhancer.EnhanceUtils;
import org.datanucleus.asm.Label;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.ClassMethod;

public class JdoCopyKeyFieldsFromObjectId extends ClassMethod
{
    public static JdoCopyKeyFieldsFromObjectId getInstance(final ClassEnhancer enhancer) {
        return new JdoCopyKeyFieldsFromObjectId(enhancer, enhancer.getNamer().getCopyKeyFieldsFromObjectIdMethodName(), 1, null, new Class[] { enhancer.getNamer().getObjectIdFieldConsumerClass(), Object.class }, new String[] { "fc", "oid" });
    }
    
    public JdoCopyKeyFieldsFromObjectId(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
        super(enhancer, name, access, returnType, argTypes, argNames);
    }
    
    @Override
    public void execute() {
        this.visitor.visitCode();
        final ClassMetaData cmd = this.enhancer.getClassMetaData();
        if (cmd.getIdentityType() == IdentityType.APPLICATION) {
            if (!cmd.isInstantiable()) {
                final Label startLabel = new Label();
                this.visitor.visitLabel(startLabel);
                this.visitor.visitInsn(177);
                final Label endLabel = new Label();
                this.visitor.visitLabel(endLabel);
                this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel, 0);
                this.visitor.visitLocalVariable(this.argNames[0], this.getNamer().getObjectIdFieldConsumerDescriptor(), null, startLabel, endLabel, 1);
                this.visitor.visitLocalVariable(this.argNames[1], EnhanceUtils.CD_Object, null, startLabel, endLabel, 2);
                this.visitor.visitMaxs(0, 3);
            }
            else {
                final int[] pkFieldNums = cmd.getPKMemberPositions();
                final String objectIdClass = cmd.getObjectidClass();
                final String ACN_objectIdClass = objectIdClass.replace('.', '/');
                if (this.enhancer.getMetaDataManager().getApiAdapter().isSingleFieldIdentityClass(objectIdClass)) {
                    final Label startLabel2 = new Label();
                    this.visitor.visitLabel(startLabel2);
                    this.visitor.visitVarInsn(25, 1);
                    final Label l1 = new Label();
                    this.visitor.visitJumpInsn(199, l1);
                    this.visitor.visitTypeInsn(187, "java/lang/IllegalArgumentException");
                    this.visitor.visitInsn(89);
                    this.visitor.visitLdcInsn("ObjectIdFieldConsumer is null");
                    this.visitor.visitMethodInsn(183, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
                    this.visitor.visitInsn(191);
                    this.visitor.visitLabel(l1);
                    if (JavaUtils.useStackMapFrames()) {
                        this.visitor.visitFrame(3, 0, null, 0, null);
                    }
                    this.visitor.visitVarInsn(25, 2);
                    this.visitor.visitTypeInsn(193, ACN_objectIdClass);
                    final Label l2 = new Label();
                    this.visitor.visitJumpInsn(154, l2);
                    this.visitor.visitTypeInsn(187, "java/lang/ClassCastException");
                    this.visitor.visitInsn(89);
                    this.visitor.visitLdcInsn("oid is not instanceof " + objectIdClass);
                    this.visitor.visitMethodInsn(183, "java/lang/ClassCastException", "<init>", "(Ljava/lang/String;)V");
                    this.visitor.visitInsn(191);
                    this.visitor.visitLabel(l2);
                    if (JavaUtils.useStackMapFrames()) {
                        this.visitor.visitFrame(3, 0, null, 0, null);
                    }
                    this.visitor.visitVarInsn(25, 2);
                    this.visitor.visitTypeInsn(192, ACN_objectIdClass);
                    this.visitor.visitVarInsn(58, 3);
                    final Label l3 = new Label();
                    this.visitor.visitLabel(l3);
                    this.visitor.visitVarInsn(25, 1);
                    EnhanceUtils.addBIPUSHToMethod(this.visitor, pkFieldNums[0]);
                    final AbstractMemberMetaData fmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(pkFieldNums[0]);
                    final Class primitiveType = ClassUtils.getPrimitiveTypeForType(fmd.getType());
                    if (primitiveType != null) {
                        final String ACN_fieldType = fmd.getTypeName().replace('.', '/');
                        final String getKeyReturnDesc = Type.getDescriptor(primitiveType);
                        this.visitor.visitVarInsn(25, 3);
                        this.visitor.visitMethodInsn(182, ACN_objectIdClass, "getKey", "()" + getKeyReturnDesc);
                        this.visitor.visitMethodInsn(184, ACN_fieldType, "valueOf", "(" + getKeyReturnDesc + ")L" + ACN_fieldType + ";");
                        this.visitor.visitMethodInsn(185, this.getNamer().getObjectIdFieldConsumerAsmClassName(), "storeObjectField", "(I" + EnhanceUtils.CD_Object + ")V");
                    }
                    else {
                        this.visitor.visitVarInsn(25, 3);
                        this.visitor.visitMethodInsn(182, ACN_objectIdClass, "getKey", "()" + this.getNamer().getTypeDescriptorForSingleFieldIdentityGetKey(objectIdClass));
                        this.visitor.visitMethodInsn(185, this.getNamer().getObjectIdFieldConsumerAsmClassName(), "store" + this.getNamer().getTypeNameForUseWithSingleFieldIdentity(objectIdClass) + "Field", "(I" + this.getNamer().getTypeDescriptorForSingleFieldIdentityGetKey(objectIdClass) + ")V");
                    }
                    this.visitor.visitInsn(177);
                    final Label endLabel2 = new Label();
                    this.visitor.visitLabel(endLabel2);
                    this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel2, endLabel2, 0);
                    this.visitor.visitLocalVariable(this.argNames[0], this.getNamer().getObjectIdFieldConsumerDescriptor(), null, startLabel2, endLabel2, 1);
                    this.visitor.visitLocalVariable(this.argNames[1], EnhanceUtils.CD_Object, null, startLabel2, endLabel2, 2);
                    this.visitor.visitLocalVariable("o", this.getNamer().getSingleFieldIdentityDescriptor(objectIdClass), null, l3, endLabel2, 3);
                    this.visitor.visitMaxs(3, 4);
                }
                else {
                    final Label l4 = new Label();
                    final Label l1 = new Label();
                    final Label l5 = new Label();
                    this.visitor.visitTryCatchBlock(l4, l1, l5, "java/lang/Exception");
                    final Label startLabel3 = new Label();
                    this.visitor.visitLabel(startLabel3);
                    this.visitor.visitVarInsn(25, 1);
                    final Label l6 = new Label();
                    this.visitor.visitJumpInsn(199, l6);
                    this.visitor.visitTypeInsn(187, "java/lang/IllegalArgumentException");
                    this.visitor.visitInsn(89);
                    this.visitor.visitLdcInsn("ObjectIdFieldConsumer is null");
                    this.visitor.visitMethodInsn(183, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
                    this.visitor.visitInsn(191);
                    this.visitor.visitLabel(l6);
                    if (JavaUtils.useStackMapFrames()) {
                        this.visitor.visitFrame(3, 0, null, 0, null);
                    }
                    this.visitor.visitVarInsn(25, 2);
                    this.visitor.visitTypeInsn(193, ACN_objectIdClass);
                    final Label l7 = new Label();
                    this.visitor.visitJumpInsn(154, l7);
                    this.visitor.visitTypeInsn(187, "java/lang/ClassCastException");
                    this.visitor.visitInsn(89);
                    this.visitor.visitLdcInsn("oid is not instanceof " + objectIdClass);
                    this.visitor.visitMethodInsn(183, "java/lang/ClassCastException", "<init>", "(Ljava/lang/String;)V");
                    this.visitor.visitInsn(191);
                    this.visitor.visitLabel(l7);
                    if (JavaUtils.useStackMapFrames()) {
                        this.visitor.visitFrame(3, 0, null, 0, null);
                    }
                    this.visitor.visitVarInsn(25, 2);
                    this.visitor.visitTypeInsn(192, ACN_objectIdClass);
                    this.visitor.visitVarInsn(58, 3);
                    this.visitor.visitLabel(l4);
                    Label reflectionFieldStart = null;
                    for (int i = 0; i < pkFieldNums.length; ++i) {
                        final AbstractMemberMetaData fmd2 = this.enhancer.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(pkFieldNums[i]);
                        final String fieldTypeDesc = Type.getDescriptor(fmd2.getType());
                        final String typeMethodName = EnhanceUtils.getTypeNameForJDOMethod(fmd2.getType());
                        final int pkFieldModifiers = ClassUtils.getModifiersForFieldOfClass(this.enhancer.getClassLoaderResolver(), objectIdClass, fmd2.getName());
                        final AbstractClassMetaData acmd = this.enhancer.getMetaDataManager().getMetaDataForClass(fmd2.getType(), this.enhancer.getClassLoaderResolver());
                        if (acmd != null && acmd.getIdentityType() != IdentityType.NONDURABLE) {
                            this.visitor.visitVarInsn(25, 1);
                            EnhanceUtils.addBIPUSHToMethod(this.visitor, fmd2.getFieldId());
                            this.visitor.visitVarInsn(25, 0);
                            this.visitor.visitMethodInsn(182, this.getClassEnhancer().getASMClassName(), this.getNamer().getGetPersistenceManagerMethodName(), "()L" + this.getNamer().getPersistenceManagerAsmClassName() + ";");
                            this.visitor.visitVarInsn(25, 3);
                            this.visitor.visitFieldInsn(180, ACN_objectIdClass, fmd2.getName(), "L" + acmd.getObjectidClass().replace('.', '/') + ";");
                            this.visitor.visitInsn(3);
                            this.visitor.visitMethodInsn(185, this.getNamer().getPersistenceManagerAsmClassName(), "getObjectById", "(Ljava/lang/Object;Z)Ljava/lang/Object;");
                            this.visitor.visitMethodInsn(185, this.getNamer().getObjectIdFieldConsumerAsmClassName(), "storeObjectField", "(ILjava/lang/Object;)V");
                        }
                        else if (fmd2 instanceof PropertyMetaData) {
                            this.visitor.visitVarInsn(25, 1);
                            EnhanceUtils.addBIPUSHToMethod(this.visitor, fmd2.getFieldId());
                            this.visitor.visitVarInsn(25, 3);
                            this.visitor.visitMethodInsn(182, ACN_objectIdClass, ClassUtils.getJavaBeanGetterName(fmd2.getName(), fmd2.getTypeName().equals("boolean")), "()" + Type.getDescriptor(fmd2.getType()));
                            this.visitor.visitMethodInsn(185, this.getNamer().getObjectIdFieldConsumerAsmClassName(), "store" + typeMethodName + "Field", "(I" + EnhanceUtils.getTypeDescriptorForJDOMethod(fmd2.getType()) + ")V");
                        }
                        else if (Modifier.isPublic(pkFieldModifiers)) {
                            this.visitor.visitVarInsn(25, 1);
                            EnhanceUtils.addBIPUSHToMethod(this.visitor, fmd2.getFieldId());
                            this.visitor.visitVarInsn(25, 3);
                            this.visitor.visitFieldInsn(180, ACN_objectIdClass, fmd2.getName(), fieldTypeDesc);
                            this.visitor.visitMethodInsn(185, this.getNamer().getObjectIdFieldConsumerAsmClassName(), "store" + typeMethodName + "Field", "(I" + EnhanceUtils.getTypeDescriptorForJDOMethod(fmd2.getType()) + ")V");
                        }
                        else {
                            this.visitor.visitVarInsn(25, 3);
                            this.visitor.visitMethodInsn(182, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
                            this.visitor.visitLdcInsn(fmd2.getName());
                            this.visitor.visitMethodInsn(182, "java/lang/Class", "getDeclaredField", "(Ljava/lang/String;)Ljava/lang/reflect/Field;");
                            this.visitor.visitVarInsn(58, 4);
                            if (reflectionFieldStart == null) {
                                reflectionFieldStart = new Label();
                                this.visitor.visitLabel(reflectionFieldStart);
                            }
                            this.visitor.visitVarInsn(25, 4);
                            this.visitor.visitInsn(4);
                            this.visitor.visitMethodInsn(182, "java/lang/reflect/Field", "setAccessible", "(Z)V");
                            this.visitor.visitVarInsn(25, 1);
                            EnhanceUtils.addBIPUSHToMethod(this.visitor, fmd2.getFieldId());
                            this.visitor.visitVarInsn(25, 4);
                            this.visitor.visitVarInsn(25, 3);
                            if (fmd2.getTypeName().equals("boolean")) {
                                this.visitor.visitMethodInsn(182, "java/lang/reflect/Field", "getBoolean", "(Ljava/lang/Object;)Z");
                            }
                            else if (fmd2.getTypeName().equals("byte")) {
                                this.visitor.visitMethodInsn(182, "java/lang/reflect/Field", "getByte", "(Ljava/lang/Object;)B");
                            }
                            else if (fmd2.getTypeName().equals("char")) {
                                this.visitor.visitMethodInsn(182, "java/lang/reflect/Field", "getChar", "(Ljava/lang/Object;)C");
                            }
                            else if (fmd2.getTypeName().equals("double")) {
                                this.visitor.visitMethodInsn(182, "java/lang/reflect/Field", "getDouble", "(Ljava/lang/Object;)D");
                            }
                            else if (fmd2.getTypeName().equals("float")) {
                                this.visitor.visitMethodInsn(182, "java/lang/reflect/Field", "getFloat", "(Ljava/lang/Object;)F");
                            }
                            else if (fmd2.getTypeName().equals("int")) {
                                this.visitor.visitMethodInsn(182, "java/lang/reflect/Field", "getInt", "(Ljava/lang/Object;)I");
                            }
                            else if (fmd2.getTypeName().equals("long")) {
                                this.visitor.visitMethodInsn(182, "java/lang/reflect/Field", "getLong", "(Ljava/lang/Object;)L");
                            }
                            else if (fmd2.getTypeName().equals("short")) {
                                this.visitor.visitMethodInsn(182, "java/lang/reflect/Field", "getShort", "(Ljava/lang/Object;)S");
                            }
                            else if (fmd2.getTypeName().equals("java.lang.String")) {
                                this.visitor.visitMethodInsn(182, "java/lang/reflect/Field", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
                                this.visitor.visitTypeInsn(192, "java/lang/String");
                            }
                            else {
                                this.visitor.visitMethodInsn(182, "java/lang/reflect/Field", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
                            }
                            this.visitor.visitMethodInsn(185, this.getNamer().getObjectIdFieldConsumerAsmClassName(), "store" + typeMethodName + "Field", "(I" + EnhanceUtils.getTypeDescriptorForJDOMethod(fmd2.getType()) + ")V");
                        }
                    }
                    this.visitor.visitLabel(l1);
                    final Label l8 = new Label();
                    this.visitor.visitJumpInsn(167, l8);
                    this.visitor.visitLabel(l5);
                    if (JavaUtils.useStackMapFrames()) {
                        this.visitor.visitFrame(0, 4, new Object[] { this.getClassEnhancer().getASMClassName(), this.getClassEnhancer().getNamer().getObjectIdFieldConsumerAsmClassName(), "java/lang/Object", ACN_objectIdClass }, 1, new Object[] { "java/lang/Exception" });
                    }
                    this.visitor.visitVarInsn(58, 4);
                    this.visitor.visitLabel(l8);
                    if (JavaUtils.useStackMapFrames()) {
                        this.visitor.visitFrame(3, 0, null, 0, null);
                    }
                    this.visitor.visitInsn(177);
                    final Label endLabel3 = new Label();
                    this.visitor.visitLabel(endLabel3);
                    this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel3, endLabel3, 0);
                    this.visitor.visitLocalVariable(this.argNames[0], this.getNamer().getObjectIdFieldConsumerDescriptor(), null, startLabel3, endLabel3, 1);
                    this.visitor.visitLocalVariable(this.argNames[1], EnhanceUtils.CD_Object, null, startLabel3, endLabel3, 2);
                    this.visitor.visitLocalVariable("o", "L" + ACN_objectIdClass + ";", null, l4, endLabel3, 3);
                    if (reflectionFieldStart != null) {
                        this.visitor.visitLocalVariable("field", "Ljava/lang/reflect/Field;", null, reflectionFieldStart, l5, 4);
                        this.visitor.visitMaxs(4, 5);
                    }
                    else {
                        this.visitor.visitMaxs(4, 4);
                    }
                }
            }
        }
        else {
            final Label startLabel = new Label();
            this.visitor.visitLabel(startLabel);
            this.visitor.visitInsn(177);
            final Label endLabel = new Label();
            this.visitor.visitLabel(endLabel);
            this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel, 0);
            this.visitor.visitLocalVariable(this.argNames[0], "L" + this.getNamer().getObjectIdFieldConsumerAsmClassName() + ";", null, startLabel, endLabel, 1);
            this.visitor.visitLocalVariable(this.argNames[1], EnhanceUtils.CD_Object, null, startLabel, endLabel, 2);
            this.visitor.visitMaxs(0, 3);
        }
        this.visitor.visitEnd();
    }
}
