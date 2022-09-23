// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.query;

import org.datanucleus.util.Localiser;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.store.rdbms.sql.SQLJoin;
import org.datanucleus.util.Imports;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.InheritanceStrategy;
import org.datanucleus.store.scostore.Store;
import org.datanucleus.store.rdbms.scostore.BaseContainerStore;
import org.datanucleus.store.rdbms.sql.expression.BooleanSubqueryExpression;
import org.datanucleus.store.rdbms.table.JoinTable;
import org.datanucleus.store.rdbms.scostore.FKMapStore;
import org.datanucleus.store.rdbms.scostore.JoinMapStore;
import org.datanucleus.store.rdbms.scostore.FKListStore;
import org.datanucleus.store.rdbms.scostore.JoinListStore;
import org.datanucleus.store.rdbms.scostore.FKSetStore;
import org.datanucleus.store.rdbms.scostore.JoinSetStore;
import org.datanucleus.FetchPlanForClass;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.expression.BooleanExpression;
import org.datanucleus.store.types.SCOUtils;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import java.util.HashSet;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.SQLController;
import org.datanucleus.query.evaluator.JavaQueryEvaluator;
import java.util.List;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.query.QueryTimeoutException;
import org.datanucleus.store.query.QueryInterruptedException;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.query.QueryResult;
import org.datanucleus.store.connection.ManagedConnectionResourceListener;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.store.rdbms.scostore.IteratorStatement;
import org.datanucleus.store.rdbms.sql.SQLStatementHelper;
import org.datanucleus.store.query.CandidateIdsQueryResult;
import java.util.Collection;
import org.datanucleus.store.query.Query;
import org.datanucleus.query.evaluator.JDOQLEvaluator;
import java.util.ArrayList;
import java.util.Collections;
import org.datanucleus.store.query.QueryManager;
import org.datanucleus.query.symbol.Symbol;
import java.util.Iterator;
import java.util.Set;
import org.datanucleus.metadata.AbstractClassMetaData;
import java.security.AccessController;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import org.datanucleus.metadata.AbstractMemberMetaData;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.query.QueryUtils;
import java.security.PrivilegedAction;
import org.datanucleus.store.rdbms.mapping.java.AbstractContainerMapping;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import java.util.Map;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.query.AbstractJDOQLQuery;

public class JDOQLQuery extends AbstractJDOQLQuery
{
    protected transient RDBMSQueryCompilation datastoreCompilation;
    boolean statementReturnsEmpty;
    
    public JDOQLQuery(final StoreManager storeMgr, final ExecutionContext ec) {
        this(storeMgr, ec, (JDOQLQuery)null);
    }
    
    public JDOQLQuery(final StoreManager storeMgr, final ExecutionContext ec, final JDOQLQuery q) {
        super(storeMgr, ec, q);
        this.datastoreCompilation = null;
        this.statementReturnsEmpty = false;
    }
    
    public JDOQLQuery(final StoreManager storeMgr, final ExecutionContext ec, final String query) {
        super(storeMgr, ec, query);
        this.datastoreCompilation = null;
        this.statementReturnsEmpty = false;
    }
    
    @Override
    protected void discardCompiled() {
        super.discardCompiled();
        this.datastoreCompilation = null;
    }
    
    @Override
    protected boolean isCompiled() {
        if (this.evaluateInMemory()) {
            return this.compilation != null;
        }
        if (this.compilation == null || this.datastoreCompilation == null) {
            return false;
        }
        if (!this.datastoreCompilation.isPrecompilable()) {
            NucleusLogger.GENERAL.info("Query compiled but not precompilable so ditching datastore compilation");
            this.datastoreCompilation = null;
            return false;
        }
        return true;
    }
    
    @Override
    protected boolean evaluateInMemory() {
        if (this.candidateCollection == null) {
            return super.evaluateInMemory();
        }
        if (this.compilation != null && this.compilation.getSubqueryAliases() != null) {
            NucleusLogger.QUERY.warn("In-memory evaluator doesn't currently handle subqueries completely so evaluating in datastore");
            return false;
        }
        final Object val = this.getExtension("datanucleus.query.evaluateInMemory");
        if (val == null) {
            return true;
        }
        final Boolean bool = Boolean.valueOf((String)val);
        return bool == null || bool;
    }
    
    @Override
    protected String getQueryCacheKey() {
        if (this.getSerializeRead() != null && this.getSerializeRead()) {
            return super.getQueryCacheKey() + " FOR UPDATE";
        }
        return super.getQueryCacheKey();
    }
    
