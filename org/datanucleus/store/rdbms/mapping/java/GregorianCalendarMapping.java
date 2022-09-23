// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import java.util.Date;
import java.util.TimeZone;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import org.datanucleus.ExecutionContext;
import java.util.Calendar;
import org.datanucleus.NucleusContext;
import java.util.GregorianCalendar;
import org.datanucleus.ClassNameConstants;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.AbstractMemberMetaData;

public class GregorianCalendarMapping extends SingleFieldMultiMapping
{
    boolean singleColumn;
    
    public GregorianCalendarMapping() {
        this.singleColumn = false;
    }
    
    @Override
    public void initialize(final AbstractMemberMetaData fmd, final Table table, final ClassLoaderResolver clr) {
        super.initialize(fmd, table, clr);
        this.addColumns();
    }
    
    @Override
    public void initialize(final RDBMSStoreManager storeMgr, final String type) {
        super.initialize(storeMgr, type);
        this.addColumns();
    }
    
    protected void addColumns() {
        if (this.mmd != null && this.mmd.hasExtension("calendar-one-column") && this.mmd.getValueForExtension("calendar-one-column").equals("true")) {
            this.singleColumn = true;
        }
        if (this.singleColumn) {
            this.addColumns(ClassNameConstants.JAVA_SQL_TIMESTAMP);
        }
        else {
            this.addColumns(ClassNameConstants.LONG);
            this.addColumns(ClassNameConstants.JAVA_LANG_STRING);
        }
    }
    
    @Override
    public Class getJavaType() {
        return GregorianCalendar.class;
    }
    
    @Override
    public String getJavaTypeForDatastoreMapping(final int index) {
        if (this.singleColumn) {
            return ClassNameConstants.JAVA_SQL_TIMESTAMP;
        }
        if (index == 0) {
            return ClassNameConstants.LONG;
        }
        if (index == 1) {
            return ClassNameConstants.JAVA_LANG_STRING;
        }
        return null;
    }
    
    @Override
    public Object getValueForDatastoreMapping(final NucleusContext nucleusCtx, final int index, final Object value) {
        if (this.singleColumn) {
            return value;
        }
        if (index == 0) {
            return ((Calendar)value).getTime().getTime();
        }
        if (index == 1) {
            return ((Calendar)value).getTimeZone().getID();
        }
        throw new IndexOutOfBoundsException();
    }
    
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final Object value) {
        final GregorianCalendar cal = (GregorianCalendar)value;
        if (this.singleColumn) {
            Timestamp ts = null;
            if (cal != null) {
                ts = new Timestamp(cal.getTimeInMillis());
            }
            this.getDatastoreMapping(0).setObject(ps, exprIndex[0], ts);
        }
        else if (cal == null) {
            this.getDatastoreMapping(0).setObject(ps, exprIndex[0], null);
            this.getDatastoreMapping(1).setObject(ps, exprIndex[1], null);
        }
        else {
            this.getDatastoreMapping(0).setLong(ps, exprIndex[0], cal.getTime().getTime());
            this.getDatastoreMapping(1).setString(ps, exprIndex[1], cal.getTimeZone().getID());
        }
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        try {
            if (this.getDatastoreMapping(0).getObject(resultSet, exprIndex[0]) == null) {
                return null;
            }
        }
        catch (Exception ex) {}
        if (this.singleColumn) {
            final Timestamp ts = (Timestamp)this.getDatastoreMapping(0).getObject(resultSet, exprIndex[0]);
            final GregorianCalendar cal = new GregorianCalendar();
            cal.setTimeInMillis(ts.getTime());
            final String timezoneID = ec.getNucleusContext().getPersistenceConfiguration().getStringProperty("datanucleus.ServerTimeZoneID");
            if (timezoneID != null) {
                cal.setTimeZone(TimeZone.getTimeZone(timezoneID));
            }
            return cal;
        }
        final long millisecs = this.getDatastoreMapping(0).getLong(resultSet, exprIndex[0]);
        final GregorianCalendar cal2 = new GregorianCalendar();
        cal2.setTime(new Date(millisecs));
        final String timezoneId = this.getDatastoreMapping(1).getString(resultSet, exprIndex[1]);
        if (timezoneId != null) {
            cal2.setTimeZone(TimeZone.getTimeZone(timezoneId));
        }
        return cal2;
    }
}
