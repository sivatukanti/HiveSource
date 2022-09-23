// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.scostore;

import java.sql.SQLException;
import org.datanucleus.store.rdbms.table.JoinTable;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.exceptions.MappedDatastoreException;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import org.datanucleus.store.rdbms.mapping.java.EmbeddedElementPCMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedReferenceMapping;
import org.datanucleus.store.rdbms.mapping.java.SerialisedPCMapping;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.query.ResultObjectFactory;
import java.sql.ResultSet;
import org.datanucleus.state.ObjectProvider;
import java.util.ListIterator;

public class ListStoreIterator implements ListIterator
{
    private final ObjectProvider op;
    private final ListIterator delegate;
    private Object lastElement;
    private int currentIndex;
    private final AbstractListStore abstractListStore;
    
    ListStoreIterator(final ObjectProvider op, final ResultSet resultSet, final ResultObjectFactory rof, final AbstractListStore als) throws MappedDatastoreException {
        this.lastElement = null;
        this.currentIndex = -1;
        this.op = op;
        this.abstractListStore = als;
        final ExecutionContext ec = op.getExecutionContext();
        final ArrayList results = new ArrayList();
        if (resultSet != null) {
            final Table containerTable = als.getContainerTable();
            final boolean elementsAreSerialised = als.isElementsAreSerialised();
            final boolean elementsAreEmbedded = als.isElementsAreEmbedded();
            final JavaTypeMapping elementMapping = als.getElementMapping();
            while (this.next(resultSet)) {
                Object nextElement;
                if (elementsAreEmbedded || elementsAreSerialised) {
                    final int[] param = new int[elementMapping.getNumberOfDatastoreMappings()];
                    for (int i = 0; i < param.length; ++i) {
                        param[i] = i + 1;
                    }
                    if (elementMapping instanceof SerialisedPCMapping || elementMapping instanceof SerialisedReferenceMapping || elementMapping instanceof EmbeddedElementPCMapping) {
                        int ownerFieldNumber = -1;
                        if (containerTable != null) {
                            ownerFieldNumber = this.getOwnerMemberMetaData(this.abstractListStore.containerTable).getAbsoluteFieldNumber();
                        }
                        nextElement = elementMapping.getObject(ec, resultSet, param, op, ownerFieldNumber);
                    }
                    else {
                        nextElement = elementMapping.getObject(ec, resultSet, param);
                    }
                }
                else if (elementMapping instanceof ReferenceMapping) {
                    final int[] param = new int[elementMapping.getNumberOfDatastoreMappings()];
                    for (int i = 0; i < param.length; ++i) {
                        param[i] = i + 1;
                    }
                    nextElement = elementMapping.getObject(ec, resultSet, param);
                }
                else {
                    nextElement = rof.getObject(ec, resultSet);
                }
                results.add(nextElement);
            }
        }
        this.delegate = results.listIterator();
    }
    
    @Override
    public void add(final Object o) {
        this.currentIndex = this.delegate.nextIndex();
        this.abstractListStore.add(this.op, o, this.currentIndex, -1);
        this.delegate.add(o);
        this.lastElement = null;
    }
    
    @Override
    public boolean hasNext() {
        return this.delegate.hasNext();
    }
    
    @Override
    public boolean hasPrevious() {
        return this.delegate.hasPrevious();
    }
    
    @Override
    public Object next() {
        this.currentIndex = this.delegate.nextIndex();
        return this.lastElement = this.delegate.next();
    }
    
    @Override
    public int nextIndex() {
        return this.delegate.nextIndex();
    }
    
    @Override
    public Object previous() {
        this.currentIndex = this.delegate.previousIndex();
        return this.lastElement = this.delegate.previous();
    }
    
    @Override
    public int previousIndex() {
        return this.delegate.previousIndex();
    }
    
    @Override
    public synchronized void remove() {
        if (this.lastElement == null) {
            throw new IllegalStateException("No entry to remove");
        }
        this.abstractListStore.remove(this.op, this.currentIndex, -1);
        this.delegate.remove();
        this.lastElement = null;
        this.currentIndex = -1;
    }
    
    @Override
    public synchronized void set(final Object o) {
        if (this.lastElement == null) {
            throw new IllegalStateException("No entry to replace");
        }
        this.abstractListStore.set(this.op, this.currentIndex, o, true);
        this.delegate.set(o);
        this.lastElement = o;
    }
    
    protected AbstractMemberMetaData getOwnerMemberMetaData(final Table containerTable) {
        return ((JoinTable)containerTable).getOwnerMemberMetaData();
    }
    
    protected boolean next(final Object resultSet) throws MappedDatastoreException {
        try {
            return ((ResultSet)resultSet).next();
        }
        catch (SQLException e) {
            throw new MappedDatastoreException(e.getMessage(), e);
        }
    }
}
