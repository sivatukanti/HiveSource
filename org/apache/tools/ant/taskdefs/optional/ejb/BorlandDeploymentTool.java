// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Vector;
import java.io.IOException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.BuildException;
import java.util.Iterator;
import org.apache.tools.ant.Task;
import java.io.File;
import java.util.Hashtable;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;

public class BorlandDeploymentTool extends GenericDeploymentTool implements ExecuteStreamHandler
{
    public static final String PUBLICID_BORLAND_EJB = "-//Inprise Corporation//DTD Enterprise JavaBeans 1.1//EN";
    protected static final String DEFAULT_BAS45_EJB11_DTD_LOCATION = "/com/inprise/j2ee/xml/dtds/ejb-jar.dtd";
    protected static final String DEFAULT_BAS_DTD_LOCATION = "/com/inprise/j2ee/xml/dtds/ejb-inprise.dtd";
    protected static final String BAS_DD = "ejb-inprise.xml";
    protected static final String BES_DD = "ejb-borland.xml";
    protected static final String JAVA2IIOP = "java2iiop";
    protected static final String VERIFY = "com.inprise.ejb.util.Verify";
    private String jarSuffix;
    private String borlandDTD;
    private boolean java2iiopdebug;
    private String java2iioparams;
    private boolean generateclient;
    static final int BES = 5;
    static final int BAS = 4;
    private int version;
    private boolean verify;
    private String verifyArgs;
    private Hashtable genfiles;
    
    public BorlandDeploymentTool() {
        this.jarSuffix = "-ejb.jar";
        this.java2iiopdebug = false;
        this.java2iioparams = null;
        this.generateclient = false;
        this.version = 4;
        this.verify = true;
        this.verifyArgs = "";
        this.genfiles = new Hashtable();
    }
    
    public void setDebug(final boolean debug) {
        this.java2iiopdebug = debug;
    }
    
    public void setVerify(final boolean verify) {
        this.verify = verify;
    }
    
    public void setSuffix(final String inString) {
        this.jarSuffix = inString;
    }
    
    public void setVerifyArgs(final String args) {
        this.verifyArgs = args;
    }
    
    public void setBASdtd(final String inString) {
        this.borlandDTD = inString;
    }
    
    public void setGenerateclient(final boolean b) {
        this.generateclient = b;
    }
    
    public void setVersion(final int version) {
        this.version = version;
    }
    
    public void setJava2iiopParams(final String params) {
        this.java2iioparams = params;
    }
    
    protected DescriptorHandler getBorlandDescriptorHandler(final File srcDir) {
        final DescriptorHandler handler = new DescriptorHandler(this.getTask(), srcDir) {
            @Override
            protected void processElement() {
                if (this.currentElement.equals("type-storage")) {
                    final String fileNameWithMETA = this.currentText;
                    final String fileName = fileNameWithMETA.substring("META-INF/".length(), fileNameWithMETA.length());
                    final File descriptorFile = new File(srcDir, fileName);
                    this.ejbFiles.put(fileNameWithMETA, descriptorFile);
                }
            }
        };
        handler.registerDTD("-//Inprise Corporation//DTD Enterprise JavaBeans 1.1//EN", (this.borlandDTD == null) ? "/com/inprise/j2ee/xml/dtds/ejb-inprise.dtd" : this.borlandDTD);
        for (final EjbJar.DTDLocation dtdLocation : this.getConfig().dtdLocations) {
            handler.registerDTD(dtdLocation.getPublicId(), dtdLocation.getLocation());
        }
        return handler;
    }
    
    @Override
    protected void addVendorFiles(final Hashtable ejbFiles, final String ddPrefix) {
        if (this.version != 5 && this.version != 4) {
            throw new BuildException("version " + this.version + " is not supported");
        }
        final String dd = (this.version == 5) ? "ejb-borland.xml" : "ejb-inprise.xml";
        this.log("vendor file : " + ddPrefix + dd, 4);
        final File borlandDD = new File(this.getConfig().descriptorDir, ddPrefix + dd);
        if (borlandDD.exists()) {
            this.log("Borland specific file found " + borlandDD, 3);
            ejbFiles.put("META-INF/" + dd, borlandDD);
            return;
        }
        this.log("Unable to locate borland deployment descriptor. It was expected to be in " + borlandDD.getPath(), 1);
    }
    
    @Override
    File getVendorOutputJarFile(final String baseName) {
        return new File(this.getDestDir(), baseName + this.jarSuffix);
    }
    
    private void verifyBorlandJar(final File sourceJar) {
        if (this.version == 4) {
            this.verifyBorlandJarV4(sourceJar);
            return;
        }
        if (this.version == 5) {
            this.verifyBorlandJarV5(sourceJar);
            return;
        }
        this.log("verify jar skipped because the version is invalid [" + this.version + "]", 1);
    }
    
    private void verifyBorlandJarV5(final File sourceJar) {
        this.log("verify BES " + sourceJar, 2);
        try {
            ExecTask execTask = null;
            execTask = new ExecTask(this.getTask());
            execTask.setDir(new File("."));
            execTask.setExecutable("iastool");
            if (this.getCombinedClasspath() != null) {
                execTask.createArg().setValue("-VBJclasspath");
                execTask.createArg().setValue(this.getCombinedClasspath().toString());
            }
            if (this.java2iiopdebug) {
                execTask.createArg().setValue("-debug");
            }
            execTask.createArg().setValue("-verify");
            execTask.createArg().setValue("-src");
            execTask.createArg().setValue(sourceJar.getPath());
            this.log("Calling iastool", 3);
            execTask.execute();
        }
        catch (Exception e) {
            final String msg = "Exception while calling generateclient Details: " + e.toString();
            throw new BuildException(msg, e);
        }
    }
    
