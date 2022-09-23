// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Date;
import org.xml.sax.AttributeList;
import java.io.InputStream;
import java.io.FileInputStream;
import org.xml.sax.InputSource;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.HandlerBase;
import java.util.Properties;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.SAXParserFactory;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.xml.parsers.SAXParser;
import java.io.File;

public class IPlanetEjbc
{
    private static final int MIN_NUM_ARGS = 2;
    private static final int MAX_NUM_ARGS = 8;
    private static final int NUM_CLASSES_WITH_IIOP = 15;
    private static final int NUM_CLASSES_WITHOUT_IIOP = 9;
    private static final String ENTITY_BEAN = "entity";
    private static final String STATELESS_SESSION = "stateless";
    private static final String STATEFUL_SESSION = "stateful";
    private File stdDescriptor;
    private File iasDescriptor;
    private File destDirectory;
    private String classpath;
    private String[] classpathElements;
    private boolean retainSource;
    private boolean debugOutput;
    private File iasHomeDir;
    private SAXParser parser;
    private EjbcHandler handler;
    private Hashtable ejbFiles;
    private String displayName;
    
    public IPlanetEjbc(final File stdDescriptor, final File iasDescriptor, final File destDirectory, final String classpath, final SAXParser parser) {
        this.retainSource = false;
        this.debugOutput = false;
        this.handler = new EjbcHandler();
        this.ejbFiles = new Hashtable();
        this.stdDescriptor = stdDescriptor;
        this.iasDescriptor = iasDescriptor;
        this.destDirectory = destDirectory;
        this.classpath = classpath;
        this.parser = parser;
        final List elements = new ArrayList();
        if (classpath != null) {
            final StringTokenizer st = new StringTokenizer(classpath, File.pathSeparator);
            while (st.hasMoreTokens()) {
                elements.add(st.nextToken());
            }
            this.classpathElements = elements.toArray(new String[elements.size()]);
        }
    }
    
    public void setRetainSource(final boolean retainSource) {
        this.retainSource = retainSource;
    }
    
    public void setDebugOutput(final boolean debugOutput) {
        this.debugOutput = debugOutput;
    }
    
    public void registerDTD(final String publicID, final String location) {
        this.handler.registerDTD(publicID, location);
    }
    
    public void setIasHomeDir(final File iasHomeDir) {
        this.iasHomeDir = iasHomeDir;
    }
    
