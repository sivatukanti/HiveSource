// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator;

import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.query.expression.CaseExpression;
import org.datanucleus.query.expression.SubqueryExpression;
import org.datanucleus.query.expression.VariableExpression;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.query.expression.CreatorExpression;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.expression.ExpressionEvaluator;

public class AbstractExpressionEvaluator implements ExpressionEvaluator
{
    @Override
    public Object evaluate(final Expression expr) {
        return this.compileOrAndExpression(expr);
    }
    
    protected Object compileOrAndExpression(final Expression expr) {
        if (expr.getOperator() == Expression.OP_OR) {
            return this.processOrExpression(expr);
        }
        if (expr.getOperator() == Expression.OP_AND) {
            return this.processAndExpression(expr);
        }
        return this.compileRelationalExpression(expr);
    }
    
    protected Object compileRelationalExpression(final Expression expr) {
        if (expr.getOperator() == Expression.OP_EQ) {
            return this.processEqExpression(expr);
        }
        if (expr.getOperator() == Expression.OP_NOTEQ) {
            return this.processNoteqExpression(expr);
        }
        if (expr.getOperator() == Expression.OP_LIKE) {
            return this.processLikeExpression(expr);
        }
        if (expr.getOperator() == Expression.OP_GTEQ) {
            return this.processGteqExpression(expr);
        }
        if (expr.getOperator() == Expression.OP_LTEQ) {
            return this.processLteqExpression(expr);
        }
        if (expr.getOperator() == Expression.OP_GT) {
            return this.processGtExpression(expr);
        }
        if (expr.getOperator() == Expression.OP_LT) {
            return this.processLtExpression(expr);
        }
        if (expr.getOperator() == Expression.OP_IS) {
            return this.processIsExpression(expr);
        }
        if (expr.getOperator() == Expression.OP_ISNOT) {
            return this.processIsnotExpression(expr);
        }
        if (expr.getOperator() == Expression.OP_CAST) {
            return this.processCastExpression(expr);
        }
        if (expr.getOperator() == Expression.OP_IN) {
            return this.processInExpression(expr);
        }
        if (expr.getOperator() == Expression.OP_NOTIN) {
            return this.processNotInExpression(expr);
        }
        return this.compileAdditiveMultiplicativeExpression(expr);
    }
    
    protected Object compileAdditiveMultiplicativeExpression(final Expression expr) {
        if (expr.getOperator() == Expression.OP_ADD) {
            return this.processAddExpression(expr);
        }
        if (expr.getOperator() == Expression.OP_SUB) {
            return this.processSubExpression(expr);
        }
        if (expr.getOperator() == Expression.OP_MUL) {
            return this.processMulExpression(expr);
        }
        if (expr.getOperator() == Expression.OP_DIV) {
            return this.processDivExpression(expr);
        }
        if (expr.getOperator() == Expression.OP_MOD) {
            return this.processModExpression(expr);
        }
        return this.compileUnaryExpression(expr);
    }
    
    protected Object compileUnaryExpression(final Expression expr) {
        if (expr.getOperator() == Expression.OP_NEG) {
            return this.processNegExpression(expr);
        }
        if (expr.getOperator() == Expression.OP_COM) {
            return this.processComExpression(expr);
        }
        if (expr.getOperator() == Expression.OP_NOT) {
            return this.processNotExpression(expr);
        }
        if (expr.getOperator() == Expression.OP_DISTINCT) {
            return this.processDistinctExpression(expr);
        }
        return this.compilePrimaryExpression(expr);
    }
    
    protected Object compilePrimaryExpression(final Expression expr) {
        if (expr instanceof CreatorExpression) {
            return this.processCreatorExpression((CreatorExpression)expr);
        }
        if (expr instanceof PrimaryExpression) {
            return this.processPrimaryExpression((PrimaryExpression)expr);
        }
        if (expr instanceof ParameterExpression) {
            return this.processParameterExpression((ParameterExpression)expr);
        }
        if (expr instanceof VariableExpression) {
            return this.processVariableExpression((VariableExpression)expr);
        }
        if (expr instanceof SubqueryExpression) {
            return this.processSubqueryExpression((SubqueryExpression)expr);
        }
        if (expr instanceof CaseExpression) {
            return this.processCaseExpression((CaseExpression)expr);
        }
        if (expr instanceof InvokeExpression) {
            return this.processInvokeExpression((InvokeExpression)expr);
        }
        if (expr instanceof Literal) {
            return this.processLiteral((Literal)expr);
        }
        return null;
    }
    
