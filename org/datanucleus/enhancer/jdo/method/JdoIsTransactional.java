// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.enhancer.ClassEnhancer;

public class JdoIsTransactional extends JdoIsXXX
{
    public static JdoIsTransactional getInstance(final ClassEnhancer enhancer) {
        return new JdoIsTransactional(enhancer, enhancer.getNamer().getIsTransactionalMethodName(), 17, Boolean.TYPE, null, null);
    }
    
    public JdoIsTransactional(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
        super(enhancer, name, access, returnType, argTypes, argNames);
    }
    
    @Override
    protected String getStateManagerIsMethod() {
        return "isTransactional";
    }
}
