// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import java.util.List;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;

public class AggregateTemporalExpression extends TemporalExpression
{
    public AggregateTemporalExpression(final SQLStatement stmt, final JavaTypeMapping mapping, final String functionName, final List args) {
        super(stmt, mapping, functionName, args);
    }
}
