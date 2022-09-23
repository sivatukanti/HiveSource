// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.sql.ResultSet;
import org.apache.derby.catalog.DefaultInfo;
import org.apache.derby.iapi.util.IdUtil;
import org.apache.derby.iapi.store.access.RowUtil;
import org.apache.derby.iapi.services.io.StreamStorable;
import org.apache.derby.iapi.store.access.SortObserver;
import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.catalog.IndexDescriptor;
import org.apache.derby.iapi.sql.dictionary.IndexLister;
import org.apache.derby.catalog.Statistics;
import org.apache.derby.iapi.sql.dictionary.StatisticsDescriptor;
import org.apache.derby.catalog.types.StatisticsImpl;
import java.util.Properties;
import org.apache.derby.iapi.sql.dictionary.DefaultDescriptor;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.sql.dictionary.SPSDescriptor;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.sql.compile.Visitable;
import java.util.Arrays;
import org.apache.derby.impl.sql.compile.StatementNode;
import java.util.ListIterator;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptorList;
import java.util.Iterator;
import org.apache.derby.iapi.sql.dictionary.DependencyDescriptor;
import java.util.List;
import org.apache.derby.iapi.sql.dictionary.ReferencedKeyConstraintDescriptor;
import org.apache.derby.catalog.ReferencedColumns;
import org.apache.derby.catalog.types.ReferencedColumnsDescriptorImpl;
import org.apache.derby.iapi.sql.dictionary.CheckConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.TriggerDescriptor;
import org.apache.derby.iapi.sql.depend.Dependent;
import java.util.ArrayList;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.services.io.Storable;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptorList;
import org.apache.derby.iapi.store.access.ScanController;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.store.access.ColumnOrdering;
import org.apache.derby.iapi.store.access.SortController;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.dictionary.IndexRowGenerator;
import org.apache.derby.iapi.store.access.GroupFetchScanController;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.store.access.RowLocationRetRowSource;

class AlterTableConstantAction extends DDLSingleTableConstantAction implements RowLocationRetRowSource
{
    private SchemaDescriptor sd;
    private String tableName;
    private UUID schemaId;
    private int tableType;
    private ColumnInfo[] columnInfo;
    private ConstraintConstantAction[] constraintActions;
    private char lockGranularity;
    private long tableConglomerateId;
    private boolean compressTable;
    private int behavior;
    private boolean sequential;
    private boolean truncateTable;
    private boolean purge;
    private boolean defragment;
    private boolean truncateEndOfTable;
    private boolean updateStatistics;
    private boolean updateStatisticsAll;
    private boolean dropStatistics;
    private boolean dropStatisticsAll;
    private String indexNameForStatistics;
    private boolean doneScan;
    private boolean[] needToDropSort;
    private boolean[] validRow;
    private int bulkFetchSize;
    private int currentCompressRow;
    private int numIndexes;
    private int rowCount;
    private long estimatedRowCount;
    private long[] indexConglomerateNumbers;
    private long[] sortIds;
    private FormatableBitSet indexedCols;
    private ConglomerateController compressHeapCC;
    private ExecIndexRow[] indexRows;
    private ExecRow[] baseRow;
    private ExecRow currentRow;
    private GroupFetchScanController compressHeapGSC;
    private IndexRowGenerator[] compressIRGs;
    private DataValueDescriptor[][] baseRowArray;
    private RowLocation[] compressRL;
    private SortController[] sorters;
    private int droppedColumnPosition;
    private ColumnOrdering[][] ordering;
    private int[][] collation;
    private TableDescriptor td;
    private LanguageConnectionContext lcc;
    private DataDictionary dd;
    private DependencyManager dm;
    private TransactionController tc;
    private Activation activation;
    
    AlterTableConstantAction(final SchemaDescriptor sd, final String tableName, final UUID uuid, final long tableConglomerateId, final int tableType, final ColumnInfo[] columnInfo, final ConstraintConstantAction[] constraintActions, final char lockGranularity, final boolean compressTable, final int behavior, final boolean sequential, final boolean truncateTable, final boolean purge, final boolean defragment, final boolean truncateEndOfTable, final boolean updateStatistics, final boolean updateStatisticsAll, final boolean dropStatistics, final boolean dropStatisticsAll, final String indexNameForStatistics) {
        super(uuid);
        this.bulkFetchSize = 16;
        this.sd = sd;
        this.tableName = tableName;
        this.tableConglomerateId = tableConglomerateId;
        this.tableType = tableType;
        this.columnInfo = columnInfo;
        this.constraintActions = constraintActions;
        this.lockGranularity = lockGranularity;
        this.compressTable = compressTable;
        this.behavior = behavior;
        this.sequential = sequential;
        this.truncateTable = truncateTable;
        this.purge = purge;
        this.defragment = defragment;
        this.truncateEndOfTable = truncateEndOfTable;
        this.updateStatistics = updateStatistics;
        this.updateStatisticsAll = updateStatisticsAll;
        this.dropStatistics = dropStatistics;
        this.dropStatisticsAll = dropStatisticsAll;
        this.indexNameForStatistics = indexNameForStatistics;
    }
    
    public String toString() {
        if (this.truncateTable) {
            return "TRUNCATE TABLE " + this.tableName;
        }
        return "ALTER TABLE " + this.tableName;
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        try {
            this.executeConstantActionBody(activation);
        }
        finally {
            this.clearState();
        }
    }
    
    private void executeConstantActionBody(final Activation activation) throws StandardException {
        this.activation = activation;
        this.lcc = activation.getLanguageConnectionContext();
        this.dd = this.lcc.getDataDictionary();
        this.dm = this.dd.getDependencyManager();
        this.tc = this.lcc.getTransactionExecute();
        int n = 0;
        int n2 = 0;
        if (this.compressTable && (this.purge || this.defragment || this.truncateEndOfTable)) {
            this.td = this.dd.getTableDescriptor(this.tableId);
            if (this.td == null) {
                throw StandardException.newException("X0X05.S", this.tableName);
            }
            if (this.purge) {
                this.purgeRows(this.tc);
            }
            if (this.defragment) {
                this.defragmentRows(this.tc);
            }
            if (this.truncateEndOfTable) {
                this.truncateEnd(this.tc);
            }
        }
        else {
            if (this.updateStatistics) {
                this.updateStatistics();
                return;
            }
            if (this.dropStatistics) {
                this.dropStatistics();
                return;
            }
            this.dd.startWriting(this.lcc);
            if (this.tableConglomerateId == 0L) {
                this.td = this.dd.getTableDescriptor(this.tableId);
                if (this.td == null) {
                    throw StandardException.newException("X0X05.S", this.tableName);
                }
                this.tableConglomerateId = this.td.getHeapConglomerateId();
            }
            this.lockTableForDDL(this.tc, this.tableConglomerateId, true);
            this.td = this.dd.getTableDescriptor(this.tableId);
            if (this.td == null) {
                throw StandardException.newException("X0X05.S", this.tableName);
            }
            if (this.truncateTable) {
                this.dm.invalidateFor(this.td, 42, this.lcc);
            }
            else {
                this.dm.invalidateFor(this.td, 12, this.lcc);
            }
            activation.setDDLTableDescriptor(this.td);
            if (this.sd == null) {
                this.sd = DDLConstantAction.getAndCheckSchemaDescriptor(this.dd, this.schemaId, "ALTER TABLE");
            }
            if (this.truncateTable) {
                this.dm.invalidateFor(this.td, 42, this.lcc);
            }
            else {
                this.dm.invalidateFor(this.td, 12, this.lcc);
            }
            if (this.columnInfo != null) {
                boolean b = false;
                for (int i = 0; i < this.columnInfo.length; ++i) {
                    if (this.columnInfo[i].action == 0 && !this.columnInfo[i].dataType.isNullable() && this.columnInfo[i].defaultInfo == null && this.columnInfo[i].autoincInc == 0L) {
                        b = true;
                    }
                }
                if (b) {
                    n = this.getSemiRowCount(this.tc);
                    if (n > 0) {
                        throw StandardException.newException("X0Y57.S", this.td.getQualifiedName());
                    }
                    n2 = 1;
                }
                for (int j = 0; j < this.columnInfo.length; ++j) {
                    if (this.columnInfo[j].action == 0) {
                        this.addNewColumnToTable(j);
                    }
                    else if (this.columnInfo[j].action == 5 || this.columnInfo[j].action == 6 || this.columnInfo[j].action == 7) {
                        this.modifyColumnDefault(j);
                    }
                    else if (this.columnInfo[j].action == 2) {
                        this.modifyColumnType(j);
                    }
                    else if (this.columnInfo[j].action == 3) {
                        this.modifyColumnConstraint(this.columnInfo[j].name, true);
                    }
                    else if (this.columnInfo[j].action == 4) {
                        if (n2 == 0) {
                            n2 = 1;
                            n = this.getSemiRowCount(this.tc);
                        }
                        if (this.validateNotNullConstraint(new String[] { this.columnInfo[j].name }, new boolean[1], n, this.lcc, "X0Y80.S")) {
                            this.modifyColumnConstraint(this.columnInfo[j].name, false);
                        }
                    }
                    else if (this.columnInfo[j].action == 1) {
                        this.dropColumnFromTable(this.columnInfo[j].name);
                    }
                }
            }
            this.adjustUDTDependencies(this.lcc, this.dd, this.td, this.columnInfo, false);
            if (this.constraintActions != null) {
                for (int k = 0; k < this.constraintActions.length; ++k) {
                    final ConstraintConstantAction constraintConstantAction = this.constraintActions[k];
                    if (constraintConstantAction instanceof CreateConstraintConstantAction) {
                        switch (constraintConstantAction.getConstraintType()) {
                            case 2: {
                                if (this.dd.getConstraintDescriptors(this.td).getPrimaryKey() != null) {
                                    throw StandardException.newException("X0Y58.S", this.td.getQualifiedName());
                                }
                                if (n2 == 0) {
                                    n2 = 1;
                                    n = this.getSemiRowCount(this.tc);
                                    break;
                                }
                                break;
                            }
                            case 4: {
                                if (n2 == 0) {
                                    n2 = 1;
                                    n = this.getSemiRowCount(this.tc);
                                }
                                if (n > 0) {
                                    ConstraintConstantAction.validateConstraint(constraintConstantAction.getConstraintName(), ((CreateConstraintConstantAction)constraintConstantAction).getConstraintText(), this.td, this.lcc, true);
                                    break;
                                }
                                break;
                            }
                        }
                    }
                    this.constraintActions[k].executeConstantAction(activation);
                }
            }
            if (this.lockGranularity != '\0') {
                this.td.setLockGranularity(this.lockGranularity);
                this.dd.updateLockGranularity(this.td, this.sd, this.lockGranularity, this.tc);
            }
            if (this.compressTable) {
                this.compressTable();
            }
            if (this.truncateTable) {
                this.truncateTable();
            }
        }
    }
    
