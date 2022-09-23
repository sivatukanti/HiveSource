// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.expression.NumericSubqueryExpression;
import org.datanucleus.store.rdbms.sql.expression.StringLiteral;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.sql.expression.AggregateNumericExpression;
import org.datanucleus.query.compiler.CompilationComponent;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class AvgFunction extends SimpleNumericAggregateMethod
{
    @Override
    protected String getFunctionName() {
        return "AVG";
    }
    
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (expr != null) {
            throw new NucleusException(AvgFunction.LOCALISER.msg("060002", this.getFunctionName(), expr));
        }
        if (args == null || args.size() != 1) {
            throw new NucleusException(this.getFunctionName() + " is only supported with a single argument");
        }
        final Class returnType = Double.class;
        if (this.stmt.getQueryGenerator().getCompilationComponent() == CompilationComponent.RESULT || this.stmt.getQueryGenerator().getCompilationComponent() == CompilationComponent.HAVING) {
            final JavaTypeMapping m = this.getMappingForClass(returnType);
            return new AggregateNumericExpression(this.stmt, m, this.getFunctionName(), args);
        }
        final SQLExpression argExpr = args.get(0);
        final SQLStatement subStmt = new SQLStatement(this.stmt, this.stmt.getRDBMSManager(), argExpr.getSQLTable().getTable(), argExpr.getSQLTable().getAlias(), null);
        subStmt.setClassLoaderResolver(this.clr);
        final JavaTypeMapping mapping = this.stmt.getRDBMSManager().getMappingManager().getMappingWithDatastoreMapping(String.class, false, false, this.clr);
        final String aggregateString = this.getFunctionName() + "(" + argExpr.toSQLText() + ")";
        final SQLExpression aggExpr = this.exprFactory.newLiteral(subStmt, mapping, aggregateString);
        ((StringLiteral)aggExpr).generateStatementWithoutQuotes();
        subStmt.select(aggExpr, null);
        final JavaTypeMapping subqMapping = this.exprFactory.getMappingForType(returnType, false);
        final SQLExpression subqExpr = new NumericSubqueryExpression(this.stmt, subStmt);
        subqExpr.setJavaTypeMapping(subqMapping);
        return subqExpr;
    }
    
    @Override
    protected Class getClassForMapping() {
        return Double.TYPE;
    }
}
