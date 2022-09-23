// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.store.types.converters.TypeConverter;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.types.TypeManager;
import org.datanucleus.store.rdbms.RDBMSStoreManager;

public class TypeConverterLongMapping extends TypeConverterMapping
{
    @Override
    public void initialize(final RDBMSStoreManager storeMgr, final String type) {
        super.initialize(storeMgr, type);
        final ClassLoaderResolver clr = storeMgr.getNucleusContext().getClassLoaderResolver(null);
        final Class fieldType = clr.classForName(type);
        final Class datastoreType = TypeManager.getDatastoreTypeForTypeConverter(this.converter, fieldType);
        if (!Long.class.isAssignableFrom(datastoreType)) {
            throw new NucleusException("Attempt to create TypeConverterLongMapping for type " + type + " yet this is not using Long in the datastore");
        }
    }
    
    @Override
    public void initialize(final AbstractMemberMetaData mmd, final Table table, final ClassLoaderResolver clr) {
        this.initialize(mmd, table, clr, null);
    }
    
    @Override
    public void initialize(final AbstractMemberMetaData fmd, final Table table, final ClassLoaderResolver clr, final TypeConverter conv) {
        super.initialize(fmd, table, clr, conv);
        final Class datastoreType = TypeManager.getDatastoreTypeForTypeConverter(this.converter, this.mmd.getType());
        if (!Long.class.isAssignableFrom(datastoreType)) {
            throw new NucleusException("Attempt to create TypeConverterLongMapping for member " + this.mmd.getFullFieldName() + " yet this is not using Long in the datastore");
        }
    }
    
    @Override
    public String getJavaTypeForDatastoreMapping(final int index) {
        return Long.class.getName();
    }
}
