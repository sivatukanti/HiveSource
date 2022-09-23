// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.expression.AggregateNumericExpression;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class SumFunction extends SimpleNumericAggregateMethod
{
    @Override
    protected String getFunctionName() {
        return "SUM";
    }
    
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (expr != null) {
            throw new NucleusException(SumFunction.LOCALISER.msg("060002", this.getFunctionName(), expr));
        }
        if (args == null || args.size() != 1) {
            throw new NucleusException(this.getFunctionName() + " is only supported with a single argument");
        }
        JavaTypeMapping m = null;
        if (args.get(0) instanceof SQLExpression) {
            final SQLExpression argExpr = args.get(0);
            final Class cls = argExpr.getJavaTypeMapping().getJavaType();
            if (cls == Integer.class || cls == Short.class || cls == Long.class) {
                m = this.getMappingForClass(Long.class);
            }
            else {
                if (!Number.class.isAssignableFrom(cls)) {
                    throw new NucleusUserException("Cannot perform static SUM with arg of type " + cls.getName());
                }
                m = this.getMappingForClass(argExpr.getJavaTypeMapping().getJavaType());
            }
        }
        else {
            m = this.getMappingForClass(this.getClassForMapping());
        }
        return new AggregateNumericExpression(this.stmt, m, this.getFunctionName(), args);
    }
    
    @Override
    protected Class getClassForMapping() {
        return Double.TYPE;
    }
}
