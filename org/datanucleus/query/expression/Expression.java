// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.expression;

import org.datanucleus.ClassConstants;
import org.datanucleus.query.symbol.SymbolTable;
import org.datanucleus.query.symbol.Symbol;
import org.datanucleus.util.Localiser;
import java.io.Serializable;

public abstract class Expression implements Serializable
{
    protected static final Localiser LOCALISER;
    protected Expression parent;
    protected Operator op;
    protected Expression left;
    protected Expression right;
    protected Symbol symbol;
    protected String alias;
    public static final DyadicOperator OP_OR;
    public static final DyadicOperator OP_AND;
    public static final MonadicOperator OP_NOT;
    public static final DyadicOperator OP_EQ;
    public static final DyadicOperator OP_NOTEQ;
    public static final DyadicOperator OP_LT;
    public static final DyadicOperator OP_LTEQ;
    public static final DyadicOperator OP_GT;
    public static final DyadicOperator OP_GTEQ;
    public static final DyadicOperator OP_LIKE;
    public static final DyadicOperator OP_IS;
    public static final DyadicOperator OP_ISNOT;
    public static final DyadicOperator OP_CAST;
    public static final DyadicOperator OP_IN;
    public static final DyadicOperator OP_NOTIN;
    public static final DyadicOperator OP_ADD;
    public static final DyadicOperator OP_SUB;
    public static final DyadicOperator OP_CONCAT;
    public static final DyadicOperator OP_MUL;
    public static final DyadicOperator OP_DIV;
    public static final DyadicOperator OP_MOD;
    public static final MonadicOperator OP_NEG;
    public static final MonadicOperator OP_COM;
    public static final MonadicOperator OP_DISTINCT;
    
    protected Expression() {
    }
    
    protected Expression(final MonadicOperator op, final Expression operand) {
        this.op = op;
        this.left = operand;
        if (this.left != null) {
            this.left.parent = this;
        }
    }
    
    protected Expression(final Expression operand1, final DyadicOperator op, final Expression operand2) {
        this.op = op;
        this.left = operand1;
        this.right = operand2;
        if (this.left != null) {
            this.left.parent = this;
        }
        if (this.right != null) {
            this.right.parent = this;
        }
    }
    
    public Expression getParent() {
        return this.parent;
    }
    
    public void setLeft(final Expression expr) {
        this.left = expr;
    }
    
    public void setRight(final Expression expr) {
        this.right = expr;
    }
    
    public Operator getOperator() {
        return this.op;
    }
    
    public Expression getLeft() {
        return this.left;
    }
    
    public Expression getRight() {
        return this.right;
    }
    
    public Symbol getSymbol() {
        return this.symbol;
    }
    
    public void setAlias(final String alias) {
        this.alias = alias;
    }
    
    public String getAlias() {
        return this.alias;
    }
    
    public Object evaluate(final ExpressionEvaluator eval) {
        return eval.evaluate(this);
    }
    
    public abstract Symbol bind(final SymbolTable p0);
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
        OP_OR = new DyadicOperator("OR", 0, true);
        OP_AND = new DyadicOperator("AND", 1, true);
        OP_NOT = new MonadicOperator("NOT ", 2);
        OP_EQ = new DyadicOperator("=", 3, false);
        OP_NOTEQ = new DyadicOperator("<>", 3, false);
        OP_LT = new DyadicOperator("<", 3, false);
        OP_LTEQ = new DyadicOperator("<=", 3, false);
        OP_GT = new DyadicOperator(">", 3, false);
        OP_GTEQ = new DyadicOperator(">=", 3, false);
        OP_LIKE = new DyadicOperator("LIKE", 3, false);
        OP_IS = new DyadicOperator("IS", 3, false);
        OP_ISNOT = new DyadicOperator("IS NOT", 3, false);
        OP_CAST = new DyadicOperator("CAST", 3, false);
        OP_IN = new DyadicOperator("IN", 3, false);
        OP_NOTIN = new DyadicOperator("NOT IN", 3, false);
        OP_ADD = new DyadicOperator("+", 4, true);
        OP_SUB = new DyadicOperator("-", 4, false);
        OP_CONCAT = new DyadicOperator("||", 4, true);
        OP_MUL = new DyadicOperator("*", 5, true);
        OP_DIV = new DyadicOperator("/", 5, false);
        OP_MOD = new DyadicOperator("%", 5, false);
        OP_NEG = new MonadicOperator("-", 6);
        OP_COM = new MonadicOperator("~", 6);
        OP_DISTINCT = new MonadicOperator("DISTINCT", 6);
    }
    
    public static class Operator implements Serializable
    {
        protected final String symbol;
        protected final int precedence;
        
        public Operator(final String symbol, final int precedence) {
            this.symbol = symbol;
            this.precedence = precedence;
        }
        
        @Override
        public String toString() {
            return this.symbol;
        }
    }
    
    public static class MonadicOperator extends Operator
    {
        public MonadicOperator(final String symbol, final int precedence) {
            super(symbol, precedence);
        }
        
        public boolean isHigherThan(final Operator op) {
            return op != null && this.precedence > op.precedence;
        }
    }
    
    public static class DyadicOperator extends Operator
    {
        private final boolean isAssociative;
        
        public DyadicOperator(final String symbol, final int precedence, final boolean isAssociative) {
            super(" " + symbol + " ", precedence);
            this.isAssociative = isAssociative;
        }
        
        public boolean isHigherThanLeftSide(final Operator op) {
            return op != null && this.precedence > op.precedence;
        }
        
        public boolean isHigherThanRightSide(final Operator op) {
            if (op == null) {
                return false;
            }
            if (this.precedence == op.precedence) {
                return !this.isAssociative;
            }
            return this.precedence > op.precedence;
        }
    }
}
