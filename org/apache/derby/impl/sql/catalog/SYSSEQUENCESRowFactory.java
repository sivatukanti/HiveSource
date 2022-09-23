// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.DataDescriptorGenerator;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.catalog.TypeDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.SQLLongint;
import org.apache.derby.iapi.types.UserType;
import org.apache.derby.iapi.types.SQLVarchar;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.SQLChar;
import org.apache.derby.iapi.sql.dictionary.SequenceDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.sql.dictionary.CatalogRowFactory;

public class SYSSEQUENCESRowFactory extends CatalogRowFactory
{
    public static final String TABLENAME_STRING = "SYSSEQUENCES";
    public static final int SYSSEQUENCES_COLUMN_COUNT = 10;
    public static final int SYSSEQUENCES_SEQUENCEID = 1;
    public static final int SYSSEQUENCES_SEQUENCENAME = 2;
    public static final int SYSSEQUENCES_SCHEMAID = 3;
    public static final int SYSSEQUENCES_SEQUENCEDATATYPE = 4;
    public static final int SYSSEQUENCES_CURRENT_VALUE = 5;
    public static final int SYSSEQUENCES_START_VALUE = 6;
    public static final int SYSSEQUENCES_MINIMUM_VALUE = 7;
    public static final int SYSSEQUENCES_MAXIMUM_VALUE = 8;
    public static final int SYSSEQUENCES_INCREMENT = 9;
    public static final int SYSSEQUENCES_CYCLE_OPTION = 10;
    private static final int[][] indexColumnPositions;
    static final int SYSSEQUENCES_INDEX1_ID = 0;
    static final int SYSSEQUENCES_INDEX2_ID = 1;
    private static final boolean[] uniqueness;
    private static final String[] uuids;
    
    SYSSEQUENCESRowFactory(final UUIDFactory uuidFactory, final ExecutionFactory executionFactory, final DataValueFactory dataValueFactory) {
        super(uuidFactory, executionFactory, dataValueFactory);
        this.initInfo(10, "SYSSEQUENCES", SYSSEQUENCESRowFactory.indexColumnPositions, SYSSEQUENCESRowFactory.uniqueness, SYSSEQUENCESRowFactory.uuids);
    }
    
    public ExecRow makeRow(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2) throws StandardException {
        String string = null;
        String sequenceName = null;
        String string2 = null;
        Object catalogType = null;
        Long currentValue = null;
        long startValue = 0L;
        long minimumValue = 0L;
        long maximumValue = 0L;
        long increment = 0L;
        boolean canCycle = false;
        if (tupleDescriptor != null) {
            final SequenceDescriptor sequenceDescriptor = (SequenceDescriptor)tupleDescriptor;
            string = sequenceDescriptor.getUUID().toString();
            sequenceName = sequenceDescriptor.getSequenceName();
            string2 = sequenceDescriptor.getSchemaId().toString();
            catalogType = sequenceDescriptor.getDataType().getCatalogType();
            currentValue = sequenceDescriptor.getCurrentValue();
            startValue = sequenceDescriptor.getStartValue();
            minimumValue = sequenceDescriptor.getMinimumValue();
            maximumValue = sequenceDescriptor.getMaximumValue();
            increment = sequenceDescriptor.getIncrement();
            canCycle = sequenceDescriptor.canCycle();
        }
        final ExecRow valueRow = this.getExecutionFactory().getValueRow(10);
        valueRow.setColumn(1, new SQLChar(string));
        valueRow.setColumn(2, new SQLVarchar(sequenceName));
        valueRow.setColumn(3, new SQLChar(string2));
        valueRow.setColumn(4, new UserType(catalogType));
        SQLLongint sqlLongint;
        if (currentValue == null) {
            sqlLongint = new SQLLongint();
        }
        else {
            sqlLongint = new SQLLongint((long)currentValue);
        }
        valueRow.setColumn(5, sqlLongint);
        valueRow.setColumn(6, new SQLLongint(startValue));
        valueRow.setColumn(7, new SQLLongint(minimumValue));
        valueRow.setColumn(8, new SQLLongint(maximumValue));
        valueRow.setColumn(9, new SQLLongint(increment));
        valueRow.setColumn(10, new SQLChar(canCycle ? "Y" : "N"));
        return valueRow;
    }
    
    public TupleDescriptor buildDescriptor(final ExecRow execRow, final TupleDescriptor tupleDescriptor, final DataDictionary dataDictionary) throws StandardException {
        final DataDescriptorGenerator dataDescriptorGenerator = dataDictionary.getDataDescriptorGenerator();
        final UUID recreateUUID = this.getUUIDFactory().recreateUUID(execRow.getColumn(1).getString());
        final String string = execRow.getColumn(2).getString();
        final UUID recreateUUID2 = this.getUUIDFactory().recreateUUID(execRow.getColumn(3).getString());
        final DataTypeDescriptor type = DataTypeDescriptor.getType((TypeDescriptor)execRow.getColumn(4).getObject());
        final DataValueDescriptor column = execRow.getColumn(5);
        Long n;
        if (column.isNull()) {
            n = null;
        }
        else {
            n = new Long(column.getLong());
        }
        return dataDescriptorGenerator.newSequenceDescriptor(dataDictionary.getSchemaDescriptor(recreateUUID2, null), recreateUUID, string, type, n, execRow.getColumn(6).getLong(), execRow.getColumn(7).getLong(), execRow.getColumn(8).getLong(), execRow.getColumn(9).getLong(), execRow.getColumn(10).getString().equals("Y"));
    }
    
    public SystemColumn[] buildColumnList() throws StandardException {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("SEQUENCEID", false), SystemColumnImpl.getIdentifierColumn("SEQUENCENAME", false), SystemColumnImpl.getUUIDColumn("SCHEMAID", false), SystemColumnImpl.getJavaColumn("SEQUENCEDATATYPE", "org.apache.derby.catalog.TypeDescriptor", false), SystemColumnImpl.getColumn("CURRENTVALUE", -5, true), SystemColumnImpl.getColumn("STARTVALUE", -5, false), SystemColumnImpl.getColumn("MINIMUMVALUE", -5, false), SystemColumnImpl.getColumn("MAXIMUMVALUE", -5, false), SystemColumnImpl.getColumn("INCREMENT", -5, false), SystemColumnImpl.getIndicatorColumn("CYCLEOPTION") };
    }
    
    static {
        indexColumnPositions = new int[][] { { 1 }, { 3, 2 } };
        uniqueness = null;
        uuids = new String[] { "9810800c-0121-c5e2-e794-00000043e718", "6ea6ffac-0121-c5e6-29e6-00000043e718", "7a92cf84-0121-c5fa-caf1-00000043e718", "6b138684-0121-c5e9-9114-00000043e718" };
    }
}
