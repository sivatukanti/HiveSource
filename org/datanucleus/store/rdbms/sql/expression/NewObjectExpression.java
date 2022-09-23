// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import java.util.Collection;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import java.util.List;

public class NewObjectExpression extends SQLExpression
{
    Class newClass;
    List<SQLExpression> ctrArgExprs;
    
    public NewObjectExpression(final SQLStatement stmt, final Class cls, final List<SQLExpression> args) {
        super(stmt, null, null);
        this.newClass = null;
        this.ctrArgExprs = null;
        this.newClass = cls;
        if (args != null) {
            (this.ctrArgExprs = new ArrayList<SQLExpression>()).addAll(args);
        }
    }
    
    public Class getNewClass() {
        return this.newClass;
    }
    
    public List<SQLExpression> getConstructorArgExpressions() {
        return this.ctrArgExprs;
    }
}
