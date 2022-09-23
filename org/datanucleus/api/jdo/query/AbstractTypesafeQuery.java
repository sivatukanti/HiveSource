// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.query;

import java.util.Iterator;
import org.datanucleus.query.expression.DyadicExpression;
import org.datanucleus.query.typesafe.OrderExpression;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.symbol.Symbol;
import org.datanucleus.query.symbol.PropertySymbol;
import org.datanucleus.query.symbol.SymbolResolver;
import org.datanucleus.query.compiler.JDOQLSymbolResolver;
import org.datanucleus.query.symbol.SymbolTable;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.query.compiler.QueryCompilation;
import org.datanucleus.ExecutionContext;
import javax.jdo.PersistenceManager;
import java.util.List;

public abstract class AbstractTypesafeQuery<T>
{
    protected QueryType type;
    protected Class candidateCls;
    protected String candidateAlias;
    protected List<ExpressionImpl> updateExprs;
    protected List<ExpressionImpl> updateVals;
    protected List<ExpressionImpl> result;
    protected Boolean resultDistinct;
    protected BooleanExpressionImpl filter;
    protected List<ExpressionImpl> grouping;
    protected ExpressionImpl having;
    protected List<OrderExpressionImpl> ordering;
    protected PersistenceManager pm;
    protected ExecutionContext ec;
    QueryCompilation compilation;
    
    public AbstractTypesafeQuery(final PersistenceManager pm, final Class<T> cls, final String alias) {
        this.type = QueryType.SELECT;
        this.candidateAlias = null;
        this.resultDistinct = null;
        this.compilation = null;
        this.pm = pm;
        this.ec = ((JDOPersistenceManager)pm).getExecutionContext();
        this.candidateCls = cls;
        this.candidateAlias = alias;
    }
    
    protected void discardCompiled() {
        this.compilation = null;
    }
    
    protected QueryCompilation compile(final MetaDataManager mmgr, final ClassLoaderResolver clr) {
        final SymbolTable symtbl = new SymbolTable();
        symtbl.setSymbolResolver(new JDOQLSymbolResolver(mmgr, clr, symtbl, this.candidateCls, this.candidateAlias));
        symtbl.addSymbol(new PropertySymbol(this.candidateAlias, this.candidateCls));
        Expression[] resultExprs = null;
        if (this.result != null && !this.result.isEmpty()) {
            resultExprs = new Expression[this.result.size()];
            final Iterator iter = this.result.iterator();
            int i = 0;
            while (iter.hasNext()) {
                final ExpressionImpl result = iter.next();
                final Expression resultExpr = result.getQueryExpression();
                resultExpr.bind(symtbl);
                resultExprs[i++] = resultExpr;
            }
            if (resultExprs.length == 1 && resultExprs[0] instanceof PrimaryExpression) {
                final String resultExprId = ((PrimaryExpression)resultExprs[0]).getId();
                if (resultExprId.equalsIgnoreCase(this.candidateAlias)) {
                    resultExprs = null;
                }
            }
        }
        Expression filterExpr = null;
        if (this.filter != null) {
            filterExpr = this.filter.getQueryExpression();
            if (filterExpr != null) {
                filterExpr.bind(symtbl);
            }
        }
        Expression[] groupingExprs = null;
        if (this.grouping != null && !this.grouping.isEmpty()) {
            groupingExprs = new Expression[this.grouping.size()];
            final Iterator iter2 = this.grouping.iterator();
            int j = 0;
            while (iter2.hasNext()) {
                final ExpressionImpl grp = iter2.next();
                final Expression groupingExpr = grp.getQueryExpression();
                groupingExpr.bind(symtbl);
                groupingExprs[j++] = groupingExpr;
            }
        }
        Expression havingExpr = null;
        if (this.having != null) {
            havingExpr = this.having.getQueryExpression();
            havingExpr.bind(symtbl);
        }
        Expression[] orderExprs = null;
        if (this.ordering != null && !this.ordering.isEmpty()) {
            orderExprs = new Expression[this.ordering.size()];
            final Iterator<OrderExpressionImpl> iter3 = (Iterator<OrderExpressionImpl>)this.ordering.iterator();
            int k = 0;
            while (iter3.hasNext()) {
                final OrderExpressionImpl order = iter3.next();
                final org.datanucleus.query.expression.OrderExpression orderExpr = new org.datanucleus.query.expression.OrderExpression(((ExpressionImpl)order.getExpression()).getQueryExpression(), (order.getDirection() == OrderExpression.OrderDirection.ASC) ? "ascending" : "descending");
                orderExpr.bind(symtbl);
                orderExprs[k++] = orderExpr;
            }
        }
        Expression[] updateExprs = null;
        if (this.updateExprs != null) {
            final Iterator<ExpressionImpl> expIter = (Iterator<ExpressionImpl>)this.updateExprs.iterator();
            final Iterator<ExpressionImpl> valIter = (Iterator<ExpressionImpl>)this.updateVals.iterator();
            updateExprs = new Expression[this.updateExprs.size()];
            int l = 0;
            while (expIter.hasNext()) {
                final ExpressionImpl updateExpr = expIter.next();
                final ExpressionImpl updateVal = valIter.next();
                updateExprs[l++] = new DyadicExpression(updateExpr.getQueryExpression(), Expression.OP_EQ, updateVal.getQueryExpression());
            }
        }
        (this.compilation = new QueryCompilation(this.candidateCls, this.candidateAlias, symtbl, resultExprs, null, filterExpr, groupingExprs, havingExpr, orderExprs, updateExprs)).setQueryLanguage("JDOQL");
        return this.compilation;
    }
    
    public QueryCompilation getCompilation() {
        if (this.compilation == null) {
            this.compilation = this.compile(this.ec.getMetaDataManager(), this.ec.getClassLoaderResolver());
        }
        return this.compilation;
    }
    
    enum QueryType
    {
        SELECT, 
        BULK_UPDATE, 
        BULK_DELETE;
    }
}