    private void clearState() {
        this.td = null;
        this.lcc = null;
        this.dd = null;
        this.dm = null;
        this.tc = null;
        this.activation = null;
    }
    
    private void dropStatistics() throws StandardException {
        this.td = this.dd.getTableDescriptor(this.tableId);
        this.dd.startWriting(this.lcc);
        this.dm.invalidateFor(this.td, 40, this.lcc);
        if (this.dropStatisticsAll) {
            this.dd.dropStatisticsDescriptors(this.td.getUUID(), null, this.tc);
        }
        else {
            this.dd.dropStatisticsDescriptors(this.td.getUUID(), this.dd.getConglomerateDescriptor(this.indexNameForStatistics, this.sd, false).getUUID(), this.tc);
        }
    }
    
    private void updateStatistics() throws StandardException {
        this.td = this.dd.getTableDescriptor(this.tableId);
        ConglomerateDescriptor[] array;
        if (this.updateStatisticsAll) {
            array = null;
        }
        else {
            array = new ConglomerateDescriptor[] { this.dd.getConglomerateDescriptor(this.indexNameForStatistics, this.sd, false) };
        }
        this.dd.getIndexStatsRefresher(false).runExplicitly(this.lcc, this.td, array, "ALTER TABLE");
    }
    
    private void truncateEnd(final TransactionController transactionController) throws StandardException {
        switch (this.td.getTableType()) {
            case 2:
            case 5: {
                break;
            }
            default: {
                final ConglomerateDescriptor[] conglomerateDescriptors = this.td.getConglomerateDescriptors();
                for (int i = 0; i < conglomerateDescriptors.length; ++i) {
                    transactionController.compressConglomerate(conglomerateDescriptors[i].getConglomerateNumber());
                }
                break;
            }
        }
    }
    
    private void defragmentRows(final TransactionController transactionController) throws StandardException {
        GroupFetchScanController defragmentConglomerate = null;
        int n = 0;
        int[][] array = null;
        ScanController[] array2 = null;
        ConglomerateController[] array3 = null;
        DataValueDescriptor[][] array4 = null;
        TransactionController startNestedUserTransaction = null;
        try {
            startNestedUserTransaction = transactionController.startNestedUserTransaction(false, true);
            switch (this.td.getTableType()) {
                case 2:
                case 5: {}
                default: {
                    final ExecRow valueRow = this.lcc.getLanguageConnectionFactory().getExecutionFactory().getValueRow(this.td.getNumberOfColumns());
                    final ColumnDescriptorList columnDescriptorList = this.td.getColumnDescriptorList();
                    for (int size = columnDescriptorList.size(), i = 0; i < size; ++i) {
                        final ColumnDescriptor element = columnDescriptorList.elementAt(i);
                        valueRow.setColumn(element.getPosition(), element.getType().getNull());
                    }
                    final DataValueDescriptor[][] array5 = new DataValueDescriptor[100][];
                    array5[0] = valueRow.getRowArray();
                    final RowLocation[] array6 = new RowLocation[100];
                    final RowLocation[] array7 = new RowLocation[100];
                    n = this.td.getConglomerateDescriptors().length - 1;
                    if (n > 0) {
                        array = new int[n][];
                        array2 = new ScanController[n];
                        array3 = new ConglomerateController[n];
                        array4 = new DataValueDescriptor[n][];
                        setup_indexes(startNestedUserTransaction, this.td, array, array2, array3, array4);
                    }
                    defragmentConglomerate = startNestedUserTransaction.defragmentConglomerate(this.td.getHeapConglomerateId(), false, true, 4, 7, 5);
                    int fetchNextGroup;
                    while ((fetchNextGroup = defragmentConglomerate.fetchNextGroup(array5, array6, array7)) != 0) {
                        if (n > 0) {
                            for (int j = 0; j < fetchNextGroup; ++j) {
                                for (int k = 0; k < n; ++k) {
                                    fixIndex(array5[j], array4[k], array6[j], array7[j], array3[k], array2[k], array[k]);
                                }
                            }
                        }
                    }
                    startNestedUserTransaction.commit();
                    break;
                }
            }
        }
        finally {
            if (defragmentConglomerate != null) {
                defragmentConglomerate.close();
            }
            if (n > 0) {
                for (int l = 0; l < n; ++l) {
                    if (array2 != null && array2[l] != null) {
                        array2[l].close();
                        array2[l] = null;
                    }
                    if (array3 != null && array3[l] != null) {
                        array3[l].close();
                        array3[l] = null;
                    }
                }
            }
            if (startNestedUserTransaction != null) {
                startNestedUserTransaction.destroy();
            }
        }
    }
    
    private static void setup_indexes(final TransactionController transactionController, final TableDescriptor tableDescriptor, final int[][] array, final ScanController[] array2, final ConglomerateController[] array3, final DataValueDescriptor[][] array4) throws StandardException {
        final ConglomerateDescriptor[] conglomerateDescriptors = tableDescriptor.getConglomerateDescriptors();
        int n = 0;
        for (int i = 0; i < conglomerateDescriptors.length; ++i) {
            final ConglomerateDescriptor conglomerateDescriptor = conglomerateDescriptors[i];
            if (conglomerateDescriptor.isIndex()) {
                array2[n] = transactionController.openScan(conglomerateDescriptor.getConglomerateNumber(), true, 4, 7, 5, null, null, 0, null, null, 0);
                array3[n] = transactionController.openConglomerate(conglomerateDescriptor.getConglomerateNumber(), true, 4, 7, 5);
                final int[] baseColumnPositions = conglomerateDescriptor.getIndexDescriptor().baseColumnPositions();
                final int[] array5 = new int[baseColumnPositions.length];
                for (int j = 0; j < baseColumnPositions.length; ++j) {
                    array5[j] = baseColumnPositions[j] - 1;
                }
                array[n] = array5;
                array4[n] = new DataValueDescriptor[baseColumnPositions.length + 1];
                ++n;
            }
        }
    }
    
