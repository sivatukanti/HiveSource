// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator;

import org.datanucleus.query.expression.Expression;
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;
import org.datanucleus.ClassLoaderResolver;
import java.util.Map;
import org.datanucleus.query.compiler.QueryCompilation;
import java.util.Collection;
import org.datanucleus.store.query.Query;

public class JPQLEvaluator extends JavaQueryEvaluator
{
    public JPQLEvaluator(final Query query, final Collection candidates, final QueryCompilation compilation, final Map parameterValues, final ClassLoaderResolver clr) {
        super("JPQL", query, compilation, parameterValues, clr, candidates);
        if (this.parameterValues != null && this.parameterValues.size() > 0) {
            final Set keys = this.parameterValues.keySet();
            boolean numericKeys = false;
            int origin = Integer.MAX_VALUE;
            for (final Object key : keys) {
                if (numericKeys || key instanceof Integer) {
                    numericKeys = true;
                    if ((int)key >= origin) {
                        continue;
                    }
                    origin = (int)key;
                }
            }
            if (numericKeys && origin != 0) {
                final Map paramValues = new HashMap();
                for (final Map.Entry entry : this.parameterValues.entrySet()) {
                    if (entry.getKey() instanceof Integer) {
                        final int pos = entry.getKey();
                        paramValues.put(pos - 1, entry.getValue());
                    }
                    else {
                        paramValues.put(entry.getKey(), entry.getValue());
                    }
                }
                this.parameterValues = paramValues;
            }
        }
    }
    
    @Override
    protected Collection evaluateSubquery(final Query query, final QueryCompilation compilation, final Collection candidates, final Object outerCandidate) {
        final JPQLEvaluator eval = new JPQLEvaluator(query, candidates, compilation, this.parameterValues, this.clr);
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
        return new JPQLResultClassMapper(this.query.getResultClass()).map(resultSet, result);
    }
}
