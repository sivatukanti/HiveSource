// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.Project;
import java.io.PrintStream;
import org.apache.tools.ant.SubBuildListener;
import org.apache.tools.ant.BuildLogger;

public class RecorderEntry implements BuildLogger, SubBuildListener
{
    private String filename;
    private boolean record;
    private int loglevel;
    private PrintStream out;
    private long targetStartTime;
    private boolean emacsMode;
    private Project project;
    
    protected RecorderEntry(final String name) {
        this.filename = null;
        this.record = true;
        this.loglevel = 2;
        this.out = null;
        this.targetStartTime = 0L;
        this.emacsMode = false;
        this.targetStartTime = System.currentTimeMillis();
        this.filename = name;
    }
    
    public String getFilename() {
        return this.filename;
    }
    
    public void setRecordState(final Boolean state) {
        if (state != null) {
            this.flush();
            this.record = state;
        }
    }
    
    public void buildStarted(final BuildEvent event) {
        this.log("> BUILD STARTED", 4);
    }
    
    public void buildFinished(final BuildEvent event) {
        this.log("< BUILD FINISHED", 4);
        if (this.record && this.out != null) {
            final Throwable error = event.getException();
            if (error == null) {
                this.out.println(StringUtils.LINE_SEP + "BUILD SUCCESSFUL");
            }
            else {
                this.out.println(StringUtils.LINE_SEP + "BUILD FAILED" + StringUtils.LINE_SEP);
                error.printStackTrace(this.out);
            }
        }
        this.cleanup();
    }
    
    public void subBuildFinished(final BuildEvent event) {
        if (event.getProject() == this.project) {
            this.cleanup();
        }
    }
    
    public void subBuildStarted(final BuildEvent event) {
    }
    
    public void targetStarted(final BuildEvent event) {
        this.log(">> TARGET STARTED -- " + event.getTarget(), 4);
        this.log(StringUtils.LINE_SEP + event.getTarget().getName() + ":", 2);
        this.targetStartTime = System.currentTimeMillis();
    }
    
    public void targetFinished(final BuildEvent event) {
        this.log("<< TARGET FINISHED -- " + event.getTarget(), 4);
        final String time = formatTime(System.currentTimeMillis() - this.targetStartTime);
        this.log(event.getTarget() + ":  duration " + time, 3);
        this.flush();
    }
    
    public void taskStarted(final BuildEvent event) {
        this.log(">>> TASK STARTED -- " + event.getTask(), 4);
    }
    
    public void taskFinished(final BuildEvent event) {
        this.log("<<< TASK FINISHED -- " + event.getTask(), 4);
        this.flush();
    }
    
    public void messageLogged(final BuildEvent event) {
        this.log("--- MESSAGE LOGGED", 4);
        final StringBuffer buf = new StringBuffer();
        if (event.getTask() != null) {
            final String name = event.getTask().getTaskName();
            if (!this.emacsMode) {
                final String label = "[" + name + "] ";
                for (int size = 12 - label.length(), i = 0; i < size; ++i) {
                    buf.append(" ");
                }
                buf.append(label);
            }
        }
        buf.append(event.getMessage());
        this.log(buf.toString(), event.getPriority());
    }
    
    private void log(final String mesg, final int level) {
        if (this.record && level <= this.loglevel && this.out != null) {
            this.out.println(mesg);
        }
    }
    
    private void flush() {
        if (this.record && this.out != null) {
            this.out.flush();
        }
    }
    
    public void setMessageOutputLevel(final int level) {
        if (level >= 0 && level <= 4) {
            this.loglevel = level;
        }
    }
    
    public void setOutputPrintStream(final PrintStream output) {
        this.closeFile();
        this.out = output;
    }
    
    public void setEmacsMode(final boolean emacsMode) {
        this.emacsMode = emacsMode;
    }
    
    public void setErrorPrintStream(final PrintStream err) {
        this.setOutputPrintStream(err);
    }
    
    private static String formatTime(final long millis) {
        final long seconds = millis / 1000L;
        final long minutes = seconds / 60L;
        if (minutes > 0L) {
            return Long.toString(minutes) + " minute" + ((minutes == 1L) ? " " : "s ") + Long.toString(seconds % 60L) + " second" + ((seconds % 60L == 1L) ? "" : "s");
        }
        return Long.toString(seconds) + " second" + ((seconds % 60L == 1L) ? "" : "s");
    }
    
    public void setProject(final Project project) {
        this.project = project;
        if (project != null) {
            project.addBuildListener(this);
        }
    }
    
    public Project getProject() {
        return this.project;
    }
    
    public void cleanup() {
        this.closeFile();
        if (this.project != null) {
            this.project.removeBuildListener(this);
        }
        this.project = null;
    }
    
    void openFile(final boolean append) throws BuildException {
        this.openFileImpl(append);
    }
    
    void closeFile() {
        if (this.out != null) {
            this.out.close();
            this.out = null;
        }
    }
    
    void reopenFile() throws BuildException {
        this.openFileImpl(true);
    }
    
    private void openFileImpl(final boolean append) throws BuildException {
        if (this.out == null) {
            try {
                this.out = new PrintStream(new FileOutputStream(this.filename, append));
            }
            catch (IOException ioe) {
                throw new BuildException("Problems opening file using a recorder entry", ioe);
            }
        }
    }
}
