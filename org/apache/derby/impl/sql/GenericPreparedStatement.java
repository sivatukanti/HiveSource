// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql;

import org.apache.derby.iapi.util.ReuseFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.impl.sql.compile.StatementNode;
import org.apache.derby.catalog.DependableFinder;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.iapi.sql.PreparedStatement;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.ParameterValueSet;
import org.apache.derby.iapi.services.stream.HeaderPrintWriter;
import org.apache.derby.iapi.sql.ResultSet;
import org.apache.derby.iapi.sql.conn.StatementContext;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.services.cache.Cacheable;
import java.sql.Timestamp;
import org.apache.derby.catalog.UUID;
import java.util.List;
import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;
import org.apache.derby.iapi.sql.execute.ExecCursorTableReference;
import java.sql.SQLWarning;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.services.loader.GeneratedClass;
import org.apache.derby.iapi.sql.Statement;
import org.apache.derby.iapi.sql.execute.ExecPreparedStatement;

public class GenericPreparedStatement implements ExecPreparedStatement
{
    public Statement statement;
    protected GeneratedClass activationClass;
    protected ResultDescription resultDesc;
    protected DataTypeDescriptor[] paramTypeDescriptors;
    private String spsName;
    private SQLWarning warnings;
    private boolean referencesSessionSchema;
    protected ExecCursorTableReference targetTable;
    protected ResultColumnDescriptor[] targetColumns;
    protected String[] updateColumns;
    protected int updateMode;
    protected ConstantAction executionConstants;
    protected Object[] savedObjects;
    protected List requiredPermissionsList;
    protected String UUIDString;
    protected UUID UUIDValue;
    private boolean needsSavepoint;
    private String execStmtName;
    private String execSchemaName;
    protected boolean isAtomic;
    protected String sourceTxt;
    private int inUseCount;
    boolean compilingStatement;
    boolean invalidatedWhileCompiling;
    protected long parseTime;
    protected long bindTime;
    protected long optimizeTime;
    protected long generateTime;
    protected long compileTime;
    protected Timestamp beginCompileTimestamp;
    protected Timestamp endCompileTimestamp;
    protected boolean isValid;
    protected boolean spsAction;
    private Cacheable cacheHolder;
    private long versionCounter;
    private RowCountStatistics rowCountStats;
    
    GenericPreparedStatement() {
        this.rowCountStats = new RowCountStatistics();
        this.UUIDValue = Monitor.getMonitor().getUUIDFactory().createUUID();
        this.UUIDString = this.UUIDValue.toString();
        this.spsAction = false;
    }
    
    public GenericPreparedStatement(final Statement statement) {
        this();
        this.statement = statement;
    }
    
    public synchronized boolean upToDate() throws StandardException {
        return this.isUpToDate();
    }
    
    public synchronized boolean upToDate(final GeneratedClass generatedClass) {
        return this.activationClass == generatedClass && this.isUpToDate();
    }
    
    private boolean isUpToDate() {
        return this.isValid && this.activationClass != null && !this.compilingStatement;
    }
    
    public void rePrepare(final LanguageConnectionContext languageConnectionContext) throws StandardException {
        this.rePrepare(languageConnectionContext, false);
    }
    
    public void rePrepare(final LanguageConnectionContext languageConnectionContext, final boolean b) throws StandardException {
        if (!this.upToDate()) {
            this.statement.prepare(languageConnectionContext, b);
        }
    }
    
    public Activation getActivation(final LanguageConnectionContext languageConnectionContext, final boolean b) throws StandardException {
        final GenericActivationHolder genericActivationHolder;
        synchronized (this) {
            GeneratedClass generatedClass = this.getActivationClass();
            if (generatedClass == null) {
                this.rePrepare(languageConnectionContext);
                generatedClass = this.getActivationClass();
            }
            genericActivationHolder = new GenericActivationHolder(languageConnectionContext, generatedClass, this, b);
            ++this.inUseCount;
        }
        languageConnectionContext.closeUnusedActivations();
        Activation activation = null;
        final StatementContext statementContext = languageConnectionContext.getStatementContext();
        if (statementContext != null) {
            activation = statementContext.getActivation();
        }
        genericActivationHolder.setParentActivation(activation);
        return genericActivationHolder;
    }
    
    public ResultSet executeSubStatement(final LanguageConnectionContext languageConnectionContext, final boolean b, final long n) throws StandardException {
        final Activation lastActivation = languageConnectionContext.getLastActivation();
        final Activation activation = this.getActivation(languageConnectionContext, false);
        activation.setSingleExecution();
        languageConnectionContext.setupSubStatementSessionContext(lastActivation);
        return this.executeStmt(activation, b, false, n);
    }
    
