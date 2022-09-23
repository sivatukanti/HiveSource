// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional;

import org.apache.tools.ant.types.XMLCatalog;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import java.lang.reflect.Field;
import org.apache.tools.ant.BuildException;
import java.util.Enumeration;
import javax.xml.transform.TransformerConfigurationException;
import java.io.IOException;
import java.net.URL;
import org.apache.tools.ant.types.resources.URLProvider;
import org.apache.tools.ant.types.resources.FileProvider;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.XMLReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import org.apache.tools.ant.util.JAXPUtils;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import org.apache.tools.ant.types.resources.FileResource;
import java.io.File;
import org.apache.tools.ant.taskdefs.XSLTProcess;
import java.util.Hashtable;
import java.util.Vector;
import javax.xml.transform.URIResolver;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import org.xml.sax.EntityResolver;
import org.apache.tools.ant.taskdefs.XSLTLogger;
import org.apache.tools.ant.types.Resource;
import javax.xml.transform.TransformerFactory;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.taskdefs.XSLTLoggerAware;
import javax.xml.transform.ErrorListener;
import org.apache.tools.ant.taskdefs.XSLTLiaison3;

public class TraXLiaison implements XSLTLiaison3, ErrorListener, XSLTLoggerAware
{
    private static final FileUtils FILE_UTILS;
    private Project project;
    private String factoryName;
    private TransformerFactory tfactory;
    private Resource stylesheet;
    private XSLTLogger logger;
    private EntityResolver entityResolver;
    private Transformer transformer;
    private Templates templates;
    private long templatesModTime;
    private URIResolver uriResolver;
    private Vector outputProperties;
    private Hashtable params;
    private Vector attributes;
    private boolean suppressWarnings;
    private XSLTProcess.TraceConfiguration traceConfiguration;
    
    public TraXLiaison() throws Exception {
        this.factoryName = null;
        this.tfactory = null;
        this.outputProperties = new Vector();
        this.params = new Hashtable();
        this.attributes = new Vector();
        this.suppressWarnings = false;
        this.traceConfiguration = null;
    }
    
    public void setStylesheet(final File stylesheet) throws Exception {
        final FileResource fr = new FileResource();
        fr.setProject(this.project);
        fr.setFile(stylesheet);
        this.setStylesheet(fr);
    }
    
    public void setStylesheet(final Resource stylesheet) throws Exception {
        if (this.stylesheet != null) {
            this.transformer = null;
            if (!this.stylesheet.equals(stylesheet) || stylesheet.getLastModified() != this.templatesModTime) {
                this.templates = null;
            }
        }
        this.stylesheet = stylesheet;
    }
    
    public void transform(final File infile, final File outfile) throws Exception {
        if (this.transformer == null) {
            this.createTransformer();
        }
        InputStream fis = null;
        OutputStream fos = null;
        try {
            fis = new BufferedInputStream(new FileInputStream(infile));
            fos = new BufferedOutputStream(new FileOutputStream(outfile));
            final StreamResult res = new StreamResult(fos);
            res.setSystemId(JAXPUtils.getSystemId(outfile));
            final Source src = this.getSource(fis, infile);
            this.setTransformationParameters();
            this.transformer.transform(src, res);
        }
        finally {
            FileUtils.close(fis);
            FileUtils.close(fos);
        }
    }
    
    private Source getSource(final InputStream is, final File infile) throws ParserConfigurationException, SAXException {
        Source src = null;
        if (this.entityResolver != null) {
            if (!this.getFactory().getFeature("http://javax.xml.transform.sax.SAXSource/feature")) {
                throw new IllegalStateException("xcatalog specified, but parser doesn't support SAX");
            }
            final SAXParserFactory spFactory = SAXParserFactory.newInstance();
            spFactory.setNamespaceAware(true);
            final XMLReader reader = spFactory.newSAXParser().getXMLReader();
            reader.setEntityResolver(this.entityResolver);
            src = new SAXSource(reader, new InputSource(is));
        }
        else {
            src = new StreamSource(is);
        }
        src.setSystemId(JAXPUtils.getSystemId(infile));
        return src;
    }
    
