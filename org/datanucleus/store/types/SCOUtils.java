// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types;

import org.datanucleus.ClassConstants;
import java.util.Queue;
import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import org.datanucleus.store.FieldValues;
import org.datanucleus.ExecutionContext;
import org.datanucleus.state.FetchPlanState;
import org.datanucleus.exceptions.NucleusException;
import java.util.Comparator;
import org.datanucleus.ClassLoaderResolver;
import java.lang.reflect.Array;
import org.datanucleus.store.scostore.CollectionStore;
import org.datanucleus.store.scostore.SetStore;
import java.util.HashSet;
import org.datanucleus.store.scostore.MapStore;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.store.StoreManager;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.util.StringUtils;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.util.Localiser;

public class SCOUtils
{
    private static final Localiser LOCALISER;
    
    public static SCO newSCOInstance(final ObjectProvider ownerOP, final AbstractMemberMetaData mmd, final Class declaredType, final Class instantiatedType, final Object value, final boolean forInsert, final boolean forUpdate, final boolean replaceField) {
        if (!mmd.getType().isAssignableFrom(declaredType)) {
            throw new NucleusUserException(SCOUtils.LOCALISER.msg("023010", declaredType.getName(), mmd.getName(), mmd.getType()));
        }
        final TypeManager typeMgr = ownerOP.getExecutionContext().getNucleusContext().getTypeManager();
        if (value != null && typeMgr.isSecondClassWrapper(value.getClass().getName())) {
            if (replaceField) {
                ownerOP.replaceField(mmd.getAbsoluteFieldNumber(), value);
            }
            return (SCO)value;
        }
        String typeName = declaredType.getName();
        if (instantiatedType != null) {
            typeName = instantiatedType.getName();
        }
        if (value != null) {
            typeName = value.getClass().getName();
        }
        final StoreManager storeMgr = ownerOP.getExecutionContext().getStoreManager();
        final boolean backedWrapper = storeMgr.useBackedSCOWrapperForMember(mmd, ownerOP.getExecutionContext());
        Class wrapperType = null;
        if (backedWrapper) {
            wrapperType = getBackedWrapperTypeForType(declaredType, instantiatedType, typeName, typeMgr);
        }
        else {
            wrapperType = getSimpleWrapperTypeForType(declaredType, instantiatedType, typeName, typeMgr);
        }
        if (wrapperType == null) {
            throw new NucleusUserException(SCOUtils.LOCALISER.msg("023011", declaredType.getName(), StringUtils.toJVMIDString(value), mmd.getFullFieldName()));
        }
        SCO sco = null;
        try {
            sco = createSCOWrapper(wrapperType, ownerOP, mmd, replaceField, value, forInsert, forUpdate);
        }
        catch (UnsupportedOperationException uoe) {
            if (!backedWrapper) {
                throw uoe;
            }
            NucleusLogger.PERSISTENCE.warn("Creation of backed wrapper for " + mmd.getFullFieldName() + " unsupported, so trying simple wrapper");
            wrapperType = getSimpleWrapperTypeForType(declaredType, instantiatedType, typeName, typeMgr);
            sco = createSCOWrapper(wrapperType, ownerOP, mmd, replaceField, value, forInsert, forUpdate);
        }
        return sco;
    }
    
    private static Class getBackedWrapperTypeForType(final Class declaredType, final Class instantiatedType, final String typeName, final TypeManager typeMgr) {
        Class wrapperType = typeMgr.getWrappedTypeBackedForType(typeName);
        if (wrapperType == null) {
            if (instantiatedType != null) {
                wrapperType = typeMgr.getWrappedTypeBackedForType(instantiatedType.getName());
            }
            if (wrapperType == null) {
                wrapperType = typeMgr.getWrappedTypeBackedForType(declaredType.getName());
            }
        }
        return wrapperType;
    }
    
