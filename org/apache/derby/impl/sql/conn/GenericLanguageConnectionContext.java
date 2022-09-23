// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.conn;

import org.apache.derby.iapi.services.io.FormatableBitSet;
import java.util.IdentityHashMap;
import org.apache.derby.iapi.sql.dictionary.RoleGrantDescriptor;
import org.apache.derby.impl.sql.execute.InternalTriggerExecutionContext;
import org.apache.derby.impl.sql.execute.AutoincrementCounter;
import org.apache.derby.iapi.sql.execute.ExecPreparedStatement;
import org.apache.derby.iapi.util.InterruptStatus;
import org.apache.derby.iapi.sql.execute.ExecutionContext;
import java.util.Iterator;
import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.db.TriggerExecutionContext;
import org.apache.derby.iapi.sql.execute.ExecutionStmtValidator;
import org.apache.derby.iapi.sql.ParameterValueSet;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.impl.sql.compile.CompilerContextImpl;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import java.util.List;
import org.apache.derby.iapi.sql.dictionary.IndexRowGenerator;
import java.util.Properties;
import org.apache.derby.iapi.store.access.ColumnOrdering;
import org.apache.derby.iapi.store.access.XATransactionController;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.impl.sql.GenericPreparedStatement;
import org.apache.derby.iapi.services.cache.Cacheable;
import org.apache.derby.iapi.services.cache.CacheManager;
import org.apache.derby.impl.sql.GenericStatement;
import org.apache.derby.iapi.sql.ResultSet;
import org.apache.derby.iapi.sql.execute.CursorActivation;
import org.apache.derby.iapi.sql.PreparedStatement;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.util.IdUtil;
import org.apache.derby.iapi.services.property.PersistentSet;
import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.iapi.services.context.ContextManager;
import java.util.WeakHashMap;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.compile.ASTVisitor;
import java.util.HashMap;
import org.apache.derby.iapi.services.stream.HeaderPrintWriter;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.conn.SQLSessionContext;
import org.apache.derby.iapi.sql.conn.Authorizer;
import org.apache.derby.iapi.sql.conn.StatementContext;
import org.apache.derby.iapi.sql.conn.LanguageConnectionFactory;
import org.apache.derby.iapi.sql.compile.OptimizerFactory;
import org.apache.derby.iapi.sql.compile.TypeCompilerFactory;
import org.apache.derby.iapi.sql.LanguageFactory;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.db.Database;
import org.apache.derby.iapi.sql.execute.RunTimeStatistics;
import java.util.Map;
import java.util.ArrayList;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.services.context.ContextImpl;

public class GenericLanguageConnectionContext extends ContextImpl implements LanguageConnectionContext
{
    private static final int NON_XA = 0;
    private static final int XA_ONE_PHASE = 1;
    private static final int XA_TWO_PHASE = 2;
    private final ArrayList acts;
    private volatile boolean unusedActs;
    private int maxActsSize;
    protected int bindCount;
    private boolean ddWriteMode;
    private boolean runTimeStatisticsSetting;
    private boolean statisticsTiming;
    private boolean xplainOnlyMode;
    private String xplain_schema;
    private Map xplain_statements;
    private ArrayList allDeclaredGlobalTempTables;
    private int currentSavepointLevel;
    protected long nextCursorId;
    protected int nextSavepointId;
    private RunTimeStatistics runTimeStatisticsObject;
    private StringBuffer sb;
    private Database db;
    private final int instanceNumber;
    private String drdaID;
    private String dbname;
    private Object lastQueryTree;
    private final TransactionController tran;
    private TransactionController readOnlyNestedTransaction;
    private int queryNestingDepth;
    protected DataValueFactory dataFactory;
    protected LanguageFactory langFactory;
    protected TypeCompilerFactory tcf;
    protected OptimizerFactory of;
    protected LanguageConnectionFactory connFactory;
    private final StatementContext[] statementContexts;
    private int statementDepth;
    protected int outermostTrigger;
    protected Authorizer authorizer;
    protected String userName;
    private SQLSessionContext topLevelSSC;
    private SchemaDescriptor cachedInitialDefaultSchemaDescr;
    private int defaultIsolationLevel;
    protected int isolationLevel;
    private boolean isolationLevelExplicitlySet;
    private boolean isolationLevelSetUsingSQLorJDBC;
    protected int prepareIsolationLevel;
    private boolean logStatementText;
    private boolean logQueryPlan;
    private HeaderPrintWriter istream;
    private int lockEscalationThreshold;
    private ArrayList stmtValidators;
    private ArrayList triggerExecutionContexts;
    private ArrayList triggerTables;
    private boolean optimizerTrace;
    private boolean optimizerTraceHtml;
    private String lastOptimizerTraceOutput;
    private String optimizerTraceOutput;
    private HashMap autoincrementHT;
    private boolean autoincrementUpdate;
    private long identityVal;
    private boolean identityNotNull;
    private HashMap autoincrementCacheHashtable;
    private ASTVisitor astWalker;
    private StandardException interruptedException;
    private WeakHashMap referencedColumnMap;
    private String sessionUser;
    Map printedObjectsMap;
    
    public GenericLanguageConnectionContext(final ContextManager contextManager, final TransactionController tran, final LanguageFactory langFactory, final LanguageConnectionFactory connFactory, final Database db, final String userName, final int instanceNumber, final String drdaID, final String dbname) throws StandardException {
        super(contextManager, "LanguageConnectionContext");
        this.unusedActs = false;
        this.xplainOnlyMode = false;
        this.xplain_schema = null;
        this.xplain_statements = new HashMap();
        this.currentSavepointLevel = 0;
        this.statementContexts = new StatementContext[2];
        this.outermostTrigger = -1;
        this.userName = null;
        this.cachedInitialDefaultSchemaDescr = null;
        this.defaultIsolationLevel = 2;
        this.isolationLevel = this.defaultIsolationLevel;
        this.isolationLevelExplicitlySet = false;
        this.isolationLevelSetUsingSQLorJDBC = false;
        this.prepareIsolationLevel = 0;
        this.sessionUser = null;
        this.printedObjectsMap = null;
        this.acts = new ArrayList();
        this.tran = tran;
        this.dataFactory = connFactory.getDataValueFactory();
        this.tcf = connFactory.getTypeCompilerFactory();
        this.of = connFactory.getOptimizerFactory();
        this.langFactory = langFactory;
        this.connFactory = connFactory;
        this.db = db;
        this.userName = userName;
        this.instanceNumber = instanceNumber;
        this.drdaID = drdaID;
        this.dbname = dbname;
        this.logStatementText = Boolean.valueOf(PropertyUtil.getServiceProperty(this.getTransactionCompile(), "derby.language.logStatementText"));
        this.setRunTimeStatisticsMode(this.logQueryPlan = Boolean.valueOf(PropertyUtil.getServiceProperty(this.getTransactionCompile(), "derby.language.logQueryPlan")));
        this.lockEscalationThreshold = PropertyUtil.getServiceInt(tran, "derby.locks.escalationThreshold", 100, Integer.MAX_VALUE, 5000);
        this.stmtValidators = new ArrayList();
        this.triggerExecutionContexts = new ArrayList();
        this.triggerTables = new ArrayList();
    }
    
    public void initialize() throws StandardException {
        this.interruptedException = null;
        this.sessionUser = IdUtil.getUserAuthorizationId(this.userName);
        this.authorizer = new GenericAuthorizer(this);
        this.setDefaultSchema(this.initDefaultSchemaDescriptor());
        this.referencedColumnMap = new WeakHashMap();
    }
    
