// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.pool.impl;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.ObjectPoolFactory;

public class GenericObjectPoolFactory implements ObjectPoolFactory
{
    protected int _maxIdle;
    protected int _minIdle;
    protected int _maxActive;
    protected long _maxWait;
    protected byte _whenExhaustedAction;
    protected boolean _testOnBorrow;
    protected boolean _testOnReturn;
    protected boolean _testWhileIdle;
    protected long _timeBetweenEvictionRunsMillis;
    protected int _numTestsPerEvictionRun;
    protected long _minEvictableIdleTimeMillis;
    protected long _softMinEvictableIdleTimeMillis;
    protected boolean _lifo;
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
    
    public ObjectPool createPool() {
        return new GenericObjectPool(this._factory, this._maxActive, this._whenExhaustedAction, this._maxWait, this._maxIdle, this._minIdle, this._testOnBorrow, this._testOnReturn, this._timeBetweenEvictionRunsMillis, this._numTestsPerEvictionRun, this._minEvictableIdleTimeMillis, this._testWhileIdle, this._softMinEvictableIdleTimeMillis, this._lifo);
    }
}