    public Hashtable getEjbFiles() {
        return this.ejbFiles;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public String[] getCmpDescriptors() {
        final List returnList = new ArrayList();
        final EjbInfo[] ejbs = this.handler.getEjbs();
        for (int i = 0; i < ejbs.length; ++i) {
            final List descriptors = ejbs[i].getCmpDescriptors();
            returnList.addAll(descriptors);
        }
        return returnList.toArray(new String[returnList.size()]);
    }
    
    public static void main(final String[] args) {
        File destDirectory = null;
        String classpath = null;
        SAXParser parser = null;
        boolean debug = false;
        boolean retainSource = false;
        if (args.length < 2 || args.length > 8) {
            usage();
            return;
        }
        final File stdDescriptor = new File(args[args.length - 2]);
        final File iasDescriptor = new File(args[args.length - 1]);
        for (int i = 0; i < args.length - 2; ++i) {
            if (args[i].equals("-classpath")) {
                classpath = args[++i];
            }
            else if (args[i].equals("-d")) {
                destDirectory = new File(args[++i]);
            }
            else if (args[i].equals("-debug")) {
                debug = true;
            }
            else {
                if (!args[i].equals("-keepsource")) {
                    usage();
                    return;
                }
                retainSource = true;
            }
        }
        if (classpath == null) {
            final Properties props = System.getProperties();
            classpath = props.getProperty("java.class.path");
        }
        if (destDirectory == null) {
            final Properties props = System.getProperties();
            destDirectory = new File(props.getProperty("user.dir"));
        }
        final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        parserFactory.setValidating(true);
        try {
            parser = parserFactory.newSAXParser();
        }
        catch (Exception e) {
            System.out.println("An exception was generated while trying to ");
            System.out.println("create a new SAXParser.");
            e.printStackTrace();
            return;
        }
        final IPlanetEjbc ejbc = new IPlanetEjbc(stdDescriptor, iasDescriptor, destDirectory, classpath, parser);
        ejbc.setDebugOutput(debug);
        ejbc.setRetainSource(retainSource);
        try {
            ejbc.execute();
        }
        catch (IOException e2) {
            System.out.println("An IOException has occurred while reading the XML descriptors (" + e2.getMessage() + ").");
        }
        catch (SAXException e3) {
            System.out.println("A SAXException has occurred while reading the XML descriptors (" + e3.getMessage() + ").");
        }
        catch (EjbcException e4) {
            System.out.println("An error has occurred while executing the ejbc utility (" + e4.getMessage() + ").");
        }
    }
    
    private static void usage() {
        System.out.println("java org.apache.tools.ant.taskdefs.optional.ejb.IPlanetEjbc \\");
        System.out.println("  [OPTIONS] [EJB 1.1 descriptor] [iAS EJB descriptor]");
        System.out.println("");
        System.out.println("Where OPTIONS are:");
        System.out.println("  -debug -- for additional debugging output");
        System.out.println("  -keepsource -- to retain Java source files generated");
        System.out.println("  -classpath [classpath] -- classpath used for compilation");
        System.out.println("  -d [destination directory] -- directory for compiled classes");
        System.out.println("");
        System.out.println("If a classpath is not specified, the system classpath");
        System.out.println("will be used.  If a destination directory is not specified,");
        System.out.println("the current working directory will be used (classes will");
        System.out.println("still be placed in subfolders which correspond to their");
        System.out.println("package name).");
        System.out.println("");
        System.out.println("The EJB home interface, remote interface, and implementation");
        System.out.println("class must be found in the destination directory.  In");
        System.out.println("addition, the destination will look for the stubs and skeletons");
        System.out.println("in the destination directory to ensure they are up to date.");
    }
    
    public void execute() throws EjbcException, IOException, SAXException {
        this.checkConfiguration();
        final EjbInfo[] ejbs = this.getEjbs();
        for (int i = 0; i < ejbs.length; ++i) {
            this.log("EJBInfo...");
            this.log(ejbs[i].toString());
        }
        for (int i = 0; i < ejbs.length; ++i) {
            final EjbInfo ejb = ejbs[i];
            ejb.checkConfiguration(this.destDirectory);
            if (ejb.mustBeRecompiled(this.destDirectory)) {
                this.log(ejb.getName() + " must be recompiled using ejbc.");
                final String[] arguments = this.buildArgumentList(ejb);
                this.callEjbc(arguments);
            }
            else {
                this.log(ejb.getName() + " is up to date.");
            }
        }
    }
    
    private void callEjbc(final String[] arguments) {
        final StringBuffer args = new StringBuffer();
        for (int i = 0; i < arguments.length; ++i) {
            args.append(arguments[i]).append(" ");
        }
        String command;
        if (this.iasHomeDir == null) {
            command = "";
        }
        else {
            command = this.iasHomeDir.toString() + File.separator + "bin" + File.separator;
        }
        command += "ejbc ";
        this.log(command + (Object)args);
        try {
            final Process p = Runtime.getRuntime().exec(command + (Object)args);
            final RedirectOutput output = new RedirectOutput(p.getInputStream());
            final RedirectOutput error = new RedirectOutput(p.getErrorStream());
            output.start();
            error.start();
            p.waitFor();
            p.destroy();
        }
        catch (IOException e) {
            this.log("An IOException has occurred while trying to execute ejbc.");
            e.printStackTrace();
        }
        catch (InterruptedException ex) {}
    }
    
    protected void checkConfiguration() throws EjbcException {
        String msg = "";
        if (this.stdDescriptor == null) {
            msg += "A standard XML descriptor file must be specified.  ";
        }
        if (this.iasDescriptor == null) {
            msg += "An iAS-specific XML descriptor file must be specified.  ";
        }
        if (this.classpath == null) {
            msg += "A classpath must be specified.    ";
        }
        if (this.parser == null) {
            msg += "An XML parser must be specified.    ";
        }
        if (this.destDirectory == null) {
            msg += "A destination directory must be specified.  ";
        }
        else if (!this.destDirectory.exists()) {
            msg += "The destination directory specified does not exist.  ";
        }
        else if (!this.destDirectory.isDirectory()) {
            msg += "The destination specified is not a directory.  ";
        }
        if (msg.length() > 0) {
            throw new EjbcException(msg);
        }
    }
    
    private EjbInfo[] getEjbs() throws IOException, SAXException {
        EjbInfo[] ejbs = null;
        this.parser.parse(this.stdDescriptor, this.handler);
        this.parser.parse(this.iasDescriptor, this.handler);
        ejbs = this.handler.getEjbs();
        return ejbs;
    }
    
    private String[] buildArgumentList(final EjbInfo ejb) {
        final List arguments = new ArrayList();
        if (this.debugOutput) {
            arguments.add("-debug");
        }
        if (ejb.getBeantype().equals("stateless")) {
            arguments.add("-sl");
        }
        else if (ejb.getBeantype().equals("stateful")) {
            arguments.add("-sf");
        }
        if (ejb.getIiop()) {
            arguments.add("-iiop");
        }
        if (ejb.getCmp()) {
            arguments.add("-cmp");
        }
        if (this.retainSource) {
            arguments.add("-gs");
        }
        if (ejb.getHasession()) {
            arguments.add("-fo");
        }
        arguments.add("-classpath");
        arguments.add(this.classpath);
        arguments.add("-d");
        arguments.add(this.destDirectory.toString());
        arguments.add(ejb.getHome().getQualifiedClassName());
        arguments.add(ejb.getRemote().getQualifiedClassName());
        arguments.add(ejb.getImplementation().getQualifiedClassName());
        return arguments.toArray(new String[arguments.size()]);
    }
    
    private void log(final String msg) {
        if (this.debugOutput) {
            System.out.println(msg);
        }
    }
    
    public class EjbcException extends Exception
    {
        public EjbcException(final String msg) {
            super(msg);
        }
    }
    
    private class EjbcHandler extends HandlerBase
    {
        private static final String PUBLICID_EJB11 = "-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN";
        private static final String PUBLICID_IPLANET_EJB_60 = "-//Sun Microsystems, Inc.//DTD iAS Enterprise JavaBeans 1.0//EN";
        private static final String DEFAULT_IAS60_EJB11_DTD_LOCATION = "ejb-jar_1_1.dtd";
        private static final String DEFAULT_IAS60_DTD_LOCATION = "IASEjb_jar_1_0.dtd";
        private Map resourceDtds;
        private Map fileDtds;
        private Map ejbs;
        private EjbInfo currentEjb;
        private boolean iasDescriptor;
        private String currentLoc;
        private String currentText;
        private String ejbType;
        
        public EjbcHandler() {
            this.resourceDtds = new HashMap();
            this.fileDtds = new HashMap();
            this.ejbs = new HashMap();
            this.iasDescriptor = false;
            this.currentLoc = "";
            this.registerDTD("-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN", "ejb-jar_1_1.dtd");
            this.registerDTD("-//Sun Microsystems, Inc.//DTD iAS Enterprise JavaBeans 1.0//EN", "IASEjb_jar_1_0.dtd");
        }
        
        public EjbInfo[] getEjbs() {
            return (EjbInfo[])this.ejbs.values().toArray(new EjbInfo[this.ejbs.size()]);
        }
        
        public String getDisplayName() {
            return IPlanetEjbc.this.displayName;
        }
        
        public void registerDTD(final String publicID, final String location) {
            IPlanetEjbc.this.log("Registering: " + location);
            if (publicID == null || location == null) {
                return;
            }
            if (ClassLoader.getSystemResource(location) != null) {
                IPlanetEjbc.this.log("Found resource: " + location);
                this.resourceDtds.put(publicID, location);
            }
            else {
                final File dtdFile = new File(location);
                if (dtdFile.exists() && dtdFile.isFile()) {
                    IPlanetEjbc.this.log("Found file: " + location);
                    this.fileDtds.put(publicID, location);
                }
            }
        }
        
        @Override
        public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException {
            InputStream inputStream = null;
            try {
                String location = this.resourceDtds.get(publicId);
                if (location != null) {
                    inputStream = ClassLoader.getSystemResource(location).openStream();
                }
                else {
                    location = this.fileDtds.get(publicId);
                    if (location != null) {
                        inputStream = new FileInputStream(location);
                    }
                }
            }
            catch (IOException e) {
                return super.resolveEntity(publicId, systemId);
            }
            if (inputStream == null) {
                return super.resolveEntity(publicId, systemId);
            }
            return new InputSource(inputStream);
        }
        
        @Override
        public void startElement(final String name, final AttributeList atts) throws SAXException {
            this.currentLoc = this.currentLoc + "\\" + name;
            this.currentText = "";
            if (this.currentLoc.equals("\\ejb-jar")) {
                this.iasDescriptor = false;
            }
            else if (this.currentLoc.equals("\\ias-ejb-jar")) {
                this.iasDescriptor = true;
            }
            if (name.equals("session") || name.equals("entity")) {
                this.ejbType = name;
            }
        }
        
        @Override
        public void characters(final char[] ch, final int start, final int len) throws SAXException {
            this.currentText += new String(ch).substring(start, start + len);
        }
        
        @Override
        public void endElement(final String name) throws SAXException {
            if (this.iasDescriptor) {
                this.iasCharacters(this.currentText);
            }
            else {
                this.stdCharacters(this.currentText);
            }
            final int nameLength = name.length() + 1;
            final int locLength = this.currentLoc.length();
            this.currentLoc = this.currentLoc.substring(0, locLength - nameLength);
        }
        
        private void stdCharacters(final String value) {
            if (this.currentLoc.equals("\\ejb-jar\\display-name")) {
                IPlanetEjbc.this.displayName = value;
                return;
            }
            final String base = "\\ejb-jar\\enterprise-beans\\" + this.ejbType;
            if (this.currentLoc.equals(base + "\\ejb-name")) {
                this.currentEjb = this.ejbs.get(value);
                if (this.currentEjb == null) {
                    this.currentEjb = new EjbInfo(value);
                    this.ejbs.put(value, this.currentEjb);
                }
            }
            else if (this.currentLoc.equals(base + "\\home")) {
                this.currentEjb.setHome(value);
            }
            else if (this.currentLoc.equals(base + "\\remote")) {
                this.currentEjb.setRemote(value);
            }
            else if (this.currentLoc.equals(base + "\\ejb-class")) {
                this.currentEjb.setImplementation(value);
            }
            else if (this.currentLoc.equals(base + "\\prim-key-class")) {
                this.currentEjb.setPrimaryKey(value);
            }
            else if (this.currentLoc.equals(base + "\\session-type")) {
                this.currentEjb.setBeantype(value);
            }
            else if (this.currentLoc.equals(base + "\\persistence-type")) {
                this.currentEjb.setCmp(value);
            }
        }
        
        private void iasCharacters(final String value) {
            final String base = "\\ias-ejb-jar\\enterprise-beans\\" + this.ejbType;
            if (this.currentLoc.equals(base + "\\ejb-name")) {
                this.currentEjb = this.ejbs.get(value);
                if (this.currentEjb == null) {
                    this.currentEjb = new EjbInfo(value);
                    this.ejbs.put(value, this.currentEjb);
                }
            }
            else if (this.currentLoc.equals(base + "\\iiop")) {
                this.currentEjb.setIiop(value);
            }
            else if (this.currentLoc.equals(base + "\\failover-required")) {
                this.currentEjb.setHasession(value);
            }
            else if (this.currentLoc.equals(base + "\\persistence-manager" + "\\properties-file-location")) {
                this.currentEjb.addCmpDescriptor(value);
            }
        }
    }
    
    private class EjbInfo
    {
        private String name;
        private Classname home;
        private Classname remote;
        private Classname implementation;
        private Classname primaryKey;
        private String beantype;
        private boolean cmp;
        private boolean iiop;
        private boolean hasession;
        private List cmpDescriptors;
        
        public EjbInfo(final String name) {
            this.beantype = "entity";
            this.cmp = false;
            this.iiop = false;
            this.hasession = false;
            this.cmpDescriptors = new ArrayList();
            this.name = name;
        }
        
        public String getName() {
            if (this.name != null) {
                return this.name;
            }
            if (this.implementation == null) {
                return "[unnamed]";
            }
            return this.implementation.getClassName();
        }
        
        public void setHome(final String home) {
            this.setHome(new Classname(home));
        }
        
        public void setHome(final Classname home) {
            this.home = home;
        }
        
        public Classname getHome() {
            return this.home;
        }
        
        public void setRemote(final String remote) {
            this.setRemote(new Classname(remote));
        }
        
        public void setRemote(final Classname remote) {
            this.remote = remote;
        }
        
        public Classname getRemote() {
            return this.remote;
        }
        
        public void setImplementation(final String implementation) {
            this.setImplementation(new Classname(implementation));
        }
        
        public void setImplementation(final Classname implementation) {
            this.implementation = implementation;
        }
        
        public Classname getImplementation() {
            return this.implementation;
        }
        
        public void setPrimaryKey(final String primaryKey) {
            this.setPrimaryKey(new Classname(primaryKey));
        }
        
        public void setPrimaryKey(final Classname primaryKey) {
            this.primaryKey = primaryKey;
        }
        
        public Classname getPrimaryKey() {
            return this.primaryKey;
        }
        
        public void setBeantype(final String beantype) {
            this.beantype = beantype.toLowerCase();
        }
        
        public String getBeantype() {
            return this.beantype;
        }
        
        public void setCmp(final boolean cmp) {
            this.cmp = cmp;
        }
        
        public void setCmp(final String cmp) {
            this.setCmp(cmp.equals("Container"));
        }
        
        public boolean getCmp() {
            return this.cmp;
        }
        
        public void setIiop(final boolean iiop) {
            this.iiop = iiop;
        }
        
        public void setIiop(final String iiop) {
            this.setIiop(iiop.equals("true"));
        }
        
        public boolean getIiop() {
            return this.iiop;
        }
        
        public void setHasession(final boolean hasession) {
            this.hasession = hasession;
        }
        
        public void setHasession(final String hasession) {
            this.setHasession(hasession.equals("true"));
        }
        
        public boolean getHasession() {
            return this.hasession;
        }
        
        public void addCmpDescriptor(final String descriptor) {
            this.cmpDescriptors.add(descriptor);
        }
        
        public List getCmpDescriptors() {
            return this.cmpDescriptors;
        }
        
        private void checkConfiguration(final File buildDir) throws EjbcException {
            if (this.home == null) {
                throw new EjbcException("A home interface was not found for the " + this.name + " EJB.");
            }
            if (this.remote == null) {
                throw new EjbcException("A remote interface was not found for the " + this.name + " EJB.");
            }
            if (this.implementation == null) {
                throw new EjbcException("An EJB implementation class was not found for the " + this.name + " EJB.");
            }
            if (!this.beantype.equals("entity") && !this.beantype.equals("stateless") && !this.beantype.equals("stateful")) {
                throw new EjbcException("The beantype found (" + this.beantype + ") " + "isn't valid in the " + this.name + " EJB.");
            }
            if (this.cmp && !this.beantype.equals("entity")) {
                System.out.println("CMP stubs and skeletons may not be generated for a Session Bean -- the \"cmp\" attribute will be ignoredfor the " + this.name + " EJB.");
            }
            if (this.hasession && !this.beantype.equals("stateful")) {
                System.out.println("Highly available stubs and skeletons may only be generated for a Stateful Session Bean -- the \"hasession\" attribute will be ignored for the " + this.name + " EJB.");
            }
            if (!this.remote.getClassFile(buildDir).exists()) {
                throw new EjbcException("The remote interface " + this.remote.getQualifiedClassName() + " could not be " + "found.");
            }
            if (!this.home.getClassFile(buildDir).exists()) {
                throw new EjbcException("The home interface " + this.home.getQualifiedClassName() + " could not be " + "found.");
            }
            if (!this.implementation.getClassFile(buildDir).exists()) {
                throw new EjbcException("The EJB implementation class " + this.implementation.getQualifiedClassName() + " could " + "not be found.");
            }
        }
        
        public boolean mustBeRecompiled(final File destDir) {
            final long sourceModified = this.sourceClassesModified(destDir);
            final long destModified = this.destClassesModified(destDir);
            return destModified < sourceModified;
        }
        
        private long sourceClassesModified(final File buildDir) {
            final File remoteFile = this.remote.getClassFile(buildDir);
            long modified = remoteFile.lastModified();
            if (modified == -1L) {
                System.out.println("The class " + this.remote.getQualifiedClassName() + " couldn't " + "be found on the classpath");
                return -1L;
            }
            long latestModified = modified;
            final File homeFile = this.home.getClassFile(buildDir);
            modified = homeFile.lastModified();
            if (modified == -1L) {
                System.out.println("The class " + this.home.getQualifiedClassName() + " couldn't be " + "found on the classpath");
                return -1L;
            }
            latestModified = Math.max(latestModified, modified);
            File pkFile;
            if (this.primaryKey != null) {
                pkFile = this.primaryKey.getClassFile(buildDir);
                modified = pkFile.lastModified();
                if (modified == -1L) {
                    System.out.println("The class " + this.primaryKey.getQualifiedClassName() + "couldn't be " + "found on the classpath");
                    return -1L;
                }
                latestModified = Math.max(latestModified, modified);
            }
            else {
                pkFile = null;
            }
            final File implFile = this.implementation.getClassFile(buildDir);
            modified = implFile.lastModified();
            if (modified == -1L) {
                System.out.println("The class " + this.implementation.getQualifiedClassName() + " couldn't be found on the classpath");
                return -1L;
            }
            String pathToFile = this.remote.getQualifiedClassName();
            pathToFile = pathToFile.replace('.', File.separatorChar) + ".class";
            IPlanetEjbc.this.ejbFiles.put(pathToFile, remoteFile);
            pathToFile = this.home.getQualifiedClassName();
            pathToFile = pathToFile.replace('.', File.separatorChar) + ".class";
            IPlanetEjbc.this.ejbFiles.put(pathToFile, homeFile);
            pathToFile = this.implementation.getQualifiedClassName();
            pathToFile = pathToFile.replace('.', File.separatorChar) + ".class";
            IPlanetEjbc.this.ejbFiles.put(pathToFile, implFile);
            if (pkFile != null) {
                pathToFile = this.primaryKey.getQualifiedClassName();
                pathToFile = pathToFile.replace('.', File.separatorChar) + ".class";
                IPlanetEjbc.this.ejbFiles.put(pathToFile, pkFile);
            }
            return latestModified;
        }
        
        private long destClassesModified(final File destDir) {
            final String[] classnames = this.classesToGenerate();
            long destClassesModified = new Date().getTime();
            boolean allClassesFound = true;
            for (int i = 0; i < classnames.length; ++i) {
                final String pathToClass = classnames[i].replace('.', File.separatorChar) + ".class";
                final File classFile = new File(destDir, pathToClass);
                IPlanetEjbc.this.ejbFiles.put(pathToClass, classFile);
                allClassesFound = (allClassesFound && classFile.exists());
                if (allClassesFound) {
                    final long fileMod = classFile.lastModified();
                    destClassesModified = Math.min(destClassesModified, fileMod);
                }
            }
            return allClassesFound ? destClassesModified : -1L;
        }
        
        private String[] classesToGenerate() {
            final String[] classnames = this.iiop ? new String[15] : new String[9];
            final String remotePkg = this.remote.getPackageName() + ".";
            final String remoteClass = this.remote.getClassName();
            final String homePkg = this.home.getPackageName() + ".";
            final String homeClass = this.home.getClassName();
            final String implPkg = this.implementation.getPackageName() + ".";
            final String implFullClass = this.implementation.getQualifiedWithUnderscores();
            int index = 0;
            classnames[index++] = implPkg + "ejb_fac_" + implFullClass;
            classnames[index++] = implPkg + "ejb_home_" + implFullClass;
            classnames[index++] = implPkg + "ejb_skel_" + implFullClass;
            classnames[index++] = remotePkg + "ejb_kcp_skel_" + remoteClass;
            classnames[index++] = homePkg + "ejb_kcp_skel_" + homeClass;
            classnames[index++] = remotePkg + "ejb_kcp_stub_" + remoteClass;
            classnames[index++] = homePkg + "ejb_kcp_stub_" + homeClass;
            classnames[index++] = remotePkg + "ejb_stub_" + remoteClass;
            classnames[index++] = homePkg + "ejb_stub_" + homeClass;
            if (!this.iiop) {
                return classnames;
            }
            classnames[index++] = "org.omg.stub." + remotePkg + "_" + remoteClass + "_Stub";
            classnames[index++] = "org.omg.stub." + homePkg + "_" + homeClass + "_Stub";
            classnames[index++] = "org.omg.stub." + remotePkg + "_ejb_RmiCorbaBridge_" + remoteClass + "_Tie";
            classnames[index++] = "org.omg.stub." + homePkg + "_ejb_RmiCorbaBridge_" + homeClass + "_Tie";
            classnames[index++] = remotePkg + "ejb_RmiCorbaBridge_" + remoteClass;
            classnames[index++] = homePkg + "ejb_RmiCorbaBridge_" + homeClass;
            return classnames;
        }
        
        @Override
        public String toString() {
            String s = "EJB name: " + this.name + "\n\r              home:      " + this.home + "\n\r              remote:    " + this.remote + "\n\r              impl:      " + this.implementation + "\n\r              primaryKey: " + this.primaryKey + "\n\r              beantype:  " + this.beantype + "\n\r              cmp:       " + this.cmp + "\n\r              iiop:      " + this.iiop + "\n\r              hasession: " + this.hasession;
            final Iterator i = this.cmpDescriptors.iterator();
            while (i.hasNext()) {
                s = s + "\n\r              CMP Descriptor: " + i.next();
            }
            return s;
        }
    }
    
    private static class Classname
    {
        private String qualifiedName;
        private String packageName;
        private String className;
        
        public Classname(final String qualifiedName) {
            if (qualifiedName == null) {
                return;
            }
            this.qualifiedName = qualifiedName;
            final int index = qualifiedName.lastIndexOf(46);
            if (index == -1) {
                this.className = qualifiedName;
                this.packageName = "";
            }
            else {
                this.packageName = qualifiedName.substring(0, index);
                this.className = qualifiedName.substring(index + 1);
            }
        }
        
        public String getQualifiedClassName() {
            return this.qualifiedName;
        }
        
        public String getPackageName() {
            return this.packageName;
        }
        
        public String getClassName() {
            return this.className;
        }
        
        public String getQualifiedWithUnderscores() {
            return this.qualifiedName.replace('.', '_');
        }
        
        public File getClassFile(final File directory) {
            final String pathToFile = this.qualifiedName.replace('.', File.separatorChar) + ".class";
            return new File(directory, pathToFile);
        }
        
        @Override
        public String toString() {
            return this.getQualifiedClassName();
        }
    }
    
    private static class RedirectOutput extends Thread
    {
        private InputStream stream;
        
        public RedirectOutput(final InputStream stream) {
            this.stream = stream;
        }
        
        @Override
        public void run() {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(this.stream));
            try {
                String text;
                while ((text = reader.readLine()) != null) {
                    System.out.println(text);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    reader.close();
                }
                catch (IOException ex) {}
            }
        }
    }
}
