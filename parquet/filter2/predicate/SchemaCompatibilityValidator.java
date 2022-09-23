// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter2.predicate;

import java.util.Iterator;
import java.util.HashMap;
import parquet.Preconditions;
import parquet.schema.MessageType;
import parquet.schema.OriginalType;
import parquet.column.ColumnDescriptor;
import parquet.hadoop.metadata.ColumnPath;
import java.util.Map;

public class SchemaCompatibilityValidator implements FilterPredicate.Visitor<Void>
{
    private final Map<ColumnPath, Class<?>> columnTypesEncountered;
    private final Map<ColumnPath, ColumnDescriptor> columnsAccordingToSchema;
    private final Map<ColumnPath, OriginalType> originalTypes;
    
    public static void validate(final FilterPredicate predicate, final MessageType schema) {
        Preconditions.checkNotNull(predicate, "predicate");
        Preconditions.checkNotNull(schema, "schema");
        predicate.accept((FilterPredicate.Visitor<Object>)new SchemaCompatibilityValidator(schema));
    }
    
    private SchemaCompatibilityValidator(final MessageType schema) {
        this.columnTypesEncountered = new HashMap<ColumnPath, Class<?>>();
        this.columnsAccordingToSchema = new HashMap<ColumnPath, ColumnDescriptor>();
        this.originalTypes = new HashMap<ColumnPath, OriginalType>();
        for (final ColumnDescriptor cd : schema.getColumns()) {
            final ColumnPath columnPath = ColumnPath.get(cd.getPath());
            this.columnsAccordingToSchema.put(columnPath, cd);
            final OriginalType ot = schema.getType(cd.getPath()).getOriginalType();
            if (ot != null) {
                this.originalTypes.put(columnPath, ot);
            }
        }
    }
    
    @Override
    public <T extends Comparable<T>> Void visit(final Operators.Eq<T> pred) {
        this.validateColumnFilterPredicate(pred);
        return null;
    }
    
    @Override
    public <T extends Comparable<T>> Void visit(final Operators.NotEq<T> pred) {
        this.validateColumnFilterPredicate(pred);
        return null;
    }
    
    @Override
    public <T extends Comparable<T>> Void visit(final Operators.Lt<T> pred) {
        this.validateColumnFilterPredicate(pred);
        return null;
    }
    
    @Override
    public <T extends Comparable<T>> Void visit(final Operators.LtEq<T> pred) {
        this.validateColumnFilterPredicate(pred);
        return null;
    }
    
    @Override
    public <T extends Comparable<T>> Void visit(final Operators.Gt<T> pred) {
        this.validateColumnFilterPredicate(pred);
        return null;
    }
    
    @Override
    public <T extends Comparable<T>> Void visit(final Operators.GtEq<T> pred) {
        this.validateColumnFilterPredicate(pred);
        return null;
    }
    
    @Override
    public Void visit(final Operators.And and) {
        and.getLeft().accept((FilterPredicate.Visitor<Object>)this);
        and.getRight().accept((FilterPredicate.Visitor<Object>)this);
        return null;
    }
    
    @Override
    public Void visit(final Operators.Or or) {
        or.getLeft().accept((FilterPredicate.Visitor<Object>)this);
        or.getRight().accept((FilterPredicate.Visitor<Object>)this);
        return null;
    }
    
    @Override
    public Void visit(final Operators.Not not) {
        not.getPredicate().accept((FilterPredicate.Visitor<Object>)this);
        return null;
    }
    
    @Override
    public <T extends Comparable<T>, U extends UserDefinedPredicate<T>> Void visit(final Operators.UserDefined<T, U> udp) {
        this.validateColumn(udp.getColumn());
        return null;
    }
    
    @Override
    public <T extends Comparable<T>, U extends UserDefinedPredicate<T>> Void visit(final Operators.LogicalNotUserDefined<T, U> udp) {
        return udp.getUserDefined().accept((FilterPredicate.Visitor<Void>)this);
    }
    
    private <T extends Comparable<T>> void validateColumnFilterPredicate(final Operators.ColumnFilterPredicate<T> pred) {
        this.validateColumn(pred.getColumn());
    }
    
    private <T extends Comparable<T>> void validateColumn(final Operators.Column<T> column) {
        final ColumnPath path = column.getColumnPath();
        final Class<?> alreadySeen = this.columnTypesEncountered.get(path);
        if (alreadySeen != null && !alreadySeen.equals(column.getColumnType())) {
            throw new IllegalArgumentException("Column: " + path.toDotString() + " was provided with different types in the same predicate." + " Found both: (" + alreadySeen + ", " + column.getColumnType() + ")");
        }
        if (alreadySeen == null) {
            this.columnTypesEncountered.put(path, column.getColumnType());
        }
        final ColumnDescriptor descriptor = this.getColumnDescriptor(path);
        if (descriptor.getMaxRepetitionLevel() > 0) {
            throw new IllegalArgumentException("FilterPredicates do not currently support repeated columns. Column " + path.toDotString() + " is repeated.");
        }
        ValidTypeMap.assertTypeValid(column, descriptor.getType(), this.originalTypes.get(path));
    }
    
    private ColumnDescriptor getColumnDescriptor(final ColumnPath columnPath) {
        final ColumnDescriptor cd = this.columnsAccordingToSchema.get(columnPath);
        Preconditions.checkArgument(cd != null, "Column " + columnPath + " was not found in schema!");
        return cd;
    }
}
