// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.services.cache.ClassSize;
import java.sql.PreparedStatement;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.db.DatabaseContext;
import java.text.ParseException;
import java.text.DateFormat;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.services.i18n.LocaleFinder;
import java.sql.Time;
import org.apache.derby.iapi.error.StandardException;
import java.sql.SQLException;
import java.util.Date;
import java.sql.ResultSet;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import java.util.GregorianCalendar;
import java.sql.Timestamp;
import java.util.Calendar;

public final class SQLTime extends DataType implements DateTimeDataValue
{
    private int encodedTime;
    private int encodedTimeFraction;
    private static final int BASE_MEMORY_USAGE;
    private static final char IBM_EUR_SEPARATOR = '.';
    private static final char[] IBM_EUR_SEPARATOR_OR_END;
    static final char JIS_SEPARATOR = ':';
    private static final char[] US_OR_JIS_MINUTE_END;
    private static final char[] ANY_SEPARATOR;
    private static final String[] AM_PM;
    private static final char[] END_OF_STRING;
    
    public int estimateMemoryUsage() {
        return SQLTime.BASE_MEMORY_USAGE;
    }
    
    public String getString() {
        if (!this.isNull()) {
            return encodedTimeToString(this.encodedTime);
        }
        return null;
    }
    
    int getEncodedTime() {
        return this.encodedTime;
    }
    
