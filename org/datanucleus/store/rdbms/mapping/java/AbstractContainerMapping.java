// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import java.util.List;
import org.datanucleus.store.types.SCO;
import org.datanucleus.metadata.MetaDataUtils;
import org.datanucleus.store.types.SCOUtils;
import org.datanucleus.store.rdbms.exceptions.NoDatastoreMappingException;
import org.datanucleus.store.rdbms.mapping.datastore.DatastoreMapping;
import java.sql.ResultSet;
import java.util.Iterator;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.state.ObjectProvider;
import java.util.Map;
import java.util.HashSet;
import java.util.Collection;
import org.datanucleus.exceptions.NucleusException;
import java.sql.PreparedStatement;
import org.datanucleus.ExecutionContext;
import org.datanucleus.ClassNameConstants;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.AbstractMemberMetaData;

public abstract class AbstractContainerMapping extends SingleFieldMapping
{
    @Override
    public void initialize(final AbstractMemberMetaData mmd, final Table table, final ClassLoaderResolver clr) {
        super.initialize(mmd, table, clr);
        if (mmd.getContainer() == null) {
            throw new NucleusUserException(AbstractContainerMapping.LOCALISER_RDBMS.msg("041023", mmd.getFullFieldName()));
        }
        if (!this.containerIsStoredInSingleColumn()) {
            this.storeMgr.newJoinDatastoreContainerObject(mmd, clr);
        }
    }
    
    @Override
    public boolean hasSimpleDatastoreRepresentation() {
        return false;
    }
    
    @Override
    protected void prepareDatastoreMapping() {
        if (this.containerIsStoredInSingleColumn()) {
            super.prepareDatastoreMapping();
        }
    }
    
    @Override
    public String getJavaTypeForDatastoreMapping(final int index) {
        if (this.containerIsStoredInSingleColumn()) {
            return ClassNameConstants.JAVA_IO_SERIALIZABLE;
        }
        return super.getJavaTypeForDatastoreMapping(index);
    }
    
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final Object value) {
        if (this.mmd == null || !this.containerIsStoredInSingleColumn()) {
            throw new NucleusException(this.failureMessage("setObject")).setFatal();
        }
        ObjectProvider[] sms = null;
        final ApiAdapter api = ec.getApiAdapter();
        if (value != null) {
            Collection smsColl = null;
            if (value instanceof Collection) {
                for (final Object elem : (Collection)value) {
                    if (api.isPersistable(elem)) {
                        final ObjectProvider sm = ec.findObjectProvider(elem);
                        if (sm == null) {
                            continue;
                        }
                        if (smsColl == null) {
                            smsColl = new HashSet();
                        }
                        smsColl.add(sm);
                    }
                }
            }
            else if (value instanceof Map) {
                for (final Map.Entry entry : ((Map)value).entrySet()) {
                    final Object key = entry.getKey();
                    final Object val = entry.getValue();
                    if (api.isPersistable(key)) {
                        final ObjectProvider sm2 = ec.findObjectProvider(key);
                        if (sm2 != null) {
                            if (smsColl == null) {
                                smsColl = new HashSet();
                            }
                            smsColl.add(sm2);
                        }
                    }
                    if (api.isPersistable(val)) {
                        final ObjectProvider sm2 = ec.findObjectProvider(val);
                        if (sm2 == null) {
                            continue;
                        }
                        if (smsColl == null) {
                            smsColl = new HashSet();
                        }
                        smsColl.add(sm2);
                    }
                }
            }
            if (smsColl != null) {
                sms = smsColl.toArray(new ObjectProvider[smsColl.size()]);
            }
        }
        if (sms != null) {
            for (int i = 0; i < sms.length; ++i) {
                sms[i].setStoringPC();
            }
        }
        this.getDatastoreMapping(0).setObject(ps, exprIndex[0], value);
        if (sms != null) {
            for (int i = 0; i < sms.length; ++i) {
                sms[i].unsetStoringPC();
            }
        }
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        if (this.mmd == null || !this.containerIsStoredInSingleColumn()) {
            throw new NucleusException(this.failureMessage("getObject")).setFatal();
        }
        return this.getDatastoreMapping(0).getObject(resultSet, exprIndex[0]);
    }
    
    @Override
    public Table getTable() {
        if (this.containerIsStoredInSingleColumn()) {
            return this.table;
        }
        return null;
    }
    
    @Override
    public int getNumberOfDatastoreMappings() {
        if (this.containerIsStoredInSingleColumn()) {
            return super.getNumberOfDatastoreMappings();
        }
        return 0;
    }
    
    @Override
    public DatastoreMapping getDatastoreMapping(final int index) {
        if (this.containerIsStoredInSingleColumn()) {
            return super.getDatastoreMapping(index);
        }
        throw new NoDatastoreMappingException(this.mmd.getName());
    }
    
    @Override
    public DatastoreMapping[] getDatastoreMappings() {
        if (this.containerIsStoredInSingleColumn()) {
            return super.getDatastoreMappings();
        }
        throw new NoDatastoreMappingException(this.mmd.getName());
    }
    
    protected boolean containerIsStoredInSingleColumn() {
        return (this.mmd != null && this.mmd.isSerialized()) || (this.mmd != null && this.mmd.hasCollection() && SCOUtils.collectionHasSerialisedElements(this.mmd)) || (this.mmd != null && this.mmd.hasMap() && SCOUtils.mapHasSerialisedKeysAndValues(this.mmd)) || (this.mmd != null && this.mmd.hasArray() && SCOUtils.arrayIsStoredInSingleColumn(this.mmd, this.storeMgr.getMetaDataManager()) && !MetaDataUtils.getInstance().arrayStorableAsByteArrayInSingleColumn(this.mmd));
    }
    
    @Override
    public boolean includeInFetchStatement() {
        return this.containerIsStoredInSingleColumn();
    }
    
    @Override
    public boolean includeInUpdateStatement() {
        return this.containerIsStoredInSingleColumn();
    }
    
    @Override
    public boolean includeInInsertStatement() {
        return this.containerIsStoredInSingleColumn();
    }
    
    protected SCO replaceFieldWithWrapper(final ObjectProvider op, final Object value, final boolean forInsert, final boolean forUpdate) {
        Class type = this.mmd.getType();
        if (value != null) {
            type = value.getClass();
        }
        else if (this.mmd.getOrderMetaData() != null && type.isAssignableFrom(List.class)) {
            type = List.class;
        }
        final SCO sco = SCOUtils.newSCOInstance(op, this.mmd, this.mmd.getType(), type, value, forInsert, forUpdate, true);
        return sco;
    }
    
    public void postFetch(final ObjectProvider sm) {
        if (this.containerIsStoredInSingleColumn()) {
            return;
        }
        this.replaceFieldWithWrapper(sm, null, false, false);
    }
}