    private static void fixIndex(final DataValueDescriptor[] array, final DataValueDescriptor[] array2, final RowLocation rowLocation, final RowLocation rowLocation2, final ConglomerateController conglomerateController, final ScanController scanController, final int[] array3) throws StandardException {
        for (int i = 0; i < array3.length; ++i) {
            array2[i] = array[array3[i]];
        }
        array2[array2.length - 1] = rowLocation;
        scanController.reopenScan(array2, 1, null, array2, -1);
        if (scanController.next()) {
            scanController.delete();
        }
        array2[array2.length - 1] = rowLocation2;
        conglomerateController.insert(array2);
    }
    
    private void purgeRows(final TransactionController transactionController) throws StandardException {
        switch (this.td.getTableType()) {
            case 2:
            case 5: {
                break;
            }
            default: {
                final ConglomerateDescriptor[] conglomerateDescriptors = this.td.getConglomerateDescriptors();
                for (int i = 0; i < conglomerateDescriptors.length; ++i) {
                    transactionController.purgeConglomerate(conglomerateDescriptors[i].getConglomerateNumber());
                }
                break;
            }
        }
    }
    
    private void addNewColumnToTable(final int n) throws StandardException {
        final ColumnDescriptor columnDescriptor = this.td.getColumnDescriptor(this.columnInfo[n].name);
        final int n2 = this.td.getMaxColumnID() + n;
        if (columnDescriptor != null) {
            throw StandardException.newException("X0Y32.S", columnDescriptor.getDescriptorType(), this.columnInfo[n].name, this.td.getDescriptorType(), this.td.getQualifiedName());
        }
        DataValueDescriptor dataValueDescriptor;
        if (this.columnInfo[n].defaultValue != null) {
            dataValueDescriptor = this.columnInfo[n].defaultValue;
        }
        else {
            dataValueDescriptor = this.columnInfo[n].dataType.getNull();
        }
        this.tc.addColumnToConglomerate(this.td.getHeapConglomerateId(), n2, dataValueDescriptor, this.columnInfo[n].dataType.getCollationType());
        UUID uuid = this.columnInfo[n].newDefaultUUID;
        if (this.columnInfo[n].defaultInfo != null && uuid == null) {
            uuid = this.dd.getUUIDFactory().createUUID();
        }
        final ColumnDescriptor e = new ColumnDescriptor(this.columnInfo[n].name, n2 + 1, this.columnInfo[n].dataType, this.columnInfo[n].defaultValue, this.columnInfo[n].defaultInfo, this.td, uuid, this.columnInfo[n].autoincStart, this.columnInfo[n].autoincInc);
        this.dd.addDescriptor(e, this.td, 2, false, this.tc);
        this.td.getColumnDescriptorList().add(e);
        if (e.isAutoincrement()) {
            this.updateNewAutoincrementColumn(this.columnInfo[n].name, this.columnInfo[n].autoincStart, this.columnInfo[n].autoincInc);
        }
        if (e.hasNonNullDefault()) {
            this.updateNewColumnToDefault(e);
        }
        this.addColumnDependencies(this.lcc, this.dd, this.td, this.columnInfo[n]);
        this.dd.updateSYSCOLPERMSforAddColumnToUserTable(this.td.getUUID(), this.tc);
    }
    
