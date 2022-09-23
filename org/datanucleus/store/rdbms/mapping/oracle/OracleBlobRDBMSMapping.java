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
import oracle.sql.BLOB;
import oracle.jdbc.driver.OracleResultSet;
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
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import java.sql.Blob;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.io.IOException;
import java.io.StreamCorruptedException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.util.NucleusLogger;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.util.BitSet;
import java.math.BigInteger;
import java.math.BigDecimal;
import org.datanucleus.util.TypeConversionHelper;
import org.datanucleus.ClassNameConstants;
import org.datanucleus.store.rdbms.mapping.datastore.BlobImpl;
import java.sql.ResultSet;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.mapping.datastore.AbstractDatastoreMapping;

public class OracleBlobRDBMSMapping extends AbstractDatastoreMapping
{
    public OracleBlobRDBMSMapping(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column col) {
        super(storeMgr, mapping);
        this.column = col;
        this.initialize();
    }
    
    protected OracleBlobRDBMSMapping(final RDBMSStoreManager storeMgr, final JavaTypeMapping mapping) {
        super(storeMgr, mapping);
    }
    
    private void initialize() {
        this.initTypeInfo();
    }
    
    @Override
    public String getInsertionInputParameter() {
        return "EMPTY_BLOB()";
    }
    
    @Override
    public boolean insertValuesOnInsert() {
        return false;
    }
    
    @Override
    public Object getObject(final ResultSet rs, final int param) {
        Object obj = null;
        try {
            final Blob blob = rs.getBlob(param);
            if (!rs.wasNull()) {
                final byte[] bytes = blob.getBytes(1L, (int)blob.length());
                if (bytes.length < 1) {
                    return null;
                }
                try {
                    if (this.getJavaTypeMapping().isSerialised()) {
                        final BlobImpl b = new BlobImpl(bytes);
                        obj = b.getObject();
                    }
                    else if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.BOOLEAN_ARRAY)) {
                        obj = TypeConversionHelper.getBooleanArrayFromByteArray(bytes);
                    }
                    else if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.BYTE_ARRAY)) {
                        obj = bytes;
                    }
                    else if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.CHAR_ARRAY)) {
                        obj = TypeConversionHelper.getCharArrayFromByteArray(bytes);
                    }
                    else if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.JAVA_LANG_STRING)) {
                        obj = new String(bytes);
                    }
                    else if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.DOUBLE_ARRAY)) {
                        obj = TypeConversionHelper.getDoubleArrayFromByteArray(bytes);
                    }
                    else if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.FLOAT_ARRAY)) {
                        obj = TypeConversionHelper.getFloatArrayFromByteArray(bytes);
                    }
                    else if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.INT_ARRAY)) {
                        obj = TypeConversionHelper.getIntArrayFromByteArray(bytes);
                    }
                    else if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.LONG_ARRAY)) {
                        obj = TypeConversionHelper.getLongArrayFromByteArray(bytes);
                    }
                    else if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.SHORT_ARRAY)) {
                        obj = TypeConversionHelper.getShortArrayFromByteArray(bytes);
                    }
                    else if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.JAVA_LANG_BOOLEAN_ARRAY)) {
                        obj = TypeConversionHelper.getBooleanObjectArrayFromByteArray(bytes);
                    }
                    else if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.JAVA_LANG_BYTE_ARRAY)) {
                        obj = TypeConversionHelper.getByteObjectArrayFromByteArray(bytes);
                    }
                    else if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.JAVA_LANG_CHARACTER_ARRAY)) {
                        obj = TypeConversionHelper.getCharObjectArrayFromByteArray(bytes);
                    }
                    else if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.JAVA_LANG_DOUBLE_ARRAY)) {
                        obj = TypeConversionHelper.getDoubleObjectArrayFromByteArray(bytes);
                    }
                    else if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.JAVA_LANG_FLOAT_ARRAY)) {
                        obj = TypeConversionHelper.getFloatObjectArrayFromByteArray(bytes);
                    }
                    else if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.JAVA_LANG_INTEGER_ARRAY)) {
                        obj = TypeConversionHelper.getIntObjectArrayFromByteArray(bytes);
                    }
                    else if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.JAVA_LANG_LONG_ARRAY)) {
                        obj = TypeConversionHelper.getLongObjectArrayFromByteArray(bytes);
                    }
                    else if (this.getJavaTypeMapping().getType().equals(ClassNameConstants.JAVA_LANG_SHORT_ARRAY)) {
                        obj = TypeConversionHelper.getShortObjectArrayFromByteArray(bytes);
                    }
                    else {
                        if (this.getJavaTypeMapping().getType().equals(BigDecimal[].class.getName())) {
                            return TypeConversionHelper.getBigDecimalArrayFromByteArray(bytes);
                        }
                        if (this.getJavaTypeMapping().getType().equals(BigInteger[].class.getName())) {
                            return TypeConversionHelper.getBigIntegerArrayFromByteArray(bytes);
                        }
                        if (this.getJavaTypeMapping().getType().equals(BitSet.class.getName())) {
                            return TypeConversionHelper.getBitSetFromBooleanArray((boolean[])TypeConversionHelper.getBooleanArrayFromByteArray(bytes));
                        }
                        obj = new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
                    }
                }
                catch (StreamCorruptedException e) {
                    final String msg = "StreamCorruptedException: object is corrupted";
                    NucleusLogger.DATASTORE.error(msg);
                    throw new NucleusUserException(msg, e).setFatal();
                }
                catch (IOException e2) {
                    final String msg = "IOException: error when reading object";
                    NucleusLogger.DATASTORE.error(msg);
                    throw new NucleusUserException(msg, e2).setFatal();
                }
                catch (ClassNotFoundException e3) {
                    final String msg = "ClassNotFoundException: error when creating object";
                    NucleusLogger.DATASTORE.error(msg);
                    throw new NucleusUserException(msg, e3).setFatal();
                }
            }
        }
        catch (SQLException sqle) {
            throw new NucleusDataStoreException(OracleBlobRDBMSMapping.LOCALISER.msg("055002", "Object", "" + param, this.column, sqle.getMessage()), sqle);
        }
        return obj;
    }
    
    @Override
    public String getString(final ResultSet resultSet, final int exprIndex) {
        return (String)this.getObject(resultSet, exprIndex);
    }
    
    @Override
    public SQLTypeInfo getTypeInfo() {
        return this.storeMgr.getSQLTypeInfoForJDBCType(2004);
    }
    
    @Override
    public String getUpdateInputParameter() {
        return "EMPTY_BLOB()";
    }
    
    public boolean includeInSQLFetchStatement() {
        return true;
    }
    
    public static void updateBlobColumn(ObjectProvider sm, final Table table, final DatastoreMapping mapping, final byte[] bytes) {
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
                            BLOB blob = null;
                            if (jdbcMajorVersion <= 8) {
                                final OracleResultSet ors = (OracleResultSet)rs;
                                blob = ors.getBLOB(1);
                            }
                            else {
                                blob = (BLOB)rs.getBlob(1);
                            }
                            if (blob != null) {
                                blob.putBytes(1L, bytes);
                            }
                        }
                        else {
                            final Blob blob2 = rs.getBlob(1);
                            if (blob2 != null) {
                                blob2.setBytes(1L, bytes);
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
            throw new NucleusDataStoreException("Update of BLOB value failed: " + textStmt, e);
        }
    }
}
