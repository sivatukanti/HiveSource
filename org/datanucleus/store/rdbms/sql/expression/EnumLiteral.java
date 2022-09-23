// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import org.datanucleus.ClassNameConstants;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;

public class EnumLiteral extends EnumExpression implements SQLLiteral
{
    private final Enum value;
    
    public EnumLiteral(final SQLStatement stmt, final JavaTypeMapping mapping, final Object value, final String parameterName) {
        super(stmt, null, mapping);
        this.parameterName = parameterName;
        if (value == null) {
            this.value = null;
        }
        else {
            if (!(value instanceof Enum)) {
                throw new NucleusException("Cannot create " + this.getClass().getName() + " for value of type " + value.getClass().getName());
            }
            this.value = (Enum)value;
        }
        if (mapping.getJavaTypeForDatastoreMapping(0).equals(ClassNameConstants.JAVA_LANG_STRING)) {
            this.delegate = new StringLiteral(stmt, mapping, (this.value != null) ? this.value.name() : null, parameterName);
        }
        else {
            this.delegate = new IntegerLiteral(stmt, mapping, (this.value != null) ? Integer.valueOf(this.value.ordinal()) : null, parameterName);
        }
    }
    
    @Override
    public void setJavaTypeMapping(final JavaTypeMapping mapping) {
        super.setJavaTypeMapping(mapping);
        if (mapping.getJavaTypeForDatastoreMapping(0).equals(ClassNameConstants.JAVA_LANG_STRING)) {
            this.delegate = new StringLiteral(this.stmt, mapping, (this.value != null) ? this.value.name() : null, this.parameterName);
        }
        else {
            this.delegate = new IntegerLiteral(this.stmt, mapping, (this.value != null) ? Integer.valueOf(this.value.ordinal()) : null, this.parameterName);
        }
    }
    
    @Override
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public boolean isParameter() {
        return this.delegate.isParameter();
    }
    
    @Override
    public void setNotParameter() {
        ((SQLLiteral)this.delegate).setNotParameter();
    }
}
