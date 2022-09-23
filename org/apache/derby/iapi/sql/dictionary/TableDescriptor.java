// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.catalog.DependableFinder;
import java.util.Iterator;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.util.IdUtil;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import java.util.List;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.iapi.sql.depend.Provider;

public class TableDescriptor extends TupleDescriptor implements UniqueSQLObjectDescriptor, Provider, Dependent
{
    public static final int BASE_TABLE_TYPE = 0;
    public static final int SYSTEM_TABLE_TYPE = 1;
    public static final int VIEW_TYPE = 2;
    public static final int GLOBAL_TEMPORARY_TABLE_TYPE = 3;
    public static final int SYNONYM_TYPE = 4;
    public static final int VTI_TYPE = 5;
    public static final char ROW_LOCK_GRANULARITY = 'R';
    public static final char TABLE_LOCK_GRANULARITY = 'T';
    public static final char DEFAULT_LOCK_GRANULARITY = 'R';
    public static final int ISTATS_CREATE_THRESHOLD;
    public static final int ISTATS_ABSDIFF_THRESHOLD;
    public static final double ISTATS_LNDIFF_THRESHOLD;
    private char lockGranularity;
    private boolean onCommitDeleteRows;
    private boolean onRollbackDeleteRows;
    private boolean indexStatsUpToDate;
    private String indexStatsUpdateReason;
    SchemaDescriptor schema;
    String tableName;
    UUID oid;
    int tableType;
    private volatile long heapConglomNumber;
    ColumnDescriptorList columnDescriptorList;
    ConglomerateDescriptorList conglomerateDescriptorList;
    ConstraintDescriptorList constraintDescriptorList;
    private GenericDescriptorList triggerDescriptorList;
    ViewDescriptor viewDescriptor;
    private List statisticsDescriptorList;
    
    private FormatableBitSet referencedColumnMapGet() {
        return ((LanguageConnectionContext)ContextService.getContextOrNull("LanguageConnectionContext")).getReferencedColumnMap(this);
    }
    
    private void referencedColumnMapPut(final FormatableBitSet set) {
        final LanguageConnectionContext languageConnectionContext = (LanguageConnectionContext)ContextService.getContextOrNull("LanguageConnectionContext");
        if (languageConnectionContext != null) {
            languageConnectionContext.setReferencedColumnMap(this, set);
        }
    }
    
    public TableDescriptor(final DataDictionary dataDictionary, final String s, final SchemaDescriptor schemaDescriptor, final int n, final boolean onCommitDeleteRows, final boolean onRollbackDeleteRows) {
        this(dataDictionary, s, schemaDescriptor, n, '\0');
        this.onCommitDeleteRows = onCommitDeleteRows;
        this.onRollbackDeleteRows = onRollbackDeleteRows;
    }
    
    public TableDescriptor(final DataDictionary dataDictionary, final String tableName, final SchemaDescriptor schema, final int tableType, final char lockGranularity) {
        super(dataDictionary);
        this.indexStatsUpToDate = true;
        this.heapConglomNumber = -1L;
        this.schema = schema;
        this.tableName = tableName;
        this.tableType = tableType;
        this.lockGranularity = lockGranularity;
        this.conglomerateDescriptorList = new ConglomerateDescriptorList();
        this.columnDescriptorList = new ColumnDescriptorList();
        this.constraintDescriptorList = new ConstraintDescriptorList();
        this.triggerDescriptorList = new GenericDescriptorList();
    }
    
    public String getSchemaName() {
        return this.schema.getSchemaName();
    }
    
    public SchemaDescriptor getSchemaDescriptor() {
        return this.schema;
    }
    
