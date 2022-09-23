// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.typesafe;

public interface StringExpression extends ComparableExpression<String>
{
    StringExpression add(final Expression p0);
    
    CharacterExpression charAt(final int p0);
    
    CharacterExpression charAt(final NumericExpression p0);
    
    BooleanExpression endsWith(final StringExpression p0);
    
    BooleanExpression endsWith(final String p0);
    
    BooleanExpression equalsIgnoreCase(final StringExpression p0);
    
    BooleanExpression equalsIgnoreCase(final String p0);
    
    NumericExpression indexOf(final StringExpression p0);
    
    NumericExpression indexOf(final String p0);
    
    NumericExpression indexOf(final StringExpression p0, final NumericExpression p1);
    
    NumericExpression indexOf(final String p0, final NumericExpression p1);
    
    NumericExpression indexOf(final String p0, final int p1);
    
    NumericExpression indexOf(final StringExpression p0, final int p1);
    
    NumericExpression length();
    
    BooleanExpression startsWith(final StringExpression p0);
    
    BooleanExpression startsWith(final String p0);
    
    StringExpression substring(final NumericExpression p0);
    
    StringExpression substring(final int p0);
    
    StringExpression substring(final NumericExpression p0, final NumericExpression p1);
    
    StringExpression substring(final int p0, final int p1);
    
    StringExpression toLowerCase();
    
    StringExpression toUpperCase();
    
    StringExpression trim();
}
