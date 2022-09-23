// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.expression;

import org.datanucleus.query.symbol.Symbol;
import org.datanucleus.query.symbol.SymbolTable;
import java.math.BigDecimal;
import java.math.BigInteger;

public class Literal extends Expression
{
    Object value;
    
    public Literal(final Object value) {
        this.value = value;
    }
    
    public Object getLiteral() {
        return this.value;
    }
    
    public void negate() {
        if (this.value == null) {
            return;
        }
        if (this.value instanceof BigInteger) {
            this.value = ((BigInteger)this.value).negate();
        }
        else if (this.value instanceof BigDecimal) {
            this.value = ((BigDecimal)this.value).negate();
        }
        else if (this.value instanceof Integer) {
            this.value = -1 * (int)this.value;
        }
        else if (this.value instanceof Long) {
            this.value = -1L * (long)this.value;
        }
        else if (this.value instanceof Double) {
            this.value = -1.0 * (double)this.value;
        }
        else if (this.value instanceof Float) {
            this.value = -1.0f * (float)this.value;
        }
        else if (this.value instanceof Short) {
            this.value = (short)(-1 * (short)this.value);
        }
    }
    
    @Override
    public Symbol bind(final SymbolTable symtbl) {
        return null;
    }
    
    @Override
    public String toString() {
        return "Literal{" + this.value + "}" + ((this.alias != null) ? (" AS " + this.alias) : "");
    }
}
