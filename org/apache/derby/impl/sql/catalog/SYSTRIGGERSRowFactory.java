// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.catalog.ReferencedColumns;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;
import java.sql.Timestamp;
import org.apache.derby.iapi.types.SQLBoolean;
import org.apache.derby.iapi.types.UserType;
import org.apache.derby.iapi.types.SQLTimestamp;
import org.apache.derby.iapi.types.SQLVarchar;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.SQLChar;
import org.apache.derby.catalog.types.ReferencedColumnsDescriptorImpl;
import org.apache.derby.iapi.sql.dictionary.TriggerDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.sql.dictionary.CatalogRowFactory;

public class SYSTRIGGERSRowFactory extends CatalogRowFactory
{
    static final String TABLENAME_STRING = "SYSTRIGGERS";
    public static final int SYSTRIGGERS_TRIGGERID = 1;
    public static final int SYSTRIGGERS_TRIGGERNAME = 2;
    public static final int SYSTRIGGERS_SCHEMAID = 3;
    public static final int SYSTRIGGERS_CREATIONTIMESTAMP = 4;
    public static final int SYSTRIGGERS_EVENT = 5;
    public static final int SYSTRIGGERS_FIRINGTIME = 6;
    public static final int SYSTRIGGERS_TYPE = 7;
    public static final int SYSTRIGGERS_STATE = 8;
    public static final int SYSTRIGGERS_TABLEID = 9;
    public static final int SYSTRIGGERS_WHENSTMTID = 10;
    public static final int SYSTRIGGERS_ACTIONSTMTID = 11;
    public static final int SYSTRIGGERS_REFERENCEDCOLUMNS = 12;
    public static final int SYSTRIGGERS_TRIGGERDEFINITION = 13;
    public static final int SYSTRIGGERS_REFERENCINGOLD = 14;
    public static final int SYSTRIGGERS_REFERENCINGNEW = 15;
    public static final int SYSTRIGGERS_OLDREFERENCINGNAME = 16;
    public static final int SYSTRIGGERS_NEWREFERENCINGNAME = 17;
    public static final int SYSTRIGGERS_COLUMN_COUNT = 17;
    public static final int SYSTRIGGERS_INDEX1_ID = 0;
    public static final int SYSTRIGGERS_INDEX2_ID = 1;
    public static final int SYSTRIGGERS_INDEX3_ID = 2;
    private static final int[][] indexColumnPositions;
    private static final boolean[] uniqueness;
    private static final String[] uuids;
    
    SYSTRIGGERSRowFactory(final UUIDFactory uuidFactory, final ExecutionFactory executionFactory, final DataValueFactory dataValueFactory) {
        super(uuidFactory, executionFactory, dataValueFactory);
        this.initInfo(17, "SYSTRIGGERS", SYSTRIGGERSRowFactory.indexColumnPositions, SYSTRIGGERSRowFactory.uniqueness, SYSTRIGGERSRowFactory.uuids);
    }
    
    public ExecRow makeRow(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2) throws StandardException {
        String name = null;
        Object uuid = null;
        Object uuid2 = null;
        Object uuid3 = null;
        Object actionId = null;
        Object whenClauseId = null;
        Timestamp creationTimestamp = null;
        String s = null;
        String s2 = null;
        String s3 = null;
        String s4 = null;
        String triggerDefinition = null;
        String oldReferencingName = null;
        String newReferencingName = null;
        Object o = null;
        boolean referencingOld = false;
        boolean referencingNew = false;
        if (tupleDescriptor != null) {
            final TriggerDescriptor triggerDescriptor = (TriggerDescriptor)tupleDescriptor;
            name = triggerDescriptor.getName();
            uuid = triggerDescriptor.getUUID();
            uuid2 = triggerDescriptor.getSchemaDescriptor().getUUID();
            creationTimestamp = triggerDescriptor.getCreationTimestamp();
            s = (triggerDescriptor.listensForEvent(1) ? "U" : (triggerDescriptor.listensForEvent(2) ? "D" : "I"));
            s2 = (triggerDescriptor.isBeforeTrigger() ? "B" : "A");
            s3 = (triggerDescriptor.isRowTrigger() ? "R" : "S");
            s4 = (triggerDescriptor.isEnabled() ? "E" : "D");
            uuid3 = triggerDescriptor.getTableDescriptor().getUUID();
            final int[] referencedCols = triggerDescriptor.getReferencedCols();
            final int[] referencedColsInTriggerAction = triggerDescriptor.getReferencedColsInTriggerAction();
            o = ((referencedCols != null || referencedColsInTriggerAction != null) ? new ReferencedColumnsDescriptorImpl(referencedCols, referencedColsInTriggerAction) : null);
            actionId = triggerDescriptor.getActionId();
            whenClauseId = triggerDescriptor.getWhenClauseId();
            triggerDefinition = triggerDescriptor.getTriggerDefinition();
            referencingOld = triggerDescriptor.getReferencingOld();
            referencingNew = triggerDescriptor.getReferencingNew();
            oldReferencingName = triggerDescriptor.getOldReferencingName();
            newReferencingName = triggerDescriptor.getNewReferencingName();
        }
        final ExecRow valueRow = this.getExecutionFactory().getValueRow(17);
        valueRow.setColumn(1, new SQLChar((uuid == null) ? null : uuid.toString()));
        valueRow.setColumn(2, new SQLVarchar(name));
        valueRow.setColumn(3, new SQLChar((uuid2 == null) ? null : uuid2.toString()));
        valueRow.setColumn(4, new SQLTimestamp(creationTimestamp));
        valueRow.setColumn(5, new SQLChar(s));
        valueRow.setColumn(6, new SQLChar(s2));
        valueRow.setColumn(7, new SQLChar(s3));
        valueRow.setColumn(8, new SQLChar(s4));
        valueRow.setColumn(9, new SQLChar((uuid3 == null) ? null : uuid3.toString()));
        valueRow.setColumn(10, new SQLChar((whenClauseId == null) ? null : whenClauseId.toString()));
        valueRow.setColumn(11, new SQLChar((actionId == null) ? null : actionId.toString()));
        valueRow.setColumn(12, new UserType(o));
        valueRow.setColumn(13, this.dvf.getLongvarcharDataValue(triggerDefinition));
        valueRow.setColumn(14, new SQLBoolean(referencingOld));
        valueRow.setColumn(15, new SQLBoolean(referencingNew));
        valueRow.setColumn(16, new SQLVarchar(oldReferencingName));
        valueRow.setColumn(17, new SQLVarchar(newReferencingName));
        return valueRow;
    }
    
