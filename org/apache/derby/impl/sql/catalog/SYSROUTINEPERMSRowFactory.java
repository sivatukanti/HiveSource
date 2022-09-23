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
import org.apache.derby.iapi.sql.dictionary.RoutinePermsDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;

public class SYSROUTINEPERMSRowFactory extends PermissionsCatalogRowFactory
{
    static final String TABLENAME_STRING = "SYSROUTINEPERMS";
    private static final int ROUTINEPERMSID_COL_NUM = 1;
    private static final int GRANTEE_COL_NUM = 2;
    private static final int GRANTOR_COL_NUM = 3;
    private static final int ALIASID_COL_NUM = 4;
    private static final int GRANTOPTION_COL_NUM = 5;
    private static final int COLUMN_COUNT = 5;
    static final int GRANTEE_ALIAS_GRANTOR_INDEX_NUM = 0;
    public static final int ROUTINEPERMSID_INDEX_NUM = 1;
    public static final int ALIASID_INDEX_NUM = 2;
    private static final int[][] indexColumnPositions;
    public static final int GRANTEE_COL_NUM_IN_GRANTEE_ALIAS_GRANTOR_INDEX = 1;
    private static final boolean[] indexUniqueness;
    private static final String[] uuids;
    
    SYSROUTINEPERMSRowFactory(final UUIDFactory uuidFactory, final ExecutionFactory executionFactory, final DataValueFactory dataValueFactory) {
        super(uuidFactory, executionFactory, dataValueFactory);
        this.initInfo(5, "SYSROUTINEPERMS", SYSROUTINEPERMSRowFactory.indexColumnPositions, SYSROUTINEPERMSRowFactory.indexUniqueness, SYSROUTINEPERMSRowFactory.uuids);
    }
    
    public ExecRow makeRow(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2) throws StandardException {
        String string = null;
        String string2 = null;
        DataValueDescriptor dataValueDescriptor;
        DataValueDescriptor dataValueDescriptor2;
        if (tupleDescriptor == null) {
            dataValueDescriptor = this.getNullAuthorizationID();
            dataValueDescriptor2 = this.getNullAuthorizationID();
        }
        else {
            final RoutinePermsDescriptor routinePermsDescriptor = (RoutinePermsDescriptor)tupleDescriptor;
            UUID uuid = routinePermsDescriptor.getUUID();
            if (uuid == null) {
                uuid = this.getUUIDFactory().createUUID();
                routinePermsDescriptor.setUUID(uuid);
            }
            string = uuid.toString();
            dataValueDescriptor = this.getAuthorizationID(routinePermsDescriptor.getGrantee());
            dataValueDescriptor2 = this.getAuthorizationID(routinePermsDescriptor.getGrantor());
            if (routinePermsDescriptor.getRoutineUUID() != null) {
                string2 = routinePermsDescriptor.getRoutineUUID().toString();
            }
        }
        final ExecRow valueRow = this.getExecutionFactory().getValueRow(5);
        valueRow.setColumn(1, new SQLChar(string));
        valueRow.setColumn(2, dataValueDescriptor);
        valueRow.setColumn(3, dataValueDescriptor2);
        valueRow.setColumn(4, new SQLChar(string2));
        valueRow.setColumn(5, new SQLChar("N"));
        return valueRow;
    }
    
    public TupleDescriptor buildDescriptor(final ExecRow execRow, final TupleDescriptor tupleDescriptor, final DataDictionary dataDictionary) throws StandardException {
        final UUID recreateUUID = this.getUUIDFactory().recreateUUID(execRow.getColumn(1).getString());
        final RoutinePermsDescriptor routinePermsDescriptor = new RoutinePermsDescriptor(dataDictionary, this.getAuthorizationID(execRow, 2), this.getAuthorizationID(execRow, 3), this.getUUIDFactory().recreateUUID(execRow.getColumn(4).getString()));
        routinePermsDescriptor.setUUID(recreateUUID);
        return routinePermsDescriptor;
    }
    
    public SystemColumn[] buildColumnList() throws StandardException {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("ROUTINEPERMSID", false), SystemColumnImpl.getIdentifierColumn("GRANTEE", false), SystemColumnImpl.getIdentifierColumn("GRANTOR", false), SystemColumnImpl.getUUIDColumn("ALIASID", false), SystemColumnImpl.getIndicatorColumn("GRANTOPTION") };
    }
    
    public ExecIndexRow buildIndexKeyRow(final int n, final PermissionsDescriptor permissionsDescriptor) throws StandardException {
        Row row = null;
        switch (n) {
            case 0: {
                row = this.getExecutionFactory().getIndexableRow(2);
                row.setColumn(1, this.getAuthorizationID(permissionsDescriptor.getGrantee()));
                row.setColumn(2, new SQLChar(((RoutinePermsDescriptor)permissionsDescriptor).getRoutineUUID().toString()));
                break;
            }
            case 1: {
                row = this.getExecutionFactory().getIndexableRow(1);
                row.setColumn(1, new SQLChar(permissionsDescriptor.getObjectID().toString()));
                break;
            }
            case 2: {
                row = this.getExecutionFactory().getIndexableRow(1);
                row.setColumn(1, new SQLChar(((RoutinePermsDescriptor)permissionsDescriptor).getRoutineUUID().toString()));
                break;
            }
        }
        return (ExecIndexRow)row;
    }
    
    public int getPrimaryKeyIndexNumber() {
        return 0;
    }
    
    public int orPermissions(final ExecRow execRow, final PermissionsDescriptor permissionsDescriptor, final boolean[] array) throws StandardException {
        return 0;
    }
    
    public int removePermissions(final ExecRow execRow, final PermissionsDescriptor permissionsDescriptor, final boolean[] array) throws StandardException {
        return -1;
    }
    
    public void setUUIDOfThePassedDescriptor(final ExecRow execRow, final PermissionsDescriptor permissionsDescriptor) throws StandardException {
        permissionsDescriptor.setUUID(this.getUUIDFactory().recreateUUID(execRow.getColumn(1).getString()));
    }
    
    static {
        indexColumnPositions = new int[][] { { 2, 4, 3 }, { 1 }, { 4 } };
        indexUniqueness = new boolean[] { true, true, false };
        uuids = new String[] { "2057c01b-0103-0e39-b8e7-00000010f010", "185e801c-0103-0e39-b8e7-00000010f010", "c065801d-0103-0e39-b8e7-00000010f010", "40f70088-010c-4c2f-c8de-0000000f43a0", "08264012-010c-bc85-060d-000000109ab8" };
    }
}
