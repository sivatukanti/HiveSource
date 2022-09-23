// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query;

import java.util.NoSuchElementException;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import org.datanucleus.ExecutionContext;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.ListIterator;
import java.util.Iterator;
import org.datanucleus.util.StringUtils;
import org.datanucleus.util.WeakValueMap;
import java.util.HashMap;
import org.datanucleus.util.SoftValueMap;
import java.util.Map;
import java.util.List;
import java.io.Serializable;

public class CandidateIdsQueryResult extends AbstractQueryResult implements Serializable
{
    final List<Object> ids;
    Map<Integer, Object> results;
    boolean validateObjects;
    
    public CandidateIdsQueryResult(final Query query, final List<Object> ids) {
        super(query);
        this.results = null;
        this.validateObjects = true;
        this.ids = ids;
        this.size = ((ids != null) ? ids.size() : 0);
        this.validateObjects = query.getBooleanExtensionProperty("datanucleus.query.resultCache.validateObjects", true);
        final String ext = (String)query.getExtension("datanucleus.query.resultCache.type");
        if (ext != null) {
            if (ext.equalsIgnoreCase("soft")) {
                this.results = (Map<Integer, Object>)new SoftValueMap();
            }
            else if (ext.equalsIgnoreCase("strong")) {
                this.results = new HashMap<Integer, Object>();
            }
            else if (ext.equalsIgnoreCase("weak")) {
                this.results = (Map<Integer, Object>)new WeakValueMap();
            }
            else if (ext.equalsIgnoreCase("none")) {
                this.results = null;
            }
            else {
                this.results = new HashMap<Integer, Object>();
            }
        }
        else {
            this.results = new HashMap<Integer, Object>();
        }
    }
    
    @Override
    protected void closeResults() {
    }
    
    @Override
    protected void closingConnection() {
        if (this.loadResultsAtCommit && this.isOpen()) {
            for (int i = 0; i < this.size; ++i) {
                this.getObjectForIndex(i);
            }
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof CandidateIdsQueryResult)) {
            return false;
        }
        final CandidateIdsQueryResult other = (CandidateIdsQueryResult)o;
        if (this.query != null) {
            return other.query == this.query;
        }
        return StringUtils.toJVMIDString(other).equals(StringUtils.toJVMIDString(this));
    }
    
    @Override
    public Object get(final int index) {
        if (index < 0 || index >= this.size) {
            throw new ArrayIndexOutOfBoundsException("Index should be between 0 and " + (this.size - 1));
        }
        return this.getObjectForIndex(index);
    }
    
    @Override
    public Iterator iterator() {
        return new ResultIterator();
    }
    
    @Override
    public ListIterator listIterator() {
        return new ResultIterator();
    }
    
    protected Object getObjectForIndex(final int index) {
        if (this.ids == null) {
            return null;
        }
        final Object id = this.ids.get(index);
        if (this.results != null) {
            final Object obj = this.results.get(index);
            if (obj != null) {
                return obj;
            }
        }
        if (this.query == null) {
            throw new NucleusUserException("Query has already been closed");
        }
        if (this.query.getExecutionContext() == null || this.query.getExecutionContext().isClosed()) {
            throw new NucleusUserException("ExecutionContext has already been closed");
        }
        final ExecutionContext ec = this.query.getExecutionContext();
        final Object obj2 = ec.findObject(id, this.validateObjects, false, null);
        if (this.results != null) {
            this.results.put(index, obj2);
        }
        return obj2;
    }
    
    protected Object writeReplace() throws ObjectStreamException {
        this.disconnect();
        final List results = new ArrayList(this.results.size());
        for (int i = 0; i < this.results.size(); ++i) {
            results.add(this.results.get(i));
        }
        return results;
    }
    
    public class ResultIterator extends AbstractQueryResultIterator
    {
        int next;
        
        public ResultIterator() {
            this.next = 0;
        }
        
        @Override
        public boolean hasNext() {
            return CandidateIdsQueryResult.this.isOpen() && CandidateIdsQueryResult.this.size - this.next > 0;
        }
        
        @Override
        public boolean hasPrevious() {
            return this.next >= 1;
        }
        
        @Override
        public Object next() {
            if (this.next == CandidateIdsQueryResult.this.size) {
                throw new NoSuchElementException("Already at end of List");
            }
            final Object obj = CandidateIdsQueryResult.this.getObjectForIndex(this.next);
            ++this.next;
            return obj;
        }
        
        @Override
        public int nextIndex() {
            return this.next;
        }
        
        @Override
        public Object previous() {
            if (this.next == 0) {
                throw new NoSuchElementException("Already at start of List");
            }
            final Object obj = CandidateIdsQueryResult.this.getObjectForIndex(this.next - 1);
            --this.next;
            return obj;
        }
        
        @Override
        public int previousIndex() {
            return this.next - 1;
        }
    }
}
