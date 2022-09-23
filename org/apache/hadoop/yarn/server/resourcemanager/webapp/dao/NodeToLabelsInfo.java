// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "nodeToLabelsInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class NodeToLabelsInfo
{
    protected HashMap<String, NodeLabelsInfo> nodeToLabels;
    
    public NodeToLabelsInfo() {
        this.nodeToLabels = new HashMap<String, NodeLabelsInfo>();
    }
    
    public HashMap<String, NodeLabelsInfo> getNodeToLabels() {
        return this.nodeToLabels;
    }
}
