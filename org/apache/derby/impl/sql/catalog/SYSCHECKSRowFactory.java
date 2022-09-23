// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import org.apache.derby.iapi.sql.dictionary.SubCheckConstraintDescriptor;
import org.apache.derby.catalog.ReferencedColumns;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import org.apache.derby.iapi.types.UserType;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.SQLChar;
import org.apache.derby.iapi.sql.dictionary.CheckConstraintDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.sql.dictionary.CatalogRowFactory;

class SYSCHECKSRowFactory extends CatalogRowFactory
{
    private static final String TABLENAME_STRING = "SYSCHECKS";
    private static final int SYSCHECKS_COLUMN_COUNT = 3;
    private static final int SYSCHECKS_CONSTRAINTID = 1;
    private static final int SYSCHECKS_CHECKDEFINITION = 2;
    private static final int SYSCHECKS_REFERENCEDCOLUMNS = 3;
    static final int SYSCHECKS_INDEX1_ID = 0;
    private static final boolean[] uniqueness;
    private static final int[][] indexColumnPositions;
    private static final String[] uuids;
    
    SYSCHECKSRowFactory(final UUIDFactory uuidFactory, final ExecutionFactory executionFactory, final DataValueFactory dataValueFactory) {
        super(uuidFactory, executionFactory, dataValueFactory);
        this.initInfo(3, "SYSCHECKS", SYSCHECKSRowFactory.indexColumnPositions, SYSCHECKSRowFactory.uniqueness, SYSCHECKSRowFactory.uuids);
    }
    
    public ExecRow makeRow(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2) throws StandardException {
        Object referencedColumnsDescriptor = null;
        String constraintText = null;
        String string = null;
        if (tupleDescriptor != null) {
            final CheckConstraintDescriptor checkConstraintDescriptor = (CheckConstraintDescriptor)tupleDescriptor;
            string = checkConstraintDescriptor.getUUID().toString();
            constraintText = checkConstraintDescriptor.getConstraintText();
            referencedColumnsDescriptor = checkConstraintDescriptor.getReferencedColumnsDescriptor();
        }
        final ExecIndexRow indexableRow = this.getExecutionFactory().getIndexableRow(3);
        indexableRow.setColumn(1, new SQLChar(string));
        indexableRow.setColumn(2, this.dvf.getLongvarcharDataValue(constraintText));
        indexableRow.setColumn(3, new UserType(referencedColumnsDescriptor));
        return indexableRow;
    }
    
    public TupleDescriptor buildDescriptor(final ExecRow execRow, final TupleDescriptor tupleDescriptor, final DataDictionary dataDictionary) throws StandardException {
        dataDictionary.getDataDescriptorGenerator();
        return new SubCheckConstraintDescriptor(this.getUUIDFactory().recreateUUID(execRow.getColumn(1).getString()), execRow.getColumn(2).getString(), (ReferencedColumns)execRow.getColumn(3).getObject());
    }
    
    public SystemColumn[] buildColumnList() throws StandardException {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("CONSTRAINTID", false), SystemColumnImpl.getColumn("CHECKDEFINITION", -1, false), SystemColumnImpl.getJavaColumn("REFERENCEDCOLUMNS", "org.apache.derby.catalog.ReferencedColumns", false) };
    }
    
    static {
        uniqueness = null;
        indexColumnPositions = new int[][] { { 1 } };
        uuids = new String[] { "80000056-00d0-fd77-3ed8-000a0a0b1900", "80000059-00d0-fd77-3ed8-000a0a0b1900", "80000058-00d0-fd77-3ed8-000a0a0b1900" };
    }
}
