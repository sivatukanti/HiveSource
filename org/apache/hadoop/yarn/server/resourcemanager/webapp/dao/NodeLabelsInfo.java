// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import java.util.Collection;
import java.util.Set;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "nodeLabelsInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class NodeLabelsInfo
{
    protected ArrayList<String> nodeLabels;
    
    public NodeLabelsInfo() {
        this.nodeLabels = new ArrayList<String>();
    }
    
    public NodeLabelsInfo(final ArrayList<String> nodeLabels) {
        this.nodeLabels = new ArrayList<String>();
        this.nodeLabels = nodeLabels;
    }
    
    public NodeLabelsInfo(final Set<String> nodeLabelsSet) {
        this.nodeLabels = new ArrayList<String>();
        this.nodeLabels = new ArrayList<String>(nodeLabelsSet);
    }
    
    public ArrayList<String> getNodeLabels() {
        return this.nodeLabels;
    }
    
    public void setNodeLabels(final ArrayList<String> nodeLabels) {
        this.nodeLabels = nodeLabels;
    }
}
