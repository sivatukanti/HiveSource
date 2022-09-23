// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.rdbms.mapping.datastore.DatastoreMapping;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.metadata.VersionMetaData;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.store.rdbms.table.Table;

public class VersionMapping extends SingleFieldMapping
{
    private final JavaTypeMapping delegate;
    
    public VersionMapping(final Table table, final JavaTypeMapping delegate) {
        this.initialize(table.getStoreManager(), delegate.getType());
        this.delegate = delegate;
        this.table = table;
        final VersionMetaData vermd = table.getVersionMetaData();
        final ColumnMetaData versionColumnMetaData = vermd.getColumnMetaData();
        final IdentifierFactory idFactory = table.getStoreManager().getIdentifierFactory();
        DatastoreIdentifier id = null;
        ColumnMetaData colmd;
        if (versionColumnMetaData == null) {
            id = idFactory.newVersionFieldIdentifier();
            colmd = new ColumnMetaData();
            colmd.setName(id.getIdentifierName());
            table.getVersionMetaData().setColumnMetaData(colmd);
        }
        else {
            colmd = versionColumnMetaData;
            if (colmd.getName() == null) {
                id = idFactory.newVersionFieldIdentifier();
                colmd.setName(id.getIdentifierName());
            }
            else {
                id = idFactory.newColumnIdentifier(colmd.getName());
            }
        }
        final Column column = table.addColumn(this.getType(), id, this, colmd);
        table.getStoreManager().getMappingManager().createDatastoreMapping(delegate, column, this.getType());
    }
    
    @Override
    public boolean includeInFetchStatement() {
        return false;
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
    
    @Override
    public Class getJavaType() {
        return VersionMapping.class;
    }
    
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final Object value) {
        this.delegate.setObject(ec, ps, exprIndex, value);
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        return this.delegate.getObject(ec, resultSet, exprIndex);
    }
}
