// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.types.SQLVarchar;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Row;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.SQLChar;
import org.apache.derby.iapi.sql.dictionary.PermDescriptor;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import org.apache.derby.iapi.sql.dictionary.PermissionsDescriptor;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;

public class SYSPERMSRowFactory extends PermissionsCatalogRowFactory
{
    private static final String TABLENAME_STRING = "SYSPERMS";
    private static final int SYSPERMS_COLUMN_COUNT = 7;
    private static final int SYSPERMS_PERMISSIONID = 1;
    private static final int SYSPERMS_OBJECTTYPE = 2;
    private static final int SYSPERMS_OBJECTID = 3;
    private static final int SYSPERMS_PERMISSION = 4;
    private static final int SYSPERMS_GRANTOR = 5;
    private static final int SYSPERMS_GRANTEE = 6;
    private static final int SYSPERMS_IS_GRANTABLE = 7;
    private static final int[][] indexColumnPositions;
    public static final int PERMS_UUID_IDX_NUM = 0;
    public static final int PERMS_OBJECTID_IDX_NUM = 1;
    public static final int GRANTEE_OBJECTID_GRANTOR_INDEX_NUM = 2;
    private static final boolean[] uniqueness;
    private static final String[] uuids;
    
    SYSPERMSRowFactory(final UUIDFactory uuidFactory, final ExecutionFactory executionFactory, final DataValueFactory dataValueFactory) {
        super(uuidFactory, executionFactory, dataValueFactory);
        this.initInfo(7, "SYSPERMS", SYSPERMSRowFactory.indexColumnPositions, SYSPERMSRowFactory.uniqueness, SYSPERMSRowFactory.uuids);
    }
    
    public ExecIndexRow buildIndexKeyRow(final int n, final PermissionsDescriptor permissionsDescriptor) throws StandardException {
        Row row = null;
        switch (n) {
            case 2: {
                row = this.getExecutionFactory().getIndexableRow(2);
                row.setColumn(1, this.getAuthorizationID(permissionsDescriptor.getGrantee()));
                row.setColumn(2, new SQLChar(((PermDescriptor)permissionsDescriptor).getPermObjectId().toString()));
                break;
            }
            case 0: {
                row = this.getExecutionFactory().getIndexableRow(1);
                row.setColumn(1, new SQLChar(((PermDescriptor)permissionsDescriptor).getUUID().toString()));
                break;
            }
        }
        return (ExecIndexRow)row;
    }
    
    public int getPrimaryKeyIndexNumber() {
        return 2;
    }
    
    public int orPermissions(final ExecRow execRow, final PermissionsDescriptor permissionsDescriptor, final boolean[] array) throws StandardException {
        return 0;
    }
    
    public int removePermissions(final ExecRow execRow, final PermissionsDescriptor permissionsDescriptor, final boolean[] array) throws StandardException {
        return -1;
    }
    
    void setUUIDOfThePassedDescriptor(final ExecRow execRow, final PermissionsDescriptor permissionsDescriptor) throws StandardException {
        permissionsDescriptor.setUUID(this.getUUIDFactory().recreateUUID(execRow.getColumn(1).getString()));
    }
    
    public ExecRow makeRow(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2) throws StandardException {
        String string = null;
        String objectType = "SEQUENCE";
        String string2 = null;
        String permission = "USAGE";
        String grantor = null;
        String grantee = null;
        boolean grantable = false;
        if (tupleDescriptor != null) {
            final PermDescriptor permDescriptor = (PermDescriptor)tupleDescriptor;
            UUID uuid = permDescriptor.getUUID();
            if (uuid == null) {
                uuid = this.getUUIDFactory().createUUID();
                permDescriptor.setUUID(uuid);
            }
            string = uuid.toString();
            objectType = permDescriptor.getObjectType();
            string2 = permDescriptor.getPermObjectId().toString();
            permission = permDescriptor.getPermission();
            grantor = permDescriptor.getGrantor();
            grantee = permDescriptor.getGrantee();
            grantable = permDescriptor.isGrantable();
        }
        final ExecRow valueRow = this.getExecutionFactory().getValueRow(7);
        valueRow.setColumn(1, new SQLChar(string));
        valueRow.setColumn(2, new SQLVarchar(objectType));
        valueRow.setColumn(3, new SQLChar(string2));
        valueRow.setColumn(4, new SQLChar(permission));
        valueRow.setColumn(5, new SQLVarchar(grantor));
        valueRow.setColumn(6, new SQLVarchar(grantee));
        valueRow.setColumn(7, new SQLChar(grantable ? "Y" : "N"));
        return valueRow;
    }
    
    public TupleDescriptor buildDescriptor(final ExecRow execRow, final TupleDescriptor tupleDescriptor, final DataDictionary dataDictionary) throws StandardException {
        return dataDictionary.getDataDescriptorGenerator().newPermDescriptor(this.getUUIDFactory().recreateUUID(execRow.getColumn(1).getString()), execRow.getColumn(2).getString(), this.getUUIDFactory().recreateUUID(execRow.getColumn(3).getString()), execRow.getColumn(4).getString(), execRow.getColumn(5).getString(), execRow.getColumn(6).getString(), execRow.getColumn(7).getString().equals("Y"));
    }
    
    public SystemColumn[] buildColumnList() throws StandardException {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("UUID", false), SystemColumnImpl.getColumn("OBJECTTYPE", 12, false, 36), SystemColumnImpl.getUUIDColumn("OBJECTID", false), SystemColumnImpl.getColumn("PERMISSION", 1, false, 36), SystemColumnImpl.getIdentifierColumn("GRANTOR", false), SystemColumnImpl.getIdentifierColumn("GRANTEE", false), SystemColumnImpl.getIndicatorColumn("ISGRANTABLE") };
    }
    
    static {
        indexColumnPositions = new int[][] { { 1 }, { 3 }, { 6, 3, 5 } };
        uniqueness = new boolean[] { true, false, true };
        uuids = new String[] { "9810800c-0121-c5e1-a2f5-00000043e718", "6ea6ffac-0121-c5e3-f286-00000043e718", "5cc556fc-0121-c5e6-4e43-00000043e718", "7a92cf84-0122-51e6-2c5e-00000047b548", "9810800c-0125-8de5-3aa0-0000001999e8" };
    }
}
