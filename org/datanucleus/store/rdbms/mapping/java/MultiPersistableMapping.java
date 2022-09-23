// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import org.datanucleus.store.rdbms.exceptions.NullValueException;
import org.datanucleus.identity.OIDFactory;
import org.datanucleus.identity.OID;
import java.sql.ResultSet;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.store.exceptions.NotYetFlushedException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.state.ObjectProvider;
import java.sql.PreparedStatement;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ExecutionContext;

public abstract class MultiPersistableMapping extends MultiMapping
{
    protected int getMappingNumberForValue(final ExecutionContext ec, final Object value) {
        if (value == null) {
            return -1;
        }
        final ClassLoaderResolver clr = ec.getClassLoaderResolver();
        for (int i = 0; i < this.javaTypeMappings.length; ++i) {
            final Class cls = clr.classForName(this.javaTypeMappings[i].getType());
            if (cls.isAssignableFrom(value.getClass())) {
                return i;
            }
        }
        Class mappingJavaType = null;
        final MetaDataManager mmgr = this.storeMgr.getNucleusContext().getMetaDataManager();
        boolean isPersistentInterface = mmgr.isPersistentInterface(this.getType());
        if (isPersistentInterface) {
            mappingJavaType = clr.classForName(this.getType());
        }
        else if (this.mmd != null && this.mmd.getFieldTypes() != null && this.mmd.getFieldTypes().length == 1) {
            isPersistentInterface = mmgr.isPersistentInterface(this.mmd.getFieldTypes()[0]);
            if (isPersistentInterface) {
                mappingJavaType = clr.classForName(this.mmd.getFieldTypes()[0]);
            }
        }
        if (mappingJavaType != null && mappingJavaType.isAssignableFrom(value.getClass())) {
            return -2;
        }
        return -1;
    }
    
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] pos, final Object value) {
        this.setObject(ec, ps, pos, value, null, -1);
    }
    
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] pos, final Object value, final ObjectProvider ownerOP, final int ownerFieldNumber) {
        boolean setValueFKOnly = false;
        if (pos != null && pos.length < this.getNumberOfDatastoreMappings()) {
            setValueFKOnly = true;
        }
        final int javaTypeMappingNumber = this.getMappingNumberForValue(ec, value);
        if (value != null && javaTypeMappingNumber == -1) {
            throw new ClassCastException(MultiPersistableMapping.LOCALISER_RDBMS.msg("041044", (this.mmd != null) ? this.mmd.getFullFieldName() : "", this.getType(), value.getClass().getName()));
        }
        if (value != null) {
            final ApiAdapter api = ec.getApiAdapter();
            final ClassLoaderResolver clr = ec.getClassLoaderResolver();
            if (!ec.isInserting(value)) {
                Object id = api.getIdForObject(value);
                boolean requiresPersisting = false;
                if (ec.getApiAdapter().isDetached(value) && ownerOP != null) {
                    requiresPersisting = true;
                }
                else if (id == null) {
                    requiresPersisting = true;
                }
                else {
                    final ExecutionContext valueEC = api.getExecutionContext(value);
                    if (valueEC != null && ec != valueEC) {
                        throw new NucleusUserException(MultiPersistableMapping.LOCALISER_RDBMS.msg("041015"), id);
                    }
                }
                if (requiresPersisting) {
                    final Object pcNew = ec.persistObjectInternal(value, null, -1, 0);
                    ec.flushInternal(false);
                    id = api.getIdForObject(pcNew);
                    if (ec.getApiAdapter().isDetached(value) && ownerOP != null) {
                        ownerOP.replaceFieldMakeDirty(ownerFieldNumber, pcNew);
                        final RelationType relationType = this.mmd.getRelationType(clr);
                        if (relationType == RelationType.ONE_TO_ONE_BI) {
                            final ObjectProvider relatedSM = ec.findObjectProvider(pcNew);
                            final AbstractMemberMetaData[] relatedMmds = this.mmd.getRelatedMemberMetaData(clr);
                            relatedSM.replaceFieldMakeDirty(relatedMmds[0].getAbsoluteFieldNumber(), ownerOP.getObject());
                        }
                        else if (relationType == RelationType.MANY_TO_ONE_BI && NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                            NucleusLogger.PERSISTENCE.debug("PCMapping.setObject : object " + ownerOP.getInternalObjectId() + " has field " + ownerFieldNumber + " that is 1-N bidirectional - should really update the reference in the relation. Not yet supported");
                        }
                    }
                }
                if (this.getNumberOfDatastoreMappings() <= 0) {
                    return;
                }
            }
        }
        if (pos == null) {
            return;
        }
        final ObjectProvider op = (value != null) ? ec.findObjectProvider(value) : null;
        try {
            if (op != null) {
                op.setStoringPC();
            }
            int n = 0;
            NotYetFlushedException notYetFlushed = null;
            for (int i = 0; i < this.javaTypeMappings.length; ++i) {
                if (setValueFKOnly) {
                    n = 0;
                }
                else if (n >= pos.length) {
                    n = 0;
                }
                int[] posMapping;
                if (this.javaTypeMappings[i].getReferenceMapping() != null) {
                    posMapping = new int[this.javaTypeMappings[i].getReferenceMapping().getNumberOfDatastoreMappings()];
                }
                else {
                    posMapping = new int[this.javaTypeMappings[i].getNumberOfDatastoreMappings()];
                }
                for (int j = 0; j < posMapping.length; ++j) {
                    posMapping[j] = pos[n++];
                }
                try {
                    if (javaTypeMappingNumber == -2 || (value != null && javaTypeMappingNumber == i)) {
                        this.javaTypeMappings[i].setObject(ec, ps, posMapping, value);
                    }
                    else if (!setValueFKOnly) {
                        this.javaTypeMappings[i].setObject(ec, ps, posMapping, null);
                    }
                }
                catch (NotYetFlushedException e) {
                    notYetFlushed = e;
                }
            }
            if (notYetFlushed != null) {
                throw notYetFlushed;
            }
        }
        finally {
            if (op != null) {
                op.unsetStoringPC();
            }
        }
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet rs, final int[] pos) {
        int n = 0;
        for (int i = 0; i < this.javaTypeMappings.length; ++i) {
            if (n >= pos.length) {
                n = 0;
            }
            int[] posMapping;
            if (this.javaTypeMappings[i].getReferenceMapping() != null) {
                posMapping = new int[this.javaTypeMappings[i].getReferenceMapping().getNumberOfDatastoreMappings()];
            }
            else {
                posMapping = new int[this.javaTypeMappings[i].getNumberOfDatastoreMappings()];
            }
            for (int j = 0; j < posMapping.length; ++j) {
                posMapping[j] = pos[n++];
            }
            Object value = null;
            try {
                value = this.javaTypeMappings[i].getObject(ec, rs, posMapping);
                if (value != null) {
                    if (value instanceof OID) {
                        Column col = null;
                        if (this.javaTypeMappings[i].getReferenceMapping() != null) {
                            col = this.javaTypeMappings[i].getReferenceMapping().getDatastoreMapping(0).getColumn();
                        }
                        else {
                            col = this.javaTypeMappings[i].getDatastoreMapping(0).getColumn();
                        }
                        final String className = col.getStoredJavaType();
                        value = OIDFactory.getInstance(ec.getNucleusContext(), className, ((OID)value).getKeyValue());
                        return ec.findObject(value, false, true, null);
                    }
                    if (ec.getClassLoaderResolver().classForName(this.getType()).isAssignableFrom(value.getClass())) {
                        return value;
                    }
                }
            }
            catch (NullValueException e) {}
            catch (NucleusObjectNotFoundException ex) {}
        }
        return null;
    }
}
