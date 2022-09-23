// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import org.apache.derby.iapi.sql.dictionary.DataDescriptorGenerator;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.types.SQLVarchar;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.SQLChar;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.sql.dictionary.CatalogRowFactory;

class SYSTABLESRowFactory extends CatalogRowFactory
{
    private static final String TABLENAME_STRING = "SYSTABLES";
    protected static final int SYSTABLES_COLUMN_COUNT = 5;
    protected static final int SYSTABLES_TABLEID = 1;
    protected static final int SYSTABLES_TABLENAME = 2;
    protected static final int SYSTABLES_TABLETYPE = 3;
    protected static final int SYSTABLES_SCHEMAID = 4;
    protected static final int SYSTABLES_LOCKGRANULARITY = 5;
    protected static final int SYSTABLES_INDEX1_ID = 0;
    protected static final int SYSTABLES_INDEX1_TABLENAME = 1;
    protected static final int SYSTABLES_INDEX1_SCHEMAID = 2;
    protected static final int SYSTABLES_INDEX2_ID = 1;
    protected static final int SYSTABLES_INDEX2_TABLEID = 1;
    private static final String[] uuids;
    private static final int[][] indexColumnPositions;
    
    SYSTABLESRowFactory(final UUIDFactory uuidFactory, final ExecutionFactory executionFactory, final DataValueFactory dataValueFactory) {
        super(uuidFactory, executionFactory, dataValueFactory);
        this.initInfo(5, "SYSTABLES", SYSTABLESRowFactory.indexColumnPositions, null, SYSTABLESRowFactory.uuids);
    }
    
    public ExecRow makeRow(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2) throws StandardException {
        String s = null;
        String s2 = null;
        String string = null;
        String string2 = null;
        String name = null;
        if (tupleDescriptor != null) {
            final TableDescriptor tableDescriptor = (TableDescriptor)tupleDescriptor;
            final SchemaDescriptor schemaDescriptor = (SchemaDescriptor)tupleDescriptor2;
            UUID uuid = tableDescriptor.getUUID();
            if (uuid == null) {
                uuid = this.getUUIDFactory().createUUID();
                tableDescriptor.setUUID(uuid);
            }
            string = uuid.toString();
            string2 = schemaDescriptor.getUUID().toString();
            name = tableDescriptor.getName();
            switch (tableDescriptor.getTableType()) {
                case 0: {
                    s = "T";
                    break;
                }
                case 1: {
                    s = "S";
                    break;
                }
                case 2: {
                    s = "V";
                    break;
                }
                case 4: {
                    s = "A";
                    break;
                }
            }
            s2 = new String(new char[] { tableDescriptor.getLockGranularity() });
        }
        final ExecRow valueRow = this.getExecutionFactory().getValueRow(5);
        valueRow.setColumn(1, new SQLChar(string));
        valueRow.setColumn(2, new SQLVarchar(name));
        valueRow.setColumn(3, new SQLChar(s));
        valueRow.setColumn(4, new SQLChar(string2));
        valueRow.setColumn(5, new SQLChar(s2));
        return valueRow;
    }
    
    ExecIndexRow buildEmptyIndexRow(final int n, final RowLocation rowLocation) throws StandardException {
        final int indexColumnCount = this.getIndexColumnCount(n);
        final ExecIndexRow indexableRow = this.getExecutionFactory().getIndexableRow(indexColumnCount + 1);
        indexableRow.setColumn(indexColumnCount + 1, rowLocation);
        switch (n) {
            case 0: {
                indexableRow.setColumn(1, new SQLVarchar());
                indexableRow.setColumn(2, new SQLChar());
                break;
            }
            case 1: {
                indexableRow.setColumn(1, new SQLChar());
                break;
            }
        }
        return indexableRow;
    }
    
    TupleDescriptor buildDescriptor(final ExecRow execRow, final TupleDescriptor tupleDescriptor, final DataDictionary dataDictionary, final int n) throws StandardException {
        return this.buildDescriptorBody(execRow, tupleDescriptor, dataDictionary, n);
    }
    
    public TupleDescriptor buildDescriptor(final ExecRow execRow, final TupleDescriptor tupleDescriptor, final DataDictionary dataDictionary) throws StandardException {
        return this.buildDescriptorBody(execRow, tupleDescriptor, dataDictionary, 4);
    }
    
    public TupleDescriptor buildDescriptorBody(final ExecRow execRow, final TupleDescriptor tupleDescriptor, final DataDictionary dataDictionary, final int n) throws StandardException {
        final DataDescriptorGenerator dataDescriptorGenerator = dataDictionary.getDataDescriptorGenerator();
        final UUID recreateUUID = this.getUUIDFactory().recreateUUID(execRow.getColumn(1).getString());
        final String string = execRow.getColumn(2).getString();
        int n2 = 0;
        switch (execRow.getColumn(3).getString().charAt(0)) {
            case 'T': {
                n2 = 0;
                break;
            }
            case 'S': {
                n2 = 1;
                break;
            }
            case 'V': {
                n2 = 2;
                break;
            }
            case 'A': {
                n2 = 4;
                break;
            }
            default: {
                n2 = -1;
                break;
            }
        }
        final TableDescriptor tableDescriptor = dataDescriptorGenerator.newTableDescriptor(string, dataDictionary.getSchemaDescriptor(this.getUUIDFactory().recreateUUID(execRow.getColumn(4).getString()), n, null), n2, execRow.getColumn(5).getString().charAt(0));
        tableDescriptor.setUUID(recreateUUID);
        return tableDescriptor;
    }
    
    protected String getTableName(final ExecRow execRow) throws StandardException {
        return execRow.getColumn(2).getString();
    }
    
    public SystemColumn[] buildColumnList() throws StandardException {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("TABLEID", false), SystemColumnImpl.getIdentifierColumn("TABLENAME", false), SystemColumnImpl.getIndicatorColumn("TABLETYPE"), SystemColumnImpl.getUUIDColumn("SCHEMAID", false), SystemColumnImpl.getIndicatorColumn("LOCKGRANULARITY") };
    }
    
    static {
        uuids = new String[] { "80000018-00d0-fd77-3ed8-000a0a0b1900", "80000028-00d0-fd77-3ed8-000a0a0b1900", "8000001a-00d0-fd77-3ed8-000a0a0b1900", "8000001c-00d0-fd77-3ed8-000a0a0b1900" };
        indexColumnPositions = new int[][] { { 2, 4 }, { 1 } };
    }
}
