// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import java.util.Map;
import org.apache.derby.iapi.error.PublicAPI;
import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.util.Enumeration;
import java.sql.SQLException;
import java.sql.ResultSet;
import org.apache.derby.iapi.error.StandardException;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.derby.iapi.sql.dictionary.TriggerDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.execute.CursorResultSet;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.jdbc.ConnectionContext;
import org.apache.derby.iapi.sql.execute.ExecutionStmtValidator;
import org.apache.derby.iapi.db.TriggerExecutionContext;

public class InternalTriggerExecutionContext implements TriggerExecutionContext, ExecutionStmtValidator
{
    protected int[] changedColIds;
    protected String[] changedColNames;
    protected int dmlType;
    protected String statementText;
    protected ConnectionContext cc;
    protected UUID targetTableId;
    protected String targetTableName;
    protected LanguageConnectionContext lcc;
    protected CursorResultSet beforeResultSet;
    protected CursorResultSet afterResultSet;
    protected ExecRow afterRow;
    protected boolean cleanupCalled;
    protected TriggerEvent event;
    protected TriggerDescriptor triggerd;
    private Vector resultSetVector;
    private Vector aiCounters;
    private Hashtable aiHT;
    
    public InternalTriggerExecutionContext(final LanguageConnectionContext lcc, final ConnectionContext cc, final String statementText, final int dmlType, final int[] changedColIds, final String[] changedColNames, final UUID targetTableId, final String targetTableName, final Vector aiCounters) throws StandardException {
        this.dmlType = dmlType;
        this.changedColIds = changedColIds;
        this.changedColNames = changedColNames;
        this.statementText = statementText;
        this.cc = cc;
        this.lcc = lcc;
        this.targetTableId = targetTableId;
        this.targetTableName = targetTableName;
        this.resultSetVector = new Vector();
        this.aiCounters = aiCounters;
        lcc.pushTriggerExecutionContext(this);
    }
    
    void setBeforeResultSet(final CursorResultSet beforeResultSet) {
        this.beforeResultSet = beforeResultSet;
    }
    
    void setAfterResultSet(final CursorResultSet afterResultSet) throws StandardException {
        this.afterResultSet = afterResultSet;
        if (this.aiCounters != null) {
            if (this.triggerd.isRowTrigger()) {
                afterResultSet.open();
                this.afterRow = afterResultSet.getNextRow();
                afterResultSet.close();
            }
            else if (!this.triggerd.isBeforeTrigger()) {
                this.resetAICounters(false);
            }
        }
    }
    
    void setCurrentTriggerEvent(final TriggerEvent event) {
        this.event = event;
    }
    
    void clearCurrentTriggerEvent() {
        this.event = null;
    }
    
    void setTrigger(final TriggerDescriptor triggerd) {
        this.triggerd = triggerd;
    }
    
    void clearTrigger() throws StandardException {
        this.event = null;
        this.triggerd = null;
        if (this.afterResultSet != null) {
            this.afterResultSet.close();
            this.afterResultSet = null;
        }
        if (this.beforeResultSet != null) {
            this.beforeResultSet.close();
            this.beforeResultSet = null;
        }
    }
    
    protected void cleanup() throws StandardException {
        this.lcc.popTriggerExecutionContext(this);
        final Enumeration<ResultSet> elements = (Enumeration<ResultSet>)this.resultSetVector.elements();
        while (elements.hasMoreElements()) {
            final ResultSet set = elements.nextElement();
            try {
                set.close();
            }
            catch (SQLException ex) {}
        }
        this.resultSetVector = null;
        if (this.afterResultSet != null) {
            this.afterResultSet.close();
            this.afterResultSet = null;
        }
        if (this.beforeResultSet != null) {
            this.beforeResultSet.close();
            this.beforeResultSet = null;
        }
        this.lcc = null;
        this.cleanupCalled = true;
    }
    
    private void ensureProperContext() throws SQLException {
        if (this.cleanupCalled) {
            throw new SQLException(MessageService.getTextMessage("XCL31.S"), "XCL31", 20000);
        }
    }
    
    public void validateStatement(final ConstantAction constantAction) throws StandardException {
        if (constantAction instanceof DDLConstantAction) {
            throw StandardException.newException("X0Y69.S", this.triggerd.getName());
        }
    }
    
    public String getTargetTableName() {
        return this.targetTableName;
    }
    
