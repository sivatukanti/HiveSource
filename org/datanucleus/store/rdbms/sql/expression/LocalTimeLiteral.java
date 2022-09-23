// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import java.util.Calendar;
import org.datanucleus.store.types.converters.LocalTimeStringConverter;
import org.datanucleus.ClassNameConstants;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import javax.time.calendar.LocalTime;

public class LocalTimeLiteral extends JavaxTimeLiteral
{
    private final LocalTime value;
    
    public LocalTimeLiteral(final SQLStatement stmt, final JavaTypeMapping mapping, final Object value, final String parameterName) {
        super(stmt, mapping, parameterName);
        if (value == null) {
            this.value = null;
        }
        else {
            if (!(value instanceof LocalTime)) {
                throw new NucleusException("Cannot create " + this.getClass().getName() + " for value of type " + value.getClass().getName());
            }
            this.value = (LocalTime)value;
        }
        if (mapping.getJavaTypeForDatastoreMapping(0).equals(ClassNameConstants.JAVA_LANG_STRING)) {
            final String str = new LocalTimeStringConverter().toDatastoreType((LocalTime)value);
            this.delegate = new StringLiteral(stmt, mapping, (this.value != null) ? str : null, parameterName);
        }
        else if (this.value == null) {
            this.delegate = new TemporalLiteral(stmt, mapping, null, parameterName);
        }
        else {
            final LocalTime localTime = (LocalTime)value;
            final Calendar cal = Calendar.getInstance();
            cal.set(11, localTime.getHourOfDay());
            cal.set(12, localTime.getMinuteOfHour());
            cal.set(13, localTime.getSecondOfMinute());
            this.delegate = new TemporalLiteral(stmt, mapping, cal.getTime(), parameterName);
        }
    }
    
    @Override
    public Object getValue() {
        return this.value;
    }
}
