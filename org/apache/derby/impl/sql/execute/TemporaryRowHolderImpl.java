// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.execute.CursorResultSet;
import org.apache.derby.iapi.types.SQLRef;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.store.access.ColumnOrdering;
import org.apache.derby.iapi.types.SQLLongint;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.store.access.ScanController;
import java.util.Properties;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.execute.TemporaryRowHolder;

class TemporaryRowHolderImpl implements TemporaryRowHolder
{
    public static final int DEFAULT_OVERFLOWTHRESHOLD = 5;
    protected static final int STATE_UNINIT = 0;
    protected static final int STATE_INSERT = 1;
    protected static final int STATE_DRAIN = 2;
    protected ExecRow[] rowArray;
    protected int lastArraySlot;
    private int numRowsIn;
    protected int state;
    private long CID;
    private boolean conglomCreated;
    private ConglomerateController cc;
    private Properties properties;
    private ScanController scan;
    private ResultDescription resultDescription;
    Activation activation;
    private boolean isUniqueStream;
    private boolean isVirtualMemHeap;
    private boolean uniqueIndexCreated;
    private boolean positionIndexCreated;
    private long uniqueIndexConglomId;
    private long positionIndexConglomId;
    private ConglomerateController uniqueIndex_cc;
    private ConglomerateController positionIndex_cc;
    private DataValueDescriptor[] uniqueIndexRow;
    private DataValueDescriptor[] positionIndexRow;
    private RowLocation destRowLocation;
    private SQLLongint position_sqllong;
    
    public TemporaryRowHolderImpl(final Activation activation, final Properties properties, final ResultDescription resultDescription) {
        this(activation, properties, resultDescription, 5, false, false);
    }
    
    public TemporaryRowHolderImpl(final Activation activation, final Properties properties, final ResultDescription resultDescription, final boolean b) {
        this(activation, properties, resultDescription, 1, b, false);
    }
    
    public TemporaryRowHolderImpl(final Activation activation, final Properties properties, final ResultDescription resultDescription, final int n, final boolean isUniqueStream, final boolean isVirtualMemHeap) {
        this.state = 0;
        this.uniqueIndexRow = null;
        this.positionIndexRow = null;
        this.activation = activation;
        this.properties = properties;
        this.resultDescription = resultDescription;
        this.isUniqueStream = isUniqueStream;
        this.isVirtualMemHeap = isVirtualMemHeap;
        this.rowArray = new ExecRow[n];
        this.lastArraySlot = -1;
    }
    
    private ExecRow cloneRow(final ExecRow execRow) {
        final DataValueDescriptor[] rowArray = execRow.getRowArray();
        final int length = rowArray.length;
        final ExecRow cloneMe = ((ValueRow)execRow).cloneMe();
        for (int i = 0; i < length; ++i) {
            if (rowArray[i] != null) {
                cloneMe.setColumn(i + 1, rowArray[i].cloneHolder());
            }
        }
        if (execRow instanceof IndexValueRow) {
            return new IndexValueRow(cloneMe);
        }
        return cloneMe;
    }
    
    public void insert(final ExecRow execRow) throws StandardException {
        if (!this.isVirtualMemHeap) {
            this.state = 1;
        }
        if (this.uniqueIndexCreated && this.isRowAlreadyExist(execRow)) {
            return;
        }
        ++this.numRowsIn;
        if (this.lastArraySlot + 1 < this.rowArray.length) {
            this.rowArray[++this.lastArraySlot] = this.cloneRow(execRow);
            if (!this.isUniqueStream) {
                return;
            }
        }
        if (!this.conglomCreated) {
            final TransactionController transactionController = this.activation.getTransactionController();
            this.CID = transactionController.createConglomerate("heap", execRow.getRowArray(), null, null, this.properties, 3);
            this.conglomCreated = true;
            this.cc = transactionController.openConglomerate(this.CID, false, 4, 7, 5);
            if (this.isUniqueStream) {
                this.destRowLocation = this.cc.newRowLocationTemplate();
            }
        }
        if (this.isUniqueStream) {
            this.cc.insertAndFetchLocation(execRow.getRowArray(), this.destRowLocation);
            this.insertToPositionIndex(this.numRowsIn - 1, this.destRowLocation);
            if (!this.uniqueIndexCreated) {
                this.isRowAlreadyExist(execRow);
            }
        }
        else {
            this.cc.insert(execRow.getRowArray());
            if (this.isVirtualMemHeap) {
                this.state = 1;
            }
        }
    }
    
