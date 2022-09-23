// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus;

import org.datanucleus.store.query.Query;
import org.datanucleus.store.Extent;
import java.util.Iterator;
import java.util.ArrayList;
import org.datanucleus.state.FetchPlanState;
import org.datanucleus.state.ObjectProvider;
import java.util.Map;

public class ExecutionContextThreadedImpl extends ExecutionContextImpl
{
    public ExecutionContextThreadedImpl(final NucleusContext ctx, final Object owner, final Map<String, Object> options) {
        super(ctx, owner, options);
    }
    
    @Override
    public boolean getMultithreaded() {
        return true;
    }
    
    @Override
    public void processNontransactionalUpdate() {
        try {
            this.lock.lock();
            super.processNontransactionalUpdate();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void enlistInTransaction(final ObjectProvider sm) {
        try {
            this.lock.lock();
            super.enlistInTransaction(sm);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void evictFromTransaction(final ObjectProvider sm) {
        try {
            this.lock.lock();
            super.evictFromTransaction(sm);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void addObjectProvider(final ObjectProvider op) {
        try {
            this.lock.lock();
            super.addObjectProvider(op);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void removeObjectProvider(final ObjectProvider op) {
        try {
            this.lock.lock();
            super.removeObjectProvider(op);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public ObjectProvider findObjectProvider(final Object pc) {
        try {
            this.lock.lock();
            return super.findObjectProvider(pc);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void hereIsObjectProvider(final ObjectProvider sm, final Object pc) {
        try {
            this.lock.lock();
            super.hereIsObjectProvider(sm, pc);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void close() {
        try {
            this.lock.lock();
            super.close();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void evictObject(final Object obj) {
        try {
            this.lock.lock();
            super.evictObject(obj);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void refreshObject(final Object obj) {
        try {
            this.lock.lock();
            super.refreshObject(obj);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void retrieveObject(final Object obj, final boolean fgOnly) {
        try {
            this.lock.lock();
            super.retrieveObject(obj, fgOnly);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public Object persistObject(final Object obj, final boolean merging) {
        try {
            this.lock.lock();
            return super.persistObject(obj, merging);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public Object[] persistObjects(final Object[] objs) {
        try {
            this.lock.lock();
            return super.persistObjects(objs);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void deleteObject(final Object obj) {
        try {
            this.lock.lock();
            super.deleteObject(obj);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void deleteObjects(final Object[] objs) {
        try {
            this.lock.lock();
            super.deleteObjects(objs);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void makeObjectTransient(final Object obj, final FetchPlanState state) {
        try {
            this.lock.lock();
            super.makeObjectTransient(obj, state);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void makeObjectTransactional(final Object obj) {
        try {
            this.lock.lock();
            super.makeObjectTransactional(obj);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void attachObject(final ObjectProvider ownerOP, final Object pc, final boolean sco) {
        try {
            this.lock.lock();
            super.attachObject(ownerOP, pc, sco);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public Object attachObjectCopy(final ObjectProvider ownerOP, final Object pc, final boolean sco) {
        try {
            this.lock.lock();
            return super.attachObjectCopy(ownerOP, pc, sco);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void detachObject(final Object obj, final FetchPlanState state) {
        try {
            this.lock.lock();
            super.detachObject(obj, state);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public Object detachObjectCopy(final Object pc, final FetchPlanState state) {
        try {
            this.lock.lock();
            return super.detachObjectCopy(pc, state);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void clearDirty(final ObjectProvider op) {
        try {
            this.lock.lock();
            super.clearDirty(op);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void clearDirty() {
        try {
            this.lock.lock();
            super.clearDirty();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void evictAllObjects() {
        this.assertIsOpen();
        try {
            this.lock.lock();
            synchronized (this.cache) {
                final ArrayList<ObjectProvider> opsToEvict = new ArrayList<ObjectProvider>();
                opsToEvict.addAll(((Map<K, ? extends ObjectProvider>)this.cache).values());
                for (final ObjectProvider op : opsToEvict) {
                    final Object pc = op.getObject();
                    op.evict();
                    this.removeObjectFromLevel1Cache(this.getApiAdapter().getIdForObject(pc));
                }
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void markDirty(final ObjectProvider op, final boolean directUpdate) {
        try {
            this.lock.lock();
            super.markDirty(op, directUpdate);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void flush() {
        try {
            this.lock.lock();
            super.flush();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void flushInternal(final boolean flushToDatastore) {
        try {
            this.lock.lock();
            super.flushInternal(flushToDatastore);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void replaceObjectId(final Object pc, final Object oldID, final Object newID) {
        try {
            this.lock.lock();
            super.replaceObjectId(pc, oldID, newID);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public Extent getExtent(final Class pcClass, final boolean subclasses) {
        try {
            this.lock.lock();
            return super.getExtent(pcClass, subclasses);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public Query newQuery() {
        try {
            this.lock.lock();
            return super.newQuery();
        }
        finally {
            this.lock.unlock();
        }
    }
}
