// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.session;

import org.apache.hive.service.auth.HiveAuthFactory;
import org.apache.hive.service.cli.RowSet;
import org.apache.hive.service.cli.FetchType;
import org.apache.hive.service.cli.FetchOrientation;
import org.apache.hive.service.cli.TableSchema;
import java.util.List;
import org.apache.hive.service.cli.OperationHandle;
import org.apache.hive.service.cli.GetInfoValue;
import org.apache.hive.service.cli.GetInfoType;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import java.util.Map;

public interface HiveSession extends HiveSessionBase
{
    void open(final Map<String, String> p0) throws Exception;
    
    IMetaStoreClient getMetaStoreClient() throws HiveSQLException;
    
    GetInfoValue getInfo(final GetInfoType p0) throws HiveSQLException;
    
    OperationHandle executeStatement(final String p0, final Map<String, String> p1) throws HiveSQLException;
    
    OperationHandle executeStatementAsync(final String p0, final Map<String, String> p1) throws HiveSQLException;
    
    OperationHandle getTypeInfo() throws HiveSQLException;
    
    OperationHandle getCatalogs() throws HiveSQLException;
    
    OperationHandle getSchemas(final String p0, final String p1) throws HiveSQLException;
    
    OperationHandle getTables(final String p0, final String p1, final String p2, final List<String> p3) throws HiveSQLException;
    
    OperationHandle getTableTypes() throws HiveSQLException;
    
    OperationHandle getColumns(final String p0, final String p1, final String p2, final String p3) throws HiveSQLException;
    
    OperationHandle getFunctions(final String p0, final String p1, final String p2) throws HiveSQLException;
    
    void close() throws HiveSQLException;
    
    void cancelOperation(final OperationHandle p0) throws HiveSQLException;
    
    void closeOperation(final OperationHandle p0) throws HiveSQLException;
    
    TableSchema getResultSetMetadata(final OperationHandle p0) throws HiveSQLException;
    
    RowSet fetchResults(final OperationHandle p0, final FetchOrientation p1, final long p2, final FetchType p3) throws HiveSQLException;
    
    String getDelegationToken(final HiveAuthFactory p0, final String p1, final String p2) throws HiveSQLException;
    
    void cancelDelegationToken(final HiveAuthFactory p0, final String p1) throws HiveSQLException;
    
    void renewDelegationToken(final HiveAuthFactory p0, final String p1) throws HiveSQLException;
    
    void closeExpiredOperations();
    
    long getNoOperationTime();
}
