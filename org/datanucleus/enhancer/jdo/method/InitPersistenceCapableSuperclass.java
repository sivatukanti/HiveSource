// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.ClassMethod;

public class InitPersistenceCapableSuperclass extends ClassMethod
{
    public static InitPersistenceCapableSuperclass getInstance(final ClassEnhancer enhancer) {
        return new InitPersistenceCapableSuperclass(enhancer, enhancer.getNamer().getPersistableSuperclassInitMethodName(), 10, Class.class, null, null);
    }
    
    public InitPersistenceCapableSuperclass(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
        super(enhancer, name, access, returnType, argTypes, argNames);
    }
    
    @Override
    public void execute() {
        this.visitor.visitCode();
        final String pcSuperclassName = this.enhancer.getClassMetaData().getPersistenceCapableSuperclass();
        if (pcSuperclassName != null) {
            this.visitor.visitLdcInsn(pcSuperclassName);
            this.visitor.visitMethodInsn(184, this.getClassEnhancer().getASMClassName(), this.getNamer().getLoadClassMethodName(), "(Ljava/lang/String;)Ljava/lang/Class;");
        }
        else {
            this.visitor.visitInsn(1);
        }
        this.visitor.visitInsn(176);
        this.visitor.visitMaxs(1, 0);
        this.visitor.visitEnd();
    }
}
