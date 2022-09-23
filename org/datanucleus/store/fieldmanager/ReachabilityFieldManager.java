// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.fieldmanager;

import org.datanucleus.ClassConstants;
import java.util.Iterator;
import org.datanucleus.api.ApiAdapter;
import java.util.Map;
import java.util.Collection;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.metadata.AbstractMemberMetaData;
import java.util.Set;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.util.Localiser;

public class ReachabilityFieldManager extends AbstractFieldManager
{
    protected static final Localiser LOCALISER;
    private final ObjectProvider op;
    private Set reachables;
    
    public ReachabilityFieldManager(final ObjectProvider op, final Set reachables) {
        this.reachables = null;
        this.op = op;
        this.reachables = reachables;
    }
    
    protected void processPersistable(final Object obj, final AbstractMemberMetaData mmd) {
        final ObjectProvider objOP = this.op.getExecutionContext().findObjectProvider(obj);
        if (objOP != null) {
            objOP.runReachability(this.reachables);
        }
        else if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(ReachabilityFieldManager.LOCALISER.msg("007005", this.op.getExecutionContext().getApiAdapter().getIdForObject(obj), mmd.getFullFieldName()));
        }
    }
    
    @Override
    public void storeObjectField(final int fieldNumber, final Object value) {
        final AbstractMemberMetaData mmd = this.op.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
        if (value != null) {
            final boolean persistCascade = mmd.isCascadePersist();
            final RelationType relType = mmd.getRelationType(this.op.getExecutionContext().getClassLoaderResolver());
            final ApiAdapter api = this.op.getExecutionContext().getApiAdapter();
            if (persistCascade) {
                if (RelationType.isRelationSingleValued(relType)) {
                    if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                        NucleusLogger.PERSISTENCE.debug(ReachabilityFieldManager.LOCALISER.msg("007004", mmd.getFullFieldName()));
                    }
                    this.processPersistable(value, mmd);
                }
                else if (RelationType.isRelationMultiValued(relType)) {
                    if (value instanceof Collection) {
                        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                            NucleusLogger.PERSISTENCE.debug(ReachabilityFieldManager.LOCALISER.msg("007002", mmd.getFullFieldName()));
                        }
                        final Collection coll = (Collection)value;
                        for (final Object element : coll) {
                            if (api.isPersistable(element)) {
                                this.processPersistable(element, mmd);
                            }
                        }
                    }
                    else if (value instanceof Map) {
                        final Map map = (Map)value;
                        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                            NucleusLogger.PERSISTENCE.debug(ReachabilityFieldManager.LOCALISER.msg("007002", mmd.getFullFieldName()));
                        }
                        final Set keys = map.keySet();
                        for (final Object mapKey : keys) {
                            if (api.isPersistable(mapKey)) {
                                this.processPersistable(mapKey, mmd);
                            }
                        }
                        final Collection values = map.values();
                        for (final Object mapValue : values) {
                            if (api.isPersistable(mapValue)) {
                                this.processPersistable(mapValue, mmd);
                            }
                        }
                    }
                    else if (value instanceof Object[]) {
                        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                            NucleusLogger.PERSISTENCE.debug(ReachabilityFieldManager.LOCALISER.msg("007003", mmd.getFullFieldName()));
                        }
                        final Object[] array = (Object[])value;
                        for (int i = 0; i < array.length; ++i) {
                            final Object element = array[i];
                            if (api.isPersistable(element)) {
                                this.processPersistable(element, mmd);
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
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
