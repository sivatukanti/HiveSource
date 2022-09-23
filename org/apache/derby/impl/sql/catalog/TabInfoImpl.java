// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.services.io.StreamStorable;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import java.util.Properties;
import org.apache.derby.iapi.store.access.ScanController;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.execute.RowChanger;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.execute.TupleFilter;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.IndexRowGenerator;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.dictionary.CatalogRowFactory;

class TabInfoImpl
{
    static final int ROWNOTDUPLICATE = -1;
    private IndexInfoImpl[] indexes;
    private long heapConglomerate;
    private int numIndexesSet;
    private boolean heapSet;
    private final CatalogRowFactory crf;
    private boolean computedStreamStorableHeapColIds;
    private int[] streamStorableHeapColIds;
    
    TabInfoImpl(final CatalogRowFactory crf) {
        this.computedStreamStorableHeapColIds = false;
        this.heapConglomerate = -1L;
        this.crf = crf;
        final int numIndexes = crf.getNumIndexes();
        if (numIndexes > 0) {
            this.indexes = new IndexInfoImpl[numIndexes];
            for (int i = 0; i < numIndexes; ++i) {
                this.indexes[i] = new IndexInfoImpl(i, crf);
            }
        }
    }
    
    long getHeapConglomerate() {
        return this.heapConglomerate;
    }
    
    void setHeapConglomerate(final long heapConglomerate) {
        this.heapConglomerate = heapConglomerate;
        this.heapSet = true;
    }
    
    long getIndexConglomerate(final int n) {
        return this.indexes[n].getConglomerateNumber();
    }
    
    void setIndexConglomerate(final int n, final long conglomerateNumber) {
        this.indexes[n].setConglomerateNumber(conglomerateNumber);
        ++this.numIndexesSet;
    }
    
    void setIndexConglomerate(final ConglomerateDescriptor conglomerateDescriptor) {
        final String conglomerateName = conglomerateDescriptor.getConglomerateName();
        for (int i = 0; i < this.indexes.length; ++i) {
            if (this.indexes[i].getIndexName().equals(conglomerateName)) {
                this.indexes[i].setConglomerateNumber(conglomerateDescriptor.getConglomerateNumber());
                break;
            }
        }
        ++this.numIndexesSet;
    }
    
    String getTableName() {
        return this.crf.getCatalogName();
    }
    
    String getIndexName(final int n) {
        return this.indexes[n].getIndexName();
    }
    
    CatalogRowFactory getCatalogRowFactory() {
        return this.crf;
    }
    
    boolean isComplete() {
        return this.heapSet && (this.indexes == null || this.indexes.length == this.numIndexesSet);
    }
    
    int getIndexColumnCount(final int n) {
        return this.indexes[n].getColumnCount();
    }
    
    IndexRowGenerator getIndexRowGenerator(final int n) {
        return this.indexes[n].getIndexRowGenerator();
    }
    
    void setIndexRowGenerator(final int n, final IndexRowGenerator indexRowGenerator) {
        this.indexes[n].setIndexRowGenerator(indexRowGenerator);
    }
    
    int getNumberOfIndexes() {
        if (this.indexes == null) {
            return 0;
        }
        return this.indexes.length;
    }
    
    int getBaseColumnPosition(final int n, final int n2) {
        return this.indexes[n].getBaseColumnPosition(n2);
    }
    
    boolean isIndexUnique(final int n) {
        return this.indexes[n].isIndexUnique();
    }
    
    int insertRow(final ExecRow execRow, final TransactionController transactionController) throws StandardException {
        return this.insertRowListImpl(new ExecRow[] { execRow }, transactionController, new RowLocation[] { null });
    }
    
    int insertRowList(final ExecRow[] array, final TransactionController transactionController) throws StandardException {
        return this.insertRowListImpl(array, transactionController, new RowLocation[1]);
    }
    
    private int insertRowListImpl(final ExecRow[] array, final TransactionController transactionController, final RowLocation[] array2) throws StandardException {
        int n = -1;
        final int numIndexes = this.crf.getNumIndexes();
        final ConglomerateController[] array3 = new ConglomerateController[numIndexes];
        final ConglomerateController openConglomerate = transactionController.openConglomerate(this.getHeapConglomerate(), false, 4, 6, 4);
        for (int i = 0; i < numIndexes; ++i) {
            final long indexConglomerate = this.getIndexConglomerate(i);
            if (indexConglomerate > -1L) {
                array3[i] = transactionController.openConglomerate(indexConglomerate, false, 4, 6, 4);
            }
        }
        final RowLocation rowLocationTemplate = openConglomerate.newRowLocationTemplate();
        array2[0] = rowLocationTemplate;
        for (int j = 0; j < array.length; ++j) {
            final ExecRow execRow = array[j];
            openConglomerate.insertAndFetchLocation(execRow.getRowArray(), rowLocationTemplate);
            for (int k = 0; k < numIndexes; ++k) {
                if (array3[k] != null) {
                    if (array3[k].insert(this.getIndexRowFromHeapRow(this.getIndexRowGenerator(k), rowLocationTemplate, execRow).getRowArray()) == 1) {
                        n = j;
                    }
                }
            }
        }
        for (int l = 0; l < numIndexes; ++l) {
            if (array3[l] != null) {
                array3[l].close();
            }
        }
        openConglomerate.close();
        return n;
    }
    
