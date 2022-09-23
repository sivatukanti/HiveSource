// 
// Decompiled by Procyon v0.5.36
// 

package com.automationanywhere.commandsdk.i18n;

import java.io.IOException;
import java.net.URLConnection;
import java.net.URL;
import java.io.InputStream;
import java.io.Reader;
import java.util.PropertyResourceBundle;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.ResourceBundle;

public class UTF8Control extends ResourceBundle.Control
{
    @Override
    public ResourceBundle newBundle(final String baseName, final Locale locale, final String format, final ClassLoader loader, final boolean reload) throws IllegalAccessException, InstantiationException, IOException {
        final String bundleName = this.toBundleName(baseName, locale);
        final String resourceName = this.toResourceName(bundleName, "properties");
        ResourceBundle bundle = null;
        InputStream stream = null;
        if (reload) {
            final URL url = loader.getResource(resourceName);
            if (url != null) {
                final URLConnection connection = url.openConnection();
                if (connection != null) {
                    connection.setUseCaches(false);
                    stream = connection.getInputStream();
                }
            }
        }
        else {
            stream = loader.getResourceAsStream(resourceName);
        }
        if (stream != null) {
            try {
                bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF8"));
            }
            finally {
                stream.close();
            }
        }
        return bundle;
    }
}
