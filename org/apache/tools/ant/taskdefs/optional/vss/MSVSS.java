// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.vss;

import org.apache.tools.ant.types.EnumeratedAttribute;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.io.IOException;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import java.text.ParseException;
import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.types.Commandline;
import java.text.DateFormat;
import org.apache.tools.ant.Task;

public abstract class MSVSS extends Task implements MSVSSConstants
{
    private String ssDir;
    private String vssLogin;
    private String vssPath;
    private String serverPath;
    private String version;
    private String date;
    private String label;
    private String autoResponse;
    private String localPath;
    private String comment;
    private String fromLabel;
    private String toLabel;
    private String outputFileName;
    private String user;
    private String fromDate;
    private String toDate;
    private String style;
    private boolean quiet;
    private boolean recursive;
    private boolean writable;
    private boolean failOnError;
    private boolean getLocalCopy;
    private int numDays;
    private DateFormat dateFormat;
    private CurrentModUpdated timestamp;
    private WritableFiles writableFiles;
    
    public MSVSS() {
        this.ssDir = null;
        this.vssLogin = null;
        this.vssPath = null;
        this.serverPath = null;
        this.version = null;
        this.date = null;
        this.label = null;
        this.autoResponse = null;
        this.localPath = null;
        this.comment = null;
        this.fromLabel = null;
        this.toLabel = null;
        this.outputFileName = null;
        this.user = null;
        this.fromDate = null;
        this.toDate = null;
        this.style = null;
        this.quiet = false;
        this.recursive = false;
        this.writable = false;
        this.failOnError = true;
        this.getLocalCopy = true;
        this.numDays = Integer.MIN_VALUE;
        this.dateFormat = DateFormat.getDateInstance(3);
        this.timestamp = null;
        this.writableFiles = null;
    }
    
    abstract Commandline buildCmdLine();
    
    public final void setSsdir(final String dir) {
        this.ssDir = FileUtils.translatePath(dir);
    }
    
    public final void setLogin(final String vssLogin) {
        this.vssLogin = vssLogin;
    }
    
    public final void setVsspath(final String vssPath) {
        String projectPath;
        if (vssPath.startsWith("vss://")) {
            projectPath = vssPath.substring(5);
        }
        else {
            projectPath = vssPath;
        }
        if (projectPath.startsWith("$")) {
            this.vssPath = projectPath;
        }
        else {
            this.vssPath = "$" + projectPath;
        }
    }
    
    public final void setServerpath(final String serverPath) {
        this.serverPath = serverPath;
    }
    
    public final void setFailOnError(final boolean failOnError) {
        this.failOnError = failOnError;
    }
    
    @Override
    public void execute() throws BuildException {
        int result = 0;
        final Commandline commandLine = this.buildCmdLine();
        result = this.run(commandLine);
        if (Execute.isFailure(result) && this.getFailOnError()) {
            final String msg = "Failed executing: " + this.formatCommandLine(commandLine) + " With a return code of " + result;
            throw new BuildException(msg, this.getLocation());
        }
    }
    
    protected void setInternalComment(final String comment) {
        this.comment = comment;
    }
    
    protected void setInternalAutoResponse(final String autoResponse) {
        this.autoResponse = autoResponse;
    }
    
    protected void setInternalDate(final String date) {
        this.date = date;
    }
    
    protected void setInternalDateFormat(final DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }
    
    protected void setInternalFailOnError(final boolean failOnError) {
        this.failOnError = failOnError;
    }
    
    protected void setInternalFromDate(final String fromDate) {
        this.fromDate = fromDate;
    }
    
    protected void setInternalFromLabel(final String fromLabel) {
        this.fromLabel = fromLabel;
    }
    
    protected void setInternalLabel(final String label) {
        this.label = label;
    }
    
    protected void setInternalLocalPath(final String localPath) {
        this.localPath = localPath;
    }
    
    protected void setInternalNumDays(final int numDays) {
        this.numDays = numDays;
    }
    
    protected void setInternalOutputFilename(final String outputFileName) {
        this.outputFileName = outputFileName;
    }
    
    protected void setInternalQuiet(final boolean quiet) {
        this.quiet = quiet;
    }
    
    protected void setInternalRecursive(final boolean recursive) {
        this.recursive = recursive;
    }
    
    protected void setInternalStyle(final String style) {
        this.style = style;
    }
    
    protected void setInternalToDate(final String toDate) {
        this.toDate = toDate;
    }
    
