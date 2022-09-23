// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query;

import org.datanucleus.ClassConstants;
import java.util.Map;
import org.datanucleus.ExecutionContext;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.ListIterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.store.connection.ManagedConnectionResourceListener;
import java.util.List;
import org.datanucleus.util.Localiser;
import java.io.Serializable;
import java.util.AbstractList;

public abstract class AbstractQueryResult extends AbstractList implements QueryResult, Serializable
{
    protected static final Localiser LOCALISER;
    protected boolean closed;
    protected Query query;
    protected List<ManagedConnectionResourceListener> connectionListeners;
    ApiAdapter api;
    protected int size;
    protected String resultSizeMethod;
    protected boolean loadResultsAtCommit;
    
    public AbstractQueryResult(final Query query) {
        this.closed = false;
        this.connectionListeners = null;
        this.size = -1;
        this.resultSizeMethod = "last";
        this.loadResultsAtCommit = true;
        this.query = query;
        if (query != null) {
            this.api = query.getExecutionContext().getApiAdapter();
            this.resultSizeMethod = query.getStringExtensionProperty("datanucleus.query.resultSizeMethod", "last");
            this.loadResultsAtCommit = query.getBooleanExtensionProperty("datanucleus.query.loadResultsAtCommit", true);
        }
    }
    
    @Override
    public void disconnect() {
        if (this.query == null) {
            return;
        }
        try {
            this.closingConnection();
            this.closeResults();
        }
        finally {
            this.query = null;
        }
    }
    
    protected abstract void closingConnection();
    
    protected abstract void closeResults();
    
    @Override
    public synchronized void close() {
        if (this.closed) {
            return;
        }
        this.closeResults();
        this.query = null;
        this.closed = true;
        if (this.connectionListeners != null) {
            final Iterator<ManagedConnectionResourceListener> iter = this.connectionListeners.iterator();
            while (iter.hasNext()) {
                iter.next().resourcePostClose();
            }
            this.connectionListeners.clear();
            this.connectionListeners = null;
        }
    }
    
    public void addConnectionListener(final ManagedConnectionResourceListener listener) {
        if (this.connectionListeners == null) {
            this.connectionListeners = new ArrayList<ManagedConnectionResourceListener>();
        }
        this.connectionListeners.add(listener);
    }
    
    protected boolean isOpen() {
        return !this.closed;
    }
    
    protected void assertIsOpen() {
        if (!this.isOpen()) {
            throw this.api.getUserExceptionForException(AbstractQueryResult.LOCALISER.msg("052600"), null);
        }
    }
    
    @Override
    public void add(final int index, final Object element) {
        throw new UnsupportedOperationException(AbstractQueryResult.LOCALISER.msg("052603"));
    }
    
    @Override
    public boolean add(final Object o) {
        throw new UnsupportedOperationException(AbstractQueryResult.LOCALISER.msg("052603"));
    }
    
    @Override
    public boolean addAll(final int index, final Collection c) {
        throw new UnsupportedOperationException(AbstractQueryResult.LOCALISER.msg("052603"));
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException(AbstractQueryResult.LOCALISER.msg("052603"));
    }
    
    @Override
    public boolean contains(final Object o) {
        throw new UnsupportedOperationException(AbstractQueryResult.LOCALISER.msg("052604"));
    }
    
    @Override
    public boolean containsAll(final Collection c) {
        throw new UnsupportedOperationException(AbstractQueryResult.LOCALISER.msg("052604"));
    }
    
    @Override
    public abstract boolean equals(final Object p0);
    
    @Override
    public abstract Object get(final int p0);
    
    @Override
    public int hashCode() {
        if (this.query != null) {
            return this.query.hashCode();
        }
        return super.hashCode();
    }
    
    @Override
    public int indexOf(final Object o) {
        throw new UnsupportedOperationException(AbstractQueryResult.LOCALISER.msg("052604"));
    }
    
    @Override
    public boolean isEmpty() {
        return this.size() < 1;
    }
    
    @Override
    public abstract Iterator iterator();
    
    @Override
    public int lastIndexOf(final Object o) {
        throw new UnsupportedOperationException(AbstractQueryResult.LOCALISER.msg("052604"));
    }
    
