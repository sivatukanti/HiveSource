// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.util.List;
import java.util.Iterator;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.types.Environment;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import java.io.PrintStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.File;
import java.util.Vector;
import java.util.ArrayList;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.Task;

public abstract class AbstractCvsTask extends Task
{
    public static final int DEFAULT_COMPRESSION_LEVEL = 3;
    private static final int MAXIMUM_COMRESSION_LEVEL = 9;
    private Commandline cmd;
    private ArrayList<Module> modules;
    private Vector<Commandline> vecCommandlines;
    private String cvsRoot;
    private String cvsRsh;
    private String cvsPackage;
    private String tag;
    private static final String DEFAULT_COMMAND = "checkout";
    private String command;
    private boolean quiet;
    private boolean reallyquiet;
    private int compression;
    private boolean noexec;
    private int port;
    private File passFile;
    private File dest;
    private boolean append;
    private File output;
    private File error;
    private boolean failOnError;
    private ExecuteStreamHandler executeStreamHandler;
    private OutputStream outputStream;
    private OutputStream errorStream;
    
    public AbstractCvsTask() {
        this.cmd = new Commandline();
        this.modules = new ArrayList<Module>();
        this.vecCommandlines = new Vector<Commandline>();
        this.command = null;
        this.quiet = false;
        this.reallyquiet = false;
        this.compression = 0;
        this.noexec = false;
        this.port = 0;
        this.passFile = null;
        this.append = false;
        this.failOnError = false;
    }
    
    public void setExecuteStreamHandler(final ExecuteStreamHandler handler) {
        this.executeStreamHandler = handler;
    }
    
    protected ExecuteStreamHandler getExecuteStreamHandler() {
        if (this.executeStreamHandler == null) {
            this.setExecuteStreamHandler(new PumpStreamHandler(this.getOutputStream(), this.getErrorStream()));
        }
        return this.executeStreamHandler;
    }
    
    protected void setOutputStream(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }
    
    protected OutputStream getOutputStream() {
        if (this.outputStream == null) {
            if (this.output != null) {
                try {
                    this.setOutputStream(new PrintStream(new BufferedOutputStream(new FileOutputStream(this.output.getPath(), this.append))));
                    return this.outputStream;
                }
                catch (IOException e) {
                    throw new BuildException(e, this.getLocation());
                }
            }
            this.setOutputStream(new LogOutputStream(this, 2));
        }
        return this.outputStream;
    }
    
    protected void setErrorStream(final OutputStream errorStream) {
        this.errorStream = errorStream;
    }
    
    protected OutputStream getErrorStream() {
        if (this.errorStream == null) {
            if (this.error != null) {
                try {
                    this.setErrorStream(new PrintStream(new BufferedOutputStream(new FileOutputStream(this.error.getPath(), this.append))));
                    return this.errorStream;
                }
                catch (IOException e) {
                    throw new BuildException(e, this.getLocation());
                }
            }
            this.setErrorStream(new LogOutputStream(this, 1));
        }
        return this.errorStream;
    }
    
