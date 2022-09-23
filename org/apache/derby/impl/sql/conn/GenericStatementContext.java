// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.conn;

import java.util.Iterator;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.sql.depend.Dependency;
import org.apache.derby.iapi.error.StandardException;
import java.util.TimerTask;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.services.timer.TimerFactory;
import org.apache.derby.iapi.sql.conn.SQLSessionContext;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.ParameterValueSet;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import java.util.ArrayList;
import org.apache.derby.iapi.sql.ResultSet;
import org.apache.derby.iapi.sql.conn.StatementContext;
import org.apache.derby.iapi.services.context.ContextImpl;

final class GenericStatementContext extends ContextImpl implements StatementContext
{
    private boolean setSavePoint;
    private String internalSavePointName;
    private ResultSet topResultSet;
    private ArrayList dependencies;
    private NoPutResultSet[] subqueryTrackingArray;
    private NoPutResultSet[] materializedSubqueries;
    private final LanguageConnectionContext lcc;
    private boolean inUse;
    private volatile boolean cancellationFlag;
    private CancelQueryTask cancelTask;
    private boolean parentInTrigger;
    private boolean isForReadOnly;
    private boolean isAtomic;
    private boolean isSystemCode;
    private boolean rollbackParentContext;
    private boolean statementWasInvalidated;
    private String stmtText;
    private ParameterValueSet pvs;
    private short sqlAllowed;
    private Activation activation;
    private SQLSessionContext sqlSessionContext;
    
    GenericStatementContext(final LanguageConnectionContext lcc) {
        super(lcc.getContextManager(), "StatementContext");
        this.inUse = true;
        this.cancellationFlag = false;
        this.cancelTask = null;
        this.isForReadOnly = false;
        this.sqlAllowed = -1;
        this.lcc = lcc;
        this.internalSavePointName = lcc.getUniqueSavepointName();
    }
    
    private static TimerFactory getTimerFactory() {
        return Monitor.getMonitor().getTimerFactory();
    }
    
    public void setInUse(final boolean parentInTrigger, final boolean isAtomic, final boolean isForReadOnly, final String stmtText, final ParameterValueSet pvs, final long n) {
        this.inUse = true;
        this.parentInTrigger = parentInTrigger;
        this.isForReadOnly = isForReadOnly;
        this.isAtomic = isAtomic;
        this.stmtText = stmtText;
        this.pvs = pvs;
        this.rollbackParentContext = false;
        if (n > 0L) {
            this.cancelTask = new CancelQueryTask(this);
            getTimerFactory().schedule(this.cancelTask, n);
        }
    }
    
    public void clearInUse() {
        this.stuffTopResultSet(null, null);
        this.inUse = false;
        this.parentInTrigger = false;
        this.isAtomic = false;
        this.isForReadOnly = false;
        this.stmtText = null;
        this.sqlAllowed = -1;
        this.isSystemCode = false;
        this.rollbackParentContext = false;
        this.statementWasInvalidated = false;
        if (this.cancelTask != null) {
            this.cancelTask.forgetContext();
            this.cancelTask = null;
        }
        this.cancellationFlag = false;
        this.activation = null;
        this.sqlSessionContext = null;
    }
    
    public void setSavePoint() throws StandardException {
        this.pleaseBeOnStack();
        this.lcc.getTransactionExecute().setSavePoint(this.internalSavePointName, null);
        this.setSavePoint = true;
    }
    
    public void resetSavePoint() throws StandardException {
        if (this.inUse && this.setSavePoint) {
            this.lcc.getTransactionExecute().setSavePoint(this.internalSavePointName, null);
        }
    }
    
    public void clearSavePoint() throws StandardException {
        this.pleaseBeOnStack();
        this.lcc.getTransactionExecute().releaseSavePoint(this.internalSavePointName, null);
        this.setSavePoint = false;
    }
    
    public void setTopResultSet(final ResultSet set, NoPutResultSet[] materializedSubqueries) throws StandardException {
        this.pleaseBeOnStack();
        if (this.materializedSubqueries != null) {
            if (materializedSubqueries != null) {
                for (int i = 0; i < materializedSubqueries.length; ++i) {
                    if (this.subqueryTrackingArray[i] != null) {
                        materializedSubqueries[i] = this.materializedSubqueries[i];
                    }
                }
            }
            else {
                materializedSubqueries = this.materializedSubqueries;
            }
            this.materializedSubqueries = null;
        }
        this.stuffTopResultSet(set, materializedSubqueries);
    }
    
    private void stuffTopResultSet(final ResultSet topResultSet, final NoPutResultSet[] subqueryTrackingArray) {
        this.topResultSet = topResultSet;
        this.subqueryTrackingArray = subqueryTrackingArray;
        this.dependencies = null;
    }
    
    public void setSubqueryResultSet(final int n, final NoPutResultSet set, final int n2) throws StandardException {
        this.pleaseBeOnStack();
        if (this.subqueryTrackingArray == null) {
            if (this.topResultSet == null) {
                this.subqueryTrackingArray = new NoPutResultSet[n2];
                this.materializedSubqueries = new NoPutResultSet[n2];
            }
            else {
                this.subqueryTrackingArray = this.topResultSet.getSubqueryTrackingArray(n2);
            }
        }
        this.subqueryTrackingArray[n] = set;
        if (this.materializedSubqueries != null) {
            this.materializedSubqueries[n] = set;
        }
    }
    
