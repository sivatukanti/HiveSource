// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.javacc;

import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;
import java.util.Enumeration;
import java.io.IOException;
import org.apache.tools.ant.taskdefs.ExecuteWatchdog;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.types.CommandlineJava;
import java.io.File;
import java.util.Hashtable;
import org.apache.tools.ant.Task;

public class JJTree extends Task
{
    private static final String OUTPUT_FILE = "OUTPUT_FILE";
    private static final String BUILD_NODE_FILES = "BUILD_NODE_FILES";
    private static final String MULTI = "MULTI";
    private static final String NODE_DEFAULT_VOID = "NODE_DEFAULT_VOID";
    private static final String NODE_FACTORY = "NODE_FACTORY";
    private static final String NODE_SCOPE_HOOK = "NODE_SCOPE_HOOK";
    private static final String NODE_USES_PARSER = "NODE_USES_PARSER";
    private static final String STATIC = "STATIC";
    private static final String VISITOR = "VISITOR";
    private static final String NODE_PACKAGE = "NODE_PACKAGE";
    private static final String VISITOR_EXCEPTION = "VISITOR_EXCEPTION";
    private static final String NODE_PREFIX = "NODE_PREFIX";
    private final Hashtable optionalAttrs;
    private String outputFile;
    private static final String DEFAULT_SUFFIX = ".jj";
    private File outputDirectory;
    private File targetFile;
    private File javaccHome;
    private CommandlineJava cmdl;
    private String maxMemory;
    
