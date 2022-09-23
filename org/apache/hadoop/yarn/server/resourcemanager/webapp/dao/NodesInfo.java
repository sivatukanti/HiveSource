// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "nodes")
@XmlAccessorType(XmlAccessType.FIELD)
public class NodesInfo
{
    protected ArrayList<NodeInfo> node;
    
    public NodesInfo() {
        this.node = new ArrayList<NodeInfo>();
    }
    
    public void add(final NodeInfo nodeinfo) {
        this.node.add(nodeinfo);
    }
}
