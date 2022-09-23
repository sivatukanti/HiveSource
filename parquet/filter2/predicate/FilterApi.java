// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter2.predicate;

import parquet.hadoop.metadata.ColumnPath;

public final class FilterApi
{
    private FilterApi() {
    }
    
    public static Operators.IntColumn intColumn(final String columnPath) {
        return new Operators.IntColumn(ColumnPath.fromDotString(columnPath));
    }
    
    public static Operators.LongColumn longColumn(final String columnPath) {
        return new Operators.LongColumn(ColumnPath.fromDotString(columnPath));
    }
    
    public static Operators.FloatColumn floatColumn(final String columnPath) {
        return new Operators.FloatColumn(ColumnPath.fromDotString(columnPath));
    }
    
    public static Operators.DoubleColumn doubleColumn(final String columnPath) {
        return new Operators.DoubleColumn(ColumnPath.fromDotString(columnPath));
    }
    
    public static Operators.BooleanColumn booleanColumn(final String columnPath) {
        return new Operators.BooleanColumn(ColumnPath.fromDotString(columnPath));
    }
    
    public static Operators.BinaryColumn binaryColumn(final String columnPath) {
        return new Operators.BinaryColumn(ColumnPath.fromDotString(columnPath));
    }
    
    public static <T extends Comparable<T>, C extends Column> Operators.Eq<T> eq(final C column, final T value) {
        return new Operators.Eq<T>((Operators.Column<T>)column, value);
    }
    
    public static <T extends Comparable<T>, C extends parquet.filter2.predicate.Operators.Column> Operators.NotEq<T> notEq(final C column, final T value) {
        return new Operators.NotEq<T>((Operators.Column<T>)column, value);
    }
    
    public static <T extends Comparable<T>, C extends parquet.filter2.predicate.Operators.Column> Operators.Lt<T> lt(final C column, final T value) {
        return new Operators.Lt<T>((Operators.Column<T>)column, value);
    }
    
    public static <T extends Comparable<T>, C extends parquet.filter2.predicate.Operators.Column> Operators.LtEq<T> ltEq(final C column, final T value) {
        return new Operators.LtEq<T>((Operators.Column<T>)column, value);
    }
    
    public static <T extends Comparable<T>, C extends parquet.filter2.predicate.Operators.Column> Operators.Gt<T> gt(final C column, final T value) {
        return new Operators.Gt<T>((Operators.Column<T>)column, value);
    }
    
    public static <T extends Comparable<T>, C extends parquet.filter2.predicate.Operators.Column> Operators.GtEq<T> gtEq(final C column, final T value) {
        return new Operators.GtEq<T>((Operators.Column<T>)column, value);
    }
    
    public static <T extends Comparable<T>, U extends UserDefinedPredicate<T>> Operators.UserDefined<T, U> userDefined(final Operators.Column<T> column, final Class<U> clazz) {
        return new Operators.UserDefinedByClass<T, U>(column, clazz);
    }
    
    public static <T extends Comparable<T>, U extends parquet.filter2.predicate.UserDefinedPredicate> Operators.UserDefined<T, U> userDefined(final Operators.Column<T> column, final U udp) {
        return new Operators.UserDefinedByInstance<T, U>(column, udp);
    }
    
    public static FilterPredicate and(final FilterPredicate left, final FilterPredicate right) {
        return new Operators.And(left, right);
    }
    
    public static FilterPredicate or(final FilterPredicate left, final FilterPredicate right) {
        return new Operators.Or(left, right);
    }
    
    public static FilterPredicate not(final FilterPredicate predicate) {
        return new Operators.Not(predicate);
    }
}
