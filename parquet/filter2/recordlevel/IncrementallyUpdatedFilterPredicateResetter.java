// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter2.recordlevel;

import parquet.Preconditions;

public final class IncrementallyUpdatedFilterPredicateResetter implements IncrementallyUpdatedFilterPredicate.Visitor
{
    private static final IncrementallyUpdatedFilterPredicateResetter INSTANCE;
    
    public static void reset(final IncrementallyUpdatedFilterPredicate pred) {
        Preconditions.checkNotNull(pred, "pred");
        pred.accept(IncrementallyUpdatedFilterPredicateResetter.INSTANCE);
    }
    
    private IncrementallyUpdatedFilterPredicateResetter() {
    }
    
    @Override
    public boolean visit(final IncrementallyUpdatedFilterPredicate.ValueInspector p) {
        p.reset();
        return false;
    }
    
    @Override
    public boolean visit(final IncrementallyUpdatedFilterPredicate.And and) {
        and.getLeft().accept(this);
        and.getRight().accept(this);
        return false;
    }
    
    @Override
    public boolean visit(final IncrementallyUpdatedFilterPredicate.Or or) {
        or.getLeft().accept(this);
        or.getRight().accept(this);
        return false;
    }
    
    static {
        INSTANCE = new IncrementallyUpdatedFilterPredicateResetter();
    }
}
