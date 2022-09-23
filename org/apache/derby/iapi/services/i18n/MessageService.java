// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.i18n;

import java.text.MessageFormat;
import org.apache.derby.iapi.error.ShutdownException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Locale;

public final class MessageService
{
    private static final Locale EN;
    private static BundleFinder finder;
    
    private MessageService() {
    }
    
    public static ResourceBundle getBundleForLocale(final Locale locale, final String s) {
        try {
            return getBundleWithEnDefault("org.apache.derby.loc.m" + hashString50(s), locale);
        }
        catch (MissingResourceException ex) {
            return null;
        }
    }
    
    public static void setFinder(final BundleFinder finder) {
        MessageService.finder = finder;
    }
    
    public static String getTextMessage(final String s) {
        return getCompleteMessage(s, null);
    }
    
    public static String getTextMessage(final String s, final Object o) {
        return getCompleteMessage(s, new Object[] { o });
    }
    
    public static String getTextMessage(final String s, final Object o, final Object o2) {
        return getCompleteMessage(s, new Object[] { o, o2 });
    }
    
    public static String getTextMessage(final String s, final Object o, final Object o2, final Object o3) {
        return getCompleteMessage(s, new Object[] { o, o2, o3 });
    }
    
    public static String getTextMessage(final String s, final Object o, final Object o2, final Object o3, final Object o4) {
        return getCompleteMessage(s, new Object[] { o, o2, o3, o4 });
    }
    
    public static String getTextMessage(final String s, final Object o, final Object o2, final Object o3, final Object o4, final Object o5) {
        return getCompleteMessage(s, new Object[] { o, o2, o3, o4, o5 });
    }
    
    public static String getTextMessage(final String s, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9, final Object o10, final Object o11, final Object o12, final Object o13, final Object o14) {
        return getCompleteMessage(s, new Object[] { o, o2, o3, o4, o5, o6, o7, o8, o9, o10, o11, o12, o13, o14 });
    }
    
    public static String getCompleteMessage(final String s, final Object[] array) {
        try {
            return formatMessage(getBundle(s), s, array, true);
        }
        catch (MissingResourceException ex) {}
        catch (ShutdownException ex2) {}
        return formatMessage(getBundleForLocale(MessageService.EN, s), s, array, false);
    }
    
    public static void getLocalizedMessage(final int n, final short n2, final String s, final String s2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final String s3, final String s4, final String s5, final String s6, final String[] array, final int[] array2) {
        final int index = s6.indexOf("_");
        Locale en = MessageService.EN;
        if (index != -1) {
            final int lastIndex = s6.lastIndexOf("_");
            final String substring = s6.substring(0, index);
            if (lastIndex == index) {
                en = new Locale(substring, s6.substring(index + 1));
            }
            else {
                en = new Locale(substring, s6.substring(index + 1, lastIndex), s6.substring(lastIndex + 1));
            }
        }
        String s7 = s4;
        Object[] array3 = null;
        if (s != null && s.length() > 0) {
            final char[] charArray = s.toCharArray();
            int n9 = 0;
            int n10 = -1;
            for (int i = 0; i < charArray.length; ++i) {
                if (charArray[i] == '\u0014') {
                    ++n9;
                    n10 = i;
                }
            }
            if (n9 == 0) {
                s7 = new String(charArray);
            }
            else {
                s7 = new String(charArray, n10 + 1, charArray.length - n10 - 1);
                array3 = new Object[n9];
                int offset = 0;
                int n11 = 0;
                for (int j = 0; j < n10 + 1; ++j) {
                    if (j == n10 || charArray[j] == '\u0014') {
                        array3[n11++] = new String(charArray, offset, j - offset);
                        offset = j + 1;
                    }
                }
            }
        }
        try {
            array[0] = formatMessage(getBundleForLocale(en, s7), s7, array3, true);
            array2[0] = 0;
            return;
        }
        catch (MissingResourceException ex) {}
        catch (ShutdownException ex2) {}
        array[0] = formatMessage(getBundleForLocale(MessageService.EN, s7), s7, array3, false);
        array2[0] = 0;
    }
    
    public static String getLocalizedMessage(final Locale locale, final String s, final Object[] array) {
        try {
            return formatMessage(getBundleForLocale(locale, s), s, array, true);
        }
        catch (MissingResourceException ex) {}
        catch (ShutdownException ex2) {}
        return formatMessage(getBundleForLocale(MessageService.EN, s), s, array, false);
    }
    
    public static String getProperty(final String s, final String str) {
        final ResourceBundle bundle = getBundle(s);
        try {
            if (bundle != null) {
                return bundle.getString(s.concat(".").concat(str));
            }
        }
        catch (MissingResourceException ex) {}
        return null;
    }
    
    public static String formatMessage(final ResourceBundle resourceBundle, String string, Object[] arguments, final boolean b) {
        if (arguments == null) {
            arguments = new Object[0];
        }
        if (resourceBundle != null) {
            try {
                string = resourceBundle.getString(string);
                try {
                    return MessageFormat.format(string, arguments);
                }
                catch (IllegalArgumentException ex2) {}
                catch (NullPointerException ex3) {}
            }
            catch (MissingResourceException ex) {
                if (b) {
                    throw ex;
                }
            }
        }
        if (string == null) {
            string = "UNKNOWN";
        }
        final StringBuffer sb = new StringBuffer(string);
        final int length = arguments.length;
        if (length > 0) {
            sb.append(" : ");
        }
        for (int i = 0; i < length; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append('[');
            sb.append(i);
            sb.append("] ");
            if (arguments[i] == null) {
                sb.append("null");
            }
            else {
                sb.append(arguments[i].toString());
            }
        }
        return sb.toString();
    }
    
    private static ResourceBundle getBundle(final String s) {
        ResourceBundle resourceBundle = null;
        if (MessageService.finder != null) {
            resourceBundle = MessageService.finder.getBundle(s);
        }
        if (resourceBundle == null) {
            resourceBundle = getBundleForLocale(Locale.getDefault(), s);
        }
        return resourceBundle;
    }
    
    public static ResourceBundle getBundleWithEnDefault(final String s, final Locale locale) {
        try {
            return ResourceBundle.getBundle(s, locale);
        }
        catch (MissingResourceException ex) {
            return ResourceBundle.getBundle(s, MessageService.EN);
        }
    }
    
    public static int hashString50(final String s) {
        int n = 0;
        int length = s.length();
        if (length > 5) {
            length = 5;
        }
        for (int i = 0; i < length; ++i) {
            n += s.charAt(i);
        }
        return n % '2';
    }
    
    static {
        EN = new Locale("en", "US");
    }
}
