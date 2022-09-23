// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.resolver;

import org.apache.xml.resolver.CatalogException;
import org.apache.xml.resolver.readers.CatalogReader;
import java.io.IOException;
import java.util.Vector;
import java.net.URLConnection;
import java.net.FileNameMap;
import org.apache.xml.resolver.Catalog;
import org.apache.commons.configuration2.io.FileLocator;
import org.apache.xml.resolver.CatalogManager;
import org.xml.sax.SAXException;
import java.io.InputStream;
import java.net.URL;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.xml.sax.InputSource;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration2.io.FileLocatorUtils;
import org.apache.commons.configuration2.io.ConfigurationLogger;
import org.apache.commons.configuration2.io.FileSystem;
import org.xml.sax.EntityResolver;

public class CatalogResolver implements EntityResolver
{
    private static final int DEBUG_ALL = 9;
    private static final int DEBUG_NORMAL = 4;
    private static final int DEBUG_NONE = 0;
    private final CatalogManager manager;
    private FileSystem fs;
    private org.apache.xml.resolver.tools.CatalogResolver resolver;
    private ConfigurationLogger log;
    
    public CatalogResolver() {
        this.manager = new CatalogManager();
        this.fs = FileLocatorUtils.DEFAULT_FILE_SYSTEM;
        this.manager.setIgnoreMissingProperties(true);
        this.manager.setUseStaticCatalog(false);
        this.manager.setFileSystem(this.fs);
        this.initLogger(null);
    }
    
    public void setCatalogFiles(final String catalogs) {
        this.manager.setCatalogFiles(catalogs);
    }
    
    public void setFileSystem(final FileSystem fileSystem) {
        this.fs = fileSystem;
        this.manager.setFileSystem(fileSystem);
    }
    
    public void setBaseDir(final String baseDir) {
        this.manager.setBaseDir(baseDir);
    }
    
    public void setInterpolator(final ConfigurationInterpolator ci) {
        this.manager.setInterpolator(ci);
    }
    
    public void setDebug(final boolean debug) {
        if (debug) {
            this.manager.setVerbosity(9);
        }
        else {
            this.manager.setVerbosity(0);
        }
    }
    
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException {
        String resolved = this.getResolver().getResolvedEntity(publicId, systemId);
        if (resolved != null) {
            final String badFilePrefix = "file://";
            final String correctFilePrefix = "file:///";
            if (resolved.startsWith(badFilePrefix) && !resolved.startsWith(correctFilePrefix)) {
                resolved = correctFilePrefix + resolved.substring(badFilePrefix.length());
            }
            try {
                final URL url = locate(this.fs, null, resolved);
                if (url == null) {
                    throw new ConfigurationException("Could not locate " + resolved);
                }
                final InputStream is = this.fs.getInputStream(url);
                final InputSource iSource = new InputSource(resolved);
                iSource.setPublicId(publicId);
                iSource.setByteStream(is);
                return iSource;
            }
            catch (Exception e) {
                this.log.warn("Failed to create InputSource for " + resolved, e);
                return null;
            }
        }
        return null;
    }
    
    public ConfigurationLogger getLogger() {
        return this.log;
    }
    
    public void setLogger(final ConfigurationLogger log) {
        this.initLogger(log);
    }
    
    private void initLogger(final ConfigurationLogger log) {
        this.log = ((log != null) ? log : ConfigurationLogger.newDummyLogger());
    }
    
    private synchronized org.apache.xml.resolver.tools.CatalogResolver getResolver() {
        if (this.resolver == null) {
            this.resolver = new org.apache.xml.resolver.tools.CatalogResolver((org.apache.xml.resolver.CatalogManager)this.manager);
        }
        return this.resolver;
    }
    
    private static URL locate(final FileSystem fs, final String basePath, final String name) {
        final FileLocator locator = FileLocatorUtils.fileLocator().fileSystem(fs).basePath(basePath).fileName(name).create();
        return FileLocatorUtils.locate(locator);
    }
    
    public static class CatalogManager extends org.apache.xml.resolver.CatalogManager
    {
        private static org.apache.xml.resolver.Catalog staticCatalog;
        private FileSystem fs;
        private String baseDir;
        private ConfigurationInterpolator interpolator;
        
        public CatalogManager() {
            this.baseDir = System.getProperty("user.dir");
        }
        
        public void setFileSystem(final FileSystem fileSystem) {
            this.fs = fileSystem;
        }
        
        public FileSystem getFileSystem() {
            return this.fs;
        }
        
