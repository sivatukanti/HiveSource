// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter2.recordlevel;

import parquet.Preconditions;

public class IncrementallyUpdatedFilterPredicateEvaluator implements IncrementallyUpdatedFilterPredicate.Visitor
{
    private static final IncrementallyUpdatedFilterPredicateEvaluator INSTANCE;
    
    public static boolean evaluate(final IncrementallyUpdatedFilterPredicate pred) {
        Preconditions.checkNotNull(pred, "pred");
        return pred.accept(IncrementallyUpdatedFilterPredicateEvaluator.INSTANCE);
    }
    
    private IncrementallyUpdatedFilterPredicateEvaluator() {
    }
    
    @Override
    public boolean visit(final IncrementallyUpdatedFilterPredicate.ValueInspector p) {
        if (!p.isKnown()) {
            p.updateNull();
        }
        return p.getResult();
    }
    
    @Override
    public boolean visit(final IncrementallyUpdatedFilterPredicate.And and) {
        return and.getLeft().accept(this) && and.getRight().accept(this);
    }
    
    @Override
    public boolean visit(final IncrementallyUpdatedFilterPredicate.Or or) {
        return or.getLeft().accept(this) || or.getRight().accept(this);
    }
    
    static {
        INSTANCE = new IncrementallyUpdatedFilterPredicateEvaluator();
    }
}
