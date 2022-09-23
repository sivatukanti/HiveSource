// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.compiler;

import org.datanucleus.ClassConstants;
import org.datanucleus.query.QueryUtils;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.symbol.Symbol;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.symbol.SymbolTable;
import org.datanucleus.util.Localiser;
import java.io.Serializable;

public class QueryCompilation implements Serializable
{
    protected static final Localiser LOCALISER;
    protected String queryLanguage;
    protected Class candidateClass;
    protected String candidateAlias;
    protected boolean returnsSingleRow;
    protected SymbolTable symtbl;
    protected boolean resultDistinct;
    protected Expression[] exprResult;
    protected Expression[] exprFrom;
    protected Expression[] exprUpdate;
    protected Expression exprFilter;
    protected Expression[] exprGrouping;
    protected Expression exprHaving;
    protected Expression[] exprOrdering;
    protected Map<String, QueryCompilation> subqueryCompilations;
    
    public QueryCompilation(final Class candidateCls, final String candidateAlias, final SymbolTable symtbl, final Expression[] results, final Expression[] froms, final Expression filter, final Expression[] groupings, final Expression having, final Expression[] orderings, final Expression[] updates) {
        this.candidateAlias = "this";
        this.returnsSingleRow = false;
        this.resultDistinct = false;
        this.exprFilter = null;
        this.subqueryCompilations = null;
        this.candidateClass = candidateCls;
        this.candidateAlias = candidateAlias;
        this.symtbl = symtbl;
        this.exprResult = results;
        this.exprFrom = froms;
        this.exprFilter = filter;
        this.exprGrouping = groupings;
        this.exprHaving = having;
        this.exprOrdering = orderings;
        this.exprUpdate = updates;
    }
    
    public void setQueryLanguage(final String lang) {
        this.queryLanguage = lang;
    }
    
    public String getQueryLanguage() {
        return this.queryLanguage;
    }
    
    public void setResultDistinct() {
        this.resultDistinct = true;
    }
    
    public boolean getResultDistinct() {
        return this.resultDistinct;
    }
    
    public void setReturnsSingleRow() {
        this.returnsSingleRow = true;
    }
    
    public void addSubqueryCompilation(final String alias, final QueryCompilation compilation) {
        if (this.subqueryCompilations == null) {
            this.subqueryCompilations = new HashMap<String, QueryCompilation>();
        }
        this.subqueryCompilations.put(alias, compilation);
    }
    
    public QueryCompilation getCompilationForSubquery(final String alias) {
        return (this.subqueryCompilations != null) ? this.subqueryCompilations.get(alias) : null;
    }
    
    public String[] getSubqueryAliases() {
        if (this.subqueryCompilations == null || this.subqueryCompilations.isEmpty()) {
            return null;
        }
        final String[] aliases = new String[this.subqueryCompilations.size()];
        final Iterator<String> iter = this.subqueryCompilations.keySet().iterator();
        int i = 0;
        while (iter.hasNext()) {
            aliases[i++] = iter.next();
        }
        return aliases;
    }
    
    public boolean returnsSingleRow() {
        return this.returnsSingleRow;
    }
    
    public Class[] getResultTypes() {
        if (this.exprResult != null && this.exprResult.length > 0) {
            final Class[] results = new Class[this.exprResult.length];
            for (int i = 0; i < this.exprResult.length; ++i) {
                final Symbol colSym = this.exprResult[i].getSymbol();
                results[i] = colSym.getValueType();
            }
            return results;
        }
        return new Class[] { this.candidateClass };
    }
    
    public Class getCandidateClass() {
        return this.candidateClass;
    }
    
    public String getCandidateAlias() {
        return this.candidateAlias;
    }
    
    public SymbolTable getSymbolTable() {
        return this.symtbl;
    }
    
    public Expression[] getExprResult() {
        return this.exprResult;
    }
    
    public Expression[] getExprFrom() {
        return this.exprFrom;
    }
    
    public Expression[] getExprUpdate() {
        return this.exprUpdate;
    }
    
    public Expression getExprFilter() {
        return this.exprFilter;
    }
    
    public void setExprFilter(final Expression filter) {
        this.exprFilter = filter;
    }
    
    public Expression[] getExprGrouping() {
        return this.exprGrouping;
    }
    
    public Expression getExprHaving() {
        return this.exprHaving;
    }
    
    public void setExprHaving(final Expression having) {
        this.exprHaving = having;
    }
    
