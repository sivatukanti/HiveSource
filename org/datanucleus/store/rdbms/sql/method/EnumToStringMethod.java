// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.ClassNameConstants;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.sql.expression.EnumExpression;
import org.datanucleus.store.rdbms.sql.expression.StringLiteral;
import org.datanucleus.store.rdbms.sql.expression.EnumLiteral;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class EnumToStringMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (expr instanceof EnumLiteral) {
            final Enum val = (Enum)((EnumLiteral)expr).getValue();
            return new StringLiteral(this.stmt, this.exprFactory.getMappingForType(String.class, false), val.toString(), (String)null);
        }
        if (!(expr instanceof EnumExpression)) {
            throw new NucleusException(EnumToStringMethod.LOCALISER.msg("060001", "ordinal", expr));
        }
        final EnumExpression enumExpr = (EnumExpression)expr;
        final JavaTypeMapping m = enumExpr.getJavaTypeMapping();
        if (m.getJavaTypeForDatastoreMapping(0).equals(ClassNameConstants.JAVA_LANG_STRING)) {
            return enumExpr.getDelegate();
        }
        throw new NucleusException("EnumExpression.toString is not supported when the enum is stored as a numeric");
    }
}
