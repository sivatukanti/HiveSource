// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.scostore;

import org.datanucleus.store.rdbms.table.JoinTable;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.table.Table;
import java.sql.SQLException;
import org.datanucleus.store.rdbms.exceptions.MappedDatastoreException;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import org.datanucleus.store.rdbms.mapping.java.EmbeddedElementPCMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedReferenceMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedPCMapping;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.query.ResultObjectFactory;
import java.sql.ResultSet;
import org.datanucleus.ExecutionContext;
import org.datanucleus.state.ObjectProvider;
import java.util.Iterator;

class SetStoreIterator implements Iterator
{
    private final AbstractSetStore abstractSetStore;
    private final ObjectProvider op;
    private final ExecutionContext ec;
    private final Iterator delegate;
    private Object lastElement;
    
    SetStoreIterator(final ObjectProvider op, final ResultSet rs, final ResultObjectFactory rof, final AbstractSetStore setStore) throws MappedDatastoreException {
        this.lastElement = null;
        this.op = op;
        this.ec = op.getExecutionContext();
        this.abstractSetStore = setStore;
        final ArrayList results = new ArrayList();
        if (rs != null) {
            while (this.next(rs)) {
                Object nextElement;
                if (this.abstractSetStore.elementsAreEmbedded || this.abstractSetStore.elementsAreSerialised) {
                    final int[] param = new int[this.abstractSetStore.elementMapping.getNumberOfDatastoreMappings()];
                    for (int i = 0; i < param.length; ++i) {
                        param[i] = i + 1;
                    }
                    if (this.abstractSetStore.elementMapping instanceof SerialisedPCMapping || this.abstractSetStore.elementMapping instanceof SerialisedReferenceMapping || this.abstractSetStore.elementMapping instanceof EmbeddedElementPCMapping) {
                        int ownerFieldNumber = -1;
                        if (this.abstractSetStore.containerTable != null) {
                            ownerFieldNumber = this.getOwnerMemberMetaData(this.abstractSetStore.containerTable).getAbsoluteFieldNumber();
                        }
                        nextElement = this.abstractSetStore.elementMapping.getObject(this.ec, rs, param, op, ownerFieldNumber);
                    }
                    else {
                        nextElement = this.abstractSetStore.elementMapping.getObject(this.ec, rs, param);
                    }
                }
                else if (this.abstractSetStore.elementMapping instanceof ReferenceMapping) {
                    final int[] param = new int[this.abstractSetStore.elementMapping.getNumberOfDatastoreMappings()];
                    for (int i = 0; i < param.length; ++i) {
                        param[i] = i + 1;
                    }
                    nextElement = this.abstractSetStore.elementMapping.getObject(this.ec, rs, param);
                }
                else {
                    nextElement = rof.getObject(this.ec, rs);
                }
                results.add(nextElement);
            }
        }
        this.delegate = results.iterator();
    }
    
    @Override
    public boolean hasNext() {
        return this.delegate.hasNext();
    }
    
    @Override
    public Object next() {
        return this.lastElement = this.delegate.next();
    }
    
    @Override
    public synchronized void remove() {
        if (this.lastElement == null) {
            throw new IllegalStateException("No entry to remove");
        }
        this.abstractSetStore.remove(this.op, this.lastElement, -1, true);
        this.delegate.remove();
        this.lastElement = null;
    }
    
    protected boolean next(final Object rs) throws MappedDatastoreException {
        try {
            return ((ResultSet)rs).next();
        }
        catch (SQLException e) {
            throw new MappedDatastoreException("SQLException", e);
        }
    }
    
    protected AbstractMemberMetaData getOwnerMemberMetaData(final Table containerTable) {
        return ((JoinTable)containerTable).getOwnerMemberMetaData();
    }
}