    public ResultSet executeSubStatement(final Activation activation, final Activation activation2, final boolean b, final long n) throws StandardException {
        activation.getLanguageConnectionContext().setupSubStatementSessionContext(activation);
        return this.executeStmt(activation2, b, false, n);
    }
    
    public ResultSet execute(final Activation activation, final boolean b, final long n) throws StandardException {
        return this.executeStmt(activation, false, b, n);
    }
    
    private ResultSet executeStmt(final Activation activation, final boolean b, final boolean b2, final long n) throws StandardException {
        boolean b3 = false;
        if (activation == null || activation.getPreparedStatement() != this) {
            throw StandardException.newException("XCL09.S", "execute");
        }
        LanguageConnectionContext languageConnectionContext;
        StatementContext pushStatementContext;
        ResultSet execute;
        while (true) {
            languageConnectionContext = activation.getLanguageConnectionContext();
            if (languageConnectionContext.getLogStatementText()) {
                final HeaderPrintWriter stream = Monitor.getStream();
                final String activeStateTxIdString = languageConnectionContext.getTransactionExecute().getActiveStateTxIdString();
                String string = "";
                final ParameterValueSet parameterValueSet = activation.getParameterValueSet();
                if (parameterValueSet != null && parameterValueSet.getParameterCount() > 0) {
                    string = " with " + parameterValueSet.getParameterCount() + " parameters " + parameterValueSet.toString();
                }
                stream.printlnWithHeader("(XID = " + activeStateTxIdString + "), " + "(SESSIONID = " + languageConnectionContext.getInstanceNumber() + "), " + "(DATABASE = " + languageConnectionContext.getDbname() + "), " + "(DRDAID = " + languageConnectionContext.getDrdaID() + "), Executing prepared statement: " + this.getSource() + " :End prepared statement" + string);
            }
            final ParameterValueSet parameterValueSet2 = activation.getParameterValueSet();
            if (!this.spsAction) {
                this.rePrepare(languageConnectionContext, b2);
            }
            pushStatementContext = languageConnectionContext.pushStatementContext(this.isAtomic, this.updateMode == 1, this.getSource(), parameterValueSet2, b, n);
            pushStatementContext.setActivation(activation);
            if (this.needsSavepoint()) {
                pushStatementContext.setSavePoint();
                b3 = true;
            }
            if (this.executionConstants != null) {
                languageConnectionContext.validateStmtExecution(this.executionConstants);
            }
            try {
                execute = activation.execute();
                execute.open();
            }
            catch (StandardException ex) {
                if (!ex.getMessageId().equals("XCL32.S") || this.spsAction) {
                    throw ex;
                }
                pushStatementContext.cleanupOnError(ex);
                continue;
            }
            break;
        }
        if (b3) {
            pushStatementContext.clearSavePoint();
        }
        languageConnectionContext.popStatementContext(pushStatementContext, null);
        if (activation.isSingleExecution() && execute.isClosed()) {
            activation.close();
        }
        return execute;
    }
    
    public ResultDescription getResultDescription() {
        return this.resultDesc;
    }
    
    public DataTypeDescriptor[] getParameterTypes() {
        return this.paramTypeDescriptors;
    }
    
    public String getSource() {
        return (this.sourceTxt != null) ? this.sourceTxt : ((this.statement == null) ? "null" : this.statement.getSource());
    }
    
    public void setSource(final String sourceTxt) {
        this.sourceTxt = sourceTxt;
    }
    
    public final void setSPSName(final String spsName) {
        this.spsName = spsName;
    }
    
    public String getSPSName() {
        return this.spsName;
    }
    
    public long getCompileTimeInMillis() {
        return this.compileTime;
    }
    
    public long getParseTimeInMillis() {
        return this.parseTime;
    }
    
    public long getBindTimeInMillis() {
        return this.bindTime;
    }
    
    public long getOptimizeTimeInMillis() {
        return this.optimizeTime;
    }
    
    public long getGenerateTimeInMillis() {
        return this.generateTime;
    }
    
    public Timestamp getBeginCompileTimestamp() {
        return this.beginCompileTimestamp;
    }
    
    public Timestamp getEndCompileTimestamp() {
        return this.endCompileTimestamp;
    }
    
    void setCompileTimeWarnings(final SQLWarning warnings) {
        this.warnings = warnings;
    }
    
    public final SQLWarning getCompileTimeWarnings() {
        return this.warnings;
    }
    
    protected void setCompileTimeMillis(final long parseTime, final long bindTime, final long optimizeTime, final long generateTime, final long compileTime, final Timestamp beginCompileTimestamp, final Timestamp endCompileTimestamp) {
        this.parseTime = parseTime;
        this.bindTime = bindTime;
        this.optimizeTime = optimizeTime;
        this.generateTime = generateTime;
        this.compileTime = compileTime;
        this.beginCompileTimestamp = beginCompileTimestamp;
        this.endCompileTimestamp = endCompileTimestamp;
    }
    