    protected void setInternalToLabel(final String toLabel) {
        this.toLabel = toLabel;
    }
    
    protected void setInternalUser(final String user) {
        this.user = user;
    }
    
    protected void setInternalVersion(final String version) {
        this.version = version;
    }
    
    protected void setInternalWritable(final boolean writable) {
        this.writable = writable;
    }
    
    protected void setInternalFileTimeStamp(final CurrentModUpdated timestamp) {
        this.timestamp = timestamp;
    }
    
    protected void setInternalWritableFiles(final WritableFiles writableFiles) {
        this.writableFiles = writableFiles;
    }
    
    protected void setInternalGetLocalCopy(final boolean getLocalCopy) {
        this.getLocalCopy = getLocalCopy;
    }
    
    protected String getSSCommand() {
        if (this.ssDir == null) {
            return "ss";
        }
        return this.ssDir.endsWith(File.separator) ? (this.ssDir + "ss") : (this.ssDir + File.separator + "ss");
    }
    
    protected String getVsspath() {
        return this.vssPath;
    }
    
    protected String getQuiet() {
        return this.quiet ? "-O-" : "";
    }
    
    protected String getRecursive() {
        return this.recursive ? "-R" : "";
    }
    
    protected String getWritable() {
        return this.writable ? "-W" : "";
    }
    
    protected String getLabel() {
        String shortLabel = "";
        if (this.label != null && this.label.length() > 0) {
            shortLabel = "-L" + this.getShortLabel();
        }
        return shortLabel;
    }
    
    private String getShortLabel() {
        String shortLabel;
        if (this.label != null && this.label.length() > 31) {
            shortLabel = this.label.substring(0, 30);
            this.log("Label is longer than 31 characters, truncated to: " + shortLabel, 1);
        }
        else {
            shortLabel = this.label;
        }
        return shortLabel;
    }
    
    protected String getStyle() {
        return (this.style != null) ? this.style : "";
    }
    
    protected String getVersionDateLabel() {
        String versionDateLabel = "";
        if (this.version != null) {
            versionDateLabel = "-V" + this.version;
        }
        else if (this.date != null) {
            versionDateLabel = "-Vd" + this.date;
        }
        else {
            final String shortLabel = this.getShortLabel();
            if (shortLabel != null && !shortLabel.equals("")) {
                versionDateLabel = "-VL" + shortLabel;
            }
        }
        return versionDateLabel;
    }
    
    protected String getVersion() {
        return (this.version != null) ? ("-V" + this.version) : "";
    }
    
    protected String getLocalpath() {
        String lclPath = "";
        if (this.localPath != null) {
            final File dir = this.getProject().resolveFile(this.localPath);
            if (!dir.exists()) {
                final boolean done = dir.mkdirs();
                if (!done) {
                    final String msg = "Directory " + this.localPath + " creation was not " + "successful for an unknown reason";
                    throw new BuildException(msg, this.getLocation());
                }
                this.getProject().log("Created dir: " + dir.getAbsolutePath());
            }
            lclPath = "-GL" + this.localPath;
        }
        return lclPath;
    }
    
    protected String getComment() {
        return (this.comment != null) ? ("-C" + this.comment) : "-C-";
    }
    
    protected String getAutoresponse() {
        if (this.autoResponse == null) {
            return "-I-";
        }
        if (this.autoResponse.equalsIgnoreCase("Y")) {
            return "-I-Y";
        }
        if (this.autoResponse.equalsIgnoreCase("N")) {
            return "-I-N";
        }
        return "-I-";
    }
    
    protected String getLogin() {
        return (this.vssLogin != null) ? ("-Y" + this.vssLogin) : "";
    }
    
    protected String getOutput() {
        return (this.outputFileName != null) ? ("-O" + this.outputFileName) : "";
    }
    
    protected String getUser() {
        return (this.user != null) ? ("-U" + this.user) : "";
    }
    
