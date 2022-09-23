// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.expression;

import java.lang.reflect.Field;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.util.ClassUtils;
import java.lang.reflect.Modifier;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.query.symbol.PropertySymbol;
import org.datanucleus.query.symbol.Symbol;
import org.datanucleus.query.symbol.SymbolTable;
import java.util.Iterator;
import java.util.List;

public class PrimaryExpression extends Expression
{
    List<String> tuples;
    
    public PrimaryExpression(final List<String> tuples) {
        this.tuples = tuples;
    }
    
    public PrimaryExpression(final Expression left, final List<String> tuples) {
        this.left = left;
        if (left != null) {
            left.parent = this;
        }
        this.tuples = tuples;
    }
    
    public String getId() {
        final StringBuilder str = new StringBuilder();
        for (final String tuple : this.tuples) {
            if (str.length() > 0) {
                str.append('.');
            }
            str.append(tuple);
        }
        return str.toString();
    }
    
    public List<String> getTuples() {
        return this.tuples;
    }
    
    @Override
    public Symbol bind(final SymbolTable symtbl) {
        if (this.left != null) {
            this.left.bind(symtbl);
        }
        if (this.left == null && symtbl.hasSymbol(this.getId())) {
            this.symbol = symtbl.getSymbol(this.getId());
            if (this.symbol.getType() == 2) {
                throw new PrimaryExpressionIsVariableException(this.symbol.getQualifiedName());
            }
            return this.symbol;
        }
        else {
            if (this.left != null) {
                return null;
            }
            if (this.symbol == null) {
                try {
                    final Class symbolType = symtbl.getSymbolResolver().getType(this.tuples);
                    this.symbol = new PropertySymbol(this.getId(), symbolType);
                }
                catch (NucleusUserException ex) {}
            }
            if (this.symbol == null && symtbl.getParentSymbolTable() != null) {
                try {
                    final Class symbolType = symtbl.getParentSymbolTable().getSymbolResolver().getType(this.tuples);
                    this.symbol = new PropertySymbol(this.getId(), symbolType);
                }
                catch (NucleusUserException ex2) {}
            }
            if (this.symbol == null) {
                String className = this.getId();
                try {
                    final Class cls = symtbl.getSymbolResolver().resolveClass(className);
                    throw new PrimaryExpressionIsClassLiteralException(cls);
                }
                catch (ClassNotResolvedException cnre) {
                    if (className.indexOf(46) < 0) {
                        final Class primaryCls = symtbl.getSymbolResolver().getPrimaryClass();
                        if (primaryCls == null) {
                            throw new NucleusUserException("Class name " + className + " could not be resolved");
                        }
                        try {
                            final Field fld = primaryCls.getDeclaredField(className);
                            if (!Modifier.isStatic(fld.getModifiers())) {
                                throw new NucleusUserException("Identifier " + className + " is unresolved (not a static field)");
                            }
                            throw new PrimaryExpressionIsClassStaticFieldException(fld);
                        }
                        catch (NoSuchFieldException nsfe) {
                            if (symtbl.getSymbolResolver().supportsImplicitVariables() && this.left == null) {
                                throw new PrimaryExpressionIsVariableException(className);
                            }
                            throw new NucleusUserException("Class name " + className + " could not be resolved");
                        }
                    }
                    try {
                        final String staticFieldName = className.substring(className.lastIndexOf(46) + 1);
                        className = className.substring(0, className.lastIndexOf(46));
                        final Class cls2 = symtbl.getSymbolResolver().resolveClass(className);
                        try {
                            final Field fld2 = cls2.getDeclaredField(staticFieldName);
                            if (!Modifier.isStatic(fld2.getModifiers())) {
                                throw new NucleusUserException("Identifier " + className + "." + staticFieldName + " is unresolved (not a static field)");
                            }
                            throw new PrimaryExpressionIsClassStaticFieldException(fld2);
                        }
                        catch (NoSuchFieldException nsfe2) {
                            throw new NucleusUserException("Identifier " + className + "." + staticFieldName + " is unresolved (not a static field)");
                        }
                    }
                    catch (ClassNotResolvedException cnre2) {
                        if (this.getId().indexOf(".") > 0) {
                            final Iterator<String> tupleIter = this.tuples.iterator();
                            Class cls3 = null;
                            while (tupleIter.hasNext()) {
                                final String tuple = tupleIter.next();
                                if (cls3 == null) {
                                    Symbol sym = symtbl.getSymbol(tuple);
                                    if (sym == null) {
                                        sym = symtbl.getSymbol("this");
                                        if (sym == null) {
                                            break;
                                        }
                                    }
                                    cls3 = sym.getValueType();
                                }
                                else {
                                    if (cls3.isArray() && tuple.equals("length") && !tupleIter.hasNext()) {
                                        final PrimaryExpression primExpr = new PrimaryExpression(this.left, this.tuples.subList(0, this.tuples.size() - 1));
                                        final InvokeExpression invokeExpr = new InvokeExpression(primExpr, "size", null);
                                        throw new PrimaryExpressionIsInvokeException(invokeExpr);
                                    }
                                    cls3 = ClassUtils.getClassForMemberOfClass(cls3, tuple);
                                }
                            }
                            if (cls3 != null) {}
                        }
                        if (symtbl.getSymbolResolver().supportsImplicitVariables() && this.left == null) {
                            final VariableExpression varExpr = new VariableExpression(className);
                            varExpr.bind(symtbl);
                            this.left = varExpr;
                            this.tuples.remove(0);
                            return this.symbol;
                        }
                        throw new NucleusUserException("Cannot find type of (part of) " + this.getId() + " since symbol has no type; implicit variable?");
                    }
                }
            }
            return this.symbol;
        }
    }
    
    @Override
    public String toString() {
        if (this.left != null) {
            return "PrimaryExpression{" + this.left + "." + this.getId() + "}" + ((this.alias != null) ? (" AS " + this.alias) : "");
        }
        return "PrimaryExpression{" + this.getId() + "}" + ((this.alias != null) ? (" AS " + this.alias) : "");
    }
}
