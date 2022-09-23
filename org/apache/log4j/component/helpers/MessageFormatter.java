// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.component.helpers;

public final class MessageFormatter
{
    private static final char DELIM_START = '{';
    private static final char DELIM_STOP = '}';
    
    private MessageFormatter() {
    }
    
    public static String format(final String messagePattern, final Object argument) {
        final int j = messagePattern.indexOf(123);
        final int len = messagePattern.length();
        char escape = 'x';
        if (j == -1 || j + 1 == len) {
            return messagePattern;
        }
        final char delimStop = messagePattern.charAt(j + 1);
        if (j > 0) {
            escape = messagePattern.charAt(j - 1);
        }
        if (delimStop != '}' || escape == '\\') {
            return messagePattern;
        }
        final StringBuffer sbuf = new StringBuffer(len + 20);
        sbuf.append(messagePattern.substring(0, j));
        sbuf.append(argument);
        sbuf.append(messagePattern.substring(j + 2));
        return sbuf.toString();
    }
    
    public static String format(final String messagePattern, final Object arg1, final Object arg2) {
        int i = 0;
        final int len = messagePattern.length();
        final StringBuffer sbuf = new StringBuffer(messagePattern.length() + 50);
        int l = 0;
        while (l < 2) {
            final int j = messagePattern.indexOf(123, i);
            if (j == -1 || j + 1 == len) {
                if (i == 0) {
                    return messagePattern;
                }
                sbuf.append(messagePattern.substring(i, messagePattern.length()));
                return sbuf.toString();
            }
            else {
                final char delimStop = messagePattern.charAt(j + 1);
                if (delimStop != '}') {
                    sbuf.append(messagePattern.substring(i, messagePattern.length()));
                    return sbuf.toString();
                }
                sbuf.append(messagePattern.substring(i, j));
                if (l == 0) {
                    sbuf.append(arg1);
                }
                else {
                    sbuf.append(arg2);
                }
                i = j + 2;
                ++l;
            }
        }
        sbuf.append(messagePattern.substring(i, messagePattern.length()));
        return sbuf.toString();
    }
}
