// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.util.Enumeration;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.BuildException;
import java.io.IOException;
import java.util.Vector;
import java.io.File;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.Task;

public class SubAnt extends Task
{
    private Path buildpath;
    private Ant ant;
    private String subTarget;
    private String antfile;
    private File genericantfile;
    private boolean verbose;
    private boolean inheritAll;
    private boolean inheritRefs;
    private boolean failOnError;
    private String output;
    private Vector properties;
    private Vector references;
    private Vector propertySets;
    private Vector targets;
    
    public SubAnt() {
        this.ant = null;
        this.subTarget = null;
        this.antfile = this.getDefaultBuildFile();
        this.genericantfile = null;
        this.verbose = false;
        this.inheritAll = false;
        this.inheritRefs = false;
        this.failOnError = true;
        this.output = null;
        this.properties = new Vector();
        this.references = new Vector();
        this.propertySets = new Vector();
        this.targets = new Vector();
    }
    
    protected String getDefaultBuildFile() {
        return "build.xml";
    }
    
    public void handleOutput(final String output) {
        if (this.ant != null) {
            this.ant.handleOutput(output);
        }
        else {
            super.handleOutput(output);
        }
    }
    
    public int handleInput(final byte[] buffer, final int offset, final int length) throws IOException {
        if (this.ant != null) {
            return this.ant.handleInput(buffer, offset, length);
        }
        return super.handleInput(buffer, offset, length);
    }
    
    public void handleFlush(final String output) {
        if (this.ant != null) {
            this.ant.handleFlush(output);
        }
        else {
            super.handleFlush(output);
        }
    }
    
    public void handleErrorOutput(final String output) {
        if (this.ant != null) {
            this.ant.handleErrorOutput(output);
        }
        else {
            super.handleErrorOutput(output);
        }
    }
    
    public void handleErrorFlush(final String output) {
        if (this.ant != null) {
            this.ant.handleErrorFlush(output);
        }
        else {
            super.handleErrorFlush(output);
        }
    }
    
    @Override
    public void execute() {
        if (this.buildpath == null) {
            throw new BuildException("No buildpath specified");
        }
        final String[] filenames = this.buildpath.list();
        final int count = filenames.length;
        if (count < 1) {
            this.log("No sub-builds to iterate on", 1);
            return;
        }
        BuildException buildException = null;
        for (int i = 0; i < count; ++i) {
            File file = null;
            String subdirPath = null;
            Throwable thrownException = null;
            try {
                File directory = null;
                file = new File(filenames[i]);
                if (file.isDirectory()) {
                    if (this.verbose) {
                        subdirPath = file.getPath();
                        this.log("Entering directory: " + subdirPath + "\n", 2);
                    }
                    if (this.genericantfile != null) {
                        directory = file;
                        file = this.genericantfile;
                    }
                    else {
                        file = new File(file, this.antfile);
                    }
                }
                this.execute(file, directory);
                if (this.verbose && subdirPath != null) {
                    this.log("Leaving directory: " + subdirPath + "\n", 2);
                }
            }
            catch (RuntimeException ex) {
                if (!this.getProject().isKeepGoingMode()) {
                    if (this.verbose && subdirPath != null) {
                        this.log("Leaving directory: " + subdirPath + "\n", 2);
                    }
                    throw ex;
                }
                thrownException = ex;
            }
            catch (Throwable ex2) {
                if (!this.getProject().isKeepGoingMode()) {
                    if (this.verbose && subdirPath != null) {
                        this.log("Leaving directory: " + subdirPath + "\n", 2);
                    }
                    throw new BuildException(ex2);
                }
                thrownException = ex2;
            }
            if (thrownException != null) {
                if (thrownException instanceof BuildException) {
                    this.log("File '" + file + "' failed with message '" + thrownException.getMessage() + "'.", 0);
                    if (buildException == null) {
                        buildException = (BuildException)thrownException;
                    }
                }
                else {
                    this.log("Target '" + file + "' failed with message '" + thrownException.getMessage() + "'.", 0);
                    thrownException.printStackTrace(System.err);
                    if (buildException == null) {
                        buildException = new BuildException(thrownException);
                    }
                }
                if (this.verbose && subdirPath != null) {
                    this.log("Leaving directory: " + subdirPath + "\n", 2);
                }
            }
        }
        if (buildException != null) {
            throw buildException;
        }
    }
    
