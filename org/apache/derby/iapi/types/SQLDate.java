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
import java.util.Date;
import org.apache.derby.iapi.error.StandardException;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import java.util.GregorianCalendar;
import java.sql.Timestamp;
import java.util.Calendar;

public final class SQLDate extends DataType implements DateTimeDataValue
{
    private int encodedDate;
    private static final int BASE_MEMORY_USAGE;
    static final char ISO_SEPARATOR = '-';
    private static final char[] ISO_SEPARATOR_ONLY;
    private static final char IBM_USA_SEPARATOR = '/';
    private static final char[] IBM_USA_SEPARATOR_ONLY;
    private static final char IBM_EUR_SEPARATOR = '.';
    private static final char[] IBM_EUR_SEPARATOR_ONLY;
    private static final char[] END_OF_STRING;
    
    public int estimateMemoryUsage() {
        return SQLDate.BASE_MEMORY_USAGE;
    }
    
    int getEncodedDate() {
        return this.encodedDate;
    }
    
    public String getString() {
        if (!this.isNull()) {
            return encodedDateToString(this.encodedDate);
        }
        return null;
    }
    
    public Timestamp getTimestamp(final Calendar calendar) {
        if (this.isNull()) {
            return null;
        }
        return new Timestamp(this.getTimeInMillis(calendar));
    }
    
    private long getTimeInMillis(Calendar calendar) {
        if (calendar == null) {
            calendar = new GregorianCalendar();
        }
        calendar.clear();
        setDateInCalendar(calendar, this.encodedDate);
        return calendar.getTimeInMillis();
    }
    
    static void setDateInCalendar(final Calendar calendar, final int n) {
        calendar.set(getYear(n), getMonth(n) - 1, getDay(n));
    }
    
    public Object getObject() {
        return this.getDate((Calendar)null);
    }
    
    public int getLength() {
        return 4;
    }
    
    public String getTypeName() {
        return "DATE";
    }
    
