// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.shaded.com.google.common.util.concurrent;

import org.apache.curator.shaded.com.google.common.base.Objects;
import org.apache.curator.shaded.com.google.common.collect.MapMaker;
import java.util.concurrent.ConcurrentMap;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.curator.shaded.com.google.common.math.IntMath;
import java.math.RoundingMode;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import java.util.List;
import java.util.Collections;
import java.util.Arrays;
import org.apache.curator.shaded.com.google.common.collect.Iterables;
import java.util.concurrent.locks.ReadWriteLock;
import org.apache.curator.shaded.com.google.common.base.Supplier;
import org.apache.curator.shaded.com.google.common.annotations.Beta;

@Beta
public abstract class Striped<L>
{
    private static final Supplier<ReadWriteLock> READ_WRITE_LOCK_SUPPLIER;
    private static final int ALL_SET = -1;
    
    private Striped() {
    }
    
    public abstract L get(final Object p0);
    
    public abstract L getAt(final int p0);
    
    abstract int indexFor(final Object p0);
    
    public abstract int size();
    
    public Iterable<L> bulkGet(final Iterable<?> keys) {
        final Object[] array = Iterables.toArray(keys, Object.class);
        final int[] stripes = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            stripes[i] = this.indexFor(array[i]);
        }
        Arrays.sort(stripes);
        for (int i = 0; i < array.length; ++i) {
            array[i] = this.getAt(stripes[i]);
        }
        final List<L> asList = Arrays.asList((L[])array);
        return (Iterable<L>)Collections.unmodifiableList((List<?>)asList);
    }
    
    public static Striped<Lock> lock(final int stripes) {
        return new CompactStriped<Lock>(stripes, (Supplier)new Supplier<Lock>() {
            @Override
            public Lock get() {
                return new PaddedLock();
            }
        });
    }
    
    public static Striped<Lock> lazyWeakLock(final int stripes) {
        return new LazyStriped<Lock>(stripes, new Supplier<Lock>() {
            @Override
            public Lock get() {
                return new ReentrantLock(false);
            }
        });
    }
    
    public static Striped<Semaphore> semaphore(final int stripes, final int permits) {
        return new CompactStriped<Semaphore>(stripes, (Supplier)new Supplier<Semaphore>() {
            @Override
            public Semaphore get() {
                return new PaddedSemaphore(permits);
            }
        });
    }
    
    public static Striped<Semaphore> lazyWeakSemaphore(final int stripes, final int permits) {
        return new LazyStriped<Semaphore>(stripes, new Supplier<Semaphore>() {
            @Override
            public Semaphore get() {
                return new Semaphore(permits, false);
            }
        });
    }
    
    public static Striped<ReadWriteLock> readWriteLock(final int stripes) {
        return new CompactStriped<ReadWriteLock>(stripes, (Supplier)Striped.READ_WRITE_LOCK_SUPPLIER);
    }
    
    public static Striped<ReadWriteLock> lazyWeakReadWriteLock(final int stripes) {
        return new LazyStriped<ReadWriteLock>(stripes, Striped.READ_WRITE_LOCK_SUPPLIER);
    }
    
    private static int ceilToPowerOfTwo(final int x) {
        return 1 << IntMath.log2(x, RoundingMode.CEILING);
    }
    
    private static int smear(int hashCode) {
        hashCode ^= (hashCode >>> 20 ^ hashCode >>> 12);
        return hashCode ^ hashCode >>> 7 ^ hashCode >>> 4;
    }
    
    static {
        READ_WRITE_LOCK_SUPPLIER = new Supplier<ReadWriteLock>() {
            @Override
            public ReadWriteLock get() {
                return new ReentrantReadWriteLock();
            }
        };
    }
    
    private abstract static class PowerOfTwoStriped<L> extends Striped<L>
    {
        final int mask;
        
        PowerOfTwoStriped(final int stripes) {
            super(null);
            Preconditions.checkArgument(stripes > 0, (Object)"Stripes must be positive");
            this.mask = ((stripes > 1073741824) ? -1 : (ceilToPowerOfTwo(stripes) - 1));
        }
        
        @Override
        final int indexFor(final Object key) {
            final int hash = smear(key.hashCode());
            return hash & this.mask;
        }
        
        @Override
        public final L get(final Object key) {
            return this.getAt(this.indexFor(key));
        }
    }
    
    private static class CompactStriped<L> extends PowerOfTwoStriped<L>
    {
        private final Object[] array;
        
        private CompactStriped(final int stripes, final Supplier<L> supplier) {
            super(stripes);
            Preconditions.checkArgument(stripes <= 1073741824, (Object)"Stripes must be <= 2^30)");
            this.array = new Object[this.mask + 1];
            for (int i = 0; i < this.array.length; ++i) {
                this.array[i] = supplier.get();
            }
        }
        
        @Override
        public L getAt(final int index) {
            return (L)this.array[index];
        }
        
        @Override
        public int size() {
            return this.array.length;
        }
    }
    
    private static class LazyStriped<L> extends PowerOfTwoStriped<L>
    {
        final ConcurrentMap<Integer, L> locks;
        final Supplier<L> supplier;
        final int size;
        
        LazyStriped(final int stripes, final Supplier<L> supplier) {
            super(stripes);
            this.size = ((this.mask == -1) ? Integer.MAX_VALUE : (this.mask + 1));
            this.supplier = supplier;
            this.locks = new MapMaker().weakValues().makeMap();
        }
        
        @Override
        public L getAt(final int index) {
            if (this.size != Integer.MAX_VALUE) {
                Preconditions.checkElementIndex(index, this.size());
            }
            L existing = this.locks.get(index);
            if (existing != null) {
                return existing;
            }
            final L created = this.supplier.get();
            existing = this.locks.putIfAbsent(index, created);
            return Objects.firstNonNull(existing, created);
        }
        
        @Override
        public int size() {
            return this.size;
        }
    }
    
    private static class PaddedLock extends ReentrantLock
    {
        long q1;
        long q2;
        long q3;
        
        PaddedLock() {
            super(false);
        }
    }
    
    private static class PaddedSemaphore extends Semaphore
    {
        long q1;
        long q2;
        long q3;
        
        PaddedSemaphore(final int permits) {
            super(permits, false);
        }
    }
}
