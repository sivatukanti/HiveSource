// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.cache;

import org.datanucleus.store.FieldValues;
import org.datanucleus.identity.IdentityUtils;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import java.util.Calendar;
import java.util.Date;
import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import org.datanucleus.store.fieldmanager.FieldManager;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.metadata.MetaDataUtils;
import org.datanucleus.exceptions.NucleusException;
import java.lang.reflect.Array;
import java.util.Map;
import org.datanucleus.util.NucleusLogger;
import java.util.ArrayList;
import java.util.Collection;
import org.datanucleus.metadata.RelationType;
import java.util.Iterator;
import java.util.List;
import org.datanucleus.ExecutionContext;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.fieldmanager.AbstractFieldManager;

public class L2CacheRetrieveFieldManager extends AbstractFieldManager
{
    ObjectProvider op;
    ExecutionContext ec;
    CachedPC cachedPC;
    List<Integer> fieldsNotLoaded;
    
    public L2CacheRetrieveFieldManager(final ObjectProvider op, final CachedPC cachedpc) {
        this.fieldsNotLoaded = null;
        this.op = op;
        this.ec = op.getExecutionContext();
        this.cachedPC = cachedpc;
    }
    
    public int[] getFieldsNotLoaded() {
        if (this.fieldsNotLoaded == null) {
            return null;
        }
        final int[] flds = new int[this.fieldsNotLoaded.size()];
        int i = 0;
        for (final Integer fldNum : this.fieldsNotLoaded) {
            flds[i++] = fldNum;
        }
        return flds;
    }
    
    @Override
    public boolean fetchBooleanField(final int fieldNumber) {
        return (boolean)this.cachedPC.getFieldValue(fieldNumber);
    }
    
    @Override
    public byte fetchByteField(final int fieldNumber) {
        return (byte)this.cachedPC.getFieldValue(fieldNumber);
    }
    
    @Override
    public char fetchCharField(final int fieldNumber) {
        return (char)this.cachedPC.getFieldValue(fieldNumber);
    }
    
    @Override
    public double fetchDoubleField(final int fieldNumber) {
        return (double)this.cachedPC.getFieldValue(fieldNumber);
    }
    
    @Override
    public float fetchFloatField(final int fieldNumber) {
        return (float)this.cachedPC.getFieldValue(fieldNumber);
    }
    
    @Override
    public int fetchIntField(final int fieldNumber) {
        return (int)this.cachedPC.getFieldValue(fieldNumber);
    }
    
    @Override
    public long fetchLongField(final int fieldNumber) {
        return (long)this.cachedPC.getFieldValue(fieldNumber);
    }
    
    @Override
    public short fetchShortField(final int fieldNumber) {
        return (short)this.cachedPC.getFieldValue(fieldNumber);
    }
    
    @Override
    public String fetchStringField(final int fieldNumber) {
        return (String)this.cachedPC.getFieldValue(fieldNumber);
    }
    
