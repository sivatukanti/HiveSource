// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import java.util.HashMap;
import java.lang.reflect.InvocationTargetException;
import org.datanucleus.exceptions.NucleusException;
import java.lang.reflect.Constructor;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import java.util.Map;
import org.datanucleus.util.Localiser;

public final class DatastoreMappingFactory
{
    private static final Localiser LOCALISER;
    private static Map DATASTORE_MAPPING_CONSTRUCTOR_BY_CLASS;
    private static final Class[] DATASTORE_MAPPING_CTR_ARG_CLASSES;
    
    private DatastoreMappingFactory() {
    }
    
    public static DatastoreMapping createMapping(final Class mappingClass, final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column column) {
        Object obj = null;
        try {
            final Object[] args = { mapping, storeMgr, column };
            Constructor ctr = DatastoreMappingFactory.DATASTORE_MAPPING_CONSTRUCTOR_BY_CLASS.get(mappingClass);
            if (ctr == null) {
                ctr = mappingClass.getConstructor((Class[])DatastoreMappingFactory.DATASTORE_MAPPING_CTR_ARG_CLASSES);
                DatastoreMappingFactory.DATASTORE_MAPPING_CONSTRUCTOR_BY_CLASS.put(mappingClass, ctr);
            }
            try {
                obj = ctr.newInstance(args);
            }
            catch (InvocationTargetException e) {
                throw new NucleusException(DatastoreMappingFactory.LOCALISER.msg("041009", mappingClass.getName(), e.getTargetException()), e.getTargetException()).setFatal();
            }
            catch (Exception e2) {
                throw new NucleusException(DatastoreMappingFactory.LOCALISER.msg("041009", mappingClass.getName(), e2), e2).setFatal();
            }
        }
        catch (NoSuchMethodException nsme) {
            throw new NucleusException(DatastoreMappingFactory.LOCALISER.msg("041007", JavaTypeMapping.class, RDBMSStoreManager.class, Column.class, mappingClass.getName())).setFatal();
        }
        return (DatastoreMapping)obj;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
        DatastoreMappingFactory.DATASTORE_MAPPING_CONSTRUCTOR_BY_CLASS = new HashMap();
        DATASTORE_MAPPING_CTR_ARG_CLASSES = new Class[] { JavaTypeMapping.class, RDBMSStoreManager.class, Column.class };
    }
}
