// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.sql.expression.CharacterExpression;
import org.datanucleus.store.rdbms.sql.expression.StringExpression;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class StringReplaceAllMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args == null || args.size() != 2) {
            throw new NucleusException(StringReplaceAllMethod.LOCALISER.msg("060003", "replaceAll", "StringExpression", 2, "StringExpression/CharacterExpression"));
        }
        final SQLExpression strExpr1 = args.get(0);
        final SQLExpression strExpr2 = args.get(1);
        if (!(strExpr1 instanceof StringExpression) && !(strExpr1 instanceof CharacterExpression)) {
            throw new NucleusException(StringReplaceAllMethod.LOCALISER.msg("060003", "replaceAll", "StringExpression", 1, "StringExpression/CharacterExpression"));
        }
        if (!(strExpr2 instanceof StringExpression) && !(strExpr2 instanceof CharacterExpression)) {
            throw new NucleusException(StringReplaceAllMethod.LOCALISER.msg("060003", "replaceAll", "StringExpression", 2, "StringExpression/CharacterExpression"));
        }
        final List<SQLExpression> newArgs = new ArrayList<SQLExpression>(3);
        newArgs.add(expr);
        newArgs.add(strExpr1);
        newArgs.add(strExpr2);
        final JavaTypeMapping mapping = this.exprFactory.getMappingForType(String.class, false);
        return new StringExpression(this.stmt, mapping, "replace", newArgs);
    }
}
