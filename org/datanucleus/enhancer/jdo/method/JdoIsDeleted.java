// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.enhancer.ClassEnhancer;

public class JdoIsDeleted extends JdoIsXXX
{
    public static JdoIsDeleted getInstance(final ClassEnhancer enhancer) {
        return new JdoIsDeleted(enhancer, enhancer.getNamer().getIsDeletedMethodName(), 17, Boolean.TYPE, null, null);
    }
    
    public JdoIsDeleted(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
        super(enhancer, name, access, returnType, argTypes, argNames);
    }
    
    @Override
    protected String getStateManagerIsMethod() {
        return "isDeleted";
    }
}
