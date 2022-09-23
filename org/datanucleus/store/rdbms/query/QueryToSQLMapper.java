// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.query;

import org.datanucleus.store.rdbms.sql.expression.BooleanLiteral;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.rdbms.sql.expression.StringSubqueryExpression;
import org.datanucleus.store.rdbms.mapping.java.StringMapping;
import org.datanucleus.store.rdbms.sql.expression.TemporalSubqueryExpression;
import org.datanucleus.store.rdbms.mapping.java.TemporalMapping;
import java.lang.reflect.Constructor;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.store.rdbms.mapping.java.OIDMapping;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.store.rdbms.sql.expression.NumericSubqueryExpression;
import org.datanucleus.store.rdbms.sql.expression.SubqueryExpressionComponent;
import org.datanucleus.store.rdbms.sql.expression.BooleanSubqueryExpression;
import org.datanucleus.query.expression.SubqueryExpression;
import java.util.StringTokenizer;
import java.util.ArrayList;
import org.datanucleus.query.expression.ArrayExpression;
import org.datanucleus.store.rdbms.mapping.java.PersistableIdMapping;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import org.datanucleus.metadata.FieldPersistenceModifier;
import org.datanucleus.metadata.DiscriminatorMetaData;
import org.datanucleus.metadata.DiscriminatorStrategy;
import org.datanucleus.store.rdbms.mapping.java.EmbeddedMapping;
import org.datanucleus.store.rdbms.sql.expression.ExpressionUtils;
import org.datanucleus.store.rdbms.sql.expression.ParameterLiteral;
import java.util.List;
import org.datanucleus.FetchGroupManager;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.store.rdbms.table.CollectionTable;
import org.datanucleus.FetchGroup;
import org.datanucleus.query.expression.JoinExpression;
import org.datanucleus.store.rdbms.table.ElementContainerTable;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.util.StringUtils;
import org.datanucleus.query.NullOrderingType;
import org.datanucleus.query.expression.OrderExpression;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.mapping.java.AbstractContainerMapping;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.metadata.VersionMetaData;
import org.datanucleus.store.rdbms.sql.expression.ColumnExpression;
import org.datanucleus.metadata.InheritanceStrategy;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.store.rdbms.sql.expression.NewObjectExpression;
import org.datanucleus.query.expression.CreatorExpression;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.rdbms.sql.expression.UnboundExpression;
import org.datanucleus.query.expression.VariableExpression;
import org.datanucleus.store.rdbms.sql.expression.SQLLiteral;
import org.datanucleus.store.rdbms.sql.SQLStatementHelper;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.store.rdbms.sql.expression.TemporalLiteral;
import org.datanucleus.store.VersionHelper;
import org.datanucleus.store.rdbms.sql.expression.IntegerLiteral;
import org.datanucleus.store.rdbms.sql.expression.NumericExpression;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.VersionStrategy;
import org.datanucleus.store.rdbms.table.ClassTable;
import org.datanucleus.query.expression.CaseExpression;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.query.expression.DyadicExpression;
import org.datanucleus.query.expression.ExpressionEvaluator;
import org.datanucleus.store.rdbms.sql.expression.BooleanExpression;
import org.datanucleus.query.QueryUtils;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.expression.ClassExpression;
import org.datanucleus.query.symbol.Symbol;
import java.util.Iterator;
import org.datanucleus.store.query.QueryCompilerSyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.HashMap;
import org.datanucleus.store.rdbms.sql.SQLJoin;
import java.util.Set;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import java.util.Stack;
import org.datanucleus.query.compiler.CompilationComponent;
import org.datanucleus.util.Imports;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.rdbms.sql.expression.SQLExpressionFactory;
import org.datanucleus.FetchPlan;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import java.util.Map;
import org.datanucleus.query.compiler.QueryCompilation;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.util.Localiser;
import org.datanucleus.query.evaluator.AbstractExpressionEvaluator;

public class QueryToSQLMapper extends AbstractExpressionEvaluator implements QueryGenerator
{
    protected static final Localiser LOCALISER;
    public static final String OPTION_CASE_INSENSITIVE = "CASE_INSENSITIVE";
    public static final String OPTION_EXPLICIT_JOINS = "EXPLICIT_JOINS";
    public static final String OPTION_BULK_UPDATE_VERSION = "BULK_UPDATE_VERSION";
    public static final String OPTION_BULK_DELETE_NO_RESULT = "BULK_DELETE_NO_RESULT";
    public static final String OPTION_SELECT_CANDIDATE_ID_ONLY = "RESULT_CANDIDATE_ID";
    final String candidateAlias;
    final AbstractClassMetaData candidateCmd;
    final QueryCompilation compilation;
    final Map parameters;
    Map<String, Object> parameterValueByName;
    Map<Integer, String> paramNameByPosition;
    int positionalParamNumber;
    Map<String, Object> extensionsByName;
    final SQLStatement stmt;
    final StatementClassMapping resultDefinitionForClass;
    final StatementResultMapping resultDefinition;
    Map<Object, SQLExpression> expressionForParameter;
    final RDBMSStoreManager storeMgr;
    final FetchPlan fetchPlan;
    final SQLExpressionFactory exprFactory;
    ExecutionContext ec;
    ClassLoaderResolver clr;
    Imports importsDefinition;
    Map<String, Object> compileProperties;
    CompilationComponent compileComponent;
    Stack<SQLExpression> stack;
    Map<String, SQLTableMapping> sqlTableByPrimary;
    Map<String, String> explicitJoinPrimaryByAlias;
    Map<String, JavaTypeMapping> paramMappingForName;
    Set<String> options;
    public QueryToSQLMapper parentMapper;
    SQLJoin.JoinType defaultJoinType;
    boolean precompilable;
    
    public QueryToSQLMapper(final SQLStatement stmt, final QueryCompilation compilation, final Map parameters, final StatementClassMapping resultDefForClass, final StatementResultMapping resultDef, final AbstractClassMetaData cmd, final FetchPlan fetchPlan, final ExecutionContext ec, final Imports importsDefinition, final Set<String> options, final Map<String, Object> extensions) {
        this.parameterValueByName = null;
        this.paramNameByPosition = null;
        this.positionalParamNumber = -1;
        this.extensionsByName = null;
        this.importsDefinition = null;
        this.compileProperties = new HashMap<String, Object>();
        this.stack = new Stack<SQLExpression>();
        this.sqlTableByPrimary = new HashMap<String, SQLTableMapping>();
        this.explicitJoinPrimaryByAlias = null;
        this.paramMappingForName = new HashMap<String, JavaTypeMapping>();
        this.options = new HashSet<String>();
        this.parentMapper = null;
        this.defaultJoinType = null;
        this.precompilable = true;
        this.parameters = parameters;
        this.compilation = compilation;
        this.stmt = stmt;
        this.resultDefinitionForClass = resultDefForClass;
        this.resultDefinition = resultDef;
        this.candidateAlias = compilation.getCandidateAlias();
        this.fetchPlan = fetchPlan;
        this.storeMgr = stmt.getRDBMSManager();
        this.exprFactory = stmt.getRDBMSManager().getSQLExpressionFactory();
        this.candidateCmd = cmd;
        this.ec = ec;
        this.clr = ec.getClassLoaderResolver();
        this.importsDefinition = importsDefinition;
        if (options != null) {
            this.options.addAll(options);
        }
        this.extensionsByName = extensions;
        this.stmt.setQueryGenerator(this);
        final SQLTableMapping tblMapping = new SQLTableMapping(stmt.getPrimaryTable(), this.candidateCmd, stmt.getPrimaryTable().getTable().getIdMapping());
        this.setSQLTableMappingForAlias(this.candidateAlias, tblMapping);
    }
    
    void setDefaultJoinType(final SQLJoin.JoinType joinType) {
        this.defaultJoinType = joinType;
    }
    
    void setParentMapper(final QueryToSQLMapper parent) {
        this.parentMapper = parent;
    }
    
    @Override
    public String getQueryLanguage() {
        return this.compilation.getQueryLanguage();
    }
    
    @Override
    public ClassLoaderResolver getClassLoaderResolver() {
        return this.clr;
    }
    
    @Override
    public CompilationComponent getCompilationComponent() {
        return this.compileComponent;
    }
    
    @Override
    public ExecutionContext getExecutionContext() {
        return this.ec;
    }
    
    @Override
    public Object getProperty(final String name) {
        return this.compileProperties.get(name);
    }
    
    public boolean isPrecompilable() {
        return this.precompilable;
    }
    
    protected void setNotPrecompilable() {
        if (this.parentMapper != null) {
            this.parentMapper.setNotPrecompilable();
        }
        this.precompilable = false;
    }
    
    public Map<Integer, String> getParameterNameByPosition() {
        return this.paramNameByPosition;
    }
    
    public void compile() {
        this.compileFrom();
        this.compileFilter();
        if (this.compilation.getResultDistinct()) {
            this.stmt.setDistinct(true);
        }
        else if (!this.options.contains("EXPLICIT_JOINS") && this.compilation.getExprResult() == null && this.stmt.getNumberOfTableGroups() > 1) {
            this.stmt.setDistinct(true);
        }
        this.compileResult();
        this.compileGrouping();
        this.compileHaving();
        this.compileOrdering();
        final Collection<String> symbols = this.compilation.getSymbolTable().getSymbolNames();
        final Iterator<String> symIter = symbols.iterator();
        while (symIter.hasNext()) {
            final Symbol sym = this.compilation.getSymbolTable().getSymbol(symIter.next());
            if (sym.getType() == 2 && this.compilation.getCompilationForSubquery(sym.getQualifiedName()) == null && !this.hasSQLTableMappingForAlias(sym.getQualifiedName())) {
                throw new QueryCompilerSyntaxException("Query has variable \"" + sym.getQualifiedName() + "\" which is not bound to the query");
            }
        }
    }
    
    protected void compileFrom() {
        if (this.compilation.getExprFrom() != null) {
            this.compileComponent = CompilationComponent.FROM;
            final Expression[] fromExprs = this.compilation.getExprFrom();
            for (int i = 0; i < fromExprs.length; ++i) {
                final ClassExpression clsExpr = (ClassExpression)fromExprs[i];
                this.compileFromClassExpression(clsExpr);
            }
            this.compileComponent = null;
        }
    }
    
    protected void compileFilter() {
        if (this.compilation.getExprFilter() != null) {
            this.compileComponent = CompilationComponent.FILTER;
            if (QueryUtils.expressionHasOrOperator(this.compilation.getExprFilter())) {
                this.compileProperties.put("Filter.OR", true);
            }
            if (QueryUtils.expressionHasNotOperator(this.compilation.getExprFilter())) {
                this.compileProperties.put("Filter.NOT", true);
            }
            BooleanExpression filterExpr = (BooleanExpression)this.compilation.getExprFilter().evaluate(this);
            filterExpr = this.getBooleanExpressionForUseInFilter(filterExpr);
            this.stmt.whereAnd(filterExpr, true);
            this.compileComponent = null;
        }
    }
    
