// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator;

import org.datanucleus.query.expression.Expression;
import java.util.List;
import org.datanucleus.ClassLoaderResolver;
import java.util.Map;
import org.datanucleus.query.compiler.QueryCompilation;
import java.util.Collection;
import org.datanucleus.store.query.Query;

public class JDOQLEvaluator extends JavaQueryEvaluator
{
    public JDOQLEvaluator(final Query query, final Collection candidates, final QueryCompilation compilation, final Map parameterValues, final ClassLoaderResolver clr) {
        super("JDOQL", query, compilation, parameterValues, clr, candidates);
    }
    
    @Override
    protected Collection evaluateSubquery(final Query query, final QueryCompilation compilation, final Collection candidates, final Object outerCandidate) {
        final JDOQLEvaluator eval = new JDOQLEvaluator(query, candidates, compilation, this.parameterValues, this.clr);
        return eval.execute(true, true, true, true, true);
    }
    
    @Override
    public Collection execute(final boolean applyFilter, final boolean applyOrdering, final boolean applyResult, final boolean applyResultClass, final boolean applyRange) {
        final Collection results = super.execute(applyFilter, applyOrdering, applyResult, applyResultClass, applyRange);
        if (results instanceof List) {
            return new InMemoryQueryResult((List)results, this.query.getExecutionContext().getApiAdapter());
        }
        return results;
    }
    
    @Override
    Collection mapResultClass(final Collection resultSet) {
        final Expression[] result = this.compilation.getExprResult();
        return new JDOQLResultClassMapper(this.query.getResultClass()).map(resultSet, result);
    }
}
