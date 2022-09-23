// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.flush;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.StringUtils;
import java.util.ListIterator;
import org.datanucleus.store.scostore.MapStore;
import org.datanucleus.store.scostore.ListStore;
import org.datanucleus.store.scostore.CollectionStore;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.scostore.Store;
import java.util.Collections;
import java.util.Iterator;
import org.datanucleus.util.NucleusLogger;
import java.util.ArrayList;
import java.util.List;
import org.datanucleus.util.Localiser;

public class OperationQueue
{
    protected static final Localiser LOCALISER;
    protected List<Operation> queuedOperations;
    
    public OperationQueue() {
        this.queuedOperations = new ArrayList<Operation>();
    }
    
    public synchronized void enqueue(final Operation oper) {
        this.queuedOperations.add(oper);
    }
    
    public synchronized void log() {
        NucleusLogger.GENERAL.debug(">> OperationQueue :");
        for (final Operation op : this.queuedOperations) {
            NucleusLogger.GENERAL.debug(">> " + op);
        }
    }
    
    public void clear() {
        this.queuedOperations.clear();
    }
    
    public List<Operation> getOperations() {
        return Collections.unmodifiableList((List<? extends Operation>)this.queuedOperations);
    }
    
    public synchronized void performAll() {
        for (final Operation op : this.queuedOperations) {
            op.perform();
        }
        this.queuedOperations.clear();
    }
    
    public synchronized void performAll(final Store store, final ObjectProvider op) {
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(OperationQueue.LOCALISER.msg("023005", op.getObjectAsPrintable(), store.getOwnerMemberMetaData().getFullFieldName()));
        }
        final List<Operation> flushOperations = new ArrayList<Operation>();
        final ListIterator<Operation> operIter = this.queuedOperations.listIterator();
        while (operIter.hasNext()) {
            final Operation oper = operIter.next();
            if (oper instanceof SCOOperation && ((SCOOperation)oper).getStore() == store) {
                flushOperations.add(oper);
                operIter.remove();
            }
        }
        final ListIterator<Operation> flushOperIter = flushOperations.listIterator();
        while (flushOperIter.hasNext()) {
            final Operation oper2 = flushOperIter.next();
            if (store instanceof CollectionStore) {
                if (!(store instanceof ListStore)) {
                    if (isAddFollowedByRemoveOnSameSCO(store, op, oper2, flushOperIter)) {
                        flushOperIter.next();
                    }
                    else if (isRemoveFollowedByAddOnSameSCO(store, op, oper2, flushOperIter)) {
                        flushOperIter.next();
                    }
                    else {
                        oper2.perform();
                    }
                }
                else {
                    oper2.perform();
                }
            }
            else if (store instanceof MapStore) {
                if (isPutFollowedByRemoveOnSameSCO(store, op, oper2, flushOperIter)) {
                    flushOperIter.next();
                }
                else {
                    oper2.perform();
                }
            }
            else {
                oper2.perform();
            }
        }
    }
    
    protected static boolean isAddFollowedByRemoveOnSameSCO(final Store store, final ObjectProvider op, final Operation currentOper, final ListIterator<Operation> listIter) {
        if (CollectionAddOperation.class.isInstance(currentOper)) {
            boolean addThenRemove = false;
            if (listIter.hasNext()) {
                final Operation operNext = listIter.next();
                if (CollectionRemoveOperation.class.isInstance(operNext)) {
                    final Object value = CollectionAddOperation.class.cast(currentOper).getValue();
                    if (value == CollectionRemoveOperation.class.cast(operNext).getValue()) {
                        addThenRemove = true;
                        NucleusLogger.PERSISTENCE.info("Member " + store.getOwnerMemberMetaData().getFullFieldName() + " of " + StringUtils.toJVMIDString(op.getObject()) + " had an add then a remove of element " + StringUtils.toJVMIDString(value) + " - operations ignored");
                    }
                }
                listIter.previous();
            }
            return addThenRemove;
        }
        return false;
    }
    
    protected static boolean isRemoveFollowedByAddOnSameSCO(final Store store, final ObjectProvider op, final Operation currentOper, final ListIterator<Operation> listIter) {
        if (CollectionRemoveOperation.class.isInstance(currentOper)) {
            boolean removeThenAdd = false;
            if (listIter.hasNext()) {
                final Operation opNext = listIter.next();
                if (CollectionAddOperation.class.isInstance(opNext)) {
                    final Object value = CollectionRemoveOperation.class.cast(currentOper).getValue();
                    if (value == CollectionAddOperation.class.cast(opNext).getValue()) {
                        removeThenAdd = true;
                        NucleusLogger.PERSISTENCE.info("Member" + store.getOwnerMemberMetaData().getFullFieldName() + " of " + StringUtils.toJVMIDString(op.getObject()) + " had a remove then add of element " + StringUtils.toJVMIDString(value) + " - operations ignored");
                    }
                }
                listIter.previous();
            }
            return removeThenAdd;
        }
        return false;
    }
    
    protected static boolean isPutFollowedByRemoveOnSameSCO(final Store store, final ObjectProvider op, final Operation currentOper, final ListIterator<Operation> listIter) {
        if (MapPutOperation.class.isInstance(currentOper)) {
            boolean putThenRemove = false;
            if (listIter.hasNext()) {
                final Operation operNext = listIter.next();
                if (MapRemoveOperation.class.isInstance(operNext)) {
                    final Object key = MapPutOperation.class.cast(currentOper).getKey();
                    if (key == MapRemoveOperation.class.cast(operNext).getKey()) {
                        putThenRemove = true;
                        NucleusLogger.PERSISTENCE.info("Member " + store.getOwnerMemberMetaData().getFullFieldName() + " of " + StringUtils.toJVMIDString(op.getObject()) + " had a put then a remove of key " + StringUtils.toJVMIDString(key) + " - operations ignored");
                    }
                }
                listIter.previous();
            }
            return putThenRemove;
        }
        return false;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
