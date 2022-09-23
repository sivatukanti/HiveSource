// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter2.predicate;

public interface FilterPredicate
{
     <R> R accept(final Visitor<R> p0);
    
    public interface Visitor<R>
    {
         <T extends Comparable<T>> R visit(final Operators.Eq<T> p0);
        
         <T extends Comparable<T>> R visit(final Operators.NotEq<T> p0);
        
         <T extends Comparable<T>> R visit(final Operators.Lt<T> p0);
        
         <T extends Comparable<T>> R visit(final Operators.LtEq<T> p0);
        
         <T extends Comparable<T>> R visit(final Operators.Gt<T> p0);
        
         <T extends Comparable<T>> R visit(final Operators.GtEq<T> p0);
        
        R visit(final Operators.And p0);
        
        R visit(final Operators.Or p0);
        
        R visit(final Operators.Not p0);
        
         <T extends Comparable<T>, U extends UserDefinedPredicate<T>> R visit(final Operators.UserDefined<T, U> p0);
        
         <T extends Comparable<T>, U extends UserDefinedPredicate<T>> R visit(final Operators.LogicalNotUserDefined<T, U> p0);
    }
}
