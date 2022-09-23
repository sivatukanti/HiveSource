// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.compiler;

import org.datanucleus.query.JDOQLQueryHelper;
import org.datanucleus.query.expression.OrderExpression;
import java.util.List;
import org.datanucleus.query.expression.VariableExpression;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.query.expression.DyadicExpression;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.symbol.Symbol;
import java.util.Iterator;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.query.symbol.PropertySymbol;
import org.datanucleus.query.symbol.SymbolTable;
import java.util.HashMap;
import java.util.Map;
import org.datanucleus.util.Imports;
import java.util.Collection;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.query.symbol.SymbolResolver;

public class JDOQLCompiler extends JavaQueryCompiler implements SymbolResolver
{
    boolean allowAll;
    
    public JDOQLCompiler(final MetaDataManager metaDataManager, final ClassLoaderResolver clr, final String from, final Class candidateClass, final Collection candidates, final String filter, final Imports imports, final String ordering, final String result, final String grouping, final String having, final String params, final String variables, final String update) {
        super(metaDataManager, clr, from, candidateClass, candidates, filter, imports, ordering, result, grouping, having, params, variables, update);
        this.allowAll = false;
    }
    
    public void setAllowAll(final boolean allow) {
        this.allowAll = allow;
    }
    
    @Override
    public QueryCompilation compile(final Map parameters, final Map subqueryMap) {
        final Map parseOptions = new HashMap();
        if (this.parameters != null) {
            parseOptions.put("explicitParameters", true);
        }
        else {
            parseOptions.put("implicitParameters", true);
        }
        this.parser = new JDOQLParser(parseOptions);
        (this.symtbl = new SymbolTable()).setSymbolResolver(this);
        if (this.parentCompiler != null) {
            this.symtbl.setParentSymbolTable(this.parentCompiler.symtbl);
        }
        if (subqueryMap != null && !subqueryMap.isEmpty()) {
            for (final String subqueryName : subqueryMap.keySet()) {
                final Symbol sym = new PropertySymbol(subqueryName);
                sym.setType(2);
                this.symtbl.addSymbol(sym);
            }
        }
        final Expression[] exprFrom = this.compileFrom();
        this.compileCandidatesParametersVariables(parameters);
        final Expression exprFilter = this.compileFilter();
        final Expression[] exprOrdering = this.compileOrdering();
        final Expression[] exprResult = this.compileResult();
        final Expression[] exprGrouping = this.compileGrouping();
        final Expression exprHaving = this.compileHaving();
        final Expression[] exprUpdate = this.compileUpdate();
        if (exprGrouping != null) {
            if (exprResult != null) {
                for (int i = 0; i < exprResult.length; ++i) {
                    if (!isExpressionGroupingOrAggregate(exprResult[i], exprGrouping)) {
                        throw new NucleusUserException("JDOQL query has result clause " + exprResult[i] + " but this is invalid (see JDO spec 14.6.10)." + " When specified with grouping should be aggregate, or grouping expression");
                    }
                }
            }
            if (exprOrdering != null) {
                for (int i = 0; i < exprOrdering.length; ++i) {
                    if (!isExpressionGroupingOrAggregate(exprOrdering[i], exprGrouping)) {
                        throw new NucleusUserException("JDOQL query has ordering clause " + exprOrdering[i] + " but this is invalid (see JDO spec 14.6.10)." + " When specified with grouping should be aggregate, or grouping expression");
                    }
                }
            }
        }
        if (exprHaving != null && !containsOnlyGroupingOrAggregates(exprHaving, exprGrouping)) {
            throw new NucleusUserException("JDOQL query has having clause " + exprHaving + " but this is invalid (see JDO spec 14.6.10)." + " Should contain only aggregates, or grouping expressions");
        }
        final QueryCompilation compilation = new QueryCompilation(this.candidateClass, this.candidateAlias, this.symtbl, exprResult, exprFrom, exprFilter, exprGrouping, exprHaving, exprOrdering, exprUpdate);
        compilation.setQueryLanguage(this.getLanguage());
        final boolean optimise = this.metaDataManager.getNucleusContext().getPersistenceConfiguration().getBooleanProperty("datanucleus.query.compileOptimised");
        if (optimise) {
            final QueryCompilerOptimiser optimiser = new QueryCompilerOptimiser(compilation);
            optimiser.optimise();
        }
        return compilation;
    }
    
