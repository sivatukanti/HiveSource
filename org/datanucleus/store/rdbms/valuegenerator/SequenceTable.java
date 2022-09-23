// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.valuegenerator;

import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.valuegenerator.ValueGenerationException;
import java.sql.SQLException;
import java.sql.ResultSet;
import org.datanucleus.store.rdbms.SQLController;
import java.sql.PreparedStatement;
import org.datanucleus.ExecutionContext;
import java.util.HashSet;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.table.TableImpl;

public class SequenceTable extends TableImpl
{
    private JavaTypeMapping sequenceNameMapping;
    private JavaTypeMapping nextValMapping;
    private String insertStmt;
    private String incrementByStmt;
    private String deleteStmt;
    private String deleteAllStmt;
    private String fetchAllStmt;
    private String fetchStmt;
    private String sequenceNameColumnName;
    private String nextValColumnName;
    
    public SequenceTable(final DatastoreIdentifier identifier, final RDBMSStoreManager storeMgr, final String seqNameColName, final String nextValColName) {
        super(identifier, storeMgr);
        this.sequenceNameMapping = null;
        this.nextValMapping = null;
        this.insertStmt = null;
        this.incrementByStmt = null;
        this.deleteStmt = null;
        this.deleteAllStmt = null;
        this.fetchAllStmt = null;
        this.fetchStmt = null;
        this.sequenceNameColumnName = seqNameColName;
        this.nextValColumnName = nextValColName;
    }
    
    @Override
    public void initialize(final ClassLoaderResolver clr) {
        this.assertIsUninitialized();
        final IdentifierFactory idFactory = this.storeMgr.getIdentifierFactory();
        this.sequenceNameMapping = this.storeMgr.getMappingManager().getMapping(String.class);
        final Column colSequenceName = this.addColumn(String.class.getName(), idFactory.newColumnIdentifier(this.sequenceNameColumnName), this.sequenceNameMapping, null);
        colSequenceName.setAsPrimaryKey();
        colSequenceName.getColumnMetaData().setLength(Integer.valueOf("255"));
        colSequenceName.getColumnMetaData().setJdbcType("VARCHAR");
        this.getStoreManager().getMappingManager().createDatastoreMapping(this.sequenceNameMapping, colSequenceName, String.class.getName());
        this.nextValMapping = this.storeMgr.getMappingManager().getMapping(Long.class);
        final Column colNextVal = this.addColumn(Long.class.getName(), idFactory.newColumnIdentifier(this.nextValColumnName), this.nextValMapping, null);
        this.getStoreManager().getMappingManager().createDatastoreMapping(this.nextValMapping, colNextVal, Long.class.getName());
        this.insertStmt = "INSERT INTO " + this.identifier.getFullyQualifiedName(false) + " (" + colSequenceName.getIdentifier() + "," + colNextVal.getIdentifier() + ") VALUES (?,?)";
        this.incrementByStmt = "UPDATE " + this.identifier.getFullyQualifiedName(false) + " SET " + colNextVal.getIdentifier() + "=(" + colNextVal.getIdentifier() + "+?) WHERE " + colSequenceName.getIdentifier() + "=?";
        this.deleteStmt = "DELETE FROM " + this.identifier.getFullyQualifiedName(false) + " WHERE " + colSequenceName.getIdentifier() + "=?";
        this.deleteAllStmt = "DELETE FROM " + this.identifier.getFullyQualifiedName(false);
        this.fetchStmt = "SELECT " + colNextVal.getIdentifier() + " FROM " + this.identifier.getFullyQualifiedName(false) + " WHERE " + colSequenceName.getIdentifier() + "=?";
        if (this.dba.supportsOption("LockWithSelectForUpdate")) {
            this.fetchStmt += " FOR UPDATE";
        }
        this.fetchAllStmt = "SELECT " + colNextVal.getIdentifier() + "," + colSequenceName.getIdentifier() + " FROM " + this.identifier.getFullyQualifiedName(false) + " ORDER BY " + colSequenceName.getIdentifier();
        this.storeMgr.registerTableInitialized(this);
        this.state = 2;
    }
    
    @Override
    public JavaTypeMapping getIdMapping() {
        throw new NucleusException("Attempt to get ID mapping of Sequence table!").setFatal();
    }
    
    public HashSet getFetchAllSequences(final ManagedConnection conn) throws SQLException {
        final HashSet sequenceNames = new HashSet();
        PreparedStatement ps = null;
        final SQLController sqlControl = this.storeMgr.getSQLController();
        try {
            ps = sqlControl.getStatementForQuery(conn, this.fetchAllStmt);
            final ResultSet rs = sqlControl.executeStatementQuery(null, conn, this.fetchAllStmt, ps);
            try {
                while (rs.next()) {
                    sequenceNames.add(rs.getString(2));
                }
            }
            finally {
                rs.close();
            }
        }
        finally {
            if (ps != null) {
                sqlControl.closeStatement(conn, ps);
            }
        }
        return sequenceNames;
    }
    
