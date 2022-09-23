// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.tools.i18n;

import java.security.AccessController;
import java.sql.Time;
import java.text.ParseException;
import java.text.FieldPosition;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.StringTokenizer;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.MissingResourceException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.DateFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.security.PrivilegedAction;

public final class LocalizedResource implements PrivilegedAction
{
    private static final boolean SUPPORTS_BIG_DECIMAL_CALLS;
    private ResourceBundle res;
    private Locale locale;
    private String encode;
    private static final String MESSAGE_FILE = "org.apache.derby.loc.toolsmessages";
    private static final String ENV_CODESET = "derby.ui.codeset";
    private static final String ENV_LOCALE = "derby.ui.locale";
    private String messageFileName;
    private String resourceKey;
    private LocalizedOutput out;
    private LocalizedInput in;
    private boolean enableLocalized;
    private boolean unicodeEscape;
    private static LocalizedResource local;
    private int dateSize;
    private int timeSize;
    private int timestampSize;
    private DateFormat formatDate;
    private DateFormat formatTime;
    private DateFormat formatTimestamp;
    private NumberFormat formatNumber;
    private DecimalFormat formatDecimal;
    
    public LocalizedResource() {
        this.init();
    }
    
    public LocalizedResource(final String s, final String s2, final String s3) {
        this.init(s, s2, s3);
    }
    
    public static LocalizedResource getInstance() {
        if (LocalizedResource.local == null) {
            LocalizedResource.local = new LocalizedResource();
        }
        return LocalizedResource.local;
    }
    
    public static void resetLocalizedResourceCache() {
        LocalizedResource.local = null;
    }
    
    public void init() {
        this.init(null, null, null);
    }
    
    public void init(final String encode, final String s, final String messageFileName) {
        if (encode != null) {
            this.encode = encode;
        }
        if (this.encode == null) {
            final String envProperty = this.getEnvProperty("derby.ui.codeset");
            if (envProperty != null) {
                this.encode = envProperty;
            }
        }
        this.locale = this.getNewLocale(s);
        if (this.locale == null) {
            this.locale = this.getNewLocale(this.getEnvProperty("derby.ui.locale"));
        }
        if (this.locale == null) {
            this.locale = Locale.getDefault();
        }
        if (messageFileName != null) {
            this.messageFileName = messageFileName;
        }
        else {
            this.messageFileName = "org.apache.derby.loc.toolsmessages";
        }
        this.out = this.getNewOutput(System.out);
        this.in = this.getNewInput(System.in);
        if (this.enableLocalized && this.locale != null) {
            this.formatDecimal = (DecimalFormat)NumberFormat.getInstance(this.locale);
            this.formatNumber = NumberFormat.getInstance(this.locale);
            this.formatDate = DateFormat.getDateInstance(1, this.locale);
            this.formatTime = DateFormat.getTimeInstance(1, this.locale);
            this.formatTimestamp = DateFormat.getDateTimeInstance(1, 1, this.locale);
        }
        else {
            this.formatDecimal = (DecimalFormat)NumberFormat.getInstance();
            this.formatNumber = NumberFormat.getInstance();
            this.formatDate = DateFormat.getDateInstance(1);
            this.formatTime = DateFormat.getTimeInstance(1);
            this.formatTimestamp = DateFormat.getDateTimeInstance(1, 1);
        }
        this.initMaxSizes2();
    }
    
    private void setResource() {
        if (this.res != null) {
            return;
        }
        if (this.locale == null || this.locale.toString().equals("none")) {
            this.res = ResourceBundle.getBundle(this.messageFileName);
        }
        else {
            try {
                this.res = ResourceBundle.getBundle(this.messageFileName, this.locale);
            }
            catch (MissingResourceException ex) {
                this.res = ResourceBundle.getBundle(this.messageFileName, Locale.ENGLISH);
            }
        }
    }
    
    private void initMaxSizes2() {
        this.dateSize = 0;
        this.timeSize = 0;
        this.timestampSize = 0;
        final Date date = new Date(60907276800000L);
        final Timestamp timestamp = new Timestamp(date.getTime());
        int i = 0;
        while (i <= 11) {
            final int length = this.getDateAsString(date).length();
            if (length > this.dateSize) {
                this.dateSize = length;
            }
            timestamp.setTime(date.getTime() + 79199L);
            final int length2 = this.getTimestampAsString(timestamp).length();
            if (length2 > this.timestampSize) {
                this.timestampSize = length2;
            }
            ++i;
            date.setTime(date.getTime() + 2592000000L);
        }
        int length3 = 18;
        for (int j = 0; j < 24; ++j) {
            final String format = this.formatTime.format(new Date((j * 3600L + 3540L + 59L) * 1000L));
            if (format.length() > length3) {
                length3 = format.length();
            }
        }
        this.timeSize = length3;
    }
    
