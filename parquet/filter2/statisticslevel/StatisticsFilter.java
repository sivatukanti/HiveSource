// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter2.statisticslevel;

import parquet.filter2.predicate.UserDefinedPredicate;
import parquet.column.statistics.Statistics;
import parquet.filter2.predicate.Operators;
import java.util.Iterator;
import java.util.HashMap;
import parquet.Preconditions;
import java.util.List;
import parquet.hadoop.metadata.ColumnChunkMetaData;
import parquet.hadoop.metadata.ColumnPath;
import java.util.Map;
import parquet.filter2.predicate.FilterPredicate;

public class StatisticsFilter implements FilterPredicate.Visitor<Boolean>
{
    private final Map<ColumnPath, ColumnChunkMetaData> columns;
    
    public static boolean canDrop(final FilterPredicate pred, final List<ColumnChunkMetaData> columns) {
        Preconditions.checkNotNull(pred, "pred");
        Preconditions.checkNotNull(columns, "columns");
        return pred.accept((FilterPredicate.Visitor<Boolean>)new StatisticsFilter(columns));
    }
    
    private StatisticsFilter(final List<ColumnChunkMetaData> columnsList) {
        this.columns = new HashMap<ColumnPath, ColumnChunkMetaData>();
        for (final ColumnChunkMetaData chunk : columnsList) {
            this.columns.put(chunk.getPath(), chunk);
        }
    }
    
    private ColumnChunkMetaData getColumnChunk(final ColumnPath columnPath) {
        final ColumnChunkMetaData c = this.columns.get(columnPath);
        Preconditions.checkArgument(c != null, "Column " + columnPath.toDotString() + " not found in schema!");
        return c;
    }
    
    private boolean isAllNulls(final ColumnChunkMetaData column) {
        return column.getStatistics().getNumNulls() == column.getValueCount();
    }
    
    private boolean hasNulls(final ColumnChunkMetaData column) {
        return column.getStatistics().getNumNulls() > 0L;
    }
    
    @Override
    public <T extends Comparable<T>> Boolean visit(final Operators.Eq<T> eq) {
        final Operators.Column<T> filterColumn = (Operators.Column<T>)eq.getColumn();
        final T value = (T)eq.getValue();
        final ColumnChunkMetaData columnChunk = this.getColumnChunk(filterColumn.getColumnPath());
        final Statistics<T> stats = (Statistics<T>)columnChunk.getStatistics();
        if (stats.isEmpty()) {
            return false;
        }
        if (value == null) {
            return !this.hasNulls(columnChunk);
        }
        if (this.isAllNulls(columnChunk)) {
            return true;
        }
        return value.compareTo(stats.genericGetMin()) < 0 || value.compareTo(stats.genericGetMax()) > 0;
    }
    
    @Override
    public <T extends Comparable<T>> Boolean visit(final Operators.NotEq<T> notEq) {
        final Operators.Column<T> filterColumn = (Operators.Column<T>)notEq.getColumn();
        final T value = (T)notEq.getValue();
        final ColumnChunkMetaData columnChunk = this.getColumnChunk(filterColumn.getColumnPath());
        final Statistics<T> stats = (Statistics<T>)columnChunk.getStatistics();
        if (stats.isEmpty()) {
            return false;
        }
        if (value == null) {
            return this.isAllNulls(columnChunk);
        }
        if (this.hasNulls(columnChunk)) {
            return false;
        }
        return value.compareTo(stats.genericGetMin()) == 0 && value.compareTo(stats.genericGetMax()) == 0;
    }
    
    @Override
    public <T extends Comparable<T>> Boolean visit(final Operators.Lt<T> lt) {
        final Operators.Column<T> filterColumn = (Operators.Column<T>)lt.getColumn();
        final T value = (T)lt.getValue();
        final ColumnChunkMetaData columnChunk = this.getColumnChunk(filterColumn.getColumnPath());
        final Statistics<T> stats = (Statistics<T>)columnChunk.getStatistics();
        if (stats.isEmpty()) {
            return false;
        }
        if (this.isAllNulls(columnChunk)) {
            return true;
        }
        return value.compareTo(stats.genericGetMin()) <= 0;
    }
    