    private static Class getSimpleWrapperTypeForType(final Class declaredType, final Class instantiatedType, final String typeName, final TypeManager typeMgr) {
        Class wrapperType = typeMgr.getWrapperTypeForType(typeName);
        if (wrapperType == null) {
            if (instantiatedType != null) {
                wrapperType = typeMgr.getWrapperTypeForType(instantiatedType.getName());
            }
            if (wrapperType == null) {
                wrapperType = typeMgr.getWrapperTypeForType(declaredType.getName());
            }
        }
        return wrapperType;
    }
    
    private static SCO createSCOWrapper(final Class wrapperType, final ObjectProvider ownerOP, final AbstractMemberMetaData mmd, final boolean replaceField, final Object fieldValue, final boolean forInsert, final boolean forUpdate) {
        final SCO sco = (SCO)ClassUtils.newInstance(wrapperType, new Class[] { ObjectProvider.class, AbstractMemberMetaData.class }, new Object[] { ownerOP, mmd });
        if (replaceField) {
            ownerOP.replaceField(mmd.getAbsoluteFieldNumber(), sco);
        }
        if (fieldValue != null) {
            sco.initialise(fieldValue, forInsert, forUpdate);
        }
        else {
            sco.initialise();
        }
        return sco;
    }
    
    public static String getContainerInfoMessage(final ObjectProvider ownerOP, final String fieldName, final SCOContainer cont, final boolean useCache, final boolean queued, final boolean allowNulls, final boolean lazyLoading) {
        final String msg = SCOUtils.LOCALISER.msg("023004", ownerOP.getObjectAsPrintable(), fieldName, cont.getClass().getName(), "[cache-values=" + useCache + ", lazy-loading=" + lazyLoading + ", queued-operations=" + queued + ", allow-nulls=" + allowNulls + "]");
        return msg;
    }
    
    public static String getSCOWrapperOptionsMessage(final boolean useCache, final boolean queued, final boolean allowNulls, final boolean lazyLoading) {
        final StringBuffer str = new StringBuffer();
        if (useCache) {
            str.append("cached");
        }
        if (lazyLoading) {
            if (str.length() > 0) {
                str.append(",");
            }
            str.append("lazy-loaded");
        }
        if (queued) {
            if (str.length() > 0) {
                str.append(",");
            }
            str.append("queued");
        }
        if (allowNulls) {
            if (str.length() > 0) {
                str.append(",");
            }
            str.append("allowNulls");
        }
        return str.toString();
    }
    
    public static boolean allowNullsInContainer(final boolean defaultValue, final AbstractMemberMetaData mmd) {
        if (mmd.getContainer() == null) {
            return defaultValue;
        }
        return Boolean.TRUE.equals(mmd.getContainer().allowNulls()) || (!Boolean.FALSE.equals(mmd.getContainer().allowNulls()) && defaultValue);
    }
    
    public static boolean useContainerCache(final ObjectProvider ownerOP, final AbstractMemberMetaData mmd) {
        if (ownerOP == null) {
            return false;
        }
        boolean useCache = ownerOP.getExecutionContext().getNucleusContext().getPersistenceConfiguration().getBooleanProperty("datanucleus.cache.collections");
        if (ownerOP.getExecutionContext().getBooleanProperty("datanucleus.cache.collections") != null) {
            useCache = ownerOP.getExecutionContext().getBooleanProperty("datanucleus.cache.collections");
        }
        if (mmd.getOrderMetaData() != null && !mmd.getOrderMetaData().isIndexedList()) {
            useCache = true;
        }
        else if (mmd.getContainer() != null && mmd.getContainer().hasExtension("cache")) {
            useCache = Boolean.parseBoolean(mmd.getContainer().getValueForExtension("cache"));
        }
        return useCache;
    }
    
