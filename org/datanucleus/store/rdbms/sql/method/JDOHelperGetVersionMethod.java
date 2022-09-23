// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.api.ApiAdapter;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.sql.expression.TemporalExpression;
import org.datanucleus.store.rdbms.sql.expression.NumericExpression;
import org.datanucleus.metadata.VersionStrategy;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import org.datanucleus.store.rdbms.sql.expression.IllegalExpressionOperationException;
import org.datanucleus.store.rdbms.sql.expression.ObjectExpression;
import org.datanucleus.store.rdbms.sql.expression.ObjectLiteral;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.expression.NullLiteral;
import org.datanucleus.store.rdbms.sql.expression.SQLLiteral;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class JDOHelperGetVersionMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression ignore, final List args) {
        if (args == null || args.size() == 0) {
            throw new NucleusUserException("Cannot invoke JDOHelper.getVersion without an argument");
        }
        final SQLExpression expr = args.get(0);
        if (expr == null) {
            throw new NucleusUserException("Cannot invoke JDOHelper.getVersion on null expression");
        }
        if (expr instanceof SQLLiteral) {
            final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
            final ApiAdapter api = storeMgr.getApiAdapter();
            final Object obj = ((SQLLiteral)expr).getValue();
            if (obj == null || !api.isPersistable(obj)) {
                return new NullLiteral(this.stmt, null, null, null);
            }
            final Object ver = this.stmt.getRDBMSManager().getApiAdapter().getVersionForObject(obj);
            final JavaTypeMapping m = this.getMappingForClass(ver.getClass());
            return new ObjectLiteral(this.stmt, m, ver, null);
        }
        else {
            if (!ObjectExpression.class.isAssignableFrom(expr.getClass())) {
                throw new IllegalExpressionOperationException("JDOHelper.getVersion", expr);
            }
            if (!(((ObjectExpression)expr).getJavaTypeMapping() instanceof PersistableMapping)) {
                return expr;
            }
            JavaTypeMapping mapping = ((ObjectExpression)expr).getJavaTypeMapping();
            final DatastoreClass table = (DatastoreClass)expr.getSQLTable().getTable();
            if (table.getIdMapping() != mapping) {
                throw new NucleusUserException("Dont currently support JDOHelper.getVersion(ObjectExpression) for expr=" + expr + " on table=" + expr.getSQLTable());
            }
            mapping = table.getVersionMapping(true);
            if (mapping == null) {
                throw new NucleusUserException("Cannot use JDOHelper.getVersion on object that has no version information");
            }
            if (table.getVersionMetaData().getVersionStrategy() == VersionStrategy.VERSION_NUMBER) {
                return new NumericExpression(this.stmt, expr.getSQLTable(), mapping);
            }
            return new TemporalExpression(this.stmt, expr.getSQLTable(), mapping);
        }
    }
}
