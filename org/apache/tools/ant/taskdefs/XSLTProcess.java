// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.ProjectComponent;
import java.io.OutputStream;
import org.apache.tools.ant.DynamicConfigurator;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.Project;
import java.util.Enumeration;
import java.util.Iterator;
import org.apache.tools.ant.util.ResourceUtils;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.taskdefs.optional.TraXLiaison;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.types.XMLCatalog;
import org.apache.tools.ant.types.Path;
import java.util.Vector;
import org.apache.tools.ant.types.Resource;
import java.io.File;

public class XSLTProcess extends MatchingTask implements XSLTLogger
{
    private File destDir;
    private File baseDir;
    private String xslFile;
    private Resource xslResource;
    private String targetExtension;
    private String fileNameParameter;
    private String fileDirParameter;
    private Vector params;
    private File inFile;
    private File outFile;
    private String processor;
    private Path classpath;
    private XSLTLiaison liaison;
    private boolean stylesheetLoaded;
    private boolean force;
    private Vector outputProperties;
    private XMLCatalog xmlCatalog;
    private static final FileUtils FILE_UTILS;
    private boolean performDirectoryScan;
    private Factory factory;
    private boolean reuseLoadedStylesheet;
    private AntClassLoader loader;
    private Mapper mapperElement;
    private Union resources;
    private boolean useImplicitFileset;
    public static final String PROCESSOR_TRAX = "trax";
    private boolean suppressWarnings;
    private boolean failOnTransformationError;
    private boolean failOnError;
    private boolean failOnNoResources;
    private CommandlineJava.SysProperties sysProperties;
    private TraceConfiguration traceConfiguration;
    
    public XSLTProcess() {
        this.destDir = null;
        this.baseDir = null;
        this.xslFile = null;
        this.xslResource = null;
        this.targetExtension = ".html";
        this.fileNameParameter = null;
        this.fileDirParameter = null;
        this.params = new Vector();
        this.inFile = null;
        this.outFile = null;
        this.classpath = null;
        this.stylesheetLoaded = false;
        this.force = false;
        this.outputProperties = new Vector();
        this.xmlCatalog = new XMLCatalog();
        this.performDirectoryScan = true;
        this.factory = null;
        this.reuseLoadedStylesheet = true;
        this.loader = null;
        this.mapperElement = null;
        this.resources = new Union();
        this.useImplicitFileset = true;
        this.suppressWarnings = false;
        this.failOnTransformationError = true;
        this.failOnError = true;
        this.failOnNoResources = true;
        this.sysProperties = new CommandlineJava.SysProperties();
    }
    
    public void setScanIncludedDirectories(final boolean b) {
        this.performDirectoryScan = b;
    }
    
    public void setReloadStylesheet(final boolean b) {
        this.reuseLoadedStylesheet = !b;
    }
    
    public void addMapper(final Mapper mapper) {
        if (this.mapperElement != null) {
            this.handleError("Cannot define more than one mapper");
        }
        else {
            this.mapperElement = mapper;
        }
    }
    
    public void add(final ResourceCollection rc) {
        this.resources.add(rc);
    }
    
    public void addConfiguredStyle(final Resources rc) {
        if (rc.size() != 1) {
            this.handleError("The style element must be specified with exactly one nested resource.");
        }
        else {
            this.setXslResource(rc.iterator().next());
        }
    }
    
    public void setXslResource(final Resource xslResource) {
        this.xslResource = xslResource;
    }
    
    public void add(final FileNameMapper fileNameMapper) throws BuildException {
        final Mapper mapper = new Mapper(this.getProject());
        mapper.add(fileNameMapper);
        this.addMapper(mapper);
    }
    
