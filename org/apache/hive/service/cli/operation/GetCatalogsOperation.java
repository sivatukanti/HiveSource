// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.operation;

import org.apache.hive.service.cli.FetchOrientation;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HivePrivilegeObject;
import java.util.List;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveOperationType;
import org.apache.hive.service.cli.OperationState;
import org.apache.hive.service.cli.RowSetFactory;
import org.apache.hive.service.cli.OperationType;
import org.apache.hive.service.cli.session.HiveSession;
import org.apache.hive.service.cli.RowSet;
import org.apache.hive.service.cli.TableSchema;

public class GetCatalogsOperation extends MetadataOperation
{
    private static final TableSchema RESULT_SET_SCHEMA;
    private final RowSet rowSet;
    
    protected GetCatalogsOperation(final HiveSession parentSession) {
        super(parentSession, OperationType.GET_CATALOGS);
        this.rowSet = RowSetFactory.create(GetCatalogsOperation.RESULT_SET_SCHEMA, this.getProtocolVersion());
    }
    
    public void runInternal() throws HiveSQLException {
        this.setState(OperationState.RUNNING);
        try {
            if (this.isAuthV2Enabled()) {
                this.authorizeMetaGets(HiveOperationType.GET_CATALOGS, null);
            }
            this.setState(OperationState.FINISHED);
        }
        catch (HiveSQLException e) {
            this.setState(OperationState.ERROR);
            throw e;
        }
    }
    
    @Override
    public TableSchema getResultSetSchema() throws HiveSQLException {
        return GetCatalogsOperation.RESULT_SET_SCHEMA;
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
        RESULT_SET_SCHEMA = new TableSchema().addStringColumn("TABLE_CAT", "Catalog name. NULL if not applicable.");
    }
}