    public static boolean useCachedLazyLoading(final ObjectProvider ownerOP, final AbstractMemberMetaData mmd) {
        if (ownerOP == null) {
            return false;
        }
        boolean lazy = false;
        final AbstractClassMetaData cmd = ownerOP.getClassMetaData();
        final Boolean lazyCollections = ownerOP.getExecutionContext().getNucleusContext().getPersistenceConfiguration().getBooleanObjectProperty("datanucleus.cache.collections.lazy");
        if (lazyCollections != null) {
            lazy = lazyCollections;
        }
        else if (mmd.getContainer() != null && mmd.getContainer().hasExtension("cache-lazy-loading")) {
            lazy = Boolean.parseBoolean(mmd.getContainer().getValueForExtension("cache-lazy-loading"));
        }
        else {
            boolean inFP = false;
            final int[] fpFields = ownerOP.getExecutionContext().getFetchPlan().getFetchPlanForClass(cmd).getMemberNumbers();
            final int fieldNo = mmd.getAbsoluteFieldNumber();
            if (fpFields != null && fpFields.length > 0) {
                for (int i = 0; i < fpFields.length; ++i) {
                    if (fpFields[i] == fieldNo) {
                        inFP = true;
                        break;
                    }
                }
            }
            lazy = !inFP;
        }
        return lazy;
    }
    
    public static boolean collectionHasElementsWithoutIdentity(final AbstractMemberMetaData mmd) {
        boolean elementsWithoutIdentity = false;
        if (mmd.isSerialized()) {
            elementsWithoutIdentity = true;
        }
        else if (mmd.getElementMetaData() != null && mmd.getElementMetaData().getEmbeddedMetaData() != null && mmd.getJoinMetaData() != null) {
            elementsWithoutIdentity = true;
        }
        else if (mmd.getCollection() != null && mmd.getCollection().isEmbeddedElement()) {
            elementsWithoutIdentity = true;
        }
        return elementsWithoutIdentity;
    }
    
    public static boolean mapHasKeysWithoutIdentity(final AbstractMemberMetaData fmd) {
        boolean keysWithoutIdentity = false;
        if (fmd.isSerialized()) {
            keysWithoutIdentity = true;
        }
        else if (fmd.getKeyMetaData() != null && fmd.getKeyMetaData().getEmbeddedMetaData() != null && fmd.getJoinMetaData() != null) {
            keysWithoutIdentity = true;
        }
        else if (fmd.getMap() != null && fmd.getMap().isEmbeddedKey()) {
            keysWithoutIdentity = true;
        }
        return keysWithoutIdentity;
    }
    
    public static boolean mapHasValuesWithoutIdentity(final AbstractMemberMetaData fmd) {
        boolean valuesWithoutIdentity = false;
        if (fmd.isSerialized()) {
            valuesWithoutIdentity = true;
        }
        else if (fmd.getValueMetaData() != null && fmd.getValueMetaData().getEmbeddedMetaData() != null && fmd.getJoinMetaData() != null) {
            valuesWithoutIdentity = true;
        }
        else if (fmd.getMap() != null && fmd.getMap().isEmbeddedValue()) {
            valuesWithoutIdentity = true;
        }
        return valuesWithoutIdentity;
    }
    
    public static boolean collectionHasSerialisedElements(final AbstractMemberMetaData fmd) {
        boolean serialised = fmd.isSerialized();
        if (fmd.getCollection() != null && fmd.getCollection().isEmbeddedElement() && fmd.getJoinMetaData() == null) {
            serialised = true;
        }
        return serialised;
    }
    
    public static boolean arrayIsStoredInSingleColumn(final AbstractMemberMetaData fmd, final MetaDataManager mmgr) {
        boolean singleColumn = fmd.isSerialized();
        if (!singleColumn && fmd.getArray() != null && fmd.getJoinMetaData() == null) {
            if (fmd.getArray().isEmbeddedElement()) {
                singleColumn = true;
            }
            final Class elementClass = fmd.getType().getComponentType();
            final ApiAdapter api = mmgr.getApiAdapter();
            if (!elementClass.isInterface() && !api.isPersistable(elementClass)) {
                singleColumn = true;
            }
        }
        return singleColumn;
    }
    