    @Override
    public <T extends Comparable<T>> Boolean visit(final Operators.LtEq<T> ltEq) {
        final Operators.Column<T> filterColumn = (Operators.Column<T>)ltEq.getColumn();
        final T value = (T)ltEq.getValue();
        final ColumnChunkMetaData columnChunk = this.getColumnChunk(filterColumn.getColumnPath());
        final Statistics<T> stats = (Statistics<T>)columnChunk.getStatistics();
        if (stats.isEmpty()) {
            return false;
        }
        if (this.isAllNulls(columnChunk)) {
            return true;
        }
        return value.compareTo(stats.genericGetMin()) < 0;
    }
    
    @Override
    public <T extends Comparable<T>> Boolean visit(final Operators.Gt<T> gt) {
        final Operators.Column<T> filterColumn = (Operators.Column<T>)gt.getColumn();
        final T value = (T)gt.getValue();
        final ColumnChunkMetaData columnChunk = this.getColumnChunk(filterColumn.getColumnPath());
        final Statistics<T> stats = (Statistics<T>)columnChunk.getStatistics();
        if (stats.isEmpty()) {
            return false;
        }
        if (this.isAllNulls(columnChunk)) {
            return true;
        }
        return value.compareTo(stats.genericGetMax()) >= 0;
    }
    
    @Override
    public <T extends Comparable<T>> Boolean visit(final Operators.GtEq<T> gtEq) {
        final Operators.Column<T> filterColumn = (Operators.Column<T>)gtEq.getColumn();
        final T value = (T)gtEq.getValue();
        final ColumnChunkMetaData columnChunk = this.getColumnChunk(filterColumn.getColumnPath());
        final Statistics<T> stats = (Statistics<T>)columnChunk.getStatistics();
        if (stats.isEmpty()) {
            return false;
        }
        if (this.isAllNulls(columnChunk)) {
            return true;
        }
        return value.compareTo(stats.genericGetMax()) > 0;
    }
    
    @Override
    public Boolean visit(final Operators.And and) {
        return and.getLeft().accept((FilterPredicate.Visitor<Boolean>)this) || and.getRight().accept((FilterPredicate.Visitor<Boolean>)this);
    }
    
    @Override
    public Boolean visit(final Operators.Or or) {
        return or.getLeft().accept((FilterPredicate.Visitor<Boolean>)this) && or.getRight().accept((FilterPredicate.Visitor<Boolean>)this);
    }
    
    @Override
    public Boolean visit(final Operators.Not not) {
        throw new IllegalArgumentException("This predicate contains a not! Did you forget to run this predicate through LogicalInverseRewriter? " + not);
    }
    
    private <T extends Comparable<T>, U extends UserDefinedPredicate<T>> Boolean visit(final Operators.UserDefined<T, U> ud, final boolean inverted) {
        final Operators.Column<T> filterColumn = ud.getColumn();
        final ColumnChunkMetaData columnChunk = this.getColumnChunk(filterColumn.getColumnPath());
        final U udp = ud.getUserDefinedPredicate();
        final Statistics<T> stats = (Statistics<T>)columnChunk.getStatistics();
        if (stats.isEmpty()) {
            return false;
        }
        if (this.isAllNulls(columnChunk)) {
            return false;
        }
        final parquet.filter2.predicate.Statistics<T> udpStats = new parquet.filter2.predicate.Statistics<T>(stats.genericGetMin(), stats.genericGetMax());
        if (inverted) {
            return udp.inverseCanDrop(udpStats);
        }
        return udp.canDrop(udpStats);
    }
    
    @Override
    public <T extends Comparable<T>, U extends UserDefinedPredicate<T>> Boolean visit(final Operators.UserDefined<T, U> ud) {
        return this.visit(ud, false);
    }
    
    @Override
    public <T extends Comparable<T>, U extends UserDefinedPredicate<T>> Boolean visit(final Operators.LogicalNotUserDefined<T, U> lnud) {
        return this.visit(lnud.getUserDefined(), true);
    }
}
