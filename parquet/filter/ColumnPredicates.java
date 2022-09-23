// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter;

import parquet.io.api.Binary;
import parquet.column.ColumnReader;
import parquet.Preconditions;

public class ColumnPredicates
{
    public static Predicate equalTo(final String target) {
        Preconditions.checkNotNull(target, "target");
        return new Predicate() {
            @Override
            public boolean apply(final ColumnReader input) {
                return target.equals(input.getBinary().toStringUsingUTF8());
            }
        };
    }
    
    public static Predicate applyFunctionToString(final PredicateFunction<String> fn) {
        return new Predicate() {
            @Override
            public boolean apply(final ColumnReader input) {
                return fn.functionToApply(input.getBinary().toStringUsingUTF8());
            }
        };
    }
    
    public static Predicate equalTo(final int target) {
        return new Predicate() {
            @Override
            public boolean apply(final ColumnReader input) {
                return input.getInteger() == target;
            }
        };
    }
    
    public static Predicate applyFunctionToInteger(final IntegerPredicateFunction fn) {
        return new Predicate() {
            @Override
            public boolean apply(final ColumnReader input) {
                return fn.functionToApply(input.getInteger());
            }
        };
    }
    
    public static Predicate equalTo(final long target) {
        return new Predicate() {
            @Override
            public boolean apply(final ColumnReader input) {
                return input.getLong() == target;
            }
        };
    }
    
    public static Predicate applyFunctionToLong(final LongPredicateFunction fn) {
        return new Predicate() {
            @Override
            public boolean apply(final ColumnReader input) {
                return fn.functionToApply(input.getLong());
            }
        };
    }
    
    public static Predicate equalTo(final float target) {
        return new Predicate() {
            @Override
            public boolean apply(final ColumnReader input) {
                return input.getFloat() == target;
            }
        };
    }
    
    public static Predicate applyFunctionToFloat(final FloatPredicateFunction fn) {
        return new Predicate() {
            @Override
            public boolean apply(final ColumnReader input) {
                return fn.functionToApply(input.getFloat());
            }
        };
    }
    
    public static Predicate equalTo(final double target) {
        return new Predicate() {
            @Override
            public boolean apply(final ColumnReader input) {
                return input.getDouble() == target;
            }
        };
    }
    
    public static Predicate applyFunctionToDouble(final DoublePredicateFunction fn) {
        return new Predicate() {
            @Override
            public boolean apply(final ColumnReader input) {
                return fn.functionToApply(input.getDouble());
            }
        };
    }
    
    public static Predicate equalTo(final boolean target) {
        return new Predicate() {
            @Override
            public boolean apply(final ColumnReader input) {
                return input.getBoolean() == target;
            }
        };
    }
    
    public static Predicate applyFunctionToBoolean(final BooleanPredicateFunction fn) {
        return new Predicate() {
            @Override
            public boolean apply(final ColumnReader input) {
                return fn.functionToApply(input.getBoolean());
            }
        };
    }
    
    public static <E extends Enum> Predicate equalTo(final E target) {
        Preconditions.checkNotNull(target, "target");
        final String targetAsString = target.name();
        return new Predicate() {
            @Override
            public boolean apply(final ColumnReader input) {
                return targetAsString.equals(input.getBinary().toStringUsingUTF8());
            }
        };
    }
    
    public static Predicate applyFunctionToBinary(final PredicateFunction<Binary> fn) {
        return new Predicate() {
            @Override
            public boolean apply(final ColumnReader input) {
                return fn.functionToApply(input.getBinary());
            }
        };
    }
    
    public interface BooleanPredicateFunction
    {
        boolean functionToApply(final boolean p0);
    }
    
    public interface DoublePredicateFunction
    {
        boolean functionToApply(final double p0);
    }
    
    public interface FloatPredicateFunction
    {
        boolean functionToApply(final float p0);
    }
    
    public interface LongPredicateFunction
    {
        boolean functionToApply(final long p0);
    }
    
    public interface IntegerPredicateFunction
    {
        boolean functionToApply(final int p0);
    }
    
    public interface PredicateFunction<T>
    {
        boolean functionToApply(final T p0);
    }
    
    public interface Predicate
    {
        boolean apply(final ColumnReader p0);
    }
}