    private void dropColumnFromTable(final String str) throws StandardException {
        final boolean b = this.behavior == 0;
        final ColumnDescriptorList generatedColumns = this.td.getGeneratedColumns();
        final int size = generatedColumns.size();
        final ArrayList list = new ArrayList<String>();
        for (int i = 0; i < size; ++i) {
            final ColumnDescriptor element = generatedColumns.elementAt(i);
            final String[] referencedColumnNames = element.getDefaultInfo().getReferencedColumnNames();
            for (int length = referencedColumnNames.length, j = 0; j < length; ++j) {
                if (str.equals(referencedColumnNames[j])) {
                    final String columnName = element.getColumnName();
                    if (!b) {
                        throw StandardException.newException("X0Y25.S", this.dm.getActionString(37), str, "GENERATED COLUMN", columnName);
                    }
                    list.add(columnName);
                }
            }
        }
        this.dd.getDataDescriptorGenerator();
        final int size2 = list.size();
        if (this.td.getColumnDescriptorList().size() - size2 == 1) {
            throw StandardException.newException("X0Y25.S", this.dm.getActionString(37), "THE *LAST* COLUMN " + str, "TABLE", this.td.getQualifiedName());
        }
        for (int k = 0; k < size2; ++k) {
            final String s = list.get(k);
            this.activation.addWarning(StandardException.newWarning("01009", s, this.td.getName()));
            this.dropColumnFromTable(s);
        }
        this.td = this.dd.getTableDescriptor(this.tableId);
        final ColumnDescriptor columnDescriptor = this.td.getColumnDescriptor(str);
        if (columnDescriptor == null) {
            throw StandardException.newException("42X14", str, this.td.getQualifiedName());
        }
        final int size3 = this.td.getColumnDescriptorList().size();
        this.droppedColumnPosition = columnDescriptor.getPosition();
        final FormatableBitSet referencedColumnMap = new FormatableBitSet(size3 + 1);
        referencedColumnMap.set(this.droppedColumnPosition);
        this.td.setReferencedColumnMap(referencedColumnMap);
        this.dm.invalidateFor(this.td, b ? 37 : 46, this.lcc);
        if (columnDescriptor.getDefaultInfo() != null) {
            this.dm.clearDependencies(this.lcc, columnDescriptor.getDefaultDescriptor(this.dd));
        }
        for (final TriggerDescriptor triggerDescriptor : this.dd.getTriggerDescriptors(this.td)) {
            boolean b2 = false;
            final int[] referencedCols = triggerDescriptor.getReferencedCols();
            if (referencedCols != null) {
                final int length2 = referencedCols.length;
                boolean b3 = false;
                int l;
                for (l = 0; l < length2; ++l) {
                    if (referencedCols[l] > this.droppedColumnPosition) {
                        b3 = true;
                    }
                    else if (referencedCols[l] == this.droppedColumnPosition) {
                        if (b) {
                            triggerDescriptor.drop(this.lcc);
                            b2 = true;
                            this.activation.addWarning(StandardException.newWarning("01502", triggerDescriptor.getName(), this.td.getName()));
                            break;
                        }
                        throw StandardException.newException("X0Y25.S", this.dm.getActionString(37), str, "TRIGGER", triggerDescriptor.getName());
                    }
                }
                if (l == length2 && b3) {
                    this.dd.dropTriggerDescriptor(triggerDescriptor, this.tc);
                    for (int n = 0; n < length2; ++n) {
                        if (referencedCols[n] > this.droppedColumnPosition) {
                            final int[] array = referencedCols;
                            final int n2 = n;
                            --array[n2];
                        }
                    }
                    this.dd.addDescriptor(triggerDescriptor, this.sd, 13, false, this.tc);
                }
            }
            if (b2) {
                continue;
            }
            final int[] referencedColsInTriggerAction = triggerDescriptor.getReferencedColsInTriggerAction();
            if (referencedColsInTriggerAction == null) {
                continue;
            }
            final int length3 = referencedColsInTriggerAction.length;
            boolean b4 = false;
            int n3;
            for (n3 = 0; n3 < length3; ++n3) {
                if (referencedColsInTriggerAction[n3] > this.droppedColumnPosition) {
                    b4 = true;
                }
                else if (referencedColsInTriggerAction[n3] == this.droppedColumnPosition) {
                    if (b) {
                        triggerDescriptor.drop(this.lcc);
                        this.activation.addWarning(StandardException.newWarning("01502", triggerDescriptor.getName(), this.td.getName()));
                        break;
                    }
                    throw StandardException.newException("X0Y25.S", this.dm.getActionString(37), str, "TRIGGER", triggerDescriptor.getName());
                }
            }
            if (n3 != length3 || !b4) {
                continue;
            }
            this.dd.dropTriggerDescriptor(triggerDescriptor, this.tc);
            for (int n4 = 0; n4 < length3; ++n4) {
                if (referencedColsInTriggerAction[n4] > this.droppedColumnPosition) {
                    final int[] array2 = referencedColsInTriggerAction;
                    final int n5 = n4;
                    --array2[n5];
                }
            }
            this.dd.addDescriptor(triggerDescriptor, this.sd, 13, false, this.tc);
        }
        final ConstraintDescriptorList constraintDescriptors = this.dd.getConstraintDescriptors(this.td);
        final int size4 = constraintDescriptors.size();
        final ArrayList list2 = new ArrayList();
        int n6 = 0;
        final ConstraintDescriptor[] array3 = new ConstraintDescriptor[size4];
        for (int n7 = size4 - 1; n7 >= 0; --n7) {
            final ConstraintDescriptor element2 = constraintDescriptors.elementAt(n7);
            final int[] referencedColumns = element2.getReferencedColumns();
            final int length4 = referencedColumns.length;
            boolean b5 = false;
            int n8;
            for (n8 = 0; n8 < length4; ++n8) {
                if (referencedColumns[n8] > this.droppedColumnPosition) {
                    b5 = true;
                }
                if (referencedColumns[n8] == this.droppedColumnPosition) {
                    break;
                }
            }
            if (n8 == length4) {
                if (element2 instanceof CheckConstraintDescriptor && b5) {
                    this.dd.dropConstraintDescriptor(element2, this.tc);
                    for (int n9 = 0; n9 < length4; ++n9) {
                        if (referencedColumns[n9] > this.droppedColumnPosition) {
                            final int[] array4 = referencedColumns;
                            final int n10 = n9;
                            --array4[n10];
                        }
                    }
                    ((CheckConstraintDescriptor)element2).setReferencedColumnsDescriptor(new ReferencedColumnsDescriptorImpl(referencedColumns));
                    this.dd.addConstraintDescriptor(element2, this.tc);
                }
            }
            else {
                if (!b) {
                    throw StandardException.newException("X0Y25.S", this.dm.getActionString(37), str, "CONSTRAINT", element2.getConstraintName());
                }
                if (element2 instanceof ReferencedKeyConstraintDescriptor) {
                    array3[n6++] = element2;
                }
                else {
                    this.dm.invalidateFor(element2, 19, this.lcc);
                    this.dropConstraint(element2, this.td, list2, this.activation, this.lcc, true);
                    this.activation.addWarning(StandardException.newWarning("01500", element2.getConstraintName(), this.td.getName()));
                }
            }
        }
        for (int n11 = n6 - 1; n11 >= 0; --n11) {
            final ConstraintDescriptor constraintDescriptor = array3[n11];
            this.dropConstraint(constraintDescriptor, this.td, list2, this.activation, this.lcc, false);
            this.activation.addWarning(StandardException.newWarning("01500", constraintDescriptor.getConstraintName(), this.td.getName()));
            if (b) {
                final ConstraintDescriptorList foreignKeys = this.dd.getForeignKeys(constraintDescriptor.getUUID());
                for (int n12 = 0; n12 < foreignKeys.size(); ++n12) {
                    final ConstraintDescriptor element3 = foreignKeys.elementAt(n12);
                    this.dm.invalidateFor(element3, 19, this.lcc);
                    this.dropConstraint(element3, this.td, list2, this.activation, this.lcc, true);
                    this.activation.addWarning(StandardException.newWarning("01500", element3.getConstraintName(), element3.getTableDescriptor().getName()));
                }
            }
            this.dm.invalidateFor(constraintDescriptor, 19, this.lcc);
            this.dm.clearDependencies(this.lcc, constraintDescriptor);
        }
        this.createNewBackingCongloms(list2, null);
        this.td = this.dd.getTableDescriptor(this.tableId);
        this.compressTable();
        final ColumnDescriptorList columnDescriptorList = this.td.getColumnDescriptorList();
        this.dd.dropColumnDescriptor(this.td.getUUID(), str, this.tc);
        final ColumnDescriptor[] array5 = new ColumnDescriptor[size3 - columnDescriptor.getPosition()];
        for (int position = columnDescriptor.getPosition(), n13 = 0; position < size3; ++position, ++n13) {
            final ColumnDescriptor element4 = columnDescriptorList.elementAt(position);
            this.dd.dropColumnDescriptor(this.td.getUUID(), element4.getColumnName(), this.tc);
            element4.setPosition(position);
            if (element4.isAutoincrement()) {
                element4.setAutoinc_create_or_modify_Start_Increment(0);
            }
            array5[n13] = element4;
        }
        this.dd.addDescriptorArray(array5, this.td, 2, false, this.tc);
        final ListIterator listIterator = this.dd.getProvidersDescriptorList(this.td.getObjectID().toString()).listIterator();
        while (listIterator.hasNext()) {
            final DependencyDescriptor dependencyDescriptor = listIterator.next();
            if (dependencyDescriptor.getDependentFinder().getSQLObjectType().equals("StoredPreparedStatement")) {
                final ListIterator listIterator2 = this.dd.getProvidersDescriptorList(dependencyDescriptor.getUUID().toString()).listIterator();
                while (listIterator2.hasNext()) {
                    final DependencyDescriptor dependencyDescriptor2 = listIterator2.next();
                    if (dependencyDescriptor2.getDependentFinder().getSQLObjectType().equals("Trigger")) {
                        this.columnDroppedAndTriggerDependencies(this.dd.getTriggerDescriptor(dependencyDescriptor2.getUUID()), b, str);
                    }
                }
            }
        }
        this.dd.updateSYSCOLPERMSforDropColumn(this.td.getUUID(), this.tc, columnDescriptor);
        columnDescriptorList.remove(this.td.getColumnDescriptor(str));
    }
    
    private void columnDroppedAndTriggerDependencies(final TriggerDescriptor triggerDescriptor, final boolean b, final String s) throws StandardException {
        this.dd.dropTriggerDescriptor(triggerDescriptor, this.tc);
        final SchemaDescriptor schemaDescriptor = this.dd.getSchemaDescriptor(this.dd.getSPSDescriptor(triggerDescriptor.getActionId()).getCompSchemaId(), null);
        final CompilerContext pushCompilerContext = this.lcc.pushCompilerContext(schemaDescriptor);
        final StatementNode statementNode = (StatementNode)pushCompilerContext.getParser().parseStatement(triggerDescriptor.getTriggerDefinition());
        this.lcc.popCompilerContext(pushCompilerContext);
        CompilerContext pushCompilerContext2 = null;
        try {
            final SPSDescriptor actionSPS = triggerDescriptor.getActionSPS(this.lcc);
            final int[] a = new int[this.td.getNumberOfColumns()];
            Arrays.fill(a, -1);
            actionSPS.setText(this.dd.getTriggerActionString(statementNode, triggerDescriptor.getOldReferencingName(), triggerDescriptor.getNewReferencingName(), triggerDescriptor.getTriggerDefinition(), triggerDescriptor.getReferencedCols(), a, 0, triggerDescriptor.getTableDescriptor(), triggerDescriptor.getTriggerEventMask(), true));
            pushCompilerContext2 = this.lcc.pushCompilerContext(schemaDescriptor);
            pushCompilerContext2.setReliability(0);
            final StatementNode statementNode2 = (StatementNode)pushCompilerContext2.getParser().parseStatement(actionSPS.getText());
            pushCompilerContext2.setCurrentDependent(actionSPS.getPreparedStatement());
            statementNode2.bindStatement();
        }
        catch (StandardException ex) {
            if (!ex.getMessageId().equals("42X04") && !ex.getMessageId().equals("42X14") && !ex.getMessageId().equals("42802") && !ex.getMessageId().equals("42X05")) {
                throw ex;
            }
            if (b) {
                triggerDescriptor.drop(this.lcc);
                this.activation.addWarning(StandardException.newWarning("01502", triggerDescriptor.getName(), this.td.getName()));
                return;
            }
            throw StandardException.newException("X0Y25.S", this.dm.getActionString(37), s, "TRIGGER", triggerDescriptor.getName());
        }
        finally {
            if (pushCompilerContext2 != null) {
                this.lcc.popCompilerContext(pushCompilerContext2);
            }
        }
        this.dd.addDescriptor(triggerDescriptor, this.sd, 13, false, this.tc);
    }
    