    protected SchemaDescriptor initDefaultSchemaDescriptor() throws StandardException {
        if (this.cachedInitialDefaultSchemaDescr == null) {
            final DataDictionary dataDictionary = this.getDataDictionary();
            this.getSessionUserId();
            SchemaDescriptor schemaDescriptor = dataDictionary.getSchemaDescriptor(this.getSessionUserId(), this.getTransactionCompile(), false);
            if (schemaDescriptor == null) {
                schemaDescriptor = new SchemaDescriptor(dataDictionary, this.getSessionUserId(), this.getSessionUserId(), null, false);
            }
            this.cachedInitialDefaultSchemaDescr = schemaDescriptor;
        }
        return this.cachedInitialDefaultSchemaDescr;
    }
    
    private SchemaDescriptor getInitialDefaultSchemaDescriptor() {
        return this.cachedInitialDefaultSchemaDescr;
    }
    
    public boolean getLogStatementText() {
        return this.logStatementText;
    }
    
    public void setLogStatementText(final boolean logStatementText) {
        this.logStatementText = logStatementText;
    }
    
    public boolean getLogQueryPlan() {
        return this.logQueryPlan;
    }
    
    public boolean usesSqlAuthorization() {
        return this.getDataDictionary().usesSqlAuthorization();
    }
    
    public int getLockEscalationThreshold() {
        return this.lockEscalationThreshold;
    }
    
    public void addActivation(final Activation e) throws StandardException {
        this.acts.add(e);
        if (this.acts.size() > this.maxActsSize) {
            this.maxActsSize = this.acts.size();
        }
    }
    
    public void closeUnusedActivations() throws StandardException {
        if (this.unusedActs && this.acts.size() > 20) {
            this.unusedActs = false;
            for (int i = this.acts.size() - 1; i >= 0; --i) {
                if (i < this.acts.size()) {
                    final Activation activation = this.acts.get(i);
                    if (!activation.isInUse()) {
                        activation.close();
                    }
                }
            }
        }
    }
    
    public void notifyUnusedActivation() {
        this.unusedActs = true;
    }
    
    public boolean checkIfAnyDeclaredGlobalTempTablesForThisConnection() {
        return this.allDeclaredGlobalTempTables != null;
    }
    
    public void addDeclaredGlobalTempTable(final TableDescriptor tableDescriptor) throws StandardException {
        if (this.findDeclaredGlobalTempTable(tableDescriptor.getName()) != null) {
            throw StandardException.newException("X0Y32.S", "Declared global temporary table", tableDescriptor.getName(), "Schema", "SESSION");
        }
        final TempTableInfo e = new TempTableInfo(tableDescriptor, this.currentSavepointLevel);
        if (this.allDeclaredGlobalTempTables == null) {
            this.allDeclaredGlobalTempTables = new ArrayList();
        }
        this.allDeclaredGlobalTempTables.add(e);
    }
    
    public boolean dropDeclaredGlobalTempTable(final String s) {
        final TempTableInfo declaredGlobalTempTable = this.findDeclaredGlobalTempTable(s);
        if (declaredGlobalTempTable != null) {
            if (declaredGlobalTempTable.getDeclaredInSavepointLevel() == this.currentSavepointLevel) {
                this.allDeclaredGlobalTempTables.remove(this.allDeclaredGlobalTempTables.indexOf(declaredGlobalTempTable));
                if (this.allDeclaredGlobalTempTables.size() == 0) {
                    this.allDeclaredGlobalTempTables = null;
                }
            }
            else {
                declaredGlobalTempTable.setDroppedInSavepointLevel(this.currentSavepointLevel);
            }
            return true;
        }
        return false;
    }
    
    private void tempTablesReleaseSavepointLevels() {
        for (int i = 0; i < this.allDeclaredGlobalTempTables.size(); ++i) {
            final TempTableInfo tempTableInfo = this.allDeclaredGlobalTempTables.get(i);
            if (tempTableInfo.getDroppedInSavepointLevel() > this.currentSavepointLevel) {
                tempTableInfo.setDroppedInSavepointLevel(this.currentSavepointLevel);
            }
            if (tempTableInfo.getDeclaredInSavepointLevel() > this.currentSavepointLevel) {
                tempTableInfo.setDeclaredInSavepointLevel(this.currentSavepointLevel);
            }
            if (tempTableInfo.getModifiedInSavepointLevel() > this.currentSavepointLevel) {
                tempTableInfo.setModifiedInSavepointLevel(this.currentSavepointLevel);
            }
        }
    }
    
    private void tempTablesAndCommit(final boolean b) throws StandardException {
        for (int i = this.allDeclaredGlobalTempTables.size() - 1; i >= 0; --i) {
            final TempTableInfo tempTableInfo = this.allDeclaredGlobalTempTables.get(i);
            if (tempTableInfo.getDroppedInSavepointLevel() != -1) {
                this.allDeclaredGlobalTempTables.remove(i);
            }
            else {
                tempTableInfo.setDeclaredInSavepointLevel(-1);
                tempTableInfo.setModifiedInSavepointLevel(-1);
            }
        }
        for (int j = 0; j < this.allDeclaredGlobalTempTables.size(); ++j) {
            final TableDescriptor tableDescriptor = this.allDeclaredGlobalTempTables.get(j).getTableDescriptor();
            if (tableDescriptor.isOnCommitDeleteRows()) {
                if (!this.checkIfAnyActivationHasHoldCursor(tableDescriptor.getName())) {
                    this.getDataDictionary().getDependencyManager().invalidateFor(tableDescriptor, 1, this);
                    if (!b) {
                        this.cleanupTempTableOnCommitOrRollback(tableDescriptor, true);
                    }
                }
            }
        }
    }
    
    private void tempTablesXApostCommit() throws StandardException {
        final TransactionController transactionExecute = this.getTransactionExecute();
        for (int i = 0; i < this.allDeclaredGlobalTempTables.size(); ++i) {
            transactionExecute.dropConglomerate(((TempTableInfo)this.allDeclaredGlobalTempTables.get(i)).getTableDescriptor().getHeapConglomerateId());
            this.allDeclaredGlobalTempTables.remove(i);
        }
        transactionExecute.commit();
    }
    
    public void resetFromPool() throws StandardException {
        this.interruptedException = null;
        this.identityNotNull = false;
        this.dropAllDeclaredGlobalTempTables();
        this.setDefaultSchema(null);
        this.getCurrentSQLSessionContext().setRole(null);
        this.getCurrentSQLSessionContext().setUser(this.getSessionUserId());
        this.referencedColumnMap = new WeakHashMap();
    }
    
    public void setLastQueryTree(final Object lastQueryTree) {
        this.lastQueryTree = lastQueryTree;
    }
    
    public Object getLastQueryTree() {
        return this.lastQueryTree;
    }
    