    @Override
    public void execute() throws BuildException {
        if ("style".equals(this.getTaskType())) {
            this.log("Warning: the task name <style> is deprecated. Use <xslt> instead.", 1);
        }
        final File savedBaseDir = this.baseDir;
        final String baseMessage = "specify the stylesheet either as a filename in style attribute or as a nested resource";
        if (this.xslResource == null && this.xslFile == null) {
            this.handleError(baseMessage);
            return;
        }
        if (this.xslResource != null && this.xslFile != null) {
            this.handleError(baseMessage + " but not as both");
            return;
        }
        if (this.inFile != null && !this.inFile.exists()) {
            this.handleError("input file " + this.inFile + " does not exist");
            return;
        }
        try {
            this.setupLoader();
            if (this.sysProperties.size() > 0) {
                this.sysProperties.setSystem();
            }
            if (this.baseDir == null) {
                this.baseDir = this.getProject().getBaseDir();
            }
            this.liaison = this.getLiaison();
            if (this.liaison instanceof XSLTLoggerAware) {
                ((XSLTLoggerAware)this.liaison).setLogger(this);
            }
            this.log("Using " + this.liaison.getClass().toString(), 3);
            Resource styleResource;
            if (this.xslFile != null) {
                File stylesheet = this.getProject().resolveFile(this.xslFile);
                if (!stylesheet.exists()) {
                    final File alternative = XSLTProcess.FILE_UTILS.resolveFile(this.baseDir, this.xslFile);
                    if (alternative.exists()) {
                        this.log("DEPRECATED - the 'style' attribute should be relative to the project's");
                        this.log("             basedir, not the tasks's basedir.");
                        stylesheet = alternative;
                    }
                }
                final FileResource fr = new FileResource();
                fr.setProject(this.getProject());
                fr.setFile(stylesheet);
                styleResource = fr;
            }
            else {
                styleResource = this.xslResource;
            }
            if (!styleResource.isExists()) {
                this.handleError("stylesheet " + styleResource + " doesn't exist.");
                return;
            }
            if (this.inFile != null && this.outFile != null) {
                this.process(this.inFile, this.outFile, styleResource);
                return;
            }
            this.checkDest();
            if (this.useImplicitFileset) {
                final DirectoryScanner scanner = this.getDirectoryScanner(this.baseDir);
                this.log("Transforming into " + this.destDir, 2);
                String[] list = scanner.getIncludedFiles();
                for (int i = 0; i < list.length; ++i) {
                    this.process(this.baseDir, list[i], this.destDir, styleResource);
                }
                if (this.performDirectoryScan) {
                    final String[] dirs = scanner.getIncludedDirectories();
                    for (int j = 0; j < dirs.length; ++j) {
                        list = new File(this.baseDir, dirs[j]).list();
                        for (int k = 0; k < list.length; ++k) {
                            this.process(this.baseDir, dirs[j] + File.separator + list[k], this.destDir, styleResource);
                        }
                    }
                }
            }
            else if (this.resources.size() == 0) {
                if (this.failOnNoResources) {
                    this.handleError("no resources specified");
                }
                return;
            }
            this.processResources(styleResource);
        }
        finally {
            if (this.loader != null) {
                this.loader.resetThreadContextLoader();
                this.loader.cleanup();
                this.loader = null;
            }
            if (this.sysProperties.size() > 0) {
                this.sysProperties.restoreSystem();
            }
            this.liaison = null;
            this.stylesheetLoaded = false;
            this.baseDir = savedBaseDir;
        }
    }
    
    public void setForce(final boolean force) {
        this.force = force;
    }
    
    public void setBasedir(final File dir) {
        this.baseDir = dir;
    }
    
    public void setDestdir(final File dir) {
        this.destDir = dir;
    }
    
    public void setExtension(final String name) {
        this.targetExtension = name;
    }
    
    public void setStyle(final String xslFile) {
        this.xslFile = xslFile;
    }
    