    protected void runCommand(final Commandline toExecute) throws BuildException {
        final Environment env = new Environment();
        if (this.port > 0) {
            Environment.Variable var = new Environment.Variable();
            var.setKey("CVS_CLIENT_PORT");
            var.setValue(String.valueOf(this.port));
            env.addVariable(var);
            var = new Environment.Variable();
            var.setKey("CVS_PSERVER_PORT");
            var.setValue(String.valueOf(this.port));
            env.addVariable(var);
        }
        if (this.passFile == null) {
            final File defaultPassFile = new File(System.getProperty("cygwin.user.home", System.getProperty("user.home")) + File.separatorChar + ".cvspass");
            if (defaultPassFile.exists()) {
                this.setPassfile(defaultPassFile);
            }
        }
        if (this.passFile != null) {
            if (this.passFile.isFile() && this.passFile.canRead()) {
                final Environment.Variable var = new Environment.Variable();
                var.setKey("CVS_PASSFILE");
                var.setValue(String.valueOf(this.passFile));
                env.addVariable(var);
                this.log("Using cvs passfile: " + String.valueOf(this.passFile), 3);
            }
            else if (!this.passFile.canRead()) {
                this.log("cvs passfile: " + String.valueOf(this.passFile) + " ignored as it is not readable", 1);
            }
            else {
                this.log("cvs passfile: " + String.valueOf(this.passFile) + " ignored as it is not a file", 1);
            }
        }
        if (this.cvsRsh != null) {
            final Environment.Variable var = new Environment.Variable();
            var.setKey("CVS_RSH");
            var.setValue(String.valueOf(this.cvsRsh));
            env.addVariable(var);
        }
        final Execute exe = new Execute(this.getExecuteStreamHandler(), null);
        exe.setAntRun(this.getProject());
        if (this.dest == null) {
            this.dest = this.getProject().getBaseDir();
        }
        if (!this.dest.exists()) {
            this.dest.mkdirs();
        }
        exe.setWorkingDirectory(this.dest);
        exe.setCommandline(toExecute.getCommandline());
        exe.setEnvironment(env.getVariables());
        try {
            final String actualCommandLine = this.executeToString(exe);
            this.log(actualCommandLine, 3);
            final int retCode = exe.execute();
            this.log("retCode=" + retCode, 4);
            if (this.failOnError && Execute.isFailure(retCode)) {
                throw new BuildException("cvs exited with error code " + retCode + StringUtils.LINE_SEP + "Command line was [" + actualCommandLine + "]", this.getLocation());
            }
        }
        catch (IOException e) {
            if (this.failOnError) {
                throw new BuildException(e, this.getLocation());
            }
            this.log("Caught exception: " + e.getMessage(), 1);
        }
        catch (BuildException e2) {
            if (this.failOnError) {
                throw e2;
            }
            Throwable t = e2.getCause();
            if (t == null) {
                t = e2;
            }
            this.log("Caught exception: " + t.getMessage(), 1);
        }
        catch (Exception e3) {
            if (this.failOnError) {
                throw new BuildException(e3, this.getLocation());
            }
            this.log("Caught exception: " + e3.getMessage(), 1);
        }
    }
    
    @Override
    public void execute() throws BuildException {
        final String savedCommand = this.getCommand();
        if (this.getCommand() == null && this.vecCommandlines.size() == 0) {
            this.setCommand("checkout");
        }
        final String c = this.getCommand();
        Commandline cloned = null;
        if (c != null) {
            cloned = (Commandline)this.cmd.clone();
            cloned.createArgument(true).setLine(c);
            this.addConfiguredCommandline(cloned, true);
        }
        try {
            for (int size = this.vecCommandlines.size(), i = 0; i < size; ++i) {
                this.runCommand(this.vecCommandlines.elementAt(i));
            }
        }
        finally {
            if (cloned != null) {
                this.removeCommandline(cloned);
            }
            this.setCommand(savedCommand);
            FileUtils.close(this.outputStream);
            FileUtils.close(this.errorStream);
        }
    }
    
    private String executeToString(final Execute execute) {
        final String cmdLine = Commandline.describeCommand(execute.getCommandline());
        final StringBuffer stringBuffer = this.removeCvsPassword(cmdLine);
        final String newLine = StringUtils.LINE_SEP;
        final String[] variableArray = execute.getEnvironment();
        if (variableArray != null) {
            stringBuffer.append(newLine);
            stringBuffer.append(newLine);
            stringBuffer.append("environment:");
            stringBuffer.append(newLine);
            for (int z = 0; z < variableArray.length; ++z) {
                stringBuffer.append(newLine);
                stringBuffer.append("\t");
                stringBuffer.append(variableArray[z]);
            }
        }
        return stringBuffer.toString();
    }
    
    private StringBuffer removeCvsPassword(final String cmdLine) {
        final StringBuffer stringBuffer = new StringBuffer(cmdLine);
        final int start = cmdLine.indexOf("-d:");
        if (start >= 0) {
            int stop = cmdLine.indexOf("@", start);
            final int startproto = cmdLine.indexOf(":", start);
            final int startuser = cmdLine.indexOf(":", startproto + 1);
            final int startpass = cmdLine.indexOf(":", startuser + 1);
            stop = cmdLine.indexOf("@", start);
            if (stop >= 0 && startpass > startproto && startpass < stop) {
                for (int i = startpass + 1; i < stop; ++i) {
                    stringBuffer.replace(i, i + 1, "*");
                }
            }
        }
        return stringBuffer;
    }
    
