// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query;

import org.datanucleus.plugin.ConfigurationElement;
import org.datanucleus.plugin.PluginManager;
import org.datanucleus.ClassLoaderResolver;
import java.util.Iterator;
import org.datanucleus.util.StringUtils;
import org.datanucleus.query.evaluator.memory.ArrayContainsMethodEvaluator;
import org.datanucleus.query.evaluator.memory.ArraySizeMethodEvaluator;
import org.datanucleus.query.QueryUtils;
import java.util.List;
import org.datanucleus.query.compiler.QueryCompilation;
import java.lang.reflect.InvocationTargetException;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.ExecutionContext;
import org.datanucleus.PersistenceConfiguration;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.ClassConstants;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.HashMap;
import org.datanucleus.query.evaluator.memory.InvocationEvaluator;
import java.util.Map;
import org.datanucleus.store.query.cache.QueryResultsCache;
import org.datanucleus.store.query.cache.QueryDatastoreCompilationCache;
import org.datanucleus.query.cache.QueryCompilationCache;
import org.datanucleus.store.StoreManager;
import org.datanucleus.NucleusContext;
import org.datanucleus.util.Localiser;

public class QueryManager
{
    protected static final Localiser LOCALISER;
    protected NucleusContext nucleusCtx;
    protected StoreManager storeMgr;
    QueryCompilationCache queryCompilationCache;
    QueryDatastoreCompilationCache queryCompilationCacheDatastore;
    QueryResultsCache queryResultsCache;
    Map<String, Map<Object, InvocationEvaluator>> queryMethodEvaluatorMap;
    
    public QueryManager(final NucleusContext nucleusContext, final StoreManager storeMgr) {
        this.queryCompilationCache = null;
        this.queryCompilationCacheDatastore = null;
        this.queryResultsCache = null;
        this.queryMethodEvaluatorMap = new HashMap<String, Map<Object, InvocationEvaluator>>();
        this.nucleusCtx = nucleusContext;
        this.storeMgr = storeMgr;
        this.initialiseQueryCaches();
    }
    
    protected void initialiseQueryCaches() {
        final PersistenceConfiguration conf = this.nucleusCtx.getPersistenceConfiguration();
        String cacheType = conf.getStringProperty("datanucleus.cache.queryCompilation.type");
        if (cacheType != null && !cacheType.equalsIgnoreCase("none")) {
            final String cacheClassName = this.nucleusCtx.getPluginManager().getAttributeValueForExtension("org.datanucleus.cache_query_compilation", "name", cacheType, "class-name");
            if (cacheClassName == null) {
                throw new NucleusUserException(QueryManager.LOCALISER.msg("021500", cacheType)).setFatal();
            }
            try {
                this.queryCompilationCache = (QueryCompilationCache)this.nucleusCtx.getPluginManager().createExecutableExtension("org.datanucleus.cache_query_compilation", "name", cacheType, "class-name", new Class[] { ClassConstants.NUCLEUS_CONTEXT }, new Object[] { this.nucleusCtx });
                if (NucleusLogger.CACHE.isDebugEnabled()) {
                    NucleusLogger.CACHE.debug(QueryManager.LOCALISER.msg("021502", cacheClassName));
                }
            }
            catch (Exception e) {
                throw new NucleusUserException(QueryManager.LOCALISER.msg("021501", cacheType, cacheClassName), e).setFatal();
            }
        }
        cacheType = conf.getStringProperty("datanucleus.cache.queryCompilationDatastore.type");
        if (cacheType != null && !cacheType.equalsIgnoreCase("none")) {
            final String cacheClassName = this.nucleusCtx.getPluginManager().getAttributeValueForExtension("org.datanucleus.cache_query_compilation_store", "name", cacheType, "class-name");
            if (cacheClassName == null) {
                throw new NucleusUserException(QueryManager.LOCALISER.msg("021500", cacheType)).setFatal();
            }
            try {
                this.queryCompilationCacheDatastore = (QueryDatastoreCompilationCache)this.nucleusCtx.getPluginManager().createExecutableExtension("org.datanucleus.cache_query_compilation_store", "name", cacheType, "class-name", new Class[] { ClassConstants.NUCLEUS_CONTEXT }, new Object[] { this.nucleusCtx });
                if (NucleusLogger.CACHE.isDebugEnabled()) {
                    NucleusLogger.CACHE.debug(QueryManager.LOCALISER.msg("021502", cacheClassName));
                }
            }
            catch (Exception e) {
                throw new NucleusUserException(QueryManager.LOCALISER.msg("021501", cacheType, cacheClassName), e).setFatal();
            }
        }
        cacheType = conf.getStringProperty("datanucleus.cache.queryResults.type");
        if (cacheType != null && !cacheType.equalsIgnoreCase("none")) {
            final String cacheClassName = this.nucleusCtx.getPluginManager().getAttributeValueForExtension("org.datanucleus.cache_query_result", "name", cacheType, "class-name");
            if (cacheClassName == null) {
                throw new NucleusUserException(QueryManager.LOCALISER.msg("021500", cacheType)).setFatal();
            }
            try {
                this.queryResultsCache = (QueryResultsCache)this.nucleusCtx.getPluginManager().createExecutableExtension("org.datanucleus.cache_query_result", "name", cacheType, "class-name", new Class[] { ClassConstants.NUCLEUS_CONTEXT }, new Object[] { this.nucleusCtx });
                if (NucleusLogger.CACHE.isDebugEnabled()) {
                    NucleusLogger.CACHE.debug(QueryManager.LOCALISER.msg("021502", cacheClassName));
                }
            }
            catch (Exception e) {
                throw new NucleusUserException(QueryManager.LOCALISER.msg("021501", cacheType, cacheClassName), e).setFatal();
            }
        }
    }
    