    private Source getSource(final InputStream is, final Resource resource) throws ParserConfigurationException, SAXException {
        Source src = null;
        if (this.entityResolver != null) {
            if (!this.getFactory().getFeature("http://javax.xml.transform.sax.SAXSource/feature")) {
                throw new IllegalStateException("xcatalog specified, but parser doesn't support SAX");
            }
            final SAXParserFactory spFactory = SAXParserFactory.newInstance();
            spFactory.setNamespaceAware(true);
            final XMLReader reader = spFactory.newSAXParser().getXMLReader();
            reader.setEntityResolver(this.entityResolver);
            src = new SAXSource(reader, new InputSource(is));
        }
        else {
            src = new StreamSource(is);
        }
        src.setSystemId(this.resourceToURI(resource));
        return src;
    }
    
    private String resourceToURI(final Resource resource) {
        final FileProvider fp = resource.as(FileProvider.class);
        if (fp != null) {
            return TraXLiaison.FILE_UTILS.toURI(fp.getFile().getAbsolutePath());
        }
        final URLProvider up = resource.as(URLProvider.class);
        if (up != null) {
            final URL u = up.getURL();
            return String.valueOf(u);
        }
        return resource.getName();
    }
    
    private void readTemplates() throws IOException, TransformerConfigurationException, ParserConfigurationException, SAXException {
        InputStream xslStream = null;
        try {
            xslStream = new BufferedInputStream(this.stylesheet.getInputStream());
            this.templatesModTime = this.stylesheet.getLastModified();
            final Source src = this.getSource(xslStream, this.stylesheet);
            this.templates = this.getFactory().newTemplates(src);
        }
        finally {
            if (xslStream != null) {
                xslStream.close();
            }
        }
    }
    
    private void createTransformer() throws Exception {
        if (this.templates == null) {
            this.readTemplates();
        }
        (this.transformer = this.templates.newTransformer()).setErrorListener(this);
        if (this.uriResolver != null) {
            this.transformer.setURIResolver(this.uriResolver);
        }
        for (int size = this.outputProperties.size(), i = 0; i < size; ++i) {
            final String[] pair = this.outputProperties.elementAt(i);
            this.transformer.setOutputProperty(pair[0], pair[1]);
        }
        if (this.traceConfiguration != null) {
            if ("org.apache.xalan.transformer.TransformerImpl".equals(this.transformer.getClass().getName())) {
                try {
                    final Class traceSupport = Class.forName("org.apache.tools.ant.taskdefs.optional.Xalan2TraceSupport", true, Thread.currentThread().getContextClassLoader());
                    final XSLTTraceSupport ts = traceSupport.newInstance();
                    ts.configureTrace(this.transformer, this.traceConfiguration);
                }
                catch (Exception e) {
                    final String msg = "Failed to enable tracing because of " + e;
                    if (this.project != null) {
                        this.project.log(msg, 1);
                    }
                    else {
                        System.err.println(msg);
                    }
                }
            }
            else {
                final String msg2 = "Not enabling trace support for transformer implementation" + this.transformer.getClass().getName();
                if (this.project != null) {
                    this.project.log(msg2, 1);
                }
                else {
                    System.err.println(msg2);
                }
            }
        }
    }
    
    private void setTransformationParameters() {
        final Enumeration enumeration = this.params.keys();
        while (enumeration.hasMoreElements()) {
            final String name = enumeration.nextElement();
            final String value = this.params.get(name);
            this.transformer.setParameter(name, value);
        }
    }
    
    private TransformerFactory getFactory() throws BuildException {
        if (this.tfactory != null) {
            return this.tfactory;
        }
        if (this.factoryName == null) {
            this.tfactory = TransformerFactory.newInstance();
        }
        else {
            try {
                Class clazz = null;
                try {
                    clazz = Class.forName(this.factoryName, true, Thread.currentThread().getContextClassLoader());
                }
                catch (ClassNotFoundException cnfe) {
                    final String msg = "Failed to load " + this.factoryName + " via the configured classpath, will try" + " Ant's classpath instead.";
                    if (this.logger != null) {
                        this.logger.log(msg);
                    }
                    else if (this.project != null) {
                        this.project.log(msg, 1);
                    }
                    else {
                        System.err.println(msg);
                    }
                }
                if (clazz == null) {
                    clazz = Class.forName(this.factoryName);
                }
                this.tfactory = clazz.newInstance();
            }
            catch (Exception e) {
                throw new BuildException(e);
            }
        }
        try {
            final Field _isNotSecureProcessing = this.tfactory.getClass().getDeclaredField("_isNotSecureProcessing");
            _isNotSecureProcessing.setAccessible(true);
            _isNotSecureProcessing.set(this.tfactory, Boolean.TRUE);
        }
        catch (Exception x) {
            if (this.project != null) {
                this.project.log(x.toString(), 4);
            }
        }
        this.tfactory.setErrorListener(this);
        for (int size = this.attributes.size(), i = 0; i < size; ++i) {
            final Object[] pair = this.attributes.elementAt(i);
            this.tfactory.setAttribute((String)pair[0], pair[1]);
        }
        if (this.uriResolver != null) {
            this.tfactory.setURIResolver(this.uriResolver);
        }
        return this.tfactory;
    }
    
