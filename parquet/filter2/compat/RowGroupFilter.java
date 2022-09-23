// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter2.compat;

import java.util.Iterator;
import parquet.filter2.predicate.FilterPredicate;
import parquet.filter2.statisticslevel.StatisticsFilter;
import java.util.ArrayList;
import parquet.filter2.predicate.SchemaCompatibilityValidator;
import parquet.Preconditions;
import parquet.schema.MessageType;
import parquet.hadoop.metadata.BlockMetaData;
import java.util.List;

public class RowGroupFilter implements FilterCompat.Visitor<List<BlockMetaData>>
{
    private final List<BlockMetaData> blocks;
    private final MessageType schema;
    
    public static List<BlockMetaData> filterRowGroups(final FilterCompat.Filter filter, final List<BlockMetaData> blocks, final MessageType schema) {
        Preconditions.checkNotNull(filter, "filter");
        return filter.accept((FilterCompat.Visitor<List<BlockMetaData>>)new RowGroupFilter(blocks, schema));
    }
    
    private RowGroupFilter(final List<BlockMetaData> blocks, final MessageType schema) {
        this.blocks = Preconditions.checkNotNull(blocks, "blocks");
        this.schema = Preconditions.checkNotNull(schema, "schema");
    }
    
    @Override
    public List<BlockMetaData> visit(final FilterCompat.FilterPredicateCompat filterPredicateCompat) {
        final FilterPredicate filterPredicate = filterPredicateCompat.getFilterPredicate();
        SchemaCompatibilityValidator.validate(filterPredicate, this.schema);
        final List<BlockMetaData> filteredBlocks = new ArrayList<BlockMetaData>();
        for (final BlockMetaData block : this.blocks) {
            if (!StatisticsFilter.canDrop(filterPredicate, block.getColumns())) {
                filteredBlocks.add(block);
            }
        }
        return filteredBlocks;
    }
    
    @Override
    public List<BlockMetaData> visit(final FilterCompat.UnboundRecordFilterCompat unboundRecordFilterCompat) {
        return this.blocks;
    }
    
    @Override
    public List<BlockMetaData> visit(final FilterCompat.NoOpFilter noOpFilter) {
        return this.blocks;
    }
}
