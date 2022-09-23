// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.ejb;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import javax.xml.parsers.SAXParser;

public interface EJBDeploymentTool
{
    void processDescriptor(final String p0, final SAXParser p1) throws BuildException;
    
    void validateConfigured() throws BuildException;
    
    void setTask(final Task p0);
    
    void configure(final EjbJar.Config p0);
}