        public void setBaseDir(final String baseDir) {
            if (baseDir != null) {
                this.baseDir = baseDir;
            }
        }
        
        public String getBaseDir() {
            return this.baseDir;
        }
        
        public void setInterpolator(final ConfigurationInterpolator ci) {
            this.interpolator = ci;
        }
        
        public ConfigurationInterpolator getInterpolator() {
            return this.interpolator;
        }
        
        public org.apache.xml.resolver.Catalog getPrivateCatalog() {
            org.apache.xml.resolver.Catalog catalog = CatalogManager.staticCatalog;
            if (catalog != null) {
                if (this.getUseStaticCatalog()) {
                    return catalog;
                }
            }
            try {
                catalog = new Catalog();
                catalog.setCatalogManager((org.apache.xml.resolver.CatalogManager)this);
                catalog.setupReaders();
                catalog.loadSystemCatalogs();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            if (this.getUseStaticCatalog()) {
                CatalogManager.staticCatalog = catalog;
            }
            return catalog;
        }
        
        public org.apache.xml.resolver.Catalog getCatalog() {
            return this.getPrivateCatalog();
        }
    }
    
    public static class Catalog extends org.apache.xml.resolver.Catalog
    {
        private FileSystem fs;
        private final FileNameMap fileNameMap;
        
        public Catalog() {
            this.fileNameMap = URLConnection.getFileNameMap();
        }
        
        public void loadSystemCatalogs() throws IOException {
            this.fs = ((CatalogManager)this.catalogManager).getFileSystem();
            final String base = ((CatalogManager)this.catalogManager).getBaseDir();
            final Vector<String> catalogs = (Vector<String>)this.catalogManager.getCatalogFiles();
            if (catalogs != null) {
                for (int count = 0; count < catalogs.size(); ++count) {
                    final String fileName = catalogs.elementAt(count);
                    URL url = null;
                    InputStream is = null;
                    try {
                        url = locate(this.fs, base, fileName);
                        if (url != null) {
                            is = this.fs.getInputStream(url);
                        }
                    }
                    catch (ConfigurationException ce) {
                        final String name = url.toString();
                        this.catalogManager.debug.message(9, "Unable to get input stream for " + name + ". " + ce.getMessage());
                    }
                    if (is != null) {
                        final String mimeType = this.fileNameMap.getContentTypeFor(fileName);
                        try {
                            if (mimeType != null) {
                                this.parseCatalog(mimeType, is);
                                continue;
                            }
                        }
                        catch (Exception ex) {
                            this.catalogManager.debug.message(9, "Exception caught parsing input stream for " + fileName + ". " + ex.getMessage());
                        }
                        finally {
                            is.close();
                        }
                    }
                    this.parseCatalog(base, fileName);
                }
            }
        }
        
        public void parseCatalog(final String baseDir, final String fileName) throws IOException {
            this.base = locate(this.fs, baseDir, fileName);
            this.catalogCwd = this.base;
            this.default_override = this.catalogManager.getPreferPublic();
            this.catalogManager.debug.message(4, "Parse catalog: " + fileName);
            boolean parsed = false;
            for (int count = 0; !parsed && count < this.readerArr.size(); ++count) {
                final CatalogReader reader = this.readerArr.get(count);
                InputStream inStream;
                try {
                    inStream = this.fs.getInputStream(this.base);
                }
                catch (Exception ex) {
                    this.catalogManager.debug.message(4, "Unable to access " + this.base + ex.getMessage());
                    break;
                }
                try {
                    reader.readCatalog((org.apache.xml.resolver.Catalog)this, inStream);
                    parsed = true;
                }
                catch (CatalogException ce) {
                    this.catalogManager.debug.message(4, "Parse failed for " + fileName + ce.getMessage());
                    if (ce.getExceptionType() == 7) {
                        try {
                            inStream.close();
                        }
                        catch (IOException ioe) {
                            inStream = null;
                        }
                        break;
                    }
                    try {
                        inStream.close();
                    }
                    catch (IOException ioe) {
                        inStream = null;
                    }
                }
                finally {
                    try {
                        inStream.close();
                    }
                    catch (IOException ioe2) {
                        inStream = null;
                    }
                }
            }
            if (parsed) {
                this.parsePendingCatalogs();
            }
        }
        
        protected String normalizeURI(final String uriref) {
            final ConfigurationInterpolator ci = ((CatalogManager)this.catalogManager).getInterpolator();
            final String resolved = (ci != null) ? String.valueOf(ci.interpolate(uriref)) : uriref;
            return super.normalizeURI(resolved);
        }
    }
}
