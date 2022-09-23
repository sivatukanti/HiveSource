// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.compiler;

import org.datanucleus.query.JPQLQueryHelper;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.symbol.Symbol;
import java.util.Iterator;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.query.symbol.PropertySymbol;
import org.datanucleus.query.symbol.SymbolResolver;
import org.datanucleus.query.symbol.SymbolTable;
import java.util.Map;
import org.datanucleus.util.Imports;
import java.util.Collection;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.MetaDataManager;

public class JPQLCompiler extends JavaQueryCompiler
{
    public JPQLCompiler(final MetaDataManager metaDataManager, final ClassLoaderResolver clr, final String from, final Class candidateClass, final Collection candidates, final String filter, final Imports imports, final String ordering, final String result, final String grouping, final String having, final String params, final String update) {
        super(metaDataManager, clr, from, candidateClass, candidates, filter, imports, ordering, result, grouping, having, params, null, update);
        this.from = from;
        this.caseSensitiveAliases = false;
    }
    
    @Override
    public QueryCompilation compile(final Map parameters, final Map subqueryMap) {
        this.parser = new JPQLParser(null, parameters);
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
        Expression[] exprResult = this.compileResult();
        final Expression[] exprGrouping = this.compileGrouping();
        final Expression exprHaving = this.compileHaving();
        final Expression[] exprUpdate = this.compileUpdate();
        if (exprResult != null && exprResult.length == 1 && exprResult[0] instanceof PrimaryExpression) {
            final String resultExprId = ((PrimaryExpression)exprResult[0]).getId();
            if (resultExprId.equalsIgnoreCase(this.candidateAlias)) {
                exprResult = null;
            }
        }
        final QueryCompilation compilation = new QueryCompilation(this.candidateClass, this.candidateAlias, this.symtbl, exprResult, exprFrom, exprFilter, exprGrouping, exprHaving, exprOrdering, exprUpdate);
        compilation.setQueryLanguage(this.getLanguage());
        return compilation;
    }
    
    @Override
    public boolean supportsImplicitVariables() {
        return false;
    }
    
    @Override
    public boolean caseSensitiveSymbolNames() {
        return false;
    }
    
    @Override
    public String getLanguage() {
        return "JPQL";
    }
    
    @Override
    protected boolean isKeyword(final String name) {
        return JPQLQueryHelper.isKeyword(name);
    }
}
