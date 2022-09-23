// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.SQLChar;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.sql.dictionary.CatalogRowFactory;

class SYSDUMMY1RowFactory extends CatalogRowFactory
{
    protected static final int SYSDUMMY1_COLUMN_COUNT = 1;
    private static final String[] uuids;
    
    SYSDUMMY1RowFactory(final UUIDFactory uuidFactory, final ExecutionFactory executionFactory, final DataValueFactory dataValueFactory) {
        super(uuidFactory, executionFactory, dataValueFactory);
        this.initInfo(1, "SYSDUMMY1", null, null, SYSDUMMY1RowFactory.uuids);
    }
    
    public ExecRow makeRow(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2) throws StandardException {
        final ExecRow valueRow = this.getExecutionFactory().getValueRow(1);
        valueRow.setColumn(1, new SQLChar("Y"));
        return valueRow;
    }
    
    public TupleDescriptor buildDescriptor(final ExecRow execRow, final TupleDescriptor tupleDescriptor, final DataDictionary dataDictionary) throws StandardException {
        return null;
    }
    
    public SystemColumn[] buildColumnList() {
        return new SystemColumn[] { SystemColumnImpl.getColumn("IBMREQD", 1, true, 1) };
    }
    
    static {
        uuids = new String[] { "c013800d-00f8-5b70-bea3-00000019ed88", "c013800d-00f8-5b70-fee8-000000198c88" };
    }
}
