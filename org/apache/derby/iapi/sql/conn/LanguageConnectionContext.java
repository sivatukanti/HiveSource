// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.conn;

import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.compile.ASTVisitor;
import org.apache.derby.catalog.UUID;
import java.util.Map;
import org.apache.derby.iapi.db.Database;
import org.apache.derby.iapi.sql.execute.RunTimeStatistics;
import org.apache.derby.iapi.db.TriggerExecutionContext;
import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.sql.execute.ExecutionStmtValidator;
import org.apache.derby.iapi.sql.ParameterValueSet;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.PreparedStatement;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.compile.OptimizerFactory;
import org.apache.derby.iapi.sql.LanguageFactory;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.execute.CursorActivation;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.Context;

public interface LanguageConnectionContext extends Context
{
    public static final String CONTEXT_ID = "LanguageConnectionContext";
    public static final int OUTERMOST_STATEMENT = 1;
    public static final int SQL92_SCHEMAS = 0;
    public static final int USER_NAME_SCHEMA = 1;
    public static final int NO_SCHEMAS = 2;
    public static final String xidStr = "(XID = ";
    public static final String lccStr = "(SESSIONID = ";
    public static final String dbnameStr = "(DATABASE = ";
    public static final String drdaStr = "(DRDAID = ";
    public static final int SINGLE_TRANSACTION_LOCK = 1;
    public static final int MULTI_TRANSACTION_LOCK = 2;
    public static final int UNKNOWN_CASING = -1;
    public static final int ANSI_CASING = 0;
    public static final int ANTI_ANSI_CASING = 1;
    
    void initialize() throws StandardException;
    
    boolean getLogStatementText();
    
    void setLogStatementText(final boolean p0);
    
    boolean getLogQueryPlan();
    
    int getLockEscalationThreshold();
    
    void addActivation(final Activation p0) throws StandardException;
    
    void notifyUnusedActivation();
    
    void removeActivation(final Activation p0) throws StandardException;
    
    int getActivationCount();
    
    CursorActivation lookupCursorActivation(final String p0);
    
    Activation getLastActivation();
    
    String getUniqueCursorName();
    
    String getUniqueSavepointName();
    
    int getUniqueSavepointID();
    
    boolean checkIfAnyDeclaredGlobalTempTablesForThisConnection();
    
    void markTempTableAsModifiedInUnitOfWork(final String p0);
    
    void addDeclaredGlobalTempTable(final TableDescriptor p0) throws StandardException;
    
    boolean dropDeclaredGlobalTempTable(final String p0);
    
    TableDescriptor getTableDescriptorForDeclaredGlobalTempTable(final String p0);
    
    void resetFromPool() throws StandardException;
    
    void internalCommit(final boolean p0) throws StandardException;
    
    void internalCommitNoSync(final int p0) throws StandardException;
    
    void userCommit() throws StandardException;
    
    void xaCommit(final boolean p0) throws StandardException;
    
    void internalRollback() throws StandardException;
    
    void userRollback() throws StandardException;
    
    void internalRollbackToSavepoint(final String p0, final boolean p1, final Object p2) throws StandardException;
    
    void releaseSavePoint(final String p0, final Object p1) throws StandardException;
    
    void xaRollback() throws StandardException;
    
    void languageSetSavePoint(final String p0, final Object p1) throws StandardException;
    
    void beginNestedTransaction(final boolean p0) throws StandardException;
    
    void commitNestedTransaction() throws StandardException;
    
    TransactionController getTransactionCompile();
    
    TransactionController getTransactionExecute();
    
    DataDictionary getDataDictionary();
    
    DataValueFactory getDataValueFactory();
    
    LanguageFactory getLanguageFactory();
    
    OptimizerFactory getOptimizerFactory();
    
    LanguageConnectionFactory getLanguageConnectionFactory();
    
    String getCurrentUserId(final Activation p0);
    
    String getSessionUserId();
    
    SchemaDescriptor getDefaultSchema();
    
    SchemaDescriptor getDefaultSchema(final Activation p0);
    
    void setDefaultSchema(final SchemaDescriptor p0) throws StandardException;
    
    void setDefaultSchema(final Activation p0, final SchemaDescriptor p1) throws StandardException;
    
    void resetSchemaUsages(final Activation p0, final String p1) throws StandardException;
    
    String getCurrentSchemaName();
    
    String getCurrentSchemaName(final Activation p0);
    
    boolean isInitialDefaultSchema(final String p0);
    
    Long getIdentityValue();
    
    void setIdentityValue(final long p0);
    
    boolean verifyNoOpenResultSets(final PreparedStatement p0, final Provider p1, final int p2) throws StandardException;
    
    boolean verifyAllHeldResultSetsAreClosed() throws StandardException;
    
    CompilerContext pushCompilerContext();
    
    CompilerContext pushCompilerContext(final SchemaDescriptor p0);
    
    void popCompilerContext(final CompilerContext p0);
    
