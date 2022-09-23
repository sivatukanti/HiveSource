// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import java.util.ArrayList;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import java.util.Iterator;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.store.rdbms.sql.SQLText;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.util.Localiser;

public abstract class SQLExpression
{
    protected static final Localiser LOCALISER;
    protected SQLStatement stmt;
    protected SQLTable table;
    protected JavaTypeMapping mapping;
    protected final SQLText st;
    protected Expression.Operator lowestOperator;
    protected ColumnExpressionList subExprs;
    protected String parameterName;
    
    protected SQLExpression(final SQLStatement stmt, final SQLTable table, final JavaTypeMapping mapping) {
        this.st = new SQLText();
        this.lowestOperator = null;
        this.parameterName = null;
        this.stmt = stmt;
        this.table = table;
        this.mapping = mapping;
        if (table != null) {
            this.subExprs = new ColumnExpressionList();
            if (mapping != null) {
                for (int i = 0; i < mapping.getNumberOfDatastoreMappings(); ++i) {
                    final ColumnExpression colExpr = new ColumnExpression(stmt, table, mapping.getDatastoreMapping(i).getColumn());
                    this.subExprs.addExpression(colExpr);
                }
            }
            this.st.append(this.subExprs);
        }
    }
    
    protected SQLExpression(final Expression.MonadicOperator op, final SQLExpression expr1) {
        this.st = new SQLText();
        this.lowestOperator = null;
        this.parameterName = null;
        this.st.append(op.toString());
        if (op.isHigherThan(expr1.lowestOperator)) {
            this.st.append('(').append(expr1).append(')');
        }
        else {
            this.st.append(expr1);
        }
        this.stmt = expr1.stmt;
        this.mapping = expr1.mapping;
        this.lowestOperator = op;
    }
    
    protected SQLExpression(final SQLExpression expr1, final Expression.DyadicOperator op, final SQLExpression expr2) {
        this.st = new SQLText();
        this.lowestOperator = null;
        this.parameterName = null;
        this.stmt = expr1.stmt;
        this.mapping = ((expr1.mapping != null) ? expr1.mapping : expr2.mapping);
        this.lowestOperator = op;
        Label_0141: {
            if (op == Expression.OP_CONCAT) {
                try {
                    final SQLExpression concatExpr = this.stmt.getSQLExpressionFactory().invokeOperation("concat", expr1, expr2);
                    this.st.append(concatExpr.encloseInParentheses());
                    return;
                }
                catch (UnsupportedOperationException uoe) {
                    break Label_0141;
                }
            }
            if (op == Expression.OP_MOD) {
                try {
                    final SQLExpression modExpr = this.stmt.getSQLExpressionFactory().invokeOperation("mod", expr1, expr2);
                    this.st.append(modExpr.encloseInParentheses());
                    return;
                }
                catch (UnsupportedOperationException ex) {}
            }
        }
        if (op.isHigherThanLeftSide(expr1.lowestOperator)) {
            this.st.append('(').append(expr1).append(')');
        }
        else {
            this.st.append(expr1);
        }
        this.st.append(op.toString());
        if (op.isHigherThanRightSide(expr2.lowestOperator)) {
            this.st.append('(').append(expr2).append(')');
        }
        else {
            this.st.append(expr2);
        }
        if (op == Expression.OP_LIKE && this.stmt.getRDBMSManager().getDatastoreAdapter().supportsOption("EscapeExpressionInLikePredicate") && expr2 instanceof SQLLiteral) {
            final DatastoreAdapter dba = this.stmt.getRDBMSManager().getDatastoreAdapter();
            this.st.append(' ');
            this.st.append(dba.getEscapePatternExpression());
            this.st.append(' ');
        }
    }
    
    protected SQLExpression(final SQLStatement stmt, final JavaTypeMapping mapping, final String functionName, final List<SQLExpression> args, final List types) {
        this.st = new SQLText();
        this.lowestOperator = null;
        this.parameterName = null;
        if (types != null && args != null && args.size() != types.size()) {
            throw new NucleusException("Number of arguments (" + args.size() + ") and their types (" + types.size() + ") are inconsistent");
        }
        if ((this.stmt = stmt) == null && args != null && args.size() > 0) {
            this.stmt = args.get(0).stmt;
        }
        this.mapping = mapping;
        this.st.append(functionName).append('(');
        if (args != null) {
            final Iterator<SQLExpression> argIter = args.listIterator();
            final Iterator typesIter = (types != null) ? types.listIterator() : null;
            while (argIter.hasNext()) {
                final SQLExpression argExpr = argIter.next();
                this.st.append(argExpr);
                if (typesIter != null) {
                    final Object argType = typesIter.next();
                    this.st.append(" AS ");
                    if (argType instanceof SQLExpression) {
                        this.st.append((SQLExpression)argType);
                    }
                    else {
                        this.st.append(argType.toString());
                    }
                }
                if (argIter.hasNext()) {
                    this.st.append(",");
                }
            }
        }
        this.st.append(')');
    }
    
    public Expression.Operator getLowestOperator() {
        return this.lowestOperator;
    }
    
    public int getNumberOfSubExpressions() {
        return (this.subExprs != null) ? this.subExprs.size() : 1;
    }
    
    public ColumnExpression getSubExpression(final int index) {
        if (this.subExprs == null) {
            return null;
        }
        if (index < 0 || index >= this.subExprs.size()) {
            return null;
        }
        return this.subExprs.getExpression(index);
    }
    
