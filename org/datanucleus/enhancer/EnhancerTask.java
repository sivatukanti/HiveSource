// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import java.util.StringTokenizer;
import org.apache.tools.ant.types.FileSet;
import java.util.Vector;
import java.io.File;
import org.apache.tools.ant.taskdefs.Java;

public class EnhancerTask extends Java
{
    private File dir;
    private String ifpropertyset;
    private String fileSuffixes;
    Vector<FileSet> filesets;
    
    public EnhancerTask() {
        this.fileSuffixes = "jdo";
        this.filesets = new Vector<FileSet>();
        this.setClassname("org.datanucleus.enhancer.DataNucleusEnhancer");
        this.setFork(true);
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.ifpropertyset != null && this.getProject().getProperty(this.ifpropertyset) == null) {
            this.log("Property " + this.ifpropertyset + " is not set. This task will not execute.", 3);
            return;
        }
        final File[] files = this.getFiles();
        if (files.length == 0) {
            this.log("Scanning for files with suffixes: " + this.fileSuffixes, 3);
            final StringTokenizer token = new StringTokenizer(this.fileSuffixes, ",");
            while (token.hasMoreTokens()) {
                final DirectoryScanner ds = this.getDirectoryScanner(this.getDir());
                ds.setIncludes(new String[] { "**\\*." + token.nextToken() });
                ds.scan();
                for (int i = 0; i < ds.getIncludedFiles().length; ++i) {
                    this.createArg().setFile(new File(this.getDir(), ds.getIncludedFiles()[i]));
                }
            }
        }
        else {
            this.log("FileSet has " + files.length + " files. Enhancer task will not scan for additional files.", 3);
            for (int j = 0; j < files.length; ++j) {
                this.createArg().setFile(files[j]);
            }
        }
        super.execute();
    }
    
    public void setCheckonly(final boolean checkonly) {
        if (checkonly) {
            this.createArg().setValue("-checkonly");
            this.createArg().setValue("" + checkonly);
            this.log("Enhancer checkonly: " + checkonly, 3);
        }
    }
    
    public void setGeneratePK(final boolean flag) {
        if (flag) {
            this.createArg().setValue("-generatePK");
            this.createArg().setValue("" + flag);
            this.log("Enhancer generatePK: " + flag, 3);
        }
    }
    
    public void setGenerateConstructor(final boolean flag) {
        if (flag) {
            this.createArg().setValue("-generateConstructor");
            this.createArg().setValue("" + flag);
            this.log("Enhancer generateConstructor: " + flag, 3);
        }
    }
    
    public void setDetachListener(final boolean flag) {
        if (flag) {
            this.createArg().setValue("-detachListener");
            this.createArg().setValue("" + flag);
            this.log("Enhancer detachListener: " + flag, 3);
        }
    }
    
    private DirectoryScanner getDirectoryScanner(final File dir) {
        final FileSet fileset = new FileSet();
        fileset.setDir(dir);
        return fileset.getDirectoryScanner(this.getProject());
    }
    
    public void setDestination(final File destdir) {
        if (destdir != null && destdir.isDirectory()) {
            this.createArg().setValue("-d");
            this.createArg().setFile(destdir);
            this.log("Enhancer destdir: " + destdir, 3);
        }
        else {
            this.log("Ignoring destination: " + destdir, 1);
        }
    }
    
    public void setApi(final String api) {
        if (api != null && api.length() > 0) {
            this.createArg().setValue("-api");
            this.createArg().setValue(api);
            this.log("Enhancer api: " + api, 3);
        }
    }
    
    public void setPersistenceUnit(final String unit) {
        if (unit != null && unit.length() > 0) {
            this.createArg().setValue("-pu");
            this.createArg().setValue(unit);
            this.log("Enhancer pu: " + unit, 3);
        }
    }
    
    @Override
    public void setDir(final File dir) {
        this.dir = dir;
    }
    
    public File getDir() {
        return (this.dir == null) ? this.getProject().getBaseDir() : this.dir;
    }
    
    public void setFileSuffixes(final String suffixes) {
        this.fileSuffixes = suffixes;
    }
    
    public void setAlwaysDetachable(final boolean detachable) {
        if (detachable) {
            this.createArg().setValue("-alwaysDetachable");
            this.log("Enhancer alwaysDetachable: " + detachable, 3);
        }
    }
    
    public void setVerbose(final boolean verbose) {
        if (verbose) {
            this.createArg().setValue("-v");
            this.log("Enhancer verbose: " + verbose, 3);
        }
    }
    
    public void setQuiet(final boolean quiet) {
        if (quiet) {
            this.createArg().setValue("-q");
            this.log("Enhancer quiet: " + quiet, 3);
        }
    }
    
    public void addFileSet(final FileSet fs) {
        this.filesets.addElement(fs);
    }
    
    protected File[] getFiles() {
        final Vector<File> v = new Vector<File>();
        for (int size = this.filesets.size(), i = 0; i < size; ++i) {
            final FileSet fs = this.filesets.elementAt(i);
            final DirectoryScanner ds = fs.getDirectoryScanner(this.getProject());
            ds.scan();
            final String[] f = ds.getIncludedFiles();
            for (int j = 0; j < f.length; ++j) {
                final String pathname = f[j];
                File file = new File(ds.getBasedir(), pathname);
                file = this.getProject().resolveFile(file.getPath());
                v.add(file);
            }
        }
        final File[] files = new File[v.size()];
        v.copyInto(files);
        return files;
    }
    
    public void setIf(final String ifpropertyset) {
        this.ifpropertyset = ifpropertyset;
    }
}
