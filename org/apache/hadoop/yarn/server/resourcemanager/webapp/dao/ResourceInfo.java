// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import org.apache.hadoop.yarn.api.records.Resource;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ResourceInfo
{
    int memory;
    int vCores;
    
    public ResourceInfo() {
    }
    
    public ResourceInfo(final Resource res) {
        this.memory = res.getMemory();
        this.vCores = res.getVirtualCores();
    }
    
    public int getMemory() {
        return this.memory;
    }
    
    public int getvCores() {
        return this.vCores;
    }
    
    @Override
    public String toString() {
        return "<memory:" + this.memory + ", vCores:" + this.vCores + ">";
    }
    
    public void setMemory(final int memory) {
        this.memory = memory;
    }
    
    public void setvCores(final int vCores) {
        this.vCores = vCores;
    }
}
