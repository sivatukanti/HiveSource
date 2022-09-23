// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import org.apache.derby.iapi.sql.dictionary.SubKeyConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.SQLChar;
import org.apache.derby.iapi.sql.dictionary.KeyConstraintDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.sql.dictionary.CatalogRowFactory;

public class SYSKEYSRowFactory extends CatalogRowFactory
{
    private static final String TABLENAME_STRING = "SYSKEYS";
    protected static final int SYSKEYS_COLUMN_COUNT = 2;
    protected static final int SYSKEYS_CONSTRAINTID = 1;
    protected static final int SYSKEYS_CONGLOMERATEID = 2;
    protected static final int SYSKEYS_INDEX1_ID = 0;
    private static final boolean[] uniqueness;
    private static final int[][] indexColumnPositions;
    private static final String[] uuids;
    
    SYSKEYSRowFactory(final UUIDFactory uuidFactory, final ExecutionFactory executionFactory, final DataValueFactory dataValueFactory) {
        super(uuidFactory, executionFactory, dataValueFactory);
        this.initInfo(2, "SYSKEYS", SYSKEYSRowFactory.indexColumnPositions, SYSKEYSRowFactory.uniqueness, SYSKEYSRowFactory.uuids);
    }
    
    public ExecRow makeRow(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2) throws StandardException {
        String string = null;
        String indexUUIDString = null;
        if (tupleDescriptor != null) {
            final KeyConstraintDescriptor keyConstraintDescriptor = (KeyConstraintDescriptor)tupleDescriptor;
            string = keyConstraintDescriptor.getUUID().toString();
            indexUUIDString = keyConstraintDescriptor.getIndexUUIDString();
        }
        final ExecRow valueRow = this.getExecutionFactory().getValueRow(2);
        valueRow.setColumn(1, new SQLChar(string));
        valueRow.setColumn(2, new SQLChar(indexUUIDString));
        return valueRow;
    }
    
    public TupleDescriptor buildDescriptor(final ExecRow execRow, final TupleDescriptor tupleDescriptor, final DataDictionary dataDictionary) throws StandardException {
        dataDictionary.getDataDescriptorGenerator();
        return new SubKeyConstraintDescriptor(this.getUUIDFactory().recreateUUID(execRow.getColumn(1).getString()), this.getUUIDFactory().recreateUUID(execRow.getColumn(2).getString()));
    }
    
    public SystemColumn[] buildColumnList() throws StandardException {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("CONSTRAINTID", false), SystemColumnImpl.getUUIDColumn("CONGLOMERATEID", false) };
    }
    
    static {
        uniqueness = null;
        indexColumnPositions = new int[][] { { 1 } };
        uuids = new String[] { "80000039-00d0-fd77-3ed8-000a0a0b1900", "8000003c-00d0-fd77-3ed8-000a0a0b1900", "8000003b-00d0-fd77-3ed8-000a0a0b1900" };
    }
}
