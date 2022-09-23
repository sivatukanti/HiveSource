// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.Row;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import org.apache.derby.iapi.sql.dictionary.PermissionsDescriptor;
import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.types.UserType;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.SQLChar;
import org.apache.derby.iapi.sql.dictionary.ColPermsDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;

class SYSCOLPERMSRowFactory extends PermissionsCatalogRowFactory
{
    static final String TABLENAME_STRING = "SYSCOLPERMS";
    private static final int COLPERMSID_COL_NUM = 1;
    private static final int GRANTEE_COL_NUM = 2;
    private static final int GRANTOR_COL_NUM = 3;
    private static final int TABLEID_COL_NUM = 4;
    private static final int TYPE_COL_NUM = 5;
    protected static final int COLUMNS_COL_NUM = 6;
    private static final int COLUMN_COUNT = 6;
    static final int GRANTEE_TABLE_TYPE_GRANTOR_INDEX_NUM = 0;
    static final int COLPERMSID_INDEX_NUM = 1;
    static final int TABLEID_INDEX_NUM = 2;
    protected static final int TOTAL_NUM_OF_INDEXES = 3;
    private static final int[][] indexColumnPositions;
    public static final int GRANTEE_COL_NUM_IN_GRANTEE_TABLE_TYPE_GRANTOR_INDEX = 1;
    private static final boolean[] indexUniqueness;
    private static final String[] uuids;
    
    SYSCOLPERMSRowFactory(final UUIDFactory uuidFactory, final ExecutionFactory executionFactory, final DataValueFactory dataValueFactory) {
        super(uuidFactory, executionFactory, dataValueFactory);
        this.initInfo(6, "SYSCOLPERMS", SYSCOLPERMSRowFactory.indexColumnPositions, SYSCOLPERMSRowFactory.indexUniqueness, SYSCOLPERMSRowFactory.uuids);
    }
    
    public ExecRow makeRow(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2) throws StandardException {
        String string = null;
        String string2 = null;
        String type = null;
        Object columns = null;
        DataValueDescriptor dataValueDescriptor;
        DataValueDescriptor dataValueDescriptor2;
        if (tupleDescriptor == null) {
            dataValueDescriptor = this.getNullAuthorizationID();
            dataValueDescriptor2 = this.getNullAuthorizationID();
        }
        else {
            final ColPermsDescriptor colPermsDescriptor = (ColPermsDescriptor)tupleDescriptor;
            UUID uuid = colPermsDescriptor.getUUID();
            if (uuid == null) {
                uuid = this.getUUIDFactory().createUUID();
                colPermsDescriptor.setUUID(uuid);
            }
            string = uuid.toString();
            dataValueDescriptor = this.getAuthorizationID(colPermsDescriptor.getGrantee());
            dataValueDescriptor2 = this.getAuthorizationID(colPermsDescriptor.getGrantor());
            string2 = colPermsDescriptor.getTableUUID().toString();
            type = colPermsDescriptor.getType();
            columns = colPermsDescriptor.getColumns();
        }
        final ExecRow valueRow = this.getExecutionFactory().getValueRow(6);
        valueRow.setColumn(1, new SQLChar(string));
        valueRow.setColumn(2, dataValueDescriptor);
        valueRow.setColumn(3, dataValueDescriptor2);
        valueRow.setColumn(4, new SQLChar(string2));
        valueRow.setColumn(5, new SQLChar(type));
        valueRow.setColumn(6, new UserType(columns));
        return valueRow;
    }
    
    public TupleDescriptor buildDescriptor(final ExecRow execRow, final TupleDescriptor tupleDescriptor, final DataDictionary dataDictionary) throws StandardException {
        final UUID recreateUUID = this.getUUIDFactory().recreateUUID(execRow.getColumn(1).getString());
        final ColPermsDescriptor colPermsDescriptor = new ColPermsDescriptor(dataDictionary, this.getAuthorizationID(execRow, 2), this.getAuthorizationID(execRow, 3), this.getUUIDFactory().recreateUUID(execRow.getColumn(4).getString()), execRow.getColumn(5).getString(), (FormatableBitSet)execRow.getColumn(6).getObject());
        colPermsDescriptor.setUUID(recreateUUID);
        return colPermsDescriptor;
    }
    
