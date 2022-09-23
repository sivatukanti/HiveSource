// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.query;

import java.util.NoSuchElementException;
import org.datanucleus.store.query.AbstractQueryResultIterator;
import java.io.ObjectStreamException;
import org.datanucleus.util.StringUtils;
import java.util.ListIterator;
import java.util.List;
import java.util.ArrayList;
import org.datanucleus.util.NucleusLogger;
import java.util.Iterator;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.rdbms.JDBCUtils;
import org.datanucleus.exceptions.NucleusUserException;
import java.sql.SQLException;
import org.datanucleus.util.WeakValueMap;
import org.datanucleus.util.SoftValueMap;
import java.util.HashMap;
import org.datanucleus.exceptions.NucleusException;
import java.util.Collection;
import java.sql.ResultSet;
import org.datanucleus.store.query.Query;
import java.util.Map;
import java.io.Serializable;

public final class ScrollableQueryResult extends AbstractRDBMSQueryResult implements Serializable
{
    private Map<Integer, Object> resultsObjsByIndex;
    protected Map<Integer, Object> resultIds;
    int startIndex;
    int endIndex;
    boolean applyRangeChecks;
    
    public ScrollableQueryResult(final Query query, final ResultObjectFactory rof, final ResultSet rs, final Collection candidates) {
        super(query, rof, rs);
        this.resultsObjsByIndex = null;
        this.resultIds = null;
        this.startIndex = 0;
        this.endIndex = -1;
        this.applyRangeChecks = false;
        if (candidates != null) {
            throw new NucleusException("Unsupported Feature: Candidate Collection is only allowed using ForwardQueryResults").setFatal();
        }
        if (query.useResultsCaching()) {
            this.resultIds = new HashMap<Integer, Object>();
        }
        final String ext = (String)query.getExtension("datanucleus.query.resultCacheType");
        if (ext != null) {
            if (ext.equalsIgnoreCase("soft")) {
                this.resultsObjsByIndex = (Map<Integer, Object>)new SoftValueMap();
            }
            else if (ext.equalsIgnoreCase("weak")) {
                this.resultsObjsByIndex = (Map<Integer, Object>)new WeakValueMap();
            }
            else if (ext.equalsIgnoreCase("strong")) {
                this.resultsObjsByIndex = new HashMap<Integer, Object>();
            }
            else if (ext.equalsIgnoreCase("none")) {
                this.resultsObjsByIndex = null;
            }
            else {
                this.resultsObjsByIndex = (Map<Integer, Object>)new WeakValueMap();
            }
        }
        else {
            this.resultsObjsByIndex = (Map<Integer, Object>)new WeakValueMap();
        }
        this.applyRangeChecks = !query.processesRangeInDatastoreQuery();
        if (this.applyRangeChecks) {
            this.startIndex = (int)query.getRangeFromIncl();
        }
    }
    
    @Override
    public void initialise() {
        if (this.resultsObjsByIndex != null) {
            final int fetchSize = this.query.getFetchPlan().getFetchSize();
            if (fetchSize == -1) {
                this.loadObjects(this.startIndex, -1);
                this.cacheQueryResults();
            }
            else if (fetchSize > 0) {
                this.loadObjects(this.startIndex, fetchSize);
            }
        }
    }
    
    protected void loadObjects(final int start, final int maxNumber) {
        int index = start;
        boolean hasMoreResults = true;
        while (hasMoreResults) {
            if (maxNumber >= 0 && index == maxNumber + start) {
                hasMoreResults = false;
            }
            else if (this.applyRangeChecks && index >= this.query.getRangeToExcl()) {
                this.size = (int)(this.query.getRangeToExcl() - this.query.getRangeFromIncl());
                hasMoreResults = false;
            }
            else {
                try {
                    final boolean rowExists = this.rs.absolute(index + 1);
                    if (!rowExists) {
                        hasMoreResults = false;
                        this.size = index;
                        if (this.applyRangeChecks && index < this.query.getRangeToExcl()) {
                            this.size = (int)(index - this.query.getRangeFromIncl());
                        }
                        this.endIndex = index - 1;
                    }
                    else {
                        this.getObjectForIndex(index);
                        ++index;
                    }
                }
                catch (SQLException sqle) {}
            }
        }
    }
    
