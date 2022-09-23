// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.atomic;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;
import java.nio.ByteBuffer;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;

public class DistributedAtomicInteger implements DistributedAtomicNumber<Integer>
{
    private final DistributedAtomicValue value;
    
    public DistributedAtomicInteger(final CuratorFramework client, final String counterPath, final RetryPolicy retryPolicy) {
        this(client, counterPath, retryPolicy, null);
    }
    
    public DistributedAtomicInteger(final CuratorFramework client, final String counterPath, final RetryPolicy retryPolicy, final PromotedToLock promotedToLock) {
        this.value = new DistributedAtomicValue(client, counterPath, retryPolicy, promotedToLock);
    }
    
    @Override
    public AtomicValue<Integer> get() throws Exception {
        return new AtomicInteger((AtomicValue)this.value.get());
    }
    
    @Override
    public void forceSet(final Integer newValue) throws Exception {
        this.value.forceSet(this.valueToBytes(newValue));
    }
    
    @Override
    public AtomicValue<Integer> compareAndSet(final Integer expectedValue, final Integer newValue) throws Exception {
        return new AtomicInteger((AtomicValue)this.value.compareAndSet(this.valueToBytes(expectedValue), this.valueToBytes(newValue)));
    }
    
    @Override
    public AtomicValue<Integer> trySet(final Integer newValue) throws Exception {
        return new AtomicInteger((AtomicValue)this.value.trySet(this.valueToBytes(newValue)));
    }
    
    @Override
    public boolean initialize(final Integer initialize) throws Exception {
        return this.value.initialize(this.valueToBytes(initialize));
    }
    
    @Override
    public AtomicValue<Integer> increment() throws Exception {
        return this.worker(1);
    }
    
    @Override
    public AtomicValue<Integer> decrement() throws Exception {
        return this.worker(-1);
    }
    
    @Override
    public AtomicValue<Integer> add(final Integer delta) throws Exception {
        return this.worker(delta);
    }
    
    @Override
    public AtomicValue<Integer> subtract(final Integer delta) throws Exception {
        return this.worker(-1 * delta);
    }
    
    @VisibleForTesting
    byte[] valueToBytes(final Integer newValue) {
        Preconditions.checkNotNull(newValue, (Object)"newValue cannot be null");
        final byte[] newData = new byte[4];
        final ByteBuffer wrapper = ByteBuffer.wrap(newData);
        wrapper.putInt(newValue);
        return newData;
    }
    
    @VisibleForTesting
    int bytesToValue(final byte[] data) {
        if (data == null || data.length == 0) {
            return 0;
        }
        final ByteBuffer wrapper = ByteBuffer.wrap(data);
        try {
            return wrapper.getInt();
        }
        catch (BufferUnderflowException e) {
            throw this.value.createCorruptionException(data);
        }
        catch (BufferOverflowException e2) {
            throw this.value.createCorruptionException(data);
        }
    }
    
    private AtomicValue<Integer> worker(final Integer addAmount) throws Exception {
        Preconditions.checkNotNull(addAmount, (Object)"addAmount cannot be null");
        final MakeValue makeValue = new MakeValue() {
            @Override
            public byte[] makeFrom(final byte[] previous) {
                final int previousValue = (previous != null) ? DistributedAtomicInteger.this.bytesToValue(previous) : 0;
                final int newValue = previousValue + addAmount;
                return DistributedAtomicInteger.this.valueToBytes(newValue);
            }
        };
        final AtomicValue<byte[]> result = this.value.trySet(makeValue);
        return new AtomicInteger((AtomicValue)result);
    }
    
    private class AtomicInteger implements AtomicValue<Integer>
    {
        private AtomicValue<byte[]> bytes;
        
        private AtomicInteger(final AtomicValue<byte[]> bytes) {
            this.bytes = bytes;
        }
        
        @Override
        public boolean succeeded() {
            return this.bytes.succeeded();
        }
        
        @Override
        public Integer preValue() {
            return DistributedAtomicInteger.this.bytesToValue(this.bytes.preValue());
        }
        
        @Override
        public Integer postValue() {
            return DistributedAtomicInteger.this.bytesToValue(this.bytes.postValue());
        }
        
        @Override
        public AtomicStats getStats() {
            return this.bytes.getStats();
        }
    }
}
