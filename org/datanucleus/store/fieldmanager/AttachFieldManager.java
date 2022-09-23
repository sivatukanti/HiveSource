// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.fieldmanager;

import org.datanucleus.ClassConstants;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.AbstractClassMetaData;
import java.lang.reflect.Array;
import java.util.Set;
import java.util.Map;
import org.datanucleus.store.types.SCOContainer;
import org.datanucleus.util.StringUtils;
import org.datanucleus.store.types.SCO;
import org.datanucleus.store.types.SCOUtils;
import java.util.Collection;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.util.Localiser;

public class AttachFieldManager extends AbstractFieldManager
{
    protected static final Localiser LOCALISER;
    private final ObjectProvider attachedOP;
    private final boolean[] secondClassMutableFields;
    private final boolean[] dirtyFields;
    private final boolean persistent;
    private final boolean cascadeAttach;
    boolean copy;
    
    public AttachFieldManager(final ObjectProvider attachedOP, final boolean[] secondClassMutableFields, final boolean[] dirtyFields, final boolean persistent, final boolean cascadeAttach, final boolean copy) {
        this.copy = true;
        this.attachedOP = attachedOP;
        this.secondClassMutableFields = secondClassMutableFields;
        this.dirtyFields = dirtyFields;
        this.persistent = persistent;
        this.cascadeAttach = cascadeAttach;
        this.copy = copy;
    }
    
