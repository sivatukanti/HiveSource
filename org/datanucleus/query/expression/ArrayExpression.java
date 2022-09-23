// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.expression;

import java.util.Collection;
import org.datanucleus.util.StringUtils;
import org.datanucleus.query.symbol.Symbol;
import org.datanucleus.query.symbol.SymbolTable;
import java.util.ArrayList;
import java.util.List;

public class ArrayExpression extends Expression
{
    List<Expression> elements;
    
    public ArrayExpression(final Expression[] elements) {
        this.elements = new ArrayList<Expression>();
        if (elements != null) {
            for (int i = 0; i < elements.length; ++i) {
                this.elements.add(elements[i]);
                elements[i].parent = this;
            }
        }
    }
    
    public Expression getElement(final int index) {
        if (index < 0 || index >= this.elements.size()) {
            throw new IndexOutOfBoundsException();
        }
        return this.elements.get(index);
    }
    
    public int getArraySize() {
        return this.elements.size();
    }
    
    @Override
    public Symbol bind(final SymbolTable symtbl) {
        for (int i = 0; i < this.elements.size(); ++i) {
            final Expression expr = this.elements.get(i);
            expr.bind(symtbl);
        }
        return this.symbol;
    }
    
    @Override
    public String toString() {
        return "ArrayExpression{" + StringUtils.collectionToString(this.elements) + "}";
    }
}
