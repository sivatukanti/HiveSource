// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.expression;

import org.datanucleus.query.symbol.Symbol;
import org.datanucleus.query.symbol.SymbolTable;
import org.datanucleus.util.StringUtils;
import org.datanucleus.query.NullOrderingType;

public class OrderExpression extends Expression
{
    private String sortOrder;
    private NullOrderingType nullOrder;
    
    public OrderExpression(final Expression expr, final String sortOrder, final String nullOrder) {
        this.left = expr;
        this.sortOrder = sortOrder;
        if (!StringUtils.isWhitespace(nullOrder)) {
            this.nullOrder = (nullOrder.equalsIgnoreCase("nulls first") ? NullOrderingType.NULLS_FIRST : NullOrderingType.NULLS_LAST);
        }
    }
    
    public OrderExpression(final Expression expr, final String sortOrder) {
        this.left = expr;
        this.sortOrder = sortOrder;
    }
    
    public OrderExpression(final Expression expr) {
        this.left = expr;
    }
    
    public String getSortOrder() {
        return this.sortOrder;
    }
    
    public NullOrderingType getNullOrder() {
        return this.nullOrder;
    }
    
    @Override
    public Symbol bind(final SymbolTable symtbl) {
        if (this.left instanceof VariableExpression) {
            final VariableExpression ve = (VariableExpression)this.left;
            ve.bind(symtbl);
        }
        return null;
    }
    
    @Override
    public Object evaluate(final ExpressionEvaluator eval) {
        return eval.evaluate(this.left);
    }
    
    @Override
    public String toString() {
        final String nullOrderString = (this.nullOrder != null) ? ((this.nullOrder == NullOrderingType.NULLS_FIRST) ? "NULLS FIRST" : "NULLS LAST") : null;
        return "OrderExpression{" + this.left + " " + this.sortOrder + ((nullOrderString != null) ? (" [" + nullOrderString + "]") : null) + "}";
    }
}
