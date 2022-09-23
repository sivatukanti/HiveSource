// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.sql.expression.CaseExpression;
import org.datanucleus.store.rdbms.sql.expression.IntegerLiteral;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.store.rdbms.sql.expression.NumericExpression;
import org.datanucleus.store.rdbms.sql.expression.ExpressionUtils;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.sql.expression.ParameterLiteral;
import org.datanucleus.store.rdbms.sql.expression.CharacterExpression;
import org.datanucleus.store.rdbms.sql.expression.StringExpression;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class StringIndexOf5Method extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args == null || args.size() == 0 || args.size() > 2) {
            throw new NucleusException(StringIndexOf5Method.LOCALISER.msg("060003", "indexOf", "StringExpression", 0, "StringExpression/CharacterExpression/ParameterLiteral"));
        }
        final SQLExpression substrExpr = args.get(0);
        if (!(substrExpr instanceof StringExpression) && !(substrExpr instanceof CharacterExpression) && !(substrExpr instanceof ParameterLiteral)) {
            throw new NucleusException(StringIndexOf5Method.LOCALISER.msg("060003", "indexOf", "StringExpression", 0, "StringExpression/CharacterExpression/ParameterLiteral"));
        }
        final ArrayList funcArgs = new ArrayList();
        if (args.size() == 1) {
            funcArgs.add(expr);
            funcArgs.add(substrExpr);
            final SQLExpression oneExpr = ExpressionUtils.getLiteralForOne(this.stmt);
            final NumericExpression locateExpr = new NumericExpression(this.stmt, this.getMappingForClass(Integer.TYPE), "STRPOS", funcArgs);
            return new NumericExpression(locateExpr, Expression.OP_SUB, oneExpr);
        }
        final SQLExpression fromExpr = args.get(1);
        if (!(fromExpr instanceof NumericExpression)) {
            throw new NucleusException(StringIndexOf5Method.LOCALISER.msg("060003", "indexOf", "StringExpression", 1, "NumericExpression"));
        }
        final ArrayList substrArgs = new ArrayList(1);
        substrArgs.add(fromExpr);
        final SQLExpression strExpr = this.exprFactory.invokeMethod(this.stmt, "java.lang.String", "substring", expr, substrArgs);
        funcArgs.add(strExpr);
        funcArgs.add(substrExpr);
        final NumericExpression locateExpr2 = new NumericExpression(this.stmt, this.getMappingForClass(Integer.TYPE), "STRPOS", funcArgs);
        final SQLExpression[] whenExprs = { null };
        final NumericExpression zeroExpr = new IntegerLiteral(this.stmt, this.exprFactory.getMappingForType(Integer.class, false), 0, null);
        whenExprs[0] = locateExpr2.gt(zeroExpr);
        final SQLExpression[] actionExprs = { null };
        final SQLExpression oneExpr2 = ExpressionUtils.getLiteralForOne(this.stmt);
        final NumericExpression posExpr1 = new NumericExpression(locateExpr2, Expression.OP_SUB, oneExpr2);
        actionExprs[0] = new NumericExpression(posExpr1, Expression.OP_ADD, fromExpr);
        final SQLExpression elseExpr = new IntegerLiteral(this.stmt, this.exprFactory.getMappingForType(Integer.class, false), -1, null);
        return new CaseExpression(whenExprs, actionExprs, elseExpr);
    }
}
