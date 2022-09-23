// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.webapp.dao;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "containers")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContainersInfo
{
    protected ArrayList<ContainerInfo> container;
    
    public ContainersInfo() {
        this.container = new ArrayList<ContainerInfo>();
    }
    
    public void add(final ContainerInfo containerInfo) {
        this.container.add(containerInfo);
    }
    
    public ArrayList<ContainerInfo> getContainers() {
        return this.container;
    }
}
