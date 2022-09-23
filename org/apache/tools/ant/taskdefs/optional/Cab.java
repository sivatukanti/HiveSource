// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional;

import java.util.Enumeration;
import java.io.OutputStream;
import org.apache.tools.ant.taskdefs.StreamPumper;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.DirectoryScanner;
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;
import java.util.Vector;
import java.io.File;
import org.apache.tools.ant.taskdefs.MatchingTask;

public class Cab extends MatchingTask
{
    private static final int DEFAULT_RESULT = -99;
    private File cabFile;
    private File baseDir;
    private Vector filesets;
    private boolean doCompress;
    private boolean doVerbose;
    private String cmdOptions;
    protected String archiveType;
    private static final FileUtils FILE_UTILS;
    
    public Cab() {
        this.filesets = new Vector();
        this.doCompress = true;
        this.doVerbose = false;
        this.archiveType = "cab";
    }
    
    public void setCabfile(final File cabFile) {
        this.cabFile = cabFile;
    }
    
    public void setBasedir(final File baseDir) {
        this.baseDir = baseDir;
    }
    
    public void setCompress(final boolean compress) {
        this.doCompress = compress;
    }
    
    public void setVerbose(final boolean verbose) {
        this.doVerbose = verbose;
    }
    
    public void setOptions(final String options) {
        this.cmdOptions = options;
    }
    
    public void addFileset(final FileSet set) {
        if (this.filesets.size() > 0) {
            throw new BuildException("Only one nested fileset allowed");
        }
        this.filesets.addElement(set);
    }
    
    protected void checkConfiguration() throws BuildException {
        if (this.baseDir == null && this.filesets.size() == 0) {
            throw new BuildException("basedir attribute or one nested fileset is required!", this.getLocation());
        }
        if (this.baseDir != null && !this.baseDir.exists()) {
            throw new BuildException("basedir does not exist!", this.getLocation());
        }
        if (this.baseDir != null && this.filesets.size() > 0) {
            throw new BuildException("Both basedir attribute and a nested fileset is not allowed");
        }
        if (this.cabFile == null) {
            throw new BuildException("cabfile attribute must be set!", this.getLocation());
        }
    }
    
    protected ExecTask createExec() throws BuildException {
        final ExecTask exec = new ExecTask(this);
        return exec;
    }
    
    protected boolean isUpToDate(final Vector files) {
        boolean upToDate = true;
        for (int size = files.size(), i = 0; i < size && upToDate; ++i) {
            final String file = files.elementAt(i).toString();
            if (Cab.FILE_UTILS.resolveFile(this.baseDir, file).lastModified() > this.cabFile.lastModified()) {
                upToDate = false;
            }
        }
        return upToDate;
    }
    
    protected File createListFile(final Vector files) throws IOException {
        final File listFile = Cab.FILE_UTILS.createTempFile("ant", "", null, true, true);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(listFile));
            for (int size = files.size(), i = 0; i < size; ++i) {
                writer.write('\"' + files.elementAt(i).toString() + '\"');
                writer.newLine();
            }
        }
        finally {
            FileUtils.close(writer);
        }
        return listFile;
    }
    
    protected void appendFiles(final Vector files, final DirectoryScanner ds) {
        final String[] dsfiles = ds.getIncludedFiles();
        for (int i = 0; i < dsfiles.length; ++i) {
            files.addElement(dsfiles[i]);
        }
    }
    
    protected Vector getFileList() throws BuildException {
        final Vector files = new Vector();
        if (this.baseDir != null) {
            this.appendFiles(files, super.getDirectoryScanner(this.baseDir));
        }
        else {
            final FileSet fs = this.filesets.elementAt(0);
            this.baseDir = fs.getDir();
            this.appendFiles(files, fs.getDirectoryScanner(this.getProject()));
        }
        return files;
    }
    
    @Override
    public void execute() throws BuildException {
        this.checkConfiguration();
        final Vector files = this.getFileList();
        if (this.isUpToDate(files)) {
            return;
        }
        this.log("Building " + this.archiveType + ": " + this.cabFile.getAbsolutePath());
        if (!Os.isFamily("windows")) {
            this.log("Using listcab/libcabinet", 3);
            final StringBuffer sb = new StringBuffer();
            final Enumeration fileEnum = files.elements();
            while (fileEnum.hasMoreElements()) {
                sb.append(fileEnum.nextElement()).append("\n");
            }
            sb.append("\n").append(this.cabFile.getAbsolutePath()).append("\n");
            try {
                final Process p = Execute.launch(this.getProject(), new String[] { "listcab" }, null, (this.baseDir != null) ? this.baseDir : this.getProject().getBaseDir(), true);
                final OutputStream out = p.getOutputStream();
                final LogOutputStream outLog = new LogOutputStream(this, 3);
                final LogOutputStream errLog = new LogOutputStream(this, 0);
                final StreamPumper outPump = new StreamPumper(p.getInputStream(), outLog);
                final StreamPumper errPump = new StreamPumper(p.getErrorStream(), errLog);
                new Thread(outPump).start();
                new Thread(errPump).start();
                out.write(sb.toString().getBytes());
                out.flush();
                out.close();
                int result = -99;
                try {
                    result = p.waitFor();
                    outPump.waitFor();
                    outLog.close();
                    errPump.waitFor();
                    errLog.close();
                }
                catch (InterruptedException ie) {
                    this.log("Thread interrupted: " + ie);
                }
                if (Execute.isFailure(result)) {
                    this.log("Error executing listcab; error code: " + result);
                }
            }
            catch (IOException ex) {
                final String msg = "Problem creating " + this.cabFile + " " + ex.getMessage();
                throw new BuildException(msg, this.getLocation());
            }
        }
        else {
            try {
                final File listFile = this.createListFile(files);
                final ExecTask exec = this.createExec();
                File outFile = null;
                exec.setFailonerror(true);
                exec.setDir(this.baseDir);
                if (!this.doVerbose) {
                    outFile = Cab.FILE_UTILS.createTempFile("ant", "", null, true, true);
                    exec.setOutput(outFile);
                }
                exec.setExecutable("cabarc");
                exec.createArg().setValue("-r");
                exec.createArg().setValue("-p");
                if (!this.doCompress) {
                    exec.createArg().setValue("-m");
                    exec.createArg().setValue("none");
                }
                if (this.cmdOptions != null) {
                    exec.createArg().setLine(this.cmdOptions);
                }
                exec.createArg().setValue("n");
                exec.createArg().setFile(this.cabFile);
                exec.createArg().setValue("@" + listFile.getAbsolutePath());
                exec.execute();
                if (outFile != null) {
                    outFile.delete();
                }
                listFile.delete();
            }
            catch (IOException ioe) {
                final String msg2 = "Problem creating " + this.cabFile + " " + ioe.getMessage();
                throw new BuildException(msg2, this.getLocation());
            }
        }
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
    }
}
