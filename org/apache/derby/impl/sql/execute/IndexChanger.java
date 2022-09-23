// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.execute.CursorResultSet;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.ResultDescription;
import java.util.Properties;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import org.apache.derby.iapi.store.access.ScanController;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.sql.dictionary.IndexRowGenerator;

public class IndexChanger
{
    private IndexRowGenerator irg;
    private long indexCID;
    private DynamicCompiledOpenConglomInfo indexDCOCI;
    private StaticCompiledOpenConglomInfo indexSCOCI;
    private String indexName;
    private ConglomerateController baseCC;
    private TransactionController tc;
    private int lockMode;
    private FormatableBitSet baseRowReadMap;
    private ConglomerateController indexCC;
    private ScanController indexSC;
    private ExecIndexRow ourIndexRow;
    private ExecIndexRow ourUpdatedIndexRow;
    private TemporaryRowHolderImpl rowHolder;
    private boolean rowHolderPassedIn;
    private int isolationLevel;
    private final Activation activation;
    private boolean ownIndexSC;
    
    public IndexChanger(final IndexRowGenerator irg, final long indexCID, final StaticCompiledOpenConglomInfo indexSCOCI, final DynamicCompiledOpenConglomInfo indexDCOCI, final String indexName, final ConglomerateController baseCC, final TransactionController tc, final int lockMode, final FormatableBitSet baseRowReadMap, final int isolationLevel, final Activation activation) throws StandardException {
        this.indexCC = null;
        this.indexSC = null;
        this.ourIndexRow = null;
        this.ourUpdatedIndexRow = null;
        this.rowHolder = null;
        this.ownIndexSC = true;
        this.irg = irg;
        this.indexCID = indexCID;
        this.indexSCOCI = indexSCOCI;
        this.indexDCOCI = indexDCOCI;
        this.baseCC = baseCC;
        this.tc = tc;
        this.lockMode = lockMode;
        this.baseRowReadMap = baseRowReadMap;
        this.rowHolderPassedIn = false;
        this.isolationLevel = isolationLevel;
        this.activation = activation;
        this.indexName = indexName;
        if (activation != null && activation.getIndexConglomerateNumber() == indexCID) {
            this.ownIndexSC = false;
        }
    }
    
    public void setRowHolder(final TemporaryRowHolderImpl rowHolder) {
        this.rowHolder = rowHolder;
        this.rowHolderPassedIn = (rowHolder != null);
    }
    
    public void setBaseCC(final ConglomerateController baseCC) {
        this.baseCC = baseCC;
    }
    
    private void setOurIndexRow(final ExecRow execRow, final RowLocation rowLocation) throws StandardException {
        if (this.ourIndexRow == null) {
            this.ourIndexRow = this.irg.getIndexRowTemplate();
        }
        this.irg.getIndexRow(execRow, rowLocation, this.ourIndexRow, this.baseRowReadMap);
    }
    
    private void setOurUpdatedIndexRow(final ExecRow execRow, final RowLocation rowLocation) throws StandardException {
        if (this.ourUpdatedIndexRow == null) {
            this.ourUpdatedIndexRow = this.irg.getIndexRowTemplate();
        }
        this.irg.getIndexRow(execRow, rowLocation, this.ourUpdatedIndexRow, this.baseRowReadMap);
    }
    
    private boolean indexRowChanged() throws StandardException {
        for (int nColumns = this.ourIndexRow.nColumns(), i = 1; i <= nColumns; ++i) {
            if (!this.ourIndexRow.getColumn(i).compare(2, this.ourUpdatedIndexRow.getColumn(i), true, true)) {
                return true;
            }
        }
        return false;
    }
    
    private void setScan() throws StandardException {
        if (!this.ownIndexSC) {
            this.indexSC = this.activation.getIndexScanController();
        }
        else if (this.indexSC == null) {
            this.baseCC.newRowLocationTemplate();
            if (this.indexSCOCI == null) {
                this.indexSC = this.tc.openScan(this.indexCID, false, 4, this.lockMode, this.isolationLevel, null, this.ourIndexRow.getRowArray(), 1, null, this.ourIndexRow.getRowArray(), -1);
            }
            else {
                this.indexSC = this.tc.openCompiledScan(false, 4, this.lockMode, this.isolationLevel, null, this.ourIndexRow.getRowArray(), 1, null, this.ourIndexRow.getRowArray(), -1, this.indexSCOCI, this.indexDCOCI);
            }
        }
        else {
            this.indexSC.reopenScan(this.ourIndexRow.getRowArray(), 1, null, this.ourIndexRow.getRowArray(), -1);
        }
    }
    