    public static boolean mapHasSerialisedKeysAndValues(final AbstractMemberMetaData fmd) {
        boolean inverseKeyField = false;
        if (fmd.getKeyMetaData() != null && fmd.getKeyMetaData().getMappedBy() != null) {
            inverseKeyField = true;
        }
        boolean inverseValueField = false;
        if (fmd.getValueMetaData() != null && fmd.getValueMetaData().getMappedBy() != null) {
            inverseValueField = true;
        }
        boolean serialised = fmd.isSerialized();
        if (fmd.getMap() != null && fmd.getJoinMetaData() == null && fmd.getMap().isEmbeddedKey() && fmd.getMap().isEmbeddedValue() && !inverseKeyField && !inverseValueField) {
            serialised = true;
        }
        return serialised;
    }
    
    public static boolean attachCopyElements(final ObjectProvider ownerOP, final Collection scoColl, final Collection detachedElements, final boolean elementsWithoutId) {
        boolean updated = false;
        final ApiAdapter api = ownerOP.getExecutionContext().getApiAdapter();
        Iterator scoCollIter = scoColl.iterator();
        while (scoCollIter.hasNext()) {
            final Object currentElem = scoCollIter.next();
            final Object currentElemId = api.getIdForObject(currentElem);
            final Iterator desiredIter = detachedElements.iterator();
            boolean contained = false;
            if (elementsWithoutId) {
                contained = detachedElements.contains(currentElem);
            }
            else {
                while (desiredIter.hasNext()) {
                    final Object desiredElem = desiredIter.next();
                    if (currentElemId != null) {
                        if (currentElemId.equals(api.getIdForObject(desiredElem))) {
                            contained = true;
                            break;
                        }
                        continue;
                    }
                    else {
                        if (currentElem == desiredElem) {
                            contained = true;
                            break;
                        }
                        continue;
                    }
                }
            }
            if (!contained) {
                scoCollIter.remove();
                updated = true;
            }
        }
        for (final Object detachedElement : detachedElements) {
            if (elementsWithoutId) {
                if (scoColl.contains(detachedElement)) {
                    continue;
                }
                scoColl.add(detachedElement);
                updated = true;
            }
            else {
                final Object detachedElemId = api.getIdForObject(detachedElement);
                scoCollIter = scoColl.iterator();
                boolean contained = false;
                while (scoCollIter.hasNext()) {
                    final Object scoCollElem = scoCollIter.next();
                    final Object scoCollElemId = api.getIdForObject(scoCollElem);
                    if (scoCollElemId != null && scoCollElemId.equals(detachedElemId)) {
                        contained = true;
                        break;
                    }
                }
                if (!contained) {
                    scoColl.add(detachedElement);
                    updated = true;
                }
                else {
                    ownerOP.getExecutionContext().attachObjectCopy(ownerOP, detachedElement, false);
                }
            }
        }
        return updated;
    }
    
    public static void attachCopyForCollection(final ObjectProvider ownerOP, final Object[] detachedElements, final Collection attached, final boolean elementsWithoutIdentity) {
        final ApiAdapter api = ownerOP.getExecutionContext().getApiAdapter();
        for (int i = 0; i < detachedElements.length; ++i) {
            if (api.isPersistable(detachedElements[i]) && api.isDetachable(detachedElements[i])) {
                attached.add(ownerOP.getExecutionContext().attachObjectCopy(ownerOP, detachedElements[i], elementsWithoutIdentity));
            }
            else {
                attached.add(detachedElements[i]);
            }
        }
    }
    
    public static boolean updateListWithListElements(final List list, final List elements) {
        boolean updated = false;
        final ArrayList newCopy = new ArrayList(elements);
        final Iterator attachedIter = list.iterator();
        while (attachedIter.hasNext()) {
            final Object attachedElement = attachedIter.next();
            if (!newCopy.remove(attachedElement)) {
                attachedIter.remove();
                updated = true;
            }
        }
        final ArrayList oldCopy = new ArrayList(list);
        for (final Object element : elements) {
            if (!oldCopy.remove(element)) {
                list.add(element);
                updated = true;
            }
        }
        final Iterator elementsIter = elements.iterator();
        int position = 0;
        while (elementsIter.hasNext()) {
            final Object element2 = elementsIter.next();
            final Object currentElement = list.get(position);
            boolean updatePosition = false;
            if ((element2 == null && currentElement != null) || (element2 != null && currentElement == null)) {
                updatePosition = true;
            }
            else if (element2 != null && currentElement != null && !currentElement.equals(element2)) {
                updatePosition = true;
            }
            if (updatePosition) {
                ((SCOList)list).set(position, element2, false);
                updated = true;
            }
            ++position;
        }
        return updated;
    }
    
