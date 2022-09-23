// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.ejb;

import org.apache.tools.ant.BuildException;
import java.util.Hashtable;
import java.io.File;

public class WeblogicTOPLinkDeploymentTool extends WeblogicDeploymentTool
{
    private static final String TL_DTD_LOC = "http://www.objectpeople.com/tlwl/dtd/toplink-cmp_2_5_1.dtd";
    private String toplinkDescriptor;
    private String toplinkDTD;
    
    public void setToplinkdescriptor(final String inString) {
        this.toplinkDescriptor = inString;
    }
    
    public void setToplinkdtd(final String inString) {
        this.toplinkDTD = inString;
    }
    
    @Override
    protected DescriptorHandler getDescriptorHandler(final File srcDir) {
        final DescriptorHandler handler = super.getDescriptorHandler(srcDir);
        if (this.toplinkDTD != null) {
            handler.registerDTD("-//The Object People, Inc.//DTD TOPLink for WebLogic CMP 2.5.1//EN", this.toplinkDTD);
        }
        else {
            handler.registerDTD("-//The Object People, Inc.//DTD TOPLink for WebLogic CMP 2.5.1//EN", "http://www.objectpeople.com/tlwl/dtd/toplink-cmp_2_5_1.dtd");
        }
        return handler;
    }
    
    @Override
    protected void addVendorFiles(final Hashtable ejbFiles, final String ddPrefix) {
        super.addVendorFiles(ejbFiles, ddPrefix);
        final File toplinkDD = new File(this.getConfig().descriptorDir, ddPrefix + this.toplinkDescriptor);
        if (toplinkDD.exists()) {
            ejbFiles.put("META-INF/" + this.toplinkDescriptor, toplinkDD);
        }
        else {
            this.log("Unable to locate toplink deployment descriptor. It was expected to be in " + toplinkDD.getPath(), 1);
        }
    }
    
    @Override
    public void validateConfigured() throws BuildException {
        super.validateConfigured();
        if (this.toplinkDescriptor == null) {
            throw new BuildException("The toplinkdescriptor attribute must be specified");
        }
    }
}
