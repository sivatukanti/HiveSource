// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

import org.apache.hive.service.auth.HiveAuthFactory;
import java.util.List;
import java.util.Map;

public interface ICLIService
{
    SessionHandle openSession(final String p0, final String p1, final Map<String, String> p2) throws HiveSQLException;
    
    SessionHandle openSessionWithImpersonation(final String p0, final String p1, final Map<String, String> p2, final String p3) throws HiveSQLException;
    
    void closeSession(final SessionHandle p0) throws HiveSQLException;
    
    GetInfoValue getInfo(final SessionHandle p0, final GetInfoType p1) throws HiveSQLException;
    
    OperationHandle executeStatement(final SessionHandle p0, final String p1, final Map<String, String> p2) throws HiveSQLException;
    
    OperationHandle executeStatementAsync(final SessionHandle p0, final String p1, final Map<String, String> p2) throws HiveSQLException;
    
    OperationHandle getTypeInfo(final SessionHandle p0) throws HiveSQLException;
    
    OperationHandle getCatalogs(final SessionHandle p0) throws HiveSQLException;
    
    OperationHandle getSchemas(final SessionHandle p0, final String p1, final String p2) throws HiveSQLException;
    
    OperationHandle getTables(final SessionHandle p0, final String p1, final String p2, final String p3, final List<String> p4) throws HiveSQLException;
    
    OperationHandle getTableTypes(final SessionHandle p0) throws HiveSQLException;
    
    OperationHandle getColumns(final SessionHandle p0, final String p1, final String p2, final String p3, final String p4) throws HiveSQLException;
    
    OperationHandle getFunctions(final SessionHandle p0, final String p1, final String p2, final String p3) throws HiveSQLException;
    
    OperationStatus getOperationStatus(final OperationHandle p0) throws HiveSQLException;
    
    void cancelOperation(final OperationHandle p0) throws HiveSQLException;
    
    void closeOperation(final OperationHandle p0) throws HiveSQLException;
    
    TableSchema getResultSetMetadata(final OperationHandle p0) throws HiveSQLException;
    
    RowSet fetchResults(final OperationHandle p0) throws HiveSQLException;
    
    RowSet fetchResults(final OperationHandle p0, final FetchOrientation p1, final long p2, final FetchType p3) throws HiveSQLException;
    
    String getDelegationToken(final SessionHandle p0, final HiveAuthFactory p1, final String p2, final String p3) throws HiveSQLException;
    
    void cancelDelegationToken(final SessionHandle p0, final HiveAuthFactory p1, final String p2) throws HiveSQLException;
    
    void renewDelegationToken(final SessionHandle p0, final HiveAuthFactory p1, final String p2) throws HiveSQLException;
}
