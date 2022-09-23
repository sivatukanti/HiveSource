// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store;

import org.datanucleus.ClassConstants;
import java.util.Map;
import org.datanucleus.metadata.AbstractMemberMetaData;
import java.util.Collections;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import org.datanucleus.util.NucleusLogger;
import java.util.concurrent.ConcurrentHashMap;
import org.datanucleus.util.Localiser;

public class StoreDataManager
{
    protected static final Localiser LOCALISER;
    protected ConcurrentHashMap<Object, StoreData> storeDataByClass;
    protected ConcurrentHashMap<Object, StoreData> savedStoreDataByClass;
    
    public StoreDataManager() {
        this.storeDataByClass = new ConcurrentHashMap<Object, StoreData>();
    }
    
    public void clear() {
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(StoreDataManager.LOCALISER.msg("032002"));
        }
        this.storeDataByClass.clear();
    }
    
    protected void registerStoreData(final StoreData data) {
        if (data.isFCO()) {
            if (this.storeDataByClass.containsKey(data.getName())) {
                return;
            }
            this.storeDataByClass.put(data.getName(), data);
        }
        else {
            if (this.storeDataByClass.containsKey(data.getMetaData())) {
                return;
            }
            this.storeDataByClass.put(data.getMetaData(), data);
        }
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(StoreDataManager.LOCALISER.msg("032001", data));
        }
    }
    
    public StoreData[] getStoreDataForProperties(final String key1, final Object value1, final String key2, final Object value2) {
        Collection<StoreData> results = null;
        final Collection storeDatas = this.storeDataByClass.values();
        for (final StoreData data : storeDatas) {
            if (data.getProperties() != null) {
                final Object prop1Value = data.getProperties().get(key1);
                final Object prop2Value = data.getProperties().get(key2);
                if (prop1Value == null || !prop1Value.equals(value1) || prop2Value == null || !prop2Value.equals(value2)) {
                    continue;
                }
                if (results == null) {
                    results = new HashSet<StoreData>();
                }
                results.add(data);
            }
        }
        if (results != null) {
            return results.toArray(new StoreData[results.size()]);
        }
        return null;
    }
    
    public boolean managesClass(final String className) {
        return this.storeDataByClass.containsKey(className);
    }
    
    public Collection<StoreData> getManagedStoreData() {
        return Collections.unmodifiableCollection((Collection<? extends StoreData>)this.storeDataByClass.values());
    }
    
    public StoreData get(final String className) {
        return this.storeDataByClass.get(className);
    }
    
    public StoreData get(final AbstractMemberMetaData apmd) {
        return this.storeDataByClass.get(apmd);
    }
    
    public int size() {
        return this.storeDataByClass.size();
    }
    
    public void begin() {
        this.savedStoreDataByClass = new ConcurrentHashMap<Object, StoreData>(this.storeDataByClass);
    }
    
    public void rollback() {
        this.storeDataByClass = this.savedStoreDataByClass;
        this.savedStoreDataByClass = null;
    }
    
    public void commit() {
        this.savedStoreDataByClass = null;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