    private void dropAllDeclaredGlobalTempTables() throws StandardException {
        if (this.allDeclaredGlobalTempTables == null) {
            return;
        }
        final DependencyManager dependencyManager = this.getDataDictionary().getDependencyManager();
        StandardException ex = null;
        for (int i = 0; i < this.allDeclaredGlobalTempTables.size(); ++i) {
            try {
                final TableDescriptor tableDescriptor = this.allDeclaredGlobalTempTables.get(i).getTableDescriptor();
                dependencyManager.invalidateFor(tableDescriptor, 1, this);
                this.tran.dropConglomerate(tableDescriptor.getHeapConglomerateId());
            }
            catch (StandardException ex2) {
                if (ex == null) {
                    ex = ex2;
                }
                else {
                    try {
                        ex2.initCause(ex);
                        ex = ex2;
                    }
                    catch (IllegalStateException ex4) {}
                }
            }
        }
        this.allDeclaredGlobalTempTables = null;
        try {
            this.internalCommit(true);
        }
        catch (StandardException ex3) {
            if (ex == null) {
                ex = ex3;
            }
            else {
                try {
                    ex3.initCause(ex);
                    ex = ex3;
                }
                catch (IllegalStateException ex5) {}
            }
        }
        if (ex != null) {
            throw ex;
        }
    }
    
    private void tempTablesAndRollback() throws StandardException {
        for (int i = this.allDeclaredGlobalTempTables.size() - 1; i >= 0; --i) {
            final TempTableInfo element = this.allDeclaredGlobalTempTables.get(i);
            if (element.getDeclaredInSavepointLevel() >= this.currentSavepointLevel) {
                if (element.getDroppedInSavepointLevel() == -1) {
                    final TableDescriptor tableDescriptor = element.getTableDescriptor();
                    this.invalidateCleanupDroppedTable(tableDescriptor);
                    this.tran.dropConglomerate(tableDescriptor.getHeapConglomerateId());
                    this.allDeclaredGlobalTempTables.remove(i);
                }
                else if (element.getDroppedInSavepointLevel() >= this.currentSavepointLevel) {
                    this.allDeclaredGlobalTempTables.remove(i);
                }
            }
            else if (element.getDroppedInSavepointLevel() >= this.currentSavepointLevel) {
                element.setTableDescriptor(this.cleanupTempTableOnCommitOrRollback(element.getTableDescriptor(), false));
                element.setDroppedInSavepointLevel(-1);
                element.setModifiedInSavepointLevel(-1);
                this.allDeclaredGlobalTempTables.set(i, element);
            }
            else if (element.getModifiedInSavepointLevel() >= this.currentSavepointLevel) {
                element.setModifiedInSavepointLevel(-1);
                this.invalidateCleanupDroppedTable(element.getTableDescriptor());
            }
        }
        if (this.allDeclaredGlobalTempTables.size() == 0) {
            this.allDeclaredGlobalTempTables = null;
        }
    }
    
    private void invalidateCleanupDroppedTable(final TableDescriptor tableDescriptor) throws StandardException {
        this.getDataDictionary().getDependencyManager().invalidateFor(tableDescriptor, 1, this);
        this.cleanupTempTableOnCommitOrRollback(tableDescriptor, true);
    }
    
    private void replaceDeclaredGlobalTempTable(final String s, final TableDescriptor tableDescriptor) {
        final TempTableInfo declaredGlobalTempTable = this.findDeclaredGlobalTempTable(s);
        declaredGlobalTempTable.setDroppedInSavepointLevel(-1);
        declaredGlobalTempTable.setDeclaredInSavepointLevel(-1);
        declaredGlobalTempTable.setTableDescriptor(tableDescriptor);
        this.allDeclaredGlobalTempTables.set(this.allDeclaredGlobalTempTables.indexOf(declaredGlobalTempTable), declaredGlobalTempTable);
    }
    
    public TableDescriptor getTableDescriptorForDeclaredGlobalTempTable(final String s) {
        final TempTableInfo declaredGlobalTempTable = this.findDeclaredGlobalTempTable(s);
        if (declaredGlobalTempTable == null) {
            return null;
        }
        return declaredGlobalTempTable.getTableDescriptor();
    }
    
    private TempTableInfo findDeclaredGlobalTempTable(final String s) {
        if (this.allDeclaredGlobalTempTables == null) {
            return null;
        }
        for (int i = 0; i < this.allDeclaredGlobalTempTables.size(); ++i) {
            if (((TempTableInfo)this.allDeclaredGlobalTempTables.get(i)).matches(s)) {
                return (TempTableInfo)this.allDeclaredGlobalTempTables.get(i);
            }
        }
        return null;
    }
    
    public void markTempTableAsModifiedInUnitOfWork(final String s) {
        this.findDeclaredGlobalTempTable(s).setModifiedInSavepointLevel(this.currentSavepointLevel);
    }
    
    public PreparedStatement prepareInternalStatement(SchemaDescriptor systemSchemaDescriptor, final String s, final boolean b, final boolean b2) throws StandardException {
        if (b2) {
            systemSchemaDescriptor = this.getDataDictionary().getSystemSchemaDescriptor();
        }
        return this.connFactory.getStatement(systemSchemaDescriptor, s, b).prepare(this, b2);
    }
    
    public PreparedStatement prepareInternalStatement(final String s) throws StandardException {
        return this.connFactory.getStatement(this.getDefaultSchema(), s, true).prepare(this);
    }
    
    public void removeActivation(final Activation o) {
        this.acts.remove(o);
        if (this.maxActsSize > 20 && this.maxActsSize > 2 * this.acts.size()) {
            this.acts.trimToSize();
            this.maxActsSize = this.acts.size();
        }
    }
    
    public int getActivationCount() {
        return this.acts.size();
    }
    