    @Override
    public Expression[] compileUpdate() {
        if (this.allowAll && this.update != null) {
            ((JDOQLParser)this.parser).allowSingleEquals(true);
        }
        final Expression[] result = super.compileUpdate();
        ((JDOQLParser)this.parser).allowSingleEquals(false);
        return result;
    }
    
    private static boolean containsOnlyGroupingOrAggregates(final Expression expr, final Expression[] exprGrouping) {
        if (expr == null) {
            return true;
        }
        if (expr instanceof DyadicExpression) {
            final Expression left = expr.getLeft();
            final Expression right = expr.getRight();
            return containsOnlyGroupingOrAggregates(left, exprGrouping) && containsOnlyGroupingOrAggregates(right, exprGrouping);
        }
        if (expr instanceof InvokeExpression) {
            final InvokeExpression invExpr = (InvokeExpression)expr;
            if (isExpressionGroupingOrAggregate(invExpr, exprGrouping)) {
                return true;
            }
            final Expression invokedExpr = invExpr.getLeft();
            if (invokedExpr != null && !containsOnlyGroupingOrAggregates(invokedExpr, exprGrouping)) {
                return false;
            }
            final List<Expression> invArgs = invExpr.getArguments();
            if (invArgs != null) {
                for (final Expression argExpr : invArgs) {
                    if (!containsOnlyGroupingOrAggregates(argExpr, exprGrouping)) {
                        return false;
                    }
                }
            }
            return true;
        }
        else {
            if (expr instanceof PrimaryExpression) {
                return isExpressionGroupingOrAggregate(expr, exprGrouping);
            }
            return expr instanceof Literal || expr instanceof ParameterExpression || expr instanceof VariableExpression;
        }
    }
    
    private static boolean isExpressionGroupingOrAggregate(final Expression expr, final Expression[] exprGrouping) {
        if (expr instanceof InvokeExpression) {
            final InvokeExpression invExpr = (InvokeExpression)expr;
            if (invExpr.getLeft() == null) {
                final String methodName = invExpr.getOperation();
                if (methodName.equals("avg") || methodName.equals("AVG") || methodName.equals("count") || methodName.equals("COUNT") || methodName.equals("sum") || methodName.equals("SUM") || methodName.equals("min") || methodName.equals("MIN") || methodName.equals("max") || methodName.equals("MAX")) {
                    return true;
                }
            }
            for (int j = 0; j < exprGrouping.length; ++j) {
                if (exprGrouping[j] instanceof InvokeExpression && invExpr.toStringWithoutAlias().equalsIgnoreCase(exprGrouping[j].toString())) {
                    return true;
                }
            }
        }
        else if (expr instanceof PrimaryExpression) {
            final PrimaryExpression primExpr = (PrimaryExpression)expr;
            final String id = primExpr.getId();
            if (id.equals("this")) {
                return true;
            }
            for (int i = 0; i < exprGrouping.length; ++i) {
                if (exprGrouping[i] instanceof PrimaryExpression) {
                    final String groupId = ((PrimaryExpression)exprGrouping[i]).getId();
                    if (id.equals(groupId)) {
                        return true;
                    }
                }
            }
        }
        else {
            if (expr instanceof OrderExpression) {
                final Expression orderExpr = ((OrderExpression)expr).getLeft();
                return isExpressionGroupingOrAggregate(orderExpr, exprGrouping);
            }
            if (expr instanceof Literal) {
                return true;
            }
            if (expr instanceof ParameterExpression) {
                return true;
            }
            if (expr instanceof VariableExpression) {
                return true;
            }
            final String exprStr = expr.toString();
            for (int j = 0; j < exprGrouping.length; ++j) {
                if (exprGrouping[j].toString().equals(exprStr)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean supportsImplicitVariables() {
        return this.variables == null;
    }
    
    @Override
    public boolean caseSensitiveSymbolNames() {
        return true;
    }
    
    @Override
    public String getLanguage() {
        return "JDOQL";
    }
    
    @Override
    protected boolean isKeyword(final String name) {
        return name != null && JDOQLQueryHelper.isKeyword(name);
    }
}
