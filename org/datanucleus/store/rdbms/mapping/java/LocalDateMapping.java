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
import javax.time.calendar.LocalDate;

public class LocalDateMapping extends TemporalMapping
{
    @Override
    public Class getJavaType() {
        return LocalDate.class;
    }
    
    @Override
    public String getJavaTypeForDatastoreMapping(final int index) {
        if (this.datastoreMappings == null || this.datastoreMappings.length == 0) {
            final ColumnMetaData[] colmds = JavaTypeMapping.getColumnMetaDataForMember(this.mmd, this.roleForMember);
            boolean useString = false;
            if (colmds != null && colmds.length > 0 && colmds[0].getJdbcType() != null && MetaDataUtils.isJdbcTypeString(colmds[0].getJdbcType())) {
                useString = true;
            }
            return useString ? ClassNameConstants.JAVA_LANG_STRING : ClassNameConstants.JAVA_SQL_DATE;
        }
        if (this.datastoreMappings != null && this.datastoreMappings.length > 0 && this.datastoreMappings[0].isStringBased()) {
            return ClassNameConstants.JAVA_LANG_STRING;
        }
        return ClassNameConstants.JAVA_SQL_DATE;
    }
    
    @Override
    protected int getDefaultLengthAsString() {
        return 10;
    }
    
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final Object value) {
        if (value == null) {
            this.getDatastoreMapping(0).setObject(ps, exprIndex[0], null);
        }
        else if (this.datastoreMappings != null && this.datastoreMappings.length > 0 && this.datastoreMappings[0].isStringBased()) {
            final TypeConverter conv = ec.getNucleusContext().getTypeManager().getTypeConverterForType(LocalDate.class, String.class);
            if (conv == null) {
                throw new NucleusUserException("This type doesn't support persistence as a String");
            }
            final Object obj = conv.toDatastoreType(value);
            this.getDatastoreMapping(0).setObject(ps, exprIndex[0], obj);
        }
        else {
            final LocalDate localDate = (LocalDate)value;
            final Calendar cal = Calendar.getInstance();
            cal.set(localDate.getYear(), localDate.getMonthOfYear().ordinal(), localDate.getDayOfMonth());
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
            final TypeConverter conv = ec.getNucleusContext().getTypeManager().getTypeConverterForType(LocalDate.class, String.class);
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
                final LocalDate localDate = LocalDate.of(cal.get(1), cal.get(2) + 1, cal.get(5));
                return localDate;
            }
            if (datastoreValue instanceof Timestamp) {
                final Timestamp ts = (Timestamp)datastoreValue;
                final LocalDate localDate2 = LocalDate.of(ts.getYear(), ts.getMonth() + 1, ts.getDay());
                return localDate2;
            }
            return null;
        }
    }
}
