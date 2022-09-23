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

public class DistributedAtomicLong implements DistributedAtomicNumber<Long>
{
    private final DistributedAtomicValue value;
    
    public DistributedAtomicLong(final CuratorFramework client, final String counterPath, final RetryPolicy retryPolicy) {
        this(client, counterPath, retryPolicy, null);
    }
    
    public DistributedAtomicLong(final CuratorFramework client, final String counterPath, final RetryPolicy retryPolicy, final PromotedToLock promotedToLock) {
        this.value = new DistributedAtomicValue(client, counterPath, retryPolicy, promotedToLock);
    }
    
    @Override
    public AtomicValue<Long> get() throws Exception {
        return new AtomicLong((AtomicValue)this.value.get());
    }
    
    @Override
    public void forceSet(final Long newValue) throws Exception {
        this.value.forceSet(this.valueToBytes(newValue));
    }
    
    @Override
    public AtomicValue<Long> compareAndSet(final Long expectedValue, final Long newValue) throws Exception {
        return new AtomicLong((AtomicValue)this.value.compareAndSet(this.valueToBytes(expectedValue), this.valueToBytes(newValue)));
    }
    
    @Override
    public AtomicValue<Long> trySet(final Long newValue) throws Exception {
        return new AtomicLong((AtomicValue)this.value.trySet(this.valueToBytes(newValue)));
    }
    
    @Override
    public boolean initialize(final Long initialize) throws Exception {
        return this.value.initialize(this.valueToBytes(initialize));
    }
    
    @Override
    public AtomicValue<Long> increment() throws Exception {
        return this.worker(1L);
    }
    
    @Override
    public AtomicValue<Long> decrement() throws Exception {
        return this.worker(-1L);
    }
    
    @Override
    public AtomicValue<Long> add(final Long delta) throws Exception {
        return this.worker(delta);
    }
    
    @Override
    public AtomicValue<Long> subtract(final Long delta) throws Exception {
        return this.worker(-1L * delta);
    }
    
    @VisibleForTesting
    byte[] valueToBytes(final Long newValue) {
        Preconditions.checkNotNull(newValue, (Object)"newValue cannot be null");
        final byte[] newData = new byte[8];
        final ByteBuffer wrapper = ByteBuffer.wrap(newData);
        wrapper.putLong(newValue);
        return newData;
    }
    
    @VisibleForTesting
    long bytesToValue(final byte[] data) {
        if (data == null || data.length == 0) {
            return 0L;
        }
        final ByteBuffer wrapper = ByteBuffer.wrap(data);
        try {
            return wrapper.getLong();
        }
        catch (BufferUnderflowException e) {
            throw this.value.createCorruptionException(data);
        }
        catch (BufferOverflowException e2) {
            throw this.value.createCorruptionException(data);
        }
    }
    
    private AtomicValue<Long> worker(final Long addAmount) throws Exception {
        Preconditions.checkNotNull(addAmount, (Object)"addAmount cannot be null");
        final MakeValue makeValue = new MakeValue() {
            @Override
            public byte[] makeFrom(final byte[] previous) {
                final long previousValue = (previous != null) ? DistributedAtomicLong.this.bytesToValue(previous) : 0L;
                final long newValue = previousValue + addAmount;
                return DistributedAtomicLong.this.valueToBytes(newValue);
            }
        };
        final AtomicValue<byte[]> result = this.value.trySet(makeValue);
        return new AtomicLong((AtomicValue)result);
    }
    
    private class AtomicLong implements AtomicValue<Long>
    {
        private AtomicValue<byte[]> bytes;
        
        private AtomicLong(final AtomicValue<byte[]> bytes) {
            this.bytes = bytes;
        }
        
        @Override
        public boolean succeeded() {
            return this.bytes.succeeded();
        }
        
        @Override
        public Long preValue() {
            return DistributedAtomicLong.this.bytesToValue(this.bytes.preValue());
        }
        
        @Override
        public Long postValue() {
            return DistributedAtomicLong.this.bytesToValue(this.bytes.postValue());
        }
        
        @Override
        public AtomicStats getStats() {
            return this.bytes.getStats();
        }
    }
}