    int deleteRow(final TransactionController transactionController, final ExecIndexRow execIndexRow, final int n) throws StandardException {
        return this.deleteRows(transactionController, execIndexRow, 1, null, null, execIndexRow, -1, n, true);
    }
    
    int deleteRow(final TransactionController transactionController, final ExecIndexRow execIndexRow, final int n, final boolean b) throws StandardException {
        return this.deleteRows(transactionController, execIndexRow, 1, null, null, execIndexRow, -1, n, b);
    }
    
    int deleteRows(final TransactionController transactionController, final ExecIndexRow execIndexRow, final int n, final Qualifier[][] array, final TupleFilter tupleFilter, final ExecIndexRow execIndexRow2, final int n2, final int n3) throws StandardException {
        return this.deleteRows(transactionController, execIndexRow, n, array, tupleFilter, execIndexRow2, n2, n3, true);
    }
    
    private int deleteRows(final TransactionController transactionController, final ExecIndexRow execIndexRow, final int n, final Qualifier[][] array, final TupleFilter tupleFilter, final ExecIndexRow execIndexRow2, final int n2, final int n3, final boolean b) throws StandardException {
        final ExecRow emptyRow = this.crf.makeEmptyRow();
        int n4 = 0;
        boolean equals = true;
        final RowChanger rowChanger = this.getRowChanger(transactionController, null, emptyRow);
        final int n5 = (execIndexRow != null && execIndexRow2 != null) ? 6 : 7;
        final int n6 = (execIndexRow != null && execIndexRow2 != null && execIndexRow == execIndexRow2) ? 4 : 5;
        rowChanger.open(n5, b);
        final DataValueDescriptor[] array2 = (DataValueDescriptor[])((execIndexRow == null) ? null : execIndexRow.getRowArray());
        final DataValueDescriptor[] array3 = (DataValueDescriptor[])((execIndexRow2 == null) ? null : execIndexRow2.getRowArray());
        final ConglomerateController openConglomerate = transactionController.openConglomerate(this.getHeapConglomerate(), false, 0x4 | (b ? 0 : 128), n5, 4);
        final ScanController openScan = transactionController.openScan(this.getIndexConglomerate(n3), false, 0x4 | (b ? 0 : 128), n5, n6, null, array2, n, array, array3, n2);
        final ExecIndexRow indexRowFromHeapRow = this.getIndexRowFromHeapRow(this.getIndexRowGenerator(n3), openConglomerate.newRowLocationTemplate(), this.crf.makeEmptyRow());
        while (openScan.fetchNext(indexRowFromHeapRow.getRowArray())) {
            final RowLocation rowLocation = (RowLocation)indexRowFromHeapRow.getColumn(indexRowFromHeapRow.nColumns());
            openConglomerate.fetch(rowLocation, emptyRow.getRowArray(), null);
            if (tupleFilter != null) {
                equals = tupleFilter.execute(emptyRow).equals(true);
            }
            if (equals) {
                rowChanger.deleteRow(emptyRow, rowLocation);
                ++n4;
            }
        }
        openConglomerate.close();
        openScan.close();
        rowChanger.close();
        return n4;
    }
    
    ExecRow getRow(final TransactionController transactionController, final ExecIndexRow execIndexRow, final int n) throws StandardException {
        final ConglomerateController openConglomerate = transactionController.openConglomerate(this.getHeapConglomerate(), false, 0, 6, 4);
        try {
            return this.getRow(transactionController, openConglomerate, execIndexRow, n);
        }
        finally {
            openConglomerate.close();
        }
    }
    
    RowLocation getRowLocation(final TransactionController transactionController, final ExecIndexRow execIndexRow, final int n) throws StandardException {
        final ConglomerateController openConglomerate = transactionController.openConglomerate(this.getHeapConglomerate(), false, 0, 6, 4);
        try {
            final RowLocation[] array = { null };
            this.getRowInternal(transactionController, openConglomerate, execIndexRow, n, array);
            return array[0];
        }
        finally {
            openConglomerate.close();
        }
    }
    
    ExecRow getRow(final TransactionController transactionController, final ConglomerateController conglomerateController, final ExecIndexRow execIndexRow, final int n) throws StandardException {
        return this.getRowInternal(transactionController, conglomerateController, execIndexRow, n, new RowLocation[1]);
    }
    
