// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.sos;

import java.io.IOException;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.BuildException;
import java.io.File;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.Task;

public abstract class SOS extends Task implements SOSCmd
{
    private static final int ERROR_EXIT_STATUS = 255;
    private String sosCmdDir;
    private String sosUsername;
    private String sosPassword;
    private String projectPath;
    private String vssServerPath;
    private String sosServerPath;
    private String sosHome;
    private String localPath;
    private String version;
    private String label;
    private String comment;
    private String filename;
    private boolean noCompress;
    private boolean noCache;
    private boolean recursive;
    private boolean verbose;
    protected Commandline commandLine;
    
    public SOS() {
        this.sosCmdDir = null;
        this.sosUsername = null;
        this.sosPassword = "";
        this.projectPath = null;
        this.vssServerPath = null;
        this.sosServerPath = null;
        this.sosHome = null;
        this.localPath = null;
        this.version = null;
        this.label = null;
        this.comment = null;
        this.filename = null;
        this.noCompress = false;
        this.noCache = false;
        this.recursive = false;
        this.verbose = false;
    }
    
    public final void setNoCache(final boolean nocache) {
        this.noCache = nocache;
    }
    
    public final void setNoCompress(final boolean nocompress) {
        this.noCompress = nocompress;
    }
    
    public final void setSosCmd(final String dir) {
        this.sosCmdDir = FileUtils.translatePath(dir);
    }
    
    public final void setUsername(final String username) {
        this.sosUsername = username;
    }
    
    public final void setPassword(final String password) {
        this.sosPassword = password;
    }
    
    public final void setProjectPath(final String projectpath) {
        if (projectpath.startsWith("$")) {
            this.projectPath = projectpath;
        }
        else {
            this.projectPath = "$" + projectpath;
        }
    }
    
    public final void setVssServerPath(final String vssServerPath) {
        this.vssServerPath = vssServerPath;
    }
    
    public final void setSosHome(final String sosHome) {
        this.sosHome = sosHome;
    }
    
    public final void setSosServerPath(final String sosServerPath) {
        this.sosServerPath = sosServerPath;
    }
    
    public final void setLocalPath(final Path path) {
        this.localPath = path.toString();
    }
    
    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }
    
    protected void setInternalFilename(final String file) {
        this.filename = file;
    }
    
    protected void setInternalRecursive(final boolean recurse) {
        this.recursive = recurse;
    }
    
    protected void setInternalComment(final String text) {
        this.comment = text;
    }
    
    protected void setInternalLabel(final String text) {
        this.label = text;
    }
    
    protected void setInternalVersion(final String text) {
        this.version = text;
    }
    
    protected String getSosCommand() {
        if (this.sosCmdDir == null) {
            return "soscmd";
        }
        return this.sosCmdDir + File.separator + "soscmd";
    }
    
    protected String getComment() {
        return this.comment;
    }
    
    protected String getVersion() {
        return this.version;
    }
    
    protected String getLabel() {
        return this.label;
    }
    
    protected String getUsername() {
        return this.sosUsername;
    }
    
    protected String getPassword() {
        return this.sosPassword;
    }
    
    protected String getProjectPath() {
        return this.projectPath;
    }
    
    protected String getVssServerPath() {
        return this.vssServerPath;
    }
    
    protected String getSosHome() {
        return this.sosHome;
    }
    
    protected String getSosServerPath() {
        return this.sosServerPath;
    }
    
    protected String getFilename() {
        return this.filename;
    }
    
    protected String getNoCompress() {
        return this.noCompress ? "-nocompress" : "";
    }
    
    protected String getNoCache() {
        return this.noCache ? "-nocache" : "";
    }
    
    protected String getVerbose() {
        return this.verbose ? "-verbose" : "";
    }
    
    protected String getRecursive() {
        return this.recursive ? "-recursive" : "";
    }
    
    protected String getLocalPath() {
        if (this.localPath == null) {
            return this.getProject().getBaseDir().getAbsolutePath();
        }
        final File dir = this.getProject().resolveFile(this.localPath);
        if (!dir.exists()) {
            final boolean done = dir.mkdirs();
            if (!done) {
                final String msg = "Directory " + this.localPath + " creation was not " + "successful for an unknown reason";
                throw new BuildException(msg, this.getLocation());
            }
            this.getProject().log("Created dir: " + dir.getAbsolutePath());
        }
        return dir.getAbsolutePath();
    }
    
    abstract Commandline buildCmdLine();
    
    @Override
    public void execute() throws BuildException {
        int result = 0;
        this.buildCmdLine();
        result = this.run(this.commandLine);
        if (result == 255) {
            final String msg = "Failed executing: " + this.commandLine.toString();
            throw new BuildException(msg, this.getLocation());
        }
    }
    
    protected int run(final Commandline cmd) {
        try {
            final Execute exe = new Execute(new LogStreamHandler(this, 2, 1));
            exe.setAntRun(this.getProject());
            exe.setWorkingDirectory(this.getProject().getBaseDir());
            exe.setCommandline(cmd.getCommandline());
            exe.setVMLauncher(false);
            return exe.execute();
        }
        catch (IOException e) {
            throw new BuildException(e, this.getLocation());
        }
    }
    
    protected void getRequiredAttributes() {
        this.commandLine.setExecutable(this.getSosCommand());
        if (this.getSosServerPath() == null) {
            throw new BuildException("sosserverpath attribute must be set!", this.getLocation());
        }
        this.commandLine.createArgument().setValue("-server");
        this.commandLine.createArgument().setValue(this.getSosServerPath());
        if (this.getUsername() == null) {
            throw new BuildException("username attribute must be set!", this.getLocation());
        }
        this.commandLine.createArgument().setValue("-name");
        this.commandLine.createArgument().setValue(this.getUsername());
        this.commandLine.createArgument().setValue("-password");
        this.commandLine.createArgument().setValue(this.getPassword());
        if (this.getVssServerPath() == null) {
            throw new BuildException("vssserverpath attribute must be set!", this.getLocation());
        }
        this.commandLine.createArgument().setValue("-database");
        this.commandLine.createArgument().setValue(this.getVssServerPath());
        if (this.getProjectPath() == null) {
            throw new BuildException("projectpath attribute must be set!", this.getLocation());
        }
        this.commandLine.createArgument().setValue("-project");
        this.commandLine.createArgument().setValue(this.getProjectPath());
    }
    
    protected void getOptionalAttributes() {
        this.commandLine.createArgument().setValue(this.getVerbose());
        this.commandLine.createArgument().setValue(this.getNoCompress());
        if (this.getSosHome() == null) {
            this.commandLine.createArgument().setValue(this.getNoCache());
        }
        else {
            this.commandLine.createArgument().setValue("-soshome");
            this.commandLine.createArgument().setValue(this.getSosHome());
        }
        if (this.getLocalPath() != null) {
            this.commandLine.createArgument().setValue("-workdir");
            this.commandLine.createArgument().setValue(this.getLocalPath());
        }
    }
}
