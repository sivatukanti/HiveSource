// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.operation;

import org.datanucleus.store.rdbms.sql.expression.SQLExpressionFactory;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public interface SQLOperation
{
    SQLExpression getExpression(final SQLExpression p0, final SQLExpression p1);
    
    void setExpressionFactory(final SQLExpressionFactory p0);
}
