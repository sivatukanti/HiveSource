// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query;

import java.util.Collection;
import org.datanucleus.query.compiler.QueryCompilation;
import org.datanucleus.query.compiler.JavaQueryCompiler;
import org.datanucleus.query.QueryUtils;
import org.datanucleus.query.compiler.JDOQLCompiler;
import java.util.Map;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.query.JDOQLSingleStringParser;
import java.util.Iterator;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.StoreManager;

public abstract class AbstractJDOQLQuery extends AbstractJavaQuery
{
    public AbstractJDOQLQuery(final StoreManager storeMgr, final ExecutionContext ec) {
        super(storeMgr, ec);
    }
    
    public AbstractJDOQLQuery(final StoreManager storeMgr, final ExecutionContext ec, final AbstractJDOQLQuery q) {
        this(storeMgr, ec);
        this.candidateClass = ((q != null) ? q.candidateClass : null);
        this.candidateClassName = ((q != null) ? q.candidateClassName : null);
        this.subclasses = (q == null || q.subclasses);
        this.filter = ((q != null) ? q.filter : null);
        this.imports = ((q != null) ? q.imports : null);
        this.explicitVariables = ((q != null) ? q.explicitVariables : null);
        this.explicitParameters = ((q != null) ? q.explicitParameters : null);
        this.grouping = ((q != null) ? q.grouping : null);
        this.ordering = ((q != null) ? q.ordering : null);
        this.update = ((q != null) ? q.update : null);
        this.result = ((q != null) ? q.result : null);
        this.resultClass = ((q != null) ? q.resultClass : null);
        this.resultDistinct = (q != null && q.resultDistinct);
        this.range = ((q != null) ? q.range : null);
        this.fromInclNo = ((q != null) ? q.fromInclNo : 0L);
        this.toExclNo = ((q != null) ? q.toExclNo : Long.MAX_VALUE);
        this.fromInclParam = ((q != null) ? q.fromInclParam : null);
        this.toExclParam = ((q != null) ? q.toExclParam : null);
        if (q != null) {
            this.ignoreCache = q.ignoreCache;
        }
        if (q != null && q.subqueries != null && !q.subqueries.isEmpty()) {
            for (final String key : q.subqueries.keySet()) {
                final SubqueryDefinition subquery = q.subqueries.get(key);
                this.addSubquery(subquery.query, subquery.variableDecl, subquery.candidateExpression, subquery.parameterMap);
            }
        }
    }
    
    public AbstractJDOQLQuery(final StoreManager storeMgr, final ExecutionContext ec, final String query) {
        this(storeMgr, ec);
        final JDOQLSingleStringParser parser = new JDOQLSingleStringParser(this, query);
        boolean allowAllSyntax = ec.getNucleusContext().getPersistenceConfiguration().getBooleanProperty("datanucleus.query.jdoql.allowAll");
        if (ec.getBooleanProperty("datanucleus.query.jdoql.allowAll") != null) {
            allowAllSyntax = ec.getBooleanProperty("datanucleus.query.jdoql.allowAll");
        }
        if (allowAllSyntax) {
            parser.setAllowDelete(true);
            parser.setAllowUpdate(true);
        }
        parser.parse();
        if (this.candidateClassName != null) {
            try {
                this.candidateClass = this.getParsedImports().resolveClassDeclaration(this.candidateClassName, this.clr, null);
                this.candidateClassName = this.candidateClass.getName();
            }
            catch (ClassNotResolvedException e) {
                NucleusLogger.QUERY.warn("Candidate class for JDOQL single-string query (" + this.candidateClassName + ") could not be resolved", e);
            }
        }
    }
    
    @Override
    public void setGrouping(final String grouping) {
        this.discardCompiled();
        this.assertIsModifiable();
        this.setHaving(this.grouping = null);
        if (grouping != null && grouping.length() > 0) {
            if (grouping.indexOf("HAVING") >= 0) {
                this.setHaving(grouping.substring(grouping.indexOf("HAVING") + 7));
                this.grouping = grouping.substring(0, grouping.indexOf("HAVING") - 1);
            }
            else if (grouping.indexOf("having") >= 0) {
                this.setHaving(grouping.substring(grouping.indexOf("having") + 7));
                this.grouping = grouping.substring(0, grouping.indexOf("having") - 1);
            }
            else {
                this.grouping = grouping.trim();
            }
        }
    }
    
