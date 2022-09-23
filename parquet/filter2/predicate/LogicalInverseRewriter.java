// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter2.predicate;

import parquet.Preconditions;

public final class LogicalInverseRewriter implements FilterPredicate.Visitor<FilterPredicate>
{
    private static final LogicalInverseRewriter INSTANCE;
    
    public static FilterPredicate rewrite(final FilterPredicate pred) {
        Preconditions.checkNotNull(pred, "pred");
        return pred.accept((FilterPredicate.Visitor<FilterPredicate>)LogicalInverseRewriter.INSTANCE);
    }
    
    private LogicalInverseRewriter() {
    }
    
    @Override
    public <T extends Comparable<T>> FilterPredicate visit(final Operators.Eq<T> eq) {
        return eq;
    }
    
    @Override
    public <T extends Comparable<T>> FilterPredicate visit(final Operators.NotEq<T> notEq) {
        return notEq;
    }
    
    @Override
    public <T extends Comparable<T>> FilterPredicate visit(final Operators.Lt<T> lt) {
        return lt;
    }
    
    @Override
    public <T extends Comparable<T>> FilterPredicate visit(final Operators.LtEq<T> ltEq) {
        return ltEq;
    }
    
    @Override
    public <T extends Comparable<T>> FilterPredicate visit(final Operators.Gt<T> gt) {
        return gt;
    }
    
    @Override
    public <T extends Comparable<T>> FilterPredicate visit(final Operators.GtEq<T> gtEq) {
        return gtEq;
    }
    
    @Override
    public FilterPredicate visit(final Operators.And and) {
        return FilterApi.and(and.getLeft().accept((FilterPredicate.Visitor<FilterPredicate>)this), and.getRight().accept((FilterPredicate.Visitor<FilterPredicate>)this));
    }
    
    @Override
    public FilterPredicate visit(final Operators.Or or) {
        return FilterApi.or(or.getLeft().accept((FilterPredicate.Visitor<FilterPredicate>)this), or.getRight().accept((FilterPredicate.Visitor<FilterPredicate>)this));
    }
    
    @Override
    public FilterPredicate visit(final Operators.Not not) {
        return LogicalInverter.invert(not.getPredicate().accept((FilterPredicate.Visitor<FilterPredicate>)this));
    }
    
    @Override
    public <T extends Comparable<T>, U extends UserDefinedPredicate<T>> FilterPredicate visit(final Operators.UserDefined<T, U> udp) {
        return udp;
    }
    
    @Override
    public <T extends Comparable<T>, U extends UserDefinedPredicate<T>> FilterPredicate visit(final Operators.LogicalNotUserDefined<T, U> udp) {
        return udp;
    }
    
    static {
        INSTANCE = new LogicalInverseRewriter();
    }
}
