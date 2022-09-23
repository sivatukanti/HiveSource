// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Set;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Hashtable;

public class Localiser
{
    private static boolean displayCodesInMessages;
    private static Hashtable<String, Localiser> helpers;
    private static Locale locale;
    private static Hashtable<String, MessageFormat> msgFormats;
    private ResourceBundle bundle;
    private ClassLoader loader;
    
    private Localiser(final String bundleName, final ClassLoader classLoader) {
        this.bundle = null;
        this.loader = null;
        try {
            this.loader = classLoader;
            this.bundle = ResourceBundle.getBundle(bundleName, Localiser.locale, classLoader);
        }
        catch (MissingResourceException mre) {
            NucleusLogger.GENERAL.error("ResourceBundle " + bundleName + " for locale " + Localiser.locale + " was not found!");
        }
    }
    
    public static synchronized void setLanguage(final String languageCode) {
        if (languageCode == null) {
            return;
        }
        if (Localiser.locale.getLanguage().equalsIgnoreCase(languageCode)) {
            return;
        }
        NucleusLogger.GENERAL.info("Setting localisation to " + languageCode + " from " + Localiser.locale.getLanguage());
        Localiser.locale = new Locale(languageCode);
        final Set<String> bundleNames = Localiser.helpers.keySet();
        for (final String bundleName : bundleNames) {
            final Localiser localiser = Localiser.helpers.get(bundleName);
            try {
                final ClassLoader loader = localiser.loader;
                localiser.bundle = ResourceBundle.getBundle(bundleName, Localiser.locale, loader);
            }
            catch (MissingResourceException mre) {
                NucleusLogger.GENERAL.error("ResourceBundle " + bundleName + " for locale " + Localiser.locale + " was not found!");
            }
        }
    }
    
    public static void setDisplayCodesInMessages(final boolean display) {
        NucleusLogger.GENERAL.info("Setting localisation codes display to " + display);
        Localiser.displayCodesInMessages = display;
    }
    
    public static Localiser getInstance(final String bundle_name, final ClassLoader class_loader) {
        Localiser localiser = Localiser.helpers.get(bundle_name);
        if (localiser != null) {
            return localiser;
        }
        localiser = new Localiser(bundle_name, class_loader);
        Localiser.helpers.put(bundle_name, localiser);
        return localiser;
    }
    
    public String msg(final boolean includeCode, final String messageKey) {
        return getMessage(includeCode, this.bundle, messageKey, null);
    }
    
    public String msg(final boolean includeCode, final String messageKey, final long arg) {
        final Object[] args = { String.valueOf(arg) };
        return getMessage(true, this.bundle, messageKey, args);
    }
    
    public String msg(final boolean includeCode, final String messageKey, final Object arg1) {
        final Object[] args = { arg1 };
        return getMessage(includeCode, this.bundle, messageKey, args);
    }
    
    public String msg(final boolean includeCode, final String messageKey, final Object arg1, final Object arg2) {
        final Object[] args = { arg1, arg2 };
        return getMessage(includeCode, this.bundle, messageKey, args);
    }
    
    public String msg(final boolean includeCode, final String messageKey, final Object arg1, final Object arg2, final Object arg3) {
        final Object[] args = { arg1, arg2, arg3 };
        return getMessage(includeCode, this.bundle, messageKey, args);
    }
    
    public String msg(final boolean includeCode, final String messageKey, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
        final Object[] args = { arg1, arg2, arg3, arg4 };
        return getMessage(includeCode, this.bundle, messageKey, args);
    }
    
    public String msg(final boolean includeCode, final String messageKey, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
        final Object[] args = { arg1, arg2, arg3, arg4, arg5 };
        return getMessage(includeCode, this.bundle, messageKey, args);
    }
    
    public String msg(final String messageKey) {
        return this.msg(true, messageKey);
    }
    
    public String msg(final String messageKey, final Object arg1) {
        return this.msg(true, messageKey, arg1);
    }
    
    public String msg(final String messageKey, final Object arg1, final Object arg2) {
        return this.msg(true, messageKey, arg1, arg2);
    }
    
    public String msg(final String messageKey, final Object arg1, final Object arg2, final Object arg3) {
        return this.msg(true, messageKey, arg1, arg2, arg3);
    }
    
    public String msg(final String messageKey, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
        return this.msg(true, messageKey, arg1, arg2, arg3, arg4);
    }
    
    public String msg(final String messageKey, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
        return this.msg(true, messageKey, arg1, arg2, arg3, arg4, arg5);
    }
    
    public String msg(final String messageKey, final long arg) {
        return this.msg(true, messageKey, arg);
    }
    
    private static final String getMessage(final boolean includeCode, final ResourceBundle bundle, final String messageKey, final Object[] msgArgs) {
        if (messageKey == null) {
            NucleusLogger.GENERAL.error("Attempt to retrieve resource with NULL name !");
            return null;
        }
        if (msgArgs != null) {
            for (int i = 0; i < msgArgs.length; ++i) {
                if (msgArgs[i] == null) {
                    msgArgs[i] = "";
                }
                if (Throwable.class.isAssignableFrom(msgArgs[i].getClass())) {
                    msgArgs[i] = getStringFromException((Throwable)msgArgs[i]);
                }
            }
        }
        try {
            String stringForKey = bundle.getString(messageKey);
            if (includeCode && Localiser.displayCodesInMessages) {
                final char c = messageKey.charAt(0);
                if (c >= '0' && c <= '9') {
                    stringForKey = "[DN-" + messageKey + "] " + stringForKey;
                }
            }
            if (msgArgs != null) {
                MessageFormat formatter = Localiser.msgFormats.get(stringForKey);
                if (formatter == null) {
                    formatter = new MessageFormat(stringForKey);
                    Localiser.msgFormats.put(stringForKey, formatter);
                }
                return formatter.format(msgArgs);
            }
            return stringForKey;
        }
        catch (MissingResourceException mre) {
            NucleusLogger.GENERAL.error("Parameter " + messageKey + " doesn't exist for bundle " + bundle);
            return null;
        }
    }
    
    private static String getStringFromException(final Throwable exception) {
        final StringBuilder msg = new StringBuilder();
        if (exception != null) {
            final StringWriter stringWriter = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(stringWriter);
            exception.printStackTrace(printWriter);
            printWriter.close();
            try {
                stringWriter.close();
            }
            catch (Exception ex) {}
            msg.append(exception.getMessage());
            msg.append('\n');
            msg.append(stringWriter.toString());
            if (exception instanceof SQLException) {
                if (((SQLException)exception).getNextException() != null) {
                    msg.append('\n');
                    msg.append(getStringFromException(((SQLException)exception).getNextException()));
                }
            }
            else if (exception instanceof InvocationTargetException && ((InvocationTargetException)exception).getTargetException() != null) {
                msg.append('\n');
                msg.append(getStringFromException(((InvocationTargetException)exception).getTargetException()));
            }
        }
        return msg.toString();
    }
    
    static {
        Localiser.displayCodesInMessages = false;
        Localiser.helpers = new Hashtable<String, Localiser>();
        Localiser.locale = Locale.getDefault();
        Localiser.msgFormats = new Hashtable<String, MessageFormat>();
    }
}
