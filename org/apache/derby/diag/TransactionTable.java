// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.diag;

import org.apache.derby.impl.jdbc.EmbedResultSetMetaData;
import org.apache.derby.vti.VTIEnvironment;
import org.apache.derby.iapi.util.StringUtil;
import java.sql.SQLException;
import org.apache.derby.iapi.sql.conn.ConnectionUtil;
import org.apache.derby.iapi.error.StandardException;
import java.sql.ResultSetMetaData;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;
import org.apache.derby.iapi.store.access.TransactionInfo;
import org.apache.derby.vti.VTICosting;
import org.apache.derby.vti.VTITemplate;

public class TransactionTable extends VTITemplate implements VTICosting
{
    private TransactionInfo[] transactionTable;
    boolean initialized;
    int currentRow;
    private boolean wasNull;
    private static final ResultColumnDescriptor[] columnInfo;
    private static final ResultSetMetaData metadata;
    
    public TransactionTable() throws StandardException {
        DiagUtil.checkAccess();
    }
    
    public ResultSetMetaData getMetaData() {
        return TransactionTable.metadata;
    }
    
    public boolean next() throws SQLException {
        if (!this.initialized) {
            this.transactionTable = ConnectionUtil.getCurrentLCC().getTransactionExecute().getAccessManager().getTransactionInfo();
            this.initialized = true;
            this.currentRow = -1;
        }
        if (this.transactionTable == null) {
            return false;
        }
        ++this.currentRow;
        while (this.currentRow < this.transactionTable.length) {
            if (this.transactionTable[this.currentRow] != null) {
                return true;
            }
            ++this.currentRow;
        }
        this.transactionTable = null;
        return false;
    }
    
    public void close() {
        this.transactionTable = null;
    }
    
    public String getString(final int n) {
        final TransactionInfo transactionInfo = this.transactionTable[this.currentRow];
        String s = null;
        switch (n) {
            case 1: {
                s = transactionInfo.getTransactionIdString();
                break;
            }
            case 2: {
                s = transactionInfo.getGlobalTransactionIdString();
                break;
            }
            case 3: {
                s = transactionInfo.getUsernameString();
                break;
            }
            case 4: {
                s = transactionInfo.getTransactionTypeString();
                break;
            }
            case 5: {
                s = transactionInfo.getTransactionStatusString();
                break;
            }
            case 6: {
                s = transactionInfo.getFirstLogInstantString();
                break;
            }
            case 7: {
                s = StringUtil.truncate(transactionInfo.getStatementTextString(), 32672);
                break;
            }
            default: {
                s = null;
                break;
            }
        }
        this.wasNull = (s == null);
        return s;
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
        return false;
    }
    
    static {
        columnInfo = new ResultColumnDescriptor[] { EmbedResultSetMetaData.getResultColumnDescriptor("XID", 12, false, 15), EmbedResultSetMetaData.getResultColumnDescriptor("GLOBAL_XID", 12, true, 140), EmbedResultSetMetaData.getResultColumnDescriptor("USERNAME", 12, true, 128), EmbedResultSetMetaData.getResultColumnDescriptor("TYPE", 12, false, 30), EmbedResultSetMetaData.getResultColumnDescriptor("STATUS", 12, false, 8), EmbedResultSetMetaData.getResultColumnDescriptor("FIRST_INSTANT", 12, true, 20), EmbedResultSetMetaData.getResultColumnDescriptor("SQL_TEXT", 12, true, 32672) };
        metadata = new EmbedResultSetMetaData(TransactionTable.columnInfo);
    }
}