    private void modifyColumnType(final int n) throws StandardException {
        final ColumnDescriptor columnDescriptor = this.td.getColumnDescriptor(this.columnInfo[n].name);
        final ColumnDescriptor columnDescriptor2 = new ColumnDescriptor(this.columnInfo[n].name, columnDescriptor.getPosition(), this.columnInfo[n].dataType, columnDescriptor.getDefaultValue(), columnDescriptor.getDefaultInfo(), this.td, columnDescriptor.getDefaultUUID(), this.columnInfo[n].autoincStart, this.columnInfo[n].autoincInc);
        this.dd.dropColumnDescriptor(this.td.getUUID(), this.columnInfo[n].name, this.tc);
        this.dd.addDescriptor(columnDescriptor2, this.td, 2, false, this.tc);
    }
    
    private void modifyColumnConstraint(final String s, final boolean b) throws StandardException {
        final ColumnDescriptor columnDescriptor = this.td.getColumnDescriptor(s);
        final DataTypeDescriptor nullabilityType = columnDescriptor.getType().getNullabilityType(b);
        final ConstraintDescriptorList constraintDescriptors = this.dd.getConstraintDescriptors(this.td);
        final int position = columnDescriptor.getPosition();
        for (int i = 0; i < constraintDescriptors.size(); ++i) {
            final ConstraintDescriptor element = constraintDescriptors.elementAt(i);
            if (element.getConstraintType() == 3) {
                final ColumnDescriptorList columnDescriptors = element.getColumnDescriptors();
                for (int j = 0; j < columnDescriptors.size(); ++j) {
                    if (columnDescriptors.elementAt(j).getPosition() != position) {
                        break;
                    }
                    final ConglomerateDescriptor conglomerateDescriptor = this.td.getConglomerateDescriptor(element.getConglomerateId());
                    if (!conglomerateDescriptor.getIndexDescriptor().isUnique()) {
                        break;
                    }
                    this.recreateUniqueConstraintBackingIndexAsUniqueWhenNotNull(conglomerateDescriptor, this.td, this.activation, this.lcc);
                }
            }
        }
        final ColumnDescriptor columnDescriptor2 = new ColumnDescriptor(s, columnDescriptor.getPosition(), nullabilityType, columnDescriptor.getDefaultValue(), columnDescriptor.getDefaultInfo(), this.td, columnDescriptor.getDefaultUUID(), columnDescriptor.getAutoincStart(), columnDescriptor.getAutoincInc());
        this.dd.dropColumnDescriptor(this.td.getUUID(), s, this.tc);
        this.dd.addDescriptor(columnDescriptor2, this.td, 2, false, this.tc);
    }
    
    private void modifyColumnDefault(final int n) throws StandardException {
        final ColumnDescriptor columnDescriptor = this.td.getColumnDescriptor(this.columnInfo[n].name);
        this.dd.getDataDescriptorGenerator();
        final int position = columnDescriptor.getPosition();
        if (columnDescriptor.hasNonNullDefault()) {
            final DefaultDescriptor defaultDescriptor = new DefaultDescriptor(this.dd, this.columnInfo[n].oldDefaultUUID, this.td.getUUID(), position);
            this.dm.invalidateFor(defaultDescriptor, 31, this.lcc);
            this.dm.clearDependencies(this.lcc, defaultDescriptor);
        }
        UUID uuid = this.columnInfo[n].newDefaultUUID;
        if (this.columnInfo[n].defaultInfo != null && uuid == null) {
            uuid = this.dd.getUUIDFactory().createUUID();
        }
        final ColumnDescriptor columnDescriptor2 = new ColumnDescriptor(this.columnInfo[n].name, position, this.columnInfo[n].dataType, this.columnInfo[n].defaultValue, this.columnInfo[n].defaultInfo, this.td, uuid, this.columnInfo[n].autoincStart, this.columnInfo[n].autoincInc, this.columnInfo[n].autoinc_create_or_modify_Start_Increment);
        this.dd.dropColumnDescriptor(this.td.getUUID(), this.columnInfo[n].name, this.tc);
        this.dd.addDescriptor(columnDescriptor2, this.td, 2, false, this.tc);
        if (this.columnInfo[n].action == 6) {
            this.dd.setAutoincrementValue(this.tc, this.td.getUUID(), this.columnInfo[n].name, this.getColumnMax(this.td, this.columnInfo[n].name, this.columnInfo[n].autoincInc), true);
        }
        else if (this.columnInfo[n].action == 5) {
            this.dd.setAutoincrementValue(this.tc, this.td.getUUID(), this.columnInfo[n].name, this.columnInfo[n].autoincStart, false);
        }
    }
    
    private void compressTable() throws StandardException {
        final Properties properties = new Properties();
        ExecRow emptyExecRow = this.td.getEmptyExecRow();
        int[] columnCollationIds = this.td.getColumnCollationIds();
        this.compressHeapCC = this.tc.openConglomerate(this.td.getHeapConglomerateId(), false, 4, 7, 5);
        final RowLocation rowLocationTemplate = this.compressHeapCC.newRowLocationTemplate();
        this.compressHeapCC.getInternalTablePropertySet(properties);
        this.compressHeapCC.close();
        this.compressHeapCC = null;
        this.baseRow = new ExecRow[this.bulkFetchSize];
        this.baseRowArray = new DataValueDescriptor[this.bulkFetchSize][];
        this.validRow = new boolean[this.bulkFetchSize];
        this.getAffectedIndexes();
        this.compressRL = new RowLocation[this.bulkFetchSize];
        this.indexRows = new ExecIndexRow[this.numIndexes];
        if (!this.compressTable) {
            final ExecRow valueRow = this.activation.getExecutionFactory().getValueRow(emptyExecRow.nColumns() - 1);
            final int[] array = new int[columnCollationIds.length - 1];
            for (int i = 0; i < valueRow.nColumns(); ++i) {
                valueRow.setColumn(i + 1, (i < this.droppedColumnPosition - 1) ? emptyExecRow.getColumn(i + 1) : emptyExecRow.getColumn(i + 1 + 1));
                array[i] = columnCollationIds[(i < this.droppedColumnPosition - 1) ? i : (i + 1)];
            }
            emptyExecRow = valueRow;
            columnCollationIds = array;
        }
        this.setUpAllSorts(emptyExecRow, rowLocationTemplate);
        this.openBulkFetchScan(this.td.getHeapConglomerateId());
        this.estimatedRowCount = this.compressHeapGSC.getEstimatedRowCount();
        for (int j = 0; j < this.bulkFetchSize; ++j) {
            this.baseRow[j] = this.td.getEmptyExecRow();
            this.baseRowArray[j] = this.baseRow[j].getRowArray();
            this.compressRL[j] = this.compressHeapGSC.newRowLocationTemplate();
        }
        final long andLoadConglomerate = this.tc.createAndLoadConglomerate("heap", emptyExecRow.getRowArray(), null, columnCollationIds, properties, 0, this, null);
        this.closeBulkFetchScan();
        final ScanController openScan = this.tc.openScan(andLoadConglomerate, false, 4, 7, 5, null, null, 0, null, null, 0);
        openScan.setEstimatedRowCount(this.rowCount);
        openScan.close();
        this.dd.startWriting(this.lcc);
        if (this.compressIRGs.length > 0) {
            this.updateAllIndexes(andLoadConglomerate, this.dd);
        }
        final long heapConglomerateId = this.td.getHeapConglomerateId();
        this.dd.updateConglomerateDescriptor(this.td.getConglomerateDescriptor(heapConglomerateId), andLoadConglomerate, this.tc);
        this.dm.invalidateFor(this.td, 33, this.lcc);
        this.tc.dropConglomerate(heapConglomerateId);
        this.cleanUp();
    }
    
