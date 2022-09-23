// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "appAttempts")
@XmlAccessorType(XmlAccessType.FIELD)
public class AppAttemptsInfo
{
    @XmlElement(name = "appAttempt")
    protected ArrayList<AppAttemptInfo> attempt;
    
    public AppAttemptsInfo() {
        this.attempt = new ArrayList<AppAttemptInfo>();
    }
    
    public void add(final AppAttemptInfo info) {
        this.attempt.add(info);
    }
    
    public ArrayList<AppAttemptInfo> getAttempts() {
        return this.attempt;
    }
}
