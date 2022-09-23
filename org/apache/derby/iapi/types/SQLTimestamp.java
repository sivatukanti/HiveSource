// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.services.cache.ClassSize;
import org.apache.derby.iapi.util.ReuseFactory;
import java.sql.PreparedStatement;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.db.DatabaseContext;
import java.text.DateFormat;
import java.text.ParseException;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.services.i18n.LocaleFinder;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import java.sql.Time;
import org.apache.derby.iapi.error.StandardException;
import java.util.GregorianCalendar;
import java.sql.Date;
import java.util.Calendar;

public final class SQLTimestamp extends DataType implements DateTimeDataValue
{
    static final int MAX_FRACTION_DIGITS = 9;
    static final int FRACTION_TO_NANO = 1;
    static final int ONE_BILLION = 1000000000;
    private int encodedDate;
    private int encodedTime;
    private int nanos;
    private static final int BASE_MEMORY_USAGE;
    static final char DATE_SEPARATOR = '-';
    private static final char[] DATE_SEPARATORS;
    private static final char IBM_DATE_TIME_SEPARATOR = '-';
    private static final char ODBC_DATE_TIME_SEPARATOR = ' ';
    private static final char[] DATE_TIME_SEPARATORS;
    private static final char[] DATE_TIME_SEPARATORS_OR_END;
    private static final char IBM_TIME_SEPARATOR = '.';
    private static final char ODBC_TIME_SEPARATOR = ':';
    private static final char[] TIME_SEPARATORS;
    private static final char[] TIME_SEPARATORS_OR_END;
    private static final char[] END_OF_STRING;
    
    public int estimateMemoryUsage() {
        return SQLTimestamp.BASE_MEMORY_USAGE;
    }
    
    public String getString() {
        if (!this.isNull()) {
            String str = this.getTimestamp(null).toString();
            int i = str.indexOf(45);
            if (i >= 0 && i < 4) {
                final StringBuffer sb = new StringBuffer();
                while (i < 4) {
                    sb.append('0');
                    ++i;
                }
                sb.append(str);
                str = sb.toString();
            }
            return str;
        }
        return null;
    }
    
    public Date getDate(Calendar calendar) throws StandardException {
        if (this.isNull()) {
            return null;
        }
        if (calendar == null) {
            calendar = new GregorianCalendar();
        }
        calendar.clear();
        SQLDate.setDateInCalendar(calendar, this.encodedDate);
        return new Date(calendar.getTimeInMillis());
    }
    
    public Time getTime(final Calendar calendar) throws StandardException {
        if (this.isNull()) {
            return null;
        }
        return SQLTime.getTime(calendar, this.encodedTime, this.nanos);
    }
    
    public Object getObject() {
        return this.getTimestamp(null);
    }
    
    public int getLength() {
        return 12;
    }
    
    public String getTypeName() {
        return "TIMESTAMP";
    }
    
