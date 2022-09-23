// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.execute.ExecutionStmtValidator;
import org.apache.derby.iapi.sql.execute.CursorResultSet;
import org.apache.derby.iapi.sql.dictionary.TriggerDescriptor;
import org.apache.derby.iapi.error.StandardException;
import java.util.Vector;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.jdbc.ConnectionContext;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;

public class TriggerEventActivator
{
    private LanguageConnectionContext lcc;
    private TransactionController tc;
    private TriggerInfo triggerInfo;
    private InternalTriggerExecutionContext tec;
    private GenericTriggerExecutor[][] executors;
    private Activation activation;
    private ConnectionContext cc;
    private String statementText;
    private int dmlType;
    private UUID tableId;
    private String tableName;
    private Vector aiCounters;
    
    public TriggerEventActivator(final LanguageConnectionContext lcc, final TransactionController tc, final UUID tableId, final TriggerInfo triggerInfo, final int dmlType, final Activation activation, final Vector vector) throws StandardException {
        if (triggerInfo == null) {
            return;
        }
        this.tableName = triggerInfo.triggerArray[0].getTableDescriptor().getQualifiedName();
        this.lcc = lcc;
        this.tc = tc;
        this.activation = activation;
        this.tableId = tableId;
        this.dmlType = dmlType;
        this.triggerInfo = triggerInfo;
        this.cc = (ConnectionContext)lcc.getContextManager().getContext("JDBC_ConnectionContext");
        this.statementText = lcc.getStatementContext().getStatementText();
        this.tec = ((GenericExecutionFactory)lcc.getLanguageConnectionFactory().getExecutionFactory()).getTriggerExecutionContext(lcc, this.cc, this.statementText, dmlType, triggerInfo.columnIds, triggerInfo.columnNames, tableId, this.tableName, vector);
        this.setupExecutors(triggerInfo);
    }
    
    void reopen() throws StandardException {
        this.tec = ((GenericExecutionFactory)this.lcc.getLanguageConnectionFactory().getExecutionFactory()).getTriggerExecutionContext(this.lcc, this.cc, this.statementText, this.dmlType, this.triggerInfo.columnIds, this.triggerInfo.columnNames, this.tableId, this.tableName, this.aiCounters);
        this.setupExecutors(this.triggerInfo);
    }
    
    private void setupExecutors(final TriggerInfo triggerInfo) throws StandardException {
        this.executors = new GenericTriggerExecutor[6][];
        final Vector[] array = new Vector[6];
        for (int i = 0; i < 6; ++i) {
            array[i] = new Vector();
        }
        for (int j = 0; j < triggerInfo.triggerArray.length; ++j) {
            final TriggerDescriptor triggerDescriptor = triggerInfo.triggerArray[j];
            switch (triggerDescriptor.getTriggerEventMask()) {
                case 4: {
                    if (triggerDescriptor.isBeforeTrigger()) {
                        array[0].addElement(triggerDescriptor);
                        break;
                    }
                    array[3].addElement(triggerDescriptor);
                    break;
                }
                case 2: {
                    if (triggerDescriptor.isBeforeTrigger()) {
                        array[1].addElement(triggerDescriptor);
                        break;
                    }
                    array[4].addElement(triggerDescriptor);
                    break;
                }
                case 1: {
                    if (triggerDescriptor.isBeforeTrigger()) {
                        array[2].addElement(triggerDescriptor);
                        break;
                    }
                    array[5].addElement(triggerDescriptor);
                    break;
                }
            }
        }
        for (int k = 0; k < array.length; ++k) {
            final int size = array[k].size();
            if (size > 0) {
                this.executors[k] = new GenericTriggerExecutor[size];
                for (int l = 0; l < size; ++l) {
                    final TriggerDescriptor triggerDescriptor2 = array[k].elementAt(l);
                    this.executors[k][l] = (triggerDescriptor2.isRowTrigger() ? new RowTriggerExecutor(this.tec, triggerDescriptor2, this.activation, this.lcc) : new StatementTriggerExecutor(this.tec, triggerDescriptor2, this.activation, this.lcc));
                }
            }
        }
    }
    
    public void notifyEvent(final TriggerEvent currentTriggerEvent, final CursorResultSet set, final CursorResultSet set2, final int[] array) throws StandardException {
        if (this.executors == null) {
            return;
        }
        final int number = currentTriggerEvent.getNumber();
        if (this.executors[number] == null) {
            return;
        }
        this.tec.setCurrentTriggerEvent(currentTriggerEvent);
        try {
            if (set != null) {
                set.open();
            }
            if (set2 != null) {
                set2.open();
            }
            this.lcc.pushExecutionStmtValidator(this.tec);
            for (int i = 0; i < this.executors[number].length; ++i) {
                if (i > 0) {
                    if (set != null) {
                        ((NoPutResultSet)set).reopenCore();
                    }
                    if (set2 != null) {
                        ((NoPutResultSet)set2).reopenCore();
                    }
                }
                this.tec.resetAICounters(true);
                this.executors[number][i].fireTrigger(currentTriggerEvent, set, set2, array);
            }
        }
        finally {
            this.lcc.popExecutionStmtValidator(this.tec);
            this.tec.clearCurrentTriggerEvent();
        }
    }
    
    public void cleanup() throws StandardException {
        if (this.tec != null) {
            this.tec.cleanup();
        }
    }
}