    private ExecRow getRowInternal(final TransactionController transactionController, final ConglomerateController conglomerateController, final ExecIndexRow execIndexRow, final int n, final RowLocation[] array) throws StandardException {
        final ExecRow emptyRow = this.crf.makeEmptyRow();
        final ScanController openScan = transactionController.openScan(this.getIndexConglomerate(n), false, 0, 6, 4, null, execIndexRow.getRowArray(), 1, null, execIndexRow.getRowArray(), -1);
        final ExecIndexRow indexRowFromHeapRow = this.getIndexRowFromHeapRow(this.getIndexRowGenerator(n), conglomerateController.newRowLocationTemplate(), this.crf.makeEmptyRow());
        try {
            if (openScan.fetchNext(indexRowFromHeapRow.getRowArray())) {
                conglomerateController.fetch(array[0] = (RowLocation)indexRowFromHeapRow.getColumn(indexRowFromHeapRow.nColumns()), emptyRow.getRowArray(), null);
                return emptyRow;
            }
            return null;
        }
        finally {
            openScan.close();
        }
    }
    
    void updateRow(final ExecIndexRow execIndexRow, final ExecRow execRow, final int n, final boolean[] array, final int[] array2, final TransactionController transactionController) throws StandardException {
        this.updateRow(execIndexRow, new ExecRow[] { execRow }, n, array, array2, transactionController);
    }
    
    void updateRow(final ExecIndexRow execIndexRow, final ExecRow[] array, final int n, final boolean[] array2, final int[] array3, final TransactionController transactionController) throws StandardException {
        final ExecRow emptyRow = this.crf.makeEmptyRow();
        final RowChanger rowChanger = this.getRowChanger(transactionController, array3, emptyRow);
        rowChanger.openForUpdate(array2, 6, true);
        final ConglomerateController openConglomerate = transactionController.openConglomerate(this.getHeapConglomerate(), false, 4, 6, 4);
        final ScanController openScan = transactionController.openScan(this.getIndexConglomerate(n), false, 4, 6, 4, null, execIndexRow.getRowArray(), 1, null, execIndexRow.getRowArray(), -1);
        final ExecIndexRow indexRowFromHeapRow = this.getIndexRowFromHeapRow(this.getIndexRowGenerator(n), openConglomerate.newRowLocationTemplate(), this.crf.makeEmptyRow());
        int n2 = 0;
        while (openScan.fetchNext(indexRowFromHeapRow.getRowArray())) {
            final RowLocation rowLocation = (RowLocation)indexRowFromHeapRow.getColumn(indexRowFromHeapRow.nColumns());
            openConglomerate.fetch(rowLocation, emptyRow.getRowArray(), null);
            rowChanger.updateRow(emptyRow, (n2 == array.length - 1) ? array[n2] : array[n2++], rowLocation);
        }
        rowChanger.finish();
        openConglomerate.close();
        openScan.close();
        rowChanger.close();
    }
    
    Properties getCreateHeapProperties() {
        return this.crf.getCreateHeapProperties();
    }
    
    Properties getCreateIndexProperties(final int n) {
        return this.crf.getCreateIndexProperties(n);
    }
    
    private RowChanger getRowChanger(final TransactionController transactionController, final int[] array, final ExecRow execRow) throws StandardException {
        final int numIndexes = this.crf.getNumIndexes();
        final IndexRowGenerator[] array2 = new IndexRowGenerator[numIndexes];
        final long[] array3 = new long[numIndexes];
        for (int i = 0; i < numIndexes; ++i) {
            array2[i] = this.getIndexRowGenerator(i);
            array3[i] = this.getIndexConglomerate(i);
        }
        return this.crf.getExecutionFactory().getRowChanger(this.getHeapConglomerate(), null, null, array2, array3, null, null, this.crf.getHeapColumnCount(), transactionController, array, this.getStreamStorableHeapColIds(execRow), null);
    }
    
    private int[] getStreamStorableHeapColIds(final ExecRow execRow) throws StandardException {
        if (!this.computedStreamStorableHeapColIds) {
            int n = 0;
            final DataValueDescriptor[] rowArray = execRow.getRowArray();
            for (int i = 0; i < rowArray.length; ++i) {
                if (rowArray[i] instanceof StreamStorable) {
                    ++n;
                }
            }
            if (n > 0) {
                this.streamStorableHeapColIds = new int[n];
                int n2 = 0;
                for (int j = 0; j < rowArray.length; ++j) {
                    if (rowArray[j] instanceof StreamStorable) {
                        this.streamStorableHeapColIds[n2++] = j;
                    }
                }
            }
            this.computedStreamStorableHeapColIds = true;
        }
        return this.streamStorableHeapColIds;
    }
    
    private ExecIndexRow getIndexRowFromHeapRow(final IndexRowGenerator indexRowGenerator, final RowLocation rowLocation, final ExecRow execRow) throws StandardException {
        final ExecIndexRow indexRowTemplate = indexRowGenerator.getIndexRowTemplate();
        indexRowGenerator.getIndexRow(execRow, rowLocation, indexRowTemplate, null);
        return indexRowTemplate;
    }
    
    public String toString() {
        return "";
    }
}
