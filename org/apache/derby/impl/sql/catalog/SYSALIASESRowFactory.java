// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import org.apache.derby.catalog.UUID;
import org.apache.derby.catalog.AliasInfo;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.UserType;
import org.apache.derby.iapi.types.SQLBoolean;
import org.apache.derby.iapi.types.SQLVarchar;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.SQLChar;
import org.apache.derby.iapi.sql.dictionary.AliasDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.sql.dictionary.CatalogRowFactory;

class SYSALIASESRowFactory extends CatalogRowFactory
{
    private static final int SYSALIASES_COLUMN_COUNT = 9;
    private static final int SYSALIASES_ALIASID = 1;
    private static final int SYSALIASES_ALIAS = 2;
    private static final int SYSALIASES_SCHEMAID = 3;
    private static final int SYSALIASES_JAVACLASSNAME = 4;
    private static final int SYSALIASES_ALIASTYPE = 5;
    private static final int SYSALIASES_NAMESPACE = 6;
    private static final int SYSALIASES_SYSTEMALIAS = 7;
    public static final int SYSALIASES_ALIASINFO = 8;
    private static final int SYSALIASES_SPECIFIC_NAME = 9;
    protected static final int SYSALIASES_INDEX1_ID = 0;
    protected static final int SYSALIASES_INDEX2_ID = 1;
    protected static final int SYSALIASES_INDEX3_ID = 2;
    private static final boolean[] uniqueness;
    private static int[][] indexColumnPositions;
    private static final String[] uuids;
    
    SYSALIASESRowFactory(final UUIDFactory uuidFactory, final ExecutionFactory executionFactory, final DataValueFactory dataValueFactory) {
        super(uuidFactory, executionFactory, dataValueFactory);
        this.initInfo(9, "SYSALIASES", SYSALIASESRowFactory.indexColumnPositions, SYSALIASESRowFactory.uniqueness, SYSALIASESRowFactory.uuids);
    }
    
    public ExecRow makeRow(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2) throws StandardException {
        String string = null;
        String javaClassName = null;
        String s = null;
        String string2 = null;
        String descriptorName = null;
        String specificName = null;
        char nameSpace = 'P';
        boolean systemAlias = false;
        Object aliasInfo = null;
        if (tupleDescriptor != null) {
            final AliasDescriptor aliasDescriptor = (AliasDescriptor)tupleDescriptor;
            string2 = aliasDescriptor.getUUID().toString();
            descriptorName = aliasDescriptor.getDescriptorName();
            string = aliasDescriptor.getSchemaUUID().toString();
            javaClassName = aliasDescriptor.getJavaClassName();
            final char aliasType = aliasDescriptor.getAliasType();
            nameSpace = aliasDescriptor.getNameSpace();
            systemAlias = aliasDescriptor.getSystemAlias();
            aliasInfo = aliasDescriptor.getAliasInfo();
            specificName = aliasDescriptor.getSpecificName();
            s = new String(new char[] { aliasType });
        }
        final ExecRow valueRow = this.getExecutionFactory().getValueRow(9);
        valueRow.setColumn(1, new SQLChar(string2));
        valueRow.setColumn(2, new SQLVarchar(descriptorName));
        valueRow.setColumn(3, new SQLChar(string));
        valueRow.setColumn(4, this.dvf.getLongvarcharDataValue(javaClassName));
        valueRow.setColumn(5, new SQLChar(s));
        valueRow.setColumn(6, new SQLChar(new String(new char[] { nameSpace })));
        valueRow.setColumn(7, new SQLBoolean(systemAlias));
        valueRow.setColumn(8, new UserType(aliasInfo));
        valueRow.setColumn(9, new SQLVarchar(specificName));
        return valueRow;
    }
    
    public TupleDescriptor buildDescriptor(final ExecRow execRow, final TupleDescriptor tupleDescriptor, final DataDictionary dataDictionary) throws StandardException {
        final UUID recreateUUID = this.getUUIDFactory().recreateUUID(execRow.getColumn(1).getString());
        final String string = execRow.getColumn(2).getString();
        final DataValueDescriptor column = execRow.getColumn(3);
        return new AliasDescriptor(dataDictionary, recreateUUID, string, column.isNull() ? null : this.getUUIDFactory().recreateUUID(column.getString()), execRow.getColumn(4).getString(), execRow.getColumn(5).getString().charAt(0), execRow.getColumn(6).getString().charAt(0), execRow.getColumn(7).getBoolean(), (AliasInfo)execRow.getColumn(8).getObject(), execRow.getColumn(9).getString());
    }
    
    public SystemColumn[] buildColumnList() throws StandardException {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("ALIASID", false), SystemColumnImpl.getIdentifierColumn("ALIAS", false), SystemColumnImpl.getUUIDColumn("SCHEMAID", true), SystemColumnImpl.getColumn("JAVACLASSNAME", -1, false, Integer.MAX_VALUE), SystemColumnImpl.getIndicatorColumn("ALIASTYPE"), SystemColumnImpl.getIndicatorColumn("NAMESPACE"), SystemColumnImpl.getColumn("SYSTEMALIAS", 16, false), SystemColumnImpl.getJavaColumn("ALIASINFO", "org.apache.derby.catalog.AliasInfo", true), SystemColumnImpl.getIdentifierColumn("SPECIFICNAME", false) };
    }
    
    static {
        uniqueness = null;
        SYSALIASESRowFactory.indexColumnPositions = new int[][] { { 3, 2, 6 }, { 1 }, { 3, 9 } };
        uuids = new String[] { "c013800d-00d7-ddbd-08ce-000a0a411400", "c013800d-00d7-ddbd-75d4-000a0a411400", "c013800d-00d7-ddbe-b99d-000a0a411400", "c013800d-00d7-ddbe-c4e1-000a0a411400", "c013800d-00d7-ddbe-34ae-000a0a411400" };
    }
}
