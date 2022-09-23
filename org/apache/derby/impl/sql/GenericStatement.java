// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql;

import org.apache.derby.iapi.sql.compile.ASTVisitor;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.services.daemon.IndexStatisticsDaemon;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.compile.Parser;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.services.stream.HeaderPrintWriter;
import org.apache.derby.iapi.sql.conn.StatementContext;
import org.apache.derby.iapi.sql.compile.Visitable;
import org.apache.derby.impl.sql.compile.StatementNode;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.iapi.sql.ParameterValueSet;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.util.InterruptStatus;
import org.apache.derby.iapi.services.loader.GeneratedClass;
import org.apache.derby.impl.sql.conn.GenericLanguageConnectionContext;
import java.sql.Timestamp;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.PreparedStatement;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.Statement;

public class GenericStatement implements Statement
{
    private final SchemaDescriptor compilationSchema;
    private final String statementText;
    private final boolean isForReadOnly;
    private int prepareIsolationLevel;
    private GenericPreparedStatement preparedStmt;
    
    public GenericStatement(final SchemaDescriptor compilationSchema, final String statementText, final boolean isForReadOnly) {
        this.compilationSchema = compilationSchema;
        this.statementText = statementText;
        this.isForReadOnly = isForReadOnly;
    }
    
    public PreparedStatement prepare(final LanguageConnectionContext languageConnectionContext) throws StandardException {
        return this.prepare(languageConnectionContext, false);
    }
    
    public PreparedStatement prepare(final LanguageConnectionContext languageConnectionContext, final boolean b) throws StandardException {
        final int statementDepth = languageConnectionContext.getStatementDepth();
        Object anObject = null;
        while (true) {
            int n = 0;
            try {
                return this.prepMinion(languageConnectionContext, true, null, null, b);
            }
            catch (StandardException ex) {
                if ("XSAI2.S".equals(ex.getMessageId())) {
                    final String value = String.valueOf(ex.getArguments()[0]);
                    if (!value.equals(anObject)) {
                        n = 1;
                    }
                    anObject = value;
                }
                throw ex;
            }
            finally {
                synchronized (this.preparedStmt) {
                    if (n != 0 || this.preparedStmt.invalidatedWhileCompiling) {
                        this.preparedStmt.isValid = false;
                        this.preparedStmt.invalidatedWhileCompiling = false;
                        n = 1;
                    }
                }
                if (n != 0) {
                    while (languageConnectionContext.getStatementDepth() > statementDepth) {
                        languageConnectionContext.popStatementContext(languageConnectionContext.getStatementContext(), null);
                    }
                }
            }
        }
    }
    
