// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.diag;

import org.apache.derby.impl.jdbc.EmbedResultSetMetaData;
import org.apache.derby.vti.VTIEnvironment;
import java.sql.SQLException;
import org.apache.derby.iapi.error.PublicAPI;
import org.apache.derby.iapi.sql.conn.ConnectionUtil;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import java.sql.ResultSetMetaData;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.store.access.SpaceInfo;
import org.apache.derby.vti.VTICosting;
import org.apache.derby.vti.VTITemplate;

public class SpaceTable extends VTITemplate implements VTICosting
{
    private ConglomInfo[] conglomTable;
    boolean initialized;
    int currentRow;
    private boolean wasNull;
    private String schemaName;
    private String tableName;
    private SpaceInfo spaceInfo;
    private TransactionController tc;
    private static final ResultColumnDescriptor[] columnInfo;
    private static final ResultSetMetaData metadata;
    
    public SpaceTable() {
    }
    
    public SpaceTable(final String schemaName, final String tableName) {
        this.schemaName = schemaName;
        this.tableName = tableName;
    }
    
    public SpaceTable(final String tableName) {
        this.tableName = tableName;
    }
    
    private void getConglomInfo(final LanguageConnectionContext languageConnectionContext) throws StandardException {
        final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
        if (this.schemaName == null) {
            this.schemaName = languageConnectionContext.getCurrentSchemaName();
        }
        ConglomerateDescriptor[] array;
        if (this.tableName != null) {
            final TableDescriptor tableDescriptor = dataDictionary.getTableDescriptor(this.tableName, dataDictionary.getSchemaDescriptor(this.schemaName, this.tc, true), this.tc);
            if (tableDescriptor == null) {
                this.conglomTable = new ConglomInfo[0];
                return;
            }
            array = tableDescriptor.getConglomerateDescriptors();
        }
        else {
            array = dataDictionary.getConglomerateDescriptors(null);
        }
        this.conglomTable = new ConglomInfo[array.length];
        for (int i = 0; i < array.length; ++i) {
            String s;
            if (array[i].isIndex()) {
                s = array[i].getConglomerateName();
            }
            else if (this.tableName != null) {
                s = this.tableName;
            }
            else {
                s = dataDictionary.getTableDescriptor(array[i].getTableID()).getName();
            }
            this.conglomTable[i] = new ConglomInfo(array[i].getTableID().toString(), array[i].getConglomerateNumber(), s, array[i].isIndex());
        }
    }
    
    private void getSpaceInfo(final int n) throws StandardException {
        final ConglomerateController openConglomerate = this.tc.openConglomerate(this.conglomTable[n].getConglomId(), false, 0, 6, 2);
        this.spaceInfo = openConglomerate.getSpaceInfo();
        openConglomerate.close();
    }
    
    public ResultSetMetaData getMetaData() {
        return SpaceTable.metadata;
    }
    
    public boolean next() throws SQLException {
        try {
            if (!this.initialized) {
                final LanguageConnectionContext currentLCC = ConnectionUtil.getCurrentLCC();
                this.tc = currentLCC.getTransactionExecute();
                this.getConglomInfo(currentLCC);
                this.initialized = true;
                this.currentRow = -1;
            }
            if (this.conglomTable == null) {
                return false;
            }
            ++this.currentRow;
            if (this.currentRow >= this.conglomTable.length) {
                return false;
            }
            this.spaceInfo = null;
            this.getSpaceInfo(this.currentRow);
            return true;
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    public void close() {
        this.conglomTable = null;
        this.spaceInfo = null;
        this.tc = null;
    }
    
    public String getString(final int n) {
        final ConglomInfo conglomInfo = this.conglomTable[this.currentRow];
        String s = null;
        switch (n) {
            case 1: {
                s = conglomInfo.getConglomName();
                break;
            }
            case 8: {
                s = conglomInfo.getTableID();
                break;
            }
        }
        this.wasNull = (s == null);
        return s;
    }
    
    public long getLong(final int n) {
        final ConglomInfo conglomInfo = this.conglomTable[this.currentRow];
        long n2 = 0L;
        switch (n) {
            case 3: {
                n2 = this.spaceInfo.getNumAllocatedPages();
                break;
            }
            case 4: {
                n2 = this.spaceInfo.getNumFreePages();
                break;
            }
            case 5: {
                n2 = this.spaceInfo.getNumUnfilledPages();
                break;
            }
            case 7: {
                n2 = this.spaceInfo.getNumFreePages() * this.spaceInfo.getPageSize();
                break;
            }
            default: {
                n2 = -1L;
                break;
            }
        }
        this.wasNull = false;
        return n2;
    }
    
    public short getShort(final int n) {
        final ConglomInfo conglomInfo = this.conglomTable[this.currentRow];
        this.wasNull = false;
        return (short)(conglomInfo.getIsIndex() ? 1 : 0);
    }
    
    public int getInt(final int n) {
        return this.spaceInfo.getPageSize();
    }
    
    public boolean wasNull() {
        return this.wasNull;
    }
    
    public double getEstimatedRowCount(final VTIEnvironment vtiEnvironment) {
        return 10000.0;
    }
    
    public double getEstimatedCostPerInstantiation(final VTIEnvironment vtiEnvironment) {
        return 100000.0;
    }
    
    public boolean supportsMultipleInstantiations(final VTIEnvironment vtiEnvironment) {
        return true;
    }
    
    static {
        columnInfo = new ResultColumnDescriptor[] { EmbedResultSetMetaData.getResultColumnDescriptor("CONGLOMERATENAME", 12, true, 128), EmbedResultSetMetaData.getResultColumnDescriptor("ISINDEX", 5, false), EmbedResultSetMetaData.getResultColumnDescriptor("NUMALLOCATEDPAGES", -5, false), EmbedResultSetMetaData.getResultColumnDescriptor("NUMFREEPAGES", -5, false), EmbedResultSetMetaData.getResultColumnDescriptor("NUMUNFILLEDPAGES", -5, false), EmbedResultSetMetaData.getResultColumnDescriptor("PAGESIZE", 4, false), EmbedResultSetMetaData.getResultColumnDescriptor("ESTIMSPACESAVING", -5, false), EmbedResultSetMetaData.getResultColumnDescriptor("TABLEID", 1, false, 36) };
        metadata = new EmbedResultSetMetaData(SpaceTable.columnInfo);
    }
}
