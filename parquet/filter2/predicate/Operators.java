// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter2.predicate;

import parquet.io.api.Binary;
import parquet.Preconditions;
import parquet.hadoop.metadata.ColumnPath;
import java.io.Serializable;

public final class Operators
{
    private Operators() {
    }
    
    public abstract static class Column<T extends Comparable<T>> implements Serializable
    {
        private final ColumnPath columnPath;
        private final Class<T> columnType;
        
        protected Column(final ColumnPath columnPath, final Class<T> columnType) {
            Preconditions.checkNotNull(columnPath, "columnPath");
            Preconditions.checkNotNull(columnType, "columnType");
            this.columnPath = columnPath;
            this.columnType = columnType;
        }
        
        public Class<T> getColumnType() {
            return this.columnType;
        }
        
        public ColumnPath getColumnPath() {
            return this.columnPath;
        }
        
        @Override
        public String toString() {
            return "column(" + this.columnPath.toDotString() + ")";
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final Column column = (Column)o;
            return this.columnType.equals(column.columnType) && this.columnPath.equals(column.columnPath);
        }
        
        @Override
        public int hashCode() {
            int result = this.columnPath.hashCode();
            result = 31 * result + this.columnType.hashCode();
            return result;
        }
    }
    
    public static final class IntColumn extends Column<Integer> implements SupportsLtGt
    {
        IntColumn(final ColumnPath columnPath) {
            super(columnPath, Integer.class);
        }
    }
    
    public static final class LongColumn extends Column<Long> implements SupportsLtGt
    {
        LongColumn(final ColumnPath columnPath) {
            super(columnPath, Long.class);
        }
    }
    
    public static final class DoubleColumn extends Column<Double> implements SupportsLtGt
    {
        DoubleColumn(final ColumnPath columnPath) {
            super(columnPath, Double.class);
        }
    }
    
    public static final class FloatColumn extends Column<Float> implements SupportsLtGt
    {
        FloatColumn(final ColumnPath columnPath) {
            super(columnPath, Float.class);
        }
    }
    
    public static final class BooleanColumn extends Column<Boolean> implements SupportsEqNotEq
    {
        BooleanColumn(final ColumnPath columnPath) {
            super(columnPath, Boolean.class);
        }
    }
    
    public static final class BinaryColumn extends Column<Binary> implements SupportsLtGt
    {
        BinaryColumn(final ColumnPath columnPath) {
            super(columnPath, Binary.class);
        }
    }
    
    abstract static class ColumnFilterPredicate<T extends Comparable<T>> implements FilterPredicate, Serializable
    {
        private final Column<T> column;
        private final T value;
        private final String toString;
        
        protected ColumnFilterPredicate(final Column<T> column, final T value) {
            this.column = Preconditions.checkNotNull(column, "column");
            this.value = value;
            final String name = this.getClass().getSimpleName().toLowerCase();
            this.toString = name + "(" + column.getColumnPath().toDotString() + ", " + value + ")";
        }
        
        public Column<T> getColumn() {
            return this.column;
        }
        
        public T getValue() {
            return this.value;
        }
        
        @Override
        public String toString() {
            return this.toString;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final ColumnFilterPredicate that = (ColumnFilterPredicate)o;
            if (!this.column.equals(that.column)) {
                return false;
            }
            if (this.value != null) {
                if (this.value.equals(that.value)) {
                    return true;
                }
            }
            else if (that.value == null) {
                return true;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            int result = this.column.hashCode();
            result = 31 * result + ((this.value != null) ? this.value.hashCode() : 0);
            result = 31 * result + this.getClass().hashCode();
            return result;
        }
    }
    
    public static final class Eq<T extends Comparable<T>> extends ColumnFilterPredicate<T>
    {
        Eq(final Column<T> column, final T value) {
            super(column, value);
        }
        
        @Override
        public <R> R accept(final FilterPredicate.Visitor<R> visitor) {
            return visitor.visit((Eq<Comparable>)this);
        }
    }
    
    public static final class NotEq<T extends Comparable<T>> extends ColumnFilterPredicate<T>
    {
        NotEq(final Column<T> column, final T value) {
            super(column, value);
        }
        
        @Override
        public <R> R accept(final FilterPredicate.Visitor<R> visitor) {
            return visitor.visit((NotEq<Comparable>)this);
        }
    }
    
