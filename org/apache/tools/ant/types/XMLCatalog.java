// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types;

import java.lang.reflect.Method;
import org.apache.tools.ant.util.JAXPUtils;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Iterator;
import org.xml.sax.XMLReader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.Project;
import java.util.Stack;
import javax.xml.transform.TransformerException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.Source;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import java.util.Collection;
import org.apache.tools.ant.BuildException;
import java.util.Vector;
import org.apache.tools.ant.util.FileUtils;
import javax.xml.transform.URIResolver;
import org.xml.sax.EntityResolver;

public class XMLCatalog extends DataType implements Cloneable, EntityResolver, URIResolver
{
    private static final FileUtils FILE_UTILS;
    private Vector<ResourceLocation> elements;
    private Path classpath;
    private Path catalogPath;
    public static final String APACHE_RESOLVER = "org.apache.tools.ant.types.resolver.ApacheCatalogResolver";
    public static final String CATALOG_RESOLVER = "org.apache.xml.resolver.tools.CatalogResolver";
    private CatalogResolver catalogResolver;
    
    public XMLCatalog() {
        this.elements = new Vector<ResourceLocation>();
        this.catalogResolver = null;
        this.setChecked(false);
    }
    
    private Vector<ResourceLocation> getElements() {
        return this.getRef().elements;
    }
    
    private Path getClasspath() {
        return this.getRef().classpath;
    }
    
