// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.query;

import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.typesafe.PersistableExpression;
import org.datanucleus.query.typesafe.CharacterExpression;

public class CharacterExpressionImpl<T> extends ComparableExpressionImpl<Character> implements CharacterExpression
{
    public CharacterExpressionImpl(final PersistableExpression parent, final String name) {
        super(parent, name);
    }
    
    public CharacterExpressionImpl(final Class<Character> cls, final String name, final ExpressionType type) {
        super(cls, name, type);
    }
    
    public CharacterExpressionImpl(final org.datanucleus.query.expression.Expression queryExpr) {
        super(queryExpr);
    }
}
