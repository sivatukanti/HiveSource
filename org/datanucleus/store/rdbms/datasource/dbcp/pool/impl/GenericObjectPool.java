// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.pool.impl;

import java.util.TimerTask;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.PoolUtils;
import java.util.NoSuchElementException;
import java.util.LinkedList;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.PoolableObjectFactory;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.ObjectPool;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.BaseObjectPool;

public class GenericObjectPool extends BaseObjectPool implements ObjectPool
{
    public static final byte WHEN_EXHAUSTED_FAIL = 0;
    public static final byte WHEN_EXHAUSTED_BLOCK = 1;
    public static final byte WHEN_EXHAUSTED_GROW = 2;
    public static final int DEFAULT_MAX_IDLE = 8;
    public static final int DEFAULT_MIN_IDLE = 0;
    public static final int DEFAULT_MAX_ACTIVE = 8;
    public static final byte DEFAULT_WHEN_EXHAUSTED_ACTION = 1;
    public static final boolean DEFAULT_LIFO = true;
    public static final long DEFAULT_MAX_WAIT = -1L;
    public static final boolean DEFAULT_TEST_ON_BORROW = false;
    public static final boolean DEFAULT_TEST_ON_RETURN = false;
    public static final boolean DEFAULT_TEST_WHILE_IDLE = false;
    public static final long DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS = -1L;
    public static final int DEFAULT_NUM_TESTS_PER_EVICTION_RUN = 3;
    public static final long DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS = 1800000L;
    public static final long DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS = -1L;
    private int _maxIdle;
    private int _minIdle;
    private int _maxActive;
    private long _maxWait;
    private byte _whenExhaustedAction;
    private volatile boolean _testOnBorrow;
    private volatile boolean _testOnReturn;
    private boolean _testWhileIdle;
    private long _timeBetweenEvictionRunsMillis;
    private int _numTestsPerEvictionRun;
    private long _minEvictableIdleTimeMillis;
    private long _softMinEvictableIdleTimeMillis;
    private boolean _lifo;
    private CursorableLinkedList _pool;
    private CursorableLinkedList.Cursor _evictionCursor;
    private PoolableObjectFactory _factory;
    private int _numActive;
    private Evictor _evictor;
    private int _numInternalProcessing;
    private final LinkedList _allocationQueue;
    
    public GenericObjectPool() {
        this(null, 8, (byte)1, -1L, 8, 0, false, false, -1L, 3, 1800000L, false);
    }
    
    public GenericObjectPool(final PoolableObjectFactory factory) {
        this(factory, 8, (byte)1, -1L, 8, 0, false, false, -1L, 3, 1800000L, false);
    }
    
    public GenericObjectPool(final PoolableObjectFactory factory, final Config config) {
        this(factory, config.maxActive, config.whenExhaustedAction, config.maxWait, config.maxIdle, config.minIdle, config.testOnBorrow, config.testOnReturn, config.timeBetweenEvictionRunsMillis, config.numTestsPerEvictionRun, config.minEvictableIdleTimeMillis, config.testWhileIdle, config.softMinEvictableIdleTimeMillis, config.lifo);
    }
    
    public GenericObjectPool(final PoolableObjectFactory factory, final int maxActive) {
        this(factory, maxActive, (byte)1, -1L, 8, 0, false, false, -1L, 3, 1800000L, false);
    }
    
    public GenericObjectPool(final PoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait) {
        this(factory, maxActive, whenExhaustedAction, maxWait, 8, 0, false, false, -1L, 3, 1800000L, false);
    }
    
    public GenericObjectPool(final PoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final boolean testOnBorrow, final boolean testOnReturn) {
        this(factory, maxActive, whenExhaustedAction, maxWait, 8, 0, testOnBorrow, testOnReturn, -1L, 3, 1800000L, false);
    }
    
