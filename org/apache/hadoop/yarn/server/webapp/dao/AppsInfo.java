// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.webapp.dao;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "apps")
@XmlAccessorType(XmlAccessType.FIELD)
public class AppsInfo
{
    protected ArrayList<AppInfo> app;
    
    public AppsInfo() {
        this.app = new ArrayList<AppInfo>();
    }
    
    public void add(final AppInfo appinfo) {
        this.app.add(appinfo);
    }
    
    public ArrayList<AppInfo> getApps() {
        return this.app;
    }
}