    public void close() {
        if (this.queryCompilationCache != null) {
            this.queryCompilationCache.close();
            this.queryCompilationCache = null;
        }
        if (this.queryCompilationCacheDatastore != null) {
            this.queryCompilationCacheDatastore.close();
            this.queryCompilationCacheDatastore = null;
        }
        if (this.queryResultsCache != null) {
            this.queryResultsCache.close();
            this.queryResultsCache = null;
        }
        this.queryMethodEvaluatorMap.clear();
        this.queryMethodEvaluatorMap = null;
    }
    
    public Query newQuery(final String language, final ExecutionContext ec, final Object query) {
        if (language == null) {
            return null;
        }
        final String languageImpl = language;
        try {
            if (query != null) {
                Query q = null;
                if (query instanceof String) {
                    final Class[] argsClass = { ClassConstants.STORE_MANAGER, ClassConstants.EXECUTION_CONTEXT, String.class };
                    final Object[] args = { this.storeMgr, ec, query };
                    q = (Query)ec.getNucleusContext().getPluginManager().createExecutableExtension("org.datanucleus.store_query_query", new String[] { "name", "datastore" }, new String[] { languageImpl, ec.getStoreManager().getStoreManagerKey() }, "class-name", argsClass, args);
                    if (q == null) {
                        throw new NucleusException(QueryManager.LOCALISER.msg("021034", languageImpl, ec.getStoreManager().getStoreManagerKey()));
                    }
                }
                else if (query instanceof Query) {
                    final Class[] argsClass = { ClassConstants.STORE_MANAGER, ClassConstants.EXECUTION_CONTEXT, query.getClass() };
                    final Object[] args = { this.storeMgr, ec, query };
                    q = (Query)ec.getNucleusContext().getPluginManager().createExecutableExtension("org.datanucleus.store_query_query", new String[] { "name", "datastore" }, new String[] { languageImpl, ec.getStoreManager().getStoreManagerKey() }, "class-name", argsClass, args);
                    if (q == null) {
                        throw new NucleusException(QueryManager.LOCALISER.msg("021034", languageImpl, ec.getStoreManager().getStoreManagerKey()));
                    }
                }
                else {
                    final Class[] argsClass = { ClassConstants.STORE_MANAGER, ClassConstants.EXECUTION_CONTEXT, Object.class };
                    final Object[] args = { this.storeMgr, ec, query };
                    q = (Query)ec.getNucleusContext().getPluginManager().createExecutableExtension("org.datanucleus.store_query_query", new String[] { "name", "datastore" }, new String[] { languageImpl, ec.getStoreManager().getStoreManagerKey() }, "class-name", argsClass, args);
                    if (q == null) {
                        throw new NucleusException(QueryManager.LOCALISER.msg("021034", languageImpl, ec.getStoreManager().getStoreManagerKey()));
                    }
                }
                return q;
            }
            final Class[] argsClass2 = { ClassConstants.STORE_MANAGER, ClassConstants.EXECUTION_CONTEXT };
            final Object[] args2 = { this.storeMgr, ec };
            final Query q2 = (Query)ec.getNucleusContext().getPluginManager().createExecutableExtension("org.datanucleus.store_query_query", new String[] { "name", "datastore" }, new String[] { languageImpl, ec.getStoreManager().getStoreManagerKey() }, "class-name", argsClass2, args2);
            if (q2 == null) {
                throw new NucleusException(QueryManager.LOCALISER.msg("021034", languageImpl, ec.getStoreManager().getStoreManagerKey()));
            }
            return q2;
        }
        catch (InvocationTargetException e) {
            final Throwable t = e.getTargetException();
            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
            if (t instanceof Error) {
                throw (Error)t;
            }
            throw new NucleusException(t.getMessage(), t).setFatal();
        }
        catch (Exception e2) {
            throw new NucleusException(e2.getMessage(), e2).setFatal();
        }
    }
    