    @Override
    public void setResult(final String result) {
        this.discardCompiled();
        this.assertIsModifiable();
        if (result == null) {
            this.result = null;
            this.resultDistinct = false;
            return;
        }
        final String str = result.trim();
        if (str.startsWith("distinct ") || str.startsWith("DISTINCT ")) {
            this.resultDistinct = true;
            this.result = str.substring(8).trim();
        }
        else {
            this.resultDistinct = false;
            this.result = str;
        }
    }
    
    protected String getQueryCacheKey() {
        String queryCacheKey = this.toString();
        if (this.getFetchPlan() != null) {
            queryCacheKey += this.getFetchPlan().toString();
        }
        return queryCacheKey;
    }
    
    @Override
    public String getSingleStringQuery() {
        if (this.singleString != null) {
            return this.singleString;
        }
        final StringBuffer str = new StringBuffer();
        if (this.type == 1) {
            str.append("UPDATE " + this.from + " SET " + this.update + " ");
        }
        else if (this.type == 2) {
            str.append("DELETE ");
        }
        else {
            str.append("SELECT ");
        }
        if (this.unique) {
            str.append("UNIQUE ");
        }
        if (this.result != null) {
            if (this.resultDistinct) {
                str.append("DISTINCT ");
            }
            str.append(this.result + " ");
        }
        if (this.resultClass != null) {
            str.append("INTO " + this.resultClass.getName() + " ");
        }
        if (this.from != null) {
            str.append("FROM " + this.from + " ");
        }
        else if (this.candidateClassName != null) {
            str.append("FROM " + this.candidateClassName + " ");
            if (!this.subclasses) {
                str.append("EXCLUDE SUBCLASSES ");
            }
        }
        if (this.filter != null) {
            str.append("WHERE " + this.dereferenceFilter(this.filter) + " ");
        }
        if (this.explicitVariables != null) {
            str.append("VARIABLES " + this.explicitVariables + " ");
        }
        if (this.explicitParameters != null) {
            str.append("PARAMETERS " + this.explicitParameters + " ");
        }
        if (this.imports != null) {
            str.append(this.imports + " ");
        }
        if (this.grouping != null) {
            str.append("GROUP BY " + this.grouping + " ");
        }
        if (this.having != null) {
            str.append("HAVING " + this.having + " ");
        }
        if (this.ordering != null) {
            str.append("ORDER BY " + this.ordering + " ");
        }
        if (this.range != null) {
            str.append("RANGE " + this.range);
        }
        else if (this.fromInclNo > 0L || this.toExclNo != Long.MAX_VALUE) {
            str.append("RANGE " + this.fromInclNo + "," + this.toExclNo);
        }
        return this.singleString = str.toString().trim();
    }
    
