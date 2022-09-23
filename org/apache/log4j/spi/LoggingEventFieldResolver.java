// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.spi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.Locale;
import org.apache.log4j.rule.InFixToPostFix;
import java.util.List;

public final class LoggingEventFieldResolver
{
    public static final List KEYWORD_LIST;
    public static final String LOGGER_FIELD = "LOGGER";
    public static final String LEVEL_FIELD = "LEVEL";
    public static final String CLASS_FIELD = "CLASS";
    public static final String FILE_FIELD = "FILE";
    public static final String LINE_FIELD = "LINE";
    public static final String METHOD_FIELD = "METHOD";
    public static final String MSG_FIELD = "MSG";
    public static final String NDC_FIELD = "NDC";
    public static final String EXCEPTION_FIELD = "EXCEPTION";
    public static final String TIMESTAMP_FIELD = "TIMESTAMP";
    public static final String THREAD_FIELD = "THREAD";
    public static final String PROP_FIELD = "PROP.";
    public static final String EMPTY_STRING = "";
    private static final LoggingEventFieldResolver RESOLVER;
    
    private LoggingEventFieldResolver() {
        LoggingEventFieldResolver.KEYWORD_LIST.add("LOGGER");
        LoggingEventFieldResolver.KEYWORD_LIST.add("LEVEL");
        LoggingEventFieldResolver.KEYWORD_LIST.add("CLASS");
        LoggingEventFieldResolver.KEYWORD_LIST.add("FILE");
        LoggingEventFieldResolver.KEYWORD_LIST.add("LINE");
        LoggingEventFieldResolver.KEYWORD_LIST.add("METHOD");
        LoggingEventFieldResolver.KEYWORD_LIST.add("MSG");
        LoggingEventFieldResolver.KEYWORD_LIST.add("NDC");
        LoggingEventFieldResolver.KEYWORD_LIST.add("EXCEPTION");
        LoggingEventFieldResolver.KEYWORD_LIST.add("TIMESTAMP");
        LoggingEventFieldResolver.KEYWORD_LIST.add("THREAD");
        LoggingEventFieldResolver.KEYWORD_LIST.add("PROP.");
    }
    
    public String applyFields(final String replaceText, final LoggingEvent event) {
        if (replaceText == null) {
            return null;
        }
        final InFixToPostFix.CustomTokenizer tokenizer = new InFixToPostFix.CustomTokenizer(replaceText);
        final StringBuffer result = new StringBuffer();
        boolean found = false;
        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken();
            if (this.isField(token) || token.toUpperCase(Locale.US).startsWith("PROP.")) {
                result.append(this.getValue(token, event).toString());
                found = true;
            }
            else {
                result.append(token);
            }
        }
        if (found) {
            return result.toString();
        }
        return null;
    }
    
    public static LoggingEventFieldResolver getInstance() {
        return LoggingEventFieldResolver.RESOLVER;
    }
    
    public boolean isField(final String fieldName) {
        return fieldName != null && (LoggingEventFieldResolver.KEYWORD_LIST.contains(fieldName.toUpperCase(Locale.US)) || fieldName.toUpperCase().startsWith("PROP."));
    }
    
    public Object getValue(final String fieldName, final LoggingEvent event) {
        final String upperField = fieldName.toUpperCase(Locale.US);
        if ("LOGGER".equals(upperField)) {
            return event.getLoggerName();
        }
        if ("LEVEL".equals(upperField)) {
            return event.getLevel();
        }
        if ("MSG".equals(upperField)) {
            return event.getMessage();
        }
        if ("NDC".equals(upperField)) {
            final String ndcValue = event.getNDC();
            return (ndcValue == null) ? "" : ndcValue;
        }
        if ("EXCEPTION".equals(upperField)) {
            final String[] throwableRep = event.getThrowableStrRep();
            if (throwableRep == null) {
                return "";
            }
            return getExceptionMessage(throwableRep);
        }
        else {
            if ("TIMESTAMP".equals(upperField)) {
                return new Long(event.timeStamp);
            }
            if ("THREAD".equals(upperField)) {
                return event.getThreadName();
            }
            if (upperField.startsWith("PROP.")) {
                Object propValue = event.getMDC(fieldName.substring(5));
                if (propValue == null) {
                    final String lowerPropKey = fieldName.substring(5).toLowerCase();
                    final Set entrySet = event.getProperties().entrySet();
                    for (final Map.Entry thisEntry : entrySet) {
                        if (thisEntry.getKey().toString().equalsIgnoreCase(lowerPropKey)) {
                            propValue = thisEntry.getValue();
                        }
                    }
                }
                return (propValue == null) ? "" : propValue.toString();
            }
            final LocationInfo info = event.getLocationInformation();
            if ("CLASS".equals(upperField)) {
                return (info == null) ? "" : info.getClassName();
            }
            if ("FILE".equals(upperField)) {
                return (info == null) ? "" : info.getFileName();
            }
            if ("LINE".equals(upperField)) {
                return (info == null) ? "" : info.getLineNumber();
            }
            if ("METHOD".equals(upperField)) {
                return (info == null) ? "" : info.getMethodName();
            }
            throw new IllegalArgumentException("Unsupported field name: " + fieldName);
        }
    }
    
    private static String getExceptionMessage(final String[] exception) {
        final StringBuffer buff = new StringBuffer();
        for (int i = 0; i < exception.length; ++i) {
            buff.append(exception[i]);
        }
        return buff.toString();
    }
    
    static {
        KEYWORD_LIST = new ArrayList();
        RESOLVER = new LoggingEventFieldResolver();
    }
}