    public static final class Lt<T extends Comparable<T>> extends ColumnFilterPredicate<T>
    {
        Lt(final Column<T> column, final T value) {
            super(column, Preconditions.checkNotNull(value, "value"));
        }
        
        @Override
        public <R> R accept(final FilterPredicate.Visitor<R> visitor) {
            return visitor.visit((Lt<Comparable>)this);
        }
    }
    
    public static final class LtEq<T extends Comparable<T>> extends ColumnFilterPredicate<T>
    {
        LtEq(final Column<T> column, final T value) {
            super(column, Preconditions.checkNotNull(value, "value"));
        }
        
        @Override
        public <R> R accept(final FilterPredicate.Visitor<R> visitor) {
            return visitor.visit((LtEq<Comparable>)this);
        }
    }
    
    public static final class Gt<T extends Comparable<T>> extends ColumnFilterPredicate<T>
    {
        Gt(final Column<T> column, final T value) {
            super(column, Preconditions.checkNotNull(value, "value"));
        }
        
        @Override
        public <R> R accept(final FilterPredicate.Visitor<R> visitor) {
            return visitor.visit((Gt<Comparable>)this);
        }
    }
    
    public static final class GtEq<T extends Comparable<T>> extends ColumnFilterPredicate<T>
    {
        GtEq(final Column<T> column, final T value) {
            super(column, Preconditions.checkNotNull(value, "value"));
        }
        
        @Override
        public <R> R accept(final FilterPredicate.Visitor<R> visitor) {
            return visitor.visit((GtEq<Comparable>)this);
        }
    }
    
    private abstract static class BinaryLogicalFilterPredicate implements FilterPredicate, Serializable
    {
        private final FilterPredicate left;
        private final FilterPredicate right;
        private final String toString;
        
        protected BinaryLogicalFilterPredicate(final FilterPredicate left, final FilterPredicate right) {
            this.left = Preconditions.checkNotNull(left, "left");
            this.right = Preconditions.checkNotNull(right, "right");
            final String name = this.getClass().getSimpleName().toLowerCase();
            this.toString = name + "(" + left + ", " + right + ")";
        }
        
        public FilterPredicate getLeft() {
            return this.left;
        }
        
        public FilterPredicate getRight() {
            return this.right;
        }
        
        @Override
        public String toString() {
            return this.toString;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final BinaryLogicalFilterPredicate that = (BinaryLogicalFilterPredicate)o;
            return this.left.equals(that.left) && this.right.equals(that.right);
        }
        
        @Override
        public int hashCode() {
            int result = this.left.hashCode();
            result = 31 * result + this.right.hashCode();
            result = 31 * result + this.getClass().hashCode();
            return result;
        }
    }
    
    public static final class And extends BinaryLogicalFilterPredicate
    {
        And(final FilterPredicate left, final FilterPredicate right) {
            super(left, right);
        }
        
        @Override
        public <R> R accept(final FilterPredicate.Visitor<R> visitor) {
            return visitor.visit(this);
        }
    }
    
    public static final class Or extends BinaryLogicalFilterPredicate
    {
        Or(final FilterPredicate left, final FilterPredicate right) {
            super(left, right);
        }
        
        @Override
        public <R> R accept(final FilterPredicate.Visitor<R> visitor) {
            return visitor.visit(this);
        }
    }
    
    public static class Not implements FilterPredicate, Serializable
    {
        private final FilterPredicate predicate;
        private final String toString;
        
        Not(final FilterPredicate predicate) {
            this.predicate = Preconditions.checkNotNull(predicate, "predicate");
            this.toString = "not(" + predicate + ")";
        }
        
        public FilterPredicate getPredicate() {
            return this.predicate;
        }
        
        @Override
        public String toString() {
            return this.toString;
        }
        
        @Override
        public <R> R accept(final Visitor<R> visitor) {
            return visitor.visit(this);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final Not not = (Not)o;
            return this.predicate.equals(not.predicate);
        }
        
        @Override
        public int hashCode() {
            return this.predicate.hashCode() * 31 + this.getClass().hashCode();
        }
    }
    
    public abstract static class UserDefined<T extends Comparable<T>, U extends UserDefinedPredicate<T>> implements FilterPredicate, Serializable
    {
        protected final Column<T> column;
        
        UserDefined(final Column<T> column) {
            this.column = Preconditions.checkNotNull(column, "column");
        }
        
        public Column<T> getColumn() {
            return this.column;
        }
        
