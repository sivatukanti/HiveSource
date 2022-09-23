// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import org.datanucleus.ClassNameConstants;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.SQLStatement;

public class EnumExpression extends DelegatedExpression
{
    public EnumExpression(final SQLStatement stmt, final SQLTable table, final JavaTypeMapping mapping) {
        super(stmt, table, mapping);
        if (mapping.getJavaTypeForDatastoreMapping(0).equals(ClassNameConstants.JAVA_LANG_STRING)) {
            this.delegate = new StringExpression(stmt, table, mapping);
        }
        else {
            this.delegate = new NumericExpression(stmt, table, mapping);
        }
    }
    
    @Override
    public void setJavaTypeMapping(final JavaTypeMapping mapping) {
        super.setJavaTypeMapping(mapping);
        if (mapping.getJavaTypeForDatastoreMapping(0).equals(ClassNameConstants.JAVA_LANG_STRING)) {
            this.delegate = new StringExpression(this.stmt, this.table, mapping);
        }
        else {
            this.delegate = new NumericExpression(this.stmt, this.table, mapping);
        }
    }
}
