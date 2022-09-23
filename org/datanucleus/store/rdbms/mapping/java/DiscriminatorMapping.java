// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.store.rdbms.mapping.MappingManager;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.metadata.DiscriminatorStrategy;
import org.datanucleus.store.rdbms.mapping.datastore.DatastoreMapping;
import java.sql.ResultSet;
import org.datanucleus.ClassNameConstants;
import java.sql.PreparedStatement;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.metadata.DiscriminatorMetaData;
import org.datanucleus.store.rdbms.table.Table;

public class DiscriminatorMapping extends SingleFieldMapping
{
    private final JavaTypeMapping delegate;
    
    public DiscriminatorMapping(final Table table, final JavaTypeMapping delegate, final DiscriminatorMetaData dismd) {
        this.initialize(table.getStoreManager(), delegate.getType());
        this.table = table;
        this.delegate = delegate;
        final IdentifierFactory idFactory = table.getStoreManager().getIdentifierFactory();
        DatastoreIdentifier id = null;
        if (dismd.getColumnMetaData() == null) {
            id = idFactory.newDiscriminatorFieldIdentifier();
            final ColumnMetaData colmd = new ColumnMetaData();
            colmd.setName(id.getIdentifierName());
            dismd.setColumnMetaData(colmd);
        }
        else {
            final ColumnMetaData colmd = dismd.getColumnMetaData();
            if (colmd.getName() == null) {
                id = idFactory.newDiscriminatorFieldIdentifier();
                colmd.setName(id.getIdentifierName());
            }
            else {
                id = idFactory.newColumnIdentifier(colmd.getName());
            }
        }
        final Column column = table.addColumn(this.getType(), id, this, dismd.getColumnMetaData());
        table.getStoreManager().getMappingManager().createDatastoreMapping(delegate, column, this.getType());
    }
    
    @Override
    public Class getJavaType() {
        return DiscriminatorMapping.class;
    }
    
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final Object value) {
        Object valueObj = value;
        if (value instanceof String && (this.getType().equals(ClassNameConstants.LONG) || this.getType().equals(ClassNameConstants.JAVA_LANG_LONG))) {
            valueObj = Long.valueOf((String)value);
        }
        this.delegate.setObject(ec, ps, exprIndex, valueObj);
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        Object valueObj;
        final Object value = valueObj = this.delegate.getObject(ec, resultSet, exprIndex);
        if (value instanceof String && (this.getType().equals(ClassNameConstants.LONG) || this.getType().equals(ClassNameConstants.JAVA_LANG_LONG))) {
            valueObj = Long.valueOf((String)value);
        }
        return valueObj;
    }
    
    @Override
    public int getNumberOfDatastoreMappings() {
        return this.delegate.getNumberOfDatastoreMappings();
    }
    
    @Override
    public DatastoreMapping getDatastoreMapping(final int index) {
        return this.delegate.getDatastoreMapping(index);
    }
    
    @Override
    public DatastoreMapping[] getDatastoreMappings() {
        return this.delegate.getDatastoreMappings();
    }
    
    @Override
    public void addDatastoreMapping(final DatastoreMapping datastoreMapping) {
        this.delegate.addDatastoreMapping(datastoreMapping);
    }
    
    public static DiscriminatorMapping createDiscriminatorMapping(final Table table, final DiscriminatorMetaData dismd) {
        final RDBMSStoreManager storeMgr = table.getStoreManager();
        final MappingManager mapMgr = storeMgr.getMappingManager();
        if (dismd.getStrategy() == DiscriminatorStrategy.CLASS_NAME) {
            return new DiscriminatorStringMapping(table, mapMgr.getMapping(String.class), dismd);
        }
        if (dismd.getStrategy() != DiscriminatorStrategy.VALUE_MAP) {
            return null;
        }
        final ColumnMetaData disColmd = dismd.getColumnMetaData();
        if (disColmd == null || disColmd.getJdbcType() == null) {
            return new DiscriminatorStringMapping(table, mapMgr.getMapping(String.class), dismd);
        }
        if (disColmd.getJdbcType().equalsIgnoreCase("INTEGER") || disColmd.getJdbcType().equalsIgnoreCase("BIGINT") || disColmd.getJdbcType().equalsIgnoreCase("NUMERIC")) {
            return new DiscriminatorLongMapping(table, mapMgr.getMapping(Long.class), dismd);
        }
        return new DiscriminatorStringMapping(table, mapMgr.getMapping(String.class), dismd);
    }
}
