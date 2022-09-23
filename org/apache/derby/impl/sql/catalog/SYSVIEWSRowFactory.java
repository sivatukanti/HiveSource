// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import org.apache.derby.iapi.sql.dictionary.DataDescriptorGenerator;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.SQLChar;
import org.apache.derby.iapi.sql.dictionary.ViewDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.sql.dictionary.CatalogRowFactory;

public class SYSVIEWSRowFactory extends CatalogRowFactory
{
    private static final String TABLENAME_STRING = "SYSVIEWS";
    protected static final int SYSVIEWS_COLUMN_COUNT = 4;
    protected static final int SYSVIEWS_TABLEID = 1;
    protected static final int SYSVIEWS_VIEWDEFINITION = 2;
    protected static final int SYSVIEWS_CHECKOPTION = 3;
    protected static final int SYSVIEWS_COMPILATION_SCHEMAID = 4;
    protected static final int SYSVIEWS_TABLEID_WIDTH = 36;
    protected static final int SYSVIEWS_INDEX1_ID = 0;
    private static final int[][] indexColumnPositions;
    private static final boolean[] uniqueness;
    private static final String[] uuids;
    
    SYSVIEWSRowFactory(final UUIDFactory uuidFactory, final ExecutionFactory executionFactory, final DataValueFactory dataValueFactory) {
        super(uuidFactory, executionFactory, dataValueFactory);
        this.initInfo(4, "SYSVIEWS", SYSVIEWSRowFactory.indexColumnPositions, SYSVIEWSRowFactory.uniqueness, SYSVIEWSRowFactory.uuids);
    }
    
    public ExecRow makeRow(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2) throws StandardException {
        String string = null;
        String s = null;
        String viewText = null;
        String s2 = null;
        if (tupleDescriptor != null) {
            final ViewDescriptor viewDescriptor = (ViewDescriptor)tupleDescriptor;
            UUID uuid = viewDescriptor.getUUID();
            if (uuid == null) {
                uuid = this.getUUIDFactory().createUUID();
                viewDescriptor.setUUID(uuid);
            }
            string = uuid.toString();
            viewText = viewDescriptor.getViewText();
            viewDescriptor.getCheckOptionType();
            s2 = "N";
            final UUID compSchemaId = viewDescriptor.getCompSchemaId();
            s = ((compSchemaId == null) ? null : compSchemaId.toString());
        }
        final ExecRow valueRow = this.getExecutionFactory().getValueRow(4);
        valueRow.setColumn(1, new SQLChar(string));
        valueRow.setColumn(2, this.dvf.getLongvarcharDataValue(viewText));
        valueRow.setColumn(3, new SQLChar(s2));
        valueRow.setColumn(4, new SQLChar(s));
        return valueRow;
    }
    
    public TupleDescriptor buildDescriptor(final ExecRow execRow, final TupleDescriptor tupleDescriptor, final DataDictionary dataDictionary) throws StandardException {
        UUID recreateUUID = null;
        final DataDescriptorGenerator dataDescriptorGenerator = dataDictionary.getDataDescriptorGenerator();
        final UUID recreateUUID2 = this.getUUIDFactory().recreateUUID(execRow.getColumn(1).getString());
        final String string = execRow.getColumn(2).getString();
        execRow.getColumn(3).getString();
        final int n = 0;
        final String string2 = execRow.getColumn(4).getString();
        if (string2 != null) {
            recreateUUID = this.getUUIDFactory().recreateUUID(string2);
        }
        return dataDescriptorGenerator.newViewDescriptor(recreateUUID2, null, string, n, recreateUUID);
    }
    
    public SystemColumn[] buildColumnList() throws StandardException {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("TABLEID", false), SystemColumnImpl.getColumn("VIEWDEFINITION", -1, false, 32700), SystemColumnImpl.getIndicatorColumn("CHECKOPTION"), SystemColumnImpl.getUUIDColumn("COMPILATIONSCHEMAID", true) };
    }
    
    static {
        indexColumnPositions = new int[][] { { 1 } };
        uniqueness = null;
        uuids = new String[] { "8000004d-00d0-fd77-3ed8-000a0a0b1900", "80000050-00d0-fd77-3ed8-000a0a0b1900", "8000004f-00d0-fd77-3ed8-000a0a0b1900" };
    }
}
