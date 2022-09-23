// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.common.util;

import com.google.common.collect.Interners;
import java.text.NumberFormat;
import java.util.Properties;
import org.apache.hadoop.io.Text;
import org.apache.commons.lang.StringUtils;
import java.util.Locale;
import java.net.UnknownHostException;
import java.net.InetAddress;
import org.apache.commons.logging.Log;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Collection;
import java.util.Date;
import java.text.DateFormat;
import org.apache.hadoop.fs.Path;
import java.net.URISyntaxException;
import java.net.URI;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.Interner;
import java.text.DecimalFormat;
import org.apache.hadoop.hive.common.classification.InterfaceStability;
import org.apache.hadoop.hive.common.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class HiveStringUtils
{
    public static final int SHUTDOWN_HOOK_PRIORITY = 0;
    private static final DecimalFormat decimalFormat;
    private static final Interner<String> STRING_INTERNER;
    private static DecimalFormat oneDecimal;
    public static final String[] emptyStringArray;
    public static final char COMMA = ',';
    public static final String COMMA_STR = ",";
    public static final char ESCAPE_CHAR = '\\';
    
    public static String intern(final String str) {
        if (str == null) {
            return null;
        }
        return HiveStringUtils.STRING_INTERNER.intern(str);
    }
    
    public static List<String> intern(final List<String> list) {
        if (list == null) {
            return null;
        }
        final List<String> newList = new ArrayList<String>(list.size());
        for (final String str : list) {
            newList.add(intern(str));
        }
        return newList;
    }
    
    public static Map<String, String> intern(final Map<String, String> map) {
        if (map == null) {
            return null;
        }
        final Map<String, String> newMap = new HashMap<String, String>(map.size());
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            newMap.put(intern(entry.getKey()), intern(entry.getValue()));
        }
        return newMap;
    }
    
    public static String stringifyException(final Throwable e) {
        final StringWriter stm = new StringWriter();
        final PrintWriter wrt = new PrintWriter(stm);
        e.printStackTrace(wrt);
        wrt.close();
        return stm.toString();
    }
    
    public static String simpleHostname(final String fullHostname) {
        final int offset = fullHostname.indexOf(46);
        if (offset != -1) {
            return fullHostname.substring(0, offset);
        }
        return fullHostname;
    }
    
    public static String humanReadableInt(final long number) {
        final long absNumber = Math.abs(number);
        double result = (double)number;
        String suffix = "";
        if (absNumber < 1024L) {
            return String.valueOf(number);
        }
        if (absNumber < 1048576L) {
            result = number / 1024.0;
            suffix = "k";
        }
        else if (absNumber < 1073741824L) {
            result = number / 1048576.0;
            suffix = "m";
        }
        else {
            result = number / 1.073741824E9;
            suffix = "g";
        }
        return HiveStringUtils.oneDecimal.format(result) + suffix;
    }
    
    public static String formatPercent(final double done, final int digits) {
        final DecimalFormat percentFormat = new DecimalFormat("0.00%");
        final double scale = Math.pow(10.0, digits + 2);
        final double rounded = Math.floor(done * scale);
        percentFormat.setDecimalSeparatorAlwaysShown(false);
        percentFormat.setMinimumFractionDigits(digits);
        percentFormat.setMaximumFractionDigits(digits);
        return percentFormat.format(rounded / scale);
    }
    
    public static String arrayToString(final String[] strs) {
        if (strs.length == 0) {
            return "";
        }
        final StringBuilder sbuf = new StringBuilder();
        sbuf.append(strs[0]);
        for (int idx = 1; idx < strs.length; ++idx) {
            sbuf.append(",");
            sbuf.append(strs[idx]);
        }
        return sbuf.toString();
    }
    
    public static String byteToHexString(final byte[] bytes, final int start, final int end) {
        if (bytes == null) {
            throw new IllegalArgumentException("bytes == null");
        }
        final StringBuilder s = new StringBuilder();
        for (int i = start; i < end; ++i) {
            s.append(String.format("%02x", bytes[i]));
        }
        return s.toString();
    }
    
    public static String byteToHexString(final byte[] bytes) {
        return byteToHexString(bytes, 0, bytes.length);
    }
    
    public static byte[] hexStringToByte(final String hex) {
        final byte[] bts = new byte[hex.length() / 2];
        for (int i = 0; i < bts.length; ++i) {
            bts[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bts;
    }
    
    public static String uriToString(final URI[] uris) {
        if (uris == null) {
            return null;
        }
        final StringBuilder ret = new StringBuilder(uris[0].toString());
        for (int i = 1; i < uris.length; ++i) {
            ret.append(",");
            ret.append(uris[i].toString());
        }
        return ret.toString();
    }
    
    public static URI[] stringToURI(final String[] str) {
        if (str == null) {
            return null;
        }
        final URI[] uris = new URI[str.length];
        for (int i = 0; i < str.length; ++i) {
            try {
                uris[i] = new URI(str[i]);
            }
            catch (URISyntaxException ur) {
                throw new IllegalArgumentException("Failed to create uri for " + str[i], ur);
            }
        }
        return uris;
    }
    
    public static Path[] stringToPath(final String[] str) {
        if (str == null) {
            return null;
        }
        final Path[] p = new Path[str.length];
        for (int i = 0; i < str.length; ++i) {
            p[i] = new Path(str[i]);
        }
        return p;
    }
    
    public static String formatTimeDiff(final long finishTime, final long startTime) {
        final long timeDiff = finishTime - startTime;
        return formatTime(timeDiff);
    }
    
    public static String formatTime(final long timeDiff) {
        final StringBuilder buf = new StringBuilder();
        final long hours = timeDiff / 3600000L;
        long rem = timeDiff % 3600000L;
        final long minutes = rem / 60000L;
        rem %= 60000L;
        final long seconds = rem / 1000L;
        if (hours != 0L) {
            buf.append(hours);
            buf.append("hrs, ");
        }
        if (minutes != 0L) {
            buf.append(minutes);
            buf.append("mins, ");
        }
        buf.append(seconds);
        buf.append("sec");
        return buf.toString();
    }
    
    public static String getFormattedTimeWithDiff(final DateFormat dateFormat, final long finishTime, final long startTime) {
        final StringBuilder buf = new StringBuilder();
        if (0L != finishTime) {
            buf.append(dateFormat.format(new Date(finishTime)));
            if (0L != startTime) {
                buf.append(" (" + formatTimeDiff(finishTime, startTime) + ")");
            }
        }
        return buf.toString();
    }
    
    public static String[] getStrings(final String str) {
        final Collection<String> values = getStringCollection(str);
        if (values.size() == 0) {
            return null;
        }
        return values.toArray(new String[values.size()]);
    }
    
    public static Collection<String> getStringCollection(final String str) {
        List<String> values = new ArrayList<String>();
        if (str == null) {
            return values;
        }
        final StringTokenizer tokenizer = new StringTokenizer(str, ",");
        values = new ArrayList<String>();
        while (tokenizer.hasMoreTokens()) {
            values.add(tokenizer.nextToken());
        }
        return values;
    }
    
    public static Collection<String> getTrimmedStringCollection(final String str) {
        return new ArrayList<String>(Arrays.asList(getTrimmedStrings(str)));
    }
    
    public static String[] getTrimmedStrings(final String str) {
        if (null == str || "".equals(str.trim())) {
            return HiveStringUtils.emptyStringArray;
        }
        return str.trim().split("\\s*,\\s*");
    }
    
    public static String[] split(final String str) {
        return split(str, '\\', ',');
    }
    
    public static String[] split(final String str, final char escapeChar, final char separator) {
        if (str == null) {
            return null;
        }
        final ArrayList<String> strList = new ArrayList<String>();
        final StringBuilder split = new StringBuilder();
        int index = 0;
        while ((index = findNext(str, separator, escapeChar, index, split)) >= 0) {
            ++index;
            strList.add(split.toString());
            split.setLength(0);
        }
        strList.add(split.toString());
        int last = strList.size();
        while (--last >= 0 && "".equals(strList.get(last))) {
            strList.remove(last);
        }
        return strList.toArray(new String[strList.size()]);
    }
    
    public static String[] split(final String str, final char separator) {
        if ("".equals(str)) {
            return new String[] { "" };
        }
        final ArrayList<String> strList = new ArrayList<String>();
        int startIndex = 0;
        for (int nextIndex = 0; (nextIndex = str.indexOf(separator, startIndex)) != -1; startIndex = nextIndex + 1) {
            strList.add(str.substring(startIndex, nextIndex));
        }
        strList.add(str.substring(startIndex));
        int last = strList.size();
        while (--last >= 0 && "".equals(strList.get(last))) {
            strList.remove(last);
        }
        return strList.toArray(new String[strList.size()]);
    }
    
    public static String[] splitAndUnEscape(final String str) {
        return splitAndUnEscape(str, '\\', ',');
    }
    
    public static String[] splitAndUnEscape(final String str, final char escapeChar, final char separator) {
        final String[] result = split(str, escapeChar, separator);
        if (result != null) {
            for (int idx = 0; idx < result.length; ++idx) {
                result[idx] = unEscapeString(result[idx], escapeChar, separator);
            }
        }
        return result;
    }
    
    public static int findNext(final String str, final char separator, final char escapeChar, final int start, final StringBuilder split) {
        int numPreEscapes = 0;
        for (int i = start; i < str.length(); ++i) {
            final char curChar = str.charAt(i);
            if (numPreEscapes == 0 && curChar == separator) {
                return i;
            }
            split.append(curChar);
            numPreEscapes = ((curChar == escapeChar) ? (++numPreEscapes % 2) : 0);
        }
        return -1;
    }
    
    public static String escapeString(final String str) {
        return escapeString(str, '\\', ',');
    }
    
    public static String escapeString(final String str, final char escapeChar, final char charToEscape) {
        return escapeString(str, escapeChar, new char[] { charToEscape });
    }
    
    private static boolean hasChar(final char[] chars, final char character) {
        for (final char target : chars) {
            if (character == target) {
                return true;
            }
        }
        return false;
    }
    
    public static String escapeString(final String str, final char escapeChar, final char[] charsToEscape) {
        if (str == null) {
            return null;
        }
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); ++i) {
            final char curChar = str.charAt(i);
            if (curChar == escapeChar || hasChar(charsToEscape, curChar)) {
                result.append(escapeChar);
            }
            result.append(curChar);
        }
        return result.toString();
    }
    
    public static String unEscapeString(final String str) {
        return unEscapeString(str, '\\', ',');
    }
    
    public static String unEscapeString(final String str, final char escapeChar, final char charToEscape) {
        return unEscapeString(str, escapeChar, new char[] { charToEscape });
    }
    
    public static String unEscapeString(final String str, final char escapeChar, final char[] charsToEscape) {
        if (str == null) {
            return null;
        }
        final StringBuilder result = new StringBuilder(str.length());
        boolean hasPreEscape = false;
        for (int i = 0; i < str.length(); ++i) {
            final char curChar = str.charAt(i);
            if (hasPreEscape) {
                if (curChar != escapeChar && !hasChar(charsToEscape, curChar)) {
                    throw new IllegalArgumentException("Illegal escaped string " + str + " unescaped " + escapeChar + " at " + (i - 1));
                }
                result.append(curChar);
                hasPreEscape = false;
            }
            else {
                if (hasChar(charsToEscape, curChar)) {
                    throw new IllegalArgumentException("Illegal escaped string " + str + " unescaped " + curChar + " at " + i);
                }
                if (curChar == escapeChar) {
                    hasPreEscape = true;
                }
                else {
                    result.append(curChar);
                }
            }
        }
        if (hasPreEscape) {
            throw new IllegalArgumentException("Illegal escaped string " + str + ", not expecting " + escapeChar + " in the end.");
        }
        return result.toString();
    }
    
    private static String toStartupShutdownString(final String prefix, final String[] msg) {
        final StringBuilder b = new StringBuilder(prefix);
        b.append("\n/************************************************************");
        for (final String s : msg) {
            b.append("\n" + prefix + s);
        }
        b.append("\n************************************************************/");
        return b.toString();
    }
    
    public static void startupShutdownMessage(final Class<?> clazz, final String[] args, final Log LOG) {
        final String hostname = getHostname();
        final String classname = clazz.getSimpleName();
        LOG.info(toStartupShutdownString("STARTUP_MSG: ", new String[] { "Starting " + classname, "  host = " + hostname, "  args = " + Arrays.asList(args), "  version = " + HiveVersionInfo.getVersion(), "  classpath = " + System.getProperty("java.class.path"), "  build = " + HiveVersionInfo.getUrl() + " -r " + HiveVersionInfo.getRevision() + "; compiled by '" + HiveVersionInfo.getUser() + "' on " + HiveVersionInfo.getDate() }));
        ShutdownHookManager.addShutdownHook(new Runnable() {
            @Override
            public void run() {
                LOG.info(toStartupShutdownString("SHUTDOWN_MSG: ", new String[] { "Shutting down " + classname + " at " + hostname }));
            }
        }, 0);
    }
    
    public static String getHostname() {
        try {
            return "" + InetAddress.getLocalHost();
        }
        catch (UnknownHostException uhe) {
            return "" + uhe;
        }
    }
    
    public static String escapeHTML(final String string) {
        if (string == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        boolean lastCharacterWasSpace = false;
        final char[] charArray;
        final char[] chars = charArray = string.toCharArray();
        for (final char c : charArray) {
            if (c == ' ') {
                if (lastCharacterWasSpace) {
                    lastCharacterWasSpace = false;
                    sb.append("&nbsp;");
                }
                else {
                    lastCharacterWasSpace = true;
                    sb.append(" ");
                }
            }
            else {
                lastCharacterWasSpace = false;
                switch (c) {
                    case '<': {
                        sb.append("&lt;");
                        break;
                    }
                    case '>': {
                        sb.append("&gt;");
                        break;
                    }
                    case '&': {
                        sb.append("&amp;");
                        break;
                    }
                    case '\"': {
                        sb.append("&quot;");
                        break;
                    }
                    default: {
                        sb.append(c);
                        break;
                    }
                }
            }
        }
        return sb.toString();
    }
    
    public static String byteDesc(final long len) {
        double val = 0.0;
        String ending = "";
        if (len < 1048576L) {
            val = 1.0 * len / 1024.0;
            ending = " KB";
        }
        else if (len < 1073741824L) {
            val = 1.0 * len / 1048576.0;
            ending = " MB";
        }
        else if (len < 1099511627776L) {
            val = 1.0 * len / 1.073741824E9;
            ending = " GB";
        }
        else if (len < 1125899906842624L) {
            val = 1.0 * len / 1.099511627776E12;
            ending = " TB";
        }
        else {
            val = 1.0 * len / 1.125899906842624E15;
            ending = " PB";
        }
        return limitDecimalTo2(val) + ending;
    }
    
    public static synchronized String limitDecimalTo2(final double d) {
        return HiveStringUtils.decimalFormat.format(d);
    }
    
    public static String join(final CharSequence separator, final Iterable<?> strings) {
        final Iterator<?> i = strings.iterator();
        if (!i.hasNext()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder(i.next().toString());
        while (i.hasNext()) {
            sb.append(separator);
            sb.append(i.next().toString());
        }
        return sb.toString();
    }
    
    public static String camelize(final String s) {
        final StringBuilder sb = new StringBuilder();
        final String[] split;
        final String[] words = split = split(s.toLowerCase(Locale.US), '\\', '_');
        for (final String word : split) {
            sb.append(StringUtils.capitalize(word));
        }
        return sb.toString();
    }
    
    public static boolean isUtfStartByte(final byte b) {
        return (b & 0xC0) != 0x80;
    }
    
    public static int getTextUtfLength(final Text t) {
        final byte[] data = t.getBytes();
        int len = 0;
        for (int i = 0; i < t.getLength(); ++i) {
            if (isUtfStartByte(data[i])) {
                ++len;
            }
        }
        return len;
    }
    
    public static String normalizeIdentifier(final String identifier) {
        return identifier.trim().toLowerCase();
    }
    
    public static Map getPropertiesExplain(final Properties properties) {
        if (properties != null) {
            final String value = properties.getProperty("columns.comments");
            if (value != null) {
                final Map clone = new HashMap(properties);
                clone.put("columns.comments", quoteComments(value));
                return clone;
            }
        }
        return properties;
    }
    
    public static String quoteComments(final String value) {
        final char[] chars = value.toCharArray();
        if (!commentProvided(chars)) {
            return null;
        }
        final StringBuilder builder = new StringBuilder();
        int prev = 0;
        for (int i = 0; i < chars.length; ++i) {
            if (chars[i] == '\0') {
                if (builder.length() > 0) {
                    builder.append(',');
                }
                builder.append('\'').append(chars, prev, i - prev).append('\'');
                prev = i + 1;
            }
        }
        builder.append(",'").append(chars, prev, chars.length - prev).append('\'');
        return builder.toString();
    }
    
    public static boolean commentProvided(final char[] chars) {
        for (final char achar : chars) {
            if (achar != '\0') {
                return true;
            }
        }
        return false;
    }
    
    static {
        final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);
        (decimalFormat = (DecimalFormat)numberFormat).applyPattern("#.##");
        STRING_INTERNER = Interners.newWeakInterner();
        HiveStringUtils.oneDecimal = new DecimalFormat("0.0");
        emptyStringArray = new String[0];
    }
    
    public enum TraditionalBinaryPrefix
    {
        KILO(1024L), 
        MEGA(TraditionalBinaryPrefix.KILO.value << 10), 
        GIGA(TraditionalBinaryPrefix.MEGA.value << 10), 
        TERA(TraditionalBinaryPrefix.GIGA.value << 10), 
        PETA(TraditionalBinaryPrefix.TERA.value << 10), 
        EXA(TraditionalBinaryPrefix.PETA.value << 10);
        
        public final long value;
        public final char symbol;
        
        private TraditionalBinaryPrefix(final long value) {
            this.value = value;
            this.symbol = this.toString().charAt(0);
        }
        
        public static TraditionalBinaryPrefix valueOf(char symbol) {
            symbol = Character.toUpperCase(symbol);
            for (final TraditionalBinaryPrefix prefix : values()) {
                if (symbol == prefix.symbol) {
                    return prefix;
                }
            }
            throw new IllegalArgumentException("Unknown symbol '" + symbol + "'");
        }
        
        public static long string2long(String s) {
            s = s.trim();
            final int lastpos = s.length() - 1;
            final char lastchar = s.charAt(lastpos);
            if (Character.isDigit(lastchar)) {
                return Long.parseLong(s);
            }
            long prefix;
            try {
                prefix = valueOf(lastchar).value;
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid size prefix '" + lastchar + "' in '" + s + "'. Allowed prefixes are k, m, g, t, p, e(case insensitive)");
            }
            final long num = Long.parseLong(s.substring(0, lastpos));
            if (num > Long.MAX_VALUE / prefix || num < Long.MIN_VALUE / prefix) {
                throw new IllegalArgumentException(s + " does not fit in a Long");
            }
            return num * prefix;
        }
    }
}