    protected Object getObjectForIndex(final int index) {
        if (this.resultsObjsByIndex != null) {
            final Object obj = this.resultsObjsByIndex.get(index);
            if (obj != null) {
                return obj;
            }
        }
        if (this.rs == null) {
            throw new NucleusUserException("Results for query have already been closed. Perhaps you called flush(), closed the query, or ended a transaction");
        }
        try {
            this.rs.absolute(index + 1);
            final Object obj = this.rof.getObject(this.query.getExecutionContext(), this.rs);
            JDBCUtils.logWarnings(this.rs);
            if (this.bulkLoadedValueByMemberNumber != null) {
                final ExecutionContext ec = this.query.getExecutionContext();
                final Map<Integer, Object> memberValues = this.bulkLoadedValueByMemberNumber.get(ec.getApiAdapter().getIdForObject(obj));
                if (memberValues != null) {
                    final ObjectProvider op = ec.findObjectProvider(obj);
                    for (final Map.Entry<Integer, Object> memberValueEntry : memberValues.entrySet()) {
                        final int fieldNumber = memberValueEntry.getKey();
                        op.replaceField(fieldNumber, memberValueEntry.getValue());
                    }
                    op.replaceAllLoadedSCOFieldsWithWrappers();
                }
            }
            if (this.resultsObjsByIndex != null) {
                this.resultsObjsByIndex.put(index, obj);
                if (this.resultIds != null) {
                    this.resultIds.put(index, this.query.getExecutionContext().getApiAdapter().getIdForObject(obj));
                }
            }
            return obj;
        }
        catch (SQLException sqe) {
            throw this.query.getExecutionContext().getApiAdapter().getDataStoreExceptionForException(ScrollableQueryResult.LOCALISER.msg("052601", sqe.getMessage()), sqe);
        }
    }
    
    @Override
    public synchronized void close() {
        if (this.resultsObjsByIndex != null) {
            this.resultsObjsByIndex.clear();
        }
        super.close();
    }
    
    @Override
    protected void closingConnection() {
        if (this.loadResultsAtCommit && this.isOpen()) {
            NucleusLogger.QUERY.info(ScrollableQueryResult.LOCALISER.msg("052606", this.query.toString()));
            if (this.endIndex < 0) {
                this.endIndex = this.size() - 1;
                if (this.applyRangeChecks) {
                    this.endIndex = (int)this.query.getRangeToExcl() - 1;
                }
            }
            for (int i = this.startIndex; i < this.endIndex + 1; ++i) {
                this.getObjectForIndex(i);
            }
            this.cacheQueryResults();
        }
    }
    
