// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.Project;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.types.Commandline;
import java.io.File;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.Task;

public abstract class ClearCase extends Task
{
    private String mClearToolDir;
    private String mviewPath;
    private String mobjSelect;
    private static int pcnt;
    private boolean mFailonerr;
    private static final String CLEARTOOL_EXE = "cleartool";
    public static final String COMMAND_UPDATE = "update";
    public static final String COMMAND_CHECKOUT = "checkout";
    public static final String COMMAND_CHECKIN = "checkin";
    public static final String COMMAND_UNCHECKOUT = "uncheckout";
    public static final String COMMAND_LOCK = "lock";
    public static final String COMMAND_UNLOCK = "unlock";
    public static final String COMMAND_MKBL = "mkbl";
    public static final String COMMAND_MKLABEL = "mklabel";
    public static final String COMMAND_MKLBTYPE = "mklbtype";
    public static final String COMMAND_RMTYPE = "rmtype";
    public static final String COMMAND_LSCO = "lsco";
    public static final String COMMAND_MKELEM = "mkelem";
    public static final String COMMAND_MKATTR = "mkattr";
    public static final String COMMAND_MKDIR = "mkdir";
    
    public ClearCase() {
        this.mClearToolDir = "";
        this.mviewPath = null;
        this.mobjSelect = null;
        this.mFailonerr = true;
    }
    
    public final void setClearToolDir(final String dir) {
        this.mClearToolDir = FileUtils.translatePath(dir);
    }
    
    protected final String getClearToolCommand() {
        String toReturn = this.mClearToolDir;
        if (!toReturn.equals("") && !toReturn.endsWith("/")) {
            toReturn += "/";
        }
        toReturn += "cleartool";
        return toReturn;
    }
    
    public final void setViewPath(final String viewPath) {
        this.mviewPath = viewPath;
    }
    
    public String getViewPath() {
        return this.mviewPath;
    }
    
    public String getViewPathBasename() {
        return new File(this.mviewPath).getName();
    }
    
    public final void setObjSelect(final String objSelect) {
        this.mobjSelect = objSelect;
    }
    
    public String getObjSelect() {
        return this.mobjSelect;
    }
    
    protected int run(final Commandline cmd) {
        try {
            final Project aProj = this.getProject();
            final Execute exe = new Execute(new LogStreamHandler(this, 2, 1));
            exe.setAntRun(aProj);
            exe.setWorkingDirectory(aProj.getBaseDir());
            exe.setCommandline(cmd.getCommandline());
            return exe.execute();
        }
        catch (IOException e) {
            throw new BuildException(e, this.getLocation());
        }
    }
    
    protected String runS(final Commandline cmdline) {
        final String outV = "opts.cc.runS.output" + ClearCase.pcnt++;
        final ExecTask exe = new ExecTask(this);
        final Commandline.Argument arg = exe.createArg();
        exe.setExecutable(cmdline.getExecutable());
        arg.setLine(Commandline.toString(cmdline.getArguments()));
        exe.setOutputproperty(outV);
        exe.execute();
        return this.getProject().getProperty(outV);
    }
    
    public void setFailOnErr(final boolean failonerr) {
        this.mFailonerr = failonerr;
    }
    
    public boolean getFailOnErr() {
        return this.mFailonerr;
    }
    
    static {
        ClearCase.pcnt = 0;
    }
}
