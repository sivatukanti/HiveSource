// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.table;

import org.datanucleus.store.rdbms.exceptions.ColumnDefinitionException;
import org.datanucleus.store.rdbms.schema.RDBMSColumnInfo;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.store.rdbms.mapping.datastore.DatastoreMapping;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.RDBMSStoreManager;

public interface Column
{
    public static final int WRAPPER_FUNCTION_SELECT = 0;
    public static final int WRAPPER_FUNCTION_INSERT = 1;
    public static final int WRAPPER_FUNCTION_UPDATE = 2;
    
    RDBMSStoreManager getStoreManager();
    
    String getStoredJavaType();
    
    void setIdentifier(final DatastoreIdentifier p0);
    
    DatastoreIdentifier getIdentifier();
    
    void setAsPrimaryKey();
    
    boolean isPrimaryKey();
    
    Column setNullable();
    
    boolean isNullable();
    
    Column setDefaultable();
    
    boolean isDefaultable();
    
    Column setUnique();
    
    boolean isUnique();
    
    Column setIdentity(final boolean p0);
    
    boolean isIdentity();
    
    void setDefaultValue(final Object p0);
    
    Object getDefaultValue();
    
    void setDatastoreMapping(final DatastoreMapping p0);
    
    DatastoreMapping getDatastoreMapping();
    
    void setColumnMetaData(final ColumnMetaData p0);
    
    ColumnMetaData getColumnMetaData();
    
    JavaTypeMapping getJavaTypeMapping();
    
    Table getTable();
    
    String applySelectFunction(final String p0);
    
    void copyConfigurationTo(final Column p0);
    
    AbstractMemberMetaData getMemberMetaData();
    
    boolean isUnlimitedLength();
    
    Column setTypeInfo(final SQLTypeInfo p0);
    
    SQLTypeInfo getTypeInfo();
    
    int getJdbcType();
    
    String getSQLDefinition();
    
    void initializeColumnInfoFromDatastore(final RDBMSColumnInfo p0);
    
    void validate(final RDBMSColumnInfo p0);
    
    Column setConstraints(final String p0);
    
    String getConstraints();
    
    void checkPrimitive() throws ColumnDefinitionException;
    
    void checkInteger() throws ColumnDefinitionException;
    
    void checkDecimal() throws ColumnDefinitionException;
    
    void checkString() throws ColumnDefinitionException;
    
    void setWrapperFunction(final String p0, final int p1);
    
    String getWrapperFunction(final int p0);
}
