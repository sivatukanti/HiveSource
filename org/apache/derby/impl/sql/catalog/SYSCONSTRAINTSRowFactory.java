// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDescriptorGenerator;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.SubCheckConstraintDescriptor;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.dictionary.SubKeyConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.SubConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.SQLInteger;
import org.apache.derby.iapi.types.SQLVarchar;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.SQLChar;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.sql.dictionary.CatalogRowFactory;

public class SYSCONSTRAINTSRowFactory extends CatalogRowFactory
{
    private static final String TABLENAME_STRING = "SYSCONSTRAINTS";
    protected static final int SYSCONSTRAINTS_COLUMN_COUNT = 7;
    protected static final int SYSCONSTRAINTS_CONSTRAINTID = 1;
    protected static final int SYSCONSTRAINTS_TABLEID = 2;
    protected static final int SYSCONSTRAINTS_CONSTRAINTNAME = 3;
    protected static final int SYSCONSTRAINTS_TYPE = 4;
    protected static final int SYSCONSTRAINTS_SCHEMAID = 5;
    protected static final int SYSCONSTRAINTS_STATE = 6;
    protected static final int SYSCONSTRAINTS_REFERENCECOUNT = 7;
    protected static final int SYSCONSTRAINTS_INDEX1_ID = 0;
    protected static final int SYSCONSTRAINTS_INDEX2_ID = 1;
    protected static final int SYSCONSTRAINTS_INDEX3_ID = 2;
    private static final boolean[] uniqueness;
    private static final int[][] indexColumnPositions;
    private static final String[] uuids;
    
    SYSCONSTRAINTSRowFactory(final UUIDFactory uuidFactory, final ExecutionFactory executionFactory, final DataValueFactory dataValueFactory) {
        super(uuidFactory, executionFactory, dataValueFactory);
        this.initInfo(7, "SYSCONSTRAINTS", SYSCONSTRAINTSRowFactory.indexColumnPositions, SYSCONSTRAINTSRowFactory.uniqueness, SYSCONSTRAINTSRowFactory.uuids);
    }
    
    public ExecRow makeRow(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2) throws StandardException {
        String s = null;
        String string = null;
        String string2 = null;
        String constraintName = null;
        String string3 = null;
        boolean enabled = true;
        int referenceCount = 0;
        if (tupleDescriptor != null) {
            final ConstraintDescriptor constraintDescriptor = (ConstraintDescriptor)tupleDescriptor;
            string = constraintDescriptor.getUUID().toString();
            string2 = constraintDescriptor.getTableId().toString();
            constraintName = constraintDescriptor.getConstraintName();
            switch (constraintDescriptor.getConstraintType()) {
                case 2: {
                    s = "P";
                    break;
                }
                case 3: {
                    s = "U";
                    break;
                }
                case 4: {
                    s = "C";
                    break;
                }
                case 6: {
                    s = "F";
                    break;
                }
            }
            string3 = constraintDescriptor.getSchemaDescriptor().getUUID().toString();
            enabled = constraintDescriptor.isEnabled();
            referenceCount = constraintDescriptor.getReferenceCount();
        }
        final ExecRow valueRow = this.getExecutionFactory().getValueRow(7);
        valueRow.setColumn(1, new SQLChar(string));
        valueRow.setColumn(2, new SQLChar(string2));
        valueRow.setColumn(3, new SQLVarchar(constraintName));
        valueRow.setColumn(4, new SQLChar(s));
        valueRow.setColumn(5, new SQLChar(string3));
        valueRow.setColumn(6, new SQLChar(enabled ? "E" : "D"));
        valueRow.setColumn(7, new SQLInteger(referenceCount));
        return valueRow;
    }
    
