// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder.combined;

import org.apache.commons.configuration2.beanutils.BeanHelper;
import org.apache.commons.beanutils.DynaProperty;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaBean;

class MultiWrapDynaBean implements DynaBean
{
    private final DynaClass dynaClass;
    private final Map<String, DynaBean> propsToBeans;
    
    public MultiWrapDynaBean(final Collection<?> beans) {
        this.propsToBeans = new HashMap<String, DynaBean>();
        final Collection<DynaClass> beanClasses = new ArrayList<DynaClass>(beans.size());
        for (final Object bean : beans) {
            final DynaBean dynaBean = createDynaBean(bean);
            final DynaClass beanClass = dynaBean.getDynaClass();
            for (final DynaProperty prop : beanClass.getDynaProperties()) {
                if (!this.propsToBeans.containsKey(prop.getName())) {
                    this.propsToBeans.put(prop.getName(), dynaBean);
                }
            }
            beanClasses.add(beanClass);
        }
        this.dynaClass = new MultiWrapDynaClass(beanClasses);
    }
    
    @Override
    public boolean contains(final String name, final String key) {
        throw new UnsupportedOperationException("contains() operation not supported!");
    }
    
    @Override
    public Object get(final String name) {
        return this.fetchBean(name).get(name);
    }
    
    @Override
    public Object get(final String name, final int index) {
        return this.fetchBean(name).get(name, index);
    }
    
    @Override
    public Object get(final String name, final String key) {
        return this.fetchBean(name).get(name, key);
    }
    
    @Override
    public DynaClass getDynaClass() {
        return this.dynaClass;
    }
    
    @Override
    public void remove(final String name, final String key) {
        throw new UnsupportedOperationException("remove() operation not supported!");
    }
    
    @Override
    public void set(final String name, final Object value) {
        this.fetchBean(name).set(name, value);
    }
    
    @Override
    public void set(final String name, final int index, final Object value) {
        this.fetchBean(name).set(name, index, value);
    }
    
    @Override
    public void set(final String name, final String key, final Object value) {
        this.fetchBean(name).set(name, key, value);
    }
    
    private DynaBean fetchBean(final String property) {
        DynaBean dynaBean = this.propsToBeans.get(property);
        if (dynaBean == null) {
            dynaBean = this.propsToBeans.values().iterator().next();
        }
        return dynaBean;
    }
    
    private static DynaBean createDynaBean(final Object bean) {
        if (bean instanceof DynaBean) {
            return (DynaBean)bean;
        }
        return BeanHelper.createWrapDynaBean(bean);
    }
}
