// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.scostore;

import org.datanucleus.store.rdbms.mapping.datastore.AbstractDatastoreMapping;
import org.datanucleus.store.rdbms.table.JoinTable;
import org.datanucleus.store.rdbms.mapping.MappingHelper;
import org.datanucleus.store.rdbms.mapping.java.EmbeddedValuePCMapping;
import org.datanucleus.store.rdbms.mapping.java.EmbeddedKeyPCMapping;
import org.datanucleus.store.FieldValues;
import org.datanucleus.store.types.SCOUtils;
import java.util.Iterator;
import java.util.Map;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.SQLController;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.ExecutionContext;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.store.rdbms.JDBCUtils;
import java.util.NoSuchElementException;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.scostore.MapStore;

public abstract class AbstractMapStore extends BaseContainerStore implements MapStore
{
    protected boolean iterateUsingDiscriminator;
    protected Table mapTable;
    protected DatastoreClass valueTable;
    protected AbstractClassMetaData kmd;
    protected AbstractClassMetaData vmd;
    protected JavaTypeMapping keyMapping;
    protected JavaTypeMapping valueMapping;
    protected String keyType;
    protected String valueType;
    protected boolean keysAreEmbedded;
    protected boolean keysAreSerialised;
    protected boolean valuesAreEmbedded;
    protected boolean valuesAreSerialised;
    private String containsValueStmt;
    
    public AbstractMapStore(final RDBMSStoreManager storeMgr, final ClassLoaderResolver clr) {
        super(storeMgr, clr);
        this.iterateUsingDiscriminator = false;
    }
    
    protected void initialise() {
        this.containsValueStmt = this.getContainsValueStmt(this.getOwnerMapping(), this.getValueMapping(), this.getMapTable());
    }
    
    @Override
    public boolean keysAreEmbedded() {
        return this.keysAreEmbedded;
    }
    
    @Override
    public boolean keysAreSerialised() {
        return this.keysAreSerialised;
    }
    
    @Override
    public boolean valuesAreEmbedded() {
        return this.valuesAreEmbedded;
    }
    
    @Override
    public boolean valuesAreSerialised() {
        return this.valuesAreSerialised;
    }
    
    @Override
    public boolean containsKey(final ObjectProvider op, final Object key) {
        if (key == null) {
            return false;
        }
        try {
            this.getValue(op, key);
            return true;
        }
        catch (NoSuchElementException e) {
            return false;
        }
    }
    
