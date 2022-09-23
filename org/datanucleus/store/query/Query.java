// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query;

import org.datanucleus.ClassConstants;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.symbol.SymbolTable;
import org.datanucleus.query.JDOQLQueryHelper;
import org.datanucleus.exceptions.TransactionNotActiveException;
import java.util.Iterator;
import java.util.Collections;
import org.datanucleus.exceptions.TransactionNotReadableException;
import org.datanucleus.exceptions.NucleusUnsupportedOptionException;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.QueryResultMetaData;
import org.datanucleus.exceptions.ClassNotResolvedException;
import java.util.StringTokenizer;
import org.datanucleus.query.symbol.Symbol;
import org.datanucleus.query.QueryUtils;
import org.datanucleus.util.StringUtils;
import java.util.Collection;
import org.datanucleus.store.Extent;
import java.util.Set;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;
import org.datanucleus.query.compiler.QueryCompilation;
import org.datanucleus.util.Imports;
import java.util.HashMap;
import java.util.Map;
import org.datanucleus.FetchPlan;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.StoreManager;
import org.datanucleus.util.Localiser;
import org.datanucleus.ExecutionContextListener;
import java.io.Serializable;

public abstract class Query implements Serializable, ExecutionContextListener
{
    protected static final Localiser LOCALISER;
    public static final String EXTENSION_FLUSH_BEFORE_EXECUTION = "datanucleus.query.flushBeforeExecution";
    public static final String EXTENSION_USE_FETCH_PLAN = "datanucleus.query.useFetchPlan";
    public static final String EXTENSION_RESULT_SIZE_METHOD = "datanucleus.query.resultSizeMethod";
    public static final String EXTENSION_LOAD_RESULTS_AT_COMMIT = "datanucleus.query.loadResultsAtCommit";
    public static final String EXTENSION_COMPILATION_CACHED = "datanucleus.query.compilation.cached";
    public static final String EXTENSION_RESULTS_CACHED = "datanucleus.query.results.cached";
    public static final String EXTENSION_EVALUATE_IN_MEMORY = "datanucleus.query.evaluateInMemory";
    public static final String EXTENSION_CHECK_UNUSED_PARAMETERS = "datanucleus.query.checkUnusedParameters";
    public static final String EXTENSION_MULTITHREAD = "datanucleus.query.multithread";
    public static final String EXTENSION_RESULT_CACHE_TYPE = "datanucleus.query.resultCacheType";
    protected final transient StoreManager storeMgr;
    protected transient ExecutionContext ec;
    protected final transient ClassLoaderResolver clr;
    public static final short SELECT = 0;
    public static final short BULK_UPDATE = 1;
    public static final short BULK_DELETE = 2;
    public static final short OTHER = 3;
    protected short type;
    protected Class candidateClass;
    protected String candidateClassName;
    protected boolean subclasses;
    protected boolean unique;
    protected transient String from;
    protected transient String update;
    protected String result;
    protected boolean resultDistinct;
    protected Class resultClass;
    protected String resultClassName;
    protected String filter;
    protected String imports;
    protected String explicitVariables;
    protected String explicitParameters;
    protected String ordering;
    protected String grouping;
    protected String having;
    protected String range;
    protected long fromInclNo;
    protected long toExclNo;
    protected String fromInclParam;
    protected String toExclParam;
    protected boolean unmodifiable;
    protected boolean ignoreCache;
    private FetchPlan fetchPlan;
    private Boolean serializeRead;
    private Integer readTimeoutMillis;
    private Integer writeTimeoutMillis;
    protected Map<String, Object> extensions;
    protected Map<String, SubqueryDefinition> subqueries;
    protected transient HashMap implicitParameters;
    protected transient Imports parsedImports;
    protected transient String[] parameterNames;
    protected transient QueryCompilation compilation;
    protected transient HashSet<QueryResult> queryResults;
    protected transient Map<Thread, Object> tasks;
    protected Map inputParameters;
    
    public Query(final StoreManager storeMgr, final ExecutionContext ec) {
        this.type = 0;
        this.subclasses = true;
        this.unique = false;
        this.from = null;
        this.update = null;
        this.result = null;
        this.resultDistinct = false;
        this.resultClass = null;
        this.resultClassName = null;
        this.fromInclNo = 0L;
        this.toExclNo = Long.MAX_VALUE;
        this.fromInclParam = null;
        this.toExclParam = null;
        this.unmodifiable = false;
        this.ignoreCache = false;
        this.serializeRead = null;
        this.readTimeoutMillis = null;
        this.writeTimeoutMillis = null;
        this.extensions = null;
        this.subqueries = null;
        this.implicitParameters = null;
        this.parsedImports = null;
        this.parameterNames = null;
        this.compilation = null;
        this.queryResults = new HashSet<QueryResult>(1);
        this.tasks = new ConcurrentHashMap<Thread, Object>(1);
        this.storeMgr = storeMgr;
        this.ec = ec;
        if (ec == null) {
            throw new NucleusUserException(Query.LOCALISER.msg("021012"));
        }
        this.clr = ec.getClassLoaderResolver();
        this.ignoreCache = ec.getIgnoreCache();
        this.readTimeoutMillis = ec.getDatastoreReadTimeoutMillis();
        this.writeTimeoutMillis = ec.getDatastoreWriteTimeoutMillis();
    }
    