    public UUID getTargetTableId() {
        return this.targetTableId;
    }
    
    public int getEventType() {
        return this.dmlType;
    }
    
    public String getEventStatementText() {
        return this.statementText;
    }
    
    public String[] getModifiedColumns() {
        return this.changedColNames;
    }
    
    public boolean wasColumnModified(final String anObject) {
        if (this.changedColNames == null) {
            return true;
        }
        for (int i = 0; i < this.changedColNames.length; ++i) {
            if (this.changedColNames[i].equals(anObject)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean wasColumnModified(final int n) {
        if (this.changedColIds == null) {
            return true;
        }
        for (int i = 0; i < this.changedColNames.length; ++i) {
            if (this.changedColIds[i] == n) {
                return true;
            }
        }
        return false;
    }
    
    public ResultSet getOldRowSet() throws SQLException {
        this.ensureProperContext();
        if (this.beforeResultSet == null) {
            return null;
        }
        try {
            CursorResultSet beforeResultSet = this.beforeResultSet;
            if (beforeResultSet instanceof TemporaryRowHolderResultSet) {
                beforeResultSet = (CursorResultSet)((TemporaryRowHolderResultSet)beforeResultSet).clone();
            }
            else if (beforeResultSet instanceof TableScanResultSet) {
                beforeResultSet = (CursorResultSet)((TableScanResultSet)beforeResultSet).clone();
            }
            beforeResultSet.open();
            final ResultSet resultSet = this.cc.getResultSet(beforeResultSet);
            this.resultSetVector.addElement(resultSet);
            return resultSet;
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    public ResultSet getNewRowSet() throws SQLException {
        this.ensureProperContext();
        if (this.afterResultSet == null) {
            return null;
        }
        try {
            CursorResultSet afterResultSet = this.afterResultSet;
            if (afterResultSet instanceof TemporaryRowHolderResultSet) {
                afterResultSet = (CursorResultSet)((TemporaryRowHolderResultSet)afterResultSet).clone();
            }
            else if (afterResultSet instanceof TableScanResultSet) {
                afterResultSet = (CursorResultSet)((TableScanResultSet)afterResultSet).clone();
            }
            afterResultSet.open();
            final ResultSet resultSet = this.cc.getResultSet(afterResultSet);
            this.resultSetVector.addElement(resultSet);
            return resultSet;
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    public ResultSet getOldRow() throws SQLException {
        final ResultSet oldRowSet = this.getOldRowSet();
        if (oldRowSet != null) {
            oldRowSet.next();
        }
        return oldRowSet;
    }
    
    public ResultSet getNewRow() throws SQLException {
        final ResultSet newRowSet = this.getNewRowSet();
        if (newRowSet != null) {
            newRowSet.next();
        }
        return newRowSet;
    }
    
    public Long getAutoincrementValue(final String key) {
        if (this.aiHT != null) {
            final Long n = this.aiHT.get(key);
            if (n != null) {
                return n;
            }
        }
        if (this.aiCounters != null) {
            for (int i = 0; i < this.aiCounters.size(); ++i) {
                final AutoincrementCounter autoincrementCounter = this.aiCounters.elementAt(i);
                if (key.equals(autoincrementCounter.getIdentity())) {
                    return autoincrementCounter.getCurrentValue();
                }
            }
        }
        return null;
    }
    
    public void copyHashtableToAIHT(final Map t) {
        if (t == null) {
            return;
        }
        if (this.aiHT == null) {
            this.aiHT = new Hashtable();
        }
        this.aiHT.putAll(t);
    }
    
    public void resetAICounters(final boolean b) {
        if (this.aiCounters == null) {
            return;
        }
        this.afterRow = null;
        for (int size = this.aiCounters.size(), i = 0; i < size; ++i) {
            ((AutoincrementCounter)this.aiCounters.elementAt(i)).reset(b);
        }
    }
    
    public void updateAICounters() throws StandardException {
        if (this.aiCounters == null) {
            return;
        }
        for (int size = this.aiCounters.size(), i = 0; i < size; ++i) {
            final AutoincrementCounter autoincrementCounter = this.aiCounters.elementAt(i);
            autoincrementCounter.update(this.afterRow.getColumn(autoincrementCounter.getColumnPosition()).getLong());
        }
    }
    
    public String toString() {
        return this.triggerd.getName();
    }
}
