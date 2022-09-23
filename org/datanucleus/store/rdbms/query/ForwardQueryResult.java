// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.query;

import java.util.NoSuchElementException;
import org.datanucleus.store.query.AbstractQueryResultIterator;
import java.io.ObjectStreamException;
import org.datanucleus.util.StringUtils;
import java.util.ListIterator;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.ExecutionContext;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.Map;
import org.datanucleus.store.rdbms.JDBCUtils;
import java.util.Iterator;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.ResultSet;
import org.datanucleus.store.query.Query;
import java.util.Collection;
import java.util.List;
import java.io.Serializable;

public final class ForwardQueryResult extends AbstractRDBMSQueryResult implements Serializable
{
    protected boolean moreResultSetRows;
    protected List resultObjs;
    protected List resultIds;
    private Collection candidates;
    private boolean applyRangeChecks;
    
    public ForwardQueryResult(final Query query, final ResultObjectFactory rof, final ResultSet rs, final Collection candidates) throws SQLException {
        super(query, rof, rs);
        this.resultObjs = new ArrayList();
        this.resultIds = null;
        this.applyRangeChecks = false;
        if (query.useResultsCaching()) {
            this.resultIds = new ArrayList();
        }
        this.applyRangeChecks = !query.processesRangeInDatastoreQuery();
        if (candidates != null) {
            this.candidates = new ArrayList(candidates);
        }
    }
    
    @Override
    public void initialise() throws SQLException {
        this.moreResultSetRows = this.rs.next();
        if (this.applyRangeChecks) {
            for (int i = 0; i < this.query.getRangeFromIncl(); ++i) {
                if (!(this.moreResultSetRows = this.rs.next())) {
                    break;
                }
            }
        }
        final int fetchSize = this.query.getFetchPlan().getFetchSize();
        if (!this.moreResultSetRows) {
            this.closeResults();
        }
        else if (fetchSize == -1) {
            this.advanceToEndOfResultSet();
        }
        else if (fetchSize > 0) {
            this.processNumberOfResults(fetchSize);
        }
    }
    
    private void processNumberOfResults(final int number) {
        final Iterator iter = this.iterator();
        if (number < 0) {
            while (iter.hasNext()) {
                iter.next();
            }
        }
        else {
            for (int i = 0; i < number; ++i) {
                if (iter.hasNext()) {
                    iter.next();
                }
            }
        }
    }
    
    private void advanceToEndOfResultSet() {
        this.processNumberOfResults(-1);
    }
    
    protected Object nextResultSetElement() {
        if (this.rof == null) {
            return null;
        }
        final ExecutionContext ec = this.query.getExecutionContext();
        final Object nextElement = this.rof.getObject(ec, this.rs);
        JDBCUtils.logWarnings(this.rs);
        this.resultObjs.add(nextElement);
        if (this.resultIds != null) {
            this.resultIds.add(ec.getApiAdapter().getIdForObject(nextElement));
        }
        if (this.bulkLoadedValueByMemberNumber != null) {
            final Map<Integer, Object> memberValues = this.bulkLoadedValueByMemberNumber.get(ec.getApiAdapter().getIdForObject(nextElement));
            if (memberValues != null) {
                final ObjectProvider op = ec.findObjectProvider(nextElement);
                for (final Map.Entry<Integer, Object> memberValueEntry : memberValues.entrySet()) {
                    final int fieldNumber = memberValueEntry.getKey();
                    op.replaceField(fieldNumber, memberValueEntry.getValue());
                }
                op.replaceAllLoadedSCOFieldsWithWrappers();
            }
        }
        if (this.rs == null) {
            throw new NucleusUserException("Results for query have already been closed. Perhaps you called flush(), closed the query, or ended a transaction");
        }
        try {
            this.moreResultSetRows = this.rs.next();
            if (this.applyRangeChecks) {
                final int maxElements = (int)(this.query.getRangeToExcl() - this.query.getRangeFromIncl());
                if (this.resultObjs.size() == maxElements) {
                    this.moreResultSetRows = false;
                }
            }
            if (!this.moreResultSetRows) {
                this.closeResults();
            }
        }
        catch (SQLException e) {
            throw ec.getApiAdapter().getDataStoreExceptionForException(ForwardQueryResult.LOCALISER.msg("052601", e.getMessage()), e);
        }
        return nextElement;
    }
    
    @Override
    protected void closeResults() {
        super.closeResults();
        if (this.resultIds != null) {
            this.query.getQueryManager().addDatastoreQueryResult(this.query, this.query.getInputParameters(), this.resultIds);
            this.resultIds = null;
        }
    }
    
    @Override
    public synchronized void close() {
        this.moreResultSetRows = false;
        this.resultObjs.clear();
        if (this.resultIds != null) {
            this.resultIds.clear();
        }
        super.close();
    }
    
    @Override
    protected void closingConnection() {
        if (this.loadResultsAtCommit && this.isOpen() && this.moreResultSetRows) {
            NucleusLogger.QUERY.info(ForwardQueryResult.LOCALISER.msg("052606", this.query.toString()));
            try {
                this.advanceToEndOfResultSet();
            }
            catch (RuntimeException re) {
                if (!(re instanceof NucleusUserException)) {
                    throw this.query.getExecutionContext().getApiAdapter().getUserExceptionForException("Exception thrown while loading remaining rows of query", re);
                }
                NucleusLogger.QUERY.warn("Exception thrown while loading remaining rows of query : " + re.getMessage());
            }
        }
    }
    
