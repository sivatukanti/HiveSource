// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional;

import org.xml.sax.SAXParseException;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.io.InputStream;
import org.xml.sax.InputSource;
import java.io.FileInputStream;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.apache.tools.ant.util.JAXPUtils;
import org.xml.sax.Parser;
import org.xml.sax.helpers.ParserAdapter;
import org.xml.sax.ErrorHandler;
import org.apache.tools.ant.DirectoryScanner;
import org.xml.sax.EntityResolver;
import org.apache.tools.ant.types.ResourceLocation;
import org.apache.tools.ant.types.DTDLocation;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.types.XMLCatalog;
import org.xml.sax.XMLReader;
import org.apache.tools.ant.types.Path;
import java.util.Vector;
import java.io.File;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.Task;

public class XMLValidateTask extends Task
{
    private static final FileUtils FILE_UTILS;
    protected static final String INIT_FAILED_MSG = "Could not start xml validation: ";
    protected boolean failOnError;
    protected boolean warn;
    protected boolean lenient;
    protected String readerClassName;
    protected File file;
    protected Vector filesets;
    protected Path classpath;
    protected XMLReader xmlReader;
    protected ValidatorErrorHandler errorHandler;
    private Vector attributeList;
    private final Vector propertyList;
    private XMLCatalog xmlCatalog;
    public static final String MESSAGE_FILES_VALIDATED = " file(s) have been successfully validated.";
    private AntClassLoader readerLoader;
    
    public XMLValidateTask() {
        this.failOnError = true;
        this.warn = true;
        this.lenient = false;
        this.readerClassName = null;
        this.file = null;
        this.filesets = new Vector();
        this.xmlReader = null;
        this.errorHandler = new ValidatorErrorHandler();
        this.attributeList = new Vector();
        this.propertyList = new Vector();
        this.xmlCatalog = new XMLCatalog();
        this.readerLoader = null;
    }
    
    public void setFailOnError(final boolean fail) {
        this.failOnError = fail;
    }
    
    public void setWarn(final boolean bool) {
        this.warn = bool;
    }
    
    public void setLenient(final boolean bool) {
        this.lenient = bool;
    }
    
    public void setClassName(final String className) {
        this.readerClassName = className;
    }
    
