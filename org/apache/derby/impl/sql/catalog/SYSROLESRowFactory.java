// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.SQLVarchar;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.SQLChar;
import org.apache.derby.iapi.sql.dictionary.RoleGrantDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.sql.dictionary.CatalogRowFactory;

public class SYSROLESRowFactory extends CatalogRowFactory
{
    private static final String TABLENAME_STRING = "SYSROLES";
    private static final int SYSROLES_COLUMN_COUNT = 6;
    private static final int SYSROLES_ROLE_UUID = 1;
    private static final int SYSROLES_ROLEID = 2;
    private static final int SYSROLES_GRANTEE = 3;
    private static final int SYSROLES_GRANTOR = 4;
    private static final int SYSROLES_WITHADMINOPTION = 5;
    static final int SYSROLES_ISDEF = 6;
    private static final int[][] indexColumnPositions;
    static final int SYSROLES_ROLEID_COLPOS_IN_INDEX_ID_EE_OR = 1;
    static final int SYSROLES_GRANTEE_COLPOS_IN_INDEX_ID_EE_OR = 2;
    static final int SYSROLES_INDEX_ID_EE_OR_IDX = 0;
    static final int SYSROLES_INDEX_ID_DEF_IDX = 1;
    static final int SYSROLES_INDEX_UUID_IDX = 2;
    private static final boolean[] uniqueness;
    private static final String[] uuids;
    
    SYSROLESRowFactory(final UUIDFactory uuidFactory, final ExecutionFactory executionFactory, final DataValueFactory dataValueFactory) {
        super(uuidFactory, executionFactory, dataValueFactory);
        this.initInfo(6, "SYSROLES", SYSROLESRowFactory.indexColumnPositions, SYSROLESRowFactory.uniqueness, SYSROLESRowFactory.uuids);
    }
    
    public ExecRow makeRow(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2) throws StandardException {
        String string = null;
        String roleName = null;
        String grantee = null;
        String grantor = null;
        boolean withAdminOption = false;
        boolean def = false;
        if (tupleDescriptor != null) {
            final RoleGrantDescriptor roleGrantDescriptor = (RoleGrantDescriptor)tupleDescriptor;
            roleName = roleGrantDescriptor.getRoleName();
            grantee = roleGrantDescriptor.getGrantee();
            grantor = roleGrantDescriptor.getGrantor();
            withAdminOption = roleGrantDescriptor.isWithAdminOption();
            def = roleGrantDescriptor.isDef();
            string = roleGrantDescriptor.getUUID().toString();
        }
        final ExecRow valueRow = this.getExecutionFactory().getValueRow(6);
        valueRow.setColumn(1, new SQLChar(string));
        valueRow.setColumn(2, new SQLVarchar(roleName));
        valueRow.setColumn(3, new SQLVarchar(grantee));
        valueRow.setColumn(4, new SQLVarchar(grantor));
        valueRow.setColumn(5, new SQLChar(withAdminOption ? "Y" : "N"));
        valueRow.setColumn(6, new SQLChar(def ? "Y" : "N"));
        return valueRow;
    }
    
    public TupleDescriptor buildDescriptor(final ExecRow execRow, final TupleDescriptor tupleDescriptor, final DataDictionary dataDictionary) throws StandardException {
        return dataDictionary.getDataDescriptorGenerator().newRoleGrantDescriptor(this.getUUIDFactory().recreateUUID(execRow.getColumn(1).getString()), execRow.getColumn(2).getString(), execRow.getColumn(3).getString(), execRow.getColumn(4).getString(), execRow.getColumn(5).getString().equals("Y"), execRow.getColumn(6).getString().equals("Y"));
    }
    
    public SystemColumn[] buildColumnList() throws StandardException {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("UUID", false), SystemColumnImpl.getIdentifierColumn("ROLEID", false), SystemColumnImpl.getIdentifierColumn("GRANTEE", false), SystemColumnImpl.getIdentifierColumn("GRANTOR", false), SystemColumnImpl.getIndicatorColumn("WITHADMINOPTION"), SystemColumnImpl.getIndicatorColumn("ISDEF") };
    }
    
    static {
        indexColumnPositions = new int[][] { { 2, 3, 4 }, { 2, 6 }, { 1 } };
        uniqueness = new boolean[] { true, false, true };
        uuids = new String[] { "e03f4017-0115-382c-08df-ffffe275b270", "c851401a-0115-382c-08df-ffffe275b270", "c065801d-0115-382c-08df-ffffe275b270", "787c0020-0115-382c-08df-ffffe275b270", "629f8094-0116-d8f9-5f97-ffffe275b270" };
    }
}
