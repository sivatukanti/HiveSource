// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.typesafe;

public interface TypesafeSubquery<T>
{
    PersistableExpression candidate();
    
    TypesafeSubquery filter(final BooleanExpression p0);
    
    TypesafeSubquery groupBy(final Expression... p0);
    
    TypesafeSubquery having(final Expression p0);
    
     <S> NumericExpression<S> selectUnique(final NumericExpression<S> p0);
    
    StringExpression selectUnique(final StringExpression p0);
    
     <S> DateExpression<S> selectUnique(final DateExpression<S> p0);
    
     <S> DateTimeExpression<S> selectUnique(final DateTimeExpression<S> p0);
    
     <S> TimeExpression<S> selectUnique(final TimeExpression<S> p0);
    
    CharacterExpression selectUnique(final CharacterExpression p0);
    
    CollectionExpression select(final CollectionExpression p0);
}
