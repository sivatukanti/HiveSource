// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.Row;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import org.apache.derby.iapi.sql.dictionary.PermissionsDescriptor;
import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.SQLChar;
import org.apache.derby.iapi.sql.dictionary.TablePermsDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;

public class SYSTABLEPERMSRowFactory extends PermissionsCatalogRowFactory
{
    static final String TABLENAME_STRING = "SYSTABLEPERMS";
    private static final int TABLEPERMSID_COL_NUM = 1;
    private static final int GRANTEE_COL_NUM = 2;
    private static final int GRANTOR_COL_NUM = 3;
    private static final int TABLEID_COL_NUM = 4;
    private static final int SELECTPRIV_COL_NUM = 5;
    private static final int DELETEPRIV_COL_NUM = 6;
    private static final int INSERTPRIV_COL_NUM = 7;
    private static final int UPDATEPRIV_COL_NUM = 8;
    private static final int REFERENCESPRIV_COL_NUM = 9;
    private static final int TRIGGERPRIV_COL_NUM = 10;
    private static final int COLUMN_COUNT = 10;
    public static final int GRANTEE_TABLE_GRANTOR_INDEX_NUM = 0;
    public static final int TABLEPERMSID_INDEX_NUM = 1;
    public static final int TABLEID_INDEX_NUM = 2;
    private static final int[][] indexColumnPositions;
    public static final int GRANTEE_COL_NUM_IN_GRANTEE_TABLE_GRANTOR_INDEX = 1;
    private static final boolean[] indexUniqueness;
    private static final String[] uuids;
    
    SYSTABLEPERMSRowFactory(final UUIDFactory uuidFactory, final ExecutionFactory executionFactory, final DataValueFactory dataValueFactory) {
        super(uuidFactory, executionFactory, dataValueFactory);
        this.initInfo(10, "SYSTABLEPERMS", SYSTABLEPERMSRowFactory.indexColumnPositions, SYSTABLEPERMSRowFactory.indexUniqueness, SYSTABLEPERMSRowFactory.uuids);
    }
    
    public ExecRow makeRow(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2) throws StandardException {
        String string = null;
        String string2 = null;
        String selectPriv = null;
        String deletePriv = null;
        String insertPriv = null;
        String updatePriv = null;
        String referencesPriv = null;
        String triggerPriv = null;
        DataValueDescriptor dataValueDescriptor;
        DataValueDescriptor dataValueDescriptor2;
        if (tupleDescriptor == null) {
            dataValueDescriptor = this.getNullAuthorizationID();
            dataValueDescriptor2 = this.getNullAuthorizationID();
        }
        else {
            final TablePermsDescriptor tablePermsDescriptor = (TablePermsDescriptor)tupleDescriptor;
            UUID uuid = tablePermsDescriptor.getUUID();
            if (uuid == null) {
                uuid = this.getUUIDFactory().createUUID();
                tablePermsDescriptor.setUUID(uuid);
            }
            string = uuid.toString();
            dataValueDescriptor = this.getAuthorizationID(tablePermsDescriptor.getGrantee());
            dataValueDescriptor2 = this.getAuthorizationID(tablePermsDescriptor.getGrantor());
            string2 = tablePermsDescriptor.getTableUUID().toString();
            selectPriv = tablePermsDescriptor.getSelectPriv();
            deletePriv = tablePermsDescriptor.getDeletePriv();
            insertPriv = tablePermsDescriptor.getInsertPriv();
            updatePriv = tablePermsDescriptor.getUpdatePriv();
            referencesPriv = tablePermsDescriptor.getReferencesPriv();
            triggerPriv = tablePermsDescriptor.getTriggerPriv();
        }
        final ExecRow valueRow = this.getExecutionFactory().getValueRow(10);
        valueRow.setColumn(1, new SQLChar(string));
        valueRow.setColumn(2, dataValueDescriptor);
        valueRow.setColumn(3, dataValueDescriptor2);
        valueRow.setColumn(4, new SQLChar(string2));
        valueRow.setColumn(5, new SQLChar(selectPriv));
        valueRow.setColumn(6, new SQLChar(deletePriv));
        valueRow.setColumn(7, new SQLChar(insertPriv));
        valueRow.setColumn(8, new SQLChar(updatePriv));
        valueRow.setColumn(9, new SQLChar(referencesPriv));
        valueRow.setColumn(10, new SQLChar(triggerPriv));
        return valueRow;
    }
    
    public TupleDescriptor buildDescriptor(final ExecRow execRow, final TupleDescriptor tupleDescriptor, final DataDictionary dataDictionary) throws StandardException {
        final UUID recreateUUID = this.getUUIDFactory().recreateUUID(execRow.getColumn(1).getString());
        final TablePermsDescriptor tablePermsDescriptor = new TablePermsDescriptor(dataDictionary, this.getAuthorizationID(execRow, 2), this.getAuthorizationID(execRow, 3), this.getUUIDFactory().recreateUUID(execRow.getColumn(4).getString()), execRow.getColumn(5).getString(), execRow.getColumn(6).getString(), execRow.getColumn(7).getString(), execRow.getColumn(8).getString(), execRow.getColumn(9).getString(), execRow.getColumn(10).getString());
        tablePermsDescriptor.setUUID(recreateUUID);
        return tablePermsDescriptor;
    }
    
