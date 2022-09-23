// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import com.google.common.base.Preconditions;
import java.util.regex.Matcher;
import java.util.Map;
import java.util.Iterator;
import org.apache.commons.lang3.SystemUtils;
import org.apache.hadoop.net.NetUtils;
import org.slf4j.Logger;
import org.apache.commons.logging.Log;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.hadoop.fs.Path;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.Locale;
import com.google.common.net.InetAddresses;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Pattern;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class StringUtils
{
    public static final int SHUTDOWN_HOOK_PRIORITY = 0;
    public static final Pattern SHELL_ENV_VAR_PATTERN;
    public static final Pattern WIN_ENV_VAR_PATTERN;
    public static final Pattern ENV_VAR_PATTERN;
    public static final String[] emptyStringArray;
    public static final char COMMA = ',';
    public static final String COMMA_STR = ",";
    public static final char ESCAPE_CHAR = '\\';
    
    public static String stringifyException(final Throwable e) {
        final StringWriter stm = new StringWriter();
        final PrintWriter wrt = new PrintWriter(stm);
        e.printStackTrace(wrt);
        wrt.close();
        return stm.toString();
    }
    
    public static String simpleHostname(final String fullHostname) {
        if (InetAddresses.isInetAddress(fullHostname)) {
            return fullHostname;
        }
        final int offset = fullHostname.indexOf(46);
        if (offset != -1) {
            return fullHostname.substring(0, offset);
        }
        return fullHostname;
    }
    
    @Deprecated
    public static String humanReadableInt(final long number) {
        return TraditionalBinaryPrefix.long2String(number, "", 1);
    }
    
    public static String format(final String format, final Object... objects) {
        return String.format(Locale.ENGLISH, format, objects);
    }
    
    public static String formatPercent(final double fraction, final int decimalPlaces) {
        return format("%." + decimalPlaces + "f%%", fraction * 100.0);
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
            s.append(format("%02x", bytes[i]));
        }
        return s.toString();
    }
    
    public static String byteToHexString(final byte[] bytes) {
        return byteToHexString(bytes, 0, bytes.length);
    }
    
    public static String byteToHexString(final byte b) {
        return byteToHexString(new byte[] { b });
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
    
    public static String formatTimeSortable(final long timeDiff) {
        final StringBuilder buf = new StringBuilder();
        long hours = timeDiff / 3600000L;
        long rem = timeDiff % 3600000L;
        long minutes = rem / 60000L;
        rem %= 60000L;
        long seconds = rem / 1000L;
        if (hours > 99L) {
            hours = 99L;
            minutes = 59L;
            seconds = 59L;
        }
        buf.append(String.format("%02d", hours));
        buf.append("hrs, ");
        buf.append(String.format("%02d", minutes));
        buf.append("mins, ");
        buf.append(String.format("%02d", seconds));
        buf.append("sec");
        return buf.toString();
    }
    
    public static String getFormattedTimeWithDiff(final FastDateFormat dateFormat, final long finishTime, final long startTime) {
        final String formattedFinishTime = dateFormat.format(finishTime);
        return getFormattedTimeWithDiff(formattedFinishTime, finishTime, startTime);
    }
    
    public static String getFormattedTimeWithDiff(final String formattedFinishTime, final long finishTime, final long startTime) {
        final StringBuilder buf = new StringBuilder();
        if (0L != finishTime) {
            buf.append(formattedFinishTime);
            if (0L != startTime) {
                buf.append(" (" + formatTimeDiff(finishTime, startTime) + ")");
            }
        }
        return buf.toString();
    }
    
    public static String[] getStrings(final String str) {
        final String delim = ",";
        return getStrings(str, delim);
    }
    
    public static String[] getStrings(final String str, final String delim) {
        final Collection<String> values = getStringCollection(str, delim);
        if (values.size() == 0) {
            return null;
        }
        return values.toArray(new String[values.size()]);
    }
    
    public static Collection<String> getStringCollection(final String str) {
        final String delim = ",";
        return getStringCollection(str, delim);
    }
    
    public static Collection<String> getStringCollection(final String str, final String delim) {
        final List<String> values = new ArrayList<String>();
        if (str == null) {
            return values;
        }
        final StringTokenizer tokenizer = new StringTokenizer(str, delim);
        while (tokenizer.hasMoreTokens()) {
            values.add(tokenizer.nextToken());
        }
        return values;
    }
    
    public static Collection<String> getTrimmedStringCollection(final String str) {
        final Set<String> set = new LinkedHashSet<String>(Arrays.asList(getTrimmedStrings(str)));
        set.remove("");
        return set;
    }
    
    public static String[] getTrimmedStrings(final String str) {
        if (null == str || str.trim().isEmpty()) {
            return StringUtils.emptyStringArray;
        }
        return str.trim().split("\\s*[,\n]\\s*");
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
        if (str.isEmpty()) {
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
    
    public static String toStartupShutdownString(final String prefix, final String[] msg) {
        final StringBuilder b = new StringBuilder(prefix);
        b.append("\n/************************************************************");
        for (final String s : msg) {
            b.append("\n").append(prefix).append(s);
        }
        b.append("\n************************************************************/");
        return b.toString();
    }
    
    public static void startupShutdownMessage(final Class<?> clazz, final String[] args, final Log LOG) {
        startupShutdownMessage(clazz, args, LogAdapter.create(LOG));
    }
    
    public static void startupShutdownMessage(final Class<?> clazz, final String[] args, final Logger LOG) {
        startupShutdownMessage(clazz, args, LogAdapter.create(LOG));
    }
    
    static void startupShutdownMessage(final Class<?> clazz, final String[] args, final LogAdapter LOG) {
        final String hostname = NetUtils.getHostname();
        final String classname = clazz.getSimpleName();
        LOG.info(createStartupShutdownMessage(classname, hostname, args));
        if (SystemUtils.IS_OS_UNIX) {
            try {
                SignalLogger.INSTANCE.register(LOG);
            }
            catch (Throwable t) {
                LOG.warn("failed to register any UNIX signal loggers: ", t);
            }
        }
        ShutdownHookManager.get().addShutdownHook(new Runnable() {
            @Override
            public void run() {
                LOG.info(StringUtils.toStartupShutdownString("SHUTDOWN_MSG: ", new String[] { "Shutting down " + classname + " at " + hostname }));
            }
        }, 0);
    }
    
    public static String createStartupShutdownMessage(final String classname, final String hostname, final String[] args) {
        return toStartupShutdownString("STARTUP_MSG: ", new String[] { "Starting " + classname, "  host = " + hostname, "  args = " + ((args != null) ? Arrays.asList(args) : new ArrayList<String>()), "  version = " + VersionInfo.getVersion(), "  classpath = " + System.getProperty("java.class.path"), "  build = " + VersionInfo.getUrl() + " -r " + VersionInfo.getRevision() + "; compiled by '" + VersionInfo.getUser() + "' on " + VersionInfo.getDate(), "  java = " + System.getProperty("java.version") });
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
        return TraditionalBinaryPrefix.long2String(len, "B", 2);
    }
    
    @Deprecated
    public static String limitDecimalTo2(final double d) {
        return format("%.2f", d);
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
    
    public static String join(final char separator, final Iterable<?> strings) {
        return join(separator + "", strings);
    }
    
    public static String join(final CharSequence separator, final String[] strings) {
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (final String s : strings) {
            if (first) {
                first = false;
            }
            else {
                sb.append(separator);
            }
            sb.append(s);
        }
        return sb.toString();
    }
    
    public static String join(final char separator, final String[] strings) {
        return join(separator + "", strings);
    }
    
    public static String camelize(final String s) {
        final StringBuilder sb = new StringBuilder();
        final String[] split;
        final String[] words = split = split(toLowerCase(s), '\\', '_');
        for (final String word : split) {
            sb.append(org.apache.commons.lang3.StringUtils.capitalize(word));
        }
        return sb.toString();
    }
    
    public static String replaceTokens(final String template, final Pattern pattern, final Map<String, String> replacements) {
        final StringBuffer sb = new StringBuffer();
        final Matcher matcher = pattern.matcher(template);
        while (matcher.find()) {
            String replacement = replacements.get(matcher.group(1));
            if (replacement == null) {
                replacement = "";
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
    
    public static String getStackTrace(final Thread t) {
        final StackTraceElement[] stackTrace = t.getStackTrace();
        final StringBuilder str = new StringBuilder();
        for (final StackTraceElement e : stackTrace) {
            str.append(e.toString() + "\n");
        }
        return str.toString();
    }
    
    public static String popOptionWithArgument(final String name, final List<String> args) throws IllegalArgumentException {
        String val = null;
        final Iterator<String> iter = args.iterator();
        while (iter.hasNext()) {
            final String cur = iter.next();
            if (cur.equals("--")) {
                break;
            }
            if (!cur.equals(name)) {
                continue;
            }
            iter.remove();
            if (!iter.hasNext()) {
                throw new IllegalArgumentException("option " + name + " requires 1 argument.");
            }
            val = iter.next();
            iter.remove();
            break;
        }
        return val;
    }
    
    public static boolean popOption(final String name, final List<String> args) {
        final Iterator<String> iter = args.iterator();
        while (iter.hasNext()) {
            final String cur = iter.next();
            if (cur.equals("--")) {
                break;
            }
            if (cur.equals(name)) {
                iter.remove();
                return true;
            }
        }
        return false;
    }
    
    public static String popFirstNonOption(final List<String> args) {
        final Iterator<String> iter = args.iterator();
        while (iter.hasNext()) {
            String cur = iter.next();
            if (cur.equals("--")) {
                if (!iter.hasNext()) {
                    return null;
                }
                cur = iter.next();
                iter.remove();
                return cur;
            }
            else {
                if (!cur.startsWith("-")) {
                    iter.remove();
                    return cur;
                }
                continue;
            }
        }
        return null;
    }
    
    public static String toLowerCase(final String str) {
        return str.toLowerCase(Locale.ENGLISH);
    }
    
    public static String toUpperCase(final String str) {
        return str.toUpperCase(Locale.ENGLISH);
    }
    
    public static boolean equalsIgnoreCase(final String s1, final String s2) {
        Preconditions.checkNotNull(s1);
        return s1.equalsIgnoreCase(s2);
    }
    
    public static boolean isAlpha(final String str) {
        if (str == null) {
            return false;
        }
        for (int sz = str.length(), i = 0; i < sz; ++i) {
            if (!Character.isLetter(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static String wrap(final String str, int wrapLength, String newLineStr, final boolean wrapLongWords) {
        if (str == null) {
            return null;
        }
        if (newLineStr == null) {
            newLineStr = System.lineSeparator();
        }
        if (wrapLength < 1) {
            wrapLength = 1;
        }
        final int inputLineLength = str.length();
        int offset = 0;
        final StringBuffer wrappedLine = new StringBuffer(inputLineLength + 32);
        while (inputLineLength - offset > wrapLength) {
            if (str.charAt(offset) == ' ') {
                ++offset;
            }
            else {
                int spaceToWrapAt = str.lastIndexOf(32, wrapLength + offset);
                if (spaceToWrapAt >= offset) {
                    wrappedLine.append(str.substring(offset, spaceToWrapAt));
                    wrappedLine.append(newLineStr);
                    offset = spaceToWrapAt + 1;
                }
                else if (wrapLongWords) {
                    wrappedLine.append(str.substring(offset, wrapLength + offset));
                    wrappedLine.append(newLineStr);
                    offset += wrapLength;
                }
                else {
                    spaceToWrapAt = str.indexOf(32, wrapLength + offset);
                    if (spaceToWrapAt >= 0) {
                        wrappedLine.append(str.substring(offset, spaceToWrapAt));
                        wrappedLine.append(newLineStr);
                        offset = spaceToWrapAt + 1;
                    }
                    else {
                        wrappedLine.append(str.substring(offset));
                        offset = inputLineLength;
                    }
                }
            }
        }
        wrappedLine.append(str.substring(offset));
        return wrappedLine.toString();
    }
    
    static {
        SHELL_ENV_VAR_PATTERN = Pattern.compile("\\$([A-Za-z_]{1}[A-Za-z0-9_]*)");
        WIN_ENV_VAR_PATTERN = Pattern.compile("%(.*?)%");
        ENV_VAR_PATTERN = (Shell.WINDOWS ? StringUtils.WIN_ENV_VAR_PATTERN : StringUtils.SHELL_ENV_VAR_PATTERN);
        emptyStringArray = new String[0];
    }
    
    public enum TraditionalBinaryPrefix
    {
        KILO(10), 
        MEGA(TraditionalBinaryPrefix.KILO.bitShift + 10), 
        GIGA(TraditionalBinaryPrefix.MEGA.bitShift + 10), 
        TERA(TraditionalBinaryPrefix.GIGA.bitShift + 10), 
        PETA(TraditionalBinaryPrefix.TERA.bitShift + 10), 
        EXA(TraditionalBinaryPrefix.PETA.bitShift + 10);
        
        public final long value;
        public final char symbol;
        public final int bitShift;
        public final long bitMask;
        
        private TraditionalBinaryPrefix(final int bitShift) {
            this.bitShift = bitShift;
            this.value = 1L << bitShift;
            this.bitMask = this.value - 1L;
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
        
        public static String long2String(long n, String unit, final int decimalPlaces) {
            if (unit == null) {
                unit = "";
            }
            if (n == Long.MIN_VALUE) {
                return "-8 " + TraditionalBinaryPrefix.EXA.symbol + unit;
            }
            final StringBuilder b = new StringBuilder();
            if (n < 0L) {
                b.append('-');
                n = -n;
            }
            if (n < TraditionalBinaryPrefix.KILO.value) {
                b.append(n);
                return (unit.isEmpty() ? b : b.append(" ").append(unit)).toString();
            }
            int i;
            for (i = 0; i < values().length && n >= values()[i].value; ++i) {}
            TraditionalBinaryPrefix prefix = values()[i - 1];
            if ((n & prefix.bitMask) == 0x0L) {
                b.append(n >> prefix.bitShift);
            }
            else {
                final String format = "%." + decimalPlaces + "f";
                String s = StringUtils.format(format, n / (double)prefix.value);
                if (s.startsWith("1024")) {
                    prefix = values()[i];
                    s = StringUtils.format(format, n / (double)prefix.value);
                }
                b.append(s);
            }
            return b.append(' ').append(prefix.symbol).append(unit).toString();
        }
    }
}
