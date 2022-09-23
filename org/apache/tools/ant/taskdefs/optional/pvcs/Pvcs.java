// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.pvcs;

import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.MessageFormat;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Enumeration;
import java.text.ParseException;
import java.io.FileNotFoundException;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.util.FileUtils;
import java.io.OutputStream;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import java.io.FileOutputStream;
import java.util.Random;
import java.io.File;
import org.apache.tools.ant.Project;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.types.Commandline;
import java.util.Vector;
import org.apache.tools.ant.Task;

public class Pvcs extends Task
{
    private static final int POS_1 = 1;
    private static final int POS_2 = 2;
    private static final int POS_3 = 3;
    private String pvcsbin;
    private String repository;
    private String pvcsProject;
    private Vector pvcsProjects;
    private String workspace;
    private String force;
    private String promotiongroup;
    private String label;
    private String revision;
    private boolean ignorerc;
    private boolean updateOnly;
    private String filenameFormat;
    private String lineStart;
    private String userId;
    private String config;
    private static final String PCLI_EXE = "pcli";
    private static final String GET_EXE = "get";
    
    protected int runCmd(final Commandline cmd, final ExecuteStreamHandler out) {
        try {
            final Project aProj = this.getProject();
            final Execute exe = new Execute(out);
            exe.setAntRun(aProj);
            exe.setWorkingDirectory(aProj.getBaseDir());
            exe.setCommandline(cmd.getCommandline());
            return exe.execute();
        }
        catch (IOException e) {
            final String msg = "Failed executing: " + cmd.toString() + ". Exception: " + e.getMessage();
            throw new BuildException(msg, this.getLocation());
        }
    }
    
    private String getExecutable(final String exe) {
        final StringBuffer correctedExe = new StringBuffer();
        if (this.getPvcsbin() != null) {
            if (this.pvcsbin.endsWith(File.separator)) {
                correctedExe.append(this.pvcsbin);
            }
            else {
                correctedExe.append(this.pvcsbin).append(File.separator);
            }
        }
        return correctedExe.append(exe).toString();
    }
    
    @Override
    public void execute() throws BuildException {
        int result = 0;
        if (this.repository == null || this.repository.trim().equals("")) {
            throw new BuildException("Required argument repository not specified");
        }
        final Commandline commandLine = new Commandline();
        commandLine.setExecutable(this.getExecutable("pcli"));
        commandLine.createArgument().setValue("lvf");
        commandLine.createArgument().setValue("-z");
        commandLine.createArgument().setValue("-aw");
        if (this.getWorkspace() != null) {
            commandLine.createArgument().setValue("-sp" + this.getWorkspace());
        }
        commandLine.createArgument().setValue("-pr" + this.getRepository());
        final String uid = this.getUserId();
        if (uid != null) {
            commandLine.createArgument().setValue("-id" + uid);
        }
        if (this.getPvcsproject() == null && this.getPvcsprojects().isEmpty()) {
            this.pvcsProject = "/";
        }
        if (this.getPvcsproject() != null) {
            commandLine.createArgument().setValue(this.getPvcsproject());
        }
        if (!this.getPvcsprojects().isEmpty()) {
            final Enumeration e = this.getPvcsprojects().elements();
            while (e.hasMoreElements()) {
                final String projectName = e.nextElement().getName();
                if (projectName == null || projectName.trim().equals("")) {
                    throw new BuildException("name is a required attribute of pvcsproject");
                }
                commandLine.createArgument().setValue(projectName);
            }
        }
        File tmp = null;
        File tmp2 = null;
        try {
            final Random rand = new Random(System.currentTimeMillis());
            tmp = new File("pvcs_ant_" + rand.nextLong() + ".log");
            final FileOutputStream fos = new FileOutputStream(tmp);
            tmp2 = new File("pvcs_ant_" + rand.nextLong() + ".log");
            this.log(commandLine.describeCommand(), 3);
            try {
                result = this.runCmd(commandLine, new PumpStreamHandler(fos, new LogOutputStream(this, 1)));
            }
            finally {
                FileUtils.close(fos);
            }
            if (Execute.isFailure(result) && !this.ignorerc) {
                final String msg = "Failed executing: " + commandLine.toString();
                throw new BuildException(msg, this.getLocation());
            }
            if (!tmp.exists()) {
                throw new BuildException("Communication between ant and pvcs failed. No output generated from executing PVCS commandline interface \"pcli\" and \"get\"");
            }
            this.log("Creating folders", 2);
            this.createFolders(tmp);
            this.massagePCLI(tmp, tmp2);
            commandLine.clearArgs();
            commandLine.setExecutable(this.getExecutable("get"));
            if (this.getConfig() != null && this.getConfig().length() > 0) {
                commandLine.createArgument().setValue("-c" + this.getConfig());
            }
            if (this.getForce() != null && this.getForce().equals("yes")) {
                commandLine.createArgument().setValue("-Y");
            }
            else {
                commandLine.createArgument().setValue("-N");
            }
            if (this.getPromotiongroup() != null) {
                commandLine.createArgument().setValue("-G" + this.getPromotiongroup());
            }
            else if (this.getLabel() != null) {
                commandLine.createArgument().setValue("-v" + this.getLabel());
            }
            else if (this.getRevision() != null) {
                commandLine.createArgument().setValue("-r" + this.getRevision());
            }
            if (this.updateOnly) {
                commandLine.createArgument().setValue("-U");
            }
            commandLine.createArgument().setValue("@" + tmp2.getAbsolutePath());
            this.log("Getting files", 2);
            this.log("Executing " + commandLine.toString(), 3);
            result = this.runCmd(commandLine, new LogStreamHandler(this, 2, 1));
            if (result != 0 && !this.ignorerc) {
                final String msg = "Failed executing: " + commandLine.toString() + ". Return code was " + result;
                throw new BuildException(msg, this.getLocation());
            }
        }
        catch (FileNotFoundException e2) {
            final String msg2 = "Failed executing: " + commandLine.toString() + ". Exception: " + e2.getMessage();
            throw new BuildException(msg2, this.getLocation());
        }
        catch (IOException e3) {
            final String msg2 = "Failed executing: " + commandLine.toString() + ". Exception: " + e3.getMessage();
            throw new BuildException(msg2, this.getLocation());
        }
        catch (ParseException e4) {
            final String msg2 = "Failed executing: " + commandLine.toString() + ". Exception: " + e4.getMessage();
            throw new BuildException(msg2, this.getLocation());
        }
        finally {
            if (tmp != null) {
                tmp.delete();
            }
            if (tmp2 != null) {
                tmp2.delete();
            }
        }
    }
    