    @Override
    protected synchronized void compileInternal(final Map parameterValues) {
        if (this.isCompiled()) {
            return;
        }
        super.compileInternal(parameterValues);
        final boolean inMemory = this.evaluateInMemory();
        if (this.candidateCollection != null && inMemory) {
            return;
        }
        final RDBMSStoreManager storeMgr = (RDBMSStoreManager)this.getStoreManager();
        if (this.candidateClass == null) {
            throw new NucleusUserException(JDOQLQuery.LOCALISER.msg("021009", this.candidateClassName));
        }
        this.ec.hasPersistenceInformationForClass(this.candidateClass);
        AbstractClassMetaData acmd = this.ec.getMetaDataManager().getMetaDataForClass(this.candidateClass, this.clr);
        if (this.candidateClass.isInterface()) {
            final String[] impls = this.ec.getMetaDataManager().getClassesImplementingInterface(this.candidateClass.getName(), this.clr);
            if (!acmd.isImplementationOfPersistentDefinition() || impls.length != 1) {
                acmd = this.ec.getMetaDataManager().getMetaDataForInterface(this.candidateClass, this.clr);
                if (acmd == null) {
                    throw new NucleusUserException("Attempting to query an interface yet it is not declared 'persistent'. Define the interface in metadata as being persistent to perform this operation, and make sure any implementations use the same identity and identity member(s)");
                }
            }
        }
        if (parameterValues != null) {
            final Set paramNames = parameterValues.entrySet();
            for (final Map.Entry entry : paramNames) {
                final Object paramName = entry.getKey();
                if (paramName instanceof String) {
                    final Symbol sym = this.compilation.getSymbolTable().getSymbol((String)paramName);
                    final Object value = entry.getValue();
                    if (value == null && sym != null && sym.getValueType() != null && sym.getValueType().isPrimitive()) {
                        throw new NucleusUserException(JDOQLQuery.LOCALISER.msg("021117", paramName, sym.getValueType().getName()));
                    }
                    continue;
                }
            }
        }
        final QueryManager qm = this.getQueryManager();
        final String datastoreKey = storeMgr.getQueryCacheKey();
        final String queryCacheKey = this.getQueryCacheKey();
        if (this.useCaching() && queryCacheKey != null) {
            boolean nullParameter = false;
            if (parameterValues != null) {
                for (final Object val : parameterValues.values()) {
                    if (val == null) {
                        nullParameter = true;
                        break;
                    }
                }
            }
            if (!nullParameter) {
                this.datastoreCompilation = (RDBMSQueryCompilation)qm.getDatastoreQueryCompilation(datastoreKey, this.getLanguage(), queryCacheKey);
                if (this.datastoreCompilation != null) {
                    this.setResultDistinct(this.compilation.getResultDistinct());
                    return;
                }
            }
        }
        if (this.type == 1) {
            this.datastoreCompilation = new RDBMSQueryCompilation();
            this.compileQueryUpdate(parameterValues, acmd);
        }
        else if (this.type == 2) {
            this.datastoreCompilation = new RDBMSQueryCompilation();
            this.compileQueryDelete(parameterValues, acmd);
        }
        else {
            synchronized (this.datastoreCompilation = new RDBMSQueryCompilation()) {
                if (inMemory) {
                    this.compileQueryToRetrieveCandidates(parameterValues, acmd);
                }
                else {
                    this.compileQueryFull(parameterValues, acmd);
                    if (this.result != null) {
                        final StatementResultMapping resultMapping = this.datastoreCompilation.getResultDefinition();
                        for (int i = 0; i < resultMapping.getNumberOfResultExpressions(); ++i) {
                            final Object stmtMap = resultMapping.getMappingForResultExpression(i);
                            if (stmtMap instanceof StatementMappingIndex) {
                                final StatementMappingIndex idx = (StatementMappingIndex)stmtMap;
                                final AbstractMemberMetaData mmd = idx.getMapping().getMemberMetaData();
                                if (mmd != null && (mmd.hasCollection() || mmd.hasMap() || mmd.hasArray()) && idx.getMapping() instanceof AbstractContainerMapping) {
                                    throw new NucleusUserException(JDOQLQuery.LOCALISER.msg("021213"));
                                }
                            }
                        }
                        if (this.resultClass != null) {
                            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                                @Override
                                public Object run() {
                                    final StatementResultMapping resultMapping = JDOQLQuery.this.datastoreCompilation.getResultDefinition();
                                    if (QueryUtils.resultClassIsSimple(JDOQLQuery.this.resultClass.getName())) {
                                        if (resultMapping.getNumberOfResultExpressions() > 1) {
                                            throw new NucleusUserException(JDOQLQuery.LOCALISER.msg("021201", JDOQLQuery.this.resultClass.getName()));
                                        }
                                        final Object stmtMap = resultMapping.getMappingForResultExpression(0);
                                        if (!(stmtMap instanceof StatementMappingIndex)) {
                                            throw new NucleusUserException("Don't support result clause of " + JDOQLQuery.this.result + " with resultClass of " + JDOQLQuery.this.resultClass.getName());
                                        }
                                        final StatementMappingIndex idx = (StatementMappingIndex)stmtMap;
                                        final Class exprType = idx.getMapping().getJavaType();
                                        boolean typeConsistent = false;
                                        if (exprType == JDOQLQuery.this.resultClass) {
                                            typeConsistent = true;
                                        }
                                        else if (exprType.isPrimitive()) {
                                            final Class resultClassPrimitive = ClassUtils.getPrimitiveTypeForType(JDOQLQuery.this.resultClass);
                                            if (resultClassPrimitive == exprType) {
                                                typeConsistent = true;
                                            }
                                        }
                                        if (!typeConsistent) {
                                            throw new NucleusUserException(JDOQLQuery.LOCALISER.msg("021202", JDOQLQuery.this.resultClass.getName(), exprType));
                                        }
                                    }
                                    else if (QueryUtils.resultClassIsUserType(JDOQLQuery.this.resultClass.getName())) {
                                        final Class[] ctrTypes = new Class[resultMapping.getNumberOfResultExpressions()];
                                        for (int i = 0; i < ctrTypes.length; ++i) {
                                            final Object stmtMap2 = resultMapping.getMappingForResultExpression(i);
                                            if (stmtMap2 instanceof StatementMappingIndex) {
                                                ctrTypes[i] = ((StatementMappingIndex)stmtMap2).getMapping().getJavaType();
                                            }
                                            else if (stmtMap2 instanceof StatementNewObjectMapping) {}
                                        }
                                        final Constructor ctr = ClassUtils.getConstructorWithArguments(JDOQLQuery.this.resultClass, ctrTypes);
                                        if (ctr == null && !ClassUtils.hasDefaultConstructor(JDOQLQuery.this.resultClass)) {
                                            throw new NucleusUserException(JDOQLQuery.LOCALISER.msg("021205", JDOQLQuery.this.resultClass.getName()));
                                        }
                                        if (ctr == null) {
                                            for (int j = 0; j < resultMapping.getNumberOfResultExpressions(); ++j) {
                                                final Object stmtMap3 = resultMapping.getMappingForResultExpression(j);
                                                if (stmtMap3 instanceof StatementMappingIndex) {
                                                    final StatementMappingIndex mapIdx = (StatementMappingIndex)stmtMap3;
                                                    final AbstractMemberMetaData mmd = mapIdx.getMapping().getMemberMetaData();
                                                    String fieldName = mapIdx.getColumnAlias();
                                                    final Class fieldType = mapIdx.getMapping().getJavaType();
                                                    if (fieldName == null && mmd != null) {
                                                        fieldName = mmd.getName();
                                                    }
                                                    if (fieldName != null) {
                                                        Class resultFieldType = null;
                                                        boolean publicField = true;
                                                        try {
                                                            final Field fld = JDOQLQuery.this.resultClass.getDeclaredField(fieldName);
                                                            resultFieldType = fld.getType();
                                                            if (!ClassUtils.typesAreCompatible(fieldType, resultFieldType) && !ClassUtils.typesAreCompatible(resultFieldType, fieldType)) {
                                                                throw new NucleusUserException(JDOQLQuery.LOCALISER.msg("021211", fieldName, fieldType.getName(), resultFieldType.getName()));
                                                            }
                                                            if (!Modifier.isPublic(fld.getModifiers())) {
                                                                publicField = false;
                                                            }
                                                        }
                                                        catch (NoSuchFieldException nsfe) {
                                                            publicField = false;
                                                        }
                                                        if (!publicField) {
                                                            final Method setMethod = QueryUtils.getPublicSetMethodForFieldOfResultClass(JDOQLQuery.this.resultClass, fieldName, resultFieldType);
                                                            if (setMethod == null) {
                                                                final Method putMethod = QueryUtils.getPublicPutMethodForResultClass(JDOQLQuery.this.resultClass);
                                                                if (putMethod == null) {
                                                                    throw new NucleusUserException(JDOQLQuery.LOCALISER.msg("021212", JDOQLQuery.this.resultClass.getName(), fieldName));
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                else if (stmtMap3 instanceof StatementNewObjectMapping) {}
                                            }
                                        }
                                    }
                                    return null;
                                }
                            });
                        }
                    }
                }
                if (NucleusLogger.QUERY.isDebugEnabled()) {
                    NucleusLogger.QUERY.debug(JDOQLQuery.LOCALISER.msg("021085", this, this.datastoreCompilation.getSQL()));
                }
                boolean hasParams = false;
                if (this.explicitParameters != null) {
                    hasParams = true;
                }
                else if (parameterValues != null && parameterValues.size() > 0) {
                    hasParams = true;
                }
                if (!this.statementReturnsEmpty && queryCacheKey != null) {
                    if (!this.datastoreCompilation.isPrecompilable() || (this.datastoreCompilation.getSQL().indexOf(63) < 0 && hasParams)) {
                        NucleusLogger.QUERY.debug(JDOQLQuery.LOCALISER.msg("021075"));
                    }
                    else {
                        qm.addDatastoreQueryCompilation(datastoreKey, this.getLanguage(), queryCacheKey, this.datastoreCompilation);
                    }
                }
            }
        }
    }
    
    public String getSQL() {
        if (this.datastoreCompilation != null) {
            return this.datastoreCompilation.getSQL();
        }
        return null;
    }
    
    @Override
    protected Object performExecute(final Map parameters) {
        if (this.statementReturnsEmpty) {
            return Collections.EMPTY_LIST;
        }
        final boolean inMemory = this.evaluateInMemory();
        if (this.candidateCollection != null) {
            if (this.candidateCollection.isEmpty()) {
                return Collections.EMPTY_LIST;
            }
            if (inMemory) {
                final List candidates = new ArrayList(this.candidateCollection);
                final JavaQueryEvaluator resultMapper = new JDOQLEvaluator(this, candidates, this.compilation, parameters, this.clr);
                return resultMapper.execute(true, true, true, true, true);
            }
        }
        else if (this.type == 0) {
            final List<Object> cachedResults = this.getQueryManager().getDatastoreQueryResult(this, parameters);
            if (cachedResults != null) {
                return new CandidateIdsQueryResult(this, cachedResults);
            }
        }
        Object results = null;
        final RDBMSStoreManager storeMgr = (RDBMSStoreManager)this.getStoreManager();
        final ManagedConnection mconn = storeMgr.getConnection(this.ec);
        try {
            final long startTime = System.currentTimeMillis();
            if (NucleusLogger.QUERY.isDebugEnabled()) {
                NucleusLogger.QUERY.debug(JDOQLQuery.LOCALISER.msg("021046", this.getLanguage(), this.getSingleStringQuery(), null));
            }
            final AbstractClassMetaData acmd = this.ec.getMetaDataManager().getMetaDataForClass(this.candidateClass, this.clr);
            final SQLController sqlControl = storeMgr.getSQLController();
            PreparedStatement ps = null;
            try {
                if (this.type == 0) {
                    ps = RDBMSQueryUtils.getPreparedStatementForQuery(mconn, this.datastoreCompilation.getSQL(), this);
                    SQLStatementHelper.applyParametersToStatement(ps, this.ec, this.datastoreCompilation.getStatementParameters(), this.datastoreCompilation.getParameterNameByPosition(), parameters);
                    RDBMSQueryUtils.prepareStatementForExecution(ps, this, true);
                    this.registerTask(ps);
                    ResultSet rs = null;
                    try {
                        rs = sqlControl.executeStatementQuery(this.ec, mconn, this.toString(), ps);
                    }
                    finally {
                        this.deregisterTask();
                    }
                    AbstractRDBMSQueryResult qr = null;
                    try {
                        if (inMemory) {
                            final ResultObjectFactory rof = storeMgr.newResultObjectFactory(acmd, this.datastoreCompilation.getResultDefinitionForClass(), this.ignoreCache, this.getFetchPlan(), this.candidateClass);
                            final List candidates2 = new ArrayList();
                            while (rs.next()) {
                                candidates2.add(rof.getObject(this.ec, rs));
                            }
                            final JavaQueryEvaluator resultMapper2 = new JDOQLEvaluator(this, candidates2, this.compilation, parameters, this.clr);
                            results = resultMapper2.execute(true, true, true, true, true);
                        }
                        else {
                            ResultObjectFactory rof = null;
                            if (this.result != null) {
                                rof = new ResultClassROF(storeMgr, this.resultClass, this.datastoreCompilation.getResultDefinition());
                            }
                            else if (this.resultClass != null && this.resultClass != this.candidateClass) {
                                rof = new ResultClassROF(storeMgr, this.resultClass, this.datastoreCompilation.getResultDefinitionForClass());
                            }
                            else {
                                rof = storeMgr.newResultObjectFactory(acmd, this.datastoreCompilation.getResultDefinitionForClass(), this.ignoreCache, this.getFetchPlan(), this.candidateClass);
                            }
                            final String resultSetType = RDBMSQueryUtils.getResultSetTypeForQuery(this);
                            if (resultSetType.equals("scroll-insensitive") || resultSetType.equals("scroll-sensitive")) {
                                qr = new ScrollableQueryResult(this, rof, rs, this.getResultDistinct() ? null : this.candidateCollection);
                            }
                            else {
                                qr = new ForwardQueryResult(this, rof, rs, this.getResultDistinct() ? null : this.candidateCollection);
                            }
                            final Map<String, IteratorStatement> scoIterStmts = this.datastoreCompilation.getSCOIteratorStatements();
                            if (scoIterStmts != null) {
                                for (final Map.Entry<String, IteratorStatement> stmtIterEntry : scoIterStmts.entrySet()) {
                                    final IteratorStatement iterStmt = stmtIterEntry.getValue();
                                    final String iterStmtSQL = iterStmt.getSQLStatement().getSelectStatement().toSQL();
                                    NucleusLogger.DATASTORE_RETRIEVE.debug(">> JDOQL Bulk-Fetch of " + iterStmt.getBackingStore().getOwnerMemberMetaData().getFullFieldName());
                                    try {
                                        try {
                                            final PreparedStatement psSco = sqlControl.getStatementForQuery(mconn, iterStmtSQL);
                                            final ResultSet rsSCO = sqlControl.executeStatementQuery(this.ec, mconn, iterStmtSQL, psSco);
                                            qr.registerMemberBulkResultSet(iterStmt, rsSCO);
                                        }
                                        finally {
                                            mconn.release();
                                        }
                                    }
                                    catch (SQLException e) {
                                        throw new NucleusDataStoreException(JDOQLQuery.LOCALISER.msg("056006", iterStmtSQL), e);
                                    }
                                }
                            }
                            qr.initialise();
                            final QueryResult qr2 = qr;
                            final ManagedConnection mconn2 = mconn;
                            final ManagedConnectionResourceListener listener = new ManagedConnectionResourceListener() {
                                @Override
                                public void transactionFlushed() {
                                }
                                
                                @Override
                                public void transactionPreClose() {
                                    qr2.disconnect();
                                }
                                
                                @Override
                                public void managedConnectionPreClose() {
                                    if (!JDOQLQuery.this.ec.getTransaction().isActive()) {
                                        qr2.disconnect();
                                    }
                                }
                                
                                @Override
                                public void managedConnectionPostClose() {
                                }
                                
                                @Override
                                public void resourcePostClose() {
                                    mconn2.removeListener(this);
                                }
                            };
                            mconn.addListener(listener);
                            qr.addConnectionListener(listener);
                            results = qr;
                        }
                    }
                    finally {
                        if (qr == null) {
                            rs.close();
                        }
                    }
                }
                else if (this.type == 1) {
                    ps = sqlControl.getStatementForUpdate(mconn, this.datastoreCompilation.getSQL(), false);
                    SQLStatementHelper.applyParametersToStatement(ps, this.ec, this.datastoreCompilation.getStatementParameters(), this.datastoreCompilation.getParameterNameByPosition(), parameters);
                    RDBMSQueryUtils.prepareStatementForExecution(ps, this, false);
                    final int[] updateResults = sqlControl.executeStatementUpdate(this.ec, mconn, this.toString(), ps, true);
                    try {
                        this.ec.getNucleusContext().getLevel2Cache().evictAll(this.candidateClass, this.subclasses);
                    }
                    catch (UnsupportedOperationException ex) {}
                    results = updateResults[0];
                }
                else if (this.type == 1 || this.type == 2) {
                    long bulkResult = 0L;
                    if (this.datastoreCompilation.getSQL() != null) {
                        ps = sqlControl.getStatementForUpdate(mconn, this.datastoreCompilation.getSQL(), false);
                        SQLStatementHelper.applyParametersToStatement(ps, this.ec, this.datastoreCompilation.getStatementParameters(), this.datastoreCompilation.getParameterNameByPosition(), parameters);
                        RDBMSQueryUtils.prepareStatementForExecution(ps, this, false);
                        final int[] execResults = sqlControl.executeStatementUpdate(this.ec, mconn, this.toString(), ps, true);
                        bulkResult = execResults[0];
                    }
                    else {
                        final List<String> sqls = this.datastoreCompilation.getSQLs();
                        final List<Boolean> sqlUseInCountFlags = this.datastoreCompilation.getSQLUseInCountFlags();
                        final Iterator<Boolean> sqlUseInCountIter = sqlUseInCountFlags.iterator();
                        for (final String sql : sqls) {
                            final Boolean useInCount = sqlUseInCountIter.next();
                            ps = sqlControl.getStatementForUpdate(mconn, sql, false);
                            SQLStatementHelper.applyParametersToStatement(ps, this.ec, this.datastoreCompilation.getStatementParameters(), this.datastoreCompilation.getParameterNameByPosition(), parameters);
                            RDBMSQueryUtils.prepareStatementForExecution(ps, this, false);
                            final int[] execResults2 = sqlControl.executeStatementUpdate(this.ec, mconn, this.toString(), ps, true);
                            if (useInCount) {
                                bulkResult += execResults2[0];
                            }
                        }
                    }
                    try {
                        this.ec.getNucleusContext().getLevel2Cache().evictAll(this.candidateClass, this.subclasses);
                    }
                    catch (UnsupportedOperationException ex2) {}
                    results = bulkResult;
                }
            }
            catch (SQLException sqle) {
                if (storeMgr.getDatastoreAdapter().isStatementCancel(sqle)) {
                    throw new QueryInterruptedException("Query has been interrupted", sqle);
                }
                if (storeMgr.getDatastoreAdapter().isStatementTimeout(sqle)) {
                    throw new QueryTimeoutException("Query has been timed out", sqle);
                }
                throw new NucleusException(JDOQLQuery.LOCALISER.msg("021042"), sqle);
            }
            if (NucleusLogger.QUERY.isDebugEnabled()) {
                NucleusLogger.QUERY.debug(JDOQLQuery.LOCALISER.msg("021074", this.getLanguage(), "" + (System.currentTimeMillis() - startTime)));
            }
            return results;
        }
        finally {
            mconn.release();
        }
    }
    
    @Override
    protected void assertSupportsCancel() {
    }
    
    @Override
    protected boolean cancelTaskObject(final Object obj) {
        final Statement ps = (Statement)obj;
        try {
            ps.cancel();
            return true;
        }
        catch (SQLException sqle) {
            NucleusLogger.DATASTORE_RETRIEVE.warn("Error cancelling query", sqle);
            return false;
        }
    }
    
    @Override
    protected boolean supportsTimeout() {
        return true;
    }
    
    private void compileQueryFull(final Map parameters, final AbstractClassMetaData candidateCmd) {
        if (this.type != 0) {
            return;
        }
        long startTime = 0L;
        if (NucleusLogger.QUERY.isDebugEnabled()) {
            startTime = System.currentTimeMillis();
            NucleusLogger.QUERY.debug(JDOQLQuery.LOCALISER.msg("021083", this.getLanguage(), this.toString()));
        }
        if (this.result != null) {
            this.datastoreCompilation.setResultDefinition(new StatementResultMapping());
        }
        else {
            this.datastoreCompilation.setResultDefinitionForClass(new StatementClassMapping());
        }
        SQLStatement stmt = null;
        try {
            stmt = RDBMSQueryUtils.getStatementForCandidates((RDBMSStoreManager)this.getStoreManager(), null, candidateCmd, this.datastoreCompilation.getResultDefinitionForClass(), this.ec, this.candidateClass, this.subclasses, this.result, null, null);
        }
        catch (NucleusException ne) {
            NucleusLogger.QUERY.warn("Query for candidates of " + this.candidateClass.getName() + (this.subclasses ? " and subclasses" : "") + " resulted in no possible candidates", ne);
            this.statementReturnsEmpty = true;
            return;
        }
        final Set<String> options = new HashSet<String>();
        options.add("BULK_UPDATE_VERSION");
        final QueryToSQLMapper sqlMapper = new QueryToSQLMapper(stmt, this.compilation, parameters, this.datastoreCompilation.getResultDefinitionForClass(), this.datastoreCompilation.getResultDefinition(), candidateCmd, this.getFetchPlan(), this.ec, this.getParsedImports(), options, this.extensions);
        sqlMapper.compile();
        this.datastoreCompilation.setParameterNameByPosition(sqlMapper.getParameterNameByPosition());
        this.datastoreCompilation.setPrecompilable(sqlMapper.isPrecompilable());
        if (!this.getResultDistinct() && stmt.isDistinct()) {
            this.setResultDistinct(true);
            this.compilation.setResultDistinct();
        }
        if (this.candidateCollection != null) {
            BooleanExpression candidateExpr = null;
            final Iterator iter = this.candidateCollection.iterator();
            final JavaTypeMapping idMapping = stmt.getPrimaryTable().getTable().getIdMapping();
            while (iter.hasNext()) {
                final Object candidate = iter.next();
                final SQLExpression idExpr = stmt.getSQLExpressionFactory().newExpression(stmt, stmt.getPrimaryTable(), idMapping);
                final SQLExpression idVal = stmt.getSQLExpressionFactory().newLiteral(stmt, idMapping, candidate);
                if (candidateExpr == null) {
                    candidateExpr = idExpr.eq(idVal);
                }
                else {
                    candidateExpr = candidateExpr.ior(idExpr.eq(idVal));
                }
            }
            stmt.whereAnd(candidateExpr, true);
        }
        if (this.range != null) {
            long lower = this.fromInclNo;
            long upper = this.toExclNo;
            if (this.fromInclParam != null) {
                if (parameters.containsKey(this.fromInclParam)) {
                    lower = parameters.get(this.fromInclParam).longValue();
                }
                else {
                    final int pos = parameters.size() - 2;
                    lower = parameters.get(pos).longValue();
                }
            }
            if (this.toExclParam != null) {
                if (parameters.containsKey(this.toExclParam)) {
                    upper = parameters.get(this.toExclParam).longValue();
                }
                else {
                    final int pos = parameters.size() - 1;
                    upper = parameters.get(pos).longValue();
                }
            }
            stmt.setRange(lower, upper - lower);
        }
        final boolean useUpdateLock = RDBMSQueryUtils.useUpdateLockForQuery(this);
        stmt.addExtension("lock-for-update", useUpdateLock);
        this.datastoreCompilation.setSQL(stmt.getSelectStatement().toString());
        this.datastoreCompilation.setStatementParameters(stmt.getSelectStatement().getParametersForStatement());
        if (this.result == null && (this.resultClass == null || this.resultClass == this.candidateClass)) {
            final FetchPlanForClass fpc = this.getFetchPlan().getFetchPlanForClass(candidateCmd);
            final int[] fpMembers = fpc.getMemberNumbers();
            for (int i = 0; i < fpMembers.length; ++i) {
                final AbstractMemberMetaData fpMmd = candidateCmd.getMetaDataForManagedMemberAtAbsolutePosition(fpMembers[i]);
                final RelationType fpRelType = fpMmd.getRelationType(this.clr);
                if (RelationType.isRelationMultiValued(fpRelType)) {
                    if (fpMmd.hasCollection()) {
                        if (!SCOUtils.collectionHasSerialisedElements(fpMmd)) {
                            final IteratorStatement iterStmt = this.getSQLStatementForContainerFieldBatch(this.ec, candidateCmd, parameters, fpMmd);
                            if (iterStmt != null) {
                                this.datastoreCompilation.setSCOIteratorStatement(fpMmd.getFullFieldName(), iterStmt);
                            }
                            else {
                                NucleusLogger.GENERAL.debug("Note that query has field " + fpMmd.getFullFieldName() + " marked in the FetchPlan, yet this is currently not fetched by this query");
                            }
                        }
                    }
                    else if (fpMmd.hasMap()) {
                        NucleusLogger.GENERAL.debug("Note that query has field " + fpMmd.getFullFieldName() + " marked in the FetchPlan, yet this is currently not fetched by this query");
                    }
                    else {
                        NucleusLogger.GENERAL.debug("Note that query has field " + fpMmd.getFullFieldName() + " marked in the FetchPlan, yet this is currently not fetched by this query");
                    }
                }
            }
        }
        if (NucleusLogger.QUERY.isDebugEnabled()) {
            NucleusLogger.QUERY.debug(JDOQLQuery.LOCALISER.msg("021084", this.getLanguage(), System.currentTimeMillis() - startTime));
        }
    }
    
    protected IteratorStatement getSQLStatementForContainerFieldBatch(final ExecutionContext ec, final AbstractClassMetaData candidateCmd, final Map parameters, final AbstractMemberMetaData mmd) {
        IteratorStatement iterStmt = null;
        final Store backingStore = ((RDBMSStoreManager)this.storeMgr).getBackingStoreForField(this.clr, mmd, null);
        if (backingStore instanceof JoinSetStore) {
            iterStmt = ((JoinSetStore)backingStore).getIteratorStatement(this.clr, ec.getFetchPlan(), false);
        }
        else if (backingStore instanceof FKSetStore) {
            iterStmt = ((FKSetStore)backingStore).getIteratorStatement(this.clr, ec.getFetchPlan(), false);
        }
        else if (backingStore instanceof JoinListStore) {
            iterStmt = ((JoinListStore)backingStore).getIteratorStatement(this.clr, ec.getFetchPlan(), false, -1, -1);
        }
        else if (backingStore instanceof FKListStore) {
            iterStmt = ((FKListStore)backingStore).getIteratorStatement(this.clr, ec.getFetchPlan(), false, -1, -1);
        }
        else {
            if (backingStore instanceof JoinMapStore) {
                return null;
            }
            if (backingStore instanceof FKMapStore) {
                return null;
            }
        }
        if (backingStore instanceof JoinSetStore || backingStore instanceof JoinListStore) {
            final SQLStatement sqlStmt = iterStmt.getSQLStatement();
            final JoinTable joinTbl = (JoinTable)sqlStmt.getPrimaryTable().getTable();
            final JavaTypeMapping joinOwnerMapping = joinTbl.getOwnerMapping();
            final SQLStatement existsStmt = RDBMSQueryUtils.getStatementForCandidates((RDBMSStoreManager)this.getStoreManager(), sqlStmt, candidateCmd, this.datastoreCompilation.getResultDefinitionForClass(), ec, this.candidateClass, this.subclasses, this.result, null, null);
            final Set<String> options = new HashSet<String>();
            options.add("RESULT_CANDIDATE_ID");
            final QueryToSQLMapper sqlMapper = new QueryToSQLMapper(existsStmt, this.compilation, parameters, null, null, candidateCmd, this.getFetchPlan(), ec, this.getParsedImports(), options, this.extensions);
            sqlMapper.compile();
            final BooleanExpression existsExpr = new BooleanSubqueryExpression(sqlStmt, "EXISTS", existsStmt);
            sqlStmt.whereAnd(existsExpr, true);
            final SQLExpression joinTblOwnerExpr = sqlStmt.getRDBMSManager().getSQLExpressionFactory().newExpression(sqlStmt, sqlStmt.getPrimaryTable(), joinOwnerMapping);
            final SQLExpression existsOwnerExpr = sqlStmt.getRDBMSManager().getSQLExpressionFactory().newExpression(existsStmt, existsStmt.getPrimaryTable(), existsStmt.getPrimaryTable().getTable().getIdMapping());
            existsStmt.whereAnd(joinTblOwnerExpr.eq(existsOwnerExpr), true);
            final int[] ownerColIndexes = sqlStmt.select(joinTblOwnerExpr, null);
            final StatementMappingIndex ownerMapIdx = new StatementMappingIndex(existsStmt.getPrimaryTable().getTable().getIdMapping());
            ownerMapIdx.setColumnPositions(ownerColIndexes);
            iterStmt.setOwnerMapIndex(ownerMapIdx);
        }
        else if (backingStore instanceof FKSetStore || backingStore instanceof FKListStore) {
            final SQLStatement sqlStmt = iterStmt.getSQLStatement();
            final SQLStatement existsStmt2 = RDBMSQueryUtils.getStatementForCandidates((RDBMSStoreManager)this.getStoreManager(), sqlStmt, candidateCmd, this.datastoreCompilation.getResultDefinitionForClass(), ec, this.candidateClass, this.subclasses, this.result, null, null);
            final Set<String> options2 = new HashSet<String>();
            options2.add("RESULT_CANDIDATE_ID");
            final QueryToSQLMapper sqlMapper2 = new QueryToSQLMapper(existsStmt2, this.compilation, parameters, null, null, candidateCmd, this.getFetchPlan(), ec, this.getParsedImports(), options2, this.extensions);
            sqlMapper2.compile();
            final BooleanExpression existsExpr2 = new BooleanSubqueryExpression(sqlStmt, "EXISTS", existsStmt2);
            sqlStmt.whereAnd(existsExpr2, true);
            final SQLExpression elemTblOwnerExpr = sqlStmt.getRDBMSManager().getSQLExpressionFactory().newExpression(sqlStmt, sqlStmt.getPrimaryTable(), ((BaseContainerStore)backingStore).getOwnerMapping());
            final SQLExpression existsOwnerExpr2 = sqlStmt.getRDBMSManager().getSQLExpressionFactory().newExpression(existsStmt2, existsStmt2.getPrimaryTable(), existsStmt2.getPrimaryTable().getTable().getIdMapping());
            existsStmt2.whereAnd(elemTblOwnerExpr.eq(existsOwnerExpr2), true);
            final int[] ownerColIndexes2 = sqlStmt.select(elemTblOwnerExpr, null);
            final StatementMappingIndex ownerMapIdx2 = new StatementMappingIndex(existsStmt2.getPrimaryTable().getTable().getIdMapping());
            ownerMapIdx2.setColumnPositions(ownerColIndexes2);
            iterStmt.setOwnerMapIndex(ownerMapIdx2);
        }
        else if (!(backingStore instanceof JoinMapStore)) {
            if (backingStore instanceof FKMapStore) {}
        }
        return iterStmt;
    }
    
    private void compileQueryToRetrieveCandidates(final Map parameters, final AbstractClassMetaData candidateCmd) {
        if (this.type != 0) {
            return;
        }
        final StatementClassMapping resultsDef = new StatementClassMapping();
        this.datastoreCompilation.setResultDefinitionForClass(resultsDef);
        SQLStatement stmt = null;
        try {
            stmt = RDBMSQueryUtils.getStatementForCandidates((RDBMSStoreManager)this.getStoreManager(), null, candidateCmd, this.datastoreCompilation.getResultDefinitionForClass(), this.ec, this.candidateClass, this.subclasses, this.result, null, null);
        }
        catch (NucleusException ne) {
            NucleusLogger.QUERY.warn("Query for candidates of " + this.candidateClass.getName() + (this.subclasses ? " and subclasses" : "") + " resulted in no possible candidates", ne);
            this.statementReturnsEmpty = true;
            return;
        }
        if (stmt.allUnionsForSamePrimaryTable()) {
            SQLStatementHelper.selectFetchPlanOfCandidateInStatement(stmt, this.datastoreCompilation.getResultDefinitionForClass(), candidateCmd, this.getFetchPlan(), 1);
        }
        else {
            SQLStatementHelper.selectIdentityOfCandidateInStatement(stmt, this.datastoreCompilation.getResultDefinitionForClass(), candidateCmd);
        }
        this.datastoreCompilation.setSQL(stmt.getSelectStatement().toString());
        this.datastoreCompilation.setStatementParameters(stmt.getSelectStatement().getParametersForStatement());
    }
    
    protected void compileQueryUpdate(final Map parameterValues, final AbstractClassMetaData candidateCmd) {
        final Expression[] updateExprs = this.compilation.getExprUpdate();
        if (updateExprs == null || updateExprs.length == 0) {
            return;
        }
        final RDBMSStoreManager storeMgr = (RDBMSStoreManager)this.getStoreManager();
        DatastoreClass candidateTbl = storeMgr.getDatastoreClass(candidateCmd.getFullClassName(), this.clr);
        if (candidateTbl == null) {
            throw new NucleusDataStoreException("Bulk update of " + candidateCmd.getFullClassName() + " not supported since candidate has no table of its own");
        }
        final InheritanceStrategy inhStr = candidateCmd.getBaseAbstractClassMetaData().getInheritanceMetaData().getStrategy();
        final List<BulkTable> tables = new ArrayList<BulkTable>();
        tables.add(new BulkTable(candidateTbl, true));
        if (inhStr != InheritanceStrategy.COMPLETE_TABLE) {
            while (candidateTbl.getSuperDatastoreClass() != null) {
                candidateTbl = candidateTbl.getSuperDatastoreClass();
                tables.add(new BulkTable(candidateTbl, false));
            }
        }
        final Collection<String> subclassNames = storeMgr.getSubClassesForClass(candidateCmd.getFullClassName(), true, this.clr);
        if (subclassNames != null && !subclassNames.isEmpty()) {
            for (final String subclassName : subclassNames) {
                final DatastoreClass subclassTbl = storeMgr.getDatastoreClass(subclassName, this.clr);
                if (candidateTbl != subclassTbl) {
                    tables.add(0, new BulkTable(subclassTbl, inhStr == InheritanceStrategy.COMPLETE_TABLE));
                }
            }
        }
        final List<String> sqls = new ArrayList<String>();
        final List<Boolean> sqlCountFlags = new ArrayList<Boolean>();
        for (final BulkTable bulkTable : tables) {
            final DatastoreClass table = bulkTable.table;
            final SQLStatement stmt = new SQLStatement(storeMgr, table, null, null);
            stmt.setClassLoaderResolver(this.clr);
            stmt.setCandidateClassName(candidateCmd.getFullClassName());
            if (table.getMultitenancyMapping() != null) {
                final JavaTypeMapping tenantMapping = table.getMultitenancyMapping();
                final SQLTable tenantSqlTbl = stmt.getPrimaryTable();
                final SQLExpression tenantExpr = stmt.getSQLExpressionFactory().newExpression(stmt, tenantSqlTbl, tenantMapping);
                final SQLExpression tenantVal = stmt.getSQLExpressionFactory().newLiteral(stmt, tenantMapping, storeMgr.getStringProperty("datanucleus.TenantID"));
                stmt.whereAnd(tenantExpr.eq(tenantVal), true);
            }
            final Set<String> options = new HashSet<String>();
            options.add("CASE_INSENSITIVE");
            options.add("EXPLICIT_JOINS");
            final QueryToSQLMapper sqlMapper = new QueryToSQLMapper(stmt, this.compilation, parameterValues, null, null, candidateCmd, this.getFetchPlan(), this.ec, null, options, this.extensions);
            sqlMapper.setDefaultJoinType(SQLJoin.JoinType.INNER_JOIN);
            sqlMapper.compile();
            if (stmt.hasUpdates()) {
                sqls.add(stmt.getUpdateStatement().toString());
                sqlCountFlags.add(bulkTable.useInCount);
                this.datastoreCompilation.setStatementParameters(stmt.getSelectStatement().getParametersForStatement());
            }
        }
        if (sqls.size() == 1) {
            this.datastoreCompilation.setSQL(sqls.get(0));
        }
        else {
            this.datastoreCompilation.setSQL(sqls, sqlCountFlags);
        }
    }
    
    protected void compileQueryDelete(final Map parameterValues, final AbstractClassMetaData candidateCmd) {
        final RDBMSStoreManager storeMgr = (RDBMSStoreManager)this.getStoreManager();
        DatastoreClass candidateTbl = storeMgr.getDatastoreClass(candidateCmd.getFullClassName(), this.clr);
        if (candidateTbl == null) {
            throw new NucleusDataStoreException("Bulk delete of " + candidateCmd.getFullClassName() + " not supported since candidate has no table of its own");
        }
        final InheritanceStrategy inhStr = candidateCmd.getBaseAbstractClassMetaData().getInheritanceMetaData().getStrategy();
        final List<BulkTable> tables = new ArrayList<BulkTable>();
        tables.add(new BulkTable(candidateTbl, true));
        if (inhStr != InheritanceStrategy.COMPLETE_TABLE) {
            while (candidateTbl.getSuperDatastoreClass() != null) {
                candidateTbl = candidateTbl.getSuperDatastoreClass();
                tables.add(new BulkTable(candidateTbl, false));
            }
        }
        final Collection<String> subclassNames = storeMgr.getSubClassesForClass(candidateCmd.getFullClassName(), true, this.clr);
        if (subclassNames != null && !subclassNames.isEmpty()) {
            for (final String subclassName : subclassNames) {
                final DatastoreClass subclassTbl = storeMgr.getDatastoreClass(subclassName, this.clr);
                if (candidateTbl != subclassTbl) {
                    tables.add(0, new BulkTable(subclassTbl, inhStr == InheritanceStrategy.COMPLETE_TABLE));
                }
            }
        }
        final List<String> sqls = new ArrayList<String>();
        final List<Boolean> sqlCountFlags = new ArrayList<Boolean>();
        for (final BulkTable bulkTable : tables) {
            final DatastoreClass table = bulkTable.table;
            final SQLStatement stmt = new SQLStatement(storeMgr, table, null, null);
            stmt.setClassLoaderResolver(this.clr);
            stmt.setCandidateClassName(candidateCmd.getFullClassName());
            if (table.getMultitenancyMapping() != null) {
                final JavaTypeMapping tenantMapping = table.getMultitenancyMapping();
                final SQLTable tenantSqlTbl = stmt.getPrimaryTable();
                final SQLExpression tenantExpr = stmt.getSQLExpressionFactory().newExpression(stmt, tenantSqlTbl, tenantMapping);
                final SQLExpression tenantVal = stmt.getSQLExpressionFactory().newLiteral(stmt, tenantMapping, storeMgr.getStringProperty("datanucleus.TenantID"));
                stmt.whereAnd(tenantExpr.eq(tenantVal), true);
            }
            final Set<String> options = new HashSet<String>();
            options.add("BULK_DELETE_NO_RESULT");
            final QueryToSQLMapper sqlMapper = new QueryToSQLMapper(stmt, this.compilation, parameterValues, null, null, candidateCmd, this.getFetchPlan(), this.ec, null, options, this.extensions);
            sqlMapper.setDefaultJoinType(SQLJoin.JoinType.INNER_JOIN);
            sqlMapper.compile();
            sqls.add(stmt.getDeleteStatement().toString());
            sqlCountFlags.add(bulkTable.useInCount);
            this.datastoreCompilation.setStatementParameters(stmt.getDeleteStatement().getParametersForStatement());
        }
        if (sqls.size() == 1) {
            this.datastoreCompilation.setSQL(sqls.get(0));
        }
        else {
            this.datastoreCompilation.setSQL(sqls, sqlCountFlags);
        }
    }
    
    @Override
    public Set<String> getSupportedExtensions() {
        final Set<String> supported = super.getSupportedExtensions();
        supported.add("datanucleus.rdbms.query.resultSetType");
        supported.add("datanucleus.rdbms.query.resultSetConcurrency");
        supported.add("datanucleus.rdbms.query.fetchDirection");
        return supported;
    }
    
    @Override
    public boolean processesRangeInDatastoreQuery() {
        if (this.range == null) {
            return true;
        }
        final RDBMSStoreManager storeMgr = (RDBMSStoreManager)this.getStoreManager();
        final DatastoreAdapter dba = storeMgr.getDatastoreAdapter();
        final boolean using_limit_where_clause = dba.getRangeByLimitEndOfStatementClause(this.fromInclNo, this.toExclNo).length() > 0;
        final boolean using_rownum = dba.getRangeByRowNumberColumn().length() > 0 || dba.getRangeByRowNumberColumn2().length() > 0;
        return using_limit_where_clause || using_rownum;
    }
    
    @Override
    public void addExtension(final String key, final Object value) {
        if (key != null && key.equals("datanucleus.query.evaluateInMemory")) {
            this.datastoreCompilation = null;
            this.getQueryManager().deleteDatastoreQueryCompilation(this.getStoreManager().getQueryCacheKey(), this.getLanguage(), this.toString());
        }
        super.addExtension(key, value);
    }
    
    @Override
    public void setExtensions(final Map extensions) {
        if (extensions != null && extensions.containsKey("datanucleus.query.evaluateInMemory")) {
            this.datastoreCompilation = null;
            this.getQueryManager().deleteDatastoreQueryCompilation(this.getStoreManager().getQueryCacheKey(), this.getLanguage(), this.toString());
        }
        super.setExtensions(extensions);
    }
    
    @Override
    public Object getNativeQuery() {
        if (this.datastoreCompilation != null) {
            return this.datastoreCompilation.getSQL();
        }
        return super.getNativeQuery();
    }
    
    private class BulkTable
    {
        DatastoreClass table;
        boolean useInCount;
        
        public BulkTable(final DatastoreClass tbl, final boolean useInCount) {
            this.table = tbl;
            this.useInCount = useInCount;
        }
        
        @Override
        public String toString() {
            return this.table.toString();
        }
    }
}