    public Path createClasspath() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject());
        }
        this.setChecked(false);
        return this.classpath.createPath();
    }
    
    public void setClasspath(final Path classpath) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        if (this.classpath == null) {
            this.classpath = classpath;
        }
        else {
            this.classpath.append(classpath);
        }
        this.setChecked(false);
    }
    
    public void setClasspathRef(final Reference r) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.createClasspath().setRefid(r);
        this.setChecked(false);
    }
    
    public Path createCatalogPath() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (this.catalogPath == null) {
            this.catalogPath = new Path(this.getProject());
        }
        this.setChecked(false);
        return this.catalogPath.createPath();
    }
    
    public void setCatalogPathRef(final Reference r) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.createCatalogPath().setRefid(r);
        this.setChecked(false);
    }
    
    public Path getCatalogPath() {
        return this.getRef().catalogPath;
    }
    
    public void addDTD(final ResourceLocation dtd) throws BuildException {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.getElements().addElement(dtd);
        this.setChecked(false);
    }
    
    public void addEntity(final ResourceLocation entity) throws BuildException {
        this.addDTD(entity);
    }
    
    public void addConfiguredXMLCatalog(final XMLCatalog catalog) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.getElements().addAll(catalog.getElements());
        final Path nestedClasspath = catalog.getClasspath();
        this.createClasspath().append(nestedClasspath);
        final Path nestedCatalogPath = catalog.getCatalogPath();
        this.createCatalogPath().append(nestedCatalogPath);
        this.setChecked(false);
    }
    
    @Override
    public void setRefid(final Reference r) throws BuildException {
        if (!this.elements.isEmpty()) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }
    
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
        if (this.isReference()) {
            return this.getRef().resolveEntity(publicId, systemId);
        }
        this.dieOnCircularReference();
        this.log("resolveEntity: '" + publicId + "': '" + systemId + "'", 4);
        final InputSource inputSource = this.getCatalogResolver().resolveEntity(publicId, systemId);
        if (inputSource == null) {
            this.log("No matching catalog entry found, parser will use: '" + systemId + "'", 4);
        }
        return inputSource;
    }
    
    public Source resolve(final String href, final String base) throws TransformerException {
        if (this.isReference()) {
            return this.getRef().resolve(href, base);
        }
        this.dieOnCircularReference();
        SAXSource source = null;
        final String uri = this.removeFragment(href);
        this.log("resolve: '" + uri + "' with base: '" + base + "'", 4);
        source = (SAXSource)this.getCatalogResolver().resolve(uri, base);
        if (source == null) {
            this.log("No matching catalog entry found, parser will use: '" + href + "'", 4);
            source = new SAXSource();
            URL baseURL = null;
            try {
                if (base == null) {
                    baseURL = XMLCatalog.FILE_UTILS.getFileURL(this.getProject().getBaseDir());
                }
                else {
                    baseURL = new URL(base);
                }
                final URL url = (uri.length() == 0) ? baseURL : new URL(baseURL, uri);
                source.setInputSource(new InputSource(url.toString()));
            }
            catch (MalformedURLException ex) {
                source.setInputSource(new InputSource(uri));
            }
        }
        this.setEntityResolver(source);
        return source;
    }
    
    @Override
    protected synchronized void dieOnCircularReference(final Stack<Object> stk, final Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        }
        else {
            if (this.classpath != null) {
                DataType.pushAndInvokeCircularReferenceCheck(this.classpath, stk, p);
            }
            if (this.catalogPath != null) {
                DataType.pushAndInvokeCircularReferenceCheck(this.catalogPath, stk, p);
            }
            this.setChecked(true);
        }
    }
    
    private XMLCatalog getRef() {
        if (!this.isReference()) {
            return this;
        }
        return this.getCheckedRef(XMLCatalog.class, "xmlcatalog");
    }
    
    private CatalogResolver getCatalogResolver() {
        if (this.catalogResolver == null) {
            AntClassLoader loader = null;
            loader = this.getProject().createClassLoader(Path.systemClasspath);
            try {
                Class<?> clazz = Class.forName("org.apache.tools.ant.types.resolver.ApacheCatalogResolver", true, loader);
                final ClassLoader apacheResolverLoader = clazz.getClassLoader();
                final Class<?> baseResolverClass = Class.forName("org.apache.xml.resolver.tools.CatalogResolver", true, apacheResolverLoader);
                final ClassLoader baseResolverLoader = baseResolverClass.getClassLoader();
                clazz = Class.forName("org.apache.tools.ant.types.resolver.ApacheCatalogResolver", true, baseResolverLoader);
                final Object obj = clazz.newInstance();
                this.catalogResolver = new ExternalResolver(clazz, obj);
            }
            catch (Throwable ex) {
                this.catalogResolver = new InternalResolver();
                if (this.getCatalogPath() != null && this.getCatalogPath().list().length != 0) {
                    this.log("Warning: XML resolver not found; external catalogs will be ignored", 1);
                }
                this.log("Failed to load Apache resolver: " + ex, 4);
            }
        }
        return this.catalogResolver;
    }
    
    private void setEntityResolver(final SAXSource source) throws TransformerException {
        XMLReader reader = source.getXMLReader();
        if (reader == null) {
            final SAXParserFactory spFactory = SAXParserFactory.newInstance();
            spFactory.setNamespaceAware(true);
            try {
                reader = spFactory.newSAXParser().getXMLReader();
            }
            catch (ParserConfigurationException ex) {
                throw new TransformerException(ex);
            }
            catch (SAXException ex2) {
                throw new TransformerException(ex2);
            }
        }
        reader.setEntityResolver(this);
        source.setXMLReader(reader);
    }
    
    private ResourceLocation findMatchingEntry(final String publicId) {
        for (final ResourceLocation element : this.getElements()) {
            if (element.getPublicId().equals(publicId)) {
                return element;
            }
        }
        return null;
    }
    
    private String removeFragment(final String uri) {
        String result = uri;
        final int hashPos = uri.indexOf("#");
        if (hashPos >= 0) {
            result = uri.substring(0, hashPos);
        }
        return result;
    }
    
    private InputSource filesystemLookup(final ResourceLocation matchingEntry) {
        String uri = matchingEntry.getLocation();
        uri = uri.replace(File.separatorChar, '/');
        URL baseURL = null;
        if (matchingEntry.getBase() != null) {
            baseURL = matchingEntry.getBase();
        }
        else {
            try {
                baseURL = XMLCatalog.FILE_UTILS.getFileURL(this.getProject().getBaseDir());
            }
            catch (MalformedURLException ex) {
                throw new BuildException("Project basedir cannot be converted to a URL");
            }
        }
        InputSource source = null;
        URL url = null;
        Label_0213: {
            try {
                url = new URL(baseURL, uri);
            }
            catch (MalformedURLException ex2) {
                final File testFile = new File(uri);
                if (testFile.exists() && testFile.canRead()) {
                    this.log("uri : '" + uri + "' matches a readable file", 4);
                    try {
                        url = XMLCatalog.FILE_UTILS.getFileURL(testFile);
                        break Label_0213;
                    }
                    catch (MalformedURLException ex3) {
                        throw new BuildException("could not find an URL for :" + testFile.getAbsolutePath());
                    }
                }
                this.log("uri : '" + uri + "' does not match a readable file", 4);
            }
        }
        if (url != null && url.getProtocol().equals("file")) {
            final String fileName = XMLCatalog.FILE_UTILS.fromURI(url.toString());
            if (fileName != null) {
                this.log("fileName " + fileName, 4);
                final File resFile = new File(fileName);
                if (resFile.exists() && resFile.canRead()) {
                    try {
                        source = new InputSource(new FileInputStream(resFile));
                        final String sysid = JAXPUtils.getSystemId(resFile);
                        source.setSystemId(sysid);
                        this.log("catalog entry matched a readable file: '" + sysid + "'", 4);
                    }
                    catch (IOException ex4) {}
                }
            }
        }
        return source;
    }
    
    private InputSource classpathLookup(final ResourceLocation matchingEntry) {
        InputSource source = null;
        AntClassLoader loader = null;
        Path cp = this.classpath;
        if (cp != null) {
            cp = this.classpath.concatSystemClasspath("ignore");
        }
        else {
            cp = new Path(this.getProject()).concatSystemClasspath("last");
        }
        loader = this.getProject().createClassLoader(cp);
        final InputStream is = loader.getResourceAsStream(matchingEntry.getLocation());
        if (is != null) {
            source = new InputSource(is);
            final URL entryURL = loader.getResource(matchingEntry.getLocation());
            final String sysid = entryURL.toExternalForm();
            source.setSystemId(sysid);
            this.log("catalog entry matched a resource in the classpath: '" + sysid + "'", 4);
        }
        return source;
    }
    
    private InputSource urlLookup(final ResourceLocation matchingEntry) {
        final String uri = matchingEntry.getLocation();
        URL baseURL = null;
        if (matchingEntry.getBase() != null) {
            baseURL = matchingEntry.getBase();
        }
        else {
            try {
                baseURL = XMLCatalog.FILE_UTILS.getFileURL(this.getProject().getBaseDir());
            }
            catch (MalformedURLException ex) {
                throw new BuildException("Project basedir cannot be converted to a URL");
            }
        }
        InputSource source = null;
        URL url = null;
        try {
            url = new URL(baseURL, uri);
        }
        catch (MalformedURLException ex2) {}
        if (url != null) {
            try {
                final InputStream is = url.openStream();
                if (is != null) {
                    source = new InputSource(is);
                    final String sysid = url.toExternalForm();
                    source.setSystemId(sysid);
                    this.log("catalog entry matched as a URL: '" + sysid + "'", 4);
                }
            }
            catch (IOException ex3) {}
        }
        return source;
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
    }
    
    private class InternalResolver implements CatalogResolver
    {
        public InternalResolver() {
            XMLCatalog.this.log("Apache resolver library not found, internal resolver will be used", 3);
        }
        
        public InputSource resolveEntity(final String publicId, final String systemId) {
            InputSource result = null;
            final ResourceLocation matchingEntry = XMLCatalog.this.findMatchingEntry(publicId);
            if (matchingEntry != null) {
                XMLCatalog.this.log("Matching catalog entry found for publicId: '" + matchingEntry.getPublicId() + "' location: '" + matchingEntry.getLocation() + "'", 4);
                result = XMLCatalog.this.filesystemLookup(matchingEntry);
                if (result == null) {
                    result = XMLCatalog.this.classpathLookup(matchingEntry);
                }
                if (result == null) {
                    result = XMLCatalog.this.urlLookup(matchingEntry);
                }
            }
            return result;
        }
        
        public Source resolve(final String href, final String base) throws TransformerException {
            SAXSource result = null;
            InputSource source = null;
            final ResourceLocation matchingEntry = XMLCatalog.this.findMatchingEntry(href);
            if (matchingEntry != null) {
                XMLCatalog.this.log("Matching catalog entry found for uri: '" + matchingEntry.getPublicId() + "' location: '" + matchingEntry.getLocation() + "'", 4);
                ResourceLocation entryCopy = matchingEntry;
                if (base != null) {
                    try {
                        final URL baseURL = new URL(base);
                        entryCopy = new ResourceLocation();
                        entryCopy.setBase(baseURL);
                    }
                    catch (MalformedURLException ex) {}
                }
                entryCopy.setPublicId(matchingEntry.getPublicId());
                entryCopy.setLocation(matchingEntry.getLocation());
                source = XMLCatalog.this.filesystemLookup(entryCopy);
                if (source == null) {
                    source = XMLCatalog.this.classpathLookup(entryCopy);
                }
                if (source == null) {
                    source = XMLCatalog.this.urlLookup(entryCopy);
                }
                if (source != null) {
                    result = new SAXSource(source);
                }
            }
            return result;
        }
    }
    
    private class ExternalResolver implements CatalogResolver
    {
        private Method setXMLCatalog;
        private Method parseCatalog;
        private Method resolveEntity;
        private Method resolve;
        private Object resolverImpl;
        private boolean externalCatalogsProcessed;
        
        public ExternalResolver(final Class<?> resolverImplClass, final Object resolverImpl) {
            this.setXMLCatalog = null;
            this.parseCatalog = null;
            this.resolveEntity = null;
            this.resolve = null;
            this.resolverImpl = null;
            this.externalCatalogsProcessed = false;
            this.resolverImpl = resolverImpl;
            try {
                this.setXMLCatalog = resolverImplClass.getMethod("setXMLCatalog", XMLCatalog.class);
                this.parseCatalog = resolverImplClass.getMethod("parseCatalog", String.class);
                this.resolveEntity = resolverImplClass.getMethod("resolveEntity", String.class, String.class);
                this.resolve = resolverImplClass.getMethod("resolve", String.class, String.class);
            }
            catch (NoSuchMethodException ex) {
                throw new BuildException(ex);
            }
            XMLCatalog.this.log("Apache resolver library found, xml-commons resolver will be used", 3);
        }
        
        public InputSource resolveEntity(final String publicId, final String systemId) {
            InputSource result = null;
            this.processExternalCatalogs();
            final ResourceLocation matchingEntry = XMLCatalog.this.findMatchingEntry(publicId);
            if (matchingEntry != null) {
                XMLCatalog.this.log("Matching catalog entry found for publicId: '" + matchingEntry.getPublicId() + "' location: '" + matchingEntry.getLocation() + "'", 4);
                result = XMLCatalog.this.filesystemLookup(matchingEntry);
                if (result == null) {
                    result = XMLCatalog.this.classpathLookup(matchingEntry);
                }
                if (result != null) {
                    return result;
                }
                try {
                    result = (InputSource)this.resolveEntity.invoke(this.resolverImpl, publicId, systemId);
                    return result;
                }
                catch (Exception ex) {
                    throw new BuildException(ex);
                }
            }
            try {
                result = (InputSource)this.resolveEntity.invoke(this.resolverImpl, publicId, systemId);
            }
            catch (Exception ex) {
                throw new BuildException(ex);
            }
            return result;
        }
        
        public Source resolve(final String href, String base) throws TransformerException {
            SAXSource result = null;
            InputSource source = null;
            this.processExternalCatalogs();
            final ResourceLocation matchingEntry = XMLCatalog.this.findMatchingEntry(href);
            if (matchingEntry != null) {
                XMLCatalog.this.log("Matching catalog entry found for uri: '" + matchingEntry.getPublicId() + "' location: '" + matchingEntry.getLocation() + "'", 4);
                ResourceLocation entryCopy = matchingEntry;
                if (base != null) {
                    try {
                        final URL baseURL = new URL(base);
                        entryCopy = new ResourceLocation();
                        entryCopy.setBase(baseURL);
                    }
                    catch (MalformedURLException ex3) {}
                }
                entryCopy.setPublicId(matchingEntry.getPublicId());
                entryCopy.setLocation(matchingEntry.getLocation());
                source = XMLCatalog.this.filesystemLookup(entryCopy);
                if (source == null) {
                    source = XMLCatalog.this.classpathLookup(entryCopy);
                }
                if (source != null) {
                    result = new SAXSource(source);
                }
                else {
                    try {
                        result = (SAXSource)this.resolve.invoke(this.resolverImpl, href, base);
                    }
                    catch (Exception ex) {
                        throw new BuildException(ex);
                    }
                }
            }
            else {
                if (base == null) {
                    try {
                        base = XMLCatalog.FILE_UTILS.getFileURL(XMLCatalog.this.getProject().getBaseDir()).toString();
                    }
                    catch (MalformedURLException x) {
                        throw new TransformerException(x);
                    }
                }
                try {
                    result = (SAXSource)this.resolve.invoke(this.resolverImpl, href, base);
                }
                catch (Exception ex2) {
                    throw new BuildException(ex2);
                }
            }
            return result;
        }
        
        private void processExternalCatalogs() {
            if (!this.externalCatalogsProcessed) {
                try {
                    this.setXMLCatalog.invoke(this.resolverImpl, XMLCatalog.this);
                }
                catch (Exception ex) {
                    throw new BuildException(ex);
                }
                final Path catPath = XMLCatalog.this.getCatalogPath();
                if (catPath != null) {
                    XMLCatalog.this.log("Using catalogpath '" + XMLCatalog.this.getCatalogPath() + "'", 4);
                    final String[] catPathList = XMLCatalog.this.getCatalogPath().list();
                    for (int i = 0; i < catPathList.length; ++i) {
                        final File catFile = new File(catPathList[i]);
                        XMLCatalog.this.log("Parsing " + catFile, 4);
                        try {
                            this.parseCatalog.invoke(this.resolverImpl, catFile.getPath());
                        }
                        catch (Exception ex2) {
                            throw new BuildException(ex2);
                        }
                    }
                }
            }
            this.externalCatalogsProcessed = true;
        }
    }
    
    private interface CatalogResolver extends URIResolver, EntityResolver
    {
        InputSource resolveEntity(final String p0, final String p1);
        
        Source resolve(final String p0, final String p1) throws TransformerException;
    }
}
