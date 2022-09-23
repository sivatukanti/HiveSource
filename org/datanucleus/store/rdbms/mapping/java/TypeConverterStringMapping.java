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

public class TypeConverterStringMapping extends TypeConverterMapping
{
    @Override
    public void initialize(final RDBMSStoreManager storeMgr, final String type) {
        super.initialize(storeMgr, type);
        final ClassLoaderResolver clr = storeMgr.getNucleusContext().getClassLoaderResolver(null);
        final Class fieldType = clr.classForName(type);
        final Class datastoreType = TypeManager.getDatastoreTypeForTypeConverter(this.converter, fieldType);
        if (!String.class.isAssignableFrom(datastoreType)) {
            throw new NucleusException("Attempt to create TypeConverterStringMapping for type " + type + " yet this is not using String in the datastore");
        }
    }
    
    @Override
    public void initialize(final AbstractMemberMetaData mmd, final Table table, final ClassLoaderResolver clr) {
        this.initialize(mmd, table, clr, null);
    }
    
    @Override
    public void initialize(final AbstractMemberMetaData mmd, final Table table, final ClassLoaderResolver clr, final TypeConverter conv) {
        super.initialize(mmd, table, clr, conv);
        final Class datastoreType = TypeManager.getDatastoreTypeForTypeConverter(this.converter, mmd.getType());
        if (!String.class.isAssignableFrom(datastoreType)) {
            throw new NucleusException("Attempt to create TypeConverterStringMapping for member " + mmd.getFullFieldName() + " yet this is not using String in the datastore");
        }
    }
    
    @Override
    public String getJavaTypeForDatastoreMapping(final int index) {
        return String.class.getName();
    }
}
