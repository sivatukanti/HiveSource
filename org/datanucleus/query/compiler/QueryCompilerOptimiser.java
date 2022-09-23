// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.compiler;

import java.util.List;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.query.expression.DyadicExpression;
import org.datanucleus.query.expression.PrimaryExpression;
import java.util.ArrayList;
import org.datanucleus.query.expression.VariableExpression;
import org.datanucleus.query.expression.Expression;
import java.util.Iterator;
import java.util.Set;
import org.datanucleus.util.NucleusLogger;
import java.util.HashSet;

public class QueryCompilerOptimiser
{
    QueryCompilation compilation;
    
    public QueryCompilerOptimiser(final QueryCompilation compilation) {
        this.compilation = compilation;
    }
    
    public void optimise() {
        if (this.compilation == null) {
            return;
        }
        if (this.compilation.getExprFilter() != null) {
            final Set<String> redundantVariables = new HashSet<String>();
            this.findRedundantFilterVariables(this.compilation.getExprFilter(), redundantVariables);
            if (!redundantVariables.isEmpty()) {
                for (final String var : redundantVariables) {
                    if (NucleusLogger.QUERY.isDebugEnabled()) {
                        NucleusLogger.QUERY.debug("Query was defined with variable " + var + " yet this was redundant, so has been replaced by the candidate");
                    }
                    this.compilation.setExprFilter(this.replaceVariableWithCandidateInExpression(var, this.compilation.getExprFilter()));
                    this.compilation.setExprHaving(this.replaceVariableWithCandidateInExpression(var, this.compilation.getExprHaving()));
                    final Expression[] exprResult = this.compilation.getExprResult();
                    if (exprResult != null) {
                        for (int i = 0; i < exprResult.length; ++i) {
                            exprResult[i] = this.replaceVariableWithCandidateInExpression(var, exprResult[i]);
                        }
                    }
                    final Expression[] exprGrouping = this.compilation.getExprGrouping();
                    if (exprGrouping != null) {
                        for (int j = 0; j < exprGrouping.length; ++j) {
                            exprGrouping[j] = this.replaceVariableWithCandidateInExpression(var, exprGrouping[j]);
                        }
                    }
                    this.compilation.getSymbolTable().removeSymbol(this.compilation.getSymbolTable().getSymbol(var));
                }
            }
        }
    }
    
    private Expression replaceVariableWithCandidateInExpression(final String varName, final Expression expr) {
        if (expr == null) {
            return null;
        }
        if (expr instanceof VariableExpression && ((VariableExpression)expr).getId().equals(varName)) {
            final List<String> tuples = new ArrayList<String>();
            tuples.add(this.compilation.getCandidateAlias());
            final Expression replExpr = new PrimaryExpression(tuples);
            replExpr.bind(this.compilation.getSymbolTable());
            return replExpr;
        }
        if (expr instanceof DyadicExpression) {
            final DyadicExpression dyExpr = (DyadicExpression)expr;
            if (dyExpr.getLeft() != null) {
                dyExpr.setLeft(this.replaceVariableWithCandidateInExpression(varName, dyExpr.getLeft()));
            }
            if (dyExpr.getRight() != null) {
                dyExpr.setRight(this.replaceVariableWithCandidateInExpression(varName, dyExpr.getRight()));
            }
        }
        else if (expr instanceof PrimaryExpression) {
            if (expr.getLeft() != null) {
                if (expr.getLeft() instanceof VariableExpression && ((VariableExpression)expr.getLeft()).getId().equals(varName)) {
                    expr.setLeft(null);
                }
                else {
                    expr.setLeft(this.replaceVariableWithCandidateInExpression(varName, expr.getLeft()));
                }
            }
        }
        else if (expr instanceof InvokeExpression) {
            final InvokeExpression invokeExpr = (InvokeExpression)expr;
            if (invokeExpr.getLeft() != null) {
                invokeExpr.setLeft(this.replaceVariableWithCandidateInExpression(varName, invokeExpr.getLeft()));
            }
        }
        return expr;
    }
    
    private void findRedundantFilterVariables(final Expression filterExpr, final Set<String> varNames) {
        if (filterExpr instanceof DyadicExpression) {
            final DyadicExpression dyExpr = (DyadicExpression)filterExpr;
            if (dyExpr.getOperator() == Expression.OP_EQ) {
                if (dyExpr.getLeft() instanceof VariableExpression) {
                    if (dyExpr.getRight() instanceof PrimaryExpression) {
                        final PrimaryExpression rightExpr = (PrimaryExpression)dyExpr.getRight();
                        if (rightExpr.getId().equals(this.compilation.getCandidateAlias())) {
                            varNames.add(((VariableExpression)dyExpr.getLeft()).getId());
                        }
                    }
                }
                else if (dyExpr.getRight() instanceof VariableExpression && dyExpr.getLeft() instanceof PrimaryExpression) {
                    final PrimaryExpression leftExpr = (PrimaryExpression)dyExpr.getLeft();
                    if (leftExpr.getId().equals(this.compilation.getCandidateAlias())) {
                        varNames.add(((VariableExpression)dyExpr.getRight()).getId());
                    }
                }
            }
            else if (dyExpr.getOperator() == Expression.OP_AND) {
                this.findRedundantFilterVariables(dyExpr.getLeft(), varNames);
                this.findRedundantFilterVariables(dyExpr.getRight(), varNames);
            }
            else if (dyExpr.getOperator() == Expression.OP_OR) {
                this.findRedundantFilterVariables(dyExpr.getLeft(), varNames);
                this.findRedundantFilterVariables(dyExpr.getRight(), varNames);
            }
        }
    }
}
