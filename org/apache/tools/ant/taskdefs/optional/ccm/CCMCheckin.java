// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.ccm;

import java.util.Date;

public class CCMCheckin extends CCMCheck
{
    public CCMCheckin() {
        this.setCcmAction("ci");
        this.setComment("Checkin " + new Date());
    }
}
