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

public abstract class SimpleNumericAggregateMethod extends AbstractSQLMethod
{
    protected abstract String getFunctionName();
    
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (expr != null) {
            throw new NucleusException(SimpleNumericAggregateMethod.LOCALISER.msg("060002", this.getFunctionName(), expr));
        }
        if (args == null || args.size() != 1) {
            throw new NucleusException(this.getFunctionName() + " is only supported with a single argument");
        }
        if (this.stmt.getQueryGenerator().getCompilationComponent() == CompilationComponent.RESULT || this.stmt.getQueryGenerator().getCompilationComponent() == CompilationComponent.HAVING) {
            JavaTypeMapping m = null;
            if (args.get(0) instanceof SQLExpression) {
                final SQLExpression argExpr = args.get(0);
                m = this.getMappingForClass(argExpr.getJavaTypeMapping().getJavaType());
            }
            else {
                m = this.getMappingForClass(this.getClassForMapping());
            }
            return new AggregateNumericExpression(this.stmt, m, this.getFunctionName(), args);
        }
        final SQLExpression argExpr2 = args.get(0);
        final SQLStatement subStmt = new SQLStatement(this.stmt, this.stmt.getRDBMSManager(), argExpr2.getSQLTable().getTable(), argExpr2.getSQLTable().getAlias(), null);
        subStmt.setClassLoaderResolver(this.clr);
        final JavaTypeMapping mapping = this.stmt.getRDBMSManager().getMappingManager().getMappingWithDatastoreMapping(String.class, false, false, this.clr);
        final String aggregateString = this.getFunctionName() + "(" + argExpr2.toSQLText() + ")";
        final SQLExpression aggExpr = this.exprFactory.newLiteral(subStmt, mapping, aggregateString);
        ((StringLiteral)aggExpr).generateStatementWithoutQuotes();
        subStmt.select(aggExpr, null);
        final JavaTypeMapping subqMapping = this.exprFactory.getMappingForType(Integer.class, false);
        final SQLExpression subqExpr = new NumericSubqueryExpression(this.stmt, subStmt);
        subqExpr.setJavaTypeMapping(subqMapping);
        return subqExpr;
    }
    
    protected abstract Class getClassForMapping();
}