    StatementContext pushStatementContext(final boolean p0, final boolean p1, final String p2, final ParameterValueSet p3, final boolean p4, final long p5);
    
    void popStatementContext(final StatementContext p0, final Throwable p1);
    
    void pushExecutionStmtValidator(final ExecutionStmtValidator p0);
    
    void popExecutionStmtValidator(final ExecutionStmtValidator p0) throws StandardException;
    
    void validateStmtExecution(final ConstantAction p0) throws StandardException;
    
    void pushTriggerExecutionContext(final TriggerExecutionContext p0) throws StandardException;
    
    void popTriggerExecutionContext(final TriggerExecutionContext p0) throws StandardException;
    
    TriggerExecutionContext getTriggerExecutionContext();
    
    void pushTriggerTable(final TableDescriptor p0);
    
    void popTriggerTable(final TableDescriptor p0);
    
    TableDescriptor getTriggerTable();
    
    int incrementBindCount();
    
    int decrementBindCount();
    
    int getBindCount();
    
    void setDataDictionaryWriteMode();
    
    boolean dataDictionaryInWriteMode();
    
    void setRunTimeStatisticsMode(final boolean p0);
    
    boolean getRunTimeStatisticsMode();
    
    void setStatisticsTiming(final boolean p0);
    
    boolean getStatisticsTiming();
    
    void setRunTimeStatisticsObject(final RunTimeStatistics p0);
    
    RunTimeStatistics getRunTimeStatisticsObject();
    
    int getStatementDepth();
    
    Database getDatabase();
    
    boolean isIsolationLevelSetUsingSQLorJDBC();
    
    void resetIsolationLevelFlagUsedForSQLandJDBC();
    
    void setIsolationLevel(final int p0) throws StandardException;
    
    int getCurrentIsolationLevel();
    
    String getCurrentIsolationLevelStr();
    
    void setPrepareIsolationLevel(final int p0);
    
    int getPrepareIsolationLevel();
    
    void setReadOnly(final boolean p0) throws StandardException;
    
    boolean isReadOnly();
    
    Authorizer getAuthorizer();
    
    StatementContext getStatementContext();
    
    PreparedStatement prepareInternalStatement(final SchemaDescriptor p0, final String p1, final boolean p2, final boolean p3) throws StandardException;
    
    PreparedStatement prepareInternalStatement(final String p0) throws StandardException;
    
    boolean setOptimizerTrace(final boolean p0);
    
    boolean getOptimizerTrace();
    
    boolean setOptimizerTraceHtml(final boolean p0);
    
    boolean getOptimizerTraceHtml();
    
    String getOptimizerTraceOutput();
    
    void setOptimizerTraceOutput(final String p0);
    
    void appendOptimizerTraceOutput(final String p0);
    
    boolean isTransactionPristine();
    
    Long lastAutoincrementValue(final String p0, final String p1, final String p2);
    
    void setAutoincrementUpdate(final boolean p0);
    
    boolean getAutoincrementUpdate();
    
    void copyHashtableToAIHT(final Map p0);
    
    long nextAutoincrementValue(final String p0, final String p1, final String p2) throws StandardException;
    
    void autoincrementFlushCache(final UUID p0) throws StandardException;
    
    void autoincrementCreateCounter(final String p0, final String p1, final String p2, final Long p3, final long p4, final int p5);
    
    int getInstanceNumber();
    
    String getDrdaID();
    
    void setDrdaID(final String p0);
    
    String getDbname();
    
    boolean usesSqlAuthorization();
    
    void closeUnusedActivations() throws StandardException;
    
    void setCurrentRole(final Activation p0, final String p1);
    
    String getCurrentRoleId(final Activation p0);
    
    String getCurrentRoleIdDelimited(final Activation p0) throws StandardException;
    
    boolean roleIsSettable(final Activation p0, final String p1) throws StandardException;
    
    void setupNestedSessionContext(final Activation p0, final boolean p1, final String p2) throws StandardException;
    
    SQLSessionContext getTopLevelSQLSessionContext();
    
    void setupSubStatementSessionContext(final Activation p0) throws StandardException;
    
    SQLSessionContext createSQLSessionContext();
    
    void setLastQueryTree(final Object p0);
    
    Object getLastQueryTree();
    
    Map getPrintedObjectsMap();
    
    void setXplainOnlyMode(final boolean p0);
    
    boolean getXplainOnlyMode();
    
    void setXplainSchema(final String p0);
    
    String getXplainSchema();
    
    void setXplainStatement(final Object p0, final Object p1);
    
    Object getXplainStatement(final Object p0);
    
    void setASTVisitor(final ASTVisitor p0);
    
    ASTVisitor getASTVisitor();
    
    void setInterruptedException(final StandardException p0);
    
    StandardException getInterruptedException();
    
    FormatableBitSet getReferencedColumnMap(final TableDescriptor p0);
    
    void setReferencedColumnMap(final TableDescriptor p0, final FormatableBitSet p1);
}
