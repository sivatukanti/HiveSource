// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.xml;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;

public final class UtilLoggingEntityResolver implements EntityResolver
{
    public InputSource resolveEntity(final String publicId, final String systemId) {
        if (systemId.endsWith("logger.dtd")) {
            return new InputSource(new ByteArrayInputStream(new byte[0]));
        }
        return null;
    }
}
