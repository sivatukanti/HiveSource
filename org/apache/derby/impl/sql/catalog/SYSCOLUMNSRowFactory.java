// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import org.apache.derby.catalog.UUID;
import org.apache.derby.catalog.DefaultInfo;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.catalog.TypeDescriptor;
import org.apache.derby.catalog.types.DefaultInfoImpl;
import org.apache.derby.iapi.sql.dictionary.UniqueTupleDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import java.util.Properties;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.SQLLongint;
import org.apache.derby.iapi.types.UserType;
import org.apache.derby.iapi.types.SQLInteger;
import org.apache.derby.iapi.types.SQLVarchar;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.SQLChar;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.sql.dictionary.CatalogRowFactory;

public class SYSCOLUMNSRowFactory extends CatalogRowFactory
{
    static final String TABLENAME_STRING = "SYSCOLUMNS";
    protected static final int SYSCOLUMNS_COLUMN_COUNT = 9;
    protected static final int SYSCOLUMNS_TABLEID = 1;
    protected static final int SYSCOLUMNS_REFERENCEID = 1;
    protected static final int SYSCOLUMNS_COLUMNNAME = 2;
    protected static final int SYSCOLUMNS_COLUMNNUMBER = 3;
    protected static final int SYSCOLUMNS_COLUMNDATATYPE = 4;
    protected static final int SYSCOLUMNS_COLUMNDEFAULT = 5;
    protected static final int SYSCOLUMNS_COLUMNDEFAULTID = 6;
    protected static final int SYSCOLUMNS_AUTOINCREMENTVALUE = 7;
    protected static final int SYSCOLUMNS_AUTOINCREMENTSTART = 8;
    protected static final int SYSCOLUMNS_AUTOINCREMENTINC = 9;
    protected static final int SYSCOLUMNS_INDEX1_ID = 0;
    protected static final int SYSCOLUMNS_INDEX2_ID = 1;
    private static final boolean[] uniqueness;
    private static final String[] uuids;
    private static final int[][] indexColumnPositions;
    
    SYSCOLUMNSRowFactory(final UUIDFactory uuidFactory, final ExecutionFactory executionFactory, final DataValueFactory dataValueFactory) {
        this(uuidFactory, executionFactory, dataValueFactory, "SYSCOLUMNS");
    }
    
    SYSCOLUMNSRowFactory(final UUIDFactory uuidFactory, final ExecutionFactory executionFactory, final DataValueFactory dataValueFactory, final String s) {
        super(uuidFactory, executionFactory, dataValueFactory);
        this.initInfo(9, s, SYSCOLUMNSRowFactory.indexColumnPositions, SYSCOLUMNSRowFactory.uniqueness, SYSCOLUMNSRowFactory.uuids);
    }
    
