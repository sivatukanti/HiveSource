// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter2.recordlevel;

import parquet.filter2.predicate.Operators;
import java.util.ArrayList;
import parquet.Preconditions;
import java.util.HashMap;
import java.util.List;
import parquet.hadoop.metadata.ColumnPath;
import java.util.Map;
import parquet.filter2.predicate.FilterPredicate;

public abstract class IncrementallyUpdatedFilterPredicateBuilderBase implements FilterPredicate.Visitor<IncrementallyUpdatedFilterPredicate>
{
    private boolean built;
    private final Map<ColumnPath, List<IncrementallyUpdatedFilterPredicate.ValueInspector>> valueInspectorsByColumn;
    
    public IncrementallyUpdatedFilterPredicateBuilderBase() {
        this.built = false;
        this.valueInspectorsByColumn = new HashMap<ColumnPath, List<IncrementallyUpdatedFilterPredicate.ValueInspector>>();
    }
    
    public final IncrementallyUpdatedFilterPredicate build(final FilterPredicate pred) {
        Preconditions.checkArgument(!this.built, "This builder has already been used");
        final IncrementallyUpdatedFilterPredicate incremental = pred.accept((FilterPredicate.Visitor<IncrementallyUpdatedFilterPredicate>)this);
        this.built = true;
        return incremental;
    }
    
    protected final void addValueInspector(final ColumnPath columnPath, final IncrementallyUpdatedFilterPredicate.ValueInspector valueInspector) {
        List<IncrementallyUpdatedFilterPredicate.ValueInspector> valueInspectors = this.valueInspectorsByColumn.get(columnPath);
        if (valueInspectors == null) {
            valueInspectors = new ArrayList<IncrementallyUpdatedFilterPredicate.ValueInspector>();
            this.valueInspectorsByColumn.put(columnPath, valueInspectors);
        }
        valueInspectors.add(valueInspector);
    }
    
    public Map<ColumnPath, List<IncrementallyUpdatedFilterPredicate.ValueInspector>> getValueInspectorsByColumn() {
        return this.valueInspectorsByColumn;
    }
    
    @Override
    public final IncrementallyUpdatedFilterPredicate visit(final Operators.And and) {
        return new IncrementallyUpdatedFilterPredicate.And(and.getLeft().accept((FilterPredicate.Visitor<IncrementallyUpdatedFilterPredicate>)this), and.getRight().accept((FilterPredicate.Visitor<IncrementallyUpdatedFilterPredicate>)this));
    }
    
    @Override
    public final IncrementallyUpdatedFilterPredicate visit(final Operators.Or or) {
        return new IncrementallyUpdatedFilterPredicate.Or(or.getLeft().accept((FilterPredicate.Visitor<IncrementallyUpdatedFilterPredicate>)this), or.getRight().accept((FilterPredicate.Visitor<IncrementallyUpdatedFilterPredicate>)this));
    }
    
    @Override
    public final IncrementallyUpdatedFilterPredicate visit(final Operators.Not not) {
        throw new IllegalArgumentException("This predicate contains a not! Did you forget to run this predicate through LogicalInverseRewriter? " + not);
    }
}
