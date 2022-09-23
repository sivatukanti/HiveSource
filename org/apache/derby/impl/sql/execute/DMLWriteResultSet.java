// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import java.io.InputStream;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.services.io.StreamStorable;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.sql.execute.ExecRow;

abstract class DMLWriteResultSet extends NoRowsResultSetImpl
{
    protected WriteCursorConstantAction constantAction;
    protected int[] baseRowReadMap;
    protected int[] streamStorableHeapColIds;
    protected ExecRow deferredSparseRow;
    protected DynamicCompiledOpenConglomInfo heapDCOCI;
    protected DynamicCompiledOpenConglomInfo[] indexDCOCIs;
    private boolean needToObjectifyStream;
    public long rowCount;
    
    DMLWriteResultSet(final Activation activation) throws StandardException {
        this(activation, activation.getConstantAction());
    }
    
    DMLWriteResultSet(final Activation activation, final ConstantAction constantAction) throws StandardException {
        super(activation);
        this.constantAction = (WriteCursorConstantAction)constantAction;
        this.baseRowReadMap = this.constantAction.getBaseRowReadMap();
        this.streamStorableHeapColIds = this.constantAction.getStreamStorableHeapColIds();
        final TransactionController transactionController = activation.getTransactionController();
        if (!(constantAction instanceof UpdatableVTIConstantAction)) {
            this.heapDCOCI = transactionController.getDynamicCompiledConglomInfo(this.constantAction.conglomId);
            if (this.constantAction.indexCIDS.length != 0) {
                this.indexDCOCIs = new DynamicCompiledOpenConglomInfo[this.constantAction.indexCIDS.length];
                for (int i = 0; i < this.constantAction.indexCIDS.length; ++i) {
                    this.indexDCOCIs[i] = transactionController.getDynamicCompiledConglomInfo(this.constantAction.indexCIDS[i]);
                }
            }
        }
        this.needToObjectifyStream = (this.constantAction.getTriggerInfo() != null);
    }
    
    public final long modifiedRowCount() {
        return this.rowCount + RowUtil.rowCountBase;
    }
    
    protected ExecRow getNextRowCore(final NoPutResultSet set) throws StandardException {
        final ExecRow nextRowCore = set.getNextRowCore();
        if (this.needToObjectifyStream) {
            this.objectifyStreams(nextRowCore);
        }
        return nextRowCore;
    }
    
    private void objectifyStreams(final ExecRow execRow) throws StandardException {
        if (execRow != null && this.streamStorableHeapColIds != null) {
            for (int i = 0; i < this.streamStorableHeapColIds.length; ++i) {
                final int n = this.streamStorableHeapColIds[i];
                final DataValueDescriptor column = execRow.getColumn(((this.baseRowReadMap == null) ? n : this.baseRowReadMap[n]) + 1);
                if (column != null) {
                    final InputStream returnStream = ((StreamStorable)column).returnStream();
                    ((StreamStorable)column).loadStream();
                    if (returnStream != null) {
                        for (int j = 1; j <= execRow.nColumns(); ++j) {
                            final DataValueDescriptor column2 = execRow.getColumn(j);
                            if (column2 instanceof StreamStorable && ((StreamStorable)column2).returnStream() == returnStream) {
                                execRow.setColumn(j, column.cloneValue(false));
                            }
                        }
                    }
                }
            }
        }
    }
    
    protected ExecRow makeDeferredSparseRow(final ExecRow execRow, final FormatableBitSet set, final LanguageConnectionContext languageConnectionContext) throws StandardException {
        ExecRow emptyValueRow;
        if (set == null) {
            emptyValueRow = execRow;
        }
        else {
            emptyValueRow = RowUtil.getEmptyValueRow(set.getLength() - 1, languageConnectionContext);
            int n = 1;
            for (int i = 1; i <= emptyValueRow.nColumns(); ++i) {
                if (set.isSet(i)) {
                    emptyValueRow.setColumn(i, execRow.getColumn(n++));
                }
            }
        }
        return emptyValueRow;
    }
    
    int decodeLockMode(final int n) {
        if (n >>> 16 == 0) {
            return n;
        }
        if (this.lcc.getCurrentIsolationLevel() == 4) {
            return n >>> 16;
        }
        return n & 0xFF;
    }
    
    String getIndexNameFromCID(final long n) {
        return this.constantAction.getIndexNameFromCID(n);
    }
}
