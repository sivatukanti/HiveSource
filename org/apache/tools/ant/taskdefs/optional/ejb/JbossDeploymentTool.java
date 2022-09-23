// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.ejb;

import org.apache.tools.ant.BuildException;
import java.io.File;
import java.util.Hashtable;

public class JbossDeploymentTool extends GenericDeploymentTool
{
    protected static final String JBOSS_DD = "jboss.xml";
    protected static final String JBOSS_CMP10D = "jaws.xml";
    protected static final String JBOSS_CMP20D = "jbosscmp-jdbc.xml";
    private String jarSuffix;
    
    public JbossDeploymentTool() {
        this.jarSuffix = ".jar";
    }
    
    public void setSuffix(final String inString) {
        this.jarSuffix = inString;
    }
    
    @Override
    protected void addVendorFiles(final Hashtable ejbFiles, final String ddPrefix) {
        final File jbossDD = new File(this.getConfig().descriptorDir, ddPrefix + "jboss.xml");
        if (!jbossDD.exists()) {
            this.log("Unable to locate jboss deployment descriptor. It was expected to be in " + jbossDD.getPath(), 1);
            return;
        }
        ejbFiles.put("META-INF/jboss.xml", jbossDD);
        String descriptorFileName = "jaws.xml";
        if ("2.0".equals(this.getParent().getCmpversion())) {
            descriptorFileName = "jbosscmp-jdbc.xml";
        }
        final File jbossCMPD = new File(this.getConfig().descriptorDir, ddPrefix + descriptorFileName);
        if (jbossCMPD.exists()) {
            ejbFiles.put("META-INF/" + descriptorFileName, jbossCMPD);
            return;
        }
        this.log("Unable to locate jboss cmp descriptor. It was expected to be in " + jbossCMPD.getPath(), 3);
    }
    
    @Override
    File getVendorOutputJarFile(final String baseName) {
        if (this.getDestDir() == null && this.getParent().getDestdir() == null) {
            throw new BuildException("DestDir not specified");
        }
        if (this.getDestDir() == null) {
            return new File(this.getParent().getDestdir(), baseName + this.jarSuffix);
        }
        return new File(this.getDestDir(), baseName + this.jarSuffix);
    }
    
    @Override
    public void validateConfigured() throws BuildException {
    }
    
    private EjbJar getParent() {
        return (EjbJar)this.getTask();
    }
}