    public static void attachCopyForMap(final ObjectProvider ownerOP, final Set detachedEntries, final Map attached, final boolean keysWithoutIdentity, final boolean valuesWithoutIdentity) {
        final Iterator iter = detachedEntries.iterator();
        final ApiAdapter api = ownerOP.getExecutionContext().getApiAdapter();
        while (iter.hasNext()) {
            final Map.Entry entry = iter.next();
            Object val = entry.getValue();
            Object key = entry.getKey();
            if (api.isPersistable(val) && api.isDetachable(val)) {
                val = ownerOP.getExecutionContext().attachObjectCopy(ownerOP, val, valuesWithoutIdentity);
            }
            if (api.isPersistable(key) && api.isDetachable(key)) {
                key = ownerOP.getExecutionContext().attachObjectCopy(ownerOP, key, keysWithoutIdentity);
            }
            attached.put(key, val);
        }
    }
    
    public static boolean updateMapWithMapKeysValues(final ApiAdapter api, final Map map, final Map keysValues) {
        boolean updated = false;
        final Map copy = new HashMap(map);
        for (final Map.Entry entry : copy.entrySet()) {
            final Object key = entry.getKey();
            if (!keysValues.containsKey(key)) {
                map.remove(key);
                updated = true;
            }
        }
        for (final Map.Entry entry2 : keysValues.entrySet()) {
            final Object key2 = entry2.getKey();
            final Object value = entry2.getValue();
            if (!map.containsKey(key2)) {
                map.put(key2, keysValues.get(key2));
                updated = true;
            }
            else {
                final Object oldValue = map.get(key2);
                if (api.isPersistable(value) && api.getIdForObject(value) != api.getIdForObject(oldValue)) {
                    map.put(key2, value);
                }
                else {
                    if ((oldValue != null || value == null) && (oldValue == null || oldValue.equals(value))) {
                        continue;
                    }
                    map.put(key2, value);
                }
            }
        }
        return updated;
    }
    
    public static void populateMapDelegateWithStoreData(final Map delegate, final MapStore store, final ObjectProvider ownerOP) {
        final HashSet keys = new HashSet();
        if (!store.keysAreEmbedded() && !store.keysAreSerialised()) {
            final SetStore keystore = store.keySetStore();
            final Iterator keyIter = keystore.iterator(ownerOP);
            while (keyIter.hasNext()) {
                keys.add(keyIter.next());
            }
        }
        final HashSet values = new HashSet();
        if (!store.valuesAreEmbedded() && !store.valuesAreSerialised()) {
            final SetStore valuestore = store.valueSetStore();
            final Iterator valueIter = valuestore.iterator(ownerOP);
            while (valueIter.hasNext()) {
                values.add(valueIter.next());
            }
        }
        final SetStore entries = store.entrySetStore();
        final Iterator entryIter = entries.iterator(ownerOP);
        while (entryIter.hasNext()) {
            final Map.Entry entry = entryIter.next();
            final Object key = entry.getKey();
            final Object value = entry.getValue();
            delegate.put(key, value);
        }
        if (!store.keysAreEmbedded() && !store.keysAreSerialised() && delegate.size() != keys.size()) {
            NucleusLogger.DATASTORE_RETRIEVE.warn("The number of Map key objects (" + keys.size() + ")" + " was different to the number of entries (" + delegate.size() + ")." + " Likely there is a bug in your datastore");
        }
        if (!store.valuesAreEmbedded() && !store.valuesAreSerialised() && delegate.size() != values.size()) {
            NucleusLogger.DATASTORE_RETRIEVE.warn("The number of Map value objects (" + values.size() + ")" + " was different to the number of entries (" + delegate.size() + ")." + " Likely there is a bug in your datastore");
        }
        keys.clear();
        values.clear();
    }
    
