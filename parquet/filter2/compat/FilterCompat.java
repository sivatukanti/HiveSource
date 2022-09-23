// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter2.compat;

import parquet.filter.UnboundRecordFilter;
import parquet.filter2.predicate.LogicalInverseRewriter;
import parquet.Preconditions;
import parquet.filter2.predicate.FilterPredicate;
import parquet.Log;

public class FilterCompat
{
    private static final Log LOG;
    public static final Filter NOOP;
    
    public static Filter get(final FilterPredicate filterPredicate) {
        Preconditions.checkNotNull(filterPredicate, "filterPredicate");
        FilterCompat.LOG.info("Filtering using predicate: " + filterPredicate);
        final FilterPredicate collapsedPredicate = LogicalInverseRewriter.rewrite(filterPredicate);
        if (!filterPredicate.equals(collapsedPredicate)) {
            FilterCompat.LOG.info("Predicate has been collapsed to: " + collapsedPredicate);
        }
        return new FilterPredicateCompat(collapsedPredicate);
    }
    
    public static Filter get(final UnboundRecordFilter unboundRecordFilter) {
        return new UnboundRecordFilterCompat(unboundRecordFilter);
    }
    
    public static Filter get(final FilterPredicate filterPredicate, final UnboundRecordFilter unboundRecordFilter) {
        Preconditions.checkArgument(filterPredicate == null || unboundRecordFilter == null, "Cannot provide both a FilterPredicate and an UnboundRecordFilter");
        if (filterPredicate != null) {
            return get(filterPredicate);
        }
        if (unboundRecordFilter != null) {
            return get(unboundRecordFilter);
        }
        return FilterCompat.NOOP;
    }
    
    static {
        LOG = Log.getLog(FilterCompat.class);
        NOOP = new NoOpFilter();
    }
    
    public static final class FilterPredicateCompat implements Filter
    {
        private final FilterPredicate filterPredicate;
        
        private FilterPredicateCompat(final FilterPredicate filterPredicate) {
            this.filterPredicate = Preconditions.checkNotNull(filterPredicate, "filterPredicate");
        }
        
        public FilterPredicate getFilterPredicate() {
            return this.filterPredicate;
        }
        
        @Override
        public <R> R accept(final Visitor<R> visitor) {
            return visitor.visit(this);
        }
    }
    
    public static final class UnboundRecordFilterCompat implements Filter
    {
        private final UnboundRecordFilter unboundRecordFilter;
        
        private UnboundRecordFilterCompat(final UnboundRecordFilter unboundRecordFilter) {
            this.unboundRecordFilter = Preconditions.checkNotNull(unboundRecordFilter, "unboundRecordFilter");
        }
        
        public UnboundRecordFilter getUnboundRecordFilter() {
            return this.unboundRecordFilter;
        }
        
        @Override
        public <R> R accept(final Visitor<R> visitor) {
            return visitor.visit(this);
        }
    }
    
    public static final class NoOpFilter implements Filter
    {
        private NoOpFilter() {
        }
        
        @Override
        public <R> R accept(final Visitor<R> visitor) {
            return visitor.visit(this);
        }
    }
    
    public interface Visitor<T>
    {
        T visit(final FilterPredicateCompat p0);
        
        T visit(final UnboundRecordFilterCompat p0);
        
        T visit(final NoOpFilter p0);
    }
    
    public interface Filter
    {
         <R> R accept(final Visitor<R> p0);
    }
}
