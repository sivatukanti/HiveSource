// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata.xml;

import java.util.Set;
import org.datanucleus.plugin.ConfigurationElement;
import javax.xml.transform.stream.StreamSource;
import java.util.HashSet;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import org.datanucleus.metadata.InvalidMetaDataException;
import org.xml.sax.SAXException;
import org.datanucleus.exceptions.NucleusUserException;
import org.xml.sax.EntityResolver;
import org.datanucleus.ClassConstants;
import org.datanucleus.util.EntityResolverFactory;
import javax.xml.validation.SchemaFactory;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import org.datanucleus.util.StringUtils;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.metadata.MetaData;
import java.net.URL;
import javax.xml.parsers.SAXParser;
import org.datanucleus.plugin.PluginManager;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.util.Localiser;
import org.xml.sax.helpers.DefaultHandler;

public class MetaDataParser extends DefaultHandler
{
    protected static final Localiser LOCALISER;
    protected final MetaDataManager mgr;
    protected final PluginManager pluginMgr;
    protected final boolean validate;
    protected boolean namespaceAware;
    SAXParser parser;
    
    public MetaDataParser(final MetaDataManager mgr, final PluginManager pluginMgr, final boolean validate) {
        this.namespaceAware = true;
        this.parser = null;
        this.mgr = mgr;
        this.pluginMgr = pluginMgr;
        this.validate = validate;
    }
    
    public void setNamespaceAware(final boolean aware) {
        if (this.namespaceAware != aware) {
            this.parser = null;
        }
        this.namespaceAware = aware;
    }
    
    public MetaData parseMetaDataURL(final URL url, final String handlerName) {
        if (url == null) {
            final String msg = MetaDataParser.LOCALISER.msg("044031");
            NucleusLogger.METADATA.error(msg);
            throw new NucleusException(msg);
        }
        InputStream in = null;
        try {
            in = url.openStream();
        }
        catch (Exception ex) {}
        if (in == null) {
            try {
                in = new FileInputStream(StringUtils.getFileForFilename(url.getFile()));
            }
            catch (Exception ex2) {}
        }
        if (in == null) {
            NucleusLogger.METADATA.error(MetaDataParser.LOCALISER.msg("044032", url.toString()));
            throw new NucleusException(MetaDataParser.LOCALISER.msg("044032", url.toString()));
        }
        return this.parseMetaDataStream(in, url.toString(), handlerName);
    }
    
    public MetaData parseMetaDataFile(final String fileName, final String handlerName) {
        InputStream in = null;
        try {
            in = new URL(fileName).openStream();
        }
        catch (Exception ex) {}
        if (in == null) {
            try {
                in = new FileInputStream(StringUtils.getFileForFilename(fileName));
            }
            catch (Exception ex2) {}
        }
        if (in == null) {
            NucleusLogger.METADATA.error(MetaDataParser.LOCALISER.msg("044032", fileName));
            throw new NucleusException(MetaDataParser.LOCALISER.msg("044032", fileName));
        }
        return this.parseMetaDataStream(in, fileName, handlerName);
    }
    
    public synchronized MetaData parseMetaDataStream(final InputStream in, final String filename, final String handlerName) {
        if (in == null) {
            throw new NullPointerException("input stream is null");
        }
        Label_0053: {
            if (!NucleusLogger.METADATA.isDebugEnabled()) {
                break Label_0053;
            }
            NucleusLogger.METADATA.debug(MetaDataParser.LOCALISER.msg("044030", filename, handlerName, this.validate ? "true" : "false"));
            try {
                if (this.parser == null) {
                    final SAXParserFactory factory = SAXParserFactory.newInstance();
                    factory.setValidating(this.validate);
                    factory.setNamespaceAware(this.namespaceAware);
                    if (this.validate) {
                        try {
                            final Schema schema = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema").newSchema(this.getRegisteredSchemas(this.pluginMgr));
                            if (schema != null) {
                                try {
                                    factory.setSchema(schema);
                                }
                                catch (UnsupportedOperationException e) {
                                    NucleusLogger.METADATA.info(e.getMessage());
                                }
                            }
                        }
                        catch (Exception e2) {
                            NucleusLogger.METADATA.info(e2.getMessage());
                        }
                        try {
                            factory.setFeature("http://apache.org/xml/features/validation/schema", true);
                        }
                        catch (Exception e2) {
                            NucleusLogger.METADATA.info(e2.getMessage());
                        }
                    }
                    this.parser = factory.newSAXParser();
                }
                DefaultHandler handler = null;
                EntityResolver entityResolver = null;
                try {
                    entityResolver = EntityResolverFactory.getInstance(this.pluginMgr, handlerName);
                    if (entityResolver != null) {
                        this.parser.getXMLReader().setEntityResolver(entityResolver);
                    }
                    final Class[] argTypes = { ClassConstants.METADATA_MANAGER, String.class, EntityResolver.class };
                    final Object[] argValues = { this.mgr, filename, entityResolver };
                    handler = (DefaultHandler)this.pluginMgr.createExecutableExtension("org.datanucleus.metadata_handler", "name", handlerName, "class-name", argTypes, argValues);
                    if (handler == null) {
                        throw new NucleusUserException(MetaDataParser.LOCALISER.msg("044028", handlerName)).setFatal();
                    }
                }
                catch (Exception e3) {
                    final String msg = MetaDataParser.LOCALISER.msg("044029", handlerName, e3.getMessage());
                    throw new NucleusException(msg, e3);
                }
                ((AbstractMetaDataHandler)handler).setValidate(this.validate);
                this.parser.parse(in, handler);
                return ((AbstractMetaDataHandler)handler).getMetaData();
            }
            catch (NucleusException e4) {
                throw e4;
            }
            catch (Exception e5) {
                Throwable cause = e5;
                if (e5 instanceof SAXException) {
                    final SAXException se = (SAXException)e5;
                    cause = se.getException();
                }
                cause = ((e5.getCause() == null) ? cause : e5.getCause());
                NucleusLogger.METADATA.error(MetaDataParser.LOCALISER.msg("044040", filename, cause));
                if (cause instanceof InvalidMetaDataException) {
                    throw (InvalidMetaDataException)cause;
                }
                final String message = MetaDataParser.LOCALISER.msg("044033", e5);
                throw new NucleusException(message, cause);
            }
            finally {
                try {
                    in.close();
                }
                catch (Exception ex) {}
            }
        }
    }
    
    private Source[] getRegisteredSchemas(final PluginManager pm) {
        final ConfigurationElement[] elems = pm.getConfigurationElementsForExtension("org.datanucleus.metadata_entityresolver", null, (String)null);
        final Set<Source> sources = new HashSet<Source>();
        for (int i = 0; i < elems.length; ++i) {
            if (elems[i].getAttribute("type") == null) {
                final InputStream in = MetaDataParser.class.getResourceAsStream(elems[i].getAttribute("url"));
                if (in == null) {
                    NucleusLogger.METADATA.warn("local resource \"" + elems[i].getAttribute("url") + "\" does not exist!!!");
                }
                sources.add(new StreamSource(in));
            }
        }
        return sources.toArray(new Source[sources.size()]);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
