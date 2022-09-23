// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.types.SQLVarchar;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.SQLChar;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.sql.dictionary.CatalogRowFactory;

public class SYSSCHEMASRowFactory extends CatalogRowFactory
{
    private static final String TABLENAME_STRING = "SYSSCHEMAS";
    public static final int SYSSCHEMAS_COLUMN_COUNT = 3;
    public static final int SYSSCHEMAS_SCHEMAID = 1;
    public static final int SYSSCHEMAS_SCHEMANAME = 2;
    public static final int SYSSCHEMAS_SCHEMAAID = 3;
    protected static final int SYSSCHEMAS_INDEX1_ID = 0;
    protected static final int SYSSCHEMAS_INDEX2_ID = 1;
    private static final int[][] indexColumnPositions;
    private static final boolean[] uniqueness;
    private static final String[] uuids;
    
    SYSSCHEMASRowFactory(final UUIDFactory uuidFactory, final ExecutionFactory executionFactory, final DataValueFactory dataValueFactory) {
        super(uuidFactory, executionFactory, dataValueFactory);
        this.initInfo(3, "SYSSCHEMAS", SYSSCHEMASRowFactory.indexColumnPositions, SYSSCHEMASRowFactory.uniqueness, SYSSCHEMASRowFactory.uuids);
    }
    
    public ExecRow makeRow(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2) throws StandardException {
        String schemaName = null;
        String string = null;
        String authorizationId = null;
        if (tupleDescriptor != null) {
            final SchemaDescriptor schemaDescriptor = (SchemaDescriptor)tupleDescriptor;
            schemaName = schemaDescriptor.getSchemaName();
            UUID uuid = schemaDescriptor.getUUID();
            if (uuid == null) {
                uuid = this.getUUIDFactory().createUUID();
                schemaDescriptor.setUUID(uuid);
            }
            string = uuid.toString();
            authorizationId = schemaDescriptor.getAuthorizationId();
        }
        final ExecRow valueRow = this.getExecutionFactory().getValueRow(3);
        valueRow.setColumn(1, new SQLChar(string));
        valueRow.setColumn(2, new SQLVarchar(schemaName));
        valueRow.setColumn(3, new SQLVarchar(authorizationId));
        return valueRow;
    }
    
    public TupleDescriptor buildDescriptor(final ExecRow execRow, final TupleDescriptor tupleDescriptor, final DataDictionary dataDictionary) throws StandardException {
        return dataDictionary.getDataDescriptorGenerator().newSchemaDescriptor(execRow.getColumn(2).getString(), execRow.getColumn(3).getString(), this.getUUIDFactory().recreateUUID(execRow.getColumn(1).getString()));
    }
    
    public SystemColumn[] buildColumnList() throws StandardException {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("SCHEMAID", false), SystemColumnImpl.getIdentifierColumn("SCHEMANAME", false), SystemColumnImpl.getIdentifierColumn("AUTHORIZATIONID", false) };
    }
    
    static {
        indexColumnPositions = new int[][] { { 2 }, { 1 } };
        uniqueness = null;
        uuids = new String[] { "80000022-00d0-fd77-3ed8-000a0a0b1900", "8000002a-00d0-fd77-3ed8-000a0a0b1900", "80000024-00d0-fd77-3ed8-000a0a0b1900", "80000026-00d0-fd77-3ed8-000a0a0b1900" };
    }
}