    public void setCvsRoot(String root) {
        if (root != null && root.trim().equals("")) {
            root = null;
        }
        this.cvsRoot = root;
    }
    
    public String getCvsRoot() {
        return this.cvsRoot;
    }
    
    public void setCvsRsh(String rsh) {
        if (rsh != null && rsh.trim().equals("")) {
            rsh = null;
        }
        this.cvsRsh = rsh;
    }
    
    public String getCvsRsh() {
        return this.cvsRsh;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setPassfile(final File passFile) {
        this.passFile = passFile;
    }
    
    public File getPassFile() {
        return this.passFile;
    }
    
    public void setDest(final File dest) {
        this.dest = dest;
    }
    
    public File getDest() {
        return this.dest;
    }
    
    public void setPackage(final String p) {
        this.cvsPackage = p;
    }
    
    public String getPackage() {
        return this.cvsPackage;
    }
    
    public String getTag() {
        return this.tag;
    }
    
    public void setTag(final String p) {
        if (p != null && p.trim().length() > 0) {
            this.tag = p;
            this.addCommandArgument("-r" + p);
        }
    }
    
    public void addCommandArgument(final String arg) {
        this.addCommandArgument(this.cmd, arg);
    }
    
    public void addCommandArgument(final Commandline c, final String arg) {
        c.createArgument().setValue(arg);
    }
    
    public void setDate(final String p) {
        if (p != null && p.trim().length() > 0) {
            this.addCommandArgument("-D");
            this.addCommandArgument(p);
        }
    }
    
    public void setCommand(final String c) {
        this.command = c;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public void setQuiet(final boolean q) {
        this.quiet = q;
    }
    
    public void setReallyquiet(final boolean q) {
        this.reallyquiet = q;
    }
    
    public void setNoexec(final boolean ne) {
        this.noexec = ne;
    }
    
    public void setOutput(final File output) {
        this.output = output;
    }
    
    public void setError(final File error) {
        this.error = error;
    }
    
    public void setAppend(final boolean value) {
        this.append = value;
    }
    
    public void setFailOnError(final boolean failOnError) {
        this.failOnError = failOnError;
    }
    
    protected void configureCommandline(final Commandline c) {
        if (c == null) {
            return;
        }
        c.setExecutable("cvs");
        if (this.cvsPackage != null) {
            c.createArgument().setLine(this.cvsPackage);
        }
        for (final Module m : this.modules) {
            c.createArgument().setValue(m.getName());
        }
        if (this.compression > 0 && this.compression <= 9) {
            c.createArgument(true).setValue("-z" + this.compression);
        }
        if (this.quiet && !this.reallyquiet) {
            c.createArgument(true).setValue("-q");
        }
        if (this.reallyquiet) {
            c.createArgument(true).setValue("-Q");
        }
        if (this.noexec) {
            c.createArgument(true).setValue("-n");
        }
        if (this.cvsRoot != null) {
            c.createArgument(true).setLine("-d" + this.cvsRoot);
        }
    }
    
    protected void removeCommandline(final Commandline c) {
        this.vecCommandlines.removeElement(c);
    }
    
    public void addConfiguredCommandline(final Commandline c) {
        this.addConfiguredCommandline(c, false);
    }
    
    public void addConfiguredCommandline(final Commandline c, final boolean insertAtStart) {
        if (c == null) {
            return;
        }
        this.configureCommandline(c);
        if (insertAtStart) {
            this.vecCommandlines.insertElementAt(c, 0);
        }
        else {
            this.vecCommandlines.addElement(c);
        }
    }
    
    public void setCompressionLevel(final int level) {
        this.compression = level;
    }
    
    public void setCompression(final boolean usecomp) {
        this.setCompressionLevel(usecomp ? 3 : 0);
    }
    
    public void addModule(final Module m) {
        this.modules.add(m);
    }
    
    protected List<Module> getModules() {
        final List<Module> clone = (List<Module>)this.modules.clone();
        return clone;
    }
    
    public static final class Module
    {
        private String name;
        
        public void setName(final String s) {
            this.name = s;
        }
        
        public String getName() {
            return this.name;
        }
    }
}
