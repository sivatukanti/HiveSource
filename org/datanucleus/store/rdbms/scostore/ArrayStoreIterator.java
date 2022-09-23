// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.scostore;

import org.datanucleus.store.rdbms.table.JoinTable;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.table.Table;
import java.sql.SQLException;
import org.datanucleus.store.rdbms.exceptions.MappedDatastoreException;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import org.datanucleus.store.rdbms.mapping.java.EmbeddedElementPCMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedReferenceMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedPCMapping;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.query.ResultObjectFactory;
import java.sql.ResultSet;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.ExecutionContext;
import java.util.Iterator;

public class ArrayStoreIterator implements Iterator
{
    private final ExecutionContext ec;
    private final Iterator delegate;
    private Object lastElement;
    
    ArrayStoreIterator(final ObjectProvider op, final ResultSet rs, final ResultObjectFactory rof, final ElementContainerStore backingStore) throws MappedDatastoreException {
        this.lastElement = null;
        this.ec = op.getExecutionContext();
        final ArrayList results = new ArrayList();
        if (rs != null) {
            final JavaTypeMapping elementMapping = backingStore.getElementMapping();
            while (this.next(rs)) {
                Object nextElement;
                if (backingStore.isElementsAreEmbedded() || backingStore.isElementsAreSerialised()) {
                    final int[] param = new int[elementMapping.getNumberOfDatastoreMappings()];
                    for (int i = 0; i < param.length; ++i) {
                        param[i] = i + 1;
                    }
                    if (elementMapping instanceof SerialisedPCMapping || elementMapping instanceof SerialisedReferenceMapping || elementMapping instanceof EmbeddedElementPCMapping) {
                        int ownerFieldNumber = -1;
                        if (backingStore.getContainerTable() != null) {
                            ownerFieldNumber = this.getOwnerFieldMetaData(backingStore.getContainerTable()).getAbsoluteFieldNumber();
                        }
                        nextElement = elementMapping.getObject(this.ec, rs, param, op, ownerFieldNumber);
                    }
                    else {
                        nextElement = elementMapping.getObject(this.ec, rs, param);
                    }
                }
                else if (elementMapping instanceof ReferenceMapping) {
                    final int[] param = new int[elementMapping.getNumberOfDatastoreMappings()];
                    for (int i = 0; i < param.length; ++i) {
                        param[i] = i + 1;
                    }
                    nextElement = elementMapping.getObject(this.ec, rs, param);
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
    }
    
    protected boolean next(final Object rs) throws MappedDatastoreException {
        try {
            return ((ResultSet)rs).next();
        }
        catch (SQLException e) {
            throw new MappedDatastoreException("SQLException", e);
        }
    }
    
    protected AbstractMemberMetaData getOwnerFieldMetaData(final Table containerTable) {
        return ((JoinTable)containerTable).getOwnerMemberMetaData();
    }
}