    public void setFactory(final String name) {
        this.factoryName = name;
    }
    
    public void setAttribute(final String name, final Object value) {
        final Object[] pair = { name, value };
        this.attributes.addElement(pair);
    }
    
    public void setOutputProperty(final String name, final String value) {
        final String[] pair = { name, value };
        this.outputProperties.addElement(pair);
    }
    
    public void setEntityResolver(final EntityResolver aResolver) {
        this.entityResolver = aResolver;
    }
    
    public void setURIResolver(final URIResolver aResolver) {
        this.uriResolver = aResolver;
    }
    
    public void addParam(final String name, final String value) {
        this.params.put(name, value);
    }
    
    public void setLogger(final XSLTLogger l) {
        this.logger = l;
    }
    
    public void error(final TransformerException e) {
        this.logError(e, "Error");
    }
    
    public void fatalError(final TransformerException e) {
        this.logError(e, "Fatal Error");
        throw new BuildException("Fatal error during transformation using " + this.stylesheet + ": " + e.getMessageAndLocation(), e);
    }
    
    public void warning(final TransformerException e) {
        if (!this.suppressWarnings) {
            this.logError(e, "Warning");
        }
    }
    
    private void logError(final TransformerException e, final String type) {
        if (this.logger == null) {
            return;
        }
        final StringBuffer msg = new StringBuffer();
        final SourceLocator locator = e.getLocator();
        if (locator != null) {
            final String systemid = locator.getSystemId();
            if (systemid != null) {
                String url = systemid;
                if (url.startsWith("file:")) {
                    url = FileUtils.getFileUtils().fromURI(url);
                }
                msg.append(url);
            }
            else {
                msg.append("Unknown file");
            }
            final int line = locator.getLineNumber();
            if (line != -1) {
                msg.append(":");
                msg.append(line);
                final int column = locator.getColumnNumber();
                if (column != -1) {
                    msg.append(":");
                    msg.append(column);
                }
            }
        }
        msg.append(": ");
        msg.append(type);
        msg.append("! ");
        msg.append(e.getMessage());
        if (e.getCause() != null) {
            msg.append(" Cause: ");
            msg.append(e.getCause());
        }
        this.logger.log(msg.toString());
    }
    
    @Deprecated
    protected String getSystemId(final File file) {
        return JAXPUtils.getSystemId(file);
    }
    
    public void configure(final XSLTProcess xsltTask) {
        this.project = xsltTask.getProject();
        final XSLTProcess.Factory factory = xsltTask.getFactory();
        if (factory != null) {
            this.setFactory(factory.getName());
            final Enumeration attrs = factory.getAttributes();
            while (attrs.hasMoreElements()) {
                final XSLTProcess.Factory.Attribute attr = attrs.nextElement();
                this.setAttribute(attr.getName(), attr.getValue());
            }
        }
        final XMLCatalog xmlCatalog = xsltTask.getXMLCatalog();
        if (xmlCatalog != null) {
            this.setEntityResolver(xmlCatalog);
            this.setURIResolver(xmlCatalog);
        }
        final Enumeration props = xsltTask.getOutputProperties();
        while (props.hasMoreElements()) {
            final XSLTProcess.OutputProperty prop = props.nextElement();
            this.setOutputProperty(prop.getName(), prop.getValue());
        }
        this.suppressWarnings = xsltTask.getSuppressWarnings();
        this.traceConfiguration = xsltTask.getTraceConfiguration();
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
    }
}
