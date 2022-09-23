// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.expression;

import java.util.Collection;
import org.datanucleus.util.StringUtils;
import org.datanucleus.query.symbol.Symbol;
import org.datanucleus.query.symbol.SymbolTable;
import java.util.Iterator;
import java.util.List;

public class InvokeExpression extends Expression
{
    String methodName;
    List<Expression> arguments;
    
    public InvokeExpression(final Expression invoked, final String methodName, final List args) {
        this.left = invoked;
        this.methodName = methodName;
        this.arguments = (List<Expression>)args;
        if (invoked != null) {
            invoked.parent = this;
        }
        if (args != null && !args.isEmpty()) {
            final Iterator<Expression> argIter = args.iterator();
            while (argIter.hasNext()) {
                argIter.next().parent = this;
            }
        }
    }
    
    public String getOperation() {
        return this.methodName;
    }
    
    public List<Expression> getArguments() {
        return this.arguments;
    }
    
    @Override
    public Symbol bind(final SymbolTable symtbl) {
        if (this.left != null) {
            try {
                this.left.bind(symtbl);
            }
            catch (PrimaryExpressionIsVariableException pive) {
                (this.left = pive.getVariableExpression()).bind(symtbl);
            }
            catch (PrimaryExpressionIsInvokeException piie) {
                (this.left = piie.getInvokeExpression()).bind(symtbl);
            }
            catch (PrimaryExpressionIsClassLiteralException picle2) {
                this.methodName = ((PrimaryExpression)this.left).getId() + "." + this.methodName;
                this.left = null;
            }
        }
        if (this.arguments != null && this.arguments.size() > 0) {
            for (int i = 0; i < this.arguments.size(); ++i) {
                final Expression expr = this.arguments.get(i);
                try {
                    expr.bind(symtbl);
                }
                catch (PrimaryExpressionIsVariableException pive2) {
                    final VariableExpression ve = pive2.getVariableExpression();
                    ve.bind(symtbl);
                    this.arguments.remove(i);
                    this.arguments.add(i, ve);
                }
                catch (PrimaryExpressionIsInvokeException piie2) {
                    final InvokeExpression ve2 = piie2.getInvokeExpression();
                    ve2.bind(symtbl);
                    this.arguments.remove(i);
                    this.arguments.add(i, ve2);
                }
                catch (PrimaryExpressionIsClassLiteralException picle) {
                    final Literal l = picle.getLiteral();
                    l.bind(symtbl);
                    this.arguments.remove(i);
                    this.arguments.add(i, l);
                }
            }
        }
        return this.symbol;
    }
    
    public String toStringWithoutAlias() {
        if (this.left == null) {
            return "InvokeExpression{STATIC." + this.methodName + "(" + StringUtils.collectionToString(this.arguments) + ")}";
        }
        return "InvokeExpression{[" + this.left + "]." + this.methodName + "(" + StringUtils.collectionToString(this.arguments) + ")}";
    }
    
    @Override
    public String toString() {
        if (this.left == null) {
            return "InvokeExpression{STATIC." + this.methodName + "(" + StringUtils.collectionToString(this.arguments) + ")}" + ((this.alias != null) ? (" AS " + this.alias) : "");
        }
        return "InvokeExpression{[" + this.left + "]." + this.methodName + "(" + StringUtils.collectionToString(this.arguments) + ")}" + ((this.alias != null) ? (" AS " + this.alias) : "");
    }
}
