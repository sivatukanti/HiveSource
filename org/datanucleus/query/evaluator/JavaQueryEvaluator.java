// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator;

import org.datanucleus.ClassConstants;
import org.datanucleus.query.expression.InvokeExpression;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import org.datanucleus.query.evaluator.memory.VariableNotSetException;
import org.datanucleus.query.evaluator.memory.InMemoryFailure;
import org.datanucleus.query.expression.ExpressionEvaluator;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.List;
import java.util.Iterator;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.expression.CreatorExpression;
import org.datanucleus.query.QueryUtils;
import org.datanucleus.util.StringUtils;
import org.datanucleus.util.NucleusLogger;
import java.util.ArrayList;
import java.util.HashMap;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.query.evaluator.memory.InMemoryExpressionEvaluator;
import java.util.Map;
import org.datanucleus.query.compiler.QueryCompilation;
import org.datanucleus.store.query.Query;
import java.util.Collection;
import org.datanucleus.util.Localiser;

public abstract class JavaQueryEvaluator
{
    protected static final Localiser LOCALISER;
    public static final String RESULTS_SET = "DATANUCLEUS_RESULTS_SET";
    protected final String language;
    protected String candidateAlias;
    protected Collection candidates;
    protected Query query;
    protected QueryCompilation compilation;
    protected Map parameterValues;
    protected InMemoryExpressionEvaluator evaluator;
    protected Map<String, Object> state;
    protected ClassLoaderResolver clr;
    
    public JavaQueryEvaluator(final String language, final Query query, final QueryCompilation compilation, final Map parameterValues, final ClassLoaderResolver clr, final Collection candidates) {
        this.candidateAlias = "this";
        this.language = language;
        this.query = query;
        this.compilation = compilation;
        this.parameterValues = parameterValues;
        this.clr = clr;
        this.candidates = candidates;
        this.candidateAlias = ((compilation.getCandidateAlias() != null) ? compilation.getCandidateAlias() : this.candidateAlias);
        (this.state = new HashMap<String, Object>()).put(this.candidateAlias, query.getCandidateClass());
        this.evaluator = new InMemoryExpressionEvaluator(query.getExecutionContext(), parameterValues, this.state, query.getParsedImports(), clr, this.candidateAlias, query.getLanguage());
    }
    
    protected abstract Collection evaluateSubquery(final Query p0, final QueryCompilation p1, final Collection p2, final Object p3);
    