    private PreparedStatement prepMinion(final LanguageConnectionContext languageConnectionContext, final boolean b, final Object[] array, final SchemaDescriptor schemaDescriptor, final boolean b2) throws StandardException {
        long currentTimeMillis = 0L;
        long currentTimeMillis2 = 0L;
        Timestamp timestamp = null;
        Timestamp timestamp2 = null;
        StatementContext pushStatementContext = null;
        if (this.preparedStmt != null && this.preparedStmt.upToDate()) {
            return this.preparedStmt;
        }
        if (languageConnectionContext.getOptimizerTrace()) {
            languageConnectionContext.setOptimizerTraceOutput(this.getSource() + "\n");
        }
        final long currentTimeMillis3 = getCurrentTimeMillis(languageConnectionContext);
        if (currentTimeMillis3 != 0L) {
            timestamp = new Timestamp(currentTimeMillis3);
        }
        this.prepareIsolationLevel = languageConnectionContext.getPrepareIsolationLevel();
        int n = 0;
        if (this.preparedStmt == null) {
            if (b) {
                this.preparedStmt = (GenericPreparedStatement)((GenericLanguageConnectionContext)languageConnectionContext).lookupStatement(this);
            }
            if (this.preparedStmt == null) {
                this.preparedStmt = new GenericPreparedStatement(this);
            }
            else {
                n = 1;
            }
        }
        Label_0291: {
            synchronized (this.preparedStmt) {
                while (true) {
                    while (n == 0 || !this.preparedStmt.referencesSessionSchema()) {
                        if (this.preparedStmt.upToDate()) {
                            return this.preparedStmt;
                        }
                        if (!this.preparedStmt.compilingStatement) {
                            this.preparedStmt.compilingStatement = true;
                            this.preparedStmt.setActivationClass(null);
                            break Label_0291;
                        }
                        try {
                            this.preparedStmt.wait();
                        }
                        catch (InterruptedException ex4) {
                            InterruptStatus.setInterrupted();
                        }
                    }
                    n = 0;
                    this.preparedStmt = new GenericPreparedStatement(this);
                    continue;
                }
            }
            try {
                final HeaderPrintWriter headerPrintWriter = languageConnectionContext.getLogStatementText() ? Monitor.getStream() : null;
                if (!this.preparedStmt.isStorable() || languageConnectionContext.getStatementDepth() == 0) {
                    pushStatementContext = languageConnectionContext.pushStatementContext(true, this.isForReadOnly, this.getSource(), null, false, 0L);
                }
                final CompilerContext pushCompilerContext = languageConnectionContext.pushCompilerContext(this.compilationSchema);
                if (this.prepareIsolationLevel != 0) {
                    pushCompilerContext.setScanIsolationLevel(this.prepareIsolationLevel);
                }
                if (b2 || (schemaDescriptor != null && schemaDescriptor.isSystemSchema() && schemaDescriptor.equals(this.compilationSchema))) {
                    pushCompilerContext.setReliability(0);
                }
                try {
                    if (headerPrintWriter != null) {
                        headerPrintWriter.printlnWithHeader("(XID = " + languageConnectionContext.getTransactionExecute().getActiveStateTxIdString() + "), " + "(SESSIONID = " + languageConnectionContext.getInstanceNumber() + "), " + "(DATABASE = " + languageConnectionContext.getDbname() + "), " + "(DRDAID = " + languageConnectionContext.getDrdaID() + "), Begin compiling prepared statement: " + this.getSource() + " :End prepared statement");
                    }
                    final Parser parser = pushCompilerContext.getParser();
                    pushCompilerContext.setCurrentDependent(this.preparedStmt);
                    final StatementNode statementNode = (StatementNode)parser.parseStatement(this.statementText, array);
                    final long currentTimeMillis4 = getCurrentTimeMillis(languageConnectionContext);
                    this.walkAST(languageConnectionContext, statementNode, 0);
                    final DataDictionary dataDictionary = languageConnectionContext.getDataDictionary();
                    final int n2 = (dataDictionary == null) ? 0 : dataDictionary.startReading(languageConnectionContext);
                    try {
                        languageConnectionContext.beginNestedTransaction(true);
                        statementNode.bindStatement();
                        currentTimeMillis = getCurrentTimeMillis(languageConnectionContext);
                        this.walkAST(languageConnectionContext, statementNode, 1);
                        if (this.preparedStmt.referencesSessionSchema(statementNode) && n != 0) {
                            ((GenericLanguageConnectionContext)languageConnectionContext).removeStatement(this);
                        }
                        statementNode.optimizeStatement();
                        currentTimeMillis2 = getCurrentTimeMillis(languageConnectionContext);
                        this.walkAST(languageConnectionContext, statementNode, 2);
                        if (headerPrintWriter != null) {
                            headerPrintWriter.printlnWithHeader("(XID = " + languageConnectionContext.getTransactionExecute().getActiveStateTxIdString() + "), " + "(SESSIONID = " + languageConnectionContext.getInstanceNumber() + "), " + "(DATABASE = " + languageConnectionContext.getDbname() + "), " + "(DRDAID = " + languageConnectionContext.getDrdaID() + "), End compiling prepared statement: " + this.getSource() + " :End prepared statement");
                        }
                    }
                    catch (StandardException ex) {
                        languageConnectionContext.commitNestedTransaction();
                        if (headerPrintWriter != null) {
                            headerPrintWriter.printlnWithHeader("(XID = " + languageConnectionContext.getTransactionExecute().getActiveStateTxIdString() + "), " + "(SESSIONID = " + languageConnectionContext.getInstanceNumber() + "), " + "(DATABASE = " + languageConnectionContext.getDbname() + "), " + "(DRDAID = " + languageConnectionContext.getDrdaID() + "), Error compiling prepared statement: " + this.getSource() + " :End prepared statement");
                        }
                        throw ex;
                    }
                    finally {
                        if (dataDictionary != null) {
                            dataDictionary.doneReading(n2, languageConnectionContext);
                        }
                    }
                    long currentTimeMillis5;
                    try {
                        final GeneratedClass generate = statementNode.generate(this.preparedStmt.getByteCodeSaver());
                        currentTimeMillis5 = getCurrentTimeMillis(languageConnectionContext);
                        if (currentTimeMillis5 != 0L) {
                            timestamp2 = new Timestamp(currentTimeMillis5);
                        }
                        this.preparedStmt.setConstantAction(statementNode.makeConstantAction());
                        this.preparedStmt.setSavedObjects(pushCompilerContext.getSavedObjects());
                        this.preparedStmt.setRequiredPermissionsList(pushCompilerContext.getRequiredPermissionsList());
                        this.preparedStmt.incrementVersionCounter();
                        this.preparedStmt.setActivationClass(generate);
                        this.preparedStmt.setNeedsSavepoint(statementNode.needsSavepoint());
                        this.preparedStmt.setCursorInfo((CursorInfo)pushCompilerContext.getCursorInfo());
                        this.preparedStmt.setIsAtomic(statementNode.isAtomic());
                        this.preparedStmt.setExecuteStatementNameAndSchema(statementNode.executeStatementName(), statementNode.executeSchemaName());
                        this.preparedStmt.setSPSName(statementNode.getSPSName());
                        this.preparedStmt.completeCompile(statementNode);
                        this.preparedStmt.setCompileTimeWarnings(pushCompilerContext.getWarnings());
                        final TableDescriptor[] updateIndexStatistics = statementNode.updateIndexStatisticsFor();
                        if (updateIndexStatistics.length > 0) {
                            final IndexStatisticsDaemon indexStatsRefresher = languageConnectionContext.getDataDictionary().getIndexStatsRefresher(true);
                            if (indexStatsRefresher != null) {
                                for (int i = 0; i < updateIndexStatistics.length; ++i) {
                                    indexStatsRefresher.schedule(updateIndexStatistics[i]);
                                }
                            }
                        }
                    }
                    catch (StandardException ex2) {
                        languageConnectionContext.commitNestedTransaction();
                        throw ex2;
                    }
                    if (languageConnectionContext.getRunTimeStatisticsMode()) {
                        this.preparedStmt.setCompileTimeMillis(currentTimeMillis4 - currentTimeMillis3, currentTimeMillis - currentTimeMillis4, currentTimeMillis2 - currentTimeMillis, currentTimeMillis5 - currentTimeMillis2, currentTimeMillis5 - currentTimeMillis3, timestamp, timestamp2);
                    }
                }
                finally {
                    languageConnectionContext.popCompilerContext(pushCompilerContext);
                }
            }
            catch (StandardException ex3) {
                if (n != 0) {
                    ((GenericLanguageConnectionContext)languageConnectionContext).removeStatement(this);
                }
                throw ex3;
            }
            finally {
                synchronized (this.preparedStmt) {
                    this.preparedStmt.compilingStatement = false;
                    this.preparedStmt.notifyAll();
                }
            }
        }
        languageConnectionContext.commitNestedTransaction();
        if (pushStatementContext != null) {
            languageConnectionContext.popStatementContext(pushStatementContext, null);
        }
        return this.preparedStmt;
    }
    
