// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.store.access.SortController;
import org.apache.derby.impl.services.daemon.IndexStatisticsDaemonImpl;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.store.access.GroupFetchScanController;
import org.apache.derby.iapi.store.access.RowLocationRetRowSource;
import org.apache.derby.iapi.sql.dictionary.DataDescriptorGenerator;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.catalog.Statistics;
import org.apache.derby.iapi.sql.dictionary.StatisticsDescriptor;
import org.apache.derby.catalog.types.StatisticsImpl;
import org.apache.derby.iapi.store.access.SortObserver;
import org.apache.derby.iapi.store.access.ColumnOrdering;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptorList;
import org.apache.derby.iapi.sql.dictionary.IndexRowGenerator;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import java.util.Properties;
import org.apache.derby.catalog.UUID;

class CreateIndexConstantAction extends IndexConstantAction
{
    private final boolean forCreateTable;
    private boolean unique;
    private boolean uniqueWithDuplicateNulls;
    private String indexType;
    private String[] columnNames;
    private boolean[] isAscending;
    private boolean isConstraint;
    private UUID conglomerateUUID;
    private Properties properties;
    private ExecRow indexTemplateRow;
    private long conglomId;
    private long droppedConglomNum;
    
    CreateIndexConstantAction(final boolean forCreateTable, final boolean unique, final boolean uniqueWithDuplicateNulls, final String indexType, final String s, final String s2, final String s3, final UUID uuid, final String[] columnNames, final boolean[] isAscending, final boolean isConstraint, final UUID conglomerateUUID, final Properties properties) {
        super(uuid, s2, s3, s);
        this.forCreateTable = forCreateTable;
        this.unique = unique;
        this.uniqueWithDuplicateNulls = uniqueWithDuplicateNulls;
        this.indexType = indexType;
        this.columnNames = columnNames;
        this.isAscending = isAscending;
        this.isConstraint = isConstraint;
        this.conglomerateUUID = conglomerateUUID;
        this.properties = properties;
        this.conglomId = -1L;
        this.droppedConglomNum = -1L;
    }
    
    CreateIndexConstantAction(final ConglomerateDescriptor conglomerateDescriptor, final TableDescriptor tableDescriptor, final Properties properties) {
        super(tableDescriptor.getUUID(), conglomerateDescriptor.getConglomerateName(), tableDescriptor.getName(), tableDescriptor.getSchemaName());
        this.forCreateTable = false;
        this.droppedConglomNum = conglomerateDescriptor.getConglomerateNumber();
        final IndexRowGenerator indexDescriptor = conglomerateDescriptor.getIndexDescriptor();
        this.unique = indexDescriptor.isUnique();
        this.uniqueWithDuplicateNulls = indexDescriptor.isUniqueWithDuplicateNulls();
        this.indexType = indexDescriptor.indexType();
        this.columnNames = conglomerateDescriptor.getColumnNames();
        this.isAscending = indexDescriptor.isAscending();
        this.isConstraint = conglomerateDescriptor.isConstraint();
        this.conglomerateUUID = conglomerateDescriptor.getUUID();
        this.properties = properties;
        this.conglomId = -1L;
        if (this.columnNames == null) {
            final int[] baseColumnPositions = indexDescriptor.baseColumnPositions();
            this.columnNames = new String[baseColumnPositions.length];
            final ColumnDescriptorList columnDescriptorList = tableDescriptor.getColumnDescriptorList();
            for (int i = 0; i < baseColumnPositions.length; ++i) {
                this.columnNames[i] = columnDescriptorList.elementAt(baseColumnPositions[i] - 1).getColumnName();
            }
        }
    }
    