    @Override
    public void storeObjectField(final int fieldNumber, Object value) {
        final AbstractClassMetaData cmd = this.attachedOP.getClassMetaData();
        final AbstractMemberMetaData mmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
        final ExecutionContext ec = this.attachedOP.getExecutionContext();
        final RelationType relationType = mmd.getRelationType(ec.getClassLoaderResolver());
        if (mmd.hasExtension("attach") && mmd.getValueForExtension("attach").equalsIgnoreCase("never")) {
            this.attachedOP.replaceFieldMakeDirty(fieldNumber, null);
            return;
        }
        final ApiAdapter api = ec.getApiAdapter();
        if (value == null) {
            Object oldValue = null;
            if (mmd.isDependent() && this.persistent) {
                try {
                    this.attachedOP.loadFieldFromDatastore(fieldNumber);
                }
                catch (Exception ex) {}
                oldValue = this.attachedOP.provideField(fieldNumber);
            }
            this.attachedOP.replaceField(fieldNumber, null);
            if (this.dirtyFields[fieldNumber] || !this.persistent) {
                this.attachedOP.makeDirty(fieldNumber);
            }
            if (mmd.isDependent() && !mmd.isEmbedded() && oldValue != null && api.isPersistable(oldValue)) {
                this.attachedOP.flush();
                NucleusLogger.PERSISTENCE.debug(AttachFieldManager.LOCALISER.msg("026026", oldValue, mmd.getFullFieldName()));
                ec.deleteObjectInternal(oldValue);
            }
        }
        else if (this.secondClassMutableFields[fieldNumber]) {
            if (mmd.isSerialized()) {
                this.attachedOP.replaceFieldMakeDirty(fieldNumber, value);
                this.attachedOP.makeDirty(fieldNumber);
            }
            else {
                Object oldValue = null;
                if (this.persistent && !this.attachedOP.isFieldLoaded(fieldNumber)) {
                    this.attachedOP.loadField(fieldNumber);
                }
                oldValue = this.attachedOP.provideField(fieldNumber);
                boolean changed = this.dirtyFields[fieldNumber];
                if (!changed) {
                    if (oldValue == null) {
                        changed = true;
                    }
                    else if (mmd.hasCollection() && relationType != RelationType.NONE) {
                        final boolean collsEqual = SCOUtils.collectionsAreEqual(api, (Collection)oldValue, (Collection)value);
                        changed = !collsEqual;
                    }
                    else {
                        changed = !oldValue.equals(value);
                    }
                }
                SCO sco;
                if (oldValue == null || !(oldValue instanceof SCO)) {
                    if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                        NucleusLogger.PERSISTENCE.debug(AttachFieldManager.LOCALISER.msg("026029", StringUtils.toJVMIDString(this.attachedOP.getObject()), this.attachedOP.getInternalObjectId(), mmd.getName()));
                    }
                    sco = SCOUtils.newSCOInstance(this.attachedOP, mmd, mmd.getType(), null, null, false, false, false);
                    if (sco instanceof SCOContainer) {
                        ((SCOContainer)sco).load();
                    }
                    this.attachedOP.replaceFieldMakeDirty(fieldNumber, sco);
                }
                else {
                    sco = (SCO)oldValue;
                }
                if (this.cascadeAttach) {
                    if (this.copy) {
                        sco.attachCopy(value);
                    }
                    else if (sco instanceof Collection) {
                        SCOUtils.attachForCollection(this.attachedOP, ((Collection)value).toArray(), SCOUtils.collectionHasElementsWithoutIdentity(mmd));
                    }
                    else if (sco instanceof Map) {
                        SCOUtils.attachForMap(this.attachedOP, ((Map)value).entrySet(), SCOUtils.mapHasKeysWithoutIdentity(mmd), SCOUtils.mapHasValuesWithoutIdentity(mmd));
                    }
                    else {
                        sco.initialise(value, false, false);
                    }
                }
                if (changed || !this.persistent) {
                    this.attachedOP.makeDirty(fieldNumber);
                }
            }
        }
        else if (mmd.getType().isArray() && RelationType.isRelationMultiValued(relationType)) {
            if (mmd.isSerialized() || mmd.isEmbedded()) {
                this.attachedOP.replaceField(fieldNumber, value);
                if (this.dirtyFields[fieldNumber] || !this.persistent) {
                    this.attachedOP.makeDirty(fieldNumber);
                }
            }
            else {
                Object oldValue = this.attachedOP.provideField(fieldNumber);
                if (oldValue == null && !this.attachedOP.getLoadedFields()[fieldNumber] && this.persistent) {
                    this.attachedOP.loadField(fieldNumber);
                    oldValue = this.attachedOP.provideField(fieldNumber);
                }
                if (this.cascadeAttach) {
                    final Object arr = Array.newInstance(mmd.getType().getComponentType(), Array.getLength(value));
                    for (int i = 0; i < Array.getLength(value); ++i) {
                        final Object elem = Array.get(value, i);
                        if (this.copy) {
                            final Object elemAttached = ec.attachObjectCopy(this.attachedOP, elem, false);
                            Array.set(arr, i, elemAttached);
                        }
                        else {
                            ec.attachObject(this.attachedOP, elem, false);
                            Array.set(arr, i, elem);
                        }
                    }
                    this.attachedOP.replaceFieldMakeDirty(fieldNumber, arr);
                }
                if (this.dirtyFields[fieldNumber] || !this.persistent) {
                    this.attachedOP.makeDirty(fieldNumber);
                }
            }
        }
        else if (RelationType.isRelationSingleValued(relationType)) {
            final ObjectProvider valueSM = ec.findObjectProvider(value);
            if (valueSM != null && valueSM.getReferencedPC() != null && !api.isPersistent(value)) {
                if (this.dirtyFields[fieldNumber]) {
                    this.attachedOP.replaceFieldMakeDirty(fieldNumber, valueSM.getReferencedPC());
                }
                else {
                    this.attachedOP.replaceField(fieldNumber, valueSM.getReferencedPC());
                }
            }
            if (this.cascadeAttach) {
                final boolean sco2 = mmd.getEmbeddedMetaData() != null || mmd.isSerialized() || mmd.isEmbedded();
                if (this.copy) {
                    value = ec.attachObjectCopy(this.attachedOP, value, sco2);
                    if (sco2 || this.dirtyFields[fieldNumber]) {
                        this.attachedOP.replaceFieldMakeDirty(fieldNumber, value);
                    }
                    else {
                        this.attachedOP.replaceField(fieldNumber, value);
                    }
                }
                else {
                    ec.attachObject(this.attachedOP, value, sco2);
                }
                if (this.dirtyFields[fieldNumber] || !this.persistent) {
                    this.attachedOP.makeDirty(fieldNumber);
                }
                else if (sco2 && value != null && api.isDirty(value)) {
                    this.attachedOP.makeDirty(fieldNumber);
                }
            }
            else if (this.dirtyFields[fieldNumber] || !this.persistent) {
                this.attachedOP.makeDirty(fieldNumber);
            }
        }
        else {
            this.attachedOP.replaceField(fieldNumber, value);
            if (this.dirtyFields[fieldNumber] || !this.persistent) {
                this.attachedOP.makeDirty(fieldNumber);
            }
        }
    }
    
    @Override
    public void storeBooleanField(final int fieldNumber, final boolean value) {
        final SingleValueFieldManager sfv = new SingleValueFieldManager();
        sfv.storeBooleanField(fieldNumber, value);
        this.attachedOP.replaceFields(new int[] { fieldNumber }, sfv);
        if (this.dirtyFields[fieldNumber] || !this.persistent) {
            this.attachedOP.makeDirty(fieldNumber);
        }
    }
    
    @Override
    public void storeByteField(final int fieldNumber, final byte value) {
        final SingleValueFieldManager sfv = new SingleValueFieldManager();
        sfv.storeByteField(fieldNumber, value);
        this.attachedOP.replaceFields(new int[] { fieldNumber }, sfv);
        if (this.dirtyFields[fieldNumber] || !this.persistent) {
            this.attachedOP.makeDirty(fieldNumber);
        }
    }
    
    @Override
    public void storeCharField(final int fieldNumber, final char value) {
        final SingleValueFieldManager sfv = new SingleValueFieldManager();
        sfv.storeCharField(fieldNumber, value);
        this.attachedOP.replaceFields(new int[] { fieldNumber }, sfv);
        if (this.dirtyFields[fieldNumber] || !this.persistent) {
            this.attachedOP.makeDirty(fieldNumber);
        }
    }
    
    @Override
    public void storeDoubleField(final int fieldNumber, final double value) {
        final SingleValueFieldManager sfv = new SingleValueFieldManager();
        sfv.storeDoubleField(fieldNumber, value);
        this.attachedOP.replaceFields(new int[] { fieldNumber }, sfv);
        if (this.dirtyFields[fieldNumber] || !this.persistent) {
            this.attachedOP.makeDirty(fieldNumber);
        }
    }
    
    @Override
    public void storeFloatField(final int fieldNumber, final float value) {
        final SingleValueFieldManager sfv = new SingleValueFieldManager();
        sfv.storeFloatField(fieldNumber, value);
        this.attachedOP.replaceFields(new int[] { fieldNumber }, sfv);
        if (this.dirtyFields[fieldNumber] || !this.persistent) {
            this.attachedOP.makeDirty(fieldNumber);
        }
    }
    
    @Override
    public void storeIntField(final int fieldNumber, final int value) {
        final SingleValueFieldManager sfv = new SingleValueFieldManager();
        sfv.storeIntField(fieldNumber, value);
        this.attachedOP.replaceFields(new int[] { fieldNumber }, sfv);
        if (this.dirtyFields[fieldNumber] || !this.persistent) {
            this.attachedOP.makeDirty(fieldNumber);
        }
    }
    
    @Override
    public void storeLongField(final int fieldNumber, final long value) {
        final SingleValueFieldManager sfv = new SingleValueFieldManager();
        sfv.storeLongField(fieldNumber, value);
        this.attachedOP.replaceFields(new int[] { fieldNumber }, sfv);
        if (this.dirtyFields[fieldNumber] || !this.persistent) {
            this.attachedOP.makeDirty(fieldNumber);
        }
    }
    
    @Override
    public void storeShortField(final int fieldNumber, final short value) {
        final SingleValueFieldManager sfv = new SingleValueFieldManager();
        sfv.storeShortField(fieldNumber, value);
        this.attachedOP.replaceFields(new int[] { fieldNumber }, sfv);
        if (this.dirtyFields[fieldNumber] || !this.persistent) {
            this.attachedOP.makeDirty(fieldNumber);
        }
    }
    
    @Override
    public void storeStringField(final int fieldNumber, final String value) {
        final SingleValueFieldManager sfv = new SingleValueFieldManager();
        sfv.storeStringField(fieldNumber, value);
        this.attachedOP.replaceFields(new int[] { fieldNumber }, sfv);
        if (this.dirtyFields[fieldNumber] || !this.persistent) {
            this.attachedOP.makeDirty(fieldNumber);
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
