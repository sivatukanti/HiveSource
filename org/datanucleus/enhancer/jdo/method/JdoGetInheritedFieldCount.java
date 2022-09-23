// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.ClassMethod;

public class JdoGetInheritedFieldCount extends ClassMethod
{
    public static JdoGetInheritedFieldCount getInstance(final ClassEnhancer enhancer) {
        return new JdoGetInheritedFieldCount(enhancer, enhancer.getNamer().getGetInheritedFieldCountMethodName(), 12, Integer.TYPE, null, null);
    }
    
    public JdoGetInheritedFieldCount(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
        super(enhancer, name, access, returnType, argTypes, argNames);
    }
    
    @Override
    public void execute() {
        final ClassMetaData cmd = this.enhancer.getClassMetaData();
        final String persistenceCapableSuperclass = cmd.getPersistenceCapableSuperclass();
        this.visitor.visitCode();
        if (persistenceCapableSuperclass != null && persistenceCapableSuperclass.length() > 0) {
            this.visitor.visitMethodInsn(184, persistenceCapableSuperclass.replace('.', '/'), this.getNamer().getGetManagedFieldCountMethodName(), "()I");
            this.visitor.visitInsn(172);
            this.visitor.visitMaxs(1, 0);
        }
        else {
            this.visitor.visitInsn(3);
            this.visitor.visitInsn(172);
            this.visitor.visitMaxs(1, 0);
        }
        this.visitor.visitEnd();
    }
}