    public Long getNextVal(final String sequenceName, final ManagedConnection conn, final int incrementBy, final DatastoreIdentifier tableIdentifier, final String columnName, final int initialValue) throws SQLException {
        PreparedStatement ps = null;
        Long nextVal = null;
        final SQLController sqlControl = this.storeMgr.getSQLController();
        try {
            ps = sqlControl.getStatementForQuery(conn, this.fetchStmt);
            this.sequenceNameMapping.setString(null, ps, new int[] { 1 }, sequenceName);
            final ResultSet rs = sqlControl.executeStatementQuery(null, conn, this.fetchStmt, ps);
            try {
                if (!rs.next()) {
                    boolean addedSequence = false;
                    if (initialValue >= 0) {
                        this.addSequence(sequenceName, (long)(incrementBy + initialValue), conn);
                        nextVal = (long)initialValue;
                    }
                    else {
                        if (columnName != null && tableIdentifier != null) {
                            PreparedStatement ps2 = null;
                            ResultSet rs2 = null;
                            try {
                                final String fetchInitStmt = "SELECT MAX(" + columnName + ") FROM " + tableIdentifier.getFullyQualifiedName(false);
                                ps2 = sqlControl.getStatementForQuery(conn, fetchInitStmt);
                                rs2 = sqlControl.executeStatementQuery(null, conn, fetchInitStmt, ps2);
                                if (rs2.next()) {
                                    final long val = rs2.getLong(1);
                                    this.addSequence(sequenceName, incrementBy + 1 + val, conn);
                                    nextVal = 1L + val;
                                    addedSequence = true;
                                }
                            }
                            catch (Exception e2) {
                                if (rs2 != null) {}
                            }
                            finally {
                                if (rs2 != null) {
                                    rs2.close();
                                }
                                if (ps2 != null) {
                                    sqlControl.closeStatement(conn, ps2);
                                }
                            }
                        }
                        if (!addedSequence) {
                            this.addSequence(sequenceName, (long)(incrementBy + 0), conn);
                            nextVal = (long)initialValue;
                        }
                    }
                }
                else {
                    nextVal = rs.getLong(1);
                    this.incrementSequence(sequenceName, incrementBy, conn);
                }
            }
            finally {
                rs.close();
            }
        }
        catch (SQLException e) {
            throw new ValueGenerationException(SequenceTable.LOCALISER.msg("061001", e.getMessage()), e);
        }
        finally {
            if (ps != null) {
                sqlControl.closeStatement(conn, ps);
            }
        }
        return nextVal;
    }
    
    private void incrementSequence(final String sequenceName, final long incrementBy, final ManagedConnection conn) throws SQLException {
        PreparedStatement ps = null;
        final SQLController sqlControl = this.storeMgr.getSQLController();
        try {
            ps = sqlControl.getStatementForUpdate(conn, this.incrementByStmt, false);
            this.nextValMapping.setLong(null, ps, new int[] { 1 }, incrementBy);
            this.sequenceNameMapping.setString(null, ps, new int[] { 2 }, sequenceName);
            sqlControl.executeStatementUpdate(null, conn, this.incrementByStmt, ps, true);
        }
        finally {
            if (ps != null) {
                sqlControl.closeStatement(conn, ps);
            }
        }
    }
    
    private void addSequence(final String sequenceName, final Long nextVal, final ManagedConnection conn) throws SQLException {
        PreparedStatement ps = null;
        final SQLController sqlControl = this.storeMgr.getSQLController();
        try {
            ps = sqlControl.getStatementForUpdate(conn, this.insertStmt, false);
            this.sequenceNameMapping.setString(null, ps, new int[] { 1 }, sequenceName);
            this.nextValMapping.setLong(null, ps, new int[] { 2 }, nextVal);
            sqlControl.executeStatementUpdate(null, conn, this.insertStmt, ps, true);
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            if (ps != null) {
                sqlControl.closeStatement(conn, ps);
            }
        }
    }
    
    public void deleteSequence(final String sequenceName, final ManagedConnection conn) throws SQLException {
        PreparedStatement ps = null;
        final SQLController sqlControl = this.storeMgr.getSQLController();
        try {
            ps = sqlControl.getStatementForUpdate(conn, this.deleteStmt, false);
            ps.setString(1, sequenceName);
            sqlControl.executeStatementUpdate(null, conn, this.deleteStmt, ps, true);
        }
        finally {
            if (ps != null) {
                sqlControl.closeStatement(conn, ps);
            }
        }
    }
    
    public void deleteAllSequences(final ManagedConnection conn) throws SQLException {
        PreparedStatement ps = null;
        final SQLController sqlControl = this.storeMgr.getSQLController();
        try {
            ps = sqlControl.getStatementForUpdate(conn, this.deleteAllStmt, false);
            sqlControl.executeStatementUpdate(null, conn, this.deleteAllStmt, ps, true);
        }
        finally {
            if (ps != null) {
                sqlControl.closeStatement(conn, ps);
            }
        }
    }
    
    @Override
    public JavaTypeMapping getMemberMapping(final AbstractMemberMetaData mmd) {
        return null;
    }
}