    private void verifyBorlandJarV4(final File sourceJar) {
        Java javaTask = null;
        this.log("verify BAS " + sourceJar, 2);
        try {
            String args = this.verifyArgs;
            args = args + " " + sourceJar.getPath();
            javaTask = new Java(this.getTask());
            javaTask.setTaskName("verify");
            javaTask.setClassname("com.inprise.ejb.util.Verify");
            final Commandline.Argument arguments = javaTask.createArg();
            arguments.setLine(args);
            final Path classpath = this.getCombinedClasspath();
            if (classpath != null) {
                javaTask.setClasspath(classpath);
                javaTask.setFork(true);
            }
            this.log("Calling com.inprise.ejb.util.Verify for " + sourceJar.toString(), 3);
            javaTask.execute();
        }
        catch (Exception e) {
            final String msg = "Exception while calling com.inprise.ejb.util.Verify Details: " + e.toString();
            throw new BuildException(msg, e);
        }
    }
    
    private void generateClient(final File sourceJar) {
        this.getTask().getProject().addTaskDefinition("internal_bas_generateclient", BorlandGenerateClient.class);
        BorlandGenerateClient gentask = null;
        this.log("generate client for " + sourceJar, 2);
        try {
            final Project project = this.getTask().getProject();
            gentask = (BorlandGenerateClient)project.createTask("internal_bas_generateclient");
            gentask.setEjbjar(sourceJar);
            gentask.setDebug(this.java2iiopdebug);
            final Path classpath = this.getCombinedClasspath();
            if (classpath != null) {
                gentask.setClasspath(classpath);
            }
            gentask.setVersion(this.version);
            gentask.setTaskName("generate client");
            gentask.execute();
        }
        catch (Exception e) {
            final String msg = "Exception while calling com.inprise.ejb.util.Verify Details: " + e.toString();
            throw new BuildException(msg, e);
        }
    }
    
    private void buildBorlandStubs(final Iterator ithomes) {
        Execute execTask = null;
        execTask = new Execute(this);
        final Project project = this.getTask().getProject();
        execTask.setAntRun(project);
        execTask.setWorkingDirectory(project.getBaseDir());
        final Commandline commandline = new Commandline();
        commandline.setExecutable("java2iiop");
        if (this.java2iiopdebug) {
            commandline.createArgument().setValue("-VBJdebug");
        }
        commandline.createArgument().setValue("-VBJclasspath");
        commandline.createArgument().setPath(this.getCombinedClasspath());
        commandline.createArgument().setValue("-list_files");
        commandline.createArgument().setValue("-no_tie");
        if (this.java2iioparams != null) {
            this.log("additional  " + this.java2iioparams + " to java2iiop ", 0);
            commandline.createArgument().setLine(this.java2iioparams);
        }
        commandline.createArgument().setValue("-root_dir");
        commandline.createArgument().setValue(this.getConfig().srcDir.getAbsolutePath());
        commandline.createArgument().setValue("-compile");
        while (ithomes.hasNext()) {
            commandline.createArgument().setValue(ithomes.next().toString());
        }
        try {
            this.log("Calling java2iiop", 3);
            this.log(commandline.describeCommand(), 4);
            execTask.setCommandline(commandline.getCommandline());
            final int result = execTask.execute();
            if (Execute.isFailure(result)) {
                final String msg = "Failed executing java2iiop (ret code is " + result + ")";
                throw new BuildException(msg, this.getTask().getLocation());
            }
        }
        catch (IOException e) {
            this.log("java2iiop exception :" + e.getMessage(), 0);
            throw new BuildException(e, this.getTask().getLocation());
        }
    }
    
    @Override
    protected void writeJar(final String baseName, final File jarFile, final Hashtable files, final String publicId) throws BuildException {
        final Vector homes = new Vector();
        for (final String clazz : files.keySet()) {
            if (clazz.endsWith("Home.class")) {
                final String home = this.toClass(clazz);
                homes.add(home);
                this.log(" Home " + home, 3);
            }
        }
        this.buildBorlandStubs(homes.iterator());
        files.putAll(this.genfiles);
        super.writeJar(baseName, jarFile, files, publicId);
        if (this.verify) {
            this.verifyBorlandJar(jarFile);
        }
        if (this.generateclient) {
            this.generateClient(jarFile);
        }
        this.genfiles.clear();
    }
    
    private String toClass(final String filename) {
        String classname = filename.substring(0, filename.lastIndexOf(".class"));
        classname = classname.replace('\\', '.');
        return classname;
    }
    
    private String toClassFile(final String filename) {
        String classfile = filename.substring(0, filename.lastIndexOf(".java"));
        classfile += ".class";
        return classfile;
    }
    
    public void start() throws IOException {
    }
    
    public void stop() {
    }
    
    public void setProcessInputStream(final OutputStream param1) throws IOException {
    }
    
    public void setProcessOutputStream(final InputStream is) throws IOException {
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String javafile;
            while ((javafile = reader.readLine()) != null) {
                if (javafile.endsWith(".java")) {
                    final String classfile = this.toClassFile(javafile);
                    final String key = classfile.substring(this.getConfig().srcDir.getAbsolutePath().length() + 1);
                    this.genfiles.put(key, new File(classfile));
                }
            }
            reader.close();
        }
        catch (Exception e) {
            final String msg = "Exception while parsing  java2iiop output. Details: " + e.toString();
            throw new BuildException(msg, e);
        }
    }
    
    public void setProcessErrorStream(final InputStream is) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        final String s = reader.readLine();
        if (s != null) {
            this.log("[java2iiop] " + s, 0);
        }
    }
}