    @Override
    public boolean containsValue(final ObjectProvider op, final Object value) {
        if (value == null) {
            return false;
        }
        if (!this.validateValueForReading(op, value)) {
            return false;
        }
        boolean exists = false;
        try {
            final ExecutionContext ec = op.getExecutionContext();
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForQuery(mconn, this.containsValueStmt);
                try {
                    int jdbcPosition = 1;
                    jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                    BackingStoreHelper.populateValueInStatement(ec, ps, value, jdbcPosition, this.getValueMapping());
                    final ResultSet rs = sqlControl.executeStatementQuery(ec, mconn, this.containsValueStmt, ps);
                    try {
                        if (rs.next()) {
                            exists = true;
                        }
                        JDBCUtils.logWarnings(rs);
                    }
                    finally {
                        rs.close();
                    }
                }
                finally {
                    sqlControl.closeStatement(mconn, ps);
                }
            }
            finally {
                mconn.release();
            }
        }
        catch (SQLException e) {
            throw new NucleusDataStoreException(AbstractMapStore.LOCALISER.msg("056019", this.containsValueStmt), e);
        }
        return exists;
    }
    
    @Override
    public Object get(final ObjectProvider op, final Object key) {
        try {
            return this.getValue(op, key);
        }
        catch (NoSuchElementException e) {
            return null;
        }
    }
    
    @Override
    public void putAll(final ObjectProvider op, final Map m) {
        for (final Map.Entry e : m.entrySet()) {
            this.put(op, e.getKey(), e.getValue());
        }
    }
    
    protected void validateKeyType(final ClassLoaderResolver clr, final Object key) {
        if (key == null && !this.allowNulls) {
            throw new NullPointerException(AbstractMapStore.LOCALISER.msg("056062"));
        }
        if (key != null && !clr.isAssignableFrom(this.keyType, key.getClass())) {
            throw new ClassCastException(AbstractMapStore.LOCALISER.msg("056064", key.getClass().getName(), this.keyType));
        }
    }
    
    protected void validateValueType(final ClassLoaderResolver clr, final Object value) {
        if (value == null && !this.allowNulls) {
            throw new NullPointerException(AbstractMapStore.LOCALISER.msg("056063"));
        }
        if (value != null && !clr.isAssignableFrom(this.valueType, value.getClass())) {
            throw new ClassCastException(AbstractMapStore.LOCALISER.msg("056065", value.getClass().getName(), this.valueType));
        }
    }
    
    protected boolean validateKeyForReading(final ObjectProvider op, final Object key) {
        this.validateKeyType(op.getExecutionContext().getClassLoaderResolver(), key);
        if (!this.keysAreEmbedded && !this.keysAreSerialised) {
            final ExecutionContext ec = op.getExecutionContext();
            if (key != null && (!ec.getApiAdapter().isPersistent(key) || ec != ec.getApiAdapter().getExecutionContext(key)) && !ec.getApiAdapter().isDetached(key)) {
                return false;
            }
        }
        return true;
    }
    
    protected boolean validateValueForReading(final ObjectProvider op, final Object value) {
        this.validateValueType(op.getExecutionContext().getClassLoaderResolver(), value);
        if (!this.valuesAreEmbedded && !this.valuesAreSerialised) {
            final ExecutionContext ec = op.getExecutionContext();
            if (value != null && (!ec.getApiAdapter().isPersistent(value) || ec != ec.getApiAdapter().getExecutionContext(value)) && !ec.getApiAdapter().isDetached(value)) {
                return false;
            }
        }
        return true;
    }
    
    protected void validateKeyForWriting(final ObjectProvider ownerOP, final Object key) {
        final ExecutionContext ec = ownerOP.getExecutionContext();
        this.validateKeyType(ec.getClassLoaderResolver(), key);
        if (!this.keysAreEmbedded && !this.keysAreSerialised) {
            SCOUtils.validateObjectForWriting(ec, key, null);
        }
    }
    
    protected void validateValueForWriting(final ObjectProvider ownerOP, final Object value) {
        final ExecutionContext ec = ownerOP.getExecutionContext();
        this.validateValueType(ec.getClassLoaderResolver(), value);
        if (!this.valuesAreEmbedded && !this.valuesAreSerialised) {
            SCOUtils.validateObjectForWriting(ec, value, null);
        }
    }
    
    protected abstract Object getValue(final ObjectProvider p0, final Object p1) throws NoSuchElementException;
    
    @Override
    public boolean updateEmbeddedKey(final ObjectProvider op, final Object key, final int fieldNumber, final Object newValue) {
        boolean modified = false;
        if (this.keyMapping != null && this.keyMapping instanceof EmbeddedKeyPCMapping) {
            final String fieldName = this.vmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber).getName();
            if (fieldName == null) {
                return false;
            }
            final JavaTypeMapping fieldMapping = ((EmbeddedKeyPCMapping)this.keyMapping).getJavaTypeMapping(fieldName);
            if (fieldMapping == null) {
                return false;
            }
            modified = this.updatedEmbeddedKey(op, key, fieldNumber, newValue, fieldMapping);
        }
        return modified;
    }
    
    @Override
    public boolean updateEmbeddedValue(final ObjectProvider op, final Object value, final int fieldNumber, final Object newValue) {
        boolean modified = false;
        if (this.valueMapping != null && this.valueMapping instanceof EmbeddedValuePCMapping) {
            final String fieldName = this.vmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber).getName();
            if (fieldName == null) {
                return false;
            }
            final JavaTypeMapping fieldMapping = ((EmbeddedValuePCMapping)this.valueMapping).getJavaTypeMapping(fieldName);
            if (fieldMapping == null) {
                return false;
            }
            modified = this.updateEmbeddedValue(op, value, fieldNumber, newValue, fieldMapping);
        }
        return modified;
    }
    
    public JavaTypeMapping getValueMapping() {
        return this.valueMapping;
    }
    
    public JavaTypeMapping getKeyMapping() {
        return this.keyMapping;
    }
    
    public boolean isValuesAreEmbedded() {
        return this.valuesAreEmbedded;
    }
    
    public boolean isValuesAreSerialised() {
        return this.valuesAreSerialised;
    }
    
    public Table getMapTable() {
        return this.mapTable;
    }
    
    public AbstractClassMetaData getKmd() {
        return this.kmd;
    }
    
    public AbstractClassMetaData getVmd() {
        return this.vmd;
    }
    
    private String getContainsValueStmt(final JavaTypeMapping ownerMapping, final JavaTypeMapping valueMapping, final Table mapTable) {
        final StringBuffer stmt = new StringBuffer("SELECT ");
        for (int i = 0; i < ownerMapping.getNumberOfDatastoreMappings(); ++i) {
            if (i > 0) {
                stmt.append(",");
            }
            stmt.append(ownerMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
        }
        stmt.append(" FROM ");
        stmt.append(mapTable.toString());
        stmt.append(" WHERE ");
        BackingStoreHelper.appendWhereClauseForMapping(stmt, ownerMapping, null, true);
        BackingStoreHelper.appendWhereClauseForMapping(stmt, valueMapping, null, false);
        return stmt.toString();
    }
    
    public boolean updateEmbeddedValue(final ObjectProvider op, final Object value, final int fieldNumber, final Object newValue, final JavaTypeMapping fieldMapping) {
        final String stmt = this.getUpdateEmbeddedValueStmt(fieldMapping, this.getOwnerMapping(), this.getValueMapping(), this.getMapTable());
        boolean modified;
        try {
            final ExecutionContext ec = op.getExecutionContext();
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, stmt, false);
                try {
                    int jdbcPosition = 1;
                    fieldMapping.setObject(ec, ps, MappingHelper.getMappingIndices(jdbcPosition, fieldMapping), newValue);
                    jdbcPosition += fieldMapping.getNumberOfDatastoreMappings();
                    jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                    jdbcPosition = BackingStoreHelper.populateEmbeddedValueFieldsInStatement(op, value, ps, jdbcPosition, (JoinTable)this.getMapTable(), this);
                    sqlControl.executeStatementUpdate(ec, mconn, stmt, ps, true);
                    modified = true;
                }
                finally {
                    sqlControl.closeStatement(mconn, ps);
                }
            }
            finally {
                mconn.release();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new NucleusDataStoreException(AbstractMapStore.LOCALISER.msg("056011", stmt), e);
        }
        return modified;
    }
    
    protected String getUpdateEmbeddedKeyStmt(final JavaTypeMapping fieldMapping, final JavaTypeMapping ownerMapping, final JavaTypeMapping keyMapping, final Table mapTable) {
        final StringBuffer stmt = new StringBuffer("UPDATE ");
        stmt.append(mapTable.toString());
        stmt.append(" SET ");
        for (int i = 0; i < fieldMapping.getNumberOfDatastoreMappings(); ++i) {
            if (i > 0) {
                stmt.append(",");
            }
            stmt.append(fieldMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
            stmt.append(" = ");
            stmt.append(((AbstractDatastoreMapping)fieldMapping.getDatastoreMapping(i)).getUpdateInputParameter());
        }
        stmt.append(" WHERE ");
        BackingStoreHelper.appendWhereClauseForMapping(stmt, ownerMapping, null, true);
        final EmbeddedKeyPCMapping embeddedMapping = (EmbeddedKeyPCMapping)keyMapping;
        for (int j = 0; j < embeddedMapping.getNumberOfJavaTypeMappings(); ++j) {
            final JavaTypeMapping m = embeddedMapping.getJavaTypeMapping(j);
            if (m != null) {
                for (int k = 0; k < m.getNumberOfDatastoreMappings(); ++k) {
                    stmt.append(" AND ");
                    stmt.append(m.getDatastoreMapping(k).getColumn().getIdentifier().toString());
                    stmt.append(" = ");
                    stmt.append(((AbstractDatastoreMapping)m.getDatastoreMapping(k)).getUpdateInputParameter());
                }
            }
        }
        return stmt.toString();
    }
    
    protected String getUpdateEmbeddedValueStmt(final JavaTypeMapping fieldMapping, final JavaTypeMapping ownerMapping, final JavaTypeMapping valueMapping, final Table mapTable) {
        final StringBuffer stmt = new StringBuffer("UPDATE ");
        stmt.append(mapTable.toString());
        stmt.append(" SET ");
        for (int i = 0; i < fieldMapping.getNumberOfDatastoreMappings(); ++i) {
            if (i > 0) {
                stmt.append(",");
            }
            stmt.append(fieldMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
            stmt.append(" = ");
            stmt.append(((AbstractDatastoreMapping)fieldMapping.getDatastoreMapping(i)).getUpdateInputParameter());
        }
        stmt.append(" WHERE ");
        BackingStoreHelper.appendWhereClauseForMapping(stmt, ownerMapping, null, true);
        final EmbeddedValuePCMapping embeddedMapping = (EmbeddedValuePCMapping)valueMapping;
        for (int j = 0; j < embeddedMapping.getNumberOfJavaTypeMappings(); ++j) {
            final JavaTypeMapping m = embeddedMapping.getJavaTypeMapping(j);
            if (m != null) {
                for (int k = 0; k < m.getNumberOfDatastoreMappings(); ++k) {
                    stmt.append(" AND ");
                    stmt.append(m.getDatastoreMapping(k).getColumn().getIdentifier().toString());
                    stmt.append(" = ");
                    stmt.append(((AbstractDatastoreMapping)m.getDatastoreMapping(k)).getUpdateInputParameter());
                }
            }
        }
        return stmt.toString();
    }
    
    public boolean updatedEmbeddedKey(final ObjectProvider op, final Object key, final int fieldNumber, final Object newValue, final JavaTypeMapping fieldMapping) {
        final String stmt = this.getUpdateEmbeddedKeyStmt(fieldMapping, this.getOwnerMapping(), this.getKeyMapping(), this.getMapTable());
        boolean modified;
        try {
            final ExecutionContext ec = op.getExecutionContext();
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final SQLController sqlControl = this.storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, stmt, false);
                try {
                    int jdbcPosition = 1;
                    fieldMapping.setObject(ec, ps, MappingHelper.getMappingIndices(jdbcPosition, fieldMapping), key);
                    jdbcPosition += fieldMapping.getNumberOfDatastoreMappings();
                    jdbcPosition = BackingStoreHelper.populateOwnerInStatement(op, ec, ps, jdbcPosition, this);
                    jdbcPosition = BackingStoreHelper.populateEmbeddedKeyFieldsInStatement(op, key, ps, jdbcPosition, (JoinTable)this.getMapTable(), this);
                    sqlControl.executeStatementUpdate(ec, mconn, stmt, ps, true);
                    modified = true;
                }
                finally {
                    sqlControl.closeStatement(mconn, ps);
                }
            }
            finally {
                mconn.release();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new NucleusDataStoreException(AbstractMapStore.LOCALISER.msg("056010", stmt), e);
        }
        return modified;
    }
}
