// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter2.predicate;

import parquet.Preconditions;

public final class LogicalInverter implements FilterPredicate.Visitor<FilterPredicate>
{
    private static final LogicalInverter INSTANCE;
    
    public static FilterPredicate invert(final FilterPredicate pred) {
        Preconditions.checkNotNull(pred, "pred");
        return pred.accept((FilterPredicate.Visitor<FilterPredicate>)LogicalInverter.INSTANCE);
    }
    
    private LogicalInverter() {
    }
    
    @Override
    public <T extends Comparable<T>> FilterPredicate visit(final Operators.Eq<T> eq) {
        return new Operators.NotEq<Object>(eq.getColumn(), eq.getValue());
    }
    
    @Override
    public <T extends Comparable<T>> FilterPredicate visit(final Operators.NotEq<T> notEq) {
        return new Operators.Eq<Object>(notEq.getColumn(), notEq.getValue());
    }
    
    @Override
    public <T extends Comparable<T>> FilterPredicate visit(final Operators.Lt<T> lt) {
        return new Operators.GtEq<Object>(lt.getColumn(), lt.getValue());
    }
    
    @Override
    public <T extends Comparable<T>> FilterPredicate visit(final Operators.LtEq<T> ltEq) {
        return new Operators.Gt<Object>(ltEq.getColumn(), ltEq.getValue());
    }
    
    @Override
    public <T extends Comparable<T>> FilterPredicate visit(final Operators.Gt<T> gt) {
        return new Operators.LtEq<Object>(gt.getColumn(), gt.getValue());
    }
    
    @Override
    public <T extends Comparable<T>> FilterPredicate visit(final Operators.GtEq<T> gtEq) {
        return new Operators.Lt<Object>(gtEq.getColumn(), gtEq.getValue());
    }
    
    @Override
    public FilterPredicate visit(final Operators.And and) {
        return new Operators.Or(and.getLeft().accept((FilterPredicate.Visitor<FilterPredicate>)this), and.getRight().accept((FilterPredicate.Visitor<FilterPredicate>)this));
    }
    
    @Override
    public FilterPredicate visit(final Operators.Or or) {
        return new Operators.And(or.getLeft().accept((FilterPredicate.Visitor<FilterPredicate>)this), or.getRight().accept((FilterPredicate.Visitor<FilterPredicate>)this));
    }
    
    @Override
    public FilterPredicate visit(final Operators.Not not) {
        return not.getPredicate();
    }
    
    @Override
    public <T extends Comparable<T>, U extends UserDefinedPredicate<T>> FilterPredicate visit(final Operators.UserDefined<T, U> udp) {
        return new Operators.LogicalNotUserDefined<Object, Object>(udp);
    }
    
    @Override
    public <T extends Comparable<T>, U extends UserDefinedPredicate<T>> FilterPredicate visit(final Operators.LogicalNotUserDefined<T, U> udp) {
        return udp.getUserDefined();
    }
    
    static {
        INSTANCE = new LogicalInverter();
    }
}
