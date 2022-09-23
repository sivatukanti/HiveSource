// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import java.util.ArrayList;
import java.lang.reflect.Array;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;

public class ArrayLiteral extends ArrayExpression implements SQLLiteral
{
    final Object value;
    
    public ArrayLiteral(final SQLStatement stmt, final JavaTypeMapping mapping, final Object value, final String parameterName) {
        super(stmt, null, mapping);
        this.parameterName = parameterName;
        this.value = value;
        if (value != null && !value.getClass().isArray()) {
            throw new NucleusUserException("Invalid argument literal : " + value);
        }
        if (parameterName != null) {
            this.st.appendParameter(parameterName, mapping, this.value);
        }
        else {
            this.setStatement();
        }
    }
    
    @Override
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public void setNotParameter() {
        if (this.parameterName == null) {
            return;
        }
        this.parameterName = null;
        this.st.clearStatement();
        this.setStatement();
    }
    
    protected void setStatement() {
        if (this.value != null && Array.getLength(this.value) > 0) {
            final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
            this.elementExpressions = new ArrayList<SQLExpression>();
            this.st.append("(");
            boolean hadPrev = false;
            for (int i = 0; i < Array.getLength(this.value); ++i) {
                final Object current = Array.get(this.value, i);
                if (current != null) {
                    final JavaTypeMapping m = storeMgr.getSQLExpressionFactory().getMappingForType(current.getClass(), false);
                    final SQLExpression expr = storeMgr.getSQLExpressionFactory().newLiteral(this.stmt, m, current);
                    this.st.append(hadPrev ? "," : "");
                    this.st.append(expr);
                    this.elementExpressions.add(expr);
                    hadPrev = true;
                }
            }
            this.st.append(")");
        }
    }
}
