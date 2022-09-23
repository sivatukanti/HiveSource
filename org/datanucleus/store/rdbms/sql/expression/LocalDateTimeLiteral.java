// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import java.util.Date;
import javax.time.calendar.LocalDate;
import org.datanucleus.store.types.converters.LocalDateStringConverter;
import org.datanucleus.ClassNameConstants;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import javax.time.calendar.LocalDateTime;

public class LocalDateTimeLiteral extends JavaxTimeLiteral
{
    private final LocalDateTime value;
    
    public LocalDateTimeLiteral(final SQLStatement stmt, final JavaTypeMapping mapping, final Object value, final String parameterName) {
        super(stmt, mapping, parameterName);
        if (value == null) {
            this.value = null;
        }
        else {
            if (!(value instanceof LocalDateTime)) {
                throw new NucleusException("Cannot create " + this.getClass().getName() + " for value of type " + value.getClass().getName());
            }
            this.value = (LocalDateTime)value;
        }
        if (mapping.getJavaTypeForDatastoreMapping(0).equals(ClassNameConstants.JAVA_LANG_STRING)) {
            final String str = new LocalDateStringConverter().toDatastoreType((LocalDate)value);
            this.delegate = new StringLiteral(stmt, mapping, (this.value != null) ? str : null, parameterName);
        }
        else if (this.value == null) {
            this.delegate = new TemporalLiteral(stmt, mapping, null, parameterName);
        }
        else {
            final LocalDateTime localDate = (LocalDateTime)value;
            final Date date = new Date();
            date.setYear(localDate.getYear());
            date.setMonth(localDate.getMonthOfYear().ordinal());
            date.setDate(localDate.getDayOfMonth());
            date.setHours(localDate.getHourOfDay());
            date.setMinutes(localDate.getMinuteOfHour());
            date.setSeconds(localDate.getSecondOfMinute());
            this.delegate = new TemporalLiteral(stmt, mapping, date, parameterName);
        }
    }
    
    @Override
    public Object getValue() {
        return this.value;
    }
}