    public Collection execute(final boolean applyFilter, final boolean applyOrdering, final boolean applyResult, final boolean applyResultClass, final boolean applyRange) {
        if (!applyFilter && !applyOrdering && !applyResult && !applyResultClass && !applyRange) {
            return this.candidates;
        }
        final Collection executeCandidates = new ArrayList();
        final Expression[] result = this.compilation.getExprResult();
        if (this.candidates != null) {
            if (applyResult && result != null && result.length > 1) {
                for (final Object candidate : this.candidates) {
                    if (!executeCandidates.contains(candidate)) {
                        executeCandidates.add(candidate);
                    }
                }
            }
            else {
                executeCandidates.addAll(this.candidates);
            }
        }
        final String[] subqueryAliases = this.compilation.getSubqueryAliases();
        if (subqueryAliases != null) {
            for (int i = 0; i < subqueryAliases.length; ++i) {
                final Query subquery = this.query.getSubqueryForVariable(subqueryAliases[i]).getQuery();
                final QueryCompilation subqueryCompilation = this.compilation.getCompilationForSubquery(subqueryAliases[i]);
                if (subqueryCompilation.getExprFrom() != null) {
                    NucleusLogger.QUERY.warn("In-memory evaluation of subquery with 'from'=" + StringUtils.objectArrayToString(subqueryCompilation.getExprFrom()) + " but from clause evaluation not currently supported!");
                }
                final Collection subqueryResult = this.evaluateSubquery(subquery, subqueryCompilation, executeCandidates, null);
                if (QueryUtils.queryReturnsSingleRow(subquery)) {
                    this.state.put(subqueryAliases[i], subqueryResult.iterator().next());
                }
                else {
                    this.state.put(subqueryAliases[i], subqueryResult);
                }
            }
        }
        List resultSet = new ArrayList(executeCandidates);
        final Expression filter = this.compilation.getExprFilter();
        if (applyFilter && filter != null) {
            if (NucleusLogger.QUERY.isDebugEnabled()) {
                NucleusLogger.QUERY.debug(JavaQueryEvaluator.LOCALISER.msg("021012", "filter", this.language, filter));
            }
            resultSet = this.handleFilter(resultSet);
        }
        final Expression[] ordering = this.compilation.getExprOrdering();
        if (applyOrdering && ordering != null) {
            if (NucleusLogger.QUERY.isDebugEnabled()) {
                NucleusLogger.QUERY.debug(JavaQueryEvaluator.LOCALISER.msg("021012", "ordering", this.language, StringUtils.objectArrayToString(ordering)));
            }
            resultSet = this.ordering(resultSet);
        }
        if (applyRange && this.query.getRange() != null) {
            long fromIncl = this.query.getRangeFromIncl();
            long toExcl = this.query.getRangeToExcl();
            if (this.query.getRangeFromInclParam() != null) {
                fromIncl = this.parameterValues.get(this.query.getRangeFromInclParam()).longValue();
            }
            if (this.query.getRangeToExclParam() != null) {
                toExcl = this.parameterValues.get(this.query.getRangeToExclParam()).longValue();
            }
            if (NucleusLogger.QUERY.isDebugEnabled()) {
                NucleusLogger.QUERY.debug(JavaQueryEvaluator.LOCALISER.msg("021012", "range", this.language, "" + fromIncl + "," + toExcl));
            }
            resultSet = this.handleRange(resultSet, fromIncl, toExcl);
        }
        if (applyResult && result != null) {
            if (NucleusLogger.QUERY.isDebugEnabled()) {
                NucleusLogger.QUERY.debug(JavaQueryEvaluator.LOCALISER.msg("021012", "result", this.language, StringUtils.objectArrayToString(result)));
            }
            List aggregateList = new ArrayList();
            List s = resultSet;
            final Expression[] grouping = this.compilation.getExprGrouping();
            if (grouping != null) {
                s = this.sortByGrouping(resultSet);
            }
            aggregateList = s;
            if (grouping != null) {
                aggregateList = this.handleAggregates(s);
            }
            resultSet = this.handleResult(aggregateList);
            if (this.query.getResultDistinct()) {
                final List tmpList = new ArrayList();
                for (final Object obj : resultSet) {
                    if (!tmpList.contains(obj)) {
                        tmpList.add(obj);
                    }
                }
                resultSet = tmpList;
            }
        }
        if (applyResultClass && this.query.getResultClass() != null) {
            if (NucleusLogger.QUERY.isDebugEnabled()) {
                NucleusLogger.QUERY.debug(JavaQueryEvaluator.LOCALISER.msg("021012", "resultClass", this.language, this.query.getResultClass().getName()));
            }
            if (result != null && !(result[0] instanceof CreatorExpression)) {
                return this.mapResultClass(resultSet);
            }
        }
        return resultSet;
    }
    
    private List handleFilter(final List set) {
        final Expression filter = this.compilation.getExprFilter();
        if (filter == null) {
            return set;
        }
        final List result = new ArrayList();
        final Iterator it = set.iterator();
        if (NucleusLogger.QUERY.isDebugEnabled()) {
            NucleusLogger.QUERY.debug("Evaluating filter for " + set.size() + " candidates");
        }
        while (it.hasNext()) {
            final Object obj = it.next();
            if (!this.state.containsKey(this.candidateAlias)) {
                throw new NucleusUserException("Alias \"" + this.candidateAlias + "\" doesn't exist in the query or the candidate alias wasn't defined");
            }
            this.state.put(this.candidateAlias, obj);
            final InMemoryExpressionEvaluator eval = new InMemoryExpressionEvaluator(this.query.getExecutionContext(), this.parameterValues, this.state, this.query.getParsedImports(), this.clr, this.candidateAlias, this.query.getLanguage());
            final Object evalResult = this.evaluateBooleanExpression(filter, eval);
            if (!Boolean.TRUE.equals(evalResult)) {
                continue;
            }
            if (NucleusLogger.QUERY.isDebugEnabled()) {
                NucleusLogger.QUERY.debug(JavaQueryEvaluator.LOCALISER.msg("021023", StringUtils.toJVMIDString(obj)));
            }
            result.add(obj);
        }
        return result;
    }
    