    public LocalizedInput getNewInput(final InputStream inputStream) {
        try {
            if (this.encode != null) {
                return new LocalizedInput(inputStream, this.encode);
            }
        }
        catch (UnsupportedEncodingException ex) {}
        return new LocalizedInput(inputStream);
    }
    
    public LocalizedInput getNewEncodedInput(final InputStream inputStream, final String s) {
        try {
            return new LocalizedInput(inputStream, s);
        }
        catch (UnsupportedEncodingException ex) {
            return new LocalizedInput(inputStream);
        }
    }
    
    public LocalizedOutput getNewOutput(final OutputStream outputStream) {
        try {
            if (this.encode != null) {
                return new LocalizedOutput(outputStream, this.encode);
            }
        }
        catch (UnsupportedEncodingException ex) {}
        return new LocalizedOutput(outputStream);
    }
    
    public LocalizedOutput getNewEncodedOutput(final OutputStream outputStream, final String s) throws UnsupportedEncodingException {
        return this.out = new LocalizedOutput(outputStream, s);
    }
    
    public String getTextMessage(final String s) {
        return this.getTextMessage(s, new Object[0]);
    }
    
    public String getTextMessage(final String s, final Object o) {
        return this.getTextMessage(s, new Object[] { o });
    }
    
    public String getTextMessage(final String s, final Object o, final Object o2) {
        return this.getTextMessage(s, new Object[] { o, o2 });
    }
    
    public String getTextMessage(final String s, final Object o, final Object o2, final Object o3) {
        return this.getTextMessage(s, new Object[] { o, o2, o3 });
    }
    
    public String getTextMessage(final String s, final Object o, final Object o2, final Object o3, final Object o4) {
        return this.getTextMessage(s, new Object[] { o, o2, o3, o4 });
    }
    
