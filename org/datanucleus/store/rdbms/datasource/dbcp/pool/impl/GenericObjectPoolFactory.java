// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.pool.impl;

import org.datanucleus.store.rdbms.datasource.dbcp.pool.ObjectPool;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.PoolableObjectFactory;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.ObjectPoolFactory;

public class GenericObjectPoolFactory implements ObjectPoolFactory
{
    @Deprecated
    protected int _maxIdle;
    @Deprecated
    protected int _minIdle;
    @Deprecated
    protected int _maxActive;
    @Deprecated
    protected long _maxWait;
    @Deprecated
    protected byte _whenExhaustedAction;
    @Deprecated
    protected boolean _testOnBorrow;
    @Deprecated
    protected boolean _testOnReturn;
    @Deprecated
    protected boolean _testWhileIdle;
    @Deprecated
    protected long _timeBetweenEvictionRunsMillis;
    @Deprecated
    protected int _numTestsPerEvictionRun;
    @Deprecated
    protected long _minEvictableIdleTimeMillis;
    @Deprecated
    protected long _softMinEvictableIdleTimeMillis;
    @Deprecated
    protected boolean _lifo;
    @Deprecated
    protected PoolableObjectFactory _factory;
    
    public GenericObjectPoolFactory(final PoolableObjectFactory factory) {
        this(factory, 8, (byte)1, -1L, 8, 0, false, false, -1L, 3, 1800000L, false);
    }
    
    public GenericObjectPoolFactory(final PoolableObjectFactory factory, final GenericObjectPool.Config config) throws NullPointerException {
        this(factory, config.maxActive, config.whenExhaustedAction, config.maxWait, config.maxIdle, config.minIdle, config.testOnBorrow, config.testOnReturn, config.timeBetweenEvictionRunsMillis, config.numTestsPerEvictionRun, config.minEvictableIdleTimeMillis, config.testWhileIdle, config.softMinEvictableIdleTimeMillis, config.lifo);
    }
    
    public GenericObjectPoolFactory(final PoolableObjectFactory factory, final int maxActive) {
        this(factory, maxActive, (byte)1, -1L, 8, 0, false, false, -1L, 3, 1800000L, false);
    }
    
    public GenericObjectPoolFactory(final PoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait) {
        this(factory, maxActive, whenExhaustedAction, maxWait, 8, 0, false, false, -1L, 3, 1800000L, false);
    }
    
    public GenericObjectPoolFactory(final PoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final boolean testOnBorrow, final boolean testOnReturn) {
        this(factory, maxActive, whenExhaustedAction, maxWait, 8, 0, testOnBorrow, testOnReturn, -1L, 3, 1800000L, false);
    }
    
    public GenericObjectPoolFactory(final PoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final int maxIdle) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, 0, false, false, -1L, 3, 1800000L, false);
    }
    
    public GenericObjectPoolFactory(final PoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final int maxIdle, final boolean testOnBorrow, final boolean testOnReturn) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, 0, testOnBorrow, testOnReturn, -1L, 3, 1800000L, false);
    }
    
    public GenericObjectPoolFactory(final PoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final int maxIdle, final boolean testOnBorrow, final boolean testOnReturn, final long timeBetweenEvictionRunsMillis, final int numTestsPerEvictionRun, final long minEvictableIdleTimeMillis, final boolean testWhileIdle) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, 0, testOnBorrow, testOnReturn, timeBetweenEvictionRunsMillis, numTestsPerEvictionRun, minEvictableIdleTimeMillis, testWhileIdle, -1L);
    }
    
    public GenericObjectPoolFactory(final PoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final int maxIdle, final int minIdle, final boolean testOnBorrow, final boolean testOnReturn, final long timeBetweenEvictionRunsMillis, final int numTestsPerEvictionRun, final long minEvictableIdleTimeMillis, final boolean testWhileIdle) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, minIdle, testOnBorrow, testOnReturn, timeBetweenEvictionRunsMillis, numTestsPerEvictionRun, minEvictableIdleTimeMillis, testWhileIdle, -1L);
    }
    
    public GenericObjectPoolFactory(final PoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final int maxIdle, final int minIdle, final boolean testOnBorrow, final boolean testOnReturn, final long timeBetweenEvictionRunsMillis, final int numTestsPerEvictionRun, final long minEvictableIdleTimeMillis, final boolean testWhileIdle, final long softMinEvictableIdleTimeMillis) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, minIdle, testOnBorrow, testOnReturn, timeBetweenEvictionRunsMillis, numTestsPerEvictionRun, minEvictableIdleTimeMillis, testWhileIdle, softMinEvictableIdleTimeMillis, true);
    }
    
    public GenericObjectPoolFactory(final PoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final int maxIdle, final int minIdle, final boolean testOnBorrow, final boolean testOnReturn, final long timeBetweenEvictionRunsMillis, final int numTestsPerEvictionRun, final long minEvictableIdleTimeMillis, final boolean testWhileIdle, final long softMinEvictableIdleTimeMillis, final boolean lifo) {
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
        this._softMinEvictableIdleTimeMillis = 1800000L;
        this._lifo = true;
        this._factory = null;
        this._maxIdle = maxIdle;
        this._minIdle = minIdle;
        this._maxActive = maxActive;
        this._maxWait = maxWait;
        this._whenExhaustedAction = whenExhaustedAction;
        this._testOnBorrow = testOnBorrow;
        this._testOnReturn = testOnReturn;
        this._testWhileIdle = testWhileIdle;
        this._timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        this._numTestsPerEvictionRun = numTestsPerEvictionRun;
        this._minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
        this._softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
        this._lifo = lifo;
        this._factory = factory;
    }
    
    @Override
    public ObjectPool createPool() {
        return new GenericObjectPool(this._factory, this._maxActive, this._whenExhaustedAction, this._maxWait, this._maxIdle, this._minIdle, this._testOnBorrow, this._testOnReturn, this._timeBetweenEvictionRunsMillis, this._numTestsPerEvictionRun, this._minEvictableIdleTimeMillis, this._testWhileIdle, this._softMinEvictableIdleTimeMillis, this._lifo);
    }
    
    public int getMaxIdle() {
        return this._maxIdle;
    }
    
    public int getMinIdle() {
        return this._minIdle;
    }
    
    public int getMaxActive() {
        return this._maxActive;
    }
    
    public long getMaxWait() {
        return this._maxWait;
    }
    
    public byte getWhenExhaustedAction() {
        return this._whenExhaustedAction;
    }
    
    public boolean getTestOnBorrow() {
        return this._testOnBorrow;
    }
    
    public boolean getTestOnReturn() {
        return this._testOnReturn;
    }
    
    public boolean getTestWhileIdle() {
        return this._testWhileIdle;
    }
    
    public long getTimeBetweenEvictionRunsMillis() {
        return this._timeBetweenEvictionRunsMillis;
    }
    
    public int getNumTestsPerEvictionRun() {
        return this._numTestsPerEvictionRun;
    }
    
    public long getMinEvictableIdleTimeMillis() {
        return this._minEvictableIdleTimeMillis;
    }
    
    public long getSoftMinEvictableIdleTimeMillis() {
        return this._softMinEvictableIdleTimeMillis;
    }
    
    public boolean getLifo() {
        return this._lifo;
    }
    
    public PoolableObjectFactory getFactory() {
        return this._factory;
    }
}
