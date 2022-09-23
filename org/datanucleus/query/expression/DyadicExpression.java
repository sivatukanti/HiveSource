// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.expression;

import java.lang.reflect.Field;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.query.symbol.Symbol;
import org.datanucleus.query.symbol.SymbolTable;

public class DyadicExpression extends Expression
{
    public DyadicExpression(final MonadicOperator op, final Expression operand) {
        super(op, operand);
    }
    
    public DyadicExpression(final Expression operand1, final DyadicOperator op, final Expression operand2) {
        super(operand1, op, operand2);
    }
    
    @Override
    public Object evaluate(final ExpressionEvaluator eval) {
        this.left.evaluate(eval);
        if (this.right != null) {
            this.right.evaluate(eval);
        }
        return super.evaluate(eval);
    }
    
    @Override
    public Symbol bind(final SymbolTable symtbl) {
        if (this.left != null) {
            try {
                this.left.bind(symtbl);
            }
            catch (PrimaryExpressionIsClassLiteralException peil) {
                (this.left = peil.getLiteral()).bind(symtbl);
            }
            catch (PrimaryExpressionIsClassStaticFieldException peil2) {
                final Field fld = peil2.getLiteralField();
                try {
                    final Object value = fld.get(null);
                    (this.left = new Literal(value)).bind(symtbl);
                }
                catch (Exception e) {
                    throw new NucleusUserException("Error processing static field " + fld.getName(), e);
                }
            }
            catch (PrimaryExpressionIsVariableException pive) {
                (this.left = pive.getVariableExpression()).bind(symtbl);
            }
            catch (PrimaryExpressionIsInvokeException piie) {
                (this.left = piie.getInvokeExpression()).bind(symtbl);
            }
        }
        if (this.right != null) {
            try {
                this.right.bind(symtbl);
            }
            catch (PrimaryExpressionIsClassLiteralException peil) {
                (this.right = peil.getLiteral()).bind(symtbl);
            }
            catch (PrimaryExpressionIsClassStaticFieldException peil2) {
                final Field fld = peil2.getLiteralField();
                try {
                    final Object value = fld.get(null);
                    (this.right = new Literal(value)).bind(symtbl);
                }
                catch (Exception e) {
                    throw new NucleusUserException("Error processing static field " + fld.getName(), e);
                }
            }
            catch (PrimaryExpressionIsVariableException pive) {
                (this.right = pive.getVariableExpression()).bind(symtbl);
            }
            catch (PrimaryExpressionIsInvokeException piie) {
                (this.right = piie.getInvokeExpression()).bind(symtbl);
            }
        }
        if (this.left != null && this.left instanceof VariableExpression) {
            final Symbol leftSym = this.left.getSymbol();
            if (leftSym != null && leftSym.getValueType() == null && this.right instanceof Literal && ((Literal)this.right).getLiteral() != null) {
                leftSym.setValueType(((Literal)this.right).getLiteral().getClass());
            }
        }
        if (this.right != null) {
            final Symbol rightSym = this.right.getSymbol();
            if (rightSym != null && rightSym.getValueType() == null && this.left instanceof Literal && ((Literal)this.left).getLiteral() != null) {
                rightSym.setValueType(((Literal)this.left).getLiteral().getClass());
            }
        }
        if (this.op == Expression.OP_EQ || this.op == Expression.OP_NOTEQ || this.op == Expression.OP_GT || this.op == Expression.OP_GTEQ || this.op == Expression.OP_LT || this.op == Expression.OP_LTEQ) {
            Class leftType = (this.left.getSymbol() != null) ? this.left.getSymbol().getValueType() : null;
            Class rightType = (this.right.getSymbol() != null) ? this.right.getSymbol().getValueType() : null;
            if (this.left instanceof ParameterExpression && leftType == null && rightType != null) {
                this.left.getSymbol().setValueType(rightType);
            }
            else if (this.right instanceof ParameterExpression && rightType == null && leftType != null) {
                this.right.getSymbol().setValueType(leftType);
            }
            leftType = ((this.left.getSymbol() != null) ? this.left.getSymbol().getValueType() : null);
            rightType = ((this.right.getSymbol() != null) ? this.right.getSymbol().getValueType() : null);
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "DyadicExpression{" + this.getLeft() + " " + this.getOperator() + " " + this.getRight() + "}";
    }
}
