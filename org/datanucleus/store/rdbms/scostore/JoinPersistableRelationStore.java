// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.scostore;

import org.datanucleus.store.rdbms.mapping.MappingHelper;
import org.datanucleus.store.rdbms.mapping.datastore.AbstractDatastoreMapping;
import java.sql.PreparedStatement;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.rdbms.SQLController;
import org.datanucleus.ExecutionContext;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.StoreManager;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.table.PersistableJoinTable;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.scostore.PersistableRelationStore;

public class JoinPersistableRelationStore implements PersistableRelationStore
{
    protected RDBMSStoreManager storeMgr;
    protected DatastoreAdapter dba;
    protected JavaTypeMapping ownerMapping;
    protected AbstractMemberMetaData ownerMemberMetaData;
    protected PersistableJoinTable joinTable;
    protected ClassLoaderResolver clr;
    protected String addStmt;
    protected String updateStmt;
    protected String removeStmt;
    
    public JoinPersistableRelationStore(final AbstractMemberMetaData mmd, final PersistableJoinTable joinTable, final ClassLoaderResolver clr) {
        this.storeMgr = joinTable.getStoreManager();
        this.dba = this.storeMgr.getDatastoreAdapter();
        this.ownerMemberMetaData = mmd;
        this.joinTable = joinTable;
        this.clr = clr;
    }
    
    @Override
    public StoreManager getStoreManager() {
        return this.storeMgr;
    }
    
    @Override
    public AbstractMemberMetaData getOwnerMemberMetaData() {
        return this.ownerMemberMetaData;
    }
    
