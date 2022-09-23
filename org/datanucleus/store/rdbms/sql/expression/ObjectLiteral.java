// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import org.datanucleus.query.expression.Expression;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.StringUtils;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.identity.OID;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;

public class ObjectLiteral extends ObjectExpression implements SQLLiteral
{
    private Object value;
    
    public ObjectLiteral(final SQLStatement stmt, final JavaTypeMapping mapping, final Object value, final String parameterName) {
        super(stmt, null, mapping);
        this.value = value;
        this.parameterName = parameterName;
        if (parameterName != null) {
            if (value != null) {
                this.subExprs = new ColumnExpressionList();
                this.addSubexpressionsForValue(this.value, mapping);
            }
            if (mapping.getNumberOfDatastoreMappings() == 1) {
                this.st.appendParameter(parameterName, mapping, this.value);
            }
        }
        else {
            this.subExprs = new ColumnExpressionList();
            if (value != null) {
                this.addSubexpressionsForValue(this.value, mapping);
            }
            this.st.append(this.subExprs.toString());
        }
    }
    
    private void addSubexpressionsForValue(final Object value, final JavaTypeMapping mapping) {
        final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
        final ClassLoaderResolver clr = this.stmt.getClassLoaderResolver();
        String objClassName = value.getClass().getName();
        if (mapping instanceof PersistableMapping) {
            objClassName = mapping.getType();
        }
        else if (value instanceof OID) {
            objClassName = ((OID)value).getPcClass();
        }
        final AbstractClassMetaData cmd = storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(objClassName, clr);
        if (cmd != null) {
            for (int numCols = mapping.getNumberOfDatastoreMappings(), i = 0; i < numCols; ++i) {
                ColumnExpression colExpr = null;
                if (this.parameterName == null && mapping instanceof PersistableMapping) {
                    final Object colValue = ((PersistableMapping)mapping).getValueForDatastoreMapping(this.stmt.getRDBMSManager().getNucleusContext(), i, value);
                    colExpr = new ColumnExpression(this.stmt, colValue);
                }
                else {
                    colExpr = new ColumnExpression(this.stmt, this.parameterName, mapping, value, i);
                }
                this.subExprs.addExpression(colExpr);
            }
        }
        else {
            NucleusLogger.GENERAL.error(">> ObjectLiteral doesn't yet cater for values of type " + StringUtils.toJVMIDString(value));
        }
    }
    
    @Override
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public BooleanExpression eq(final SQLExpression expr) {
        this.addSubexpressionsToRelatedExpression(expr);
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_EQ, expr);
        }
        if (this.value == null) {
            return new NullLiteral(this.stmt, null, null, null).eq(expr);
        }
        if (expr instanceof ObjectExpression) {
            return ExpressionUtils.getEqualityExpressionForObjectExpressions(this, (ObjectExpression)expr, true);
        }
        return super.eq(expr);
    }
    
    @Override
    public BooleanExpression ne(final SQLExpression expr) {
        this.addSubexpressionsToRelatedExpression(expr);
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
        }
        if (this.value == null) {
            return new NullLiteral(this.stmt, null, null, null).ne(expr);
        }
        if (expr instanceof ObjectExpression) {
            return ExpressionUtils.getEqualityExpressionForObjectExpressions(this, (ObjectExpression)expr, false);
        }
        return super.ne(expr);
    }
    
    @Override
    public String toString() {
        if (this.value != null) {
            return super.toString() + " = " + this.value.toString();
        }
        return super.toString() + " = NULL";
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
        if (this.parameterName == null) {
            this.subExprs = new ColumnExpressionList();
            if (this.value != null) {
                this.addSubexpressionsForValue(this.value, this.mapping);
            }
            this.st.append(this.subExprs.toString());
        }
        else {
            this.st.appendParameter(this.parameterName, this.mapping, this.value);
        }
    }
}
