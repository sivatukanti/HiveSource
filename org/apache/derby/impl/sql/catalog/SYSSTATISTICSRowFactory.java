// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import org.apache.derby.catalog.UUID;
import org.apache.derby.catalog.Statistics;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;
import java.sql.Timestamp;
import org.apache.derby.iapi.types.UserType;
import org.apache.derby.iapi.types.SQLInteger;
import org.apache.derby.iapi.types.SQLBoolean;
import org.apache.derby.iapi.types.SQLTimestamp;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.SQLChar;
import org.apache.derby.iapi.sql.dictionary.StatisticsDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.sql.dictionary.CatalogRowFactory;

public class SYSSTATISTICSRowFactory extends CatalogRowFactory
{
    static final String TABLENAME_STRING = "SYSSTATISTICS";
    protected static final int SYSSTATISTICS_ID = 1;
    protected static final int SYSSTATISTICS_REFERENCEID = 2;
    protected static final int SYSSTATISTICS_TABLEID = 3;
    protected static final int SYSSTATISTICS_TIMESTAMP = 4;
    protected static final int SYSSTATISTICS_TYPE = 5;
    protected static final int SYSSTATISTICS_VALID = 6;
    protected static final int SYSSTATISTICS_COLCOUNT = 7;
    protected static final int SYSSTATISTICS_STAT = 8;
    protected static final int SYSSTATISTICS_COLUMN_COUNT = 8;
    protected static final int SYSSTATISTICS_INDEX1_ID = 0;
    private static final boolean[] uniqueness;
    private static final int[][] indexColumnPositions;
    private static final String[] uuids;
    
    SYSSTATISTICSRowFactory(final UUIDFactory uuidFactory, final ExecutionFactory executionFactory, final DataValueFactory dataValueFactory) {
        super(uuidFactory, executionFactory, dataValueFactory);
        this.initInfo(8, "SYSSTATISTICS", SYSSTATISTICSRowFactory.indexColumnPositions, SYSSTATISTICSRowFactory.uniqueness, SYSSTATISTICSRowFactory.uuids);
    }
    
    public ExecRow makeRow(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2) throws StandardException {
        String string = null;
        String string2 = null;
        String string3 = null;
        String statType = null;
        Timestamp updateTimestamp = null;
        int columnCount = 0;
        Object statistic = null;
        boolean valid = false;
        final ExecRow valueRow = this.getExecutionFactory().getValueRow(8);
        if (tupleDescriptor != null) {
            final StatisticsDescriptor statisticsDescriptor = (StatisticsDescriptor)tupleDescriptor;
            string = statisticsDescriptor.getUUID().toString();
            string3 = statisticsDescriptor.getTableUUID().toString();
            string2 = statisticsDescriptor.getReferenceID().toString();
            updateTimestamp = statisticsDescriptor.getUpdateTimestamp();
            statType = statisticsDescriptor.getStatType();
            valid = statisticsDescriptor.isValid();
            statistic = statisticsDescriptor.getStatistic();
            columnCount = statisticsDescriptor.getColumnCount();
        }
        valueRow.setColumn(1, new SQLChar(string));
        valueRow.setColumn(2, new SQLChar(string2));
        valueRow.setColumn(3, new SQLChar(string3));
        valueRow.setColumn(4, new SQLTimestamp(updateTimestamp));
        valueRow.setColumn(5, new SQLChar(statType));
        valueRow.setColumn(6, new SQLBoolean(valid));
        valueRow.setColumn(7, new SQLInteger(columnCount));
        valueRow.setColumn(8, new UserType(statistic));
        return valueRow;
    }
    
    public TupleDescriptor buildDescriptor(final ExecRow execRow, final TupleDescriptor tupleDescriptor, final DataDictionary dataDictionary) throws StandardException {
        final UUIDFactory uuidFactory = this.getUUIDFactory();
        final UUID recreateUUID = uuidFactory.recreateUUID(execRow.getColumn(1).getString());
        final UUID recreateUUID2 = uuidFactory.recreateUUID(execRow.getColumn(2).getString());
        final UUID recreateUUID3 = uuidFactory.recreateUUID(execRow.getColumn(3).getString());
        final Timestamp timestamp = (Timestamp)execRow.getColumn(4).getObject();
        final String string = execRow.getColumn(5).getString();
        execRow.getColumn(6).getBoolean();
        return new StatisticsDescriptor(dataDictionary, recreateUUID, recreateUUID2, recreateUUID3, string, (Statistics)execRow.getColumn(8).getObject(), execRow.getColumn(7).getInt());
    }
    
    public SystemColumn[] buildColumnList() throws StandardException {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("STATID", false), SystemColumnImpl.getUUIDColumn("REFERENCEID", false), SystemColumnImpl.getUUIDColumn("TABLEID", false), SystemColumnImpl.getColumn("CREATIONTIMESTAMP", 93, false), SystemColumnImpl.getIndicatorColumn("TYPE"), SystemColumnImpl.getColumn("VALID", 16, false), SystemColumnImpl.getColumn("COLCOUNT", 4, false), SystemColumnImpl.getJavaColumn("STATISTICS", "org.apache.derby.catalog.Statistics", false) };
    }
    
    static {
        uniqueness = new boolean[] { false };
        indexColumnPositions = new int[][] { { 3, 2 } };
        uuids = new String[] { "f81e0010-00e3-6612-5a96-009e3a3b5e00", "08264012-00e3-6612-5a96-009e3a3b5e00", "c013800d-00e3-ffbe-37c6-009e3a3b5e00" };
    }
}
