// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.ResultSet;
import org.apache.derby.iapi.sql.conn.StatementContext;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.CursorResultSet;
import org.apache.derby.iapi.sql.execute.ExecPreparedStatement;
import org.apache.derby.iapi.sql.dictionary.SPSDescriptor;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.dictionary.TriggerDescriptor;

public abstract class GenericTriggerExecutor
{
    protected InternalTriggerExecutionContext tec;
    protected TriggerDescriptor triggerd;
    protected Activation activation;
    protected LanguageConnectionContext lcc;
    private boolean whenClauseRetrieved;
    private boolean actionRetrieved;
    private SPSDescriptor whenClause;
    private SPSDescriptor action;
    private ExecPreparedStatement ps;
    private Activation spsActivation;
    
    GenericTriggerExecutor(final InternalTriggerExecutionContext tec, final TriggerDescriptor triggerd, final Activation activation, final LanguageConnectionContext lcc) {
        this.tec = tec;
        this.triggerd = triggerd;
        this.activation = activation;
        this.lcc = lcc;
    }
    
    abstract void fireTrigger(final TriggerEvent p0, final CursorResultSet p1, final CursorResultSet p2, final int[] p3) throws StandardException;
    
    protected SPSDescriptor getWhenClause() throws StandardException {
        if (!this.whenClauseRetrieved) {
            this.whenClauseRetrieved = true;
            this.whenClause = this.triggerd.getWhenClauseSPS();
        }
        return this.whenClause;
    }
    
    protected SPSDescriptor getAction() throws StandardException {
        if (!this.actionRetrieved) {
            this.actionRetrieved = true;
            this.action = this.triggerd.getActionSPS(this.lcc);
        }
        return this.action;
    }
    
    protected void executeSPS(final SPSDescriptor spsDescriptor) throws StandardException {
        int n = 0;
        while (true) {
            if (this.ps == null || n != 0) {
                this.lcc.getStatementContext().setActivation(this.activation);
                this.ps = spsDescriptor.getPreparedStatement();
                (this.ps = this.ps.getClone()).setValid();
                this.spsActivation = this.ps.getActivation(this.lcc, false);
                this.ps.setSource(spsDescriptor.getText());
                this.ps.setSPSAction();
            }
            final StatementContext statementContext = this.lcc.getStatementContext();
            try {
                final ResultSet executeSubStatement = this.ps.executeSubStatement(this.activation, this.spsActivation, false, 0L);
                if (executeSubStatement.returnsRows()) {
                    while (executeSubStatement.getNextRow() != null) {}
                }
                executeSubStatement.close();
            }
            catch (StandardException ex) {
                final StatementContext statementContext2 = this.lcc.getStatementContext();
                if (statementContext2 != null && statementContext != statementContext2) {
                    statementContext2.cleanupOnError(ex);
                }
                if (ex.getMessageId().equals("XCL32.S")) {
                    n = 1;
                    spsDescriptor.revalidate(this.lcc);
                    continue;
                }
                this.spsActivation.close();
                throw ex;
            }
            break;
        }
    }
    
    protected void clearSPS() throws StandardException {
        if (this.spsActivation != null) {
            this.spsActivation.close();
        }
        this.ps = null;
        this.spsActivation = null;
    }
}