    private boolean isRowAlreadyExist(final ExecRow execRow) throws StandardException {
        final DataValueDescriptor column = execRow.getColumn(execRow.nColumns());
        if (this.CID != 0L && column instanceof SQLRef) {
            final RowLocation rowLocation = (RowLocation)column.getObject();
            if (!this.uniqueIndexCreated) {
                final TransactionController transactionController = this.activation.getTransactionController();
                (this.uniqueIndexRow = new DataValueDescriptor[2])[0] = rowLocation;
                this.uniqueIndexRow[1] = rowLocation;
                this.uniqueIndexConglomId = transactionController.createConglomerate("BTREE", this.uniqueIndexRow, null, null, this.makeIndexProperties(this.uniqueIndexRow, this.CID), 3);
                this.uniqueIndex_cc = transactionController.openConglomerate(this.uniqueIndexConglomId, false, 4, 7, 5);
                this.uniqueIndexCreated = true;
            }
            this.uniqueIndexRow[0] = rowLocation;
            this.uniqueIndexRow[1] = rowLocation;
            final int insert;
            if ((insert = this.uniqueIndex_cc.insert(this.uniqueIndexRow)) != 0 && insert == 1) {
                return true;
            }
        }
        return false;
    }
    
    private void insertToPositionIndex(final int value, final RowLocation rowLocation) throws StandardException {
        if (!this.positionIndexCreated) {
            final TransactionController transactionController = this.activation.getTransactionController();
            final int n = 2;
            this.position_sqllong = new SQLLongint();
            (this.positionIndexRow = new DataValueDescriptor[n])[0] = this.position_sqllong;
            this.positionIndexRow[1] = rowLocation;
            this.positionIndexConglomId = transactionController.createConglomerate("BTREE", this.positionIndexRow, null, null, this.makeIndexProperties(this.positionIndexRow, this.CID), 3);
            this.positionIndex_cc = transactionController.openConglomerate(this.positionIndexConglomId, false, 4, 7, 5);
            this.positionIndexCreated = true;
        }
        this.position_sqllong.setValue(value);
        this.positionIndexRow[0] = this.position_sqllong;
        this.positionIndexRow[1] = rowLocation;
        this.positionIndex_cc.insert(this.positionIndexRow);
    }
    
    public CursorResultSet getResultSet() {
        this.state = 2;
        final TransactionController transactionController = this.activation.getTransactionController();
        if (this.isUniqueStream) {
            return new TemporaryRowHolderResultSet(transactionController, this.rowArray, this.resultDescription, this.isVirtualMemHeap, true, this.positionIndexConglomId, this);
        }
        return new TemporaryRowHolderResultSet(transactionController, this.rowArray, this.resultDescription, this.isVirtualMemHeap, this);
    }
    
    public void truncate() throws StandardException {
        this.close();
        for (int i = 0; i < this.rowArray.length; ++i) {
            this.rowArray[i] = null;
        }
        this.numRowsIn = 0;
    }
    
    public long getTemporaryConglomId() {
        return this.CID;
    }
    
    public long getPositionIndexConglomId() {
        return this.positionIndexConglomId;
    }
    
    private Properties makeIndexProperties(final DataValueDescriptor[] array, final long l) throws StandardException {
        final int length = array.length;
        final Properties properties = new Properties();
        properties.put("allowDuplicates", "false");
        properties.put("nKeyFields", String.valueOf(length));
        properties.put("nUniqueColumns", String.valueOf(length - 1));
        properties.put("rowLocationColumn", String.valueOf(length - 1));
        properties.put("baseConglomerateId", String.valueOf(l));
        return properties;
    }
    
    public void setRowHolderTypeToUniqueStream() {
        this.isUniqueStream = true;
    }
    
    public void close() throws StandardException {
        if (this.scan != null) {
            this.scan.close();
            this.scan = null;
        }
        if (this.cc != null) {
            this.cc.close();
            this.cc = null;
        }
        if (this.uniqueIndex_cc != null) {
            this.uniqueIndex_cc.close();
            this.uniqueIndex_cc = null;
        }
        if (this.positionIndex_cc != null) {
            this.positionIndex_cc.close();
            this.positionIndex_cc = null;
        }
        final TransactionController transactionController = this.activation.getTransactionController();
        if (this.uniqueIndexCreated) {
            transactionController.dropConglomerate(this.uniqueIndexConglomId);
            this.uniqueIndexCreated = false;
        }
        if (this.positionIndexCreated) {
            transactionController.dropConglomerate(this.positionIndexConglomId);
            this.positionIndexCreated = false;
        }
        if (this.conglomCreated) {
            transactionController.dropConglomerate(this.CID);
            this.conglomCreated = false;
            this.CID = 0L;
        }
        this.state = 0;
        this.lastArraySlot = -1;
    }
}
