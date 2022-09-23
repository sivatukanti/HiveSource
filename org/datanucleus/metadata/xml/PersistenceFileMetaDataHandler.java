// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata.xml;

import org.xml.sax.SAXException;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.PersistenceUnitMetaData;
import org.xml.sax.Attributes;
import java.net.URISyntaxException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.metadata.PersistenceFileMetaData;
import org.xml.sax.EntityResolver;
import org.datanucleus.metadata.MetaDataManager;
import java.net.URI;

public class PersistenceFileMetaDataHandler extends AbstractMetaDataHandler
{
    URI rootURI;
    
    public PersistenceFileMetaDataHandler(final MetaDataManager mgr, final String filename, final EntityResolver resolver) {
        super(mgr, filename, resolver);
        this.rootURI = null;
        this.pushStack(this.metadata = new PersistenceFileMetaData(filename));
        String rootFilename = null;
        if (filename.endsWith("/META-INF/persistence.xml")) {
            rootFilename = filename.substring(0, filename.length() - "/META-INF/persistence.xml".length());
        }
        else {
            rootFilename = filename.substring(0, filename.lastIndexOf("/"));
        }
        try {
            rootFilename = rootFilename.replace(" ", "%20");
            this.rootURI = new URI(rootFilename);
        }
        catch (URISyntaxException e) {
            NucleusLogger.METADATA.warn("Error deriving persistence-unit root URI from " + rootFilename, e);
        }
    }
    
    @Override
    public void startElement(final String uri, String localName, final String qName, final Attributes attrs) throws SAXException {
        if (localName.length() < 1) {
            localName = qName;
        }
        try {
            if (!localName.equals("persistence")) {
                if (localName.equals("persistence-unit")) {
                    final PersistenceFileMetaData filemd = (PersistenceFileMetaData)this.getStack();
                    final PersistenceUnitMetaData pumd = new PersistenceUnitMetaData(this.getAttr(attrs, "name"), this.getAttr(attrs, "transaction-type"), this.rootURI);
                    filemd.addPersistenceUnit(pumd);
                    this.pushStack(pumd);
                }
                else if (!localName.equals("properties")) {
                    if (localName.equals("property")) {
                        final PersistenceUnitMetaData pumd2 = (PersistenceUnitMetaData)this.getStack();
                        pumd2.addProperty(this.getAttr(attrs, "name"), this.getAttr(attrs, "value"));
                    }
                    else if (!localName.equals("mapping-file")) {
                        if (!localName.equals("class")) {
                            if (!localName.equals("jar-file")) {
                                if (!localName.equals("jta-data-source")) {
                                    if (!localName.equals("non-jta-data-source")) {
                                        if (!localName.equals("description")) {
                                            if (!localName.equals("provider")) {
                                                if (!localName.equals("shared-cache-mode")) {
                                                    if (!localName.equals("validation-mode")) {
                                                        if (!localName.equals("exclude-unlisted-classes")) {
                                                            final String message = PersistenceFileMetaDataHandler.LOCALISER.msg("044037", qName);
                                                            NucleusLogger.METADATA.error(message);
                                                            throw new RuntimeException(message);
                                                        }
                                                        final PersistenceUnitMetaData pumd2 = (PersistenceUnitMetaData)this.getStack();
                                                        pumd2.setExcludeUnlistedClasses();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (RuntimeException ex) {
            NucleusLogger.METADATA.error(PersistenceFileMetaDataHandler.LOCALISER.msg("044042", qName, this.getStack(), uri), ex);
            throw ex;
        }
    }
    
    @Override
    public void endElement(final String uri, String localName, final String qName) throws SAXException {
        if (localName.length() < 1) {
            localName = qName;
        }
        final String currentString = this.getString().trim();
        if (currentString.length() > 0) {
            final MetaData md = this.getStack();
            if (localName.equals("description")) {
                ((PersistenceUnitMetaData)md).setDescription(currentString);
            }
            else if (localName.equals("provider")) {
                ((PersistenceUnitMetaData)md).setProvider(currentString);
            }
            else if (localName.equals("jta-data-source")) {
                ((PersistenceUnitMetaData)md).setJtaDataSource(currentString);
            }
            else if (localName.equals("non-jta-data-source")) {
                ((PersistenceUnitMetaData)md).setNonJtaDataSource(currentString);
            }
            else if (localName.equals("class")) {
                ((PersistenceUnitMetaData)md).addClassName(currentString);
            }
            else if (localName.equals("mapping-file")) {
                ((PersistenceUnitMetaData)md).addMappingFile(currentString);
            }
            else if (localName.equals("jar-file")) {
                ((PersistenceUnitMetaData)md).addJarFile(currentString);
            }
            else if (localName.equals("shared-cache-mode")) {
                ((PersistenceUnitMetaData)md).setCaching(currentString);
            }
            else if (localName.equals("validation-mode")) {
                ((PersistenceUnitMetaData)md).setValidationMode(currentString);
            }
        }
        if (qName.equals("persistence-unit")) {
            this.popStack();
        }
    }
}
