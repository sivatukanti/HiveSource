// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.enhancer.EnhanceUtils;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.ClassMethod;

public class JdoGetManagedFieldCount extends ClassMethod
{
    public static JdoGetManagedFieldCount getInstance(final ClassEnhancer enhancer) {
        return new JdoGetManagedFieldCount(enhancer, enhancer.getNamer().getGetManagedFieldCountMethodName(), 12, Integer.TYPE, null, null);
    }
    
    public JdoGetManagedFieldCount(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
        super(enhancer, name, access, returnType, argTypes, argNames);
    }
    
    @Override
    public void execute() {
        final ClassMetaData cmd = this.enhancer.getClassMetaData();
        final String persistenceCapableSuperclass = cmd.getPersistenceCapableSuperclass();
        this.visitor.visitCode();
        if (persistenceCapableSuperclass != null && persistenceCapableSuperclass.length() > 0) {
            EnhanceUtils.addBIPUSHToMethod(this.visitor, cmd.getNoOfManagedMembers());
            this.visitor.visitMethodInsn(184, persistenceCapableSuperclass.replace('.', '/'), this.methodName, "()I");
            this.visitor.visitInsn(96);
            this.visitor.visitInsn(172);
            this.visitor.visitMaxs(2, 0);
        }
        else {
            EnhanceUtils.addBIPUSHToMethod(this.visitor, cmd.getNoOfManagedMembers());
            this.visitor.visitInsn(172);
            this.visitor.visitMaxs(1, 0);
        }
        this.visitor.visitEnd();
    }
}