    private void truncateTable() throws StandardException {
        final Properties properties = new Properties();
        final ConstraintDescriptorList constraintDescriptors = this.dd.getConstraintDescriptors(this.td);
        for (int i = 0; i < constraintDescriptors.size(); ++i) {
            final ConstraintDescriptor element = constraintDescriptors.elementAt(i);
            if (element instanceof ReferencedKeyConstraintDescriptor && ((ReferencedKeyConstraintDescriptor)element).hasNonSelfReferencingFK(1)) {
                throw StandardException.newException("XCL48.S", this.td.getName());
            }
        }
        for (final TriggerDescriptor triggerDescriptor : this.dd.getTriggerDescriptors(this.td)) {
            if (triggerDescriptor.listensForEvent(2) && triggerDescriptor.isEnabled()) {
                throw StandardException.newException("XCL49.S", this.td.getName(), triggerDescriptor.getName());
            }
        }
        final ExecRow emptyExecRow = this.td.getEmptyExecRow();
        this.compressHeapCC = this.tc.openConglomerate(this.td.getHeapConglomerateId(), false, 4, 7, 5);
        final RowLocation rowLocationTemplate = this.compressHeapCC.newRowLocationTemplate();
        this.compressHeapCC.getInternalTablePropertySet(properties);
        this.compressHeapCC.close();
        this.compressHeapCC = null;
        final long conglomerate = this.tc.createConglomerate("heap", emptyExecRow.getRowArray(), null, this.td.getColumnCollationIds(), properties, 0);
        this.getAffectedIndexes();
        if (this.numIndexes > 0) {
            this.indexRows = new ExecIndexRow[this.numIndexes];
            this.ordering = new ColumnOrdering[this.numIndexes][];
            this.collation = new int[this.numIndexes][];
            for (int j = 0; j < this.numIndexes; ++j) {
                final IndexRowGenerator indexRowGenerator = this.compressIRGs[j];
                indexRowGenerator.getIndexRow(emptyExecRow, rowLocationTemplate, this.indexRows[j] = indexRowGenerator.getIndexRowTemplate(), null);
                final int[] baseColumnPositions = indexRowGenerator.baseColumnPositions();
                final boolean[] ascending = indexRowGenerator.isAscending();
                final int n = baseColumnPositions.length + 1;
                this.ordering[j] = new ColumnOrdering[n];
                this.collation[j] = indexRowGenerator.getColumnCollationIds(this.td.getColumnDescriptorList());
                for (int k = 0; k < n - 1; ++k) {
                    this.ordering[j][k] = new IndexColumnOrder(k, ascending[k]);
                }
                this.ordering[j][n - 1] = new IndexColumnOrder(n - 1);
            }
        }
        this.dd.startWriting(this.lcc);
        if (this.numIndexes > 0) {
            final long[] array = new long[this.numIndexes];
            for (int l = 0; l < this.numIndexes; ++l) {
                this.updateIndex(conglomerate, this.dd, l, array);
            }
        }
        final long heapConglomerateId = this.td.getHeapConglomerateId();
        this.dd.updateConglomerateDescriptor(this.td.getConglomerateDescriptor(heapConglomerateId), conglomerate, this.tc);
        this.dm.invalidateFor(this.td, 42, this.lcc);
        this.tc.dropConglomerate(heapConglomerateId);
        this.cleanUp();
    }
    
    private void updateAllIndexes(final long n, final DataDictionary dataDictionary) throws StandardException {
        final long[] array = new long[this.numIndexes];
        if (this.sequential) {
            if (this.numIndexes >= 1) {
                this.updateIndex(n, dataDictionary, 0, array);
            }
            for (int i = 1; i < this.numIndexes; ++i) {
                this.openBulkFetchScan(n);
                while (this.getNextRowFromRowSource() != null) {
                    this.objectifyStreamingColumns();
                    this.insertIntoSorter(i, this.compressRL[this.currentCompressRow - 1]);
                }
                this.updateIndex(n, dataDictionary, i, array);
                this.closeBulkFetchScan();
            }
        }
        else {
            for (int j = 0; j < this.numIndexes; ++j) {
                this.updateIndex(n, dataDictionary, j, array);
            }
        }
    }
    
    private void updateIndex(final long i, final DataDictionary dataDictionary, final int n, final long[] array) throws StandardException {
        final Properties properties = new Properties();
        final ConglomerateDescriptor conglomerateDescriptor = this.td.getConglomerateDescriptor(this.indexConglomerateNumbers[n]);
        final ConglomerateController openConglomerate = this.tc.openConglomerate(this.indexConglomerateNumbers[n], false, 4, 7, 5);
        openConglomerate.getInternalTablePropertySet(properties);
        final int nColumns = this.indexRows[n].nColumns();
        properties.put("baseConglomerateId", Long.toString(i));
        if (conglomerateDescriptor.getIndexDescriptor().isUnique()) {
            properties.put("nUniqueColumns", Integer.toString(nColumns - 1));
        }
        else {
            properties.put("nUniqueColumns", Integer.toString(nColumns));
        }
        if (conglomerateDescriptor.getIndexDescriptor().isUniqueWithDuplicateNulls()) {
            properties.put("uniqueWithDuplicateNulls", Boolean.toString(true));
        }
        properties.put("rowLocationColumn", Integer.toString(nColumns - 1));
        properties.put("nKeyFields", Integer.toString(nColumns));
        openConglomerate.close();
        boolean b = false;
        if (!this.truncateTable) {
            this.sorters[n].completedInserts();
            this.sorters[n] = null;
            CardinalityCounter cardinalityCounter;
            if (this.td.statisticsExist(conglomerateDescriptor)) {
                cardinalityCounter = new CardinalityCounter(this.tc.openSortRowSource(this.sortIds[n]));
                b = true;
            }
            else {
                cardinalityCounter = new CardinalityCounter(this.tc.openSortRowSource(this.sortIds[n]));
            }
            array[n] = this.tc.createAndLoadConglomerate("BTREE", this.indexRows[n].getRowArray(), this.ordering[n], this.collation[n], properties, 0, cardinalityCounter, null);
            if (b) {
                dataDictionary.dropStatisticsDescriptors(this.td.getUUID(), conglomerateDescriptor.getUUID(), this.tc);
            }
            final long rowCount;
            if ((rowCount = cardinalityCounter.getRowCount()) > 0L) {
                final long[] cardinality = cardinalityCounter.getCardinality();
                for (int j = 0; j < cardinality.length; ++j) {
                    dataDictionary.addDescriptor(new StatisticsDescriptor(dataDictionary, dataDictionary.getUUIDFactory().createUUID(), conglomerateDescriptor.getUUID(), this.td.getUUID(), "I", new StatisticsImpl(rowCount, cardinality[j]), j + 1), null, 14, true, this.tc);
                }
            }
        }
        else {
            array[n] = this.tc.createConglomerate("BTREE", this.indexRows[n].getRowArray(), this.ordering[n], this.collation[n], properties, 0);
            if (this.td.statisticsExist(conglomerateDescriptor)) {
                dataDictionary.dropStatisticsDescriptors(this.td.getUUID(), conglomerateDescriptor.getUUID(), this.tc);
            }
        }
        dataDictionary.updateConglomerateDescriptor(this.td.getConglomerateDescriptors(this.indexConglomerateNumbers[n]), array[n], this.tc);
        this.tc.dropConglomerate(this.indexConglomerateNumbers[n]);
    }
    
