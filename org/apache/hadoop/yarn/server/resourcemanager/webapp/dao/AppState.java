// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "appstate")
@XmlAccessorType(XmlAccessType.FIELD)
public class AppState
{
    String state;
    
    public AppState() {
    }
    
    public AppState(final String state) {
        this.state = state;
    }
    
    public void setState(final String state) {
        this.state = state;
    }
    
    public String getState() {
        return this.state;
    }
}
