// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.cache;

import java.util.Iterator;
import org.datanucleus.api.ApiAdapter;
import java.util.Calendar;
import java.util.Date;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import org.datanucleus.util.NucleusLogger;
import java.util.HashSet;
import java.util.ArrayList;
import org.datanucleus.store.types.SCOContainer;
import org.datanucleus.store.types.SCO;
import java.util.List;
import java.util.Collection;
import org.datanucleus.store.fieldmanager.FieldManager;
import org.datanucleus.metadata.MetaDataUtils;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.FieldPersistenceModifier;
import org.datanucleus.ExecutionContext;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.fieldmanager.AbstractFieldManager;

public class L2CachePopulateFieldManager extends AbstractFieldManager
{
    ObjectProvider op;
    ExecutionContext ec;
    CachedPC cachedPC;
    
    public L2CachePopulateFieldManager(final ObjectProvider op, final CachedPC cachedpc) {
        this.op = op;
        this.ec = op.getExecutionContext();
        this.cachedPC = cachedpc;
    }
    
    @Override
    public void storeBooleanField(final int fieldNumber, final boolean value) {
        final AbstractMemberMetaData mmd = this.op.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
        if (mmd.getPersistenceModifier() == FieldPersistenceModifier.TRANSACTIONAL) {
            this.cachedPC.setLoadedField(fieldNumber, false);
            return;
        }
        if (!mmd.isCacheable()) {
            this.cachedPC.setLoadedField(fieldNumber, false);
            return;
        }
        this.cachedPC.setLoadedField(fieldNumber, true);
        this.cachedPC.setFieldValue(fieldNumber, value);
    }
    
    @Override
    public void storeCharField(final int fieldNumber, final char value) {
        final AbstractMemberMetaData mmd = this.op.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
        if (mmd.getPersistenceModifier() == FieldPersistenceModifier.TRANSACTIONAL) {
            this.cachedPC.setLoadedField(fieldNumber, false);
            return;
        }
        if (!mmd.isCacheable()) {
            this.cachedPC.setLoadedField(fieldNumber, false);
            return;
        }
        this.cachedPC.setLoadedField(fieldNumber, true);
        this.cachedPC.setFieldValue(fieldNumber, value);
    }
    
    @Override
    public void storeByteField(final int fieldNumber, final byte value) {
        final AbstractMemberMetaData mmd = this.op.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
        if (mmd.getPersistenceModifier() == FieldPersistenceModifier.TRANSACTIONAL) {
            this.cachedPC.setLoadedField(fieldNumber, false);
            return;
        }
        if (!mmd.isCacheable()) {
            this.cachedPC.setLoadedField(fieldNumber, false);
            return;
        }
        this.cachedPC.setLoadedField(fieldNumber, true);
        this.cachedPC.setFieldValue(fieldNumber, value);
    }
    
    @Override
    public void storeShortField(final int fieldNumber, final short value) {
        final AbstractMemberMetaData mmd = this.op.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
        if (mmd.getPersistenceModifier() == FieldPersistenceModifier.TRANSACTIONAL) {
            this.cachedPC.setLoadedField(fieldNumber, false);
            return;
        }
        if (!mmd.isCacheable()) {
            this.cachedPC.setLoadedField(fieldNumber, false);
            return;
        }
        this.cachedPC.setLoadedField(fieldNumber, true);
        this.cachedPC.setFieldValue(fieldNumber, value);
    }
    
    @Override
    public void storeIntField(final int fieldNumber, final int value) {
        final AbstractMemberMetaData mmd = this.op.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
        if (mmd.getPersistenceModifier() == FieldPersistenceModifier.TRANSACTIONAL) {
            this.cachedPC.setLoadedField(fieldNumber, false);
            return;
        }
        if (!mmd.isCacheable()) {
            this.cachedPC.setLoadedField(fieldNumber, false);
            return;
        }
        this.cachedPC.setLoadedField(fieldNumber, true);
        this.cachedPC.setFieldValue(fieldNumber, value);
    }
    
    @Override
    public void storeLongField(final int fieldNumber, final long value) {
        final AbstractMemberMetaData mmd = this.op.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
        if (mmd.getPersistenceModifier() == FieldPersistenceModifier.TRANSACTIONAL) {
            this.cachedPC.setLoadedField(fieldNumber, false);
            return;
        }
        if (!mmd.isCacheable()) {
            this.cachedPC.setLoadedField(fieldNumber, false);
            return;
        }
        this.cachedPC.setLoadedField(fieldNumber, true);
        this.cachedPC.setFieldValue(fieldNumber, value);
    }
    
    @Override
    public void storeFloatField(final int fieldNumber, final float value) {
        final AbstractMemberMetaData mmd = this.op.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
        if (mmd.getPersistenceModifier() == FieldPersistenceModifier.TRANSACTIONAL) {
            this.cachedPC.setLoadedField(fieldNumber, false);
            return;
        }
        if (!mmd.isCacheable()) {
            this.cachedPC.setLoadedField(fieldNumber, false);
            return;
        }
        this.cachedPC.setLoadedField(fieldNumber, true);
        this.cachedPC.setFieldValue(fieldNumber, value);
    }
    
