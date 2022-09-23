// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.expression;

import org.datanucleus.query.symbol.PropertySymbol;
import org.datanucleus.query.symbol.Symbol;
import org.datanucleus.query.symbol.SymbolTable;

public class ParameterExpression extends Expression
{
    String name;
    int position;
    Class type;
    
    public ParameterExpression(final String name, final int position) {
        this.name = name;
        this.position = position;
    }
    
    public ParameterExpression(final String name, final Class type) {
        this.name = name;
        this.type = type;
        this.position = -1;
    }
    
    public String getId() {
        return this.name;
    }
    
    public int getPosition() {
        return this.position;
    }
    
    public Class getType() {
        return this.type;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public Symbol bind(final SymbolTable symtbl) {
        if (symtbl.hasSymbol(this.getId())) {
            this.symbol = symtbl.getSymbol(this.getId());
        }
        else {
            (this.symbol = new PropertySymbol(this.getId())).setType(1);
            symtbl.addSymbol(this.symbol);
        }
        return this.symbol;
    }
    
    @Override
    public String toString() {
        return "ParameterExpression{" + this.name + "}";
    }
}
