// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.pool.impl;

import java.util.TimerTask;
import java.util.Set;
import java.util.TreeMap;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.PoolUtils;
import java.util.NoSuchElementException;
import java.util.HashMap;
import java.util.LinkedList;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.KeyedPoolableObjectFactory;
import java.util.Map;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.KeyedObjectPool;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.BaseKeyedObjectPool;

public class GenericKeyedObjectPool extends BaseKeyedObjectPool implements KeyedObjectPool
{
    public static final byte WHEN_EXHAUSTED_FAIL = 0;
    public static final byte WHEN_EXHAUSTED_BLOCK = 1;
    public static final byte WHEN_EXHAUSTED_GROW = 2;
    public static final int DEFAULT_MAX_IDLE = 8;
    public static final int DEFAULT_MAX_ACTIVE = 8;
    public static final int DEFAULT_MAX_TOTAL = -1;
    public static final byte DEFAULT_WHEN_EXHAUSTED_ACTION = 1;
    public static final long DEFAULT_MAX_WAIT = -1L;
    public static final boolean DEFAULT_TEST_ON_BORROW = false;
    public static final boolean DEFAULT_TEST_ON_RETURN = false;
    public static final boolean DEFAULT_TEST_WHILE_IDLE = false;
    public static final long DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS = -1L;
    public static final int DEFAULT_NUM_TESTS_PER_EVICTION_RUN = 3;
    public static final long DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS = 1800000L;
    public static final int DEFAULT_MIN_IDLE = 0;
    public static final boolean DEFAULT_LIFO = true;
    private int _maxIdle;
    private volatile int _minIdle;
    private int _maxActive;
    private int _maxTotal;
    private long _maxWait;
    private byte _whenExhaustedAction;
    private volatile boolean _testOnBorrow;
    private volatile boolean _testOnReturn;
    private boolean _testWhileIdle;
    private long _timeBetweenEvictionRunsMillis;
    private int _numTestsPerEvictionRun;
    private long _minEvictableIdleTimeMillis;
    private Map _poolMap;
    private int _totalActive;
    private int _totalIdle;
    private int _totalInternalProcessing;
    private KeyedPoolableObjectFactory _factory;
    private Evictor _evictor;
    private CursorableLinkedList _poolList;
    private CursorableLinkedList.Cursor _evictionCursor;
    private CursorableLinkedList.Cursor _evictionKeyCursor;
    private boolean _lifo;
    private LinkedList _allocationQueue;
    
    public GenericKeyedObjectPool() {
        this(null, 8, (byte)1, -1L, 8, false, false, -1L, 3, 1800000L, false);
    }
    
    public GenericKeyedObjectPool(final KeyedPoolableObjectFactory factory) {
        this(factory, 8, (byte)1, -1L, 8, false, false, -1L, 3, 1800000L, false);
    }
    
    public GenericKeyedObjectPool(final KeyedPoolableObjectFactory factory, final Config config) {
        this(factory, config.maxActive, config.whenExhaustedAction, config.maxWait, config.maxIdle, config.maxTotal, config.minIdle, config.testOnBorrow, config.testOnReturn, config.timeBetweenEvictionRunsMillis, config.numTestsPerEvictionRun, config.minEvictableIdleTimeMillis, config.testWhileIdle, config.lifo);
    }
    
    public GenericKeyedObjectPool(final KeyedPoolableObjectFactory factory, final int maxActive) {
        this(factory, maxActive, (byte)1, -1L, 8, false, false, -1L, 3, 1800000L, false);
    }
    
    public GenericKeyedObjectPool(final KeyedPoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait) {
        this(factory, maxActive, whenExhaustedAction, maxWait, 8, false, false, -1L, 3, 1800000L, false);
    }
    
    public GenericKeyedObjectPool(final KeyedPoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final boolean testOnBorrow, final boolean testOnReturn) {
        this(factory, maxActive, whenExhaustedAction, maxWait, 8, testOnBorrow, testOnReturn, -1L, 3, 1800000L, false);
    }
    
