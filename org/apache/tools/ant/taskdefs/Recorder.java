// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.LogLevel;
import org.apache.tools.ant.types.EnumeratedAttribute;
import java.util.Iterator;
import java.util.Map;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import java.util.Hashtable;
import org.apache.tools.ant.SubBuildListener;
import org.apache.tools.ant.Task;

public class Recorder extends Task implements SubBuildListener
{
    private String filename;
    private Boolean append;
    private Boolean start;
    private int loglevel;
    private boolean emacsMode;
    private static Hashtable recorderEntries;
    
    public Recorder() {
        this.filename = null;
        this.append = null;
        this.start = null;
        this.loglevel = -1;
        this.emacsMode = false;
    }
    
    @Override
    public void init() {
        this.getProject().addBuildListener(this);
    }
    
    public void setName(final String fname) {
        this.filename = fname;
    }
    
    public void setAction(final ActionChoices action) {
        if (action.getValue().equalsIgnoreCase("start")) {
            this.start = Boolean.TRUE;
        }
        else {
            this.start = Boolean.FALSE;
        }
    }
    
    public void setAppend(final boolean append) {
        this.append = (append ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setEmacsMode(final boolean emacsMode) {
        this.emacsMode = emacsMode;
    }
    
    public void setLoglevel(final VerbosityLevelChoices level) {
        this.loglevel = level.getLevel();
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.filename == null) {
            throw new BuildException("No filename specified");
        }
        this.getProject().log("setting a recorder for name " + this.filename, 4);
        final RecorderEntry recorder = this.getRecorder(this.filename, this.getProject());
        recorder.setMessageOutputLevel(this.loglevel);
        recorder.setEmacsMode(this.emacsMode);
        if (this.start != null) {
            if (this.start) {
                recorder.reopenFile();
                recorder.setRecordState(this.start);
            }
            else {
                recorder.setRecordState(this.start);
                recorder.closeFile();
            }
        }
    }
    
    protected RecorderEntry getRecorder(final String name, final Project proj) throws BuildException {
        final Object o = Recorder.recorderEntries.get(name);
        RecorderEntry entry;
        if (o == null) {
            entry = new RecorderEntry(name);
            if (this.append == null) {
                entry.openFile(false);
            }
            else {
                entry.openFile(this.append);
            }
            entry.setProject(proj);
            Recorder.recorderEntries.put(name, entry);
        }
        else {
            entry = (RecorderEntry)o;
        }
        return entry;
    }
    
    public void buildStarted(final BuildEvent event) {
    }
    
    public void subBuildStarted(final BuildEvent event) {
    }
    
    public void targetStarted(final BuildEvent event) {
    }
    
    public void targetFinished(final BuildEvent event) {
    }
    
    public void taskStarted(final BuildEvent event) {
    }
    
    public void taskFinished(final BuildEvent event) {
    }
    
    public void messageLogged(final BuildEvent event) {
    }
    
    public void buildFinished(final BuildEvent event) {
        this.cleanup();
    }
    
    public void subBuildFinished(final BuildEvent event) {
        if (event.getProject() == this.getProject()) {
            this.cleanup();
        }
    }
    
    private void cleanup() {
        final Hashtable entries = (Hashtable)Recorder.recorderEntries.clone();
        for (final Map.Entry entry : entries.entrySet()) {
            final RecorderEntry re = entry.getValue();
            if (re.getProject() == this.getProject()) {
                Recorder.recorderEntries.remove(entry.getKey());
            }
        }
        this.getProject().removeBuildListener(this);
    }
    
    static {
        Recorder.recorderEntries = new Hashtable();
    }
    
    public static class ActionChoices extends EnumeratedAttribute
    {
        private static final String[] VALUES;
        
        @Override
        public String[] getValues() {
            return ActionChoices.VALUES;
        }
        
        static {
            VALUES = new String[] { "start", "stop" };
        }
    }
    
    public static class VerbosityLevelChoices extends LogLevel
    {
    }
}
