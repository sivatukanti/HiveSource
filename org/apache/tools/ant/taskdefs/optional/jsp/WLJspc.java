// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.jsp;

import java.util.StringTokenizer;
import java.util.Date;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.BuildException;
import java.util.Vector;
import org.apache.tools.ant.types.Path;
import java.io.File;
import org.apache.tools.ant.taskdefs.MatchingTask;

public class WLJspc extends MatchingTask
{
    private File destinationDirectory;
    private File sourceDirectory;
    private String destinationPackage;
    private Path compileClasspath;
    private String pathToPackage;
    private Vector filesToDo;
    
    public WLJspc() {
        this.pathToPackage = "";
        this.filesToDo = new Vector();
    }
    
    @Override
    public void execute() throws BuildException {
        if (!this.destinationDirectory.isDirectory()) {
            throw new BuildException("destination directory " + this.destinationDirectory.getPath() + " is not valid");
        }
        if (!this.sourceDirectory.isDirectory()) {
            throw new BuildException("src directory " + this.sourceDirectory.getPath() + " is not valid");
        }
        if (this.destinationPackage == null) {
            throw new BuildException("package attribute must be present.", this.getLocation());
        }
        this.pathToPackage = this.destinationPackage.replace('.', File.separatorChar);
        final DirectoryScanner ds = super.getDirectoryScanner(this.sourceDirectory);
        if (this.compileClasspath == null) {
            this.compileClasspath = new Path(this.getProject());
        }
        this.compileClasspath = this.compileClasspath.concatSystemClasspath();
        final String[] files = ds.getIncludedFiles();
        final Java helperTask = new Java(this);
        helperTask.setFork(true);
        helperTask.setClassname("weblogic.jspc");
        helperTask.setTaskName(this.getTaskName());
        final String[] args = new String[12];
        File jspFile = null;
        String parents = "";
        int j = 0;
        args[j++] = "-d";
        args[j++] = this.destinationDirectory.getAbsolutePath().trim();
        args[j++] = "-docroot";
        args[j++] = this.sourceDirectory.getAbsolutePath().trim();
        args[j++] = "-keepgenerated";
        args[j++] = "-compilerclass";
        args[j++] = "sun.tools.javac.Main";
        args[j++] = "-classpath";
        args[j++] = this.compileClasspath.toString();
        this.scanDir(files);
        this.log("Compiling " + this.filesToDo.size() + " JSP files");
        for (int size = this.filesToDo.size(), i = 0; i < size; ++i) {
            final String filename = this.filesToDo.elementAt(i);
            jspFile = new File(filename);
            args[j] = "-package";
            parents = jspFile.getParent();
            if (parents != null && !"".equals(parents)) {
                parents = this.replaceString(parents, File.separator, "_.");
                args[j + 1] = this.destinationPackage + "." + "_" + parents;
            }
            else {
                args[j + 1] = this.destinationPackage;
            }
            args[j + 2] = this.sourceDirectory + File.separator + filename;
            helperTask.clearArgs();
            for (int x = 0; x < j + 3; ++x) {
                helperTask.createArg().setValue(args[x]);
            }
            helperTask.setClasspath(this.compileClasspath);
            if (helperTask.executeJava() != 0) {
                this.log(filename + " failed to compile", 1);
            }
        }
    }
    
    public void setClasspath(final Path classpath) {
        if (this.compileClasspath == null) {
            this.compileClasspath = classpath;
        }
        else {
            this.compileClasspath.append(classpath);
        }
    }
    
    public Path createClasspath() {
        if (this.compileClasspath == null) {
            this.compileClasspath = new Path(this.getProject());
        }
        return this.compileClasspath;
    }
    
    public void setSrc(final File dirName) {
        this.sourceDirectory = dirName;
    }
    
    public void setDest(final File dirName) {
        this.destinationDirectory = dirName;
    }
    
    public void setPackage(final String packageName) {
        this.destinationPackage = packageName;
    }
    
    protected void scanDir(final String[] files) {
        final long now = new Date().getTime();
        File jspFile = null;
        String parents = null;
        String pack = "";
        for (int i = 0; i < files.length; ++i) {
            final File srcFile = new File(this.sourceDirectory, files[i]);
            jspFile = new File(files[i]);
            parents = jspFile.getParent();
            if (parents != null && !"".equals(parents)) {
                parents = this.replaceString(parents, File.separator, "_/");
                pack = this.pathToPackage + File.separator + "_" + parents;
            }
            else {
                pack = this.pathToPackage;
            }
            String filePath = pack + File.separator + "_";
            final int startingIndex = (files[i].lastIndexOf(File.separator) != -1) ? (files[i].lastIndexOf(File.separator) + 1) : 0;
            final int endingIndex = files[i].indexOf(".jsp");
            if (endingIndex == -1) {
                this.log("Skipping " + files[i] + ". Not a JSP", 3);
            }
            else {
                filePath += files[i].substring(startingIndex, endingIndex);
                filePath += ".class";
                final File classFile = new File(this.destinationDirectory, filePath);
                if (srcFile.lastModified() > now) {
                    this.log("Warning: file modified in the future: " + files[i], 1);
                }
                if (srcFile.lastModified() > classFile.lastModified()) {
                    this.filesToDo.addElement(files[i]);
                    this.log("Recompiling File " + files[i], 3);
                }
            }
        }
    }
    
    protected String replaceString(final String inpString, final String escapeChars, final String replaceChars) {
        String localString = "";
        int numTokens = 0;
        final StringTokenizer st = new StringTokenizer(inpString, escapeChars, true);
        numTokens = st.countTokens();
        for (int i = 0; i < numTokens; ++i) {
            String test = st.nextToken();
            test = (test.equals(escapeChars) ? replaceChars : test);
            localString += test;
        }
        return localString;
    }
}
