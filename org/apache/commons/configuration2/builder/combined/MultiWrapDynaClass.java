// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder.combined;

import java.util.Iterator;
import org.apache.commons.beanutils.DynaBean;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Collection;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.DynaClass;

class MultiWrapDynaClass implements DynaClass
{
    private static final DynaProperty[] EMPTY_PROPS;
    private final Collection<DynaProperty> properties;
    private final Map<String, DynaProperty> namedProperties;
    
    public MultiWrapDynaClass(final Collection<? extends DynaClass> wrappedCls) {
        this.properties = new LinkedList<DynaProperty>();
        this.namedProperties = new HashMap<String, DynaProperty>();
        this.initProperties(wrappedCls);
    }
    
    @Override
    public String getName() {
        return null;
    }
    
    @Override
    public DynaProperty getDynaProperty(final String name) {
        return this.namedProperties.get(name);
    }
    
    @Override
    public DynaProperty[] getDynaProperties() {
        return this.properties.toArray(MultiWrapDynaClass.EMPTY_PROPS);
    }
    
    @Override
    public DynaBean newInstance() throws IllegalAccessException, InstantiationException {
        throw new UnsupportedOperationException("Cannot create an instance of MultiWrapDynaBean!");
    }
    
    private void initProperties(final Collection<? extends DynaClass> wrappedCls) {
        for (final DynaClass cls : wrappedCls) {
            final DynaProperty[] dynaProperties;
            final DynaProperty[] props = dynaProperties = cls.getDynaProperties();
            for (final DynaProperty p : dynaProperties) {
                this.properties.add(p);
                this.namedProperties.put(p.getName(), p);
            }
        }
    }
    
    static {
        EMPTY_PROPS = new DynaProperty[0];
    }
}
