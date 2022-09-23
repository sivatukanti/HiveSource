// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.FutureCallback;
import java.util.concurrent.Callable;
import com.google.common.util.concurrent.ListenableFuture;
import org.apache.htrace.core.TraceScope;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import org.apache.htrace.core.Tracer;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.LinkedBlockingQueue;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ExecutionException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Collection;
import org.apache.hadoop.HadoopIllegalArgumentException;
import java.util.HashMap;
import org.apache.hadoop.util.StringUtils;
import com.google.common.cache.Cache;
import java.util.Collections;
import com.google.common.cache.CacheLoader;
import com.google.common.base.Ticker;
import java.util.concurrent.TimeUnit;
import com.google.common.cache.CacheBuilder;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.conf.Configuration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Set;
import org.apache.hadoop.util.Timer;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.List;
import com.google.common.cache.LoadingCache;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class Groups
{
    @VisibleForTesting
    static final Logger LOG;
    private final GroupMappingServiceProvider impl;
    private final LoadingCache<String, List<String>> cache;
    private final AtomicReference<Map<String, List<String>>> staticMapRef;
    private final long cacheTimeout;
    private final long negativeCacheTimeout;
    private final long warningDeltaMs;
    private final Timer timer;
    private Set<String> negativeCache;
    private final boolean reloadGroupsInBackground;
    private final int reloadGroupsThreadCount;
    private final AtomicLong backgroundRefreshSuccess;
    private final AtomicLong backgroundRefreshException;
    private final AtomicLong backgroundRefreshQueued;
    private final AtomicLong backgroundRefreshRunning;
    private static Groups GROUPS;
    
    public Groups(final Configuration conf) {
        this(conf, new Timer());
    }
    
    public Groups(final Configuration conf, final Timer timer) {
        this.staticMapRef = new AtomicReference<Map<String, List<String>>>();
        this.backgroundRefreshSuccess = new AtomicLong(0L);
        this.backgroundRefreshException = new AtomicLong(0L);
        this.backgroundRefreshQueued = new AtomicLong(0L);
        this.backgroundRefreshRunning = new AtomicLong(0L);
        this.impl = ReflectionUtils.newInstance(conf.getClass("hadoop.security.group.mapping", JniBasedUnixGroupsMappingWithFallback.class, GroupMappingServiceProvider.class), conf);
        this.cacheTimeout = conf.getLong("hadoop.security.groups.cache.secs", 300L) * 1000L;
        this.negativeCacheTimeout = conf.getLong("hadoop.security.groups.negative-cache.secs", 30L) * 1000L;
        this.warningDeltaMs = conf.getLong("hadoop.security.groups.cache.warn.after.ms", 5000L);
        this.reloadGroupsInBackground = conf.getBoolean("hadoop.security.groups.cache.background.reload", false);
        this.reloadGroupsThreadCount = conf.getInt("hadoop.security.groups.cache.background.reload.threads", 3);
        this.parseStaticMapping(conf);
        this.timer = timer;
        this.cache = CacheBuilder.newBuilder().refreshAfterWrite(this.cacheTimeout, TimeUnit.MILLISECONDS).ticker(new TimerToTickerAdapter(timer)).expireAfterWrite(10L * this.cacheTimeout, TimeUnit.MILLISECONDS).build((CacheLoader<? super String, List<String>>)new GroupCacheLoader());
        if (this.negativeCacheTimeout > 0L) {
            final Cache<String, Boolean> tempMap = CacheBuilder.newBuilder().expireAfterWrite(this.negativeCacheTimeout, TimeUnit.MILLISECONDS).ticker(new TimerToTickerAdapter(timer)).build();
            this.negativeCache = Collections.newSetFromMap(tempMap.asMap());
        }
        if (Groups.LOG.isDebugEnabled()) {
            Groups.LOG.debug("Group mapping impl=" + this.impl.getClass().getName() + "; cacheTimeout=" + this.cacheTimeout + "; warningDeltaMs=" + this.warningDeltaMs);
        }
    }
    
    @VisibleForTesting
    Set<String> getNegativeCache() {
        return this.negativeCache;
    }
    
    private void parseStaticMapping(final Configuration conf) {
        final String staticMapping = conf.get("hadoop.user.group.static.mapping.overrides", "dr.who=;");
        final Collection<String> mappings = StringUtils.getStringCollection(staticMapping, ";");
        final Map<String, List<String>> staticUserToGroupsMap = new HashMap<String, List<String>>();
        for (final String users : mappings) {
            final Collection<String> userToGroups = StringUtils.getStringCollection(users, "=");
            if (userToGroups.size() < 1 || userToGroups.size() > 2) {
                throw new HadoopIllegalArgumentException("Configuration hadoop.user.group.static.mapping.overrides is invalid");
            }
            final String[] userToGroupsArray = userToGroups.toArray(new String[userToGroups.size()]);
            final String user = userToGroupsArray[0];
            List<String> groups = Collections.emptyList();
            if (userToGroupsArray.length == 2) {
                groups = (List<String>)(List)StringUtils.getStringCollection(userToGroupsArray[1]);
            }
            staticUserToGroupsMap.put(user, groups);
        }
        this.staticMapRef.set(staticUserToGroupsMap.isEmpty() ? null : staticUserToGroupsMap);
    }
    
    private boolean isNegativeCacheEnabled() {
        return this.negativeCacheTimeout > 0L;
    }
    
    private IOException noGroupsForUser(final String user) {
        return new IOException("No groups found for user " + user);
    }
    
    public List<String> getGroups(final String user) throws IOException {
        final Map<String, List<String>> staticUserToGroupsMap = this.staticMapRef.get();
        if (staticUserToGroupsMap != null) {
            final List<String> staticMapping = staticUserToGroupsMap.get(user);
            if (staticMapping != null) {
                return staticMapping;
            }
        }
        if (this.isNegativeCacheEnabled() && this.negativeCache.contains(user)) {
            throw this.noGroupsForUser(user);
        }
        try {
            return this.cache.get(user);
        }
        catch (ExecutionException e) {
            throw (IOException)e.getCause();
        }
    }
    
    public long getBackgroundRefreshSuccess() {
        return this.backgroundRefreshSuccess.get();
    }
    
    public long getBackgroundRefreshException() {
        return this.backgroundRefreshException.get();
    }
    
    public long getBackgroundRefreshQueued() {
        return this.backgroundRefreshQueued.get();
    }
    
    public long getBackgroundRefreshRunning() {
        return this.backgroundRefreshRunning.get();
    }
    
    public void refresh() {
        Groups.LOG.info("clearing userToGroupsMap cache");
        try {
            this.impl.cacheGroupsRefresh();
        }
        catch (IOException e) {
            Groups.LOG.warn("Error refreshing groups cache", e);
        }
        this.cache.invalidateAll();
        if (this.isNegativeCacheEnabled()) {
            this.negativeCache.clear();
        }
    }
    
    public void cacheGroupsAdd(final List<String> groups) {
        try {
            this.impl.cacheGroupsAdd(groups);
        }
        catch (IOException e) {
            Groups.LOG.warn("Error caching groups", e);
        }
    }
    
    public static Groups getUserToGroupsMappingService() {
        return getUserToGroupsMappingService(new Configuration());
    }
    
    public static synchronized Groups getUserToGroupsMappingService(final Configuration conf) {
        if (Groups.GROUPS == null) {
            if (Groups.LOG.isDebugEnabled()) {
                Groups.LOG.debug(" Creating new Groups object");
            }
            Groups.GROUPS = new Groups(conf);
        }
        return Groups.GROUPS;
    }
    
    @InterfaceAudience.Private
    public static synchronized Groups getUserToGroupsMappingServiceWithLoadedConfiguration(final Configuration conf) {
        return Groups.GROUPS = new Groups(conf);
    }
    
    static {
        LOG = LoggerFactory.getLogger(Groups.class);
        Groups.GROUPS = null;
    }
    
    private static class TimerToTickerAdapter extends Ticker
    {
        private Timer timer;
        
        public TimerToTickerAdapter(final Timer timer) {
            this.timer = timer;
        }
        
        @Override
        public long read() {
            final long NANOSECONDS_PER_MS = 1000000L;
            return this.timer.monotonicNow() * 1000000L;
        }
    }
    
    private class GroupCacheLoader extends CacheLoader<String, List<String>>
    {
        private ListeningExecutorService executorService;
        
        GroupCacheLoader() {
            if (Groups.this.reloadGroupsInBackground) {
                final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("Group-Cache-Reload").setDaemon(true).build();
                final ThreadPoolExecutor parentExecutor = new ThreadPoolExecutor(Groups.this.reloadGroupsThreadCount, Groups.this.reloadGroupsThreadCount, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory);
                parentExecutor.allowCoreThreadTimeOut(true);
                this.executorService = MoreExecutors.listeningDecorator(parentExecutor);
            }
        }
        
        @Override
        public List<String> load(final String user) throws Exception {
            Groups.LOG.debug("GroupCacheLoader - load.");
            TraceScope scope = null;
            final Tracer tracer = Tracer.curThreadTracer();
            if (tracer != null) {
                scope = tracer.newScope("Groups#fetchGroupList");
                scope.addKVAnnotation("user", user);
            }
            List<String> groups = null;
            try {
                groups = this.fetchGroupList(user);
            }
            finally {
                if (scope != null) {
                    scope.close();
                }
            }
            if (groups.isEmpty()) {
                if (Groups.this.isNegativeCacheEnabled()) {
                    Groups.this.negativeCache.add(user);
                }
                throw Groups.this.noGroupsForUser(user);
            }
            return Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(new LinkedHashSet<String>(groups)));
        }
        
        @Override
        public ListenableFuture<List<String>> reload(final String key, final List<String> oldValue) throws Exception {
            Groups.LOG.debug("GroupCacheLoader - reload (async).");
            if (!Groups.this.reloadGroupsInBackground) {
                return super.reload(key, oldValue);
            }
            Groups.this.backgroundRefreshQueued.incrementAndGet();
            final ListenableFuture<List<String>> listenableFuture = this.executorService.submit((Callable<List<String>>)new Callable<List<String>>() {
                @Override
                public List<String> call() throws Exception {
                    Groups.this.backgroundRefreshQueued.decrementAndGet();
                    Groups.this.backgroundRefreshRunning.incrementAndGet();
                    final List<String> results = GroupCacheLoader.this.load(key);
                    return results;
                }
            });
            Futures.addCallback(listenableFuture, new FutureCallback<List<String>>() {
                @Override
                public void onSuccess(final List<String> result) {
                    Groups.this.backgroundRefreshSuccess.incrementAndGet();
                    Groups.this.backgroundRefreshRunning.decrementAndGet();
                }
                
                @Override
                public void onFailure(final Throwable t) {
                    Groups.this.backgroundRefreshException.incrementAndGet();
                    Groups.this.backgroundRefreshRunning.decrementAndGet();
                }
            });
            return listenableFuture;
        }
        
        private List<String> fetchGroupList(final String user) throws IOException {
            final long startMs = Groups.this.timer.monotonicNow();
            final List<String> groupList = Groups.this.impl.getGroups(user);
            final long endMs = Groups.this.timer.monotonicNow();
            final long deltaMs = endMs - startMs;
            UserGroupInformation.metrics.addGetGroups(deltaMs);
            if (deltaMs > Groups.this.warningDeltaMs) {
                Groups.LOG.warn("Potential performance problem: getGroups(user=" + user + ") took " + deltaMs + " milliseconds.");
            }
            return groupList;
        }
    }
}