    private Boolean evaluateBooleanExpression(final Expression expr, final InMemoryExpressionEvaluator eval) {
        try {
            final Object result = expr.evaluate(eval);
            return (result instanceof InMemoryFailure) ? Boolean.FALSE : result;
        }
        catch (VariableNotSetException vnse) {
            if (NucleusLogger.QUERY.isDebugEnabled()) {
                NucleusLogger.QUERY.debug(JavaQueryEvaluator.LOCALISER.msg("021024", vnse.getVariableExpression().getId(), StringUtils.objectArrayToString(vnse.getValues())));
            }
            if (vnse.getValues() == null || vnse.getValues().length == 0) {
                eval.setVariableValue(vnse.getVariableExpression().getId(), null);
                if (NucleusLogger.QUERY.isDebugEnabled()) {
                    NucleusLogger.QUERY.debug(JavaQueryEvaluator.LOCALISER.msg("021025", vnse.getVariableExpression().getId(), "(null)"));
                }
                if (Boolean.TRUE.equals(this.evaluateBooleanExpression(expr, eval))) {
                    return Boolean.TRUE;
                }
            }
            else {
                for (int i = 0; i < vnse.getValues().length; ++i) {
                    eval.setVariableValue(vnse.getVariableExpression().getId(), vnse.getValues()[i]);
                    if (NucleusLogger.QUERY.isDebugEnabled()) {
                        NucleusLogger.QUERY.debug(JavaQueryEvaluator.LOCALISER.msg("021025", vnse.getVariableExpression().getId(), vnse.getValues()[i]));
                    }
                    if (Boolean.TRUE.equals(this.evaluateBooleanExpression(expr, eval))) {
                        return Boolean.TRUE;
                    }
                }
            }
            if (NucleusLogger.QUERY.isDebugEnabled()) {
                NucleusLogger.QUERY.debug(JavaQueryEvaluator.LOCALISER.msg("021026", vnse.getVariableExpression().getId()));
            }
            eval.removeVariableValue(vnse.getVariableExpression().getId());
            return Boolean.FALSE;
        }
    }
    
    private List handleRange(final List set, final long fromIncl, final long toExcl) {
        if (toExcl - fromIncl <= 0L) {
            return Collections.EMPTY_LIST;
        }
        final List resultList = new ArrayList();
        final Iterator it = set.iterator();
        for (long l = 0L; l < fromIncl && it.hasNext(); ++l) {
            it.next();
        }
        for (long l = 0L; l < toExcl - fromIncl && it.hasNext(); ++l) {
            resultList.add(it.next());
        }
        return resultList;
    }
    
    private List sortByGrouping(final List set) {
        final Object[] o = set.toArray();
        final Expression[] grouping = this.compilation.getExprGrouping();
        Arrays.sort(o, new Comparator() {
            @Override
            public int compare(final Object arg0, final Object arg1) {
                for (int i = 0; i < grouping.length; ++i) {
                    JavaQueryEvaluator.this.state.put(JavaQueryEvaluator.this.candidateAlias, arg0);
                    final Object a = grouping[i].evaluate(JavaQueryEvaluator.this.evaluator);
                    JavaQueryEvaluator.this.state.put(JavaQueryEvaluator.this.candidateAlias, arg1);
                    final Object b = grouping[i].evaluate(JavaQueryEvaluator.this.evaluator);
                    int result = 0;
                    if (a == null && b == null) {
                        result = 0;
                    }
                    else if (a == null) {
                        result = -1;
                    }
                    else {
                        result = ((Comparable)a).compareTo(b);
                    }
                    if (result != 0) {
                        return result;
                    }
                }
                return 0;
            }
        });
        return Arrays.asList(o);
    }
    
    private List ordering(final List set) {
        final Expression[] ordering = this.compilation.getExprOrdering();
        if (ordering == null) {
            return set;
        }
        this.state.put("DATANUCLEUS_RESULTS_SET", set);
        return QueryUtils.orderCandidates(set, ordering, this.state, this.candidateAlias, this.query.getExecutionContext(), this.clr, this.parameterValues, this.query.getParsedImports(), this.query.getLanguage());
    }
    
    private List handleAggregates(final List resultSet) {
        final Expression[] grouping = this.compilation.getExprGrouping();
        final Comparator c = new Comparator() {
            @Override
            public int compare(final Object arg0, final Object arg1) {
                for (int i = 0; i < grouping.length; ++i) {
                    JavaQueryEvaluator.this.state.put(JavaQueryEvaluator.this.candidateAlias, arg0);
                    final Object a = grouping[i].evaluate(JavaQueryEvaluator.this.evaluator);
                    JavaQueryEvaluator.this.state.put(JavaQueryEvaluator.this.candidateAlias, arg1);
                    final Object b = grouping[i].evaluate(JavaQueryEvaluator.this.evaluator);
                    if (a == null && b == null) {
                        return 0;
                    }
                    if (a == null) {
                        return -1;
                    }
                    if (b == null) {
                        return 1;
                    }
                    final int result = ((Comparable)a).compareTo(b);
                    if (result != 0) {
                        return result;
                    }
                }
                return 0;
            }
        };
        final List groups = new ArrayList();
        List group = new ArrayList();
        groups.add(group);
        for (int i = 0; i < resultSet.size(); ++i) {
            if (i > 0 && c.compare(resultSet.get(i - 1), resultSet.get(i)) != 0) {
                group = new ArrayList();
                groups.add(group);
            }
            group.add(resultSet.get(i));
        }
        final List result = new ArrayList();
        final Expression having = this.compilation.getExprHaving();
        if (having != null) {
            for (int j = 0; j < groups.size(); ++j) {
                if (this.satisfiesHavingClause(groups.get(j))) {
                    result.addAll(groups.get(j));
                }
            }
        }
        else {
            for (int j = 0; j < groups.size(); ++j) {
                result.addAll(groups.get(j));
            }
        }
        return result;
    }
    
