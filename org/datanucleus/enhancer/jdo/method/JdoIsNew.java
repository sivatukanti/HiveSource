// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.enhancer.ClassEnhancer;

public class JdoIsNew extends JdoIsXXX
{
    public static JdoIsNew getInstance(final ClassEnhancer enhancer) {
        return new JdoIsNew(enhancer, enhancer.getNamer().getIsNewMethodName(), 17, Boolean.TYPE, null, null);
    }
    
    public JdoIsNew(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
        super(enhancer, name, access, returnType, argTypes, argNames);
    }
    
    @Override
    protected String getStateManagerIsMethod() {
        return "isNew";
    }
}
