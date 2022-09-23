// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.services.loader.GeneratedMethod;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.conn.StatementContext;
import org.apache.derby.iapi.sql.execute.ExecRow;

public class AnyResultSet extends NoPutResultSetImpl
{
    private ExecRow rowWithNulls;
    private StatementContext statementContext;
    public final NoPutResultSet source;
    private GeneratedMethod emptyRowFun;
    public int subqueryNumber;
    public int pointOfAttachment;
    
    public AnyResultSet(final NoPutResultSet source, final Activation activation, final GeneratedMethod emptyRowFun, final int n, final int subqueryNumber, final int pointOfAttachment, final double n2, final double n3) {
        super(activation, n, n2, n3);
        this.source = source;
        this.emptyRowFun = emptyRowFun;
        this.subqueryNumber = subqueryNumber;
        this.pointOfAttachment = pointOfAttachment;
        this.recordConstructorTime();
    }
    
    public void openCore() throws StandardException {
        if (this.isOpen) {
            this.reopenCore();
            return;
        }
        this.beginTime = this.getCurrentTimeMillis();
        this.source.openCore();
        if (this.statementContext == null) {
            this.statementContext = this.getLanguageConnectionContext().getStatementContext();
        }
        this.statementContext.setSubqueryResultSet(this.subqueryNumber, this, this.activation.getNumSubqueries());
        ++this.numOpens;
        this.isOpen = true;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    public void reopenCore() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        this.source.reopenCore();
        ++this.numOpens;
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    public void finish() throws StandardException {
        this.source.finish();
        this.finishAndRTS();
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        ExecRow currentRow = null;
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            final ExecRow nextRowCore = this.source.getNextRowCore();
            if (nextRowCore != null) {
                currentRow = nextRowCore;
            }
            else if (this.rowWithNulls == null) {
                this.rowWithNulls = (ExecRow)this.emptyRowFun.invoke(this.activation);
                currentRow = this.rowWithNulls;
            }
            else {
                currentRow = this.rowWithNulls;
            }
        }
        this.setCurrentRow(currentRow);
        ++this.rowsSeen;
        this.nextTime += this.getElapsedMillis(this.beginTime);
        return currentRow;
    }
    
    public void close() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            this.clearCurrentRow();
            this.source.close();
            super.close();
        }
        this.closeTime += this.getElapsedMillis(this.beginTime);
    }
    
    public int getPointOfAttachment() {
        return this.pointOfAttachment;
    }
    
    public long getTimeSpent(final int n) {
        final long n2 = this.constructorTime + this.openTime + this.nextTime + this.closeTime;
        if (n == 0) {
            return n2 - this.source.getTimeSpent(1);
        }
        return n2;
    }
}
