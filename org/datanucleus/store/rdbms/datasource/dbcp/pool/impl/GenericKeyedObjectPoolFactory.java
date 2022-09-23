// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.pool.impl;

import org.datanucleus.store.rdbms.datasource.dbcp.pool.KeyedObjectPool;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.KeyedPoolableObjectFactory;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.KeyedObjectPoolFactory;

public class GenericKeyedObjectPoolFactory implements KeyedObjectPoolFactory
{
    @Deprecated
    protected int _maxIdle;
    @Deprecated
    protected int _maxActive;
    @Deprecated
    protected int _maxTotal;
    @Deprecated
    protected int _minIdle;
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
    protected KeyedPoolableObjectFactory _factory;
    @Deprecated
    protected boolean _lifo;
    
    public GenericKeyedObjectPoolFactory(final KeyedPoolableObjectFactory factory) {
        this(factory, 8, (byte)1, -1L, 8, false, false, -1L, 3, 1800000L, false);
    }
    
    public GenericKeyedObjectPoolFactory(final KeyedPoolableObjectFactory factory, final GenericKeyedObjectPool.Config config) throws NullPointerException {
        this(factory, config.maxActive, config.whenExhaustedAction, config.maxWait, config.maxIdle, config.maxTotal, config.minIdle, config.testOnBorrow, config.testOnReturn, config.timeBetweenEvictionRunsMillis, config.numTestsPerEvictionRun, config.minEvictableIdleTimeMillis, config.testWhileIdle, config.lifo);
    }
    
    public GenericKeyedObjectPoolFactory(final KeyedPoolableObjectFactory factory, final int maxActive) {
        this(factory, maxActive, (byte)1, -1L, 8, -1, false, false, -1L, 3, 1800000L, false);
    }
    
    public GenericKeyedObjectPoolFactory(final KeyedPoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait) {
        this(factory, maxActive, whenExhaustedAction, maxWait, 8, -1, false, false, -1L, 3, 1800000L, false);
    }
    
    public GenericKeyedObjectPoolFactory(final KeyedPoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final boolean testOnBorrow, final boolean testOnReturn) {
        this(factory, maxActive, whenExhaustedAction, maxWait, 8, -1, testOnBorrow, testOnReturn, -1L, 3, 1800000L, false);
    }
    
    public GenericKeyedObjectPoolFactory(final KeyedPoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final int maxIdle) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, -1, false, false, -1L, 3, 1800000L, false);
    }
    
    public GenericKeyedObjectPoolFactory(final KeyedPoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final int maxIdle, final int maxTotal) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, maxTotal, false, false, -1L, 3, 1800000L, false);
    }
    
    public GenericKeyedObjectPoolFactory(final KeyedPoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final int maxIdle, final boolean testOnBorrow, final boolean testOnReturn) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, -1, testOnBorrow, testOnReturn, -1L, 3, 1800000L, false);
    }
    
    public GenericKeyedObjectPoolFactory(final KeyedPoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final int maxIdle, final boolean testOnBorrow, final boolean testOnReturn, final long timeBetweenEvictionRunsMillis, final int numTestsPerEvictionRun, final long minEvictableIdleTimeMillis, final boolean testWhileIdle) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, -1, testOnBorrow, testOnReturn, timeBetweenEvictionRunsMillis, numTestsPerEvictionRun, minEvictableIdleTimeMillis, testWhileIdle);
    }
    
    public GenericKeyedObjectPoolFactory(final KeyedPoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final int maxIdle, final int maxTotal, final boolean testOnBorrow, final boolean testOnReturn, final long timeBetweenEvictionRunsMillis, final int numTestsPerEvictionRun, final long minEvictableIdleTimeMillis, final boolean testWhileIdle) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, maxTotal, 0, testOnBorrow, testOnReturn, timeBetweenEvictionRunsMillis, numTestsPerEvictionRun, minEvictableIdleTimeMillis, testWhileIdle);
    }
    
    public GenericKeyedObjectPoolFactory(final KeyedPoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final int maxIdle, final int maxTotal, final int minIdle, final boolean testOnBorrow, final boolean testOnReturn, final long timeBetweenEvictionRunsMillis, final int numTestsPerEvictionRun, final long minEvictableIdleTimeMillis, final boolean testWhileIdle) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, maxTotal, minIdle, testOnBorrow, testOnReturn, timeBetweenEvictionRunsMillis, numTestsPerEvictionRun, minEvictableIdleTimeMillis, testWhileIdle, true);
    }
    
    public GenericKeyedObjectPoolFactory(final KeyedPoolableObjectFactory factory, final int maxActive, final byte whenExhaustedAction, final long maxWait, final int maxIdle, final int maxTotal, final int minIdle, final boolean testOnBorrow, final boolean testOnReturn, final long timeBetweenEvictionRunsMillis, final int numTestsPerEvictionRun, final long minEvictableIdleTimeMillis, final boolean testWhileIdle, final boolean lifo) {
        this._maxIdle = 8;
        this._maxActive = 8;
        this._maxTotal = -1;
        this._minIdle = 0;
        this._maxWait = -1L;
        this._whenExhaustedAction = 1;
        this._testOnBorrow = false;
        this._testOnReturn = false;
        this._testWhileIdle = false;
        this._timeBetweenEvictionRunsMillis = -1L;
        this._numTestsPerEvictionRun = 3;
        this._minEvictableIdleTimeMillis = 1800000L;
        this._factory = null;
        this._lifo = true;
        this._maxIdle = maxIdle;
        this._maxActive = maxActive;
        this._maxTotal = maxTotal;
        this._minIdle = minIdle;
        this._maxWait = maxWait;
        this._whenExhaustedAction = whenExhaustedAction;
        this._testOnBorrow = testOnBorrow;
        this._testOnReturn = testOnReturn;
        this._testWhileIdle = testWhileIdle;
        this._timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        this._numTestsPerEvictionRun = numTestsPerEvictionRun;
        this._minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
        this._factory = factory;
        this._lifo = lifo;
    }
    
    @Override
    public KeyedObjectPool createPool() {
        return new GenericKeyedObjectPool(this._factory, this._maxActive, this._whenExhaustedAction, this._maxWait, this._maxIdle, this._maxTotal, this._minIdle, this._testOnBorrow, this._testOnReturn, this._timeBetweenEvictionRunsMillis, this._numTestsPerEvictionRun, this._minEvictableIdleTimeMillis, this._testWhileIdle, this._lifo);
    }
    
    public int getMaxIdle() {
        return this._maxIdle;
    }
    
    public int getMaxActive() {
        return this._maxActive;
    }
    
    public int getMaxTotal() {
        return this._maxTotal;
    }
    
    public int getMinIdle() {
        return this._minIdle;
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
    
    public KeyedPoolableObjectFactory getFactory() {
        return this._factory;
    }
    
    public boolean getLifo() {
        return this._lifo;
    }
}