    @Override
    public boolean add(final ObjectProvider sm1, final ObjectProvider sm2) {
        final String addStmt = this.getAddStmt();
        final ExecutionContext ec = sm1.getExecutionContext();
        final SQLController sqlControl = this.storeMgr.getSQLController();
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, addStmt, false);
            try {
                int jdbcPosition = 1;
                jdbcPosition = populateOwnerInStatement(sm1, ec, ps, jdbcPosition, this.joinTable);
                BackingStoreHelper.populateElementInStatement(ec, ps, sm2.getObject(), jdbcPosition, this.joinTable.getRelatedMapping());
                final int[] nums = sqlControl.executeStatementUpdate(ec, mconn, addStmt, ps, true);
                return nums != null && nums.length == 1 && nums[0] == 1;
            }
            finally {
                sqlControl.closeStatement(mconn, ps);
                mconn.release();
            }
        }
        catch (SQLException sqle) {
            throw new NucleusDataStoreException("Exception thrown inserting row into persistable relation join table", sqle);
        }
    }
    
    @Override
    public boolean remove(final ObjectProvider op) {
        final String removeStmt = this.getRemoveStmt();
        final ExecutionContext ec = op.getExecutionContext();
        final SQLController sqlControl = this.storeMgr.getSQLController();
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, removeStmt, false);
            try {
                final int jdbcPosition = 1;
                populateOwnerInStatement(op, ec, ps, jdbcPosition, this.joinTable);
                final int[] nums = sqlControl.executeStatementUpdate(ec, mconn, removeStmt, ps, true);
                return nums != null && nums.length == 1 && nums[0] == 1;
            }
            finally {
                sqlControl.closeStatement(mconn, ps);
                mconn.release();
            }
        }
        catch (SQLException sqle) {
            throw new NucleusDataStoreException("Exception thrown deleting row from persistable relation join table", sqle);
        }
    }
    
    @Override
    public boolean update(final ObjectProvider sm1, final ObjectProvider sm2) {
        final String updateStmt = this.getUpdateStmt();
        final ExecutionContext ec = sm1.getExecutionContext();
        final SQLController sqlControl = this.storeMgr.getSQLController();
        try {
            final ManagedConnection mconn = this.storeMgr.getConnection(ec);
            final PreparedStatement ps = sqlControl.getStatementForUpdate(mconn, updateStmt, false);
            try {
                int jdbcPosition = 1;
                jdbcPosition = BackingStoreHelper.populateElementInStatement(ec, ps, sm2.getObject(), jdbcPosition, this.joinTable.getRelatedMapping());
                populateOwnerInStatement(sm1, ec, ps, jdbcPosition, this.joinTable);
                final int[] nums = sqlControl.executeStatementUpdate(ec, mconn, updateStmt, ps, true);
                return nums != null && nums.length == 1 && nums[0] == 1;
            }
            finally {
                sqlControl.closeStatement(mconn, ps);
                mconn.release();
            }
        }
        catch (SQLException sqle) {
            throw new NucleusDataStoreException("Exception thrown updating row into persistable relation join table", sqle);
        }
    }
    
    protected String getAddStmt() {
        if (this.addStmt == null) {
            final JavaTypeMapping ownerMapping = this.joinTable.getOwnerMapping();
            final JavaTypeMapping relatedMapping = this.joinTable.getRelatedMapping();
            final StringBuffer stmt = new StringBuffer("INSERT INTO ");
            stmt.append(this.joinTable.toString());
            stmt.append(" (");
            for (int i = 0; i < ownerMapping.getNumberOfDatastoreMappings(); ++i) {
                if (i > 0) {
                    stmt.append(",");
                }
                stmt.append(ownerMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
            }
            for (int i = 0; i < relatedMapping.getNumberOfDatastoreMappings(); ++i) {
                stmt.append(",");
                stmt.append(relatedMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
            }
            stmt.append(") VALUES (");
            for (int i = 0; i < ownerMapping.getNumberOfDatastoreMappings(); ++i) {
                if (i > 0) {
                    stmt.append(",");
                }
                stmt.append(((AbstractDatastoreMapping)ownerMapping.getDatastoreMapping(i)).getInsertionInputParameter());
            }
            for (int i = 0; i < relatedMapping.getNumberOfDatastoreMappings(); ++i) {
                stmt.append(",");
                stmt.append(((AbstractDatastoreMapping)relatedMapping.getDatastoreMapping(0)).getInsertionInputParameter());
            }
            stmt.append(") ");
            this.addStmt = stmt.toString();
        }
        return this.addStmt;
    }
    
    protected String getUpdateStmt() {
        if (this.updateStmt == null) {
            final JavaTypeMapping ownerMapping = this.joinTable.getOwnerMapping();
            final JavaTypeMapping relatedMapping = this.joinTable.getRelatedMapping();
            final StringBuffer stmt = new StringBuffer("UPDATE ");
            stmt.append(this.joinTable.toString());
            stmt.append(" SET ");
            for (int i = 0; i < relatedMapping.getNumberOfDatastoreMappings(); ++i) {
                if (i > 0) {
                    stmt.append(",");
                }
                stmt.append(relatedMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
                stmt.append("=");
                stmt.append(((AbstractDatastoreMapping)ownerMapping.getDatastoreMapping(i)).getInsertionInputParameter());
            }
            stmt.append(" WHERE ");
            BackingStoreHelper.appendWhereClauseForMapping(stmt, ownerMapping, null, true);
            this.updateStmt = stmt.toString();
        }
        return this.updateStmt;
    }
    
    protected String getRemoveStmt() {
        if (this.removeStmt == null) {
            final JavaTypeMapping ownerMapping = this.joinTable.getOwnerMapping();
            final StringBuffer stmt = new StringBuffer("DELETE FROM ");
            stmt.append(this.joinTable.toString());
            stmt.append(" WHERE ");
            BackingStoreHelper.appendWhereClauseForMapping(stmt, ownerMapping, null, true);
            this.removeStmt = stmt.toString();
        }
        return this.removeStmt;
    }
    
    public static int populateOwnerInStatement(final ObjectProvider sm, final ExecutionContext ec, final PreparedStatement ps, final int jdbcPosition, final PersistableJoinTable joinTable) {
        if (!joinTable.getStoreManager().insertValuesOnInsert(joinTable.getOwnerMapping().getDatastoreMapping(0))) {
            return jdbcPosition;
        }
        if (joinTable.getOwnerMemberMetaData() != null) {
            joinTable.getOwnerMapping().setObject(ec, ps, MappingHelper.getMappingIndices(jdbcPosition, joinTable.getOwnerMapping()), sm.getObject(), sm, joinTable.getOwnerMemberMetaData().getAbsoluteFieldNumber());
        }
        else {
            joinTable.getOwnerMapping().setObject(ec, ps, MappingHelper.getMappingIndices(jdbcPosition, joinTable.getOwnerMapping()), sm.getObject());
        }
        return jdbcPosition + joinTable.getOwnerMapping().getNumberOfDatastoreMappings();
    }
}