    public String getName() {
        return this.tableName;
    }
    
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }
    
    public String getQualifiedName() {
        return IdUtil.mkQualifiedName(this.getSchemaName(), this.getName());
    }
    
    public UUID getUUID() {
        return this.oid;
    }
    
    public int getTableType() {
        return this.tableType;
    }
    
    public long getHeapConglomerateId() throws StandardException {
        ConglomerateDescriptor conglomerateDescriptor = null;
        if (this.heapConglomNumber != -1L) {
            return this.heapConglomNumber;
        }
        final ConglomerateDescriptor[] conglomerateDescriptors = this.getConglomerateDescriptors();
        for (int i = 0; i < conglomerateDescriptors.length; ++i) {
            conglomerateDescriptor = conglomerateDescriptors[i];
            if (!conglomerateDescriptor.isIndex()) {
                break;
            }
        }
        return this.heapConglomNumber = conglomerateDescriptor.getConglomerateNumber();
    }
    
    public int getNumberOfColumns() {
        return this.getColumnDescriptorList().size();
    }
    
    public FormatableBitSet getReferencedColumnMap() {
        return this.referencedColumnMapGet();
    }
    
    public void setReferencedColumnMap(final FormatableBitSet set) {
        this.referencedColumnMapPut(set);
    }
    
    public FormatableBitSet makeColumnMap(final ColumnDescriptorList list) {
        final FormatableBitSet set = new FormatableBitSet(this.columnDescriptorList.size() + 1);
        for (int size = list.size(), i = 0; i < size; ++i) {
            set.set(list.elementAt(i).getPosition());
        }
        return set;
    }
    
    public int getMaxColumnID() throws StandardException {
        int max = 1;
        for (int size = this.getColumnDescriptorList().size(), i = 0; i < size; ++i) {
            max = Math.max(max, this.columnDescriptorList.elementAt(i).getPosition());
        }
        return max;
    }
    
    public void setUUID(final UUID oid) {
        this.oid = oid;
    }
    
    public char getLockGranularity() {
        return this.lockGranularity;
    }
    
    public void setLockGranularity(final char lockGranularity) {
        this.lockGranularity = lockGranularity;
    }
    
    public boolean isOnRollbackDeleteRows() {
        return this.onRollbackDeleteRows;
    }
    
    public boolean isOnCommitDeleteRows() {
        return this.onCommitDeleteRows;
    }
    
    public void resetHeapConglomNumber() {
        this.heapConglomNumber = -1L;
    }
    
    public ExecRow getEmptyExecRow() throws StandardException {
        final int numberOfColumns = this.getNumberOfColumns();
        final ExecRow valueRow = this.getDataDictionary().getExecutionFactory().getValueRow(numberOfColumns);
        for (int i = 0; i < numberOfColumns; ++i) {
            valueRow.setColumn(i + 1, this.columnDescriptorList.elementAt(i).getType().getNull());
        }
        return valueRow;
    }
    
    public int[] getColumnCollationIds() throws StandardException {
        final int[] array = new int[this.getNumberOfColumns()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = this.columnDescriptorList.elementAt(i).getType().getCollationType();
        }
        return array;
    }
    
    public ConglomerateDescriptorList getConglomerateDescriptorList() {
        return this.conglomerateDescriptorList;
    }
    
    public ViewDescriptor getViewDescriptor() {
        return this.viewDescriptor;
    }
    
    public void setViewDescriptor(final ViewDescriptor viewDescriptor) {
        this.viewDescriptor = viewDescriptor;
    }
    
    public boolean isPersistent() {
        return this.tableType != 3 && super.isPersistent();
    }
    
    public boolean isSynonymDescriptor() {
        return this.tableType == 4;
    }
    
    public int getTotalNumberOfIndexes() throws StandardException {
        return this.getQualifiedNumberOfIndexes(0, false);
    }
    
    public int getQualifiedNumberOfIndexes(final int n, final boolean b) {
        int n2 = 0;
        for (final ConglomerateDescriptor conglomerateDescriptor : this.conglomerateDescriptorList) {
            if (conglomerateDescriptor.isIndex()) {
                final IndexRowGenerator indexDescriptor = conglomerateDescriptor.getIndexDescriptor();
                if (indexDescriptor.numberOfOrderedColumns() < n && (!b || indexDescriptor.isUnique())) {
                    continue;
                }
                ++n2;
            }
        }
        return n2;
    }
    
    public void getAllRelevantTriggers(final int n, final int[] array, final GenericDescriptorList list) throws StandardException {
        for (final TriggerDescriptor e : this.getDataDictionary().getTriggerDescriptors(this)) {
            if (e.needsToFire(n, array)) {
                list.add(e);
            }
        }
    }
    
    public void getAllRelevantConstraints(final int n, final boolean b, final int[] array, final boolean[] array2, final ConstraintDescriptorList list) throws StandardException {
        final ConstraintDescriptorList constraintDescriptors = this.getDataDictionary().getConstraintDescriptors(this);
        for (int size = constraintDescriptors.size(), i = 0; i < size; ++i) {
            final ConstraintDescriptor element = constraintDescriptors.elementAt(i);
            if (!b || element.getConstraintType() != 4) {
                if (!array2[0] && element instanceof ReferencedKeyConstraintDescriptor && n != 3 && n != 2) {
                    array2[0] = ((ReferencedKeyConstraintDescriptor)element).hasSelfReferencingFK(constraintDescriptors, 1);
                }
                if (element.needsToFire(n, array)) {
                    if (element instanceof ReferencedKeyConstraintDescriptor && (n == 3 || n == 2)) {
                        array2[0] = true;
                    }
                    list.add((ReferencedKeyConstraintDescriptor)element);
                }
            }
        }
    }
    
    public DependableFinder getDependableFinder() {
        if (this.referencedColumnMapGet() == null) {
            return this.getDependableFinder(137);
        }
        return this.getColumnDependableFinder(393, this.referencedColumnMapGet().getByteArray());
    }
    
    public String getObjectName() {
        if (this.referencedColumnMapGet() == null) {
            return this.tableName;
        }
        final StringBuffer sb = new StringBuffer();
        sb.append(this.tableName);
        int n = 1;
        for (int i = 0; i < this.columnDescriptorList.size(); ++i) {
            final ColumnDescriptor element = this.columnDescriptorList.elementAt(i);
            if (this.referencedColumnMapGet().isSet(element.getPosition())) {
                if (n != 0) {
                    sb.append("(").append(element.getColumnName());
                    n = 0;
                }
                else {
                    sb.append(", ").append(element.getColumnName());
                }
            }
        }
        if (n == 0) {
            sb.append(")");
        }
        return sb.toString();
    }
    
    public UUID getObjectID() {
        return this.oid;
    }
    
    public String getClassType() {
        return "Table";
    }
    
    public String toString() {
        return "";
    }
    
    public ColumnDescriptorList getColumnDescriptorList() {
        return this.columnDescriptorList;
    }
    
    public ColumnDescriptorList getGeneratedColumns() {
        final ColumnDescriptorList columnDescriptorList = this.getColumnDescriptorList();
        final ColumnDescriptorList list = new ColumnDescriptorList();
        for (int size = columnDescriptorList.size(), i = 0; i < size; ++i) {
            final ColumnDescriptor element = columnDescriptorList.elementAt(i);
            if (element.hasGenerationClause()) {
                list.add(this.oid, element);
            }
        }
        return list;
    }
    
    public int[] getColumnIDs(final String[] array) {
        final int length = array.length;
        final int[] array2 = new int[length];
        for (int i = 0; i < length; ++i) {
            array2[i] = this.getColumnDescriptor(array[i]).getPosition();
        }
        return array2;
    }
    
    public ConstraintDescriptorList getConstraintDescriptorList() throws StandardException {
        return this.constraintDescriptorList;
    }
    
    public void setConstraintDescriptorList(final ConstraintDescriptorList constraintDescriptorList) {
        this.constraintDescriptorList = constraintDescriptorList;
    }
    
    public void emptyConstraintDescriptorList() throws StandardException {
        this.constraintDescriptorList = new ConstraintDescriptorList();
    }
    
    public ReferencedKeyConstraintDescriptor getPrimaryKey() throws StandardException {
        return this.getDataDictionary().getConstraintDescriptors(this).getPrimaryKey();
    }
    
    public GenericDescriptorList getTriggerDescriptorList() throws StandardException {
        return this.triggerDescriptorList;
    }
    
    public void setTriggerDescriptorList(final GenericDescriptorList triggerDescriptorList) {
        this.triggerDescriptorList = triggerDescriptorList;
    }
    
    public void emptyTriggerDescriptorList() throws StandardException {
        this.triggerDescriptorList = new GenericDescriptorList();
    }
    
    public boolean tableNameEquals(final String s, final String anObject) {
        final String schemaName = this.getSchemaName();
        if (schemaName == null || anObject == null) {
            return this.tableName.equals(s);
        }
        return schemaName.equals(anObject) && this.tableName.equals(s);
    }
    
    public void removeConglomerateDescriptor(final ConglomerateDescriptor conglomerateDescriptor) throws StandardException {
        this.conglomerateDescriptorList.dropConglomerateDescriptor(this.getUUID(), conglomerateDescriptor);
    }
    
    public void removeConstraintDescriptor(final ConstraintDescriptor o) throws StandardException {
        this.constraintDescriptorList.remove(o);
    }
    
    public ColumnDescriptor getColumnDescriptor(final String s) {
        return this.columnDescriptorList.getColumnDescriptor(this.oid, s);
    }
    
    public ColumnDescriptor getColumnDescriptor(final int n) {
        return this.columnDescriptorList.getColumnDescriptor(this.oid, n);
    }
    
    public ConglomerateDescriptor[] getConglomerateDescriptors() {
        final ConglomerateDescriptor[] a = new ConglomerateDescriptor[this.conglomerateDescriptorList.size()];
        this.conglomerateDescriptorList.toArray(a);
        return a;
    }
    
    public ConglomerateDescriptor getConglomerateDescriptor(final long n) throws StandardException {
        return this.conglomerateDescriptorList.getConglomerateDescriptor(n);
    }
    
    public ConglomerateDescriptor[] getConglomerateDescriptors(final long n) throws StandardException {
        return this.conglomerateDescriptorList.getConglomerateDescriptors(n);
    }
    
    public ConglomerateDescriptor getConglomerateDescriptor(final UUID uuid) throws StandardException {
        return this.conglomerateDescriptorList.getConglomerateDescriptor(uuid);
    }
    
    public ConglomerateDescriptor[] getConglomerateDescriptors(final UUID uuid) throws StandardException {
        return this.conglomerateDescriptorList.getConglomerateDescriptors(uuid);
    }
    
    public IndexLister getIndexLister() throws StandardException {
        return new IndexLister(this);
    }
    
    public boolean tableHasAutoincrement() {
        for (int size = this.getColumnDescriptorList().size(), i = 0; i < size; ++i) {
            if (this.columnDescriptorList.elementAt(i).isAutoincrement()) {
                return true;
            }
        }
        return false;
    }
    
    public String[] getColumnNamesArray() {
        final int numberOfColumns = this.getNumberOfColumns();
        final String[] array = new String[numberOfColumns];
        for (int i = 0; i < numberOfColumns; ++i) {
            array[i] = this.getColumnDescriptor(i + 1).getColumnName();
        }
        return array;
    }
    
    public long[] getAutoincIncrementArray() {
        if (!this.tableHasAutoincrement()) {
            return null;
        }
        final int numberOfColumns = this.getNumberOfColumns();
        final long[] array = new long[numberOfColumns];
        for (int i = 0; i < numberOfColumns; ++i) {
            final ColumnDescriptor columnDescriptor = this.getColumnDescriptor(i + 1);
            if (columnDescriptor.isAutoincrement()) {
                array[i] = columnDescriptor.getAutoincInc();
            }
        }
        return array;
    }
    
    public synchronized List getStatistics() throws StandardException {
        if (this.statisticsDescriptorList != null) {
            return this.statisticsDescriptorList;
        }
        return this.statisticsDescriptorList = this.getDataDictionary().getStatisticsDescriptors(this);
    }
    
    public void markForIndexStatsUpdate(final long n) throws StandardException {
        final List statistics = this.getStatistics();
        if (statistics.isEmpty() && n >= TableDescriptor.ISTATS_CREATE_THRESHOLD) {
            this.indexStatsUpToDate = false;
            this.indexStatsUpdateReason = "no stats, row-estimate=" + n;
            return;
        }
        final Iterator<StatisticsDescriptor> iterator = statistics.iterator();
        while (iterator.hasNext()) {
            final long rowEstimate = iterator.next().getStatistic().getRowEstimate();
            if (Math.abs(n - rowEstimate) >= TableDescriptor.ISTATS_ABSDIFF_THRESHOLD) {
                final double abs = Math.abs(Math.log((double)rowEstimate) - Math.log((double)n));
                if (Double.compare(abs, TableDescriptor.ISTATS_LNDIFF_THRESHOLD) == 1) {
                    this.indexStatsUpToDate = false;
                    this.indexStatsUpdateReason = "t-est=" + n + ", i-est=" + rowEstimate + " => cmp=" + abs;
                    break;
                }
                continue;
            }
        }
    }
    
    public boolean getAndClearIndexStatsIsUpToDate() {
        final boolean indexStatsUpToDate = this.indexStatsUpToDate;
        this.indexStatsUpToDate = true;
        return indexStatsUpToDate;
    }
    
    public String getIndexStatsUpdateReason() {
        return this.indexStatsUpdateReason;
    }
    
    public boolean statisticsExist(final ConglomerateDescriptor conglomerateDescriptor) throws StandardException {
        final List statistics = this.getStatistics();
        if (conglomerateDescriptor == null) {
            return statistics.size() > 0;
        }
        final UUID uuid = conglomerateDescriptor.getUUID();
        final Iterator<StatisticsDescriptor> iterator = statistics.iterator();
        while (iterator.hasNext()) {
            if (uuid.equals(iterator.next().getReferenceID())) {
                return true;
            }
        }
        return false;
    }
    
    public double selectivityForConglomerate(final ConglomerateDescriptor conglomerateDescriptor, final int n) throws StandardException {
        final UUID uuid = conglomerateDescriptor.getUUID();
        for (final StatisticsDescriptor statisticsDescriptor : this.getStatistics()) {
            if (!uuid.equals(statisticsDescriptor.getReferenceID())) {
                continue;
            }
            if (statisticsDescriptor.getColumnCount() != n) {
                continue;
            }
            return statisticsDescriptor.getStatistic().selectivity(null);
        }
        return Math.pow(0.1, n);
    }
    
    public String getDescriptorName() {
        return this.tableName;
    }
    
    public String getDescriptorType() {
        return (this.tableType == 4) ? "Synonym" : "Table/View";
    }
    
    public synchronized boolean isValid() {
        return true;
    }
    
    public void prepareToInvalidate(final Provider provider, final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
        throw StandardException.newException("X0Y29.S", this.getDataDictionary().getDependencyManager().getActionString(n), provider.getObjectName(), this.getQualifiedName());
    }
    
    public void makeInvalid(final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
    }
    
    static {
        ISTATS_CREATE_THRESHOLD = PropertyUtil.getSystemInt("derby.storage.indexStats.debug.createThreshold", 100);
        ISTATS_ABSDIFF_THRESHOLD = PropertyUtil.getSystemInt("derby.storage.indexStats.debug.absdiffThreshold", 1000);
        double double1 = 1.0;
        try {
            final String systemProperty = PropertyUtil.getSystemProperty("derby.storage.indexStats.debug.lndiffThreshold");
            if (systemProperty != null) {
                double1 = Double.parseDouble(systemProperty);
            }
        }
        catch (NumberFormatException ex) {}
        ISTATS_LNDIFF_THRESHOLD = double1;
    }
}