    public GenericKeyedObjectPool(final KeyedPoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final int maxIdle) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, false, false, -1L, 3, 1800000L, false);
    }
    
    public GenericKeyedObjectPool(final KeyedPoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final int maxIdle, final boolean testOnBorrow, final boolean testOnReturn) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, testOnBorrow, testOnReturn, -1L, 3, 1800000L, false);
    }
    
    public GenericKeyedObjectPool(final KeyedPoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final int maxIdle, final boolean testOnBorrow, final boolean testOnReturn, final long timeBetweenEvictionRunsMillis, final int numTestsPerEvictionRun, final long minEvictableIdleTimeMillis, final boolean testWhileIdle) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, -1, testOnBorrow, testOnReturn, timeBetweenEvictionRunsMillis, numTestsPerEvictionRun, minEvictableIdleTimeMillis, testWhileIdle);
    }
    
    public GenericKeyedObjectPool(final KeyedPoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final int maxIdle, final int maxTotal, final boolean testOnBorrow, final boolean testOnReturn, final long timeBetweenEvictionRunsMillis, final int numTestsPerEvictionRun, final long minEvictableIdleTimeMillis, final boolean testWhileIdle) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, maxTotal, 0, testOnBorrow, testOnReturn, timeBetweenEvictionRunsMillis, numTestsPerEvictionRun, minEvictableIdleTimeMillis, testWhileIdle);
    }
    
    public GenericKeyedObjectPool(final KeyedPoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final int maxIdle, final int maxTotal, final int minIdle, final boolean testOnBorrow, final boolean testOnReturn, final long timeBetweenEvictionRunsMillis, final int numTestsPerEvictionRun, final long minEvictableIdleTimeMillis, final boolean testWhileIdle) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, maxTotal, minIdle, testOnBorrow, testOnReturn, timeBetweenEvictionRunsMillis, numTestsPerEvictionRun, minEvictableIdleTimeMillis, testWhileIdle, true);
    }
    
    public GenericKeyedObjectPool(final KeyedPoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final int maxIdle, final int maxTotal, final int minIdle, final boolean testOnBorrow, final boolean testOnReturn, final long timeBetweenEvictionRunsMillis, final int numTestsPerEvictionRun, final long minEvictableIdleTimeMillis, final boolean testWhileIdle, final boolean lifo) {
        this._maxIdle = 8;
        this._minIdle = 0;
        this._maxActive = 8;
        this._maxTotal = -1;
        this._maxWait = -1L;
        this._whenExhaustedAction = 1;
        this._testOnBorrow = false;
        this._testOnReturn = false;
        this._testWhileIdle = false;
        this._timeBetweenEvictionRunsMillis = -1L;
        this._numTestsPerEvictionRun = 3;
        this._minEvictableIdleTimeMillis = 1800000L;
        this._poolMap = null;
        this._totalActive = 0;
        this._totalIdle = 0;
        this._totalInternalProcessing = 0;
        this._factory = null;
        this._evictor = null;
        this._poolList = null;
        this._evictionCursor = null;
        this._evictionKeyCursor = null;
        this._lifo = true;
        this._allocationQueue = new LinkedList();
        this._factory = factory;
        this._maxActive = maxActive;
        this._lifo = lifo;
        switch (whenExhaustedAction) {
            case 0:
            case 1:
            case 2: {
                this._whenExhaustedAction = whenExhaustedAction;
                this._maxWait = maxWait;
                this._maxIdle = maxIdle;
                this._maxTotal = maxTotal;
                this._minIdle = minIdle;
                this._testOnBorrow = testOnBorrow;
                this._testOnReturn = testOnReturn;
                this._timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
                this._numTestsPerEvictionRun = numTestsPerEvictionRun;
                this._minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
                this._testWhileIdle = testWhileIdle;
                this._poolMap = new HashMap();
                this._poolList = new CursorableLinkedList();
                this.startEvictor(this._timeBetweenEvictionRunsMillis);
            }
            default: {
                throw new IllegalArgumentException("whenExhaustedAction " + whenExhaustedAction + " not recognized.");
            }
        }
    }
    
    public synchronized int getMaxActive() {
        return this._maxActive;
    }
    
    public synchronized void setMaxActive(final int maxActive) {
        this._maxActive = maxActive;
        this.allocate();
    }
    
    public synchronized int getMaxTotal() {
        return this._maxTotal;
    }
    
    public synchronized void setMaxTotal(final int maxTotal) {
        this._maxTotal = maxTotal;
        this.allocate();
    }
    
    public synchronized byte getWhenExhaustedAction() {
        return this._whenExhaustedAction;
    }
    
    public synchronized void setWhenExhaustedAction(final byte whenExhaustedAction) {
        switch (whenExhaustedAction) {
            case 0:
            case 1:
            case 2: {
                this._whenExhaustedAction = whenExhaustedAction;
                this.allocate();
            }
            default: {
                throw new IllegalArgumentException("whenExhaustedAction " + whenExhaustedAction + " not recognized.");
            }
        }
    }
    
    public synchronized long getMaxWait() {
        return this._maxWait;
    }
    
    public synchronized void setMaxWait(final long maxWait) {
        this._maxWait = maxWait;
    }
    
    public synchronized int getMaxIdle() {
        return this._maxIdle;
    }
    
    public synchronized void setMaxIdle(final int maxIdle) {
        this._maxIdle = maxIdle;
        this.allocate();
    }
    
    public void setMinIdle(final int poolSize) {
        this._minIdle = poolSize;
    }
    
    public int getMinIdle() {
        return this._minIdle;
    }
    
    public boolean getTestOnBorrow() {
        return this._testOnBorrow;
    }
    
    public void setTestOnBorrow(final boolean testOnBorrow) {
        this._testOnBorrow = testOnBorrow;
    }
    
    public boolean getTestOnReturn() {
        return this._testOnReturn;
    }
    
    public void setTestOnReturn(final boolean testOnReturn) {
        this._testOnReturn = testOnReturn;
    }
    
    public synchronized long getTimeBetweenEvictionRunsMillis() {
        return this._timeBetweenEvictionRunsMillis;
    }
    
    public synchronized void setTimeBetweenEvictionRunsMillis(final long timeBetweenEvictionRunsMillis) {
        this.startEvictor(this._timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis);
    }
    
    public synchronized int getNumTestsPerEvictionRun() {
        return this._numTestsPerEvictionRun;
    }
    
    public synchronized void setNumTestsPerEvictionRun(final int numTestsPerEvictionRun) {
        this._numTestsPerEvictionRun = numTestsPerEvictionRun;
    }
    
    public synchronized long getMinEvictableIdleTimeMillis() {
        return this._minEvictableIdleTimeMillis;
    }
    
    public synchronized void setMinEvictableIdleTimeMillis(final long minEvictableIdleTimeMillis) {
        this._minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }
    
    public synchronized boolean getTestWhileIdle() {
        return this._testWhileIdle;
    }
    
    public synchronized void setTestWhileIdle(final boolean testWhileIdle) {
        this._testWhileIdle = testWhileIdle;
    }
    
    public synchronized void setConfig(final Config conf) {
        this.setMaxIdle(conf.maxIdle);
        this.setMaxActive(conf.maxActive);
        this.setMaxTotal(conf.maxTotal);
        this.setMinIdle(conf.minIdle);
        this.setMaxWait(conf.maxWait);
        this.setWhenExhaustedAction(conf.whenExhaustedAction);
        this.setTestOnBorrow(conf.testOnBorrow);
        this.setTestOnReturn(conf.testOnReturn);
        this.setTestWhileIdle(conf.testWhileIdle);
        this.setNumTestsPerEvictionRun(conf.numTestsPerEvictionRun);
        this.setMinEvictableIdleTimeMillis(conf.minEvictableIdleTimeMillis);
        this.setTimeBetweenEvictionRunsMillis(conf.timeBetweenEvictionRunsMillis);
    }
    
    public synchronized boolean getLifo() {
        return this._lifo;
    }
    
    public synchronized void setLifo(final boolean lifo) {
        this._lifo = lifo;
    }
    
    @Override
    public Object borrowObject(final Object key) throws Exception {
        final long starttime = System.currentTimeMillis();
        final Latch latch = new Latch(key);
        final byte whenExhaustedAction;
        final long maxWait;
        synchronized (this) {
            whenExhaustedAction = this._whenExhaustedAction;
            maxWait = this._maxWait;
            this._allocationQueue.add(latch);
            this.allocate();
        }
        while (true) {
            synchronized (this) {
                this.assertOpen();
            }
            if (null == latch.getPair()) {
                if (!latch.mayCreate()) {
                    switch (whenExhaustedAction) {
                        case 2: {
                            synchronized (this) {
                                if (latch.getPair() == null && !latch.mayCreate()) {
                                    this._allocationQueue.remove(latch);
                                    latch.getPool().incrementInternalProcessingCount();
                                }
                            }
                            break;
                        }
                        case 0: {
                            synchronized (this) {
                                if (latch.getPair() != null || latch.mayCreate()) {
                                    break;
                                }
                                this._allocationQueue.remove(latch);
                            }
                            throw new NoSuchElementException("Pool exhausted");
                        }
                        case 1: {
                            try {
                                synchronized (latch) {
                                    if (latch.getPair() != null || latch.mayCreate()) {
                                        break;
                                    }
                                    if (maxWait <= 0L) {
                                        latch.wait();
                                    }
                                    else {
                                        final long elapsed = System.currentTimeMillis() - starttime;
                                        final long waitTime = maxWait - elapsed;
                                        if (waitTime > 0L) {
                                            latch.wait(waitTime);
                                        }
                                    }
                                }
                            }
                            catch (InterruptedException e) {
                                synchronized (this) {
                                    if (latch.getPair() != null || latch.mayCreate()) {
                                        break;
                                    }
                                    this._allocationQueue.remove(latch);
                                }
                                Thread.currentThread().interrupt();
                                throw e;
                            }
                            if (maxWait > 0L && System.currentTimeMillis() - starttime >= maxWait) {
                                synchronized (this) {
                                    if (latch.getPair() != null || latch.mayCreate()) {
                                        break;
                                    }
                                    this._allocationQueue.remove(latch);
                                }
                                throw new NoSuchElementException("Timeout waiting for idle object");
                            }
                            continue;
                        }
                        default: {
                            throw new IllegalArgumentException("whenExhaustedAction " + whenExhaustedAction + " not recognized.");
                        }
                    }
                }
            }
            boolean newlyCreated = false;
            if (null == latch.getPair()) {
                try {
                    final Object obj = this._factory.makeObject(key);
                    latch.setPair(new ObjectTimestampPair(obj));
                    newlyCreated = true;
                }
                finally {
                    if (!newlyCreated) {
                        synchronized (this) {
                            latch.getPool().decrementInternalProcessingCount();
                            this.allocate();
                        }
                    }
                }
            }
            try {
                this._factory.activateObject(key, latch.getPair().value);
                if (this._testOnBorrow && !this._factory.validateObject(key, latch.getPair().value)) {
                    throw new Exception("ValidateObject failed");
                }
                synchronized (this) {
                    latch.getPool().decrementInternalProcessingCount();
                    latch.getPool().incrementActiveCount();
                }
                return latch.getPair().value;
            }
            catch (Throwable e2) {
                PoolUtils.checkRethrow(e2);
                try {
                    this._factory.destroyObject(key, latch.getPair().value);
                }
                catch (Throwable e3) {
                    PoolUtils.checkRethrow(e3);
                }
                synchronized (this) {
                    latch.getPool().decrementInternalProcessingCount();
                    if (!newlyCreated) {
                        latch.reset();
                        this._allocationQueue.add(0, latch);
                    }
                    this.allocate();
                }
                if (newlyCreated) {
                    throw new NoSuchElementException("Could not create a validated object, cause: " + e2.getMessage());
                }
                continue;
            }
        }
    }
    
    private void allocate() {
        boolean clearOldest = false;
        synchronized (this) {
            if (this.isClosed()) {
                return;
            }
            final Iterator allocationQueueIter = this._allocationQueue.iterator();
            while (allocationQueueIter.hasNext()) {
                final Latch latch = allocationQueueIter.next();
                ObjectQueue pool = this._poolMap.get(latch.getkey());
                if (null == pool) {
                    pool = new ObjectQueue();
                    this._poolMap.put(latch.getkey(), pool);
                    this._poolList.add(latch.getkey());
                }
                latch.setPool(pool);
                if (!pool.queue.isEmpty()) {
                    allocationQueueIter.remove();
                    latch.setPair((ObjectTimestampPair)pool.queue.removeFirst());
                    pool.incrementInternalProcessingCount();
                    --this._totalIdle;
                    synchronized (latch) {
                        latch.notify();
                    }
                }
                else {
                    if (this._maxTotal > 0 && this._totalActive + this._totalIdle + this._totalInternalProcessing >= this._maxTotal) {
                        clearOldest = true;
                        break;
                    }
                    if ((this._maxActive < 0 || pool.activeCount + pool.internalProcessingCount < this._maxActive) && (this._maxTotal < 0 || this._totalActive + this._totalIdle + this._totalInternalProcessing < this._maxTotal)) {
                        allocationQueueIter.remove();
                        latch.setMayCreate(true);
                        pool.incrementInternalProcessingCount();
                        synchronized (latch) {
                            latch.notify();
                        }
                    }
                    else {
                        if (this._maxActive < 0) {
                            break;
                        }
                        continue;
                    }
                }
            }
        }
        if (clearOldest) {
            this.clearOldest();
        }
    }
    
    @Override
    public void clear() {
        final Map toDestroy = new HashMap();
        synchronized (this) {
            final Iterator it = this._poolMap.keySet().iterator();
            while (it.hasNext()) {
                final Object key = it.next();
                final ObjectQueue pool = this._poolMap.get(key);
                final List objects = new ArrayList();
                objects.addAll(pool.queue);
                toDestroy.put(key, objects);
                it.remove();
                this._poolList.remove(key);
                this._totalIdle -= pool.queue.size();
                this._totalInternalProcessing += pool.queue.size();
                pool.queue.clear();
            }
        }
        this.destroy(toDestroy, this._factory);
    }
    
    public void clearOldest() {
        final Map toDestroy = new HashMap();
        final Map map = new TreeMap();
        synchronized (this) {
            for (final Object key : this._poolMap.keySet()) {
                final CursorableLinkedList list = this._poolMap.get(key).queue;
                final Iterator it = list.iterator();
                while (it.hasNext()) {
                    map.put(it.next(), key);
                }
            }
            final Set setPairKeys = map.entrySet();
            int itemsToRemove = (int)(map.size() * 0.15) + 1;
            for (Iterator iter = setPairKeys.iterator(); iter.hasNext() && itemsToRemove > 0; --itemsToRemove) {
                final Map.Entry entry = iter.next();
                final Object key2 = entry.getValue();
                final ObjectTimestampPair pairTimeStamp = entry.getKey();
                final CursorableLinkedList list2 = this._poolMap.get(key2).queue;
                list2.remove(pairTimeStamp);
                if (toDestroy.containsKey(key2)) {
                    toDestroy.get(key2).add(pairTimeStamp);
                }
                else {
                    final List listForKey = new ArrayList();
                    listForKey.add(pairTimeStamp);
                    toDestroy.put(key2, listForKey);
                }
                if (list2.isEmpty()) {
                    this._poolMap.remove(key2);
                    this._poolList.remove(key2);
                }
                --this._totalIdle;
                ++this._totalInternalProcessing;
            }
        }
        this.destroy(toDestroy, this._factory);
    }
    
    @Override
    public void clear(final Object key) {
        final Map toDestroy = new HashMap();
        synchronized (this) {
            final ObjectQueue pool = this._poolMap.remove(key);
            if (pool == null) {
                return;
            }
            this._poolList.remove(key);
            final List objects = new ArrayList();
            objects.addAll(pool.queue);
            toDestroy.put(key, objects);
            this._totalIdle -= pool.queue.size();
            this._totalInternalProcessing += pool.queue.size();
            pool.queue.clear();
        }
        this.destroy(toDestroy, this._factory);
    }
    
    private void destroy(final Map m, final KeyedPoolableObjectFactory factory) {
        for (final Map.Entry entry : m.entrySet()) {
            final Object key = entry.getKey();
            final Collection c = entry.getValue();
            final Iterator it = c.iterator();
            while (it.hasNext()) {
                try {
                    factory.destroyObject(key, it.next().value);
                }
                catch (Exception e) {
                    synchronized (this) {
                        --this._totalInternalProcessing;
                        this.allocate();
                    }
                }
                finally {
                    synchronized (this) {
                        --this._totalInternalProcessing;
                        this.allocate();
                    }
                }
            }
        }
    }
    
    @Override
    public synchronized int getNumActive() {
        return this._totalActive;
    }
    
    @Override
    public synchronized int getNumIdle() {
        return this._totalIdle;
    }
    
    @Override
    public synchronized int getNumActive(final Object key) {
        final ObjectQueue pool = this._poolMap.get(key);
        return (pool != null) ? pool.activeCount : 0;
    }
    
    @Override
    public synchronized int getNumIdle(final Object key) {
        final ObjectQueue pool = this._poolMap.get(key);
        return (pool != null) ? pool.queue.size() : 0;
    }
    
    @Override
    public void returnObject(final Object key, final Object obj) throws Exception {
        try {
            this.addObjectToPool(key, obj, true);
        }
        catch (Exception e) {
            if (this._factory != null) {
                try {
                    this._factory.destroyObject(key, obj);
                }
                catch (Exception ex) {}
                final ObjectQueue pool = this._poolMap.get(key);
                if (pool != null) {
                    synchronized (this) {
                        pool.decrementActiveCount();
                        this.allocate();
                    }
                }
            }
        }
    }
    
    private void addObjectToPool(final Object key, final Object obj, final boolean decrementNumActive) throws Exception {
        boolean success = true;
        if (this._testOnReturn && !this._factory.validateObject(key, obj)) {
            success = false;
        }
        else {
            this._factory.passivateObject(key, obj);
        }
        boolean shouldDestroy = !success;
        ObjectQueue pool;
        synchronized (this) {
            pool = this._poolMap.get(key);
            if (null == pool) {
                pool = new ObjectQueue();
                this._poolMap.put(key, pool);
                this._poolList.add(key);
            }
            if (this.isClosed()) {
                shouldDestroy = true;
            }
            else if (this._maxIdle >= 0 && pool.queue.size() >= this._maxIdle) {
                shouldDestroy = true;
            }
            else if (success) {
                if (this._lifo) {
                    pool.queue.addFirst(new ObjectTimestampPair(obj));
                }
                else {
                    pool.queue.addLast(new ObjectTimestampPair(obj));
                }
                ++this._totalIdle;
                if (decrementNumActive) {
                    pool.decrementActiveCount();
                }
                this.allocate();
            }
        }
        if (shouldDestroy) {
            try {
                this._factory.destroyObject(key, obj);
            }
            catch (Exception ex) {}
            if (decrementNumActive) {
                synchronized (this) {
                    pool.decrementActiveCount();
                    this.allocate();
                }
            }
        }
    }
    
    @Override
    public void invalidateObject(final Object key, final Object obj) throws Exception {
        try {
            this._factory.destroyObject(key, obj);
        }
        finally {
            synchronized (this) {
                ObjectQueue pool = this._poolMap.get(key);
                if (null == pool) {
                    pool = new ObjectQueue();
                    this._poolMap.put(key, pool);
                    this._poolList.add(key);
                }
                pool.decrementActiveCount();
                this.allocate();
            }
        }
    }
    
    @Override
    public void addObject(final Object key) throws Exception {
        this.assertOpen();
        if (this._factory == null) {
            throw new IllegalStateException("Cannot add objects without a factory.");
        }
        final Object obj = this._factory.makeObject(key);
        try {
            this.assertOpen();
            this.addObjectToPool(key, obj, false);
        }
        catch (IllegalStateException ex) {
            try {
                this._factory.destroyObject(key, obj);
            }
            catch (Exception ex2) {}
            throw ex;
        }
    }
    
    public synchronized void preparePool(final Object key, final boolean populateImmediately) {
        ObjectQueue pool = this._poolMap.get(key);
        if (null == pool) {
            pool = new ObjectQueue();
            this._poolMap.put(key, pool);
            this._poolList.add(key);
        }
        if (populateImmediately) {
            try {
                this.ensureMinIdle(key);
            }
            catch (Exception ex) {}
        }
    }
    
    @Override
    public void close() throws Exception {
        super.close();
        synchronized (this) {
            this.clear();
            if (null != this._evictionCursor) {
                this._evictionCursor.close();
                this._evictionCursor = null;
            }
            if (null != this._evictionKeyCursor) {
                this._evictionKeyCursor.close();
                this._evictionKeyCursor = null;
            }
            this.startEvictor(-1L);
        }
    }
    
    @Override
    @Deprecated
    public void setFactory(final KeyedPoolableObjectFactory factory) throws IllegalStateException {
        final Map toDestroy = new HashMap();
        final KeyedPoolableObjectFactory oldFactory = this._factory;
        synchronized (this) {
            this.assertOpen();
            if (0 < this.getNumActive()) {
                throw new IllegalStateException("Objects are already active");
            }
            final Iterator it = this._poolMap.keySet().iterator();
            while (it.hasNext()) {
                final Object key = it.next();
                final ObjectQueue pool = this._poolMap.get(key);
                if (pool != null) {
                    final List objects = new ArrayList();
                    objects.addAll(pool.queue);
                    toDestroy.put(key, objects);
                    it.remove();
                    this._poolList.remove(key);
                    this._totalIdle -= pool.queue.size();
                    this._totalInternalProcessing += pool.queue.size();
                    pool.queue.clear();
                }
            }
            this._factory = factory;
        }
        this.destroy(toDestroy, oldFactory);
    }
    
    public void evict() throws Exception {
        Object key = null;
        final boolean testWhileIdle;
        final long minEvictableIdleTimeMillis;
        synchronized (this) {
            testWhileIdle = this._testWhileIdle;
            minEvictableIdleTimeMillis = this._minEvictableIdleTimeMillis;
            if (this._evictionKeyCursor != null && this._evictionKeyCursor._lastReturned != null) {
                key = this._evictionKeyCursor._lastReturned.value();
            }
        }
        for (int i = 0, m = this.getNumTests(); i < m; ++i) {
            final ObjectTimestampPair pair;
            synchronized (this) {
                if (this._poolMap == null || this._poolMap.size() == 0) {
                    continue;
                }
                if (null == this._evictionKeyCursor) {
                    this.resetEvictionKeyCursor();
                    key = null;
                }
                if (null == this._evictionCursor) {
                    if (this._evictionKeyCursor.hasNext()) {
                        key = this._evictionKeyCursor.next();
                        this.resetEvictionObjectCursor(key);
                    }
                    else {
                        this.resetEvictionKeyCursor();
                        if (this._evictionKeyCursor != null && this._evictionKeyCursor.hasNext()) {
                            key = this._evictionKeyCursor.next();
                            this.resetEvictionObjectCursor(key);
                        }
                    }
                }
                if (this._evictionCursor == null) {
                    continue;
                }
                if (((this._lifo && !this._evictionCursor.hasPrevious()) || (!this._lifo && !this._evictionCursor.hasNext())) && this._evictionKeyCursor != null) {
                    if (this._evictionKeyCursor.hasNext()) {
                        key = this._evictionKeyCursor.next();
                        this.resetEvictionObjectCursor(key);
                    }
                    else {
                        this.resetEvictionKeyCursor();
                        if (this._evictionKeyCursor != null && this._evictionKeyCursor.hasNext()) {
                            key = this._evictionKeyCursor.next();
                            this.resetEvictionObjectCursor(key);
                        }
                    }
                }
                if ((this._lifo && !this._evictionCursor.hasPrevious()) || (!this._lifo && !this._evictionCursor.hasNext())) {
                    continue;
                }
                pair = (ObjectTimestampPair)(this._lifo ? this._evictionCursor.previous() : ((ObjectTimestampPair)this._evictionCursor.next()));
                this._evictionCursor.remove();
                --this._totalIdle;
                ++this._totalInternalProcessing;
            }
            boolean removeObject = false;
            if (minEvictableIdleTimeMillis > 0L && System.currentTimeMillis() - pair.tstamp > minEvictableIdleTimeMillis) {
                removeObject = true;
            }
            if (testWhileIdle && !removeObject) {
                boolean active = false;
                try {
                    this._factory.activateObject(key, pair.value);
                    active = true;
                }
                catch (Exception e) {
                    removeObject = true;
                }
                if (active) {
                    if (!this._factory.validateObject(key, pair.value)) {
                        removeObject = true;
                    }
                    else {
                        try {
                            this._factory.passivateObject(key, pair.value);
                        }
                        catch (Exception e) {
                            removeObject = true;
                        }
                    }
                }
            }
            if (removeObject) {
                try {
                    this._factory.destroyObject(key, pair.value);
                }
                catch (Exception e2) {
                    if (this._minIdle == 0) {
                        synchronized (this) {
                            final ObjectQueue objectQueue = this._poolMap.get(key);
                            if (objectQueue != null && objectQueue.queue.isEmpty()) {
                                this._poolMap.remove(key);
                                this._poolList.remove(key);
                            }
                        }
                    }
                }
                finally {
                    if (this._minIdle == 0) {
                        synchronized (this) {
                            final ObjectQueue objectQueue2 = this._poolMap.get(key);
                            if (objectQueue2 != null && objectQueue2.queue.isEmpty()) {
                                this._poolMap.remove(key);
                                this._poolList.remove(key);
                            }
                        }
                    }
                }
            }
            synchronized (this) {
                if (!removeObject) {
                    this._evictionCursor.add(pair);
                    ++this._totalIdle;
                    if (this._lifo) {
                        this._evictionCursor.previous();
                    }
                }
                --this._totalInternalProcessing;
            }
        }
    }
    
    private void resetEvictionKeyCursor() {
        if (this._evictionKeyCursor != null) {
            this._evictionKeyCursor.close();
        }
        this._evictionKeyCursor = this._poolList.cursor();
        if (null != this._evictionCursor) {
            this._evictionCursor.close();
            this._evictionCursor = null;
        }
    }
    
    private void resetEvictionObjectCursor(final Object key) {
        if (this._evictionCursor != null) {
            this._evictionCursor.close();
        }
        if (this._poolMap == null) {
            return;
        }
        final ObjectQueue pool = this._poolMap.get(key);
        if (pool != null) {
            final CursorableLinkedList queue = pool.queue;
            this._evictionCursor = queue.cursor(this._lifo ? queue.size() : 0);
        }
    }
    
    private void ensureMinIdle() throws Exception {
        if (this._minIdle > 0) {
            final Object[] keysCopy;
            synchronized (this) {
                keysCopy = this._poolMap.keySet().toArray();
            }
            for (int i = 0; i < keysCopy.length; ++i) {
                this.ensureMinIdle(keysCopy[i]);
            }
        }
    }
    
    private void ensureMinIdle(final Object key) throws Exception {
        final ObjectQueue pool;
        synchronized (this) {
            pool = this._poolMap.get(key);
        }
        if (pool == null) {
            return;
        }
        for (int objectDeficit = this.calculateDeficit(pool, false), i = 0; i < objectDeficit && this.calculateDeficit(pool, true) > 0; ++i) {
            try {
                this.addObject(key);
            }
            finally {
                synchronized (this) {
                    pool.decrementInternalProcessingCount();
                    this.allocate();
                }
            }
        }
    }
    
    protected synchronized void startEvictor(final long delay) {
        if (null != this._evictor) {
            EvictionTimer.cancel(this._evictor);
            this._evictor = null;
        }
        if (delay > 0L) {
            EvictionTimer.schedule(this._evictor = new Evictor(), delay, delay);
        }
    }
    
    synchronized String debugInfo() {
        final StringBuffer buf = new StringBuffer();
        buf.append("Active: ").append(this.getNumActive()).append("\n");
        buf.append("Idle: ").append(this.getNumIdle()).append("\n");
        for (final Object key : this._poolMap.keySet()) {
            buf.append("\t").append(key).append(" ").append(this._poolMap.get(key)).append("\n");
        }
        return buf.toString();
    }
    
    private synchronized int getNumTests() {
        if (this._numTestsPerEvictionRun >= 0) {
            return Math.min(this._numTestsPerEvictionRun, this._totalIdle);
        }
        return (int)Math.ceil(this._totalIdle / Math.abs((double)this._numTestsPerEvictionRun));
    }
    
    private synchronized int calculateDeficit(final ObjectQueue pool, final boolean incrementInternal) {
        int objectDefecit = 0;
        objectDefecit = this.getMinIdle() - pool.queue.size();
        if (this.getMaxActive() > 0) {
            final int growLimit = Math.max(0, this.getMaxActive() - pool.activeCount - pool.queue.size() - pool.internalProcessingCount);
            objectDefecit = Math.min(objectDefecit, growLimit);
        }
        if (this.getMaxTotal() > 0) {
            final int growLimit = Math.max(0, this.getMaxTotal() - this.getNumActive() - this.getNumIdle() - this._totalInternalProcessing);
            objectDefecit = Math.min(objectDefecit, growLimit);
        }
        if (incrementInternal && objectDefecit > 0) {
            pool.incrementInternalProcessingCount();
        }
        return objectDefecit;
    }
    
    private class ObjectQueue
    {
        private int activeCount;
        private final CursorableLinkedList queue;
        private int internalProcessingCount;
        
        private ObjectQueue() {
            this.activeCount = 0;
            this.queue = new CursorableLinkedList();
            this.internalProcessingCount = 0;
        }
        
        void incrementActiveCount() {
            synchronized (GenericKeyedObjectPool.this) {
                GenericKeyedObjectPool.this._totalActive++;
            }
            ++this.activeCount;
        }
        
        void decrementActiveCount() {
            synchronized (GenericKeyedObjectPool.this) {
                GenericKeyedObjectPool.this._totalActive--;
            }
            if (this.activeCount > 0) {
                --this.activeCount;
            }
        }
        
        void incrementInternalProcessingCount() {
            synchronized (GenericKeyedObjectPool.this) {
                GenericKeyedObjectPool.this._totalInternalProcessing++;
            }
            ++this.internalProcessingCount;
        }
        
        void decrementInternalProcessingCount() {
            synchronized (GenericKeyedObjectPool.this) {
                GenericKeyedObjectPool.this._totalInternalProcessing--;
            }
            --this.internalProcessingCount;
        }
    }
    
    static class ObjectTimestampPair implements Comparable
    {
        Object value;
        long tstamp;
        
        ObjectTimestampPair(final Object val) {
            this(val, System.currentTimeMillis());
        }
        
        ObjectTimestampPair(final Object val, final long time) {
            this.value = val;
            this.tstamp = time;
        }
        
        @Override
        public String toString() {
            return this.value + ";" + this.tstamp;
        }
        
        @Override
        public int compareTo(final Object obj) {
            return this.compareTo((ObjectTimestampPair)obj);
        }
        
        public int compareTo(final ObjectTimestampPair other) {
            final long tstampdiff = this.tstamp - other.tstamp;
            if (tstampdiff == 0L) {
                return System.identityHashCode(this) - System.identityHashCode(other);
            }
            return (int)Math.min(Math.max(tstampdiff, -2147483648L), 2147483647L);
        }
        
        public Object getValue() {
            return this.value;
        }
        
        public long getTstamp() {
            return this.tstamp;
        }
    }
    
    private class Evictor extends TimerTask
    {
        @Override
        public void run() {
            try {
                GenericKeyedObjectPool.this.evict();
            }
            catch (Exception e) {}
            catch (OutOfMemoryError oome) {
                oome.printStackTrace(System.err);
            }
            try {
                GenericKeyedObjectPool.this.ensureMinIdle();
            }
            catch (Exception ex) {}
        }
    }
    
    public static class Config
    {
        public int maxIdle;
        public int maxActive;
        public int maxTotal;
        public int minIdle;
        public long maxWait;
        public byte whenExhaustedAction;
        public boolean testOnBorrow;
        public boolean testOnReturn;
        public boolean testWhileIdle;
        public long timeBetweenEvictionRunsMillis;
        public int numTestsPerEvictionRun;
        public long minEvictableIdleTimeMillis;
        public boolean lifo;
        
        public Config() {
            this.maxIdle = 8;
            this.maxActive = 8;
            this.maxTotal = -1;
            this.minIdle = 0;
            this.maxWait = -1L;
            this.whenExhaustedAction = 1;
            this.testOnBorrow = false;
            this.testOnReturn = false;
            this.testWhileIdle = false;
            this.timeBetweenEvictionRunsMillis = -1L;
            this.numTestsPerEvictionRun = 3;
            this.minEvictableIdleTimeMillis = 1800000L;
            this.lifo = true;
        }
    }
    
    private static final class Latch
    {
        private final Object _key;
        private ObjectQueue _pool;
        private ObjectTimestampPair _pair;
        private boolean _mayCreate;
        
        private Latch(final Object key) {
            this._mayCreate = false;
            this._key = key;
        }
        
        private synchronized Object getkey() {
            return this._key;
        }
        
        private synchronized ObjectQueue getPool() {
            return this._pool;
        }
        
        private synchronized void setPool(final ObjectQueue pool) {
            this._pool = pool;
        }
        
        private synchronized ObjectTimestampPair getPair() {
            return this._pair;
        }
        
        private synchronized void setPair(final ObjectTimestampPair pair) {
            this._pair = pair;
        }
        
        private synchronized boolean mayCreate() {
            return this._mayCreate;
        }
        
        private synchronized void setMayCreate(final boolean mayCreate) {
            this._mayCreate = mayCreate;
        }
        
        private synchronized void reset() {
            this._pair = null;
            this._mayCreate = false;
        }
    }
}