    public static Object[] toArray(final CollectionStore backingStore, final ObjectProvider op) {
        final Object[] result = new Object[backingStore.size(op)];
        final Iterator it = backingStore.iterator(op);
        int i = 0;
        while (it.hasNext()) {
            result[i] = it.next();
            ++i;
        }
        return result;
    }
    
    public static Object[] toArray(final CollectionStore backingStore, final ObjectProvider op, Object[] a) {
        final int size = backingStore.size(op);
        if (a.length < size) {
            a = (Object[])Array.newInstance(a.getClass().getComponentType(), size);
        }
        final Iterator it = backingStore.iterator(op);
        for (int i = 0; i < size; ++i) {
            a[i] = it.next();
        }
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }
    
    public static Comparator getComparator(final AbstractMemberMetaData fmd, final ClassLoaderResolver clr) {
        Comparator comparator = null;
        String comparatorName = null;
        if (fmd.hasMap() && fmd.getMap().hasExtension("comparator-name")) {
            comparatorName = fmd.getMap().getValueForExtension("comparator-name");
        }
        else if (fmd.hasCollection() && fmd.getCollection().hasExtension("comparator-name")) {
            comparatorName = fmd.getCollection().getValueForExtension("comparator-name");
        }
        if (comparatorName != null) {
            Class comparatorCls = null;
            try {
                comparatorCls = clr.classForName(comparatorName);
                comparator = (Comparator)ClassUtils.newInstance(comparatorCls, null, null);
            }
            catch (NucleusException jpe) {
                NucleusLogger.PERSISTENCE.warn(SCOUtils.LOCALISER.msg("023012", fmd.getFullFieldName(), comparatorName));
            }
        }
        return comparator;
    }
    
    public static void refreshFetchPlanFieldsForCollection(final ObjectProvider ownerOP, final Object[] elements) {
        final ApiAdapter api = ownerOP.getExecutionContext().getApiAdapter();
        for (int i = 0; i < elements.length; ++i) {
            if (api.isPersistable(elements[i])) {
                ownerOP.getExecutionContext().refreshObject(elements[i]);
            }
        }
    }
    
    public static void refreshFetchPlanFieldsForMap(final ObjectProvider ownerOP, final Set entries) {
        final ApiAdapter api = ownerOP.getExecutionContext().getApiAdapter();
        for (final Map.Entry entry : entries) {
            final Object val = entry.getValue();
            final Object key = entry.getKey();
            if (api.isPersistable(key)) {
                ownerOP.getExecutionContext().refreshObject(key);
            }
            if (api.isPersistable(val)) {
                ownerOP.getExecutionContext().refreshObject(val);
            }
        }
    }
    
    public static void detachForCollection(final ObjectProvider ownerOP, final Object[] elements, final FetchPlanState state) {
        final ApiAdapter api = ownerOP.getExecutionContext().getApiAdapter();
        for (int i = 0; i < elements.length; ++i) {
            if (api.isPersistable(elements[i])) {
                ownerOP.getExecutionContext().detachObject(elements[i], state);
            }
        }
    }
    
    public static void detachCopyForCollection(final ObjectProvider ownerOP, final Object[] elements, final FetchPlanState state, final Collection detached) {
        final ApiAdapter api = ownerOP.getExecutionContext().getApiAdapter();
        for (int i = 0; i < elements.length; ++i) {
            if (elements[i] == null) {
                detached.add(null);
            }
            else {
                final Object object = elements[i];
                if (api.isPersistable(object)) {
                    detached.add(ownerOP.getExecutionContext().detachObjectCopy(object, state));
                }
                else {
                    detached.add(object);
                }
            }
        }
    }
    
