// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.api.ApiAdapter;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.ReferenceIdMapping;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import org.datanucleus.store.rdbms.mapping.java.PersistableIdMapping;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import org.datanucleus.store.rdbms.sql.expression.IllegalExpressionOperationException;
import org.datanucleus.store.rdbms.sql.expression.ObjectExpression;
import org.datanucleus.store.rdbms.sql.expression.ObjectLiteral;
import org.datanucleus.store.rdbms.sql.expression.SQLLiteral;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.expression.NullLiteral;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class JDOHelperGetObjectIdMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression ignore, final List args) {
        if (args == null || args.size() == 0) {
            throw new NucleusUserException("Cannot invoke JDOHelper.getObjectId without an argument");
        }
        final SQLExpression expr = args.get(0);
        if (expr == null) {
            return new NullLiteral(this.stmt, null, null, null);
        }
        if (expr instanceof SQLLiteral) {
            final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
            final ApiAdapter api = storeMgr.getApiAdapter();
            final Object id = api.getIdForObject(((SQLLiteral)expr).getValue());
            if (id == null) {
                return new NullLiteral(this.stmt, null, null, null);
            }
            final JavaTypeMapping m = this.getMappingForClass(id.getClass());
            return new ObjectLiteral(this.stmt, m, id, null);
        }
        else {
            if (!ObjectExpression.class.isAssignableFrom(expr.getClass())) {
                throw new IllegalExpressionOperationException("JDOHelper.getObjectId", expr);
            }
            if (expr.getJavaTypeMapping() instanceof PersistableMapping) {
                final JavaTypeMapping mapping = new PersistableIdMapping((PersistableMapping)expr.getJavaTypeMapping());
                return new ObjectExpression(this.stmt, expr.getSQLTable(), mapping);
            }
            if (expr.getJavaTypeMapping() instanceof ReferenceMapping) {
                final JavaTypeMapping mapping = new ReferenceIdMapping((ReferenceMapping)expr.getJavaTypeMapping());
                return new ObjectExpression(this.stmt, expr.getSQLTable(), mapping);
            }
            return expr;
        }
    }
}
