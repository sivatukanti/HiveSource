// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.query;

import org.datanucleus.util.Localiser;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.InheritanceStrategy;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.store.rdbms.sql.expression.BooleanExpression;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
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
import java.util.Set;
import org.datanucleus.store.types.SCOUtils;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.store.rdbms.sql.SQLJoin;
import org.datanucleus.util.Imports;
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
import org.datanucleus.query.evaluator.JPQLEvaluator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.datanucleus.store.query.QueryManager;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.ClassLoaderResolver;
import java.security.AccessController;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import org.datanucleus.metadata.AbstractMemberMetaData;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.query.QueryUtils;
import java.security.PrivilegedAction;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import java.util.Map;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.query.AbstractJPQLQuery;

public class JPQLQuery extends AbstractJPQLQuery
{
    protected transient RDBMSQueryCompilation datastoreCompilation;
    
    public JPQLQuery(final StoreManager storeMgr, final ExecutionContext ec) {
        this(storeMgr, ec, (JPQLQuery)null);
    }
    
    public JPQLQuery(final StoreManager storeMgr, final ExecutionContext ec, final JPQLQuery q) {
        super(storeMgr, ec, q);
    }
    
    public JPQLQuery(final StoreManager storeMgr, final ExecutionContext ec, final String query) {
        super(storeMgr, ec, query);
    }
    
    @Override
    public void setImplicitParameter(final int position, final Object value) {
        if (this.datastoreCompilation != null && !this.datastoreCompilation.isPrecompilable()) {
            this.datastoreCompilation = null;
        }
        super.setImplicitParameter(position, value);
    }
    
    @Override
    public void setImplicitParameter(final String name, final Object value) {
        if (this.datastoreCompilation != null && !this.datastoreCompilation.isPrecompilable()) {
            this.datastoreCompilation = null;
        }
        super.setImplicitParameter(name, value);
    }
    
    @Override
    protected void discardCompiled() {
        super.discardCompiled();
        this.datastoreCompilation = null;
    }
    
