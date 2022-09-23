// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.store.rdbms.sql.expression.NumericExpression;
import org.datanucleus.store.rdbms.sql.expression.StringExpression;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.sql.expression.ExpressionUtils;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.sql.expression.TemporalExpression;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class DateGetMonth2Method extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (!(expr instanceof TemporalExpression)) {
            throw new NucleusException(DateGetMonth2Method.LOCALISER.msg("060001", "getMonth()", expr));
        }
        final SQLExpression one = ExpressionUtils.getLiteralForOne(this.stmt);
        final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
        final JavaTypeMapping mapping2 = storeMgr.getMappingManager().getMapping(String.class);
        final SQLExpression mm = this.exprFactory.newLiteral(this.stmt, mapping2, "MM");
        final ArrayList funcArgs = new ArrayList();
        funcArgs.add(expr);
        funcArgs.add(mm);
        final ArrayList funcArgs2 = new ArrayList();
        funcArgs2.add(new StringExpression(this.stmt, this.getMappingForClass(Integer.TYPE), "TO_CHAR", funcArgs));
        final NumericExpression numExpr = new NumericExpression(new NumericExpression(this.stmt, this.getMappingForClass(Integer.TYPE), "TO_NUMBER", funcArgs2), Expression.OP_SUB, one);
        numExpr.encloseInParentheses();
        return numExpr;
    }
}
