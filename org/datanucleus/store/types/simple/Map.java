// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.simple;

import org.datanucleus.ClassConstants;
import java.io.ObjectStreamException;
import java.util.Iterator;
import java.util.Collection;
import java.util.Set;
import org.datanucleus.state.FetchPlanState;
import org.datanucleus.store.types.SCOUtils;
import org.datanucleus.util.NucleusLogger;
import java.util.HashMap;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.util.Localiser;
import java.io.Serializable;
import org.datanucleus.store.types.SCOMap;
import java.util.AbstractMap;

public class Map extends AbstractMap implements java.util.Map, SCOMap, Cloneable, Serializable
{
    protected static final Localiser LOCALISER;
    protected transient ObjectProvider ownerOP;
    protected transient AbstractMemberMetaData ownerMmd;
    protected java.util.Map delegate;
    
    public Map(final ObjectProvider ownerOP, final AbstractMemberMetaData mmd) {
        this.ownerOP = ownerOP;
        this.ownerMmd = mmd;
    }
    
    @Override
    public synchronized void initialise(final Object o, final boolean forInsert, final boolean forUpdate) {
        final java.util.Map m = (java.util.Map)o;
        if (m != null) {
            this.delegate = new HashMap(m);
        }
        else {
            this.delegate = new HashMap();
        }
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(Map.LOCALISER.msg("023003", this.ownerOP.getObjectAsPrintable(), this.ownerMmd.getName(), "" + this.size(), SCOUtils.getSCOWrapperOptionsMessage(true, false, true, false)));
        }
    }
    
    @Override
    public void initialise() {
        this.delegate = new HashMap();
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(Map.LOCALISER.msg("023003", this.ownerOP.getObjectAsPrintable(), this.ownerMmd.getName(), "" + this.size(), SCOUtils.getSCOWrapperOptionsMessage(true, false, true, false)));
        }
    }
    
    @Override
    public Object getValue() {
        return this.delegate;
    }
    
    @Override
    public void load() {
    }
    
    @Override
    public boolean isLoaded() {
        return true;
    }
    
    @Override
    public void updateEmbeddedKey(final Object key, final int fieldNumber, final Object newValue) {
        this.makeDirty();
    }
    
    @Override
    public void updateEmbeddedValue(final Object value, final int fieldNumber, final Object newValue) {
        this.makeDirty();
    }
    
    @Override
    public String getFieldName() {
        return this.ownerMmd.getName();
    }
    
    @Override
    public Object getOwner() {
        return (this.ownerOP != null) ? this.ownerOP.getObject() : null;
    }
    
    @Override
    public synchronized void unsetOwner() {
        if (this.ownerOP != null) {
            this.ownerOP = null;
            this.ownerMmd = null;
        }
    }
    
    public void makeDirty() {
        if (this.ownerOP != null) {
            this.ownerOP.makeDirty(this.ownerMmd.getAbsoluteFieldNumber());
        }
    }
    
    @Override
    public Object detachCopy(final FetchPlanState state) {
        final java.util.Map detached = new HashMap();
        SCOUtils.detachCopyForMap(this.ownerOP, this.entrySet(), state, detached);
        return detached;
    }
    
    @Override
    public void attachCopy(final Object value) {
        final java.util.Map m = (java.util.Map)value;
        final boolean keysWithoutIdentity = SCOUtils.mapHasKeysWithoutIdentity(this.ownerMmd);
        final boolean valuesWithoutIdentity = SCOUtils.mapHasValuesWithoutIdentity(this.ownerMmd);
        final java.util.Map attachedKeysValues = new HashMap(m.size());
        SCOUtils.attachCopyForMap(this.ownerOP, m.entrySet(), attachedKeysValues, keysWithoutIdentity, valuesWithoutIdentity);
        SCOUtils.updateMapWithMapKeysValues(this.ownerOP.getExecutionContext().getApiAdapter(), this, attachedKeysValues);
    }
    
    @Override
    public Object clone() {
        return ((HashMap)this.delegate).clone();
    }
    
    @Override
    public synchronized boolean containsKey(final Object key) {
        return this.delegate.containsKey(key);
    }
    
    @Override
    public synchronized boolean containsValue(final Object value) {
        return this.delegate.containsValue(value);
    }
    
    @Override
    public synchronized Set entrySet() {
        return this.delegate.entrySet();
    }
    
    @Override
    public synchronized boolean equals(final Object o) {
        return this.delegate.equals(o);
    }
    
    @Override
    public synchronized Object get(final Object key) {
        return this.delegate.get(key);
    }
    
    @Override
    public synchronized int hashCode() {
        return this.delegate.hashCode();
    }
    
    @Override
    public synchronized boolean isEmpty() {
        return this.delegate.isEmpty();
    }
    
    @Override
    public synchronized Set keySet() {
        return this.delegate.keySet();
    }
    
    @Override
    public synchronized int size() {
        return this.delegate.size();
    }
    
    @Override
    public synchronized Collection values() {
        return this.delegate.values();
    }
    
    @Override
    public synchronized void clear() {
        if (this.ownerOP != null && !this.delegate.isEmpty() && (SCOUtils.hasDependentKey(this.ownerMmd) || SCOUtils.hasDependentValue(this.ownerMmd))) {
            for (final Entry entry : this.delegate.entrySet()) {
                if (SCOUtils.hasDependentKey(this.ownerMmd)) {
                    this.ownerOP.getExecutionContext().deleteObjectInternal(entry.getKey());
                }
                if (SCOUtils.hasDependentValue(this.ownerMmd)) {
                    this.ownerOP.getExecutionContext().deleteObjectInternal(entry.getValue());
                }
            }
        }
        this.delegate.clear();
        this.makeDirty();
        if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
            this.ownerOP.getExecutionContext().processNontransactionalUpdate();
        }
    }
    
    @Override
    public synchronized Object put(final Object key, final Object value) {
        final Object oldValue = this.delegate.put(key, value);
        this.makeDirty();
        if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
            this.ownerOP.getExecutionContext().processNontransactionalUpdate();
        }
        return oldValue;
    }
    
    @Override
    public synchronized void putAll(final java.util.Map m) {
        this.delegate.putAll(m);
        this.makeDirty();
        if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
            this.ownerOP.getExecutionContext().processNontransactionalUpdate();
        }
    }
    
    @Override
    public synchronized Object remove(final Object key) {
        final Object value = this.delegate.remove(key);
        if (this.ownerOP != null && (SCOUtils.hasDependentKey(this.ownerMmd) || SCOUtils.hasDependentValue(this.ownerMmd))) {
            if (SCOUtils.hasDependentKey(this.ownerMmd)) {
                this.ownerOP.getExecutionContext().deleteObjectInternal(key);
            }
            if (SCOUtils.hasDependentValue(this.ownerMmd)) {
                this.ownerOP.getExecutionContext().deleteObjectInternal(value);
            }
        }
        this.makeDirty();
        if (this.ownerOP != null && !this.ownerOP.getExecutionContext().getTransaction().isActive()) {
            this.ownerOP.getExecutionContext().processNontransactionalUpdate();
        }
        return value;
    }
    
    protected Object writeReplace() throws ObjectStreamException {
        return new HashMap(this.delegate);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
