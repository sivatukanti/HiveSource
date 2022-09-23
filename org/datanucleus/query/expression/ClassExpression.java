// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.expression;

import org.datanucleus.query.symbol.Symbol;
import org.datanucleus.query.symbol.SymbolTable;

public class ClassExpression extends Expression
{
    String candidateExpression;
    
    public ClassExpression(final String alias) {
        this.alias = alias;
    }
    
    public void setCandidateExpression(final String expr) {
        this.candidateExpression = expr;
    }
    
    public String getCandidateExpression() {
        return this.candidateExpression;
    }
    
    public void setJoinExpression(final JoinExpression expr) {
        this.right = expr;
    }
    
    @Override
    public String getAlias() {
        return this.alias;
    }
    
    @Override
    public Symbol bind(final SymbolTable symtbl) {
        return this.symbol = symtbl.getSymbol(this.alias);
    }
    
    @Override
    public String toString() {
        if (this.right != null) {
            return "ClassExpression(" + ((this.candidateExpression != null) ? ("candidate=" + this.candidateExpression + " ") : "") + "alias=" + this.alias + " join=" + this.right + ")";
        }
        return "ClassExpression(" + ((this.candidateExpression != null) ? ("candidate=" + this.candidateExpression + " ") : "") + "alias=" + this.alias + ")";
    }
}
