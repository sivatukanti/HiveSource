// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.pool;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.TimerTask;
import java.util.Timer;

public final class PoolUtils
{
    private static Timer MIN_IDLE_TIMER;
    
    public static void checkRethrow(final Throwable t) {
        if (t instanceof ThreadDeath) {
            throw (ThreadDeath)t;
        }
        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError)t;
        }
    }
    
    public static PoolableObjectFactory adapt(final KeyedPoolableObjectFactory keyedFactory) throws IllegalArgumentException {
        return adapt(keyedFactory, new Object());
    }
    
    public static PoolableObjectFactory adapt(final KeyedPoolableObjectFactory keyedFactory, final Object key) throws IllegalArgumentException {
        return new PoolableObjectFactoryAdaptor(keyedFactory, key);
    }
    
    public static KeyedPoolableObjectFactory adapt(final PoolableObjectFactory factory) throws IllegalArgumentException {
        return new KeyedPoolableObjectFactoryAdaptor(factory);
    }
    
    public static ObjectPool adapt(final KeyedObjectPool keyedPool) throws IllegalArgumentException {
        return adapt(keyedPool, new Object());
    }
    
    public static ObjectPool adapt(final KeyedObjectPool keyedPool, final Object key) throws IllegalArgumentException {
        return new ObjectPoolAdaptor(keyedPool, key);
    }
    
    public static KeyedObjectPool adapt(final ObjectPool pool) throws IllegalArgumentException {
        return new KeyedObjectPoolAdaptor(pool);
    }
    
    public static ObjectPool checkedPool(final ObjectPool pool, final Class type) {
        if (pool == null) {
            throw new IllegalArgumentException("pool must not be null.");
        }
        if (type == null) {
            throw new IllegalArgumentException("type must not be null.");
        }
        return new CheckedObjectPool(pool, type);
    }
    
    public static KeyedObjectPool checkedPool(final KeyedObjectPool keyedPool, final Class type) {
        if (keyedPool == null) {
            throw new IllegalArgumentException("keyedPool must not be null.");
        }
        if (type == null) {
            throw new IllegalArgumentException("type must not be null.");
        }
        return new CheckedKeyedObjectPool(keyedPool, type);
    }
    
    public static TimerTask checkMinIdle(final ObjectPool pool, final int minIdle, final long period) throws IllegalArgumentException {
        if (pool == null) {
            throw new IllegalArgumentException("keyedPool must not be null.");
        }
        if (minIdle < 0) {
            throw new IllegalArgumentException("minIdle must be non-negative.");
        }
        final TimerTask task = new ObjectPoolMinIdleTimerTask(pool, minIdle);
        getMinIdleTimer().schedule(task, 0L, period);
        return task;
    }
    
    public static TimerTask checkMinIdle(final KeyedObjectPool keyedPool, final Object key, final int minIdle, final long period) throws IllegalArgumentException {
        if (keyedPool == null) {
            throw new IllegalArgumentException("keyedPool must not be null.");
        }
        if (key == null) {
            throw new IllegalArgumentException("key must not be null.");
        }
        if (minIdle < 0) {
            throw new IllegalArgumentException("minIdle must be non-negative.");
        }
        final TimerTask task = new KeyedObjectPoolMinIdleTimerTask(keyedPool, key, minIdle);
        getMinIdleTimer().schedule(task, 0L, period);
        return task;
    }
    
    public static Map checkMinIdle(final KeyedObjectPool keyedPool, final Collection keys, final int minIdle, final long period) throws IllegalArgumentException {
        if (keys == null) {
            throw new IllegalArgumentException("keys must not be null.");
        }
        final Map tasks = new HashMap(keys.size());
        for (final Object key : keys) {
            final TimerTask task = checkMinIdle(keyedPool, key, minIdle, period);
            tasks.put(key, task);
        }
        return tasks;
    }
    
    public static void prefill(final ObjectPool pool, final int count) throws Exception, IllegalArgumentException {
        if (pool == null) {
            throw new IllegalArgumentException("pool must not be null.");
        }
        for (int i = 0; i < count; ++i) {
            pool.addObject();
        }
    }
    
    public static void prefill(final KeyedObjectPool keyedPool, final Object key, final int count) throws Exception, IllegalArgumentException {
        if (keyedPool == null) {
            throw new IllegalArgumentException("keyedPool must not be null.");
        }
        if (key == null) {
            throw new IllegalArgumentException("key must not be null.");
        }
        for (int i = 0; i < count; ++i) {
            keyedPool.addObject(key);
        }
    }
    
    public static void prefill(final KeyedObjectPool keyedPool, final Collection keys, final int count) throws Exception, IllegalArgumentException {
        if (keys == null) {
            throw new IllegalArgumentException("keys must not be null.");
        }
        final Iterator iter = keys.iterator();
        while (iter.hasNext()) {
            prefill(keyedPool, iter.next(), count);
        }
    }
    
    public static ObjectPool synchronizedPool(final ObjectPool pool) {
        if (pool == null) {
            throw new IllegalArgumentException("pool must not be null.");
        }
        return new SynchronizedObjectPool(pool);
    }
    
    public static KeyedObjectPool synchronizedPool(final KeyedObjectPool keyedPool) {
        if (keyedPool == null) {
            throw new IllegalArgumentException("keyedPool must not be null.");
        }
        return new SynchronizedKeyedObjectPool(keyedPool);
    }
    
    public static PoolableObjectFactory synchronizedPoolableFactory(final PoolableObjectFactory factory) {
        return new SynchronizedPoolableObjectFactory(factory);
    }
    
    public static KeyedPoolableObjectFactory synchronizedPoolableFactory(final KeyedPoolableObjectFactory keyedFactory) {
        return new SynchronizedKeyedPoolableObjectFactory(keyedFactory);
    }
    
    public static ObjectPool erodingPool(final ObjectPool pool) {
        return erodingPool(pool, 1.0f);
    }
    
    public static ObjectPool erodingPool(final ObjectPool pool, final float factor) {
        if (pool == null) {
            throw new IllegalArgumentException("pool must not be null.");
        }
        if (factor <= 0.0f) {
            throw new IllegalArgumentException("factor must be positive.");
        }
        return new ErodingObjectPool(pool, factor);
    }
    
    public static KeyedObjectPool erodingPool(final KeyedObjectPool keyedPool) {
        return erodingPool(keyedPool, 1.0f);
    }
    
    public static KeyedObjectPool erodingPool(final KeyedObjectPool keyedPool, final float factor) {
        return erodingPool(keyedPool, factor, false);
    }
    
    public static KeyedObjectPool erodingPool(final KeyedObjectPool keyedPool, final float factor, final boolean perKey) {
        if (keyedPool == null) {
            throw new IllegalArgumentException("keyedPool must not be null.");
        }
        if (factor <= 0.0f) {
            throw new IllegalArgumentException("factor must be positive.");
        }
        if (perKey) {
            return new ErodingPerKeyKeyedObjectPool(keyedPool, factor);
        }
        return new ErodingKeyedObjectPool(keyedPool, factor);
    }
    
    private static synchronized Timer getMinIdleTimer() {
        if (PoolUtils.MIN_IDLE_TIMER == null) {
            PoolUtils.MIN_IDLE_TIMER = new Timer(true);
        }
        return PoolUtils.MIN_IDLE_TIMER;
    }
    
    private static class PoolableObjectFactoryAdaptor implements PoolableObjectFactory
    {
        private final Object key;
        private final KeyedPoolableObjectFactory keyedFactory;
        
        PoolableObjectFactoryAdaptor(final KeyedPoolableObjectFactory keyedFactory, final Object key) throws IllegalArgumentException {
            if (keyedFactory == null) {
                throw new IllegalArgumentException("keyedFactory must not be null.");
            }
            if (key == null) {
                throw new IllegalArgumentException("key must not be null.");
            }
            this.keyedFactory = keyedFactory;
            this.key = key;
        }
        
        @Override
        public Object makeObject() throws Exception {
            return this.keyedFactory.makeObject(this.key);
        }
        
        @Override
        public void destroyObject(final Object obj) throws Exception {
            this.keyedFactory.destroyObject(this.key, obj);
        }
        
        @Override
        public boolean validateObject(final Object obj) {
            return this.keyedFactory.validateObject(this.key, obj);
        }
        
        @Override
        public void activateObject(final Object obj) throws Exception {
            this.keyedFactory.activateObject(this.key, obj);
        }
        
        @Override
        public void passivateObject(final Object obj) throws Exception {
            this.keyedFactory.passivateObject(this.key, obj);
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append("PoolableObjectFactoryAdaptor");
            sb.append("{key=").append(this.key);
            sb.append(", keyedFactory=").append(this.keyedFactory);
            sb.append('}');
            return sb.toString();
        }
    }
    
    private static class KeyedPoolableObjectFactoryAdaptor implements KeyedPoolableObjectFactory
    {
        private final PoolableObjectFactory factory;
        
        KeyedPoolableObjectFactoryAdaptor(final PoolableObjectFactory factory) throws IllegalArgumentException {
            if (factory == null) {
                throw new IllegalArgumentException("factory must not be null.");
            }
            this.factory = factory;
        }
        
        @Override
        public Object makeObject(final Object key) throws Exception {
            return this.factory.makeObject();
        }
        
        @Override
        public void destroyObject(final Object key, final Object obj) throws Exception {
            this.factory.destroyObject(obj);
        }
        
        @Override
        public boolean validateObject(final Object key, final Object obj) {
            return this.factory.validateObject(obj);
        }
        
        @Override
        public void activateObject(final Object key, final Object obj) throws Exception {
            this.factory.activateObject(obj);
        }
        
        @Override
        public void passivateObject(final Object key, final Object obj) throws Exception {
            this.factory.passivateObject(obj);
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append("KeyedPoolableObjectFactoryAdaptor");
            sb.append("{factory=").append(this.factory);
            sb.append('}');
            return sb.toString();
        }
    }
    
    private static class ObjectPoolAdaptor implements ObjectPool
    {
        private final Object key;
        private final KeyedObjectPool keyedPool;
        
        ObjectPoolAdaptor(final KeyedObjectPool keyedPool, final Object key) throws IllegalArgumentException {
            if (keyedPool == null) {
                throw new IllegalArgumentException("keyedPool must not be null.");
            }
            if (key == null) {
                throw new IllegalArgumentException("key must not be null.");
            }
            this.keyedPool = keyedPool;
            this.key = key;
        }
        
        @Override
        public Object borrowObject() throws Exception, NoSuchElementException, IllegalStateException {
            return this.keyedPool.borrowObject(this.key);
        }
        
        @Override
        public void returnObject(final Object obj) {
            try {
                this.keyedPool.returnObject(this.key, obj);
            }
            catch (Exception ex) {}
        }
        
        @Override
        public void invalidateObject(final Object obj) {
            try {
                this.keyedPool.invalidateObject(this.key, obj);
            }
            catch (Exception ex) {}
        }
        
        @Override
        public void addObject() throws Exception, IllegalStateException {
            this.keyedPool.addObject(this.key);
        }
        
        @Override
        public int getNumIdle() throws UnsupportedOperationException {
            return this.keyedPool.getNumIdle(this.key);
        }
        
        @Override
        public int getNumActive() throws UnsupportedOperationException {
            return this.keyedPool.getNumActive(this.key);
        }
        
        @Override
        public void clear() throws Exception, UnsupportedOperationException {
            this.keyedPool.clear();
        }
        
        @Override
        public void close() {
            try {
                this.keyedPool.close();
            }
            catch (Exception ex) {}
        }
        
        @Override
        @Deprecated
        public void setFactory(final PoolableObjectFactory factory) throws IllegalStateException, UnsupportedOperationException {
            this.keyedPool.setFactory(PoolUtils.adapt(factory));
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append("ObjectPoolAdaptor");
            sb.append("{key=").append(this.key);
            sb.append(", keyedPool=").append(this.keyedPool);
            sb.append('}');
            return sb.toString();
        }
    }
    
    private static class KeyedObjectPoolAdaptor implements KeyedObjectPool
    {
        private final ObjectPool pool;
        
        KeyedObjectPoolAdaptor(final ObjectPool pool) throws IllegalArgumentException {
            if (pool == null) {
                throw new IllegalArgumentException("pool must not be null.");
            }
            this.pool = pool;
        }
        
        @Override
        public Object borrowObject(final Object key) throws Exception, NoSuchElementException, IllegalStateException {
            return this.pool.borrowObject();
        }
        
        @Override
        public void returnObject(final Object key, final Object obj) {
            try {
                this.pool.returnObject(obj);
            }
            catch (Exception ex) {}
        }
        
        @Override
        public void invalidateObject(final Object key, final Object obj) {
            try {
                this.pool.invalidateObject(obj);
            }
            catch (Exception ex) {}
        }
        
        @Override
        public void addObject(final Object key) throws Exception, IllegalStateException {
            this.pool.addObject();
        }
        
        @Override
        public int getNumIdle(final Object key) throws UnsupportedOperationException {
            return this.pool.getNumIdle();
        }
        
        @Override
        public int getNumActive(final Object key) throws UnsupportedOperationException {
            return this.pool.getNumActive();
        }
        
        @Override
        public int getNumIdle() throws UnsupportedOperationException {
            return this.pool.getNumIdle();
        }
        
        @Override
        public int getNumActive() throws UnsupportedOperationException {
            return this.pool.getNumActive();
        }
        
        @Override
        public void clear() throws Exception, UnsupportedOperationException {
            this.pool.clear();
        }
        
        @Override
        public void clear(final Object key) throws Exception, UnsupportedOperationException {
            this.pool.clear();
        }
        
        @Override
        public void close() {
            try {
                this.pool.close();
            }
            catch (Exception ex) {}
        }
        
        @Override
        @Deprecated
        public void setFactory(final KeyedPoolableObjectFactory factory) throws IllegalStateException, UnsupportedOperationException {
            this.pool.setFactory(PoolUtils.adapt(factory));
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append("KeyedObjectPoolAdaptor");
            sb.append("{pool=").append(this.pool);
            sb.append('}');
            return sb.toString();
        }
    }
    
    private static class CheckedObjectPool implements ObjectPool
    {
        private final Class type;
        private final ObjectPool pool;
        
        CheckedObjectPool(final ObjectPool pool, final Class type) {
            if (pool == null) {
                throw new IllegalArgumentException("pool must not be null.");
            }
            if (type == null) {
                throw new IllegalArgumentException("type must not be null.");
            }
            this.pool = pool;
            this.type = type;
        }
        
        @Override
        public Object borrowObject() throws Exception, NoSuchElementException, IllegalStateException {
            final Object obj = this.pool.borrowObject();
            if (this.type.isInstance(obj)) {
                return obj;
            }
            throw new ClassCastException("Borrowed object is not of type: " + this.type.getName() + " was: " + obj);
        }
        
        @Override
        public void returnObject(final Object obj) {
            if (this.type.isInstance(obj)) {
                try {
                    this.pool.returnObject(obj);
                }
                catch (Exception e) {}
                return;
            }
            throw new ClassCastException("Returned object is not of type: " + this.type.getName() + " was: " + obj);
        }
        
        @Override
        public void invalidateObject(final Object obj) {
            if (this.type.isInstance(obj)) {
                try {
                    this.pool.invalidateObject(obj);
                }
                catch (Exception e) {}
                return;
            }
            throw new ClassCastException("Invalidated object is not of type: " + this.type.getName() + " was: " + obj);
        }
        
        @Override
        public void addObject() throws Exception, IllegalStateException, UnsupportedOperationException {
            this.pool.addObject();
        }
        
        @Override
        public int getNumIdle() throws UnsupportedOperationException {
            return this.pool.getNumIdle();
        }
        
        @Override
        public int getNumActive() throws UnsupportedOperationException {
            return this.pool.getNumActive();
        }
        
        @Override
        public void clear() throws Exception, UnsupportedOperationException {
            this.pool.clear();
        }
        
        @Override
        public void close() {
            try {
                this.pool.close();
            }
            catch (Exception ex) {}
        }
        
        @Override
        @Deprecated
        public void setFactory(final PoolableObjectFactory factory) throws IllegalStateException, UnsupportedOperationException {
            this.pool.setFactory(factory);
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append("CheckedObjectPool");
            sb.append("{type=").append(this.type);
            sb.append(", pool=").append(this.pool);
            sb.append('}');
            return sb.toString();
        }
    }
    
    private static class CheckedKeyedObjectPool implements KeyedObjectPool
    {
        private final Class type;
        private final KeyedObjectPool keyedPool;
        
        CheckedKeyedObjectPool(final KeyedObjectPool keyedPool, final Class type) {
            if (keyedPool == null) {
                throw new IllegalArgumentException("keyedPool must not be null.");
            }
            if (type == null) {
                throw new IllegalArgumentException("type must not be null.");
            }
            this.keyedPool = keyedPool;
            this.type = type;
        }
        
        @Override
        public Object borrowObject(final Object key) throws Exception, NoSuchElementException, IllegalStateException {
            final Object obj = this.keyedPool.borrowObject(key);
            if (this.type.isInstance(obj)) {
                return obj;
            }
            throw new ClassCastException("Borrowed object for key: " + key + " is not of type: " + this.type.getName() + " was: " + obj);
        }
        
        @Override
        public void returnObject(final Object key, final Object obj) {
            if (this.type.isInstance(obj)) {
                try {
                    this.keyedPool.returnObject(key, obj);
                }
                catch (Exception e) {}
                return;
            }
            throw new ClassCastException("Returned object for key: " + key + " is not of type: " + this.type.getName() + " was: " + obj);
        }
        
        @Override
        public void invalidateObject(final Object key, final Object obj) {
            if (this.type.isInstance(obj)) {
                try {
                    this.keyedPool.invalidateObject(key, obj);
                }
                catch (Exception e) {}
                return;
            }
            throw new ClassCastException("Invalidated object for key: " + key + " is not of type: " + this.type.getName() + " was: " + obj);
        }
        
        @Override
        public void addObject(final Object key) throws Exception, IllegalStateException, UnsupportedOperationException {
            this.keyedPool.addObject(key);
        }
        
        @Override
        public int getNumIdle(final Object key) throws UnsupportedOperationException {
            return this.keyedPool.getNumIdle(key);
        }
        
        @Override
        public int getNumActive(final Object key) throws UnsupportedOperationException {
            return this.keyedPool.getNumActive(key);
        }
        
        @Override
        public int getNumIdle() throws UnsupportedOperationException {
            return this.keyedPool.getNumIdle();
        }
        
        @Override
        public int getNumActive() throws UnsupportedOperationException {
            return this.keyedPool.getNumActive();
        }
        
        @Override
        public void clear() throws Exception, UnsupportedOperationException {
            this.keyedPool.clear();
        }
        
        @Override
        public void clear(final Object key) throws Exception, UnsupportedOperationException {
            this.keyedPool.clear(key);
        }
        
        @Override
        public void close() {
            try {
                this.keyedPool.close();
            }
            catch (Exception ex) {}
        }
        
        @Override
        @Deprecated
        public void setFactory(final KeyedPoolableObjectFactory factory) throws IllegalStateException, UnsupportedOperationException {
            this.keyedPool.setFactory(factory);
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append("CheckedKeyedObjectPool");
            sb.append("{type=").append(this.type);
            sb.append(", keyedPool=").append(this.keyedPool);
            sb.append('}');
            return sb.toString();
        }
    }
    
    private static class ObjectPoolMinIdleTimerTask extends TimerTask
    {
        private final int minIdle;
        private final ObjectPool pool;
        
        ObjectPoolMinIdleTimerTask(final ObjectPool pool, final int minIdle) throws IllegalArgumentException {
            if (pool == null) {
                throw new IllegalArgumentException("pool must not be null.");
            }
            this.pool = pool;
            this.minIdle = minIdle;
        }
        
        @Override
        public void run() {
            boolean success = false;
            try {
                if (this.pool.getNumIdle() < this.minIdle) {
                    this.pool.addObject();
                }
                success = true;
            }
            catch (Exception e) {
                this.cancel();
            }
            finally {
                if (!success) {
                    this.cancel();
                }
            }
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append("ObjectPoolMinIdleTimerTask");
            sb.append("{minIdle=").append(this.minIdle);
            sb.append(", pool=").append(this.pool);
            sb.append('}');
            return sb.toString();
        }
    }
    
    private static class KeyedObjectPoolMinIdleTimerTask extends TimerTask
    {
        private final int minIdle;
        private final Object key;
        private final KeyedObjectPool keyedPool;
        
        KeyedObjectPoolMinIdleTimerTask(final KeyedObjectPool keyedPool, final Object key, final int minIdle) throws IllegalArgumentException {
            if (keyedPool == null) {
                throw new IllegalArgumentException("keyedPool must not be null.");
            }
            this.keyedPool = keyedPool;
            this.key = key;
            this.minIdle = minIdle;
        }
        
        @Override
        public void run() {
            boolean success = false;
            try {
                if (this.keyedPool.getNumIdle(this.key) < this.minIdle) {
                    this.keyedPool.addObject(this.key);
                }
                success = true;
            }
            catch (Exception e) {
                this.cancel();
            }
            finally {
                if (!success) {
                    this.cancel();
                }
            }
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append("KeyedObjectPoolMinIdleTimerTask");
            sb.append("{minIdle=").append(this.minIdle);
            sb.append(", key=").append(this.key);
            sb.append(", keyedPool=").append(this.keyedPool);
            sb.append('}');
            return sb.toString();
        }
    }
    
    private static class SynchronizedObjectPool implements ObjectPool
    {
        private final Object lock;
        private final ObjectPool pool;
        
        SynchronizedObjectPool(final ObjectPool pool) throws IllegalArgumentException {
            if (pool == null) {
                throw new IllegalArgumentException("pool must not be null.");
            }
            this.pool = pool;
            this.lock = new Object();
        }
        
        @Override
        public Object borrowObject() throws Exception, NoSuchElementException, IllegalStateException {
            synchronized (this.lock) {
                return this.pool.borrowObject();
            }
        }
        
        @Override
        public void returnObject(final Object obj) {
            synchronized (this.lock) {
                try {
                    this.pool.returnObject(obj);
                }
                catch (Exception ex) {}
            }
        }
        
        @Override
        public void invalidateObject(final Object obj) {
            synchronized (this.lock) {
                try {
                    this.pool.invalidateObject(obj);
                }
                catch (Exception ex) {}
            }
        }
        
        @Override
        public void addObject() throws Exception, IllegalStateException, UnsupportedOperationException {
            synchronized (this.lock) {
                this.pool.addObject();
            }
        }
        
        @Override
        public int getNumIdle() throws UnsupportedOperationException {
            synchronized (this.lock) {
                return this.pool.getNumIdle();
            }
        }
        
        @Override
        public int getNumActive() throws UnsupportedOperationException {
            synchronized (this.lock) {
                return this.pool.getNumActive();
            }
        }
        
        @Override
        public void clear() throws Exception, UnsupportedOperationException {
            synchronized (this.lock) {
                this.pool.clear();
            }
        }
        
        @Override
        public void close() {
            try {
                synchronized (this.lock) {
                    this.pool.close();
                }
            }
            catch (Exception ex) {}
        }
        
        @Override
        @Deprecated
        public void setFactory(final PoolableObjectFactory factory) throws IllegalStateException, UnsupportedOperationException {
            synchronized (this.lock) {
                this.pool.setFactory(factory);
            }
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append("SynchronizedObjectPool");
            sb.append("{pool=").append(this.pool);
            sb.append('}');
            return sb.toString();
        }
    }
    
    private static class SynchronizedKeyedObjectPool implements KeyedObjectPool
    {
        private final Object lock;
        private final KeyedObjectPool keyedPool;
        
        SynchronizedKeyedObjectPool(final KeyedObjectPool keyedPool) throws IllegalArgumentException {
            if (keyedPool == null) {
                throw new IllegalArgumentException("keyedPool must not be null.");
            }
            this.keyedPool = keyedPool;
            this.lock = new Object();
        }
        
        @Override
        public Object borrowObject(final Object key) throws Exception, NoSuchElementException, IllegalStateException {
            synchronized (this.lock) {
                return this.keyedPool.borrowObject(key);
            }
        }
        
        @Override
        public void returnObject(final Object key, final Object obj) {
            synchronized (this.lock) {
                try {
                    this.keyedPool.returnObject(key, obj);
                }
                catch (Exception ex) {}
            }
        }
        
        @Override
        public void invalidateObject(final Object key, final Object obj) {
            synchronized (this.lock) {
                try {
                    this.keyedPool.invalidateObject(key, obj);
                }
                catch (Exception ex) {}
            }
        }
        
        @Override
        public void addObject(final Object key) throws Exception, IllegalStateException, UnsupportedOperationException {
            synchronized (this.lock) {
                this.keyedPool.addObject(key);
            }
        }
        
        @Override
        public int getNumIdle(final Object key) throws UnsupportedOperationException {
            synchronized (this.lock) {
                return this.keyedPool.getNumIdle(key);
            }
        }
        
        @Override
        public int getNumActive(final Object key) throws UnsupportedOperationException {
            synchronized (this.lock) {
                return this.keyedPool.getNumActive(key);
            }
        }
        
        @Override
        public int getNumIdle() throws UnsupportedOperationException {
            synchronized (this.lock) {
                return this.keyedPool.getNumIdle();
            }
        }
        
        @Override
        public int getNumActive() throws UnsupportedOperationException {
            synchronized (this.lock) {
                return this.keyedPool.getNumActive();
            }
        }
        
        @Override
        public void clear() throws Exception, UnsupportedOperationException {
            synchronized (this.lock) {
                this.keyedPool.clear();
            }
        }
        
        @Override
        public void clear(final Object key) throws Exception, UnsupportedOperationException {
            synchronized (this.lock) {
                this.keyedPool.clear(key);
            }
        }
        
        @Override
        public void close() {
            try {
                synchronized (this.lock) {
                    this.keyedPool.close();
                }
            }
            catch (Exception ex) {}
        }
        
        @Override
        @Deprecated
        public void setFactory(final KeyedPoolableObjectFactory factory) throws IllegalStateException, UnsupportedOperationException {
            synchronized (this.lock) {
                this.keyedPool.setFactory(factory);
            }
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append("SynchronizedKeyedObjectPool");
            sb.append("{keyedPool=").append(this.keyedPool);
            sb.append('}');
            return sb.toString();
        }
    }
    
    private static class SynchronizedPoolableObjectFactory implements PoolableObjectFactory
    {
        private final Object lock;
        private final PoolableObjectFactory factory;
        
        SynchronizedPoolableObjectFactory(final PoolableObjectFactory factory) throws IllegalArgumentException {
            if (factory == null) {
                throw new IllegalArgumentException("factory must not be null.");
            }
            this.factory = factory;
            this.lock = new Object();
        }
        
        @Override
        public Object makeObject() throws Exception {
            synchronized (this.lock) {
                return this.factory.makeObject();
            }
        }
        
        @Override
        public void destroyObject(final Object obj) throws Exception {
            synchronized (this.lock) {
                this.factory.destroyObject(obj);
            }
        }
        
        @Override
        public boolean validateObject(final Object obj) {
            synchronized (this.lock) {
                return this.factory.validateObject(obj);
            }
        }
        
        @Override
        public void activateObject(final Object obj) throws Exception {
            synchronized (this.lock) {
                this.factory.activateObject(obj);
            }
        }
        
        @Override
        public void passivateObject(final Object obj) throws Exception {
            synchronized (this.lock) {
                this.factory.passivateObject(obj);
            }
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append("SynchronizedPoolableObjectFactory");
            sb.append("{factory=").append(this.factory);
            sb.append('}');
            return sb.toString();
        }
    }
    
    private static class SynchronizedKeyedPoolableObjectFactory implements KeyedPoolableObjectFactory
    {
        private final Object lock;
        private final KeyedPoolableObjectFactory keyedFactory;
        
        SynchronizedKeyedPoolableObjectFactory(final KeyedPoolableObjectFactory keyedFactory) throws IllegalArgumentException {
            if (keyedFactory == null) {
                throw new IllegalArgumentException("keyedFactory must not be null.");
            }
            this.keyedFactory = keyedFactory;
            this.lock = new Object();
        }
        
        @Override
        public Object makeObject(final Object key) throws Exception {
            synchronized (this.lock) {
                return this.keyedFactory.makeObject(key);
            }
        }
        
        @Override
        public void destroyObject(final Object key, final Object obj) throws Exception {
            synchronized (this.lock) {
                this.keyedFactory.destroyObject(key, obj);
            }
        }
        
        @Override
        public boolean validateObject(final Object key, final Object obj) {
            synchronized (this.lock) {
                return this.keyedFactory.validateObject(key, obj);
            }
        }
        
        @Override
        public void activateObject(final Object key, final Object obj) throws Exception {
            synchronized (this.lock) {
                this.keyedFactory.activateObject(key, obj);
            }
        }
        
        @Override
        public void passivateObject(final Object key, final Object obj) throws Exception {
            synchronized (this.lock) {
                this.keyedFactory.passivateObject(key, obj);
            }
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append("SynchronizedKeyedPoolableObjectFactory");
            sb.append("{keyedFactory=").append(this.keyedFactory);
            sb.append('}');
            return sb.toString();
        }
    }
    
    private static class ErodingFactor
    {
        private final float factor;
        private transient volatile long nextShrink;
        private transient volatile int idleHighWaterMark;
        
        public ErodingFactor(final float factor) {
            this.factor = factor;
            this.nextShrink = System.currentTimeMillis() + (long)(900000.0f * factor);
            this.idleHighWaterMark = 1;
        }
        
        public void update(final int numIdle) {
            this.update(System.currentTimeMillis(), numIdle);
        }
        
        public void update(final long now, final int numIdle) {
            final int idle = Math.max(0, numIdle);
            this.idleHighWaterMark = Math.max(idle, this.idleHighWaterMark);
            final float maxInterval = 15.0f;
            final float minutes = 15.0f + -14.0f / this.idleHighWaterMark * idle;
            this.nextShrink = now + (long)(minutes * 60000.0f * this.factor);
        }
        
        public long getNextShrink() {
            return this.nextShrink;
        }
        
        @Override
        public String toString() {
            return "ErodingFactor{factor=" + this.factor + ", idleHighWaterMark=" + this.idleHighWaterMark + '}';
        }
    }
    
    private static class ErodingObjectPool implements ObjectPool
    {
        private final ObjectPool pool;
        private final ErodingFactor factor;
        
        public ErodingObjectPool(final ObjectPool pool, final float factor) {
            this.pool = pool;
            this.factor = new ErodingFactor(factor);
        }
        
        @Override
        public Object borrowObject() throws Exception, NoSuchElementException, IllegalStateException {
            return this.pool.borrowObject();
        }
        
        @Override
        public void returnObject(final Object obj) {
            boolean discard = false;
            final long now = System.currentTimeMillis();
            synchronized (this.pool) {
                if (this.factor.getNextShrink() < now) {
                    final int numIdle = this.pool.getNumIdle();
                    if (numIdle > 0) {
                        discard = true;
                    }
                    this.factor.update(now, numIdle);
                }
            }
            try {
                if (discard) {
                    this.pool.invalidateObject(obj);
                }
                else {
                    this.pool.returnObject(obj);
                }
            }
            catch (Exception ex) {}
        }
        
        @Override
        public void invalidateObject(final Object obj) {
            try {
                this.pool.invalidateObject(obj);
            }
            catch (Exception ex) {}
        }
        
        @Override
        public void addObject() throws Exception, IllegalStateException, UnsupportedOperationException {
            this.pool.addObject();
        }
        
        @Override
        public int getNumIdle() throws UnsupportedOperationException {
            return this.pool.getNumIdle();
        }
        
        @Override
        public int getNumActive() throws UnsupportedOperationException {
            return this.pool.getNumActive();
        }
        
        @Override
        public void clear() throws Exception, UnsupportedOperationException {
            this.pool.clear();
        }
        
        @Override
        public void close() {
            try {
                this.pool.close();
            }
            catch (Exception ex) {}
        }
        
        @Override
        @Deprecated
        public void setFactory(final PoolableObjectFactory factory) throws IllegalStateException, UnsupportedOperationException {
            this.pool.setFactory(factory);
        }
        
        @Override
        public String toString() {
            return "ErodingObjectPool{factor=" + this.factor + ", pool=" + this.pool + '}';
        }
    }
    
    private static class ErodingKeyedObjectPool implements KeyedObjectPool
    {
        private final KeyedObjectPool keyedPool;
        private final ErodingFactor erodingFactor;
        
        public ErodingKeyedObjectPool(final KeyedObjectPool keyedPool, final float factor) {
            this(keyedPool, new ErodingFactor(factor));
        }
        
        protected ErodingKeyedObjectPool(final KeyedObjectPool keyedPool, final ErodingFactor erodingFactor) {
            if (keyedPool == null) {
                throw new IllegalArgumentException("keyedPool must not be null.");
            }
            this.keyedPool = keyedPool;
            this.erodingFactor = erodingFactor;
        }
        
        @Override
        public Object borrowObject(final Object key) throws Exception, NoSuchElementException, IllegalStateException {
            return this.keyedPool.borrowObject(key);
        }
        
        @Override
        public void returnObject(final Object key, final Object obj) throws Exception {
            boolean discard = false;
            final long now = System.currentTimeMillis();
            final ErodingFactor factor = this.getErodingFactor(key);
            synchronized (this.keyedPool) {
                if (factor.getNextShrink() < now) {
                    final int numIdle = this.numIdle(key);
                    if (numIdle > 0) {
                        discard = true;
                    }
                    factor.update(now, numIdle);
                }
            }
            try {
                if (discard) {
                    this.keyedPool.invalidateObject(key, obj);
                }
                else {
                    this.keyedPool.returnObject(key, obj);
                }
            }
            catch (Exception ex) {}
        }
        
        protected int numIdle(final Object key) {
            return this.getKeyedPool().getNumIdle();
        }
        
        protected ErodingFactor getErodingFactor(final Object key) {
            return this.erodingFactor;
        }
        
        @Override
        public void invalidateObject(final Object key, final Object obj) {
            try {
                this.keyedPool.invalidateObject(key, obj);
            }
            catch (Exception ex) {}
        }
        
        @Override
        public void addObject(final Object key) throws Exception, IllegalStateException, UnsupportedOperationException {
            this.keyedPool.addObject(key);
        }
        
        @Override
        public int getNumIdle() throws UnsupportedOperationException {
            return this.keyedPool.getNumIdle();
        }
        
        @Override
        public int getNumIdle(final Object key) throws UnsupportedOperationException {
            return this.keyedPool.getNumIdle(key);
        }
        
        @Override
        public int getNumActive() throws UnsupportedOperationException {
            return this.keyedPool.getNumActive();
        }
        
        @Override
        public int getNumActive(final Object key) throws UnsupportedOperationException {
            return this.keyedPool.getNumActive(key);
        }
        
        @Override
        public void clear() throws Exception, UnsupportedOperationException {
            this.keyedPool.clear();
        }
        
        @Override
        public void clear(final Object key) throws Exception, UnsupportedOperationException {
            this.keyedPool.clear(key);
        }
        
        @Override
        public void close() {
            try {
                this.keyedPool.close();
            }
            catch (Exception ex) {}
        }
        
        @Override
        @Deprecated
        public void setFactory(final KeyedPoolableObjectFactory factory) throws IllegalStateException, UnsupportedOperationException {
            this.keyedPool.setFactory(factory);
        }
        
        protected KeyedObjectPool getKeyedPool() {
            return this.keyedPool;
        }
        
        @Override
        public String toString() {
            return "ErodingKeyedObjectPool{erodingFactor=" + this.erodingFactor + ", keyedPool=" + this.keyedPool + '}';
        }
    }
    
    private static class ErodingPerKeyKeyedObjectPool extends ErodingKeyedObjectPool
    {
        private final float factor;
        private final Map factors;
        
        public ErodingPerKeyKeyedObjectPool(final KeyedObjectPool keyedPool, final float factor) {
            super(keyedPool, null);
            this.factors = Collections.synchronizedMap(new HashMap<Object, Object>());
            this.factor = factor;
        }
        
        @Override
        protected int numIdle(final Object key) {
            return this.getKeyedPool().getNumIdle(key);
        }
        
        @Override
        protected ErodingFactor getErodingFactor(final Object key) {
            ErodingFactor factor = this.factors.get(key);
            if (factor == null) {
                factor = new ErodingFactor(this.factor);
                this.factors.put(key, factor);
            }
            return factor;
        }
        
        @Override
        public String toString() {
            return "ErodingPerKeyKeyedObjectPool{factor=" + this.factor + ", keyedPool=" + this.getKeyedPool() + '}';
        }
    }
}