    public String toString() {
        return "CREATE INDEX " + this.indexName;
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        IndexRowGenerator indexRowGenerator = null;
        int n = -1;
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        dataDictionary.startWriting(languageConnectionContext);
        final SchemaDescriptor schemaDescriptor = dataDictionary.getSchemaDescriptor(this.schemaName, transactionExecute, true);
        TableDescriptor tableDescriptor = activation.getDDLTableDescriptor();
        if (tableDescriptor == null) {
            if (this.tableId != null) {
                tableDescriptor = dataDictionary.getTableDescriptor(this.tableId);
            }
            else {
                tableDescriptor = dataDictionary.getTableDescriptor(this.tableName, schemaDescriptor, transactionExecute);
            }
        }
        if (tableDescriptor == null) {
            throw StandardException.newException("X0Y38.S", this.indexName, this.tableName);
        }
        if (tableDescriptor.getTableType() == 1) {
            throw StandardException.newException("X0Y28.S", this.indexName, this.tableName);
        }
        this.lockTableForDDL(transactionExecute, tableDescriptor.getHeapConglomerateId(), false);
        if (!this.forCreateTable) {
            dependencyManager.invalidateFor(tableDescriptor, 3, languageConnectionContext);
        }
        final int[] array = new int[this.columnNames.length];
        for (int i = 0; i < this.columnNames.length; ++i) {
            final ColumnDescriptor columnDescriptor = tableDescriptor.getColumnDescriptor(this.columnNames[i]);
            if (columnDescriptor == null) {
                throw StandardException.newException("42X14", this.columnNames[i], this.tableName);
            }
            final TypeId typeId = columnDescriptor.getType().getTypeId();
            final ClassFactory classFactory = languageConnectionContext.getLanguageConnectionFactory().getClassFactory();
            int orderable = typeId.orderable(classFactory) ? 1 : 0;
            if (orderable != 0 && typeId.userType()) {
                final String correspondingJavaTypeName = typeId.getCorrespondingJavaTypeName();
                try {
                    if (classFactory.isApplicationClass(classFactory.loadApplicationClass(correspondingJavaTypeName))) {
                        orderable = 0;
                    }
                }
                catch (ClassNotFoundException ex) {
                    orderable = 0;
                }
            }
            if (orderable == 0) {
                throw StandardException.newException("X0X67.S", typeId.getSQLTypeName());
            }
            array[i] = columnDescriptor.getPosition();
            if (n < array[i]) {
                n = array[i];
            }
        }
        final ConglomerateDescriptor[] conglomerateDescriptors = tableDescriptor.getConglomerateDescriptors();
        boolean b = false;
        for (int j = 0; j < conglomerateDescriptors.length; ++j) {
            final ConglomerateDescriptor conglomerateDescriptor = conglomerateDescriptors[j];
            if (conglomerateDescriptor.isIndex()) {
                if (this.droppedConglomNum != conglomerateDescriptor.getConglomerateNumber()) {
                    final IndexRowGenerator indexDescriptor = conglomerateDescriptor.getIndexDescriptor();
                    final int[] baseColumnPositions = indexDescriptor.baseColumnPositions();
                    final boolean[] ascending = indexDescriptor.isAscending();
                    int n2 = 0;
                    boolean b2 = (indexDescriptor.isUnique() || !this.unique) && baseColumnPositions.length == array.length;
                    if (b2 && !indexDescriptor.isUnique()) {
                        b2 = (indexDescriptor.isUniqueWithDuplicateNulls() || !this.uniqueWithDuplicateNulls);
                    }
                    if (b2 && this.indexType.equals(indexDescriptor.indexType())) {
                        while (n2 < baseColumnPositions.length && baseColumnPositions[n2] == array[n2]) {
                            if (ascending[n2] != this.isAscending[n2]) {
                                break;
                            }
                            ++n2;
                        }
                    }
                    if (n2 == array.length) {
                        if (!this.isConstraint) {
                            activation.addWarning(StandardException.newWarning("01504", conglomerateDescriptor.getConglomerateName()));
                            return;
                        }
                        this.conglomId = conglomerateDescriptor.getConglomerateNumber();
                        indexRowGenerator = new IndexRowGenerator(this.indexType, this.unique, this.uniqueWithDuplicateNulls, array, this.isAscending, array.length);
                        this.conglomerateUUID = dataDictionary.getUUIDFactory().createUUID();
                        b = true;
                        break;
                    }
                }
            }
        }
        final boolean b3 = this.droppedConglomNum > -1L;
        final DataDescriptorGenerator dataDescriptorGenerator = dataDictionary.getDataDescriptorGenerator();
        if (b && !b3) {
            final ConglomerateDescriptor conglomerateDescriptor2 = dataDescriptorGenerator.newConglomerateDescriptor(this.conglomId, this.indexName, true, indexRowGenerator, this.isConstraint, this.conglomerateUUID, tableDescriptor.getUUID(), schemaDescriptor.getUUID());
            dataDictionary.addDescriptor(conglomerateDescriptor2, schemaDescriptor, 0, false, transactionExecute);
            tableDescriptor.getConglomerateDescriptorList().add(conglomerateDescriptor2);
        }
        Properties properties;
        if (this.properties != null) {
            properties = this.properties;
        }
        else {
            properties = new Properties();
        }
        properties.put("baseConglomerateId", Long.toString(tableDescriptor.getHeapConglomerateId()));
        if (this.uniqueWithDuplicateNulls) {
            if (dataDictionary.checkVersion(160, null)) {
                properties.put("uniqueWithDuplicateNulls", Boolean.toString(true));
            }
            else if (this.uniqueWithDuplicateNulls) {
                this.unique = true;
            }
        }
        properties.put("nUniqueColumns", Integer.toString(this.unique ? array.length : (array.length + 1)));
        properties.put("rowLocationColumn", Integer.toString(array.length));
        properties.put("nKeyFields", Integer.toString(array.length + 1));
        if (!b) {
            if (dataDictionary.checkVersion(160, null)) {
                indexRowGenerator = new IndexRowGenerator(this.indexType, this.unique, this.uniqueWithDuplicateNulls, array, this.isAscending, array.length);
            }
            else {
                indexRowGenerator = new IndexRowGenerator(this.indexType, this.unique, array, this.isAscending, array.length);
            }
        }
        RowLocationRetRowSource loadSorter = null;
        long sort = 0L;
        boolean b4 = false;
        final int n3 = this.forCreateTable ? 1 : 16;
        final int numberOfColumns = tableDescriptor.getNumberOfColumns();
        int n4 = 0;
        final FormatableBitSet set = new FormatableBitSet(numberOfColumns + 1);
        for (int k = 0; k < array.length; ++k) {
            set.set(array[k]);
        }
        final FormatableBitSet shift = RowUtil.shift(set, 1);
        final GroupFetchScanController openGroupFetchScan = transactionExecute.openGroupFetchScan(tableDescriptor.getHeapConglomerateId(), false, 0, 7, 5, shift, null, 0, null, null, 0);
        final ExecRow[] array2 = new ExecRow[n3];
        final ExecIndexRow[] array3 = new ExecIndexRow[n3];
        final ExecRow[] array4 = new ExecRow[n3];
        try {
            for (int l = 0; l < n3; ++l) {
                array2[l] = activation.getExecutionFactory().getValueRow(n);
                array3[l] = indexRowGenerator.getIndexRowTemplate();
                array4[l] = activation.getExecutionFactory().getValueRow(array.length);
            }
            this.indexTemplateRow = array3[0];
            final ColumnDescriptorList columnDescriptorList = tableDescriptor.getColumnDescriptorList();
            final int size = columnDescriptorList.size();
            int n5 = 0;
            int n6 = 0;
            while (n5 < size) {
                if (shift.get(n5)) {
                    ++n6;
                    final DataTypeDescriptor type = columnDescriptorList.elementAt(n5).getType();
                    for (int n7 = 0; n7 < n3; ++n7) {
                        array2[n7].setColumn(n5 + 1, type.getNull());
                        array4[n7].setColumn(n6, array2[n7].getColumn(n5 + 1));
                    }
                    n4 += type.getTypeId().getApproximateLengthInBytes(type);
                }
                ++n5;
            }
            final RowLocation[] array5 = new RowLocation[n3];
            for (int n8 = 0; n8 < n3; ++n8) {
                array5[n8] = openGroupFetchScan.newRowLocationTemplate();
                indexRowGenerator.getIndexRow(array4[n8], array5[n8], array3[n8], set);
            }
            if (b) {
                return;
            }
            Properties properties2 = null;
            int length;
            BasicSortObserver basicSortObserver;
            if (this.unique || this.uniqueWithDuplicateNulls) {
                String s = this.indexName;
                if (this.conglomerateUUID != null) {
                    final ConglomerateDescriptor conglomerateDescriptor3 = dataDictionary.getConglomerateDescriptor(this.conglomerateUUID);
                    if (this.isConstraint && conglomerateDescriptor3 != null && conglomerateDescriptor3.getUUID() != null && tableDescriptor != null) {
                        s = dataDictionary.getConstraintDescriptor(tableDescriptor, conglomerateDescriptor3.getUUID()).getConstraintName();
                    }
                }
                if (this.unique) {
                    length = array.length;
                    basicSortObserver = new UniqueIndexSortObserver(true, this.isConstraint, s, this.indexTemplateRow, true, tableDescriptor.getName());
                }
                else {
                    length = array.length + 1;
                    properties2 = new Properties();
                    properties2.put("implType", "sort almost unique external");
                    basicSortObserver = new UniqueWithDuplicateNullsIndexSortObserver(true, this.isConstraint, s, this.indexTemplateRow, true, tableDescriptor.getName());
                }
            }
            else {
                length = array.length + 1;
                basicSortObserver = new BasicSortObserver(true, false, this.indexTemplateRow, true);
            }
            final ColumnOrdering[] array6 = new ColumnOrdering[length];
            for (int n9 = 0; n9 < length; ++n9) {
                array6[n9] = new IndexColumnOrder(n9, (!this.unique && n9 >= length - 1) || this.isAscending[n9]);
            }
            sort = transactionExecute.createSort(properties2, this.indexTemplateRow.getRowArrayClone(), array6, basicSortObserver, false, openGroupFetchScan.getEstimatedRowCount(), n4);
            b4 = true;
            loadSorter = this.loadSorter(array2, array3, transactionExecute, openGroupFetchScan, sort, array5);
            this.conglomId = transactionExecute.createAndLoadConglomerate(this.indexType, this.indexTemplateRow.getRowArray(), array6, indexRowGenerator.getColumnCollationIds(tableDescriptor.getColumnDescriptorList()), properties, 0, loadSorter, null);
        }
        finally {
            if (openGroupFetchScan != null) {
                openGroupFetchScan.close();
            }
            if (loadSorter != null) {
                loadSorter.closeRowSource();
            }
            if (b4) {
                transactionExecute.dropSort(sort);
            }
        }
        final ConglomerateController openConglomerate = transactionExecute.openConglomerate(this.conglomId, false, 0, 7, 5);
        if (!openConglomerate.isKeyed()) {
            openConglomerate.close();
            throw StandardException.newException("X0X85.S", this.indexName, this.indexType);
        }
        openConglomerate.close();
        if (!b3) {
            final ConglomerateDescriptor conglomerateDescriptor4 = dataDescriptorGenerator.newConglomerateDescriptor(this.conglomId, this.indexName, true, indexRowGenerator, this.isConstraint, this.conglomerateUUID, tableDescriptor.getUUID(), schemaDescriptor.getUUID());
            dataDictionary.addDescriptor(conglomerateDescriptor4, schemaDescriptor, 0, false, transactionExecute);
            tableDescriptor.getConglomerateDescriptorList().add(conglomerateDescriptor4);
            this.conglomerateUUID = conglomerateDescriptor4.getUUID();
        }
        final CardinalityCounter cardinalityCounter = (CardinalityCounter)loadSorter;
        final long rowCount = cardinalityCounter.getRowCount();
        if (this.addStatistics(dataDictionary, indexRowGenerator, rowCount)) {
            final long[] cardinality = cardinalityCounter.getCardinality();
            for (int n10 = 0; n10 < cardinality.length; ++n10) {
                dataDictionary.addDescriptor(new StatisticsDescriptor(dataDictionary, dataDictionary.getUUIDFactory().createUUID(), this.conglomerateUUID, tableDescriptor.getUUID(), "I", new StatisticsImpl(rowCount, cardinality[n10]), n10 + 1), null, 14, true, transactionExecute);
            }
        }
    }
    
