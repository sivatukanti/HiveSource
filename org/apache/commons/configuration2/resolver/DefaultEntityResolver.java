// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.resolver;

import java.io.InputStream;
import java.net.URLConnection;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import java.util.HashMap;
import java.net.URL;
import java.util.Map;
import org.xml.sax.EntityResolver;

public class DefaultEntityResolver implements EntityResolver, EntityRegistry
{
    private final Map<String, URL> registeredEntities;
    
    public DefaultEntityResolver() {
        this.registeredEntities = new HashMap<String, URL>();
    }
    
    @Override
    public void registerEntityId(final String publicId, final URL entityURL) {
        if (publicId == null) {
            throw new IllegalArgumentException("Public ID must not be null!");
        }
        this.getRegisteredEntities().put(publicId, entityURL);
    }
    
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException {
        URL entityURL = null;
        if (publicId != null) {
            entityURL = this.getRegisteredEntities().get(publicId);
        }
        if (entityURL != null) {
            try {
                final URLConnection connection = entityURL.openConnection();
                connection.setUseCaches(false);
                final InputStream stream = connection.getInputStream();
                final InputSource source = new InputSource(stream);
                source.setSystemId(entityURL.toExternalForm());
                return source;
            }
            catch (IOException e) {
                throw new SAXException(e);
            }
        }
        return null;
    }
    
    @Override
    public Map<String, URL> getRegisteredEntities() {
        return this.registeredEntities;
    }
}
