// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.autostart;

import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.exceptions.MissingTableException;
import org.datanucleus.store.rdbms.mapping.MappingHelper;
import java.sql.SQLException;
import org.datanucleus.store.StoreData;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.SQLController;
import org.datanucleus.store.rdbms.RDBMSStoreData;
import org.datanucleus.ExecutionContext;
import java.sql.Connection;
import java.util.HashSet;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.mapping.MappingManager;
import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.table.TableImpl;

public class SchemaTable extends TableImpl
{
    private JavaTypeMapping classMapping;
    private JavaTypeMapping tableMapping;
    private JavaTypeMapping typeMapping;
    private JavaTypeMapping ownerMapping;
    private JavaTypeMapping versionMapping;
    private JavaTypeMapping interfaceNameMapping;
    private String insertStmt;
    private String deleteStmt;
    private String deleteAllStmt;
    private String fetchAllStmt;
    private String fetchStmt;
    
    public SchemaTable(final RDBMSStoreManager storeMgr, final String tableName) {
        super(storeMgr.getIdentifierFactory().newTableIdentifier((tableName != null) ? tableName : "NUCLEUS_TABLES"), storeMgr);
        this.classMapping = null;
        this.tableMapping = null;
        this.typeMapping = null;
        this.ownerMapping = null;
        this.versionMapping = null;
        this.interfaceNameMapping = null;
        this.insertStmt = null;
        this.deleteStmt = null;
        this.deleteAllStmt = null;
        this.fetchAllStmt = null;
        this.fetchStmt = null;
    }
    
    @Override
    public void initialize(final ClassLoaderResolver clr) {
        this.assertIsUninitialized();
        final IdentifierFactory idFactory = this.storeMgr.getIdentifierFactory();
        final MappingManager mapMgr = this.getStoreManager().getMappingManager();
        this.classMapping = mapMgr.getMapping(String.class);
        final Column class_column = this.addColumn(String.class.getName(), idFactory.newColumnIdentifier("CLASS_NAME"), this.classMapping, null);
        mapMgr.createDatastoreMapping(this.classMapping, class_column, String.class.getName());
        class_column.getColumnMetaData().setLength(128);
        class_column.getColumnMetaData().setJdbcType("VARCHAR");
        class_column.setAsPrimaryKey();
        this.tableMapping = mapMgr.getMapping(String.class);
        final Column table_column = this.addColumn(String.class.getName(), idFactory.newColumnIdentifier("TABLE_NAME"), this.tableMapping, null);
        mapMgr.createDatastoreMapping(this.tableMapping, table_column, String.class.getName());
        table_column.getColumnMetaData().setLength(128);
        table_column.getColumnMetaData().setJdbcType("VARCHAR");
        this.typeMapping = mapMgr.getMapping(String.class);
        final Column type_column = this.addColumn(String.class.getName(), idFactory.newColumnIdentifier("TYPE"), this.typeMapping, null);
        mapMgr.createDatastoreMapping(this.typeMapping, type_column, String.class.getName());
        type_column.getColumnMetaData().setLength(4);
        type_column.getColumnMetaData().setJdbcType("VARCHAR");
        this.ownerMapping = mapMgr.getMapping(String.class);
        final Column owner_column = this.addColumn(String.class.getName(), idFactory.newColumnIdentifier("OWNER"), this.ownerMapping, null);
        mapMgr.createDatastoreMapping(this.ownerMapping, owner_column, String.class.getName());
        owner_column.getColumnMetaData().setLength(2);
        owner_column.getColumnMetaData().setJdbcType("VARCHAR");
        this.versionMapping = mapMgr.getMapping(String.class);
        final Column version_column = this.addColumn(String.class.getName(), idFactory.newColumnIdentifier("VERSION"), this.versionMapping, null);
        mapMgr.createDatastoreMapping(this.versionMapping, version_column, String.class.getName());
        version_column.getColumnMetaData().setLength(20);
        version_column.getColumnMetaData().setJdbcType("VARCHAR");
        this.interfaceNameMapping = mapMgr.getMapping(String.class);
        final Column interfaceName_column = this.addColumn(String.class.getName(), idFactory.newColumnIdentifier("INTERFACE_NAME"), this.interfaceNameMapping, null);
        mapMgr.createDatastoreMapping(this.interfaceNameMapping, interfaceName_column, String.class.getName());
        interfaceName_column.getColumnMetaData().setLength(255);
        interfaceName_column.getColumnMetaData().setJdbcType("VARCHAR");
        interfaceName_column.setNullable();
        this.insertStmt = "INSERT INTO " + this.identifier.getFullyQualifiedName(false) + " (" + class_column.getIdentifier() + "," + table_column.getIdentifier() + "," + type_column.getIdentifier() + "," + owner_column.getIdentifier() + "," + version_column.getIdentifier() + "," + interfaceName_column.getIdentifier() + ") VALUES (?,?,?,?,?,?)";
        this.deleteStmt = "DELETE FROM " + this.identifier.getFullyQualifiedName(false) + " WHERE " + idFactory.getIdentifierInAdapterCase("CLASS_NAME") + "=?";
        this.deleteAllStmt = "DELETE FROM " + this.identifier.getFullyQualifiedName(false);
        this.fetchAllStmt = "SELECT " + class_column.getIdentifier() + "," + table_column.getIdentifier() + "," + type_column.getIdentifier() + "," + owner_column.getIdentifier() + "," + version_column.getIdentifier() + "," + interfaceName_column.getIdentifier() + " FROM " + this.identifier.getFullyQualifiedName(false) + " ORDER BY " + table_column.getIdentifier();
        this.fetchStmt = "SELECT 1 FROM " + this.identifier.getFullyQualifiedName(false) + " WHERE " + idFactory.getIdentifierInAdapterCase("CLASS_NAME") + " = ? ";
        this.state = 2;
    }
    
