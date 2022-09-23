// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.api.ApiAdapter;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.sql.expression.CollectionExpression;
import org.datanucleus.store.rdbms.sql.expression.BooleanLiteral;
import java.util.Collection;
import org.datanucleus.store.rdbms.sql.expression.CollectionLiteral;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class CollectionIsEmptyMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args != null && args.size() > 0) {
            throw new NucleusException(CollectionIsEmptyMethod.LOCALISER.msg("060015", "isEmpty", "CollectionExpression"));
        }
        if (expr instanceof CollectionLiteral) {
            final Collection coll = (Collection)((CollectionLiteral)expr).getValue();
            final boolean isEmpty = coll == null || coll.size() == 0;
            final JavaTypeMapping m = this.exprFactory.getMappingForType(Boolean.TYPE, false);
            return new BooleanLiteral(this.stmt, m, isEmpty ? Boolean.TRUE : Boolean.FALSE);
        }
        final AbstractMemberMetaData mmd = ((CollectionExpression)expr).getJavaTypeMapping().getMemberMetaData();
        if (mmd.isSerialized()) {
            throw new NucleusUserException("Cannot perform Collection.isEmpty when the collection is being serialised");
        }
        final ApiAdapter api = this.stmt.getRDBMSManager().getApiAdapter();
        final Class elementType = this.clr.classForName(mmd.getCollection().getElementType());
        if (!api.isPersistable(elementType) && mmd.getJoinMetaData() == null) {
            throw new NucleusUserException("Cannot perform Collection.isEmpty when the collection<Non-Persistable> is not in a join table");
        }
        final SQLExpression sizeExpr = this.exprFactory.invokeMethod(this.stmt, Collection.class.getName(), "size", expr, args);
        final JavaTypeMapping mapping = this.exprFactory.getMappingForType(Integer.class, true);
        final SQLExpression zeroExpr = this.exprFactory.newLiteral(this.stmt, mapping, 0);
        return sizeExpr.eq(zeroExpr);
    }
}
