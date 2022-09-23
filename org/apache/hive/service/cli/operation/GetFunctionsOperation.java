// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.operation;

import org.apache.hive.service.cli.Type;
import org.apache.hive.service.cli.FetchOrientation;
import org.apache.hadoop.hive.ql.exec.FunctionInfo;
import java.util.Iterator;
import java.util.Set;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HivePrivilegeObject;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.ql.exec.FunctionRegistry;
import org.apache.hive.service.cli.CLIServiceUtils;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveOperationType;
import java.util.List;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HivePrivilegeObjectUtils;
import org.apache.thrift.TException;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hive.service.cli.OperationState;
import org.apache.hive.service.cli.RowSetFactory;
import org.apache.hive.service.cli.OperationType;
import org.apache.hive.service.cli.session.HiveSession;
import org.apache.hive.service.cli.RowSet;
import org.apache.hive.service.cli.TableSchema;

public class GetFunctionsOperation extends MetadataOperation
{
    private static final TableSchema RESULT_SET_SCHEMA;
    private final String catalogName;
    private final String schemaName;
    private final String functionName;
    private final RowSet rowSet;
    
    public GetFunctionsOperation(final HiveSession parentSession, final String catalogName, final String schemaName, final String functionName) {
        super(parentSession, OperationType.GET_FUNCTIONS);
        this.catalogName = catalogName;
        this.schemaName = schemaName;
        this.functionName = functionName;
        this.rowSet = RowSetFactory.create(GetFunctionsOperation.RESULT_SET_SCHEMA, this.getProtocolVersion());
    }
    
    public void runInternal() throws HiveSQLException {
        this.setState(OperationState.RUNNING);
        if (this.isAuthV2Enabled()) {
            final IMetaStoreClient metastoreClient = this.getParentSession().getMetaStoreClient();
            final String schemaPattern = this.convertSchemaPattern(this.schemaName);
            List<String> matchingDbs;
            try {
                matchingDbs = metastoreClient.getDatabases(schemaPattern);
            }
            catch (TException e) {
                this.setState(OperationState.ERROR);
                throw new HiveSQLException(e);
            }
            final List<HivePrivilegeObject> privObjs = (List<HivePrivilegeObject>)HivePrivilegeObjectUtils.getHivePrivDbObjects((List)matchingDbs);
            final String cmdStr = "catalog : " + this.catalogName + ", schemaPattern : " + this.schemaName;
            this.authorizeMetaGets(HiveOperationType.GET_FUNCTIONS, privObjs, cmdStr);
        }
        try {
            if ((null == this.catalogName || "".equals(this.catalogName)) && (null == this.schemaName || "".equals(this.schemaName))) {
                final Set<String> functionNames = (Set<String>)FunctionRegistry.getFunctionNames(CLIServiceUtils.patternToRegex(this.functionName));
                for (final String functionName : functionNames) {
                    final FunctionInfo functionInfo = FunctionRegistry.getFunctionInfo(functionName);
                    final Object[] rowData = { null, null, functionInfo.getDisplayName(), "", functionInfo.isGenericUDTF() ? 2 : 1, functionInfo.getClass().getCanonicalName() };
                    this.rowSet.addRow(rowData);
                }
            }
            this.setState(OperationState.FINISHED);
        }
        catch (Exception e2) {
            this.setState(OperationState.ERROR);
            throw new HiveSQLException(e2);
        }
    }
    
    @Override
    public TableSchema getResultSetSchema() throws HiveSQLException {
        this.assertState(OperationState.FINISHED);
        return GetFunctionsOperation.RESULT_SET_SCHEMA;
    }
    
    @Override
    public RowSet getNextRowSet(final FetchOrientation orientation, final long maxRows) throws HiveSQLException {
        this.assertState(OperationState.FINISHED);
        this.validateDefaultFetchOrientation(orientation);
        if (orientation.equals(FetchOrientation.FETCH_FIRST)) {
            this.rowSet.setStartOffset(0L);
        }
        return this.rowSet.extractSubset((int)maxRows);
    }
    
    static {
        RESULT_SET_SCHEMA = new TableSchema().addPrimitiveColumn("FUNCTION_CAT", Type.STRING_TYPE, "Function catalog (may be null)").addPrimitiveColumn("FUNCTION_SCHEM", Type.STRING_TYPE, "Function schema (may be null)").addPrimitiveColumn("FUNCTION_NAME", Type.STRING_TYPE, "Function name. This is the name used to invoke the function").addPrimitiveColumn("REMARKS", Type.STRING_TYPE, "Explanatory comment on the function").addPrimitiveColumn("FUNCTION_TYPE", Type.INT_TYPE, "Kind of function.").addPrimitiveColumn("SPECIFIC_NAME", Type.STRING_TYPE, "The name which uniquely identifies this function within its schema");
    }
}
