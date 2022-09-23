// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.simple;

import java.io.ObjectStreamException;
import org.datanucleus.state.FetchPlanState;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.types.SCO;

public class BitSet extends java.util.BitSet implements SCO
{
    protected transient ObjectProvider ownerOP;
    protected transient AbstractMemberMetaData ownerMmd;
    
    public BitSet(final ObjectProvider op, final AbstractMemberMetaData mmd) {
        this.ownerOP = op;
        this.ownerMmd = mmd;
    }
    
    @Override
    public void initialise() {
    }
    
    @Override
    public void initialise(final Object o, final boolean forInsert, final boolean forUpdate) {
        for (int i = 0; i < this.length(); ++i) {
            super.clear(i);
        }
        super.or((java.util.BitSet)o);
    }
    
    @Override
    public Object getValue() {
        final java.util.BitSet bits = new java.util.BitSet();
        bits.or(this);
        return bits;
    }
    
    @Override
    public void unsetOwner() {
        this.ownerOP = null;
        this.ownerMmd = null;
    }
    
    @Override
    public Object getOwner() {
        return (this.ownerOP != null) ? this.ownerOP.getObject() : null;
    }
    
    @Override
    public String getFieldName() {
        return this.ownerMmd.getName();
    }
    
    public void makeDirty() {
        if (this.ownerOP != null) {
            this.ownerOP.makeDirty(this.ownerMmd.getAbsoluteFieldNumber());
            if (!this.ownerOP.getExecutionContext().getTransaction().isActive()) {
                this.ownerOP.getExecutionContext().processNontransactionalUpdate();
            }
        }
    }
    
    @Override
    public Object detachCopy(final FetchPlanState state) {
        final java.util.BitSet detached = new java.util.BitSet();
        detached.or(this);
        return detached;
    }
    
    @Override
    public void attachCopy(final Object value) {
        this.initialise(value, false, true);
        this.makeDirty();
    }
    
    @Override
    public Object clone() {
        final Object obj = super.clone();
        ((BitSet)obj).unsetOwner();
        return obj;
    }
    
    protected Object writeReplace() throws ObjectStreamException {
        final java.util.BitSet copy = new java.util.BitSet();
        copy.and(this);
        return copy;
    }
    
    @Override
    public void and(final java.util.BitSet set) {
        super.and(set);
        this.makeDirty();
    }
    
    @Override
    public void andNot(final java.util.BitSet set) {
        super.andNot(set);
        this.makeDirty();
    }
    
    @Override
    public void clear(final int bitIndex) {
        super.clear(bitIndex);
        this.makeDirty();
    }
    
    @Override
    public void or(final java.util.BitSet set) {
        super.or(set);
        this.makeDirty();
    }
    
    @Override
    public void set(final int bitIndex) {
        super.set(bitIndex);
        this.makeDirty();
    }
    
    @Override
    public void xor(final java.util.BitSet set) {
        super.xor(set);
        this.makeDirty();
    }
    
    @Override
    public void clear() {
        super.clear();
        this.makeDirty();
    }
    
    @Override
    public void clear(final int fromIndex, final int toIndex) {
        super.clear(fromIndex, toIndex);
        this.makeDirty();
    }
    
    @Override
    public void flip(final int fromIndex, final int toIndex) {
        super.flip(fromIndex, toIndex);
        this.makeDirty();
    }
    
    @Override
    public void flip(final int bitIndex) {
        super.flip(bitIndex);
        this.makeDirty();
    }
    
    @Override
    public void set(final int bitIndex, final boolean value) {
        super.set(bitIndex, value);
        this.makeDirty();
    }
    
    @Override
    public void set(final int fromIndex, final int toIndex, final boolean value) {
        super.set(fromIndex, toIndex, value);
        this.makeDirty();
    }
    
    @Override
    public void set(final int fromIndex, final int toIndex) {
        super.set(fromIndex, toIndex);
        this.makeDirty();
    }
}
