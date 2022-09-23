// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.listener;

import java.io.File;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.SubBuildListener;

public class BigProjectLogger extends SimpleBigProjectLogger implements SubBuildListener
{
    private volatile boolean subBuildStartedRaised;
    private final Object subBuildLock;
    public static final String HEADER = "======================================================================";
    public static final String FOOTER = "======================================================================";
    
    public BigProjectLogger() {
        this.subBuildStartedRaised = false;
        this.subBuildLock = new Object();
    }
    
    @Override
    protected String getBuildFailedMessage() {
        return super.getBuildFailedMessage() + " - at " + this.getTimestamp();
    }
    
    @Override
    protected String getBuildSuccessfulMessage() {
        return super.getBuildSuccessfulMessage() + " - at " + this.getTimestamp();
    }
    
    @Override
    public void targetStarted(final BuildEvent event) {
        this.maybeRaiseSubBuildStarted(event);
        super.targetStarted(event);
    }
    
    @Override
    public void taskStarted(final BuildEvent event) {
        this.maybeRaiseSubBuildStarted(event);
        super.taskStarted(event);
    }
    
    @Override
    public void buildFinished(final BuildEvent event) {
        this.maybeRaiseSubBuildStarted(event);
        this.subBuildFinished(event);
        super.buildFinished(event);
    }
    
    @Override
    public void messageLogged(final BuildEvent event) {
        this.maybeRaiseSubBuildStarted(event);
        super.messageLogged(event);
    }
    
    public void subBuildStarted(final BuildEvent event) {
        final String name = this.extractNameOrDefault(event);
        final Project project = event.getProject();
        final File base = (project == null) ? null : project.getBaseDir();
        final String path = (base == null) ? "With no base directory" : ("In " + base.getAbsolutePath());
        this.printMessage(StringUtils.LINE_SEP + this.getHeader() + StringUtils.LINE_SEP + "Entering project " + name + StringUtils.LINE_SEP + path + StringUtils.LINE_SEP + this.getFooter(), this.out, event.getPriority());
    }
    
    protected String extractNameOrDefault(final BuildEvent event) {
        String name = this.extractProjectName(event);
        if (name == null) {
            name = "";
        }
        else {
            name = '\"' + name + '\"';
        }
        return name;
    }
    
    public void subBuildFinished(final BuildEvent event) {
        final String name = this.extractNameOrDefault(event);
        final String failed = (event.getException() != null) ? "failing " : "";
        this.printMessage(StringUtils.LINE_SEP + this.getHeader() + StringUtils.LINE_SEP + "Exiting " + failed + "project " + name + StringUtils.LINE_SEP + this.getFooter(), this.out, event.getPriority());
    }
    
    protected String getHeader() {
        return "======================================================================";
    }
    
    protected String getFooter() {
        return "======================================================================";
    }
    
    private void maybeRaiseSubBuildStarted(final BuildEvent event) {
        if (!this.subBuildStartedRaised) {
            synchronized (this.subBuildLock) {
                if (!this.subBuildStartedRaised) {
                    this.subBuildStartedRaised = true;
                    this.subBuildStarted(event);
                }
            }
        }
    }
}
