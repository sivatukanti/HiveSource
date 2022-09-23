// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.beanutils;

import java.util.Collection;
import java.util.Map;

public interface BeanDeclaration
{
    String getBeanFactoryName();
    
    Object getBeanFactoryParameter();
    
    String getBeanClassName();
    
    Map<String, Object> getBeanProperties();
    
    Map<String, Object> getNestedBeanDeclarations();
    
    Collection<ConstructorArg> getConstructorArgs();
}