    public void finish(final LanguageConnectionContext languageConnectionContext) {
        synchronized (this) {
            --this.inUseCount;
            if (this.cacheHolder != null) {
                return;
            }
            if (this.inUseCount != 0) {
                return;
            }
        }
        try {
            this.makeInvalid(11, languageConnectionContext);
        }
        catch (StandardException ex) {}
    }
    
    final void setConstantAction(final ConstantAction executionConstants) {
        this.executionConstants = executionConstants;
    }
    
    public final ConstantAction getConstantAction() {
        return this.executionConstants;
    }
    
    final void setSavedObjects(final Object[] savedObjects) {
        this.savedObjects = savedObjects;
    }
    
    public final Object getSavedObject(final int n) {
        return this.savedObjects[n];
    }
    
    public final Object[] getSavedObjects() {
        return this.savedObjects;
    }
    
    public boolean isValid() {
        return this.isValid;
    }
    
    public void setValid() {
        this.isValid = true;
    }
    
    public void setSPSAction() {
        this.spsAction = true;
    }
    
    public void prepareToInvalidate(final Provider provider, final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
        switch (n) {
            case 3:
            case 5:
            case 48: {}
            default: {
                languageConnectionContext.verifyNoOpenResultSets(this, provider, n);
            }
        }
    }
    
    public void makeInvalid(final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
        switch (n) {
            case 48: {}
            default: {
                synchronized (this) {
                    if (this.compilingStatement) {
                        this.invalidatedWhileCompiling = true;
                        return;
                    }
                    final boolean b = !this.isValid;
                    this.isValid = false;
                    this.compilingStatement = true;
                }
                try {
                    languageConnectionContext.getDataDictionary().getDependencyManager().clearDependencies(languageConnectionContext, this);
                    if (this.execStmtName != null) {
                        switch (n) {
                            case 5:
                            case 23: {
                                final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
                                dataDictionary.getSPSDescriptor(this.execStmtName, dataDictionary.getSchemaDescriptor(this.execSchemaName, languageConnectionContext.getTransactionCompile(), true)).makeInvalid(n, languageConnectionContext);
                                break;
                            }
                        }
                    }
                }
                finally {
                    synchronized (this) {
                        this.compilingStatement = false;
                        this.notifyAll();
                    }
                }
            }
        }
    }
    
    public boolean isPersistent() {
        return false;
    }
    
    public DependableFinder getDependableFinder() {
        return null;
    }
    
    public String getObjectName() {
        return this.UUIDString;
    }
    
    public UUID getObjectID() {
        return this.UUIDValue;
    }
    
    public String getClassType() {
        return "PreparedStatement";
    }
    
    public boolean referencesSessionSchema() {
        return this.referencesSessionSchema;
    }
    
    public boolean referencesSessionSchema(final StatementNode statementNode) throws StandardException {
        return this.referencesSessionSchema = statementNode.referencesSessionSchema();
    }
    
    void completeCompile(final StatementNode statementNode) throws StandardException {
        this.paramTypeDescriptors = statementNode.getParameterTypes();
        if (this.targetTable != null) {
            this.targetTable = null;
            this.updateMode = 0;
            this.updateColumns = null;
            this.targetColumns = null;
        }
        this.resultDesc = statementNode.makeResultDescription();
        if (this.resultDesc != null) {
            final CursorInfo cursorInfo = (CursorInfo)statementNode.getCursorInfo();
            if (cursorInfo != null) {
                this.targetTable = cursorInfo.targetTable;
                this.targetColumns = cursorInfo.targetColumns;
                this.updateColumns = cursorInfo.updateColumns;
                this.updateMode = cursorInfo.updateMode;
            }
        }
        this.isValid = true;
        this.rowCountStats.reset();
    }
    
    public GeneratedClass getActivationClass() throws StandardException {
        return this.activationClass;
    }
    
    void setActivationClass(final GeneratedClass activationClass) {
        this.activationClass = activationClass;
    }
    
    public int getUpdateMode() {
        return this.updateMode;
    }
    
    public ExecCursorTableReference getTargetTable() {
        return this.targetTable;
    }
    
    public ResultColumnDescriptor[] getTargetColumns() {
        return this.targetColumns;
    }
    
    public String[] getUpdateColumns() {
        return this.updateColumns;
    }
    
    public Object getCursorInfo() {
        return new CursorInfo(this.updateMode, this.targetTable, this.targetColumns, this.updateColumns);
    }
    
    void setCursorInfo(final CursorInfo cursorInfo) {
        if (cursorInfo != null) {
            this.updateMode = cursorInfo.updateMode;
            this.targetTable = cursorInfo.targetTable;
            this.targetColumns = cursorInfo.targetColumns;
            this.updateColumns = cursorInfo.updateColumns;
        }
    }
    
