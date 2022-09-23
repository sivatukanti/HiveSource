// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.diag;

import org.apache.derby.impl.jdbc.EmbedResultSetMetaData;
import org.apache.derby.iapi.services.locks.LockOwner;
import org.apache.derby.impl.services.locks.ActiveLock;
import org.apache.derby.vti.VTIEnvironment;
import java.sql.SQLException;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.error.PublicAPI;
import org.apache.derby.iapi.services.locks.Latch;
import org.apache.derby.iapi.sql.conn.ConnectionUtil;
import java.sql.ResultSetMetaData;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;
import org.apache.derby.impl.services.locks.TableNameInfo;
import java.util.Enumeration;
import java.util.Hashtable;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.vti.VTICosting;
import org.apache.derby.vti.VTITemplate;

public class LockTable extends VTITemplate implements VTICosting
{
    public static final int LATCH = 1;
    public static final int TABLE_AND_ROWLOCK = 2;
    public static final int ALL = -1;
    private TransactionController tc;
    private Hashtable currentRow;
    private Enumeration lockTable;
    private boolean wasNull;
    private boolean initialized;
    private final int flag;
    private TableNameInfo tabInfo;
    private static final ResultColumnDescriptor[] columnInfo;
    private static final ResultSetMetaData metadata;
    
    public LockTable() {
        this.flag = 2;
    }
    
    public LockTable(final int flag) {
        this.flag = flag;
    }
    
    public ResultSetMetaData getMetaData() {
        return LockTable.metadata;
    }
    
    public boolean next() throws SQLException {
        try {
            if (!this.initialized) {
                final LanguageConnectionContext currentLCC = ConnectionUtil.getCurrentLCC();
                this.tc = currentLCC.getTransactionExecute();
                this.lockTable = this.tc.getAccessManager().getLockFactory().makeVirtualLockTable();
                this.initialized = true;
                this.tabInfo = new TableNameInfo(currentLCC, true);
            }
            this.currentRow = null;
            if (this.lockTable != null) {
                while (this.lockTable.hasMoreElements() && this.currentRow == null) {
                    this.currentRow = this.dumpLock(this.lockTable.nextElement());
                }
            }
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
        return this.currentRow != null;
    }
    
    public void close() {
        this.lockTable = null;
    }
    
    public String getString(final int n) {
        final String s = this.currentRow.get(LockTable.columnInfo[n - 1].getName());
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
    
    private Hashtable dumpLock(final Latch value) throws StandardException {
        final Hashtable<String, Latch> hashtable = new Hashtable<String, Latch>(17);
        final Object qualifier = value.getQualifier();
        if (!value.getLockable().lockAttributes(this.flag, hashtable)) {
            return null;
        }
        if (hashtable.get("LOCKNAME") == null || hashtable.get("TYPE") == null) {
            return null;
        }
        final int count = value.getCount();
        String value2;
        if (count != 0) {
            value2 = "GRANT";
        }
        else {
            if (!(value instanceof ActiveLock)) {
                return null;
            }
            value2 = "WAIT";
        }
        Long value3 = (Long)hashtable.get("CONGLOMID");
        if (value3 == null) {
            if (hashtable.get("CONTAINERID") == null) {
                return null;
            }
            value3 = new Long(this.tc.findConglomid((long)hashtable.get("CONTAINERID")));
            hashtable.put("CONGLOMID", (Latch)value3);
        }
        hashtable.put("LOCKOBJ", value);
        final LockOwner owner = value.getCompatabilitySpace().getOwner();
        hashtable.put("XID", (Latch)((owner == null) ? "<null>" : owner.toString()));
        hashtable.put("MODE", (Latch)qualifier.toString());
        hashtable.put("LOCKCOUNT", (Latch)Integer.toString(count));
        hashtable.put("STATE", (Latch)value2);
        hashtable.put("TABLENAME", (Latch)this.tabInfo.getTableName(value3));
        final String indexName = this.tabInfo.getIndexName(value3);
        if (indexName != null) {
            hashtable.put("INDEXNAME", (Latch)indexName);
        }
        hashtable.put("TABLETYPE", (Latch)this.tabInfo.getTableType(value3));
        return hashtable;
    }
    
    static {
        columnInfo = new ResultColumnDescriptor[] { EmbedResultSetMetaData.getResultColumnDescriptor("XID", 12, false, 15), EmbedResultSetMetaData.getResultColumnDescriptor("TYPE", 12, true, 5), EmbedResultSetMetaData.getResultColumnDescriptor("MODE", 12, false, 4), EmbedResultSetMetaData.getResultColumnDescriptor("TABLENAME", 12, false, 128), EmbedResultSetMetaData.getResultColumnDescriptor("LOCKNAME", 12, false, 20), EmbedResultSetMetaData.getResultColumnDescriptor("STATE", 12, true, 5), EmbedResultSetMetaData.getResultColumnDescriptor("TABLETYPE", 12, false, 9), EmbedResultSetMetaData.getResultColumnDescriptor("LOCKCOUNT", 12, false, 5), EmbedResultSetMetaData.getResultColumnDescriptor("INDEXNAME", 12, true, 128) };
        metadata = new EmbedResultSetMetaData(LockTable.columnInfo);
    }
}