    public ExecRow makeRow(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2) throws StandardException {
        String columnName = null;
        String string = null;
        String string2 = null;
        Integer n = null;
        Object catalogType = null;
        Object o = null;
        long autoincStart = 0L;
        long autoincInc = 0L;
        long autoincValue = 0L;
        long autoinc_create_or_modify_Start_Increment = -1L;
        if (tupleDescriptor != null) {
            final ColumnDescriptor columnDescriptor = (ColumnDescriptor)tupleDescriptor;
            catalogType = columnDescriptor.getType().getCatalogType();
            string2 = columnDescriptor.getReferencingUUID().toString();
            columnName = columnDescriptor.getColumnName();
            n = new Integer(columnDescriptor.getPosition());
            autoincStart = columnDescriptor.getAutoincStart();
            autoincInc = columnDescriptor.getAutoincInc();
            autoincValue = columnDescriptor.getAutoincValue();
            autoinc_create_or_modify_Start_Increment = columnDescriptor.getAutoinc_create_or_modify_Start_Increment();
            if (columnDescriptor.getDefaultInfo() != null) {
                o = columnDescriptor.getDefaultInfo();
            }
            else {
                o = columnDescriptor.getDefaultValue();
            }
            if (columnDescriptor.getDefaultUUID() != null) {
                string = columnDescriptor.getDefaultUUID().toString();
            }
        }
        final ExecRow valueRow = this.getExecutionFactory().getValueRow(9);
        valueRow.setColumn(1, new SQLChar(string2));
        valueRow.setColumn(2, new SQLVarchar(columnName));
        valueRow.setColumn(3, new SQLInteger(n));
        valueRow.setColumn(4, new UserType(catalogType));
        valueRow.setColumn(5, new UserType(o));
        valueRow.setColumn(6, new SQLChar(string));
        if (autoinc_create_or_modify_Start_Increment == 0L || autoinc_create_or_modify_Start_Increment == 2L) {
            valueRow.setColumn(7, new SQLLongint(autoincValue));
            valueRow.setColumn(8, new SQLLongint(autoincStart));
            valueRow.setColumn(9, new SQLLongint(autoincInc));
        }
        else if (autoinc_create_or_modify_Start_Increment == 1L) {
            final ColumnDescriptor columnDescriptor2 = (ColumnDescriptor)tupleDescriptor;
            valueRow.setColumn(7, new SQLLongint(autoincStart));
            valueRow.setColumn(8, new SQLLongint(autoincStart));
            valueRow.setColumn(9, new SQLLongint(columnDescriptor2.getTableDescriptor().getColumnDescriptor(columnName).getAutoincInc()));
        }
        else {
            valueRow.setColumn(7, new SQLLongint());
            valueRow.setColumn(8, new SQLLongint());
            valueRow.setColumn(9, new SQLLongint());
        }
        return valueRow;
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
        DefaultInfoImpl defaultInfoImpl = null;
        DataValueDescriptor defaultValue = null;
        UUID recreateUUID = null;
        final UUIDFactory uuidFactory = this.getUUIDFactory();
        dataDictionary.getDataDescriptorGenerator();
        UUID uuid;
        if (tupleDescriptor != null) {
            uuid = ((UniqueTupleDescriptor)tupleDescriptor).getUUID();
        }
        else {
            uuid = uuidFactory.recreateUUID(execRow.getColumn(1).getString());
        }
        final Object object = execRow.getColumn(5).getObject();
        if (object instanceof DataValueDescriptor) {
            defaultValue = (DataValueDescriptor)object;
        }
        else if (object instanceof DefaultInfoImpl) {
            defaultInfoImpl = (DefaultInfoImpl)object;
            defaultValue = defaultInfoImpl.getDefaultValue();
        }
        final String string = execRow.getColumn(6).getString();
        if (string != null) {
            recreateUUID = uuidFactory.recreateUUID(string);
        }
        final String string2 = execRow.getColumn(2).getString();
        final int int1 = execRow.getColumn(3).getInt();
        final DataTypeDescriptor type = DataTypeDescriptor.getType((TypeDescriptor)execRow.getColumn(4).getObject());
        final long long1 = execRow.getColumn(7).getLong();
        execRow.getColumn(8).getLong();
        execRow.getColumn(9).getLong();
        return new ColumnDescriptor(string2, int1, type, defaultValue, defaultInfoImpl, uuid, recreateUUID, execRow.getColumn(8).getLong(), execRow.getColumn(9).getLong(), long1);
    }
    
    public int getPrimaryKeyIndexNumber() {
        return 0;
    }
    
    public SystemColumn[] buildColumnList() throws StandardException {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("REFERENCEID", false), SystemColumnImpl.getIdentifierColumn("COLUMNNAME", false), SystemColumnImpl.getColumn("COLUMNNUMBER", 4, false), SystemColumnImpl.getJavaColumn("COLUMNDATATYPE", "org.apache.derby.catalog.TypeDescriptor", false), SystemColumnImpl.getJavaColumn("COLUMNDEFAULT", "java.io.Serializable", true), SystemColumnImpl.getUUIDColumn("COLUMNDEFAULTID", true), SystemColumnImpl.getColumn("AUTOINCREMENTVALUE", -5, true), SystemColumnImpl.getColumn("AUTOINCREMENTSTART", -5, true), SystemColumnImpl.getColumn("AUTOINCREMENTINC", -5, true) };
    }
    
    static {
        uniqueness = new boolean[] { true, false };
        uuids = new String[] { "8000001e-00d0-fd77-3ed8-000a0a0b1900", "80000029-00d0-fd77-3ed8-000a0a0b1900", "80000020-00d0-fd77-3ed8-000a0a0b1900", "6839c016-00d9-2829-dfcd-000a0a411400" };
        indexColumnPositions = new int[][] { { 1, 2 }, { 6 } };
    }
}
