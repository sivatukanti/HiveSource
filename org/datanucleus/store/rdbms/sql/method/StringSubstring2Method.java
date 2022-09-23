// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.sql.expression.StringExpression;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.sql.expression.ExpressionUtils;
import org.datanucleus.store.rdbms.sql.expression.ParameterLiteral;
import org.datanucleus.store.rdbms.sql.expression.IntegerLiteral;
import org.datanucleus.store.rdbms.sql.expression.NumericExpression;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class StringSubstring2Method extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args == null || args.size() == 0 || args.size() > 2) {
            throw new NucleusException(StringSubstring2Method.LOCALISER.msg("060003", "substring", "StringExpression", 0, "NumericExpression/IntegerLiteral/ParameterLiteral"));
        }
        if (args.size() == 1) {
            final SQLExpression startExpr = args.get(0);
            if (!(startExpr instanceof NumericExpression) && !(startExpr instanceof IntegerLiteral) && !(startExpr instanceof ParameterLiteral)) {
                throw new NucleusException(StringSubstring2Method.LOCALISER.msg("060003", "substring", "StringExpression", 0, "NumericExpression/IntegerLiteral/ParameterLiteral"));
            }
            final SQLExpression one = ExpressionUtils.getLiteralForOne(this.stmt);
            final ArrayList funcArgs = new ArrayList();
            funcArgs.add(expr);
            funcArgs.add(startExpr.add(one));
            return new StringExpression(this.stmt, this.getMappingForClass(String.class), "SUBSTRING", funcArgs);
        }
        else {
            final SQLExpression startExpr = args.get(0);
            if (!(startExpr instanceof NumericExpression)) {
                throw new NucleusException(StringSubstring2Method.LOCALISER.msg("060003", "substring", "StringExpression", 0, "NumericExpression"));
            }
            final SQLExpression endExpr = args.get(1);
            if (!(endExpr instanceof NumericExpression)) {
                throw new NucleusException(StringSubstring2Method.LOCALISER.msg("060003", "substring", "StringExpression", 1, "NumericExpression"));
            }
            final SQLExpression one2 = ExpressionUtils.getLiteralForOne(this.stmt);
            final ArrayList funcArgs2 = new ArrayList();
            funcArgs2.add(expr);
            funcArgs2.add(startExpr.add(one2));
            funcArgs2.add(endExpr.sub(startExpr));
            return new StringExpression(this.stmt, this.getMappingForClass(String.class), "SUBSTRING", funcArgs2);
        }
    }
}