    private void execute(final File file, final File directory) throws BuildException {
        if (file.exists() && !file.isDirectory() && file.canRead()) {
            this.ant = this.createAntTask(directory);
            final String antfilename = file.getAbsolutePath();
            this.ant.setAntfile(antfilename);
            for (int size = this.targets.size(), i = 0; i < size; ++i) {
                final Ant.TargetElement targetElement = this.targets.get(i);
                this.ant.addConfiguredTarget(targetElement);
            }
            try {
                this.ant.execute();
            }
            catch (BuildException e) {
                if (this.failOnError || this.isHardError(e)) {
                    throw e;
                }
                this.log("Failure for target '" + this.subTarget + "' of: " + antfilename + "\n" + e.getMessage(), 1);
            }
            catch (Throwable e2) {
                if (this.failOnError || this.isHardError(e2)) {
                    throw new BuildException(e2);
                }
                this.log("Failure for target '" + this.subTarget + "' of: " + antfilename + "\n" + e2.toString(), 1);
            }
            finally {
                this.ant = null;
            }
            return;
        }
        final String msg = "Invalid file: " + file;
        if (this.failOnError) {
            throw new BuildException(msg);
        }
        this.log(msg, 1);
    }
    
    private boolean isHardError(final Throwable t) {
        if (t instanceof BuildException) {
            return this.isHardError(t.getCause());
        }
        return t instanceof OutOfMemoryError || t instanceof ThreadDeath;
    }
    
    public void setAntfile(final String antfile) {
        this.antfile = antfile;
    }
    
    public void setGenericAntfile(final File afile) {
        this.genericantfile = afile;
    }
    
    public void setFailonerror(final boolean failOnError) {
        this.failOnError = failOnError;
    }
    
    public void setTarget(final String target) {
        this.subTarget = target;
    }
    
    public void addConfiguredTarget(final Ant.TargetElement t) {
        final String name = t.getName();
        if ("".equals(name)) {
            throw new BuildException("target name must not be empty");
        }
        this.targets.add(t);
    }
    
    public void setVerbose(final boolean on) {
        this.verbose = on;
    }
    
    public void setOutput(final String s) {
        this.output = s;
    }
    
    public void setInheritall(final boolean b) {
        this.inheritAll = b;
    }
    
    public void setInheritrefs(final boolean b) {
        this.inheritRefs = b;
    }
    
    public void addProperty(final Property p) {
        this.properties.addElement(p);
    }
    
    public void addReference(final Ant.Reference r) {
        this.references.addElement(r);
    }
    
    public void addPropertyset(final PropertySet ps) {
        this.propertySets.addElement(ps);
    }
    
    public void addDirset(final DirSet set) {
        this.add(set);
    }
    
    public void addFileset(final FileSet set) {
        this.add(set);
    }
    
    public void addFilelist(final FileList list) {
        this.add(list);
    }
    
    public void add(final ResourceCollection rc) {
        this.getBuildpath().add(rc);
    }
    
    public void setBuildpath(final Path s) {
        this.getBuildpath().append(s);
    }
    
    public Path createBuildpath() {
        return this.getBuildpath().createPath();
    }
    
    public Path.PathElement createBuildpathElement() {
        return this.getBuildpath().createPathElement();
    }
    
    private Path getBuildpath() {
        if (this.buildpath == null) {
            this.buildpath = new Path(this.getProject());
        }
        return this.buildpath;
    }
    
    public void setBuildpathRef(final Reference r) {
        this.createBuildpath().setRefid(r);
    }
    
    private Ant createAntTask(final File directory) {
        final Ant antTask = new Ant(this);
        antTask.init();
        if (this.subTarget != null && this.subTarget.length() > 0) {
            antTask.setTarget(this.subTarget);
        }
        if (this.output != null) {
            antTask.setOutput(this.output);
        }
        if (directory != null) {
            antTask.setDir(directory);
        }
        else {
            antTask.setUseNativeBasedir(true);
        }
        antTask.setInheritAll(this.inheritAll);
        Enumeration i = this.properties.elements();
        while (i.hasMoreElements()) {
            copyProperty(antTask.createProperty(), i.nextElement());
        }
        i = this.propertySets.elements();
        while (i.hasMoreElements()) {
            antTask.addPropertyset(i.nextElement());
        }
        antTask.setInheritRefs(this.inheritRefs);
        i = this.references.elements();
        while (i.hasMoreElements()) {
            antTask.addReference(i.nextElement());
        }
        return antTask;
    }
    
    private static void copyProperty(final Property to, final Property from) {
        to.setName(from.getName());
        if (from.getValue() != null) {
            to.setValue(from.getValue());
        }
        if (from.getFile() != null) {
            to.setFile(from.getFile());
        }
        if (from.getResource() != null) {
            to.setResource(from.getResource());
        }
        if (from.getPrefix() != null) {
            to.setPrefix(from.getPrefix());
        }
        if (from.getRefid() != null) {
            to.setRefid(from.getRefid());
        }
        if (from.getEnvironment() != null) {
            to.setEnvironment(from.getEnvironment());
        }
        if (from.getClasspath() != null) {
            to.setClasspath(from.getClasspath());
        }
    }
}