    private void getAffectedIndexes() throws StandardException {
        final IndexLister indexLister = this.td.getIndexLister();
        this.compressIRGs = indexLister.getIndexRowGenerators();
        this.numIndexes = this.compressIRGs.length;
        this.indexConglomerateNumbers = indexLister.getIndexConglomerateNumbers();
        if (!this.compressTable && !this.truncateTable) {
            final ArrayList list = new ArrayList();
            for (int i = 0; i < this.compressIRGs.length; ++i) {
                int[] baseColumnPositions;
                int n;
                for (baseColumnPositions = this.compressIRGs[i].baseColumnPositions(), n = 0; n < baseColumnPositions.length && baseColumnPositions[n] != this.droppedColumnPosition; ++n) {}
                if (n != baseColumnPositions.length) {
                    if (baseColumnPositions.length == 1 || (this.behavior == 0 && this.compressIRGs[i].isUnique())) {
                        --this.numIndexes;
                        this.dropConglomerate(this.td.getConglomerateDescriptor(this.indexConglomerateNumbers[i]), this.td, true, list, this.activation, this.activation.getLanguageConnectionContext());
                        this.compressIRGs[i] = null;
                    }
                    else if (this.compressIRGs[i].isUnique()) {
                        throw StandardException.newException("X0Y25.S", this.dm.getActionString(37), this.columnInfo[0].name, "UNIQUE INDEX", this.td.getConglomerateDescriptor(this.indexConglomerateNumbers[i]).getConglomerateName());
                    }
                }
            }
            this.createNewBackingCongloms(list, this.indexConglomerateNumbers);
            final IndexRowGenerator[] compressIRGs = new IndexRowGenerator[this.numIndexes];
            final long[] indexConglomerateNumbers = new long[this.numIndexes];
            for (int j = 0, n2 = 0; j < this.numIndexes; ++j, ++n2) {
                while (this.compressIRGs[n2] == null) {
                    ++n2;
                }
                final int[] baseColumnPositions2 = this.compressIRGs[n2].baseColumnPositions();
                compressIRGs[j] = this.compressIRGs[n2];
                indexConglomerateNumbers[j] = this.indexConglomerateNumbers[n2];
                final boolean[] ascending = this.compressIRGs[n2].isAscending();
                boolean b = false;
                int length = baseColumnPositions2.length;
                for (int k = 0; k < length; ++k) {
                    if (baseColumnPositions2[k] > this.droppedColumnPosition) {
                        final int[] array = baseColumnPositions2;
                        final int n3 = k;
                        --array[n3];
                    }
                    else if (baseColumnPositions2[k] == this.droppedColumnPosition) {
                        baseColumnPositions2[k] = 0;
                        b = true;
                    }
                }
                if (b) {
                    final int[] baseColumnPositions3 = new int[--length];
                    final boolean[] isAscending = new boolean[length];
                    int l = 0;
                    int n4 = 0;
                    while (l < length) {
                        if (n4 == 0 && baseColumnPositions2[l + n4] == 0) {
                            ++n4;
                        }
                        baseColumnPositions3[l] = baseColumnPositions2[l + n4];
                        isAscending[l] = ascending[l + n4];
                        ++l;
                    }
                    final IndexDescriptor indexDescriptor = this.compressIRGs[n2].getIndexDescriptor();
                    indexDescriptor.setBaseColumnPositions(baseColumnPositions3);
                    indexDescriptor.setIsAscending(isAscending);
                    indexDescriptor.setNumberOfOrderedColumns(indexDescriptor.numberOfOrderedColumns() - 1);
                }
            }
            this.compressIRGs = compressIRGs;
            this.indexConglomerateNumbers = indexConglomerateNumbers;
        }
        final Object[] compressIndexArrays = this.compressIndexArrays(this.indexConglomerateNumbers, this.compressIRGs);
        if (compressIndexArrays != null) {
            this.indexConglomerateNumbers = (long[])compressIndexArrays[1];
            this.compressIRGs = (IndexRowGenerator[])compressIndexArrays[2];
            this.numIndexes = this.indexConglomerateNumbers.length;
        }
        this.indexedCols = new FormatableBitSet((this.compressTable || this.truncateTable) ? (this.td.getNumberOfColumns() + 1) : this.td.getNumberOfColumns());
        for (int n5 = 0; n5 < this.numIndexes; ++n5) {
            final int[] baseColumnPositions4 = this.compressIRGs[n5].getIndexDescriptor().baseColumnPositions();
            for (int n6 = 0; n6 < baseColumnPositions4.length; ++n6) {
                this.indexedCols.set(baseColumnPositions4[n6]);
            }
        }
    }
    
    private void createNewBackingCongloms(final ArrayList list, final long[] array) throws StandardException {
        for (int size = list.size(), i = 0; i < size; ++i) {
            final CreateIndexConstantAction createIndexConstantAction = list.get(i);
            if (this.dd.getConglomerateDescriptor(createIndexConstantAction.getCreatedUUID()) != null) {
                this.executeConglomReplacement(createIndexConstantAction, this.activation);
                final long replacedConglomNumber = createIndexConstantAction.getReplacedConglomNumber();
                final long createdConglomNumber = createIndexConstantAction.getCreatedConglomNumber();
                final ConglomerateDescriptor[] conglomerateDescriptors = this.td.getConglomerateDescriptors(replacedConglomNumber);
                for (int j = 0; j < conglomerateDescriptors.length; ++j) {
                    conglomerateDescriptors[j].setConglomerateNumber(createdConglomNumber);
                }
                if (array != null) {
                    for (int k = 0; k < array.length; ++k) {
                        if (array[k] == replacedConglomNumber) {
                            array[k] = createdConglomNumber;
                        }
                    }
                }
            }
        }
    }
    
    private void setUpAllSorts(final ExecRow execRow, final RowLocation rowLocation) throws StandardException {
        this.ordering = new ColumnOrdering[this.numIndexes][];
        this.collation = new int[this.numIndexes][];
        this.needToDropSort = new boolean[this.numIndexes];
        this.sortIds = new long[this.numIndexes];
        for (int i = 0; i < this.numIndexes; ++i) {
            this.indexRows[i] = this.compressIRGs[i].getIndexRowTemplate();
            this.compressIRGs[i].getIndexRow(execRow, rowLocation, this.indexRows[i], null);
            this.collation[i] = this.compressIRGs[i].getColumnCollationIds(this.td.getColumnDescriptorList());
            final int[] baseColumnPositions = this.compressIRGs[i].baseColumnPositions();
            final boolean[] ascending = this.compressIRGs[i].isAscending();
            final int n = baseColumnPositions.length + 1;
            final BasicSortObserver basicSortObserver = new BasicSortObserver(false, false, this.indexRows[i], this.numIndexes == 1);
            this.ordering[i] = new ColumnOrdering[n];
            for (int j = 0; j < n - 1; ++j) {
                this.ordering[i][j] = new IndexColumnOrder(j, ascending[j]);
            }
            this.ordering[i][n - 1] = new IndexColumnOrder(n - 1);
            this.sortIds[i] = this.tc.createSort(null, this.indexRows[i].getRowArrayClone(), this.ordering[i], basicSortObserver, false, this.estimatedRowCount, -1);
        }
        this.sorters = new SortController[this.numIndexes];
        for (int k = 0; k < this.numIndexes; ++k) {
            this.sorters[k] = this.tc.openSort(this.sortIds[k]);
            this.needToDropSort[k] = true;
        }
    }
    
    public FormatableBitSet getValidColumns() {
        return null;
    }
    
    public DataValueDescriptor[] getNextRowFromRowSource() throws StandardException {
        this.currentRow = null;
        if (!this.doneScan && (this.currentCompressRow == this.bulkFetchSize || !this.validRow[this.currentCompressRow])) {
            final int fetchNextGroup = this.compressHeapGSC.fetchNextGroup(this.baseRowArray, this.compressRL);
            this.doneScan = (fetchNextGroup != this.bulkFetchSize);
            this.currentCompressRow = 0;
            this.rowCount += fetchNextGroup;
            for (int i = 0; i < fetchNextGroup; ++i) {
                this.validRow[i] = true;
            }
            for (int j = fetchNextGroup; j < this.bulkFetchSize; ++j) {
                this.validRow[j] = false;
            }
        }
        if (this.validRow[this.currentCompressRow]) {
            if (this.compressTable) {
                this.currentRow = this.baseRow[this.currentCompressRow];
            }
            else {
                if (this.currentRow == null) {
                    this.currentRow = this.activation.getExecutionFactory().getValueRow(this.baseRowArray[this.currentCompressRow].length - 1);
                }
                for (int k = 0; k < this.currentRow.nColumns(); ++k) {
                    this.currentRow.setColumn(k + 1, (k < this.droppedColumnPosition - 1) ? this.baseRow[this.currentCompressRow].getColumn(k + 1) : this.baseRow[this.currentCompressRow].getColumn(k + 1 + 1));
                }
            }
            ++this.currentCompressRow;
        }
        if (this.currentRow != null) {
            if (this.compressIRGs.length > 0) {
                this.currentRow = this.currentRow.getClone(this.indexedCols);
            }
            return this.currentRow.getRowArray();
        }
        return null;
    }
    