    @Override
    public Object fetchObjectField(final int fieldNumber) {
        final AbstractMemberMetaData mmd = this.op.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
        Object value = this.cachedPC.getFieldValue(fieldNumber);
        final RelationType relType = mmd.getRelationType(this.ec.getClassLoaderResolver());
        if (relType != RelationType.NONE) {
            if (value != null) {
                if (Collection.class.isAssignableFrom(value.getClass())) {
                    final Collection coll = (Collection)value;
                    try {
                        final Collection fieldColl = (Collection)coll.getClass().newInstance();
                        for (final Object cachedId : coll) {
                            if (cachedId != null) {
                                fieldColl.add(this.getObjectFromCachedId(cachedId));
                            }
                            else {
                                fieldColl.add(null);
                            }
                        }
                        return this.op.wrapSCOField(fieldNumber, fieldColl, false, false, true);
                    }
                    catch (Exception e) {
                        if (this.fieldsNotLoaded == null) {
                            this.fieldsNotLoaded = new ArrayList<Integer>();
                        }
                        this.fieldsNotLoaded.add(fieldNumber);
                        NucleusLogger.CACHE.error("Exception thrown creating value for field " + mmd.getFullFieldName() + " of type " + value.getClass().getName(), e);
                        return null;
                    }
                }
                if (Map.class.isAssignableFrom(value.getClass())) {
                    final Map map = (Map)value;
                    try {
                        final Map fieldMap = (Map)map.getClass().newInstance();
                        for (final Map.Entry entry : map.entrySet()) {
                            Object mapKey = null;
                            if (mmd.getMap().keyIsPersistent()) {
                                mapKey = this.getObjectFromCachedId(entry.getKey());
                            }
                            else {
                                mapKey = entry.getKey();
                            }
                            Object mapValue = null;
                            final Object mapValueId = entry.getValue();
                            if (mapValueId != null) {
                                if (mmd.getMap().valueIsPersistent()) {
                                    mapValue = this.getObjectFromCachedId(entry.getValue());
                                }
                                else {
                                    mapValue = entry.getValue();
                                }
                            }
                            fieldMap.put(mapKey, mapValue);
                        }
                        return this.op.wrapSCOField(fieldNumber, fieldMap, false, false, true);
                    }
                    catch (Exception e) {
                        if (this.fieldsNotLoaded == null) {
                            this.fieldsNotLoaded = new ArrayList<Integer>();
                        }
                        this.fieldsNotLoaded.add(fieldNumber);
                        NucleusLogger.CACHE.error("Exception thrown creating value for field " + mmd.getFullFieldName() + " of type " + value.getClass().getName(), e);
                        return null;
                    }
                }
                if (value.getClass().isArray()) {
                    try {
                        final Object[] elementOIDs = (Object[])value;
                        final Class componentType = mmd.getType().getComponentType();
                        final Object fieldArr = Array.newInstance(componentType, elementOIDs.length);
                        final boolean persistableElement = this.ec.getApiAdapter().isPersistable(componentType);
                        for (int i = 0; i < elementOIDs.length; ++i) {
                            Object element = null;
                            if (elementOIDs[i] != null) {
                                if (componentType.isInterface() || persistableElement || componentType == Object.class) {
                                    element = this.getObjectFromCachedId(elementOIDs[i]);
                                }
                                else {
                                    element = elementOIDs[i];
                                }
                            }
                            Array.set(fieldArr, i, element);
                        }
                        return fieldArr;
                    }
                    catch (NucleusException ne) {
                        if (this.fieldsNotLoaded == null) {
                            this.fieldsNotLoaded = new ArrayList<Integer>();
                        }
                        this.fieldsNotLoaded.add(fieldNumber);
                        NucleusLogger.CACHE.error("Exception thrown trying to find element of array while getting object with id " + this.op.getInternalObjectId() + " from the L2 cache", ne);
                        return null;
                    }
                }
                if ((mmd.isSerialized() || MetaDataUtils.isMemberEmbedded(mmd, relType, this.ec.getClassLoaderResolver(), this.ec.getMetaDataManager())) && this.ec.getNucleusContext().getPersistenceConfiguration().getBooleanProperty("datanucleus.cache.level2.cacheEmbedded") && value instanceof CachedPC) {
                    final CachedPC valueCachedPC = (CachedPC)value;
                    final AbstractClassMetaData cmd = this.ec.getMetaDataManager().getMetaDataForClass(valueCachedPC.getObjectClass(), this.ec.getClassLoaderResolver());
                    final int[] fieldsToLoad = ClassUtils.getFlagsSetTo(valueCachedPC.getLoadedFields(), cmd.getAllMemberPositions(), true);
                    final ObjectProvider valueOP = this.ec.newObjectProviderForEmbedded(cmd, this.op, mmd.getAbsoluteFieldNumber());
                    valueOP.replaceFields(fieldsToLoad, new L2CacheRetrieveFieldManager(valueOP, valueCachedPC));
                    return valueOP.getObject();
                }
                try {
                    return this.getObjectFromCachedId(value);
                }
                catch (NucleusObjectNotFoundException nonfe) {
                    if (this.fieldsNotLoaded == null) {
                        this.fieldsNotLoaded = new ArrayList<Integer>();
                    }
                    this.fieldsNotLoaded.add(fieldNumber);
                    return null;
                }
            }
            return null;
        }
        if (value == null) {
            return null;
        }
        if (value instanceof StringBuffer) {
            return new StringBuffer(((StringBuffer)value).toString());
        }
        if (value instanceof Date) {
            value = ((Date)value).clone();
        }
        else if (value instanceof Calendar) {
            value = ((Calendar)value).clone();
        }
        final boolean[] mutables = mmd.getAbstractClassMetaData().getSCOMutableMemberFlags();
        if (mutables[fieldNumber]) {
            return this.op.wrapSCOField(fieldNumber, value, false, false, true);
        }
        return value;
    }
    
    private Object getObjectFromCachedId(final Object cachedId) {
        Object pcId = null;
        String pcClassName = null;
        if (cachedId instanceof CachedPC.CachedId) {
            final CachedPC.CachedId cId = (CachedPC.CachedId)cachedId;
            pcId = cId.getId();
            pcClassName = cId.getClassName();
        }
        else {
            pcId = cachedId;
            pcClassName = IdentityUtils.getClassNameForIdentitySimple(this.ec.getApiAdapter(), pcId);
        }
        final Class pcCls = this.ec.getClassLoaderResolver().classForName(pcClassName);
        return this.ec.findObject(pcId, null, pcCls, false, false);
    }
}
