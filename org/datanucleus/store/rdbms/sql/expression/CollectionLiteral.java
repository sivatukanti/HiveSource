// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import java.util.Iterator;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import java.util.ArrayList;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import java.util.List;
import java.util.Collection;

public class CollectionLiteral extends CollectionExpression implements SQLLiteral
{
    private final Collection value;
    private List<SQLExpression> elementExpressions;
    
    public CollectionLiteral(final SQLStatement stmt, final JavaTypeMapping mapping, final Object value, final String parameterName) {
        super(stmt, null, mapping);
        this.parameterName = parameterName;
        if (value == null) {
            this.value = null;
        }
        else if (parameterName != null) {
            if (value instanceof Collection) {
                this.value = (Collection)value;
            }
            else {
                this.value = null;
            }
            this.st.appendParameter(parameterName, mapping, this.value);
        }
        else {
            if (!(value instanceof Collection)) {
                throw new NucleusException("Cannot create " + this.getClass().getName() + " for value of type " + value.getClass().getName());
            }
            this.value = (Collection)value;
            this.setStatement();
        }
    }
    
    @Override
    public Object getValue() {
        return this.value;
    }
    
    public List<SQLExpression> getElementExpressions() {
        return this.elementExpressions;
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
        if (this.value != null && this.value.size() > 0) {
            final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
            this.elementExpressions = new ArrayList<SQLExpression>();
            this.st.append("(");
            boolean hadPrev = false;
            for (final Object current : this.value) {
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
    
    @Override
    public SQLExpression invoke(final String methodName, final List args) {
        if (!methodName.equals("get") || args.size() != 1 || !(this.value instanceof List)) {
            return super.invoke(methodName, args);
        }
        final SQLExpression argExpr = args.get(0);
        if (!(argExpr instanceof SQLLiteral)) {
            throw new IllegalExpressionOperationException(this, "get", argExpr);
        }
        final Object val = ((List)this.value).get((int)((SQLLiteral)argExpr).getValue());
        if (val == null) {
            return new NullLiteral(this.stmt, null, null, null);
        }
        final JavaTypeMapping m = this.stmt.getRDBMSManager().getSQLExpressionFactory().getMappingForType(val.getClass(), false);
        return new ObjectLiteral(this.stmt, m, val, null);
    }
}
