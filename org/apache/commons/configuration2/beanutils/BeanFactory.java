// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.beanutils;

public interface BeanFactory
{
    Object createBean(final BeanCreationContext p0) throws Exception;
    
    Class<?> getDefaultBeanClass();
}