    public void setClasspath(final Path classpath) {
        this.createClasspath().append(classpath);
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
    
    public void setProcessor(final String processor) {
        this.processor = processor;
    }
    
    public void setUseImplicitFileset(final boolean useimplicitfileset) {
        this.useImplicitFileset = useimplicitfileset;
    }
    
    public void addConfiguredXMLCatalog(final XMLCatalog xmlCatalog) {
        this.xmlCatalog.addConfiguredXMLCatalog(xmlCatalog);
    }
    
    public void setFileNameParameter(final String fileNameParameter) {
        this.fileNameParameter = fileNameParameter;
    }
    
    public void setFileDirParameter(final String fileDirParameter) {
        this.fileDirParameter = fileDirParameter;
    }
    
    public void setSuppressWarnings(final boolean b) {
        this.suppressWarnings = b;
    }
    
    public boolean getSuppressWarnings() {
        return this.suppressWarnings;
    }
    
    public void setFailOnTransformationError(final boolean b) {
        this.failOnTransformationError = b;
    }
    
    public void setFailOnError(final boolean b) {
        this.failOnError = b;
    }
    
    public void setFailOnNoResources(final boolean b) {
        this.failOnNoResources = b;
    }
    
    public void addSysproperty(final Environment.Variable sysp) {
        this.sysProperties.addVariable(sysp);
    }
    
    public void addSyspropertyset(final PropertySet sysp) {
        this.sysProperties.addSyspropertyset(sysp);
    }
    
    public TraceConfiguration createTrace() {
        if (this.traceConfiguration != null) {
            throw new BuildException("can't have more than one trace configuration");
        }
        return this.traceConfiguration = new TraceConfiguration();
    }
    
    public TraceConfiguration getTraceConfiguration() {
        return this.traceConfiguration;
    }
    
    private void resolveProcessor(final String proc) throws Exception {
        if (proc.equals("trax")) {
            this.liaison = new TraXLiaison();
        }
        else {
            final Class clazz = this.loadClass(proc);
            this.liaison = clazz.newInstance();
        }
    }
    
    private Class loadClass(final String classname) throws Exception {
        this.setupLoader();
        if (this.loader == null) {
            return Class.forName(classname);
        }
        return Class.forName(classname, true, this.loader);
    }
    
    private void setupLoader() {
        if (this.classpath != null && this.loader == null) {
            (this.loader = this.getProject().createClassLoader(this.classpath)).setThreadContextLoader();
        }
    }
    
    public void setOut(final File outFile) {
        this.outFile = outFile;
    }
    
    public void setIn(final File inFile) {
        this.inFile = inFile;
    }
    
    private void checkDest() {
        if (this.destDir == null) {
            this.handleError("destdir attributes must be set!");
        }
    }
    
    private void processResources(final Resource stylesheet) {
        for (final Resource r : this.resources) {
            if (!r.isExists()) {
                continue;
            }
            File base = this.baseDir;
            String name = r.getName();
            final FileProvider fp = r.as(FileProvider.class);
            if (fp != null) {
                final FileResource f = ResourceUtils.asFileResource(fp);
                base = f.getBaseDir();
                if (base == null) {
                    name = f.getFile().getAbsolutePath();
                }
            }
            this.process(base, name, this.destDir, stylesheet);
        }
    }
    
    private void process(final File baseDir, final String xmlFile, final File destDir, final Resource stylesheet) throws BuildException {
        File outF = null;
        File inF = null;
        try {
            final long styleSheetLastModified = stylesheet.getLastModified();
            inF = new File(baseDir, xmlFile);
            if (inF.isDirectory()) {
                this.log("Skipping " + inF + " it is a directory.", 3);
                return;
            }
            FileNameMapper mapper = null;
            if (this.mapperElement != null) {
                mapper = this.mapperElement.getImplementation();
            }
            else {
                mapper = new StyleMapper();
            }
            final String[] outFileName = mapper.mapFileName(xmlFile);
            if (outFileName == null || outFileName.length == 0) {
                this.log("Skipping " + this.inFile + " it cannot get mapped to output.", 3);
                return;
            }
            if (outFileName == null || outFileName.length > 1) {
                this.log("Skipping " + this.inFile + " its mapping is ambiguos.", 3);
                return;
            }
            outF = new File(destDir, outFileName[0]);
            if (this.force || inF.lastModified() > outF.lastModified() || styleSheetLastModified > outF.lastModified()) {
                this.ensureDirectoryFor(outF);
                this.log("Processing " + inF + " to " + outF);
                this.configureLiaison(stylesheet);
                this.setLiaisonDynamicFileParameters(this.liaison, inF);
                this.liaison.transform(inF, outF);
            }
        }
        catch (Exception ex) {
            this.log("Failed to process " + this.inFile, 2);
            if (outF != null) {
                outF.delete();
            }
            this.handleTransformationError(ex);
        }
    }
    
    private void process(final File inFile, final File outFile, final Resource stylesheet) throws BuildException {
        try {
            final long styleSheetLastModified = stylesheet.getLastModified();
            this.log("In file " + inFile + " time: " + inFile.lastModified(), 4);
            this.log("Out file " + outFile + " time: " + outFile.lastModified(), 4);
            this.log("Style file " + this.xslFile + " time: " + styleSheetLastModified, 4);
            if (this.force || inFile.lastModified() >= outFile.lastModified() || styleSheetLastModified >= outFile.lastModified()) {
                this.ensureDirectoryFor(outFile);
                this.log("Processing " + inFile + " to " + outFile, 2);
                this.configureLiaison(stylesheet);
                this.setLiaisonDynamicFileParameters(this.liaison, inFile);
                this.liaison.transform(inFile, outFile);
            }
            else {
                this.log("Skipping input file " + inFile + " because it is older than output file " + outFile + " and so is the stylesheet " + stylesheet, 4);
            }
        }
        catch (Exception ex) {
            this.log("Failed to process " + inFile, 2);
            if (outFile != null) {
                outFile.delete();
            }
            this.handleTransformationError(ex);
        }
    }
    
    private void ensureDirectoryFor(final File targetFile) throws BuildException {
        final File directory = targetFile.getParentFile();
        if (!directory.exists() && !directory.mkdirs()) {
            this.handleError("Unable to create directory: " + directory.getAbsolutePath());
        }
    }
    
    public Factory getFactory() {
        return this.factory;
    }
    
    public XMLCatalog getXMLCatalog() {
        this.xmlCatalog.setProject(this.getProject());
        return this.xmlCatalog;
    }
    
    public Enumeration getOutputProperties() {
        return this.outputProperties.elements();
    }
    
    protected XSLTLiaison getLiaison() {
        if (this.liaison == null) {
            if (this.processor != null) {
                try {
                    this.resolveProcessor(this.processor);
                }
                catch (Exception e) {
                    this.handleError(e);
                }
            }
            else {
                try {
                    this.resolveProcessor("trax");
                }
                catch (Throwable e2) {
                    e2.printStackTrace();
                    this.handleError(e2);
                }
            }
        }
        return this.liaison;
    }
    
    public Param createParam() {
        final Param p = new Param();
        this.params.addElement(p);
        return p;
    }
    
    public OutputProperty createOutputProperty() {
        final OutputProperty p = new OutputProperty();
        this.outputProperties.addElement(p);
        return p;
    }
    
    @Override
    public void init() throws BuildException {
        super.init();
        this.xmlCatalog.setProject(this.getProject());
    }
    
    @Deprecated
    protected void configureLiaison(final File stylesheet) throws BuildException {
        final FileResource fr = new FileResource();
        fr.setProject(this.getProject());
        fr.setFile(stylesheet);
        this.configureLiaison(fr);
    }
    
    protected void configureLiaison(final Resource stylesheet) throws BuildException {
        if (this.stylesheetLoaded && this.reuseLoadedStylesheet) {
            return;
        }
        this.stylesheetLoaded = true;
        try {
            this.log("Loading stylesheet " + stylesheet, 2);
            if (this.liaison instanceof XSLTLiaison2) {
                ((XSLTLiaison2)this.liaison).configure(this);
            }
            if (this.liaison instanceof XSLTLiaison3) {
                ((XSLTLiaison3)this.liaison).setStylesheet(stylesheet);
            }
            else {
                final FileProvider fp = stylesheet.as(FileProvider.class);
                if (fp == null) {
                    this.handleError(this.liaison.getClass().toString() + " accepts the stylesheet only as a file");
                    return;
                }
                this.liaison.setStylesheet(fp.getFile());
            }
            final Enumeration e = this.params.elements();
            while (e.hasMoreElements()) {
                final Param p = e.nextElement();
                if (p.shouldUse()) {
                    this.liaison.addParam(p.getName(), p.getExpression());
                }
            }
        }
        catch (Exception ex) {
            this.log("Failed to transform using stylesheet " + stylesheet, 2);
            this.handleTransformationError(ex);
        }
    }
    
    private void setLiaisonDynamicFileParameters(final XSLTLiaison liaison, final File inFile) throws Exception {
        if (this.fileNameParameter != null) {
            liaison.addParam(this.fileNameParameter, inFile.getName());
        }
        if (this.fileDirParameter != null) {
            final String fileName = FileUtils.getRelativePath(this.baseDir, inFile);
            final File file = new File(fileName);
            liaison.addParam(this.fileDirParameter, (file.getParent() != null) ? file.getParent().replace('\\', '/') : ".");
        }
    }
    
    public Factory createFactory() throws BuildException {
        if (this.factory != null) {
            this.handleError("'factory' element must be unique");
        }
        else {
            this.factory = new Factory();
        }
        return this.factory;
    }
    
    protected void handleError(final String msg) {
        if (this.failOnError) {
            throw new BuildException(msg, this.getLocation());
        }
        this.log(msg, 1);
    }
    
    protected void handleError(final Throwable ex) {
        if (this.failOnError) {
            throw new BuildException(ex);
        }
        this.log("Caught an exception: " + ex, 1);
    }
    
    protected void handleTransformationError(final Exception ex) {
        if (this.failOnError && this.failOnTransformationError) {
            throw new BuildException(ex);
        }
        this.log("Caught an error during transformation: " + ex, 1);
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
    }
    
    public static class Param
    {
        private String name;
        private String expression;
        private Object ifCond;
        private Object unlessCond;
        private Project project;
        
        public Param() {
            this.name = null;
            this.expression = null;
        }
        
        public void setProject(final Project project) {
            this.project = project;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public void setExpression(final String expression) {
            this.expression = expression;
        }
        
        public String getName() throws BuildException {
            if (this.name == null) {
                throw new BuildException("Name attribute is missing.");
            }
            return this.name;
        }
        
        public String getExpression() throws BuildException {
            if (this.expression == null) {
                throw new BuildException("Expression attribute is missing.");
            }
            return this.expression;
        }
        
        public void setIf(final Object ifCond) {
            this.ifCond = ifCond;
        }
        
        public void setIf(final String ifProperty) {
            this.setIf((Object)ifProperty);
        }
        
        public void setUnless(final Object unlessCond) {
            this.unlessCond = unlessCond;
        }
        
        public void setUnless(final String unlessProperty) {
            this.setUnless((Object)unlessProperty);
        }
        
        public boolean shouldUse() {
            final PropertyHelper ph = PropertyHelper.getPropertyHelper(this.project);
            return ph.testIfCondition(this.ifCond) && ph.testUnlessCondition(this.unlessCond);
        }
    }
    
    public static class OutputProperty
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
    
    public static class Factory
    {
        private String name;
        private Vector attributes;
        
        public Factory() {
            this.attributes = new Vector();
        }
        
        public String getName() {
            return this.name;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public void addAttribute(final Attribute attr) {
            this.attributes.addElement(attr);
        }
        
        public Enumeration getAttributes() {
            return this.attributes.elements();
        }
        
        public static class Attribute implements DynamicConfigurator
        {
            private String name;
            private Object value;
            
            public String getName() {
                return this.name;
            }
            
            public Object getValue() {
                return this.value;
            }
            
            public Object createDynamicElement(final String name) throws BuildException {
                return null;
            }
            
            public void setDynamicAttribute(final String name, final String value) throws BuildException {
                if ("name".equalsIgnoreCase(name)) {
                    this.name = value;
                }
                else {
                    if (!"value".equalsIgnoreCase(name)) {
                        throw new BuildException("Unsupported attribute: " + name);
                    }
                    if ("true".equalsIgnoreCase(value)) {
                        this.value = Boolean.TRUE;
                    }
                    else if ("false".equalsIgnoreCase(value)) {
                        this.value = Boolean.FALSE;
                    }
                    else {
                        try {
                            this.value = new Integer(value);
                        }
                        catch (NumberFormatException e) {
                            this.value = value;
                        }
                    }
                }
            }
        }
    }
    
    private class StyleMapper implements FileNameMapper
    {
        public void setFrom(final String from) {
        }
        
        public void setTo(final String to) {
        }
        
        public String[] mapFileName(String xmlFile) {
            final int dotPos = xmlFile.lastIndexOf(46);
            if (dotPos > 0) {
                xmlFile = xmlFile.substring(0, dotPos);
            }
            return new String[] { xmlFile + XSLTProcess.this.targetExtension };
        }
    }
    
    public final class TraceConfiguration
    {
        private boolean elements;
        private boolean extension;
        private boolean generation;
        private boolean selection;
        private boolean templates;
        
        public void setElements(final boolean b) {
            this.elements = b;
        }
        
        public boolean getElements() {
            return this.elements;
        }
        
        public void setExtension(final boolean b) {
            this.extension = b;
        }
        
        public boolean getExtension() {
            return this.extension;
        }
        
        public void setGeneration(final boolean b) {
            this.generation = b;
        }
        
        public boolean getGeneration() {
            return this.generation;
        }
        
        public void setSelection(final boolean b) {
            this.selection = b;
        }
        
        public boolean getSelection() {
            return this.selection;
        }
        
        public void setTemplates(final boolean b) {
            this.templates = b;
        }
        
        public boolean getTemplates() {
            return this.templates;
        }
        
        public OutputStream getOutputStream() {
            return new LogOutputStream(XSLTProcess.this);
        }
    }
}