    public TupleDescriptor buildDescriptor(final ExecRow execRow, final TupleDescriptor tupleDescriptor, final DataDictionary dataDictionary) throws StandardException {
        TupleDescriptor tupleDescriptor2 = null;
        TableDescriptor tableDescriptor = null;
        int n = -1;
        int[] baseColumnPositions = null;
        UUID keyConstraintId = null;
        final SubConstraintDescriptor subConstraintDescriptor = (SubConstraintDescriptor)tupleDescriptor;
        final DataDescriptorGenerator dataDescriptorGenerator = dataDictionary.getDataDescriptorGenerator();
        final UUID recreateUUID = this.getUUIDFactory().recreateUUID(execRow.getColumn(1).getString());
        final UUID recreateUUID2 = this.getUUIDFactory().recreateUUID(execRow.getColumn(2).getString());
        if (subConstraintDescriptor != null) {
            tableDescriptor = subConstraintDescriptor.getTableDescriptor();
        }
        if (tableDescriptor == null) {
            tableDescriptor = dataDictionary.getTableDescriptor(recreateUUID2);
        }
        final String string = execRow.getColumn(3).getString();
        final String string2 = execRow.getColumn(4).getString();
        int n2 = 0;
        switch (string2.charAt(0)) {
            case 'P': {
                n = 2;
                n2 = 1;
            }
            case 'U': {
                if (n2 == 0) {
                    n = 3;
                    n2 = 1;
                }
            }
            case 'F': {
                if (n2 == 0) {
                    n = 6;
                }
                ConglomerateDescriptor conglomerateDescriptor = tableDescriptor.getConglomerateDescriptor(((SubKeyConstraintDescriptor)tupleDescriptor).getIndexId());
                if (conglomerateDescriptor == null) {
                    tableDescriptor = dataDictionary.getTableDescriptor(recreateUUID2);
                    if (subConstraintDescriptor != null) {
                        subConstraintDescriptor.setTableDescriptor(tableDescriptor);
                    }
                    conglomerateDescriptor = tableDescriptor.getConglomerateDescriptor(((SubKeyConstraintDescriptor)tupleDescriptor).getIndexId());
                }
                conglomerateDescriptor.getIndexDescriptor().baseColumnPositions();
                keyConstraintId = ((SubKeyConstraintDescriptor)tupleDescriptor).getKeyConstraintId();
                baseColumnPositions = conglomerateDescriptor.getIndexDescriptor().baseColumnPositions();
                break;
            }
            case 'C': {
                n = 4;
                break;
            }
        }
        final SchemaDescriptor schemaDescriptor = dataDictionary.getSchemaDescriptor(this.getUUIDFactory().recreateUUID(execRow.getColumn(5).getString()), null);
        boolean b = false;
        switch (execRow.getColumn(6).getString().charAt(0)) {
            case 'E': {
                b = true;
                break;
            }
            case 'D': {
                b = false;
                break;
            }
            default: {
                b = true;
                break;
            }
        }
        final int int1 = execRow.getColumn(7).getInt();
        switch (n) {
            case 2: {
                tupleDescriptor2 = dataDescriptorGenerator.newPrimaryKeyConstraintDescriptor(tableDescriptor, string, false, false, baseColumnPositions, recreateUUID, ((SubKeyConstraintDescriptor)tupleDescriptor).getIndexId(), schemaDescriptor, b, int1);
                break;
            }
            case 3: {
                tupleDescriptor2 = dataDescriptorGenerator.newUniqueConstraintDescriptor(tableDescriptor, string, false, false, baseColumnPositions, recreateUUID, ((SubKeyConstraintDescriptor)tupleDescriptor).getIndexId(), schemaDescriptor, b, int1);
                break;
            }
            case 6: {
                tupleDescriptor2 = dataDescriptorGenerator.newForeignKeyConstraintDescriptor(tableDescriptor, string, false, false, baseColumnPositions, recreateUUID, ((SubKeyConstraintDescriptor)tupleDescriptor).getIndexId(), schemaDescriptor, keyConstraintId, b, ((SubKeyConstraintDescriptor)tupleDescriptor).getRaDeleteRule(), ((SubKeyConstraintDescriptor)tupleDescriptor).getRaUpdateRule());
                break;
            }
            case 4: {
                tupleDescriptor2 = dataDescriptorGenerator.newCheckConstraintDescriptor(tableDescriptor, string, false, false, recreateUUID, ((SubCheckConstraintDescriptor)tupleDescriptor).getConstraintText(), ((SubCheckConstraintDescriptor)tupleDescriptor).getReferencedColumnsDescriptor(), schemaDescriptor, b);
                break;
            }
        }
        return tupleDescriptor2;
    }
    
    protected UUID getConstraintId(final ExecRow execRow) throws StandardException {
        return this.getUUIDFactory().recreateUUID(execRow.getColumn(1).getString());
    }
    
    protected String getConstraintName(final ExecRow execRow) throws StandardException {
        return execRow.getColumn(3).getString();
    }
    
    protected UUID getSchemaId(final ExecRow execRow) throws StandardException {
        return this.getUUIDFactory().recreateUUID(execRow.getColumn(5).getString());
    }
    
    protected UUID getTableId(final ExecRow execRow) throws StandardException {
        return this.getUUIDFactory().recreateUUID(execRow.getColumn(2).getString());
    }
    
    protected int getConstraintType(final ExecRow execRow) throws StandardException {
        int n = 0;
        switch (execRow.getColumn(4).getString().charAt(0)) {
            case 'P': {
                n = 2;
                break;
            }
            case 'U': {
                n = 3;
                break;
            }
            case 'C': {
                n = 4;
                break;
            }
            case 'F': {
                n = 6;
                break;
            }
            default: {
                n = -1;
                break;
            }
        }
        return n;
    }
    
    public SystemColumn[] buildColumnList() {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("CONSTRAINTID", false), SystemColumnImpl.getUUIDColumn("TABLEID", false), SystemColumnImpl.getIdentifierColumn("CONSTRAINTNAME", false), SystemColumnImpl.getIndicatorColumn("TYPE"), SystemColumnImpl.getUUIDColumn("SCHEMAID", false), SystemColumnImpl.getIndicatorColumn("STATE"), SystemColumnImpl.getColumn("REFERENCECOUNT", 4, false) };
    }
    
    static {
        uniqueness = new boolean[] { true, true, false };
        indexColumnPositions = new int[][] { { 1 }, { 3, 5 }, { 2 } };
        uuids = new String[] { "8000002f-00d0-fd77-3ed8-000a0a0b1900", "80000036-00d0-fd77-3ed8-000a0a0b1900", "80000031-00d0-fd77-3ed8-000a0a0b1900", "80000033-00d0-fd77-3ed8-000a0a0b1900", "80000035-00d0-fd77-3ed8-000a0a0b1900" };
    }
}