    private boolean satisfiesHavingClause(final List set) {
        this.state.put("DATANUCLEUS_RESULTS_SET", set);
        final Expression having = this.compilation.getExprHaving();
        return having.evaluate(this.evaluator) == Boolean.TRUE;
    }
    
    private List handleResult(final List resultSet) {
        List result = new ArrayList();
        final Expression[] grouping = this.compilation.getExprGrouping();
        if (grouping != null) {
            final Comparator c = new Comparator() {
                @Override
                public int compare(final Object arg0, final Object arg1) {
                    for (int i = 0; i < grouping.length; ++i) {
                        JavaQueryEvaluator.this.state.put(JavaQueryEvaluator.this.candidateAlias, arg0);
                        final Object a = grouping[i].evaluate(JavaQueryEvaluator.this.evaluator);
                        JavaQueryEvaluator.this.state.put(JavaQueryEvaluator.this.candidateAlias, arg1);
                        final Object b = grouping[i].evaluate(JavaQueryEvaluator.this.evaluator);
                        if (a == null && b == null) {
                            return 0;
                        }
                        if (a == null) {
                            return -1;
                        }
                        if (b == null) {
                            return 1;
                        }
                        final int result = ((Comparable)a).compareTo(b);
                        if (result != 0) {
                            return result;
                        }
                    }
                    return 0;
                }
            };
            final List groups = new ArrayList();
            List group = new ArrayList();
            if (resultSet.size() > 0) {
                groups.add(group);
            }
            for (int i = 0; i < resultSet.size(); ++i) {
                if (i > 0 && c.compare(resultSet.get(i - 1), resultSet.get(i)) != 0) {
                    group = new ArrayList();
                    groups.add(group);
                }
                group.add(resultSet.get(i));
            }
            for (int i = 0; i < groups.size(); ++i) {
                group = groups.get(i);
                result.add(this.result(group));
            }
        }
        else {
            boolean aggregates = false;
            final Expression[] resultExprs = this.compilation.getExprResult();
            if (resultExprs.length > 0 && resultExprs[0] instanceof CreatorExpression) {
                final Expression[] resExpr = ((CreatorExpression)resultExprs[0]).getArguments().toArray(new Expression[((CreatorExpression)resultExprs[0]).getArguments().size()]);
                for (int i = 0; i < resExpr.length; ++i) {
                    if (resExpr[i] instanceof InvokeExpression) {
                        final String method = ((InvokeExpression)resExpr[i]).getOperation().toLowerCase();
                        if (method.equals("count") || method.equals("sum") || method.equals("avg") || method.equals("min") || method.equals("max")) {
                            aggregates = true;
                        }
                    }
                }
            }
            else {
                for (int j = 0; j < resultExprs.length; ++j) {
                    if (resultExprs[j] instanceof InvokeExpression) {
                        final String method2 = ((InvokeExpression)resultExprs[j]).getOperation().toLowerCase();
                        if (method2.equals("count") || method2.equals("sum") || method2.equals("avg") || method2.equals("min") || method2.equals("max")) {
                            aggregates = true;
                        }
                    }
                }
            }
            if (aggregates) {
                result.add(this.result(resultSet));
            }
            else {
                for (int j = 0; j < resultSet.size(); ++j) {
                    result.add(this.result(resultSet.get(j)));
                }
            }
        }
        if (result.size() > 0 && ((Object[])result.get(0)).length == 1) {
            final List r = result;
            result = new ArrayList();
            for (int k = 0; k < r.size(); ++k) {
                result.add(((Object[])r.get(k))[0]);
            }
        }
        return result;
    }
    
    private Object[] result(final Object obj) {
        this.state.put(this.candidateAlias, obj);
        final Expression[] result = this.compilation.getExprResult();
        final Object[] r = new Object[result.length];
        for (int i = 0; i < result.length; ++i) {
            r[i] = result[i].evaluate(this.evaluator);
        }
        return r;
    }
    
    private Object[] result(final List set) {
        this.state.put("DATANUCLEUS_RESULTS_SET", set);
        final Expression[] result = this.compilation.getExprResult();
        final Object element = (set != null && set.size() > 0) ? set.get(0) : null;
        this.state.put(this.candidateAlias, element);
        final Object[] r = new Object[result.length];
        for (int j = 0; j < result.length; ++j) {
            r[j] = result[j].evaluate(this.evaluator);
        }
        return r;
    }
    
    abstract Collection mapResultClass(final Collection p0);
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
