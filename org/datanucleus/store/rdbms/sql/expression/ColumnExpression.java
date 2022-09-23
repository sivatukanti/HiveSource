// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import org.datanucleus.query.expression.Expression;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.table.Column;

public class ColumnExpression extends SQLExpression
{
    Column column;
    Object value;
    boolean omitTableFromString;
    
    protected ColumnExpression(final SQLStatement stmt, final String parameterName, final JavaTypeMapping mapping, final Object value, final int colNumber) {
        super(stmt, null, mapping);
        this.omitTableFromString = false;
        this.st.appendParameter(parameterName, mapping, value, colNumber);
    }
    
    protected ColumnExpression(final SQLStatement stmt, final SQLTable table, final Column col) {
        super(stmt, table, null);
        this.omitTableFromString = false;
        this.column = col;
        this.st.append(this.toString());
    }
    
    protected ColumnExpression(final SQLStatement stmt, final Object value) {
        super(stmt, null, null);
        this.omitTableFromString = false;
        this.value = value;
        this.st.append(this.toString());
    }
    
    @Override
    public BooleanExpression eq(final SQLExpression expr) {
        return new BooleanExpression(this, Expression.OP_EQ, expr);
    }
    
    public BooleanExpression noteq(final SQLExpression expr) {
        return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
    }
    
    public void setOmitTableFromString(final boolean omitTable) {
        this.omitTableFromString = omitTable;
    }
    
    @Override
    public String toString() {
        if (this.value != null) {
            if (this.value instanceof String || this.value instanceof Character) {
                return "'" + this.value + "'";
            }
            return "" + this.value;
        }
        else {
            if (this.table == null) {
                return "?";
            }
            if (this.omitTableFromString) {
                return this.column.getIdentifier().toString();
            }
            if (this.table.getAlias() != null) {
                return this.table.getAlias() + "." + this.column.getIdentifier().toString();
            }
            return this.table.getTable() + "." + this.column.getIdentifier().toString();
        }
    }
}