    @Override
    public JavaTypeMapping getIdMapping() {
        throw new NucleusException("Attempt to get ID mapping of SchemaTable!").setFatal();
    }
    
    public HashSet getAllClasses(final ManagedConnection conn) throws SQLException {
        final HashSet schema_data = new HashSet();
        if (this.storeMgr.getDdlWriter() != null && !this.tableExists((Connection)conn.getConnection())) {
            return schema_data;
        }
        final SQLController sqlControl = this.storeMgr.getSQLController();
        final PreparedStatement ps = sqlControl.getStatementForQuery(conn, this.fetchAllStmt);
        try {
            final ResultSet rs = sqlControl.executeStatementQuery(null, conn, this.fetchAllStmt, ps);
            try {
                while (rs.next()) {
                    final StoreData data = new RDBMSStoreData(rs.getString(1), rs.getString(2), rs.getString(4).equals("1"), rs.getString(3).equals("FCO") ? 1 : 2, rs.getString(6));
                    schema_data.add(data);
                }
            }
            finally {
                rs.close();
            }
        }
        finally {
            sqlControl.closeStatement(conn, ps);
        }
        return schema_data;
    }
    
    public void addClass(final RDBMSStoreData data, final ManagedConnection conn) throws SQLException {
        if (this.storeMgr.getDdlWriter() != null) {
            return;
        }
        if (this.hasClass(data, conn)) {
            return;
        }
        final SQLController sqlControl = this.storeMgr.getSQLController();
        final PreparedStatement ps = sqlControl.getStatementForUpdate(conn, this.insertStmt, false);
        try {
            int jdbc_id = 1;
            this.classMapping.setString(null, ps, MappingHelper.getMappingIndices(jdbc_id, this.classMapping), data.getName());
            jdbc_id += this.classMapping.getNumberOfDatastoreMappings();
            this.tableMapping.setString(null, ps, MappingHelper.getMappingIndices(jdbc_id, this.tableMapping), data.hasTable() ? data.getTableName() : "");
            jdbc_id += this.tableMapping.getNumberOfDatastoreMappings();
            this.typeMapping.setString(null, ps, MappingHelper.getMappingIndices(jdbc_id, this.typeMapping), data.isFCO() ? "FCO" : "SCO");
            jdbc_id += this.typeMapping.getNumberOfDatastoreMappings();
            this.ownerMapping.setString(null, ps, MappingHelper.getMappingIndices(jdbc_id, this.ownerMapping), data.isTableOwner() ? "1" : "0");
            jdbc_id += this.ownerMapping.getNumberOfDatastoreMappings();
            this.versionMapping.setString(null, ps, MappingHelper.getMappingIndices(jdbc_id, this.versionMapping), "DataNucleus");
            jdbc_id += this.versionMapping.getNumberOfDatastoreMappings();
            this.interfaceNameMapping.setString(null, ps, MappingHelper.getMappingIndices(jdbc_id, this.interfaceNameMapping), data.getInterfaceName());
            jdbc_id += this.interfaceNameMapping.getNumberOfDatastoreMappings();
            sqlControl.executeStatementUpdate(null, conn, this.insertStmt, ps, true);
        }
        finally {
            sqlControl.closeStatement(conn, ps);
        }
    }
    
    private boolean hasClass(final StoreData data, final ManagedConnection conn) throws SQLException {
        if (!this.tableExists((Connection)conn.getConnection())) {
            return false;
        }
        final SQLController sqlControl = this.storeMgr.getSQLController();
        final PreparedStatement ps = sqlControl.getStatementForQuery(conn, this.fetchStmt);
        try {
            final int jdbc_id = 1;
            this.tableMapping.setString(null, ps, MappingHelper.getMappingIndices(jdbc_id, this.tableMapping), data.getName());
            final ResultSet rs = sqlControl.executeStatementQuery(null, conn, this.fetchStmt, ps);
            try {
                if (rs.next()) {
                    return true;
                }
            }
            finally {
                rs.close();
            }
        }
        finally {
            sqlControl.closeStatement(conn, ps);
        }
        return false;
    }
    
    public void deleteClass(final String class_name, final ManagedConnection conn) throws SQLException {
        final SQLController sqlControl = this.storeMgr.getSQLController();
        final PreparedStatement ps = sqlControl.getStatementForUpdate(conn, this.deleteStmt, false);
        try {
            ps.setString(1, class_name);
            sqlControl.executeStatementUpdate(null, conn, this.deleteStmt, ps, true);
        }
        finally {
            sqlControl.closeStatement(conn, ps);
        }
    }
    
    public void deleteAllClasses(final ManagedConnection conn) throws SQLException {
        final SQLController sqlControl = this.storeMgr.getSQLController();
        final PreparedStatement ps = sqlControl.getStatementForUpdate(conn, this.deleteAllStmt, false);
        try {
            sqlControl.executeStatementUpdate(null, conn, this.deleteAllStmt, ps, true);
        }
        finally {
            sqlControl.closeStatement(conn, ps);
        }
    }
    
    private boolean tableExists(final Connection conn) throws SQLException {
        try {
            this.exists(conn, false);
            return true;
        }
        catch (MissingTableException mte) {
            return false;
        }
    }
    
    @Override
    public JavaTypeMapping getMemberMapping(final AbstractMemberMetaData mmd) {
        return null;
    }
}
