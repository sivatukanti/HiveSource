// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.expression;

import org.datanucleus.query.symbol.Symbol;
import org.datanucleus.query.symbol.SymbolTable;

public class JoinExpression extends Expression
{
    JoinType type;
    PrimaryExpression primExpr;
    DyadicExpression onExpr;
    
    public JoinExpression(final PrimaryExpression expr, final String alias, final JoinType type) {
        this.primExpr = expr;
        this.alias = alias;
        this.type = type;
    }
    
    public void setJoinExpression(final JoinExpression expr) {
        this.right = expr;
    }
    
    public void setOnExpression(final DyadicExpression expr) {
        this.onExpr = expr;
    }
    
    public PrimaryExpression getPrimaryExpression() {
        return this.primExpr;
    }
    
    public DyadicExpression getOnExpression() {
        return this.onExpr;
    }
    
    @Override
    public String getAlias() {
        return this.alias;
    }
    
    public JoinType getType() {
        return this.type;
    }
    
    @Override
    public Symbol bind(final SymbolTable symtbl) {
        return null;
    }
    
    @Override
    public String toString() {
        if (this.right != null) {
            return "JoinExpression{" + this.type + " " + this.primExpr + " alias=" + this.alias + " join=" + this.right + ((this.onExpr != null) ? (" on=" + this.onExpr) : "") + "}";
        }
        return "JoinExpression{" + this.type + " " + this.primExpr + " alias=" + this.alias + ((this.onExpr != null) ? (" on=" + this.onExpr) : "") + "}";
    }
    
    public enum JoinType
    {
        JOIN_INNER, 
        JOIN_LEFT_OUTER, 
        JOIN_RIGHT_OUTER, 
        JOIN_INNER_FETCH, 
        JOIN_LEFT_OUTER_FETCH, 
        JOIN_RIGHT_OUTER_FETCH;
    }
}
