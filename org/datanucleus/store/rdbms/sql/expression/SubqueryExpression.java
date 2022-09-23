// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.SQLStatement;

public class SubqueryExpression extends SQLExpression
{
    SQLStatement subStatement;
    
    public SubqueryExpression(final SQLStatement stmt, final SQLStatement subStmt) {
        super(stmt, null, null);
        this.subStatement = subStmt;
        this.st.append("(");
        this.st.append(subStmt);
        this.st.append(")");
    }
    
    public SQLStatement getSubqueryStatement() {
        return this.subStatement;
    }
}
