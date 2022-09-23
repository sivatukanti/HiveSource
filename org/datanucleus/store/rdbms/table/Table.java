// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.table;

import java.sql.SQLException;
import java.util.Collection;
import java.sql.Connection;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.VersionMetaData;
import org.datanucleus.metadata.DiscriminatorMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.RDBMSStoreManager;

public interface Table
{
    RDBMSStoreManager getStoreManager();
    
    DatastoreIdentifier getIdentifier();
    
    String getCatalogName();
    
    String getSchemaName();
    
    Column addColumn(final String p0, final DatastoreIdentifier p1, final JavaTypeMapping p2, final ColumnMetaData p3);
    
    boolean hasColumn(final DatastoreIdentifier p0);
    
    Column getColumn(final DatastoreIdentifier p0);
    
    Column[] getColumns();
    
    JavaTypeMapping getIdMapping();
    
    JavaTypeMapping getMemberMapping(final AbstractMemberMetaData p0);
    
    DiscriminatorMetaData getDiscriminatorMetaData();
    
    JavaTypeMapping getDiscriminatorMapping(final boolean p0);
    
    JavaTypeMapping getMultitenancyMapping();
    
    VersionMetaData getVersionMetaData();
    
    JavaTypeMapping getVersionMapping(final boolean p0);
    
    void preInitialize(final ClassLoaderResolver p0);
    
    void initialize(final ClassLoaderResolver p0);
    
    void postInitialize(final ClassLoaderResolver p0);
    
    boolean isInitialized();
    
    boolean isInitializedModified();
    
    boolean validate(final Connection p0, final boolean p1, final boolean p2, final Collection p3) throws SQLException;
    
    boolean isValidated();
    
    boolean exists(final Connection p0, final boolean p1) throws SQLException;
    
    boolean create(final Connection p0) throws SQLException;
    
    void drop(final Connection p0) throws SQLException;
}