    public CursorActivation lookupCursorActivation(final String s) {
        final int size = this.acts.size();
        if (size > 0) {
            final int hashCode = s.hashCode();
            for (int i = 0; i < size; ++i) {
                final Activation activation = this.acts.get(i);
                if (activation.isInUse()) {
                    final String cursorName = activation.getCursorName();
                    if (cursorName != null) {
                        if (cursorName.hashCode() == hashCode) {
                            if (s.equals(cursorName)) {
                                final ResultSet resultSet = activation.getResultSet();
                                if (resultSet != null) {
                                    if (!resultSet.isClosed()) {
                                        return (CursorActivation)activation;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public void removeStatement(final GenericStatement genericStatement) throws StandardException {
        final CacheManager statementCache = this.getLanguageConnectionFactory().getStatementCache();
        if (statementCache == null) {
            return;
        }
        final Cacheable cached = statementCache.findCached(genericStatement);
        if (cached != null) {
            if (genericStatement.getPreparedStatement() != ((CachedStatement)cached).getPreparedStatement()) {
                statementCache.release(cached);
            }
            else {
                statementCache.remove(cached);
            }
        }
    }
    
    public PreparedStatement lookupStatement(final GenericStatement genericStatement) throws StandardException {
        final CacheManager statementCache = this.getLanguageConnectionFactory().getStatementCache();
        if (statementCache == null) {
            return null;
        }
        if (this.dataDictionaryInWriteMode()) {
            return null;
        }
        final Cacheable find = statementCache.find(genericStatement);
        final GenericPreparedStatement preparedStatement = ((CachedStatement)find).getPreparedStatement();
        synchronized (preparedStatement) {
            if (preparedStatement.upToDate() && preparedStatement.getActivationClass().getClassLoaderVersion() != this.getLanguageConnectionFactory().getClassFactory().getClassLoaderVersion()) {
                preparedStatement.makeInvalid(23, this);
            }
        }
        statementCache.release(find);
        return preparedStatement;
    }
    
    public String getUniqueCursorName() {
        return this.getNameString("SQLCUR", this.nextCursorId++);
    }
    
    public String getUniqueSavepointName() {
        return this.getNameString("SAVEPT", this.nextSavepointId++);
    }
    
    public int getUniqueSavepointID() {
        return this.nextSavepointId - 1;
    }
    
    private String getNameString(final String str, final long lng) {
        if (this.sb != null) {
            this.sb.setLength(0);
        }
        else {
            this.sb = new StringBuffer();
        }
        this.sb.append(str).append(lng);
        return this.sb.toString();
    }
    
    public void internalCommit(final boolean b) throws StandardException {
        this.doCommit(b, true, 0, false);
    }
    
    public void userCommit() throws StandardException {
        this.doCommit(true, true, 0, true);
    }
    
    public final void internalCommitNoSync(final int n) throws StandardException {
        this.doCommit(true, false, n, false);
    }
    
    public final void xaCommit(final boolean b) throws StandardException {
        this.doCommit(true, true, b ? 1 : 2, true);
    }
    
    protected void doCommit(final boolean b, final boolean b2, final int n, final boolean b3) throws StandardException {
        final StatementContext statementContext = this.getStatementContext();
        if (b3 && statementContext != null && statementContext.inUse() && statementContext.isAtomic()) {
            throw StandardException.newException("X0Y66.S");
        }
        if (this.logStatementText) {
            if (this.istream == null) {
                this.istream = Monitor.getStream();
            }
            this.istream.printlnWithHeader("(XID = " + this.tran.getTransactionIdString() + "), " + "(SESSIONID = " + this.instanceNumber + "), " + "(DATABASE = " + this.dbname + "), " + "(DRDAID = " + this.drdaID + "), Committing");
        }
        this.endTransactionActivationHandling(false);
        if (this.allDeclaredGlobalTempTables != null) {
            this.tempTablesAndCommit(n != 0);
        }
        this.currentSavepointLevel = 0;
        if (b2) {
            this.finishDDTransaction();
        }
        final TransactionController transactionExecute = this.getTransactionExecute();
        if (transactionExecute != null && b) {
            if (b2) {
                if (n == 0) {
                    transactionExecute.commit();
                }
                else {
                    ((XATransactionController)transactionExecute).xa_commit(n == 1);
                }
            }
            else {
                transactionExecute.commitNoSync(n);
            }
            this.resetSavepoints();
            if (this.allDeclaredGlobalTempTables != null && n != 0) {
                this.tempTablesXApostCommit();
            }
        }
    }
    
    private TableDescriptor cleanupTempTableOnCommitOrRollback(final TableDescriptor tableDescriptor, final boolean b) throws StandardException {
        final TransactionController transactionExecute = this.getTransactionExecute();
        final long conglomerate = transactionExecute.createConglomerate("heap", tableDescriptor.getEmptyExecRow().getRowArray(), null, tableDescriptor.getColumnCollationIds(), null, 3);
        final long heapConglomerateId = tableDescriptor.getHeapConglomerateId();
        tableDescriptor.getConglomerateDescriptorList().dropConglomerateDescriptorByUUID(tableDescriptor.getConglomerateDescriptor(heapConglomerateId).getUUID());
        tableDescriptor.getConglomerateDescriptorList().add(this.getDataDictionary().getDataDescriptorGenerator().newConglomerateDescriptor(conglomerate, null, false, null, false, null, tableDescriptor.getUUID(), tableDescriptor.getSchemaDescriptor().getUUID()));
        tableDescriptor.resetHeapConglomNumber();
        if (b) {
            transactionExecute.dropConglomerate(heapConglomerateId);
            this.replaceDeclaredGlobalTempTable(tableDescriptor.getName(), tableDescriptor);
        }
        return tableDescriptor;
    }
    
    public void internalRollback() throws StandardException {
        this.doRollback(false, false);
    }
    
    public void userRollback() throws StandardException {
        this.doRollback(false, true);
    }
    
    public void xaRollback() throws StandardException {
        this.doRollback(true, true);
    }
    
    private void doRollback(final boolean b, final boolean b2) throws StandardException {
        final StatementContext statementContext = this.getStatementContext();
        if (b2 && statementContext != null && statementContext.inUse() && statementContext.isAtomic()) {
            throw StandardException.newException("X0Y67.S");
        }
        if (this.logStatementText) {
            if (this.istream == null) {
                this.istream = Monitor.getStream();
            }
            this.istream.printlnWithHeader("(XID = " + this.tran.getTransactionIdString() + "), " + "(SESSIONID = " + this.instanceNumber + "), " + "(DATABASE = " + this.dbname + "), " + "(DRDAID = " + this.drdaID + "), Rolling back");
        }
        this.endTransactionActivationHandling(true);
        this.currentSavepointLevel = 0;
        if (this.allDeclaredGlobalTempTables != null) {
            this.tempTablesAndRollback();
        }
        this.finishDDTransaction();
        if (this.readOnlyNestedTransaction != null) {
            this.readOnlyNestedTransaction.destroy();
            this.readOnlyNestedTransaction = null;
            this.queryNestingDepth = 0;
        }
        final TransactionController transactionExecute = this.getTransactionExecute();
        if (transactionExecute != null) {
            if (b) {
                ((XATransactionController)transactionExecute).xa_rollback();
            }
            else {
                transactionExecute.abort();
            }
            this.resetSavepoints();
        }
    }
    
    private void resetSavepoints() throws StandardException {
        final List contextStack = this.getContextManager().getContextStack("StatementContext");
        for (int size = contextStack.size(), i = 0; i < size; ++i) {
            contextStack.get(i).resetSavePoint();
        }
    }
    
    public void internalRollbackToSavepoint(final String s, final boolean b, final Object o) throws StandardException {
        final TransactionController transactionExecute = this.getTransactionExecute();
        if (transactionExecute != null) {
            boolean b2;
            if (b) {
                b2 = true;
                this.endTransactionActivationHandling(true);
            }
            else {
                b2 = false;
            }
            this.currentSavepointLevel = transactionExecute.rollbackToSavePoint(s, b2, o);
        }
        if (transactionExecute != null && b && this.allDeclaredGlobalTempTables != null) {
            this.tempTablesAndRollback();
        }
    }
    
    public void releaseSavePoint(final String s, final Object o) throws StandardException {
        final TransactionController transactionExecute = this.getTransactionExecute();
        if (transactionExecute != null) {
            this.currentSavepointLevel = transactionExecute.releaseSavePoint(s, o);
            if (this.allDeclaredGlobalTempTables != null) {
                this.tempTablesReleaseSavepointLevels();
            }
        }
    }
    
    public void languageSetSavePoint(final String s, final Object o) throws StandardException {
        final TransactionController transactionExecute = this.getTransactionExecute();
        if (transactionExecute != null) {
            this.currentSavepointLevel = transactionExecute.setSavePoint(s, o);
        }
    }
    
    public void beginNestedTransaction(final boolean b) throws StandardException {
        if (this.readOnlyNestedTransaction == null) {
            this.readOnlyNestedTransaction = this.tran.startNestedUserTransaction(b, true);
        }
        ++this.queryNestingDepth;
    }
    
    public void commitNestedTransaction() throws StandardException {
        final int queryNestingDepth = this.queryNestingDepth - 1;
        this.queryNestingDepth = queryNestingDepth;
        if (queryNestingDepth == 0) {
            this.readOnlyNestedTransaction.commit();
            this.readOnlyNestedTransaction.destroy();
            this.readOnlyNestedTransaction = null;
        }
    }
    
    public TransactionController getTransactionCompile() {
        return (this.readOnlyNestedTransaction != null) ? this.readOnlyNestedTransaction : this.tran;
    }
    
    public TransactionController getTransactionExecute() {
        return this.tran;
    }
    
    public DataValueFactory getDataValueFactory() {
        return this.dataFactory;
    }
    
    public LanguageFactory getLanguageFactory() {
        return this.langFactory;
    }
    
    public OptimizerFactory getOptimizerFactory() {
        return this.of;
    }
    
    public LanguageConnectionFactory getLanguageConnectionFactory() {
        return this.connFactory;
    }
    
    private boolean checkIfAnyActivationHasHoldCursor(final String s) throws StandardException {
        for (int i = this.acts.size() - 1; i >= 0; --i) {
            if (((Activation)this.acts.get(i)).checkIfThisActivationHasHoldCursor(s)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean verifyAllHeldResultSetsAreClosed() throws StandardException {
        boolean b = false;
        for (int i = this.acts.size() - 1; i >= 0; --i) {
            final Activation activation = this.acts.get(i);
            if (activation.isInUse()) {
                if (activation.getResultSetHoldability()) {
                    final ResultSet resultSet = ((CursorActivation)activation).getResultSet();
                    if (resultSet != null && !resultSet.isClosed() && resultSet.returnsRows()) {
                        b = true;
                        break;
                    }
                }
            }
        }
        if (!b) {
            return true;
        }
        System.gc();
        System.runFinalization();
        for (int j = this.acts.size() - 1; j >= 0; --j) {
            final Activation activation2 = this.acts.get(j);
            if (activation2.isInUse()) {
                if (activation2.getResultSetHoldability()) {
                    final ResultSet resultSet2 = ((CursorActivation)activation2).getResultSet();
                    if (resultSet2 != null && !resultSet2.isClosed() && resultSet2.returnsRows()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public boolean verifyNoOpenResultSets(final PreparedStatement preparedStatement, final Provider provider, final int n) throws StandardException {
        boolean b = false;
        for (int i = this.acts.size() - 1; i >= 0; --i) {
            final Activation activation = this.acts.get(i);
            if (activation.isInUse()) {
                if (preparedStatement == activation.getPreparedStatement()) {
                    final ResultSet resultSet = activation.getResultSet();
                    if (resultSet != null && !resultSet.isClosed()) {
                        if (resultSet.returnsRows()) {
                            b = true;
                            break;
                        }
                    }
                }
            }
        }
        if (!b) {
            return false;
        }
        System.gc();
        System.runFinalization();
        for (int j = this.acts.size() - 1; j >= 0; --j) {
            final Activation activation2 = this.acts.get(j);
            if (activation2.isInUse()) {
                if (preparedStatement == activation2.getPreparedStatement()) {
                    final ResultSet resultSet2 = activation2.getResultSet();
                    if (resultSet2 != null && !resultSet2.isClosed()) {
                        if (provider != null && resultSet2.returnsRows()) {
                            throw StandardException.newException("X0X95.S", this.getDataDictionary().getDependencyManager().getActionString(n), provider.getObjectName());
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public String getSessionUserId() {
        return this.sessionUser;
    }
    
    public SchemaDescriptor getDefaultSchema() {
        return this.getCurrentSQLSessionContext().getDefaultSchema();
    }
    
    public SchemaDescriptor getDefaultSchema(final Activation activation) {
        return this.getCurrentSQLSessionContext(activation).getDefaultSchema();
    }
    
    public String getCurrentSchemaName() {
        final SchemaDescriptor defaultSchema = this.getDefaultSchema();
        if (null == defaultSchema) {
            return null;
        }
        return defaultSchema.getSchemaName();
    }
    
    public String getCurrentSchemaName(final Activation activation) {
        final SchemaDescriptor defaultSchema = this.getDefaultSchema(activation);
        if (null == defaultSchema) {
            return null;
        }
        return defaultSchema.getSchemaName();
    }
    
    public boolean isInitialDefaultSchema(final String anObject) {
        return this.cachedInitialDefaultSchemaDescr.getSchemaName().equals(anObject);
    }
    
    public void setDefaultSchema(SchemaDescriptor initialDefaultSchemaDescriptor) throws StandardException {
        if (initialDefaultSchemaDescriptor == null) {
            initialDefaultSchemaDescriptor = this.getInitialDefaultSchemaDescriptor();
        }
        this.getCurrentSQLSessionContext().setDefaultSchema(initialDefaultSchemaDescriptor);
    }
    
    public void setDefaultSchema(final Activation activation, SchemaDescriptor initialDefaultSchemaDescriptor) throws StandardException {
        if (initialDefaultSchemaDescriptor == null) {
            initialDefaultSchemaDescriptor = this.getInitialDefaultSchemaDescriptor();
        }
        this.getCurrentSQLSessionContext(activation).setDefaultSchema(initialDefaultSchemaDescriptor);
    }
    
    public void resetSchemaUsages(final Activation activation, final String s) throws StandardException {
        Activation activation2 = activation.getParentActivation();
        final SchemaDescriptor initialDefaultSchemaDescriptor = this.getInitialDefaultSchemaDescriptor();
        while (activation2 != null) {
            final SQLSessionContext sqlSessionContextForChildren = activation2.getSQLSessionContextForChildren();
            if (s.equals(sqlSessionContextForChildren.getDefaultSchema().getSchemaName())) {
                sqlSessionContextForChildren.setDefaultSchema(initialDefaultSchemaDescriptor);
            }
            activation2 = activation2.getParentActivation();
        }
        final SQLSessionContext topLevelSQLSessionContext = this.getTopLevelSQLSessionContext();
        if (s.equals(topLevelSQLSessionContext.getDefaultSchema().getSchemaName())) {
            topLevelSQLSessionContext.setDefaultSchema(initialDefaultSchemaDescriptor);
        }
    }
    
    public Long getIdentityValue() {
        return this.identityNotNull ? new Long(this.identityVal) : null;
    }
    
    public void setIdentityValue(final long identityVal) {
        this.identityVal = identityVal;
        this.identityNotNull = true;
    }
    
    public final CompilerContext pushCompilerContext() {
        return this.pushCompilerContext(null);
    }
    
    public CompilerContext pushCompilerContext(final SchemaDescriptor compilationSchema) {
        boolean b = false;
        CompilerContext compilerContext = (CompilerContext)this.getContextManager().getContext("CompilerContext");
        if (compilerContext == null) {
            b = true;
        }
        if (compilerContext == null || compilerContext.getInUse()) {
            compilerContext = new CompilerContextImpl(this.getContextManager(), this, this.tcf);
            if (b) {
                compilerContext.firstOnStack();
            }
        }
        else {
            compilerContext.resetContext();
        }
        compilerContext.setInUse(true);
        if (this.getStatementContext().getSystemCode()) {
            compilerContext.setReliability(0);
        }
        if (compilationSchema != null && compilationSchema.getUUID() != null) {
            compilerContext.setCompilationSchema(compilationSchema);
        }
        return compilerContext;
    }
    
    public void popCompilerContext(final CompilerContext compilerContext) {
        compilerContext.setCurrentDependent(null);
        compilerContext.setInUse(false);
        if (!compilerContext.isFirstOnStack()) {
            compilerContext.popMe();
        }
        else {
            compilerContext.setCompilationSchema(null);
        }
    }
    
    public StatementContext pushStatementContext(final boolean b, final boolean b2, final String s, final ParameterValueSet set, final boolean b3, final long n) {
        final int statementDepth = this.statementDepth;
        boolean b4 = false;
        boolean atomic = false;
        StatementContext statementContext = this.statementContexts[0];
        if (statementContext == null) {
            final StatementContext[] statementContexts = this.statementContexts;
            final int n2 = 0;
            final GenericStatementContext genericStatementContext = new GenericStatementContext(this);
            statementContexts[n2] = genericStatementContext;
            statementContext = genericStatementContext;
            statementContext.setSQLSessionContext(this.getTopLevelSQLSessionContext());
        }
        else if (this.statementDepth > 0) {
            StatementContext statementContext2;
            if (this.statementDepth == 1) {
                statementContext = this.statementContexts[1];
                if (statementContext == null) {
                    final StatementContext[] statementContexts2 = this.statementContexts;
                    final int n3 = 1;
                    final GenericStatementContext genericStatementContext2 = new GenericStatementContext(this);
                    statementContexts2[n3] = genericStatementContext2;
                    statementContext = genericStatementContext2;
                }
                else {
                    statementContext.pushMe();
                }
                statementContext2 = this.statementContexts[0];
            }
            else {
                statementContext2 = this.getStatementContext();
                statementContext = new GenericStatementContext(this);
            }
            statementContext.setSQLSessionContext(statementContext2.getSQLSessionContext());
            b4 = (statementContext2.inTrigger() || this.outermostTrigger == statementDepth);
            atomic = statementContext2.isAtomic();
            statementContext.setSQLAllowed(statementContext2.getSQLAllowed(), false);
            if (statementContext2.getSystemCode()) {
                statementContext.setSystemCode();
            }
        }
        else {
            statementContext.setSQLSessionContext(this.getTopLevelSQLSessionContext());
        }
        this.incrementStatementDepth();
        statementContext.setInUse(b4, b || atomic, b2, s, set, n);
        if (b3) {
            statementContext.setParentRollback();
        }
        return statementContext;
    }
    
    public void popStatementContext(final StatementContext statementContext, final Throwable t) {
        if (statementContext != null) {
            if (!statementContext.inUse()) {
                return;
            }
            statementContext.clearInUse();
        }
        this.decrementStatementDepth();
        if (this.statementDepth == -1) {
            this.resetStatementDepth();
        }
        else if (this.statementDepth != 0) {
            statementContext.popMe();
        }
    }
    
    public void pushExecutionStmtValidator(final ExecutionStmtValidator e) {
        this.stmtValidators.add(e);
    }
    
    public void popExecutionStmtValidator(final ExecutionStmtValidator o) throws StandardException {
        this.stmtValidators.remove(o);
    }
    
    public void pushTriggerExecutionContext(final TriggerExecutionContext e) throws StandardException {
        if (this.outermostTrigger == -1) {
            this.outermostTrigger = this.statementDepth;
        }
        if (this.triggerExecutionContexts.size() >= 16) {
            throw StandardException.newException("54038");
        }
        this.triggerExecutionContexts.add(e);
    }
    
    public void popTriggerExecutionContext(final TriggerExecutionContext o) throws StandardException {
        if (this.outermostTrigger == this.statementDepth) {
            this.outermostTrigger = -1;
        }
        this.triggerExecutionContexts.remove(o);
    }
    
    public TriggerExecutionContext getTriggerExecutionContext() {
        return (this.triggerExecutionContexts.size() == 0) ? null : this.triggerExecutionContexts.get(this.triggerExecutionContexts.size() - 1);
    }
    
    public void validateStmtExecution(final ConstantAction constantAction) throws StandardException {
        if (this.stmtValidators.size() > 0) {
            final Iterator<ExecutionStmtValidator> iterator = this.stmtValidators.iterator();
            while (iterator.hasNext()) {
                iterator.next().validateStatement(constantAction);
            }
        }
    }
    
    public void pushTriggerTable(final TableDescriptor e) {
        this.triggerTables.add(e);
    }
    
    public void popTriggerTable(final TableDescriptor o) {
        this.triggerTables.remove(o);
    }
    
    public TableDescriptor getTriggerTable() {
        return (this.triggerTables.size() == 0) ? null : this.triggerTables.get(this.triggerTables.size() - 1);
    }
    
    public Database getDatabase() {
        return this.db;
    }
    
    public int incrementBindCount() {
        return ++this.bindCount;
    }
    
    public int decrementBindCount() {
        return --this.bindCount;
    }
    
    public int getBindCount() {
        return this.bindCount;
    }
    
    public final void setDataDictionaryWriteMode() {
        this.ddWriteMode = true;
    }
    
    public final boolean dataDictionaryInWriteMode() {
        return this.ddWriteMode;
    }
    
    public void setRunTimeStatisticsMode(final boolean runTimeStatisticsSetting) {
        this.runTimeStatisticsSetting = runTimeStatisticsSetting;
    }
    
    public boolean getRunTimeStatisticsMode() {
        return this.runTimeStatisticsSetting;
    }
    
    public void setStatisticsTiming(final boolean statisticsTiming) {
        this.statisticsTiming = statisticsTiming;
    }
    
    public boolean getStatisticsTiming() {
        return this.statisticsTiming;
    }
    
    public void setRunTimeStatisticsObject(final RunTimeStatistics runTimeStatisticsObject) {
        this.runTimeStatisticsObject = runTimeStatisticsObject;
    }
    
    public RunTimeStatistics getRunTimeStatisticsObject() {
        return this.runTimeStatisticsObject;
    }
    
    public int getStatementDepth() {
        return this.statementDepth;
    }
    
    public boolean isIsolationLevelSetUsingSQLorJDBC() {
        return this.isolationLevelSetUsingSQLorJDBC;
    }
    
    public void resetIsolationLevelFlagUsedForSQLandJDBC() {
        this.isolationLevelSetUsingSQLorJDBC = false;
    }
    
    public void setIsolationLevel(final int isolationLevel) throws StandardException {
        final StatementContext statementContext = this.getStatementContext();
        if (statementContext != null && statementContext.inTrigger()) {
            throw StandardException.newException("X0Y71.S", this.getTriggerExecutionContext().toString());
        }
        if (this.isolationLevel != isolationLevel && !this.verifyAllHeldResultSetsAreClosed()) {
            throw StandardException.newException("X0X03.S");
        }
        final TransactionController transactionExecute = this.getTransactionExecute();
        if (!transactionExecute.isIdle()) {
            if (transactionExecute.isGlobal()) {
                throw StandardException.newException("X0Y77.S");
            }
            this.userCommit();
        }
        this.isolationLevel = isolationLevel;
        this.isolationLevelExplicitlySet = true;
        this.isolationLevelSetUsingSQLorJDBC = true;
    }
    
    public int getCurrentIsolationLevel() {
        return (this.isolationLevel == 0) ? this.defaultIsolationLevel : this.isolationLevel;
    }
    
    public String getCurrentIsolationLevelStr() {
        if (this.isolationLevel >= 0 && this.isolationLevel < ExecutionContext.CS_TO_SQL_ISOLATION_MAP.length) {
            return ExecutionContext.CS_TO_SQL_ISOLATION_MAP[this.isolationLevel][0];
        }
        return ExecutionContext.CS_TO_SQL_ISOLATION_MAP[0][0];
    }
    
    public void setPrepareIsolationLevel(final int prepareIsolationLevel) {
        this.prepareIsolationLevel = prepareIsolationLevel;
    }
    
    public int getPrepareIsolationLevel() {
        if (!this.isolationLevelExplicitlySet) {
            return this.prepareIsolationLevel;
        }
        return 0;
    }
    
    public StatementContext getStatementContext() {
        return (StatementContext)this.getContextManager().getContext("StatementContext");
    }
    
    public boolean setOptimizerTrace(final boolean optimizerTrace) {
        if (this.of == null) {
            return false;
        }
        if (!this.of.supportsOptimizerTrace()) {
            return false;
        }
        this.optimizerTrace = optimizerTrace;
        return true;
    }
    
    public boolean getOptimizerTrace() {
        return this.optimizerTrace;
    }
    
    public boolean setOptimizerTraceHtml(final boolean optimizerTraceHtml) {
        if (this.of == null) {
            return false;
        }
        if (!this.of.supportsOptimizerTrace()) {
            return false;
        }
        this.optimizerTraceHtml = optimizerTraceHtml;
        return true;
    }
    
    public boolean getOptimizerTraceHtml() {
        return this.optimizerTraceHtml;
    }
    
    public void setOptimizerTraceOutput(final String optimizerTraceOutput) {
        if (this.optimizerTrace) {
            this.lastOptimizerTraceOutput = this.optimizerTraceOutput;
            this.optimizerTraceOutput = optimizerTraceOutput;
        }
    }
    
    public void appendOptimizerTraceOutput(final String str) {
        this.optimizerTraceOutput = ((this.optimizerTraceOutput == null) ? str : (this.optimizerTraceOutput + str));
    }
    
    public String getOptimizerTraceOutput() {
        return this.lastOptimizerTraceOutput;
    }
    
    public boolean isTransactionPristine() {
        return this.getTransactionExecute().isPristine();
    }
    
    public void cleanupOnError(final Throwable t) throws StandardException {
        final int n = (t instanceof StandardException) ? ((StandardException)t).getSeverity() : 40000;
        if (this.statementContexts[0] != null) {
            this.statementContexts[0].clearInUse();
            if (n >= 40000) {
                this.statementContexts[0].popMe();
            }
        }
        if (this.statementContexts[1] != null) {
            this.statementContexts[1].clearInUse();
        }
        if (n >= 40000) {
            for (int i = this.acts.size() - 1; i >= 0; --i) {
                if (i < this.acts.size()) {
                    final Activation activation = this.acts.get(i);
                    activation.reset();
                    activation.close();
                }
            }
            this.popMe();
            InterruptStatus.saveInfoFromLcc(this);
        }
        else if (n >= 30000) {
            this.internalRollback();
        }
    }
    
    public boolean isLastHandler(final int n) {
        return false;
    }
    
    private void endTransactionActivationHandling(final boolean b) throws StandardException {
        for (int i = this.acts.size() - 1; i >= 0; --i) {
            if (i < this.acts.size()) {
                final Activation activation = this.acts.get(i);
                if (!activation.isInUse()) {
                    activation.close();
                }
                else {
                    final ResultSet resultSet = activation.getResultSet();
                    final boolean b2 = resultSet != null && resultSet.returnsRows();
                    if (b) {
                        if (b2) {
                            activation.reset();
                        }
                        if (this.dataDictionaryInWriteMode()) {
                            final ExecPreparedStatement preparedStatement = activation.getPreparedStatement();
                            if (preparedStatement != null) {
                                preparedStatement.makeInvalid(4, this);
                            }
                        }
                    }
                    else {
                        if (b2) {
                            if (!activation.getResultSetHoldability()) {
                                resultSet.close();
                            }
                            else {
                                resultSet.clearCurrentRow();
                            }
                        }
                        activation.clearHeapConglomerateController();
                    }
                }
            }
        }
    }
    
    private void finishDDTransaction() throws StandardException {
        if (this.ddWriteMode) {
            this.getDataDictionary().transactionFinished();
            this.ddWriteMode = false;
        }
    }
    
    private void incrementStatementDepth() {
        ++this.statementDepth;
    }
    
    private void decrementStatementDepth() {
        --this.statementDepth;
    }
    
    protected void resetStatementDepth() {
        this.statementDepth = 0;
    }
    
    public DataDictionary getDataDictionary() {
        return this.getDatabase().getDataDictionary();
    }
    
    public void setReadOnly(final boolean b) throws StandardException {
        if (!this.tran.isPristine()) {
            throw StandardException.newException("25501");
        }
        this.authorizer.setReadOnlyConnection(b, true);
    }
    
    public boolean isReadOnly() {
        return this.authorizer.isReadOnlyConnection();
    }
    
    public Authorizer getAuthorizer() {
        return this.authorizer;
    }
    
    public Long lastAutoincrementValue(final String s, final String s2, final String s3) {
        final String identity = AutoincrementCounter.makeIdentity(s, s2, s3);
        for (int i = this.triggerExecutionContexts.size() - 1; i >= 0; --i) {
            final Long autoincrementValue = this.triggerExecutionContexts.get(i).getAutoincrementValue(identity);
            if (autoincrementValue != null) {
                return autoincrementValue;
            }
        }
        if (this.autoincrementHT == null) {
            return null;
        }
        return (Long)this.autoincrementHT.get(identity);
    }
    
    public void setAutoincrementUpdate(final boolean autoincrementUpdate) {
        this.autoincrementUpdate = autoincrementUpdate;
    }
    
    public boolean getAutoincrementUpdate() {
        return this.autoincrementUpdate;
    }
    
    public void autoincrementCreateCounter(final String s, final String s2, final String s3, final Long n, final long n2, final int n3) {
        final String identity = AutoincrementCounter.makeIdentity(s, s2, s3);
        if (this.autoincrementCacheHashtable == null) {
            this.autoincrementCacheHashtable = new HashMap();
        }
        if (this.autoincrementCacheHashtable.get(identity) != null) {
            return;
        }
        this.autoincrementCacheHashtable.put(identity, new AutoincrementCounter(n, n2, 0L, s, s2, s3, n3));
    }
    
    public long nextAutoincrementValue(final String s, final String s2, final String s3) throws StandardException {
        final AutoincrementCounter autoincrementCounter = this.autoincrementCacheHashtable.get(AutoincrementCounter.makeIdentity(s, s2, s3));
        if (autoincrementCounter == null) {
            return 0L;
        }
        return autoincrementCounter.update();
    }
    
    public void autoincrementFlushCache(final UUID uuid) throws StandardException {
        if (this.autoincrementCacheHashtable == null) {
            return;
        }
        if (this.autoincrementHT == null) {
            this.autoincrementHT = new HashMap();
        }
        final DataDictionary dataDictionary = this.getDataDictionary();
        for (final Object next : this.autoincrementCacheHashtable.keySet()) {
            final AutoincrementCounter autoincrementCounter = this.autoincrementCacheHashtable.get(next);
            final Long currentValue = autoincrementCounter.getCurrentValue();
            autoincrementCounter.flushToDisk(this.getTransactionExecute(), dataDictionary, uuid);
            if (currentValue != null) {
                this.autoincrementHT.put(next, currentValue);
            }
        }
        this.autoincrementCacheHashtable.clear();
    }
    
    public void copyHashtableToAIHT(final Map m) {
        if (m.isEmpty()) {
            return;
        }
        if (this.autoincrementHT == null) {
            this.autoincrementHT = new HashMap();
        }
        this.autoincrementHT.putAll(m);
    }
    
    public int getInstanceNumber() {
        return this.instanceNumber;
    }
    
    public String getDrdaID() {
        return this.drdaID;
    }
    
    public void setDrdaID(final String drdaID) {
        this.drdaID = drdaID;
    }
    
    public String getDbname() {
        return this.dbname;
    }
    
    public Activation getLastActivation() {
        return this.acts.get(this.acts.size() - 1);
    }
    
    public StringBuffer appendErrorInfo() {
        final TransactionController transactionExecute = this.getTransactionExecute();
        if (transactionExecute == null) {
            return null;
        }
        final StringBuffer sb = new StringBuffer(200);
        sb.append("(XID = ");
        sb.append(transactionExecute.getTransactionIdString());
        sb.append("), ");
        sb.append("(SESSIONID = ");
        sb.append(Integer.toString(this.getInstanceNumber()));
        sb.append("), ");
        sb.append("(DATABASE = ");
        sb.append(this.getDbname());
        sb.append("), ");
        sb.append("(DRDAID = ");
        sb.append(this.getDrdaID());
        sb.append("), ");
        return sb;
    }
    
    public void setCurrentRole(final Activation activation, final String role) {
        this.getCurrentSQLSessionContext(activation).setRole(role);
    }
    
    public String getCurrentRoleId(final Activation activation) {
        return this.getCurrentSQLSessionContext(activation).getRole();
    }
    
    public String getCurrentUserId(final Activation activation) {
        return this.getCurrentSQLSessionContext(activation).getCurrentUser();
    }
    
    public String getCurrentRoleIdDelimited(final Activation activation) throws StandardException {
        String s = this.getCurrentSQLSessionContext(activation).getRole();
        if (s != null) {
            this.beginNestedTransaction(true);
            try {
                if (!this.roleIsSettable(activation, s)) {
                    this.setCurrentRole(activation, null);
                    s = null;
                }
            }
            finally {
                this.commitNestedTransaction();
            }
        }
        if (s != null) {
            s = IdUtil.normalToDelimited(s);
        }
        return s;
    }
    
    public boolean roleIsSettable(final Activation activation, final String s) throws StandardException {
        final DataDictionary dataDictionary = this.getDataDictionary();
        final String authorizationDatabaseOwner = dataDictionary.getAuthorizationDatabaseOwner();
        final String currentUserId = this.getCurrentUserId(activation);
        RoleGrantDescriptor roleGrantDescriptor;
        if (currentUserId.equals(authorizationDatabaseOwner)) {
            roleGrantDescriptor = dataDictionary.getRoleDefinitionDescriptor(s);
        }
        else {
            roleGrantDescriptor = dataDictionary.getRoleGrantDescriptor(s, currentUserId, authorizationDatabaseOwner);
            if (roleGrantDescriptor == null) {
                roleGrantDescriptor = dataDictionary.getRoleGrantDescriptor(s, "PUBLIC", authorizationDatabaseOwner);
            }
        }
        return roleGrantDescriptor != null;
    }
    
    private SQLSessionContext getCurrentSQLSessionContext(final Activation activation) {
        final Activation parentActivation = activation.getParentActivation();
        SQLSessionContext sqlSessionContext;
        if (parentActivation == null) {
            sqlSessionContext = this.getTopLevelSQLSessionContext();
        }
        else {
            sqlSessionContext = parentActivation.getSQLSessionContextForChildren();
        }
        return sqlSessionContext;
    }
    
    private SQLSessionContext getCurrentSQLSessionContext() {
        final StatementContext statementContext = this.getStatementContext();
        SQLSessionContext sqlSessionContext;
        if (statementContext == null || !statementContext.inUse()) {
            sqlSessionContext = this.getTopLevelSQLSessionContext();
        }
        else {
            sqlSessionContext = statementContext.getSQLSessionContext();
        }
        return sqlSessionContext;
    }
    
    public void setupNestedSessionContext(final Activation activation, final boolean b, final String s) throws StandardException {
        this.setupSessionContextMinion(activation, true, b, s);
    }
    
    private void setupSessionContextMinion(final Activation activation, final boolean b, final boolean b2, final String user) throws StandardException {
        final SQLSessionContext setupSQLSessionContextForChildren = activation.setupSQLSessionContextForChildren(b);
        if (b2) {
            setupSQLSessionContextForChildren.setUser(user);
        }
        else {
            setupSQLSessionContextForChildren.setUser(this.getCurrentUserId(activation));
        }
        if (b2) {
            setupSQLSessionContextForChildren.setRole(null);
        }
        else {
            setupSQLSessionContextForChildren.setRole(this.getCurrentRoleId(activation));
        }
        if (b2) {
            SchemaDescriptor schemaDescriptor = this.getDataDictionary().getSchemaDescriptor(user, this.getTransactionExecute(), false);
            if (schemaDescriptor == null) {
                schemaDescriptor = new SchemaDescriptor(this.getDataDictionary(), user, user, null, false);
            }
            setupSQLSessionContextForChildren.setDefaultSchema(schemaDescriptor);
        }
        else {
            setupSQLSessionContextForChildren.setDefaultSchema(this.getDefaultSchema(activation));
        }
        this.getStatementContext().setSQLSessionContext(setupSQLSessionContextForChildren);
    }
    
    public void setupSubStatementSessionContext(final Activation activation) throws StandardException {
        this.setupSessionContextMinion(activation, false, false, null);
    }
    
    public SQLSessionContext getTopLevelSQLSessionContext() {
        if (this.topLevelSSC == null) {
            this.topLevelSSC = new SQLSessionContextImpl(this.getInitialDefaultSchemaDescriptor(), this.getSessionUserId());
        }
        return this.topLevelSSC;
    }
    
    public SQLSessionContext createSQLSessionContext() {
        return new SQLSessionContextImpl(this.getInitialDefaultSchemaDescriptor(), this.getSessionUserId());
    }
    
    public Map getPrintedObjectsMap() {
        if (this.printedObjectsMap == null) {
            this.printedObjectsMap = new IdentityHashMap();
        }
        return this.printedObjectsMap;
    }
    
    public boolean getXplainOnlyMode() {
        return this.xplainOnlyMode;
    }
    
    public void setXplainOnlyMode(final boolean xplainOnlyMode) {
        this.xplainOnlyMode = xplainOnlyMode;
    }
    
    public String getXplainSchema() {
        return this.xplain_schema;
    }
    
    public void setXplainSchema(final String xplain_schema) {
        this.xplain_schema = xplain_schema;
    }
    
    public void setXplainStatement(final Object o, final Object o2) {
        this.xplain_statements.put(o, o2);
    }
    
    public Object getXplainStatement(final Object o) {
        return this.xplain_statements.get(o);
    }
    
    public void setASTVisitor(final ASTVisitor astWalker) {
        this.astWalker = astWalker;
    }
    
    public ASTVisitor getASTVisitor() {
        return this.astWalker;
    }
    
    public void setInterruptedException(final StandardException interruptedException) {
        this.interruptedException = interruptedException;
    }
    
    public StandardException getInterruptedException() {
        return this.interruptedException;
    }
    
    public FormatableBitSet getReferencedColumnMap(final TableDescriptor key) {
        return this.referencedColumnMap.get(key);
    }
    
    public void setReferencedColumnMap(final TableDescriptor key, final FormatableBitSet value) {
        this.referencedColumnMap.put(key, value);
    }
}