    @Override
    public void storeDoubleField(final int fieldNumber, final double value) {
        final AbstractMemberMetaData mmd = this.op.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
        if (mmd.getPersistenceModifier() == FieldPersistenceModifier.TRANSACTIONAL) {
            this.cachedPC.setLoadedField(fieldNumber, false);
            return;
        }
        if (!mmd.isCacheable()) {
            this.cachedPC.setLoadedField(fieldNumber, false);
            return;
        }
        this.cachedPC.setLoadedField(fieldNumber, true);
        this.cachedPC.setFieldValue(fieldNumber, value);
    }
    
    @Override
    public void storeStringField(final int fieldNumber, final String value) {
        final AbstractMemberMetaData mmd = this.op.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
        if (mmd.getPersistenceModifier() == FieldPersistenceModifier.TRANSACTIONAL) {
            this.cachedPC.setLoadedField(fieldNumber, false);
            return;
        }
        if (!mmd.isCacheable()) {
            this.cachedPC.setLoadedField(fieldNumber, false);
            return;
        }
        this.cachedPC.setLoadedField(fieldNumber, true);
        this.cachedPC.setFieldValue(fieldNumber, value);
    }
    
    @Override
    public void storeObjectField(final int fieldNumber, final Object value) {
        final AbstractMemberMetaData mmd = this.op.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
        if (mmd.getPersistenceModifier() == FieldPersistenceModifier.TRANSACTIONAL) {
            this.cachedPC.setLoadedField(fieldNumber, false);
            return;
        }
        if (!mmd.isCacheable()) {
            this.cachedPC.setLoadedField(fieldNumber, false);
            return;
        }
        final ApiAdapter api = this.ec.getApiAdapter();
        this.cachedPC.setLoadedField(fieldNumber, true);
        if (value == null) {
            this.cachedPC.setFieldValue(fieldNumber, null);
        }
        else if (api.isPersistable(value)) {
            if (mmd.isSerialized() || MetaDataUtils.isMemberEmbedded(mmd, mmd.getRelationType(this.ec.getClassLoaderResolver()), this.ec.getClassLoaderResolver(), this.ec.getMetaDataManager())) {
                if (this.ec.getNucleusContext().getPersistenceConfiguration().getBooleanProperty("datanucleus.cache.level2.cacheEmbedded")) {
                    final ObjectProvider valueOP = this.ec.findObjectProvider(value);
                    final int[] loadedFields = valueOP.getLoadedFieldNumbers();
                    final CachedPC valueCachedPC = new CachedPC(value.getClass(), valueOP.getLoadedFields(), null);
                    valueOP.provideFields(loadedFields, new L2CachePopulateFieldManager(valueOP, valueCachedPC));
                    this.cachedPC.setFieldValue(fieldNumber, valueCachedPC);
                }
                else {
                    this.cachedPC.setLoadedField(fieldNumber, false);
                }
                return;
            }
            this.cachedPC.setFieldValue(fieldNumber, this.getCacheableIdForId(api, value));
        }
        else if (value instanceof Collection) {
            if (MetaDataUtils.getInstance().storesPersistable(mmd, this.ec)) {
                if (value instanceof List && mmd.getOrderMetaData() != null && !mmd.getOrderMetaData().isIndexedList()) {
                    this.cachedPC.setLoadedField(fieldNumber, false);
                    return;
                }
                if (mmd.isSerialized() || mmd.isEmbedded() || mmd.getCollection().isSerializedElement() || mmd.getCollection().isEmbeddedElement()) {
                    this.cachedPC.setLoadedField(fieldNumber, false);
                    return;
                }
                final Collection collValue = (Collection)value;
                if (collValue instanceof SCO && !((SCOContainer)value).isLoaded()) {
                    this.cachedPC.setLoadedField(fieldNumber, false);
                    return;
                }
                final Iterator collIter = collValue.iterator();
                Collection returnColl = null;
                try {
                    if (value.getClass().isInterface()) {
                        if (List.class.isAssignableFrom(value.getClass()) || mmd.getOrderMetaData() != null) {
                            returnColl = new ArrayList();
                        }
                        else {
                            returnColl = new HashSet();
                        }
                    }
                    else if (value instanceof SCO) {
                        returnColl = (Collection)((SCO)value).getValue().getClass().newInstance();
                    }
                    else {
                        returnColl = (Collection)value.getClass().newInstance();
                    }
                    while (collIter.hasNext()) {
                        final Object elem = collIter.next();
                        if (elem == null) {
                            returnColl.add(null);
                        }
                        else {
                            returnColl.add(this.getCacheableIdForId(api, elem));
                        }
                    }
                    this.cachedPC.setFieldValue(fieldNumber, returnColl);
                    return;
                }
                catch (Exception e) {
                    NucleusLogger.CACHE.warn("Unable to create object of type " + value.getClass().getName() + " for L2 caching : " + e.getMessage());
                    this.cachedPC.setLoadedField(fieldNumber, false);
                    return;
                }
            }
            if (value instanceof SCOContainer) {
                if (!((SCOContainer)value).isLoaded()) {
                    this.cachedPC.setLoadedField(fieldNumber, false);
                    return;
                }
                this.cachedPC.setFieldValue(fieldNumber, ((SCO)value).getValue());
            }
            else {
                this.cachedPC.setFieldValue(fieldNumber, value);
            }
        }
        else if (value instanceof Map) {
            if (MetaDataUtils.getInstance().storesPersistable(mmd, this.ec)) {
                if (mmd.isSerialized() || mmd.isEmbedded() || mmd.getMap().isSerializedKey() || (mmd.getMap().keyIsPersistent() && mmd.getMap().isEmbeddedKey()) || mmd.getMap().isSerializedValue() || (mmd.getMap().valueIsPersistent() && mmd.getMap().isEmbeddedValue())) {
                    this.cachedPC.setLoadedField(fieldNumber, false);
                    return;
                }
                if (value instanceof SCO && !((SCOContainer)value).isLoaded()) {
                    this.cachedPC.setLoadedField(fieldNumber, false);
                    return;
                }
                try {
                    Map returnMap = null;
                    if (value.getClass().isInterface()) {
                        returnMap = new HashMap();
                    }
                    else if (value instanceof SCO) {
                        returnMap = (Map)((SCO)value).getValue().getClass().newInstance();
                    }
                    else {
                        returnMap = (Map)value.getClass().newInstance();
                    }
                    for (final Map.Entry entry : ((Map)value).entrySet()) {
                        Object mapKey = null;
                        Object mapValue = null;
                        if (mmd.getMap().keyIsPersistent()) {
                            mapKey = this.getCacheableIdForId(api, entry.getKey());
                        }
                        else {
                            mapKey = entry.getKey();
                        }
                        if (mmd.getMap().valueIsPersistent()) {
                            mapValue = this.getCacheableIdForId(api, entry.getValue());
                        }
                        else {
                            mapValue = entry.getValue();
                        }
                        returnMap.put(mapKey, mapValue);
                    }
                    this.cachedPC.setFieldValue(fieldNumber, returnMap);
                    return;
                }
                catch (Exception e2) {
                    NucleusLogger.CACHE.warn("Unable to create object of type " + value.getClass().getName() + " for L2 caching : " + e2.getMessage());
                    this.cachedPC.setLoadedField(fieldNumber, false);
                    return;
                }
            }
            if (value instanceof SCOContainer) {
                if (!((SCOContainer)value).isLoaded()) {
                    this.cachedPC.setLoadedField(fieldNumber, false);
                    return;
                }
                this.cachedPC.setFieldValue(fieldNumber, ((SCO)value).getValue());
            }
            else {
                this.cachedPC.setFieldValue(fieldNumber, value);
            }
        }
        else if (value instanceof Object[]) {
            if (MetaDataUtils.getInstance().storesPersistable(mmd, this.ec)) {
                if (mmd.isSerialized() || mmd.isEmbedded() || mmd.getArray().isSerializedElement() || mmd.getArray().isEmbeddedElement()) {
                    this.cachedPC.setLoadedField(fieldNumber, false);
                    return;
                }
                final Object[] returnArr = new Object[Array.getLength(value)];
                for (int i = 0; i < Array.getLength(value); ++i) {
                    final Object element = Array.get(value, i);
                    if (element != null) {
                        returnArr[i] = this.getCacheableIdForId(api, element);
                    }
                    else {
                        returnArr[i] = null;
                    }
                }
                this.cachedPC.setFieldValue(fieldNumber, returnArr);
            }
            else {
                this.cachedPC.setFieldValue(fieldNumber, value);
            }
        }
        else if (value instanceof StringBuffer) {
            this.cachedPC.setFieldValue(fieldNumber, new StringBuffer((CharSequence)value));
        }
        else if (value instanceof SCO) {
            final Object unwrappedValue = ((SCO)value).getValue();
            if (unwrappedValue instanceof Date) {
                this.cachedPC.setFieldValue(fieldNumber, ((Date)unwrappedValue).clone());
            }
            else if (unwrappedValue instanceof Calendar) {
                this.cachedPC.setFieldValue(fieldNumber, ((Calendar)unwrappedValue).clone());
            }
            else {
                this.cachedPC.setFieldValue(fieldNumber, unwrappedValue);
            }
        }
        else if (value instanceof Date) {
            this.cachedPC.setFieldValue(fieldNumber, ((Date)value).clone());
        }
        else if (value instanceof Calendar) {
            this.cachedPC.setFieldValue(fieldNumber, ((Calendar)value).clone());
        }
        else {
            this.cachedPC.setFieldValue(fieldNumber, value);
        }
    }
    
    private Object getCacheableIdForId(final ApiAdapter api, final Object pc) {
        if (pc == null) {
            return null;
        }
        final Object id = api.getIdForObject(pc);
        if (api.isDatastoreIdentity(id)) {
            return id;
        }
        if (api.isSingleFieldIdentity(id)) {
            return id;
        }
        return new CachedPC.CachedId(pc.getClass().getName(), id);
    }
}