    public SQLStatement getSQLStatement() {
        return this.stmt;
    }
    
    public boolean isParameter() {
        return this.parameterName != null;
    }
    
    public String getParameterName() {
        return this.parameterName;
    }
    
    public JavaTypeMapping getJavaTypeMapping() {
        return this.mapping;
    }
    
    public void setJavaTypeMapping(final JavaTypeMapping mapping) {
        this.mapping = mapping;
        if (this.parameterName != null) {
            this.st.changeMappingForParameter(this.parameterName, mapping);
        }
    }
    
    public SQLTable getSQLTable() {
        return this.table;
    }
    
    public SQLText toSQLText() {
        return this.st;
    }
    
    public SQLExpression encloseInParentheses() {
        this.st.encloseInParentheses();
        return this;
    }
    
    public BooleanExpression and(final SQLExpression expr) {
        throw new IllegalExpressionOperationException(this, "&&", expr);
    }
    
    public BooleanExpression eor(final SQLExpression expr) {
        throw new IllegalExpressionOperationException(this, "^", expr);
    }
    
    public BooleanExpression ior(final SQLExpression expr) {
        throw new IllegalExpressionOperationException(this, "||", expr);
    }
    
    public BooleanExpression not() {
        throw new IllegalExpressionOperationException("!", this);
    }
    
    public BooleanExpression eq(final SQLExpression expr) {
        if (expr instanceof DelegatedExpression) {
            return this.eq(((DelegatedExpression)expr).getDelegate());
        }
        throw new IllegalExpressionOperationException(this, "==", expr);
    }
    
    public BooleanExpression ne(final SQLExpression expr) {
        if (expr instanceof DelegatedExpression) {
            return this.ne(((DelegatedExpression)expr).getDelegate());
        }
        throw new IllegalExpressionOperationException(this, "!=", expr);
    }
    
    public BooleanExpression lt(final SQLExpression expr) {
        if (expr instanceof DelegatedExpression) {
            return this.lt(((DelegatedExpression)expr).getDelegate());
        }
        throw new IllegalExpressionOperationException(this, "<", expr);
    }
    
    public BooleanExpression le(final SQLExpression expr) {
        if (expr instanceof DelegatedExpression) {
            return this.le(((DelegatedExpression)expr).getDelegate());
        }
        throw new IllegalExpressionOperationException(this, "<=", expr);
    }
    
    public BooleanExpression gt(final SQLExpression expr) {
        if (expr instanceof DelegatedExpression) {
            return this.gt(((DelegatedExpression)expr).getDelegate());
        }
        throw new IllegalExpressionOperationException(this, ">", expr);
    }
    
    public BooleanExpression ge(final SQLExpression expr) {
        if (expr instanceof DelegatedExpression) {
            return this.ge(((DelegatedExpression)expr).getDelegate());
        }
        throw new IllegalExpressionOperationException(this, ">=", expr);
    }
    
    public BooleanExpression in(final SQLExpression expr, final boolean not) {
        throw new IllegalExpressionOperationException(this, "in", expr);
    }
    
    public SQLExpression add(final SQLExpression expr) {
        throw new IllegalExpressionOperationException(this, "+", expr);
    }
    
    public SQLExpression sub(final SQLExpression expr) {
        throw new IllegalExpressionOperationException(this, "-", expr);
    }
    
    public SQLExpression mul(final SQLExpression expr) {
        throw new IllegalExpressionOperationException(this, "*", expr);
    }
    
    public SQLExpression div(final SQLExpression expr) {
        throw new IllegalExpressionOperationException(this, "/", expr);
    }
    
    public SQLExpression mod(final SQLExpression expr) {
        throw new IllegalExpressionOperationException(this, "%", expr);
    }
    
    public SQLExpression neg() {
        throw new IllegalExpressionOperationException("-", this);
    }
    
    public SQLExpression com() {
        throw new IllegalExpressionOperationException("~", this);
    }
    
    public SQLExpression distinct() {
        this.st.prepend("DISTINCT (");
        this.st.append(")");
        return this;
    }
    
    public SQLExpression cast(final SQLExpression expr) {
        throw new IllegalExpressionOperationException("cast to " + expr, this);
    }
    
    public BooleanExpression is(final SQLExpression expr, final boolean not) {
        throw new IllegalExpressionOperationException("instanceof " + expr, this);
    }
    
    public SQLExpression invoke(final String methodName, final List args) {
        throw new IllegalExpressionOperationException("." + methodName, this);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
    
    public static class ColumnExpressionList
    {
        private List<ColumnExpression> exprs;
        
        public ColumnExpressionList() {
            this.exprs = new ArrayList<ColumnExpression>();
        }
        
        public void addExpression(final ColumnExpression expression) {
            this.exprs.add(expression);
        }
        
        public ColumnExpression getExpression(final int index) {
            return this.exprs.get(index);
        }
        
        public int size() {
            return this.exprs.size();
        }
        
        @Override
        public String toString() {
            final StringBuffer expr = new StringBuffer();
            for (int size = this.exprs.size(), i = 0; i < size; ++i) {
                expr.append(this.getExpression(i).toString());
                if (i < size - 1) {
                    expr.append(',');
                }
            }
            return expr.toString();
        }
        
        public ColumnExpression[] toArray() {
            return this.exprs.toArray(new ColumnExpression[this.exprs.size()]);
        }
    }
}
