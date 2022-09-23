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

public class JJDoc extends Task
{
    private static final String OUTPUT_FILE = "OUTPUT_FILE";
    private static final String TEXT = "TEXT";
    private static final String ONE_TABLE = "ONE_TABLE";
    private final Hashtable optionalAttrs;
    private String outputFile;
    private boolean plainText;
    private static final String DEFAULT_SUFFIX_HTML = ".html";
    private static final String DEFAULT_SUFFIX_TEXT = ".txt";
    private File targetFile;
    private File javaccHome;
    private CommandlineJava cmdl;
    private String maxMemory;
    
    public void setText(final boolean plainText) {
        this.optionalAttrs.put("TEXT", plainText ? Boolean.TRUE : Boolean.FALSE);
        this.plainText = plainText;
    }
    
    public void setOnetable(final boolean oneTable) {
        this.optionalAttrs.put("ONE_TABLE", oneTable ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setOutputfile(final String outputFile) {
        this.outputFile = outputFile;
    }
    
    public void setTarget(final File target) {
        this.targetFile = target;
    }
    
    public void setJavacchome(final File javaccHome) {
        this.javaccHome = javaccHome;
    }
    
    public void setMaxmemory(final String max) {
        this.maxMemory = max;
    }
    
    public JJDoc() {
        this.optionalAttrs = new Hashtable();
        this.outputFile = null;
        this.plainText = false;
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
        if (this.outputFile != null) {
            this.cmdl.createArgument().setValue("-OUTPUT_FILE:" + this.outputFile.replace('\\', '/'));
        }
        final File javaFile = new File(this.createOutputFileName(this.targetFile, this.outputFile, this.plainText));
        if (javaFile.exists() && this.targetFile.lastModified() < javaFile.lastModified()) {
            this.log("Target is already built - skipping (" + this.targetFile + ")", 3);
            return;
        }
        this.cmdl.createArgument().setValue(this.targetFile.getAbsolutePath());
        final Path classpath = this.cmdl.createClasspath(this.getProject());
        final File javaccJar = JavaCC.getArchiveFile(this.javaccHome);
        classpath.createPathElement().setPath(javaccJar.getAbsolutePath());
        classpath.addJavaRuntime();
        this.cmdl.setClassname(JavaCC.getMainClass(classpath, 3));
        this.cmdl.setMaxmemory(this.maxMemory);
        final Commandline.Argument arg = this.cmdl.createVmArgument();
        arg.setValue("-Dinstall.root=" + this.javaccHome.getAbsolutePath());
        final Execute process = new Execute(new LogStreamHandler(this, 2, 2), null);
        this.log(this.cmdl.describeCommand(), 3);
        process.setCommandline(this.cmdl.getCommandline());
        try {
            if (process.execute() != 0) {
                throw new BuildException("JJDoc failed.");
            }
        }
        catch (IOException e) {
            throw new BuildException("Failed to launch JJDoc", e);
        }
    }
    
    private String createOutputFileName(final File destFile, String optionalOutputFile, final boolean plain) {
        String suffix = ".html";
        String javaccFile = destFile.getAbsolutePath().replace('\\', '/');
        if (plain) {
            suffix = ".txt";
        }
        if (optionalOutputFile == null || optionalOutputFile.equals("")) {
            final int filePos = javaccFile.lastIndexOf("/");
            if (filePos >= 0) {
                javaccFile = javaccFile.substring(filePos + 1);
            }
            final int suffixPos = javaccFile.lastIndexOf(46);
            if (suffixPos == -1) {
                optionalOutputFile = javaccFile + suffix;
            }
            else {
                final String currentSuffix = javaccFile.substring(suffixPos);
                if (currentSuffix.equals(suffix)) {
                    optionalOutputFile = javaccFile + suffix;
                }
                else {
                    optionalOutputFile = javaccFile.substring(0, suffixPos) + suffix;
                }
            }
        }
        else {
            optionalOutputFile = optionalOutputFile.replace('\\', '/');
        }
        return (this.getProject().getBaseDir() + "/" + optionalOutputFile).replace('\\', '/');
    }
}