    @Override
    public Iterator iterator() {
        return new QueryResultIterator();
    }
    
    @Override
    public ListIterator listIterator() {
        return new QueryResultIterator();
    }
    
    @Override
    public synchronized boolean contains(final Object o) {
        this.assertIsOpen();
        this.advanceToEndOfResultSet();
        return this.resultObjs.contains(o);
    }
    
    @Override
    public synchronized boolean containsAll(final Collection c) {
        this.assertIsOpen();
        this.advanceToEndOfResultSet();
        return this.resultObjs.containsAll(c);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof ForwardQueryResult)) {
            return false;
        }
        final ForwardQueryResult other = (ForwardQueryResult)o;
        if (this.rs != null) {
            return other.rs == this.rs;
        }
        if (this.query != null) {
            return other.query == this.query;
        }
        return StringUtils.toJVMIDString(other).equals(StringUtils.toJVMIDString(this));
    }
    
    @Override
    public synchronized Object get(final int index) {
        this.assertIsOpen();
        this.advanceToEndOfResultSet();
        if (index < 0 || index >= this.resultObjs.size()) {
            throw new IndexOutOfBoundsException();
        }
        return this.resultObjs.get(index);
    }
    
    @Override
    public synchronized boolean isEmpty() {
        this.assertIsOpen();
        return this.resultObjs.isEmpty() && !this.moreResultSetRows;
    }
    
    @Override
    protected int getSizeUsingMethod() {
        if (this.resultSizeMethod.equalsIgnoreCase("LAST")) {
            this.advanceToEndOfResultSet();
            return this.resultObjs.size();
        }
        return super.getSizeUsingMethod();
    }
    
    @Override
    public synchronized Object[] toArray() {
        this.assertIsOpen();
        this.advanceToEndOfResultSet();
        return this.resultObjs.toArray();
    }
    
    @Override
    public synchronized Object[] toArray(final Object[] a) {
        this.assertIsOpen();
        this.advanceToEndOfResultSet();
        return this.resultObjs.toArray(a);
    }
    
    protected Object writeReplace() throws ObjectStreamException {
        this.disconnect();
        return new ArrayList(this.resultObjs);
    }
    
    private class QueryResultIterator extends AbstractQueryResultIterator
    {
        private int nextRowNum;
        Object currentElement;
        
        private QueryResultIterator() {
            this.nextRowNum = 0;
            this.currentElement = null;
        }
        
        @Override
        public boolean hasNext() {
            synchronized (ForwardQueryResult.this) {
                if (!AbstractQueryResult.this.isOpen()) {
                    return false;
                }
                if (ForwardQueryResult.this.applyRangeChecks) {
                    final int maxElements = (int)(ForwardQueryResult.this.query.getRangeToExcl() - ForwardQueryResult.this.query.getRangeFromIncl());
                    if (this.nextRowNum == maxElements) {
                        ForwardQueryResult.this.moreResultSetRows = false;
                        ForwardQueryResult.this.closeResults();
                        return false;
                    }
                }
                if (this.nextRowNum < ForwardQueryResult.this.resultObjs.size()) {
                    return true;
                }
                if (ForwardQueryResult.this.candidates != null && this.currentElement != null && !ForwardQueryResult.this.moreResultSetRows) {
                    return ForwardQueryResult.this.candidates.contains(this.currentElement);
                }
                return ForwardQueryResult.this.moreResultSetRows;
            }
        }
        
        @Override
        public boolean hasPrevious() {
            throw new UnsupportedOperationException("Not yet implemented");
        }
        
        @Override
        public Object next() {
            synchronized (ForwardQueryResult.this) {
                if (!AbstractQueryResult.this.isOpen()) {
                    throw new NoSuchElementException(QueryResultIterator.LOCALISER.msg("052600"));
                }
                if (ForwardQueryResult.this.candidates != null && this.currentElement != null && ForwardQueryResult.this.candidates.remove(this.currentElement)) {
                    ForwardQueryResult.this.resultObjs.add(this.currentElement);
                    return this.currentElement;
                }
                if (this.nextRowNum < ForwardQueryResult.this.resultObjs.size()) {
                    this.currentElement = ForwardQueryResult.this.resultObjs.get(this.nextRowNum);
                    ++this.nextRowNum;
                    return this.currentElement;
                }
                if (ForwardQueryResult.this.moreResultSetRows) {
                    this.currentElement = ForwardQueryResult.this.nextResultSetElement();
                    ++this.nextRowNum;
                    if (ForwardQueryResult.this.candidates != null) {
                        ForwardQueryResult.this.candidates.remove(this.currentElement);
                    }
                    return this.currentElement;
                }
                throw new NoSuchElementException(QueryResultIterator.LOCALISER.msg("052602"));
            }
        }
        
        @Override
        public int nextIndex() {
            throw new UnsupportedOperationException("Not yet implemented");
        }
        
        @Override
        public Object previous() {
            throw new UnsupportedOperationException("Not yet implemented");
        }
        
        @Override
        public int previousIndex() {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }
}
