// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.expression;

import org.datanucleus.query.symbol.PropertySymbol;
import org.datanucleus.query.symbol.Symbol;
import org.datanucleus.query.symbol.SymbolTable;

public class VariableExpression extends Expression
{
    String name;
    Class type;
    
    public VariableExpression(final String name) {
        this.name = name;
    }
    
    public VariableExpression(final String name, final Class type) {
        this.name = name;
        this.type = type;
    }
    
    public String getId() {
        return this.name;
    }
    
    @Override
    public Symbol bind(final SymbolTable symtbl) {
        if (symtbl.hasSymbol(this.getId())) {
            this.symbol = symtbl.getSymbol(this.getId());
        }
        else {
            if (this.type != null) {
                this.symbol = new PropertySymbol(this.getId(), this.type);
            }
            else {
                this.symbol = new PropertySymbol(this.getId());
            }
            this.symbol.setType(2);
            symtbl.addSymbol(this.symbol);
        }
        return this.symbol;
    }
    
    @Override
    public String toString() {
        return "VariableExpression{" + this.name + "}" + ((this.alias != null) ? (" AS " + this.alias) : "");
    }
}