        public abstract U getUserDefinedPredicate();
        
        @Override
        public <R> R accept(final Visitor<R> visitor) {
            return visitor.visit((UserDefined<Comparable, UserDefinedPredicate>)this);
        }
    }
    
    public static final class UserDefinedByClass<T extends Comparable<T>, U extends UserDefinedPredicate<T>> extends UserDefined<T, U>
    {
        private final Class<U> udpClass;
        private final String toString;
        private static final String INSTANTIATION_ERROR_MESSAGE = "Could not instantiate custom filter: %s. User defined predicates must be static classes with a default constructor.";
        
        UserDefinedByClass(final Column<T> column, final Class<U> udpClass) {
            super(column);
            this.udpClass = Preconditions.checkNotNull(udpClass, "udpClass");
            final String name = this.getClass().getSimpleName().toLowerCase();
            this.toString = name + "(" + column.getColumnPath().toDotString() + ", " + udpClass.getName() + ")";
            this.getUserDefinedPredicate();
        }
        
        public Class<U> getUserDefinedPredicateClass() {
            return this.udpClass;
        }
        
        @Override
        public U getUserDefinedPredicate() {
            try {
                return this.udpClass.newInstance();
            }
            catch (InstantiationException e) {
                throw new RuntimeException(String.format("Could not instantiate custom filter: %s. User defined predicates must be static classes with a default constructor.", this.udpClass), e);
            }
            catch (IllegalAccessException e2) {
                throw new RuntimeException(String.format("Could not instantiate custom filter: %s. User defined predicates must be static classes with a default constructor.", this.udpClass), e2);
            }
        }
        
        @Override
        public String toString() {
            return this.toString;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final UserDefinedByClass that = (UserDefinedByClass)o;
            return this.column.equals(that.column) && this.udpClass.equals(that.udpClass);
        }
        
        @Override
        public int hashCode() {
            int result = this.column.hashCode();
            result = 31 * result + this.udpClass.hashCode();
            result = result * 31 + this.getClass().hashCode();
            return result;
        }
    }
    
    public static final class UserDefinedByInstance<T extends Comparable<T>, U extends parquet.filter2.predicate.UserDefinedPredicate> extends UserDefined<T, U>
    {
        private final String toString;
        private final U udpInstance;
        
        UserDefinedByInstance(final Column<T> column, final U udpInstance) {
            super(column);
            this.udpInstance = Preconditions.checkNotNull(udpInstance, "udpInstance");
            final String name = this.getClass().getSimpleName().toLowerCase();
            this.toString = name + "(" + column.getColumnPath().toDotString() + ", " + udpInstance + ")";
        }
        
        @Override
        public U getUserDefinedPredicate() {
            return this.udpInstance;
        }
        
        @Override
        public String toString() {
            return this.toString;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final UserDefinedByInstance that = (UserDefinedByInstance)o;
            return this.column.equals(that.column) && this.udpInstance.equals((Object)that.udpInstance);
        }
        
        @Override
        public int hashCode() {
            int result = this.column.hashCode();
            result = 31 * result + this.udpInstance.hashCode();
            result = result * 31 + this.getClass().hashCode();
            return result;
        }
    }
    
    public static final class LogicalNotUserDefined<T extends Comparable<T>, U extends UserDefinedPredicate<T>> implements FilterPredicate, Serializable
    {
        private final UserDefined<T, U> udp;
        private final String toString;
        
        LogicalNotUserDefined(final UserDefined<T, U> userDefined) {
            this.udp = Preconditions.checkNotNull(userDefined, "userDefined");
            this.toString = "inverted(" + this.udp + ")";
        }
        
        public UserDefined<T, U> getUserDefined() {
            return this.udp;
        }
        
        @Override
        public <R> R accept(final Visitor<R> visitor) {
            return visitor.visit((LogicalNotUserDefined<Comparable, UserDefinedPredicate>)this);
        }
        
        @Override
        public String toString() {
            return this.toString;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final LogicalNotUserDefined that = (LogicalNotUserDefined)o;
            return this.udp.equals(that.udp);
        }
        
        @Override
        public int hashCode() {
            int result = this.udp.hashCode();
            result = result * 31 + this.getClass().hashCode();
            return result;
        }
    }
    
    public interface SupportsLtGt extends SupportsEqNotEq
    {
    }
    
    public interface SupportsEqNotEq
    {
    }
}