    public Expression[] getExprOrdering() {
        return this.exprOrdering;
    }
    
    public ParameterExpression getParameterExpressionForPosition(final int pos) {
        ParameterExpression paramExpr = null;
        if (this.exprResult != null) {
            for (int i = 0; i < this.exprResult.length; ++i) {
                paramExpr = QueryUtils.getParameterExpressionForPosition(this.exprResult[i], pos);
                if (paramExpr != null) {
                    return paramExpr;
                }
            }
        }
        if (this.exprFilter != null) {
            paramExpr = QueryUtils.getParameterExpressionForPosition(this.exprFilter, pos);
            if (paramExpr != null) {
                return paramExpr;
            }
        }
        if (this.exprGrouping != null) {
            for (int i = 0; i < this.exprGrouping.length; ++i) {
                paramExpr = QueryUtils.getParameterExpressionForPosition(this.exprGrouping[i], pos);
                if (paramExpr != null) {
                    return paramExpr;
                }
            }
        }
        if (this.exprHaving != null) {
            paramExpr = QueryUtils.getParameterExpressionForPosition(this.exprHaving, pos);
            if (paramExpr != null) {
                return paramExpr;
            }
        }
        if (this.exprOrdering != null) {
            for (int i = 0; i < this.exprOrdering.length; ++i) {
                paramExpr = QueryUtils.getParameterExpressionForPosition(this.exprOrdering[i], pos);
                if (paramExpr != null) {
                    return paramExpr;
                }
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder("QueryCompilation:\n");
        str.append(this.debugString("  "));
        return str.toString();
    }
    
    public String debugString(final String indent) {
        final StringBuilder str = new StringBuilder();
        if (this.exprResult != null) {
            str.append(indent).append("[result:");
            if (this.resultDistinct) {
                str.append(" DISTINCT ");
            }
            for (int i = 0; i < this.exprResult.length; ++i) {
                if (i > 0) {
                    str.append(",");
                }
                str.append(this.exprResult[i]);
            }
            str.append("]\n");
        }
        if (this.exprFrom != null) {
            str.append(indent).append("[from:");
            for (int i = 0; i < this.exprFrom.length; ++i) {
                if (i > 0) {
                    str.append(",");
                }
                str.append(this.exprFrom[i]);
            }
            str.append("]\n");
        }
        if (this.exprUpdate != null) {
            str.append(indent).append("[update:");
            for (int i = 0; i < this.exprUpdate.length; ++i) {
                if (i > 0) {
                    str.append(",");
                }
                str.append(this.exprUpdate[i]);
            }
            str.append("]\n");
        }
        if (this.exprFilter != null) {
            str.append(indent).append("[filter:").append(this.exprFilter).append("]\n");
        }
        if (this.exprGrouping != null) {
            str.append(indent).append("[grouping:");
            for (int i = 0; i < this.exprGrouping.length; ++i) {
                if (i > 0) {
                    str.append(",");
                }
                str.append(this.exprGrouping[i]);
            }
            str.append("]\n");
        }
        if (this.exprHaving != null) {
            str.append(indent).append("[having:").append(this.exprHaving).append("]\n");
        }
        if (this.exprOrdering != null) {
            str.append(indent).append("[ordering:");
            for (int i = 0; i < this.exprOrdering.length; ++i) {
                if (i > 0) {
                    str.append(",");
                }
                str.append(this.exprOrdering[i]);
            }
            str.append("]\n");
        }
        str.append(indent).append("[symbols: ");
        final Iterator<String> symIter = this.symtbl.getSymbolNames().iterator();
        while (symIter.hasNext()) {
            final String symName = symIter.next();
            final Symbol sym = this.symtbl.getSymbol(symName);
            if (sym.getValueType() != null) {
                str.append(symName + " type=" + sym.getValueType().getName());
            }
            else {
                str.append(symName + " type=unknown");
            }
            if (symIter.hasNext()) {
                str.append(", ");
            }
        }
        str.append("]");
        if (this.subqueryCompilations != null) {
            str.append("\n");
            final Iterator subqIter = this.subqueryCompilations.entrySet().iterator();
            while (subqIter.hasNext()) {
                final Map.Entry<String, QueryCompilation> entry = subqIter.next();
                str.append(indent).append("[subquery: " + entry.getKey() + "\n");
                str.append(entry.getValue().debugString(indent + "  ")).append("]");
                if (subqIter.hasNext()) {
                    str.append("\n");
                }
            }
        }
        return str.toString();
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
