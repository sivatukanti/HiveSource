// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import org.apache.derby.iapi.sql.dictionary.SubKeyConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.SQLChar;
import org.apache.derby.iapi.sql.dictionary.ForeignKeyConstraintDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.sql.dictionary.CatalogRowFactory;

public class SYSFOREIGNKEYSRowFactory extends CatalogRowFactory
{
    private static final String TABLENAME_STRING = "SYSFOREIGNKEYS";
    protected static final int SYSFOREIGNKEYS_COLUMN_COUNT = 5;
    protected static final int SYSFOREIGNKEYS_CONSTRAINTID = 1;
    protected static final int SYSFOREIGNKEYS_CONGLOMERATEID = 2;
    protected static final int SYSFOREIGNKEYS_KEYCONSTRAINTID = 3;
    protected static final int SYSFOREIGNKEYS_DELETERULE = 4;
    protected static final int SYSFOREIGNKEYS_UPDATERULE = 5;
    protected static final int SYSFOREIGNKEYS_CONSTRAINTID_WIDTH = 36;
    protected static final int SYSFOREIGNKEYS_INDEX1_ID = 0;
    protected static final int SYSFOREIGNKEYS_INDEX2_ID = 1;
    private static final int[][] indexColumnPositions;
    private static final boolean[] uniqueness;
    private static final String[] uuids;
    
    SYSFOREIGNKEYSRowFactory(final UUIDFactory uuidFactory, final ExecutionFactory executionFactory, final DataValueFactory dataValueFactory) {
        super(uuidFactory, executionFactory, dataValueFactory);
        this.initInfo(5, "SYSFOREIGNKEYS", SYSFOREIGNKEYSRowFactory.indexColumnPositions, SYSFOREIGNKEYSRowFactory.uniqueness, SYSFOREIGNKEYSRowFactory.uuids);
    }
    
    public ExecRow makeRow(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2) throws StandardException {
        String string = null;
        String string2 = null;
        String indexUUIDString = null;
        String refActionAsString = "N";
        String refActionAsString2 = "N";
        if (tupleDescriptor != null) {
            final ForeignKeyConstraintDescriptor foreignKeyConstraintDescriptor = (ForeignKeyConstraintDescriptor)tupleDescriptor;
            string = foreignKeyConstraintDescriptor.getUUID().toString();
            string2 = foreignKeyConstraintDescriptor.getReferencedConstraint().getUUID().toString();
            indexUUIDString = foreignKeyConstraintDescriptor.getIndexUUIDString();
            refActionAsString = this.getRefActionAsString(foreignKeyConstraintDescriptor.getRaDeleteRule());
            refActionAsString2 = this.getRefActionAsString(foreignKeyConstraintDescriptor.getRaUpdateRule());
        }
        final ExecIndexRow indexableRow = this.getExecutionFactory().getIndexableRow(5);
        indexableRow.setColumn(1, new SQLChar(string));
        indexableRow.setColumn(2, new SQLChar(indexUUIDString));
        indexableRow.setColumn(3, new SQLChar(string2));
        indexableRow.setColumn(4, new SQLChar(refActionAsString));
        indexableRow.setColumn(5, new SQLChar(refActionAsString2));
        return indexableRow;
    }
    
    public TupleDescriptor buildDescriptor(final ExecRow execRow, final TupleDescriptor tupleDescriptor, final DataDictionary dataDictionary) throws StandardException {
        dataDictionary.getDataDescriptorGenerator();
        return new SubKeyConstraintDescriptor(this.getUUIDFactory().recreateUUID(execRow.getColumn(1).getString()), this.getUUIDFactory().recreateUUID(execRow.getColumn(2).getString()), this.getUUIDFactory().recreateUUID(execRow.getColumn(3).getString()), this.getRefActionAsInt(execRow.getColumn(4).getString()), this.getRefActionAsInt(execRow.getColumn(5).getString()));
    }
    
    public SystemColumn[] buildColumnList() throws StandardException {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("CONSTRAINTID", false), SystemColumnImpl.getUUIDColumn("CONGLOMERATEID", false), SystemColumnImpl.getUUIDColumn("KEYCONSTRAINTID", false), SystemColumnImpl.getIndicatorColumn("DELETERULE"), SystemColumnImpl.getIndicatorColumn("UPDATERULE") };
    }
    
    int getRefActionAsInt(final String s) {
        int n = 0;
        switch (s.charAt(0)) {
            case 'C': {
                n = 0;
                break;
            }
            case 'S': {
                n = 1;
                break;
            }
            case 'R': {
                n = 2;
                break;
            }
            case 'U': {
                n = 3;
                break;
            }
            case 'D': {
                n = 4;
                break;
            }
            default: {
                n = 2;
                break;
            }
        }
        return n;
    }
    
    String getRefActionAsString(int n) {
        String s = null;
        switch (n) {
            case 0: {
                s = "C";
                break;
            }
            case 1: {
                s = "S";
                break;
            }
            case 2: {
                s = "R";
                break;
            }
            case 3: {
                s = "U";
                break;
            }
            case 4: {
                s = "D";
                n = 4;
                break;
            }
            default: {
                s = "N";
                break;
            }
        }
        return s;
    }
    
    static {
        indexColumnPositions = new int[][] { { 1 }, { 3 } };
        uniqueness = new boolean[] { true, false };
        uuids = new String[] { "8000005b-00d0-fd77-3ed8-000a0a0b1900", "80000060-00d0-fd77-3ed8-000a0a0b1900", "8000005d-00d0-fd77-3ed8-000a0a0b1900", "8000005f-00d0-fd77-3ed8-000a0a0b1900" };
    }
}
