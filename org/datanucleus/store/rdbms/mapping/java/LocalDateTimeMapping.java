// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import java.sql.Timestamp;
import java.sql.Date;
import java.sql.ResultSet;
import org.datanucleus.store.types.converters.TypeConverter;
import java.util.Calendar;
import org.datanucleus.exceptions.NucleusUserException;
import java.sql.PreparedStatement;
import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.ClassNameConstants;
import org.datanucleus.metadata.MetaDataUtils;
import javax.time.calendar.LocalDateTime;

public class LocalDateTimeMapping extends TemporalMapping
{
    @Override
    public Class getJavaType() {
        return LocalDateTime.class;
    }
    
    @Override
    public String getJavaTypeForDatastoreMapping(final int index) {
        if (this.datastoreMappings == null || this.datastoreMappings.length == 0) {
            final ColumnMetaData[] colmds = JavaTypeMapping.getColumnMetaDataForMember(this.mmd, this.roleForMember);
            boolean useString = false;
            if (colmds != null && colmds.length > 0 && colmds[0].getJdbcType() != null && MetaDataUtils.isJdbcTypeString(colmds[0].getJdbcType())) {
                useString = true;
            }
            return useString ? ClassNameConstants.JAVA_LANG_STRING : ClassNameConstants.JAVA_SQL_TIMESTAMP;
        }
        if (this.datastoreMappings != null && this.datastoreMappings.length > 0 && this.datastoreMappings[0].isStringBased()) {
            return ClassNameConstants.JAVA_LANG_STRING;
        }
        return ClassNameConstants.JAVA_SQL_TIMESTAMP;
    }
    
    @Override
    protected int getDefaultLengthAsString() {
        return 19;
    }
    
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final Object value) {
        if (value == null) {
            this.getDatastoreMapping(0).setObject(ps, exprIndex[0], null);
        }
        else if (this.datastoreMappings != null && this.datastoreMappings.length > 0 && this.datastoreMappings[0].isStringBased()) {
            final TypeConverter conv = ec.getNucleusContext().getTypeManager().getTypeConverterForType(LocalDateTime.class, String.class);
            if (conv == null) {
                throw new NucleusUserException("This type doesn't support persistence as a String");
            }
            final Object obj = conv.toDatastoreType(value);
            this.getDatastoreMapping(0).setObject(ps, exprIndex[0], obj);
        }
        else {
            final LocalDateTime localDate = (LocalDateTime)value;
            final Calendar cal = Calendar.getInstance();
            cal.set(localDate.getYear(), localDate.getMonthOfYear().ordinal(), localDate.getDayOfMonth(), localDate.getHourOfDay(), localDate.getMinuteOfHour(), localDate.getSecondOfMinute());
            this.getDatastoreMapping(0).setObject(ps, exprIndex[0], cal);
        }
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        if (exprIndex == null) {
            return null;
        }
        final Object datastoreValue = this.getDatastoreMapping(0).getObject(resultSet, exprIndex[0]);
        if (datastoreValue == null) {
            return null;
        }
        if (datastoreValue instanceof String) {
            final TypeConverter conv = ec.getNucleusContext().getTypeManager().getTypeConverterForType(LocalDateTime.class, String.class);
            if (conv != null) {
                return conv.toMemberType(datastoreValue);
            }
            throw new NucleusUserException("This type doesn't support persistence as a String");
        }
        else {
            if (datastoreValue instanceof Date) {
                final Date date = (Date)datastoreValue;
                final Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                final LocalDateTime localDateTime = LocalDateTime.of(cal.get(1), cal.get(2) + 1, cal.get(5), cal.get(11), cal.get(12), cal.get(13), cal.get(14) * 1000000);
                return localDateTime;
            }
            if (datastoreValue instanceof Timestamp) {
                final Timestamp ts = (Timestamp)datastoreValue;
                final Calendar cal = Calendar.getInstance();
                cal.setTime(ts);
                final LocalDateTime localDateTime = LocalDateTime.of(cal.get(1), cal.get(2) + 1, cal.get(5), cal.get(11), cal.get(12), cal.get(13), cal.get(14) * 1000000);
                return localDateTime;
            }
            return null;
        }
    }
}
