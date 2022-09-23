// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.spi;

import java.text.MessageFormat;
import javax.jdo.JDOFatalInternalException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Hashtable;

public class I18NHelper
{
    private static Hashtable<String, ResourceBundle> bundles;
    private static Hashtable<String, I18NHelper> helpers;
    private static Locale locale;
    private ResourceBundle bundle;
    private Throwable failure;
    private static final String bundleSuffix = ".Bundle";
    
    private I18NHelper() {
        this.bundle = null;
        this.failure = null;
    }
    
    private I18NHelper(final String bundleName, final ClassLoader loader) {
        this.bundle = null;
        this.failure = null;
        try {
            this.bundle = loadBundle(bundleName, loader);
        }
        catch (Throwable e) {
            this.failure = e;
        }
    }
    
    public static I18NHelper getInstance(final String bundleName) {
        return getInstance(bundleName, I18NHelper.class.getClassLoader());
    }
    
    public static I18NHelper getInstance(final Class cls) {
        final ClassLoader classLoader = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                return cls.getClassLoader();
            }
        });
        final String bundle = getPackageName(cls.getName()) + ".Bundle";
        return getInstance(bundle, classLoader);
    }
    
    public static I18NHelper getInstance(final String bundleName, final ClassLoader loader) {
        I18NHelper helper = I18NHelper.helpers.get(bundleName);
        if (helper != null) {
            return helper;
        }
        helper = new I18NHelper(bundleName, loader);
        I18NHelper.helpers.put(bundleName, helper);
        return I18NHelper.helpers.get(bundleName);
    }
    
    public String msg(final String messageKey) {
        this.assertBundle(messageKey);
        return getMessage(this.bundle, messageKey);
    }
    
    public String msg(final String messageKey, final Object arg1) {
        this.assertBundle(messageKey);
        return getMessage(this.bundle, messageKey, arg1);
    }
    
    public String msg(final String messageKey, final Object arg1, final Object arg2) {
        this.assertBundle(messageKey);
        return getMessage(this.bundle, messageKey, arg1, arg2);
    }
    
    public String msg(final String messageKey, final Object arg1, final Object arg2, final Object arg3) {
        this.assertBundle(messageKey);
        return getMessage(this.bundle, messageKey, arg1, arg2, arg3);
    }
    
    public String msg(final String messageKey, final Object[] args) {
        this.assertBundle(messageKey);
        return getMessage(this.bundle, messageKey, args);
    }
    
    public String msg(final String messageKey, final int arg) {
        this.assertBundle(messageKey);
        return getMessage(this.bundle, messageKey, arg);
    }
    
    public String msg(final String messageKey, final boolean arg) {
        this.assertBundle(messageKey);
        return getMessage(this.bundle, messageKey, arg);
    }
    
    public ResourceBundle getResourceBundle() {
        this.assertBundle();
        return this.bundle;
    }
    
    private static final ResourceBundle loadBundle(final String bundleName, final ClassLoader loader) {
        ResourceBundle messages = I18NHelper.bundles.get(bundleName);
        if (messages == null) {
            if (loader != null) {
                messages = ResourceBundle.getBundle(bundleName, I18NHelper.locale, loader);
            }
            else {
                messages = ResourceBundle.getBundle(bundleName, I18NHelper.locale, getSystemClassLoaderPrivileged());
            }
            I18NHelper.bundles.put(bundleName, messages);
        }
        return messages;
    }
    
    private void assertBundle() {
        if (this.failure != null) {
            throw new JDOFatalInternalException("No resources could be found for bundle:\"" + this.bundle + "\" ", this.failure);
        }
    }
    
    private void assertBundle(final String key) {
        if (this.failure != null) {
            throw new JDOFatalInternalException("No resources could be found to annotate error message key:\"" + key + "\"", this.failure);
        }
    }
    
    private static final String getMessage(final ResourceBundle messages, final String messageKey) {
        return messages.getString(messageKey);
    }
    
    private static final String getMessage(final ResourceBundle messages, final String messageKey, final Object[] msgArgs) {
        for (int i = 0; i < msgArgs.length; ++i) {
            if (msgArgs[i] == null) {
                msgArgs[i] = "";
            }
        }
        final MessageFormat formatter = new MessageFormat(messages.getString(messageKey));
        return formatter.format(msgArgs);
    }
    
    private static final String getMessage(final ResourceBundle messages, final String messageKey, final Object arg) {
        final Object[] args = { arg };
        return getMessage(messages, messageKey, args);
    }
    
    private static final String getMessage(final ResourceBundle messages, final String messageKey, final Object arg1, final Object arg2) {
        final Object[] args = { arg1, arg2 };
        return getMessage(messages, messageKey, args);
    }
    
    private static final String getMessage(final ResourceBundle messages, final String messageKey, final Object arg1, final Object arg2, final Object arg3) {
        final Object[] args = { arg1, arg2, arg3 };
        return getMessage(messages, messageKey, args);
    }
    
    private static final String getMessage(final ResourceBundle messages, final String messageKey, final int arg) {
        final Object[] args = { new Integer(arg) };
        return getMessage(messages, messageKey, args);
    }
    
    private static final String getMessage(final ResourceBundle messages, final String messageKey, final boolean arg) {
        final Object[] args = { String.valueOf(arg) };
        return getMessage(messages, messageKey, args);
    }
    
    private static final String getPackageName(final String className) {
        final int index = className.lastIndexOf(46);
        return (index != -1) ? className.substring(0, index) : "";
    }
    
    private static ClassLoader getSystemClassLoaderPrivileged() {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                return ClassLoader.getSystemClassLoader();
            }
        });
    }
    
    static {
        I18NHelper.bundles = new Hashtable<String, ResourceBundle>();
        I18NHelper.helpers = new Hashtable<String, I18NHelper>();
        I18NHelper.locale = Locale.getDefault();
    }
}
