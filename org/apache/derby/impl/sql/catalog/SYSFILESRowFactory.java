// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.SQLLongint;
import org.apache.derby.iapi.types.SQLVarchar;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.SQLChar;
import org.apache.derby.iapi.sql.dictionary.FileInfoDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.sql.dictionary.CatalogRowFactory;

class SYSFILESRowFactory extends CatalogRowFactory
{
    private static final String TABLENAME_STRING = "SYSFILES";
    private static final int SYSFILES_COLUMN_COUNT = 4;
    private static final int ID_COL_NUM = 1;
    private static final String ID_COL_NAME = "FILEID";
    private static final int SCHEMA_ID_COL_NUM = 2;
    private static final String SCHEMA_ID_COL_NAME = "SCHEMAID";
    private static final int NAME_COL_NUM = 3;
    private static final String NAME_COL_NAME = "FILENAME";
    private static final int GENERATION_ID_COL_NUM = 4;
    private static final String GENERATION_ID_COL_NAME = "GENERATIONID";
    static final int SYSFILES_INDEX1_ID = 0;
    static final int SYSFILES_INDEX2_ID = 1;
    private static final int[][] indexColumnPositions;
    private static final boolean[] uniqueness;
    private static final String[] uuids;
    
    SYSFILESRowFactory(final UUIDFactory uuidFactory, final ExecutionFactory executionFactory, final DataValueFactory dataValueFactory) {
        super(uuidFactory, executionFactory, dataValueFactory);
        this.initInfo(4, "SYSFILES", SYSFILESRowFactory.indexColumnPositions, SYSFILESRowFactory.uniqueness, SYSFILESRowFactory.uuids);
    }
    
    public ExecRow makeRow(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2) throws StandardException {
        String string = null;
        String string2 = null;
        String name = null;
        long generationId = 0L;
        if (tupleDescriptor != null) {
            final FileInfoDescriptor fileInfoDescriptor = (FileInfoDescriptor)tupleDescriptor;
            string = fileInfoDescriptor.getUUID().toString();
            string2 = fileInfoDescriptor.getSchemaDescriptor().getUUID().toString();
            name = fileInfoDescriptor.getName();
            generationId = fileInfoDescriptor.getGenerationId();
        }
        final ExecRow valueRow = this.getExecutionFactory().getValueRow(4);
        valueRow.setColumn(1, new SQLChar(string));
        valueRow.setColumn(2, new SQLChar(string2));
        valueRow.setColumn(3, new SQLVarchar(name));
        valueRow.setColumn(4, new SQLLongint(generationId));
        return valueRow;
    }
    
    public TupleDescriptor buildDescriptor(final ExecRow execRow, final TupleDescriptor tupleDescriptor, final DataDictionary dataDictionary) throws StandardException {
        return dataDictionary.getDataDescriptorGenerator().newFileInfoDescriptor(this.getUUIDFactory().recreateUUID(execRow.getColumn(1).getString()), dataDictionary.getSchemaDescriptor(this.getUUIDFactory().recreateUUID(execRow.getColumn(2).getString()), null), execRow.getColumn(3).getString(), execRow.getColumn(4).getLong());
    }
    
    public SystemColumn[] buildColumnList() throws StandardException {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("FILEID", false), SystemColumnImpl.getUUIDColumn("SCHEMAID", false), SystemColumnImpl.getIdentifierColumn("FILENAME", false), SystemColumnImpl.getColumn("GENERATIONID", -5, false) };
    }
    
    static {
        indexColumnPositions = new int[][] { { 3, 2 }, { 1 } };
        uniqueness = null;
        uuids = new String[] { "80000000-00d3-e222-873f-000a0a0b1900", "80000000-00d3-e222-9920-000a0a0b1900", "80000000-00d3-e222-a373-000a0a0b1900", "80000000-00d3-e222-be7b-000a0a0b1900" };
    }
}
