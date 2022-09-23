// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.beanutils;

public interface BeanCreationContext
{
    Class<?> getBeanClass();
    
    BeanDeclaration getBeanDeclaration();
    
    Object getParameter();
    
    void initBean(final Object p0, final BeanDeclaration p1);
    
    Object createBean(final BeanDeclaration p0);
}