    public boolean needsToClone() {
        return true;
    }
    
    public void closeRowSource() {
    }
    
    public boolean needsRowLocation() {
        return this.numIndexes > 0;
    }
    
    public void rowLocation(final RowLocation rowLocation) throws StandardException {
        if (this.compressIRGs.length > 0) {
            this.objectifyStreamingColumns();
            int length = this.compressIRGs.length;
            if (length > 1 && this.sequential) {
                length = 1;
            }
            for (int i = 0; i < length; ++i) {
                this.insertIntoSorter(i, rowLocation);
            }
        }
    }
    
    private void objectifyStreamingColumns() throws StandardException {
        for (int i = 0; i < this.currentRow.getRowArray().length; ++i) {
            if (this.indexedCols.get(i + 1)) {
                if (this.currentRow.getRowArray()[i] instanceof StreamStorable) {
                    this.currentRow.getRowArray()[i].getObject();
                }
            }
        }
    }
    
    private void insertIntoSorter(final int n, final RowLocation rowLocation) throws StandardException {
        this.indexRows[n].getNewObjectArray();
        this.compressIRGs[n].getIndexRow(this.currentRow, (RowLocation)rowLocation.cloneValue(false), this.indexRows[n], null);
        this.sorters[n].insert(this.indexRows[n].getRowArray());
    }
    
    private void cleanUp() throws StandardException {
        if (this.compressHeapCC != null) {
            this.compressHeapCC.close();
            this.compressHeapCC = null;
        }
        if (this.compressHeapGSC != null) {
            this.closeBulkFetchScan();
        }
        if (this.sorters != null) {
            for (int i = 0; i < this.compressIRGs.length; ++i) {
                if (this.sorters[i] != null) {
                    this.sorters[i].completedInserts();
                }
                this.sorters[i] = null;
            }
        }
        if (this.needToDropSort != null) {
            for (int j = 0; j < this.needToDropSort.length; ++j) {
                if (this.needToDropSort[j]) {
                    this.tc.dropSort(this.sortIds[j]);
                    this.needToDropSort[j] = false;
                }
            }
        }
    }
    
    private int getSemiRowCount(final TransactionController transactionController) throws StandardException {
        int n = 0;
        final ScanController openScan = transactionController.openScan(this.td.getHeapConglomerateId(), false, 0, 7, 5, RowUtil.EMPTY_ROW_BITSET, null, 1, null, null, -1);
        while (openScan.next() && ++n != 2) {}
        openScan.close();
        return n;
    }
    
    private void updateNewColumnToDefault(final ColumnDescriptor columnDescriptor) throws StandardException {
        final DefaultInfo defaultInfo = columnDescriptor.getDefaultInfo();
        final String columnName = columnDescriptor.getColumnName();
        String defaultText;
        if (defaultInfo.isGeneratedColumn()) {
            defaultText = "default";
        }
        else {
            defaultText = columnDescriptor.getDefaultInfo().getDefaultText();
        }
        executeUpdate(this.lcc, "UPDATE " + IdUtil.mkQualifiedName(this.td.getSchemaName(), this.td.getName()) + " SET " + IdUtil.normalToDelimited(columnName) + "=" + defaultText);
    }
    
    private static void executeUpdate(final LanguageConnectionContext languageConnectionContext, final String s) throws StandardException {
        languageConnectionContext.prepareInternalStatement(s).executeSubStatement(languageConnectionContext, true, 0L).close();
    }
    
    private long getColumnMax(final TableDescriptor tableDescriptor, final String s, final long n) throws StandardException {
        final ResultSet executeSubStatement = this.lcc.prepareInternalStatement("SELECT  " + ((n > 0L) ? "MAX" : "MIN") + "(" + IdUtil.normalToDelimited(s) + ") FROM " + IdUtil.mkQualifiedName(tableDescriptor.getSchemaName(), tableDescriptor.getName())).executeSubStatement(this.lcc, false, 0L);
        final DataValueDescriptor[] rowArray = executeSubStatement.getNextRow().getRowArray();
        executeSubStatement.close();
        executeSubStatement.finish();
        return rowArray[0].getLong();
    }
    
    private void openBulkFetchScan(final long n) throws StandardException {
        this.doneScan = false;
        this.compressHeapGSC = this.tc.openGroupFetchScan(n, false, 0, 7, 5, null, null, 0, null, null, 0);
    }
    
    private void closeBulkFetchScan() throws StandardException {
        this.compressHeapGSC.close();
        this.compressHeapGSC = null;
    }
    
    private void updateNewAutoincrementColumn(final String s, final long value, final long n) throws StandardException {
        this.lcc.setAutoincrementUpdate(true);
        this.lcc.autoincrementCreateCounter(this.td.getSchemaName(), this.td.getName(), s, new Long(value), n, 0);
        final String string = "UPDATE " + IdUtil.mkQualifiedName(this.td.getSchemaName(), this.td.getName()) + " SET " + IdUtil.normalToDelimited(s) + "=" + "org.apache.derby.iapi.db.ConnectionInfo::" + "nextAutoincrementValue(" + StringUtil.quoteStringLiteral(this.td.getSchemaName()) + "," + StringUtil.quoteStringLiteral(this.td.getName()) + "," + StringUtil.quoteStringLiteral(s) + ")";
        try {
            executeUpdate(this.lcc, string);
        }
        catch (StandardException ex) {
            if (ex.getMessageId().equals("22003")) {
                throw StandardException.newException("42Z24", ex, this.td.getName(), s);
            }
            throw ex;
        }
        finally {
            this.lcc.autoincrementFlushCache(this.td.getUUID());
            this.lcc.setAutoincrementUpdate(false);
        }
    }
    
    private boolean validateNotNullConstraint(final String[] array, final boolean[] array2, final int n, final LanguageConnectionContext languageConnectionContext, final String s) throws StandardException {
        boolean b = false;
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            final ColumnDescriptor columnDescriptor = this.td.getColumnDescriptor(array[i]);
            if (columnDescriptor == null) {
                throw StandardException.newException("42X14", array[i], this.td.getName());
            }
            if (columnDescriptor.getType().isNullable()) {
                if (n > 0) {
                    if (b) {
                        sb.append(" AND ");
                    }
                    sb.append(IdUtil.normalToDelimited(array[i]) + " IS NOT NULL ");
                }
                b = true;
                array2[i] = true;
            }
        }
        if (!b || n <= 0 || ConstraintConstantAction.validateConstraint(null, sb.toString(), this.td, languageConnectionContext, false)) {
            return b;
        }
        if (s.equals("X0Y63.S")) {
            throw StandardException.newException("X0Y63.S", this.td.getQualifiedName());
        }
        if (s.equals("X0Y63.S.1")) {
            throw StandardException.newException("X0Y63.S.1", this.td.getQualifiedName());
        }
        throw StandardException.newException("X0Y80.S", this.td.getQualifiedName(), array[0]);
    }
    
    private Object[] compressIndexArrays(final long[] array, final IndexRowGenerator[] array2) {
        final long[] array3 = new long[array.length];
        int n = 0;
        int n2 = array.length - 1;
        for (int i = 0; i < array.length; ++i) {
            int j;
            for (j = 0; j < n; ++j) {
                if (array[i] == array3[j]) {
                    array3[n2--] = i;
                    break;
                }
            }
            if (j == n) {
                array3[n++] = array[i];
            }
        }
        if (n < array.length) {
            final long[] array4 = new long[n];
            final IndexRowGenerator[] array5 = new IndexRowGenerator[n];
            final int[] array6 = new int[array.length - n];
            int n3 = 0;
            int k = 0;
            int n4 = array.length - 1;
            while (k < array.length) {
                if (k < n) {
                    array4[k] = array3[k];
                }
                else {
                    array6[array.length - k - 1] = (int)array3[k];
                }
                if (n4 >= n && k == (int)array3[n4]) {
                    --n4;
                }
                else {
                    array5[n3] = array2[k];
                    ++n3;
                }
                ++k;
            }
            return new Object[] { array6, array4, array5 };
        }
        return null;
    }
}
