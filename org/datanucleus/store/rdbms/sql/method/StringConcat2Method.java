// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import java.util.ArrayList;
import org.datanucleus.store.rdbms.sql.expression.ParameterLiteral;
import org.datanucleus.store.rdbms.sql.expression.CharacterExpression;
import org.datanucleus.store.rdbms.sql.expression.StringExpression;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class StringConcat2Method extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args == null || args.size() != 1) {
            throw new NucleusException(StringConcat2Method.LOCALISER.msg("060003", "concat", "StringExpression", 0, "StringExpression/CharacterExpression/Parameter"));
        }
        final SQLExpression otherExpr = args.get(0);
        if (!(otherExpr instanceof StringExpression) && !(otherExpr instanceof CharacterExpression) && !(otherExpr instanceof ParameterLiteral)) {
            throw new NucleusException(StringConcat2Method.LOCALISER.msg("060003", "concat", "StringExpression", 0, "StringExpression/CharacterExpression/Parameter"));
        }
        final ArrayList funcArgs = new ArrayList();
        funcArgs.add(expr);
        funcArgs.add(otherExpr);
        return new StringExpression(this.stmt, this.getMappingForClass(String.class), "CONCAT", funcArgs);
    }
}
