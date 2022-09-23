// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.util;

import org.datanucleus.ClassConstants;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import org.xml.sax.SAXException;
import java.net.URL;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import org.xml.sax.InputSource;
import java.util.HashMap;
import org.xml.sax.EntityResolver;

public abstract class AbstractXMLEntityResolver implements EntityResolver
{
    protected static final Localiser LOCALISER;
    protected HashMap publicIdEntities;
    protected HashMap systemIdEntities;
    
    public AbstractXMLEntityResolver() {
        this.publicIdEntities = new HashMap();
        this.systemIdEntities = new HashMap();
    }
    
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException {
        try {
            if (publicId != null) {
                final String internalEntity = this.publicIdEntities.get(publicId);
                if (internalEntity != null) {
                    return this.getLocalInputSource(publicId, systemId, internalEntity);
                }
            }
            if (systemId != null) {
                final String internalEntity = this.systemIdEntities.get(systemId);
                if (internalEntity != null) {
                    return this.getLocalInputSource(publicId, systemId, internalEntity);
                }
                if (systemId.startsWith("file://")) {
                    final String localPath = systemId.substring(7);
                    final File file = new File(localPath);
                    if (file.exists()) {
                        if (NucleusLogger.METADATA.isDebugEnabled()) {
                            NucleusLogger.METADATA.debug(AbstractXMLEntityResolver.LOCALISER.msg("028001", publicId, systemId));
                        }
                        final FileInputStream in = new FileInputStream(file);
                        return new InputSource(in);
                    }
                    return null;
                }
                else {
                    if (systemId.startsWith("file:")) {
                        return this.getLocalInputSource(publicId, systemId, systemId.substring(5));
                    }
                    if (systemId.startsWith("http:")) {
                        try {
                            if (NucleusLogger.METADATA.isDebugEnabled()) {
                                NucleusLogger.METADATA.debug(AbstractXMLEntityResolver.LOCALISER.msg("028001", publicId, systemId));
                            }
                            final URL url = new URL(systemId);
                            final InputStream url_stream = url.openStream();
                            return new InputSource(url_stream);
                        }
                        catch (Exception e) {
                            NucleusLogger.METADATA.error(e);
                        }
                    }
                }
            }
            NucleusLogger.METADATA.error(AbstractXMLEntityResolver.LOCALISER.msg("028002", publicId, systemId));
            return null;
        }
        catch (Exception e2) {
            NucleusLogger.METADATA.error(AbstractXMLEntityResolver.LOCALISER.msg("028003", publicId, systemId), e2);
            throw new SAXException(e2.getMessage(), e2);
        }
    }
    
    protected InputSource getLocalInputSource(final String publicId, final String systemId, final String localPath) throws FileNotFoundException {
        if (NucleusLogger.METADATA.isDebugEnabled()) {
            NucleusLogger.METADATA.debug(AbstractXMLEntityResolver.LOCALISER.msg("028000", publicId, systemId, localPath));
        }
        final InputStream in = AbstractXMLEntityResolver.class.getResourceAsStream(localPath);
        if (in == null) {
            NucleusLogger.METADATA.fatal("local resource \"" + localPath + "\" does not exist!!!");
            throw new FileNotFoundException("Unable to load resource: " + localPath);
        }
        return new InputSource(new InputStreamReader(in));
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
