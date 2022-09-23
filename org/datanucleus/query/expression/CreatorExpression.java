// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.expression;

import java.util.Collection;
import org.datanucleus.util.StringUtils;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.query.symbol.PropertySymbol;
import org.datanucleus.query.symbol.Symbol;
import org.datanucleus.query.symbol.SymbolTable;
import java.util.Iterator;
import java.util.List;

public class CreatorExpression extends Expression
{
    List tuples;
    List<Expression> arguments;
    
    public CreatorExpression(final List tuples, final List args) {
        this.tuples = tuples;
        this.arguments = (List<Expression>)args;
        if (args != null && !args.isEmpty()) {
            final Iterator<Expression> argIter = args.iterator();
            while (argIter.hasNext()) {
                argIter.next().parent = this;
            }
        }
    }
    
    public String getId() {
        final StringBuilder id = new StringBuilder();
        for (int i = 0; i < this.tuples.size(); ++i) {
            if (id.length() > 0) {
                id.append('.');
            }
            id.append(this.tuples.get(i));
        }
        return id.toString();
    }
    
    public List<Expression> getArguments() {
        return this.arguments;
    }
    
    public List getTuples() {
        return this.tuples;
    }
    
    @Override
    public Symbol bind(final SymbolTable symtbl) {
        if (symtbl.hasSymbol(this.getId())) {
            this.symbol = symtbl.getSymbol(this.getId());
        }
        else {
            try {
                final String className = this.getId();
                final Class cls = symtbl.getSymbolResolver().resolveClass(className);
                this.symbol = new PropertySymbol(this.getId(), cls);
            }
            catch (ClassNotResolvedException cnre) {
                throw new NucleusUserException("CreatorExpression defined with class of " + this.getId() + " yet this class is not found");
            }
        }
        return this.symbol;
    }
    
    @Override
    public String toString() {
        return "CreatorExpression{" + this.getId() + "(" + StringUtils.collectionToString(this.arguments) + ")}" + ((this.alias != null) ? (" AS " + this.alias) : "");
    }
}