    private void createFolders(final File file) throws IOException, ParseException {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            final MessageFormat mf = new MessageFormat(this.getFilenameFormat());
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                this.log("Considering \"" + line + "\"", 3);
                if (line.startsWith("\"\\") || line.startsWith("\"/") || (line.length() > 3 && line.startsWith("\"") && Character.isLetter(line.charAt(1)) && String.valueOf(line.charAt(2)).equals(":") && String.valueOf(line.charAt(3)).equals("\\"))) {
                    final Object[] objs = mf.parse(line);
                    final String f = (String)objs[1];
                    final int index = f.lastIndexOf(File.separator);
                    if (index > -1) {
                        final File dir = new File(f.substring(0, index));
                        if (!dir.exists()) {
                            this.log("Creating " + dir.getAbsolutePath(), 3);
                            if (dir.mkdirs()) {
                                this.log("Created " + dir.getAbsolutePath(), 2);
                            }
                            else {
                                this.log("Failed to create " + dir.getAbsolutePath(), 2);
                            }
                        }
                        else {
                            this.log(dir.getAbsolutePath() + " exists. Skipping", 3);
                        }
                    }
                    else {
                        this.log("File separator problem with " + line, 1);
                    }
                }
                else {
                    this.log("Skipped \"" + line + "\"", 3);
                }
            }
        }
        finally {
            FileUtils.close(in);
        }
    }
    
    private void massagePCLI(final File in, final File out) throws IOException {
        BufferedReader inReader = null;
        BufferedWriter outWriter = null;
        try {
            inReader = new BufferedReader(new FileReader(in));
            outWriter = new BufferedWriter(new FileWriter(out));
            String s = null;
            while ((s = inReader.readLine()) != null) {
                final String sNormal = s.replace('\\', '/');
                outWriter.write(sNormal);
                outWriter.newLine();
            }
        }
        finally {
            FileUtils.close(inReader);
            FileUtils.close(outWriter);
        }
    }
    
    public String getRepository() {
        return this.repository;
    }
    
    public String getFilenameFormat() {
        return this.filenameFormat;
    }
    
    public void setFilenameFormat(final String f) {
        this.filenameFormat = f;
    }
    
    public String getLineStart() {
        return this.lineStart;
    }
    
    public void setLineStart(final String l) {
        this.lineStart = l;
    }
    
    public void setRepository(final String repo) {
        this.repository = repo;
    }
    
    public String getPvcsproject() {
        return this.pvcsProject;
    }
    
    public void setPvcsproject(final String prj) {
        this.pvcsProject = prj;
    }
    
    public Vector getPvcsprojects() {
        return this.pvcsProjects;
    }
    
    public String getWorkspace() {
        return this.workspace;
    }
    
    public void setWorkspace(final String ws) {
        this.workspace = ws;
    }
    
    public String getPvcsbin() {
        return this.pvcsbin;
    }
    
    public void setPvcsbin(final String bin) {
        this.pvcsbin = bin;
    }
    
    public String getForce() {
        return this.force;
    }
    
    public void setForce(final String f) {
        if (f != null && f.equalsIgnoreCase("yes")) {
            this.force = "yes";
        }
        else {
            this.force = "no";
        }
    }
    
    public String getPromotiongroup() {
        return this.promotiongroup;
    }
    
    public void setPromotiongroup(final String w) {
        this.promotiongroup = w;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public void setLabel(final String l) {
        this.label = l;
    }
    
    public String getRevision() {
        return this.revision;
    }
    
    public void setRevision(final String r) {
        this.revision = r;
    }
    
    public boolean getIgnoreReturnCode() {
        return this.ignorerc;
    }
    
    public void setIgnoreReturnCode(final boolean b) {
        this.ignorerc = b;
    }
    
    public void addPvcsproject(final PvcsProject p) {
        this.pvcsProjects.addElement(p);
    }
    
    public boolean getUpdateOnly() {
        return this.updateOnly;
    }
    
    public void setUpdateOnly(final boolean l) {
        this.updateOnly = l;
    }
    
    public String getConfig() {
        return this.config;
    }
    
    public void setConfig(final File f) {
        this.config = f.toString();
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public void setUserId(final String u) {
        this.userId = u;
    }
    
    public Pvcs() {
        this.pvcsProject = null;
        this.pvcsProjects = new Vector();
        this.workspace = null;
        this.repository = null;
        this.pvcsbin = null;
        this.force = null;
        this.promotiongroup = null;
        this.label = null;
        this.ignorerc = false;
        this.updateOnly = false;
        this.lineStart = "\"P:";
        this.filenameFormat = "{0}-arc({1})";
    }
}
