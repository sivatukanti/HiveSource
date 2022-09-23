// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter2.recordlevel;

import java.util.Collection;
import java.util.ArrayList;
import parquet.io.api.Converter;
import parquet.Preconditions;
import parquet.io.PrimitiveColumnIO;
import parquet.hadoop.metadata.ColumnPath;
import java.util.Map;
import java.util.List;
import parquet.io.api.GroupConverter;

public class FilteringGroupConverter extends GroupConverter
{
    private final GroupConverter delegate;
    private final List<Integer> indexFieldPath;
    private final Map<ColumnPath, List<IncrementallyUpdatedFilterPredicate.ValueInspector>> valueInspectorsByColumn;
    private final Map<List<Integer>, PrimitiveColumnIO> columnIOsByIndexFieldPath;
    
    public FilteringGroupConverter(final GroupConverter delegate, final List<Integer> indexFieldPath, final Map<ColumnPath, List<IncrementallyUpdatedFilterPredicate.ValueInspector>> valueInspectorsByColumn, final Map<List<Integer>, PrimitiveColumnIO> columnIOsByIndexFieldPath) {
        this.delegate = Preconditions.checkNotNull(delegate, "delegate");
        this.indexFieldPath = Preconditions.checkNotNull(indexFieldPath, "indexFieldPath");
        this.columnIOsByIndexFieldPath = Preconditions.checkNotNull(columnIOsByIndexFieldPath, "columnIOsByIndexFieldPath");
        this.valueInspectorsByColumn = Preconditions.checkNotNull(valueInspectorsByColumn, "valueInspectorsByColumn");
    }
    
    @Override
    public Converter getConverter(final int fieldIndex) {
        final Converter delegateConverter = Preconditions.checkNotNull(this.delegate.getConverter(fieldIndex), "delegate converter");
        final List<Integer> newIndexFieldPath = new ArrayList<Integer>(this.indexFieldPath.size() + 1);
        newIndexFieldPath.addAll(this.indexFieldPath);
        newIndexFieldPath.add(fieldIndex);
        if (delegateConverter.isPrimitive()) {
            final PrimitiveColumnIO columnIO = this.getColumnIO(newIndexFieldPath);
            final ColumnPath columnPath = ColumnPath.get(columnIO.getColumnDescriptor().getPath());
            final IncrementallyUpdatedFilterPredicate.ValueInspector[] valueInspectors = this.getValueInspectors(columnPath);
            return new FilteringPrimitiveConverter(delegateConverter.asPrimitiveConverter(), valueInspectors);
        }
        return new FilteringGroupConverter(delegateConverter.asGroupConverter(), newIndexFieldPath, this.valueInspectorsByColumn, this.columnIOsByIndexFieldPath);
    }
    
    private PrimitiveColumnIO getColumnIO(final List<Integer> indexFieldPath) {
        final PrimitiveColumnIO found = this.columnIOsByIndexFieldPath.get(indexFieldPath);
        Preconditions.checkArgument(found != null, "Did not find PrimitiveColumnIO for index field path" + indexFieldPath);
        return found;
    }
    
    private IncrementallyUpdatedFilterPredicate.ValueInspector[] getValueInspectors(final ColumnPath columnPath) {
        final List<IncrementallyUpdatedFilterPredicate.ValueInspector> inspectorsList = this.valueInspectorsByColumn.get(columnPath);
        if (inspectorsList == null) {
            return new IncrementallyUpdatedFilterPredicate.ValueInspector[0];
        }
        return inspectorsList.toArray(new IncrementallyUpdatedFilterPredicate.ValueInspector[inspectorsList.size()]);
    }
    
    @Override
    public void start() {
        this.delegate.start();
    }
    
    @Override
    public void end() {
        this.delegate.end();
    }
}
