// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "appStatInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicationStatisticsInfo
{
    protected ArrayList<StatisticsItemInfo> statItem;
    
    public ApplicationStatisticsInfo() {
        this.statItem = new ArrayList<StatisticsItemInfo>();
    }
    
    public void add(final StatisticsItemInfo statItem) {
        this.statItem.add(statItem);
    }
    
    public ArrayList<StatisticsItemInfo> getStatItems() {
        return this.statItem;
    }
}
