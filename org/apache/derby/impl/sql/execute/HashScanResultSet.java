// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.store.access.RowUtil;
import org.apache.derby.iapi.store.access.KeyHasher;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.FormatableArrayHolder;
import org.apache.derby.iapi.services.io.FormatableIntHolder;
import org.apache.derby.iapi.sql.Activation;
import java.util.Properties;
import org.apache.derby.iapi.store.access.BackingStoreHashtable;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.services.loader.GeneratedMethod;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import java.util.List;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import org.apache.derby.iapi.sql.execute.CursorResultSet;

public class HashScanResultSet extends ScanResultSet implements CursorResultSet
{
    private boolean hashtableBuilt;
    private ExecIndexRow startPosition;
    private ExecIndexRow stopPosition;
    protected ExecRow compactRow;
    protected boolean firstNext;
    private int numFetchedOnNext;
    private int entryVectorSize;
    private List entryVector;
    private long conglomId;
    protected StaticCompiledOpenConglomInfo scoci;
    private GeneratedMethod startKeyGetter;
    private int startSearchOperator;
    private GeneratedMethod stopKeyGetter;
    private int stopSearchOperator;
    public Qualifier[][] scanQualifiers;
    public Qualifier[][] nextQualifiers;
    private int initialCapacity;
    private float loadFactor;
    private int maxCapacity;
    public String tableName;
    public String userSuppliedOptimizerOverrides;
    public String indexName;
    public boolean forUpdate;
    private boolean runTimeStatisticsOn;
    public int[] keyColumns;
    private boolean sameStartStopPosition;
    private boolean skipNullKeyColumns;
    private boolean keepAfterCommit;
    protected BackingStoreHashtable hashtable;
    protected boolean eliminateDuplicates;
    public Properties scanProperties;
    public String startPositionString;
    public String stopPositionString;
    public int hashtableSize;
    public boolean isConstraint;
    public static final int DEFAULT_INITIAL_CAPACITY = -1;
    public static final float DEFAULT_LOADFACTOR = -1.0f;
    public static final int DEFAULT_MAX_CAPACITY = -1;
    