    private void walkAST(final LanguageConnectionContext languageConnectionContext, final Visitable visitable, final int n) throws StandardException {
        final ASTVisitor astVisitor = languageConnectionContext.getASTVisitor();
        if (astVisitor != null) {
            astVisitor.begin(this.statementText, n);
            visitable.accept(astVisitor);
            astVisitor.end(n);
        }
    }
    
    public PreparedStatement prepareStorable(final LanguageConnectionContext languageConnectionContext, PreparedStatement preparedStatement, final Object[] array, final SchemaDescriptor schemaDescriptor, final boolean b) throws StandardException {
        if (preparedStatement == null) {
            preparedStatement = new GenericStorablePreparedStatement(this);
        }
        else {
            ((GenericPreparedStatement)preparedStatement).statement = this;
        }
        this.preparedStmt = (GenericPreparedStatement)preparedStatement;
        return this.prepMinion(languageConnectionContext, false, array, schemaDescriptor, b);
    }
    
    public String getSource() {
        return this.statementText;
    }
    
    public String getCompilationSchema() {
        return this.compilationSchema.getDescriptorName();
    }
    
    private static long getCurrentTimeMillis(final LanguageConnectionContext languageConnectionContext) {
        if (languageConnectionContext.getStatisticsTiming()) {
            return System.currentTimeMillis();
        }
        return 0L;
    }
    
    public PreparedStatement getPreparedStatement() {
        return this.preparedStmt;
    }
    
    public boolean equals(final Object o) {
        if (o instanceof GenericStatement) {
            final GenericStatement genericStatement = (GenericStatement)o;
            return this.statementText.equals(genericStatement.statementText) && this.isForReadOnly == genericStatement.isForReadOnly && this.compilationSchema.equals(genericStatement.compilationSchema) && this.prepareIsolationLevel == genericStatement.prepareIsolationLevel;
        }
        return false;
    }
    
    public int hashCode() {
        return this.statementText.hashCode();
    }
}
