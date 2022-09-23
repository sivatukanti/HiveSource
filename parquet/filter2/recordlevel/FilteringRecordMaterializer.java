// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter2.recordlevel;

import parquet.io.api.GroupConverter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.util.HashMap;
import parquet.Preconditions;
import parquet.hadoop.metadata.ColumnPath;
import java.util.Map;
import parquet.io.PrimitiveColumnIO;
import java.util.List;
import parquet.io.api.RecordMaterializer;

public class FilteringRecordMaterializer<T> extends RecordMaterializer<T>
{
    private final RecordMaterializer<T> delegate;
    private final FilteringGroupConverter rootConverter;
    private final IncrementallyUpdatedFilterPredicate filterPredicate;
    
    public FilteringRecordMaterializer(final RecordMaterializer<T> delegate, final List<PrimitiveColumnIO> columnIOs, final Map<ColumnPath, List<IncrementallyUpdatedFilterPredicate.ValueInspector>> valueInspectorsByColumn, final IncrementallyUpdatedFilterPredicate filterPredicate) {
        Preconditions.checkNotNull(columnIOs, "columnIOs");
        Preconditions.checkNotNull(valueInspectorsByColumn, "valueInspectorsByColumn");
        this.filterPredicate = Preconditions.checkNotNull(filterPredicate, "filterPredicate");
        this.delegate = Preconditions.checkNotNull(delegate, "delegate");
        final Map<List<Integer>, PrimitiveColumnIO> columnIOsByIndexFieldPath = new HashMap<List<Integer>, PrimitiveColumnIO>();
        for (final PrimitiveColumnIO c : columnIOs) {
            columnIOsByIndexFieldPath.put(getIndexFieldPathList(c), c);
        }
        this.rootConverter = new FilteringGroupConverter(delegate.getRootConverter(), Collections.emptyList(), valueInspectorsByColumn, columnIOsByIndexFieldPath);
    }
    
    public static List<Integer> getIndexFieldPathList(final PrimitiveColumnIO c) {
        return intArrayToList(c.getIndexFieldPath());
    }
    
    public static List<Integer> intArrayToList(final int[] arr) {
        final List<Integer> list = new ArrayList<Integer>(arr.length);
        for (final int i : arr) {
            list.add(i);
        }
        return list;
    }
    
    @Override
    public T getCurrentRecord() {
        final boolean keep = IncrementallyUpdatedFilterPredicateEvaluator.evaluate(this.filterPredicate);
        IncrementallyUpdatedFilterPredicateResetter.reset(this.filterPredicate);
        if (keep) {
            return this.delegate.getCurrentRecord();
        }
        return null;
    }
    
    @Override
    public void skipCurrentRecord() {
        this.delegate.skipCurrentRecord();
    }
    
    @Override
    public GroupConverter getRootConverter() {
        return this.rootConverter;
    }
}