    public QueryCompilationCache getQueryCompilationCache() {
        return this.queryCompilationCache;
    }
    
    public synchronized void addQueryCompilation(final String language, final String query, final QueryCompilation compilation) {
        if (this.queryCompilationCache != null) {
            final String queryKey = language + ":" + query;
            this.queryCompilationCache.put(queryKey, compilation);
        }
    }
    
    public synchronized QueryCompilation getQueryCompilationForQuery(final String language, final String query) {
        if (this.queryCompilationCache != null) {
            final String queryKey = language + ":" + query;
            final QueryCompilation compilation = this.queryCompilationCache.get(queryKey);
            if (compilation != null) {
                if (NucleusLogger.QUERY.isDebugEnabled()) {
                    NucleusLogger.QUERY.debug(QueryManager.LOCALISER.msg("021079", query, language));
                }
                return compilation;
            }
        }
        return null;
    }
    
    public QueryDatastoreCompilationCache getQueryDatastoreCompilationCache() {
        return this.queryCompilationCacheDatastore;
    }
    
    public synchronized void addDatastoreQueryCompilation(final String datastore, final String language, final String query, final Object compilation) {
        if (this.queryCompilationCacheDatastore != null) {
            final String queryKey = language + ":" + query;
            this.queryCompilationCacheDatastore.put(queryKey, compilation);
        }
    }
    
    public synchronized void deleteDatastoreQueryCompilation(final String datastore, final String language, final String query) {
        if (this.queryCompilationCacheDatastore != null) {
            final String queryKey = language + ":" + query;
            this.queryCompilationCacheDatastore.evict(queryKey);
        }
    }
    
    public synchronized Object getDatastoreQueryCompilation(final String datastore, final String language, final String query) {
        if (this.queryCompilationCacheDatastore != null) {
            final String queryKey = language + ":" + query;
            final Object compilation = this.queryCompilationCacheDatastore.get(queryKey);
            if (compilation != null) {
                if (NucleusLogger.QUERY.isDebugEnabled()) {
                    NucleusLogger.QUERY.debug(QueryManager.LOCALISER.msg("021080", query, language, datastore));
                }
                return compilation;
            }
        }
        return null;
    }
    
