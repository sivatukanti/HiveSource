// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping;

import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.mapping.datastore.DatastoreMapping;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.plugin.PluginManager;

public interface MappingManager
{
    void loadDatastoreMapping(final PluginManager p0, final ClassLoaderResolver p1, final String p2);
    
    DatastoreMapping createDatastoreMapping(final JavaTypeMapping p0, final AbstractMemberMetaData p1, final int p2, final Column p3);
    
    DatastoreMapping createDatastoreMapping(final JavaTypeMapping p0, final Column p1, final String p2);
    
    JavaTypeMapping getMapping(final Class p0);
    
    JavaTypeMapping getMapping(final Class p0, final boolean p1, final boolean p2, final String p3);
    
    JavaTypeMapping getMappingWithDatastoreMapping(final Class p0, final boolean p1, final boolean p2, final ClassLoaderResolver p3);
    
    JavaTypeMapping getMapping(final Table p0, final AbstractMemberMetaData p1, final ClassLoaderResolver p2, final int p3);
    
    Column createColumn(final JavaTypeMapping p0, final String p1, final int p2);
    
    Column createColumn(final JavaTypeMapping p0, final String p1, final ColumnMetaData p2);
    
    Column createColumn(final AbstractMemberMetaData p0, final Table p1, final JavaTypeMapping p2, final ColumnMetaData p3, final Column p4, final ClassLoaderResolver p5);
}