    private Locale getNewLocale(final String str) {
        String nextToken = "";
        String nextToken2 = "";
        if (str == null) {
            return null;
        }
        final StringTokenizer stringTokenizer = new StringTokenizer(str, "_");
        try {
            final String nextToken3 = stringTokenizer.nextToken();
            if (stringTokenizer.hasMoreTokens()) {
                nextToken = stringTokenizer.nextToken();
            }
            if (stringTokenizer.hasMoreTokens()) {
                nextToken2 = stringTokenizer.nextToken();
            }
            return new Locale(nextToken3, nextToken, nextToken2);
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public String getTextMessage(final String key, final Object[] array) {
        if (this.res == null) {
            this.setResource();
        }
        try {
            return MessageFormat.format(this.res.getString(key), array);
        }
        catch (Exception ex) {
            String string = key;
            for (int i = 0; i < array.length; ++i) {
                string = string + ", <{" + i + "}>";
            }
            return MessageFormat.format(string, array);
        }
    }
    
    public String getLocalizedString(final ResultSet set, final ResultSetMetaData resultSetMetaData, final int n) throws SQLException {
        if (!this.enableLocalized) {
            return set.getString(n);
        }
        final int columnType = resultSetMetaData.getColumnType(n);
        if (columnType == 91) {
            return this.getDateAsString(set.getDate(n));
        }
        if (columnType == 4 || columnType == 5 || columnType == -5 || columnType == -6) {
            return this.getNumberAsString(set.getLong(n));
        }
        if (columnType == 7 || columnType == 6 || columnType == 8) {
            return this.getNumberAsString(set.getDouble(n));
        }
        if (LocalizedResource.SUPPORTS_BIG_DECIMAL_CALLS && (columnType == 2 || columnType == 3)) {
            return this.getNumberAsString(set.getObject(n));
        }
        if (columnType == 92) {
            return this.getTimeAsString(set.getTime(n));
        }
        if (columnType == 93) {
            return this.getTimestampAsString(set.getTimestamp(n));
        }
        return set.getString(n);
    }
    
    public String getDateAsString(final Date date) {
        if (!this.enableLocalized) {
            return date.toString();
        }
        return this.formatDate.format(date);
    }
    
    public String getTimeAsString(final Date date) {
        if (!this.enableLocalized) {
            return date.toString();
        }
        return this.formatTime.format(date, new StringBuffer(), new FieldPosition(0)).toString();
    }
    
    public String getNumberAsString(final int i) {
        if (this.enableLocalized) {
            return this.formatNumber.format(i);
        }
        return String.valueOf(i);
    }
    
    public String getNumberAsString(final long n) {
        if (this.enableLocalized) {
            return this.formatNumber.format(n);
        }
        return String.valueOf(n);
    }
    
    public String getNumberAsString(final Object number) {
        if (this.enableLocalized) {
            return this.formatNumber.format(number, new StringBuffer(), new FieldPosition(0)).toString();
        }
        return number.toString();
    }
    
    public String getNumberAsString(final double n) {
        if (!this.enableLocalized) {
            return String.valueOf(n);
        }
        return this.formatDecimal.format(n);
    }
    
    public String getTimestampAsString(final Timestamp timestamp) {
        if (!this.enableLocalized) {
            return timestamp.toString();
        }
        return this.formatTimestamp.format(timestamp, new StringBuffer(), new FieldPosition(0)).toString();
    }
    
    public int getColumnDisplaySize(final ResultSetMetaData resultSetMetaData, final int n) throws SQLException {
        if (!this.enableLocalized) {
            return resultSetMetaData.getColumnDisplaySize(n);
        }
        final int columnType = resultSetMetaData.getColumnType(n);
        if (columnType == 91) {
            return this.dateSize;
        }
        if (columnType == 92) {
            return this.timeSize;
        }
        if (columnType == 93) {
            return this.timestampSize;
        }
        return resultSetMetaData.getColumnDisplaySize(n);
    }
    
    public String getStringFromDate(final String source) throws ParseException {
        if (!this.enableLocalized) {
            return source;
        }
        return new java.sql.Date(this.formatDate.parse(source).getTime()).toString();
    }
    
    public String getStringFromTime(final String source) throws ParseException {
        if (!this.enableLocalized) {
            return source;
        }
        return new Time(this.formatTime.parse(source).getTime()).toString();
    }
    
    public String getStringFromValue(final String source) throws ParseException {
        if (!this.enableLocalized) {
            return source;
        }
        return this.formatNumber.parse(source).toString();
    }
    
    public String getStringFromTimestamp(final String source) throws ParseException {
        if (!this.enableLocalized) {
            return source;
        }
        return new Timestamp(this.formatTimestamp.parse(source).getTime()).toString();
    }
    
    public Locale getLocale() {
        return this.locale;
    }
    
    private final synchronized String getEnvProperty(final String resourceKey) {
        String s;
        try {
            this.resourceKey = resourceKey;
            s = AccessController.doPrivileged((PrivilegedAction<String>)this);
        }
        catch (SecurityException ex) {
            s = null;
        }
        return s;
    }
    
    public final Object run() {
        return System.getProperty(this.resourceKey);
    }
    
    public static boolean enableLocalization(final boolean enableLocalized) {
        getInstance().enableLocalized = enableLocalized;
        getInstance().init();
        return enableLocalized;
    }
    
    public boolean isLocalized() {
        return getInstance().enableLocalized;
    }
    
    public static String getMessage(final String s) {
        return getInstance().getTextMessage(s);
    }
    
    public static String getMessage(final String s, final Object o) {
        return getInstance().getTextMessage(s, o);
    }
    
    public static String getMessage(final String s, final Object o, final Object o2) {
        return getInstance().getTextMessage(s, o, o2);
    }
    
    public static String getMessage(final String s, final Object o, final Object o2, final Object o3) {
        return getInstance().getTextMessage(s, o, o2, o3);
    }
    
    public static String getMessage(final String s, final Object o, final Object o2, final Object o3, final Object o4) {
        return getInstance().getTextMessage(s, o, o2, o3, o4);
    }
    
    public static LocalizedOutput OutputWriter() {
        return getInstance().out;
    }
    
    public static LocalizedInput InputReader() {
        return getInstance().in;
    }
    
    public static String getNumber(final long n) {
        return getInstance().getNumberAsString(n);
    }
    
    public static String getNumber(final int n) {
        return getInstance().getNumberAsString(n);
    }
    
    public String toString() {
        return "toString(){\nlocale=" + ((this.locale == null) ? "null" : this.locale.toString()) + "\n" + "encode=" + this.encode + "\n" + "messageFile=" + this.messageFileName + "\n" + "resourceKey=" + this.resourceKey + "\n" + "enableLocalized=" + this.enableLocalized + " \n" + "dateSize=" + this.dateSize + "\n" + "timeSize=" + this.timeSize + "\n" + "timestampSize=" + this.timestampSize + "\n}";
    }
    
    static {
        boolean supports_BIG_DECIMAL_CALLS;
        try {
            Class.forName("java.math.BigDecimal");
            ResultSet.class.getMethod("getBigDecimal", Integer.TYPE);
            supports_BIG_DECIMAL_CALLS = true;
        }
        catch (Throwable t) {
            supports_BIG_DECIMAL_CALLS = false;
        }
        SUPPORTS_BIG_DECIMAL_CALLS = supports_BIG_DECIMAL_CALLS;
    }
}