    public QueryResultsCache getQueryResultsCache() {
        return this.queryResultsCache;
    }
    
    public void evictQueryResultsForType(final Class cls) {
        if (this.queryResultsCache != null) {
            this.queryResultsCache.evict(cls);
        }
    }
    
    public synchronized void addDatastoreQueryResult(final Query query, final Map params, final List<Object> results) {
        if (this.queryResultsCache != null) {
            final String queryKey = QueryUtils.getKeyForQueryResultsCache(query, params);
            this.queryResultsCache.put(queryKey, results);
            if (NucleusLogger.QUERY.isDebugEnabled()) {
                NucleusLogger.QUERY.debug(QueryManager.LOCALISER.msg("021081", query, results.size()));
            }
        }
    }
    
    public synchronized List<Object> getDatastoreQueryResult(final Query query, final Map params) {
        if (this.queryResultsCache != null) {
            final String queryKey = QueryUtils.getKeyForQueryResultsCache(query, params);
            final List<Object> results = this.queryResultsCache.get(queryKey);
            if (results != null && NucleusLogger.QUERY.isDebugEnabled()) {
                NucleusLogger.QUERY.debug(QueryManager.LOCALISER.msg("021082", query, results.size()));
            }
            return results;
        }
        return null;
    }
    
    public InvocationEvaluator getInMemoryEvaluatorForMethod(final Class type, final String methodName) {
        if (type != null && type.isArray()) {
            if (methodName.equals("size") || methodName.equals("length")) {
                return new ArraySizeMethodEvaluator();
            }
            if (methodName.equals("contains")) {
                return new ArrayContainsMethodEvaluator();
            }
        }
        final Map<Object, InvocationEvaluator> evaluatorsForMethod = this.queryMethodEvaluatorMap.get(methodName);
        if (evaluatorsForMethod != null) {
            for (final Map.Entry<Object, InvocationEvaluator> entry : evaluatorsForMethod.entrySet()) {
                final Object clsKey = entry.getKey();
                if (clsKey instanceof Class && ((Class)clsKey).isAssignableFrom(type)) {
                    return entry.getValue();
                }
                if (clsKey instanceof String && ((String)clsKey).equals("STATIC") && type == null) {
                    return entry.getValue();
                }
            }
            return null;
        }
        final ClassLoaderResolver clr = this.nucleusCtx.getClassLoaderResolver((type != null) ? type.getClassLoader() : null);
        final PluginManager pluginMgr = this.nucleusCtx.getPluginManager();
        final ConfigurationElement[] elems = pluginMgr.getConfigurationElementsForExtension("org.datanucleus.query_method_evaluators", "method", methodName);
        final Map<Object, InvocationEvaluator> evaluators = new HashMap<Object, InvocationEvaluator>();
        InvocationEvaluator requiredEvaluator = null;
        if (elems == null) {
            return null;
        }
        for (int i = 0; i < elems.length; ++i) {
            try {
                final String evalName = elems[i].getAttribute("evaluator");
                final InvocationEvaluator eval = (InvocationEvaluator)pluginMgr.createExecutableExtension("org.datanucleus.query_method_evaluators", new String[] { "method", "evaluator" }, new String[] { methodName, evalName }, "evaluator", null, null);
                String elemClsName = elems[i].getAttribute("class");
                if (elemClsName != null && StringUtils.isWhitespace(elemClsName)) {
                    elemClsName = null;
                }
                if (elemClsName == null) {
                    if (type == null) {
                        requiredEvaluator = eval;
                    }
                    evaluators.put("STATIC", eval);
                }
                else {
                    final Class elemCls = clr.classForName(elemClsName);
                    if (elemCls.isAssignableFrom(type)) {
                        requiredEvaluator = eval;
                    }
                    evaluators.put(elemCls, eval);
                }
            }
            catch (Exception ex) {}
        }
        this.queryMethodEvaluatorMap.put(methodName, evaluators);
        return requiredEvaluator;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