    public int getTypeFormatId() {
        return 31;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeInt(this.encodedDate);
        objectOutput.writeInt(this.encodedTime);
        objectOutput.writeInt(this.nanos);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException {
        this.encodedDate = objectInput.readInt();
        this.encodedTime = objectInput.readInt();
        this.nanos = objectInput.readInt();
    }
    
    public DataValueDescriptor cloneValue(final boolean b) {
        return new SQLTimestamp(this.encodedDate, this.encodedTime, this.nanos);
    }
    
    public DataValueDescriptor getNewNull() {
        return new SQLTimestamp();
    }
    
    public void restoreToNull() {
        this.encodedDate = 0;
        this.encodedTime = 0;
        this.nanos = 0;
    }
    
    public void setValueFromResultSet(final ResultSet set, final int n, final boolean b) throws SQLException, StandardException {
        this.setValue(set.getTimestamp(n), null);
    }
    
    public int compare(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        if (this.typePrecedence() < dataValueDescriptor.typePrecedence()) {
            return -dataValueDescriptor.compare(this);
        }
        final boolean null = this.isNull();
        final boolean null2 = dataValueDescriptor.isNull();
        if (!null && !null2) {
            int n;
            int n2;
            int n3;
            if (dataValueDescriptor instanceof SQLTimestamp) {
                final SQLTimestamp sqlTimestamp = (SQLTimestamp)dataValueDescriptor;
                n = sqlTimestamp.encodedDate;
                n2 = sqlTimestamp.encodedTime;
                n3 = sqlTimestamp.nanos;
            }
            else {
                final GregorianCalendar gregorianCalendar = new GregorianCalendar();
                final Timestamp timestamp = dataValueDescriptor.getTimestamp(gregorianCalendar);
                n = computeEncodedDate(timestamp, gregorianCalendar);
                n2 = computeEncodedTime(timestamp, gregorianCalendar);
                n3 = timestamp.getNanos();
            }
            int n4;
            if (this.encodedDate < n) {
                n4 = -1;
            }
            else if (this.encodedDate > n) {
                n4 = 1;
            }
            else if (this.encodedTime < n2) {
                n4 = -1;
            }
            else if (this.encodedTime > n2) {
                n4 = 1;
            }
            else if (this.nanos < n3) {
                n4 = -1;
            }
            else if (this.nanos > n3) {
                n4 = 1;
            }
            else {
                n4 = 0;
            }
            return n4;
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
    
    public SQLTimestamp() {
    }
    
    public SQLTimestamp(final Timestamp timestamp) throws StandardException {
        this.setValue(timestamp, null);
    }
    
    SQLTimestamp(final int encodedDate, final int encodedTime, final int nanos) {
        this.encodedDate = encodedDate;
        this.encodedTime = encodedTime;
        this.nanos = nanos;
    }
    
    public SQLTimestamp(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        Calendar calendar = null;
        if (dataValueDescriptor == null || dataValueDescriptor.isNull() || dataValueDescriptor2 == null || dataValueDescriptor2.isNull()) {
            return;
        }
        if (dataValueDescriptor instanceof SQLDate) {
            this.encodedDate = ((SQLDate)dataValueDescriptor).getEncodedDate();
        }
        else {
            calendar = new GregorianCalendar();
            this.encodedDate = computeEncodedDate(dataValueDescriptor.getDate(calendar), calendar);
        }
        if (dataValueDescriptor2 instanceof SQLTime) {
            this.encodedTime = ((SQLTime)dataValueDescriptor2).getEncodedTime();
        }
        else {
            if (calendar == null) {
                calendar = new GregorianCalendar();
            }
            this.encodedTime = computeEncodedTime(dataValueDescriptor2.getTime(calendar), calendar);
        }
    }
    
    public SQLTimestamp(final String s, final boolean b, final LocaleFinder localeFinder) throws StandardException {
        this.parseTimestamp(s, b, localeFinder, null);
    }
    
    public SQLTimestamp(final String s, final boolean b, final LocaleFinder localeFinder, final Calendar calendar) throws StandardException {
        this.parseTimestamp(s, b, localeFinder, calendar);
    }
    
    private void parseTimestamp(String trimTrailing, final boolean b, final LocaleFinder localeFinder, final Calendar calendar) throws StandardException {
        final DateTimeParser dateTimeParser = new DateTimeParser(trimTrailing);
        try {
            final int[] dateOrTimestamp = parseDateOrTimestamp(dateTimeParser, true);
            this.encodedDate = dateOrTimestamp[0];
            this.encodedTime = dateOrTimestamp[1];
            this.nanos = dateOrTimestamp[2];
        }
        catch (StandardException ex2) {
            final StandardException ex = ex2;
            try {
                trimTrailing = StringUtil.trimTrailing(trimTrailing);
                final int[] localTimestamp = parseLocalTimestamp(trimTrailing, localeFinder, calendar);
                this.encodedDate = localTimestamp[0];
                this.encodedTime = localTimestamp[1];
                return;
            }
            catch (ParseException ex3) {}
            catch (StandardException ex4) {}
            if (ex != null) {
                throw ex;
            }
            throw StandardException.newException("22007.S.181");
        }
    }
    
    static int[] parseLocalTimestamp(final String source, final LocaleFinder localeFinder, Calendar calendar) throws StandardException, ParseException {
        DateFormat dateFormat;
        if (localeFinder == null) {
            dateFormat = DateFormat.getDateTimeInstance();
        }
        else if (calendar == null) {
            dateFormat = localeFinder.getTimestampFormat();
        }
        else {
            dateFormat = (DateFormat)localeFinder.getTimestampFormat().clone();
        }
        if (calendar == null) {
            calendar = new GregorianCalendar();
        }
        else {
            dateFormat.setCalendar(calendar);
        }
        final java.util.Date parse = dateFormat.parse(source);
        return new int[] { computeEncodedDate(parse, calendar), computeEncodedTime(parse, calendar) };
    }
    
    static int[] parseDateOrTimestamp(final DateTimeParser dateTimeParser, final boolean b) throws StandardException {
        final int int1 = dateTimeParser.parseInt(4, false, SQLTimestamp.DATE_SEPARATORS, false);
        final int int2 = dateTimeParser.parseInt(2, true, SQLTimestamp.DATE_SEPARATORS, false);
        final int int3 = dateTimeParser.parseInt(2, true, b ? SQLTimestamp.DATE_TIME_SEPARATORS : SQLTimestamp.DATE_TIME_SEPARATORS_OR_END, false);
        int int4 = 0;
        int int5 = 0;
        int int6 = 0;
        int n = 0;
        if (dateTimeParser.getCurrentSeparator() != '\0') {
            final char c = (dateTimeParser.getCurrentSeparator() == ' ') ? ':' : '.';
            int4 = dateTimeParser.parseInt(2, true, SQLTimestamp.TIME_SEPARATORS, false);
            if (c == dateTimeParser.getCurrentSeparator()) {
                int5 = dateTimeParser.parseInt(2, false, SQLTimestamp.TIME_SEPARATORS, false);
                if (c == dateTimeParser.getCurrentSeparator()) {
                    int6 = dateTimeParser.parseInt(2, false, SQLTimestamp.TIME_SEPARATORS_OR_END, false);
                    if (dateTimeParser.getCurrentSeparator() == '.') {
                        n = dateTimeParser.parseInt(9, true, SQLTimestamp.END_OF_STRING, true) * 1;
                    }
                }
            }
        }
        dateTimeParser.checkEnd();
        return new int[] { SQLDate.computeEncodedDate(int1, int2, int3), SQLTime.computeEncodedTime(int4, int5, int6), n };
    }
    
    void setObject(final Object o) throws StandardException {
        this.setValue((Timestamp)o);
    }
    
    protected void setFrom(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        if (dataValueDescriptor instanceof SQLTimestamp) {
            this.restoreToNull();
            final SQLTimestamp sqlTimestamp = (SQLTimestamp)dataValueDescriptor;
            this.encodedDate = sqlTimestamp.encodedDate;
            this.encodedTime = sqlTimestamp.encodedTime;
            this.nanos = sqlTimestamp.nanos;
        }
        else {
            final GregorianCalendar gregorianCalendar = new GregorianCalendar();
            this.setValue(dataValueDescriptor.getTimestamp(gregorianCalendar), gregorianCalendar);
        }
    }
    
    public void setValue(final Date date, Calendar calendar) throws StandardException {
        this.restoreToNull();
        if (date != null) {
            if (calendar == null) {
                calendar = new GregorianCalendar();
            }
            this.encodedDate = computeEncodedDate(date, calendar);
        }
    }
    
    public void setValue(final Timestamp timestamp, final Calendar calendar) throws StandardException {
        this.restoreToNull();
        this.setNumericTimestamp(timestamp, calendar);
    }
    
    public void setValue(final String s) throws StandardException {
        this.restoreToNull();
        if (s != null) {
            final DatabaseContext databaseContext = (DatabaseContext)ContextService.getContext("Database");
            this.parseTimestamp(s, false, (databaseContext == null) ? null : databaseContext.getDatabase(), null);
        }
    }
    
    NumberDataValue nullValueInt() {
        return new SQLInteger();
    }
    
    NumberDataValue nullValueDouble() {
        return new SQLDouble();
    }
    
    public NumberDataValue getYear(final NumberDataValue numberDataValue) throws StandardException {
        if (this.isNull()) {
            return this.nullValueInt();
        }
        return SQLDate.setSource(SQLDate.getYear(this.encodedDate), numberDataValue);
    }
    
    public NumberDataValue getMonth(final NumberDataValue numberDataValue) throws StandardException {
        if (this.isNull()) {
            return this.nullValueInt();
        }
        return SQLDate.setSource(SQLDate.getMonth(this.encodedDate), numberDataValue);
    }
    
    public NumberDataValue getDate(final NumberDataValue numberDataValue) throws StandardException {
        if (this.isNull()) {
            return this.nullValueInt();
        }
        return SQLDate.setSource(SQLDate.getDay(this.encodedDate), numberDataValue);
    }
    
    public NumberDataValue getHours(final NumberDataValue numberDataValue) throws StandardException {
        if (this.isNull()) {
            return this.nullValueInt();
        }
        return SQLDate.setSource(SQLTime.getHour(this.encodedTime), numberDataValue);
    }
    
    public NumberDataValue getMinutes(final NumberDataValue numberDataValue) throws StandardException {
        if (this.isNull()) {
            return this.nullValueInt();
        }
        return SQLDate.setSource(SQLTime.getMinute(this.encodedTime), numberDataValue);
    }
    
    public NumberDataValue getSeconds(final NumberDataValue numberDataValue) throws StandardException {
        if (this.isNull()) {
            return this.nullValueDouble();
        }
        NumberDataValue numberDataValue2;
        if (numberDataValue != null) {
            numberDataValue2 = numberDataValue;
        }
        else {
            numberDataValue2 = new SQLDouble();
        }
        numberDataValue2.setValue(SQLTime.getSecond(this.encodedTime) + this.nanos / 1.0E9);
        return numberDataValue2;
    }
    
    public String toString() {
        if (this.isNull()) {
            return "NULL";
        }
        return this.getTimestamp(null).toString();
    }
    
    public int hashCode() {
        if (this.isNull()) {
            return 0;
        }
        return this.encodedDate + this.encodedTime + this.nanos;
    }
    
    public int typePrecedence() {
        return 110;
    }
    
    public final boolean isNull() {
        return this.encodedDate == 0;
    }
    
    public Timestamp getTimestamp(Calendar calendar) {
        if (this.isNull()) {
            return null;
        }
        if (calendar == null) {
            calendar = new GregorianCalendar();
        }
        this.setCalendar(calendar);
        final Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
        timestamp.setNanos(this.nanos);
        return timestamp;
    }
    
    private void setCalendar(final Calendar calendar) {
        calendar.clear();
        SQLDate.setDateInCalendar(calendar, this.encodedDate);
        SQLTime.setTimeInCalendar(calendar, this.encodedTime);
        calendar.set(14, 0);
    }
    
    private void setNumericTimestamp(final Timestamp timestamp, Calendar calendar) throws StandardException {
        if (timestamp != null) {
            if (calendar == null) {
                calendar = new GregorianCalendar();
            }
            this.encodedDate = computeEncodedDate(timestamp, calendar);
            this.encodedTime = computeEncodedTime(timestamp, calendar);
            this.nanos = timestamp.getNanos();
        }
    }
    
    private static int computeEncodedDate(final java.util.Date time, final Calendar calendar) throws StandardException {
        if (time == null) {
            return 0;
        }
        calendar.setTime(time);
        return SQLDate.computeEncodedDate(calendar);
    }
    
    private static int computeEncodedTime(final java.util.Date time, final Calendar calendar) throws StandardException {
        calendar.setTime(time);
        return SQLTime.computeEncodedTime(calendar);
    }
    
    public void setInto(final PreparedStatement preparedStatement, final int n) throws SQLException, StandardException {
        preparedStatement.setTimestamp(n, this.getTimestamp(null));
    }
    
    public static DateTimeDataValue computeTimestampFunction(final DataValueDescriptor dataValueDescriptor, final DataValueFactory dataValueFactory) throws StandardException {
        try {
            if (dataValueDescriptor.isNull()) {
                return new SQLTimestamp();
            }
            if (dataValueDescriptor instanceof SQLTimestamp) {
                return (SQLTimestamp)dataValueDescriptor.cloneValue(false);
            }
            final String string = dataValueDescriptor.getString();
            if (string.length() == 14) {
                return new SQLTimestamp(SQLDate.computeEncodedDate(parseDateTimeInteger(string, 0, 4), parseDateTimeInteger(string, 4, 2), parseDateTimeInteger(string, 6, 2)), SQLTime.computeEncodedTime(parseDateTimeInteger(string, 8, 2), parseDateTimeInteger(string, 10, 2), parseDateTimeInteger(string, 12, 2)), 0);
            }
            return dataValueFactory.getTimestampValue(string, false);
        }
        catch (StandardException ex) {
            if ("22007.S.181".startsWith(ex.getSQLState())) {
                throw StandardException.newException("22008.S", dataValueDescriptor.getString(), "timestamp");
            }
            throw ex;
        }
    }
    
    static int parseDateTimeInteger(final String s, final int n, final int n2) throws StandardException {
        final int n3 = n + n2;
        int n4 = 0;
        for (int i = n; i < n3; ++i) {
            final char char1 = s.charAt(i);
            if (!Character.isDigit(char1)) {
                throw StandardException.newException("22007.S.181");
            }
            n4 = 10 * n4 + Character.digit(char1, 10);
        }
        return n4;
    }
    
    public DateTimeDataValue timestampAdd(final int n, final NumberDataValue numberDataValue, final Date date, DateTimeDataValue dateTimeDataValue) throws StandardException {
        if (dateTimeDataValue == null) {
            dateTimeDataValue = new SQLTimestamp();
        }
        final SQLTimestamp sqlTimestamp = (SQLTimestamp)dateTimeDataValue;
        if (this.isNull() || numberDataValue.isNull()) {
            sqlTimestamp.restoreToNull();
            return dateTimeDataValue;
        }
        sqlTimestamp.setFrom(this);
        final int int1 = numberDataValue.getInt();
        switch (n) {
            case 0: {
                final long n2 = this.nanos + int1;
                if (n2 >= 0L && n2 < 1000000000L) {
                    sqlTimestamp.nanos = (int)n2;
                    break;
                }
                int n3 = (int)(n2 / 1000000000L);
                if (n2 >= 0L) {
                    sqlTimestamp.nanos = (int)(n2 % 1000000000L);
                }
                else {
                    --n3;
                    sqlTimestamp.nanos = (int)(n2 - n3 * 1000000000L);
                }
                this.addInternal(13, n3, sqlTimestamp);
                break;
            }
            case 1: {
                this.addInternal(13, int1, sqlTimestamp);
                break;
            }
            case 2: {
                this.addInternal(12, int1, sqlTimestamp);
                break;
            }
            case 3: {
                this.addInternal(10, int1, sqlTimestamp);
                break;
            }
            case 4: {
                this.addInternal(5, int1, sqlTimestamp);
                break;
            }
            case 5: {
                this.addInternal(5, int1 * 7, sqlTimestamp);
                break;
            }
            case 6: {
                this.addInternal(2, int1, sqlTimestamp);
                break;
            }
            case 7: {
                this.addInternal(2, int1 * 3, sqlTimestamp);
                break;
            }
            case 8: {
                this.addInternal(1, int1, sqlTimestamp);
                break;
            }
            default: {
                throw StandardException.newException("22008.S", ReuseFactory.getInteger(n), "TIMESTAMPADD");
            }
        }
        return sqlTimestamp;
    }
    
    private void addInternal(final int n, final int n2, final SQLTimestamp sqlTimestamp) throws StandardException {
        final GregorianCalendar calendar = new GregorianCalendar();
        this.setCalendar(calendar);
        try {
            calendar.add(n, n2);
            sqlTimestamp.encodedTime = SQLTime.computeEncodedTime(calendar);
            sqlTimestamp.encodedDate = SQLDate.computeEncodedDate(calendar);
        }
        catch (StandardException ex) {
            final String sqlState = ex.getSQLState();
            if (sqlState != null && sqlState.length() > 0 && "22007.S.180".startsWith(sqlState)) {
                throw StandardException.newException("22003", "TIMESTAMP");
            }
            throw ex;
        }
    }
    
    public NumberDataValue timestampDiff(final int n, final DateTimeDataValue dateTimeDataValue, final Date date, NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            numberDataValue = new SQLLongint();
        }
        if (this.isNull() || dateTimeDataValue.isNull()) {
            numberDataValue.setToNull();
            return numberDataValue;
        }
        final SQLTimestamp promote = promote(dateTimeDataValue, date);
        final GregorianCalendar gregorianCalendar = new GregorianCalendar();
        this.setCalendar(gregorianCalendar);
        final long n2 = gregorianCalendar.getTime().getTime() / 1000L;
        promote.setCalendar(gregorianCalendar);
        long a = n2 - gregorianCalendar.getTime().getTime() / 1000L;
        int n3 = this.nanos - promote.nanos;
        if (n3 < 0 && a > 0L) {
            --a;
            n3 += 1000000000;
        }
        else if (n3 > 0 && a < 0L) {
            ++a;
            n3 -= 1000000000;
        }
        long value = 0L;
        switch (n) {
            case 0: {
                value = a * 1000000000L + n3;
                break;
            }
            case 1: {
                value = a;
                break;
            }
            case 2: {
                value = a / 60L;
                break;
            }
            case 3: {
                value = a / 3600L;
                break;
            }
            case 4: {
                value = a / 86400L;
                break;
            }
            case 5: {
                value = a / 604800L;
                break;
            }
            case 6:
            case 7: {
                if (Math.abs(a) > 31622400L) {
                    value = 12L * (a / 31622400L);
                }
                else {
                    value = a / 2678400L;
                }
                if (a >= 0L) {
                    if (value >= 2147483647L) {
                        throw StandardException.newException("22003", "INTEGER");
                    }
                    gregorianCalendar.add(2, (int)(value + 1L));
                    while (gregorianCalendar.getTime().getTime() / 1000L <= n2) {
                        gregorianCalendar.add(2, 1);
                        ++value;
                    }
                }
                else {
                    if (value <= -2147483648L) {
                        throw StandardException.newException("22003", "INTEGER");
                    }
                    gregorianCalendar.add(2, (int)(value - 1L));
                    while (gregorianCalendar.getTime().getTime() / 1000L >= n2) {
                        gregorianCalendar.add(2, -1);
                        --value;
                    }
                }
                if (n == 7) {
                    value /= 3L;
                    break;
                }
                break;
            }
            case 8: {
                value = a / 31622400L;
                if (a >= 0L) {
                    if (value >= 2147483647L) {
                        throw StandardException.newException("22003", "INTEGER");
                    }
                    gregorianCalendar.add(1, (int)(value + 1L));
                    while (gregorianCalendar.getTime().getTime() / 1000L <= n2) {
                        gregorianCalendar.add(1, 1);
                        ++value;
                    }
                    break;
                }
                else {
                    if (value <= -2147483648L) {
                        throw StandardException.newException("22003", "INTEGER");
                    }
                    gregorianCalendar.add(1, (int)(value - 1L));
                    while (gregorianCalendar.getTime().getTime() / 1000L >= n2) {
                        gregorianCalendar.add(1, -1);
                        --value;
                    }
                    break;
                }
                break;
            }
            default: {
                throw StandardException.newException("22008.S", ReuseFactory.getInteger(n), "TIMESTAMPDIFF");
            }
        }
        numberDataValue.setValue(value);
        return numberDataValue;
    }
    
    static SQLTimestamp promote(final DateTimeDataValue dateTimeDataValue, final Date date) throws StandardException {
        if (dateTimeDataValue instanceof SQLTimestamp) {
            return (SQLTimestamp)dateTimeDataValue;
        }
        if (dateTimeDataValue instanceof SQLTime) {
            return new SQLTimestamp(SQLDate.computeEncodedDate(date, null), ((SQLTime)dateTimeDataValue).getEncodedTime(), 0);
        }
        if (dateTimeDataValue instanceof SQLDate) {
            return new SQLTimestamp(((SQLDate)dateTimeDataValue).getEncodedDate(), 0, 0);
        }
        return new SQLTimestamp(dateTimeDataValue.getTimestamp(new GregorianCalendar()));
    }
    
    static {
        BASE_MEMORY_USAGE = ClassSize.estimateBaseFromCatalog(SQLTimestamp.class);
        DATE_SEPARATORS = new char[] { '-' };
        DATE_TIME_SEPARATORS = new char[] { '-', ' ' };
        DATE_TIME_SEPARATORS_OR_END = new char[] { '-', ' ', '\0' };
        TIME_SEPARATORS = new char[] { '.', ':' };
        TIME_SEPARATORS_OR_END = new char[] { '.', ':', '\0' };
        END_OF_STRING = new char[] { '\0' };
    }
}
