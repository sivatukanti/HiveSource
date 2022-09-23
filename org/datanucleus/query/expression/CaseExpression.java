// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.expression;

import java.util.Iterator;
import org.datanucleus.query.symbol.Symbol;
import org.datanucleus.query.symbol.SymbolTable;
import java.util.HashMap;
import java.util.Map;

public class CaseExpression extends Expression
{
    Map<Expression, Expression> actionByCondition;
    Expression elseExpr;
    
    public CaseExpression(final Expression elseExpr) {
        this.actionByCondition = new HashMap<Expression, Expression>();
        this.elseExpr = elseExpr;
    }
    
    public void addCondition(final Expression whenExpr, final Expression actionExpr) {
        this.actionByCondition.put(whenExpr, actionExpr);
    }
    
    public Map<Expression, Expression> getConditions() {
        return this.actionByCondition;
    }
    
    public Expression getElseExpression() {
        return this.elseExpr;
    }
    
    @Override
    public Symbol bind(final SymbolTable symtbl) {
        return null;
    }
    
    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder("CaseExpression : ");
        for (final Expression whenExpr : this.actionByCondition.keySet()) {
            final Expression actionExpr = this.actionByCondition.get(whenExpr);
            str.append("WHEN ").append(whenExpr).append(" THEN ").append(actionExpr).append(" ");
        }
        if (this.elseExpr != null) {
            str.append("ELSE ").append(this.elseExpr);
        }
        return str.toString();
    }
}
