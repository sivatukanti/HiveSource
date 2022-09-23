// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import java.util.Properties;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.IndexRowGenerator;
import org.apache.derby.iapi.types.UserType;
import org.apache.derby.catalog.IndexDescriptor;
import org.apache.derby.iapi.types.SQLBoolean;
import org.apache.derby.iapi.types.SQLVarchar;
import org.apache.derby.iapi.types.SQLLongint;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.SQLChar;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.sql.dictionary.CatalogRowFactory;

public class SYSCONGLOMERATESRowFactory extends CatalogRowFactory
{
    private static final String TABLENAME_STRING = "SYSCONGLOMERATES";
    protected static final int SYSCONGLOMERATES_COLUMN_COUNT = 8;
    protected static final int SYSCONGLOMERATES_SCHEMAID = 1;
    protected static final int SYSCONGLOMERATES_TABLEID = 2;
    protected static final int SYSCONGLOMERATES_CONGLOMERATENUMBER = 3;
    protected static final int SYSCONGLOMERATES_CONGLOMERATENAME = 4;
    protected static final int SYSCONGLOMERATES_ISINDEX = 5;
    protected static final int SYSCONGLOMERATES_DESCRIPTOR = 6;
    protected static final int SYSCONGLOMERATES_ISCONSTRAINT = 7;
    protected static final int SYSCONGLOMERATES_CONGLOMERATEID = 8;
    protected static final int SYSCONGLOMERATES_INDEX1_ID = 0;
    protected static final int SYSCONGLOMERATES_INDEX2_ID = 1;
    protected static final int SYSCONGLOMERATES_INDEX3_ID = 2;
    private static final boolean[] uniqueness;
    private static final int[][] indexColumnPositions;
    private static final String[] uuids;
    
    SYSCONGLOMERATESRowFactory(final UUIDFactory uuidFactory, final ExecutionFactory executionFactory, final DataValueFactory dataValueFactory) {
        super(uuidFactory, executionFactory, dataValueFactory);
        this.initInfo(8, "SYSCONGLOMERATES", SYSCONGLOMERATESRowFactory.indexColumnPositions, SYSCONGLOMERATESRowFactory.uniqueness, SYSCONGLOMERATESRowFactory.uuids);
    }
    
    public ExecRow makeRow(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2) throws StandardException {
        String string = null;
        Long n = null;
        String conglomerateName = null;
        Boolean b = null;
        IndexRowGenerator indexDescriptor = null;
        Boolean b2 = null;
        String string2 = null;
        String s = null;
        final ConglomerateDescriptor conglomerateDescriptor = (ConglomerateDescriptor)tupleDescriptor;
        if (tupleDescriptor != null) {
            if (tupleDescriptor2 != null) {
                s = ((SchemaDescriptor)tupleDescriptor2).getUUID().toString();
            }
            else {
                s = conglomerateDescriptor.getSchemaID().toString();
            }
            string = conglomerateDescriptor.getTableID().toString();
            n = new Long(conglomerateDescriptor.getConglomerateNumber());
            conglomerateName = conglomerateDescriptor.getConglomerateName();
            string2 = conglomerateDescriptor.getUUID().toString();
            b = new Boolean(conglomerateDescriptor.isIndex());
            indexDescriptor = conglomerateDescriptor.getIndexDescriptor();
            b2 = new Boolean(conglomerateDescriptor.isConstraint());
        }
        final ExecRow valueRow = this.getExecutionFactory().getValueRow(8);
        valueRow.setColumn(1, new SQLChar(s));
        valueRow.setColumn(2, new SQLChar(string));
        valueRow.setColumn(3, new SQLLongint(n));
        valueRow.setColumn(4, (conglomerateName == null) ? new SQLVarchar(string) : new SQLVarchar(conglomerateName));
        valueRow.setColumn(5, new SQLBoolean(b));
        valueRow.setColumn(6, new UserType((indexDescriptor == null) ? null : indexDescriptor.getIndexDescriptor()));
        valueRow.setColumn(7, new SQLBoolean(b2));
        valueRow.setColumn(8, new SQLChar(string2));
        return valueRow;
    }
    
    public ExecRow makeEmptyRow() throws StandardException {
        return this.makeRow(null, null);
    }
    
    public Properties getCreateHeapProperties() {
        final Properties properties = new Properties();
        properties.put("derby.storage.pageSize", "4096");
        properties.put("derby.storage.pageReservedSpace", "0");
        properties.put("derby.storage.minimumRecordSize", "1");
        return properties;
    }
    
    public Properties getCreateIndexProperties(final int n) {
        final Properties properties = new Properties();
        properties.put("derby.storage.pageSize", "4096");
        return properties;
    }
    
    public TupleDescriptor buildDescriptor(final ExecRow execRow, final TupleDescriptor tupleDescriptor, final DataDictionary dataDictionary) throws StandardException {
        return dataDictionary.getDataDescriptorGenerator().newConglomerateDescriptor(execRow.getColumn(3).getLong(), execRow.getColumn(4).getString(), execRow.getColumn(5).getBoolean(), new IndexRowGenerator((IndexDescriptor)execRow.getColumn(6).getObject()), execRow.getColumn(7).getBoolean(), this.getUUIDFactory().recreateUUID(execRow.getColumn(8).getString()), this.getUUIDFactory().recreateUUID(execRow.getColumn(2).getString()), this.getUUIDFactory().recreateUUID(execRow.getColumn(1).getString()));
    }
    
    protected UUID getConglomerateUUID(final ExecRow execRow) throws StandardException {
        return this.getUUIDFactory().recreateUUID(execRow.getColumn(8).getString());
    }
    
    protected UUID getTableUUID(final ExecRow execRow) throws StandardException {
        return this.getUUIDFactory().recreateUUID(execRow.getColumn(2).getString());
    }
    
    protected UUID getSchemaUUID(final ExecRow execRow) throws StandardException {
        return this.getUUIDFactory().recreateUUID(execRow.getColumn(1).getString());
    }
    
    protected String getConglomerateName(final ExecRow execRow) throws StandardException {
        return execRow.getColumn(4).getString();
    }
    
    public SystemColumn[] buildColumnList() throws StandardException {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("SCHEMAID", false), SystemColumnImpl.getUUIDColumn("TABLEID", false), SystemColumnImpl.getColumn("CONGLOMERATENUMBER", -5, false), SystemColumnImpl.getIdentifierColumn("CONGLOMERATENAME", true), SystemColumnImpl.getColumn("ISINDEX", 16, false), SystemColumnImpl.getJavaColumn("DESCRIPTOR", "org.apache.derby.catalog.IndexDescriptor", true), SystemColumnImpl.getColumn("ISCONSTRAINT", 16, true), SystemColumnImpl.getUUIDColumn("CONGLOMERATEID", false) };
    }
    
    static {
        uniqueness = new boolean[] { false, true, false };
        indexColumnPositions = new int[][] { { 8 }, { 4, 1 }, { 2 } };
        uuids = new String[] { "80000010-00d0-fd77-3ed8-000a0a0b1900", "80000027-00d0-fd77-3ed8-000a0a0b1900", "80000012-00d0-fd77-3ed8-000a0a0b1900", "80000014-00d0-fd77-3ed8-000a0a0b1900", "80000016-00d0-fd77-3ed8-000a0a0b1900" };
    }
}
