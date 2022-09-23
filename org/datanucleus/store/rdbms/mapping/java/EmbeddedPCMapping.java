// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.EmbeddedMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.mapping.MappingCallbacks;

public class EmbeddedPCMapping extends EmbeddedMapping implements MappingCallbacks
{
    @Override
    public void initialize(final AbstractMemberMetaData mmd, final Table table, final ClassLoaderResolver clr) {
        this.initialize(mmd, table, clr, mmd.getEmbeddedMetaData(), mmd.getTypeName(), 1);
    }
    
    @Override
    public void insertPostProcessing(final ObjectProvider op) {
    }
    
    @Override
    public void postFetch(final ObjectProvider sm) {
        if (this.mmd.getAbsoluteFieldNumber() < 0) {
            return;
        }
        final ObjectProvider thisSM = this.getObjectProviderForEmbeddedObject(sm);
        if (thisSM == null) {
            return;
        }
        for (int i = 0; i < this.getNumberOfJavaTypeMappings(); ++i) {
            final JavaTypeMapping m = this.getJavaTypeMapping(i);
            if (m instanceof MappingCallbacks) {
                ((MappingCallbacks)m).postFetch(thisSM);
            }
        }
    }
    
    @Override
    public void postInsert(final ObjectProvider sm) {
        if (this.mmd.getAbsoluteFieldNumber() < 0) {
            return;
        }
        final ObjectProvider thisSM = this.getObjectProviderForEmbeddedObject(sm);
        if (thisSM == null) {
            return;
        }
        for (int i = 0; i < this.getNumberOfJavaTypeMappings(); ++i) {
            final JavaTypeMapping m = this.getJavaTypeMapping(i);
            if (m instanceof MappingCallbacks) {
                ((MappingCallbacks)m).postInsert(thisSM);
            }
        }
    }
    
    @Override
    public void postUpdate(final ObjectProvider sm) {
        if (this.mmd.getAbsoluteFieldNumber() < 0) {
            return;
        }
        final ObjectProvider thisSM = this.getObjectProviderForEmbeddedObject(sm);
        if (thisSM == null) {
            return;
        }
        for (int i = 0; i < this.getNumberOfJavaTypeMappings(); ++i) {
            final JavaTypeMapping m = this.getJavaTypeMapping(i);
            if (m instanceof MappingCallbacks) {
                ((MappingCallbacks)m).postUpdate(thisSM);
            }
        }
    }
    
    @Override
    public void preDelete(final ObjectProvider sm) {
        if (this.mmd.getAbsoluteFieldNumber() < 0) {
            return;
        }
        final ObjectProvider thisSM = this.getObjectProviderForEmbeddedObject(sm);
        if (thisSM == null) {
            return;
        }
        for (int i = 0; i < this.getNumberOfJavaTypeMappings(); ++i) {
            final JavaTypeMapping m = this.getJavaTypeMapping(i);
            if (m instanceof MappingCallbacks) {
                ((MappingCallbacks)m).preDelete(thisSM);
            }
        }
    }
    
    private ObjectProvider getObjectProviderForEmbeddedObject(final ObjectProvider ownerOP) {
        final ExecutionContext ec = ownerOP.getExecutionContext();
        AbstractMemberMetaData theMmd = this.mmd;
        if (this.mmd.getParent() instanceof EmbeddedMetaData) {
            final AbstractClassMetaData cmd = ec.getMetaDataManager().getMetaDataForClass(this.mmd.getClassName(), this.clr);
            theMmd = cmd.getMetaDataForMember(this.mmd.getName());
        }
        final Object value = ownerOP.provideField(theMmd.getAbsoluteFieldNumber());
        if (value == null) {
            return null;
        }
        ObjectProvider thisSM = ec.findObjectProvider(value);
        if (thisSM == null) {
            thisSM = ec.newObjectProviderForEmbedded(value, false, ownerOP, theMmd.getAbsoluteFieldNumber());
            thisSM.setPcObjectType(this.objectType);
        }
        return thisSM;
    }
}