    public void setClasspath(final Path classpath) {
        if (this.classpath == null) {
            this.classpath = classpath;
        }
        else {
            this.classpath.append(classpath);
        }
    }
    
    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject());
        }
        return this.classpath.createPath();
    }
    
    public void setClasspathRef(final Reference r) {
        this.createClasspath().setRefid(r);
    }
    
    public void setFile(final File file) {
        this.file = file;
    }
    
    public void addConfiguredXMLCatalog(final XMLCatalog catalog) {
        this.xmlCatalog.addConfiguredXMLCatalog(catalog);
    }
    
    public void addFileset(final FileSet set) {
        this.filesets.addElement(set);
    }
    
    public Attribute createAttribute() {
        final Attribute feature = new Attribute();
        this.attributeList.addElement(feature);
        return feature;
    }
    
    public Property createProperty() {
        final Property prop = new Property();
        this.propertyList.addElement(prop);
        return prop;
    }
    
    @Override
    public void init() throws BuildException {
        super.init();
        this.xmlCatalog.setProject(this.getProject());
    }
    
    public DTDLocation createDTD() {
        final DTDLocation dtdLocation = new DTDLocation();
        this.xmlCatalog.addDTD(dtdLocation);
        return dtdLocation;
    }
    
    protected EntityResolver getEntityResolver() {
        return this.xmlCatalog;
    }
    
    protected XMLReader getXmlReader() {
        return this.xmlReader;
    }
    
    @Override
    public void execute() throws BuildException {
        try {
            int fileProcessed = 0;
            if (this.file == null && this.filesets.size() == 0) {
                throw new BuildException("Specify at least one source - a file or a fileset.");
            }
            if (this.file != null) {
                if (this.file.exists() && this.file.canRead() && this.file.isFile()) {
                    this.doValidate(this.file);
                    ++fileProcessed;
                }
                else {
                    final String errorMsg = "File " + this.file + " cannot be read";
                    if (this.failOnError) {
                        throw new BuildException(errorMsg);
                    }
                    this.log(errorMsg, 0);
                }
            }
            for (int size = this.filesets.size(), i = 0; i < size; ++i) {
                final FileSet fs = this.filesets.elementAt(i);
                final DirectoryScanner ds = fs.getDirectoryScanner(this.getProject());
                final String[] files = ds.getIncludedFiles();
                for (int j = 0; j < files.length; ++j) {
                    final File srcFile = new File(fs.getDir(this.getProject()), files[j]);
                    this.doValidate(srcFile);
                    ++fileProcessed;
                }
            }
            this.onSuccessfulValidation(fileProcessed);
        }
        finally {
            this.cleanup();
        }
    }
    
    protected void onSuccessfulValidation(final int fileProcessed) {
        this.log(fileProcessed + " file(s) have been successfully validated.");
    }
    
    protected void initValidator() {
        (this.xmlReader = this.createXmlReader()).setEntityResolver(this.getEntityResolver());
        this.xmlReader.setErrorHandler(this.errorHandler);
        if (!this.isSax1Parser()) {
            if (!this.lenient) {
                this.setFeature("http://xml.org/sax/features/validation", true);
            }
            for (int attSize = this.attributeList.size(), i = 0; i < attSize; ++i) {
                final Attribute feature = this.attributeList.elementAt(i);
                this.setFeature(feature.getName(), feature.getValue());
            }
            for (int propSize = this.propertyList.size(), j = 0; j < propSize; ++j) {
                final Property prop = this.propertyList.elementAt(j);
                this.setProperty(prop.getName(), prop.getValue());
            }
        }
    }
    
    protected boolean isSax1Parser() {
        return this.xmlReader instanceof ParserAdapter;
    }
    
    protected XMLReader createXmlReader() {
        Object reader = null;
        if (this.readerClassName == null) {
            reader = this.createDefaultReaderOrParser();
        }
        else {
            Class readerClass = null;
            try {
                if (this.classpath != null) {
                    this.readerLoader = this.getProject().createClassLoader(this.classpath);
                    readerClass = Class.forName(this.readerClassName, true, this.readerLoader);
                }
                else {
                    readerClass = Class.forName(this.readerClassName);
                }
                reader = readerClass.newInstance();
            }
            catch (ClassNotFoundException e) {
                throw new BuildException("Could not start xml validation: " + this.readerClassName, e);
            }
            catch (InstantiationException e2) {
                throw new BuildException("Could not start xml validation: " + this.readerClassName, e2);
            }
            catch (IllegalAccessException e3) {
                throw new BuildException("Could not start xml validation: " + this.readerClassName, e3);
            }
        }
        XMLReader newReader;
        if (reader instanceof XMLReader) {
            newReader = (XMLReader)reader;
            this.log("Using SAX2 reader " + reader.getClass().getName(), 3);
        }
        else {
            if (!(reader instanceof Parser)) {
                throw new BuildException("Could not start xml validation: " + reader.getClass().getName() + " implements nor SAX1 Parser nor SAX2 XMLReader.");
            }
            newReader = new ParserAdapter((Parser)reader);
            this.log("Using SAX1 parser " + reader.getClass().getName(), 3);
        }
        return newReader;
    }
    
    protected void cleanup() {
        if (this.readerLoader != null) {
            this.readerLoader.cleanup();
            this.readerLoader = null;
        }
    }
    
    private Object createDefaultReaderOrParser() {
        Object reader;
        try {
            reader = this.createDefaultReader();
        }
        catch (BuildException exc) {
            reader = JAXPUtils.getParser();
        }
        return reader;
    }
    
    protected XMLReader createDefaultReader() {
        return JAXPUtils.getXMLReader();
    }
    
    protected void setFeature(final String feature, final boolean value) throws BuildException {
        this.log("Setting feature " + feature + "=" + value, 4);
        try {
            this.xmlReader.setFeature(feature, value);
        }
        catch (SAXNotRecognizedException e) {
            throw new BuildException("Parser " + this.xmlReader.getClass().getName() + " doesn't recognize feature " + feature, e, this.getLocation());
        }
        catch (SAXNotSupportedException e2) {
            throw new BuildException("Parser " + this.xmlReader.getClass().getName() + " doesn't support feature " + feature, e2, this.getLocation());
        }
    }
    
    protected void setProperty(final String name, final String value) throws BuildException {
        if (name == null || value == null) {
            throw new BuildException("Property name and value must be specified.");
        }
        try {
            this.xmlReader.setProperty(name, value);
        }
        catch (SAXNotRecognizedException e) {
            throw new BuildException("Parser " + this.xmlReader.getClass().getName() + " doesn't recognize property " + name, e, this.getLocation());
        }
        catch (SAXNotSupportedException e2) {
            throw new BuildException("Parser " + this.xmlReader.getClass().getName() + " doesn't support property " + name, e2, this.getLocation());
        }
    }
    
    protected boolean doValidate(final File afile) {
        this.initValidator();
        boolean result = true;
        try {
            this.log("Validating " + afile.getName() + "... ", 3);
            this.errorHandler.init(afile);
            final InputSource is = new InputSource(new FileInputStream(afile));
            final String uri = XMLValidateTask.FILE_UTILS.toURI(afile.getAbsolutePath());
            is.setSystemId(uri);
            this.xmlReader.parse(is);
        }
        catch (SAXException ex) {
            this.log("Caught when validating: " + ex.toString(), 4);
            if (this.failOnError) {
                throw new BuildException("Could not validate document " + afile);
            }
            this.log("Could not validate document " + afile + ": " + ex.toString());
            result = false;
        }
        catch (IOException ex2) {
            throw new BuildException("Could not validate document " + afile, ex2);
        }
        if (this.errorHandler.getFailure()) {
            if (this.failOnError) {
                throw new BuildException(afile + " is not a valid XML document.");
            }
            result = false;
            this.log(afile + " is not a valid XML document", 0);
        }
        return result;
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
    }
    
    protected class ValidatorErrorHandler implements ErrorHandler
    {
        protected File currentFile;
        protected String lastErrorMessage;
        protected boolean failed;
        
        protected ValidatorErrorHandler() {
            this.currentFile = null;
            this.lastErrorMessage = null;
            this.failed = false;
        }
        
        public void init(final File file) {
            this.currentFile = file;
            this.failed = false;
        }
        
        public boolean getFailure() {
            return this.failed;
        }
        
        public void fatalError(final SAXParseException exception) {
            this.failed = true;
            this.doLog(exception, 0);
        }
        
        public void error(final SAXParseException exception) {
            this.failed = true;
            this.doLog(exception, 0);
        }
        
        public void warning(final SAXParseException exception) {
            if (XMLValidateTask.this.warn) {
                this.doLog(exception, 1);
            }
        }
        
        private void doLog(final SAXParseException e, final int logLevel) {
            XMLValidateTask.this.log(this.getMessage(e), logLevel);
        }
        
        private String getMessage(final SAXParseException e) {
            final String sysID = e.getSystemId();
            if (sysID != null) {
                String name = sysID;
                if (sysID.startsWith("file:")) {
                    try {
                        name = XMLValidateTask.FILE_UTILS.fromURI(sysID);
                    }
                    catch (Exception ex) {}
                }
                final int line = e.getLineNumber();
                final int col = e.getColumnNumber();
                return name + ((line == -1) ? "" : (":" + line + ((col == -1) ? "" : (":" + col)))) + ": " + e.getMessage();
            }
            return e.getMessage();
        }
    }
    
    public static class Attribute
    {
        private String attributeName;
        private boolean attributeValue;
        
        public Attribute() {
            this.attributeName = null;
        }
        
        public void setName(final String name) {
            this.attributeName = name;
        }
        
        public void setValue(final boolean value) {
            this.attributeValue = value;
        }
        
        public String getName() {
            return this.attributeName;
        }
        
        public boolean getValue() {
            return this.attributeValue;
        }
    }
    
    public static final class Property
    {
        private String name;
        private String value;
        
        public String getName() {
            return this.name;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public String getValue() {
            return this.value;
        }
        
        public void setValue(final String value) {
            this.value = value;
        }
    }
}
