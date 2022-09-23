// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import java.util.Properties;
import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import org.apache.derby.iapi.sql.execute.ExecPreparedStatement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.UUID;
import java.sql.Timestamp;
import org.apache.derby.iapi.types.UserType;
import org.apache.derby.iapi.types.SQLTimestamp;
import org.apache.derby.iapi.types.SQLBoolean;
import org.apache.derby.iapi.types.SQLVarchar;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.SQLChar;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.SPSDescriptor;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.sql.dictionary.CatalogRowFactory;

public class SYSSTATEMENTSRowFactory extends CatalogRowFactory
{
    static final String TABLENAME_STRING = "SYSSTATEMENTS";
    public static final int SYSSTATEMENTS_STMTID = 1;
    public static final int SYSSTATEMENTS_STMTNAME = 2;
    public static final int SYSSTATEMENTS_SCHEMAID = 3;
    public static final int SYSSTATEMENTS_TYPE = 4;
    public static final int SYSSTATEMENTS_VALID = 5;
    public static final int SYSSTATEMENTS_TEXT = 6;
    public static final int SYSSTATEMENTS_LASTCOMPILED = 7;
    public static final int SYSSTATEMENTS_COMPILATION_SCHEMAID = 8;
    public static final int SYSSTATEMENTS_USINGTEXT = 9;
    public static final int SYSSTATEMENTS_CONSTANTSTATE = 10;
    public static final int SYSSTATEMENTS_INITIALLY_COMPILABLE = 11;
    public static final int SYSSTATEMENTS_COLUMN_COUNT = 11;
    public static final int SYSSTATEMENTS_HIDDEN_COLUMN_COUNT = 2;
    protected static final int SYSSTATEMENTS_INDEX1_ID = 0;
    protected static final int SYSSTATEMENTS_INDEX2_ID = 1;
    private static final int[][] indexColumnPositions;
    private static final boolean[] uniqueness;
    private static final String[] uuids;
    
    SYSSTATEMENTSRowFactory(final UUIDFactory uuidFactory, final ExecutionFactory executionFactory, final DataValueFactory dataValueFactory) {
        super(uuidFactory, executionFactory, dataValueFactory);
        this.initInfo(11, "SYSSTATEMENTS", SYSSTATEMENTSRowFactory.indexColumnPositions, SYSSTATEMENTSRowFactory.uniqueness, SYSSTATEMENTSRowFactory.uuids);
    }
    
    public ExecRow makeSYSSTATEMENTSrow(final boolean b, final SPSDescriptor spsDescriptor) throws StandardException {
        String name = null;
        String string = null;
        String string2 = null;
        String s = null;
        String text = null;
        String usingText = null;
        Object preparedStatement = null;
        String typeAsString = null;
        boolean valid = true;
        Timestamp compileTime = null;
        boolean initiallyCompilable = true;
        if (spsDescriptor != null) {
            name = spsDescriptor.getName();
            final UUID uuid = spsDescriptor.getUUID();
            string2 = spsDescriptor.getSchemaDescriptor().getUUID().toString();
            string = uuid.toString();
            text = spsDescriptor.getText();
            valid = spsDescriptor.isValid();
            compileTime = spsDescriptor.getCompileTime();
            typeAsString = spsDescriptor.getTypeAsString();
            initiallyCompilable = spsDescriptor.initiallyCompilable();
            preparedStatement = spsDescriptor.getPreparedStatement(b);
            s = ((spsDescriptor.getCompSchemaId() != null) ? spsDescriptor.getCompSchemaId().toString() : null);
            usingText = spsDescriptor.getUsingText();
        }
        final ExecRow valueRow = this.getExecutionFactory().getValueRow(11);
        valueRow.setColumn(1, new SQLChar(string));
        valueRow.setColumn(2, new SQLVarchar(name));
        valueRow.setColumn(3, new SQLChar(string2));
        valueRow.setColumn(4, new SQLChar(typeAsString));
        valueRow.setColumn(5, new SQLBoolean(valid));
        valueRow.setColumn(6, this.dvf.getLongvarcharDataValue(text));
        valueRow.setColumn(7, new SQLTimestamp(compileTime));
        valueRow.setColumn(8, new SQLChar(s));
        valueRow.setColumn(9, this.dvf.getLongvarcharDataValue(usingText));
        valueRow.setColumn(10, new UserType(preparedStatement));
        valueRow.setColumn(11, new SQLBoolean(initiallyCompilable));
        return valueRow;
    }
    
    public TupleDescriptor buildDescriptor(final ExecRow execRow, final TupleDescriptor tupleDescriptor, final DataDictionary dataDictionary) throws StandardException {
        UUID recreateUUID = null;
        ExecPreparedStatement execPreparedStatement = null;
        dataDictionary.getDataDescriptorGenerator();
        final UUID recreateUUID2 = this.getUUIDFactory().recreateUUID(execRow.getColumn(1).getString());
        final String string = execRow.getColumn(2).getString();
        final UUID recreateUUID3 = this.getUUIDFactory().recreateUUID(execRow.getColumn(3).getString());
        final char char1 = execRow.getColumn(4).getString().charAt(0);
        final boolean b = !dataDictionary.isReadOnlyUpgrade() && execRow.getColumn(5).getBoolean();
        final String string2 = execRow.getColumn(6).getString();
        final Timestamp timestamp = execRow.getColumn(7).getTimestamp(new GregorianCalendar());
        final String string3 = execRow.getColumn(8).getString();
        if (string3 != null) {
            recreateUUID = this.getUUIDFactory().recreateUUID(string3);
        }
        final String string4 = execRow.getColumn(9).getString();
        if (b) {
            execPreparedStatement = (ExecPreparedStatement)execRow.getColumn(10).getObject();
        }
        final DataValueDescriptor column = execRow.getColumn(11);
        return new SPSDescriptor(dataDictionary, string, recreateUUID2, recreateUUID3, recreateUUID, char1, b, string2, string4, timestamp, execPreparedStatement, column.isNull() || column.getBoolean());
    }
    
    public ExecRow makeEmptyRow() throws StandardException {
        return this.makeSYSSTATEMENTSrow(false, null);
    }
    
    public SystemColumn[] buildColumnList() throws StandardException {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("STMTID", false), SystemColumnImpl.getIdentifierColumn("STMTNAME", false), SystemColumnImpl.getUUIDColumn("SCHEMAID", false), SystemColumnImpl.getIndicatorColumn("TYPE"), SystemColumnImpl.getColumn("VALID", 16, false), SystemColumnImpl.getColumn("TEXT", -1, false, 32700), SystemColumnImpl.getColumn("LASTCOMPILED", 93, true), SystemColumnImpl.getUUIDColumn("COMPILATIONSCHEMAID", true), SystemColumnImpl.getColumn("USINGTEXT", -1, true, 32700) };
    }
    
    public Properties getCreateHeapProperties() {
        final Properties properties = new Properties();
        properties.put("derby.storage.pageSize", "2048");
        properties.put("derby.storage.pageReservedSpace", "0");
        properties.put("derby.storage.minimumRecordSize", "1");
        return properties;
    }
    
    static {
        indexColumnPositions = new int[][] { { 1 }, { 2, 3 } };
        uniqueness = null;
        uuids = new String[] { "80000000-00d1-15f7-ab70-000a0a0b1500", "80000000-00d1-15fc-60b9-000a0a0b1500", "80000000-00d1-15fc-eda1-000a0a0b1500", "80000000-00d1-15fe-bdf8-000a0a0b1500" };
    }
}