    protected void cacheQueryResults() {
        if (this.resultIds != null) {
            final List ids = new ArrayList();
            for (final Integer position : this.resultIds.keySet()) {
                final Object resultId = this.resultIds.get(position);
                ids.add(resultId);
            }
            this.query.getQueryManager().addDatastoreQueryResult(this.query, this.query.getInputParameters(), ids);
        }
        this.resultIds = null;
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
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof ScrollableQueryResult)) {
            return false;
        }
        final ScrollableQueryResult other = (ScrollableQueryResult)o;
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
        if (index < 0 || index >= this.size()) {
            throw new IndexOutOfBoundsException();
        }
        return this.getObjectForIndex(index + this.startIndex);
    }
    
    @Override
    protected int getSizeUsingMethod() {
        int theSize = 0;
        if (this.resultSizeMethod.equalsIgnoreCase("LAST")) {
            if (this.rs == null) {
                throw new NucleusUserException("Results for query have already been closed. Perhaps you called flush(), closed the query, or ended a transaction");
            }
            try {
                final boolean hasLast = this.rs.last();
                if (!hasLast) {
                    theSize = 0;
                }
                else {
                    theSize = this.rs.getRow();
                }
            }
            catch (SQLException sqle) {
                throw this.query.getExecutionContext().getApiAdapter().getDataStoreExceptionForException(ScrollableQueryResult.LOCALISER.msg("052601", sqle.getMessage()), sqle);
            }
            if (this.applyRangeChecks) {
                if (theSize > this.query.getRangeToExcl()) {
                    this.endIndex = (int)(this.query.getRangeToExcl() - 1L);
                    theSize = (int)(this.query.getRangeToExcl() - this.query.getRangeFromIncl());
                }
                else {
                    this.endIndex = theSize - 1;
                    theSize -= (int)this.query.getRangeFromIncl();
                }
            }
        }
        else {
            theSize = super.getSizeUsingMethod();
        }
        return theSize;
    }
    
    @Override
    public Object[] toArray() {
        return this.toArrayInternal(null);
    }
    
    @Override
    public Object[] toArray(final Object[] a) {
        if (a == null) {
            throw new NullPointerException("null argument is illegal!");
        }
        return this.toArrayInternal(a);
    }
    
    private Object[] toArrayInternal(final Object[] a) {
        Object[] result = a;
        ArrayList resultList = null;
        int size = -1;
        try {
            size = this.size();
        }
        catch (Exception x) {
            size = -1;
            if (NucleusLogger.QUERY.isDebugEnabled()) {
                NucleusLogger.QUERY.debug("toArray: Could not determine size.", x);
            }
        }
        if (size >= 0 && (result == null || result.length < size)) {
            result = null;
            resultList = new ArrayList(size);
        }
        Iterator iterator = null;
        if (result != null) {
            iterator = this.iterator();
            int idx = -1;
            while (iterator.hasNext()) {
                if (++idx >= result.length) {
                    int capacity = result.length * 3 / 2 + 1;
                    if (capacity < result.length) {
                        capacity = result.length;
                    }
                    resultList = new ArrayList(capacity);
                    for (int i = 0; i < result.length; ++i) {
                        resultList.add(result[i]);
                    }
                    result = null;
                    break;
                }
                result[idx] = iterator.next();
            }
            ++idx;
            if (result != null && idx < result.length) {
                result[idx] = null;
            }
        }
        if (result == null) {
            if (resultList == null) {
                resultList = new ArrayList();
            }
            if (iterator == null) {
                iterator = this.iterator();
            }
            while (iterator.hasNext()) {
                resultList.add(iterator.next());
            }
            if (a == null) {
                result = resultList.toArray();
            }
            else {
                result = resultList.toArray(a);
            }
        }
        return result;
    }
    
    protected Object writeReplace() throws ObjectStreamException {
        this.disconnect();
        final List results = new ArrayList();
        for (int i = 0; i < this.resultsObjsByIndex.size(); ++i) {
            final Object obj = this.resultsObjsByIndex.get(i);
            results.add(obj);
        }
        return results;
    }
    
    private class QueryResultIterator extends AbstractQueryResultIterator
    {
        private int iterRowNum;
        
        public QueryResultIterator() {
            this.iterRowNum = 0;
            if (ScrollableQueryResult.this.applyRangeChecks) {
                this.iterRowNum = (int)ScrollableQueryResult.this.query.getRangeFromIncl();
            }
        }
        
        @Override
        public boolean hasNext() {
            synchronized (ScrollableQueryResult.this) {
                if (!AbstractQueryResult.this.isOpen()) {
                    return false;
                }
                final int theSize = ScrollableQueryResult.this.size();
                if (!ScrollableQueryResult.this.applyRangeChecks) {
                    return this.iterRowNum <= theSize - 1;
                }
                if (theSize < ScrollableQueryResult.this.query.getRangeToExcl() - ScrollableQueryResult.this.query.getRangeFromIncl()) {
                    return this.iterRowNum <= ScrollableQueryResult.this.query.getRangeFromIncl() + theSize - 1L;
                }
                if (this.iterRowNum == ScrollableQueryResult.this.query.getRangeToExcl() - 1L) {
                    ScrollableQueryResult.this.endIndex = this.iterRowNum;
                }
                return this.iterRowNum <= ScrollableQueryResult.this.query.getRangeToExcl() - 1L;
            }
        }
        
        @Override
        public boolean hasPrevious() {
            synchronized (ScrollableQueryResult.this) {
                if (!AbstractQueryResult.this.isOpen()) {
                    return false;
                }
                if (ScrollableQueryResult.this.applyRangeChecks) {
                    return this.iterRowNum > ScrollableQueryResult.this.query.getRangeFromIncl();
                }
                return this.iterRowNum > 0;
            }
        }
        
        @Override
        public Object next() {
            synchronized (ScrollableQueryResult.this) {
                if (!AbstractQueryResult.this.isOpen()) {
                    throw new NoSuchElementException(QueryResultIterator.LOCALISER.msg("052600"));
                }
                if (!this.hasNext()) {
                    throw new NoSuchElementException("No next element");
                }
                final Object obj = ScrollableQueryResult.this.getObjectForIndex(this.iterRowNum);
                ++this.iterRowNum;
                return obj;
            }
        }
        
        @Override
        public int nextIndex() {
            if (!this.hasNext()) {
                return ScrollableQueryResult.this.size();
            }
            if (ScrollableQueryResult.this.applyRangeChecks) {
                return this.iterRowNum - (int)ScrollableQueryResult.this.query.getRangeFromIncl();
            }
            return this.iterRowNum;
        }
        
        @Override
        public Object previous() {
            synchronized (ScrollableQueryResult.this) {
                if (!AbstractQueryResult.this.isOpen()) {
                    throw new NoSuchElementException(QueryResultIterator.LOCALISER.msg("052600"));
                }
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException("No previous element");
                }
                --this.iterRowNum;
                return ScrollableQueryResult.this.getObjectForIndex(this.iterRowNum);
            }
        }
        
        @Override
        public int previousIndex() {
            if (ScrollableQueryResult.this.applyRangeChecks) {
                return (int)((this.iterRowNum == ScrollableQueryResult.this.query.getRangeFromIncl()) ? -1L : (this.iterRowNum - ScrollableQueryResult.this.query.getRangeFromIncl() - 1L));
            }
            return (this.iterRowNum == 0) ? -1 : (this.iterRowNum - 1);
        }
    }
}
