// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ClassMetaData;
import java.lang.reflect.Modifier;
import org.datanucleus.metadata.PropertyMetaData;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.asm.Type;
import org.datanucleus.util.JavaUtils;
import org.datanucleus.enhancer.EnhanceUtils;
import org.datanucleus.asm.Label;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.ClassMethod;

public class JdoCopyKeyFieldsToObjectId extends ClassMethod
{
    public static JdoCopyKeyFieldsToObjectId getInstance(final ClassEnhancer enhancer) {
        return new JdoCopyKeyFieldsToObjectId(enhancer, enhancer.getNamer().getCopyKeyFieldsToObjectIdMethodName(), 1, null, new Class[] { Object.class }, new String[] { "oid" });
    }
    
    public JdoCopyKeyFieldsToObjectId(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
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
                this.visitor.visitLocalVariable(this.argNames[0], EnhanceUtils.CD_Object, null, startLabel, endLabel, 1);
                this.visitor.visitMaxs(0, 2);
            }
            else {
                final String objectIdClass = cmd.getObjectidClass();
                final String ACN_objectIdClass = objectIdClass.replace('.', '/');
                if (this.enhancer.getMetaDataManager().getApiAdapter().isSingleFieldIdentityClass(objectIdClass)) {
                    final Label startLabel2 = new Label();
                    this.visitor.visitLabel(startLabel2);
                    this.visitor.visitTypeInsn(187, this.getNamer().getFatalInternalExceptionAsmClassName());
                    this.visitor.visitInsn(89);
                    this.visitor.visitLdcInsn("It's illegal to call jdoCopyKeyFieldsToObjectId for a class with SingleFieldIdentity.");
                    this.visitor.visitMethodInsn(183, this.getNamer().getFatalInternalExceptionAsmClassName(), "<init>", "(Ljava/lang/String;)V");
                    this.visitor.visitInsn(191);
                    final Label endLabel2 = new Label();
                    this.visitor.visitLabel(endLabel2);
                    this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel2, endLabel2, 0);
                    this.visitor.visitLocalVariable(this.argNames[0], EnhanceUtils.CD_Object, null, startLabel2, endLabel2, 1);
                    this.visitor.visitMaxs(3, 2);
                }
                else {
                    final Label l0 = new Label();
                    final Label l2 = new Label();
                    final Label l3 = new Label();
                    this.visitor.visitTryCatchBlock(l0, l2, l3, "java/lang/Exception");
                    final Label startLabel3 = new Label();
                    this.visitor.visitLabel(startLabel3);
                    this.visitor.visitVarInsn(25, 1);
                    this.visitor.visitTypeInsn(193, ACN_objectIdClass);
                    final Label l4 = new Label();
                    this.visitor.visitJumpInsn(154, l4);
                    this.visitor.visitTypeInsn(187, "java/lang/ClassCastException");
                    this.visitor.visitInsn(89);
                    this.visitor.visitLdcInsn("key class is not " + objectIdClass + " or null");
                    this.visitor.visitMethodInsn(183, "java/lang/ClassCastException", "<init>", "(Ljava/lang/String;)V");
                    this.visitor.visitInsn(191);
                    this.visitor.visitLabel(l4);
                    if (JavaUtils.useStackMapFrames()) {
                        this.visitor.visitFrame(3, 0, null, 0, null);
                    }
                    this.visitor.visitVarInsn(25, 1);
                    this.visitor.visitTypeInsn(192, ACN_objectIdClass);
                    this.visitor.visitVarInsn(58, 2);
                    this.visitor.visitLabel(l0);
                    final int[] pkFieldNums = this.enhancer.getClassMetaData().getPKMemberPositions();
                    Label reflectionFieldStart = null;
                    for (int i = 0; i < pkFieldNums.length; ++i) {
                        final AbstractMemberMetaData fmd = this.enhancer.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(pkFieldNums[i]);
                        final String fieldTypeDesc = Type.getDescriptor(fmd.getType());
                        final AbstractClassMetaData acmd = this.enhancer.getMetaDataManager().getMetaDataForClass(fmd.getType(), this.enhancer.getClassLoaderResolver());
                        final int pkFieldModifiers = ClassUtils.getModifiersForFieldOfClass(this.enhancer.getClassLoaderResolver(), objectIdClass, fmd.getName());
                        if (acmd != null && acmd.getIdentityType() != IdentityType.NONDURABLE) {
                            if (fmd instanceof PropertyMetaData) {
                                this.visitor.visitVarInsn(25, 2);
                                this.visitor.visitVarInsn(25, 0);
                                this.visitor.visitMethodInsn(182, this.getClassEnhancer().getASMClassName(), this.getNamer().getGetMethodPrefixMethodName() + fmd.getName(), "()" + Type.getDescriptor(fmd.getType()));
                                this.visitor.visitMethodInsn(184, this.getNamer().getHelperAsmClassName(), "getObjectId", "(Ljava/lang/Object;)Ljava/lang/Object;");
                                this.visitor.visitTypeInsn(192, acmd.getObjectidClass().replace('.', '/'));
                                this.visitor.visitFieldInsn(181, ACN_objectIdClass, fmd.getName(), "L" + acmd.getObjectidClass().replace('.', '/') + ";");
                            }
                            else if (Modifier.isPublic(pkFieldModifiers)) {
                                this.visitor.visitVarInsn(25, 2);
                                this.visitor.visitVarInsn(25, 0);
                                this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), fmd.getName(), fieldTypeDesc);
                                this.visitor.visitMethodInsn(184, this.getNamer().getHelperAsmClassName(), "getObjectId", "(Ljava/lang/Object;)Ljava/lang/Object;");
                                this.visitor.visitTypeInsn(192, acmd.getObjectidClass().replace('.', '/'));
                                this.visitor.visitFieldInsn(181, ACN_objectIdClass, fmd.getName(), "L" + acmd.getObjectidClass().replace('.', '/') + ";");
                            }
                            else {
                                this.visitor.visitVarInsn(25, 2);
                                this.visitor.visitVarInsn(25, 0);
                                this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), fmd.getName(), fieldTypeDesc);
                                this.visitor.visitMethodInsn(184, this.getNamer().getHelperAsmClassName(), "getObjectId", "(Ljava/lang/Object;)Ljava/lang/Object;");
                                this.visitor.visitTypeInsn(192, acmd.getObjectidClass().replace('.', '/'));
                                this.visitor.visitFieldInsn(181, ACN_objectIdClass, fmd.getName(), "L" + acmd.getObjectidClass().replace('.', '/') + ";");
                            }
                        }
                        else if (fmd instanceof PropertyMetaData) {
                            this.visitor.visitVarInsn(25, 2);
                            this.visitor.visitVarInsn(25, 0);
                            this.visitor.visitMethodInsn(182, this.getClassEnhancer().getASMClassName(), this.getNamer().getGetMethodPrefixMethodName() + fmd.getName(), "()" + Type.getDescriptor(fmd.getType()));
                            this.visitor.visitMethodInsn(182, ACN_objectIdClass, ClassUtils.getJavaBeanSetterName(fmd.getName()), "(" + fieldTypeDesc + ")V");
                        }
                        else if (Modifier.isPublic(pkFieldModifiers)) {
                            this.visitor.visitVarInsn(25, 2);
                            this.visitor.visitVarInsn(25, 0);
                            this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), fmd.getName(), fieldTypeDesc);
                            this.visitor.visitFieldInsn(181, ACN_objectIdClass, fmd.getName(), fieldTypeDesc);
                        }
                        else {
                            this.visitor.visitVarInsn(25, 2);
                            this.visitor.visitMethodInsn(182, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
                            this.visitor.visitLdcInsn(fmd.getName());
                            this.visitor.visitMethodInsn(182, "java/lang/Class", "getDeclaredField", "(Ljava/lang/String;)Ljava/lang/reflect/Field;");
                            this.visitor.visitVarInsn(58, 3);
                            if (reflectionFieldStart == null) {
                                reflectionFieldStart = new Label();
                                this.visitor.visitLabel(reflectionFieldStart);
                            }
                            this.visitor.visitVarInsn(25, 3);
                            this.visitor.visitInsn(4);
                            this.visitor.visitMethodInsn(182, "java/lang/reflect/Field", "setAccessible", "(Z)V");
                            this.visitor.visitVarInsn(25, 3);
                            this.visitor.visitVarInsn(25, 2);
                            this.visitor.visitVarInsn(25, 0);
                            this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), fmd.getName(), fieldTypeDesc);
                            if (fmd.getTypeName().equals("boolean")) {
                                this.visitor.visitMethodInsn(182, "java/lang/reflect/Field", "setBoolean", "(Ljava/lang/Object;Z)V");
                            }
                            else if (fmd.getTypeName().equals("byte")) {
                                this.visitor.visitMethodInsn(182, "java/lang/reflect/Field", "setByte", "(Ljava/lang/Object;B)V");
                            }
                            else if (fmd.getTypeName().equals("char")) {
                                this.visitor.visitMethodInsn(182, "java/lang/reflect/Field", "setChar", "(Ljava/lang/Object;C)V");
                            }
                            else if (fmd.getTypeName().equals("double")) {
                                this.visitor.visitMethodInsn(182, "java/lang/reflect/Field", "setDouble", "(Ljava/lang/Object;D)V");
                            }
                            else if (fmd.getTypeName().equals("float")) {
                                this.visitor.visitMethodInsn(182, "java/lang/reflect/Field", "setFloat", "(Ljava/lang/Object;F)V");
                            }
                            else if (fmd.getTypeName().equals("int")) {
                                this.visitor.visitMethodInsn(182, "java/lang/reflect/Field", "setInt", "(Ljava/lang/Object;I)V");
                            }
                            else if (fmd.getTypeName().equals("long")) {
                                this.visitor.visitMethodInsn(182, "java/lang/reflect/Field", "setLong", "(Ljava/lang/Object;J)V");
                            }
                            else if (fmd.getTypeName().equals("short")) {
                                this.visitor.visitMethodInsn(182, "java/lang/reflect/Field", "setShort", "(Ljava/lang/Object;S)V");
                            }
                            else {
                                this.visitor.visitMethodInsn(182, "java/lang/reflect/Field", "set", "(Ljava/lang/Object;Ljava/lang/Object;)V");
                            }
                        }
                    }
                    this.visitor.visitLabel(l2);
                    final Label l5 = new Label();
                    this.visitor.visitJumpInsn(167, l5);
                    this.visitor.visitLabel(l3);
                    if (JavaUtils.useStackMapFrames()) {
                        this.visitor.visitFrame(0, 3, new Object[] { this.getClassEnhancer().getASMClassName(), "java/lang/Object", ACN_objectIdClass }, 1, new Object[] { "java/lang/Exception" });
                    }
                    this.visitor.visitVarInsn(58, 3);
                    this.visitor.visitLabel(l5);
                    if (JavaUtils.useStackMapFrames()) {
                        this.visitor.visitFrame(3, 0, null, 0, null);
                    }
                    this.visitor.visitInsn(177);
                    final Label endLabel3 = new Label();
                    this.visitor.visitLabel(endLabel3);
                    this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel3, endLabel3, 0);
                    this.visitor.visitLocalVariable(this.argNames[0], EnhanceUtils.CD_Object, null, startLabel3, endLabel3, 1);
                    this.visitor.visitLocalVariable("o", "L" + ACN_objectIdClass + ";", null, l0, endLabel3, 2);
                    if (reflectionFieldStart != null) {
                        this.visitor.visitLocalVariable("field", "Ljava/lang/reflect/Field;", null, reflectionFieldStart, l3, 3);
                        this.visitor.visitMaxs(3, 4);
                    }
                    else {
                        this.visitor.visitMaxs(3, 3);
                    }
                }
            }
        }
        else {
            final Label startLabel = new Label();
            this.visitor.visitLabel(startLabel);
            this.visitor.visitInsn(177);
            final Label l6 = new Label();
            this.visitor.visitLabel(l6);
            this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel, l6, 0);
            this.visitor.visitLocalVariable(this.argNames[0], EnhanceUtils.CD_Object, null, startLabel, l6, 1);
            this.visitor.visitMaxs(0, 2);
        }
        this.visitor.visitEnd();
    }
}
