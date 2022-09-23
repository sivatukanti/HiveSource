// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;

public class BooleanSubqueryExpression extends BooleanExpression
{
    public BooleanSubqueryExpression(final SQLStatement stmt, final String keyword, final SQLStatement subStmt) {
        super(stmt, null);
        this.st.append(keyword).append(" (");
        this.st.append(subStmt);
        this.st.append(")");
    }
}
