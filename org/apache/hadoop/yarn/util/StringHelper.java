// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util;

import java.util.regex.Pattern;
import com.google.common.base.Splitter;
import com.google.common.base.Joiner;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public final class StringHelper
{
    public static final Joiner SSV_JOINER;
    public static final Joiner CSV_JOINER;
    public static final Joiner JOINER;
    public static final Joiner _JOINER;
    public static final Joiner PATH_JOINER;
    public static final Joiner PATH_ARG_JOINER;
    public static final Joiner DOT_JOINER;
    public static final Splitter SSV_SPLITTER;
    public static final Splitter _SPLITTER;
    private static final Pattern ABS_URL_RE;
    
    public static String sjoin(final Object... args) {
        return StringHelper.SSV_JOINER.join(args);
    }
    
    public static String cjoin(final Object... args) {
        return StringHelper.CSV_JOINER.join(args);
    }
    
    public static String djoin(final Object... args) {
        return StringHelper.DOT_JOINER.join(args);
    }
    
    public static String _join(final Object... args) {
        return StringHelper._JOINER.join(args);
    }
    
    public static String pjoin(final Object... args) {
        return StringHelper.PATH_JOINER.join(args);
    }
    
    public static String pajoin(final Object... args) {
        return StringHelper.PATH_ARG_JOINER.join(args);
    }
    
    public static String join(final Object... args) {
        return StringHelper.JOINER.join(args);
    }
    
    public static String joins(final String sep, final Object... args) {
        return Joiner.on(sep).join(args);
    }
    
    public static Iterable<String> split(final CharSequence s) {
        return StringHelper.SSV_SPLITTER.split(s);
    }
    
    public static Iterable<String> _split(final CharSequence s) {
        return StringHelper._SPLITTER.split(s);
    }
    
    public static boolean isAbsUrl(final CharSequence url) {
        return StringHelper.ABS_URL_RE.matcher(url).find();
    }
    
    public static String ujoin(final String pathPrefix, final String... args) {
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (final String part : args) {
            if (first) {
                first = false;
                if (part.startsWith("#") || isAbsUrl(part)) {
                    sb.append(part);
                }
                else {
                    uappend(sb, pathPrefix);
                    uappend(sb, part);
                }
            }
            else {
                uappend(sb, part);
            }
        }
        return sb.toString();
    }
    
    private static void uappend(final StringBuilder sb, final String part) {
        if ((sb.length() <= 0 || sb.charAt(sb.length() - 1) != '/') && !part.startsWith("/")) {
            sb.append('/');
        }
        sb.append(part);
    }
    
    public static String percent(final double value) {
        return String.format("%.2f", value * 100.0);
    }
    
    static {
        SSV_JOINER = Joiner.on(' ');
        CSV_JOINER = Joiner.on(',');
        JOINER = Joiner.on("");
        _JOINER = Joiner.on('_');
        PATH_JOINER = Joiner.on('/');
        PATH_ARG_JOINER = Joiner.on("/:");
        DOT_JOINER = Joiner.on('.');
        SSV_SPLITTER = Splitter.on(' ').omitEmptyStrings().trimResults();
        _SPLITTER = Splitter.on('_').trimResults();
        ABS_URL_RE = Pattern.compile("^(?:\\w+:)?//");
    }
}
