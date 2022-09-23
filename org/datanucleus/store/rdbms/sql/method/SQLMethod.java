// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.sql.SQLStatement;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public interface SQLMethod
{
    SQLExpression getExpression(final SQLExpression p0, final List p1);
    
    void setStatement(final SQLStatement p0);
}