    public NoPutResultSet[] getSubqueryTrackingArray() throws StandardException {
        this.pleaseBeOnStack();
        return this.subqueryTrackingArray;
    }
    
    public void addDependency(final Dependency e) throws StandardException {
        this.pleaseBeOnStack();
        if (this.dependencies == null) {
            this.dependencies = new ArrayList();
        }
        this.dependencies.add(e);
    }
    
    public boolean inTrigger() {
        return this.parentInTrigger;
    }
    
    public void cleanupOnError(final Throwable t) throws StandardException {
        int severity = 40000;
        if (t instanceof StandardException) {
            final StandardException ex = (StandardException)t;
            severity = ex.getSeverity();
            if ("XCL32.S".equals(ex.getMessageId())) {
                this.statementWasInvalidated = true;
            }
        }
        if (!this.inUse) {
            return;
        }
        if (this.topResultSet != null) {
            this.topResultSet.cleanUp();
        }
        if (this.subqueryTrackingArray != null) {
            for (int i = 0; i < this.subqueryTrackingArray.length; ++i) {
                if (this.subqueryTrackingArray[i] != null) {
                    this.subqueryTrackingArray[i].cleanUp();
                }
            }
        }
        if (this.dependencies != null) {
            final DependencyManager dependencyManager = this.lcc.getDataDictionary().getDependencyManager();
            final Iterator<Dependency> iterator = (Iterator<Dependency>)this.dependencies.iterator();
            while (iterator.hasNext()) {
                dependencyManager.clearInMemoryDependency(iterator.next());
            }
            this.dependencies = null;
        }
        if (severity <= 20000 && this.setSavePoint) {
            this.lcc.internalRollbackToSavepoint(this.internalSavePointName, false, null);
            this.clearSavePoint();
        }
        if (severity >= 30000) {
            this.setSavePoint = false;
        }
        this.lcc.popStatementContext(this, t);
    }
    
    public boolean isLastHandler(final int n) {
        return this.inUse && !this.rollbackParentContext && n == 20000;
    }
    
    public boolean onStack() {
        return this.inUse;
    }
    
    public boolean isAtomic() {
        return this.isAtomic;
    }
    
    public String getStatementText() {
        return this.stmtText;
    }
    
    private void pleaseBeOnStack() throws StandardException {
        if (!this.inUse) {
            throw StandardException.newException("40XC0");
        }
    }
    
    public boolean inUse() {
        return this.inUse;
    }
    
    public boolean isForReadOnly() {
        return this.isForReadOnly;
    }
    
    public boolean isCancelled() {
        return this.cancellationFlag;
    }
    
    public void cancel() {
        this.cancellationFlag = true;
    }
    
    public void setSQLAllowed(final short sqlAllowed, final boolean b) {
        if (b || sqlAllowed > this.sqlAllowed) {
            this.sqlAllowed = sqlAllowed;
        }
    }
    
    public short getSQLAllowed() {
        if (!this.inUse) {
            return 3;
        }
        return this.sqlAllowed;
    }
    
    public void setParentRollback() {
        this.rollbackParentContext = true;
    }
    
    public void setSystemCode() {
        this.isSystemCode = true;
    }
    
    public boolean getSystemCode() {
        return this.isSystemCode;
    }
    
    public StringBuffer appendErrorInfo() {
        final StringBuffer appendErrorInfo = ((ContextImpl)this.lcc).appendErrorInfo();
        if (appendErrorInfo != null) {
            appendErrorInfo.append("Failed Statement is: ");
            appendErrorInfo.append(this.getStatementText());
            if (this.pvs != null && this.pvs.getParameterCount() > 0) {
                appendErrorInfo.append(" with " + this.pvs.getParameterCount() + " parameters " + this.pvs.toString());
            }
        }
        return appendErrorInfo;
    }
    
    public void setActivation(final Activation activation) {
        this.activation = activation;
    }
    
    public Activation getActivation() {
        return this.activation;
    }
    
    public SQLSessionContext getSQLSessionContext() {
        return this.sqlSessionContext;
    }
    
    public void setSQLSessionContext(final SQLSessionContext sqlSessionContext) {
        this.sqlSessionContext = sqlSessionContext;
    }
    
    public boolean getStatementWasInvalidated() {
        return this.statementWasInvalidated;
    }
    
    private static class CancelQueryTask extends TimerTask
    {
        private StatementContext statementContext;
        
        public CancelQueryTask(final StatementContext statementContext) {
            this.statementContext = statementContext;
        }
        
        public void run() {
            synchronized (this) {
                if (this.statementContext != null) {
                    this.statementContext.cancel();
                }
            }
        }
        
        public void forgetContext() {
            synchronized (this) {
                this.statementContext = null;
            }
            getTimerFactory().cancel(this);
        }
    }
}