    private void closeIndexCC() throws StandardException {
        if (this.indexCC != null) {
            this.indexCC.close();
        }
        this.indexCC = null;
    }
    
    private void closeIndexSC() throws StandardException {
        if (this.ownIndexSC && this.indexSC != null) {
            this.indexSC.close();
            this.indexSC = null;
        }
    }
    
    private void doDelete() throws StandardException {
        if (this.ownIndexSC && !this.indexSC.next()) {
            Monitor.getStream().println(MessageService.getCompleteMessage("X0Y83.S", new Object[] { this.ourIndexRow.getRowArray()[this.ourIndexRow.getRowArray().length - 1], new Long(this.indexCID) }));
            return;
        }
        this.indexSC.delete();
    }
    
    private void doInsert() throws StandardException {
        this.insertAndCheckDups(this.ourIndexRow);
    }
    
    private void doDeferredInsert() throws StandardException {
        if (this.rowHolder == null) {
            final Properties properties = new Properties();
            this.openIndexCC().getInternalTablePropertySet(properties);
            this.rowHolder = new TemporaryRowHolderImpl(this.activation, properties, null);
        }
        if (!this.rowHolderPassedIn) {
            this.rowHolder.insert(this.ourIndexRow);
        }
    }
    
    private void insertAndCheckDups(final ExecIndexRow execIndexRow) throws StandardException {
        this.openIndexCC();
        if (this.indexCC.insert(execIndexRow.getRowArray()) == 1) {
            String s = this.indexName;
            final DataDictionary dataDictionary = this.activation.getLanguageConnectionContext().getDataDictionary();
            final ConglomerateDescriptor conglomerateDescriptor = dataDictionary.getConglomerateDescriptor(this.indexCID);
            final TableDescriptor tableDescriptor = dataDictionary.getTableDescriptor(conglomerateDescriptor.getTableID());
            final String name = tableDescriptor.getName();
            if (s == null) {
                s = dataDictionary.getConstraintDescriptor(tableDescriptor, conglomerateDescriptor.getUUID()).getConstraintName();
            }
            throw StandardException.newException("23505", s, name);
        }
    }
    
    private ConglomerateController openIndexCC() throws StandardException {
        if (this.indexCC == null) {
            if (this.indexSCOCI == null) {
                this.indexCC = this.tc.openConglomerate(this.indexCID, false, 16388, this.lockMode, this.isolationLevel);
            }
            else {
                this.indexCC = this.tc.openCompiledConglomerate(false, 16388, this.lockMode, this.isolationLevel, this.indexSCOCI, this.indexDCOCI);
            }
        }
        return this.indexCC;
    }
    
    public void open() throws StandardException {
    }
    
    public void delete(final ExecRow execRow, final RowLocation rowLocation) throws StandardException {
        this.setOurIndexRow(execRow, rowLocation);
        this.setScan();
        this.doDelete();
    }
    
    public void update(final ExecRow execRow, final ExecRow execRow2, final RowLocation rowLocation) throws StandardException {
        this.setOurIndexRow(execRow, rowLocation);
        this.setOurUpdatedIndexRow(execRow2, rowLocation);
        if (this.indexRowChanged()) {
            this.setScan();
            this.doDelete();
            this.insertForUpdate(execRow2, rowLocation);
        }
    }
    
    public void insert(final ExecRow execRow, final RowLocation rowLocation) throws StandardException {
        this.setOurIndexRow(execRow, rowLocation);
        this.doInsert();
    }
    
    void insertForUpdate(final ExecRow execRow, final RowLocation rowLocation) throws StandardException {
        this.setOurIndexRow(execRow, rowLocation);
        if (this.irg.isUnique() || this.irg.isUniqueWithDuplicateNulls()) {
            this.doDeferredInsert();
        }
        else {
            this.doInsert();
        }
    }
    
    public void finish() throws StandardException {
        if (this.rowHolder != null) {
            final CursorResultSet resultSet = this.rowHolder.getResultSet();
            try {
                resultSet.open();
                ExecRow nextRow;
                while ((nextRow = resultSet.getNextRow()) != null) {
                    this.insertAndCheckDups((ExecIndexRow)nextRow);
                }
            }
            finally {
                resultSet.close();
                if (!this.rowHolderPassedIn) {
                    this.rowHolder.close();
                }
            }
        }
    }
    
    public void close() throws StandardException {
        this.closeIndexCC();
        this.closeIndexSC();
        if (this.rowHolder != null && !this.rowHolderPassedIn) {
            this.rowHolder.close();
        }
        this.baseCC = null;
    }
}
