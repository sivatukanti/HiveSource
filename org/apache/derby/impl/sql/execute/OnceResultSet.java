// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.services.loader.GeneratedMethod;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.conn.StatementContext;
import org.apache.derby.iapi.sql.execute.ExecRow;

public class OnceResultSet extends NoPutResultSetImpl
{
    public static final int DO_CARDINALITY_CHECK = 1;
    public static final int NO_CARDINALITY_CHECK = 2;
    public static final int UNIQUE_CARDINALITY_CHECK = 3;
    private ExecRow rowWithNulls;
    private StatementContext statementContext;
    public NoPutResultSet source;
    private GeneratedMethod emptyRowFun;
    private int cardinalityCheck;
    public int subqueryNumber;
    public int pointOfAttachment;
    
    public OnceResultSet(final NoPutResultSet source, final Activation activation, final GeneratedMethod emptyRowFun, final int cardinalityCheck, final int n, final int subqueryNumber, final int pointOfAttachment, final double n2, final double n3) {
        super(activation, n, n2, n3);
        this.source = source;
        this.emptyRowFun = emptyRowFun;
        this.cardinalityCheck = cardinalityCheck;
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
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        ExecRow currentRow = null;
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            final ExecRow nextRowCore = this.source.getNextRowCore();
            if (nextRowCore != null) {
                switch (this.cardinalityCheck) {
                    case 1:
                    case 2: {
                        final ExecRow clone = nextRowCore.getClone();
                        if (this.cardinalityCheck == 1 && this.source.getNextRowCore() != null) {
                            this.close();
                            throw StandardException.newException("21000");
                        }
                        currentRow = clone;
                        break;
                    }
                    case 3: {
                        final ExecRow clone2 = nextRowCore.getClone();
                        ExecRow execRow = this.source.getNextRowCore();
                        final DataValueDescriptor column = clone2.getColumn(1);
                        while (execRow != null) {
                            if (!column.compare(2, execRow.getColumn(1), true, true)) {
                                this.close();
                                throw StandardException.newException("21000");
                            }
                            execRow = this.source.getNextRowCore();
                        }
                        currentRow = clone2;
                        break;
                    }
                }
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
