// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.types.converters.ClassStringConverter;

public class ClassMapping extends ObjectAsStringMapping
{
    private static ClassStringConverter converter;
    
    @Override
    public void initialize(final AbstractMemberMetaData fmd, final Table table, final ClassLoaderResolver clr) {
        super.initialize(fmd, table, clr);
        ClassMapping.converter.setClassLoaderResolver(this.storeMgr.getNucleusContext().getClassLoaderResolver(null));
    }
    
    @Override
    public Class getJavaType() {
        return Class.class;
    }
    
    @Override
    protected String objectToString(final Object object) {
        return ClassMapping.converter.toDatastoreType((Class)object);
    }
    
    @Override
    protected Object stringToObject(final String datastoreValue) {
        return ClassMapping.converter.toMemberType(datastoreValue);
    }
    
    static {
        ClassMapping.converter = new ClassStringConverter();
    }
}