    protected Object processOrExpression(final Expression expr) {
        throw new NucleusException("Operation OR is not supported by this mapper");
    }
    
    protected Object processAndExpression(final Expression expr) {
        throw new NucleusException("Operation AND is not supported by this mapper");
    }
    
    protected Object processEqExpression(final Expression expr) {
        throw new NucleusException("Operation EQ is not supported by this mapper");
    }
    
    protected Object processNoteqExpression(final Expression expr) {
        throw new NucleusException("Operation NOTEQ is not supported by this mapper");
    }
    
    protected Object processLikeExpression(final Expression expr) {
        throw new NucleusException("Operation LIKE is not supported by this mapper");
    }
    
    protected Object processGtExpression(final Expression expr) {
        throw new NucleusException("Operation GT is not supported by this mapper");
    }
    
    protected Object processLtExpression(final Expression expr) {
        throw new NucleusException("Operation LT is not supported by this mapper");
    }
    
    protected Object processGteqExpression(final Expression expr) {
        throw new NucleusException("Operation GTEQ is not supported by this mapper");
    }
    
    protected Object processLteqExpression(final Expression expr) {
        throw new NucleusException("Operation LTEQ is not supported by this mapper");
    }
    
    protected Object processIsExpression(final Expression expr) {
        throw new NucleusException("Operation IS (instanceof) is not supported by this mapper");
    }
    
    protected Object processIsnotExpression(final Expression expr) {
        throw new NucleusException("Operation ISNOT (!instanceof) is not supported by this mapper");
    }
    
    protected Object processInExpression(final Expression expr) {
        throw new NucleusException("Operation IN is not supported by this mapper");
    }
    
    protected Object processNotInExpression(final Expression expr) {
        throw new NucleusException("Operation NOT IN is not supported by this mapper");
    }
    
    protected Object processAddExpression(final Expression expr) {
        throw new NucleusException("Operation ADD is not supported by this mapper");
    }
    
    protected Object processSubExpression(final Expression expr) {
        throw new NucleusException("Operation SUB is not supported by this mapper");
    }
    
    protected Object processMulExpression(final Expression expr) {
        throw new NucleusException("Operation MUL is not supported by this mapper");
    }
    
    protected Object processDivExpression(final Expression expr) {
        throw new NucleusException("Operation DIV is not supported by this mapper");
    }
    
    protected Object processModExpression(final Expression expr) {
        throw new NucleusException("Operation MOD is not supported by this mapper");
    }
    
    protected Object processNegExpression(final Expression expr) {
        throw new NucleusException("Operation NEG is not supported by this mapper");
    }
    
    protected Object processComExpression(final Expression expr) {
        throw new NucleusException("Operation COM is not supported by this mapper");
    }
    
    protected Object processNotExpression(final Expression expr) {
        throw new NucleusException("Operation NOT is not supported by this mapper");
    }
    
    protected Object processDistinctExpression(final Expression expr) {
        throw new NucleusException("Operation DISTINCT is not supported by this mapper");
    }
    
    protected Object processCreatorExpression(final CreatorExpression expr) {
        throw new NucleusException("Creator expression is not supported by this mapper");
    }
    
    protected Object processPrimaryExpression(final PrimaryExpression expr) {
        throw new NucleusException("Primary expression is not supported by this mapper");
    }
    
    protected Object processParameterExpression(final ParameterExpression expr) {
        throw new NucleusException("Parameter expression is not supported by this mapper");
    }
    
    protected Object processVariableExpression(final VariableExpression expr) {
        throw new NucleusException("Variable expression is not supported by this mapper");
    }
    
    protected Object processSubqueryExpression(final SubqueryExpression expr) {
        throw new NucleusException("Subquery expression is not supported by this mapper");
    }
    
    protected Object processInvokeExpression(final InvokeExpression expr) {
        throw new NucleusException("Invoke expression is not supported by this mapper");
    }
    
    protected Object processCastExpression(final Expression expr) {
        throw new NucleusException("Cast expression is not supported by this mapper");
    }
    
    protected Object processCaseExpression(final CaseExpression expr) {
        throw new NucleusException("Case expression is not supported by this mapper");
    }
    
    protected Object processLiteral(final Literal expr) {
        throw new NucleusException("Literals are not supported by this mapper");
    }
}
