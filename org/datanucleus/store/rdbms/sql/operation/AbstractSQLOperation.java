// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.operation;

import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.expression.SQLExpressionFactory;

public abstract class AbstractSQLOperation implements SQLOperation
{
    protected SQLExpressionFactory exprFactory;
    
    @Override
    public void setExpressionFactory(final SQLExpressionFactory exprFactory) {
        this.exprFactory = exprFactory;
    }
    
    protected JavaTypeMapping getMappingForClass(final Class cls) {
        return this.exprFactory.getMappingForType(cls, true);
    }
}