    public int getTypeFormatId() {
        return 298;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeInt(this.encodedDate);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException {
        this.encodedDate = objectInput.readInt();
    }
    
    public DataValueDescriptor cloneValue(final boolean b) {
        return new SQLDate(this.encodedDate);
    }
    
    public DataValueDescriptor getNewNull() {
        return new SQLDate();
    }
    
    public void restoreToNull() {
        this.encodedDate = 0;
    }
    
    public void setValueFromResultSet(final ResultSet set, final int n, final boolean b) throws SQLException, StandardException {
        this.setValue(set.getDate(n), null);
    }
    
    public int compare(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        if (this.typePrecedence() < dataValueDescriptor.typePrecedence()) {
            return -dataValueDescriptor.compare(this);
        }
        final boolean null = this.isNull();
        final boolean null2 = dataValueDescriptor.isNull();
        if (!null && !null2) {
            int n;
            if (dataValueDescriptor instanceof SQLDate) {
                n = ((SQLDate)dataValueDescriptor).encodedDate;
            }
            else {
                n = computeEncodedDate(dataValueDescriptor.getDate(new GregorianCalendar()));
            }
            int n2;
            if (this.encodedDate > n) {
                n2 = 1;
            }
            else if (this.encodedDate < n) {
                n2 = -1;
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
    
    public SQLDate() {
    }
    
    public SQLDate(final java.sql.Date date) throws StandardException {
        this.parseDate(date);
    }
    
    private void parseDate(final Date date) throws StandardException {
        this.encodedDate = computeEncodedDate(date);
    }
    
    private SQLDate(final int encodedDate) {
        this.encodedDate = encodedDate;
    }
    
    public SQLDate(final String s, final boolean b, final LocaleFinder localeFinder) throws StandardException {
        this.parseDate(s, b, localeFinder, null);
    }
    
    public SQLDate(final String s, final boolean b, final LocaleFinder localeFinder, final Calendar calendar) throws StandardException {
        this.parseDate(s, b, localeFinder, calendar);
    }
    
    private void parseDate(String trimTrailing, final boolean b, final LocaleFinder localeFinder, final Calendar calendar) throws StandardException {
        boolean b2 = true;
        final DateTimeParser dateTimeParser = new DateTimeParser(trimTrailing);
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        StandardException ex = null;
        try {
            switch (dateTimeParser.nextSeparator()) {
                case '-': {
                    this.encodedDate = SQLTimestamp.parseDateOrTimestamp(dateTimeParser, false)[0];
                    return;
                }
                case '/': {
                    if (b) {
                        b2 = false;
                        break;
                    }
                    n2 = dateTimeParser.parseInt(2, true, SQLDate.IBM_USA_SEPARATOR_ONLY, false);
                    n3 = dateTimeParser.parseInt(2, true, SQLDate.IBM_USA_SEPARATOR_ONLY, false);
                    n = dateTimeParser.parseInt(4, false, SQLDate.END_OF_STRING, false);
                    break;
                }
                case '.': {
                    if (b) {
                        b2 = false;
                        break;
                    }
                    n3 = dateTimeParser.parseInt(2, true, SQLDate.IBM_EUR_SEPARATOR_ONLY, false);
                    n2 = dateTimeParser.parseInt(2, true, SQLDate.IBM_EUR_SEPARATOR_ONLY, false);
                    n = dateTimeParser.parseInt(4, false, SQLDate.END_OF_STRING, false);
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
            this.encodedDate = computeEncodedDate(n, n2, n3);
        }
        else {
            trimTrailing = StringUtil.trimTrailing(trimTrailing);
            DateFormat dateFormat;
            if (localeFinder == null) {
                dateFormat = DateFormat.getDateInstance();
            }
            else if (calendar == null) {
                dateFormat = localeFinder.getDateFormat();
            }
            else {
                dateFormat = (DateFormat)localeFinder.getDateFormat().clone();
            }
            if (calendar != null) {
                dateFormat.setCalendar(calendar);
            }
            try {
                this.encodedDate = computeEncodedDate(dateFormat.parse(trimTrailing), calendar);
            }
            catch (ParseException ex3) {
                try {
                    this.encodedDate = SQLTimestamp.parseLocalTimestamp(trimTrailing, localeFinder, calendar)[0];
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
        this.setValue((java.sql.Date)o);
    }
    
    protected void setFrom(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        if (dataValueDescriptor instanceof SQLDate) {
            this.restoreToNull();
            this.encodedDate = ((SQLDate)dataValueDescriptor).encodedDate;
        }
        else {
            final GregorianCalendar gregorianCalendar = new GregorianCalendar();
            this.setValue(dataValueDescriptor.getDate(gregorianCalendar), gregorianCalendar);
        }
    }
    
    public void setValue(final java.sql.Date date, final Calendar calendar) throws StandardException {
        this.restoreToNull();
        this.encodedDate = computeEncodedDate(date, calendar);
    }
    
    public void setValue(final Timestamp timestamp, final Calendar calendar) throws StandardException {
        this.restoreToNull();
        this.encodedDate = computeEncodedDate(timestamp, calendar);
    }
    
    public void setValue(final String s) throws StandardException {
        this.restoreToNull();
        if (s != null) {
            final DatabaseContext databaseContext = (DatabaseContext)ContextService.getContext("Database");
            this.parseDate(s, false, (databaseContext == null) ? null : databaseContext.getDatabase(), null);
        }
    }
    
    NumberDataValue nullValueInt() {
        return new SQLInteger();
    }
    
    public NumberDataValue getYear(final NumberDataValue numberDataValue) throws StandardException {
        if (this.isNull()) {
            return this.nullValueInt();
        }
        return setSource(getYear(this.encodedDate), numberDataValue);
    }
    
    public NumberDataValue getMonth(final NumberDataValue numberDataValue) throws StandardException {
        if (this.isNull()) {
            return this.nullValueInt();
        }
        return setSource(getMonth(this.encodedDate), numberDataValue);
    }
    
    public NumberDataValue getDate(final NumberDataValue numberDataValue) throws StandardException {
        if (this.isNull()) {
            return this.nullValueInt();
        }
        return setSource(getDay(this.encodedDate), numberDataValue);
    }
    
    public NumberDataValue getHours(final NumberDataValue numberDataValue) throws StandardException {
        throw StandardException.newException("42X25", "getHours", "Date");
    }
    
    public NumberDataValue getMinutes(final NumberDataValue numberDataValue) throws StandardException {
        throw StandardException.newException("42X25", "getMinutes", "Date");
    }
    
    public NumberDataValue getSeconds(final NumberDataValue numberDataValue) throws StandardException {
        throw StandardException.newException("42X25", "getSeconds", "Date");
    }
    
    public String toString() {
        if (this.isNull()) {
            return "NULL";
        }
        return this.getDate((Calendar)null).toString();
    }
    
    public int hashCode() {
        return this.encodedDate;
    }
    
    public int typePrecedence() {
        return 100;
    }
    
    public final boolean isNull() {
        return this.encodedDate == 0;
    }
    
    public java.sql.Date getDate(final Calendar calendar) {
        if (this.isNull()) {
            return null;
        }
        return new java.sql.Date(this.getTimeInMillis(calendar));
    }
    
    static int getYear(final int n) {
        return n >>> 16;
    }
    
    static int getMonth(final int n) {
        return n >>> 8 & 0xFF;
    }
    
    static int getDay(final int n) {
        return n & 0xFF;
    }
    
    static int computeEncodedDate(final Calendar calendar) throws StandardException {
        return computeEncodedDate(calendar.get(1), calendar.get(2) + 1, calendar.get(5));
    }
    
    static int computeEncodedDate(final int n, final int n2, final int n3) throws StandardException {
        int n4 = 31;
        switch (n2) {
            case 4:
            case 6:
            case 9:
            case 11: {
                n4 = 30;
                break;
            }
            case 2: {
                n4 = ((n % 4 == 0 && (n % 100 != 0 || n % 400 == 0)) ? 29 : 28);
                break;
            }
        }
        if (n < 1 || n > 9999 || n2 < 1 || n2 > 12 || n3 < 1 || n3 > n4) {
            throw StandardException.newException("22007.S.180");
        }
        return (n << 16) + (n2 << 8) + n3;
    }
    
    static void dateToString(final int i, final int j, final int k, final StringBuffer sb) {
        final String string = Integer.toString(i);
        for (int l = string.length(); l < 4; ++l) {
            sb.append('0');
        }
        sb.append(string);
        sb.append('-');
        final String string2 = Integer.toString(j);
        final String string3 = Integer.toString(k);
        if (string2.length() == 1) {
            sb.append('0');
        }
        sb.append(string2);
        sb.append('-');
        if (string3.length() == 1) {
            sb.append('0');
        }
        sb.append(string3);
    }
    
    static String encodedDateToString(final int n) {
        final StringBuffer sb = new StringBuffer();
        dateToString(getYear(n), getMonth(n), getDay(n), sb);
        return sb.toString();
    }
    
    static NumberDataValue setSource(final int value, NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            numberDataValue = new SQLInteger();
        }
        numberDataValue.setValue(value);
        return numberDataValue;
    }
    
    private static int computeEncodedDate(final Date date) throws StandardException {
        return computeEncodedDate(date, null);
    }
    
    static int computeEncodedDate(final Date time, Calendar calendar) throws StandardException {
        if (time == null) {
            return 0;
        }
        if (calendar == null) {
            calendar = new GregorianCalendar();
        }
        calendar.setTime(time);
        return computeEncodedDate(calendar);
    }
    
    public static DateTimeDataValue computeDateFunction(final DataValueDescriptor value, final DataValueFactory dataValueFactory) throws StandardException {
        try {
            if (value.isNull()) {
                return new SQLDate();
            }
            if (value instanceof SQLDate) {
                return (SQLDate)value.cloneValue(false);
            }
            if (value instanceof SQLTimestamp) {
                final SQLDate sqlDate = new SQLDate();
                sqlDate.setValue(value);
                return sqlDate;
            }
            if (value instanceof NumberDataValue) {
                final int int1 = value.getInt();
                if (int1 <= 0 || int1 > 3652059) {
                    throw StandardException.newException("22008.S", value.getString(), "date");
                }
                final GregorianCalendar gregorianCalendar = new GregorianCalendar(1970, 0, 1, 12, 0, 0);
                gregorianCalendar.add(5, int1 - 1);
                return new SQLDate(computeEncodedDate(gregorianCalendar.get(1), gregorianCalendar.get(2) + 1, gregorianCalendar.get(5)));
            }
            else {
                final String string = value.getString();
                if (string.length() != 7) {
                    return dataValueFactory.getDateValue(string, false);
                }
                final int dateTimeInteger = SQLTimestamp.parseDateTimeInteger(string, 0, 4);
                final int dateTimeInteger2 = SQLTimestamp.parseDateTimeInteger(string, 4, 3);
                if (dateTimeInteger2 < 1 || dateTimeInteger2 > 366) {
                    throw StandardException.newException("22008.S", value.getString(), "date");
                }
                final GregorianCalendar gregorianCalendar2 = new GregorianCalendar(dateTimeInteger, 0, 1, 2, 0, 0);
                gregorianCalendar2.add(6, dateTimeInteger2 - 1);
                if (gregorianCalendar2.get(1) != dateTimeInteger) {
                    throw StandardException.newException("22008.S", value.getString(), "date");
                }
                return new SQLDate(computeEncodedDate(dateTimeInteger, gregorianCalendar2.get(2) + 1, gregorianCalendar2.get(5)));
            }
        }
        catch (StandardException ex) {
            if ("22007.S.181".startsWith(ex.getSQLState())) {
                throw StandardException.newException("22008.S", value.getString(), "date");
            }
            throw ex;
        }
    }
    
    public void setInto(final PreparedStatement preparedStatement, final int n) throws SQLException, StandardException {
        preparedStatement.setDate(n, this.getDate((Calendar)null));
    }
    
    public DateTimeDataValue timestampAdd(final int n, final NumberDataValue numberDataValue, final java.sql.Date date, final DateTimeDataValue dateTimeDataValue) throws StandardException {
        return this.toTimestamp().timestampAdd(n, numberDataValue, date, dateTimeDataValue);
    }
    
    private SQLTimestamp toTimestamp() throws StandardException {
        return new SQLTimestamp(this.getEncodedDate(), 0, 0);
    }
    
    public NumberDataValue timestampDiff(final int n, final DateTimeDataValue dateTimeDataValue, final java.sql.Date date, final NumberDataValue numberDataValue) throws StandardException {
        return this.toTimestamp().timestampDiff(n, dateTimeDataValue, date, numberDataValue);
    }
    
    static {
        BASE_MEMORY_USAGE = ClassSize.estimateBaseFromCatalog(SQLDate.class);
        ISO_SEPARATOR_ONLY = new char[] { '-' };
        IBM_USA_SEPARATOR_ONLY = new char[] { '/' };
        IBM_EUR_SEPARATOR_ONLY = new char[] { '.' };
        END_OF_STRING = new char[] { '\0' };
    }
}