    public void setCacheResults(final boolean cache) {
        if (cache && this.queryResults == null) {
            this.queryResults = new HashSet<QueryResult>();
        }
        else if (!cache) {
            this.queryResults = null;
        }
    }
    
    public String getLanguage() {
        throw new UnsupportedOperationException("Query Language accessor not supported in this query");
    }
    
    protected void discardCompiled() {
        this.parsedImports = null;
        this.parameterNames = null;
        this.compilation = null;
    }
    
    public void setCompilation(final QueryCompilation compilation) {
        this.compilation = compilation;
        if (compilation != null && NucleusLogger.QUERY.isDebugEnabled()) {
            NucleusLogger.QUERY.debug(compilation.toString());
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Query)) {
            return false;
        }
        final Query q = (Query)obj;
        if (this.candidateClass == null) {
            if (q.candidateClass != null) {
                return false;
            }
        }
        else if (!this.candidateClass.equals(q.candidateClass)) {
            return false;
        }
        if (this.filter == null) {
            if (q.filter != null) {
                return false;
            }
        }
        else if (!this.filter.equals(q.filter)) {
            return false;
        }
        if (this.imports == null) {
            if (q.imports != null) {
                return false;
            }
        }
        else if (!this.imports.equals(q.imports)) {
            return false;
        }
        if (this.explicitParameters == null) {
            if (q.explicitParameters != null) {
                return false;
            }
        }
        else if (!this.explicitParameters.equals(q.explicitParameters)) {
            return false;
        }
        if (this.explicitVariables == null) {
            if (q.explicitVariables != null) {
                return false;
            }
        }
        else if (!this.explicitVariables.equals(q.explicitVariables)) {
            return false;
        }
        if (this.unique != q.unique) {
            return false;
        }
        if (this.serializeRead != q.serializeRead) {
            return false;
        }
        if (this.unmodifiable != q.unmodifiable) {
            return false;
        }
        if (this.resultClass != q.resultClass) {
            return false;
        }
        if (this.grouping == null) {
            if (q.grouping != null) {
                return false;
            }
        }
        else if (!this.grouping.equals(q.grouping)) {
            return false;
        }
        if (this.ordering == null) {
            if (q.ordering != null) {
                return false;
            }
        }
        else if (!this.ordering.equals(q.ordering)) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return ((this.candidateClass == null) ? 0 : this.candidateClass.hashCode()) ^ ((this.result == null) ? 0 : this.result.hashCode()) ^ ((this.filter == null) ? 0 : this.filter.hashCode()) ^ ((this.imports == null) ? 0 : this.imports.hashCode()) ^ ((this.explicitParameters == null) ? 0 : this.explicitParameters.hashCode()) ^ ((this.explicitVariables == null) ? 0 : this.explicitVariables.hashCode()) ^ ((this.resultClass == null) ? 0 : this.resultClass.hashCode()) ^ ((this.grouping == null) ? 0 : this.grouping.hashCode()) ^ ((this.having == null) ? 0 : this.having.hashCode()) ^ ((this.ordering == null) ? 0 : this.ordering.hashCode()) ^ ((this.range == null) ? 0 : this.range.hashCode());
    }
    
    public short getType() {
        return this.type;
    }
    
    public void setType(final short type) {
        if (type == 0 || type == 1 || type == 2) {
            this.type = type;
            return;
        }
        throw new NucleusUserException("Query only supports types of SELECT, BULK_UPDATE, BULK_DELETE : unknown value " + type);
    }
    
    public StoreManager getStoreManager() {
        return this.storeMgr;
    }
    
    public ExecutionContext getExecutionContext() {
        return this.ec;
    }
    
    @Override
    public void executionContextClosing(final ExecutionContext ec) {
        this.closeAll();
        this.ec = null;
    }
    
    public void addExtension(final String key, final Object value) {
        if (this.extensions == null) {
            this.extensions = new HashMap<String, Object>();
        }
        this.extensions.put(key, value);
    }
    
    public void setExtensions(final Map extensions) {
        this.extensions = ((extensions != null) ? new HashMap<String, Object>(extensions) : null);
    }
    
    public Object getExtension(final String key) {
        return (this.extensions != null) ? this.extensions.get(key) : null;
    }
    
    public Map<String, Object> getExtensions() {
        return this.extensions;
    }
    
    public boolean getBooleanExtensionProperty(final String name, final boolean resultIfNotSet) {
        if (this.extensions == null || !this.extensions.containsKey(name)) {
            return this.ec.getNucleusContext().getPersistenceConfiguration().getBooleanProperty(name, resultIfNotSet);
        }
        final Object value = this.extensions.get(name);
        if (value instanceof Boolean) {
            return (boolean)value;
        }
        return Boolean.valueOf((String)value);
    }
    
    public String getStringExtensionProperty(final String name, final String resultIfNotSet) {
        if (this.extensions != null && this.extensions.containsKey(name)) {
            return this.extensions.get(name);
        }
        final String value = this.ec.getNucleusContext().getPersistenceConfiguration().getStringProperty(name);
        return (value != null) ? value : resultIfNotSet;
    }
    
    public Set<String> getSupportedExtensions() {
        final Set<String> extensions = new HashSet<String>();
        extensions.add("datanucleus.query.flushBeforeExecution");
        extensions.add("datanucleus.query.useFetchPlan");
        extensions.add("datanucleus.query.resultSizeMethod");
        extensions.add("datanucleus.query.loadResultsAtCommit");
        extensions.add("datanucleus.query.resultCacheType");
        extensions.add("datanucleus.query.results.cached");
        extensions.add("datanucleus.query.compilation.cached");
        extensions.add("datanucleus.query.multithread");
        extensions.add("datanucleus.query.evaluateInMemory");
        return extensions;
    }
    
    public FetchPlan getFetchPlan() {
        if (this.fetchPlan == null) {
            this.fetchPlan = this.ec.getFetchPlan().getCopy();
        }
        return this.fetchPlan;
    }
    
    public void setFetchPlan(final FetchPlan fp) {
        this.fetchPlan = fp;
    }
    
    public void setUpdate(final String update) {
        this.discardCompiled();
        this.assertIsModifiable();
        this.update = update;
    }
    
    public String getUpdate() {
        return this.update;
    }
    
    public Class getCandidateClass() {
        return this.candidateClass;
    }
    
    public void setCandidateClass(final Class candidateClass) {
        this.discardCompiled();
        this.assertIsModifiable();
        this.candidateClassName = ((candidateClass != null) ? candidateClass.getName() : null);
        this.candidateClass = candidateClass;
    }
    
    public void setCandidateClassName(final String candidateClassName) {
        this.candidateClassName = ((candidateClassName != null) ? candidateClassName.trim() : null);
    }
    
    public String getCandidateClassName() {
        return this.candidateClassName;
    }
    
    public void setFrom(final String from) {
        this.discardCompiled();
        this.assertIsModifiable();
        this.from = from;
    }
    
    public String getFrom() {
        return this.from;
    }
    
    public abstract void setCandidates(final Extent p0);
    
    public abstract void setCandidates(final Collection p0);
    
    public void setFilter(final String filter) {
        this.discardCompiled();
        this.assertIsModifiable();
        this.filter = (StringUtils.isWhitespace(filter) ? null : StringUtils.removeSpecialTagsFromString(filter).trim());
    }
    
    public String getFilter() {
        return this.filter;
    }
    
    public void declareImports(final String imports) {
        this.discardCompiled();
        this.assertIsModifiable();
        this.imports = (StringUtils.isWhitespace(imports) ? null : StringUtils.removeSpecialTagsFromString(imports).trim());
    }
    
    public String getImports() {
        return this.imports;
    }
    
    public void declareExplicitParameters(final String parameters) {
        this.discardCompiled();
        this.assertIsModifiable();
        this.explicitParameters = (StringUtils.isWhitespace(parameters) ? null : StringUtils.removeSpecialTagsFromString(parameters).trim());
    }
    
    public String getExplicitParameters() {
        return this.explicitParameters;
    }
    
    public void setImplicitParameter(final String name, final Object value) {
        if (this.implicitParameters == null) {
            this.implicitParameters = new HashMap();
        }
        this.implicitParameters.put(name, value);
        if (this.compilation == null) {
            this.discardCompiled();
            this.compileInternal(this.implicitParameters);
        }
        this.applyImplicitParameterValueToCompilation(name, value);
    }
    
    public void setImplicitParameter(final int position, final Object value) {
        if (this.implicitParameters == null) {
            this.implicitParameters = new HashMap();
        }
        this.implicitParameters.put(position, value);
        if (this.compilation == null) {
            this.discardCompiled();
            this.compileInternal(this.implicitParameters);
        }
        this.applyImplicitParameterValueToCompilation("" + position, value);
    }
    
    protected void applyImplicitParameterValueToCompilation(final String name, final Object value) {
        if (this.compilation == null) {
            return;
        }
        boolean symbolFound = false;
        final Symbol sym = this.compilation.getSymbolTable().getSymbol(name);
        if (sym != null) {
            symbolFound = true;
            if (sym.getValueType() == null && value != null) {
                sym.setValueType(value.getClass());
            }
            else if (sym.getValueType() != null && value != null && !QueryUtils.queryParameterTypesAreCompatible(sym.getValueType(), value.getClass())) {
                throw new QueryInvalidParametersException("Parameter " + name + " needs to be assignable from " + sym.getValueType().getName() + " yet the value is of type " + value.getClass().getName());
            }
        }
        final boolean subSymbolFound = this.applyImplicitParameterValueToSubqueries(name, value, this.compilation);
        if (subSymbolFound) {
            symbolFound = true;
        }
        if (!symbolFound) {
            throw new QueryInvalidParametersException(Query.LOCALISER.msg("021116", name));
        }
    }
    
    protected boolean applyImplicitParameterValueToSubqueries(final String name, final Object value, final QueryCompilation comp) {
        boolean symbolFound = false;
        Symbol sym = null;
        final String[] subqueryNames = comp.getSubqueryAliases();
        if (subqueryNames != null) {
            for (int i = 0; i < subqueryNames.length; ++i) {
                final QueryCompilation subCompilation = comp.getCompilationForSubquery(subqueryNames[i]);
                sym = subCompilation.getSymbolTable().getSymbol(name);
                if (sym != null) {
                    symbolFound = true;
                    if (sym.getValueType() == null && value != null) {
                        sym.setValueType(value.getClass());
                    }
                    else if (sym.getValueType() != null && value != null && !QueryUtils.queryParameterTypesAreCompatible(sym.getValueType(), value.getClass())) {
                        throw new QueryInvalidParametersException("Parameter " + name + " needs to be assignable from " + sym.getValueType().getName() + " yet the value is of type " + value.getClass().getName());
                    }
                }
                final boolean subSymbolFound = this.applyImplicitParameterValueToSubqueries(name, value, subCompilation);
                if (subSymbolFound) {
                    symbolFound = true;
                }
            }
        }
        return symbolFound;
    }
    
    public Map getImplicitParameters() {
        return this.implicitParameters;
    }
    
    public void declareExplicitVariables(final String variables) {
        this.discardCompiled();
        this.assertIsModifiable();
        this.explicitVariables = (StringUtils.isWhitespace(variables) ? null : StringUtils.removeSpecialTagsFromString(variables).trim());
    }
    
    public String getExplicitVariables() {
        return this.explicitVariables;
    }
    
    public void setOrdering(final String ordering) {
        this.discardCompiled();
        this.assertIsModifiable();
        this.ordering = ((ordering != null) ? ordering.trim() : null);
    }
    
    public String getOrdering() {
        return this.ordering;
    }
    
    public void setGrouping(final String grouping) {
        this.discardCompiled();
        this.assertIsModifiable();
        this.grouping = ((grouping != null) ? grouping.trim() : null);
    }
    
    public String getGrouping() {
        return this.grouping;
    }
    
    public void setHaving(final String having) {
        this.discardCompiled();
        this.assertIsModifiable();
        this.having = ((having != null) ? having.trim() : null);
    }
    
    public String getHaving() {
        return this.having;
    }
    
    public void setUnique(final boolean unique) {
        this.discardCompiled();
        this.assertIsModifiable();
        this.unique = unique;
    }
    
    public boolean isUnique() {
        return this.unique;
    }
    
    public void setRange(final long fromIncl, final long toExcl) {
        this.discardCompiled();
        if (fromIncl >= 0L && fromIncl < Long.MAX_VALUE) {
            this.fromInclNo = fromIncl;
        }
        if (toExcl >= 0L) {
            this.toExclNo = toExcl;
        }
        this.fromInclParam = null;
        this.toExclParam = null;
        this.range = "" + this.fromInclNo + "," + this.toExclNo;
    }
    
    public void setRange(final String range) {
        this.discardCompiled();
        this.range = range;
        if (range == null) {
            this.fromInclNo = 0L;
            this.fromInclParam = null;
            this.toExclNo = Long.MAX_VALUE;
            this.toExclParam = null;
            return;
        }
        final StringTokenizer tok = new StringTokenizer(range, ",");
        if (!tok.hasMoreTokens()) {
            throw new NucleusUserException("Invalid range. Expected 'lower, upper'");
        }
        final String first = tok.nextToken().trim();
        try {
            this.fromInclNo = Long.valueOf(first);
        }
        catch (NumberFormatException nfe) {
            this.fromInclNo = 0L;
            this.fromInclParam = first.trim();
            if (this.fromInclParam.startsWith(":")) {
                this.fromInclParam = this.fromInclParam.substring(1);
            }
        }
        if (!tok.hasMoreTokens()) {
            throw new NucleusUserException("Invalid range. Expected 'lower, upper'");
        }
        final String second = tok.nextToken().trim();
        try {
            this.toExclNo = Long.valueOf(second);
        }
        catch (NumberFormatException nfe2) {
            this.toExclNo = Long.MAX_VALUE;
            this.toExclParam = second.trim();
            if (this.toExclParam.startsWith(":")) {
                this.toExclParam = this.toExclParam.substring(1);
            }
        }
    }
    
    public String getRange() {
        return this.range;
    }
    
    public long getRangeFromIncl() {
        return this.fromInclNo;
    }
    
    public long getRangeToExcl() {
        return this.toExclNo;
    }
    
    public String getRangeFromInclParam() {
        return this.fromInclParam;
    }
    
    public String getRangeToExclParam() {
        return this.toExclParam;
    }
    
    public void setResult(final String result) {
        this.discardCompiled();
        this.assertIsModifiable();
        this.result = ((result != null) ? result.trim() : null);
    }
    
    public String getResult() {
        return this.result;
    }
    
    public void setResultDistinct(final boolean distinct) {
        this.resultDistinct = distinct;
    }
    
    public boolean getResultDistinct() {
        return this.resultDistinct;
    }
    
    public String getResultClassName() {
        return this.resultClassName;
    }
    
    public void setResultClassName(final String resultClassName) {
        this.discardCompiled();
        try {
            this.resultClass = this.clr.classForName(resultClassName);
            this.resultClassName = null;
        }
        catch (ClassNotResolvedException cnre) {
            this.resultClassName = resultClassName;
            this.resultClass = null;
        }
    }
    
    public void setResultClass(final Class result_cls) {
        this.discardCompiled();
        this.resultClass = result_cls;
        this.resultClassName = null;
    }
    
    public Class getResultClass() {
        return this.resultClass;
    }
    
    public void setResultMetaData(final QueryResultMetaData qrmd) {
        throw new NucleusException("This query doesn't support the use of setResultMetaData()");
    }
    
    public void setIgnoreCache(final boolean ignoreCache) {
        this.discardCompiled();
        this.ignoreCache = ignoreCache;
    }
    
    public boolean getIgnoreCache() {
        return this.ignoreCache;
    }
    
    public boolean isSubclasses() {
        return this.subclasses;
    }
    
    public void setSubclasses(final boolean subclasses) {
        this.discardCompiled();
        this.assertIsModifiable();
        this.subclasses = subclasses;
    }
    
    public Boolean getSerializeRead() {
        return this.serializeRead;
    }
    
    public void setSerializeRead(final Boolean serialize) {
        this.discardCompiled();
        this.assertIsModifiable();
        this.serializeRead = serialize;
    }
    
    public boolean isUnmodifiable() {
        return this.unmodifiable;
    }
    
    protected void assertIsModifiable() {
        if (this.unmodifiable) {
            throw new NucleusUserException(Query.LOCALISER.msg("021014"));
        }
    }
    
    public void setUnmodifiable() {
        this.unmodifiable = true;
    }
    
    public void setDatastoreReadTimeoutMillis(final Integer timeout) {
        if (!this.supportsTimeout()) {
            throw new NucleusUnsupportedOptionException("Timeout not supported on this query");
        }
        this.readTimeoutMillis = timeout;
    }
    
    public Integer getDatastoreReadTimeoutMillis() {
        return this.readTimeoutMillis;
    }
    
    public void setDatastoreWriteTimeoutMillis(final Integer timeout) {
        if (!this.supportsTimeout()) {
            throw new NucleusUnsupportedOptionException("Timeout not supported on this query");
        }
        this.writeTimeoutMillis = timeout;
    }
    
    public Integer getDatastoreWriteTimeoutMillis() {
        return this.writeTimeoutMillis;
    }
    
    public QueryManager getQueryManager() {
        if (this.storeMgr != null) {
            return this.storeMgr.getQueryManager();
        }
        return null;
    }
    
    public void addSubquery(final Query sub, final String variableDecl, final String candidateExpr, final Map paramMap) {
        if (StringUtils.isWhitespace(variableDecl)) {
            throw new NucleusUserException(Query.LOCALISER.msg("021115"));
        }
        if (sub == null) {
            if (this.explicitVariables == null) {
                this.explicitVariables = variableDecl;
            }
            else {
                this.explicitVariables = this.explicitVariables + ";" + variableDecl;
            }
        }
        else {
            if (this.subqueries == null) {
                this.subqueries = new HashMap<String, SubqueryDefinition>();
            }
            String subqueryVariableName = variableDecl.trim();
            final int sepPos = subqueryVariableName.indexOf(32);
            subqueryVariableName = subqueryVariableName.substring(sepPos + 1);
            if (!StringUtils.isWhitespace(candidateExpr)) {
                sub.setFrom(candidateExpr);
            }
            this.subqueries.put(subqueryVariableName, new SubqueryDefinition(sub, StringUtils.isWhitespace(candidateExpr) ? null : candidateExpr, variableDecl, paramMap));
        }
    }
    
    public SubqueryDefinition getSubqueryForVariable(final String variableName) {
        if (this.subqueries == null) {
            return null;
        }
        return this.subqueries.get(variableName);
    }
    
    public boolean hasSubqueryForVariable(final String variableName) {
        return this.subqueries != null && this.subqueries.containsKey(variableName);
    }
    
    protected void prepareDatastore() {
        boolean flush = false;
        flush = ((!this.ignoreCache && !this.ec.isDelayDatastoreOperationsEnabled()) || this.getBooleanExtensionProperty("datanucleus.query.flushBeforeExecution", false));
        if (flush) {
            this.ec.flushInternal(false);
        }
    }
    
    public QueryCompilation getCompilation() {
        return this.compilation;
    }
    
    protected boolean isCompiled() {
        return this.compilation != null;
    }
    
    public void compile() {
        try {
            if (this.candidateClass != null) {
                this.clr.setPrimary(this.candidateClass.getClassLoader());
            }
            this.compileInternal(null);
        }
        finally {
            this.clr.setPrimary(null);
        }
    }
    
    protected abstract void compileInternal(final Map p0);
    
    public Imports getParsedImports() {
        if (this.parsedImports == null) {
            this.parsedImports = new Imports();
            if (this.candidateClassName != null) {
                this.parsedImports.importPackage(this.candidateClassName);
            }
            if (this.imports != null) {
                this.parsedImports.parseImports(this.imports);
            }
        }
        return this.parsedImports;
    }
    
    public Object execute() {
        return this.executeWithArray(new Object[0]);
    }
    
    public Object executeWithArray(final Object[] parameterValues) {
        this.assertIsOpen();
        if (!this.ec.getTransaction().isActive() && !this.ec.getTransaction().getNontransactionalRead()) {
            throw new TransactionNotReadableException();
        }
        return this.executeQuery(this.getParameterMapForValues(parameterValues));
    }
    
    public Object executeWithMap(final Map parameters) {
        this.assertIsOpen();
        if (!this.ec.getTransaction().isActive() && !this.ec.getTransaction().getNontransactionalRead()) {
            throw new TransactionNotReadableException();
        }
        return this.executeQuery(parameters);
    }
    
    public Map getInputParameters() {
        return this.inputParameters;
    }
    
    protected boolean supportsTimeout() {
        return false;
    }
    
    protected Object executeQuery(final Map parameters) {
        try {
            if (this.candidateClass != null) {
                this.clr.setPrimary(this.candidateClass.getClassLoader());
            }
            this.inputParameters = new HashMap();
            if (this.implicitParameters != null) {
                this.inputParameters.putAll(this.implicitParameters);
            }
            if (parameters != null) {
                this.inputParameters.putAll(parameters);
            }
            try {
                this.compileInternal(this.inputParameters);
                this.checkForMissingParameters(this.inputParameters);
                if (this.compilation != null) {
                    this.candidateClass = this.compilation.getCandidateClass();
                }
            }
            catch (RuntimeException re) {
                this.discardCompiled();
                throw re;
            }
            this.prepareDatastore();
            if (this.toExclNo - this.fromInclNo <= 0L) {
                if (this.shouldReturnSingleRow()) {
                    return null;
                }
                return Collections.EMPTY_LIST;
            }
            else {
                boolean failed = true;
                long start = 0L;
                if (this.ec.getStatistics() != null) {
                    start = System.currentTimeMillis();
                    this.ec.getStatistics().queryBegin();
                }
                try {
                    final Object result = this.performExecute(this.inputParameters);
                    if (this.type == 2 || this.type == 1) {
                        return result;
                    }
                    final Collection qr = (Collection)result;
                    failed = false;
                    if (this.shouldReturnSingleRow()) {
                        try {
                            if (qr == null || qr.size() == 0) {
                                throw new NoQueryResultsException("No query results were returned");
                            }
                            if (!this.processesRangeInDatastoreQuery() && this.toExclNo - this.fromInclNo <= 0L) {
                                throw new NoQueryResultsException("No query results were returned in the required range");
                            }
                            final Iterator qrIter = qr.iterator();
                            final Object firstRow = qrIter.next();
                            if (qrIter.hasNext()) {
                                failed = true;
                                throw new QueryNotUniqueException();
                            }
                            return firstRow;
                        }
                        finally {
                            if (qr != null) {
                                this.close(qr);
                            }
                        }
                    }
                    if (qr instanceof QueryResult && this.queryResults != null) {
                        this.queryResults.add((QueryResult)qr);
                    }
                    return qr;
                }
                finally {
                    if (this.ec.getStatistics() != null) {
                        if (failed) {
                            this.ec.getStatistics().queryExecutedWithError();
                        }
                        else {
                            this.ec.getStatistics().queryExecuted(System.currentTimeMillis() - start);
                        }
                    }
                }
            }
        }
        finally {
            this.clr.setPrimary(null);
        }
    }
    
    protected void assertSupportsCancel() {
        throw new UnsupportedOperationException("This query implementation doesn't support the cancel of executing queries");
    }
    
    public void cancel() {
        this.assertSupportsCancel();
        for (final Map.Entry entry : this.tasks.entrySet()) {
            final boolean success = this.cancelTaskObject(entry.getValue());
            NucleusLogger.QUERY.debug("Query cancelled for thread=" + entry.getKey().getId() + " with success=" + success);
        }
        this.tasks.clear();
    }
    
    public void cancel(final Thread thread) {
        this.assertSupportsCancel();
        synchronized (this.tasks) {
            final Object threadObject = this.tasks.get(thread);
            if (threadObject != null) {
                final boolean success = this.cancelTaskObject(threadObject);
                NucleusLogger.QUERY.debug("Query (in thread=" + thread.getId() + ") cancelled with success=" + success);
            }
            this.tasks.remove(thread);
        }
    }
    
    protected void registerTask(final Object taskObject) {
        synchronized (this.tasks) {
            this.tasks.put(Thread.currentThread(), taskObject);
        }
    }
    
    protected void deregisterTask() {
        synchronized (this.tasks) {
            this.tasks.remove(Thread.currentThread());
        }
    }
    
    protected boolean cancelTaskObject(final Object obj) {
        return true;
    }
    
    protected abstract Object performExecute(final Map p0);
    
    public boolean processesRangeInDatastoreQuery() {
        return false;
    }
    
    public long deletePersistentAll() {
        return this.deletePersistentAll(new Object[0]);
    }
    
    public long deletePersistentAll(final Object[] parameterValues) {
        return this.deletePersistentAll(this.getParameterMapForValues(parameterValues));
    }
    
    public long deletePersistentAll(final Map parameters) {
        this.assertIsOpen();
        if (!this.ec.getTransaction().isActive() && !this.ec.getTransaction().getNontransactionalWrite()) {
            throw new TransactionNotActiveException();
        }
        if (this.result != null) {
            throw new NucleusUserException(Query.LOCALISER.msg("021029"));
        }
        if (this.resultClass != null) {
            throw new NucleusUserException(Query.LOCALISER.msg("021030"));
        }
        if (this.ordering != null) {
            throw new NucleusUserException(Query.LOCALISER.msg("021027"));
        }
        if (this.grouping != null) {
            throw new NucleusUserException(Query.LOCALISER.msg("021028"));
        }
        if (this.range != null) {
            throw new NucleusUserException(Query.LOCALISER.msg("021031"));
        }
        if (this.fromInclNo >= 0L && this.toExclNo >= 0L && (this.fromInclNo != 0L || this.toExclNo != Long.MAX_VALUE)) {
            throw new NucleusUserException(Query.LOCALISER.msg("021031"));
        }
        return this.performDeletePersistentAll(parameters);
    }
    
    protected long performDeletePersistentAll(final Map parameters) {
        final boolean requiresUnique = this.unique;
        try {
            if (this.unique) {
                this.unique = false;
                this.discardCompiled();
            }
            this.compileInternal(parameters);
            final Collection results = (Collection)this.performExecute(parameters);
            if (results == null) {
                return 0L;
            }
            final int number = results.size();
            if (requiresUnique && number > 1) {
                throw new NucleusUserException(Query.LOCALISER.msg("021032"));
            }
            final Iterator resultsIter = results.iterator();
            while (resultsIter.hasNext()) {
                this.ec.findObjectProvider(resultsIter.next()).flush();
            }
            this.ec.deleteObjects(results.toArray());
            if (results instanceof QueryResult) {
                ((QueryResult)results).close();
            }
            return number;
        }
        finally {
            if (requiresUnique != this.unique) {
                this.unique = requiresUnique;
                this.discardCompiled();
            }
        }
    }
    
    public void close(final Object queryResult) {
        if (queryResult != null && queryResult instanceof QueryResult) {
            if (this.queryResults != null) {
                this.queryResults.remove(queryResult);
            }
            ((QueryResult)queryResult).close();
        }
    }
    
    public void closeAll() {
        if (this.queryResults != null) {
            final QueryResult[] qrs = this.queryResults.toArray(new QueryResult[this.queryResults.size()]);
            for (int i = 0; i < qrs.length; ++i) {
                this.close(qrs[i]);
            }
        }
        if (this.fetchPlan != null) {
            this.fetchPlan.clearGroups().addGroup("default");
        }
    }
    
    protected boolean shouldReturnSingleRow() {
        return QueryUtils.queryReturnsSingleRow(this);
    }
    
    protected Map getParameterMapForValues(final Object[] parameterValues) {
        final HashMap parameterMap = new HashMap();
        int position = 0;
        if (this.explicitParameters != null) {
            final StringTokenizer t1 = new StringTokenizer(this.explicitParameters, ",");
            while (t1.hasMoreTokens()) {
                final StringTokenizer t2 = new StringTokenizer(t1.nextToken(), " ");
                if (t2.countTokens() != 2) {
                    throw new NucleusUserException(Query.LOCALISER.msg("021101", this.explicitParameters));
                }
                t2.nextToken();
                final String parameterName = t2.nextToken();
                if (!JDOQLQueryHelper.isValidJavaIdentifierForJDOQL(parameterName)) {
                    throw new NucleusUserException(Query.LOCALISER.msg("021102", parameterName));
                }
                if (parameterMap.containsKey(parameterName)) {
                    throw new NucleusUserException(Query.LOCALISER.msg("021103", parameterName));
                }
                if (parameterValues.length < position + 1) {
                    throw new NucleusUserException(Query.LOCALISER.msg("021108", "" + (position + 1), "" + parameterValues.length));
                }
                parameterMap.put(parameterName, parameterValues[position++]);
            }
            if (parameterMap.size() != parameterValues.length) {
                throw new NucleusUserException(Query.LOCALISER.msg("021108", "" + parameterMap.size(), "" + parameterValues.length));
            }
        }
        else {
            for (int i = 0; i < parameterValues.length; ++i) {
                parameterMap.put(i, parameterValues[i]);
            }
        }
        return parameterMap;
    }
    
    protected boolean useFetchPlan() {
        boolean useFetchPlan = this.getBooleanExtensionProperty("datanucleus.query.useFetchPlan", true);
        if (this.type == 1 || this.type == 2) {
            useFetchPlan = false;
        }
        return useFetchPlan;
    }
    
    public boolean useCaching() {
        return this.getBooleanExtensionProperty("datanucleus.query.compilation.cached", true);
    }
    
    public boolean useResultsCaching() {
        return this.useCaching() && this.getBooleanExtensionProperty("datanucleus.query.results.cached", false);
    }
    
    public boolean checkUnusedParameters() {
        return this.getBooleanExtensionProperty("datanucleus.query.checkUnusedParameters", true);
    }
    
    protected void checkParameterTypesAgainstCompilation(final Map parameterValues) {
        if (this.compilation == null) {
            return;
        }
        if (parameterValues == null || parameterValues.isEmpty()) {
            return;
        }
        final boolean checkUnusedParams = this.checkUnusedParameters();
        for (final Map.Entry entry : parameterValues.entrySet()) {
            final Object paramKey = entry.getKey();
            Symbol sym = null;
            sym = this.deepFindSymbolForParameterInCompilation(this.compilation, paramKey);
            if (sym != null) {
                final Class expectedValueType = sym.getValueType();
                if (entry.getValue() != null && expectedValueType != null && !QueryUtils.queryParameterTypesAreCompatible(expectedValueType, entry.getValue().getClass())) {
                    throw new NucleusUserException("Parameter \"" + paramKey + "\" was specified as " + entry.getValue().getClass().getName() + " but should have been " + expectedValueType.getName());
                }
                continue;
            }
            else {
                if (paramKey instanceof String && ((this.fromInclParam == null && this.toExclParam == null) || (!paramKey.equals(this.fromInclParam) && !paramKey.equals(this.toExclParam))) && checkUnusedParams) {
                    throw new QueryInvalidParametersException(Query.LOCALISER.msg("021116", paramKey));
                }
                continue;
            }
        }
    }
    
    protected void checkForMissingParameters(Map parameterValues) {
        if (this.compilation == null) {
            return;
        }
        if (parameterValues == null) {
            parameterValues = new HashMap();
        }
        boolean namedParametersSupplied = true;
        if (parameterValues.size() > 0) {
            final Object key = parameterValues.keySet().iterator().next();
            if (!(key instanceof String)) {
                namedParametersSupplied = false;
            }
        }
        if (namedParametersSupplied) {
            final SymbolTable symtbl = this.compilation.getSymbolTable();
            final Collection<String> symNames = symtbl.getSymbolNames();
            if (symNames != null && !symNames.isEmpty()) {
                for (final String symName : symNames) {
                    final Symbol sym = symtbl.getSymbol(symName);
                    if (sym.getType() == 1 && !parameterValues.containsKey(symName)) {
                        throw new QueryInvalidParametersException(Query.LOCALISER.msg("021119", symName));
                    }
                }
            }
        }
    }
    
    protected Symbol deepFindSymbolForParameterInCompilation(final QueryCompilation compilation, final Object paramKey) {
        Symbol sym = null;
        sym = this.getSymbolForParameterInCompilation(compilation, paramKey);
        if (sym == null) {
            final String[] subqueryNames = compilation.getSubqueryAliases();
            if (subqueryNames != null) {
                for (int i = 0; i < subqueryNames.length; ++i) {
                    sym = this.deepFindSymbolForParameterInCompilation(compilation.getCompilationForSubquery(subqueryNames[i]), paramKey);
                    if (sym != null) {
                        break;
                    }
                }
            }
        }
        return sym;
    }
    
    private Symbol getSymbolForParameterInCompilation(final QueryCompilation compilation, final Object paramKey) {
        Symbol sym = null;
        if (paramKey instanceof Integer) {
            final ParameterExpression expr = compilation.getParameterExpressionForPosition((int)paramKey);
            if (expr != null) {
                sym = expr.getSymbol();
            }
        }
        else {
            final String paramName = (String)paramKey;
            sym = compilation.getSymbolTable().getSymbol(paramName);
        }
        return sym;
    }
    
    public Class resolveClassDeclaration(final String classDecl) {
        try {
            return this.getParsedImports().resolveClassDeclaration(classDecl, this.ec.getClassLoaderResolver(), (this.candidateClass == null) ? null : this.candidateClass.getClassLoader());
        }
        catch (ClassNotResolvedException e) {
            throw new NucleusUserException(Query.LOCALISER.msg("021015", classDecl));
        }
    }
    
    protected void assertIsOpen() {
        if (this.ec == null || this.ec.isClosed()) {
            throw new NucleusUserException(Query.LOCALISER.msg("021013")).setFatal();
        }
    }
    
    public Object getNativeQuery() {
        return null;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
    
    public static class SubqueryDefinition
    {
        Query query;
        String candidateExpression;
        String variableDecl;
        Map parameterMap;
        
        public SubqueryDefinition(final Query q, final String candidates, final String variables, final Map params) {
            this.query = q;
            this.candidateExpression = candidates;
            this.variableDecl = variables;
            this.parameterMap = params;
        }
        
        public Query getQuery() {
            return this.query;
        }
        
        public String getCandidateExpression() {
            return this.candidateExpression;
        }
        
        public String getVariableDeclaration() {
            return this.variableDecl;
        }
        
        public Map getParameterMap() {
            return this.parameterMap;
        }
    }
}
