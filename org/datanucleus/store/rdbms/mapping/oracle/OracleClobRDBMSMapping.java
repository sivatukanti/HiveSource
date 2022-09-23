// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.oracle;

import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.SQLController;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.expression.SQLExpressionFactory;
import org.datanucleus.ExecutionContext;
import oracle.sql.CLOB;
import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import org.datanucleus.store.rdbms.sql.SQLStatementHelper;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.store.rdbms.mapping.datastore.DatastoreMapping;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.state.ObjectProvider;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.io.IOException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.util.Arrays;
import java.sql.ResultSet;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.exceptions.ColumnDefinitionException;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.mapping.datastore.ClobRDBMSMapping;

public class OracleClobRDBMSMapping extends ClobRDBMSMapping
{
    public OracleClobRDBMSMapping(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column col) {
        super(mapping, storeMgr, col);
        this.column = col;
        this.initialize();
    }
    
    private void initialize() {
        this.initTypeInfo();
        if (this.column != null && !this.column.isUnlimitedLength()) {
            throw new ColumnDefinitionException("Invalid length specified for CLOB column " + this.column + ", must be 'unlimited'");
        }
    }
    
    @Override
    public SQLTypeInfo getTypeInfo() {
        return this.storeMgr.getSQLTypeInfoForJDBCType(2005);
    }
    
    @Override
    public String getInsertionInputParameter() {
        return "EMPTY_CLOB()";
    }
    
    @Override
    public boolean includeInFetchStatement() {
        return true;
    }
    
    @Override
    public String getUpdateInputParameter() {
        return "EMPTY_CLOB()";
    }
    
    @Override
    public boolean insertValuesOnInsert() {
        return false;
    }
    
    @Override
    public String getString(final ResultSet rs, final int param) {
        String value = null;
        try {
            char[] cbuf = null;
            final Clob clob = rs.getClob(param);
            if (clob != null) {
                final StringBuffer sbuf = new StringBuffer();
                final Reader reader = clob.getCharacterStream();
                try {
                    final int BUFF_SIZE = 4096;
                    cbuf = new char[4096];
                    for (int charsRead = reader.read(cbuf); -1 != charsRead; charsRead = reader.read(cbuf)) {
                        sbuf.append(cbuf, 0, charsRead);
                        Arrays.fill(cbuf, '\0');
                    }
                }
                catch (IOException e) {
                    throw new NucleusDataStoreException("Error reading Oracle CLOB object: param = " + param, e);
                }
                finally {
                    try {
                        reader.close();
                    }
                    catch (IOException e2) {
                        throw new NucleusDataStoreException("Error reading Oracle CLOB object: param = " + param, e2);
                    }
                }
                value = sbuf.toString();
                if (value.length() == 0) {
                    value = null;
                }
                else if (value.equals(this.getDatastoreAdapter().getSurrogateForEmptyStrings())) {
                    value = "";
                }
            }
        }
        catch (SQLException e3) {
            throw new NucleusDataStoreException(OracleClobRDBMSMapping.LOCALISER.msg("055001", "String", "" + param), e3);
        }
        return value;
    }
    
    @Override
    public Object getObject(final ResultSet rs, final int param) {
        return this.getString(rs, param);
    }
    
