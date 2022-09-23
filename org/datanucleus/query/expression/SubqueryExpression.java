// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.expression;

import org.datanucleus.query.symbol.Symbol;
import org.datanucleus.query.symbol.SymbolTable;

public class SubqueryExpression extends Expression
{
    String keyword;
    
    public SubqueryExpression(final String keyword, final VariableExpression operand) {
        this.keyword = keyword;
        this.right = operand;
    }
    
    @Override
    public Symbol bind(final SymbolTable symtbl) {
        this.right.bind(symtbl);
        return null;
    }
    
    public String getKeyword() {
        return this.keyword;
    }
    
    @Override
    public String toString() {
        return "SubqueryExpression{" + this.keyword + "(" + this.right + ")}";
    }
}
