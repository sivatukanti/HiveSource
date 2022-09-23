// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.j2ee;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;

public class WebLogicHotDeploymentTool extends AbstractHotDeploymentTool implements HotDeploymentTool
{
    private static final int STRING_BUFFER_SIZE = 1024;
    private static final String WEBLOGIC_DEPLOY_CLASS_NAME = "weblogic.deploy";
    private static final String[] VALID_ACTIONS;
    private boolean debug;
    private String application;
    private String component;
    
    @Override
    public void deploy() {
        final Java java = new Java(this.getTask());
        java.setFork(true);
        java.setFailonerror(true);
        java.setClasspath(this.getClasspath());
        java.setClassname("weblogic.deploy");
        java.createArg().setLine(this.getArguments());
        java.execute();
    }
    
    @Override
    public void validateAttributes() throws BuildException {
        super.validateAttributes();
        final String action = this.getTask().getAction();
        if (this.getPassword() == null) {
            throw new BuildException("The password attribute must be set.");
        }
        if ((action.equals("deploy") || action.equals("update")) && this.application == null) {
            throw new BuildException("The application attribute must be set if action = " + action);
        }
        if ((action.equals("deploy") || action.equals("update")) && this.getTask().getSource() == null) {
            throw new BuildException("The source attribute must be set if action = " + action);
        }
        if ((action.equals("delete") || action.equals("undeploy")) && this.application == null) {
            throw new BuildException("The application attribute must be set if action = " + action);
        }
    }
    
    public String getArguments() throws BuildException {
        final String action = this.getTask().getAction();
        String args = null;
        if (action.equals("deploy") || action.equals("update")) {
            args = this.buildDeployArgs();
        }
        else if (action.equals("delete") || action.equals("undeploy")) {
            args = this.buildUndeployArgs();
        }
        else if (action.equals("list")) {
            args = this.buildListArgs();
        }
        return args;
    }
    
    @Override
    protected boolean isActionValid() {
        boolean valid = false;
        final String action = this.getTask().getAction();
        for (int i = 0; i < WebLogicHotDeploymentTool.VALID_ACTIONS.length; ++i) {
            if (action.equals(WebLogicHotDeploymentTool.VALID_ACTIONS[i])) {
                valid = true;
                break;
            }
        }
        return valid;
    }
    
    protected StringBuffer buildArgsPrefix() {
        final ServerDeploy task = this.getTask();
        return new StringBuffer(1024).append((this.getServer() != null) ? ("-url " + this.getServer()) : "").append(" ").append(this.debug ? "-debug " : "").append((this.getUserName() != null) ? ("-username " + this.getUserName()) : "").append(" ").append(task.getAction()).append(" ").append(this.getPassword()).append(" ");
    }
    
    protected String buildDeployArgs() {
        String args = this.buildArgsPrefix().append(this.application).append(" ").append(this.getTask().getSource()).toString();
        if (this.component != null) {
            args = "-component " + this.component + " " + args;
        }
        return args;
    }
    
    protected String buildUndeployArgs() {
        return this.buildArgsPrefix().append(this.application).append(" ").toString();
    }
    
    protected String buildListArgs() {
        return this.buildArgsPrefix().toString();
    }
    
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }
    
    public void setApplication(final String application) {
        this.application = application;
    }
    
    public void setComponent(final String component) {
        this.component = component;
    }
    
    static {
        VALID_ACTIONS = new String[] { "delete", "deploy", "list", "undeploy", "update" };
    }
}
