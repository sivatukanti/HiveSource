// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.istack.localization;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.Locale;

public class Localizer
{
    private final Locale _locale;
    private final HashMap _resourceBundles;
    
    public Localizer() {
        this(Locale.getDefault());
    }
    
    public Localizer(final Locale l) {
        this._locale = l;
        this._resourceBundles = new HashMap();
    }
    
    public Locale getLocale() {
        return this._locale;
    }
    
    public String localize(final Localizable l) {
        String key = l.getKey();
        if (key == Localizable.NOT_LOCALIZABLE) {
            return (String)l.getArguments()[0];
        }
        final String bundlename = l.getResourceBundleName();
        try {
            ResourceBundle bundle = this._resourceBundles.get(bundlename);
            if (bundle == null) {
                try {
                    bundle = ResourceBundle.getBundle(bundlename, this._locale);
                }
                catch (MissingResourceException e) {
                    final int i = bundlename.lastIndexOf(46);
                    if (i != -1) {
                        final String alternateBundleName = bundlename.substring(i + 1);
                        try {
                            bundle = ResourceBundle.getBundle(alternateBundleName, this._locale);
                        }
                        catch (MissingResourceException e2) {
                            return this.getDefaultMessage(l);
                        }
                    }
                }
                this._resourceBundles.put(bundlename, bundle);
            }
            if (bundle == null) {
                return this.getDefaultMessage(l);
            }
            if (key == null) {
                key = "undefined";
            }
            String msg;
            try {
                msg = bundle.getString(key);
            }
            catch (MissingResourceException e3) {
                msg = bundle.getString("undefined");
            }
            final Object[] args = l.getArguments();
            for (int j = 0; j < args.length; ++j) {
                if (args[j] instanceof Localizable) {
                    args[j] = this.localize((Localizable)args[j]);
                }
            }
            final String message = MessageFormat.format(msg, args);
            return message;
        }
        catch (MissingResourceException e4) {
            return this.getDefaultMessage(l);
        }
    }
    
    private String getDefaultMessage(final Localizable l) {
        final String key = l.getKey();
        final Object[] args = l.getArguments();
        final StringBuilder sb = new StringBuilder();
        sb.append("[failed to localize] ");
        sb.append(key);
        if (args != null) {
            sb.append('(');
            for (int i = 0; i < args.length; ++i) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(String.valueOf(args[i]));
            }
            sb.append(')');
        }
        return sb.toString();
    }
}