    public Timestamp getTimestamp(Calendar calendar) {
        if (this.isNull()) {
            return null;
        }
        if (calendar == null) {
            calendar = new GregorianCalendar();
        }
        else {
            calendar.clear();
            calendar.setTimeInMillis(System.currentTimeMillis());
        }
        setTimeInCalendar(calendar, this.encodedTime);
        calendar.set(14, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }
    
    public Object getObject() {
        return this.getTime(null);
    }
    
    public int getLength() {
        return 8;
    }
    
    public String getTypeName() {
        return "TIME";
    }
    
    public int getTypeFormatId() {
        return 299;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeInt(this.encodedTime);
        objectOutput.writeInt(this.encodedTimeFraction);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException {
        this.encodedTime = objectInput.readInt();
        this.encodedTimeFraction = objectInput.readInt();
    }
    
    public DataValueDescriptor cloneValue(final boolean b) {
        return new SQLTime(this.encodedTime, this.encodedTimeFraction);
    }
    
    public DataValueDescriptor getNewNull() {
        return new SQLTime();
    }
    
    public void restoreToNull() {
        this.encodedTime = -1;
        this.encodedTimeFraction = 0;
    }
    
    public void setValueFromResultSet(final ResultSet set, final int n, final boolean b) throws SQLException, StandardException {
        this.restoreToNull();
        this.encodedTime = this.computeEncodedTime(set.getTime(n));
    }
    
    public int compare(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        if (this.typePrecedence() < dataValueDescriptor.typePrecedence()) {
            return -dataValueDescriptor.compare(this);
        }
        final boolean null = this.isNull();
        final boolean null2 = dataValueDescriptor.isNull();
        if (!null && !null2) {
            int n;
            if (dataValueDescriptor instanceof SQLTime) {
                n = ((SQLTime)dataValueDescriptor).encodedTime;
            }
            else {
                n = this.computeEncodedTime(dataValueDescriptor.getTime(null));
            }
            int n2;
            if (this.encodedTime < n) {
                n2 = -1;
            }
            else if (this.encodedTime > n) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            return n2;
        }
        if (!null) {
            return -1;
        }
        if (!null2) {
            return 1;
        }
        return 0;
    }
    
    public boolean compare(final int n, final DataValueDescriptor dataValueDescriptor, final boolean b, final boolean b2) throws StandardException {
        if (!b && (this.isNull() || dataValueDescriptor.isNull())) {
            return b2;
        }
        return super.compare(n, dataValueDescriptor, b, b2);
    }
    
    public SQLTime() {
        this.encodedTime = -1;
    }
    
    public SQLTime(final Time time) throws StandardException {
        this.parseTime(time);
    }
    
    private void parseTime(final Date date) throws StandardException {
        this.encodedTime = this.computeEncodedTime(date);
    }
    
    private SQLTime(final int encodedTime, final int encodedTimeFraction) {
        this.encodedTime = encodedTime;
        this.encodedTimeFraction = encodedTimeFraction;
    }
    
    public SQLTime(final String s, final boolean b, final LocaleFinder localeFinder) throws StandardException {
        this.parseTime(s, b, localeFinder, null);
    }
    
    public SQLTime(final String s, final boolean b, final LocaleFinder localeFinder, final Calendar calendar) throws StandardException {
        this.parseTime(s, b, localeFinder, calendar);
    }
    
    private void parseTime(String trimTrailing, final boolean b, final LocaleFinder localeFinder, final Calendar calendar) throws StandardException {
        boolean b2 = true;
        final DateTimeParser dateTimeParser = new DateTimeParser(trimTrailing);
        StandardException ex = null;
        int int1 = 0;
        int n = 0;
        int n2 = 0;
        int n3 = -1;
        try {
            if (dateTimeParser.nextSeparator() == '-') {
                this.encodedTime = SQLTimestamp.parseDateOrTimestamp(dateTimeParser, true)[1];
                return;
            }
            int1 = dateTimeParser.parseInt(2, true, SQLTime.ANY_SEPARATOR, false);
            switch (dateTimeParser.getCurrentSeparator()) {
                case '.': {
                    if (b) {
                        b2 = false;
                        break;
                    }
                    n = dateTimeParser.parseInt(2, false, SQLTime.IBM_EUR_SEPARATOR_OR_END, false);
                    if (dateTimeParser.getCurrentSeparator() == '.') {
                        n2 = dateTimeParser.parseInt(2, false, SQLTime.END_OF_STRING, false);
                        break;
                    }
                    break;
                }
                case ':': {
                    n = dateTimeParser.parseInt(2, false, SQLTime.US_OR_JIS_MINUTE_END, false);
                    switch (dateTimeParser.getCurrentSeparator()) {
                        case ' ': {
                            if (b) {
                                b2 = false;
                                break;
                            }
                            n3 = dateTimeParser.parseChoice(SQLTime.AM_PM);
                            dateTimeParser.checkEnd();
                            break;
                        }
                        case ':': {
                            n2 = dateTimeParser.parseInt(2, false, SQLTime.END_OF_STRING, false);
                            break;
                        }
                    }
                    break;
                }
                case ' ': {
                    if (b) {
                        b2 = false;
                        break;
                    }
                    n3 = dateTimeParser.parseChoice(SQLTime.AM_PM);
                    break;
                }
                default: {
                    b2 = false;
                    break;
                }
            }
        }
        catch (StandardException ex2) {
            b2 = false;
            ex = ex2;
        }
        if (b2) {
            if (n3 == 0) {
                if (int1 == 12) {
                    if (n == 0 && n2 == 0) {
                        int1 = 24;
                    }
                    else {
                        int1 = 0;
                    }
                }
                else if (int1 > 12) {
                    throw StandardException.newException("22007.S.180");
                }
            }
            else if (n3 == 1) {
                if (int1 < 12) {
                    int1 += 12;
                }
                else if (int1 > 12) {
                    throw StandardException.newException("22007.S.180");
                }
            }
            dateTimeParser.checkEnd();
            this.encodedTime = computeEncodedTime(int1, n, n2);
        }
        else {
            trimTrailing = StringUtil.trimTrailing(trimTrailing);
            DateFormat dateFormat;
            if (localeFinder == null) {
                dateFormat = DateFormat.getTimeInstance();
            }
            else if (calendar == null) {
                dateFormat = localeFinder.getTimeFormat();
            }
            else {
                dateFormat = (DateFormat)localeFinder.getTimeFormat().clone();
            }
            if (calendar != null) {
                dateFormat.setCalendar(calendar);
            }
            try {
                this.encodedTime = computeEncodedTime(dateFormat.parse(trimTrailing), calendar);
            }
            catch (ParseException ex3) {
                try {
                    this.encodedTime = SQLTimestamp.parseLocalTimestamp(trimTrailing, localeFinder, calendar)[1];
                }
                catch (ParseException ex4) {
                    if (ex != null) {
                        throw ex;
                    }
                    throw StandardException.newException("22007.S.181");
                }
            }
        }
    }
    
    void setObject(final Object o) throws StandardException {
        this.setValue((Time)o);
    }
    
    protected void setFrom(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        if (dataValueDescriptor instanceof SQLTime) {
            this.restoreToNull();
            final SQLTime sqlTime = (SQLTime)dataValueDescriptor;
            this.encodedTime = sqlTime.encodedTime;
            this.encodedTimeFraction = sqlTime.encodedTimeFraction;
        }
        else {
            final GregorianCalendar gregorianCalendar = new GregorianCalendar();
            this.setValue(dataValueDescriptor.getTime(gregorianCalendar), gregorianCalendar);
        }
    }
    
    public void setValue(final Time time, final Calendar calendar) throws StandardException {
        this.restoreToNull();
        this.encodedTime = computeEncodedTime(time, calendar);
    }
    
    public void setValue(final Timestamp timestamp, final Calendar calendar) throws StandardException {
        this.restoreToNull();
        this.encodedTime = computeEncodedTime(timestamp, calendar);
    }
    
    public void setValue(final String s) throws StandardException {
        this.restoreToNull();
        if (s != null) {
            final DatabaseContext databaseContext = (DatabaseContext)ContextService.getContext("Database");
            this.parseTime(s, false, (databaseContext == null) ? null : databaseContext.getDatabase(), null);
        }
    }
    
    NumberDataValue nullValueInt() {
        return new SQLInteger();
    }
    
    public NumberDataValue getYear(final NumberDataValue numberDataValue) throws StandardException {
        throw StandardException.newException("42X25", "getYear", "Time");
    }
    
    public NumberDataValue getMonth(final NumberDataValue numberDataValue) throws StandardException {
        throw StandardException.newException("42X25", "getMonth", "Time");
    }
    
    public NumberDataValue getDate(final NumberDataValue numberDataValue) throws StandardException {
        throw StandardException.newException("42X25", "getDate", "Time");
    }
    
    public NumberDataValue getHours(final NumberDataValue numberDataValue) throws StandardException {
        if (this.isNull()) {
            return this.nullValueInt();
        }
        return SQLDate.setSource(getHour(this.encodedTime), numberDataValue);
    }
    
    public NumberDataValue getMinutes(final NumberDataValue numberDataValue) throws StandardException {
        if (this.isNull()) {
            return this.nullValueInt();
        }
        return SQLDate.setSource(getMinute(this.encodedTime), numberDataValue);
    }
    
    public NumberDataValue getSeconds(final NumberDataValue numberDataValue) throws StandardException {
        if (this.isNull()) {
            return this.nullValueInt();
        }
        return SQLDate.setSource(getSecond(this.encodedTime), numberDataValue);
    }
    
    public String toString() {
        if (this.isNull()) {
            return "NULL";
        }
        return this.getTime(null).toString();
    }
    
    public int hashCode() {
        if (this.isNull()) {
            return 0;
        }
        return this.encodedTime + this.encodedTimeFraction + 1;
    }
    
    public int typePrecedence() {
        return 120;
    }
    
    public final boolean isNull() {
        return this.encodedTime == -1;
    }
    
    public Time getTime(final Calendar calendar) {
        if (this.isNull()) {
            return null;
        }
        return getTime(calendar, this.encodedTime, 0);
    }
    
    static void setTimeInCalendar(final Calendar calendar, final int n) {
        calendar.set(11, getHour(n));
        calendar.set(12, getMinute(n));
        calendar.set(13, getSecond(n));
    }
    
    static Time getTime(Calendar calendar, final int n, final int n2) {
        if (calendar == null) {
            calendar = new GregorianCalendar();
        }
        calendar.clear();
        calendar.set(1970, 0, 1);
        setTimeInCalendar(calendar, n);
        calendar.set(14, n2 / 1000000);
        return new Time(calendar.getTimeInMillis());
    }
    
    protected static int getHour(final int n) {
        return n >>> 16 & 0xFF;
    }
    
    protected static int getMinute(final int n) {
        return n >>> 8 & 0xFF;
    }
    
    protected static int getSecond(final int n) {
        return n & 0xFF;
    }
    
    static int computeEncodedTime(final Calendar calendar) throws StandardException {
        return computeEncodedTime(calendar.get(11), calendar.get(12), calendar.get(13));
    }
    
    static int computeEncodedTime(final int n, final int n2, final int n3) throws StandardException {
        if (n == 24) {
            if (n2 != 0 || n3 != 0) {
                throw StandardException.newException("22007.S.180");
            }
        }
        else if (n < 0 || n > 23 || n2 < 0 || n2 > 59 || n3 < 0 || n3 > 59) {
            throw StandardException.newException("22007.S.180");
        }
        return (n << 16) + (n2 << 8) + n3;
    }
    
    static void timeToString(final int i, final int j, final int k, final StringBuffer sb) {
        final String string = Integer.toString(i);
        final String string2 = Integer.toString(j);
        final String string3 = Integer.toString(k);
        if (string.length() == 1) {
            sb.append("0");
        }
        sb.append(string);
        sb.append(':');
        if (string2.length() == 1) {
            sb.append("0");
        }
        sb.append(string2);
        sb.append(':');
        if (string3.length() == 1) {
            sb.append("0");
        }
        sb.append(string3);
    }
    
    protected static String encodedTimeToString(final int n) {
        final StringBuffer sb = new StringBuffer();
        timeToString(getHour(n), getMinute(n), getSecond(n), sb);
        return sb.toString();
    }
    
    private int computeEncodedTime(final Date date) throws StandardException {
        return computeEncodedTime(date, null);
    }
    
    static int computeEncodedTime(final Date time, Calendar calendar) throws StandardException {
        if (time == null) {
            return -1;
        }
        if (calendar == null) {
            calendar = new GregorianCalendar();
        }
        calendar.setTime(time);
        return computeEncodedTime(calendar);
    }
    
    public void setInto(final PreparedStatement preparedStatement, final int n) throws SQLException, StandardException {
        preparedStatement.setTime(n, this.getTime(null));
    }
    
    public DateTimeDataValue timestampAdd(final int n, final NumberDataValue numberDataValue, final java.sql.Date date, final DateTimeDataValue dateTimeDataValue) throws StandardException {
        return this.toTimestamp(date).timestampAdd(n, numberDataValue, date, dateTimeDataValue);
    }
    
    private SQLTimestamp toTimestamp(final java.sql.Date date) throws StandardException {
        return new SQLTimestamp(SQLDate.computeEncodedDate(date, null), this.getEncodedTime(), 0);
    }
    
    public NumberDataValue timestampDiff(final int n, final DateTimeDataValue dateTimeDataValue, final java.sql.Date date, final NumberDataValue numberDataValue) throws StandardException {
        return this.toTimestamp(date).timestampDiff(n, dateTimeDataValue, date, numberDataValue);
    }
    
    static {
        BASE_MEMORY_USAGE = ClassSize.estimateBaseFromCatalog(SQLTime.class);
        IBM_EUR_SEPARATOR_OR_END = new char[] { '.', '\0' };
        US_OR_JIS_MINUTE_END = new char[] { ':', ' ', '\0' };
        ANY_SEPARATOR = new char[] { '.', ':', ' ' };
        AM_PM = new String[] { "AM", "PM" };
        END_OF_STRING = new char[] { '\0' };
    }
}
