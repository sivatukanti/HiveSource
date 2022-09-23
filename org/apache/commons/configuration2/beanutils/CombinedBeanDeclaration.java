// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.beanutils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CombinedBeanDeclaration implements BeanDeclaration
{
    private final List<BeanDeclaration> childDeclarations;
    
    public CombinedBeanDeclaration(final BeanDeclaration... decl) {
        this.childDeclarations = new ArrayList<BeanDeclaration>(Arrays.asList(decl));
    }
    
    @Override
    public String getBeanFactoryName() {
        for (final BeanDeclaration d : this.childDeclarations) {
            final String factoryName = d.getBeanFactoryName();
            if (factoryName != null) {
                return factoryName;
            }
        }
        return null;
    }
    
    @Override
    public Object getBeanFactoryParameter() {
        for (final BeanDeclaration d : this.childDeclarations) {
            final Object factoryParam = d.getBeanFactoryParameter();
            if (factoryParam != null) {
                return factoryParam;
            }
        }
        return null;
    }
    
    @Override
    public String getBeanClassName() {
        for (final BeanDeclaration d : this.childDeclarations) {
            final String beanClassName = d.getBeanClassName();
            if (beanClassName != null) {
                return beanClassName;
            }
        }
        return null;
    }
    
    @Override
    public Map<String, Object> getBeanProperties() {
        final Map<String, Object> result = new HashMap<String, Object>();
        for (int i = this.childDeclarations.size() - 1; i >= 0; --i) {
            final Map<String, Object> props = this.childDeclarations.get(i).getBeanProperties();
            if (props != null) {
                result.putAll(props);
            }
        }
        return result;
    }
    
    @Override
    public Map<String, Object> getNestedBeanDeclarations() {
        final Map<String, Object> result = new HashMap<String, Object>();
        for (int i = this.childDeclarations.size() - 1; i >= 0; --i) {
            final Map<String, Object> decls = this.childDeclarations.get(i).getNestedBeanDeclarations();
            if (decls != null) {
                result.putAll(decls);
            }
        }
        return result;
    }
    
    @Override
    public Collection<ConstructorArg> getConstructorArgs() {
        for (final BeanDeclaration d : this.childDeclarations) {
            final Collection<ConstructorArg> args = d.getConstructorArgs();
            if (args != null && !args.isEmpty()) {
                return args;
            }
        }
        return (Collection<ConstructorArg>)Collections.emptyList();
    }
}
