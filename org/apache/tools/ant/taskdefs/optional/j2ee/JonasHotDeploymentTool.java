// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.j2ee;

import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;
import java.io.File;

public class JonasHotDeploymentTool extends GenericHotDeploymentTool implements HotDeploymentTool
{
    protected static final String DEFAULT_ORB = "RMI";
    private static final String JONAS_DEPLOY_CLASS_NAME = "org.objectweb.jonas.adm.JonasAdmin";
    private static final String[] VALID_ACTIONS;
    private File jonasroot;
    private String orb;
    private String davidHost;
    private int davidPort;
    
    public JonasHotDeploymentTool() {
        this.orb = null;
    }
    
    public void setDavidhost(final String inValue) {
        this.davidHost = inValue;
    }
    
    public void setDavidport(final int inValue) {
        this.davidPort = inValue;
    }
    
    public void setJonasroot(final File inValue) {
        this.jonasroot = inValue;
    }
    
    public void setOrb(final String inValue) {
        this.orb = inValue;
    }
    
    @Override
    public Path getClasspath() {
        Path aClassPath = super.getClasspath();
        if (aClassPath == null) {
            aClassPath = new Path(this.getTask().getProject());
        }
        if (this.orb != null) {
            final String aOrbJar = new File(this.jonasroot, "lib/" + this.orb + "_jonas.jar").toString();
            final String aConfigDir = new File(this.jonasroot, "config/").toString();
            final Path aJOnASOrbPath = new Path(aClassPath.getProject(), aOrbJar + File.pathSeparator + aConfigDir);
            aClassPath.append(aJOnASOrbPath);
        }
        return aClassPath;
    }
    
    @Override
    public void validateAttributes() throws BuildException {
        final Java java = this.getJava();
        final String action = this.getTask().getAction();
        if (action == null) {
            throw new BuildException("The \"action\" attribute must be set");
        }
        if (!this.isActionValid()) {
            throw new BuildException("Invalid action \"" + action + "\" passed");
        }
        if (this.getClassName() == null) {
            this.setClassName("org.objectweb.jonas.adm.JonasAdmin");
        }
        if (this.jonasroot == null || this.jonasroot.isDirectory()) {
            java.createJvmarg().setValue("-Dinstall.root=" + this.jonasroot);
            java.createJvmarg().setValue("-Djava.security.policy=" + this.jonasroot + "/config/java.policy");
            if ("DAVID".equals(this.orb)) {
                java.createJvmarg().setValue("-Dorg.omg.CORBA.ORBClass=org.objectweb.david.libs.binding.orbs.iiop.IIOPORB");
                java.createJvmarg().setValue("-Dorg.omg.CORBA.ORBSingletonClass=org.objectweb.david.libs.binding.orbs.ORBSingletonClass");
                java.createJvmarg().setValue("-Djavax.rmi.CORBA.StubClass=org.objectweb.david.libs.stub_factories.rmi.StubDelegate");
                java.createJvmarg().setValue("-Djavax.rmi.CORBA.PortableRemoteObjectClass=org.objectweb.david.libs.binding.rmi.ORBPortableRemoteObjectDelegate");
                java.createJvmarg().setValue("-Djavax.rmi.CORBA.UtilClass=org.objectweb.david.libs.helpers.RMIUtilDelegate");
                java.createJvmarg().setValue("-Ddavid.CosNaming.default_method=0");
                java.createJvmarg().setValue("-Ddavid.rmi.ValueHandlerClass=com.sun.corba.se.internal.io.ValueHandlerImpl");
                if (this.davidHost != null) {
                    java.createJvmarg().setValue("-Ddavid.CosNaming.default_host=" + this.davidHost);
                }
                if (this.davidPort != 0) {
                    java.createJvmarg().setValue("-Ddavid.CosNaming.default_port=" + this.davidPort);
                }
            }
        }
        if (this.getServer() != null) {
            java.createArg().setLine("-n " + this.getServer());
        }
        if (action.equals("deploy") || action.equals("update") || action.equals("redeploy")) {
            java.createArg().setLine("-a " + this.getTask().getSource());
        }
        else if (action.equals("delete") || action.equals("undeploy")) {
            java.createArg().setLine("-r " + this.getTask().getSource());
        }
        else if (action.equals("list")) {
            java.createArg().setValue("-l");
        }
    }
    
    @Override
    protected boolean isActionValid() {
        boolean valid = false;
        final String action = this.getTask().getAction();
        for (int i = 0; i < JonasHotDeploymentTool.VALID_ACTIONS.length; ++i) {
            if (action.equals(JonasHotDeploymentTool.VALID_ACTIONS[i])) {
                valid = true;
                break;
            }
        }
        return valid;
    }
    
    static {
        VALID_ACTIONS = new String[] { "delete", "deploy", "list", "undeploy", "update" };
    }
}