    @Override
    public abstract ListIterator listIterator();
    
    @Override
    public Object remove(final int index) {
        throw new UnsupportedOperationException(AbstractQueryResult.LOCALISER.msg("052603"));
    }
    
    @Override
    public Object set(final int index, final Object element) {
        throw new UnsupportedOperationException(AbstractQueryResult.LOCALISER.msg("052603"));
    }
    
    @Override
    public int size() {
        this.assertIsOpen();
        if (this.size < 0) {
            this.size = this.getSizeUsingMethod();
        }
        return this.size;
    }
    
    @Override
    public List subList(final int fromIndex, final int toIndex) {
        final int subListLength = toIndex - fromIndex;
        final ArrayList subList = new ArrayList(subListLength);
        for (int i = fromIndex; i < toIndex; ++i) {
            subList.add(this.get(i));
        }
        return subList;
    }
    
    @Override
    public Object[] toArray() {
        final Object[] array = new Object[this.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = this.get(i);
        }
        return array;
    }
    
    @Override
    public Object[] toArray(final Object[] a) {
        final int theSize = this.size();
        if (a.length >= theSize) {
            for (int i = 0; i < a.length; ++i) {
                if (i < theSize) {
                    a[i] = this.get(i);
                }
                else {
                    a[i] = null;
                }
            }
            return a;
        }
        return this.toArray();
    }
    
    protected int getSizeUsingMethod() {
        if (this.resultSizeMethod.equalsIgnoreCase("COUNT")) {
            if (this.query != null && this.query.getCompilation() != null) {
                final ExecutionContext ec = this.query.getExecutionContext();
                if (this.query.getCompilation().getQueryLanguage().equalsIgnoreCase("JDOQL")) {
                    final Query countQuery = this.query.getStoreManager().getQueryManager().newQuery("JDOQL", ec, this.query);
                    if (this.query.getResultDistinct()) {
                        countQuery.setResult("COUNT(DISTINCT this)");
                    }
                    else {
                        countQuery.setResult("count(this)");
                    }
                    countQuery.setOrdering(null);
                    countQuery.setRange(null);
                    final Map queryParams = this.query.getInputParameters();
                    long count;
                    if (queryParams != null) {
                        count = (long)countQuery.executeWithMap(queryParams);
                    }
                    else {
                        count = (long)countQuery.execute();
                    }
                    if (this.query.getRange() != null) {
                        final long rangeStart = this.query.getRangeFromIncl();
                        final long rangeEnd = this.query.getRangeToExcl();
                        count -= rangeStart;
                        if (count > rangeEnd - rangeStart) {
                            count = rangeEnd - rangeStart;
                        }
                    }
                    countQuery.closeAll();
                    return (int)count;
                }
                if (this.query.getCompilation().getQueryLanguage().equalsIgnoreCase("JPQL")) {
                    final Query countQuery = this.query.getStoreManager().getQueryManager().newQuery("JPQL", ec, this.query);
                    countQuery.setResult("count(" + this.query.getCompilation().getCandidateAlias() + ")");
                    countQuery.setOrdering(null);
                    countQuery.setRange(null);
                    final Map queryParams = this.query.getInputParameters();
                    long count;
                    if (queryParams != null) {
                        count = (long)countQuery.executeWithMap(queryParams);
                    }
                    else {
                        count = (long)countQuery.execute();
                    }
                    if (this.query.getRange() != null) {
                        final long rangeStart = this.query.getRangeFromIncl();
                        final long rangeEnd = this.query.getRangeToExcl();
                        count -= rangeStart;
                        if (count > rangeEnd - rangeStart) {
                            count = rangeEnd - rangeStart;
                        }
                    }
                    countQuery.closeAll();
                    return (int)count;
                }
            }
            throw new NucleusUserException("datanucleus.query.resultSizeMethod of \"COUNT\" is only valid for use with JDOQL or JPQL currently");
        }
        throw new NucleusUserException("DataNucleus doesnt currently support any method \"" + this.resultSizeMethod + "\" for determining the size of the query results");
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