    public SystemColumn[] buildColumnList() throws StandardException {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("TABLEPERMSID", false), SystemColumnImpl.getIdentifierColumn("GRANTEE", false), SystemColumnImpl.getIdentifierColumn("GRANTOR", false), SystemColumnImpl.getUUIDColumn("TABLEID", false), SystemColumnImpl.getIndicatorColumn("SELECTPRIV"), SystemColumnImpl.getIndicatorColumn("DELETEPRIV"), SystemColumnImpl.getIndicatorColumn("INSERTPRIV"), SystemColumnImpl.getIndicatorColumn("UPDATEPRIV"), SystemColumnImpl.getIndicatorColumn("REFERENCESPRIV"), SystemColumnImpl.getIndicatorColumn("TRIGGERPRIV") };
    }
    
    public ExecIndexRow buildIndexKeyRow(final int n, final PermissionsDescriptor permissionsDescriptor) throws StandardException {
        Row row = null;
        switch (n) {
            case 0: {
                row = this.getExecutionFactory().getIndexableRow(2);
                row.setColumn(1, this.getAuthorizationID(permissionsDescriptor.getGrantee()));
                row.setColumn(2, new SQLChar(((TablePermsDescriptor)permissionsDescriptor).getTableUUID().toString()));
                break;
            }
            case 1: {
                row = this.getExecutionFactory().getIndexableRow(1);
                row.setColumn(1, new SQLChar(permissionsDescriptor.getObjectID().toString()));
                break;
            }
            case 2: {
                row = this.getExecutionFactory().getIndexableRow(1);
                row.setColumn(1, new SQLChar(((TablePermsDescriptor)permissionsDescriptor).getTableUUID().toString()));
                break;
            }
        }
        return (ExecIndexRow)row;
    }
    
    public int getPrimaryKeyIndexNumber() {
        return 0;
    }
    
    public int orPermissions(final ExecRow execRow, final PermissionsDescriptor permissionsDescriptor, final boolean[] array) throws StandardException {
        final TablePermsDescriptor tablePermsDescriptor = (TablePermsDescriptor)permissionsDescriptor;
        return 0 + this.orOnePermission(execRow, array, 5, tablePermsDescriptor.getSelectPriv()) + this.orOnePermission(execRow, array, 6, tablePermsDescriptor.getDeletePriv()) + this.orOnePermission(execRow, array, 7, tablePermsDescriptor.getInsertPriv()) + this.orOnePermission(execRow, array, 8, tablePermsDescriptor.getUpdatePriv()) + this.orOnePermission(execRow, array, 9, tablePermsDescriptor.getReferencesPriv()) + this.orOnePermission(execRow, array, 10, tablePermsDescriptor.getTriggerPriv());
    }
    
    private int orOnePermission(final ExecRow execRow, final boolean[] array, final int n, final String value) throws StandardException {
        if (value.charAt(0) == 'N') {
            return 0;
        }
        final DataValueDescriptor column = execRow.getColumn(n);
        final char char1 = column.getString().charAt(0);
        if (char1 == 'Y' || char1 == value.charAt(0)) {
            return 0;
        }
        column.setValue(value);
        array[n - 1] = true;
        return 1;
    }
    
    public int removePermissions(final ExecRow execRow, final PermissionsDescriptor permissionsDescriptor, final boolean[] array) throws StandardException {
        final TablePermsDescriptor tablePermsDescriptor = (TablePermsDescriptor)permissionsDescriptor;
        int n = 0;
        if (!(this.removeOnePermission(execRow, array, 5, tablePermsDescriptor.getSelectPriv()) | this.removeOnePermission(execRow, array, 6, tablePermsDescriptor.getDeletePriv()) | this.removeOnePermission(execRow, array, 7, tablePermsDescriptor.getInsertPriv()) | this.removeOnePermission(execRow, array, 8, tablePermsDescriptor.getUpdatePriv()) | this.removeOnePermission(execRow, array, 9, tablePermsDescriptor.getReferencesPriv()) | this.removeOnePermission(execRow, array, 10, tablePermsDescriptor.getTriggerPriv()))) {
            return -1;
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i]) {
                ++n;
            }
        }
        return n;
    }
    
    private boolean removeOnePermission(final ExecRow execRow, final boolean[] array, final int n, final String s) throws StandardException {
        final DataValueDescriptor column = execRow.getColumn(n);
        final char char1 = column.getString().charAt(0);
        if (s.charAt(0) == 'N') {
            return char1 != 'N';
        }
        if (char1 != 'N') {
            column.setValue("N");
            array[n - 1] = true;
        }
        return false;
    }
    
    public void setUUIDOfThePassedDescriptor(final ExecRow execRow, final PermissionsDescriptor permissionsDescriptor) throws StandardException {
        permissionsDescriptor.setUUID(this.getUUIDFactory().recreateUUID(execRow.getColumn(1).getString()));
    }
    
    static {
        indexColumnPositions = new int[][] { { 2, 4, 3 }, { 1 }, { 4 } };
        indexUniqueness = new boolean[] { true, true, false };
        uuids = new String[] { "b8450018-0103-0e39-b8e7-00000010f010", "004b0019-0103-0e39-b8e7-00000010f010", "c851401a-0103-0e39-b8e7-00000010f010", "80220011-010c-426e-c599-0000000f1120", "f81e0010-010c-bc85-060d-000000109ab8" };
    }
}