    protected String getVersionLabel() {
        if (this.fromLabel == null && this.toLabel == null) {
            return "";
        }
        if (this.fromLabel != null && this.toLabel != null) {
            if (this.fromLabel.length() > 31) {
                this.fromLabel = this.fromLabel.substring(0, 30);
                this.log("FromLabel is longer than 31 characters, truncated to: " + this.fromLabel, 1);
            }
            if (this.toLabel.length() > 31) {
                this.toLabel = this.toLabel.substring(0, 30);
                this.log("ToLabel is longer than 31 characters, truncated to: " + this.toLabel, 1);
            }
            return "-VL" + this.toLabel + "~L" + this.fromLabel;
        }
        if (this.fromLabel != null) {
            if (this.fromLabel.length() > 31) {
                this.fromLabel = this.fromLabel.substring(0, 30);
                this.log("FromLabel is longer than 31 characters, truncated to: " + this.fromLabel, 1);
            }
            return "-V~L" + this.fromLabel;
        }
        if (this.toLabel.length() > 31) {
            this.toLabel = this.toLabel.substring(0, 30);
            this.log("ToLabel is longer than 31 characters, truncated to: " + this.toLabel, 1);
        }
        return "-VL" + this.toLabel;
    }
    
    protected String getVersionDate() throws BuildException {
        if (this.fromDate == null && this.toDate == null && this.numDays == Integer.MIN_VALUE) {
            return "";
        }
        if (this.fromDate != null && this.toDate != null) {
            return "-Vd" + this.toDate + "~d" + this.fromDate;
        }
        if (this.toDate != null && this.numDays != Integer.MIN_VALUE) {
            try {
                return "-Vd" + this.toDate + "~d" + this.calcDate(this.toDate, this.numDays);
            }
            catch (ParseException ex) {
                final String msg = "Error parsing date: " + this.toDate;
                throw new BuildException(msg, this.getLocation());
            }
        }
        if (this.fromDate != null && this.numDays != Integer.MIN_VALUE) {
            try {
                return "-Vd" + this.calcDate(this.fromDate, this.numDays) + "~d" + this.fromDate;
            }
            catch (ParseException ex) {
                final String msg = "Error parsing date: " + this.fromDate;
                throw new BuildException(msg, this.getLocation());
            }
        }
        return (this.fromDate != null) ? ("-V~d" + this.fromDate) : ("-Vd" + this.toDate);
    }
    
    protected String getGetLocalCopy() {
        return this.getLocalCopy ? "" : "-G-";
    }
    
    private boolean getFailOnError() {
        return !this.getWritableFiles().equals("skip") && this.failOnError;
    }
    
    public String getFileTimeStamp() {
        if (this.timestamp == null) {
            return "";
        }
        if (this.timestamp.getValue().equals("modified")) {
            return "-GTM";
        }
        if (this.timestamp.getValue().equals("updated")) {
            return "-GTU";
        }
        return "-GTC";
    }
    
    public String getWritableFiles() {
        if (this.writableFiles == null) {
            return "";
        }
        if (this.writableFiles.getValue().equals("replace")) {
            return "-GWR";
        }
        if (this.writableFiles.getValue().equals("skip")) {
            this.failOnError = false;
            return "-GWS";
        }
        return "";
    }
    
    private int run(final Commandline cmd) {
        try {
            final Execute exe = new Execute(new LogStreamHandler(this, 2, 1));
            if (this.serverPath != null) {
                String[] env = exe.getEnvironment();
                if (env == null) {
                    env = new String[0];
                }
                final String[] newEnv = new String[env.length + 1];
                System.arraycopy(env, 0, newEnv, 0, env.length);
                newEnv[env.length] = "SSDIR=" + this.serverPath;
                exe.setEnvironment(newEnv);
            }
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
    
    private String calcDate(final String startDate, final int daysToAdd) throws ParseException {
        final Calendar calendar = new GregorianCalendar();
        final Date currentDate = this.dateFormat.parse(startDate);
        calendar.setTime(currentDate);
        calendar.add(5, daysToAdd);
        return this.dateFormat.format(calendar.getTime());
    }
    
    private String formatCommandLine(final Commandline cmd) {
        final StringBuffer sBuff = new StringBuffer(cmd.toString());
        final int indexUser = sBuff.substring(0).indexOf("-Y");
        if (indexUser > 0) {
            final int indexPass = sBuff.substring(0).indexOf(",", indexUser);
            for (int indexAfterPass = sBuff.substring(0).indexOf(" ", indexPass), i = indexPass + 1; i < indexAfterPass; ++i) {
                sBuff.setCharAt(i, '*');
            }
        }
        return sBuff.toString();
    }
    
    public static class CurrentModUpdated extends EnumeratedAttribute
    {
        @Override
        public String[] getValues() {
            return new String[] { "current", "modified", "updated" };
        }
    }
    
    public static class WritableFiles extends EnumeratedAttribute
    {
        @Override
        public String[] getValues() {
            return new String[] { "replace", "skip", "fail" };
        }
    }
}
