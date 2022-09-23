// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.SQLStatement;

public class UnboundExpression extends SQLExpression
{
    protected String variableName;
    
    public UnboundExpression(final SQLStatement stmt, final String variableName) {
        super(stmt, null, null);
        this.variableName = variableName;
    }
    
    public String getVariableName() {
        return this.variableName;
    }
}
