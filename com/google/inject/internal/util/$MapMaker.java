// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

import java.lang.reflect.Field;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.TimerTask;
import java.lang.ref.WeakReference;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public final class $MapMaker
{
    private Strength keyStrength;
    private Strength valueStrength;
    private long expirationNanos;
    private boolean useCustomMap;
    private final $CustomConcurrentHashMap.Builder builder;
    private static final ValueReference<Object, Object> COMPUTING;
    
    public $MapMaker() {
        this.keyStrength = Strength.STRONG;
        this.valueStrength = Strength.STRONG;
        this.expirationNanos = 0L;
        this.builder = new $CustomConcurrentHashMap.Builder();
    }
    
    public $MapMaker initialCapacity(final int initialCapacity) {
        this.builder.initialCapacity(initialCapacity);
        return this;
    }
    
    public $MapMaker loadFactor(final float loadFactor) {
        this.builder.loadFactor(loadFactor);
        return this;
    }
    
    public $MapMaker concurrencyLevel(final int concurrencyLevel) {
        this.builder.concurrencyLevel(concurrencyLevel);
        return this;
    }
    
    public $MapMaker weakKeys() {
        return this.setKeyStrength(Strength.WEAK);
    }
    
    public $MapMaker softKeys() {
        return this.setKeyStrength(Strength.SOFT);
    }
    
    private $MapMaker setKeyStrength(final Strength strength) {
        if (this.keyStrength != Strength.STRONG) {
            throw new IllegalStateException("Key strength was already set to " + this.keyStrength + ".");
        }
        this.keyStrength = strength;
        this.useCustomMap = true;
        return this;
    }
    
    public $MapMaker weakValues() {
        return this.setValueStrength(Strength.WEAK);
    }
    
    public $MapMaker softValues() {
        return this.setValueStrength(Strength.SOFT);
    }
    
    private $MapMaker setValueStrength(final Strength strength) {
        if (this.valueStrength != Strength.STRONG) {
            throw new IllegalStateException("Value strength was already set to " + this.valueStrength + ".");
        }
        this.valueStrength = strength;
        this.useCustomMap = true;
        return this;
    }
    
    public $MapMaker expiration(final long duration, final TimeUnit unit) {
        if (this.expirationNanos != 0L) {
            throw new IllegalStateException("expiration time of " + this.expirationNanos + " ns was already set");
        }
        if (duration <= 0L) {
            throw new IllegalArgumentException("invalid duration: " + duration);
        }
        this.expirationNanos = unit.toNanos(duration);
        this.useCustomMap = true;
        return this;
    }
    
    public <K, V> ConcurrentMap<K, V> makeMap() {
        return (ConcurrentMap<K, V>)(this.useCustomMap ? new StrategyImpl(this).map : new ConcurrentHashMap<K, V>(this.builder.initialCapacity, this.builder.loadFactor, this.builder.concurrencyLevel));
    }
    
    public <K, V> ConcurrentMap<K, V> makeComputingMap(final $Function<? super K, ? extends V> computer) {
        return (ConcurrentMap<K, V>)new StrategyImpl(this, ($Function<? super K, ? extends V>)computer).map;
    }
    
    private static <K, V> ValueReference<K, V> computing() {
        return (ValueReference<K, V>)$MapMaker.COMPUTING;
    }
    
    static {
        COMPUTING = new ValueReference<Object, Object>() {
            public Object get() {
                return null;
            }
            
            public ValueReference<Object, Object> copyFor(final ReferenceEntry<Object, Object> entry) {
                throw new AssertionError();
            }
            
            public Object waitForValue() {
                throw new AssertionError();
            }
        };
    }
    
    private enum Strength
    {
        WEAK {
            @Override
            boolean equal(final Object a, final Object b) {
                return a == b;
            }
            
            @Override
            int hash(final Object o) {
                return System.identityHashCode(o);
            }
            
            @Override
             <K, V> ValueReference<K, V> referenceValue(final ReferenceEntry<K, V> entry, final V value) {
                return new WeakValueReference<K, V>(value, entry);
            }
            
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final $CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals, final K key, final int hash, final ReferenceEntry<K, V> next) {
                return (next == null) ? new WeakEntry<K, V>(internals, key, hash) : new LinkedWeakEntry<K, V>(internals, key, hash, next);
            }
            
            @Override
             <K, V> ReferenceEntry<K, V> copyEntry(final K key, final ReferenceEntry<K, V> original, final ReferenceEntry<K, V> newNext) {
                final WeakEntry<K, V> from = (WeakEntry<K, V>)(WeakEntry)original;
                return (newNext == null) ? new WeakEntry<K, V>(from.internals, key, from.hash) : new LinkedWeakEntry<K, V>(from.internals, key, from.hash, newNext);
            }
        }, 
        SOFT {
            @Override
            boolean equal(final Object a, final Object b) {
                return a == b;
            }
            
            @Override
            int hash(final Object o) {
                return System.identityHashCode(o);
            }
            
            @Override
             <K, V> ValueReference<K, V> referenceValue(final ReferenceEntry<K, V> entry, final V value) {
                return new SoftValueReference<K, V>(value, entry);
            }
            
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final $CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals, final K key, final int hash, final ReferenceEntry<K, V> next) {
                return (next == null) ? new SoftEntry<K, V>(internals, key, hash) : new LinkedSoftEntry<K, V>(internals, key, hash, next);
            }
            
            @Override
             <K, V> ReferenceEntry<K, V> copyEntry(final K key, final ReferenceEntry<K, V> original, final ReferenceEntry<K, V> newNext) {
                final SoftEntry<K, V> from = (SoftEntry<K, V>)(SoftEntry)original;
                return (newNext == null) ? new SoftEntry<K, V>(from.internals, key, from.hash) : new LinkedSoftEntry<K, V>(from.internals, key, from.hash, newNext);
            }
        }, 
        STRONG {
            @Override
            boolean equal(final Object a, final Object b) {
                return a.equals(b);
            }
            
            @Override
            int hash(final Object o) {
                return o.hashCode();
            }
            
            @Override
             <K, V> ValueReference<K, V> referenceValue(final ReferenceEntry<K, V> entry, final V value) {
                return new StrongValueReference<K, V>(value);
            }
            
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final $CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals, final K key, final int hash, final ReferenceEntry<K, V> next) {
                return (next == null) ? new StrongEntry<K, V>(internals, key, hash) : new LinkedStrongEntry<K, V>(internals, key, hash, next);
            }
            
            @Override
             <K, V> ReferenceEntry<K, V> copyEntry(final K key, final ReferenceEntry<K, V> original, final ReferenceEntry<K, V> newNext) {
                final StrongEntry<K, V> from = (StrongEntry<K, V>)(StrongEntry)original;
                return (newNext == null) ? new StrongEntry<K, V>(from.internals, key, from.hash) : new LinkedStrongEntry<K, V>(from.internals, key, from.hash, newNext);
            }
        };
        
        abstract boolean equal(final Object p0, final Object p1);
        
        abstract int hash(final Object p0);
        
        abstract <K, V> ValueReference<K, V> referenceValue(final ReferenceEntry<K, V> p0, final V p1);
        
        abstract <K, V> ReferenceEntry<K, V> newEntry(final $CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> p0, final K p1, final int p2, final ReferenceEntry<K, V> p3);
        
        abstract <K, V> ReferenceEntry<K, V> copyEntry(final K p0, final ReferenceEntry<K, V> p1, final ReferenceEntry<K, V> p2);
    }
    
    private static class StrategyImpl<K, V> implements Serializable, $CustomConcurrentHashMap.ComputingStrategy<K, V, ReferenceEntry<K, V>>
    {
        final Strength keyStrength;
        final Strength valueStrength;
        final ConcurrentMap<K, V> map;
        final long expirationNanos;
        $CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals;
        private static final long serialVersionUID = 0L;
        
        StrategyImpl(final $MapMaker maker) {
            this.keyStrength = maker.keyStrength;
            this.valueStrength = maker.valueStrength;
            this.expirationNanos = maker.expirationNanos;
            this.map = maker.builder.buildMap(($CustomConcurrentHashMap.Strategy<K, V, Object>)this);
        }
        
        StrategyImpl(final $MapMaker maker, final $Function<? super K, ? extends V> computer) {
            this.keyStrength = maker.keyStrength;
            this.valueStrength = maker.valueStrength;
            this.expirationNanos = maker.expirationNanos;
            this.map = maker.builder.buildComputingMap(($CustomConcurrentHashMap.ComputingStrategy<K, V, Object>)this, computer);
        }
        
        public void setValue(final ReferenceEntry<K, V> entry, final V value) {
            this.setValueReference(entry, this.valueStrength.referenceValue(entry, value));
            if (this.expirationNanos > 0L) {
                this.scheduleRemoval(entry.getKey(), value);
            }
        }
        
        void scheduleRemoval(final K key, final V value) {
            final WeakReference<K> keyReference = new WeakReference<K>(key);
            final WeakReference<V> valueReference = new WeakReference<V>(value);
            $ExpirationTimer.instance.schedule(new TimerTask() {
                @Override
                public void run() {
                    final K key = (K)keyReference.get();
                    if (key != null) {
                        StrategyImpl.this.map.remove(key, valueReference.get());
                    }
                }
            }, TimeUnit.NANOSECONDS.toMillis(this.expirationNanos));
        }
        
        public boolean equalKeys(final K a, final Object b) {
            return this.keyStrength.equal(a, b);
        }
        
        public boolean equalValues(final V a, final Object b) {
            return this.valueStrength.equal(a, b);
        }
        
        public int hashKey(final Object key) {
            return this.keyStrength.hash(key);
        }
        
        public K getKey(final ReferenceEntry<K, V> entry) {
            return entry.getKey();
        }
        
        public int getHash(final ReferenceEntry entry) {
            return entry.getHash();
        }
        
        public ReferenceEntry<K, V> newEntry(final K key, final int hash, final ReferenceEntry<K, V> next) {
            return this.keyStrength.newEntry(this.internals, key, hash, next);
        }
        
        public ReferenceEntry<K, V> copyEntry(final K key, final ReferenceEntry<K, V> original, final ReferenceEntry<K, V> newNext) {
            final ValueReference<K, V> valueReference = original.getValueReference();
            if (valueReference == $MapMaker.COMPUTING) {
                final ReferenceEntry<K, V> newEntry = this.newEntry(key, original.getHash(), newNext);
                newEntry.setValueReference(new FutureValueReference(original, newEntry));
                return newEntry;
            }
            final ReferenceEntry<K, V> newEntry = this.newEntry(key, original.getHash(), newNext);
            newEntry.setValueReference(valueReference.copyFor(newEntry));
            return newEntry;
        }
        
        public V waitForValue(final ReferenceEntry<K, V> entry) throws InterruptedException {
            ValueReference<K, V> valueReference = entry.getValueReference();
            if (valueReference == $MapMaker.COMPUTING) {
                synchronized (entry) {
                    while ((valueReference = entry.getValueReference()) == $MapMaker.COMPUTING) {
                        entry.wait();
                    }
                }
            }
            return valueReference.waitForValue();
        }
        
        public V getValue(final ReferenceEntry<K, V> entry) {
            final ValueReference<K, V> valueReference = entry.getValueReference();
            return valueReference.get();
        }
        
        public V compute(final K key, final ReferenceEntry<K, V> entry, final $Function<? super K, ? extends V> computer) {
            V value;
            try {
                value = (V)computer.apply((Object)key);
            }
            catch (Throwable t) {
                this.setValueReference(entry, new ComputationExceptionReference<K, V>(t));
                throw new $ComputationException(t);
            }
            if (value == null) {
                final String message = computer + " returned null for key " + key + ".";
                this.setValueReference(entry, new NullOutputExceptionReference<K, V>(message));
                throw new $NullOutputException(message);
            }
            this.setValue(entry, value);
            return value;
        }
        
        void setValueReference(final ReferenceEntry<K, V> entry, final ValueReference<K, V> valueReference) {
            final boolean notifyOthers = entry.getValueReference() == $MapMaker.COMPUTING;
            entry.setValueReference(valueReference);
            if (notifyOthers) {
                synchronized (entry) {
                    entry.notifyAll();
                }
            }
        }
        
        public ReferenceEntry<K, V> getNext(final ReferenceEntry<K, V> entry) {
            return entry.getNext();
        }
        
        public void setInternals(final $CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals) {
            this.internals = internals;
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            out.writeObject(this.keyStrength);
            out.writeObject(this.valueStrength);
            out.writeLong(this.expirationNanos);
            out.writeObject(this.internals);
            out.writeObject(this.map);
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                Fields.keyStrength.set(this, in.readObject());
                Fields.valueStrength.set(this, in.readObject());
                Fields.expirationNanos.set(this, in.readLong());
                Fields.internals.set(this, in.readObject());
                Fields.map.set(this, in.readObject());
            }
            catch (IllegalAccessException e) {
                throw new AssertionError((Object)e);
            }
        }
        
        private class FutureValueReference implements ValueReference<K, V>
        {
            final ReferenceEntry<K, V> original;
            final ReferenceEntry<K, V> newEntry;
            
            FutureValueReference(final ReferenceEntry<K, V> original, final ReferenceEntry<K, V> newEntry) {
                this.original = original;
                this.newEntry = newEntry;
            }
            
            public V get() {
                boolean success = false;
                try {
                    final V value = this.original.getValueReference().get();
                    success = true;
                    return value;
                }
                finally {
                    if (!success) {
                        this.removeEntry();
                    }
                }
            }
            
            public ValueReference<K, V> copyFor(final ReferenceEntry<K, V> entry) {
                return new FutureValueReference(this.original, entry);
            }
            
            public V waitForValue() throws InterruptedException {
                boolean success = false;
                try {
                    final V value = StrategyImpl.this.waitForValue(this.original);
                    success = true;
                    return value;
                }
                finally {
                    if (!success) {
                        this.removeEntry();
                    }
                }
            }
            
            void removeEntry() {
                StrategyImpl.this.internals.removeEntry(this.newEntry);
            }
        }
        
        private static class Fields
        {
            static final Field keyStrength;
            static final Field valueStrength;
            static final Field expirationNanos;
            static final Field internals;
            static final Field map;
            
            static Field findField(final String name) {
                try {
                    final Field f = StrategyImpl.class.getDeclaredField(name);
                    f.setAccessible(true);
                    return f;
                }
                catch (NoSuchFieldException e) {
                    throw new AssertionError((Object)e);
                }
            }
            
            static {
                keyStrength = findField("keyStrength");
                valueStrength = findField("valueStrength");
                expirationNanos = findField("expirationNanos");
                internals = findField("internals");
                map = findField("map");
            }
        }
    }
    
    private static class NullOutputExceptionReference<K, V> implements ValueReference<K, V>
    {
        final String message;
        
        NullOutputExceptionReference(final String message) {
            this.message = message;
        }
        
        public V get() {
            return null;
        }
        
        public ValueReference<K, V> copyFor(final ReferenceEntry<K, V> entry) {
            return this;
        }
        
        public V waitForValue() {
            throw new $NullOutputException(this.message);
        }
    }
    
    private static class ComputationExceptionReference<K, V> implements ValueReference<K, V>
    {
        final Throwable t;
        
        ComputationExceptionReference(final Throwable t) {
            this.t = t;
        }
        
        public V get() {
            return null;
        }
        
        public ValueReference<K, V> copyFor(final ReferenceEntry<K, V> entry) {
            return this;
        }
        
        public V waitForValue() {
            throw new $AsynchronousComputationException(this.t);
        }
    }
    
    private static class QueueHolder
    {
        static final $FinalizableReferenceQueue queue;
        
        static {
            queue = new $FinalizableReferenceQueue();
        }
    }
    
    private static class StrongEntry<K, V> implements ReferenceEntry<K, V>
    {
        final K key;
        final $CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals;
        final int hash;
        volatile ValueReference<K, V> valueReference;
        
        StrongEntry(final $CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals, final K key, final int hash) {
            this.valueReference = (ValueReference<K, V>)computing();
            this.internals = internals;
            this.key = key;
            this.hash = hash;
        }
        
        public K getKey() {
            return this.key;
        }
        
        public ValueReference<K, V> getValueReference() {
            return this.valueReference;
        }
        
        public void setValueReference(final ValueReference<K, V> valueReference) {
            this.valueReference = valueReference;
        }
        
        public void valueReclaimed() {
            this.internals.removeEntry(this, null);
        }
        
        public ReferenceEntry<K, V> getNext() {
            return null;
        }
        
        public int getHash() {
            return this.hash;
        }
    }
    
    private static class LinkedStrongEntry<K, V> extends StrongEntry<K, V>
    {
        final ReferenceEntry<K, V> next;
        
        LinkedStrongEntry(final $CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals, final K key, final int hash, final ReferenceEntry<K, V> next) {
            super(internals, key, hash);
            this.next = next;
        }
        
        @Override
        public ReferenceEntry<K, V> getNext() {
            return this.next;
        }
    }
    
    private static class SoftEntry<K, V> extends $FinalizableSoftReference<K> implements ReferenceEntry<K, V>
    {
        final $CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals;
        final int hash;
        volatile ValueReference<K, V> valueReference;
        
        SoftEntry(final $CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals, final K key, final int hash) {
            super(key, QueueHolder.queue);
            this.valueReference = (ValueReference<K, V>)computing();
            this.internals = internals;
            this.hash = hash;
        }
        
        public K getKey() {
            return this.get();
        }
        
        public void finalizeReferent() {
            this.internals.removeEntry(this);
        }
        
        public ValueReference<K, V> getValueReference() {
            return this.valueReference;
        }
        
        public void setValueReference(final ValueReference<K, V> valueReference) {
            this.valueReference = valueReference;
        }
        
        public void valueReclaimed() {
            this.internals.removeEntry(this, null);
        }
        
        public ReferenceEntry<K, V> getNext() {
            return null;
        }
        
        public int getHash() {
            return this.hash;
        }
    }
    
    private static class LinkedSoftEntry<K, V> extends SoftEntry<K, V>
    {
        final ReferenceEntry<K, V> next;
        
        LinkedSoftEntry(final $CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals, final K key, final int hash, final ReferenceEntry<K, V> next) {
            super(internals, key, hash);
            this.next = next;
        }
        
        @Override
        public ReferenceEntry<K, V> getNext() {
            return this.next;
        }
    }
    
    private static class WeakEntry<K, V> extends $FinalizableWeakReference<K> implements ReferenceEntry<K, V>
    {
        final $CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals;
        final int hash;
        volatile ValueReference<K, V> valueReference;
        
        WeakEntry(final $CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals, final K key, final int hash) {
            super(key, QueueHolder.queue);
            this.valueReference = (ValueReference<K, V>)computing();
            this.internals = internals;
            this.hash = hash;
        }
        
        public K getKey() {
            return this.get();
        }
        
        public void finalizeReferent() {
            this.internals.removeEntry(this);
        }
        
        public ValueReference<K, V> getValueReference() {
            return this.valueReference;
        }
        
        public void setValueReference(final ValueReference<K, V> valueReference) {
            this.valueReference = valueReference;
        }
        
        public void valueReclaimed() {
            this.internals.removeEntry(this, null);
        }
        
        public ReferenceEntry<K, V> getNext() {
            return null;
        }
        
        public int getHash() {
            return this.hash;
        }
    }
    
    private static class LinkedWeakEntry<K, V> extends WeakEntry<K, V>
    {
        final ReferenceEntry<K, V> next;
        
        LinkedWeakEntry(final $CustomConcurrentHashMap.Internals<K, V, ReferenceEntry<K, V>> internals, final K key, final int hash, final ReferenceEntry<K, V> next) {
            super(internals, key, hash);
            this.next = next;
        }
        
        @Override
        public ReferenceEntry<K, V> getNext() {
            return this.next;
        }
    }
    
    private static class WeakValueReference<K, V> extends $FinalizableWeakReference<V> implements ValueReference<K, V>
    {
        final ReferenceEntry<K, V> entry;
        
        WeakValueReference(final V referent, final ReferenceEntry<K, V> entry) {
            super(referent, QueueHolder.queue);
            this.entry = entry;
        }
        
        public void finalizeReferent() {
            this.entry.valueReclaimed();
        }
        
        public ValueReference<K, V> copyFor(final ReferenceEntry<K, V> entry) {
            return new WeakValueReference(this.get(), (ReferenceEntry<Object, Object>)entry);
        }
        
        public V waitForValue() throws InterruptedException {
            return this.get();
        }
    }
    
    private static class SoftValueReference<K, V> extends $FinalizableSoftReference<V> implements ValueReference<K, V>
    {
        final ReferenceEntry<K, V> entry;
        
        SoftValueReference(final V referent, final ReferenceEntry<K, V> entry) {
            super(referent, QueueHolder.queue);
            this.entry = entry;
        }
        
        public void finalizeReferent() {
            this.entry.valueReclaimed();
        }
        
        public ValueReference<K, V> copyFor(final ReferenceEntry<K, V> entry) {
            return new SoftValueReference(this.get(), (ReferenceEntry<Object, Object>)entry);
        }
        
        public V waitForValue() {
            return this.get();
        }
    }
    
    private static class StrongValueReference<K, V> implements ValueReference<K, V>
    {
        final V referent;
        
        StrongValueReference(final V referent) {
            this.referent = referent;
        }
        
        public V get() {
            return this.referent;
        }
        
        public ValueReference<K, V> copyFor(final ReferenceEntry<K, V> entry) {
            return this;
        }
        
        public V waitForValue() {
            return this.get();
        }
    }
    
    private interface ReferenceEntry<K, V>
    {
        ValueReference<K, V> getValueReference();
        
        void setValueReference(final ValueReference<K, V> p0);
        
        void valueReclaimed();
        
        ReferenceEntry<K, V> getNext();
        
        int getHash();
        
        K getKey();
    }
    
    private interface ValueReference<K, V>
    {
        V get();
        
        ValueReference<K, V> copyFor(final ReferenceEntry<K, V> p0);
        
        V waitForValue() throws InterruptedException;
    }
}
