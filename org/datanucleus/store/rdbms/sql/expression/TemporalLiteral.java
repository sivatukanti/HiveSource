// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import java.text.SimpleDateFormat;
import org.datanucleus.store.rdbms.mapping.datastore.CharRDBMSMapping;
import java.sql.Timestamp;
import java.sql.Time;
import java.util.List;
import org.datanucleus.exceptions.NucleusException;
import java.util.Calendar;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import java.util.Date;

public class TemporalLiteral extends TemporalExpression implements SQLLiteral
{
    private final Date value;
    
    public TemporalLiteral(final SQLStatement stmt, final JavaTypeMapping mapping, final Object value, final String parameterName) {
        super(stmt, null, mapping);
        this.parameterName = parameterName;
        if (value == null) {
            this.value = null;
        }
        else if (value instanceof Date) {
            this.value = (Date)value;
        }
        else {
            if (!(value instanceof Calendar)) {
                throw new NucleusException("Cannot create " + this.getClass().getName() + " for value of type " + value.getClass().getName());
            }
            this.value = ((Calendar)value).getTime();
        }
        if (parameterName != null) {
            this.st.appendParameter(parameterName, mapping, this.value);
        }
        else {
            this.setStatement();
        }
    }
    
    @Override
    public String toString() {
        return super.toString() + " = " + this.value.toString();
    }
    
    @Override
    public SQLExpression invoke(final String methodName, final List args) {
        if (this.parameterName == null) {
            if (methodName.equals("getDay")) {
                final Calendar cal = Calendar.getInstance();
                cal.setTime(this.value);
                final JavaTypeMapping m = this.stmt.getRDBMSManager().getMappingManager().getMapping(Integer.class);
                return new IntegerLiteral(this.stmt, m, cal.get(5), null);
            }
            if (methodName.equals("getMonth")) {
                final Calendar cal = Calendar.getInstance();
                cal.setTime(this.value);
                final JavaTypeMapping m = this.stmt.getRDBMSManager().getMappingManager().getMapping(Integer.class);
                return new IntegerLiteral(this.stmt, m, cal.get(2), null);
            }
            if (methodName.equals("getYear")) {
                final Calendar cal = Calendar.getInstance();
                cal.setTime(this.value);
                final JavaTypeMapping m = this.stmt.getRDBMSManager().getMappingManager().getMapping(Integer.class);
                return new IntegerLiteral(this.stmt, m, cal.get(1), null);
            }
            if (methodName.equals("getHour")) {
                final Calendar cal = Calendar.getInstance();
                cal.setTime(this.value);
                final JavaTypeMapping m = this.stmt.getRDBMSManager().getMappingManager().getMapping(Integer.class);
                return new IntegerLiteral(this.stmt, m, cal.get(11), null);
            }
            if (methodName.equals("getMinutes")) {
                final Calendar cal = Calendar.getInstance();
                cal.setTime(this.value);
                final JavaTypeMapping m = this.stmt.getRDBMSManager().getMappingManager().getMapping(Integer.class);
                return new IntegerLiteral(this.stmt, m, cal.get(12), null);
            }
            if (methodName.equals("getSeconds")) {
                final Calendar cal = Calendar.getInstance();
                cal.setTime(this.value);
                final JavaTypeMapping m = this.stmt.getRDBMSManager().getMappingManager().getMapping(Integer.class);
                return new IntegerLiteral(this.stmt, m, cal.get(13), null);
            }
        }
        return super.invoke(methodName, args);
    }
    
    @Override
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public void setNotParameter() {
        if (this.parameterName == null) {
            return;
        }
        this.parameterName = null;
        this.st.clearStatement();
        this.setStatement();
    }
    
    protected void setStatement() {
        String formatted;
        if (this.value instanceof Time || this.value instanceof java.sql.Date || this.value instanceof Timestamp) {
            formatted = this.value.toString();
        }
        else if (this.mapping.getDatastoreMapping(0) instanceof CharRDBMSMapping) {
            final SimpleDateFormat fmt = ((CharRDBMSMapping)this.mapping.getDatastoreMapping(0)).getJavaUtilDateFormat();
            formatted = fmt.format(this.value);
        }
        else {
            formatted = new Timestamp(this.value.getTime()).toString();
        }
        this.st.append('\'').append(formatted).append('\'');
    }
}