    public static void attachForCollection(final ObjectProvider ownerOP, final Object[] elements, final boolean elementsWithoutIdentity) {
        final ExecutionContext ec = ownerOP.getExecutionContext();
        final ApiAdapter api = ec.getApiAdapter();
        for (int i = 0; i < elements.length; ++i) {
            if (api.isPersistable(elements[i])) {
                final Object attached = ec.getAttachedObjectForId(api.getIdForObject(elements[i]));
                if (attached == null) {
                    ec.attachObject(ownerOP, elements[i], elementsWithoutIdentity);
                }
            }
        }
    }
    
    public static void detachForMap(final ObjectProvider ownerOP, final Set entries, final FetchPlanState state) {
        final ApiAdapter api = ownerOP.getExecutionContext().getApiAdapter();
        for (final Map.Entry entry : entries) {
            final Object val = entry.getValue();
            final Object key = entry.getKey();
            if (api.isPersistable(key)) {
                ownerOP.getExecutionContext().detachObject(key, state);
            }
            if (api.isPersistable(val)) {
                ownerOP.getExecutionContext().detachObject(val, state);
            }
        }
    }
    
    public static void detachCopyForMap(final ObjectProvider ownerOP, final Set entries, final FetchPlanState state, final Map detached) {
        final ApiAdapter api = ownerOP.getExecutionContext().getApiAdapter();
        for (final Map.Entry entry : entries) {
            Object val = entry.getValue();
            Object key = entry.getKey();
            if (api.isPersistable(val)) {
                val = ownerOP.getExecutionContext().detachObjectCopy(val, state);
            }
            if (api.isPersistable(key)) {
                key = ownerOP.getExecutionContext().detachObjectCopy(key, state);
            }
            detached.put(key, val);
        }
    }
    
    public static void attachForMap(final ObjectProvider ownerOP, final Set entries, final boolean keysWithoutIdentity, final boolean valuesWithoutIdentity) {
        final ExecutionContext ec = ownerOP.getExecutionContext();
        final ApiAdapter api = ec.getApiAdapter();
        for (final Map.Entry entry : entries) {
            final Object val = entry.getValue();
            final Object key = entry.getKey();
            if (api.isPersistable(key)) {
                final Object attached = ec.getAttachedObjectForId(api.getIdForObject(key));
                if (attached == null) {
                    ownerOP.getExecutionContext().attachObject(ownerOP, key, keysWithoutIdentity);
                }
            }
            if (api.isPersistable(val)) {
                final Object attached = ec.getAttachedObjectForId(api.getIdForObject(val));
                if (attached != null) {
                    continue;
                }
                ownerOP.getExecutionContext().attachObject(ownerOP, val, valuesWithoutIdentity);
            }
        }
    }
    
    public static boolean validateObjectForWriting(final ExecutionContext ec, final Object object, final FieldValues fieldValues) {
        boolean persisted = false;
        final ApiAdapter api = ec.getApiAdapter();
        if (api.isPersistable(object)) {
            final ExecutionContext objectEC = api.getExecutionContext(object);
            if (objectEC != null && ec != objectEC) {
                throw new NucleusUserException(SCOUtils.LOCALISER.msg("023009", StringUtils.toJVMIDString(object)), api.getIdForObject(object));
            }
            if (!api.isPersistent(object)) {
                boolean exists = false;
                if (api.isDetached(object)) {
                    if (ec.getNucleusContext().getPersistenceConfiguration().getBooleanProperty("datanucleus.attachSameDatastore")) {
                        exists = true;
                    }
                    else {
                        try {
                            final Object obj = ec.findObject(api.getIdForObject(object), true, false, object.getClass().getName());
                            if (obj != null) {
                                final ObjectProvider objSM = ec.findObjectProvider(obj);
                                if (objSM != null) {
                                    ec.evictFromTransaction(objSM);
                                }
                            }
                            exists = true;
                        }
                        catch (NucleusObjectNotFoundException onfe) {
                            exists = false;
                        }
                    }
                }
                if (!exists) {
                    ec.persistObjectInternal(object, fieldValues, 0);
                    persisted = true;
                }
            }
            else {
                final ObjectProvider objectSM = ec.findObjectProvider(object);
                if (objectSM.isWaitingToBeFlushedToDatastore()) {
                    if (fieldValues != null) {
                        objectSM.loadFieldValues(fieldValues);
                    }
                    objectSM.flush();
                    persisted = true;
                }
            }
        }
        return persisted;
    }
    