    public TupleDescriptor buildDescriptor(final ExecRow execRow, final TupleDescriptor tupleDescriptor, final DataDictionary dataDictionary) throws StandardException {
        UUID recreateUUID = null;
        UUID recreateUUID2 = null;
        int n = 0;
        dataDictionary.getDataDescriptorGenerator();
        final UUID recreateUUID3 = this.getUUIDFactory().recreateUUID(execRow.getColumn(1).getString());
        final String string = execRow.getColumn(2).getString();
        final UUID recreateUUID4 = this.getUUIDFactory().recreateUUID(execRow.getColumn(3).getString());
        final Timestamp timestamp = (Timestamp)execRow.getColumn(4).getObject();
        switch (execRow.getColumn(5).getString().charAt(0)) {
            case 'U': {
                n = 1;
                break;
            }
            case 'I': {
                n = 4;
                break;
            }
            case 'D': {
                n = 2;
                break;
            }
        }
        final boolean charBoolean = this.getCharBoolean(execRow.getColumn(6), 'B', 'A');
        final boolean charBoolean2 = this.getCharBoolean(execRow.getColumn(7), 'R', 'S');
        final boolean charBoolean3 = this.getCharBoolean(execRow.getColumn(8), 'E', 'D');
        final UUID recreateUUID5 = this.getUUIDFactory().recreateUUID(execRow.getColumn(9).getString());
        final String string2 = execRow.getColumn(10).getString();
        if (string2 != null) {
            recreateUUID2 = this.getUUIDFactory().recreateUUID(string2);
        }
        final String string3 = execRow.getColumn(11).getString();
        if (string3 != null) {
            recreateUUID = this.getUUIDFactory().recreateUUID(string3);
        }
        final ReferencedColumns referencedColumns = (ReferencedColumns)execRow.getColumn(12).getObject();
        return new TriggerDescriptor(dataDictionary, dataDictionary.getSchemaDescriptor(recreateUUID4, null), recreateUUID3, string, n, charBoolean, charBoolean2, charBoolean3, dataDictionary.getTableDescriptor(recreateUUID5), recreateUUID2, recreateUUID, timestamp, (referencedColumns == null) ? ((int[])null) : referencedColumns.getReferencedColumnPositions(), (referencedColumns == null) ? ((int[])null) : referencedColumns.getTriggerActionReferencedColumnPositions(), execRow.getColumn(13).getString(), execRow.getColumn(14).getBoolean(), execRow.getColumn(15).getBoolean(), execRow.getColumn(16).getString(), execRow.getColumn(17).getString());
    }
    
    public SystemColumn[] buildColumnList() throws StandardException {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("TRIGGERID", false), SystemColumnImpl.getIdentifierColumn("TRIGGERNAME", false), SystemColumnImpl.getUUIDColumn("SCHEMAID", false), SystemColumnImpl.getColumn("CREATIONTIMESTAMP", 93, false), SystemColumnImpl.getIndicatorColumn("EVENT"), SystemColumnImpl.getIndicatorColumn("FIRINGTIME"), SystemColumnImpl.getIndicatorColumn("TYPE"), SystemColumnImpl.getIndicatorColumn("STATE"), SystemColumnImpl.getUUIDColumn("TABLEID", false), SystemColumnImpl.getUUIDColumn("WHENSTMTID", true), SystemColumnImpl.getUUIDColumn("ACTIONSTMTID", true), SystemColumnImpl.getJavaColumn("REFERENCEDCOLUMNS", "org.apache.derby.catalog.ReferencedColumns", true), SystemColumnImpl.getColumn("TRIGGERDEFINITION", -1, true, Integer.MAX_VALUE), SystemColumnImpl.getColumn("REFERENCINGOLD", 16, true), SystemColumnImpl.getColumn("REFERENCINGNEW", 16, true), SystemColumnImpl.getIdentifierColumn("OLDREFERENCINGNAME", true), SystemColumnImpl.getIdentifierColumn("NEWREFERENCINGNAME", true) };
    }
    
    private boolean getCharBoolean(final DataValueDescriptor dataValueDescriptor, final char c, final char c2) throws StandardException {
        final char char1 = dataValueDescriptor.getString().charAt(0);
        return char1 == c || char1 != c2;
    }
    
    static {
        indexColumnPositions = new int[][] { { 1 }, { 2, 3 }, { 9, 4 } };
        uniqueness = new boolean[] { true, true, false };
        uuids = new String[] { "c013800d-00d7-c025-4809-000a0a411200", "c013800d-00d7-c025-480a-000a0a411200", "c013800d-00d7-c025-480b-000a0a411200", "c013800d-00d7-c025-480c-000a0a411200", "c013800d-00d7-c025-480d-000a0a411200" };
    }
}