    @Override
    protected boolean isCompiled() {
        if (this.candidateCollection != null) {
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
        if (this.candidateCollection != null) {
            return;
        }
        if (this.candidateClass == null || this.candidateClassName == null) {
            this.candidateClass = this.compilation.getCandidateClass();
            this.candidateClassName = this.candidateClass.getName();
        }
        final RDBMSStoreManager storeMgr = (RDBMSStoreManager)this.getStoreManager();
        final ClassLoaderResolver clr = this.ec.getClassLoaderResolver();
        final AbstractClassMetaData acmd = this.ec.getMetaDataManager().getMetaDataForClass(this.candidateClass, clr);
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
            this.datastoreCompilation = new RDBMSQueryCompilation();
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
                            if (mmd != null && (mmd.hasCollection() || mmd.hasMap() || mmd.hasArray())) {
                                throw new NucleusUserException(JPQLQuery.LOCALISER.msg("021213"));
                            }
                        }
                    }
                }
            }
            if (this.resultClass != null && this.result != null) {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                    @Override
                    public Object run() {
                        final StatementResultMapping resultMapping = JPQLQuery.this.datastoreCompilation.getResultDefinition();
                        if (QueryUtils.resultClassIsSimple(JPQLQuery.this.resultClass.getName())) {
                            if (resultMapping.getNumberOfResultExpressions() > 1) {
                                throw new NucleusUserException(JPQLQuery.LOCALISER.msg("021201", JPQLQuery.this.resultClass.getName()));
                            }
                            final Object stmtMap = resultMapping.getMappingForResultExpression(0);
                            final StatementMappingIndex idx = (StatementMappingIndex)stmtMap;
                            final Class exprType = idx.getMapping().getJavaType();
                            boolean typeConsistent = false;
                            if (exprType == JPQLQuery.this.resultClass) {
                                typeConsistent = true;
                            }
                            else if (exprType.isPrimitive()) {
                                final Class resultClassPrimitive = ClassUtils.getPrimitiveTypeForType(JPQLQuery.this.resultClass);
                                if (resultClassPrimitive == exprType) {
                                    typeConsistent = true;
                                }
                            }
                            if (!typeConsistent) {
                                throw new NucleusUserException(JPQLQuery.LOCALISER.msg("021202", JPQLQuery.this.resultClass.getName(), exprType));
                            }
                        }
                        else if (QueryUtils.resultClassIsUserType(JPQLQuery.this.resultClass.getName())) {
                            final Class[] ctrTypes = new Class[resultMapping.getNumberOfResultExpressions()];
                            for (int i = 0; i < ctrTypes.length; ++i) {
                                final Object stmtMap2 = resultMapping.getMappingForResultExpression(i);
                                if (stmtMap2 instanceof StatementMappingIndex) {
                                    ctrTypes[i] = ((StatementMappingIndex)stmtMap2).getMapping().getJavaType();
                                }
                                else if (stmtMap2 instanceof StatementNewObjectMapping) {}
                            }
                            final Constructor ctr = ClassUtils.getConstructorWithArguments(JPQLQuery.this.resultClass, ctrTypes);
                            if (ctr == null && !ClassUtils.hasDefaultConstructor(JPQLQuery.this.resultClass)) {
                                throw new NucleusUserException(JPQLQuery.LOCALISER.msg("021205", JPQLQuery.this.resultClass.getName()));
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
                                                final Field fld = JPQLQuery.this.resultClass.getDeclaredField(fieldName);
                                                resultFieldType = fld.getType();
                                                if (!ClassUtils.typesAreCompatible(fieldType, resultFieldType) && !ClassUtils.typesAreCompatible(resultFieldType, fieldType)) {
                                                    throw new NucleusUserException(JPQLQuery.LOCALISER.msg("021211", fieldName, fieldType.getName(), resultFieldType.getName()));
                                                }
                                                if (!Modifier.isPublic(fld.getModifiers())) {
                                                    publicField = false;
                                                }
                                            }
                                            catch (NoSuchFieldException nsfe) {
                                                publicField = false;
                                            }
                                            if (!publicField) {
                                                final Method setMethod = QueryUtils.getPublicSetMethodForFieldOfResultClass(JPQLQuery.this.resultClass, fieldName, resultFieldType);
                                                if (setMethod == null) {
                                                    final Method putMethod = QueryUtils.getPublicPutMethodForResultClass(JPQLQuery.this.resultClass);
                                                    if (putMethod == null) {
                                                        throw new NucleusUserException(JPQLQuery.LOCALISER.msg("021212", JPQLQuery.this.resultClass.getName(), fieldName));
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
            if (NucleusLogger.QUERY.isDebugEnabled()) {
                NucleusLogger.QUERY.debug(JPQLQuery.LOCALISER.msg("021085", this, this.datastoreCompilation.getSQL()));
            }
            boolean hasParams = false;
            if (this.explicitParameters != null) {
                hasParams = true;
            }
            else if (parameterValues != null && parameterValues.size() > 0) {
                hasParams = true;
            }
            if (!this.datastoreCompilation.isPrecompilable() || (this.datastoreCompilation.getSQL().indexOf(63) < 0 && hasParams)) {
                NucleusLogger.QUERY.debug(JPQLQuery.LOCALISER.msg("021075"));
            }
            else if (this.useCaching() && queryCacheKey != null) {
                qm.addDatastoreQueryCompilation(datastoreKey, this.getLanguage(), queryCacheKey, this.datastoreCompilation);
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
        if (this.candidateCollection != null) {
            if (this.candidateCollection.isEmpty()) {
                return Collections.EMPTY_LIST;
            }
            final List candidates = new ArrayList(this.candidateCollection);
            final JavaQueryEvaluator resultMapper = new JPQLEvaluator(this, candidates, this.compilation, parameters, this.clr);
            return resultMapper.execute(true, true, true, true, true);
        }
        else {
            if (this.type == 0) {
                final List<Object> cachedResults = this.getQueryManager().getDatastoreQueryResult(this, parameters);
                if (cachedResults != null) {
                    return new CandidateIdsQueryResult(this, cachedResults);
                }
            }
            Object results = null;
            final ManagedConnection mconn = this.getStoreManager().getConnection(this.ec);
            try {
                final long startTime = System.currentTimeMillis();
                if (NucleusLogger.QUERY.isDebugEnabled()) {
                    NucleusLogger.QUERY.debug(JPQLQuery.LOCALISER.msg("021046", this.getLanguage(), this.getSingleStringQuery(), null));
                }
                final RDBMSStoreManager storeMgr = (RDBMSStoreManager)this.getStoreManager();
                final AbstractClassMetaData acmd = this.ec.getMetaDataManager().getMetaDataForClass(this.candidateClass, this.clr);
                final SQLController sqlControl = storeMgr.getSQLController();
                PreparedStatement ps = null;
                try {
                    if (this.type == 0) {
                        ps = RDBMSQueryUtils.getPreparedStatementForQuery(mconn, this.datastoreCompilation.getSQL(), this);
                        SQLStatementHelper.applyParametersToStatement(ps, this.ec, this.datastoreCompilation.getStatementParameters(), null, parameters);
                        RDBMSQueryUtils.prepareStatementForExecution(ps, this, false);
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
                            if (this.evaluateInMemory()) {
                                final ResultObjectFactory rof = storeMgr.newResultObjectFactory(acmd, this.datastoreCompilation.getResultDefinitionForClass(), this.ignoreCache, this.getFetchPlan(), this.candidateClass);
                                final List candidates2 = new ArrayList();
                                while (rs.next()) {
                                    candidates2.add(rof.getObject(this.ec, rs));
                                }
                                final JavaQueryEvaluator resultMapper2 = new JPQLEvaluator(this, candidates2, this.compilation, parameters, this.clr);
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
                                        NucleusLogger.DATASTORE_RETRIEVE.debug(">> JPQL Bulk-Fetch of " + iterStmt.getBackingStore().getOwnerMemberMetaData().getFullFieldName());
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
                                            throw new NucleusDataStoreException(JPQLQuery.LOCALISER.msg("056006", iterStmtSQL), e);
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
                                        if (!JPQLQuery.this.ec.getTransaction().isActive()) {
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
                    else if (this.type == 1 || this.type == 2) {
                        long bulkResult = 0L;
                        if (this.datastoreCompilation.getSQL() != null) {
                            ps = sqlControl.getStatementForUpdate(mconn, this.datastoreCompilation.getSQL(), false);
                            SQLStatementHelper.applyParametersToStatement(ps, this.ec, this.datastoreCompilation.getStatementParameters(), null, parameters);
                            RDBMSQueryUtils.prepareStatementForExecution(ps, this, false);
                            final int[] execResult = sqlControl.executeStatementUpdate(this.ec, mconn, this.toString(), ps, true);
                            bulkResult = execResult[0];
                        }
                        else {
                            final List<String> sqls = this.datastoreCompilation.getSQLs();
                            final List<Boolean> sqlUseInCountFlags = this.datastoreCompilation.getSQLUseInCountFlags();
                            final Iterator<Boolean> sqlUseInCountIter = sqlUseInCountFlags.iterator();
                            for (final String sql : sqls) {
                                final Boolean useInCount = sqlUseInCountIter.next();
                                ps = sqlControl.getStatementForUpdate(mconn, sql, false);
                                SQLStatementHelper.applyParametersToStatement(ps, this.ec, this.datastoreCompilation.getStatementParameters(), null, parameters);
                                RDBMSQueryUtils.prepareStatementForExecution(ps, this, false);
                                final int[] execResults = sqlControl.executeStatementUpdate(this.ec, mconn, this.toString(), ps, true);
                                if (useInCount) {
                                    bulkResult += execResults[0];
                                }
                            }
                        }
                        try {
                            this.ec.getNucleusContext().getLevel2Cache().evictAll(this.candidateClass, this.subclasses);
                        }
                        catch (UnsupportedOperationException ex) {}
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
                    throw new NucleusException(JPQLQuery.LOCALISER.msg("021042"), sqle);
                }
                if (NucleusLogger.QUERY.isDebugEnabled()) {
                    NucleusLogger.QUERY.debug(JPQLQuery.LOCALISER.msg("021074", this.getLanguage(), "" + (System.currentTimeMillis() - startTime)));
                }
                return results;
            }
            finally {
                mconn.release();
            }
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
        if (this.candidateCollection != null) {
            return;
        }
        long startTime = 0L;
        if (NucleusLogger.QUERY.isDebugEnabled()) {
            startTime = System.currentTimeMillis();
            NucleusLogger.QUERY.debug(JPQLQuery.LOCALISER.msg("021083", this.getLanguage(), this.toString()));
        }
        if (this.result != null) {
            this.datastoreCompilation.setResultDefinition(new StatementResultMapping());
        }
        else {
            this.datastoreCompilation.setResultDefinitionForClass(new StatementClassMapping());
        }
        final SQLStatement stmt = RDBMSQueryUtils.getStatementForCandidates((RDBMSStoreManager)this.getStoreManager(), null, candidateCmd, this.datastoreCompilation.getResultDefinitionForClass(), this.ec, this.candidateClass, this.subclasses, this.result, this.compilation.getCandidateAlias(), this.compilation.getCandidateAlias());
        final Set<String> options = new HashSet<String>();
        options.add("CASE_INSENSITIVE");
        options.add("EXPLICIT_JOINS");
        final QueryToSQLMapper sqlMapper = new QueryToSQLMapper(stmt, this.compilation, parameters, this.datastoreCompilation.getResultDefinitionForClass(), this.datastoreCompilation.getResultDefinition(), candidateCmd, this.getFetchPlan(), this.ec, null, options, this.extensions);
        sqlMapper.setDefaultJoinType(SQLJoin.JoinType.INNER_JOIN);
        sqlMapper.compile();
        this.datastoreCompilation.setParameterNameByPosition(sqlMapper.getParameterNameByPosition());
        this.datastoreCompilation.setPrecompilable(sqlMapper.isPrecompilable());
        if (this.range != null) {
            long lower = this.fromInclNo;
            long upper = this.toExclNo;
            if (this.fromInclParam != null) {
                lower = parameters.get(this.fromInclParam).longValue();
            }
            if (this.toExclParam != null) {
                upper = parameters.get(this.toExclParam).longValue();
            }
            long count = upper - lower;
            if (upper == Long.MAX_VALUE) {
                count = -1L;
            }
            stmt.setRange(lower, count);
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
            NucleusLogger.QUERY.debug(JPQLQuery.LOCALISER.msg("021084", this.getLanguage(), System.currentTimeMillis() - startTime));
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
        if (this.candidateCollection != null) {
            return;
        }
        final StatementClassMapping resultsDef = new StatementClassMapping();
        this.datastoreCompilation.setResultDefinitionForClass(resultsDef);
        final SQLStatement stmt = RDBMSQueryUtils.getStatementForCandidates((RDBMSStoreManager)this.getStoreManager(), null, candidateCmd, this.datastoreCompilation.getResultDefinitionForClass(), this.ec, this.candidateClass, this.subclasses, this.result, null, null);
        if (stmt.allUnionsForSamePrimaryTable()) {
            SQLStatementHelper.selectFetchPlanOfCandidateInStatement(stmt, this.datastoreCompilation.getResultDefinitionForClass(), candidateCmd, this.getFetchPlan(), 1);
        }
        else {
            SQLStatementHelper.selectIdentityOfCandidateInStatement(stmt, this.datastoreCompilation.getResultDefinitionForClass(), candidateCmd);
        }
        this.datastoreCompilation.setSQL(stmt.getSelectStatement().toString());
        this.datastoreCompilation.setStatementParameters(stmt.getSelectStatement().getParametersForStatement());
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
            options.add("CASE_INSENSITIVE");
            options.add("EXPLICIT_JOINS");
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