    public static void validateObjectsForWriting(final ExecutionContext ec, final Object objects) {
        if (objects != null) {
            if (objects.getClass().isArray()) {
                if (!objects.getClass().getComponentType().isPrimitive()) {
                    final Object[] obj = (Object[])objects;
                    for (int i = 0; i < obj.length; ++i) {
                        validateObjectForWriting(ec, obj[i], null);
                    }
                }
            }
            else if (objects instanceof Collection) {
                final Collection col = (Collection)objects;
                final Iterator it = col.iterator();
                while (it.hasNext()) {
                    validateObjectForWriting(ec, it.next(), null);
                }
            }
        }
    }
    
    public static boolean isListBased(final Class type) {
        return type != null && (List.class.isAssignableFrom(type) || Queue.class.isAssignableFrom(type));
    }
    
    public static Class getContainerInstanceType(final Class declaredType, final Boolean ordered) {
        if (!declaredType.isInterface()) {
            return declaredType;
        }
        if (List.class.isAssignableFrom(declaredType)) {
            return ArrayList.class;
        }
        if (Set.class.isAssignableFrom(declaredType)) {
            return HashSet.class;
        }
        if (Map.class.isAssignableFrom(declaredType)) {
            return HashMap.class;
        }
        if (ordered) {
            return ArrayList.class;
        }
        return HashSet.class;
    }
    
    public static boolean detachAsWrapped(final ObjectProvider ownerOP) {
        return ownerOP.getExecutionContext().getNucleusContext().getPersistenceConfiguration().getBooleanProperty("datanucleus.detachAsWrapped");
    }
    
    public static boolean useQueuedUpdate(final boolean queued, final ObjectProvider op) {
        return queued && !op.getExecutionContext().isFlushing() && op.getExecutionContext().getTransaction().isActive();
    }
    
    public static boolean hasDependentElement(final AbstractMemberMetaData mmd) {
        return !collectionHasElementsWithoutIdentity(mmd) && mmd.getCollection() != null && mmd.getCollection().isDependentElement();
    }
    
    public static boolean hasDependentKey(final AbstractMemberMetaData mmd) {
        return !mapHasKeysWithoutIdentity(mmd) && mmd.getMap() != null && mmd.getMap().isDependentKey();
    }
    
    public static boolean hasDependentValue(final AbstractMemberMetaData mmd) {
        return !mapHasValuesWithoutIdentity(mmd) && mmd.getMap() != null && mmd.getMap().isDependentValue();
    }
    
    public static boolean collectionsAreEqual(final ApiAdapter api, final Collection oldColl, final Collection newColl) {
        if (oldColl == null && newColl == null) {
            return true;
        }
        if (oldColl == null || newColl == null) {
            return false;
        }
        if (oldColl.size() != newColl.size()) {
            return false;
        }
        final Iterator oldIter = oldColl.iterator();
        final Iterator newIter = newColl.iterator();
        while (oldIter.hasNext()) {
            final Object oldVal = oldIter.next();
            final Object newVal = newIter.next();
            if (oldVal == null && newVal == null) {
                continue;
            }
            if (oldVal == null || newVal == null) {
                return false;
            }
            if (api.isPersistable(oldVal)) {
                final Object oldId = api.getIdForObject(oldVal);
                final Object newId = api.getIdForObject(newVal);
                if (oldId == null || newId == null) {
                    return false;
                }
                if (!oldId.equals(newId)) {
                    return false;
                }
                continue;
            }
            else {
                if (!oldVal.equals(newVal)) {
                    return false;
                }
                continue;
            }
        }
        return true;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