    public void setBuildnodefiles(final boolean buildNodeFiles) {
        this.optionalAttrs.put("BUILD_NODE_FILES", buildNodeFiles ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setMulti(final boolean multi) {
        this.optionalAttrs.put("MULTI", multi ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setNodedefaultvoid(final boolean nodeDefaultVoid) {
        this.optionalAttrs.put("NODE_DEFAULT_VOID", nodeDefaultVoid ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setNodefactory(final boolean nodeFactory) {
        this.optionalAttrs.put("NODE_FACTORY", nodeFactory ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setNodescopehook(final boolean nodeScopeHook) {
        this.optionalAttrs.put("NODE_SCOPE_HOOK", nodeScopeHook ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setNodeusesparser(final boolean nodeUsesParser) {
        this.optionalAttrs.put("NODE_USES_PARSER", nodeUsesParser ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setStatic(final boolean staticParser) {
        this.optionalAttrs.put("STATIC", staticParser ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setVisitor(final boolean visitor) {
        this.optionalAttrs.put("VISITOR", visitor ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setNodepackage(final String nodePackage) {
        this.optionalAttrs.put("NODE_PACKAGE", nodePackage);
    }
    
    public void setVisitorException(final String visitorException) {
        this.optionalAttrs.put("VISITOR_EXCEPTION", visitorException);
    }
    
    public void setNodeprefix(final String nodePrefix) {
        this.optionalAttrs.put("NODE_PREFIX", nodePrefix);
    }
    
    public void setOutputdirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }
    
    public void setOutputfile(final String outputFile) {
        this.outputFile = outputFile;
    }
    
    public void setTarget(final File targetFile) {
        this.targetFile = targetFile;
    }
    
    public void setJavacchome(final File javaccHome) {
        this.javaccHome = javaccHome;
    }
    
    public void setMaxmemory(final String max) {
        this.maxMemory = max;
    }
    
    public JJTree() {
        this.optionalAttrs = new Hashtable();
        this.outputFile = null;
        this.outputDirectory = null;
        this.targetFile = null;
        this.javaccHome = null;
        this.cmdl = new CommandlineJava();
        this.maxMemory = null;
        this.cmdl.setVm(JavaEnvUtils.getJreExecutable("java"));
    }
    
    @Override
    public void execute() throws BuildException {
        final Enumeration iter = this.optionalAttrs.keys();
        while (iter.hasMoreElements()) {
            final String name = iter.nextElement();
            final Object value = this.optionalAttrs.get(name);
            this.cmdl.createArgument().setValue("-" + name + ":" + value.toString());
        }
        if (this.targetFile == null || !this.targetFile.isFile()) {
            throw new BuildException("Invalid target: " + this.targetFile);
        }
        File javaFile = null;
        if (this.outputDirectory == null) {
            this.cmdl.createArgument().setValue("-OUTPUT_DIRECTORY:" + this.getDefaultOutputDirectory());
            javaFile = new File(this.createOutputFileName(this.targetFile, this.outputFile, null));
        }
        else {
            if (!this.outputDirectory.isDirectory()) {
                throw new BuildException("'outputdirectory' " + this.outputDirectory + " is not a directory.");
            }
            this.cmdl.createArgument().setValue("-OUTPUT_DIRECTORY:" + this.outputDirectory.getAbsolutePath().replace('\\', '/'));
            javaFile = new File(this.createOutputFileName(this.targetFile, this.outputFile, this.outputDirectory.getPath()));
        }
        if (javaFile.exists() && this.targetFile.lastModified() < javaFile.lastModified()) {
            this.log("Target is already built - skipping (" + this.targetFile + ")", 3);
            return;
        }
        if (this.outputFile != null) {
            this.cmdl.createArgument().setValue("-OUTPUT_FILE:" + this.outputFile.replace('\\', '/'));
        }
        this.cmdl.createArgument().setValue(this.targetFile.getAbsolutePath());
        final Path classpath = this.cmdl.createClasspath(this.getProject());
        final File javaccJar = JavaCC.getArchiveFile(this.javaccHome);
        classpath.createPathElement().setPath(javaccJar.getAbsolutePath());
        classpath.addJavaRuntime();
        this.cmdl.setClassname(JavaCC.getMainClass(classpath, 2));
        this.cmdl.setMaxmemory(this.maxMemory);
        final Commandline.Argument arg = this.cmdl.createVmArgument();
        arg.setValue("-Dinstall.root=" + this.javaccHome.getAbsolutePath());
        final Execute process = new Execute(new LogStreamHandler(this, 2, 2), null);
        this.log(this.cmdl.describeCommand(), 3);
        process.setCommandline(this.cmdl.getCommandline());
        try {
            if (process.execute() != 0) {
                throw new BuildException("JJTree failed.");
            }
        }
        catch (IOException e) {
            throw new BuildException("Failed to launch JJTree", e);
        }
    }
    
    private String createOutputFileName(final File destFile, String optionalOutputFile, String outputDir) {
        optionalOutputFile = this.validateOutputFile(optionalOutputFile, outputDir);
        String jjtreeFile = destFile.getAbsolutePath().replace('\\', '/');
        if (optionalOutputFile == null || optionalOutputFile.equals("")) {
            final int filePos = jjtreeFile.lastIndexOf("/");
            if (filePos >= 0) {
                jjtreeFile = jjtreeFile.substring(filePos + 1);
            }
            final int suffixPos = jjtreeFile.lastIndexOf(46);
            if (suffixPos == -1) {
                optionalOutputFile = jjtreeFile + ".jj";
            }
            else {
                final String currentSuffix = jjtreeFile.substring(suffixPos);
                if (currentSuffix.equals(".jj")) {
                    optionalOutputFile = jjtreeFile + ".jj";
                }
                else {
                    optionalOutputFile = jjtreeFile.substring(0, suffixPos) + ".jj";
                }
            }
        }
        if (outputDir == null || outputDir.equals("")) {
            outputDir = this.getDefaultOutputDirectory();
        }
        return (outputDir + "/" + optionalOutputFile).replace('\\', '/');
    }
    
    private String validateOutputFile(final String destFile, final String outputDir) throws BuildException {
        if (destFile == null) {
            return null;
        }
        if (outputDir == null && (destFile.startsWith("/") || destFile.startsWith("\\"))) {
            final String relativeOutputFile = this.makeOutputFileRelative(destFile);
            this.setOutputfile(relativeOutputFile);
            return relativeOutputFile;
        }
        final String root = this.getRoot(new File(destFile)).getAbsolutePath();
        if (root.length() > 1 && destFile.startsWith(root.substring(0, root.length() - 1))) {
            throw new BuildException("Drive letter in 'outputfile' not supported: " + destFile);
        }
        return destFile;
    }
    
    private String makeOutputFileRelative(final String destFile) {
        final StringBuffer relativePath = new StringBuffer();
        final String defaultOutputDirectory = this.getDefaultOutputDirectory();
        int nextPos = defaultOutputDirectory.indexOf(47);
        int startPos = nextPos + 1;
        while (startPos > -1 && startPos < defaultOutputDirectory.length()) {
            relativePath.append("/..");
            nextPos = defaultOutputDirectory.indexOf(47, startPos);
            if (nextPos == -1) {
                startPos = nextPos;
            }
            else {
                startPos = nextPos + 1;
            }
        }
        relativePath.append(destFile);
        return relativePath.toString();
    }
    
    private String getDefaultOutputDirectory() {
        return this.getProject().getBaseDir().getAbsolutePath().replace('\\', '/');
    }
    
    private File getRoot(final File file) {
        File root;
        for (root = file.getAbsoluteFile(); root.getParent() != null; root = root.getParentFile()) {}
        return root;
    }
}