    public static void updateClobColumn(ObjectProvider sm, final Table table, final DatastoreMapping mapping, final String value) {
        final ExecutionContext ec = sm.getExecutionContext();
        final RDBMSStoreManager storeMgr = table.getStoreManager();
        final DatastoreClass classTable = (DatastoreClass)table;
        final SQLExpressionFactory exprFactory = storeMgr.getSQLExpressionFactory();
        final SQLStatement sqlStmt = new SQLStatement(storeMgr, table, null, null);
        sqlStmt.setClassLoaderResolver(ec.getClassLoaderResolver());
        sqlStmt.addExtension("lock-for-update", true);
        final SQLTable blobSqlTbl = SQLStatementHelper.getSQLTableForMappingOfTable(sqlStmt, sqlStmt.getPrimaryTable(), mapping.getJavaTypeMapping());
        sqlStmt.select(blobSqlTbl, mapping.getColumn(), null);
        final StatementClassMapping mappingDefinition = new StatementClassMapping();
        final AbstractClassMetaData cmd = sm.getClassMetaData();
        int inputParamNum = 1;
        if (cmd.getIdentityType() == IdentityType.DATASTORE) {
            final JavaTypeMapping datastoreIdMapping = classTable.getDatastoreObjectIdMapping();
            final SQLExpression expr = exprFactory.newExpression(sqlStmt, sqlStmt.getPrimaryTable(), datastoreIdMapping);
            final SQLExpression val = exprFactory.newLiteralParameter(sqlStmt, datastoreIdMapping, null, "ID");
            sqlStmt.whereAnd(expr.eq(val), true);
            StatementMappingIndex datastoreIdx = mappingDefinition.getMappingForMemberPosition(-1);
            if (datastoreIdx == null) {
                datastoreIdx = new StatementMappingIndex(datastoreIdMapping);
                mappingDefinition.addMappingForMember(-1, datastoreIdx);
            }
            datastoreIdx.addParameterOccurrence(new int[] { inputParamNum });
        }
        else if (cmd.getIdentityType() == IdentityType.APPLICATION) {
            final int[] pkNums = cmd.getPKMemberPositions();
            for (int i = 0; i < pkNums.length; ++i) {
                final AbstractMemberMetaData mmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(pkNums[i]);
                final JavaTypeMapping pkMapping = classTable.getMemberMapping(mmd);
                final SQLExpression expr2 = exprFactory.newExpression(sqlStmt, sqlStmt.getPrimaryTable(), pkMapping);
                final SQLExpression val2 = exprFactory.newLiteralParameter(sqlStmt, pkMapping, null, "PK" + i);
                sqlStmt.whereAnd(expr2.eq(val2), true);
                StatementMappingIndex pkIdx = mappingDefinition.getMappingForMemberPosition(pkNums[i]);
                if (pkIdx == null) {
                    pkIdx = new StatementMappingIndex(pkMapping);
                    mappingDefinition.addMappingForMember(pkNums[i], pkIdx);
                }
                final int[] inputParams = new int[pkMapping.getNumberOfDatastoreMappings()];
                for (int j = 0; j < pkMapping.getNumberOfDatastoreMappings(); ++j) {
                    inputParams[j] = inputParamNum++;
                }
                pkIdx.addParameterOccurrence(inputParams);
            }
        }
        final String textStmt = sqlStmt.getSelectStatement().toSQL();
        if (sm.isEmbedded()) {
            final ObjectProvider[] embeddedOwners = sm.getEmbeddedOwners();
            if (embeddedOwners != null) {
                sm = embeddedOwners[0];
            }
        }
        try {
            final ManagedConnection mconn = storeMgr.getConnection(ec);
            final SQLController sqlControl = storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForQuery(mconn, textStmt);
                try {
                    if (cmd.getIdentityType() == IdentityType.DATASTORE) {
                        final StatementMappingIndex datastoreIdx2 = mappingDefinition.getMappingForMemberPosition(-1);
                        for (int k = 0; k < datastoreIdx2.getNumberOfParameterOccurrences(); ++k) {
                            classTable.getDatastoreObjectIdMapping().setObject(ec, ps, datastoreIdx2.getParameterPositionsForOccurrence(k), sm.getInternalObjectId());
                        }
                    }
                    else if (cmd.getIdentityType() == IdentityType.APPLICATION) {
                        sm.provideFields(cmd.getPKMemberPositions(), storeMgr.getFieldManagerForStatementGeneration(sm, ps, mappingDefinition));
                    }
                    final ResultSet rs = sqlControl.executeStatementQuery(ec, mconn, textStmt, ps);
                    try {
                        if (!rs.next()) {
                            throw new NucleusObjectNotFoundException("No such database row", sm.getInternalObjectId());
                        }
                        final DatastoreAdapter dba = storeMgr.getDatastoreAdapter();
                        final int jdbcMajorVersion = dba.getDriverMajorVersion();
                        if (dba.getDatastoreDriverName().equalsIgnoreCase("Oracle JDBC driver") && jdbcMajorVersion < 10) {
                            final CLOB clob = (CLOB)rs.getClob(1);
                            if (clob != null) {
                                clob.putString(1L, value);
                            }
                        }
                        else {
                            final Clob clob2 = rs.getClob(1);
                            if (clob2 != null) {
                                clob2.setString(1L, value);
                            }
                        }
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
            throw new NucleusDataStoreException("Update of CLOB value failed: " + textStmt, e);
        }
    }
}