    protected void compileResult() {
        if (this.compilation.getExprUpdate() != null) {
            this.compileComponent = CompilationComponent.UPDATE;
            final Expression[] updateExprs = this.compilation.getExprUpdate();
            SQLExpression[] updateSqlExprs = new SQLExpression[updateExprs.length];
            boolean performingUpdate = false;
            for (int i = 0; i < updateExprs.length; ++i) {
                final DyadicExpression updateExpr = (DyadicExpression)updateExprs[i];
                SQLExpression leftSqlExpr = null;
                if (!(updateExpr.getLeft() instanceof PrimaryExpression)) {
                    throw new NucleusException("Dont currently support update clause containing left expression of type " + updateExpr.getLeft());
                }
                this.processPrimaryExpression((PrimaryExpression)updateExpr.getLeft());
                leftSqlExpr = this.stack.pop();
                if (leftSqlExpr.getSQLTable() != this.stmt.getPrimaryTable()) {
                    leftSqlExpr = null;
                }
                if (leftSqlExpr != null) {
                    if (!this.stmt.getDatastoreAdapter().supportsOption("TableAliasInUpdateSet")) {
                        for (int j = 0; j < leftSqlExpr.getNumberOfSubExpressions(); ++j) {
                            final ColumnExpression colExpr = leftSqlExpr.getSubExpression(j);
                            colExpr.setOmitTableFromString(true);
                        }
                    }
                    performingUpdate = true;
                    SQLExpression rightSqlExpr = null;
                    if (updateExpr.getRight() instanceof Literal) {
                        this.processLiteral((Literal)updateExpr.getRight());
                        rightSqlExpr = this.stack.pop();
                    }
                    else if (updateExpr.getRight() instanceof ParameterExpression) {
                        this.processParameterExpression((ParameterExpression)updateExpr.getRight());
                        rightSqlExpr = this.stack.pop();
                    }
                    else if (updateExpr.getRight() instanceof PrimaryExpression) {
                        this.processPrimaryExpression((PrimaryExpression)updateExpr.getRight());
                        rightSqlExpr = this.stack.pop();
                    }
                    else if (updateExpr.getRight() instanceof DyadicExpression) {
                        updateExpr.getRight().evaluate(this);
                        rightSqlExpr = this.stack.pop();
                    }
                    else {
                        if (!(updateExpr.getRight() instanceof CaseExpression)) {
                            throw new NucleusException("Dont currently support update clause containing right expression of type " + updateExpr.getRight());
                        }
                        updateExpr.getRight().evaluate(this);
                        rightSqlExpr = this.stack.pop();
                    }
                    if (rightSqlExpr != null) {
                        updateSqlExprs[i] = leftSqlExpr.eq(rightSqlExpr);
                    }
                }
            }
            if (this.candidateCmd.isVersioned() && this.options.contains("BULK_UPDATE_VERSION")) {
                SQLExpression updateSqlExpr = null;
                final ClassTable table = (ClassTable)this.stmt.getPrimaryTable().getTable();
                final JavaTypeMapping verMapping = table.getVersionMapping(true);
                final ClassTable verTable = table.getTableManagingMapping(verMapping);
                if (verTable == this.stmt.getPrimaryTable().getTable()) {
                    final VersionMetaData vermd = this.candidateCmd.getVersionMetaDataForClass();
                    if (vermd.getVersionStrategy() == VersionStrategy.VERSION_NUMBER) {
                        final SQLTable verSqlTbl = this.stmt.getTable(verTable, this.stmt.getPrimaryTable().getGroupName());
                        final SQLExpression verExpr = new NumericExpression(this.stmt, verSqlTbl, verMapping);
                        final SQLExpression incrExpr = verExpr.add(new IntegerLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Integer.class, false), 1, null));
                        updateSqlExpr = verExpr.eq(incrExpr);
                        final SQLExpression[] oldArray = updateSqlExprs;
                        updateSqlExprs = new SQLExpression[oldArray.length + 1];
                        System.arraycopy(oldArray, 0, updateSqlExprs, 0, oldArray.length);
                        updateSqlExprs[oldArray.length] = updateSqlExpr;
                        performingUpdate = true;
                    }
                    else if (vermd.getVersionStrategy() == VersionStrategy.DATE_TIME) {
                        final SQLTable verSqlTbl = this.stmt.getTable(verTable, this.stmt.getPrimaryTable().getGroupName());
                        final SQLExpression verExpr = new NumericExpression(this.stmt, verSqlTbl, verMapping);
                        final Object newVersion = VersionHelper.getNextVersion(vermd.getVersionStrategy(), null);
                        final JavaTypeMapping valMapping = this.stmt.getSQLExpressionFactory().getMappingForType(newVersion.getClass(), false);
                        final SQLExpression valExpr = new TemporalLiteral(this.stmt, valMapping, newVersion, null);
                        updateSqlExpr = verExpr.eq(valExpr);
                        final SQLExpression[] oldArray2 = updateSqlExprs;
                        updateSqlExprs = new SQLExpression[oldArray2.length + 1];
                        System.arraycopy(oldArray2, 0, updateSqlExprs, 0, oldArray2.length);
                        updateSqlExprs[oldArray2.length] = updateSqlExpr;
                        performingUpdate = true;
                    }
                }
            }
            if (performingUpdate) {
                this.stmt.setUpdates(updateSqlExprs);
            }
        }
        else if (this.compilation.getExprResult() != null) {
            this.compileComponent = CompilationComponent.RESULT;
            final Expression[] resultExprs = this.compilation.getExprResult();
            for (int k = 0; k < resultExprs.length; ++k) {
                final String alias = resultExprs[k].getAlias();
                if (resultExprs[k] instanceof InvokeExpression) {
                    this.processInvokeExpression((InvokeExpression)resultExprs[k]);
                    final SQLExpression sqlExpr = this.stack.pop();
                    this.validateExpressionForResult(sqlExpr);
                    final int[] cols = this.stmt.select(sqlExpr, alias);
                    final StatementMappingIndex idx = new StatementMappingIndex(sqlExpr.getJavaTypeMapping());
                    idx.setColumnPositions(cols);
                    if (alias != null) {
                        idx.setColumnAlias(alias);
                    }
                    this.resultDefinition.addMappingForResultExpression(k, idx);
                }
                else if (resultExprs[k] instanceof PrimaryExpression) {
                    final PrimaryExpression primExpr = (PrimaryExpression)resultExprs[k];
                    if (primExpr.getId().equals(this.candidateAlias)) {
                        final StatementClassMapping map = new StatementClassMapping(this.candidateCmd.getFullClassName(), null);
                        SQLStatementHelper.selectFetchPlanOfCandidateInStatement(this.stmt, map, this.candidateCmd, this.fetchPlan, 1);
                        this.resultDefinition.addMappingForResultExpression(k, map);
                    }
                    else {
                        this.processPrimaryExpression(primExpr);
                        final SQLExpression sqlExpr2 = this.stack.pop();
                        this.validateExpressionForResult(sqlExpr2);
                        if (sqlExpr2 instanceof SQLLiteral) {
                            final int[] cols2 = this.stmt.select(sqlExpr2, alias);
                            final StatementMappingIndex idx2 = new StatementMappingIndex(sqlExpr2.getJavaTypeMapping());
                            idx2.setColumnPositions(cols2);
                            if (alias != null) {
                                idx2.setColumnAlias(alias);
                            }
                            this.resultDefinition.addMappingForResultExpression(k, idx2);
                        }
                        else {
                            final int[] cols2 = this.stmt.select(sqlExpr2.getSQLTable(), sqlExpr2.getJavaTypeMapping(), alias);
                            final StatementMappingIndex idx2 = new StatementMappingIndex(sqlExpr2.getJavaTypeMapping());
                            idx2.setColumnPositions(cols2);
                            if (alias != null) {
                                idx2.setColumnAlias(alias);
                            }
                            this.resultDefinition.addMappingForResultExpression(k, idx2);
                        }
                    }
                }
                else if (resultExprs[k] instanceof ParameterExpression) {
                    this.processParameterExpression((ParameterExpression)resultExprs[k], true);
                    final SQLExpression sqlExpr = this.stack.pop();
                    this.validateExpressionForResult(sqlExpr);
                    final int[] cols = this.stmt.select(sqlExpr, alias);
                    final StatementMappingIndex idx = new StatementMappingIndex(sqlExpr.getJavaTypeMapping());
                    idx.setColumnPositions(cols);
                    if (alias != null) {
                        idx.setColumnAlias(alias);
                    }
                    this.resultDefinition.addMappingForResultExpression(k, idx);
                }
                else if (resultExprs[k] instanceof VariableExpression) {
                    this.processVariableExpression((VariableExpression)resultExprs[k]);
                    SQLExpression sqlExpr = this.stack.pop();
                    this.validateExpressionForResult(sqlExpr);
                    if (sqlExpr instanceof UnboundExpression) {
                        this.processUnboundExpression((UnboundExpression)sqlExpr);
                        sqlExpr = this.stack.pop();
                        NucleusLogger.QUERY.debug("QueryToSQL.exprResult variable was still unbound, so binding via cross-join");
                    }
                    final StatementMappingIndex idx3 = new StatementMappingIndex(sqlExpr.getJavaTypeMapping());
                    final int[] cols2 = this.stmt.select(sqlExpr, alias);
                    idx3.setColumnPositions(cols2);
                    if (alias != null) {
                        idx3.setColumnAlias(alias);
                    }
                    this.resultDefinition.addMappingForResultExpression(k, idx3);
                }
                else if (resultExprs[k] instanceof Literal) {
                    this.processLiteral((Literal)resultExprs[k]);
                    final SQLExpression sqlExpr = this.stack.pop();
                    this.validateExpressionForResult(sqlExpr);
                    final int[] cols = this.stmt.select(sqlExpr, alias);
                    final StatementMappingIndex idx = new StatementMappingIndex(sqlExpr.getJavaTypeMapping());
                    idx.setColumnPositions(cols);
                    if (alias != null) {
                        idx.setColumnAlias(alias);
                    }
                    this.resultDefinition.addMappingForResultExpression(k, idx);
                }
                else if (resultExprs[k] instanceof CreatorExpression) {
                    this.processCreatorExpression((CreatorExpression)resultExprs[k]);
                    final NewObjectExpression sqlExpr3 = this.stack.pop();
                    final StatementNewObjectMapping stmtMap = this.getStatementMappingForNewObjectExpression(sqlExpr3);
                    this.resultDefinition.addMappingForResultExpression(k, stmtMap);
                }
                else if (resultExprs[k] instanceof DyadicExpression) {
                    resultExprs[k].evaluate(this);
                    final SQLExpression sqlExpr = this.stack.pop();
                    final int[] cols = this.stmt.select(sqlExpr, alias);
                    final StatementMappingIndex idx = new StatementMappingIndex(sqlExpr.getJavaTypeMapping());
                    idx.setColumnPositions(cols);
                    if (alias != null) {
                        idx.setColumnAlias(alias);
                    }
                    this.resultDefinition.addMappingForResultExpression(k, idx);
                }
                else {
                    if (!(resultExprs[k] instanceof CaseExpression)) {
                        throw new NucleusException("Dont currently support result clause containing expression of type " + resultExprs[k]);
                    }
                    resultExprs[k].evaluate(this);
                    final SQLExpression sqlExpr = this.stack.pop();
                    final int[] cols = this.stmt.select(sqlExpr, alias);
                    final StatementMappingIndex idx = new StatementMappingIndex(sqlExpr.getJavaTypeMapping());
                    idx.setColumnPositions(cols);
                    if (alias != null) {
                        idx.setColumnAlias(alias);
                    }
                    this.resultDefinition.addMappingForResultExpression(k, idx);
                }
            }
            if (this.stmt.getNumberOfSelects() == 0) {
                this.stmt.select(this.exprFactory.newLiteral(this.stmt, this.storeMgr.getMappingManager().getMapping(Integer.class), 1), null);
            }
        }
        else if (!this.options.contains("BULK_DELETE_NO_RESULT")) {
            this.compileComponent = CompilationComponent.RESULT;
            if (this.candidateCmd.getIdentityType() == IdentityType.NONDURABLE) {
                if (NucleusLogger.QUERY.isDebugEnabled()) {
                    NucleusLogger.QUERY.debug(QueryToSQLMapper.LOCALISER.msg("052520", this.candidateCmd.getFullClassName()));
                }
                this.fetchPlan.setGroup("all");
            }
            if (this.options.contains("RESULT_CANDIDATE_ID")) {
                SQLStatementHelper.selectIdentityOfCandidateInStatement(this.stmt, this.resultDefinitionForClass, this.candidateCmd);
            }
            else if (this.stmt.allUnionsForSamePrimaryTable()) {
                SQLStatementHelper.selectFetchPlanOfCandidateInStatement(this.stmt, this.resultDefinitionForClass, this.candidateCmd, this.fetchPlan, (this.parentMapper == null) ? 1 : 0);
            }
            else if (this.candidateCmd.getInheritanceMetaData() != null && this.candidateCmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.COMPLETE_TABLE) {
                SQLStatementHelper.selectFetchPlanOfCandidateInStatement(this.stmt, this.resultDefinitionForClass, this.candidateCmd, this.fetchPlan, (this.parentMapper == null) ? 1 : 0);
            }
            else {
                SQLStatementHelper.selectIdentityOfCandidateInStatement(this.stmt, this.resultDefinitionForClass, this.candidateCmd);
            }
        }
        this.compileComponent = null;
    }
    
    protected void validateExpressionForResult(final SQLExpression sqlExpr) {
        final JavaTypeMapping m = sqlExpr.getJavaTypeMapping();
        if (m != null && m instanceof AbstractContainerMapping) {
            throw new NucleusUserException(QueryToSQLMapper.LOCALISER.msg("021213"));
        }
    }
    
    protected void compileGrouping() {
        if (this.compilation.getExprGrouping() != null) {
            this.compileComponent = CompilationComponent.GROUPING;
            final Expression[] groupExprs = this.compilation.getExprGrouping();
            for (int i = 0; i < groupExprs.length; ++i) {
                final Expression groupExpr = groupExprs[i];
                final SQLExpression sqlGroupExpr = (SQLExpression)groupExpr.evaluate(this);
                this.stmt.addGroupingExpression(sqlGroupExpr);
            }
            this.compileComponent = null;
        }
    }
    
    protected void compileHaving() {
        if (this.compilation.getExprHaving() != null) {
            this.compileComponent = CompilationComponent.HAVING;
            final Expression havingExpr = this.compilation.getExprHaving();
            final Object havingEval = havingExpr.evaluate(this);
            if (!(havingEval instanceof BooleanExpression)) {
                throw new NucleusUserException(QueryToSQLMapper.LOCALISER.msg("021051", havingExpr));
            }
            this.stmt.setHaving((BooleanExpression)havingEval);
            this.compileComponent = null;
        }
    }
    
    protected void compileOrdering() {
        if (this.compilation.getExprOrdering() != null) {
            this.compileComponent = CompilationComponent.ORDERING;
            final Expression[] orderingExpr = this.compilation.getExprOrdering();
            final SQLExpression[] orderSqlExprs = new SQLExpression[orderingExpr.length];
            final boolean[] directions = new boolean[orderingExpr.length];
            final String[] nullOrders = new String[orderingExpr.length];
            for (int i = 0; i < orderingExpr.length; ++i) {
                final OrderExpression orderExpr = (OrderExpression)orderingExpr[i];
                orderSqlExprs[i] = (SQLExpression)orderExpr.getLeft().evaluate(this);
                final String orderDir = orderExpr.getSortOrder();
                directions[i] = (orderDir != null && !orderDir.equals("ascending"));
                if (this.stmt.getDatastoreAdapter().supportsOption("OrderByWithNullsDirectives")) {
                    final NullOrderingType nullOrder = orderExpr.getNullOrder();
                    if (nullOrder != null) {
                        nullOrders[i] = ((nullOrder == NullOrderingType.NULLS_FIRST) ? "NULLS FIRST" : "NULLS LAST");
                    }
                }
            }
            this.stmt.setOrdering(orderSqlExprs, directions, nullOrders);
            this.compileComponent = null;
        }
    }
    
    protected void compileFromClassExpression(final ClassExpression clsExpr) {
        final Symbol clsExprSym = clsExpr.getSymbol();
        final Class baseCls = (clsExprSym != null) ? clsExprSym.getValueType() : null;
        SQLTable candSqlTbl = this.stmt.getPrimaryTable();
        final MetaDataManager mmgr = this.storeMgr.getMetaDataManager();
        AbstractClassMetaData cmd = mmgr.getMetaDataForClass(baseCls, this.clr);
        if (baseCls != null && baseCls != this.compilation.getCandidateClass()) {
            final DatastoreClass candTbl = this.storeMgr.getDatastoreClass(baseCls.getName(), this.clr);
            candSqlTbl = this.stmt.crossJoin(candTbl, clsExpr.getAlias(), null);
            final SQLTableMapping tblMapping = new SQLTableMapping(candSqlTbl, cmd, candTbl.getIdMapping());
            this.setSQLTableMappingForAlias(clsExpr.getAlias(), tblMapping);
        }
        if (clsExpr.getCandidateExpression() != null && this.parentMapper != null) {
            final String[] tokens = StringUtils.split(clsExpr.getCandidateExpression(), ".");
            final String leftAlias = tokens[0];
            final SQLTableMapping outerSqlTblMapping = this.parentMapper.getSQLTableMappingForAlias(leftAlias);
            AbstractClassMetaData leftCmd = outerSqlTblMapping.cmd;
            final AbstractMemberMetaData[] leftMmds = new AbstractMemberMetaData[tokens.length - 1];
            final AbstractMemberMetaData[] rightMmds = new AbstractMemberMetaData[tokens.length - 1];
            for (int i = 0; i < tokens.length - 1; ++i) {
                final String joinedField = tokens[i + 1];
                final AbstractMemberMetaData leftMmd = leftCmd.getMetaDataForMember(joinedField);
                AbstractMemberMetaData rightMmd = null;
                AbstractClassMetaData rightCmd = null;
                final RelationType relationType = leftMmd.getRelationType(this.clr);
                if (RelationType.isBidirectional(relationType)) {
                    rightMmd = leftMmd.getRelatedMemberMetaData(this.clr)[0];
                    rightCmd = rightMmd.getAbstractClassMetaData();
                }
                else if (relationType == RelationType.ONE_TO_ONE_UNI) {
                    rightCmd = mmgr.getMetaDataForClass(leftMmd.getType(), this.clr);
                }
                else {
                    if (relationType != RelationType.ONE_TO_MANY_UNI) {
                        throw new NucleusUserException("Subquery has been specified with a candidate-expression that includes \"" + tokens[i] + "\" that isnt a relation field!!");
                    }
                    if (leftMmd.hasCollection()) {
                        rightCmd = mmgr.getMetaDataForClass(leftMmd.getCollection().getElementType(), this.clr);
                    }
                    else if (leftMmd.hasMap()) {
                        rightCmd = mmgr.getMetaDataForClass(leftMmd.getMap().getValueType(), this.clr);
                    }
                }
                leftMmds[i] = leftMmd;
                rightMmds[i] = rightMmd;
                leftCmd = rightCmd;
            }
            SQLTable rSqlTbl = candSqlTbl;
            final SQLTable outerSqlTbl = outerSqlTblMapping.table;
            for (int j = leftMmds.length - 1; j >= 0; --j) {
                final AbstractMemberMetaData leftMmd2 = leftMmds[j];
                final AbstractMemberMetaData rightMmd2 = rightMmds[j];
                final DatastoreClass leftTbl = this.storeMgr.getDatastoreClass(leftMmd2.getClassName(true), this.clr);
                SQLTable lSqlTbl = null;
                final RelationType relationType2 = leftMmd2.getRelationType(this.clr);
                if (relationType2 == RelationType.ONE_TO_ONE_UNI) {
                    if (j == 0) {
                        final SQLExpression outerExpr = this.exprFactory.newExpression(outerSqlTbl.getSQLStatement(), outerSqlTbl, outerSqlTbl.getTable().getMemberMapping(leftMmd2));
                        final SQLExpression rightExpr = this.exprFactory.newExpression(this.stmt, rSqlTbl, rSqlTbl.getTable().getIdMapping());
                        this.stmt.whereAnd(outerExpr.eq(rightExpr), false);
                    }
                    else {
                        final JavaTypeMapping leftMapping = leftTbl.getMemberMapping(leftMmd2);
                        lSqlTbl = this.stmt.innerJoin(rSqlTbl, rSqlTbl.getTable().getIdMapping(), leftTbl, null, leftMapping, null, null);
                    }
                }
                else if (relationType2 == RelationType.ONE_TO_ONE_BI) {
                    if (leftMmd2.getMappedBy() != null) {
                        final JavaTypeMapping rightMapping = rSqlTbl.getTable().getMemberMapping(rightMmd2);
                        if (j == 0) {
                            final SQLExpression outerExpr2 = this.exprFactory.newExpression(outerSqlTbl.getSQLStatement(), outerSqlTbl, outerSqlTbl.getTable().getIdMapping());
                            final SQLExpression rightExpr2 = this.exprFactory.newExpression(this.stmt, rSqlTbl, rightMapping);
                            this.stmt.whereAnd(outerExpr2.eq(rightExpr2), false);
                        }
                        else {
                            lSqlTbl = this.stmt.innerJoin(rSqlTbl, rightMapping, leftTbl, null, leftTbl.getIdMapping(), null, null);
                        }
                    }
                    else if (j == 0) {
                        final SQLExpression outerExpr = this.exprFactory.newExpression(outerSqlTbl.getSQLStatement(), outerSqlTbl, outerSqlTbl.getTable().getMemberMapping(leftMmd2));
                        final SQLExpression rightExpr = this.exprFactory.newExpression(this.stmt, rSqlTbl, rSqlTbl.getTable().getIdMapping());
                        this.stmt.whereAnd(outerExpr.eq(rightExpr), false);
                    }
                    else {
                        lSqlTbl = this.stmt.innerJoin(rSqlTbl, rSqlTbl.getTable().getIdMapping(), leftTbl, null, leftTbl.getMemberMapping(leftMmd2), null, null);
                    }
                }
                else if (relationType2 == RelationType.ONE_TO_MANY_UNI) {
                    if (leftMmd2.getJoinMetaData() != null || rightMmd2.getJoinMetaData() != null) {
                        final ElementContainerTable joinTbl = (ElementContainerTable)this.storeMgr.getTable(leftMmd2);
                        final SQLTable joinSqlTbl = this.stmt.innerJoin(rSqlTbl, rSqlTbl.getTable().getIdMapping(), joinTbl, null, joinTbl.getElementMapping(), null, null);
                        if (j == 0) {
                            final SQLExpression outerExpr3 = this.exprFactory.newExpression(outerSqlTbl.getSQLStatement(), outerSqlTbl, outerSqlTbl.getTable().getMemberMapping(leftMmd2));
                            final SQLExpression joinExpr = this.exprFactory.newExpression(this.stmt, joinSqlTbl, joinTbl.getOwnerMapping());
                            this.stmt.whereAnd(outerExpr3.eq(joinExpr), false);
                        }
                        else {
                            lSqlTbl = this.stmt.innerJoin(joinSqlTbl, joinTbl.getOwnerMapping(), leftTbl, null, leftTbl.getIdMapping(), null, null);
                        }
                    }
                    else if (j == 0) {
                        final SQLExpression outerExpr = this.exprFactory.newExpression(outerSqlTbl.getSQLStatement(), outerSqlTbl, outerSqlTbl.getTable().getMemberMapping(leftMmd2));
                        final SQLExpression rightExpr = this.exprFactory.newExpression(this.stmt, rSqlTbl, rSqlTbl.getTable().getMemberMapping(rightMmd2));
                        this.stmt.whereAnd(outerExpr.eq(rightExpr), false);
                    }
                    else {
                        lSqlTbl = this.stmt.innerJoin(rSqlTbl, rSqlTbl.getTable().getMemberMapping(rightMmd2), leftTbl, null, leftTbl.getIdMapping(), null, null);
                    }
                }
                else if (relationType2 == RelationType.ONE_TO_MANY_BI) {
                    if (leftMmd2.getJoinMetaData() != null || rightMmd2.getJoinMetaData() != null) {
                        final ElementContainerTable joinTbl = (ElementContainerTable)this.storeMgr.getTable(leftMmd2);
                        final SQLTable joinSqlTbl = this.stmt.innerJoin(rSqlTbl, rSqlTbl.getTable().getIdMapping(), joinTbl, null, joinTbl.getElementMapping(), null, null);
                        if (j == 0) {
                            final SQLExpression outerExpr3 = this.exprFactory.newExpression(outerSqlTbl.getSQLStatement(), outerSqlTbl, outerSqlTbl.getTable().getMemberMapping(leftMmd2));
                            final SQLExpression joinExpr = this.exprFactory.newExpression(this.stmt, joinSqlTbl, joinTbl.getOwnerMapping());
                            this.stmt.whereAnd(outerExpr3.eq(joinExpr), false);
                        }
                        else {
                            lSqlTbl = this.stmt.innerJoin(joinSqlTbl, joinTbl.getOwnerMapping(), leftTbl, null, leftTbl.getIdMapping(), null, null);
                        }
                    }
                    else if (j == 0) {
                        final SQLExpression outerExpr = this.exprFactory.newExpression(outerSqlTbl.getSQLStatement(), outerSqlTbl, outerSqlTbl.getTable().getIdMapping());
                        final SQLExpression rightExpr = this.exprFactory.newExpression(this.stmt, rSqlTbl, rSqlTbl.getTable().getMemberMapping(rightMmd2));
                        this.stmt.whereAnd(outerExpr.eq(rightExpr), false);
                    }
                    else {
                        lSqlTbl = this.stmt.innerJoin(rSqlTbl, rSqlTbl.getTable().getMemberMapping(rightMmd2), leftTbl, null, leftTbl.getIdMapping(), null, null);
                    }
                }
                else if (relationType2 == RelationType.MANY_TO_ONE_BI) {
                    if (leftMmd2.getJoinMetaData() != null || rightMmd2.getJoinMetaData() != null) {
                        final ElementContainerTable joinTbl = (ElementContainerTable)this.storeMgr.getTable(leftMmd2);
                        final SQLTable joinSqlTbl = this.stmt.innerJoin(rSqlTbl, rSqlTbl.getTable().getIdMapping(), joinTbl, null, joinTbl.getOwnerMapping(), null, null);
                        if (j == 0) {
                            final SQLExpression outerExpr3 = this.exprFactory.newExpression(outerSqlTbl.getSQLStatement(), outerSqlTbl, outerSqlTbl.getTable().getMemberMapping(leftMmd2));
                            final SQLExpression joinExpr = this.exprFactory.newExpression(this.stmt, joinSqlTbl, joinTbl.getElementMapping());
                            this.stmt.whereAnd(outerExpr3.eq(joinExpr), false);
                        }
                        else {
                            lSqlTbl = this.stmt.innerJoin(joinSqlTbl, joinTbl.getElementMapping(), leftTbl, null, leftTbl.getIdMapping(), null, null);
                        }
                    }
                    else if (j == 0) {
                        final SQLExpression outerExpr = this.exprFactory.newExpression(outerSqlTbl.getSQLStatement(), outerSqlTbl, outerSqlTbl.getTable().getMemberMapping(leftMmd2));
                        final SQLExpression rightExpr = this.exprFactory.newExpression(this.stmt, rSqlTbl, rSqlTbl.getTable().getIdMapping());
                        this.stmt.whereAnd(outerExpr.eq(rightExpr), false);
                    }
                    else {
                        lSqlTbl = this.stmt.innerJoin(rSqlTbl, rSqlTbl.getTable().getIdMapping(), leftTbl, null, leftTbl.getMemberMapping(leftMmd2), null, null);
                    }
                }
                rSqlTbl = lSqlTbl;
            }
        }
        Expression rightExpr3 = clsExpr.getRight();
        SQLTable sqlTbl = candSqlTbl;
        while (rightExpr3 != null) {
            if (rightExpr3 instanceof JoinExpression) {
                final JoinExpression joinExpr2 = (JoinExpression)rightExpr3;
                final JoinExpression.JoinType joinType = joinExpr2.getType();
                final String joinAlias = joinExpr2.getAlias();
                final PrimaryExpression joinPrimExpr = joinExpr2.getPrimaryExpression();
                final Iterator<String> iter = joinPrimExpr.getTuples().iterator();
                final String rootId = iter.next();
                String joinTableGroupName = null;
                if (rootId.equalsIgnoreCase(this.candidateAlias)) {
                    joinTableGroupName = joinPrimExpr.getId();
                }
                else {
                    final SQLTableMapping sqlTblMapping = this.getSQLTableMappingForAlias(rootId);
                    if (sqlTblMapping == null) {
                        throw new NucleusUserException("Query has " + joinPrimExpr.getId() + " yet the first component " + rootId + " is unknown!");
                    }
                    cmd = sqlTblMapping.cmd;
                    joinTableGroupName = sqlTblMapping.table.getGroupName();
                    sqlTbl = sqlTblMapping.table;
                }
                while (iter.hasNext()) {
                    final String id = iter.next();
                    String[] ids = null;
                    if (id.contains(".")) {
                        ids = StringUtils.split(id, ".");
                    }
                    else {
                        ids = new String[] { id };
                    }
                    for (int k = 0; k < ids.length; ++k) {
                        final AbstractMemberMetaData mmd = cmd.getMetaDataForMember(ids[k]);
                        if (mmd == null) {
                            throw new NucleusUserException("Query has " + joinPrimExpr.getId() + " yet " + ids[k] + " is not found. Fix your input");
                        }
                        final RelationType relationType2 = mmd.getRelationType(this.clr);
                        DatastoreClass relTable = null;
                        AbstractMemberMetaData relMmd = null;
                        if (relationType2 != RelationType.NONE && (joinType == JoinExpression.JoinType.JOIN_INNER_FETCH || joinType == JoinExpression.JoinType.JOIN_LEFT_OUTER_FETCH)) {
                            final String fgName = "QUERY_FETCH_" + mmd.getFullFieldName();
                            final FetchGroupManager fetchGrpMgr = this.storeMgr.getNucleusContext().getFetchGroupManager();
                            final Class cls = this.clr.classForName(cmd.getFullClassName());
                            if (fetchGrpMgr.getFetchGroupsWithName(fgName) == null) {
                                final FetchGroup grp = new FetchGroup(this.storeMgr.getNucleusContext(), fgName, cls);
                                grp.addMember(mmd.getName());
                                fetchGrpMgr.addFetchGroup(grp);
                            }
                            this.fetchPlan.addGroup(fgName);
                        }
                        if (relationType2 == RelationType.ONE_TO_ONE_UNI) {
                            relTable = this.storeMgr.getDatastoreClass(mmd.getTypeName(), this.clr);
                            cmd = mmgr.getMetaDataForClass(mmd.getType(), this.clr);
                            if (joinType == JoinExpression.JoinType.JOIN_INNER || joinType == JoinExpression.JoinType.JOIN_INNER_FETCH) {
                                sqlTbl = this.stmt.innerJoin(sqlTbl, sqlTbl.getTable().getMemberMapping(mmd), relTable, null, relTable.getIdMapping(), null, joinTableGroupName);
                            }
                            else {
                                sqlTbl = this.stmt.leftOuterJoin(sqlTbl, sqlTbl.getTable().getMemberMapping(mmd), relTable, null, relTable.getIdMapping(), null, joinTableGroupName);
                            }
                        }
                        else if (relationType2 == RelationType.ONE_TO_ONE_BI) {
                            relTable = this.storeMgr.getDatastoreClass(mmd.getTypeName(), this.clr);
                            cmd = this.storeMgr.getMetaDataManager().getMetaDataForClass(mmd.getType(), this.clr);
                            if (mmd.getMappedBy() != null) {
                                relMmd = mmd.getRelatedMemberMetaData(this.clr)[0];
                                if (joinType == JoinExpression.JoinType.JOIN_INNER || joinType == JoinExpression.JoinType.JOIN_INNER_FETCH) {
                                    sqlTbl = this.stmt.innerJoin(sqlTbl, sqlTbl.getTable().getIdMapping(), relTable, null, relTable.getMemberMapping(relMmd), null, joinTableGroupName);
                                }
                                else {
                                    sqlTbl = this.stmt.leftOuterJoin(sqlTbl, sqlTbl.getTable().getIdMapping(), relTable, null, relTable.getMemberMapping(relMmd), null, joinTableGroupName);
                                }
                            }
                            else if (joinType == JoinExpression.JoinType.JOIN_INNER || joinType == JoinExpression.JoinType.JOIN_INNER_FETCH) {
                                sqlTbl = this.stmt.innerJoin(sqlTbl, sqlTbl.getTable().getMemberMapping(mmd), relTable, null, relTable.getIdMapping(), null, joinTableGroupName);
                            }
                            else {
                                sqlTbl = this.stmt.leftOuterJoin(sqlTbl, sqlTbl.getTable().getMemberMapping(mmd), relTable, null, relTable.getIdMapping(), null, joinTableGroupName);
                            }
                        }
                        else if (relationType2 == RelationType.ONE_TO_MANY_BI) {
                            relTable = this.storeMgr.getDatastoreClass(mmd.getCollection().getElementType(), this.clr);
                            cmd = mmd.getCollection().getElementClassMetaData(this.clr, mmgr);
                            relMmd = mmd.getRelatedMemberMetaData(this.clr)[0];
                            if (mmd.getJoinMetaData() != null || relMmd.getJoinMetaData() != null) {
                                final ElementContainerTable joinTbl2 = (ElementContainerTable)this.storeMgr.getTable(mmd);
                                if (joinType == JoinExpression.JoinType.JOIN_INNER || joinType == JoinExpression.JoinType.JOIN_INNER_FETCH) {
                                    final SQLTable joinSqlTbl2 = this.stmt.innerJoin(sqlTbl, sqlTbl.getTable().getIdMapping(), joinTbl2, null, joinTbl2.getOwnerMapping(), null, null);
                                    sqlTbl = this.stmt.innerJoin(joinSqlTbl2, joinTbl2.getElementMapping(), relTable, null, relTable.getIdMapping(), null, joinTableGroupName);
                                }
                                else {
                                    final SQLTable joinSqlTbl2 = this.stmt.leftOuterJoin(sqlTbl, sqlTbl.getTable().getIdMapping(), joinTbl2, null, joinTbl2.getOwnerMapping(), null, null);
                                    sqlTbl = this.stmt.leftOuterJoin(joinSqlTbl2, joinTbl2.getElementMapping(), relTable, null, relTable.getIdMapping(), null, joinTableGroupName);
                                }
                            }
                            else if (joinType == JoinExpression.JoinType.JOIN_INNER || joinType == JoinExpression.JoinType.JOIN_INNER_FETCH) {
                                sqlTbl = this.stmt.innerJoin(sqlTbl, sqlTbl.getTable().getIdMapping(), relTable, null, relTable.getMemberMapping(relMmd), null, joinTableGroupName);
                            }
                            else {
                                sqlTbl = this.stmt.leftOuterJoin(sqlTbl, sqlTbl.getTable().getIdMapping(), relTable, null, relTable.getMemberMapping(relMmd), null, joinTableGroupName);
                            }
                        }
                        else if (relationType2 == RelationType.ONE_TO_MANY_UNI) {
                            relTable = this.storeMgr.getDatastoreClass(mmd.getCollection().getElementType(), this.clr);
                            cmd = mmd.getCollection().getElementClassMetaData(this.clr, mmgr);
                            if (mmd.getJoinMetaData() != null) {
                                final ElementContainerTable joinTbl2 = (ElementContainerTable)this.storeMgr.getTable(mmd);
                                if (joinType == JoinExpression.JoinType.JOIN_INNER || joinType == JoinExpression.JoinType.JOIN_INNER_FETCH) {
                                    final SQLTable joinSqlTbl2 = this.stmt.innerJoin(sqlTbl, sqlTbl.getTable().getIdMapping(), joinTbl2, null, joinTbl2.getOwnerMapping(), null, null);
                                    sqlTbl = this.stmt.innerJoin(joinSqlTbl2, joinTbl2.getElementMapping(), relTable, null, relTable.getIdMapping(), null, joinTableGroupName);
                                }
                                else {
                                    final SQLTable joinSqlTbl2 = this.stmt.leftOuterJoin(sqlTbl, sqlTbl.getTable().getIdMapping(), joinTbl2, null, joinTbl2.getOwnerMapping(), null, null);
                                    sqlTbl = this.stmt.leftOuterJoin(joinSqlTbl2, joinTbl2.getElementMapping(), relTable, null, relTable.getIdMapping(), null, joinTableGroupName);
                                }
                            }
                            else {
                                final JavaTypeMapping relMapping = relTable.getExternalMapping(mmd, 5);
                                if (joinType == JoinExpression.JoinType.JOIN_INNER || joinType == JoinExpression.JoinType.JOIN_INNER_FETCH) {
                                    sqlTbl = this.stmt.innerJoin(sqlTbl, sqlTbl.getTable().getIdMapping(), relTable, null, relMapping, null, joinTableGroupName);
                                }
                                else {
                                    sqlTbl = this.stmt.leftOuterJoin(sqlTbl, sqlTbl.getTable().getIdMapping(), relTable, null, relMapping, null, joinTableGroupName);
                                }
                            }
                        }
                        else if (relationType2 == RelationType.MANY_TO_MANY_BI) {
                            relTable = this.storeMgr.getDatastoreClass(mmd.getCollection().getElementType(), this.clr);
                            cmd = mmd.getCollection().getElementClassMetaData(this.clr, mmgr);
                            relMmd = mmd.getRelatedMemberMetaData(this.clr)[0];
                            final CollectionTable joinTbl3 = (CollectionTable)this.storeMgr.getTable(mmd);
                            if (joinType == JoinExpression.JoinType.JOIN_INNER || joinType == JoinExpression.JoinType.JOIN_INNER_FETCH) {
                                final SQLTable joinSqlTbl2 = this.stmt.innerJoin(sqlTbl, sqlTbl.getTable().getIdMapping(), joinTbl3, null, joinTbl3.getOwnerMapping(), null, null);
                                sqlTbl = this.stmt.innerJoin(joinSqlTbl2, joinTbl3.getElementMapping(), relTable, null, relTable.getIdMapping(), null, joinTableGroupName);
                            }
                            else {
                                final SQLTable joinSqlTbl2 = this.stmt.leftOuterJoin(sqlTbl, sqlTbl.getTable().getIdMapping(), joinTbl3, null, joinTbl3.getOwnerMapping(), null, null);
                                sqlTbl = this.stmt.leftOuterJoin(joinSqlTbl2, joinTbl3.getElementMapping(), relTable, null, relTable.getIdMapping(), null, joinTableGroupName);
                            }
                        }
                        else if (relationType2 == RelationType.MANY_TO_ONE_BI) {
                            relTable = this.storeMgr.getDatastoreClass(mmd.getTypeName(), this.clr);
                            cmd = this.storeMgr.getMetaDataManager().getMetaDataForClass(mmd.getType(), this.clr);
                            relMmd = mmd.getRelatedMemberMetaData(this.clr)[0];
                            if (mmd.getJoinMetaData() != null || relMmd.getJoinMetaData() != null) {
                                final CollectionTable joinTbl3 = (CollectionTable)this.storeMgr.getTable(relMmd);
                                if (joinType == JoinExpression.JoinType.JOIN_INNER || joinType == JoinExpression.JoinType.JOIN_INNER_FETCH) {
                                    final SQLTable joinSqlTbl2 = this.stmt.innerJoin(sqlTbl, sqlTbl.getTable().getIdMapping(), joinTbl3, null, joinTbl3.getElementMapping(), null, null);
                                    sqlTbl = this.stmt.innerJoin(joinSqlTbl2, joinTbl3.getOwnerMapping(), relTable, null, relTable.getIdMapping(), null, joinTableGroupName);
                                }
                                else {
                                    final SQLTable joinSqlTbl2 = this.stmt.leftOuterJoin(sqlTbl, sqlTbl.getTable().getIdMapping(), joinTbl3, null, joinTbl3.getElementMapping(), null, null);
                                    sqlTbl = this.stmt.leftOuterJoin(joinSqlTbl2, joinTbl3.getOwnerMapping(), relTable, null, relTable.getIdMapping(), null, joinTableGroupName);
                                }
                            }
                            else {
                                final JavaTypeMapping fkMapping = sqlTbl.getTable().getMemberMapping(mmd);
                                if (joinType == JoinExpression.JoinType.JOIN_INNER || joinType == JoinExpression.JoinType.JOIN_INNER_FETCH) {
                                    sqlTbl = this.stmt.innerJoin(sqlTbl, fkMapping, relTable, null, relTable.getIdMapping(), null, joinTableGroupName);
                                }
                                else {
                                    sqlTbl = this.stmt.leftOuterJoin(sqlTbl, fkMapping, relTable, null, relTable.getIdMapping(), null, joinTableGroupName);
                                }
                            }
                        }
                    }
                }
                if (joinAlias != null) {
                    if (this.explicitJoinPrimaryByAlias == null) {
                        this.explicitJoinPrimaryByAlias = new HashMap<String, String>();
                    }
                    this.explicitJoinPrimaryByAlias.put(joinAlias, joinPrimExpr.getId());
                }
                final SQLTableMapping tblMapping2 = new SQLTableMapping(sqlTbl, cmd, sqlTbl.getTable().getIdMapping());
                this.setSQLTableMappingForAlias(joinAlias, tblMapping2);
                final DyadicExpression joinOnExpr = joinExpr2.getOnExpression();
                if (joinOnExpr != null) {
                    joinOnExpr.evaluate(this);
                    final BooleanExpression joinOnSqlExpr = this.stack.pop();
                    final SQLJoin join = this.stmt.getJoinForTable(sqlTbl);
                    join.addAndCondition(joinOnSqlExpr);
                }
            }
            rightExpr3 = rightExpr3.getRight();
        }
    }
    
    protected StatementNewObjectMapping getStatementMappingForNewObjectExpression(final NewObjectExpression expr) {
        final List argExprs = expr.getConstructorArgExpressions();
        final StatementNewObjectMapping stmtMap = new StatementNewObjectMapping(expr.getNewClass());
        if (argExprs != null) {
            final Iterator<SQLExpression> argIter = argExprs.iterator();
            int j = 0;
            while (argIter.hasNext()) {
                final SQLExpression argExpr = argIter.next();
                if (argExpr instanceof SQLLiteral) {
                    stmtMap.addConstructorArgMapping(j, ((SQLLiteral)argExpr).getValue());
                }
                else if (argExpr instanceof NewObjectExpression) {
                    stmtMap.addConstructorArgMapping(j, this.getStatementMappingForNewObjectExpression((NewObjectExpression)argExpr));
                }
                else {
                    final StatementMappingIndex idx = new StatementMappingIndex(argExpr.getJavaTypeMapping());
                    final int[] cols = this.stmt.select(argExpr, null);
                    idx.setColumnPositions(cols);
                    stmtMap.addConstructorArgMapping(j, idx);
                }
                ++j;
            }
        }
        return stmtMap;
    }
    
    @Override
    protected Object processAndExpression(final Expression expr) {
        final SQLExpression rightExpr = this.stack.pop();
        final SQLExpression leftExpr = this.stack.pop();
        if (!(rightExpr instanceof BooleanExpression)) {
            throw new NucleusUserException("Query has clause " + rightExpr + " used with AND. This is illegal, and should be a boolean expression");
        }
        if (!(leftExpr instanceof BooleanExpression)) {
            throw new NucleusUserException("Query has clause " + leftExpr + " used with AND. This is illegal, and should be a boolean expression");
        }
        BooleanExpression right = (BooleanExpression)rightExpr;
        BooleanExpression left = (BooleanExpression)leftExpr;
        if (left.getSQLStatement() != null && right.getSQLStatement() != null && left.getSQLStatement() != right.getSQLStatement()) {
            if (left.getSQLStatement() == this.stmt && right.getSQLStatement().isChildStatementOf(this.stmt)) {
                right.getSQLStatement().whereAnd(right, true);
                this.stack.push(left);
                return left;
            }
            if (right.getSQLStatement() == this.stmt && left.getSQLStatement().isChildStatementOf(this.stmt)) {
                left.getSQLStatement().whereAnd(left, true);
                this.stack.push(right);
                return right;
            }
        }
        if (this.compileComponent == CompilationComponent.FILTER) {
            left = this.getBooleanExpressionForUseInFilter(left);
            right = this.getBooleanExpressionForUseInFilter(right);
        }
        final BooleanExpression opExpr = left.and(right);
        this.stack.push(opExpr);
        return opExpr;
    }
    
    @Override
    protected Object processOrExpression(final Expression expr) {
        final SQLExpression rightExpr = this.stack.pop();
        final SQLExpression leftExpr = this.stack.pop();
        if (!(rightExpr instanceof BooleanExpression)) {
            throw new NucleusUserException("Query has clause " + rightExpr + " used with AND. This is illegal, and should be a boolean expression");
        }
        if (!(leftExpr instanceof BooleanExpression)) {
            throw new NucleusUserException("Query has clause " + leftExpr + " used with AND. This is illegal, and should be a boolean expression");
        }
        BooleanExpression right = (BooleanExpression)rightExpr;
        BooleanExpression left = (BooleanExpression)leftExpr;
        if (left.getSQLStatement() != null && right.getSQLStatement() != null && left.getSQLStatement() != right.getSQLStatement()) {
            if (left.getSQLStatement() == this.stmt && right.getSQLStatement().isChildStatementOf(this.stmt)) {
                right.getSQLStatement().whereAnd(right, true);
                this.stack.push(left);
                return left;
            }
            if (right.getSQLStatement() == this.stmt && left.getSQLStatement().isChildStatementOf(this.stmt)) {
                left.getSQLStatement().whereAnd(left, true);
                this.stack.push(right);
                return right;
            }
        }
        if (this.compileComponent == CompilationComponent.FILTER) {
            left = this.getBooleanExpressionForUseInFilter(left);
            right = this.getBooleanExpressionForUseInFilter(right);
        }
        left.encloseInParentheses();
        right.encloseInParentheses();
        final BooleanExpression opExpr = left.ior(right);
        this.stack.push(opExpr);
        return opExpr;
    }
    
    @Override
    protected Object processEqExpression(final Expression expr) {
        SQLExpression right = this.stack.pop();
        SQLExpression left = this.stack.pop();
        if (left instanceof ParameterLiteral && !(right instanceof ParameterLiteral)) {
            left = this.replaceParameterLiteral((ParameterLiteral)left, right.getJavaTypeMapping());
        }
        else if (right instanceof ParameterLiteral && !(left instanceof ParameterLiteral)) {
            right = this.replaceParameterLiteral((ParameterLiteral)right, left.getJavaTypeMapping());
        }
        if (left.isParameter() && right.isParameter()) {
            if (left.isParameter() && left instanceof SQLLiteral && ((SQLLiteral)left).getValue() != null) {
                this.useParameterExpressionAsLiteral((SQLLiteral)left);
            }
            if (right.isParameter() && right instanceof SQLLiteral && ((SQLLiteral)right).getValue() != null) {
                this.useParameterExpressionAsLiteral((SQLLiteral)right);
            }
        }
        ExpressionUtils.checkAndCorrectExpressionMappingsForBooleanComparison(left, right);
        if (left instanceof UnboundExpression) {
            this.processUnboundExpression((UnboundExpression)left);
            left = this.stack.pop();
        }
        if (right instanceof UnboundExpression) {
            this.processUnboundExpression((UnboundExpression)right);
            right = this.stack.pop();
        }
        if (!this.options.contains("EXPLICIT_JOINS")) {
            final boolean leftIsCrossJoin = this.stmt.getJoinTypeForTable(left.getSQLTable()) == SQLJoin.JoinType.CROSS_JOIN;
            final boolean rightIsCrossJoin = this.stmt.getJoinTypeForTable(right.getSQLTable()) == SQLJoin.JoinType.CROSS_JOIN;
            if (leftIsCrossJoin && !rightIsCrossJoin && !(right instanceof SQLLiteral)) {
                final String varName = this.getAliasForSQLTable(left.getSQLTable());
                final SQLJoin.JoinType joinType = this.getRequiredJoinTypeForAlias(varName);
                if (joinType != null) {
                    NucleusLogger.QUERY.debug("QueryToSQL.eq variable " + varName + " is mapped to table " + left.getSQLTable() + " was previously bound as CROSS JOIN but changing to " + joinType);
                    final String leftTblAlias = this.stmt.removeCrossJoin(left.getSQLTable());
                    if (joinType == SQLJoin.JoinType.LEFT_OUTER_JOIN) {
                        this.stmt.leftOuterJoin(right.getSQLTable(), right.getJavaTypeMapping(), left.getSQLTable().getTable(), leftTblAlias, left.getJavaTypeMapping(), null, left.getSQLTable().getGroupName());
                    }
                    else {
                        this.stmt.innerJoin(right.getSQLTable(), right.getJavaTypeMapping(), left.getSQLTable().getTable(), leftTblAlias, left.getJavaTypeMapping(), null, left.getSQLTable().getGroupName());
                    }
                    final JavaTypeMapping m = this.exprFactory.getMappingForType(Boolean.TYPE, true);
                    final SQLExpression opExpr = this.exprFactory.newLiteral(this.stmt, m, true).eq(this.exprFactory.newLiteral(this.stmt, m, true));
                    this.stack.push(opExpr);
                    return opExpr;
                }
            }
            else if (!leftIsCrossJoin && rightIsCrossJoin && !(left instanceof SQLLiteral)) {
                final String varName = this.getAliasForSQLTable(right.getSQLTable());
                final SQLJoin.JoinType joinType = this.getRequiredJoinTypeForAlias(varName);
                if (joinType != null) {
                    NucleusLogger.QUERY.debug("QueryToSQL.eq variable " + varName + " is mapped to table " + right.getSQLTable() + " was previously bound as CROSS JOIN but changing to " + joinType);
                    final String rightTblAlias = this.stmt.removeCrossJoin(right.getSQLTable());
                    if (joinType == SQLJoin.JoinType.LEFT_OUTER_JOIN) {
                        this.stmt.leftOuterJoin(left.getSQLTable(), left.getJavaTypeMapping(), right.getSQLTable().getTable(), rightTblAlias, right.getJavaTypeMapping(), null, right.getSQLTable().getGroupName());
                    }
                    else {
                        this.stmt.innerJoin(left.getSQLTable(), left.getJavaTypeMapping(), right.getSQLTable().getTable(), rightTblAlias, right.getJavaTypeMapping(), null, right.getSQLTable().getGroupName());
                    }
                    final JavaTypeMapping m = this.exprFactory.getMappingForType(Boolean.TYPE, true);
                    final SQLExpression opExpr = this.exprFactory.newLiteral(this.stmt, m, true).eq(this.exprFactory.newLiteral(this.stmt, m, true));
                    this.stack.push(opExpr);
                    return opExpr;
                }
            }
        }
        final BooleanExpression opExpr2 = left.eq(right);
        this.stack.push(opExpr2);
        return opExpr2;
    }
    
    @Override
    protected Object processNoteqExpression(final Expression expr) {
        SQLExpression right = this.stack.pop();
        SQLExpression left = this.stack.pop();
        if (left instanceof ParameterLiteral && !(right instanceof ParameterLiteral)) {
            left = this.replaceParameterLiteral((ParameterLiteral)left, right.getJavaTypeMapping());
        }
        else if (right instanceof ParameterLiteral && !(left instanceof ParameterLiteral)) {
            right = this.replaceParameterLiteral((ParameterLiteral)right, left.getJavaTypeMapping());
        }
        if (left.isParameter() && right.isParameter()) {
            if (left.isParameter() && left instanceof SQLLiteral && ((SQLLiteral)left).getValue() != null) {
                this.useParameterExpressionAsLiteral((SQLLiteral)left);
            }
            if (right.isParameter() && right instanceof SQLLiteral && ((SQLLiteral)right).getValue() != null) {
                this.useParameterExpressionAsLiteral((SQLLiteral)right);
            }
        }
        ExpressionUtils.checkAndCorrectExpressionMappingsForBooleanComparison(left, right);
        if (left instanceof UnboundExpression) {
            this.processUnboundExpression((UnboundExpression)left);
            left = this.stack.pop();
        }
        if (right instanceof UnboundExpression) {
            this.processUnboundExpression((UnboundExpression)right);
            right = this.stack.pop();
        }
        final BooleanExpression opExpr = left.ne(right);
        this.stack.push(opExpr);
        return opExpr;
    }
    
    @Override
    protected Object processGteqExpression(final Expression expr) {
        SQLExpression right = this.stack.pop();
        SQLExpression left = this.stack.pop();
        if (left instanceof ParameterLiteral && !(right instanceof ParameterLiteral)) {
            left = this.replaceParameterLiteral((ParameterLiteral)left, right.getJavaTypeMapping());
        }
        else if (right instanceof ParameterLiteral && !(left instanceof ParameterLiteral)) {
            right = this.replaceParameterLiteral((ParameterLiteral)right, left.getJavaTypeMapping());
        }
        ExpressionUtils.checkAndCorrectExpressionMappingsForBooleanComparison(left, right);
        if (left instanceof UnboundExpression) {
            this.processUnboundExpression((UnboundExpression)left);
            left = this.stack.pop();
        }
        if (right instanceof UnboundExpression) {
            this.processUnboundExpression((UnboundExpression)right);
            right = this.stack.pop();
        }
        final BooleanExpression opExpr = left.ge(right);
        this.stack.push(opExpr);
        return opExpr;
    }
    
    @Override
    protected Object processGtExpression(final Expression expr) {
        SQLExpression right = this.stack.pop();
        SQLExpression left = this.stack.pop();
        if (left instanceof ParameterLiteral && !(right instanceof ParameterLiteral)) {
            left = this.replaceParameterLiteral((ParameterLiteral)left, right.getJavaTypeMapping());
        }
        else if (right instanceof ParameterLiteral && !(left instanceof ParameterLiteral)) {
            right = this.replaceParameterLiteral((ParameterLiteral)right, left.getJavaTypeMapping());
        }
        ExpressionUtils.checkAndCorrectExpressionMappingsForBooleanComparison(left, right);
        if (left instanceof UnboundExpression) {
            this.processUnboundExpression((UnboundExpression)left);
            left = this.stack.pop();
        }
        if (right instanceof UnboundExpression) {
            this.processUnboundExpression((UnboundExpression)right);
            right = this.stack.pop();
        }
        final BooleanExpression opExpr = left.gt(right);
        this.stack.push(opExpr);
        return opExpr;
    }
    
    @Override
    protected Object processLteqExpression(final Expression expr) {
        SQLExpression right = this.stack.pop();
        SQLExpression left = this.stack.pop();
        if (left instanceof ParameterLiteral && !(right instanceof ParameterLiteral)) {
            left = this.replaceParameterLiteral((ParameterLiteral)left, right.getJavaTypeMapping());
        }
        else if (right instanceof ParameterLiteral && !(left instanceof ParameterLiteral)) {
            right = this.replaceParameterLiteral((ParameterLiteral)right, left.getJavaTypeMapping());
        }
        ExpressionUtils.checkAndCorrectExpressionMappingsForBooleanComparison(left, right);
        if (left instanceof UnboundExpression) {
            this.processUnboundExpression((UnboundExpression)left);
            left = this.stack.pop();
        }
        if (right instanceof UnboundExpression) {
            this.processUnboundExpression((UnboundExpression)right);
            right = this.stack.pop();
        }
        final BooleanExpression opExpr = left.le(right);
        this.stack.push(opExpr);
        return opExpr;
    }
    
    @Override
    protected Object processLtExpression(final Expression expr) {
        SQLExpression right = this.stack.pop();
        SQLExpression left = this.stack.pop();
        if (left instanceof ParameterLiteral && !(right instanceof ParameterLiteral)) {
            left = this.replaceParameterLiteral((ParameterLiteral)left, right.getJavaTypeMapping());
        }
        else if (right instanceof ParameterLiteral && !(left instanceof ParameterLiteral)) {
            right = this.replaceParameterLiteral((ParameterLiteral)right, left.getJavaTypeMapping());
        }
        ExpressionUtils.checkAndCorrectExpressionMappingsForBooleanComparison(left, right);
        if (left instanceof UnboundExpression) {
            this.processUnboundExpression((UnboundExpression)left);
            left = this.stack.pop();
        }
        if (right instanceof UnboundExpression) {
            this.processUnboundExpression((UnboundExpression)right);
            right = this.stack.pop();
        }
        final BooleanExpression opExpr = left.lt(right);
        this.stack.push(opExpr);
        return opExpr;
    }
    
    @Override
    protected Object processLiteral(final Literal expr) {
        Object litValue = expr.getLiteral();
        if (litValue instanceof Class) {
            litValue = ((Class)litValue).getName();
        }
        JavaTypeMapping m = null;
        if (litValue != null) {
            m = this.exprFactory.getMappingForType(litValue.getClass(), false);
        }
        final SQLExpression sqlExpr = this.exprFactory.newLiteral(this.stmt, m, litValue);
        this.stack.push(sqlExpr);
        return sqlExpr;
    }
    
    @Override
    protected Object processPrimaryExpression(final PrimaryExpression expr) {
        SQLExpression sqlExpr = null;
        if (expr.getLeft() != null) {
            if (expr.getLeft() instanceof DyadicExpression && expr.getLeft().getOperator() == Expression.OP_CAST) {
                String exprCastName = null;
                if (expr.getLeft().getLeft() instanceof PrimaryExpression) {
                    exprCastName = "CAST_" + ((PrimaryExpression)expr.getLeft().getLeft()).getId();
                }
                else if (expr.getLeft().getLeft() instanceof VariableExpression) {
                    exprCastName = "CAST_" + ((VariableExpression)expr.getLeft().getLeft()).getId();
                }
                else {
                    if (!(expr.getLeft().getLeft() instanceof InvokeExpression)) {
                        throw new NucleusException("Don't currently support cast of " + expr.getLeft().getLeft());
                    }
                    exprCastName = "CAST_" + expr.getLeft().getLeft();
                }
                expr.getLeft().getLeft().evaluate(this);
                sqlExpr = this.stack.pop();
                final JavaTypeMapping mapping = sqlExpr.getJavaTypeMapping();
                if (mapping instanceof EmbeddedMapping) {
                    final Literal castLitExpr = (Literal)expr.getLeft().getRight();
                    final Class castType = this.resolveClass((String)castLitExpr.getLiteral());
                    final AbstractClassMetaData castCmd = this.ec.getMetaDataManager().getMetaDataForClass(castType, this.clr);
                    final JavaTypeMapping discMapping = ((EmbeddedMapping)mapping).getDiscriminatorMapping();
                    if (discMapping != null) {
                        final AbstractClassMetaData fieldCmd = this.ec.getMetaDataManager().getMetaDataForClass(castType, this.clr);
                        final DiscriminatorMetaData dismd = fieldCmd.getDiscriminatorMetaDataRoot();
                        final SQLExpression discExpr = this.stmt.getSQLExpressionFactory().newExpression(this.stmt, sqlExpr.getSQLTable(), discMapping);
                        SQLExpression discVal = null;
                        if (dismd.getStrategy() == DiscriminatorStrategy.CLASS_NAME) {
                            discVal = this.stmt.getSQLExpressionFactory().newLiteral(this.stmt, discMapping, castCmd.getFullClassName());
                        }
                        else {
                            discVal = this.stmt.getSQLExpressionFactory().newLiteral(this.stmt, discMapping, castCmd.getDiscriminatorMetaData().getValue());
                        }
                        BooleanExpression discRestrictExpr = discExpr.eq(discVal);
                        for (final String subclassName : this.storeMgr.getSubClassesForClass(castType.getName(), true, this.clr)) {
                            final AbstractClassMetaData subtypeCmd = this.storeMgr.getMetaDataManager().getMetaDataForClass(subclassName, this.clr);
                            if (dismd.getStrategy() == DiscriminatorStrategy.CLASS_NAME) {
                                discVal = this.stmt.getSQLExpressionFactory().newLiteral(this.stmt, discMapping, subtypeCmd.getFullClassName());
                            }
                            else {
                                discVal = this.stmt.getSQLExpressionFactory().newLiteral(this.stmt, discMapping, subtypeCmd.getDiscriminatorMetaData().getValue());
                            }
                            final BooleanExpression subtypeExpr = discExpr.eq(discVal);
                            discRestrictExpr = discRestrictExpr.ior(subtypeExpr);
                        }
                        this.stmt.whereAnd(discRestrictExpr, true);
                    }
                    final SQLTableMapping tblMapping = new SQLTableMapping(sqlExpr.getSQLTable(), castCmd, sqlExpr.getJavaTypeMapping());
                    this.setSQLTableMappingForAlias(exprCastName, tblMapping);
                    final SQLTableMapping sqlMapping = this.getSQLTableMappingForPrimaryExpression(this.stmt, exprCastName, expr, Boolean.FALSE);
                    if (sqlMapping == null) {
                        throw new NucleusException("PrimaryExpression " + expr + " is not yet supported");
                    }
                    sqlExpr = this.exprFactory.newExpression(this.stmt, sqlMapping.table, sqlMapping.mapping);
                    this.stack.push(sqlExpr);
                    return sqlExpr;
                }
                else {
                    expr.getLeft().evaluate(this);
                    sqlExpr = this.stack.pop();
                    final Literal castLitExpr = (Literal)expr.getLeft().getRight();
                    final AbstractClassMetaData castCmd2 = this.ec.getMetaDataManager().getMetaDataForClass(this.resolveClass((String)castLitExpr.getLiteral()), this.clr);
                    final SQLTableMapping tblMapping2 = new SQLTableMapping(sqlExpr.getSQLTable(), castCmd2, sqlExpr.getJavaTypeMapping());
                    this.setSQLTableMappingForAlias(exprCastName, tblMapping2);
                    final SQLTableMapping sqlMapping2 = this.getSQLTableMappingForPrimaryExpression(this.stmt, exprCastName, expr, Boolean.FALSE);
                    if (sqlMapping2 == null) {
                        throw new NucleusException("PrimaryExpression " + expr + " is not yet supported");
                    }
                    sqlExpr = this.exprFactory.newExpression(this.stmt, sqlMapping2.table, sqlMapping2.mapping);
                    this.stack.push(sqlExpr);
                    return sqlExpr;
                }
            }
            else if (expr.getLeft() instanceof ParameterExpression) {
                this.setNotPrecompilable();
                final ParameterExpression paramExpr = (ParameterExpression)expr.getLeft();
                final Symbol paramSym = this.compilation.getSymbolTable().getSymbol(paramExpr.getId());
                if (paramSym.getValueType() != null && paramSym.getValueType().isArray()) {
                    final String first = expr.getTuples().get(0);
                    this.processParameterExpression(paramExpr, true);
                    final SQLExpression paramSqlExpr = this.stack.pop();
                    sqlExpr = this.exprFactory.invokeMethod(this.stmt, "ARRAY", first, paramSqlExpr, null);
                    this.stack.push(sqlExpr);
                    return sqlExpr;
                }
                this.processParameterExpression(paramExpr, true);
                final SQLExpression paramSqlExpr2 = this.stack.pop();
                final SQLLiteral lit = (SQLLiteral)paramSqlExpr2;
                final Object paramValue = lit.getValue();
                final List<String> tuples = expr.getTuples();
                final Iterator<String> tuplesIter = tuples.iterator();
                Object objValue = paramValue;
                while (tuplesIter.hasNext()) {
                    final String fieldName = tuplesIter.next();
                    objValue = this.getValueForObjectField(objValue, fieldName);
                    this.setNotPrecompilable();
                    if (objValue == null) {
                        break;
                    }
                }
                if (objValue == null) {
                    sqlExpr = this.exprFactory.newLiteral(this.stmt, null, null);
                    this.stack.push(sqlExpr);
                    return sqlExpr;
                }
                final JavaTypeMapping m = this.exprFactory.getMappingForType(objValue.getClass(), false);
                sqlExpr = this.exprFactory.newLiteral(this.stmt, m, objValue);
                this.stack.push(sqlExpr);
                return sqlExpr;
            }
            else if (expr.getLeft() instanceof VariableExpression) {
                final VariableExpression varExpr = (VariableExpression)expr.getLeft();
                this.processVariableExpression(varExpr);
                SQLExpression varSqlExpr = this.stack.pop();
                if (varSqlExpr instanceof UnboundExpression) {
                    this.processUnboundExpression((UnboundExpression)varSqlExpr);
                    varSqlExpr = this.stack.pop();
                }
                final Class varType = this.clr.classForName(varSqlExpr.getJavaTypeMapping().getType());
                if (varSqlExpr.getSQLStatement() == this.stmt.getParentStatement()) {
                    final SQLTableMapping sqlMapping3 = this.parentMapper.getSQLTableMappingForPrimaryExpression(this.stmt, null, expr, Boolean.FALSE);
                    if (sqlMapping3 == null) {
                        throw new NucleusException("PrimaryExpression " + expr.getId() + " is not yet supported");
                    }
                    sqlExpr = this.exprFactory.newExpression(varSqlExpr.getSQLStatement(), sqlMapping3.table, sqlMapping3.mapping);
                    this.stack.push(sqlExpr);
                    return sqlExpr;
                }
                else {
                    final SQLTableMapping varTblMapping = this.getSQLTableMappingForAlias(varExpr.getId());
                    if (varTblMapping == null) {
                        throw new NucleusUserException("Variable " + varExpr.getId() + " is not yet bound, so cannot get field " + expr.getId());
                    }
                    if (varTblMapping.cmd == null) {
                        throw new NucleusUserException("Variable " + varExpr.getId() + " of type " + varType.getName() + " cannot evaluate " + expr.getId());
                    }
                    final SQLTableMapping sqlMapping4 = this.getSQLTableMappingForPrimaryExpression(varSqlExpr.getSQLStatement(), varExpr.getId(), expr, Boolean.FALSE);
                    sqlExpr = this.exprFactory.newExpression(sqlMapping4.table.getSQLStatement(), sqlMapping4.table, sqlMapping4.mapping);
                    this.stack.push(sqlExpr);
                    return sqlExpr;
                }
            }
            else {
                if (!(expr.getLeft() instanceof InvokeExpression)) {
                    throw new NucleusUserException("Dont currently support PrimaryExpression with 'left' of " + expr.getLeft());
                }
                this.processInvokeExpression((InvokeExpression)expr.getLeft());
                final SQLExpression invokeSqlExpr = this.stack.pop();
                final Table tbl = invokeSqlExpr.getSQLTable().getTable();
                if (!(tbl instanceof DatastoreClass)) {
                    throw new NucleusUserException("Dont currently support evaluating " + expr.getId() + " on " + invokeSqlExpr + " with invoke having table of " + tbl);
                }
                if (expr.getTuples().size() > 1) {
                    throw new NucleusUserException("Dont currently support evaluating " + expr.getId() + " on " + invokeSqlExpr);
                }
                final JavaTypeMapping mapping2 = ((DatastoreClass)tbl).getMemberMapping(expr.getId());
                if (mapping2 == null) {
                    throw new NucleusUserException("Dont currently support evaluating " + expr.getId() + " on " + invokeSqlExpr + ". The field " + expr.getId() + " doesnt exist in table " + tbl);
                }
                sqlExpr = this.exprFactory.newExpression(this.stmt, invokeSqlExpr.getSQLTable(), mapping2);
                this.stack.push(sqlExpr);
                return sqlExpr;
            }
        }
        else {
            final SQLTableMapping sqlMapping5 = this.getSQLTableMappingForPrimaryExpression(this.stmt, null, expr, null);
            if (sqlMapping5 == null) {
                throw new NucleusException("PrimaryExpression " + expr.getId() + " is not yet supported");
            }
            sqlExpr = this.exprFactory.newExpression(this.stmt, sqlMapping5.table, sqlMapping5.mapping);
            this.stack.push(sqlExpr);
            return sqlExpr;
        }
    }
    
    private SQLTableMapping getSQLTableMappingForPrimaryExpression(SQLStatement theStmt, final String exprName, final PrimaryExpression primExpr, Boolean forceJoin) {
        if (forceJoin == null && primExpr.getParent() != null && (primExpr.getParent().getOperator() == Expression.OP_IS || primExpr.getParent().getOperator() == Expression.OP_ISNOT)) {
            forceJoin = Boolean.TRUE;
        }
        SQLTableMapping sqlMapping = null;
        final List<String> tuples = primExpr.getTuples();
        final Iterator<String> iter = tuples.iterator();
        final String first = tuples.get(0);
        String primaryName = null;
        if (exprName != null) {
            sqlMapping = this.getSQLTableMappingForAlias(exprName);
            primaryName = exprName;
        }
        else {
            if (this.hasSQLTableMappingForAlias(first)) {
                sqlMapping = this.getSQLTableMappingForAlias(first);
                primaryName = first;
                iter.next();
            }
            if (sqlMapping == null && this.parentMapper != null && this.parentMapper.hasSQLTableMappingForAlias(first)) {
                sqlMapping = this.parentMapper.getSQLTableMappingForAlias(first);
                primaryName = first;
                iter.next();
                theStmt = sqlMapping.table.getSQLStatement();
            }
            if (sqlMapping == null) {
                sqlMapping = this.getSQLTableMappingForAlias(this.candidateAlias);
                primaryName = this.candidateAlias;
            }
        }
        AbstractClassMetaData cmd = sqlMapping.cmd;
        JavaTypeMapping mapping = sqlMapping.mapping;
        while (iter.hasNext()) {
            final String component = iter.next();
            primaryName = primaryName + "." + component;
            SQLTableMapping sqlMappingNew = this.getSQLTableMappingForAlias(primaryName);
            if (sqlMappingNew == null) {
                final AbstractMemberMetaData mmd = cmd.getMetaDataForMember(component);
                if (mmd == null) {
                    throw new NucleusUserException(QueryToSQLMapper.LOCALISER.msg("021062", component, cmd.getFullClassName()));
                }
                if (mmd.getPersistenceModifier() != FieldPersistenceModifier.PERSISTENT) {
                    throw new NucleusUserException("Field " + mmd.getFullFieldName() + " is not marked as persistent so cannot be queried");
                }
                SQLTable sqlTbl = null;
                if (mapping instanceof EmbeddedMapping) {
                    sqlTbl = sqlMapping.table;
                    mapping = ((EmbeddedMapping)mapping).getJavaTypeMapping(component);
                }
                else {
                    DatastoreClass table = this.storeMgr.getDatastoreClass(cmd.getFullClassName(), this.clr);
                    if (table == null) {
                        final AbstractClassMetaData[] subCmds = this.storeMgr.getClassesManagingTableForClass(cmd, this.clr);
                        if (subCmds.length != 1) {
                            throw new NucleusUserException("Unable to find table for primary " + primaryName + " since the class " + cmd.getFullClassName() + " is managed in multiple tables");
                        }
                        table = this.storeMgr.getDatastoreClass(subCmds[0].getFullClassName(), this.clr);
                    }
                    mapping = table.getMemberMapping(mmd);
                    sqlTbl = SQLStatementHelper.getSQLTableForMappingOfTable(theStmt, sqlMapping.table, mapping);
                }
                final RelationType relationType = mmd.getRelationType(this.clr);
                if (relationType == RelationType.NONE) {
                    sqlMappingNew = new SQLTableMapping(sqlTbl, cmd, mapping);
                    cmd = sqlMappingNew.cmd;
                    this.setSQLTableMappingForAlias(primaryName, sqlMappingNew);
                }
                else if (relationType == RelationType.ONE_TO_ONE_UNI || relationType == RelationType.ONE_TO_ONE_BI) {
                    if (mmd.getMappedBy() != null) {
                        final AbstractMemberMetaData relMmd = mmd.getRelatedMemberMetaData(this.clr)[0];
                        if (relMmd.getAbstractClassMetaData().isEmbeddedOnly()) {
                            sqlMappingNew = sqlMapping;
                            cmd = relMmd.getAbstractClassMetaData();
                        }
                        else {
                            final DatastoreClass relTable = this.storeMgr.getDatastoreClass(mmd.getTypeName(), this.clr);
                            final JavaTypeMapping relMapping = relTable.getMemberMapping(relMmd);
                            sqlTbl = theStmt.getTable(relTable, primaryName);
                            if (sqlTbl == null) {
                                sqlTbl = SQLStatementHelper.addJoinForOneToOneRelation(theStmt, sqlMapping.table.getTable().getIdMapping(), sqlMapping.table, relMapping, relTable, null, null, primaryName, this.defaultJoinType);
                            }
                            if (iter.hasNext()) {
                                sqlMappingNew = new SQLTableMapping(sqlTbl, relMmd.getAbstractClassMetaData(), relTable.getIdMapping());
                                cmd = sqlMappingNew.cmd;
                            }
                            else {
                                sqlMappingNew = new SQLTableMapping(sqlTbl, cmd, relTable.getIdMapping());
                                cmd = sqlMappingNew.cmd;
                            }
                        }
                    }
                    else {
                        if (forceJoin == null && !iter.hasNext() && primExpr.getParent() != null && primExpr.getParent().getOperator() == Expression.OP_CAST && !(mapping instanceof ReferenceMapping)) {
                            forceJoin = Boolean.TRUE;
                        }
                        if (iter.hasNext() || Boolean.TRUE.equals(forceJoin)) {
                            AbstractClassMetaData relCmd = null;
                            JavaTypeMapping relMapping2 = null;
                            DatastoreClass relTable2 = null;
                            if (relationType == RelationType.ONE_TO_ONE_BI) {
                                final AbstractMemberMetaData relMmd2 = mmd.getRelatedMemberMetaData(this.clr)[0];
                                relCmd = relMmd2.getAbstractClassMetaData();
                            }
                            else {
                                relCmd = this.ec.getMetaDataManager().getMetaDataForClass(mmd.getTypeName(), this.clr);
                            }
                            if (relCmd != null && relCmd.isEmbeddedOnly()) {
                                sqlMappingNew = new SQLTableMapping(sqlTbl, relCmd, mapping);
                                cmd = relCmd;
                            }
                            else {
                                relTable2 = this.storeMgr.getDatastoreClass(relCmd.getFullClassName(), this.clr);
                                relMapping2 = relTable2.getIdMapping();
                                sqlTbl = theStmt.getTable(relTable2, primaryName);
                                if (sqlTbl == null) {
                                    sqlTbl = SQLStatementHelper.addJoinForOneToOneRelation(theStmt, mapping, sqlMapping.table, relMapping2, relTable2, null, null, primaryName, this.defaultJoinType);
                                }
                                sqlMappingNew = new SQLTableMapping(sqlTbl, relCmd, relMapping2);
                                cmd = sqlMappingNew.cmd;
                                this.setSQLTableMappingForAlias(primaryName, sqlMappingNew);
                            }
                        }
                        else {
                            sqlMappingNew = new SQLTableMapping(sqlTbl, cmd, mapping);
                            cmd = sqlMappingNew.cmd;
                        }
                    }
                }
                else if (relationType == RelationType.MANY_TO_ONE_BI) {
                    final AbstractMemberMetaData relMmd = mmd.getRelatedMemberMetaData(this.clr)[0];
                    final DatastoreClass relTable = this.storeMgr.getDatastoreClass(mmd.getTypeName(), this.clr);
                    if (mmd.getJoinMetaData() != null || relMmd.getJoinMetaData() != null) {
                        sqlTbl = theStmt.getTable(relTable, primaryName);
                        if (sqlTbl == null) {
                            final CollectionTable joinTbl = (CollectionTable)this.storeMgr.getTable(relMmd);
                            if (this.defaultJoinType == SQLJoin.JoinType.INNER_JOIN) {
                                final SQLTable joinSqlTbl = theStmt.innerJoin(sqlMapping.table, sqlMapping.table.getTable().getIdMapping(), joinTbl, null, joinTbl.getElementMapping(), null, null);
                                sqlTbl = theStmt.innerJoin(joinSqlTbl, joinTbl.getOwnerMapping(), relTable, null, relTable.getIdMapping(), null, primaryName);
                            }
                            else if (this.defaultJoinType == SQLJoin.JoinType.LEFT_OUTER_JOIN || this.defaultJoinType == null) {
                                final SQLTable joinSqlTbl = theStmt.leftOuterJoin(sqlMapping.table, sqlMapping.table.getTable().getIdMapping(), joinTbl, null, joinTbl.getElementMapping(), null, null);
                                sqlTbl = theStmt.leftOuterJoin(joinSqlTbl, joinTbl.getOwnerMapping(), relTable, null, relTable.getIdMapping(), null, primaryName);
                            }
                        }
                        sqlMappingNew = new SQLTableMapping(sqlTbl, relMmd.getAbstractClassMetaData(), relTable.getIdMapping());
                        cmd = sqlMappingNew.cmd;
                        this.setSQLTableMappingForAlias(primaryName, sqlMappingNew);
                    }
                    else {
                        sqlTbl = theStmt.getTable(relTable, primaryName);
                        if (sqlTbl == null) {
                            final Expression.Operator op = (primExpr.getParent() != null) ? primExpr.getParent().getOperator() : null;
                            if (!iter.hasNext() && (op == Expression.OP_EQ || op == Expression.OP_GT || op == Expression.OP_LT || op == Expression.OP_GTEQ || op == Expression.OP_LTEQ || op == Expression.OP_NOTEQ)) {
                                sqlMappingNew = new SQLTableMapping(sqlMapping.table, relMmd.getAbstractClassMetaData(), mapping);
                            }
                            else {
                                if (this.defaultJoinType == SQLJoin.JoinType.INNER_JOIN) {
                                    sqlTbl = theStmt.innerJoin(sqlMapping.table, mapping, relTable, null, relTable.getIdMapping(), null, primaryName);
                                }
                                else if (this.defaultJoinType == SQLJoin.JoinType.LEFT_OUTER_JOIN || this.defaultJoinType == null) {
                                    sqlTbl = theStmt.leftOuterJoin(sqlMapping.table, mapping, relTable, null, relTable.getIdMapping(), null, primaryName);
                                }
                                sqlMappingNew = new SQLTableMapping(sqlTbl, relMmd.getAbstractClassMetaData(), relTable.getIdMapping());
                                cmd = sqlMappingNew.cmd;
                                this.setSQLTableMappingForAlias(primaryName, sqlMappingNew);
                            }
                        }
                        else {
                            sqlMappingNew = new SQLTableMapping(sqlTbl, relMmd.getAbstractClassMetaData(), relTable.getIdMapping());
                            cmd = sqlMappingNew.cmd;
                            this.setSQLTableMappingForAlias(primaryName, sqlMappingNew);
                        }
                    }
                }
                else if (relationType == RelationType.ONE_TO_MANY_UNI || relationType == RelationType.ONE_TO_MANY_BI || relationType == RelationType.MANY_TO_MANY_BI) {
                    sqlMappingNew = new SQLTableMapping(sqlTbl, cmd, mapping);
                    cmd = sqlMappingNew.cmd;
                    this.setSQLTableMappingForAlias(primaryName, sqlMappingNew);
                }
            }
            else {
                cmd = sqlMappingNew.cmd;
            }
            sqlMapping = sqlMappingNew;
        }
        return sqlMapping;
    }
    
    @Override
    protected Object processParameterExpression(final ParameterExpression expr) {
        return this.processParameterExpression(expr, false);
    }
    
    protected Object processParameterExpression(final ParameterExpression expr, boolean asLiteral) {
        if (this.compileComponent == CompilationComponent.ORDERING) {
            asLiteral = true;
        }
        if (expr.getPosition() >= 0) {
            if (this.paramNameByPosition == null) {
                this.paramNameByPosition = new HashMap<Integer, String>();
            }
            this.paramNameByPosition.put(expr.getPosition(), expr.getId());
        }
        Object paramValue = null;
        boolean paramValueSet = false;
        if (this.parameters != null && this.parameters.size() > 0) {
            if (this.parameters.containsKey(expr.getId())) {
                paramValue = this.parameters.get(expr.getId());
                paramValueSet = true;
            }
            else if (this.parameterValueByName != null && this.parameterValueByName.containsKey(expr.getId())) {
                paramValue = this.parameterValueByName.get(expr.getId());
                paramValueSet = true;
            }
            else {
                int position = this.positionalParamNumber;
                if (this.positionalParamNumber < 0) {
                    position = 0;
                }
                if (this.parameters.containsKey(position)) {
                    paramValue = this.parameters.get(position);
                    paramValueSet = true;
                    this.positionalParamNumber = position + 1;
                    if (this.parameterValueByName == null) {
                        this.parameterValueByName = new HashMap<String, Object>();
                    }
                    this.parameterValueByName.put(expr.getId(), paramValue);
                }
            }
        }
        JavaTypeMapping m = this.paramMappingForName.get(expr.getId());
        if (m == null) {
            if (paramValue != null) {
                final String className = this.storeMgr.getClassNameForObjectID(paramValue, this.clr, this.ec);
                if (className != null) {
                    final AbstractClassMetaData cmd = this.storeMgr.getMetaDataManager().getMetaDataForClass(className, this.clr);
                    if (cmd.getIdentityType() == IdentityType.APPLICATION) {
                        final Class cls = this.clr.classForName(className);
                        m = this.exprFactory.getMappingForType(cls, false);
                        m = new PersistableIdMapping((PersistableMapping)m);
                    }
                }
                if (m == null) {
                    m = this.exprFactory.getMappingForType(paramValue.getClass(), false);
                }
                if (expr.getSymbol() != null && expr.getSymbol().getValueType() != null) {
                    if (!QueryUtils.queryParameterTypesAreCompatible(expr.getSymbol().getValueType(), paramValue.getClass())) {
                        throw new QueryCompilerSyntaxException(QueryToSQLMapper.LOCALISER.msg("021118", expr.getId(), expr.getSymbol().getValueType().getName(), paramValue.getClass().getName()));
                    }
                    if (expr.getSymbol().getValueType() != paramValue.getClass()) {
                        this.setNotPrecompilable();
                    }
                }
            }
            else if (expr.getSymbol() != null && expr.getSymbol().getValueType() != null) {
                m = this.exprFactory.getMappingForType(expr.getSymbol().getValueType(), false);
            }
        }
        if (asLiteral && m != null && !m.representableAsStringLiteralInStatement()) {
            asLiteral = false;
        }
        if (asLiteral) {
            if (this.isPrecompilable()) {
                NucleusLogger.QUERY.debug("Parameter " + expr + " is being resolved as a literal, so the query is no longer precompilable");
            }
            this.setNotPrecompilable();
        }
        else if (paramValue == null && expr.getSymbol() != null) {
            if (this.isPrecompilable()) {
                NucleusLogger.QUERY.debug("Parameter " + expr + " is set to null so this has to " + " be resolved as a NullLiteral, and the query is no longer precompilable");
            }
            this.setNotPrecompilable();
        }
        SQLExpression sqlExpr = null;
        if (paramValueSet && paramValue == null) {
            sqlExpr = this.exprFactory.newLiteral(this.stmt, null, null);
        }
        else if (asLiteral) {
            sqlExpr = this.exprFactory.newLiteral(this.stmt, m, paramValue);
        }
        else {
            sqlExpr = this.exprFactory.newLiteralParameter(this.stmt, m, paramValue, expr.getId());
            if (sqlExpr instanceof ParameterLiteral) {
                ((ParameterLiteral)sqlExpr).setName(expr.getId());
            }
            if (this.expressionForParameter == null) {
                this.expressionForParameter = new HashMap<Object, SQLExpression>();
            }
            this.expressionForParameter.put(expr.getId(), sqlExpr);
            this.paramMappingForName.put(expr.getId(), m);
        }
        this.stack.push(sqlExpr);
        return sqlExpr;
    }
    
    @Override
    protected Object processInvokeExpression(final InvokeExpression expr) {
        final Expression invokedExpr = expr.getLeft();
        SQLExpression invokedSqlExpr = null;
        if (invokedExpr != null) {
            if (invokedExpr instanceof PrimaryExpression) {
                this.processPrimaryExpression((PrimaryExpression)invokedExpr);
                invokedSqlExpr = this.stack.pop();
            }
            else if (invokedExpr instanceof Literal) {
                this.processLiteral((Literal)invokedExpr);
                invokedSqlExpr = this.stack.pop();
            }
            else if (invokedExpr instanceof ParameterExpression) {
                this.processParameterExpression((ParameterExpression)invokedExpr, true);
                invokedSqlExpr = this.stack.pop();
            }
            else if (invokedExpr instanceof InvokeExpression) {
                this.processInvokeExpression((InvokeExpression)invokedExpr);
                invokedSqlExpr = this.stack.pop();
            }
            else if (invokedExpr instanceof VariableExpression) {
                this.processVariableExpression((VariableExpression)invokedExpr);
                invokedSqlExpr = this.stack.pop();
            }
            else if (invokedExpr instanceof ArrayExpression) {
                final ArrayExpression arrExpr = (ArrayExpression)invokedExpr;
                final SQLExpression[] arrSqlExprs = new SQLExpression[arrExpr.getArraySize()];
                for (int i = 0; i < arrExpr.getArraySize(); ++i) {
                    final Expression arrElemExpr = arrExpr.getElement(i);
                    arrElemExpr.evaluate(this);
                    arrSqlExprs[i] = this.stack.pop();
                }
                final JavaTypeMapping m = this.exprFactory.getMappingForType(Object[].class, false);
                invokedSqlExpr = new org.datanucleus.store.rdbms.sql.expression.ArrayExpression(this.stmt, m, arrSqlExprs);
            }
            else {
                if (!(invokedExpr instanceof DyadicExpression)) {
                    throw new NucleusException("Dont currently support invoke expression " + invokedExpr);
                }
                final DyadicExpression dyExpr = (DyadicExpression)invokedExpr;
                dyExpr.evaluate(this);
                invokedSqlExpr = this.stack.pop();
            }
        }
        final String operation = expr.getOperation();
        final List args = expr.getArguments();
        List sqlExprArgs = null;
        if (args != null) {
            sqlExprArgs = new ArrayList();
            for (final Expression argExpr : args) {
                if (argExpr instanceof PrimaryExpression) {
                    this.processPrimaryExpression((PrimaryExpression)argExpr);
                    final SQLExpression argSqlExpr = this.stack.pop();
                    if (this.compileComponent == CompilationComponent.RESULT && operation.equalsIgnoreCase("count") && this.stmt.getNumberOfTableGroups() > 1 && argSqlExpr.getSQLTable() == this.stmt.getPrimaryTable() && argSqlExpr.getJavaTypeMapping() == this.stmt.getPrimaryTable().getTable().getIdMapping()) {
                        argSqlExpr.distinct();
                    }
                    sqlExprArgs.add(argSqlExpr);
                }
                else if (argExpr instanceof ParameterExpression) {
                    this.processParameterExpression((ParameterExpression)argExpr);
                    sqlExprArgs.add(this.stack.pop());
                }
                else if (argExpr instanceof InvokeExpression) {
                    this.processInvokeExpression((InvokeExpression)argExpr);
                    sqlExprArgs.add(this.stack.pop());
                }
                else if (argExpr instanceof Literal) {
                    this.processLiteral((Literal)argExpr);
                    sqlExprArgs.add(this.stack.pop());
                }
                else if (argExpr instanceof DyadicExpression) {
                    argExpr.evaluate(this);
                    sqlExprArgs.add(this.stack.pop());
                }
                else if (argExpr instanceof VariableExpression) {
                    this.processVariableExpression((VariableExpression)argExpr);
                    sqlExprArgs.add(this.stack.pop());
                }
                else {
                    if (!(argExpr instanceof CaseExpression)) {
                        throw new NucleusException("Dont currently support invoke expression argument " + argExpr);
                    }
                    this.processCaseExpression((CaseExpression)argExpr);
                    sqlExprArgs.add(this.stack.pop());
                }
            }
            if (expr.getOperation() != null && expr.getOperation().equals("INDEX")) {
                final List<Expression> indexArgs = expr.getArguments();
                if (indexArgs == null || indexArgs.size() > 1) {
                    throw new NucleusException("Can only use INDEX with single argument");
                }
                final PrimaryExpression indexExpr = indexArgs.get(0);
                String collExprName;
                final String joinAlias = collExprName = indexExpr.getId();
                if (this.explicitJoinPrimaryByAlias != null) {
                    collExprName = this.explicitJoinPrimaryByAlias.get(joinAlias);
                    if (collExprName == null) {
                        throw new NucleusException("Unable to locate primary expression for alias " + joinAlias);
                    }
                }
                final List<String> tuples = new ArrayList<String>();
                final StringTokenizer primTokenizer = new StringTokenizer(collExprName, ".");
                while (primTokenizer.hasMoreTokens()) {
                    final String token = primTokenizer.nextToken();
                    tuples.add(token);
                }
                final PrimaryExpression collPrimExpr = new PrimaryExpression(tuples);
                this.processPrimaryExpression(collPrimExpr);
                final SQLExpression collSqlExpr = this.stack.pop();
                sqlExprArgs.add(collSqlExpr);
            }
        }
        SQLExpression sqlExpr = null;
        if (invokedSqlExpr != null) {
            sqlExpr = invokedSqlExpr.invoke(operation, sqlExprArgs);
        }
        else {
            sqlExpr = this.exprFactory.invokeMethod(this.stmt, null, operation, null, sqlExprArgs);
        }
        this.stack.push(sqlExpr);
        return sqlExpr;
    }
    
    @Override
    protected Object processSubqueryExpression(final SubqueryExpression expr) {
        final String keyword = expr.getKeyword();
        final Expression subqueryExpr = expr.getRight();
        if (subqueryExpr instanceof VariableExpression) {
            this.processVariableExpression((VariableExpression)subqueryExpr);
            SQLExpression subquerySqlExpr = this.stack.pop();
            if (keyword != null && keyword.equals("EXISTS")) {
                if (subquerySqlExpr instanceof org.datanucleus.store.rdbms.sql.expression.SubqueryExpression) {
                    final SQLStatement subStmt = ((org.datanucleus.store.rdbms.sql.expression.SubqueryExpression)subquerySqlExpr).getSubqueryStatement();
                    subquerySqlExpr = new BooleanSubqueryExpression(this.stmt, keyword, subStmt);
                }
                else {
                    final SQLStatement subStmt = ((SubqueryExpressionComponent)subquerySqlExpr).getSubqueryStatement();
                    subquerySqlExpr = new BooleanSubqueryExpression(this.stmt, keyword, subStmt);
                }
            }
            else if (subquerySqlExpr instanceof org.datanucleus.store.rdbms.sql.expression.SubqueryExpression) {
                final SQLStatement subStmt = ((org.datanucleus.store.rdbms.sql.expression.SubqueryExpression)subquerySqlExpr).getSubqueryStatement();
                subquerySqlExpr = new BooleanSubqueryExpression(this.stmt, keyword, subStmt);
            }
            else if (subquerySqlExpr instanceof NumericSubqueryExpression) {
                ((NumericSubqueryExpression)subquerySqlExpr).setKeyword(keyword);
            }
            this.stack.push(subquerySqlExpr);
            return subquerySqlExpr;
        }
        throw new NucleusException("Dont currently support SubqueryExpression " + keyword + " for type " + subqueryExpr);
    }
    
    @Override
    protected Object processAddExpression(final Expression expr) {
        SQLExpression right = this.stack.pop();
        SQLExpression left = this.stack.pop();
        if (left instanceof ParameterLiteral && !(right instanceof ParameterLiteral)) {
            left = this.replaceParameterLiteral((ParameterLiteral)left, right.getJavaTypeMapping());
        }
        else if (right instanceof ParameterLiteral && !(left instanceof ParameterLiteral)) {
            right = this.replaceParameterLiteral((ParameterLiteral)right, left.getJavaTypeMapping());
        }
        final SQLExpression resultExpr = left.add(right);
        this.stack.push(resultExpr);
        return resultExpr;
    }
    
    @Override
    protected Object processDivExpression(final Expression expr) {
        final SQLExpression right = this.stack.pop();
        final SQLExpression left = this.stack.pop();
        final SQLExpression resultExpr = left.div(right);
        this.stack.push(resultExpr);
        return resultExpr;
    }
    
    @Override
    protected Object processMulExpression(final Expression expr) {
        final SQLExpression right = this.stack.pop();
        final SQLExpression left = this.stack.pop();
        final SQLExpression resultExpr = left.mul(right);
        this.stack.push(resultExpr);
        return resultExpr;
    }
    
    @Override
    protected Object processSubExpression(final Expression expr) {
        SQLExpression right = this.stack.pop();
        SQLExpression left = this.stack.pop();
        if (left instanceof ParameterLiteral && !(right instanceof ParameterLiteral)) {
            left = this.replaceParameterLiteral((ParameterLiteral)left, right.getJavaTypeMapping());
        }
        else if (right instanceof ParameterLiteral && !(left instanceof ParameterLiteral)) {
            right = this.replaceParameterLiteral((ParameterLiteral)right, left.getJavaTypeMapping());
        }
        final SQLExpression resultExpr = left.sub(right);
        this.stack.push(resultExpr);
        return resultExpr;
    }
    
    @Override
    protected Object processDistinctExpression(final Expression expr) {
        final SQLExpression sqlExpr = this.stack.pop();
        sqlExpr.distinct();
        this.stack.push(sqlExpr);
        return sqlExpr;
    }
    
    @Override
    protected Object processComExpression(final Expression expr) {
        final SQLExpression sqlExpr = this.stack.pop();
        final SQLExpression resultExpr = sqlExpr.com();
        this.stack.push(resultExpr);
        return resultExpr;
    }
    
    @Override
    protected Object processModExpression(final Expression expr) {
        final SQLExpression right = this.stack.pop();
        final SQLExpression left = this.stack.pop();
        final SQLExpression resultExpr = left.mod(right);
        this.stack.push(resultExpr);
        return resultExpr;
    }
    
    @Override
    protected Object processNegExpression(final Expression expr) {
        final SQLExpression sqlExpr = this.stack.pop();
        final SQLExpression resultExpr = sqlExpr.neg();
        this.stack.push(resultExpr);
        return resultExpr;
    }
    
    @Override
    protected Object processNotExpression(final Expression expr) {
        final SQLExpression sqlExpr = this.stack.pop();
        final SQLExpression resultExpr = sqlExpr.not();
        this.stack.push(resultExpr);
        return resultExpr;
    }
    
    @Override
    protected Object processCastExpression(final Expression expr) {
        final SQLExpression right = this.stack.pop();
        final SQLExpression left = this.stack.pop();
        final SQLExpression castExpr = left.cast(right);
        this.stack.push(castExpr);
        return castExpr;
    }
    
    @Override
    protected Object processCaseExpression(final CaseExpression expr) {
        final Map<Expression, Expression> conditions = expr.getConditions();
        final Iterator<Map.Entry<Expression, Expression>> whenExprIter = conditions.entrySet().iterator();
        final SQLExpression[] whenSqlExprs = new SQLExpression[conditions.size()];
        final SQLExpression[] actionSqlExprs = new SQLExpression[conditions.size()];
        int i = 0;
        while (whenExprIter.hasNext()) {
            final Map.Entry<Expression, Expression> entry = whenExprIter.next();
            final Expression whenExpr = entry.getKey();
            whenExpr.evaluate(this);
            whenSqlExprs[i] = this.stack.pop();
            final Expression actionExpr = entry.getValue();
            actionExpr.evaluate(this);
            actionSqlExprs[i] = this.stack.pop();
            ++i;
        }
        final Expression elseExpr = expr.getElseExpression();
        elseExpr.evaluate(this);
        final SQLExpression elseSqlExpr = this.stack.pop();
        final SQLExpression caseSqlExpr = new org.datanucleus.store.rdbms.sql.expression.CaseExpression(whenSqlExprs, actionSqlExprs, elseSqlExpr);
        this.stack.push(caseSqlExpr);
        return caseSqlExpr;
    }
    
    @Override
    protected Object processIsExpression(final Expression expr) {
        final SQLExpression right = this.stack.pop();
        final SQLExpression left = this.stack.pop();
        final SQLExpression instanceofExpr = left.is(right, false);
        this.stack.push(instanceofExpr);
        return instanceofExpr;
    }
    
    @Override
    protected Object processIsnotExpression(final Expression expr) {
        final SQLExpression right = this.stack.pop();
        final SQLExpression left = this.stack.pop();
        final SQLExpression instanceofExpr = left.is(right, true);
        this.stack.push(instanceofExpr);
        return instanceofExpr;
    }
    
    @Override
    protected Object processInExpression(final Expression expr) {
        final SQLExpression right = this.stack.pop();
        final SQLExpression left = this.stack.pop();
        final SQLExpression inExpr = left.in(right, false);
        this.stack.push(inExpr);
        return inExpr;
    }
    
    @Override
    protected Object processNotInExpression(final Expression expr) {
        final SQLExpression right = this.stack.pop();
        final SQLExpression left = this.stack.pop();
        final SQLExpression inExpr = left.in(right, true);
        this.stack.push(inExpr);
        return inExpr;
    }
    
    @Override
    protected Object processCreatorExpression(final CreatorExpression expr) {
        final String className = expr.getId();
        Class cls = null;
        try {
            cls = this.clr.classForName(className);
        }
        catch (ClassNotResolvedException cnre) {
            if (this.importsDefinition != null) {
                cls = this.importsDefinition.resolveClassDeclaration(className, this.clr, null);
            }
        }
        List<SQLExpression> ctrArgExprs = null;
        final List args = expr.getArguments();
        if (args != null) {
            final Class[] ctrArgTypes = new Class[args.size()];
            ctrArgExprs = new ArrayList<SQLExpression>(args.size());
            final Iterator iter = args.iterator();
            int i = 0;
            while (iter.hasNext()) {
                final Expression argExpr = iter.next();
                final SQLExpression sqlExpr = (SQLExpression)this.evaluate(argExpr);
                ctrArgExprs.add(sqlExpr);
                if (sqlExpr instanceof NewObjectExpression) {
                    ctrArgTypes[i] = ((NewObjectExpression)sqlExpr).getNewClass();
                }
                else if (sqlExpr.getJavaTypeMapping() instanceof OIDMapping || sqlExpr.getJavaTypeMapping() instanceof PersistableMapping) {
                    ctrArgTypes[i] = this.clr.classForName(sqlExpr.getJavaTypeMapping().getType());
                }
                else {
                    ctrArgTypes[i] = sqlExpr.getJavaTypeMapping().getJavaType();
                }
                ++i;
            }
            final Constructor ctr = ClassUtils.getConstructorWithArguments(cls, ctrArgTypes);
            if (ctr == null) {
                throw new NucleusUserException(QueryToSQLMapper.LOCALISER.msg("021033", className, StringUtils.objectArrayToString(ctrArgTypes)));
            }
        }
        final NewObjectExpression newExpr = new NewObjectExpression(this.stmt, cls, ctrArgExprs);
        this.stack.push(newExpr);
        return newExpr;
    }
    
    @Override
    protected Object processLikeExpression(final Expression expr) {
        final SQLExpression right = this.stack.pop();
        final SQLExpression left = this.stack.pop();
        final List args = new ArrayList();
        args.add(right);
        final SQLExpression likeExpr = this.exprFactory.invokeMethod(this.stmt, String.class.getName(), "like", left, args);
        this.stack.push(likeExpr);
        return likeExpr;
    }
    
    @Override
    protected Object processVariableExpression(final VariableExpression expr) {
        String varName = expr.getId();
        final Symbol varSym = expr.getSymbol();
        if (varSym != null) {
            varName = varSym.getQualifiedName();
        }
        if (this.hasSQLTableMappingForAlias(varName)) {
            final SQLTableMapping tblMapping = this.getSQLTableMappingForAlias(varName);
            final SQLExpression sqlExpr = this.exprFactory.newExpression(tblMapping.table.getSQLStatement(), tblMapping.table, tblMapping.mapping);
            this.stack.push(sqlExpr);
            return sqlExpr;
        }
        if (this.compilation.getCompilationForSubquery(varName) != null) {
            final QueryCompilation subCompilation = this.compilation.getCompilationForSubquery(varName);
            final AbstractClassMetaData subCmd = this.ec.getMetaDataManager().getMetaDataForClass(subCompilation.getCandidateClass(), this.ec.getClassLoaderResolver());
            String subAlias = null;
            if (subCompilation.getCandidateAlias() != null && !subCompilation.getCandidateAlias().equals(this.candidateAlias)) {
                subAlias = subCompilation.getCandidateAlias();
            }
            final StatementResultMapping subqueryResultMapping = new StatementResultMapping();
            final SQLStatement subStmt = RDBMSQueryUtils.getStatementForCandidates(this.storeMgr, this.stmt, subCmd, null, this.ec, subCompilation.getCandidateClass(), true, "avg(something)", subAlias, null);
            final QueryToSQLMapper sqlMapper = new QueryToSQLMapper(subStmt, subCompilation, this.parameters, null, subqueryResultMapping, subCmd, this.fetchPlan, this.ec, this.importsDefinition, this.options, this.extensionsByName);
            sqlMapper.setParentMapper(this);
            sqlMapper.compile();
            if (subqueryResultMapping.getNumberOfResultExpressions() > 1) {
                throw new NucleusUserException("Number of result expressions in subquery should be 1");
            }
            SQLExpression subExpr = null;
            if (subqueryResultMapping.getNumberOfResultExpressions() == 0) {
                subExpr = new org.datanucleus.store.rdbms.sql.expression.SubqueryExpression(this.stmt, subStmt);
            }
            else {
                final JavaTypeMapping subMapping = ((StatementMappingIndex)subqueryResultMapping.getMappingForResultExpression(0)).getMapping();
                if (subMapping instanceof TemporalMapping) {
                    subExpr = new TemporalSubqueryExpression(this.stmt, subStmt);
                }
                else if (subMapping instanceof StringMapping) {
                    subExpr = new StringSubqueryExpression(this.stmt, subStmt);
                }
                else {
                    subExpr = new NumericSubqueryExpression(this.stmt, subStmt);
                }
                if (subExpr.getJavaTypeMapping() == null) {
                    subExpr.setJavaTypeMapping(subMapping);
                }
            }
            this.stack.push(subExpr);
            return subExpr;
        }
        else {
            if (this.stmt.getParentStatement() != null && this.parentMapper != null && this.parentMapper.candidateAlias != null && this.parentMapper.candidateAlias.equals(varName)) {
                final SQLExpression varExpr = this.exprFactory.newExpression(this.stmt.getParentStatement(), this.stmt.getParentStatement().getPrimaryTable(), this.stmt.getParentStatement().getPrimaryTable().getTable().getIdMapping());
                this.stack.push(varExpr);
                return varExpr;
            }
            NucleusLogger.QUERY.debug("QueryToSQL.processVariable (unbound) variable=" + varName + " is not yet bound so returning UnboundExpression");
            final UnboundExpression unbExpr = new UnboundExpression(this.stmt, varName);
            this.stack.push(unbExpr);
            return unbExpr;
        }
    }
    
    protected SQLExpression processUnboundExpression(final UnboundExpression expr) {
        final String varName = expr.getVariableName();
        final Symbol varSym = this.compilation.getSymbolTable().getSymbol(varName);
        final SQLExpression sqlExpr = this.bindVariable(expr, varSym.getValueType());
        if (sqlExpr != null) {
            this.stack.push(sqlExpr);
            return sqlExpr;
        }
        throw new NucleusUserException("Variable " + varName + " is unbound and cannot be determined");
    }
    
    protected SQLExpression replaceParameterLiteral(final ParameterLiteral paramLit, final JavaTypeMapping mapping) {
        return this.exprFactory.newLiteralParameter(this.stmt, mapping, paramLit.getValue(), paramLit.getParameterName());
    }
    
    @Override
    public void useParameterExpressionAsLiteral(final SQLLiteral paramLiteral) {
        paramLiteral.setNotParameter();
        this.setNotPrecompilable();
    }
    
    @Override
    public boolean hasExtension(final String key) {
        return this.extensionsByName != null && this.extensionsByName.containsKey(key);
    }
    
    @Override
    public Object getValueForExtension(final String key) {
        return (this.extensionsByName == null) ? null : this.extensionsByName.get(key);
    }
    
    public SQLJoin.JoinType getRequiredJoinTypeForAlias(final String alias) {
        if (alias == null) {
            return null;
        }
        if (alias.equals(this.candidateAlias)) {
            return null;
        }
        final String extensionName = "datanucleus.query.jdoql." + alias + ".join";
        SQLJoin.JoinType joinType = null;
        if (this.hasExtension(extensionName)) {
            final String joinValue = (String)this.getValueForExtension(extensionName);
            if (joinValue.equalsIgnoreCase("INNERJOIN")) {
                joinType = SQLJoin.JoinType.INNER_JOIN;
            }
            else if (joinValue.equalsIgnoreCase("LEFTOUTERJOIN")) {
                joinType = SQLJoin.JoinType.LEFT_OUTER_JOIN;
            }
        }
        return joinType;
    }
    
    protected Object getValueForObjectField(final Object obj, final String fieldName) {
        if (obj != null) {
            Object paramFieldValue = null;
            if (this.ec.getApiAdapter().isPersistable(obj)) {
                final ObjectProvider paramSM = this.ec.findObjectProvider(obj);
                final AbstractClassMetaData paramCmd = this.ec.getMetaDataManager().getMetaDataForClass(obj.getClass(), this.clr);
                final AbstractMemberMetaData paramFieldMmd = paramCmd.getMetaDataForMember(fieldName);
                if (paramSM != null) {
                    paramSM.isLoaded(paramFieldMmd.getAbsoluteFieldNumber());
                    paramFieldValue = paramSM.provideField(paramFieldMmd.getAbsoluteFieldNumber());
                }
                else {
                    paramFieldValue = ClassUtils.getValueOfFieldByReflection(obj, fieldName);
                }
            }
            else {
                paramFieldValue = ClassUtils.getValueOfFieldByReflection(obj, fieldName);
            }
            return paramFieldValue;
        }
        return null;
    }
    
    protected SQLTableMapping getSQLTableMappingForAlias(final String alias) {
        if (alias == null) {
            return null;
        }
        if (this.options.contains("CASE_INSENSITIVE")) {
            return this.sqlTableByPrimary.get(alias.toUpperCase());
        }
        return this.sqlTableByPrimary.get(alias);
    }
    
    @Override
    public SQLTable getSQLTableForAlias(final String alias) {
        final SQLTableMapping tblMapping = this.getSQLTableMappingForAlias(alias);
        return (tblMapping != null) ? tblMapping.table : null;
    }
    
    public String getAliasForSQLTable(final SQLTable tbl) {
        final Iterator<Map.Entry<String, SQLTableMapping>> iter = this.sqlTableByPrimary.entrySet().iterator();
        String alias = null;
        while (iter.hasNext()) {
            final Map.Entry<String, SQLTableMapping> entry = iter.next();
            if (entry.getValue().table == tbl) {
                if (alias == null) {
                    alias = entry.getKey();
                }
                else {
                    if (entry.getKey().length() >= alias.length()) {
                        continue;
                    }
                    alias = entry.getKey();
                }
            }
        }
        return alias;
    }
    
    protected void setSQLTableMappingForAlias(final String alias, final SQLTableMapping mapping) {
        if (alias == null) {
            return;
        }
        if (this.options.contains("CASE_INSENSITIVE")) {
            this.sqlTableByPrimary.put(alias.toUpperCase(), mapping);
        }
        else {
            this.sqlTableByPrimary.put(alias, mapping);
        }
    }
    
    protected boolean hasSQLTableMappingForAlias(final String alias) {
        if (this.options.contains("CASE_INSENSITIVE")) {
            return this.sqlTableByPrimary.containsKey(alias.toUpperCase());
        }
        return this.sqlTableByPrimary.containsKey(alias);
    }
    
    @Override
    public void bindVariable(final String varName, final AbstractClassMetaData cmd, final SQLTable sqlTbl, final JavaTypeMapping mapping) {
        SQLTableMapping m = this.getSQLTableMappingForAlias(varName);
        if (m != null) {
            throw new NucleusException("Variable " + varName + " is already bound to " + m.table + " yet attempting to bind to " + sqlTbl);
        }
        NucleusLogger.QUERY.debug("QueryToSQL.bindVariable variable " + varName + " being bound to table=" + sqlTbl + " mapping=" + mapping);
        m = new SQLTableMapping(sqlTbl, cmd, mapping);
        this.setSQLTableMappingForAlias(varName, m);
    }
    
    @Override
    public SQLExpression bindVariable(final UnboundExpression expr, final Class type) {
        final String varName = expr.getVariableName();
        final Symbol varSym = this.compilation.getSymbolTable().getSymbol(varName);
        if (varSym.getValueType() == null) {
            varSym.setValueType(type);
        }
        final AbstractClassMetaData cmd = this.ec.getMetaDataManager().getMetaDataForClass(type, this.clr);
        if (cmd != null) {
            final DatastoreClass varTable = this.storeMgr.getDatastoreClass(varSym.getValueType().getName(), this.clr);
            final SQLTable varSqlTbl = this.stmt.crossJoin(varTable, "VAR_" + varName, null);
            final SQLTableMapping varSqlTblMapping = new SQLTableMapping(varSqlTbl, cmd, varTable.getIdMapping());
            this.setSQLTableMappingForAlias(varName, varSqlTblMapping);
            return this.exprFactory.newExpression(this.stmt, varSqlTbl, varTable.getIdMapping());
        }
        return null;
    }
    
    @Override
    public void bindParameter(final String paramName, final Class type) {
        final Symbol paramSym = this.compilation.getSymbolTable().getSymbol(paramName);
        if (paramSym != null && paramSym.getValueType() == null) {
            paramSym.setValueType(type);
        }
    }
    
    @Override
    public Class getTypeOfVariable(final String varName) {
        final Symbol sym = this.compilation.getSymbolTable().getSymbol(varName);
        if (sym != null && sym.getValueType() != null) {
            return sym.getValueType();
        }
        return null;
    }
    
    @Override
    public boolean hasExplicitJoins() {
        return this.options.contains("EXPLICIT_JOINS");
    }
    
    protected BooleanExpression getBooleanExpressionForUseInFilter(final BooleanExpression expr) {
        if (this.compileComponent != CompilationComponent.FILTER) {
            return expr;
        }
        if (!expr.hasClosure()) {
            return new BooleanExpression(expr, Expression.OP_EQ, new BooleanLiteral(this.stmt, expr.getJavaTypeMapping(), Boolean.TRUE, null));
        }
        return expr;
    }
    
    @Override
    public Class resolveClass(final String className) {
        Class cls = null;
        try {
            cls = this.clr.classForName(className);
        }
        catch (ClassNotResolvedException cnre) {
            if (this.importsDefinition != null) {
                cls = this.importsDefinition.resolveClassDeclaration(className, this.clr, null);
            }
        }
        if (cls == null && this.compilation.getQueryLanguage().equalsIgnoreCase("JPQL")) {
            final AbstractClassMetaData cmd = this.ec.getMetaDataManager().getMetaDataForEntityName(className);
            if (cmd != null) {
                return this.clr.classForName(cmd.getFullClassName());
            }
        }
        return cls;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
    
    static class SQLTableMapping
    {
        SQLTable table;
        AbstractClassMetaData cmd;
        JavaTypeMapping mapping;
        
        public SQLTableMapping(final SQLTable tbl, final AbstractClassMetaData cmd, final JavaTypeMapping m) {
            this.table = tbl;
            this.cmd = cmd;
            this.mapping = m;
        }
        
        @Override
        public String toString() {
            return "SQLTableMapping: tbl=" + this.table + " class=" + ((this.cmd != null) ? this.cmd.getFullClassName() : "null") + " mapping=" + this.mapping;
        }
    }
}