    @Override
    protected void compileInternal(final Map parameterValues) {
        if (this.compilation != null) {
            return;
        }
        final QueryManager queryMgr = this.getQueryManager();
        final String queryCacheKey = this.getQueryCacheKey();
        if (this.useCaching() && queryCacheKey != null) {
            final QueryCompilation cachedCompilation = queryMgr.getQueryCompilationForQuery(this.getLanguage(), queryCacheKey);
            if (cachedCompilation != null) {
                this.compilation = cachedCompilation;
                this.checkParameterTypesAgainstCompilation(parameterValues);
                return;
            }
        }
        if (this.resultClassName != null) {
            this.resultClass = this.resolveClassDeclaration(this.resultClassName);
            this.resultClassName = null;
        }
        long startTime = 0L;
        if (NucleusLogger.QUERY.isDebugEnabled()) {
            startTime = System.currentTimeMillis();
            NucleusLogger.QUERY.debug(AbstractJDOQLQuery.LOCALISER.msg("021044", this.getLanguage(), this.getSingleStringQuery()));
        }
        final JDOQLCompiler compiler = new JDOQLCompiler(this.ec.getMetaDataManager(), this.ec.getClassLoaderResolver(), this.from, this.candidateClass, this.candidateCollection, this.filter, this.getParsedImports(), this.ordering, this.result, this.grouping, this.having, this.explicitParameters, this.explicitVariables, this.update);
        boolean allowAllSyntax = this.ec.getNucleusContext().getPersistenceConfiguration().getBooleanProperty("datanucleus.query.jdoql.allowAll");
        if (this.ec.getBooleanProperty("datanucleus.query.jdoql.allowAll") != null) {
            allowAllSyntax = this.ec.getBooleanProperty("datanucleus.query.jdoql.allowAll");
        }
        compiler.setAllowAll(allowAllSyntax);
        this.compilation = compiler.compile(parameterValues, this.subqueries);
        if (QueryUtils.queryReturnsSingleRow(this)) {
            this.compilation.setReturnsSingleRow();
        }
        if (this.resultDistinct) {
            this.compilation.setResultDistinct();
        }
        if (NucleusLogger.QUERY.isDebugEnabled()) {
            NucleusLogger.QUERY.debug(AbstractJDOQLQuery.LOCALISER.msg("021045", this.getLanguage(), "" + (System.currentTimeMillis() - startTime)));
        }
        if (this.subqueries != null) {
            this.compileSubqueries(this.subqueries, this.compilation, compiler, parameterValues);
        }
        if (NucleusLogger.QUERY.isDebugEnabled()) {
            NucleusLogger.QUERY.debug(this.compilation.toString());
        }
        this.checkParameterTypesAgainstCompilation(parameterValues);
        if (this.useCaching() && queryCacheKey != null) {
            queryMgr.addQueryCompilation(this.getLanguage(), queryCacheKey, this.compilation);
        }
    }
    
    protected void compileSubqueries(final Map<String, SubqueryDefinition> subqueryMap, final QueryCompilation parentCompilation, final JavaQueryCompiler parentCompiler, final Map parameterValues) {
        long startTime = System.currentTimeMillis();
        for (final Map.Entry<String, SubqueryDefinition> entry : subqueryMap.entrySet()) {
            final SubqueryDefinition subqueryDefinition = entry.getValue();
            final Query subquery = subqueryDefinition.getQuery();
            if (NucleusLogger.QUERY.isDebugEnabled()) {
                startTime = System.currentTimeMillis();
                NucleusLogger.QUERY.debug(AbstractJDOQLQuery.LOCALISER.msg("021044", this.getLanguage(), ((AbstractJDOQLQuery)subquery).getSingleStringQuery()));
            }
            final JDOQLCompiler subCompiler = new JDOQLCompiler(this.ec.getMetaDataManager(), this.ec.getClassLoaderResolver(), subquery.from, subquery.candidateClass, null, subquery.filter, this.getParsedImports(), subquery.ordering, subquery.result, subquery.grouping, subquery.having, subquery.explicitParameters, null, null);
            boolean allowAllSyntax = this.ec.getNucleusContext().getPersistenceConfiguration().getBooleanProperty("datanucleus.query.jdoql.allowAll");
            if (this.ec.getBooleanProperty("datanucleus.query.jdoql.allowAll") != null) {
                allowAllSyntax = this.ec.getBooleanProperty("datanucleus.query.jdoql.allowAll");
            }
            subCompiler.setAllowAll(allowAllSyntax);
            subCompiler.setLinkToParentQuery(parentCompiler, subqueryDefinition.getParameterMap());
            final QueryCompilation subqueryCompilation = subCompiler.compile(parameterValues, null);
            if (QueryUtils.queryReturnsSingleRow(subquery)) {
                subqueryCompilation.setReturnsSingleRow();
            }
            parentCompilation.addSubqueryCompilation(entry.getKey(), subqueryCompilation);
            if (NucleusLogger.QUERY.isDebugEnabled()) {
                NucleusLogger.QUERY.debug(AbstractJDOQLQuery.LOCALISER.msg("021045", this.getLanguage(), "" + (System.currentTimeMillis() - startTime)));
            }
            if (subquery.subqueries != null) {
                this.compileSubqueries(subquery.subqueries, subqueryCompilation, subCompiler, parameterValues);
            }
        }
    }
    
    @Override
    public String getLanguage() {
        return "JDOQL";
    }
}
