// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.ccm;

public class CCMCheckinDefault extends CCMCheck
{
    public static final String DEFAULT_TASK = "default";
    
    public CCMCheckinDefault() {
        this.setCcmAction("ci");
        this.setTask("default");
    }
}