    public SystemColumn[] buildColumnList() throws StandardException {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("COLPERMSID", false), SystemColumnImpl.getIdentifierColumn("GRANTEE", false), SystemColumnImpl.getIdentifierColumn("GRANTOR", false), SystemColumnImpl.getUUIDColumn("TABLEID", false), SystemColumnImpl.getIndicatorColumn("TYPE"), SystemColumnImpl.getJavaColumn("COLUMNS", "org.apache.derby.iapi.services.io.FormatableBitSet", false) };
    }
    
    public ExecIndexRow buildIndexKeyRow(final int n, final PermissionsDescriptor permissionsDescriptor) throws StandardException {
        Row row = null;
        switch (n) {
            case 0: {
                row = this.getExecutionFactory().getIndexableRow(3);
                row.setColumn(1, this.getAuthorizationID(permissionsDescriptor.getGrantee()));
                final ColPermsDescriptor colPermsDescriptor = (ColPermsDescriptor)permissionsDescriptor;
                row.setColumn(2, new SQLChar(colPermsDescriptor.getTableUUID().toString()));
                row.setColumn(3, new SQLChar(colPermsDescriptor.getType()));
                break;
            }
            case 1: {
                row = this.getExecutionFactory().getIndexableRow(1);
                row.setColumn(1, new SQLChar(permissionsDescriptor.getObjectID().toString()));
                break;
            }
            case 2: {
                row = this.getExecutionFactory().getIndexableRow(1);
                row.setColumn(1, new SQLChar(((ColPermsDescriptor)permissionsDescriptor).getTableUUID().toString()));
                break;
            }
        }
        return (ExecIndexRow)row;
    }
    
    public int getPrimaryKeyIndexNumber() {
        return 0;
    }
    
    public int orPermissions(final ExecRow execRow, final PermissionsDescriptor permissionsDescriptor, final boolean[] array) throws StandardException {
        final ColPermsDescriptor colPermsDescriptor = (ColPermsDescriptor)permissionsDescriptor;
        final FormatableBitSet set = (FormatableBitSet)execRow.getColumn(6).getObject();
        final FormatableBitSet columns = colPermsDescriptor.getColumns();
        boolean b = false;
        for (int i = columns.anySetBit(); i >= 0; i = columns.anySetBit(i)) {
            if (!set.get(i)) {
                set.set(i);
                b = true;
            }
        }
        if (b) {
            array[5] = true;
            return 1;
        }
        return 0;
    }
    
    public int removePermissions(final ExecRow execRow, final PermissionsDescriptor permissionsDescriptor, final boolean[] array) throws StandardException {
        final FormatableBitSet columns = ((ColPermsDescriptor)permissionsDescriptor).getColumns();
        if (columns == null) {
            return -1;
        }
        final FormatableBitSet set = (FormatableBitSet)execRow.getColumn(6).getObject();
        boolean b = false;
        for (int i = columns.anySetBit(); i >= 0; i = columns.anySetBit(i)) {
            if (set.get(i)) {
                set.clear(i);
                b = true;
            }
        }
        if (!b) {
            return 0;
        }
        array[5] = true;
        if (set.anySetBit() < 0) {
            return -1;
        }
        return 1;
    }
    
    public void setUUIDOfThePassedDescriptor(final ExecRow execRow, final PermissionsDescriptor permissionsDescriptor) throws StandardException {
        permissionsDescriptor.setUUID(this.getUUIDFactory().recreateUUID(execRow.getColumn(1).getString()));
    }
    
    static {
        indexColumnPositions = new int[][] { { 2, 4, 5, 3 }, { 1 }, { 4 } };
        indexUniqueness = new boolean[] { true, true, false };
        uuids = new String[] { "286cc01e-0103-0e39-b8e7-00000010f010", "6074401f-0103-0e39-b8e7-00000010f010", "787c0020-0103-0e39-b8e7-00000010f010", "c9a3808d-010c-42a2-ae15-0000000f67f8", "80220011-010c-bc85-060d-000000109ab8" };
    }
}