    private boolean addStatistics(final DataDictionary dataDictionary, final IndexRowGenerator indexRowGenerator, final long n) throws StandardException {
        boolean b = n > 0L;
        if (dataDictionary.checkVersion(210, null) && ((IndexStatisticsDaemonImpl)dataDictionary.getIndexStatsRefresher(false)).skipDisposableStats && b && indexRowGenerator.isUnique() && indexRowGenerator.numberOfOrderedColumns() == 1) {
            b = false;
        }
        return b;
    }
    
    ExecRow getIndexTemplateRow() {
        return this.indexTemplateRow;
    }
    
    long getCreatedConglomNumber() {
        return this.conglomId;
    }
    
    long getReplacedConglomNumber() {
        return this.droppedConglomNum;
    }
    
    UUID getCreatedUUID() {
        return this.conglomerateUUID;
    }
    
    private RowLocationRetRowSource loadSorter(final ExecRow[] array, final ExecIndexRow[] array2, final TransactionController transactionController, final GroupFetchScanController groupFetchScanController, final long n, final RowLocation[] array3) throws StandardException {
        long estimatedRowCount = 0L;
        final SortController openSort = transactionController.openSort(n);
        try {
            final int length = array.length;
            final DataValueDescriptor[][] array4 = new DataValueDescriptor[length][];
            for (int i = 0; i < length; ++i) {
                array4[i] = array[i].getRowArray();
            }
            int fetchNextGroup;
            while ((fetchNextGroup = groupFetchScanController.fetchNextGroup(array4, array3)) > 0) {
                for (int j = 0; j < fetchNextGroup; ++j) {
                    openSort.insert(array2[j].getRowArray());
                    ++estimatedRowCount;
                }
            }
            groupFetchScanController.setEstimatedRowCount(estimatedRowCount);
        }
        finally {
            openSort.completedInserts();
        }
        return new CardinalityCounter(transactionController.openSortRowSource(n));
    }
}
