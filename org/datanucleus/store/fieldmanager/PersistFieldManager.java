// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.fieldmanager;

import java.util.Iterator;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.AbstractMemberMetaData;
import java.util.Map;
import java.util.List;
import java.util.Collection;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.store.types.SCO;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.state.ObjectProvider;

public class PersistFieldManager extends AbstractFieldManager
{
    private final ObjectProvider op;
    private final boolean replaceSCOsWithWrappers;
    
    public PersistFieldManager(final ObjectProvider op, final boolean replaceSCOsWithWrappers) {
        this.op = op;
        this.replaceSCOsWithWrappers = replaceSCOsWithWrappers;
    }
    
    protected Object processPersistable(final Object pc, final int ownerFieldNum, final int objectType) {
        final ApiAdapter adapter = this.op.getExecutionContext().getApiAdapter();
        if (adapter.isPersistent(pc) && (!adapter.isPersistent(pc) || !adapter.isDeleted(pc))) {
            return pc;
        }
        if (objectType != 0) {
            return this.op.getExecutionContext().persistObjectInternal(pc, this.op, ownerFieldNum, objectType);
        }
        return this.op.getExecutionContext().persistObjectInternal(pc, null, -1, objectType);
    }
    
    @Override
    public void storeObjectField(final int fieldNumber, Object value) {
        if (value != null) {
            final AbstractMemberMetaData mmd = this.op.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
            final boolean persistCascade = mmd.isCascadePersist();
            final ClassLoaderResolver clr = this.op.getExecutionContext().getClassLoaderResolver();
            final RelationType relationType = mmd.getRelationType(clr);
            if (this.replaceSCOsWithWrappers) {
                final boolean[] secondClassMutableFieldFlags = this.op.getClassMetaData().getSCOMutableMemberFlags();
                if (secondClassMutableFieldFlags[fieldNumber] && !(value instanceof SCO)) {
                    value = this.op.wrapSCOField(fieldNumber, value, false, true, true);
                }
            }
            if (persistCascade) {
                if (RelationType.isRelationSingleValued(relationType)) {
                    if (mmd.isEmbedded() || mmd.isSerialized()) {
                        this.processPersistable(value, fieldNumber, 1);
                    }
                    else {
                        this.processPersistable(value, -1, 0);
                    }
                }
                else if (RelationType.isRelationMultiValued(relationType)) {
                    final ApiAdapter api = this.op.getExecutionContext().getApiAdapter();
                    if (mmd.hasCollection()) {
                        final Collection coll = (Collection)value;
                        final Iterator iter = coll.iterator();
                        int position = 0;
                        while (iter.hasNext()) {
                            final Object element = iter.next();
                            if (api.isPersistable(element)) {
                                if (mmd.getCollection().isEmbeddedElement() || mmd.getCollection().isSerializedElement()) {
                                    this.processPersistable(element, fieldNumber, 2);
                                }
                                else {
                                    final Object newElement = this.processPersistable(element, -1, 0);
                                    final ObjectProvider elementSM = this.op.getExecutionContext().findObjectProvider(newElement);
                                    if (elementSM.getReferencedPC() != null) {
                                        if (coll instanceof List) {
                                            ((List)coll).set(position, newElement);
                                        }
                                        else {
                                            coll.remove(element);
                                            coll.add(newElement);
                                        }
                                    }
                                }
                            }
                            ++position;
                        }
                    }
                    else if (mmd.hasMap()) {
                        final Map map = (Map)value;
                        for (final Map.Entry entry : map.entrySet()) {
                            final Object mapKey = entry.getKey();
                            final Object mapValue = entry.getValue();
                            Object newMapKey = mapKey;
                            Object newMapValue = mapValue;
                            if (api.isPersistable(mapKey)) {
                                if (mmd.getMap().isEmbeddedKey() || mmd.getMap().isSerializedKey()) {
                                    this.processPersistable(mapKey, fieldNumber, 3);
                                }
                                else {
                                    newMapKey = this.processPersistable(mapKey, -1, 0);
                                }
                            }
                            if (api.isPersistable(mapValue)) {
                                if (mmd.getMap().isEmbeddedValue() || mmd.getMap().isSerializedValue()) {
                                    this.processPersistable(mapValue, fieldNumber, 4);
                                }
                                else {
                                    newMapValue = this.processPersistable(mapValue, -1, 0);
                                }
                            }
                            if (newMapKey != mapKey || newMapValue != mapValue) {
                                boolean updateKey = false;
                                boolean updateValue = false;
                                if (newMapKey != mapKey) {
                                    final ObjectProvider keySM = this.op.getExecutionContext().findObjectProvider(newMapKey);
                                    if (keySM.getReferencedPC() != null) {
                                        updateKey = true;
                                    }
                                }
                                if (newMapValue != mapValue) {
                                    final ObjectProvider valSM = this.op.getExecutionContext().findObjectProvider(newMapValue);
                                    if (valSM.getReferencedPC() != null) {
                                        updateValue = true;
                                    }
                                }
                                if (updateKey) {
                                    map.remove(mapKey);
                                    map.put(newMapKey, updateValue ? newMapValue : mapValue);
                                }
                                else {
                                    if (!updateValue) {
                                        continue;
                                    }
                                    map.put(mapKey, newMapValue);
                                }
                            }
                        }
                    }
                    else if (mmd.hasArray() && value instanceof Object[]) {
                        final Object[] array = (Object[])value;
                        for (int i = 0; i < array.length; ++i) {
                            final Object element2 = array[i];
                            if (api.isPersistable(element2)) {
                                if (mmd.getArray().isEmbeddedElement() || mmd.getArray().isSerializedElement()) {
                                    this.processPersistable(element2, fieldNumber, 2);
                                }
                                else {
                                    final Object processedElement = this.processPersistable(element2, -1, 0);
                                    final ObjectProvider elementSM2 = this.op.getExecutionContext().findObjectProvider(processedElement);
                                    if (elementSM2.getReferencedPC() != null) {
                                        array[i] = processedElement;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void storeBooleanField(final int fieldNumber, final boolean value) {
    }
    
    @Override
    public void storeByteField(final int fieldNumber, final byte value) {
    }
    
    @Override
    public void storeCharField(final int fieldNumber, final char value) {
    }
    
    @Override
    public void storeDoubleField(final int fieldNumber, final double value) {
    }
    
    @Override
    public void storeFloatField(final int fieldNumber, final float value) {
    }
    
    @Override
    public void storeIntField(final int fieldNumber, final int value) {
    }
    
    @Override
    public void storeLongField(final int fieldNumber, final long value) {
    }
    
    @Override
    public void storeShortField(final int fieldNumber, final short value) {
    }
    
    @Override
    public void storeStringField(final int fieldNumber, final String value) {
    }
}
