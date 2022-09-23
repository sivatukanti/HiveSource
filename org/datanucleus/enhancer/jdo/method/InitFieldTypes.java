// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.enhancer.EnhanceUtils;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.ClassMethod;

public class InitFieldTypes extends ClassMethod
{
    public static InitFieldTypes getInstance(final ClassEnhancer enhancer) {
        return new InitFieldTypes(enhancer, enhancer.getNamer().getFieldTypesInitMethodName(), 26, Class[].class, null, null);
    }
    
    public InitFieldTypes(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
        super(enhancer, name, access, returnType, argTypes, argNames);
    }
    
    @Override
    public void execute() {
        final AbstractMemberMetaData[] fields = this.enhancer.getClassMetaData().getManagedMembers();
        this.visitor.visitCode();
        if (fields != null && fields.length > 0) {
            EnhanceUtils.addBIPUSHToMethod(this.visitor, fields.length);
            this.visitor.visitTypeInsn(189, "java/lang/Class");
            for (int i = 0; i < fields.length; ++i) {
                this.visitor.visitInsn(89);
                EnhanceUtils.addBIPUSHToMethod(this.visitor, i);
                if (fields[i].getType().isPrimitive()) {
                    final String wrapperTypeName = ClassUtils.getWrapperTypeNameForPrimitiveTypeName(fields[i].getTypeName());
                    this.visitor.visitFieldInsn(178, wrapperTypeName.replace('.', '/'), "TYPE", "Ljava/lang/Class;");
                }
                else {
                    this.visitor.visitLdcInsn(fields[i].getTypeName());
                    this.visitor.visitMethodInsn(184, this.enhancer.getClassMetaData().getFullClassName().replace('.', '/'), this.getNamer().getLoadClassMethodName(), "(Ljava/lang/String;)Ljava/lang/Class;");
                }
                this.visitor.visitInsn(83);
            }
            this.visitor.visitInsn(176);
            this.visitor.visitMaxs(4, 0);
        }
        else {
            this.visitor.visitInsn(3);
            this.visitor.visitTypeInsn(189, "java/lang/Class");
            this.visitor.visitInsn(176);
            this.visitor.visitMaxs(1, 0);
        }
        this.visitor.visitEnd();
    }
}