    public GenericObjectPool(final PoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final int maxIdle) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, 0, false, false, -1L, 3, 1800000L, false);
    }
    
    public GenericObjectPool(final PoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final int maxIdle, final boolean testOnBorrow, final boolean testOnReturn) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, 0, testOnBorrow, testOnReturn, -1L, 3, 1800000L, false);
    }
    
    public GenericObjectPool(final PoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final int maxIdle, final boolean testOnBorrow, final boolean testOnReturn, final long timeBetweenEvictionRunsMillis, final int numTestsPerEvictionRun, final long minEvictableIdleTimeMillis, final boolean testWhileIdle) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, 0, testOnBorrow, testOnReturn, timeBetweenEvictionRunsMillis, numTestsPerEvictionRun, minEvictableIdleTimeMillis, testWhileIdle);
    }
    
    public GenericObjectPool(final PoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final int maxIdle, final int minIdle, final boolean testOnBorrow, final boolean testOnReturn, final long timeBetweenEvictionRunsMillis, final int numTestsPerEvictionRun, final long minEvictableIdleTimeMillis, final boolean testWhileIdle) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, minIdle, testOnBorrow, testOnReturn, timeBetweenEvictionRunsMillis, numTestsPerEvictionRun, minEvictableIdleTimeMillis, testWhileIdle, -1L);
    }
    
    public GenericObjectPool(final PoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final int maxIdle, final int minIdle, final boolean testOnBorrow, final boolean testOnReturn, final long timeBetweenEvictionRunsMillis, final int numTestsPerEvictionRun, final long minEvictableIdleTimeMillis, final boolean testWhileIdle, final long softMinEvictableIdleTimeMillis) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, minIdle, testOnBorrow, testOnReturn, timeBetweenEvictionRunsMillis, numTestsPerEvictionRun, minEvictableIdleTimeMillis, testWhileIdle, softMinEvictableIdleTimeMillis, true);
    }
    
    public GenericObjectPool(final PoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final int maxIdle, final int minIdle, final boolean testOnBorrow, final boolean testOnReturn, final long timeBetweenEvictionRunsMillis, final int numTestsPerEvictionRun, final long minEvictableIdleTimeMillis, final boolean testWhileIdle, final long softMinEvictableIdleTimeMillis, final boolean lifo) {
        this._maxIdle = 8;
        this._minIdle = 0;
        this._maxActive = 8;
        this._maxWait = -1L;
        this._whenExhaustedAction = 1;
        this._testOnBorrow = false;
        this._testOnReturn = false;
        this._testWhileIdle = false;
        this._timeBetweenEvictionRunsMillis = -1L;
        this._numTestsPerEvictionRun = 3;
        this._minEvictableIdleTimeMillis = 1800000L;
        this._softMinEvictableIdleTimeMillis = -1L;
        this._lifo = true;
        this._pool = null;
        this._evictionCursor = null;
        this._factory = null;
        this._numActive = 0;
        this._evictor = null;
        this._numInternalProcessing = 0;
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
                this._minIdle = minIdle;
                this._testOnBorrow = testOnBorrow;
                this._testOnReturn = testOnReturn;
                this._timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
                this._numTestsPerEvictionRun = numTestsPerEvictionRun;
                this._minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
                this._softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
                this._testWhileIdle = testWhileIdle;
                this._pool = new CursorableLinkedList();
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
        this.allocate();
    }
    
    public synchronized int getMaxIdle() {
        return this._maxIdle;
    }
    
    public synchronized void setMaxIdle(final int maxIdle) {
        this._maxIdle = maxIdle;
        this.allocate();
    }
    
    public synchronized void setMinIdle(final int minIdle) {
        this._minIdle = minIdle;
        this.allocate();
    }
    
    public synchronized int getMinIdle() {
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
    
    public synchronized long getSoftMinEvictableIdleTimeMillis() {
        return this._softMinEvictableIdleTimeMillis;
    }
    
    public synchronized void setSoftMinEvictableIdleTimeMillis(final long softMinEvictableIdleTimeMillis) {
        this._softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
    }
    
    public synchronized boolean getTestWhileIdle() {
        return this._testWhileIdle;
    }
    
    public synchronized void setTestWhileIdle(final boolean testWhileIdle) {
        this._testWhileIdle = testWhileIdle;
    }
    
    public synchronized boolean getLifo() {
        return this._lifo;
    }
    
    public synchronized void setLifo(final boolean lifo) {
        this._lifo = lifo;
    }
    
    public synchronized void setConfig(final Config conf) {
        this.setMaxIdle(conf.maxIdle);
        this.setMinIdle(conf.minIdle);
        this.setMaxActive(conf.maxActive);
        this.setMaxWait(conf.maxWait);
        this.setWhenExhaustedAction(conf.whenExhaustedAction);
        this.setTestOnBorrow(conf.testOnBorrow);
        this.setTestOnReturn(conf.testOnReturn);
        this.setTestWhileIdle(conf.testWhileIdle);
        this.setNumTestsPerEvictionRun(conf.numTestsPerEvictionRun);
        this.setMinEvictableIdleTimeMillis(conf.minEvictableIdleTimeMillis);
        this.setTimeBetweenEvictionRunsMillis(conf.timeBetweenEvictionRunsMillis);
        this.setSoftMinEvictableIdleTimeMillis(conf.softMinEvictableIdleTimeMillis);
        this.setLifo(conf.lifo);
        this.allocate();
    }
    
    @Override
    public Object borrowObject() throws Exception {
        final long starttime = System.currentTimeMillis();
        final Latch latch = new Latch();
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
            if (latch.getPair() == null) {
                if (!latch.mayCreate()) {
                    switch (whenExhaustedAction) {
                        case 2: {
                            synchronized (this) {
                                if (latch.getPair() == null && !latch.mayCreate()) {
                                    this._allocationQueue.remove(latch);
                                    ++this._numInternalProcessing;
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
                            throw new IllegalArgumentException("WhenExhaustedAction property " + whenExhaustedAction + " not recognized.");
                        }
                    }
                }
            }
            boolean newlyCreated = false;
            if (null == latch.getPair()) {
                try {
                    final Object obj = this._factory.makeObject();
                    latch.setPair(new GenericKeyedObjectPool.ObjectTimestampPair(obj));
                    newlyCreated = true;
                }
                finally {
                    if (!newlyCreated) {
                        synchronized (this) {
                            --this._numInternalProcessing;
                            this.allocate();
                        }
                    }
                }
            }
            try {
                this._factory.activateObject(latch.getPair().value);
                if (this._testOnBorrow && !this._factory.validateObject(latch.getPair().value)) {
                    throw new Exception("ValidateObject failed");
                }
                synchronized (this) {
                    --this._numInternalProcessing;
                    ++this._numActive;
                }
                return latch.getPair().value;
            }
            catch (Throwable e2) {
                PoolUtils.checkRethrow(e2);
                try {
                    this._factory.destroyObject(latch.getPair().value);
                }
                catch (Throwable e3) {
                    PoolUtils.checkRethrow(e3);
                }
                synchronized (this) {
                    --this._numInternalProcessing;
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
    
    private synchronized void allocate() {
        if (this.isClosed()) {
            return;
        }
        while (!this._pool.isEmpty() && !this._allocationQueue.isEmpty()) {
            final Latch latch = this._allocationQueue.removeFirst();
            latch.setPair((GenericKeyedObjectPool.ObjectTimestampPair)this._pool.removeFirst());
            ++this._numInternalProcessing;
            synchronized (latch) {
                latch.notify();
            }
        }
        while (!this._allocationQueue.isEmpty() && (this._maxActive < 0 || this._numActive + this._numInternalProcessing < this._maxActive)) {
            final Latch latch = this._allocationQueue.removeFirst();
            latch.setMayCreate(true);
            ++this._numInternalProcessing;
            synchronized (latch) {
                latch.notify();
            }
        }
    }
    
    @Override
    public void invalidateObject(final Object obj) throws Exception {
        try {
            if (this._factory != null) {
                this._factory.destroyObject(obj);
            }
        }
        finally {
            synchronized (this) {
                --this._numActive;
                this.allocate();
            }
        }
    }
    
    @Override
    public void clear() {
        final List toDestroy = new ArrayList();
        synchronized (this) {
            toDestroy.addAll(this._pool);
            this._numInternalProcessing += this._pool._size;
            this._pool.clear();
        }
        this.destroy(toDestroy, this._factory);
    }
    
    private void destroy(final Collection c, final PoolableObjectFactory factory) {
        final Iterator it = c.iterator();
        while (it.hasNext()) {
            try {
                factory.destroyObject(it.next().value);
            }
            catch (Exception e) {
                synchronized (this) {
                    --this._numInternalProcessing;
                    this.allocate();
                }
            }
            finally {
                synchronized (this) {
                    --this._numInternalProcessing;
                    this.allocate();
                }
            }
        }
    }
    
    @Override
    public synchronized int getNumActive() {
        return this._numActive;
    }
    
    @Override
    public synchronized int getNumIdle() {
        return this._pool.size();
    }
    
    @Override
    public void returnObject(final Object obj) throws Exception {
        try {
            this.addObjectToPool(obj, true);
        }
        catch (Exception e) {
            if (this._factory != null) {
                try {
                    this._factory.destroyObject(obj);
                }
                catch (Exception ex) {}
                synchronized (this) {
                    --this._numActive;
                    this.allocate();
                }
            }
        }
    }
    
    private void addObjectToPool(final Object obj, final boolean decrementNumActive) throws Exception {
        boolean success = true;
        if (this._testOnReturn && !this._factory.validateObject(obj)) {
            success = false;
        }
        else {
            this._factory.passivateObject(obj);
        }
        boolean shouldDestroy = !success;
        synchronized (this) {
            if (this.isClosed()) {
                shouldDestroy = true;
            }
            else if (this._maxIdle >= 0 && this._pool.size() >= this._maxIdle) {
                shouldDestroy = true;
            }
            else if (success) {
                if (this._lifo) {
                    this._pool.addFirst(new GenericKeyedObjectPool.ObjectTimestampPair(obj));
                }
                else {
                    this._pool.addLast(new GenericKeyedObjectPool.ObjectTimestampPair(obj));
                }
                if (decrementNumActive) {
                    --this._numActive;
                }
                this.allocate();
            }
        }
        if (shouldDestroy) {
            try {
                this._factory.destroyObject(obj);
            }
            catch (Exception ex) {}
            if (decrementNumActive) {
                synchronized (this) {
                    --this._numActive;
                    this.allocate();
                }
            }
        }
    }
    
    @Override
    public void close() throws Exception {
        super.close();
        synchronized (this) {
            this.clear();
            this.startEvictor(-1L);
        }
    }
    
    @Override
    @Deprecated
    public void setFactory(final PoolableObjectFactory factory) throws IllegalStateException {
        final List toDestroy = new ArrayList();
        final PoolableObjectFactory oldFactory = this._factory;
        synchronized (this) {
            this.assertOpen();
            if (0 < this.getNumActive()) {
                throw new IllegalStateException("Objects are already active");
            }
            toDestroy.addAll(this._pool);
            this._numInternalProcessing += this._pool._size;
            this._pool.clear();
            this._factory = factory;
        }
        this.destroy(toDestroy, oldFactory);
    }
    
    public void evict() throws Exception {
        this.assertOpen();
        synchronized (this) {
            if (this._pool.isEmpty()) {
                return;
            }
            if (null == this._evictionCursor) {
                this._evictionCursor = this._pool.cursor(this._lifo ? this._pool.size() : 0);
            }
        }
        for (int i = 0, m = this.getNumTests(); i < m; ++i) {
            final GenericKeyedObjectPool.ObjectTimestampPair pair;
            synchronized (this) {
                if ((this._lifo && !this._evictionCursor.hasPrevious()) || (!this._lifo && !this._evictionCursor.hasNext())) {
                    this._evictionCursor.close();
                    this._evictionCursor = this._pool.cursor(this._lifo ? this._pool.size() : 0);
                }
                pair = (GenericKeyedObjectPool.ObjectTimestampPair)(this._lifo ? this._evictionCursor.previous() : ((GenericKeyedObjectPool.ObjectTimestampPair)this._evictionCursor.next()));
                this._evictionCursor.remove();
                ++this._numInternalProcessing;
            }
            boolean removeObject = false;
            final long idleTimeMilis = System.currentTimeMillis() - pair.tstamp;
            if (this.getMinEvictableIdleTimeMillis() > 0L && idleTimeMilis > this.getMinEvictableIdleTimeMillis()) {
                removeObject = true;
            }
            else if (this.getSoftMinEvictableIdleTimeMillis() > 0L && idleTimeMilis > this.getSoftMinEvictableIdleTimeMillis() && this.getNumIdle() + 1 > this.getMinIdle()) {
                removeObject = true;
            }
            if (this.getTestWhileIdle() && !removeObject) {
                boolean active = false;
                try {
                    this._factory.activateObject(pair.value);
                    active = true;
                }
                catch (Exception e) {
                    removeObject = true;
                }
                if (active) {
                    if (!this._factory.validateObject(pair.value)) {
                        removeObject = true;
                    }
                    else {
                        try {
                            this._factory.passivateObject(pair.value);
                        }
                        catch (Exception e) {
                            removeObject = true;
                        }
                    }
                }
            }
            if (removeObject) {
                try {
                    this._factory.destroyObject(pair.value);
                }
                catch (Exception ex) {}
            }
            synchronized (this) {
                if (!removeObject) {
                    this._evictionCursor.add(pair);
                    if (this._lifo) {
                        this._evictionCursor.previous();
                    }
                }
                --this._numInternalProcessing;
            }
        }
    }
    
    private void ensureMinIdle() throws Exception {
        for (int objectDeficit = this.calculateDeficit(false), j = 0; j < objectDeficit && this.calculateDeficit(true) > 0; ++j) {
            try {
                this.addObject();
            }
            finally {
                synchronized (this) {
                    --this._numInternalProcessing;
                    this.allocate();
                }
            }
        }
    }
    
    private synchronized int calculateDeficit(final boolean incrementInternal) {
        int objectDeficit = this.getMinIdle() - this.getNumIdle();
        if (this._maxActive > 0) {
            final int growLimit = Math.max(0, this.getMaxActive() - this.getNumActive() - this.getNumIdle() - this._numInternalProcessing);
            objectDeficit = Math.min(objectDeficit, growLimit);
        }
        if (incrementInternal && objectDeficit > 0) {
            ++this._numInternalProcessing;
        }
        return objectDeficit;
    }
    
    @Override
    public void addObject() throws Exception {
        this.assertOpen();
        if (this._factory == null) {
            throw new IllegalStateException("Cannot add objects without a factory.");
        }
        final Object obj = this._factory.makeObject();
        try {
            this.assertOpen();
            this.addObjectToPool(obj, false);
        }
        catch (IllegalStateException ex) {
            try {
                this._factory.destroyObject(obj);
            }
            catch (Exception ex2) {}
            throw ex;
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
        buf.append("Idle Objects:\n");
        final Iterator it = this._pool.iterator();
        final long time = System.currentTimeMillis();
        while (it.hasNext()) {
            final GenericKeyedObjectPool.ObjectTimestampPair pair = it.next();
            buf.append("\t").append(pair.value).append("\t").append(time - pair.tstamp).append("\n");
        }
        return buf.toString();
    }
    
    private int getNumTests() {
        if (this._numTestsPerEvictionRun >= 0) {
            return Math.min(this._numTestsPerEvictionRun, this._pool.size());
        }
        return (int)Math.ceil(this._pool.size() / Math.abs((double)this._numTestsPerEvictionRun));
    }
    
    private class Evictor extends TimerTask
    {
        @Override
        public void run() {
            try {
                GenericObjectPool.this.evict();
            }
            catch (Exception e) {}
            catch (OutOfMemoryError oome) {
                oome.printStackTrace(System.err);
            }
            try {
                GenericObjectPool.this.ensureMinIdle();
            }
            catch (Exception ex) {}
        }
    }
    
    public static class Config
    {
        public int maxIdle;
        public int minIdle;
        public int maxActive;
        public long maxWait;
        public byte whenExhaustedAction;
        public boolean testOnBorrow;
        public boolean testOnReturn;
        public boolean testWhileIdle;
        public long timeBetweenEvictionRunsMillis;
        public int numTestsPerEvictionRun;
        public long minEvictableIdleTimeMillis;
        public long softMinEvictableIdleTimeMillis;
        public boolean lifo;
        
        public Config() {
            this.maxIdle = 8;
            this.minIdle = 0;
            this.maxActive = 8;
            this.maxWait = -1L;
            this.whenExhaustedAction = 1;
            this.testOnBorrow = false;
            this.testOnReturn = false;
            this.testWhileIdle = false;
            this.timeBetweenEvictionRunsMillis = -1L;
            this.numTestsPerEvictionRun = 3;
            this.minEvictableIdleTimeMillis = 1800000L;
            this.softMinEvictableIdleTimeMillis = -1L;
            this.lifo = true;
        }
    }
    
    private static final class Latch
    {
        private GenericKeyedObjectPool.ObjectTimestampPair _pair;
        private boolean _mayCreate;
        
        private Latch() {
            this._mayCreate = false;
        }
        
        private synchronized GenericKeyedObjectPool.ObjectTimestampPair getPair() {
            return this._pair;
        }
        
        private synchronized void setPair(final GenericKeyedObjectPool.ObjectTimestampPair pair) {
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