    HashScanResultSet(final long conglomId, final StaticCompiledOpenConglomInfo scoci, final Activation activation, final int n, final int n2, final GeneratedMethod startKeyGetter, final int startSearchOperator, final GeneratedMethod stopKeyGetter, final int stopSearchOperator, final boolean sameStartStopPosition, final Qualifier[][] scanQualifiers, final Qualifier[][] nextQualifiers, final int initialCapacity, final float loadFactor, final int maxCapacity, final int n3, final String tableName, final String userSuppliedOptimizerOverrides, final String indexName, final boolean isConstraint, final boolean forUpdate, final int n4, final int n5, final boolean b, final int n6, final boolean skipNullKeyColumns, final double n7, final double n8) throws StandardException {
        super(activation, n2, n, n5, b, n6, n4, n7, n8);
        this.firstNext = true;
        this.scoci = scoci;
        this.conglomId = conglomId;
        this.startKeyGetter = startKeyGetter;
        this.startSearchOperator = startSearchOperator;
        this.stopKeyGetter = stopKeyGetter;
        this.stopSearchOperator = stopSearchOperator;
        this.sameStartStopPosition = sameStartStopPosition;
        this.scanQualifiers = scanQualifiers;
        this.nextQualifiers = nextQualifiers;
        this.initialCapacity = initialCapacity;
        this.loadFactor = loadFactor;
        this.maxCapacity = maxCapacity;
        this.tableName = tableName;
        this.userSuppliedOptimizerOverrides = userSuppliedOptimizerOverrides;
        this.indexName = indexName;
        this.isConstraint = isConstraint;
        this.forUpdate = forUpdate;
        this.skipNullKeyColumns = skipNullKeyColumns;
        this.keepAfterCommit = activation.getResultSetHoldability();
        final FormatableIntHolder[] array = (FormatableIntHolder[])((FormatableArrayHolder)activation.getPreparedStatement().getSavedObject(n3)).getArray(FormatableIntHolder.class);
        this.keyColumns = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            this.keyColumns[i] = array[i].getInt();
        }
        this.runTimeStatisticsOn = this.getLanguageConnectionContext().getRunTimeStatisticsMode();
        this.compactRow = this.getCompactRow(this.candidate, this.accessedCols, false);
        this.recordConstructorTime();
    }
    
    boolean canGetInstantaneousLocks() {
        return true;
    }
    
    public void openCore() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        final TransactionController transactionController = this.activation.getTransactionController();
        this.initIsolationLevel();
        if (this.startKeyGetter != null) {
            this.startPosition = (ExecIndexRow)this.startKeyGetter.invoke(this.activation);
            if (this.sameStartStopPosition) {
                this.stopPosition = this.startPosition;
            }
        }
        if (this.stopKeyGetter != null) {
            this.stopPosition = (ExecIndexRow)this.stopKeyGetter.invoke(this.activation);
        }
        if (!this.skipScan(this.startPosition, this.stopPosition)) {
            if (!this.hashtableBuilt) {
                this.hashtable = transactionController.createBackingStoreHashtableFromScan(this.conglomId, this.forUpdate ? 4 : 0, this.lockMode, this.isolationLevel, this.accessedCols, (DataValueDescriptor[])((this.startPosition == null) ? null : this.startPosition.getRowArray()), this.startSearchOperator, this.scanQualifiers, (DataValueDescriptor[])((this.stopPosition == null) ? null : this.stopPosition.getRowArray()), this.stopSearchOperator, -1L, this.keyColumns, this.eliminateDuplicates, -1L, this.maxCapacity, this.initialCapacity, this.loadFactor, this.runTimeStatisticsOn, this.skipNullKeyColumns, this.keepAfterCommit);
                if (this.runTimeStatisticsOn) {
                    this.hashtableSize = this.hashtable.size();
                    if (this.scanProperties == null) {
                        this.scanProperties = new Properties();
                    }
                    try {
                        if (this.hashtable != null) {
                            this.hashtable.getAllRuntimeStats(this.scanProperties);
                        }
                    }
                    catch (StandardException ex) {}
                }
                this.hashtableBuilt = true;
                this.activation.informOfRowCount(this, this.hashtableSize);
            }
        }
        this.isOpen = true;
        this.resetProbeVariables();
        ++this.numOpens;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    public void reopenCore() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        this.resetProbeVariables();
        ++this.numOpens;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    private void resetProbeVariables() throws StandardException {
        this.firstNext = true;
        this.numFetchedOnNext = 0;
        this.entryVector = null;
        this.entryVectorSize = 0;
        if (this.nextQualifiers != null) {
            this.clearOrderableCache(this.nextQualifiers);
        }
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        ExecRow compactRow = null;
        DataValueDescriptor[] array = null;
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen && this.hashtableBuilt) {
            do {
                if (this.firstNext) {
                    this.firstNext = false;
                    Object value;
                    if (this.keyColumns.length == 1) {
                        value = this.hashtable.get(this.nextQualifiers[0][0].getOrderable());
                    }
                    else {
                        KeyHasher keyHasher = new KeyHasher(this.keyColumns.length);
                        for (int i = 0; i < this.keyColumns.length; ++i) {
                            if (this.nextQualifiers[0][i].getOrderable() == null) {
                                keyHasher = null;
                                break;
                            }
                            keyHasher.setObject(i, this.nextQualifiers[0][i].getOrderable());
                        }
                        value = ((keyHasher == null) ? null : this.hashtable.get(keyHasher));
                    }
                    if (value instanceof List) {
                        this.entryVector = (List)value;
                        this.entryVectorSize = this.entryVector.size();
                        array = this.entryVector.get(0);
                    }
                    else {
                        this.entryVector = null;
                        this.entryVectorSize = 0;
                        array = (DataValueDescriptor[])value;
                    }
                }
                else if (this.numFetchedOnNext < this.entryVectorSize) {
                    array = this.entryVector.get(this.numFetchedOnNext);
                }
                if (array != null) {
                    if (RowUtil.qualifyRow(array, this.nextQualifiers)) {
                        this.setCompatRow(this.compactRow, array);
                        ++this.rowsSeen;
                        compactRow = this.compactRow;
                    }
                    else {
                        compactRow = null;
                    }
                    ++this.numFetchedOnNext;
                }
                else {
                    compactRow = null;
                }
            } while (compactRow == null && this.numFetchedOnNext < this.entryVectorSize);
        }
        this.setCurrentRow(compactRow);
        this.nextTime += this.getElapsedMillis(this.beginTime);
        return compactRow;
    }
    
    public void close() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            this.clearCurrentRow();
            if (this.hashtableBuilt) {
                this.scanProperties = this.getScanProperties();
                if (this.runTimeStatisticsOn) {
                    this.startPositionString = this.printStartPosition();
                    this.stopPositionString = this.printStopPosition();
                }
                this.hashtable.close();
                this.hashtable = null;
                this.hashtableBuilt = false;
            }
            this.startPosition = null;
            this.stopPosition = null;
            super.close();
        }
        this.closeTime += this.getElapsedMillis(this.beginTime);
    }
    
    public long getTimeSpent(final int n) {
        final long n2 = this.constructorTime + this.openTime + this.nextTime + this.closeTime;
        if (n == 0) {
            return n2;
        }
        return n2;
    }
    
    public boolean requiresRelocking() {
        return this.isolationLevel == 2 || this.isolationLevel == 3 || this.isolationLevel == 1;
    }
    
    public RowLocation getRowLocation() throws StandardException {
        if (!this.isOpen) {
            return null;
        }
        if (!this.hashtableBuilt) {
            return null;
        }
        return (RowLocation)this.currentRow.getColumn(this.currentRow.nColumns());
    }
    
    public ExecRow getCurrentRow() throws StandardException {
        return null;
    }
    
    public String printStartPosition() {
        return this.printPosition(this.startSearchOperator, this.startKeyGetter, this.startPosition);
    }
    
    public String printStopPosition() {
        if (this.sameStartStopPosition) {
            return this.printPosition(this.stopSearchOperator, this.startKeyGetter, this.startPosition);
        }
        return this.printPosition(this.stopSearchOperator, this.stopKeyGetter, this.stopPosition);
    }
    
    private String printPosition(final int i, final GeneratedMethod generatedMethod, final ExecIndexRow execIndexRow) {
        final String str = "";
        if (generatedMethod == null) {
            return "\t" + MessageService.getTextMessage("42Z37.U") + "\n";
        }
        ExecIndexRow execIndexRow2;
        try {
            execIndexRow2 = (ExecIndexRow)generatedMethod.invoke(this.activation);
        }
        catch (StandardException ex) {
            if (execIndexRow == null) {
                return "\t" + MessageService.getTextMessage("42Z38.U");
            }
            return "\t" + MessageService.getTextMessage("42Z39.U") + "\n";
        }
        if (execIndexRow2 == null) {
            return "\t" + MessageService.getTextMessage("42Z37.U") + "\n";
        }
        String string = null;
        switch (i) {
            case 1: {
                string = ">=";
                break;
            }
            case -1: {
                string = ">";
                break;
            }
            default: {
                string = "unknown value (" + i + ")";
                break;
            }
        }
        String s = str + "\t" + MessageService.getTextMessage("42Z40.U", string, String.valueOf(execIndexRow2.nColumns())) + "\n" + "\t" + MessageService.getTextMessage("42Z41.U") + "\n";
        boolean b = false;
        for (int j = 0; j < execIndexRow2.nColumns(); ++j) {
            if (execIndexRow2.areNullsOrdered(j)) {
                s = s + j + " ";
                b = true;
            }
            if (b && j == execIndexRow2.nColumns() - 1) {
                s += "\n";
            }
        }
        return s;
    }
    
    public Properties getScanProperties() {
        return this.scanProperties;
    }
    
    public boolean isForUpdate() {
        return this.forUpdate;
    }
}
