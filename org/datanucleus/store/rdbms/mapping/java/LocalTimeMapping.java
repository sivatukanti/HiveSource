// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import java.util.Date;
import java.sql.Time;
import java.sql.ResultSet;
import org.datanucleus.store.types.converters.TypeConverter;
import java.util.Calendar;
import org.datanucleus.exceptions.NucleusUserException;
import java.sql.PreparedStatement;
import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.ClassNameConstants;
import org.datanucleus.metadata.MetaDataUtils;
import javax.time.calendar.LocalTime;

public class LocalTimeMapping extends TemporalMapping
{
    @Override
    public Class getJavaType() {
        return LocalTime.class;
    }
    
    @Override
    public String getJavaTypeForDatastoreMapping(final int index) {
        if (this.datastoreMappings == null || this.datastoreMappings.length == 0) {
            final ColumnMetaData[] colmds = JavaTypeMapping.getColumnMetaDataForMember(this.mmd, this.roleForMember);
            boolean useString = false;
            if (colmds != null && colmds.length > 0 && colmds[0].getJdbcType() != null && MetaDataUtils.isJdbcTypeString(colmds[0].getJdbcType())) {
                useString = true;
            }
            return useString ? ClassNameConstants.JAVA_LANG_STRING : ClassNameConstants.JAVA_SQL_TIME;
        }
        if (this.datastoreMappings[0].isStringBased()) {
            return ClassNameConstants.JAVA_LANG_STRING;
        }
        return ClassNameConstants.JAVA_SQL_TIME;
    }
    
    @Override
    protected int getDefaultLengthAsString() {
        return 12;
    }
    
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final Object value) {
        if (value == null) {
            this.getDatastoreMapping(0).setObject(ps, exprIndex[0], null);
        }
        else if (this.datastoreMappings != null && this.datastoreMappings.length > 0 && this.datastoreMappings[0].isStringBased()) {
            final TypeConverter conv = ec.getNucleusContext().getTypeManager().getTypeConverterForType(LocalTime.class, String.class);
            if (conv == null) {
                throw new NucleusUserException("This type doesn't support persistence as a String");
            }
            final Object obj = conv.toDatastoreType(value);
            this.getDatastoreMapping(0).setObject(ps, exprIndex[0], obj);
        }
        else {
            final LocalTime val = (LocalTime)value;
            final Calendar cal = Calendar.getInstance();
            cal.set(0, 0, 0, val.getHourOfDay(), val.getMinuteOfHour(), val.getSecondOfMinute());
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
            final TypeConverter conv = ec.getNucleusContext().getTypeManager().getTypeConverterForType(LocalTime.class, String.class);
            if (conv != null) {
                return conv.toMemberType(datastoreValue);
            }
            throw new NucleusUserException("This type doesn't support persistence as a String");
        }
        else {
            if (datastoreValue instanceof Time) {
                final Time time = (Time)datastoreValue;
                final Calendar cal = Calendar.getInstance();
                cal.setTime(time);
                final LocalTime localTime = LocalTime.of(cal.get(11), cal.get(12), cal.get(13), cal.get(14) * 1000000);
                return localTime;
            }
            return null;
        }
    }
}
