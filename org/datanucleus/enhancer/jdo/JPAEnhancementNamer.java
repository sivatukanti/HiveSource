// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo;

public class JPAEnhancementNamer extends JDOEnhancementNamer
{
    private static JPAEnhancementNamer instance;
    private static final String ACN_DetachedFieldAccessException;
    private static final String ACN_FatalInternalException;
    
    public static JPAEnhancementNamer getInstance() {
        if (JPAEnhancementNamer.instance == null) {
            JPAEnhancementNamer.instance = new JPAEnhancementNamer();
        }
        return JPAEnhancementNamer.instance;
    }
    
    protected JPAEnhancementNamer() {
    }
    
    @Override
    public String getDetachedFieldAccessExceptionAsmClassName() {
        return JPAEnhancementNamer.ACN_DetachedFieldAccessException;
    }
    
    @Override
    public String getFatalInternalExceptionAsmClassName() {
        return JPAEnhancementNamer.ACN_FatalInternalException;
    }
    
    static {
        JPAEnhancementNamer.instance = null;
        ACN_DetachedFieldAccessException = IllegalAccessException.class.getName().replace('.', '/');
        ACN_FatalInternalException = IllegalStateException.class.getName().replace('.', '/');
    }
}