    ByteArray getByteCodeSaver() {
        return null;
    }
    
    public boolean needsSavepoint() {
        return this.needsSavepoint;
    }
    
    void setNeedsSavepoint(final boolean needsSavepoint) {
        this.needsSavepoint = needsSavepoint;
    }
    
    void setIsAtomic(final boolean isAtomic) {
        this.isAtomic = isAtomic;
    }
    
    public boolean isAtomic() {
        return this.isAtomic;
    }
    
    void setExecuteStatementNameAndSchema(final String execStmtName, final String execSchemaName) {
        this.execStmtName = execStmtName;
        this.execSchemaName = execSchemaName;
    }
    
    public ExecPreparedStatement getClone() throws StandardException {
        final GenericPreparedStatement genericPreparedStatement = new GenericPreparedStatement(this.statement);
        genericPreparedStatement.activationClass = this.getActivationClass();
        genericPreparedStatement.resultDesc = this.resultDesc;
        genericPreparedStatement.paramTypeDescriptors = this.paramTypeDescriptors;
        genericPreparedStatement.executionConstants = this.executionConstants;
        genericPreparedStatement.UUIDString = this.UUIDString;
        genericPreparedStatement.UUIDValue = this.UUIDValue;
        genericPreparedStatement.savedObjects = this.savedObjects;
        genericPreparedStatement.execStmtName = this.execStmtName;
        genericPreparedStatement.execSchemaName = this.execSchemaName;
        genericPreparedStatement.isAtomic = this.isAtomic;
        genericPreparedStatement.sourceTxt = this.sourceTxt;
        genericPreparedStatement.targetTable = this.targetTable;
        genericPreparedStatement.targetColumns = this.targetColumns;
        genericPreparedStatement.updateColumns = this.updateColumns;
        genericPreparedStatement.updateMode = this.updateMode;
        genericPreparedStatement.needsSavepoint = this.needsSavepoint;
        genericPreparedStatement.rowCountStats = this.rowCountStats;
        return genericPreparedStatement;
    }
    
    public void setCacheHolder(final Cacheable cacheHolder) {
        this.cacheHolder = cacheHolder;
        if (cacheHolder == null) {
            if (!this.isValid || this.inUseCount != 0) {
                return;
            }
            final LanguageConnectionContext languageConnectionContext = (LanguageConnectionContext)ContextService.getFactory().getCurrentContextManager().getContext("LanguageConnectionContext");
            try {
                this.makeInvalid(11, languageConnectionContext);
            }
            catch (StandardException ex) {}
        }
    }
    
    public String toString() {
        return this.getObjectName();
    }
    
    public boolean isStorable() {
        return false;
    }
    
    public void setRequiredPermissionsList(final List requiredPermissionsList) {
        this.requiredPermissionsList = requiredPermissionsList;
    }
    
    public List getRequiredPermissionsList() {
        return this.requiredPermissionsList;
    }
    
    public final long getVersionCounter() {
        return this.versionCounter;
    }
    
    public final void incrementVersionCounter() {
        ++this.versionCounter;
    }
    
    public int incrementExecutionCount() {
        return this.rowCountStats.incrementExecutionCount();
    }
    
    public void setStalePlanCheckInterval(final int stalePlanCheckInterval) {
        this.rowCountStats.setStalePlanCheckInterval(stalePlanCheckInterval);
    }
    
    public int getStalePlanCheckInterval() {
        return this.rowCountStats.getStalePlanCheckInterval();
    }
    
    public long getInitialRowCount(final int n, final long n2) {
        return this.rowCountStats.getInitialRowCount(n, n2);
    }
    
    private static class RowCountStatistics
    {
        private int stalePlanCheckInterval;
        private int executionCount;
        private ArrayList rowCounts;
        
        int incrementExecutionCount() {
            return ++this.executionCount;
        }
        
        synchronized long getInitialRowCount(final int n, final long n2) {
            if (this.rowCounts == null) {
                this.rowCounts = new ArrayList();
            }
            if (n >= this.rowCounts.size()) {
                this.rowCounts.addAll(Collections.nCopies(n + 1 - this.rowCounts.size(), (Object)null));
            }
            final Long n3 = this.rowCounts.get(n);
            if (n3 == null) {
                this.rowCounts.set(n, ReuseFactory.getLong(n2));
                return n2;
            }
            return n3;
        }
        
        void setStalePlanCheckInterval(final int stalePlanCheckInterval) {
            this.stalePlanCheckInterval = stalePlanCheckInterval;
        }
        
        int getStalePlanCheckInterval() {
            return this.stalePlanCheckInterval;
        }
        
        synchronized void reset() {
            this.stalePlanCheckInterval = 0;
            this.executionCount = 0;
            this.rowCounts = null;
        }
    }
}
